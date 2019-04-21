package it.phpito.view.listener.selection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.as.utils.view.UtilsViewAS;
import it.phpito.controller.PHPitoManager;
import it.phpito.data.Project;
import it.phpito.exception.ProjectException;
import it.phpito.view.shell.ShellDialogPHPito;
import it.phpito.view.shell.ShellPHPito;

public class UpdateProjectSelctionAdapter extends SelectionAdapter {
	private ShellDialogPHPito shellDialog;

	public UpdateProjectSelctionAdapter(ShellDialogPHPito shellDialog) {
		super();
		this.shellDialog = shellDialog;
	}
	

	@Override
	public void widgetDefaultSelected(SelectionEvent se) {
		widgetSelected(se);
	}


	@Override
	public void widgetSelected(SelectionEvent se) {
		Project project = shellDialog.getShellPHPito().getProjectSeclect();
		try {
			project.setName(shellDialog.getTextMap().get(Project.K_NAME).getText());
			project.getServer().setPath(shellDialog.getTextMap().get(Project.K_PATH).getText());
			project.getServer().setAddress(shellDialog.getTextMap().get(Project.K_ADDRESS).getText());
			project.getServer().setPortString(shellDialog.getTextMap().get(Project.K_PORT).getText());

			int res = UtilsViewAS.getInstance().lunchMB(shellDialog, SWT.YES | SWT.NO, "Confermi???", "Sei sicuro di voler salvare le modifiche del seguente progetto???\n" + project.toString());
			if (res == SWT.YES) {
				PHPitoManager.getInstance().getReentrantLockXMLServer().updateProject(project);
				UtilsViewAS.getInstance().lunchMB(shellDialog, SWT.OK, "OK", "Modifiche salvate con sucesso.");
				ShellPHPito shellPHPito = shellDialog.getShellPHPito();
				shellDialog.dispose();
				shellPHPito.flushTable();
				shellPHPito.forceFocus();
				
			}
		} catch (ProjectException e) {
			UtilsViewAS.getInstance().lunchMBError(shellDialog, e, PHPitoManager.NAME);
		}
	}

	
}
