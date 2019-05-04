package it.phpito.view.listener.key;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

import it.phpito.view.listener.selection.server.StartServerSelectionAdapter;
import it.phpito.view.listener.selection.server.StopServerSelectionAdapter;
import it.phpito.view.shell.ShellPHPito;

public class StartStopServerKeyAdapter extends KeyAdapter {
	private ShellPHPito shellPHPito;

	public StartStopServerKeyAdapter(ShellPHPito shellPHPito) {
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void keyPressed(KeyEvent ke) {
		int kc = ke.keyCode;
		if (!(kc == SWT.ARROW_UP || kc == SWT.ARROW_DOWN)) {
			ke.doit = false;
		}
		if (String.valueOf(ke.character).equals("r"))
			new StartServerSelectionAdapter(shellPHPito).startServer();
		else if (String.valueOf(ke.character).equals("s"))
			new StopServerSelectionAdapter(shellPHPito).stopServer();
	}
	
	
}
