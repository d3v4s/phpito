package it.phpito.view.listener.selection;

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
				UtilsViewAS.getInstance().lunchMB(shellPHPito, SWT.OK, "FAIL!!!", "L'avvio del server non ha avuto sucesso.");
			shellPHPito.flushTable();
			shellPHPito.getTable().forceFocus();
		} catch (ServerException | IOException | ProjectException e) {
			UtilsViewAS.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.NAME);
		}
	}
}
