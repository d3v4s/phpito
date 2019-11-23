package phpito.view.shell.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import phpito.exception.ProjectException;
import phpito.view.shell.ShellPHPito;

/**
 * Abstract class Shell for PHPito dialog shell
 * @author Andrea Serra
 *
 */
public abstract class ShellDialogPHPito extends Shell {
	protected ShellPHPito shellPHPito;
	
	/* ################################################################################# */
	/* START CONSTRUCTORS */
	/* ################################################################################# */

	public ShellDialogPHPito(ShellPHPito shellPHPito, int style) {
		super(shellPHPito, style | SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		this.shellPHPito = shellPHPito;
	}

	/* costruttore senza style */
	public ShellDialogPHPito(ShellPHPito shellPHPito) {
		super(shellPHPito, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		this.shellPHPito = shellPHPito;
	}

	/* ################################################################################# */
	/* END CONSTRUCTORS */
	/* ################################################################################# */

	/* ################################################################################# */
	/* START OVERRIDE */
	/* ################################################################################# */

	/* override to bypass check subclass error */
	@Override
	protected void checkSubclass() {
	}

	@Override
	public void open() {
		createContents();
		super.open();
	}

	/* ################################################################################# */
	/* END OVERRIDE */
	/* ################################################################################# */

	/* ################################################################################# */
	/* START ABSTRACT METHODS */
	/* ################################################################################# */

	protected abstract void createContents();

	/* ################################################################################# */
	/* END ABSTRACT METHODS */
	/* ################################################################################# */

	/* ################################################################################# */
	/* START GET AND SET */
	/* ################################################################################# */

	public ShellPHPito getShellPHPito() {
		return shellPHPito;
	}
	public void setShellPHPito(ShellPHPito shellPHPito) {
		this.shellPHPito = shellPHPito;
	}

	/* ################################################################################# */
	/* END GET AND SET */
	/* ################################################################################# */

	/* method that flush the table on ShellPHPito and dispose the shellDialog */
	public void flushTableAndDispose() throws ProjectException {
		shellPHPito.flushTableAndFocus();
		shellPHPito.getTable().forceFocus();
		this.dispose();
	}
}
