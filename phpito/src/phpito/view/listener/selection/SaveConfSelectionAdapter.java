package phpito.view.listener.selection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Spinner;

import jaswt.core.Jaswt;
import jutilas.core.Jutilas;
import jutilas.exception.FileException;
import phpito.core.PHPitoConf;
import phpito.core.PHPitoManager;
import phpito.view.shell.ShellPHPito;
import phpito.view.shell.dialog.ShellDialogSettings;

/**
 * Class SelectionAdapter for save PHPito configuration
 * @author Andrea Serra
 *
 */
public class SaveConfSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito;
	private ShellDialogSettings shellDialogSettings;

	/* CONTRUCT */
	public SaveConfSelectionAdapter(ShellDialogSettings shellDialogPHPito) {
		super();
		this.shellDialogSettings = shellDialogPHPito;
		shellPHPito = shellDialogPHPito.getShellPHPito();
	}

	/* click event */
	@Override
	public void widgetSelected(SelectionEvent se) {
		HashMap<String, String> confMap = new HashMap<String, String>();
		HashMap<String, Button> chckBttnMap = shellDialogSettings.getConfChckBttnMap();
		HashMap<String, Spinner> spinnerMap = shellDialogSettings.getConfSpinnerMap();
		HashMap<String, String> colorsLogMonMap = shellDialogSettings.getConfColorLogMonMap(); 
		/* get configuartion by shell elements */
		for (String key : chckBttnMap.keySet()) confMap.put(key, String.valueOf(chckBttnMap.get(key).getSelection()));
		for (String key : spinnerMap.keySet()) confMap.put(key, String.valueOf(spinnerMap.get(key).getSelection()));
		for (String key : colorsLogMonMap.keySet()) confMap.put(key, colorsLogMonMap.get(key));
		confMap.put(PHPitoConf.K_CONF_STYLE_LOG_MON, String.valueOf(shellDialogSettings.getStyleLogMonCombo().getSelectionIndex()));

		/* save configuration */
		try {
			PHPitoConf.getInstance().saveConf(confMap);
			shellDialogSettings.dispose();
			String msg = "To view some changes and 'need to restart the application.\nRestart now??";
			int res = Jaswt.getInstance().launchMB(shellPHPito, SWT.YES | SWT.NO, "RESTART???", msg);
			if (res == SWT.YES) Jutilas.getInstance().restartApp();
		} catch (FileException | URISyntaxException | IOException e) {
			Jaswt.getInstance().launchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
		}
	}
}
