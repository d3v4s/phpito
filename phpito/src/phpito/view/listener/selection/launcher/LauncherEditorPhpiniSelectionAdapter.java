package phpito.view.listener.selection.launcher;

import org.eclipse.swt.events.SelectionEvent;

import jaswt.listener.selection.OpenerFileFromOSSelectionAdapter;
import jaswt.utils.Jaswt;
import phpito.core.PHPitoManager;
import phpito.data.Project;
import phpito.view.shell.dialog.ShellDialogPHPitoAbstract;

/**
 * Class SelectionAdapter to launch the window for edit the phpini file
 * @author Andrea Serra
 *
 */
public class LauncherEditorPhpiniSelectionAdapter extends OpenerFileFromOSSelectionAdapter {

	/* CONSTRUCT */
	public LauncherEditorPhpiniSelectionAdapter(ShellDialogPHPitoAbstract shellDialogPHPito, Project project) {
		super(shellDialogPHPito, null);
		try {
			this.path = project.getCreateCustomPhpiniPath();
		} catch (Exception e) {
			Jaswt.getInstance().launchMBError(shellDialogPHPito, e, PHPitoManager.getInstance().getJoggerError());
		}
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent se) {
		super.widgetSelected(se);
	}
}
