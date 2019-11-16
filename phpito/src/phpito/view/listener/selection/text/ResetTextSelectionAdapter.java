package phpito.view.listener.selection.text;

import java.util.HashMap;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

import phpito.data.Project;
import phpito.exception.ProjectException;
import phpito.view.shell.dialog.ShellDialogPHPito;

/**
 * Class SelectionAdapter for reset text in text area and if already reset close the shell
 * @author Andrea Serra
 *
 */
public class ResetTextSelectionAdapter extends SelectionAdapter {
	private ShellDialogPHPito shellDialog;

	/* CONSTRUCT */
	public ResetTextSelectionAdapter(ShellDialogPHPito shellDialog) {
		super();
		this.shellDialog = shellDialog;
	}

	/* click event */
	@Override
	public void widgetSelected(SelectionEvent se) {
		Project project = shellDialog.getShellPHPito().getProjectSelect();
		try {
			Project newProject = project.clone();
			HashMap<String, Text> textMap = shellDialog.getTextMap();
			newProject.setName(textMap.get(Project.K_NAME).getText());
			newProject.getServer().setAddress(textMap.get(Project.K_ADDRESS).getText());
			newProject.getServer().setPath(textMap.get(Project.K_PATH).getText());
			newProject.getServer().setPortString(textMap.get(Project.K_PORT).getText());
			/* if is equals close shell */
			if (project.equals(newProject)) shellDialog.dispose();
			/* else reset to original */
			else shellDialog.setTextByProject(project);
		} catch (ProjectException e) {
			shellDialog.setTextByProject(project);
		}
	}
}
