package it.phpito.view.listener.key;

import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

import it.phpito.view.shell.ShellPHPito;
import it.phpito.view.utils.UtilsViewPHPito;

public class TableKeyAdpter extends KeyAdapter {
	private ShellPHPito shellPHPito;

	public TableKeyAdpter(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void keyPressed(KeyEvent event) {
		String key = Character.toString(event.character);
		if (key.equals("r"))
			UtilsViewPHPito.getInstance().startServer(shellPHPito);
		else if (key.equals("s"))
			UtilsViewPHPito.getInstance().stopServer(shellPHPito);
	}
	
	
}
