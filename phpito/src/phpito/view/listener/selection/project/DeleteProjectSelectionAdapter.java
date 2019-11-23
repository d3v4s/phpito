package phpito.view.listener.selection.project;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import exception.XMLException;
import jaswt.core.Jaswt;
import phpito.core.PHPitoManager;
import phpito.data.Project;
import phpito.exception.ProjectException;
import phpito.view.listener.selection.launcher.LauncherDeleteProjectSelectionListener.ShellDialogDeleteProject;

/**
 * Class SelecrionAdapter to delete project
 * @author Andrea Serra
 *
 */
public class DeleteProjectSelectionAdapter extends SelectionAdapter {
	private ShellDialogDeleteProject shellDialogDeleteProject;

	/* CONSTRUCT */
	public DeleteProjectSelectionAdapter(ShellDialogDeleteProject launcherDeleteProject) {
		super();
		this.shellDialogDeleteProject = launcherDeleteProject;
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent se) {
		try {
			deleteProject();
		} catch (XMLException | ProjectException e) {
			Jaswt.getInstance().launchMBError(shellDialogDeleteProject, e, PHPitoManager.getInstance().getJoggerError());
		}
	}

	/* method to delete project */
	private void deleteProject() throws XMLException, ProjectException {
		Project project = shellDialogDeleteProject.getProject();
		boolean deleteLog = shellDialogDeleteProject.getDeleteLogCheckBtn().getSelection();
		boolean deletePhpini = shellDialogDeleteProject.getDeletePhpiniCheckBtn().getSelection();
		PHPitoManager.getInstance().deleteProject(project, deleteLog, deletePhpini);
		shellDialogDeleteProject.flushTableAndDispose();;
	}
}
