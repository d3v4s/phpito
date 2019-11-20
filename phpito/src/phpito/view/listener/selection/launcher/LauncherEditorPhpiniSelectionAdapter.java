package phpito.view.listener.selection.launcher;

import org.eclipse.swt.events.SelectionEvent;

import jaswt.core.Jaswt;
import jaswt.listener.selection.OpenFileFromOSSelectionAdapter;
import phpito.core.PHPitoManager;
import phpito.data.Project;
import phpito.exception.ProjectException;
import phpito.view.shell.dialog.ShellDialogPHPito;

/**
 * Class SelectionAdapter to launch the window for edit the phpini file
 * @author Andrea Serra
 *
 */
public class LauncherEditorPhpiniSelectionAdapter extends OpenFileFromOSSelectionAdapter {
	private ShellDialogPHPito shellDialogPHPito;
	private Project project;

	/* CONSTRUCT */
	public LauncherEditorPhpiniSelectionAdapter(ShellDialogPHPito shellDialogPHPito, Project project) {
		super(shellDialogPHPito);
		this.shellDialogPHPito = shellDialogPHPito;
		this.project = project;
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent se) {
		try {
			setPath(project.getCreateCustomPhpiniPath());
			super.widgetSelected(se);
		} catch (ProjectException e) {
			Jaswt.getInstance().launchMBError(shellDialogPHPito, e, PHPitoManager.getInstance().getJoggerError());
		}
	}
}
