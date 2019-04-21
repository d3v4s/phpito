package it.phpito.view.listener.selection;

import java.util.HashMap;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

import it.phpito.view.shell.ShellDialogPHPito;

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
