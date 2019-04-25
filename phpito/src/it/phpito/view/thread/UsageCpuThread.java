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
//				System.out.println(UtilsAS.getInstance().getSystemLoadAdverage() * 100);
				shellPHPito.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						shellPHPito.getCanvas().redraw();
						shellPHPito.getLblCPU().setText("CPU: " + String.format("%.0f", sysAdvrg) + "%");
					}
				});
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}
}
