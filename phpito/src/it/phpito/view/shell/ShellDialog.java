package it.phpito.view.shell;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;


public class ShellDialog extends Shell {
	
	/* costruttore */
	public ShellDialog(Shell parent, int style) {
		super(parent, style | SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
	}

	/* costruttore senza style */
	public ShellDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
	}

	@Override
	/* override del metodo check - per evitare il controllo della subclass */
	protected void checkSubclass() {
	}
}
