package phpito.view.listener.selection.project;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import jaswt.core.Jaswt;
import jutilas.exception.FileException;
import phpito.core.PHPitoManager;
import phpito.data.Project;
import phpito.exception.ProjectException;
import phpito.exception.ServerException;
import phpito.view.shell.ShellPHPito;
import phpito.view.shell.dialog.ShellDialogProject;

/**
 * Class SelectionAdapter for update a project
 * @author Andrea Serra
 *
 */
public class UpdateProjectSelctionAdapter extends SelectionAdapter {
	private ShellDialogProject shellDialogProject;
	private ShellPHPito shellPHPito;

	/* CONSTRUCT */
	public UpdateProjectSelctionAdapter(ShellDialogProject shellDialogProject) {
		super();
		this.shellDialogProject = shellDialogProject;
		this.shellPHPito = shellDialogProject.getShellPHPito();
	}

	/* event press enter */
	@Override
	public void widgetDefaultSelected(SelectionEvent se) {
		updateProject();
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent se) {
		updateProject();
	}

	/* method that update a project */
	private void updateProject() {
		Project oldProject = shellDialogProject.getShellPHPito().getProjectSelect();
		Project project  = shellDialogProject.getProject();
		
		try {
			/* compare the projects */
			if (project.equals(oldProject)) {
				Jaswt.getInstance().launchMB(shellDialogProject, SWT.OK, "FAIL!!!", "There are no changes.");
				return;
			}

			/* check if server is running */
			boolean restart = false;
			if (oldProject.getServer().isRunning()) {
				String msg = "Caution!!! The server is running, to continue it must be stopped.\nThe server will be restarted after the changes are complete. Continue???";
				int res = Jaswt.getInstance().launchMB(shellDialogProject, SWT.YES | SWT.NO, "CAUTION!!!", msg);
				if (res == SWT.NO) return;

				restart = true;
				if (!PHPitoManager.getInstance().stopServer(oldProject)) {
					Jaswt.getInstance().launchMB(shellDialogProject, SWT.OK, "FAIL!!!", "Server shutdown failed.");
					return;
				}
			}

			/* answer and save update */
			String msg = "Save the changes???\n" + project.toString();
			int res = Jaswt.getInstance().launchMB(shellDialogProject, SWT.YES | SWT.NO, "CONTINUE???", msg);
			if (res == SWT.YES) {
				PHPitoManager.getInstance().updateProject(project, oldProject.getIdAndName());
				if (restart && !PHPitoManager.getInstance().startServer(project)) Jaswt.getInstance().launchMB(shellDialogProject, SWT.OK, "FAIL!!!", "Server startup failed.");
				shellDialogProject.flushTableAndDispose();
				
			}
		} catch (ProjectException | IOException | ServerException | FileException e) {
			Jaswt.getInstance().launchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
		}
	}

//	/* method that set the update on project */
//	private void setUpdate(Project project) throws ProjectException {
//		project.setName(shellDialogProject.getTextMap().get(Project.K_NAME).getText());
//		project.setLogActive(shellDialogProject.getLogActvChckBttn().getSelection());
//		project.setPhpini(shellDialogProject.getPhpiniCombo().getSelectionIndex());
//		project.getServer().setPath(shellDialogProject.getTextMap().get(Project.K_PATH).getText());
//		project.getServer().setAddress(shellDialogProject.getTextMap().get(Project.K_ADDRESS).getText());
//		project.getServer().setPortString(shellDialogProject.getTextMap().get(Project.K_PORT).getText());
//	}
}
