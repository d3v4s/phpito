package phpito.view.listener.selection.launcher;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import jaswt.core.Jaswt;
import phpito.core.PHPitoManager;
import phpito.data.Project;
import phpito.exception.ProjectException;
import phpito.view.shell.dialog.ShellDialogPHPito;

/**
 * Class SelectionAdapter to launch the window for edit the phpini file
 * @author Andrea Serra
 *
 */
public class LauncherEditorPhpini implements SelectionListener {
	private ShellDialogPHPito shellDialogPHPito;
	private Project project;

	/* CONSTRUCT */
	public LauncherEditorPhpini(ShellDialogPHPito shellDialogPHPito, Project project) {
		this.shellDialogPHPito = shellDialogPHPito;
		this.project = project;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent se) {
		try {
			File file = new File(project.getCreateCustomPhpiniPath());
			Desktop.getDesktop().open(file);
//			Jutilas.getInstance().openTextEditor(project.getPhpiniPath());
		} catch (IOException | ProjectException e) {
			Jaswt.getInstance().launchMBError(shellDialogPHPito, e, PHPitoManager.getInstance().getJoggerError());
		}
	}
}
