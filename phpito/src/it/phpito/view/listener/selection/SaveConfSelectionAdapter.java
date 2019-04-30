package it.phpito.view.listener.selection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Spinner;

import it.as.utils.core.UtilsAS;
import it.as.utils.exception.FileException;
import it.as.utils.view.UtilsViewAS;
import it.phpito.controller.PHPitoConf;
import it.phpito.controller.PHPitoManager;
import it.phpito.view.shell.ShellDialogSettings;
import it.phpito.view.shell.ShellPHPito;

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
		try {
			PHPitoConf.getInstance().saveConf(confMap);
			shellDialogSettings.dispose();
			String msg = "Attenzione per visualizzare alcune modifiche e' necessario riavviare l'applicazione.\n"
					+ "Riavviare ora???";
			int res = UtilsViewAS.getInstance().lunchMB(shellPHPito, SWT.YES | SWT.NO, "Confermi???", msg);
			if (res == SWT.YES)
				UtilsAS.getInstance().restartApp();
		} catch (FileException | URISyntaxException | IOException e) {
			UtilsViewAS.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.NAME);
		}
	}
}
