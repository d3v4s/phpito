package phpito.view.shell.dialog;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import phpito.data.Project;
import phpito.view.shell.ShellPHPito;

/**
 * Class Shell for PHPito dialog shell
 * @author Andrea Serra
 *
 */
public class ShellDialogPHPito extends Shell {
	protected ShellPHPito shellPHPito;
	private HashMap<String, Text> textMap;
	private Button logActvChckBttn;
	private Combo phpiniCombo;

	/* override del metodo check - per evitare il controllo della subclass */
	/* override to bypass check subclass error */
	@Override
	protected void checkSubclass() {
	}
	
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
	/* START GET AND SET */
	/* ################################################################################# */

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

	/* ################################################################################# */
	/* END GET AND SET */
	/* ################################################################################# */

	/* metodo che controlla se le aree di testo passate con hashmap sono vuote */
	public boolean isTextsEmpty() {
		for (String key : textMap.keySet()) if (!textMap.get(key).getText().isEmpty()) return false;
		return true;
	}

	/* metodo che svuota le aree di testo passate sull'hashmap */
	public void emptyingText() {
		for (String key : textMap.keySet()) textMap.get(key).setText("");
	}

	/* method that set text area by project */
	public void setTextByProject(Project project) {
		HashMap<String, String> mapProject = project.getHashMap();
		for (String kText : textMap.keySet()) textMap.get(kText).setText(mapProject.get(kText));
	}
}
