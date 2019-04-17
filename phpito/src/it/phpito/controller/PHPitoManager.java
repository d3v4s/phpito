package it.phpito.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

import it.as.utils.core.LogErrorAS;
import it.as.utils.core.LoggerAS;
import it.as.utils.core.NetworkAS;
import it.as.utils.core.UtilsAS;
import it.as.utils.core.XMLManagerAS;
import it.as.utils.exception.FileException;
import it.phpito.data.Project;
import it.phpito.data.Server;
import it.phpito.exception.ServerException;

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

	private PHPitoManager() {
	}

	/* singleton */
	public static PHPitoManager getInstance() {
		phpItoManager = (phpItoManager == null) ? new PHPitoManager() : phpItoManager;
		return phpItoManager;
	}

	public HashMap<String, Project> getProjectsMap() throws FileException {
		HashMap<String, Project> mapProjects = new HashMap<String, Project>();
		Project project;
		Node node;
		String pid;
		XMLManagerAS xmlAS = XMLManagerAS.getInstance();
		HashMap<String, Node> mapNode = xmlAS.getMapIdElement(PATH_FILE_XML, XML_SERVER);
		for (String id : mapNode.keySet()) {
			node = mapNode.get(id);
			project = new Project();
			project.setId(Long.valueOf(id));
			project.setName(xmlAS.getArrayChildNode(node, XML_NAME).get(0).getTextContent());
			project.setServer(new Server());
			project.getServer().setAddress(xmlAS.getArrayChildNode(node, XML_ADDRESS).get(0).getTextContent());
			project.getServer().setPort(Integer.parseInt(xmlAS.getArrayChildNode(node, XML_PORT).get(0).getTextContent()));
			project.getServer().setPath(xmlAS.getArrayChildNode(node, XML_PATH).get(0).getTextContent());
			pid = xmlAS.getArrayChildNode(node, XML_PID).get(0).getTextContent();
			project.getServer().setProcessIdString(pid);
			mapProjects.put(id, project);
		}
		
		return mapProjects;
	}
	
	public Project getProjectById(Long id) throws FileException {
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
		xmlAS.getArrayChildNode(node, XML_PID).get(0).setTextContent(project.getServer().getPIDString());
		xmlAS.flush(node.getOwnerDocument(), PATH_FILE_XML);
	}
	
	@SuppressWarnings("resource")
	public boolean startServer(Project project) throws IOException, FileException, ServerException {
		if (!NetworkAS.getInstance().isAvaiblePort(project.getServer().getPort()))
			throw new ServerException("Errore!!! La porta scelta e' gia' in uso.");
		String[] cmndStart = new String[] {RUN + SCRIPT_START_SERVER,
											project.getServer().getAddressAndPort(),
											project.getServer().getPath(), "test.log"};
		String regexError = ".*Failed to listen on " + project.getServer().getAddressAndPort() + ".*";
		String regexReasError = ".*reason: ([\\w\\s]{1,}).*";
		ProcessBuilder pbStart = new ProcessBuilder(cmndStart);
		pbStart.directory(new File(DIR_SCRIPT));
		pbStart.redirectErrorStream(true);
		Process prcssStart = pbStart.start();
		
		
		
		InputStreamReader isrStart = new InputStreamReader(prcssStart.getInputStream());
		BufferedReader br = new BufferedReader(isrStart);
		
		String stdoStart = null;
		Long pid = null;
		LocalDateTime maxTime = LocalDateTime.now().plusSeconds(5L);
		while ((stdoStart = (br.ready()) ? br.readLine() : "") != null) {
			if (!stdoStart.isEmpty())
				LoggerAS.getInstance().writeLog(stdoStart, project.getName(), new String[] {"server", project.getName()});
			if (Pattern.matches(regexError, stdoStart)) {
				project.getServer().setProcessId(null);
				updateProject(project);
				String reasonError = "";
				Matcher matchReasError = Pattern.compile(regexReasError).matcher(stdoStart);
				if (matchReasError.find())
					reasonError = matchReasError.group(1);
				br.close();
				throw new ServerException("Errore Server!!! L'avvio del Server ha ritornato un errore".concat(
																	(reasonError.isEmpty()) ? "\nMessaggio: " + stdoStart :
																		"\nErrore individuato: " + reasonError));
			} else if ((pid = getPIDServer(project.getServer())) != null) {
				project.getServer().setProcessId(pid);
				updateProject(project);
				new ReadOutputServerThread(project, br).start();
				return true;
			} else if (LocalDateTime.now().isAfter(maxTime)) {
				throw new ServerException("Error Server!!! Fail to start PHP server");
			}
		}
		br.close();
		return false;
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
	
	public void flushRunningServers() throws IOException, FileException {
		HashMap<String, Project> projectMap = getProjectsMap();
		for (String id : projectMap.keySet())
			if (!isServerRunning(projectMap.get(id).getServer())) {
				Project prjct = projectMap.get(id);
				prjct.getServer().setProcessId(null);
				updateProject(prjct);
			}
	}
	
	public ArrayList<Server> getRunningServers() throws FileException, IOException {
		ArrayList<Server> serverList = new ArrayList<Server>();
		HashMap<String, Project> projectMap = getProjectsMap();
		for (String id : projectMap.keySet())
			if (isServerRunning(projectMap.get(id).getServer()))
				serverList.add(projectMap.get(id).getServer());
		
		return serverList;
	}
	
	public Long getPIDServer(Server server) throws NumberFormatException, IOException {
		String regexPID = (UtilsAS.getInstance().getOsName().contains("win")) ?
				".*TCP.*" + server.getAddressAndPortRegex() + ".*LISTENING[\\s]*([\\d]{1,})[\\D]*" :
				".*tcp.*" + server.getAddressAndPortRegex() + ".*LISTEN[\\s]*([\\d]{1,})/php.*";
		String[] cmnd = new String[] {RUN + SCRIPT_PID_SERVER, server.getAddressAndPortRegex()};
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
				br.close();
				return Long.valueOf(match.group(1));
			}
		}
		br.close();
		return null;
	}
	
	public boolean isServerRunning(Server server) throws IOException {
		if (server.getProcessID() == null)
			return false;
		String[] cmnd = new String[] {RUN + SCRIPT_CHECK_SERVER, server.getAddressAndPortRegex(), server.getPIDString()};
		String regex = (UtilsAS.getInstance().getOsName().contains("win")) ?
				".*TCP.*" + server.getAddressAndPortRegex() + ".*LISTENING[\\s]*" + server.getPIDString() + ".*":
				".*tcp.*" + server.getAddressAndPortRegex() + ".*LISTEN[\\s]*" + server.getPIDString() + "/php.*";
		ProcessBuilder pb = new ProcessBuilder(cmnd);
		pb.directory(new File(DIR_SCRIPT));
		pb.redirectErrorStream(true);
		Process prcss = pb.start();

		InputStreamReader isr = new  InputStreamReader(prcss.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		String stdo = null;
		while ((stdo = br.readLine()) != null) {
			if (Pattern.matches(regex, stdo)) {
				br.close();
				return true;
			}
		}
		br.close();
		return false;
	}
	
	public boolean stopServer(Project project) throws IOException, FileException, ServerException {
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
				if (Pattern.matches(regexFail, stdo)) {
					br.close();
					throw new ServerException("Error!!! Fail to stop server at " + project.getServer().getAddress() +
													" PID: " + project.getServer().getPIDString());
				} else if (Pattern.matches(regexStop, stdo)) {
					br.close();
					project.getServer().setProcessId(null);
					updateProject(project);
					return true;
				}
			}
			br.close();
			if (!isServerRunning(project.getServer())) {
				project.getServer().setProcessId(null);
				updateProject(project);
				return true;
			} else
				return false;
		} else {
			project.getServer().setProcessId(null);
			updateProject(project);
			throw new ServerException("Error!!! Server is not running");
		}
	}
}