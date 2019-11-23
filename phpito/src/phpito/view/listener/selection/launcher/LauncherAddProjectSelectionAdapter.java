package phpito.view.listener.selection.launcher;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import phpito.view.shell.ShellPHPito;
import phpito.view.shell.dialog.ShellDialogProject;

/**
 * Class SelectionAdapter to launch the window for add new project 
 * @author Andrea Serra
 *
 */
public class LauncherAddProjectSelectionAdapter implements SelectionListener {
	private ShellPHPito shellPHPito;

	/* CONSTRUCT */
	public LauncherAddProjectSelectionAdapter(ShellPHPito shellPHPito) {
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent e) {
		launchAddProject();
	}
	
	/* metodo per lanciare finestra che aggiunge il progetto */
	public void launchAddProject() {
		ShellDialogProject shellDialogProject = new ShellDialogProject(shellPHPito, ShellDialogProject.NEW);
		shellDialogProject.open();
	}
}
