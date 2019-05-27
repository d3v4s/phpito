package it.phpito.view.listener.selection.text;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.phpito.view.shell.dialog.ShellDialogPHPito;

public class CancelTextSelectionAdapter extends SelectionAdapter {
	private ShellDialogPHPito shellDialog;

	public CancelTextSelectionAdapter(ShellDialogPHPito shellDialog) {
		super();
		this.shellDialog = shellDialog;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (shellDialog.isTextsEmpty())
			shellDialog.dispose();
		else
			shellDialog.emptyingText();
	}
}
