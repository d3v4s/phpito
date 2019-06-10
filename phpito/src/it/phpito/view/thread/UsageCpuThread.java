package it.phpito.view.thread;

import java.io.IOException;

import it.jaswt.core.Jaswt;
import it.jogger.exception.FileLogException;
import it.jogger.exception.LockLogException;
import it.jutilas.core.JutilasSys;
import it.phpito.core.PHPitoManager;
import it.phpito.view.shell.ShellPHPito;

public class UsageCpuThread extends Thread {
	private ShellPHPito shellPHPito;

	public UsageCpuThread(ShellPHPito shellPHPito) {
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void run() {
		while (!shellPHPito.isDisposed()) {
			try {
				double sysAdvrg = JutilasSys.getInstance().getSystemLoadAdverage(1000);
				shellPHPito.getCPUMonitorCanvas().getCpuUsageQueue().put(sysAdvrg);
				if (!shellPHPito.isDisposed()) {
					shellPHPito.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							if (shellPHPito.getCPUMonitorCanvas() != null) {
								shellPHPito.getCPUMonitorCanvas().setToolTipText("CPU: " + String.format("%.0f", sysAdvrg) + "%");
								shellPHPito.getCPUMonitorCanvas().redraw();
							}
							if (shellPHPito.getLblCPU() != null)
								try {
									shellPHPito.getLblCPU().setText(PHPitoManager.getInstance().getSystemInfo(sysAdvrg));
								} catch (IOException e) {
									Jaswt.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
								}
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					PHPitoManager.getInstance().getJoggerError().writeLog(e);
				} catch (FileLogException | LockLogException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
