package it.phpito.view;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import it.as.utils.view.UtilsViewAS;
import it.phpito.controller.PHPitoManager;
import it.phpito.view.shell.ShellPHPito;

public class LuncherPHPito {
	private ShellPHPito shellPHPito;
	private Display display;
	
	public static void main(String[] args) {
		LuncherPHPito windowPHPito = new LuncherPHPito();

		try {
			windowPHPito.open();
		} catch (Exception e) {
			UtilsViewAS.getInstance().lunchMBError(new Shell(), e, PHPitoManager.NAME);
		}
	}
	
	/* metodo lancio finestra */
	public void open() throws Exception {
		display = Display.getDefault();
		shellPHPito = new ShellPHPito(display);
		shellPHPito.createContents();
		shellPHPito.open();
		shellPHPito.layout();
		while (!shellPHPito.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
}