package it.phpito.view.thread;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import it.jaswt.core.Jaswt;
import it.jogger.core.JoggerError;
import it.jogger.exception.FileLogException;
import it.jutilas.core.JutilasSys;
import it.phpito.controller.PHPitoManager;
import it.phpito.view.shell.ShellPHPito;

public class UsageCpuThread extends Thread {
	private ShellPHPito shellPHPito;
	private ArrayBlockingQueue<Double> cpuUsage;

	public UsageCpuThread(ShellPHPito shellPHPito, ArrayBlockingQueue<Double> cpuUsage) {
		this.shellPHPito = shellPHPito;
		this.cpuUsage = cpuUsage;
	}

	@Override
	public void run() {
		while (!shellPHPito.isDisposed()) {
			try {
				double sysAdvrg = JutilasSys.getInstance().getSystemLoadAdverage(1000);
				cpuUsage.put(sysAdvrg);
				if (!shellPHPito.isDisposed()) {
					shellPHPito.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							if (shellPHPito.getCanvas() != null) {
								shellPHPito.getCanvas().setToolTipText("CPU: " + String.format("%.2f", sysAdvrg) + "%");
								shellPHPito.getCanvas().redraw();
							}
							if (shellPHPito.getLblCPU() != null)
								try {
									shellPHPito.getLblCPU().setText(PHPitoManager.getInstance().getSystemInfo(sysAdvrg));
								} catch (IOException e) {
									Jaswt.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.NAME);
								}
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					JoggerError.getInstance().writeLog(e, PHPitoManager.NAME, null);
				} catch (FileLogException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
