package it.phpito.view.listener.selection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.as.utils.view.UtilsViewAS;
import it.phpito.controller.PHPitoManager;
import it.phpito.view.shell.ShellPHPito;

public class DeleteProjectSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito;

	public DeleteProjectSelectionAdapter(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		int res = UtilsViewAS.getInstance().lunchMB(shellPHPito, SWT.YES | SWT.NO, "ELIMINO???", "Sei sicuro di voler eliminare il seguente progetto?\n"
																						+ shellPHPito.getProjectSeclect().toString());
		if (res == SWT.YES) {
			PHPitoManager.getInstance().getReentrantLockXMLServer().deleteProject(shellPHPito.getIdProjectSelectString());
			shellPHPito.flushTable();
			shellPHPito.getTable().forceFocus();
		}
	}
}
