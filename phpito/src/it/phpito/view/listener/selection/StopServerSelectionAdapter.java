package it.phpito.view.listener.selection;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.phpito.view.shell.ShellPHPito;
import it.phpito.view.utils.UtilsViewPHPito;

public class StopServerSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito; 

	public StopServerSelectionAdapter(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void widgetSelected(SelectionEvent se) {
		UtilsViewPHPito.getInstance().stopServer(shellPHPito);
	}
}
