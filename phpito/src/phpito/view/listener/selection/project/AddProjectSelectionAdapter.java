package phpito.view.listener.selection.project;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

import jaswt.core.Jaswt;
import phpito.core.PHPitoManager;
import phpito.data.Project;
import phpito.data.Server;
import phpito.exception.ProjectException;
import phpito.view.shell.ShellPHPito;
import phpito.view.shell.dialog.ShellDialogProject;

/**
 * Class SelectionAdapter for add new project
 * @author Andrea Serra
 *
 */
public class AddProjectSelectionAdapter extends SelectionAdapter {
	private ShellDialogProject shellDialogProject;
	private ShellPHPito shellPHPito;

	/* CONSTRUCT */
	public AddProjectSelectionAdapter(ShellDialogProject shellDialogProject) {
		super();
		this.shellDialogProject = shellDialogProject;
		shellPHPito = shellDialogProject.getShellPHPito();
	}

	/* event press enter */
	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		addNewProject();
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent event) {
		addNewProject();
	}

	/* method that add a new project */
	private void addNewProject() {
		try {
			Project project = getNewProject();

			String msg = "Save this project???\n" + project.toString();
			int res = Jaswt.getInstance().launchMB(shellDialogProject, SWT.YES | SWT.NO, "SAVE???", msg);
			if (res == SWT.YES) {
				PHPitoManager.getInstance().addNewProject(project);
				shellDialogProject.flushTableAndDispose();
			}
		} catch (ProjectException e) {
			Jaswt.getInstance().launchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
		}
	}

	/* method that get a new project */
	private Project getNewProject() throws ProjectException {
		Project project = new Project();
		HashMap<String, Text> textMap = shellDialogProject.getTextMap();
		project.setName(textMap.get(Project.K_NAME).getText());
		project.setLogActive(shellDialogProject.getLogActvChckBttn().getSelection());
		project.setPhpini(shellDialogProject.getPhpiniCombo().getSelectionIndex());
		project.setServer(new Server());
		project.getServer().setPath(textMap.get(Project.K_PATH).getText());
		project.getServer().setAddress(textMap.get(Project.K_ADDRESS).getText());
		project.getServer().setPortString(textMap.get(Project.K_PORT).getText());
		return project;
	}
}
