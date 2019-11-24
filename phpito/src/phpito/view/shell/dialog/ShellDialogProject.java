package phpito.view.shell.dialog;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

import jaswt.core.Jaswt;
import jaswt.listener.selection.LauncherSelectPathSelectionAdapter;
import phpito.data.Project;
import phpito.view.listener.selection.launcher.LauncherEditorPhpiniSelectionAdapter;
import phpito.view.listener.selection.launcher.LauncherEnvironmentVarsSelectionAdapter;
import phpito.view.listener.selection.project.AddProjectSelectionAdapter;
import phpito.view.listener.selection.project.UpdateProjectSelctionAdapter;
import phpito.view.listener.selection.text.ResetTextSelectionAdapter;
import phpito.view.listener.selection.text.TextFocusSelectionAdapter;
import phpito.view.shell.ShellPHPito;

public class ShellDialogProject extends ShellDialogPHPito {
	private HashMap<String, Text> textMap;
	private Button logActvChckBttn;
	private Button bttnEditor;
	private Combo phpiniCombo;
	private int type;
	public static final int UPDATE = 0;
	public static final int NEW = 1;
	

	/* ################################################################################# */
	/* START CONSTRUCTORS */
	/* ################################################################################# */

	public ShellDialogProject(ShellPHPito shellPHPito, int type) {
		super(shellPHPito);
		this.type = type;
	}

	/* ################################################################################# */
	/* END CONSTRUCTORS */
	/* ################################################################################# */

	/* override for create contents */
	@Override
	protected void createContents() {
		/* preset by type */
		String title = null;
		Project project = null;
		SelectionAdapter actionSlctnAdptr = null;
		SelectionAdapter phpiniSlctnAdptr = null;
		switch (type) {
			case NEW:
				title = "New Project";
				project = Project.getDefaultProject();
				actionSlctnAdptr = new AddProjectSelectionAdapter(this);
				phpiniSlctnAdptr = new SelectionAdapter() {};
				break;
			case UPDATE:
				title = "Edit Project";
				project = shellPHPito.getProjectSelect();
				actionSlctnAdptr = new UpdateProjectSelctionAdapter(this);
				phpiniSlctnAdptr = new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						bttnEditor.setEnabled(phpiniCombo.getSelectionIndex() == 2);
					}
				};
				break;
			default:
				break;
		}
		
		this.setSize(370, 430);
		this.setText(title);
		Jaswt.getInstance().centerWindow(this);
		textMap = new HashMap<String, Text>();
		
		/* ciclo per label */
		String[] txtLbl = {"Id:", "Name:", "Path:", "Address:", "Port:", "php.ini"};
		Jaswt.getInstance().printLabelVertical(txtLbl, 20, 30, 70, shellPHPito.getFontHeight(), 20, this, SWT.NONE);
		
		/* ciclo per text */
		String[] keyList = Project.getArrayKeyProject();
		int[] width = {70, 160, 160, 160, 160};
		Jaswt.getInstance().printTextVertical(100, 30, width, shellPHPito.getFontHeight(), 20, this, keyList, textMap, new int[] {0});

		/* set aree di testo e aggiunta listener */
		SelectionAdapter[] selAdptList = new SelectionAdapter[] {
			null,
			new TextFocusSelectionAdapter(textMap.get(Project.K_PATH)),
			new TextFocusSelectionAdapter(textMap.get(Project.K_ADDRESS)),
			new TextFocusSelectionAdapter(textMap.get(Project.K_PORT)),
			actionSlctnAdptr
		};
		HashMap<String, String> mapProject = project.getHashMap();
		for (int i = 0, length = keyList.length; i < length; i++) {
			if (selAdptList[i] != null) textMap.get(keyList[i]).addSelectionListener(selAdptList[i]);
			textMap.get(keyList[i]).setText(mapProject.get(keyList[i]));
		}

		/* add combo for select phpini file */ 
		String[] phpiniList = {"Development", "Default", "Custom"};
		phpiniCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		phpiniCombo.setBounds(100, 230, 130, 20);
		for (String ini : phpiniList) phpiniCombo.add(ini);
		phpiniCombo.select(project.getPhpini());
		phpiniCombo.addSelectionListener(phpiniSlctnAdptr);

		/* check button to enable log */
		logActvChckBttn =new Button(this, SWT.CHECK);
		logActvChckBttn.setBounds(20, 290, 100, 20);
		logActvChckBttn.setText("Enable Log");
		logActvChckBttn.setSelection(project.isLogActive());
		
		Button bttn;

		/* button to select path*/
		bttn = new Button(this, SWT.PUSH);
		bttn.addSelectionListener(new LauncherSelectPathSelectionAdapter(this, textMap.get(Project.K_PATH)));
		bttn.setBounds(270, 107, 80, 30);
		bttn.setText("Select");
		bttn.setCursor(new Cursor(shellPHPito.getDisplay(), SWT.CURSOR_HAND));

		/* button to open the text editor for phpini */
		bttnEditor = new Button(this, SWT.PUSH);
		bttnEditor.addSelectionListener(new LauncherEditorPhpiniSelectionAdapter(this, project));
		bttnEditor.setBounds(240, 230, 100, 30);
		bttnEditor.setText("Open Editor");
		bttnEditor.setEnabled(project.getPhpini() == 2);

		/* button to show environment variables */
		bttn = new Button(this, SWT.PUSH);
		bttn.setBounds(200, 285, 140, 30);
		bttn.setText("Environment Vars");
		bttn.addSelectionListener(new LauncherEnvironmentVarsSelectionAdapter(this, project));

		/* loop for button save and cancel */
		selAdptList = new SelectionAdapter[] {
			new ResetTextSelectionAdapter(this, project),
			actionSlctnAdptr
		};
		String[] namesButton = new String[] {"Cancel", "Save"};
		Jaswt.getInstance().printButtonHorizontal(namesButton, 130, 350, 100, 30, 20, this, selAdptList);
	}

	/* ################################################################################# */
	/* START GET*/
	/* ################################################################################# */

	public HashMap<String, Text> getTextMap() {
		return textMap;
	}
	public Button getLogActvChckBttn() {
		return logActvChckBttn;
	}
	public Combo getPhpiniCombo() {
		return phpiniCombo;
	}

	/* ################################################################################# */
	/* END GET*/
	/* ################################################################################# */

	/* ################################################################################# */
	/* START PUBLIC METHODS */
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

	/* ################################################################################# */
	/* END PUBLIC METHODS */
	/* ################################################################################# */
}
