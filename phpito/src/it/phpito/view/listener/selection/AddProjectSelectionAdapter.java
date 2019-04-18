package it.phpito.view.listener.selection;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

import it.as.utils.exception.FileException;
import it.as.utils.view.UtilsViewAS;
import it.phpito.controller.PHPitoManager;
import it.phpito.data.Project;
import it.phpito.data.Server;
import it.phpito.exception.ProjectException;
import it.phpito.view.shell.ShellDialogPHPito;

public class AddProjectSelectionAdapter extends SelectionAdapter {
	private ShellDialogPHPito shellParent;
	private HashMap<String, Text> textMap;

	public AddProjectSelectionAdapter(ShellDialogPHPito shellParent, HashMap<String, Text> textMap) {
		super();
		this.shellParent = shellParent;
		this.textMap = textMap;
	}
	
	

	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		widgetSelected(event);
	}



	@Override
	public void widgetSelected(SelectionEvent event) {
		Project project = new Project();
		try {
			project.setName(textMap.get(ShellDialogPHPito.K_NAME).getText());
			project.setServer(new Server());
			project.getServer().setPath(textMap.get(ShellDialogPHPito.K_PATH).getText());
			project.getServer().setAddress(textMap.get(ShellDialogPHPito.K_ADDRESS).getText());
			project.getServer().setPortString(textMap.get(ShellDialogPHPito.K_PORT).getText());
			
			int res = UtilsViewAS.getInstance().lunchMB(shellParent, SWT.YES | SWT.NO, "Confermi???", "Sei sicuro di voler aggiungere il seguente progetto???\n" + project.toString());
			if (res == SWT.YES) {
				PHPitoManager.getInstance().addProject(project);
				UtilsViewAS.getInstance().lunchMB(shellParent, SWT.OK, "OK", "Nuovo progetto aggiunto con sucesso.");
				shellParent.dispose();
			}
		} catch (ProjectException | FileException e) {
			UtilsViewAS.getInstance().lunchMBError(shellParent, e, PHPitoManager.NAME);
		}
	}
}
