package it.phpito.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

import it.as.utils.core.LogErrorAS;
import it.as.utils.core.LoggerAS;
import it.as.utils.core.NetworkAS;
import it.as.utils.core.UtilsAS;
import it.as.utils.core.XMLManagerAS;
import it.as.utils.exception.FileException;
import it.phpito.ServerException;
import it.phpito.data.Project;
import it.phpito.data.Server;

public class PHPitoManager {
	private static PHPitoManager phpItoManager;
	private final String DIR_SCRIPT = Paths.get(UtilsAS.getInstance().getRunPath(), "script").toString();
	private final String EXT_SCRIPT = (UtilsAS.getInstance().getOsName().contains("win")) ? ".bat" : ".sh";
	private final String SCRIPT_START_SERVER = "start-server" + EXT_SCRIPT;
	private final String SCRIPT_STOP_SERVER = "stop-server" + EXT_SCRIPT;
	private final String SCRIPT_PID_SERVER = "pid-server" + EXT_SCRIPT;
	private final String SCRIPT_CHECK_SERVER = "check-server" + EXT_SCRIPT;
	private final String RUN = (UtilsAS.getInstance().getOsName().contains("win")) ? "" : "./";
	private final String PATH_FILE_XML = Paths.get("conf", "server.xml").toString();
	private final String XML_SERVER = "server";
	private final String XML_NAME = "name";
	private final String XML_PATH = "path";
	private final String XML_ADDRESS = "address";
	private final String XML_PORT = "port";
	private final String XML_PID = "pid";
	public static final String NAME = "PHPito";
	public boolean stop = false;

	private PHPitoManager() {
	}

	/* singleton */
	public static PHPitoManager getInstance() {
		phpItoManager = (phpItoManager == null) ? new PHPitoManager() : phpItoManager;
		return phpItoManager;
	}

	public HashMap<String, Project> getProjectsMap() throws FileException {
		HashMap<String, Project> mapProjects = new HashMap<String, Project>();
		String regexPID = "[\\d]{1,}";
		Project project;
		Node node;
		String pid;
		XMLManagerAS xmlAS = XMLManagerAS.getInstance();
		HashMap<String, Node> mapNode = xmlAS.getMapIdElement(PATH_FILE_XML, XML_SERVER);
		for (String id : mapNode.keySet()) {
			node = mapNode.get(id);
			project = new Project();
			project.setId(Integer.valueOf(id));
			project.setName(xmlAS.getArrayChildNode(node, XML_NAME).get(0).getTextContent());
			project.setServer(new Server());
			project.getServer().setAddress(xmlAS.getArrayChildNode(node, XML_ADDRESS).get(0).getTextContent());
			project.getServer().setPort(Integer.parseInt(xmlAS.getArrayChildNode(node, XML_PORT).get(0).getTextContent()));
			project.getServer().setPath(xmlAS.getArrayChildNode(node, XML_PATH).get(0).getTextContent());
			pid = xmlAS.getArrayChildNode(node, XML_PID).get(0).getTextContent();
			project.getServer().setProcessId((pid.trim().isEmpty() || !Pattern.matches(regexPID, pid.trim()) ? null : Integer.parseInt(pid)));
			mapProjects.put(id, project);
		}
		
		return mapProjects;
	}
	
	public Project getProjectById(Integer id) throws FileException {
		return getProjectsMap().get(String.valueOf(id));
	}
	
	public void updateProject(Project project) throws FileException {
		XMLManagerAS xmlAS = XMLManagerAS.getInstance();
		HashMap<String, Node> mapNode = xmlAS.getMapIdElement(PATH_FILE_XML, XML_SERVER);
		Node node = mapNode.get(project.getIdString());
		xmlAS.getArrayChildNode(node, XML_NAME).get(0).setTextContent(project.getName());
		xmlAS.getArrayChildNode(node, XML_PATH).get(0).setTextContent(project.getServer().getPath());
		xmlAS.getArrayChildNode(node, XML_ADDRESS).get(0).setTextContent(project.getServer().getAddress());
		xmlAS.getArrayChildNode(node, XML_PORT).get(0).setTextContent(project.getServer().getPortString());
		xmlAS.getArrayChildNode(node, XML_PID).get(0).setTextContent((project.getServer().getProcessID() != null) ? project.getServer().getPIDString() : "");
		xmlAS.flush(node.getOwnerDocument(), PATH_FILE_XML);
	}
	
	public void startServer(Project project) throws IOException, FileException, ServerException {
		if (!NetworkAS.getInstance().isAvaiblePort(project.getServer().getPort()))
			throw new ServerException("Errore!!! La porta scelta e' gia' in uso.");
		String[] cmndStart = new String[] {RUN + SCRIPT_START_SERVER,
											project.getServer().getAddressAndPort(),
											project.getServer().getPath(), "test.log"};
		ProcessBuilder pbStart = new ProcessBuilder(cmndStart);
//		File dirScript = new File(DIR_SCRIPT);
		pbStart.directory(new File(DIR_SCRIPT));
		pbStart.redirectErrorStream(true);
		Process prcssStart = pbStart.start();
		
		
		
		InputStreamReader isrStart = new InputStreamReader(prcssStart.getInputStream());
		BufferedReader br = new BufferedReader(isrStart);
//		String regexStart = "Listening on http://" + project.getServer().getAddressAndPort();
		String regexError = ".*Failed to listen on " + project.getServer().getAddressAndPort() + ".*";
		String regexReasError = ".*reason: ([\\w\\s]{1,}).*";
		String stdoStart = null;
		Integer pid = null;
		LocalDateTime timeMax = LocalDateTime.now().plusSeconds(10L);
		while (true) {
			stdoStart = br.readLine();
			LoggerAS.getInstance().writeLog(stdoStart, project.getName(), new String[] {"server", project.getName()});
			System.out.println(stdoStart);
			 if (Pattern.matches(regexError, stdoStart)) {
				String reasonError = "";
				Matcher matchReasError = Pattern.compile(regexReasError).matcher(stdoStart);
				if (matchReasError.find())
					reasonError = matchReasError.group(1);
				project.getServer().setProcessId(null);
				updateProject(project);
				br.close();
				throw new ServerException("Errore Server!!! L'avvio del Server ha ritornato un errore".concat(
																					(reasonError.isEmpty()) ? "\nMessaggio: " + stdoStart :
																						"\nErrore individuato: " + reasonError));
			} else if ((pid = getPIDServer(project.getServer())) != null) {
				project.getServer().setProcessId(pid);
				updateProject(project);
 				System.out.println("PHPitoManager.runServer() OK WRITE - STARTING THREAD");
				new ReadOutputServerThread(project, br).start();
				return;
			}
			if (LocalDateTime.now().isAfter(timeMax)) {
				String msgError = "Error!!! Fail to start PHP server on " + project.getServer().getAddressAndPort();
				LoggerAS.getInstance().writeLog(msgError, project.getName(), new String[] {"server", project.getName()});
				System.out.println("PHPitoManager.startServer() OK DATEEEEEEE MSG: " + msgError);
				project.getServer().setProcessId(null);
				updateProject(project);
				br.close();
				throw new ServerException(msgError);
			}
		}
	}
	
	private class ReadOutputServerThread extends Thread {
		Project project;
		BufferedReader bufferedReader;

		private ReadOutputServerThread(Project project, BufferedReader bufferReader) {
			this.project = project;
			this.bufferedReader = bufferReader;
		}

		@Override
		public void run() {
			try {
				String outServer = null;
				while ((outServer = bufferedReader.readLine()) != null) {
					LoggerAS.getInstance().writeLog(outServer, project.getName(), new String[] {"server", project.getName()});
					System.out.println(outServer);
				}
			} catch (IOException | FileException e) {
				try {
					LogErrorAS.getInstance().writeLog(e, NAME);
				} catch (FileException e1) {
					e1.printStackTrace();
				}
			} finally {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					try {
						LogErrorAS.getInstance().writeLog(e, NAME);
					} catch (FileException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
	
	public Integer getPIDServer(Server server) throws FileException, NumberFormatException, IOException {
//		String regexPID = ".*tcp[\\s]{1,}[\\d]{1,}[\\s]{1,}[\\d]{1,}[\\s]{1,}" + server.getAddressAndPort() +
//				"[\\s]{1,}[\\d]{1,3}[.]{1}[\\d]{1,3}[.]{1}[\\d]{1,3}[.]{1}[\\d]{1,3}[\\S]{1,}[\\s]{1,}LISTEN[\\s]{1,}([\\d]{1,})/php.*";
		String regexPID = ".*tcp.*" + server.getAddressAndPort().replace(".", "\\.") + ".*LISTEN[\\s]*([\\d]{1,})/php.*";
		String[] cmnd = new String[] {RUN + SCRIPT_PID_SERVER, server.getAddressAndPort()};
		ProcessBuilder pb = new ProcessBuilder(cmnd);
		pb.directory(new File(DIR_SCRIPT));
		pb.redirectErrorStream(true);
		Process prcss = pb.start();
		InputStreamReader isr = new InputStreamReader(prcss.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		String stdo = null;
		while ((stdo = br.readLine()) != null) {
			Matcher match = Pattern.compile(regexPID).matcher(stdo);
			if (match.find()) {
				server.setProcessId(Integer.valueOf(match.group(1)));
				br.close();
				return server.getProcessID();
			}
		}
		br.close();
		return null;
	}
	
	public boolean isServerRunning(Server server) throws IOException {
		String[] cmnd = new String[] {RUN + SCRIPT_CHECK_SERVER, server.getAddressAndPort(), server.getPIDString()};
		String regex = ".*tcp.*" + server.getAddressAndPort().replace(".", "\\.") + ".*LISTEN[\\s]*" + server.getPIDString() + "/php.*";
		ProcessBuilder pb = new ProcessBuilder(cmnd);
		pb.directory(new File(DIR_SCRIPT));
		pb.redirectErrorStream(true);
		Process prcss = pb.start();

		InputStreamReader isr = new  InputStreamReader(prcss.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		String stdo = null;
		while ((stdo = br.readLine()) != null) {
			if (Pattern.matches(regex, stdo)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void stopServer(Project project) throws IOException, FileException, ServerException {
		if (isServerRunning(project.getServer())) {
			String[] cmnd = new String[] {RUN + SCRIPT_STOP_SERVER, project.getServer().getPIDString()};
			String regexStop = ".*[\\W]{3} PHPito stopped server at [\\d]{4}-[\\d]{2}-[\\d]{2} [\\d]{2}:[\\d]{2}:[\\d]{2}.*";
			String regexFail = ".*[\\W]{3} Error[!]{3} Fail to stop server.*";
			ProcessBuilder pb = new ProcessBuilder(cmnd);
			pb.directory(new File(DIR_SCRIPT));
			pb.redirectErrorStream(true);
			Process prs = pb.start();
			
			InputStreamReader isr = new InputStreamReader(prs.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String stdo = null;
			while ((stdo = br.readLine()) != null) {
				LoggerAS.getInstance().writeLog(stdo, project.getName(), new String[] {"server", project.getName()});
				System.out.println(stdo);
				if (Pattern.matches(regexFail, stdo)) {
					br.close();
					throw new ServerException("Error!!! Fail to stop server at " + project.getServer().getAddress() +
													" PID: " + project.getServer().getPIDString());
				} else if (Pattern.matches(regexStop, stdo)) {
					br.close();
					project.getServer().setProcessId(null);
					updateProject(project);
					return;
				}
			}
			br.close();
		} else {
			project.getServer().setProcessId(null);
			updateProject(project);
			throw new ServerException("Error!!! Server is not running");
		}
	}
}


















