package phpito.view.listener.selection.project;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

import jaswt.core.Jaswt;
import phpito.core.PHPitoManager;
import phpito.data.Project;
import phpito.data.Server;
import phpito.exception.ProjectException;
import phpito.view.shell.ShellPHPito;
import phpito.view.shell.dialog.ShellDialogPHPito;

/**
 * Class SelectionAdapter for add new project
 * @author Andrea Serra
 *
 */
public class AddProjectSelectionAdapter extends SelectionAdapter {
	private ShellDialogPHPito shellDialog;
	private ShellPHPito shellPHPito;

	/* CONSTRUCT */
	public AddProjectSelectionAdapter(ShellDialogPHPito shellDialog) {
		super();
		this.shellDialog = shellDialog;
		shellPHPito = shellDialog.getShellPHPito();
	}

	/* event select */
	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		widgetSelected(event);
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent event) {
		Project project = new Project();
		HashMap<String, Text> textMap = shellDialog.getTextMap();
		try {
			project.setName(textMap.get(Project.K_NAME).getText());
			project.setLogActive(shellDialog.getLogActvChckBttn().getSelection());
			project.setPhpini(shellDialog.getPhpiniCombo().getSelectionIndex());
			project.setServer(new Server());
			project.getServer().setPath(textMap.get(Project.K_PATH).getText());
			project.getServer().setAddress(textMap.get(Project.K_ADDRESS).getText());
			project.getServer().setPortString(textMap.get(Project.K_PORT).getText());

			String msg = "Save this project???\n" + project.toString();
			int res = Jaswt.getInstance().launchMB(shellDialog, SWT.YES | SWT.NO, "SAVE???", msg);
			if (res == SWT.YES) {
				PHPitoManager.getInstance().getReentrantLockXMLServer().addProject(project);
				project.getPhpiniPath();
				shellPHPito.flushTable();
				shellPHPito.getTable().forceFocus();
				shellDialog.dispose();
			}
		} catch (ProjectException e) {
			Jaswt.getInstance().launchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
		}
	}
}
