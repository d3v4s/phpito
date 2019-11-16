package phpito.core.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import exception.FileLogException;
import exception.LockLogException;
import jogger.Jogger;
import jutilas.core.Jutilas;
import jutilas.exception.FileException;
import phpito.core.PHPitoManager;
import phpito.data.Project;

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
					} catch (FileLogException | LockLogException e) {
						e.printStackTrace();
						try {
							PHPitoManager.getInstance().getJoggerError().writeLog(e);
						} catch (FileLogException | LockLogException e1) {
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
		String rows = "PHPito -- PHP Server Manager\n"
						+ "Developed by Andrea Serra";
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
					} catch (FileLogException | FileException e) {
						e.printStackTrace();
						try {
							PHPitoManager.getInstance().getJoggerError().writeLog(e);
						} catch (FileLogException | LockLogException e1) {
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

	/* method to rename a log directory of project */
	public void renameProjectLogDir(String name, String newName) {
		PHPitoManager.getInstance().getJoggerDebug().writeLog("Rename Log Starting");
		try {
			if (reentrantLock.tryLock(30,TimeUnit.SECONDS)) {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("Rename Log -- LOCK OK");
				try {
					Jutilas.getInstance().renameFile(Jogger.getLogDirPath("server", name), newName);
				} catch (FileException e) {
					try {
						PHPitoManager.getInstance().getJoggerError().writeLog(e);
					} catch (FileLogException | LockLogException e1) {
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
