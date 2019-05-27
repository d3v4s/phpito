package it.phpito.view.listener.selection.server;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.jaswt.core.Jaswt;
import it.phpito.core.PHPitoManager;
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
				Jaswt.getInstance().lunchMB(shellPHPito, SWT.OK, "FAIL!!!", "L'arresto del server non ha avuto sucesso.");
		} catch (IOException | ServerException | ProjectException e) {
			Jaswt.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
		} finally {
			shellPHPito.flushTable();
			shellPHPito.getTable().forceFocus();
		}
	}
}
