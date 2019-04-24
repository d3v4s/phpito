package it.phpito.view.listener.selection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

import it.as.utils.core.UtilsAS;
import it.as.utils.exception.FileException;
import it.as.utils.view.UtilsViewAS;
import it.phpito.controller.PHPitoConf;
import it.phpito.controller.PHPitoManager;
import it.phpito.view.shell.ShellDialogPHPito;
import it.phpito.view.shell.ShellPHPito;

public class SaveConfSelectionAdapter extends SelectionAdapter {
	private ShellDialogPHPito shellDialogPHPito;
	private ShellPHPito shellPHPito;

	public SaveConfSelectionAdapter(ShellDialogPHPito shellDialogPHPito) {
		super();
		this.shellDialogPHPito = shellDialogPHPito;
		shellPHPito = shellDialogPHPito.getShellPHPito();
	}

	@Override
	public void widgetSelected(SelectionEvent se) {
		HashMap<String, String> confMap = new HashMap<String, String>();
		HashMap<String, Button> chckBttnMap = shellDialogPHPito.getConfChckBttnMap();
		for (String key : chckBttnMap.keySet())
			confMap.put(key, String.valueOf(chckBttnMap.get(key).getSelection()));
		try {
			PHPitoConf.getInstance().saveConf(confMap);
			shellDialogPHPito.dispose();
			String msg = "Attenzione per visualizzare le modifiche e' necessario riavviare l'applicazione. Confermi???";
			int res = UtilsViewAS.getInstance().lunchMB(shellPHPito, SWT.YES | SWT.NO, "Confermi???", msg);
			if (res == SWT.YES)
				UtilsAS.getInstance().restartApp();
		} catch (FileException | URISyntaxException | IOException e) {
			UtilsViewAS.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.NAME);
		}
	}
}
