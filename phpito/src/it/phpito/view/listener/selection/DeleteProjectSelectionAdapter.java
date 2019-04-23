package it.phpito.view.listener.selection;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.as.utils.view.UtilsViewAS;
import it.phpito.controller.PHPitoManager;
import it.phpito.data.Project;
import it.phpito.exception.ProjectException;
import it.phpito.exception.ServerException;
import it.phpito.view.shell.ShellPHPito;

public class DeleteProjectSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito;

	public DeleteProjectSelectionAdapter(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void widgetSelected(SelectionEvent se) {
		Project project = shellPHPito.getProjectSelect();
		int res;
		try {
			if (project.getServer().isRunnig()) {
				res = UtilsViewAS.getInstance().lunchMB(shellPHPito, SWT.YES | SWT.NO, "ATTENZIONE???", "Attenzione!!! Il progetto che si vuole eliminare e' in esecuzione?\n"
																									+ "Per poterlo elimnare verra' arrestato.\n"
																									+ "Continuare???");
				if (res == SWT.NO)
					return;
				
				PHPitoManager.getInstance().stopServer(project);
			}
		} catch (IOException | ServerException | ProjectException e) {
			UtilsViewAS.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.NAME);
		}
		res = UtilsViewAS.getInstance().lunchMB(shellPHPito, SWT.YES | SWT.NO, "ELIMINO???", "Sei sicuro di voler eliminare il seguente progetto?\n"
																						+ shellPHPito.getProjectSelect().toString());
		if (res == SWT.YES) {
			PHPitoManager.getInstance().getReentrantLockXMLServer().deleteProject(project.getIdString());
			res = UtilsViewAS.getInstance().lunchMB(shellPHPito, SWT.YES | SWT.NO, "ELIMINO???", "Vuoi eliminare anche i file di log del server?");
			if (res == SWT.YES)
				PHPitoManager.getInstance().getReentrantLockLogServer().deleteLog(project);
			shellPHPito.flushTable();
			shellPHPito.getTable().forceFocus();
		}
	}
}
