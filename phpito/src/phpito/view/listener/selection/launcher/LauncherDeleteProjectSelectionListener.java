package phpito.view.listener.selection.launcher;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;

import jaswt.core.Jaswt;
import jaswt.listener.selection.CloserShellSelectionAdpter;
import phpito.core.PHPitoManager;
import phpito.data.Project;
import phpito.exception.ProjectException;
import phpito.exception.ServerException;
import phpito.view.listener.selection.project.DeleteProjectSelectionAdapter;
import phpito.view.shell.ShellPHPito;
import phpito.view.shell.dialog.ShellDialogPHPito;

/**
 * Class SelectionListener to launch window for delete a project
 * @author Andrea Serra
 *
 */
public class LauncherDeleteProjectSelectionListener implements SelectionListener {
	private ShellPHPito shellPHPito;
	private ShellDialogPHPito shellDialogPHPito;
	private Project project;
	

	/* CONSTRUCT */
	public LauncherDeleteProjectSelectionListener(ShellPHPito shellPHPito) {
		this.shellPHPito = shellPHPito;
	}

	/* ################################################################################# */
	/* START GET AND SET */
	/* ################################################################################# */

	public ShellPHPito getShellPHPito() {
		return shellPHPito;
	}
	public void setShellPHPito(ShellPHPito shellPHPito) {
		this.shellPHPito = shellPHPito;
	}
	public ShellDialogPHPito getShellDialogPHPito() {
		return shellDialogPHPito;
	}
	public void setShellDialogPHPito(ShellDialogPHPito shellDialogPHPito) {
		this.shellDialogPHPito = shellDialogPHPito;
	}

	/* ################################################################################# */
	/* END GET AND SET */
	/* ################################################################################# */

	@Override
	public void widgetDefaultSelected(SelectionEvent evnt) {
	}

	/* click event */
	@Override
	public void widgetSelected(SelectionEvent evnt) {
		launchDeleteProject();
	}

	/* method to show delete project window */
	public void launchDeleteProject() {
		project = shellPHPito.getProjectSelect();
		int res;
		try {
			/* check if project is running */
			if (project.getServer().isRunning()) {
				String msg = "Caution!!! The project you want to delete is runnig.?\nContinue???";
				res = Jaswt.getInstance().launchMB(shellPHPito, SWT.YES | SWT.NO, "CAUTION!!!", msg);
				if (res == SWT.NO) return;
				PHPitoManager.getInstance().stopServer(project);
			}
		} catch (IOException | ServerException | ProjectException e) {
			Jaswt.getInstance().launchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
		}

		/* shell for answer and delete if yes */
		ShellDialogDeleteProject shellDialogDeleteProject = new ShellDialogDeleteProject(shellPHPito);
		shellDialogDeleteProject.open();
	}

	/* ################################################################################# */
	/* START INNER CLASS */
	/* ################################################################################# */

	/**
	 * Public inner class for create import export shell dialog
	 * @author Andrea Serra
	 *
	 */
	public class ShellDialogDeleteProject extends ShellDialogPHPito {
		private Button deletePhpiniCheckBtn;
		private Button deleteLogCheckBtn;

		/* CONSTRUCT */
		public ShellDialogDeleteProject(ShellPHPito shellPHPito) {
			super(shellPHPito);
		}

		public Button getDeletePhpiniCheckBtn() {
			return deletePhpiniCheckBtn;
		}
		public Button getDeleteLogCheckBtn() {
			return deleteLogCheckBtn;
		}
		public Project getProject() {
			return project;
		}

		@Override
		protected void createContents() {
			shellDialogPHPito.setSize(300, 300);
			shellDialogPHPito.setText("DELETE???");
			Jaswt.getInstance().centerWindow(shellDialogPHPito);

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
					new DeleteProjectSelectionAdapter(this)
			};
			Jaswt.getInstance().printButtonHorizontal(namesList, 15, 220, 130, 30, 10, shellDialogPHPito, selAdptList);
		}
	}

	/* ################################################################################# */
	/* END INNER CLASS */
	/* ################################################################################# */

}
