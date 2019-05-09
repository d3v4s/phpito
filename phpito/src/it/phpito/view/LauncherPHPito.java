package it.phpito.view;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import it.jaswt.core.Jaswt;
import it.jogger.exception.FileLogException;
import it.jogger.exception.LockLogException;
import it.jun.core.Jun;
import it.jun.exception.FileLockException;
import it.phpito.core.PHPitoManager;
import it.phpito.view.shell.ShellPHPito;

public class LauncherPHPito {
	private ShellPHPito shellPHPito;
	private Display display;
	
	public static void main(String[] args) {
		for (String arg : args) {
			if (arg.equals("debug"))
				PHPitoManager.getInstance().setDebug(true);
		}
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("DEBUG MODE ON");
			} catch (FileLogException | LockLogException e) {
				e.printStackTrace();
			}
		
		LauncherPHPito luncherPHPito = new LauncherPHPito();

		luncherPHPito.open();
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("PHPito CLOSE");
			} catch (FileLogException | LockLogException e) {
				e.printStackTrace();
			}
		System.exit(0);
	}
	
	/* metodo lancio finestra */
	public void open() {
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("Starting phpito");
			} catch (FileLogException | LockLogException e) {
				e.printStackTrace();
			}

		try {
			Jun.getInstance().tryLock();
		} catch (Exception e) {
			String msg = "Attenzione PHPito e' gia' in esecuzione!!!\n"
							+ "Forzare la seconda istanza???";
			if (PHPitoManager.getInstance().isDebug())
				try {
					PHPitoManager.getInstance().getJoggerDebug().writeLog("PHPito Already Running!!!");
				} catch (FileLogException | LockLogException e1) {
					e1.printStackTrace();
				}
			int resp = Jaswt.getInstance().lunchMB(new Shell(), SWT.YES | SWT.NO, "Attenzione!!!", msg);

			if (resp == SWT.YES)
				try {
					Jun.getInstance().forceLock();
					try {
						PHPitoManager.getInstance().getJoggerDebug().writeLog("PHPito Force Second Instance!!!");
					} catch (FileLogException | LockLogException e1) {
						e1.printStackTrace();
					}
				} catch (IOException | FileLockException e1) {
					Jaswt.getInstance().lunchMBError(new Shell(), e1, PHPitoManager.NAME);
				}
			else return;
		}
		display = Display.getDefault();
		shellPHPito = new ShellPHPito(display);
		shellPHPito.createContents();
		shellPHPito.open();
		shellPHPito.layout();
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("PHPito Display Start Read and Dispatch");
			} catch (FileLogException | LockLogException e1) {
				e1.printStackTrace();
			}
		while (!shellPHPito.isDisposed()) {
			try {
				if (!display.readAndDispatch())
					display.sleep();
			} catch (Exception e) {
				Jaswt.getInstance().lunchMBError(new Shell(), e, PHPitoManager.NAME);
			}
		}

		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("PHPito Unlock");
			} catch (FileLogException | LockLogException e1) {
				e1.printStackTrace();
			}
		Jun.getInstance().unlock();
	}
}