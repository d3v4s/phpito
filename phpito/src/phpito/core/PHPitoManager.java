package phpito.core;

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

import exception.LockLogException;
import jogger.JoggerDebug;
import jogger.JoggerError;
import jutilas.core.Jutilas;
import jutilas.core.JutilasNet;
import jutilas.core.JutilasSys;
import jutilas.exception.FileException;
import phpito.core.lock.ReentrantLockProjectsXML;
import phpito.core.lock.ReentrantLockServerLog;
import phpito.data.Project;
import phpito.data.Server;
import phpito.exception.ProjectException;
import phpito.exception.ServerException;

/**
 * Class for manage the PHPito application
 * @author Andrea Serra
 *
 */
public class PHPitoManager {
	private static PHPitoManager phpItoManager;
	private final ReentrantLockProjectsXML reentrantLockProjectsXML = new ReentrantLockProjectsXML();
	private final ReentrantLockServerLog reentrantLockLogServer = new ReentrantLockServerLog();
	private final String DIR_SCRIPT = Paths.get(JutilasSys.getInstance().getRunPath(), "script").toString();
	private final String EXT_SCRIPT = (JutilasSys.getInstance().isWindows()) ? ".bat" : ".sh";
	private final String SCRIPT_START_SERVER = "start-server" + EXT_SCRIPT;
	private final String SCRIPT_START_SERVER_INI = "start-server-ini" + EXT_SCRIPT;
	private final String SCRIPT_STOP_SERVER = "stop-server" + EXT_SCRIPT;
	private final String SCRIPT_PID_SERVER = "pid-server" + EXT_SCRIPT;
	private final String SCRIPT_CHECK_SERVER = "check-server" + EXT_SCRIPT;
	private final String RUN = (JutilasSys.getInstance().isWindows()) ? "cmd.exe": "./";
	public static final String NAME = "PHPito";
	public static final String INFO = "PHP Server Manager";
	public static final String VERSION = "1.0";
	public static final String AUTHOR = "Andrea Serra";
	public static final String LINK_GITHUB = "https://github.com/d3v4s";
	private JoggerDebug joggerDebug;
	private JoggerError joggerError;

	/* CONSTRUCT */
	private PHPitoManager() {
		joggerDebug = new JoggerDebug(NAME);
		joggerDebug.setDebug(false);
		joggerDebug.setPrintStackTrace(true);
		joggerDebug.setLock(true);
		joggerError = new JoggerError(NAME);
		joggerError.setLock(true);
	}

	/* SINGLETON */
	public static PHPitoManager getInstance() {
		return phpItoManager = phpItoManager == null ? new PHPitoManager() : phpItoManager;
	}

	/* ############################################################################# */
	/* START GET AND SET */
	/* ############################################################################# */

	/* get logger */
	public JoggerDebug getJoggerDebug() {
		return joggerDebug;
	}
	public JoggerError getJoggerError() {
		return joggerError;
	}
	/* get reentrant lock */
	public ReentrantLockProjectsXML getReentrantLockProjectsXML() {
		return reentrantLockProjectsXML;
	}
	public ReentrantLockServerLog getReentrantLockLogServer() {
		return reentrantLockLogServer;
	}

	/* ############################################################################# */
	/* END GET AND SET */
	/* ############################################################################# */

	/* ############################################################################# */
	/* START PUBLIC METHODS */
	/* ############################################################################# */

	/* method that add a new project */
	public void addNewProject(Project project) throws ProjectException {
		getReentrantLockProjectsXML().addProject(project);
		project.getPhpiniPath();
	}

	/* method that delete a project */
	public void deleteProject(Project project, boolean deleteLog, boolean deletePhpini) {
		getReentrantLockProjectsXML().deleteProject(project.getIdString());
		if (deleteLog) getReentrantLockLogServer().deleteLog(project);
		if (deletePhpini) deletePhpini(project);
	}

	public void updateProject(Project project, String oldIdName) throws ProjectException, FileException {
		getReentrantLockProjectsXML().updateProject(project);
		getReentrantLockLogServer().renameProjectLogDir(oldIdName, project.getIdAndName());
		renamePhpini(oldIdName, project.getIdAndName());
		project.getPhpiniPath();
	}

	/* metodo che ritorna stringa con info sistema */
	public String getSystemInfo(Double sysAdvrg) throws IOException {
		if (sysAdvrg == null) sysAdvrg = JutilasSys.getInstance().getSystemLoadAverage(1000);
		StringBuffer cpu = new StringBuffer("CPU: ").append(String.format("%.0f", sysAdvrg)).append("%");
		return new StringBuffer("OS: ").append(JutilasSys.getInstance().getOsName()).append("\n").append(
				"Arch: ").append(JutilasSys.getInstance().getOsArch()).append("\n").append(
				"User: ").append(JutilasSys.getInstance().getOsUser()).append("\n").append(cpu).toString();
	}

	/* metodo ritorna progetto da id */
	public Project getProjectById(Long id) throws ProjectException {
		if (id == null) return null;
		return reentrantLockProjectsXML.getProject(String.valueOf(id));
	}

	/* method that stop all running servers */
	public void stopAllRunningServer() throws ServerException, ProjectException, NumberFormatException, IOException {
		ArrayList<Project> projects = getRunningProjects();
		for (Project project : projects) stopServer(project);
	}

	/* metodo per avviare un server */
	public boolean startServer(Project project) throws IOException, ServerException, NumberFormatException, ProjectException {
		joggerDebug.writeLog("PHPito Manager - Starting Server");
		if (project == null) {
			joggerDebug.writeLog("PHPito Starting Server - Project == null");
			throw new ProjectException("Error!!! No server selected");
		}
		if (project.getServer().isRunning()) {
			joggerDebug.writeLog("PHPito Starting Server - SERVER ALREADY RUNNING");
			throw new ServerException("Error!!! Server already running");
		}
		if (!JutilasNet.getInstance().isAvailablePort(project.getServer().getPort())) {
			joggerDebug.writeLog("PHPito Starting Server - Port already in use");
			throw new ServerException("Error!!! Port already in use");
		}
		String phpini = project.getPhpiniPath();
		String script_start = phpini.isEmpty() ? SCRIPT_START_SERVER : SCRIPT_START_SERVER_INI;
		String[] cmndStart;

		/* create comand for windows or linux */
		if (JutilasSys.getInstance().isWindows()) cmndStart = new String[] {RUN, "/c" ,script_start, project.getServer().getAddressAndPort(), project.getServer().getPath(), phpini};
		else cmndStart = new String[] {RUN + script_start, project.getServer().getAddressAndPort(), project.getServer().getPath(), phpini};

		/* create a process */
		String regexError = ".*Failed to listen on " + project.getServer().getAddressAndPort() + ".*";
		String regexReasError = ".*reason: ([\\w\\s]{1,}).*";
		joggerDebug.writeLog("PHPito Starting Server - Execute command");
		ProcessBuilder pbStart = new ProcessBuilder(cmndStart);
		pbStart.environment().putAll(project.getServer().getEnvironmentVariables());;
		pbStart.directory(new File(DIR_SCRIPT));
		pbStart.redirectErrorStream(true);
		Process prcssStart = pbStart.start();

		/* prepare to read output */
		InputStreamReader isrStart = new InputStreamReader(prcssStart.getInputStream());
		BufferedReader br = new BufferedReader(isrStart);

		/* ############################################################################# */
		/* START LOCAL CLASS */
		/* ############################################################################# */

		/* loca class thread for read the server output */
		class ReadServerOutputThread extends Thread {
			Project project;
			BufferedReader bufferedReader;
			
			/* CONSTRUCT */
			private ReadServerOutputThread(Project project, BufferedReader bufferReader) {
				this.project = project;
				this.bufferedReader = bufferReader;
			}
			
			@Override
			public void run() {
				joggerDebug.writeLog("PHPito Start Thread Read Output Server");
				try {
					String outServer = null;
					while ((outServer = bufferedReader.readLine()) != null) {
						joggerDebug.writeLog("PHPito Thread Read Output Server - Write Log");
						reentrantLockLogServer.writeLog(outServer, project);
					}
				} catch (IOException e) {
					joggerDebug.writeLog("PHPito Thread Read Output Server - IOException: " + e.getMessage());
					try {
						joggerError.writeLog(e);
					} catch (LockLogException e1) {
						e1.printStackTrace();
					}
				} finally {
					joggerDebug.writeLog("PHPito Thread Read Output Server - Exit close BufferdReader");
					try {
						bufferedReader.close();
					} catch (IOException e) {
						try {
							joggerError.writeLog(e);
						} catch (LockLogException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}

		/* ############################################################################# */
		/* END LOCAL CLASS */
		/* ############################################################################# */
		
		/* start server and reading output */
		String stdoStart = null;
		Long pid = null;
		LocalDateTime maxTime = LocalDateTime.now().plusSeconds(5L);
		while ((stdoStart = (br.ready()) ? br.readLine() : "") != null) {
			joggerDebug.writeLog("PHPito Starting Server - Read Process Start Server - OUT: '" + stdoStart + "'");
			if (!stdoStart.isEmpty() && project.isLogActive()) reentrantLockLogServer.writeLog(stdoStart, project);
			if ((pid = getPIDServer(project.getServer())) != null) {
				/* case successfully start */
				joggerDebug.writeLog("PHPito Starting Server - Read Process Start Server Find PID OK");
				project.getServer().setProcessId(pid);
				reentrantLockProjectsXML.updateProject(project);
				if (project.isLogActive()) new ReadServerOutputThread(project, br).start();
				else br.close();
				return true;
			} else if (Pattern.matches(regexError, stdoStart)) {
				/* case find error */
				joggerDebug.writeLog("PHPito Starting Server - Read Process Start Server Find Error");
				String reasonError = "";
				Matcher matchReasError = Pattern.compile(regexReasError).matcher(stdoStart);
				if (matchReasError.find()) reasonError = matchReasError.group(1);
				br.close();
				flushRunningServers();
				throw new ServerException("Server Error!!! The start of the server has returned an error".concat((reasonError.isEmpty()) ? "\nMessagge: " + stdoStart :"\nCatch error: " + reasonError));
			} else if (LocalDateTime.now().isAfter(maxTime)) {
				/* case timeout error */
				joggerDebug.writeLog("PHPito Starting Server - Read Process Timeout Exit");
				br.close();
				flushRunningServers();
				throw new ServerException("Error Server!!! Start php server failed!");
			}
		}
		joggerDebug.writeLog("PHPito Starting Server - Fail Start Server");
		br.close();
		flushRunningServers();
		return false;
	}

	
	/* metodo per stoppare server */
	public boolean stopServer(Project project) throws IOException, ServerException, ProjectException, NumberFormatException {
		joggerDebug.writeLog("PHPito Stop Server");
		if (project == null) {
			joggerDebug.writeLog("PHPito Stop Server - Project == null");
			throw new ProjectException("Error!!! No server selected");
		}
		if (project.getServer().isRunning()) {
			joggerDebug.writeLog("PHPito Stop Server - Server is Running OK");
			String[] cmndStop;
			if (JutilasSys.getInstance().isWindows()) cmndStop = new String[] {RUN, "/c", SCRIPT_STOP_SERVER, project.getServer().getPIDString()};
			else cmndStop = new String[] {RUN + SCRIPT_STOP_SERVER, project.getServer().getPIDString()};
			String regexStop = ".*PHPito stopped server at.*";
			String regexFail = ".*Error!!! Fail to stop server.*";
			joggerDebug.writeLog("PHPito Stop Server - Execute Command");
			ProcessBuilder pb = new ProcessBuilder(cmndStop);
			pb.directory(new File(DIR_SCRIPT));
			pb.redirectErrorStream(true);
			Process prs = pb.start();
			
			InputStreamReader isr = new InputStreamReader(prs.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String stdo = null;
			while ((stdo = br.readLine()) != null) {
				joggerDebug.writeLog("PHPito Stop Server - OUT: " + stdo);
				if (project.isLogActive()) reentrantLockLogServer.writeLog(stdo, project);
				if (Pattern.matches(regexStop, stdo)) {
					/* case successfully */
					joggerDebug.writeLog("PHPito Stop Server - Stop OK -- RETURN TRUE");
					br.close();
					flushRunningServers();
					return true;
				} else if (Pattern.matches(regexFail, stdo)) {
					/* case fail */
					joggerDebug.writeLog("PHPito Stop Server -- FAIL STOP");
					br.close();
					flushRunningServers();
					throw new ServerException("Error!!! The stop of the server has returned an error.\nAddress: " + project.getServer().getAddress() + "\nPID: " + project.getServer().getPIDString());
				}
			}
			br.close();
			if (isServerRunning(project.getServer())) {
				joggerDebug.writeLog("PHPito Stop Server - Chech Server Running -- RETURN FALSE");
				return false;
			} else {
				joggerDebug.writeLog("PHPito Stop Server - Chech Server Running -- RETURN TRUE");
				flushRunningServers();
				return true;
			}
		} else {
			joggerDebug.writeLog("PHPito Stop Server - Server already Stopped");
			flushRunningServers();
			throw new ServerException("Error!!! Server already Stopped.");
		}
	}


	/* method to delete a php.ini file */
	public void deletePhpini(Project project) {
		joggerDebug.writeLog("Delete php.ini File - START");
		Jutilas.getInstance().recursiveDelete(project.getCustomPhpiniPath());
		joggerDebug.writeLog("Delete php.ini File - END");
	}

	/* method to delete all php.ini file */
	public void deleteAllPhpini() throws ProjectException {
		joggerDebug.writeLog("Delete All php.ini File - START");
		ArrayList<Project> projects = reentrantLockProjectsXML.getProjectsArray();
		for (Project project : projects) deletePhpini(project);
		joggerDebug.writeLog("Delete All php.ini File - END");
	}

	/* method to rename a php.ini file */
	public void renamePhpini(String name, String newName) throws FileException {
		joggerDebug.writeLog("Rename php.ini File - START");
		File file = new File(Project.getCustomPhpiniPath(name));
		if (file.exists()) Jutilas.getInstance().renameFile(file.getAbsolutePath(), Project.getCustomPhpinName(newName));
		joggerDebug.writeLog("Rename php.ini File - END");
	}

	/* metodo che aggiorna i server in esecuzione sull'xml */
	public void flushRunningServers() throws IOException, NumberFormatException, ProjectException {
		joggerDebug.writeLog("PHPito Write Running Server on XML - START");
		HashMap<String, Project> projectMap = reentrantLockProjectsXML.getProjectsMap();
		Project project = null;
		for (String id : projectMap.keySet()) {
			project = projectMap.get(id);
			if (project.getServer().isRunning()) project.getServer().setProcessId(getPIDServer(project.getServer()));
			else project.getServer().setProcessId(null);
			reentrantLockProjectsXML.updateProject(project);
		}
		joggerDebug.writeLog("PHPito Write Running Server on XML - END");
	}

	/* metodo che ritorna i server in esecuzione */
	public ArrayList<Project> getRunningProjects() throws ProjectException {
		joggerDebug.writeLog("PHPito Get Running Server - START");
		ArrayList<Project> serverList = new ArrayList<Project>();
//		flushRunningServers();
		HashMap<String, Project> projectMap = reentrantLockProjectsXML.getProjectsMap();
		for (String id : projectMap.keySet()) if (projectMap.get(id).getServer().isRunning()) serverList.add(projectMap.get(id));
		joggerDebug.writeLog("PHPito Get Running Server - END");
		return serverList;
	}

	/* ############################################################################# */
	/* END PUBLIC METHODS */
	/* ############################################################################# */

	/* ############################################################################# */
	/* START PRIVATE METHODS */
	/* ############################################################################# */

	/* metodo che ritorna il PID del server */
	private Long getPIDServer(Server server) throws NumberFormatException, IOException, ProjectException {
		joggerDebug.writeLog("PHPito Get PID Server - START");
		if (server == null) {
			joggerDebug.writeLog("PHPito Get PID Server - Server == null");
			throw new ProjectException("Error!!! No server selected");
		}
		String regexPID;
		String[] cmnd;
		if (JutilasSys.getInstance().isWindows()) {
			cmnd = new String[] {RUN, "/c", SCRIPT_PID_SERVER, server.getAddressAndPortRegex()};
			regexPID  = ".*TCP.*" + server.getAddressAndPortRegex() + ".*LISTENING[\\D]*([\\d]{1,})[\\D]*";
		} else {
			cmnd = new String[] {RUN + SCRIPT_PID_SERVER, server.getAddress(), server.getPortString()};
			regexPID = ".*LISTEN.*" + server.getAddressAndPortRegex() + ".*users.*php.*pid=([\\d]{1,})[,].*";
		}
		joggerDebug.writeLog("PHPito Get PID Server - Execute Comand");
		ProcessBuilder pb = new ProcessBuilder(cmnd);
		pb.directory(new File(DIR_SCRIPT));
		pb.redirectErrorStream(true);
		Process prcss = pb.start();
		InputStreamReader isr = new InputStreamReader(prcss.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		String stdo = null;
		Matcher match;
		while ((stdo = br.readLine()) != null) {
			joggerDebug.writeLog("PHPito Get PID Server - OUT:" + stdo);
			match = Pattern.compile(regexPID).matcher(stdo);
			if (match.find()) {
				joggerDebug.writeLog("PHPito Get PID Server - PID Find OK");
				br.close();
				return Long.valueOf(match.group(1));
			}
		}
		joggerDebug.writeLog("PHPito Get PID Server - PID Find FAIL");
		br.close();
		return null;
	}

	/* metodo che controlla se il server e' in esecuzione */
	private boolean isServerRunning(Server server) throws IOException {
		joggerDebug.writeLog("PHPito is Server Running - START");
		if (server.getProcessID() == null) {
			joggerDebug.writeLog("PHPito is Server Running - No PID Find for Server -- Return FALSE");
			return false;
		}
		String[] cmnd;
		String regex;
		if (JutilasSys.getInstance().isWindows()) {
			cmnd = new String[] {RUN, "/c", SCRIPT_CHECK_SERVER, server.getAddressAndPortRegex(), server.getPIDString()};
			regex = ".*TCP.*" + server.getAddressAndPortRegex() + ".*LISTENING[\\D]*" + server.getPIDString() + "[\\D]*.*";
		} else {
			cmnd = new String[] {RUN + SCRIPT_CHECK_SERVER, server.getAddress(), server.getPortString(), server.getPIDString()};
			// regex = ".*tcp.*" + server.getAddressAndPortRegex() + ".*LISTEN[\\D]*" + server.getPIDString() + "/php.*";
			regex = ".*LISTEN.*" + server.getAddressAndPortRegex() + ".*users.*php.*pid=" + server.getPIDString() + "[,].*";
		}
		joggerDebug.writeLog("PHPito is Server Running - Execute Command");
		ProcessBuilder pb = new ProcessBuilder(cmnd);
		pb.directory(new File(DIR_SCRIPT));
		pb.redirectErrorStream(true);
		Process prcss = pb.start();

		InputStreamReader isr = new  InputStreamReader(prcss.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		String stdo = null;
		while ((stdo = br.readLine()) != null) {
			joggerDebug.writeLog("PHPito is Server Running - OUT: " + stdo);
			if (Pattern.matches(regex, stdo)) {
				joggerDebug.writeLog("PHPito is Server Running -- Return TRUE");
				br.close();
				return true;
			}
		}
		joggerDebug.writeLog("PHPito is Server Running -- Return FALSE");
		br.close();
		return false;
	}

	/* ############################################################################# */
	/* END PRIVATE METHODS */
	/* ############################################################################# */
}