package phpito.view.listener.selection.project;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import jaswt.core.Jaswt;
import phpito.core.PHPitoManager;
import phpito.data.Project;
import phpito.exception.ProjectException;
import phpito.exception.ServerException;
import phpito.view.shell.ShellPHPito;

/**
 * Class SelecrionAdapter to delete project
 * @author Andrea Serra
 *
 */
public class DeleteProjectSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito;

	/* CONSTRUCT */
	public DeleteProjectSelectionAdapter(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent se) {
		deleteProject();
	}

	/* method to delete a project */
	// TODO create window for select element to be deleted
	public void deleteProject() {
		Project project = shellPHPito.getProjectSelect();
		int res;
		try {
			/* check if project is runninig */
			if (project.getServer().isRunning()) {
				String msg = "Caution!!! The project you want to delete is runnig.?\nContinue???";
				res = Jaswt.getInstance().launchMB(shellPHPito, SWT.YES | SWT.NO, "CAUTION!!!", msg);
				if (res == SWT.NO) return;
				
				PHPitoManager.getInstance().stopServer(project);
			}
		} catch (IOException | ServerException | ProjectException e) {
			Jaswt.getInstance().launchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
		}

		// TODO create shell for select element to be delete
		/* answer and delete if yes */
		res = Jaswt.getInstance().launchMB(shellPHPito, SWT.YES | SWT.NO, "DELETE???", "Delete this project?\n" + shellPHPito.getProjectSelect().toString());
		if (res == SWT.YES) {
			PHPitoManager.getInstance().getReentrantLockXMLServer().deleteProject(project.getIdString());
			res = Jaswt.getInstance().launchMB(shellPHPito, SWT.YES | SWT.NO, "DELETE???", "Do you want to delete also the server log files and the php.ini?");
			if (res == SWT.YES) {
				PHPitoManager.getInstance().getReentrantLockLogServer().deleteLog(project);
				try {
					PHPitoManager.getInstance().deletePhpini(project);
				} catch (ProjectException e) {
					Jaswt.getInstance().launchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
				}
			}
			shellPHPito.flushTable();
			shellPHPito.getTable().forceFocus();
		}
	}
}
