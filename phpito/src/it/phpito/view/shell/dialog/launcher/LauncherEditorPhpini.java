package it.phpito.view.shell.dialog.launcher;

import java.io.IOException;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import it.jaswt.core.Jaswt;
import it.jutilas.core.Jutilas;
import it.phpito.core.PHPitoManager;
import it.phpito.data.Project;
import it.phpito.exception.ProjectException;
import it.phpito.view.shell.dialog.ShellDialogPHPito;

public class LauncherEditorPhpini implements SelectionListener {
	private ShellDialogPHPito shellDialogPHPito;
	private Project project;
	public LauncherEditorPhpini(ShellDialogPHPito shellDialogPHPito, Project project) {
		this.shellDialogPHPito = shellDialogPHPito;
		this.project = project;
	}
	
	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	@Override
	public void widgetSelected(SelectionEvent se) {
		try {
			Jutilas.getInstance().openTextEditor(project.getPhpiniPath());
		} catch (IOException | ProjectException e) {
			Jaswt.getInstance().lunchMBError(shellDialogPHPito, e, PHPitoManager.getInstance().getJoggerError());
		}
	}
}
