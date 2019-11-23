package phpito.view.listener.selection;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

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
		shellPHPito.flushTableAndFocus();
	}
}
