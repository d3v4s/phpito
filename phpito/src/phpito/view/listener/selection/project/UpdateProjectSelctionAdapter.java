package phpito.view.listener.selection.project;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import exception.XMLException;
import jaswt.core.Jaswt;
import jutilas.exception.FileException;
import phpito.core.PHPitoManager;
import phpito.data.Project;
import phpito.exception.ProjectException;
import phpito.exception.ServerException;
import phpito.view.shell.ShellPHPito;
import phpito.view.shell.dialog.ShellDialogPHPito;

/**
 * Class SelectionAdapter for update a project
 * @author Andrea Serra
 *
 */
public class UpdateProjectSelctionAdapter extends SelectionAdapter {
	private ShellDialogPHPito shellDialog;
	private ShellPHPito shellPHPito;

	/* CONSTRUCT */
	public UpdateProjectSelctionAdapter(ShellDialogPHPito shellDialog) {
		super();
		this.shellDialog = shellDialog;
		shellPHPito = shellDialog.getShellPHPito();
	}

	/* event select */
	@Override
	public void widgetDefaultSelected(SelectionEvent se) {
		widgetSelected(se);
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent se) {
		Project project = shellDialog.getShellPHPito().getProjectSelect();
		try {
			boolean restart = false;
			/* check if server is running */
			if (project.getServer().isRunning()) {
				String msg = "Caution!!! The server is running, to continue it must be stopped.\nThe server will be restarted after the changes are complete. Continue???";
				int res = Jaswt.getInstance().launchMB(shellDialog, SWT.YES | SWT.NO, "CAUTION!!!", msg);
				if (res == SWT.NO) return;

				restart = true;
				if (!PHPitoManager.getInstance().stopServer(project)) {
					Jaswt.getInstance().launchMB(shellDialog, SWT.OK, "FAIL!!!", "Server shutdown failed.");
					return;
				}
			}

			/* set update */
			String oldIdName = project.getIdAndName();
			project.setName(shellDialog.getTextMap().get(Project.K_NAME).getText());
			project.setLogActive(shellDialog.getLogActvChckBttn().getSelection());
			project.setPhpini(shellDialog.getPhpiniCombo().getSelectionIndex());
			project.getServer().setPath(shellDialog.getTextMap().get(Project.K_PATH).getText());
			project.getServer().setAddress(shellDialog.getTextMap().get(Project.K_ADDRESS).getText());
			project.getServer().setPortString(shellDialog.getTextMap().get(Project.K_PORT).getText());

			/* answer and save update */
			String msg = "Save the changes???\n" + project.toString();
			int res = Jaswt.getInstance().launchMB(shellDialog, SWT.YES | SWT.NO, "CONTINUE???", msg);
			if (res == SWT.YES) {
				PHPitoManager.getInstance().getReentrantLockProjectsXML().updateProject(project);
				PHPitoManager.getInstance().getReentrantLockLogServer().renameProjectLogDir(oldIdName, project.getIdAndName());
				PHPitoManager.getInstance().renamePhpini(oldIdName, project.getIdAndName());
				project.getPhpiniPath();
				if (restart && !PHPitoManager.getInstance().startServer(project)) Jaswt.getInstance().launchMB(shellDialog, SWT.OK, "FAIL!!!", "Server startup failed.");
				shellPHPito.flushTable();
				shellPHPito.getTable().forceFocus();
				shellDialog.dispose();
				
			}
		} catch (ProjectException | IOException | ServerException | FileException | XMLException e) {
			Jaswt.getInstance().launchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
		}
	}

	
}
