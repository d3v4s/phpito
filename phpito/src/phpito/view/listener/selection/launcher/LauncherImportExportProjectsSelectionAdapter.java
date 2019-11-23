package phpito.view.listener.selection.launcher;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import jaswt.core.Jaswt;
import jaswt.listener.selection.CloserShellSelectionAdpter;
import jaswt.listener.selection.LauncherSelectFileSelectionAdapter;
import jaswt.listener.selection.LauncherSelectPathSelectionAdapter;
import jutilas.exception.FileException;
import phpito.core.PHPitoManager;
import phpito.exception.PHPitoException;
import phpito.exception.ProjectException;
import phpito.exception.ServerException;
import phpito.view.shell.ShellPHPito;
import phpito.view.shell.dialog.ShellDialogPHPito;

/**
 * Class SelectionAdpater for launch import or export projects window
 * @author Andrea Serra
 *
 */
public class LauncherImportExportProjectsSelectionAdapter implements SelectionListener {
	public static final int IMPORT = 0;
	public static final int EXPORT = 1;
	private ShellPHPito shellPHPito;
	private Text pathText;
	private int action;

	/* CONSTRUCT */
	public LauncherImportExportProjectsSelectionAdapter(ShellPHPito shellPHPito, int action) {
		this.shellPHPito = shellPHPito;
		this.action = action;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	/* click event */
	@Override
	public void widgetSelected(SelectionEvent e) {
		ShellDialogImportExport shellDialogImportExport = new ShellDialogImportExport(shellPHPito);
		shellDialogImportExport.open();
	}

	/* ################################################################################# */
	/* START INNER CLASS */
	/* ################################################################################# */

	/**
	 * Private inner class for create import export shell dialog
	 * @author Andrea Serra
	 *
	 */
	private class ShellDialogImportExport extends ShellDialogPHPito {

		public ShellDialogImportExport(ShellPHPito shellPHPito) {
			super(shellPHPito);
		}

		@Override
		protected void createContents() {
			this.setSize(500, 200);
			Jaswt.getInstance().centerWindow(this);

			/* text for select path */
			Label label = new Label(this, SWT.NONE);
			label.setBounds(20, 45, 30, 30);
			label.setText("Path:");
			pathText = new Text(this, SWT.NONE);
			pathText.setBounds(60, 40, 330, 30);

			/* set variables by action */
			String[] bttnNameList = new String[2];
			SelectionListener slctnLstnrBttnSelectPath = null;
			bttnNameList[0] = "Cancel";
			switch (action) {
				case IMPORT:
					this.setText("Import Projects");
					bttnNameList[1] = "Import";
					slctnLstnrBttnSelectPath = new LauncherSelectFileSelectionAdapter(shellPHPito, pathText);
					break;
				case EXPORT:
					this.setText("Export Projects");
					bttnNameList[1] = "Export";
					slctnLstnrBttnSelectPath = new LauncherSelectPathSelectionAdapter(shellPHPito, pathText);
					break;
				default:
					Jaswt.getInstance().launchMBError(shellPHPito, new PHPitoException("Invalid action!!!"), PHPitoManager.getInstance().getJoggerError());
					return;
			}

			/* button to select path*/
			Button button = new Button(this, SWT.PUSH);
			button.setBounds(400, 40, 80, 30);
			button.addSelectionListener(slctnLstnrBttnSelectPath);
			button.setText("Select");

			/* ################################################################################# */
			/* START LOCAL CLASS */
			/* ################################################################################# */

			/**
			 * Local class SelectionAdapter for import or export projects
			 * @author Andrea Serra
			 *
			 */
			class ImportExportProjectsSelectionAdapter implements SelectionListener {
				ShellDialogPHPito shellDialogPHPito;
				
				/* CONSTRUCT*/
				private ImportExportProjectsSelectionAdapter(ShellDialogPHPito shellDialogPHPito) {
					super();
					this.shellDialogPHPito = shellDialogPHPito;
				}
				
				/* click event */
				@Override
				public void widgetSelected(SelectionEvent evnt) {
					switch (action) {
					case IMPORT:
						int res = Jaswt.getInstance().launchMB(shellDialogPHPito, SWT.YES | SWT.NO, "CONTINUE???", "The currently saved projects will be deleted, along with their logs and phpini files. Continue???");
						if (res == SWT.YES) {
							try {
								PHPitoManager.getInstance().stopAllRunningServer();
								PHPitoManager.getInstance().getReentrantLockLogServer().deleteAllLog();
								PHPitoManager.getInstance().deleteAllPhpini();
								PHPitoManager.getInstance().getReentrantLockProjectsXML().importXML(pathText.getText());
								PHPitoManager.getInstance().flushRunningServers();
								shellDialogPHPito.flushTableAndDispose();
							} catch (FileException | ProjectException | IOException | ServerException e) {
								Jaswt.getInstance().launchMBError(shellDialogPHPito, e, PHPitoManager.getInstance().getJoggerError());
							}
						}
						break;
					case EXPORT:
						try {
							PHPitoManager.getInstance().getReentrantLockProjectsXML().exportXML(pathText.getText());
							shellDialogPHPito.dispose();
						} catch (FileException e) {
							Jaswt.getInstance().launchMBError(shellDialogPHPito, e, PHPitoManager.getInstance().getJoggerError());
						}
						break;
					default:
						return;
					}
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent evnt) {
				}
			}

			/* ################################################################################# */
			/* END LOCAL CLASS */
			/* ################################################################################# */
			
			/* cancel and confirm buttons */
			SelectionListener[] slctnAdptrList = {
				new CloserShellSelectionAdpter(this),
				new ImportExportProjectsSelectionAdapter(this)
			};
			Jaswt.getInstance().printButtonHorizontal(bttnNameList, 250, 110, 100, 30, 10, this, slctnAdptrList);
		}
	}

	/* ################################################################################# */
	/* END INNER CLASS */
	/* ################################################################################# */
}

