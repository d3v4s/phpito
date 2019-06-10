package it.phpito.view.listener.selection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Spinner;

import it.jaswt.core.Jaswt;
import it.jutilas.core.Jutilas;
import it.jutilas.exception.FileException;
import it.phpito.core.PHPitoConf;
import it.phpito.core.PHPitoManager;
import it.phpito.view.shell.ShellPHPito;
import it.phpito.view.shell.dialog.ShellDialogSettings;

public class SaveConfSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito;
	private ShellDialogSettings shellDialogSettings;

	public SaveConfSelectionAdapter(ShellDialogSettings shellDialogPHPito) {
		super();
		this.shellDialogSettings = shellDialogPHPito;
		shellPHPito = shellDialogPHPito.getShellPHPito();
	}

	@Override
	public void widgetSelected(SelectionEvent se) {
		HashMap<String, String> confMap = new HashMap<String, String>();
		HashMap<String, Button> chckBttnMap = shellDialogSettings.getConfChckBttnMap();
		HashMap<String, Spinner> spinnerMap = shellDialogSettings.getConfSpinnerMap();
		HashMap<String, String> colorsLogMonMap = shellDialogSettings.getConfColorLogMonMap(); 
		for (String key : chckBttnMap.keySet())
			confMap.put(key, String.valueOf(chckBttnMap.get(key).getSelection()));
		for (String key : spinnerMap.keySet())
			confMap.put(key, String.valueOf(spinnerMap.get(key).getSelection()));
		for (String key : colorsLogMonMap.keySet())
			confMap.put(key, colorsLogMonMap.get(key));
		confMap.put(PHPitoConf.K_CONF_STYLE_LOG_MON, String.valueOf(shellDialogSettings.getStyleLogMonCombo().getSelectionIndex()));
		try {
			PHPitoConf.getInstance().saveConf(confMap);
			shellDialogSettings.dispose();
			String msg = "Attenzione per visualizzare alcune modifiche e' necessario riavviare l'applicazione.\n"
						+ "Riavviare ora???";
			int res = Jaswt.getInstance().lunchMB(shellPHPito, SWT.YES | SWT.NO, "CONFERMI???", msg);
			if (res == SWT.YES)
				Jutilas.getInstance().restartApp();
		} catch (FileException | URISyntaxException | IOException e) {
			Jaswt.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
		}
	}
}
