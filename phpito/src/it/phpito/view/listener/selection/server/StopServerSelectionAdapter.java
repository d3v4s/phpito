package it.phpito.view.listener.selection.server;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.as.utils.view.UtilsViewAS;
import it.phpito.controller.PHPitoManager;
import it.phpito.data.Project;
import it.phpito.exception.ProjectException;
import it.phpito.exception.ServerException;
import it.phpito.view.shell.ShellPHPito;

public class StopServerSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito; 

	public StopServerSelectionAdapter(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void widgetSelected(SelectionEvent se) {
		stopServer();
	}
	
	public void stopServer() {
		try {
			Project p = PHPitoManager.getInstance().getProjectById(shellPHPito.getIdProjectSelect());
			if (!PHPitoManager.getInstance().stopServer(p))
				UtilsViewAS.getInstance().lunchMB(shellPHPito, SWT.OK, "FAIL!!!", "L'arresto del server non ha avuto sucesso.");
			shellPHPito.flushTable();
			shellPHPito.getTable().forceFocus();
		} catch (IOException | ServerException | ProjectException e) {
			UtilsViewAS.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.NAME);
		}
	}
}
