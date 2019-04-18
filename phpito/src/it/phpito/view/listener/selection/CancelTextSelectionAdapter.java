package it.phpito.view.listener.selection;

import java.util.HashMap;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

import it.phpito.view.shell.ShellDialogPHPito;

public class CancelTextSelectionAdapter extends SelectionAdapter {
	private ShellDialogPHPito shellDialog;
	private HashMap<String, Text> textMap;

	public CancelTextSelectionAdapter(ShellDialogPHPito shellDialog, HashMap<String, Text> textMap) {
		super();
		this.shellDialog = shellDialog;
		this.textMap = textMap;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (shellDialog.isTextsEmpty(textMap))
			shellDialog.dispose();
		else
			shellDialog.emptyingText(textMap);
	}

	
}
