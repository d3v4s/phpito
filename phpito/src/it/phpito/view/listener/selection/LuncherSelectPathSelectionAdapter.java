package it.phpito.view.listener.selection;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.as.utils.view.UtilsViewAS;
import it.phpito.data.Project;
import it.phpito.view.shell.ShellDialogPHPito;

public class LuncherSelectPathSelectionAdapter extends SelectionAdapter {
	private ShellDialogPHPito shellDialog;

	public LuncherSelectPathSelectionAdapter(ShellDialogPHPito shellDialog) {
		super();
		this.shellDialog = shellDialog;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		String path = UtilsViewAS.getInstance().lunchDirectoryDialog(shellDialog, shellDialog.getTextMap().get(Project.K_PATH).getText());
		if (!(path == null || path.isEmpty()))
			shellDialog.getTextMap().get(Project.K_PATH).setText(path);
	}
}
