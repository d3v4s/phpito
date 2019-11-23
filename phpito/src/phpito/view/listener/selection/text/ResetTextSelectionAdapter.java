package phpito.view.listener.selection.text;

import java.util.HashMap;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

import phpito.data.Project;
import phpito.exception.ProjectException;
import phpito.view.shell.dialog.ShellDialogProject;

/**
 * Class SelectionAdapter for reset text in text area and if already reset close the shell
 * @author Andrea Serra
 *
 */
public class ResetTextSelectionAdapter extends SelectionAdapter {
	private ShellDialogProject shellDialogProject;
	private Project project;

	/* CONSTRUCT */
	public ResetTextSelectionAdapter(ShellDialogProject shellDialogProject, Project project) {
		super();
		this.shellDialogProject = shellDialogProject;
		this.project = project;
	}

	/* click event */
	@Override
	public void widgetSelected(SelectionEvent se) {
		try {
			Project newProject = project.clone();
			HashMap<String, Text> textMap = shellDialogProject.getTextMap();
			newProject.setName(textMap.get(Project.K_NAME).getText());
			newProject.getServer().setAddress(textMap.get(Project.K_ADDRESS).getText());
			newProject.getServer().setPath(textMap.get(Project.K_PATH).getText());
			newProject.getServer().setPortString(textMap.get(Project.K_PORT).getText());
			/* if is equals close shell */
			if (project.equals(newProject)) shellDialogProject.dispose();
			/* else reset to original */
			else shellDialogProject.setTextByProject(project);
		} catch (ProjectException e) {
			shellDialogProject.setTextByProject(project);
		}
	}
}
