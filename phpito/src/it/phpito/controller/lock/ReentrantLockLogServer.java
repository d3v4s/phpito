package it.phpito.controller.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import it.as.utils.core.LoggerAS;
import it.as.utils.core.UtilsAS;
import it.as.utils.exception.FileException;
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

	public ReentrantLockLogServer() {
	}

	/* metodo per scriver logo del progetto */
	public void writeLog(String write, Project project) {
		try {
			if (reentrantLock.tryLock(30, TimeUnit.SECONDS)) {
				try {
					LoggerAS.getInstance().writeLog(write, project.getName(), null, new String[] {"server", project.getIdAndName()});
				} catch (FileException e) {
					e.printStackTrace();
				} finally {
					reentrantLock.unlock();
				}
			}
		} catch (InterruptedException e) {
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
						String pathFileLog = LoggerAS.getInstance().getPathFileLog(project.getName(), null, new String[] {"server", project.getIdAndName()});
						rows = UtilsAS.getInstance().getLastRowFile(pathFileLog, numRows);
						if (rows.isEmpty() && Pattern.matches(".*[/]log[_].*[-][0]{6}\\.log$", pathFileLog))
							rows = "404 Log not found";
					} catch (FileException e) {
						e.printStackTrace();
					} finally {
						reentrantLock.unlock();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return rows;
	}

}
