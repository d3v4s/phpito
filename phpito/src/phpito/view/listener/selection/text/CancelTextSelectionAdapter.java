package phpito.view.listener.selection.text;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import phpito.view.shell.dialog.ShellDialogProject;

/**
 * Class SelectionAdapter for cancel text in text area and if empty close the shell
 * @author Andrea Serra
 *
 */
public class CancelTextSelectionAdapter extends SelectionAdapter {
	private ShellDialogProject shellDialogProject;

	/* CONSTRUCT */
	public CancelTextSelectionAdapter(ShellDialogProject shellDialogProject) {
		super();
		this.shellDialogProject = shellDialogProject;
	}

	/* click event */
	@Override
	public void widgetSelected(SelectionEvent e) {
		/* if empty texts close shell */
		if (shellDialogProject.isTextsEmpty()) shellDialogProject.dispose();
		/* else emptying texts */
		else shellDialogProject.emptyingText();
	}
}
