package it.phpito.view.listener.selection;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.as.utils.view.UtilsViewAS;
import it.phpito.view.shell.ShellDialog;
import it.phpito.view.shell.ShellPHPito;

public class AddProjectSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellParent;
	public AddProjectSelectionAdapter(ShellPHPito shellParent) {
		this.shellParent = shellParent;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		ShellDialog shellDialog = new ShellDialog(shellParent);
		lunchAddProject(shellDialog);
	}
	
	/* metodo per lanciare finestra che aggiunge il progetto */
	private void lunchAddProject(ShellDialog shellDialog) {
		shellDialog.setSize(470, 600);
		shellDialog.setText("Nuovo Progetto");
		UtilsViewAS.getInstance().centerWindow(shellDialog);
		
		shellDialog.open();
	}

}
