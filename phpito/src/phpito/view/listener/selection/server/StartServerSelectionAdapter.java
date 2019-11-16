package phpito.view.listener.selection.server;

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
 * Class SelectionAdapter for start a server
 * @author Andrea Serra
 *
 */
public class StartServerSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito; 

	/* CONSTRUCT */
	public StartServerSelectionAdapter(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent se) {
		startServer();
	}

	/* method to start a server */
	public void startServer() {
		try {
			Project p = PHPitoManager.getInstance().getProjectById(shellPHPito.getIdProjectSelect());
			if (!PHPitoManager.getInstance().startServer(p)) Jaswt.getInstance().launchMB(shellPHPito, SWT.OK, "FAIL!!!", "Server startup failed.");
		} catch (ServerException | IOException | ProjectException e) {
			Jaswt.getInstance().launchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
		} finally {
			shellPHPito.flushTable();
			shellPHPito.getTable().forceFocus();
		}
	}
}
