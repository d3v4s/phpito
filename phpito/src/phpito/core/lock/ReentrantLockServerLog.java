package phpito.core.lock;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import exception.LockLogException;
import exception.LogFileException;
import jogger.Jogger;
import jutilas.exception.FileException;
import jutilas.utils.Jutilas;
import phpito.core.PHPitoManager;
import phpito.data.Project;
import phpito.exception.ProjectException;

/**
 * Class for write the server log and implementing the ReentrantLock
 * @author Andrea Serra
 *
 */
public class ReentrantLockServerLog {
	private ReentrantLock reentrantLock = new ReentrantLock();

	/* metodo per scrivere i log del server */
	public void writeLog(String write, Project project) {
		if (project != null && project.isLogActive()) {
			PHPitoManager.getInstance().getJoggerDebug().writeLog("Write Log Starting");
			try {
				if (reentrantLock.tryLock(30, TimeUnit.SECONDS)) {
					PHPitoManager.getInstance().getJoggerDebug().writeLog("Write Log -- LOCK OK");
					try {
						Jogger.writeLog(write, "server", new String[] {"server", project.getIdAndName()});
					} catch (LockLogException e) {
						try {
							PHPitoManager.getInstance().getJoggerError().writeLog(e);
						} catch (LockLogException e1) {
							e1.printStackTrace();
						}
					} finally {
						reentrantLock.unlock();
						PHPitoManager.getInstance().getJoggerDebug().writeLog("Write Log -- UNLOCK OK");
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/* metodo che ritorna ultime righe del log del progetto */
	public String readLog(Project project, int numRows) {
		String rows = "PHPito -- PHP Server Manager\nDeveloped by Andrea Serra (DevAS)";
		if (project != null && project.isLogActive()) {
			PHPitoManager.getInstance().getJoggerDebug().writeLog("Read Log Starting");
			try {
				if (reentrantLock.tryLock(30, TimeUnit.SECONDS)) {
					PHPitoManager.getInstance().getJoggerDebug().writeLog("Read Log -- LOCK OK");
					try {
						String pathFileLog = Jogger.getLogFilePath("server", new String[] {"server", project.getIdAndName()});
						rows = Jutilas.getInstance().getLastRowsFile(pathFileLog, numRows);
						if (rows.isEmpty() && Pattern.matches(".*log[_].*[-][0]{6}\\.log$", pathFileLog)) {
							PHPitoManager.getInstance().getJoggerDebug().writeLog("Read Log -- LOG EMPTY");
							rows = "404 Log not found";
						}
					} catch (FileException | LogFileException e) {
						try {
							PHPitoManager.getInstance().getJoggerError().writeLog(e);
						} catch (LockLogException e1) {
							e1.printStackTrace();
						}
					} finally {
						reentrantLock.unlock();
						PHPitoManager.getInstance().getJoggerDebug().writeLog("Read Log -- UNLOCK OK");
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return rows;
	}

	/* metodo che elimina log */
	public void deleteLog(Project project) {
		if (project != null) {
			PHPitoManager.getInstance().getJoggerDebug().writeLog("Delete Log Starting");
			try {
				if (reentrantLock.tryLock(30 , TimeUnit.SECONDS)) {
					PHPitoManager.getInstance().getJoggerDebug().writeLog("Delete Log -- LOCK OK");
					try {
						String dirLogString = Jogger.getLogDirPath(new String[] {"server", project.getIdAndName()});
						Jutilas.getInstance().recursiveDelete(dirLogString);
					} finally {
						reentrantLock.unlock();
						PHPitoManager.getInstance().getJoggerDebug().writeLog("Delete Log -- UNLOCK OK");
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/* method to delete all projects logs */
	public void deleteAllLog() throws ProjectException {
		PHPitoManager.getInstance().getJoggerDebug().writeLog("Delete All Log - START");
		ArrayList<Project> projects = PHPitoManager.getInstance().getReentrantLockProjectsXML().getProjectsArray();
		for (Project project : projects) deleteLog(project);
		PHPitoManager.getInstance().getJoggerDebug().writeLog("Delete All Log - END");
	}

	/* method to rename a log directory of project */
	public void renameProjectLogDir(String name, String newName) {
		PHPitoManager.getInstance().getJoggerDebug().writeLog("Rename Log - START");
		try {
			if (reentrantLock.tryLock(30,TimeUnit.SECONDS)) {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("Rename Log -- LOCK OK");
				try {
					Jutilas.getInstance().renameFile(Jogger.getLogDirPath("server", name), newName);
				} catch (FileException e) {
					try {
						PHPitoManager.getInstance().getJoggerError().writeLog(e);
					} catch (LockLogException e1) {
						e1.printStackTrace();
					}
				} finally {
					reentrantLock.unlock();
					PHPitoManager.getInstance().getJoggerDebug().writeLog("Rename Log -- UNLOCK OK");
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
