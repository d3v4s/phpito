package it.phpito.core.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import it.jogger.core.Jogger;
import it.jogger.exception.FileLogException;
import it.jogger.exception.LockLogException;
import it.jutilas.core.Jutilas;
import it.jutilas.exception.FileException;
import it.phpito.core.PHPitoManager;
import it.phpito.data.Project;

public class ReentrantLockLogServer {
	private ReentrantLock reentrantLock = new ReentrantLock();

	/* metodo per scriver logo del progetto */
	public void writeLog(String write, Project project) {
		if (project != null && project.isLogActive()) {
			if (PHPitoManager.getInstance().isDebug())
				try {
					PHPitoManager.getInstance().getJoggerDebug().writeLog("Write Log Starting");
				} catch (FileLogException | LockLogException e) {
					e.printStackTrace();
				}
			try {
				if (reentrantLock.tryLock(30, TimeUnit.SECONDS)) {
					if (PHPitoManager.getInstance().isDebug())
						try {
							PHPitoManager.getInstance().getJoggerDebug().writeLog("Write Log -- LOCK OK");
						} catch (FileLogException | LockLogException e) {
							e.printStackTrace();
						}
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
						if (PHPitoManager.getInstance().isDebug())
							try {
								PHPitoManager.getInstance().getJoggerDebug().writeLog("Write Log -- UNLOCK OK");
							} catch (FileLogException | LockLogException e) {
								e.printStackTrace();
							}
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
			if (PHPitoManager.getInstance().isDebug())
				try {
					PHPitoManager.getInstance().getJoggerDebug().writeLog("Read Log Starting");
				} catch (FileLogException | LockLogException e) {
					e.printStackTrace();
				}
			try {
				if (reentrantLock.tryLock(30, TimeUnit.SECONDS)) {
					if (PHPitoManager.getInstance().isDebug())
						try {
							PHPitoManager.getInstance().getJoggerDebug().writeLog("Read Log -- LOCK OK");
						} catch (FileLogException | LockLogException e) {
							e.printStackTrace();
						}
					try {
						String pathFileLog = Jogger.getLogFilePath("server", new String[] {"server", project.getIdAndName()});
						rows = Jutilas.getInstance().getLastRowFile(pathFileLog, numRows);
						if (rows.isEmpty() && Pattern.matches(".*log[_].*[-][0]{6}\\.log$", pathFileLog)) {
							if (PHPitoManager.getInstance().isDebug())
								try {
									PHPitoManager.getInstance().getJoggerDebug().writeLog("Read Log -- LOG EMPTY");
								} catch (FileLogException | LockLogException e) {
									e.printStackTrace();
								}
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
						if (PHPitoManager.getInstance().isDebug())
							try {
								PHPitoManager.getInstance().getJoggerDebug().writeLog("Read Log -- UNLOCK OK");
							} catch (FileLogException | LockLogException e) {
								e.printStackTrace();
							}
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
			if (PHPitoManager.getInstance().isDebug())
				try {
					PHPitoManager.getInstance().getJoggerDebug().writeLog("Delete Log Starting");
				} catch (FileLogException | LockLogException e) {
					e.printStackTrace();
				}
			try {
				if (reentrantLock.tryLock(30 , TimeUnit.SECONDS)) {
					if (PHPitoManager.getInstance().isDebug())
						try {
							PHPitoManager.getInstance().getJoggerDebug().writeLog("Delete Log -- LOCK OK");
						} catch (FileLogException | LockLogException e) {
							e.printStackTrace();
						}
					try {
						String dirLoString = Jogger.getLogDirPath(new String[] {"server", project.getIdAndName()});
						Jutilas.getInstance().recursiveDelete(dirLoString);
					} finally {
						reentrantLock.unlock();
						if (PHPitoManager.getInstance().isDebug())
							try {
								PHPitoManager.getInstance().getJoggerDebug().writeLog("Delete Log -- UNLOCK OK");
							} catch (FileLogException | LockLogException e) {
								e.printStackTrace();
							}
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void renameDirProjectLog(String name, String newName) {
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("Rename Log Starting");
			} catch (FileLogException | LockLogException e) {
				e.printStackTrace();
			}
		try {
			if (reentrantLock.tryLock(30,TimeUnit.SECONDS)) {
				if (PHPitoManager.getInstance().isDebug())
					try {
						PHPitoManager.getInstance().getJoggerDebug().writeLog("Rename Log -- LOCK OK");
					} catch (FileLogException | LockLogException e) {
						e.printStackTrace();
					}
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
					if (PHPitoManager.getInstance().isDebug())
						try {
							PHPitoManager.getInstance().getJoggerDebug().writeLog("Rename Log -- UNLOCK OK");
						} catch (FileLogException | LockLogException e) {
							e.printStackTrace();
						}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
