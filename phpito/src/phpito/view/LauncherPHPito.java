package phpito.view;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import exception.ApplicationException;
import exception.FileLockException;
import exception.SystemException;
import jaswt.core.Jaswt;
import jun.Jun;
import phpito.core.PHPitoManager;
import phpito.view.shell.ShellPHPito;

/**
 * Class for launch PHPito
 * @author Andrea Serra
 *
 */
public class LauncherPHPito {
	private ShellPHPito shellPHPito;
	private Display display;

	/* MAIN */
	public static void main(String[] args) {
		for (String arg : args) if (arg.equals("debug")) PHPitoManager.getInstance().getJoggerDebug().setDebug(true);

		PHPitoManager.getInstance().getJoggerDebug().writeLog("DEBUG MODE ON");
		PHPitoManager.getInstance().getJoggerDebug().writeLog("STARTING PHPITO");
		
		LauncherPHPito launcherPHPito = new LauncherPHPito();
		launcherPHPito.open();
		
		PHPitoManager.getInstance().getJoggerDebug().writeLog("PHPito CLOSE");
		System.exit(0);
	}
	
	/* method that open PHPito window */
	public void open() {

		try {
			Jun.getInstance().tryLock();
			PHPitoManager.getInstance().getJoggerDebug().writeLog("PHPITO LOCK");
		} catch (ApplicationException e) {
			/* phpito already running */
			String msg = "PHPito already running!!!\nForce second instance???";
			PHPitoManager.getInstance().getJoggerDebug().writeLog("PHPito Already Running!!!");
			int resp = Jaswt.getInstance().launchMB(new Shell(), SWT.YES | SWT.NO, "CAUTION!!!", msg);

			if (resp == SWT.YES) {
				/* force second instance */
				try {
					Jun.getInstance().forceLock();
					PHPitoManager.getInstance().getJoggerDebug().writeLog("PHPito - FORCE SECOND INSTANCE!!!");
				} catch (IOException | FileLockException e1) {
					Jaswt.getInstance().launchMBError(new Shell(), e1, PHPitoManager.getInstance().getJoggerError());
					System.exit(-1);
				}
			} else return;
		} catch (IOException | SystemException e) {
			Jaswt.getInstance().launchMBError(new Shell(), e, PHPitoManager.getInstance().getJoggerError());
			System.exit(-1);
		}

		/* open shell */
		display = Display.getDefault();
		shellPHPito = new ShellPHPito(display);
		shellPHPito.open();

		/* start read and dispatch */
		PHPitoManager.getInstance().getJoggerDebug().writeLog("PHPito Display - START READ DISPATCH");
		while (!shellPHPito.isDisposed()) {
			try {
				if (!display.readAndDispatch()) display.sleep();
			} catch (Exception e) {
				Jaswt.getInstance().launchMBError(new Shell(), e, PHPitoManager.getInstance().getJoggerError());
			}
		}

		PHPitoManager.getInstance().getJoggerDebug().writeLog("PHPITO UNLOCK");
		Jun.getInstance().unlock();
	}
}