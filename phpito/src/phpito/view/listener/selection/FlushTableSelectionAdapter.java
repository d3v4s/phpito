package phpito.view.listener.selection;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import jaswt.core.Jaswt;
import phpito.core.PHPitoManager;
import phpito.exception.ProjectException;
import phpito.view.shell.ShellPHPito;

/**
 * Class SelectionAdapter for flush the projects table 
 * @author Andrea Serra
 *
 */
public class FlushTableSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito;

	/* CONSTRUCT */
	public FlushTableSelectionAdapter(ShellPHPito shellPHPito) {
		this.shellPHPito = shellPHPito;
	}

	/* click event */
	@Override
	public void widgetSelected(SelectionEvent evnt) {
		try {
			shellPHPito.flushTable();
			shellPHPito.getTable().forceFocus();
		} catch (ProjectException e) {
			Jaswt.getInstance().launchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
		}
	}
}
