package phpito.view.listener.selection.project;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;

import exception.XMLException;
import jaswt.core.Jaswt;
import jaswt.listener.selection.CloserShellSelectionAdpter;
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
		} catch (IOException | ServerException | ProjectException | XMLException e) {
			Jaswt.getInstance().launchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
		}

		/* shell answer and delete if yes */
		shellDialogPHPito = new ShellDialogPHPito(shellPHPito);
		shellDialogPHPito.setSize(300, 300);
		shellDialogPHPito.setText("DELETE???");
		Jaswt.getInstance().centerWindow(shellDialogPHPito);
		createShellContents();
		shellDialogPHPito.open();
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
				new CloserShellSelectionAdpter(shellDialogPHPito),
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent evnt) {
						try {
							deleteProject();
							shellDialogPHPito.dispose();
						} catch (XMLException | ProjectException e) {
							Jaswt.getInstance().launchMBError(shellDialogPHPito, e, PHPitoManager.getInstance().getJoggerError());
						}
					}
					
				}
		};
		Jaswt.getInstance().printButtonHorizontal(namesList, 15, 220, 130, 30, 10, shellDialogPHPito, selAdptList);
	}

	/* method to delete project */
	private void deleteProject() throws XMLException, ProjectException {
		PHPitoManager.getInstance().getReentrantLockProjectsXML().deleteProject(project.getIdString());
		if (deleteLogCheckBtn.getSelection()) PHPitoManager.getInstance().getReentrantLockLogServer().deleteLog(project);
		if (deletePhpiniCheckBtn.getSelection()) PHPitoManager.getInstance().deletePhpini(project);
		shellPHPito.flushTable();
		shellPHPito.getTable().forceFocus();
	}
}
