package it.phpito.view;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import it.jaswt.core.Jaswt;
import it.jun.core.Jun;
import it.jun.exception.FileLockException;
import it.phpito.controller.PHPitoManager;
import it.phpito.view.shell.ShellPHPito;

public class LuncherPHPito {
	private ShellPHPito shellPHPito;
	private Display display;
	
	public static void main(String[] args) {
		LuncherPHPito luncherPHPito = new LuncherPHPito();

		luncherPHPito.open();
	}
	
	/* metodo lancio finestra */
	public void open() {
		try {
			Jun.getInstance().tryLock();
		} catch (Exception e) {
			String msg = "Attenzione PHPito e' gia' in esecuzione!!!\n"
							+ "Forzare la seconda istanza???";
			int resp = Jaswt.getInstance().lunchMB(new Shell(), SWT.YES | SWT.NO, "Attenzione!!!", msg);

			if (resp == SWT.YES)
				try {
					Jun.getInstance().forceLock();
				} catch (IOException | FileLockException e1) {
					Jaswt.getInstance().lunchMBError(new Shell(), e1, PHPitoManager.NAME);
				}
			else
				return;
				
		}
		display = Display.getDefault();
		shellPHPito = new ShellPHPito(display);
		shellPHPito.createContents();
		shellPHPito.open();
		shellPHPito.layout();
		while (!shellPHPito.isDisposed()) {
			try {
				if (!display.readAndDispatch())
					display.sleep();
			} catch (Exception e) {
				Jaswt.getInstance().lunchMBError(new Shell(), e, PHPitoManager.NAME);
			}
		}
		Jun.getInstance().unlock();
	}
}