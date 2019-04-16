package it.phpito.view.listener.selection;

import java.io.IOException;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;

import it.as.utils.exception.FileException;
import it.as.utils.view.UtilsViewAS;
import it.phpito.ServerException;
import it.phpito.controller.PHPitoManager;
import it.phpito.data.Project;

public class StartServerSelectionAdapter extends SelectionAdapter {
	private Shell parent; 
	private Integer idProject;

	public StartServerSelectionAdapter(Shell parent, Integer idProject) {
		this.idProject = idProject;
		this.parent = parent;
	}

	@Override
	public void widgetSelected(SelectionEvent se) {
		try {
			Project p = PHPitoManager.getInstance().getProjectById(idProject);
			PHPitoManager.getInstance().startServer(p);
		} catch (FileException | IOException | ServerException e) {
			UtilsViewAS.getInstance().lunchMBError(parent, e, PHPitoManager.NAME);
		}
	}
}
