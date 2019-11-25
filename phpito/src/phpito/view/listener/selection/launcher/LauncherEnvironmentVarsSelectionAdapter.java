package phpito.view.listener.selection.launcher;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import phpito.data.Project;
import phpito.view.shell.dialog.ShellDialogEnvironmentVars;
import phpito.view.shell.dialog.ShellDialogProject;

/**
 * Class for launch the environment variables shell
 * @author Andrea Serra
 *
 */
public class LauncherEnvironmentVarsSelectionAdapter extends SelectionAdapter {
	ShellDialogProject shellDialogProject;

	public LauncherEnvironmentVarsSelectionAdapter(ShellDialogProject sheDialogProject, Project project) {
		this.shellDialogProject = sheDialogProject;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		ShellDialogEnvironmentVars shellDialogEnvironmentVars = new ShellDialogEnvironmentVars(shellDialogProject);
		shellDialogEnvironmentVars.open();
	}
}
