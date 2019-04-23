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

import it.as.utils.core.LogErrorAS;
import it.as.utils.core.NetworkAS;
import it.as.utils.core.UtilsAS;
import it.as.utils.exception.FileException;
import it.phpito.controller.lock.ReentrantLockLogServer;
import it.phpito.controller.lock.ReentrantLockXMLServer;
import it.phpito.data.Project;
import it.phpito.data.Server;
import it.phpito.exception.ProjectException;
import it.phpito.exception.ServerException;

public class PHPitoManager {
	private static PHPitoManager phpItoManager;
	private final ReentrantLockXMLServer reentrantLockXMLServer = new ReentrantLockXMLServer();
	private final ReentrantLockLogServer reentrantLockLogServer = new ReentrantLockLogServer();
	private final String DIR_SCRIPT = Paths.get(UtilsAS.getInstance().getRunPath(), "script").toString();
	private final String EXT_SCRIPT = (UtilsAS.getInstance().getOsName().contains("win")) ? ".bat" : ".sh";
	private final String SCRIPT_START_SERVER = "start-server" + EXT_SCRIPT;
	private final String SCRIPT_STOP_SERVER = "stop-server" + EXT_SCRIPT;
	private final String SCRIPT_PID_SERVER = "pid-server" + EXT_SCRIPT;
	private final String SCRIPT_CHECK_SERVER = "check-server" + EXT_SCRIPT;
	private final String RUN = (UtilsAS.getInstance().getOsName().contains("win")) ? "" : "./";
	public static final String NAME = "PHPito";

	private PHPitoManager() {
	}

	/* singleton */
	public static PHPitoManager getInstance() {
		return (phpItoManager = (phpItoManager == null) ? new PHPitoManager() : phpItoManager);
	}

	/* get reentrant lock */
	public ReentrantLockXMLServer getReentrantLockXMLServer() {
		return reentrantLockXMLServer;
	}
	public ReentrantLockLogServer getReentrantLockLogServer() {
		return reentrantLockLogServer;
	}

	/* metodo ritorna progetto da id */
	public Project getProjectById(Long id) {
		if (id == null)
			return null;
		return reentrantLockXMLServer.getProjectsMap().get(String.valueOf(id));
	}
//
//	public LocalDateTime getLocalDateTimeLastModifyLogServer(Long id) throws FileException {
//		if (id == null)
//			return LocalDateTime.MAX;
//		Project project = getProjectById(id);
//		if (project == null)
//			return LocalDateTime.MAX;
//		File logFile = LoggerAS.getInstance().getFileLog(project.getName(), null, new String[] {"server", project.getIdAndName()});
//		Integer year = Integer.valueOf(new SimpleDateFormat("yyyy").format(logFile.lastModified()));
//		Integer month = Integer.valueOf(new SimpleDateFormat("MM").format(logFile.lastModified()));
//		Integer dayOfMonth = Integer.valueOf(new SimpleDateFormat("dd").format(logFile.lastModified()));
//		Integer hour = Integer.valueOf(new SimpleDateFormat("HH").format(logFile.lastModified()));
//		Integer minute = Integer.valueOf(new SimpleDateFormat("mm").format(logFile.lastModified()));
//		Integer second = Integer.valueOf(new SimpleDateFormat("ss").format(logFile.lastModified()));
//		return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
//	}
	
	public boolean startServer(Project project) throws IOException, ServerException, NumberFormatException, ProjectException {
		if (project == null)
			throw new ProjectException("Errore!!! Nessun server selezionato");
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
				reentrantLockLogServer.writeLog(stdoStart, project);
			if (Pattern.matches(regexError, stdoStart)) {
				String reasonError = "";
				Matcher matchReasError = Pattern.compile(regexReasError).matcher(stdoStart);
				if (matchReasError.find())
					reasonError = matchReasError.group(1);
				br.close();
				flushRunningServers();
				throw new ServerException("Errore Server!!! L'avvio del Server ha ritornato un errore".concat(
											(reasonError.isEmpty()) ? "\nMessaggio: " + stdoStart :
												"\nErrore individuato: " + reasonError));
			} else if ((pid = getPIDServer(project.getServer())) != null) {
				project.getServer().setProcessId(pid);
				reentrantLockXMLServer.updateProject(project);
				new ReadOutputServerThread(project, br).start();
				return true;
			} else if (LocalDateTime.now().isAfter(maxTime)) {
				br.close();
				flushRunningServers();
				throw new ServerException("Error Server!!! Avvio del server php fallito!");
			}
		}
		br.close();
		flushRunningServers();
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
					reentrantLockLogServer.writeLog(outServer, project);
				}
			} catch (IOException e) {
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
	

	public void flushRunningServers() throws IOException, NumberFormatException, ProjectException {
		HashMap<String, Project> projectMap = reentrantLockXMLServer.getProjectsMap();
		Project prjct = null;
		for (String id : projectMap.keySet()) {
			prjct = projectMap.get(id);
			if (isServerRunning(prjct.getServer())) {
				prjct.getServer().setProcessId(getPIDServer(prjct.getServer()));
			} else {
				prjct.getServer().setProcessId(null);
			}
			reentrantLockXMLServer.updateProject(prjct);
		}
	}
	
	public ArrayList<Server> getRunningServers() throws IOException, ProjectException {
		ArrayList<Server> serverList = new ArrayList<Server>();
		flushRunningServers();
		HashMap<String, Project> projectMap = reentrantLockXMLServer.getProjectsMap();
		for (String id : projectMap.keySet())
			if (isServerRunning(projectMap.get(id).getServer()))
				serverList.add(projectMap.get(id).getServer());
		
		return serverList;
	}
	
	public Long getPIDServer(Server server) throws NumberFormatException, IOException, ProjectException {
		if (server == null)
			throw new ProjectException("Errore!!! Nessun server selezionato");
		String regexPID = (UtilsAS.getInstance().getOsName().contains("win")) ?
				".*TCP.*" + server.getAddressAndPortRegex() + ".*LISTENING[\\D]*([\\d]{1,})[\\D]*" :
				".*tcp.*" + server.getAddressAndPortRegex() + ".*LISTEN[\\D]*([\\d]{1,})/php.*";
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
				".*TCP.*" + server.getAddressAndPortRegex() + ".*LISTENING[\\D]*" + server.getPIDString() + "[\\D]*.*" :
				".*tcp.*" + server.getAddressAndPortRegex() + ".*LISTEN[\\D]*" + server.getPIDString() + "/php.*";
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
	

	public boolean stopServer(Project project) throws IOException, ServerException, ProjectException {
		if (project == null)
			throw new ProjectException("Errore!!! Nessun server selezionato");
		if (isServerRunning(project.getServer())) {
			String[] cmnd = new String[] {RUN + SCRIPT_STOP_SERVER, project.getServer().getPIDString()};
			String regexStop = ".*[\\W]{3}[\\s]PHPito stopped server at [\\d]{4}-[\\d]{2}-[\\d]{2}[\\s][\\d]{2}:[\\d]{2}:[\\d]{2}.*";
			String regexFail = ".*[\\W]{3}[\\s]Error!!! Fail to stop server.*";
			ProcessBuilder pb = new ProcessBuilder(cmnd);
			pb.directory(new File(DIR_SCRIPT));
			pb.redirectErrorStream(true);
			Process prs = pb.start();
			
			InputStreamReader isr = new InputStreamReader(prs.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String stdo = null;
			while ((stdo = br.readLine()) != null) {
				reentrantLockLogServer.writeLog(stdo, project);
				if (Pattern.matches(regexFail, stdo)) {
					br.close();
					flushRunningServers();
					throw new ServerException("Errore!!! L'arresto del Server ha ritornato un errore.\n"
												+ "Indirizzo: " + project.getServer().getAddress() + "\n"
												+ "PID: " + project.getServer().getPIDString());
				} else if (Pattern.matches(regexStop, stdo)) {
					br.close();

					flushRunningServers();
					return true;
				}
			}
			br.close();
			if (!isServerRunning(project.getServer())) {
				flushRunningServers();
				return true;
			} else
				return false;
		} else {
			flushRunningServers();
			throw new ServerException("Errore!!! Il server non e' avviato.");
		}
	}
}