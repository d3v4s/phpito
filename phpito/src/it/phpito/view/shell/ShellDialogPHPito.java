package it.phpito.view.shell;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class ShellDialogPHPito extends Shell {
	private ShellPHPito shellPHPito;
	public static final String K_NAME = "name";
	public static final String K_PATH = "path";
	public static final String K_ADDRESS = "address";
	public static final String K_PORT = "port";
	
	/* costruttore */
	public ShellDialogPHPito(ShellPHPito shellPHPito, int style) {
		super(shellPHPito, style | SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		this.shellPHPito = shellPHPito;
	}

	/* costruttore senza style */
	public ShellDialogPHPito(ShellPHPito shellPHPito) {
		super(shellPHPito, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		this.shellPHPito = shellPHPito;
	}

	@Override
	/* override del metodo check - per evitare il controllo della subclass */
	protected void checkSubclass() {
	}

	/* get */
	public ShellPHPito getShellPHPito() {
		return shellPHPito;
	}
	
	/* metodo che ritorna key per aree di testo di aggiungi progetto */
	public String[] getArrayKeyAddProject() {
		return new String[]{K_NAME, K_PATH, K_ADDRESS, K_PORT};
	}

	/* metodo che controlla che le aree di testo passate con hashmap siano vuote */
	public boolean isTextsEmpty(HashMap<String, Text> textMap) {
		for (String key : textMap.keySet())
			if (!textMap.get(key).getText().isEmpty())
				return false;
		return true;
	}

	/* metodo che svuota le aree di testo passate sull'hashmap */
	public void emptyingText(HashMap<String, Text> textMap) {
		for (String key : textMap.keySet())
			textMap.get(key).setText("");
	}
}
