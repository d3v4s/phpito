package it.phpito.controller.lock;

import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import com.ibm.icu.text.SimpleDateFormat;

import it.as.utils.core.LogErrorAS;
import it.as.utils.core.LoggerAS;
import it.as.utils.core.UtilsAS;
import it.as.utils.exception.FileException;
import it.phpito.controller.PHPitoManager;
import it.phpito.data.Project;

public class ReentrantLockLogServer {
	private ReentrantLock reentrantLock = new ReentrantLock();
//	private final String[] bannerList = new String[] {
//			
//			
//			"  ____  _   _ ____  _ _        \n" + 
//			" |  _ \\| | | |  _ \\(_) |_ ___  \n" + 
//			" | |_) | |_| | |_) | | __/ _ \\ \n" + 
//			" |  __/|  _  |  __/| | || (_) |\n" + 
//			" |_|   |_| |_|_|   |_|\\__\\___/ \n" + 
//			"                               ",
//			
//			" ______   __  __     ______   __     ______   ______    \n" + 
//			"/\\  == \\ /\\ \\_\\ \\   /\\  == \\ /\\ \\   /\\__  _\\ /\\  __ \\   \n" + 
//			"\\ \\  _-/ \\ \\  __ \\  \\ \\  _-/ \\ \\ \\  \\/_/\\ \\/ \\ \\ \\/\\ \\  \n" + 
//			" \\ \\_\\    \\ \\_\\ \\_\\  \\ \\_\\    \\ \\_\\    \\ \\_\\  \\ \\_____\\ \n" + 
//			"  \\/_/     \\/_/\\/_/   \\/_/     \\/_/     \\/_/   \\/_____/ \n" + 
//			"                                                        ",
//			
//			" _______  ____  ____  _______   _   _           \n" + 
//			"|_   __ \\|_   ||   _||_   __ \\ (_) / |_         \n" + 
//			"  | |__) | | |__| |    | |__) |__ `| |-' .--.   \n" + 
//			"  |  ___/  |  __  |    |  ___/[  | | | / .'`\\ \\ \n" + 
//			" _| |_    _| |  | |_  _| |_    | | | |,| \\__. | \n" + 
//			"|_____|  |____||____||_____|  [___]\\__/ '.__.'  \n" + 
//			"                                                ",
//
//			"  _____  _    _ _____ _ _        \n" + 
//			" |  __ \\| |  | |  __ (_) |       \n" + 
//			" | |__) | |__| | |__) || |_ ___  \n" + 
//			" |  ___/|  __  |  ___/ | __/ _ \\ \n" + 
//			" | |    | |  | | |   | | || (_) |\n" + 
//			" |_|    |_|  |_|_|   |_|\\__\\___/ \n" + 
//			"                                 \n" + 
//			"                                 ",
//			
//			" ██▓███   ██░ ██  ██▓███   ██▓▄▄▄█████▓ ▒█████  \n" + 
//			"▓██░  ██▒▓██░ ██▒▓██░  ██▒▓██▒▓  ██▒ ▓▒▒██▒  ██▒\n" + 
//			"▓██░ ██▓▒▒██▀▀██░▓██░ ██▓▒▒██▒▒ ▓██░ ▒░▒██░  ██▒\n" + 
//			"▒██▄█▓▒ ▒░▓█ ░██ ▒██▄█▓▒ ▒░██░░ ▓██▓ ░ ▒██   ██░\n" + 
//			"▒██▒ ░  ░░▓█▒░██▓▒██▒ ░  ░░██░  ▒██▒ ░ ░ ████▓▒░\n" + 
//			"▒▓▒░ ░  ░ ▒ ░░▒░▒▒▓▒░ ░  ░░▓    ▒ ░░   ░ ▒░▒░▒░ \n" + 
//			"░▒ ░      ▒ ░▒░ ░░▒ ░      ▒ ░    ░      ░ ▒ ▒░ \n" + 
//			"░░        ░  ░░ ░░░        ▒ ░  ░      ░ ░ ░ ▒  \n" + 
//			"          ░  ░  ░          ░               ░ ░  \n" + 
//			"                                                "
//	};
//	private final int randomBanner = new Random().nextInt(bannerList.length);

	/* metodo per scriver logo del progetto */
	public void writeLog(String write, Project project) {
		if (project != null) {
			try {
				if (reentrantLock.tryLock(30, TimeUnit.SECONDS)) {
					try {
						LoggerAS.getInstance().writeLog(write, "server", null, new String[] {"server", project.getIdAndName()});
					} catch (FileException e) {
						try {
							LogErrorAS.getInstance().writeLog(e, PHPitoManager.NAME);
						} catch (FileException e1) {
							e1.printStackTrace();
						}
					} finally {
						reentrantLock.unlock();
					}
				}
			} catch (InterruptedException e) {
			}
		}
	}

	/* metodo che ritorna ultime righe del log del progetto */
	public String readLog(Project project, int numRows) {
		String rows = "PHPito -- PHP Server Manager\n"
						+ "Developed by Andrea Serra";
		if (project != null) {
			try {
				if (reentrantLock.tryLock(30, TimeUnit.SECONDS)) {
					try {
						String pathFileLog = LoggerAS.getInstance().getPathFileLog("server", null, new String[] {"server", project.getIdAndName()});
						rows = UtilsAS.getInstance().getLastRowFile(pathFileLog, numRows);
						if (rows.isEmpty() && Pattern.matches(".*[/]log[_].*[-][0]{6}\\.log$", pathFileLog))
							rows = "404 Log not found";
					} catch (FileException e) {
						try {
							LogErrorAS.getInstance().writeLog(e, PHPitoManager.NAME);
						} catch (FileException e1) {
							e1.printStackTrace();
						}
					} finally {
						reentrantLock.unlock();
					}
				}
			} catch (InterruptedException e) {
			}
		}
		return rows;
	}

	/* metodo che elimina log */
	public void deleteLog(Project project) {
		if (project != null) {
			try {
				if (reentrantLock.tryLock(30 , TimeUnit.SECONDS)) {
					try {
						String dirLoString = LoggerAS.getInstance().getPathDirLog(new String[] {"server", project.getIdAndName()});
						UtilsAS.getInstance().recursiveDelete(dirLoString);
					} finally {
						reentrantLock.unlock();
					}
				}
			} catch (InterruptedException e) {
			}
		}
	}
	
	public void renameDirProjectLog(String name, String newName) {
		try {
			if (reentrantLock.tryLock(30,TimeUnit.SECONDS)) {
				try {
					UtilsAS.getInstance().renameFile(LoggerAS.getInstance().getPathDirLog("server", name), newName);
				} catch (FileException e) {
					try {
						LogErrorAS.getInstance().writeLog(e, PHPitoManager.NAME);
					} catch (FileException e1) {
						e1.printStackTrace();
					}
				} finally {
					reentrantLock.unlock();
				}
			}
		} catch (InterruptedException e) {
		}
	}
	

	public LocalDateTime getLocalDateTimeLastModifyLogServer(Project project) throws FileException {
		if (project == null)
			return LocalDateTime.MAX;
		File logFile = LoggerAS.getInstance().getFileLog("server", null, new String[] {"server", project.getIdAndName()});
		Integer year = Integer.valueOf(new SimpleDateFormat("yyyy").format(logFile.lastModified()));
		Integer month = Integer.valueOf(new SimpleDateFormat("MM").format(logFile.lastModified()));
		Integer dayOfMonth = Integer.valueOf(new SimpleDateFormat("dd").format(logFile.lastModified()));
		Integer hour = Integer.valueOf(new SimpleDateFormat("HH").format(logFile.lastModified()));
		Integer minute = Integer.valueOf(new SimpleDateFormat("mm").format(logFile.lastModified()));
		Integer second = Integer.valueOf(new SimpleDateFormat("ss").format(logFile.lastModified()));
		return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
	}

}
