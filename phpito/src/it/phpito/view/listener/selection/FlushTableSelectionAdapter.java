package it.phpito.view.listener.selection;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.phpito.view.shell.ShellPHPito;

public class FlushTableSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito;

	public FlushTableSelectionAdapter(ShellPHPito shellPHPito) {
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		shellPHPito.flushTable();
		shellPHPito.getTable().forceFocus();
	}
}
