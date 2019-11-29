package phpito.view.listener.selection.server;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import jaswt.utils.Jaswt;
import phpito.core.PHPitoManager;
import phpito.data.Project;
import phpito.exception.ProjectException;
import phpito.exception.ServerException;
import phpito.view.shell.ShellPHPito;

/**
 * Class SelectionAdapter for stop a server
 * @author Andrea Serra
 *
 */
public class StopServerSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito; 

	/* CONSTRUCT */
	public StopServerSelectionAdapter(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent se) {
		stopServer();
	}

	/* method to stop a server */ 
	public void stopServer() {
		try {
			Project p = PHPitoManager.getInstance().getProjectById(shellPHPito.getIdProjectSelect());
			if (!PHPitoManager.getInstance().stopServer(p)) Jaswt.getInstance().launchMB(shellPHPito, SWT.OK, "FAIL!!!", "Server shutdown failed.");
		} catch (IOException | ServerException | ProjectException e) {
			Jaswt.getInstance().launchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
		} finally {
			shellPHPito.flushTableAndFocus();
		}
	}
}
