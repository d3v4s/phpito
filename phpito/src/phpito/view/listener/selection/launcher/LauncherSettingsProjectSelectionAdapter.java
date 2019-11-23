package phpito.view.listener.selection.launcher;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import phpito.view.shell.ShellPHPito;
import phpito.view.shell.dialog.ShellDialogProject;

/**
 * Class SelectionListener to launch the modify project window
 * @author Andrea Serra
 *
 */
public class LauncherSettingsProjectSelectionAdapter implements SelectionListener {
	ShellPHPito shellPHPito;

	/* CONSTRUCT */
	public LauncherSettingsProjectSelectionAdapter(ShellPHPito shellPHPito) {
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent e) {
		launchUpdateProject();
	}
	
	/* metodo che apre la finestra per aggiunge il progetto */
	public void launchUpdateProject() {
		ShellDialogProject shellDialog = new ShellDialogProject(shellPHPito, ShellDialogProject.UPDATE);
		shellDialog.open();
	}
}
