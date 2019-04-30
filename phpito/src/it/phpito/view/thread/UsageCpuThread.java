package it.phpito.view.thread;

import java.util.concurrent.ArrayBlockingQueue;

import it.as.utils.core.UtilsAS;
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
				double sysAdvrg = UtilsAS.getInstance().getSystemLoadAdverage() * 100;
				cpuUsage.put(sysAdvrg);
				shellPHPito.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						if (shellPHPito.getCanvas() != null) {
							shellPHPito.getCanvas().setToolTipText("CPU: " + String.format("%.0f", sysAdvrg) + "%");
							shellPHPito.getCanvas().redraw();
						}
						if (shellPHPito.getLblCPU() != null)
							shellPHPito.getLblCPU().setText("CPU: " + String.format("%.0f", sysAdvrg) + "%");
					}
				});
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}
}
