package phpito.view.listener.selection.launcher;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import phpito.view.shell.ShellPHPito;
import phpito.view.shell.dialog.ShellDialogSettings;

/**
 * Class SelectionListener to launch settings window
 * @author Andrea Serra
 *
 */
public class LauncherSettingsSelctionAdapter implements SelectionListener {
	private ShellDialogSettings shellDialogSettings;
	private ShellPHPito shellPHPito;

	/* CONSTRUCT */
	public LauncherSettingsSelctionAdapter(ShellPHPito shellPHPito) {
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent evnt) {
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent se) {
		launchSettingsPHPito();
	}

	/* metodo per lanciare finestra delle impostazioni */
	public void launchSettingsPHPito() {
		shellDialogSettings = new ShellDialogSettings(shellPHPito);
		shellDialogSettings.open();
	}
}
