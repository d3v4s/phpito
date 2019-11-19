package phpito.view.thread;

import java.io.File;
import java.time.LocalDateTime;

import com.ibm.icu.text.SimpleDateFormat;

import exception.FileLogException;
import exception.LockLogException;
import jogger.Jogger;
import phpito.core.PHPitoConf;
import phpito.core.PHPitoManager;
import phpito.core.lock.ReentrantLockServerLog;
import phpito.data.Project;
import phpito.view.shell.ShellPHPito;

/**
 * Class Thread for write in log monitor
 * @author Andrea Serra
 *
 */
public class WriterLogMonitorThread extends Thread {
	private ShellPHPito shellPHPito;
	private Project project;
	private ReentrantLockServerLog reentrantLockLogServer;
	private boolean isDeleting = false;

	/* CONSTRUCT */
	public WriterLogMonitorThread(ShellPHPito shellPHPito, ReentrantLockServerLog reentrantLockLogServer) {
		super();
		this.shellPHPito = shellPHPito;
		this.reentrantLockLogServer = reentrantLockLogServer;
	}

	/* ################################################################################# */
	/* START GET AND SET */
	/* ################################################################################# */

//	public boolean isDeleting() {
//		return isDeleting;
//	}
//	public void setDeleting(boolean isDeleting) {
//		this.isDeleting = isDeleting;
//	}

	/* ################################################################################# */
	/* END GET AND SET */
	/* ################################################################################# */

	@Override
	public void run() {
		LocalDateTime lastPrint = LocalDateTime.MIN;
		Long id = shellPHPito.getIdProjectSelect();
		project = shellPHPito.getProjectSelect();
		LocalDateTime lastMod;
		while (!shellPHPito.isDisposed()) {
			try {
				lastMod = getLocalDateTimeLastModifyLogServer(project);
				if ((shellPHPito.getIdProjectSelect() != null && (id != shellPHPito.getIdProjectSelect()) || lastPrint.isBefore(lastMod))) {
					project = shellPHPito.getProjectSelect();
					id = shellPHPito.getIdProjectSelect();
					shellPHPito.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							String out = reentrantLockLogServer.readLog(project, PHPitoConf.getInstance().getRowLogConf());
							shellPHPito.getLogOutText().setText(out);
							shellPHPito.getLogOutText().setSelection(out.length());
						}
					});
					lastPrint = LocalDateTime.now();
//				if (!isDeleting) {
//					}
				}
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
				try {
					PHPitoManager.getInstance().getJoggerError().writeLog(e);
				} catch (FileLogException | LockLogException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/* method that get the date time of server log file last modify */
	private LocalDateTime getLocalDateTimeLastModifyLogServer(Project project) throws FileLogException {
		if (project == null) return LocalDateTime.MAX;
		File logFile = Jogger.getLogFileIfExists("server", new String[] {"server", project.getIdAndName()});
		if (logFile == null) return LocalDateTime.MIN;
		long lastMod = logFile.lastModified();
		Integer year = Integer.valueOf(new SimpleDateFormat("yyyy").format(lastMod));
		Integer month = Integer.valueOf(new SimpleDateFormat("MM").format(lastMod));
		Integer dayOfMonth = Integer.valueOf(new SimpleDateFormat("dd").format(lastMod));
		Integer hour = Integer.valueOf(new SimpleDateFormat("HH").format(lastMod));
		Integer minute = Integer.valueOf(new SimpleDateFormat("mm").format(lastMod));
		Integer second = Integer.valueOf(new SimpleDateFormat("ss").format(lastMod));
		return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
	}
}
