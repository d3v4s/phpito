package it.phpito.view;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import it.jaswt.core.Jaswt;
import it.jun.core.Jun;
import it.jun.exception.FileLockException;
import it.phpito.core.PHPitoManager;
import it.phpito.view.shell.ShellPHPito;

public class LauncherPHPito {
	private ShellPHPito shellPHPito;
	private Display display;

	/* main */
	public static void main(String[] args) {
		PHPitoManager.getInstance().getJoggerDebug().setDebug(false);
		for (String arg : args)
			if (arg.equals("debug"))
				PHPitoManager.getInstance().getJoggerDebug().setDebug(true);

		PHPitoManager.getInstance().getJoggerDebug().writeLog("DEBUG MODE ON");
		
		LauncherPHPito luncherPHPito = new LauncherPHPito();

		luncherPHPito.open();
		
		PHPitoManager.getInstance().getJoggerDebug().writeLog("PHPito CLOSE");
		System.exit(0);
	}
	
	/* method that open window */
	public void open() {
		PHPitoManager.getInstance().getJoggerDebug().writeLog("Starting phpito");

		try {
			Jun.getInstance().tryLock();
		} catch (Exception e) {
			String msg = "Attenzione PHPito e' gia' in esecuzione!!!\n"
							+ "Forzare la seconda istanza???";
			PHPitoManager.getInstance().getJoggerDebug().writeLog("PHPito Already Running!!!");
			int resp = Jaswt.getInstance().lunchMB(new Shell(), SWT.YES | SWT.NO, "Attenzione!!!", msg);

			if (resp == SWT.YES)
				try {
					Jun.getInstance().forceLock();
					PHPitoManager.getInstance().getJoggerDebug().writeLog("PHPito Force Second Instance!!!");
				} catch (IOException | FileLockException e1) {
					Jaswt.getInstance().lunchMBError(new Shell(), e1, PHPitoManager.getInstance().getJoggerError());
				}
			else return;
		}
		display = Display.getDefault();
		shellPHPito = new ShellPHPito(display);
		shellPHPito.createContents();
		shellPHPito.open();
		shellPHPito.layout();
		PHPitoManager.getInstance().getJoggerDebug().writeLog("PHPito Display Start Read and Dispatch");
		while (!shellPHPito.isDisposed()) {
			try {
				if (!display.readAndDispatch()) display.sleep();
			} catch (Exception e) {
				Jaswt.getInstance().lunchMBError(new Shell(), e, PHPitoManager.getInstance().getJoggerError());
			}
		}

		PHPitoManager.getInstance().getJoggerDebug().writeLog("PHPito Unlock");
		Jun.getInstance().unlock();
	}
}