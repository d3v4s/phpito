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

public class StartServerSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito; 

	public StartServerSelectionAdapter(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void widgetSelected(SelectionEvent se) {
		startServer();
	}
	
	public void startServer() {
		try {
			Project p = PHPitoManager.getInstance().getProjectById(shellPHPito.getIdProjectSelect());
			if (!PHPitoManager.getInstance().startServer(p))
				Jaswt.getInstance().lunchMB(shellPHPito, SWT.OK, "FAIL!!!", "L'avvio del server non ha avuto sucesso.");
		} catch (ServerException | IOException | ProjectException e) {
			Jaswt.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.NAME);
		} finally {
			shellPHPito.flushTable();
			shellPHPito.getTable().forceFocus();
		}
	}
}
