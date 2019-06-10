package it.phpito.view.listener.selection.project;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

import it.jaswt.core.Jaswt;
import it.phpito.core.PHPitoManager;
import it.phpito.data.Project;
import it.phpito.data.Server;
import it.phpito.exception.ProjectException;
import it.phpito.view.shell.ShellPHPito;
import it.phpito.view.shell.dialog.ShellDialogPHPito;

public class AddProjectSelectionAdapter extends SelectionAdapter {
	private ShellDialogPHPito shellDialog;
	private ShellPHPito shellPHPito;

	public AddProjectSelectionAdapter(ShellDialogPHPito shellDialog) {
		super();
		this.shellDialog = shellDialog;
		shellPHPito = shellDialog.getShellPHPito();
	}
	
	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		widgetSelected(event);
	}

	@Override
	public void widgetSelected(SelectionEvent event) {
		Project project = new Project();
		HashMap<String, Text> textMap = shellDialog.getTextMap();
		try {
			project.setName(textMap.get(Project.K_NAME).getText());
			project.setLogActive(shellDialog.getLogActvChckBttn().getSelection());
			project.setPhpini(shellDialog.getPhpiniCombo().getSelectionIndex());
			project.setServer(new Server());
			project.getServer().setPath(textMap.get(Project.K_PATH).getText());
			project.getServer().setAddress(textMap.get(Project.K_ADDRESS).getText());
			project.getServer().setPortString(textMap.get(Project.K_PORT).getText());

			String msg = "Sei sicuro di voler aggiungere il seguente progetto???\n" + project.toString();
			int res = Jaswt.getInstance().lunchMB(shellDialog, SWT.YES | SWT.NO, "AGGIUNGO???", msg);
			if (res == SWT.YES) {
				PHPitoManager.getInstance().getReentrantLockXMLServer().addProject(project);
				project.getPhpiniPath();
				shellPHPito.flushTable();
				shellPHPito.getTable().forceFocus();
				shellDialog.dispose();
			}
		} catch (ProjectException e) {
			Jaswt.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
		}
	}
}
