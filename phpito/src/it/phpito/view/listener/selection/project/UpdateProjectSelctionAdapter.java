package it.phpito.view.listener.selection.project;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.jaswt.core.Jaswt;
import it.phpito.controller.PHPitoManager;
import it.phpito.data.Project;
import it.phpito.exception.ProjectException;
import it.phpito.exception.ServerException;
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
		Project project = shellDialog.getShellPHPito().getProjectSelect();
		try {
			boolean restart = false;
			if (project.getServer().isRunning()) {
				String msg = "Attenzione!!! Il server e' in esecuzione, per continuare il server dovra' essere arrestato.\n"
								+ "Il server verra' riavviato dopo aver concluso le modifiche. Continui???";
				int res = Jaswt.getInstance().lunchMB(shellDialog, SWT.YES | SWT.NO, "Attenzione!!!", msg);
				
				if (res == SWT.NO)
					return;
				
				restart = true;
				if (!PHPitoManager.getInstance().stopServer(project)) {
					Jaswt.getInstance().lunchMB(shellDialog, SWT.OK, "FAIL!!!", "L'arresto del server non ha avuto sucesso.");
					return;
				}
					
			}
			String oldIdName = project.getIdAndName();
			project.setName(shellDialog.getTextMap().get(Project.K_NAME).getText());
			project.setLogActive(shellDialog.getChckBttnLogActv().getSelection());
			project.getServer().setPath(shellDialog.getTextMap().get(Project.K_PATH).getText());
			project.getServer().setAddress(shellDialog.getTextMap().get(Project.K_ADDRESS).getText());
			project.getServer().setPortString(shellDialog.getTextMap().get(Project.K_PORT).getText());

			String msg = "Sei sicuro di voler salvare le modifiche del seguente progetto???\n" + project.toString();
			int res = Jaswt.getInstance().lunchMB(shellDialog, SWT.YES | SWT.NO, "Confermi???", msg);
			if (res == SWT.YES) {
				PHPitoManager.getInstance().getReentrantLockXMLServer().updateProject(project);
				PHPitoManager.getInstance().getReentrantLockLogServer().renameDirProjectLog(oldIdName, project.getIdAndName());
//				Jaswt.getInstance().lunchMB(shellDialog, SWT.OK, "OK", "Modifiche salvate con sucesso.");
				if (restart && !PHPitoManager.getInstance().startServer(project))
					Jaswt.getInstance().lunchMB(shellDialog, SWT.OK, "FAIL!!!", "L'avvio del server non ha avuto sucesso.");
				ShellPHPito shellPHPito = shellDialog.getShellPHPito();
				shellDialog.dispose();
				shellPHPito.flushTable();
				shellPHPito.forceFocus();
				
			}
		} catch (ProjectException | IOException | ServerException e) {
			Jaswt.getInstance().lunchMBError(shellDialog, e, PHPitoManager.NAME);
		}
	}

	
}
