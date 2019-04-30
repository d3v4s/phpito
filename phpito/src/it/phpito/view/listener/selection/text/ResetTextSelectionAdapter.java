package it.phpito.view.listener.selection.text;

import java.util.HashMap;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

import it.phpito.data.Project;
import it.phpito.exception.ProjectException;
import it.phpito.view.shell.ShellDialogPHPito;

public class ResetTextSelectionAdapter extends SelectionAdapter {
	private ShellDialogPHPito shellDialog;

	public ResetTextSelectionAdapter(ShellDialogPHPito shellDialog) {
		super();
		this.shellDialog = shellDialog;
	}

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
			if (project.equals(newProject))
				shellDialog.dispose();
			else
				shellDialog.setTextByProject(project);
		} catch (ProjectException e) {
			shellDialog.setTextByProject(project);
		}
	}
}
