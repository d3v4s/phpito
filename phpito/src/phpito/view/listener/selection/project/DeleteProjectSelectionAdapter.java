package phpito.view.listener.selection.project;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import exception.XMLException;
import jaswt.core.Jaswt;
import phpito.core.PHPitoManager;
import phpito.exception.ProjectException;
import phpito.view.listener.selection.launcher.LauncherDeleteProjectSelectionListener;

/**
 * Class SelecrionAdapter to delete project
 * @author Andrea Serra
 *
 */
public class DeleteProjectSelectionAdapter extends SelectionAdapter {
	private LauncherDeleteProjectSelectionListener launcherDeleteProject;

	/* CONSTRUCT */
	public DeleteProjectSelectionAdapter(LauncherDeleteProjectSelectionListener launcherDeleteProject) {
		super();
		this.launcherDeleteProject = launcherDeleteProject;
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent se) {
		try {
			deleteProject();
			launcherDeleteProject.getShellDialogPHPito().dispose();
		} catch (XMLException | ProjectException e) {
			Jaswt.getInstance().launchMBError(launcherDeleteProject.getShellDialogPHPito(), e, PHPitoManager.getInstance().getJoggerError());
		}
	}

	/* method to delete project */
	private void deleteProject() throws XMLException, ProjectException {
		PHPitoManager.getInstance().getReentrantLockProjectsXML().deleteProject(launcherDeleteProject.getProject().getIdString());
		if (launcherDeleteProject.getDeleteLogCheckBtn().getSelection()) PHPitoManager.getInstance().getReentrantLockLogServer().deleteLog(launcherDeleteProject.getProject());
		if (launcherDeleteProject.getDeletePhpiniCheckBtn().getSelection()) PHPitoManager.getInstance().deletePhpini(launcherDeleteProject.getProject());
		launcherDeleteProject.getShellPHPito().flushTable();
		launcherDeleteProject.getShellPHPito().getTable().forceFocus();
	}
}
