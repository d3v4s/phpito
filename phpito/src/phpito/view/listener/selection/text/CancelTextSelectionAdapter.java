package phpito.view.listener.selection.text;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import phpito.view.shell.dialog.ShellDialogPHPito;

/**
 * Class SelectionAdapter for cancel text in text area and if empty close the shell
 * @author Andrea Serra
 *
 */
public class CancelTextSelectionAdapter extends SelectionAdapter {
	private ShellDialogPHPito shellDialog;

	/* CONSTRUCT */
	public CancelTextSelectionAdapter(ShellDialogPHPito shellDialog) {
		super();
		this.shellDialog = shellDialog;
	}

	/* click event */
	@Override
	public void widgetSelected(SelectionEvent e) {
		/* if empty texts close shell */
		if (shellDialog.isTextsEmpty()) shellDialog.dispose();
		/* else emptying texts */
		else shellDialog.emptyingText();
	}
}
