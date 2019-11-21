package phpito.view.thread;

import java.io.IOException;

import exception.FileLogException;
import exception.LockLogException;
import jaswt.core.Jaswt;
import jutilas.core.JutilasSys;
import phpito.core.PHPitoManager;
import phpito.view.shell.ShellPHPito;

/**
 * Class Thread for calculate the CPU load average
 * @author Andrea Serra
 *
 */
public class CpuMonitorThread extends Thread {
	private ShellPHPito shellPHPito;

	/* CONSTRUCT */
	public CpuMonitorThread(ShellPHPito shellPHPito) {
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void run() {
		while (!shellPHPito.isDisposed()) {
			try {
				double sysAdvrg = JutilasSys.getInstance().getSystemLoadAverage(1000);
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
									Jaswt.getInstance().launchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
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
