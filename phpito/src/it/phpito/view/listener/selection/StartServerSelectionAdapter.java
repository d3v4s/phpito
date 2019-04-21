package it.phpito.view.listener.selection;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.phpito.view.shell.ShellPHPito;
import it.phpito.view.utils.UtilsViewPHPito;

public class StartServerSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito; 

	public StartServerSelectionAdapter(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void widgetSelected(SelectionEvent se) {
		UtilsViewPHPito.getInstance().startServer(shellPHPito);
	}
}
