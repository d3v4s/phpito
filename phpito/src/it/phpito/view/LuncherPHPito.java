package it.phpito.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import it.as.utils.view.UtilsViewAS;
import it.jun.core.Jun;
import it.phpito.controller.PHPitoManager;
import it.phpito.view.shell.ShellPHPito;

public class LuncherPHPito {
	private ShellPHPito shellPHPito;
	private Display display;
	
	public static void main(String[] args) {
		LuncherPHPito luncherPHPito = new LuncherPHPito();

		try {
			luncherPHPito.open();
		} catch (Exception e) {
			e.printStackTrace();
			UtilsViewAS.getInstance().lunchMBError(new Shell(), e, PHPitoManager.NAME);
		}
	}
	
	/* metodo lancio finestra */
	public void open() throws Exception {
		try {
			Jun.getInstance().tryLock();
		} catch (Exception e) {
			String msg = "Attenzione PHPito e' gia' in esecuzione!!!\n"
					+ "Forzare la seconda istanza???";
			int resp = UtilsViewAS.getInstance().lunchMB(new Shell(), SWT.YES | SWT.NO, "Attenzione!!!", msg);

			if (resp == SWT.YES)
				Jun.getInstance().forceLock();
			else
				return;
				
		}
		display = Display.getDefault();
		shellPHPito = new ShellPHPito(display);
		shellPHPito.createContents();
		shellPHPito.open();
		shellPHPito.layout();
		while (!shellPHPito.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		Jun.getInstance().unlock();
	}
}