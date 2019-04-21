package it.phpito.view.thread;

import java.io.IOException;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.MenuItem;

import it.as.utils.view.UtilsViewAS;
import it.phpito.controller.PHPitoManager;
import it.phpito.data.Project;
import it.phpito.view.shell.ShellPHPito;

public class EnableStartStopThread extends Thread {
	private ShellPHPito shellPHPito;
	
	public EnableStartStopThread(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void run() {
		while (!shellPHPito.isDisposed()) {
			shellPHPito.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					Project project;
					Boolean isRunnig = null;
					try {
						if ((project = shellPHPito.getProjectSeclect()) != null)
							isRunnig = project.getServer().isRunnig();
					} catch (IOException e) {
						UtilsViewAS.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.NAME);
					} finally {
						if (isRunnig != null) {
							for (Button bttn : shellPHPito.getBttnProjectList())
								bttn.setEnabled(true);
							for (MenuItem mntm : shellPHPito.getMntmProjectList())
								mntm.setEnabled(true);
							for (Button bttn : shellPHPito.getBttnStartList())
								bttn.setEnabled(!isRunnig);
							for (MenuItem mntm : shellPHPito.getMntmStartList())
								mntm.setEnabled(!isRunnig);
							for (Button bttn : shellPHPito.getBttnStopList())
								bttn.setEnabled(isRunnig);
							for (MenuItem mntm : shellPHPito.getMntmStopList())
								mntm.setEnabled(isRunnig);
						} else {
							for (Button bttn : shellPHPito.getBttnProjectList())
								bttn.setEnabled(false);
							for (MenuItem mntm : shellPHPito.getMntmProjectList())
								mntm.setEnabled(false);
							for (Button bttn : shellPHPito.getBttnStartList())
								bttn.setEnabled(false);
							for (MenuItem mntm : shellPHPito.getMntmStartList())
								mntm.setEnabled(false);
							for (Button bttn : shellPHPito.getBttnStopList())
								bttn.setEnabled(false);
							for (MenuItem mntm : shellPHPito.getMntmStopList())
								mntm.setEnabled(false);
						}
					}
				}
			});
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}
}
