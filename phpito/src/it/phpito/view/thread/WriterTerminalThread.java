package it.phpito.view.thread;

import java.time.LocalDateTime;

import org.w3c.dom.DOMException;

import it.as.utils.exception.FileException;
import it.phpito.controller.PHPitoManager;
import it.phpito.view.shell.ShellPHPito;

public class WriterTerminalThread extends Thread {
	private ShellPHPito shellPHPito;
	
	public WriterTerminalThread(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void run() {
		LocalDateTime lastPrint = LocalDateTime.MIN;
		Long idProject = shellPHPito.getIdProjectSelect();
		while (!shellPHPito.isDisposed()) {
			try {
				if (idProject != shellPHPito.getIdProjectSelect() ||
						lastPrint.isBefore(PHPitoManager.getInstance().getLocalDateTimeLastModifyLogServer(shellPHPito.getIdProjectSelect()))) {
					idProject = shellPHPito.getIdProjectSelect();
					shellPHPito.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							String out = PHPitoManager.getInstance().getReentrantLockLogServer().readLog(shellPHPito.getProjectSeclect(), 10);
							shellPHPito.getLogOutText().setText(out);
						}
					});
					lastPrint = LocalDateTime.now();
				}
			} catch (DOMException | FileException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
	}
}
