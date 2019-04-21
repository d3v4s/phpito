package it.phpito.view.utils;

import java.io.IOException;

import org.eclipse.swt.SWT;

import it.as.utils.view.UtilsViewAS;
import it.phpito.controller.PHPitoManager;
import it.phpito.data.Project;
import it.phpito.exception.ServerException;
import it.phpito.view.shell.ShellPHPito;

public class UtilsViewPHPito {
	private static UtilsViewPHPito utilsViewPHPito;

	/* costruttore */
	private UtilsViewPHPito() {
	}

	/* singleton */
	public static UtilsViewPHPito getInstance() {
//		utilsViewPHPito = (utilsViewPHPito == null) ? new UtilsViewPHPito() : utilsViewPHPito;
		return (utilsViewPHPito = (utilsViewPHPito == null) ? new UtilsViewPHPito() : utilsViewPHPito);
	}
	/* metodo che avvia server per GUI */
	public void startServer(ShellPHPito shellPHPito) {
		try {
			Project p = PHPitoManager.getInstance().getProjectById(shellPHPito.getIdProjectSelect());
			if (!PHPitoManager.getInstance().startServer(p))
				UtilsViewAS.getInstance().lunchMB(shellPHPito, SWT.OK, "FAIL!!!", "L'avvio del server non ha avuto sucesso.");
//			else
//				UtilsViewAS.getInstance().lunchMB(shellPHPito, SWT.OK, "OK", "Server PHP avviato");
			shellPHPito.flushTable();
			shellPHPito.getTable().forceFocus();
		} catch (IOException | ServerException e) {
			UtilsViewAS.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.NAME);
		}
	}

	/* metodo che stoppa server per GUI */
	public void stopServer(ShellPHPito shellPHPito) {
		try {
			Project p = PHPitoManager.getInstance().getProjectById(shellPHPito.getIdProjectSelect());
			if (PHPitoManager.getInstance().stopServer(p))
				UtilsViewAS.getInstance().lunchMB(shellPHPito, SWT.OK, "FAIL!!!", "L'arresto del server non ha avuto sucesso.");
//			else
//				UtilsViewAS.getInstance().lunchMB(shellPHPito, SWT.OK, "OK", "Server PHP fermato");
			shellPHPito.flushTable();
			shellPHPito.getTable().forceFocus();
		} catch (IOException | ServerException e) {
			UtilsViewAS.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.NAME);
		}
	}

}
