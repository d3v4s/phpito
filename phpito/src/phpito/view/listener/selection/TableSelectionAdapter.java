package phpito.view.listener.selection;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import phpito.view.listener.selection.launcher.LauncherSettingsProjectSelectionAdapter;
import phpito.view.shell.ShellPHPito;

/**
 * Class SelectionAdapter for projects table
 * @author Andrea Serra
 *
 */
public class TableSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito;

	/* CONSTRUCT */
	public TableSelectionAdapter(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}

	/* event click */
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		/* view project settings */
		new LauncherSettingsProjectSelectionAdapter(shellPHPito).launchModifyProject();;
	}

	/* select event */
	@Override
	public void widgetSelected(SelectionEvent e) {
		/* set id of project select */
		shellPHPito.autoSetIdProjectSelect();
	}
}
