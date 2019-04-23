package it.phpito.view.thread;

import java.time.LocalDateTime;

import org.w3c.dom.DOMException;

import it.as.utils.core.LogErrorAS;
import it.as.utils.exception.FileException;
import it.phpito.controller.PHPitoManager;
import it.phpito.data.Project;
import it.phpito.view.shell.ShellPHPito;

public class WriterTerminalThread extends Thread {
	private ShellPHPito shellPHPito;
	private Project project;
	public WriterTerminalThread(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
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
				lastMod = PHPitoManager.getInstance().getReentrantLockLogServer().getLocalDateTimeLastModifyLogServer((project));
				if ((shellPHPito.getIdProjectSelect() != null && id != shellPHPito.getIdProjectSelect()) ||
						lastPrint.isBefore(lastMod)) {
//					project = shellPHPito.getProjectSelect();
					id = shellPHPito.getIdProjectSelect();
					shellPHPito.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							String out = PHPitoManager.getInstance().getReentrantLockLogServer().readLog(project, 10);
							shellPHPito.getLogOutText().setText(out);
						}
					});
					lastPrint = LocalDateTime.now();
				}
			} catch (DOMException | FileException e) {
				e.printStackTrace();
				try {
					LogErrorAS.getInstance().writeLog(e, PHPitoManager.NAME);
				} catch (FileException e1) {
					e1.printStackTrace();
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
	}
}
