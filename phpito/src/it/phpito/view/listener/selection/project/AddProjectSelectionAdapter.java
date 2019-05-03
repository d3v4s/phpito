package it.phpito.view.listener.selection.project;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

import it.as.utils.view.UtilsViewAS;
import it.phpito.controller.PHPitoManager;
import it.phpito.data.Project;
import it.phpito.data.Server;
import it.phpito.exception.ProjectException;
import it.phpito.view.shell.ShellDialogPHPito;
import it.phpito.view.shell.ShellPHPito;

public class AddProjectSelectionAdapter extends SelectionAdapter {
	private ShellDialogPHPito shellDialog;

	public AddProjectSelectionAdapter(ShellDialogPHPito shellDialog) {
		super();
		this.shellDialog = shellDialog;
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
			project.setLogActive(shellDialog.getChckBttnLogActv().getSelection());
			project.setServer(new Server());
			project.getServer().setPath(textMap.get(Project.K_PATH).getText());
			project.getServer().setAddress(textMap.get(Project.K_ADDRESS).getText());
			project.getServer().setPortString(textMap.get(Project.K_PORT).getText());

			String msg = "Sei sicuro di voler aggiungere il seguente progetto???\n" + project.toString();
			int res = UtilsViewAS.getInstance().lunchMB(shellDialog, SWT.YES | SWT.NO, "AGGIUNGO???", msg);
			if (res == SWT.YES) {
				PHPitoManager.getInstance().getReentrantLockXMLServer().addProject(project);
//				UtilsViewAS.getInstance().lunchMB(shellDialog, SWT.OK, "OK", "Nuovo progetto aggiunto con sucesso.");
				ShellPHPito shellPHPito = shellDialog.getShellPHPito();
				shellDialog.dispose();
				shellPHPito.flushTable();
				shellPHPito.getTable().forceFocus();
			}
		} catch (ProjectException e) {
			UtilsViewAS.getInstance().lunchMBError(shellDialog, e, PHPitoManager.NAME);
		}
	}
}
