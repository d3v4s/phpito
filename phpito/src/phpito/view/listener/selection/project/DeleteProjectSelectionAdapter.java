package phpito.view.listener.selection.project;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;

import jaswt.core.Jaswt;
import phpito.core.PHPitoManager;
import phpito.data.Project;
import phpito.exception.ProjectException;
import phpito.exception.ServerException;
import phpito.view.shell.ShellPHPito;
import phpito.view.shell.dialog.ShellDialogPHPito;

/**
 * Class SelecrionAdapter to delete project
 * @author Andrea Serra
 *
 */
public class DeleteProjectSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito;
	ShellDialogPHPito shellDialogPHPito;
	Button deletePhpiniCheckBtn;
	Button deleteLogCheckBtn;
	Project project;

	/* CONSTRUCT */
	public DeleteProjectSelectionAdapter(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent se) {
		showDeleteProject();
	}

	/* method to show delete project window */
	public void showDeleteProject() {
		project = shellPHPito.getProjectSelect();
		int res;
		try {
			/* check if project is runninig */
			if (project.getServer().isRunning()) {
				String msg = "Caution!!! The project you want to delete is runnig.?\nContinue???";
				res = Jaswt.getInstance().launchMB(shellPHPito, SWT.YES | SWT.NO, "CAUTION!!!", msg);
				if (res == SWT.NO) return;
				
				PHPitoManager.getInstance().stopServer(project);
			}
		} catch (IOException | ServerException | ProjectException e) {
			Jaswt.getInstance().launchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
		}

		// TODO create shell for select element to be delete
		/* shell answer and delete if yes */
		shellDialogPHPito = new ShellDialogPHPito(shellPHPito);
		shellDialogPHPito.setSize(300, 300);
		shellDialogPHPito.setText("DELETE???");
		Jaswt.getInstance().centerWindow(shellDialogPHPito);
		createShellContents();
		shellDialogPHPito.open();
//		res = Jaswt.getInstance().launchMB(shellPHPito, SWT.YES | SWT.NO, "DELETE???", "Delete this project?\n" + shellPHPito.getProjectSelect().toString());
//		if (res == SWT.YES) {
//			PHPitoManager.getInstance().getReentrantLockXMLServer().deleteProject(project.getIdString());
//			res = Jaswt.getInstance().launchMB(shellPHPito, SWT.YES | SWT.NO, "DELETE???", "Do you want to delete also the server log files and the php.ini?");
//			if (res == SWT.YES) {
//				shellPHPito.setIdProjectSelectToNull();
//				PHPitoManager.getInstance().getReentrantLockLogServer().deleteLog(project);
//				PHPitoManager.getInstance().deletePhpini(project);
//			}
//			shellPHPito.flushTable();
//			shellPHPito.getTable().forceFocus();
//		}
	}

	/* method that create contents on dialog shell */
	private void createShellContents() {
		/* info project label */
		Label label = new Label(shellDialogPHPito, SWT.CENTER);
		label.setBounds(10, 20, 280, 25);
		label.setText("Delete this project?");
		label = new Label(shellDialogPHPito, SWT.NONE);
		label.setBounds(60, 45, 180, 90);
		label.setText(shellPHPito.getProjectSelect().toString());

		/* check buttons for delete log */
		deleteLogCheckBtn = new Button(shellDialogPHPito, SWT.CHECK);
		deleteLogCheckBtn.setBounds(10, 140, 280, 25);
		deleteLogCheckBtn.setText("Delete log files");
		deleteLogCheckBtn.setSelection(true);

		/* check buttons for delete phpini */
		deletePhpiniCheckBtn = new Button(shellDialogPHPito, SWT.CHECK);
		deletePhpiniCheckBtn.setBounds(10, 170, 280, 25);
		deletePhpiniCheckBtn.setText("Delete php.ini file");
		deletePhpiniCheckBtn.setSelection(true);

		/* button no and yes */
		String[] namesList = {"No", "Yes"};
		SelectionAdapter[] selAdptList = {
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						shellDialogPHPito.dispose();
					}
					
				},
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						deleteProject();
						shellDialogPHPito.dispose();
					}
					
				}
		};
		Jaswt.getInstance().printButtonHorizontal(namesList, 15, 220, 130, 30, 10, shellDialogPHPito, selAdptList);
	}

	/* method to delete project */
	private void deleteProject() {
		PHPitoManager.getInstance().getReentrantLockXMLServer().deleteProject(project.getIdString());
		if (deleteLogCheckBtn.getSelection()) PHPitoManager.getInstance().getReentrantLockLogServer().deleteLog(project);
		if (deletePhpiniCheckBtn.getSelection()) PHPitoManager.getInstance().deletePhpini(project);
		shellPHPito.flushTable();
		shellPHPito.getTable().forceFocus();
	}
}
