package it.phpito.view.listener.selection;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.DOMException;

import it.as.utils.exception.FileException;
import it.as.utils.view.UtilsViewAS;
import it.phpito.controller.PHPitoManager;
import it.phpito.data.Project;
import it.phpito.exception.ProjectException;
import it.phpito.exception.ServerException;

public class StartServerSelectionAdapter extends SelectionAdapter {
	private Shell parent; 
	private Long idProject;

	public StartServerSelectionAdapter(Shell parent, Long idProject) {
		super();
		this.idProject = idProject;
		this.parent = parent;
	}

	@Override
	public void widgetSelected(SelectionEvent se) {
		try {
			Project p = PHPitoManager.getInstance().getProjectById(idProject);
			if (PHPitoManager.getInstance().startServer(p))
				UtilsViewAS.getInstance().lunchMB(parent, SWT.OK, "OK", "Server PHP avviato");
			else
				UtilsViewAS.getInstance().lunchMB(parent, SWT.OK, "FAIL!!!", "L'avvio del server non ha avuto sucesso.");
		} catch (FileException | IOException | ServerException | DOMException | ProjectException e) {
			UtilsViewAS.getInstance().lunchMBError(parent, e, PHPitoManager.NAME);
		}
	}
}
