package phpito.view.shell.dialog;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import phpito.data.Project;
import phpito.view.shell.ShellPHPito;


public class ShellDialogPHPito extends Shell {
	protected ShellPHPito shellPHPito;
	private HashMap<String, Text> textMap;
	private Button logActvChckBttn;
	private Combo phpiniCombo;
	
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

	/* get e set */
	public ShellPHPito getShellPHPito() {
		return shellPHPito;
	}
	public void setShellPHPito(ShellPHPito shellPHPito) {
		this.shellPHPito = shellPHPito;
	}
	public HashMap<String, Text> getTextMap() {
		return textMap;
	}
	public void setTextMap(HashMap<String, Text> textMap) {
		this.textMap = textMap;
	}
	public Button getLogActvChckBttn() {
		return logActvChckBttn;
	}
	public void setLogActvChckBttn(Button chckBttnLogActv) {
		this.logActvChckBttn = chckBttnLogActv;
	}
	public Combo getPhpiniCombo() {
		return phpiniCombo;
	}

	public void setPhpiniCombo(Combo phpiniCombo) {
		this.phpiniCombo = phpiniCombo;
	}

	/* metodo che controlla che le aree di testo passate con hashmap siano vuote */
	public boolean isTextsEmpty() {
		for (String key : textMap.keySet())
			if (!textMap.get(key).getText().isEmpty())
				return false;
		return true;
	}

	/* metodo che svuota le aree di testo passate sull'hashmap */
	public void emptyingText() {
		for (String key : textMap.keySet())
			textMap.get(key).setText("");
	}
	
	public void setTextByProject(Project project) {
		HashMap<String, String> mapProject = project.getHashMap();
		for (String kText : textMap.keySet())
			textMap.get(kText).setText(mapProject.get(kText));
	}
}
