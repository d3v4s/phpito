package it.phpito.view.thread;

import java.io.File;
import java.time.LocalDateTime;

import com.ibm.icu.text.SimpleDateFormat;

import it.as.utils.core.LogErrorAS;
import it.as.utils.core.LoggerAS;
import it.as.utils.exception.FileException;
import it.phpito.controller.PHPitoConf;
import it.phpito.controller.PHPitoManager;
import it.phpito.controller.lock.ReentrantLockLogServer;
import it.phpito.data.Project;
import it.phpito.view.shell.ShellPHPito;

public class WriterLogMonitorThread extends Thread {
	private ShellPHPito shellPHPito;
	private Project project;
	private ReentrantLockLogServer reentrantLockLogServer;
	public WriterLogMonitorThread(ShellPHPito shellPHPito, ReentrantLockLogServer reentrantLockLogServer) {
		super();
		this.shellPHPito = shellPHPito;
		this.reentrantLockLogServer = reentrantLockLogServer;
	}

	@Override
	public void run() {
		LocalDateTime lastMod;
		LocalDateTime lastPrint = LocalDateTime.MIN;
		Long id = shellPHPito.getIdProjectSelect();
		project = shellPHPito.getProjectSelect();
		while (!shellPHPito.isDisposed()) {
			try {
				project = shellPHPito.getProjectSelect();
				lastMod = getLocalDateTimeLastModifyLogServer(project);
				if ((shellPHPito.getIdProjectSelect() != null && id != shellPHPito.getIdProjectSelect()) ||
						lastPrint.isBefore(lastMod)) {
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
				}
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
				try {
					LogErrorAS.getInstance().writeLog(e, PHPitoManager.NAME);
				} catch (FileException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	private LocalDateTime getLocalDateTimeLastModifyLogServer(Project project) throws FileException {
		if (project == null)
			return LocalDateTime.MAX;
		File logFile = LoggerAS.getInstance().getFileLog("server", null, new String[] {"server", project.getIdAndName()});
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
