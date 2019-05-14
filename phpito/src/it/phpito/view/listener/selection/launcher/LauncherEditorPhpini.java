package it.phpito.view.listener.selection.launcher;

import java.io.IOException;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.jaswt.core.Jaswt;
import it.jutilas.core.Jutilas;
import it.phpito.core.PHPitoManager;
import it.phpito.data.Project;
import it.phpito.exception.ProjectException;
import it.phpito.view.shell.ShellDialogPHPito;

public class LauncherEditorPhpini extends SelectionAdapter {
	private ShellDialogPHPito shellDialogPHPito;
	private Project project;
	public LauncherEditorPhpini(ShellDialogPHPito shellDialogPHPito, Project project) {
		this.shellDialogPHPito = shellDialogPHPito;
		this.project = project;
	}

	@Override
	public void widgetSelected(SelectionEvent se) {
		try {
			Jutilas.getInstance().openTextEditor(project.getPhpiniPath());
		} catch (IOException | ProjectException e) {
			Jaswt.getInstance().lunchMBError(shellDialogPHPito, e, PHPitoManager.NAME);
		}
	}
}
