package phpito.view.listener.selection.launcher;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import jaswt.core.Jaswt;
import phpito.data.Project;
import phpito.view.shell.ShellPHPito;
import phpito.view.shell.dialog.ShellDialogPHPito;
import phpito.view.shell.dialog.ShellDialogProject;

public class LauncherEnvironmentVarsSelectionAdapter extends SelectionAdapter {
	ShellDialogProject shellDialogProject;
	Project project;

	public LauncherEnvironmentVarsSelectionAdapter(ShellDialogProject sheDialogProject, Project project) {
		this.shellDialogProject = sheDialogProject;
		this.project = project;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		ShellDialogEnvironmentVars shellDialogEnvironmentVars = new ShellDialogEnvironmentVars(shellDialogProject.getShellPHPito());
		shellDialogEnvironmentVars.open();
	}

	private class ShellDialogEnvironmentVars extends ShellDialogPHPito {

		public ShellDialogEnvironmentVars(ShellPHPito shellPHPito) {
			super(shellPHPito);
		}

		@Override
		protected void createContents() {
			this.setSize(350, 400);
			Jaswt.getInstance().centerWindow(this);
		}
		
	}
}
