package it.phpito.view.listener.selection;

import java.io.IOException;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;

import it.as.utils.exception.FileException;
import it.as.utils.view.UtilsViewAS;
import it.phpito.controller.PHPitoManager;
import it.phpito.data.Project;
import it.phpito.exception.ServerException;

public class StopServerSelectionAdapter extends SelectionAdapter {
	private Shell parent; 
	private Long idProject;

	public StopServerSelectionAdapter(Shell parent, Long idProject) {
		this.idProject = idProject;
		this.parent = parent;
	}

	@Override
	public void widgetSelected(SelectionEvent se) {
		try {
			Project p = PHPitoManager.getInstance().getProjectById(idProject);
			PHPitoManager.getInstance().stopServer(p);
		} catch (FileException | IOException | ServerException e) {
			UtilsViewAS.getInstance().lunchMBError(parent, e, PHPitoManager.NAME);
		}
	}
}
