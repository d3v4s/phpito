package it.phpito.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.jogger.core.JoggerError;
import it.jogger.exception.FileLogException;
import it.jutilas.core.JutilasNet;
import it.jutilas.core.JutilasSys;
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
	private final ArrayBlockingQueue<Double> cpuUsageQueue = new ArrayBlockingQueue<Double>(80);
	private final String DIR_SCRIPT = Paths.get(JutilasSys.getInstance().getRunPath(), "script").toString();
	private final String EXT_SCRIPT = (JutilasSys.getInstance().getOsName().contains("win")) ? ".bat" : ".sh";
	private final String SCRIPT_START_SERVER = "start-server" + EXT_SCRIPT;
	private final String SCRIPT_STOP_SERVER = "stop-server" + EXT_SCRIPT;
	private final String SCRIPT_PID_SERVER = "pid-server" + EXT_SCRIPT;
	private final String SCRIPT_CHECK_SERVER = "check-server" + EXT_SCRIPT;
	private final String RUN = (JutilasSys.getInstance().getOsName().contains("win")) ? "cmd.exe": "./";
	public static final String NAME = "PHPito";
	public static final String INFO = "PHP Server Manager";
	public static final String VERSION = "1.0";
	public static final String AUTHOR = "Andrea Serra";
	public static final String LINK_GITHUB = "https://github.com/z4X0r/phpito";

	/* costruttore */
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
	/* get quque dell'uso cpu */
	public ArrayBlockingQueue<Double> getCpuUsageQueue() {
		return cpuUsageQueue;
	}

//	/* metodo che ritorna stringa con info sistema */
//	public String getSystemInfo() throws IOException {
//		double sysAdvrg = JutilasSys.getInstance().getSystemLoadAdverage(1000);
//		StringBuffer cpu = new StringBuffer("CPU: ").append(String.format("%.2f", sysAdvrg)).append("%");
//		return new StringBuffer("OS: ").append(JutilasSys.getInstance().getOsName()).append("\n").append(
//				"Arch: ").append(JutilasSys.getInstance().getOsArch()).append("\n").append(
//				"User: ").append(JutilasSys.getInstance().getOsUser()).append("\n").append(cpu).toString();
//	}

	/* metodo che ritorna stringa con info sistema */
	public String getSystemInfo(Double sysAdvrg) throws IOException {
		if (sysAdvrg == null)
			sysAdvrg = JutilasSys.getInstance().getSystemLoadAdverage(1000);
		StringBuffer cpu = new StringBuffer("CPU: ").append(String.format("%.2f", sysAdvrg)).append("%");
		return new StringBuffer("OS: ").append(JutilasSys.getInstance().getOsName()).append("\n").append(
				"Arch: ").append(JutilasSys.getInstance().getOsArch()).append("\n").append(
				"User: ").append(JutilasSys.getInstance().getOsUser()).append("\n").append(cpu).toString();
	}

	/* metodo ritorna progetto da id */
	public Project getProjectById(Long id) {
		if (id == null)
			return null;
		return reentrantLockXMLServer.getProject(String.valueOf(id));
	}

	/* metodo per avviare un server */
	public boolean startServer(Project project) throws IOException, ServerException, NumberFormatException, ProjectException {
		if (project == null)
			throw new ProjectException("Errore!!! Nessun server selezionato");
		if (!JutilasNet.getInstance().isAvaiblePort(project.getServer().getPort()))
			throw new ServerException("Errore!!! La porta scelta e' gia' in uso.");
		String[] cmndStart;
		if (JutilasSys.getInstance().getOsName().contains("win"))
			cmndStart = new String[] {RUN, "/c" ,SCRIPT_START_SERVER,
									project.getServer().getAddressAndPort(),
									project.getServer().getPath(), "test.log"};
		else
			cmndStart = new String[] {RUN + SCRIPT_START_SERVER,
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
			if (!stdoStart.isEmpty() && project.isLogActive())
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
				if (project.isLogActive())
					new ReadOutputServerThread(project, br).start();
				else 
					br.close();
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

	/* thread per leggere l'output log del server */
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
					JoggerError.getInstance().writeLog(e, NAME, null);
				} catch (FileLogException e1) {
					e1.printStackTrace();
				}
			} finally {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					try {
						JoggerError.getInstance().writeLog(e, NAME, null);
					} catch (FileLogException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	/* metodo che aggiorna i server in esecuzione sull'xml */
	public void flushRunningServers() throws IOException, NumberFormatException, ProjectException {
		HashMap<String, Project> projectMap = reentrantLockXMLServer.getProjectsMap();
		Project project = null;
		for (String id : projectMap.keySet()) {
			project = projectMap.get(id);
			if (project.getServer().isRunning()) {
				project.getServer().setProcessId(getPIDServer(project.getServer()));
			} else {
				project.getServer().setProcessId(null);
			}
			reentrantLockXMLServer.updateProject(project);
		}
	}

	/* metodo che ritorna i server in esecuzione */
	public ArrayList<Server> getRunningServers() throws IOException, ProjectException {
		ArrayList<Server> serverList = new ArrayList<Server>();
//		flushRunningServers();
		HashMap<String, Project> projectMap = reentrantLockXMLServer.getProjectsMap();
		for (String id : projectMap.keySet())
			if (projectMap.get(id).getServer().isRunning())
				serverList.add(projectMap.get(id).getServer());
		
		return serverList;
	}

	/* metodo che ritorna il PID del server */
	private Long getPIDServer(Server server) throws NumberFormatException, IOException, ProjectException {
		if (server == null)
			throw new ProjectException("Errore!!! Nessun server selezionato");
		String regexPID;
		String[] cmnd;
		if (JutilasSys.getInstance().getOsName().contains("win")) {
			cmnd = new String[] {RUN, "/c", SCRIPT_PID_SERVER, server.getAddressAndPortRegex()};
			regexPID  = ".*TCP.*" + server.getAddressAndPortRegex() + ".*LISTENING[\\D]*([\\d]{1,})[\\D]*";
		} else {
			cmnd = new String[] {RUN + SCRIPT_PID_SERVER, server.getAddressAndPortRegex()};
			regexPID = ".*tcp.*" + server.getAddressAndPortRegex() + ".*LISTEN[\\D]*([\\d]{1,})/php.*";
		}
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

	/* metodo che controlla se il server e' in esecuzione */
	private boolean isServerRunning(Server server) throws IOException {
		if (server.getProcessID() == null)
			return false;
		String[] cmnd;
		String regex;
		if (JutilasSys.getInstance().getOsName().contains("win")) {
			cmnd = new String[] {RUN, "/c", SCRIPT_CHECK_SERVER, server.getAddressAndPortRegex(), server.getPIDString()};
			regex = ".*TCP.*" + server.getAddressAndPortRegex() + ".*LISTENING[\\D]*" + server.getPIDString() + "[\\D]*.*";
		} else {
			cmnd = new String[] {RUN + SCRIPT_CHECK_SERVER, server.getAddressAndPortRegex(), server.getPIDString()};
			regex = ".*tcp.*" + server.getAddressAndPortRegex() + ".*LISTEN[\\D]*" + server.getPIDString() + "/php.*";
		}
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
	
	/* metodo per stoppare server */
	public boolean stopServer(Project project) throws IOException, ServerException, ProjectException {
		if (project == null)
			throw new ProjectException("Errore!!! Nessun server selezionato");
		if (project.getServer().isRunning()) {
			String[] cmndStop;
			if (JutilasSys.getInstance().getOsName().contains("win"))
				cmndStop = new String[] {RUN, "/c", SCRIPT_STOP_SERVER, project.getServer().getPIDString()};
			else
				cmndStop = new String[] {RUN + SCRIPT_STOP_SERVER, project.getServer().getPIDString()};
			String regexStop = ".*PHPito stopped server at.*";
			String regexFail = ".*Error!!! Fail to stop server.*";
			ProcessBuilder pb = new ProcessBuilder(cmndStop);
			pb.directory(new File(DIR_SCRIPT));
			pb.redirectErrorStream(true);
			Process prs = pb.start();
			
			InputStreamReader isr = new InputStreamReader(prs.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String stdo = null;
			while ((stdo = br.readLine()) != null) {
				if (project.isLogActive())
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