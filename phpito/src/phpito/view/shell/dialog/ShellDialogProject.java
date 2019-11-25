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
import phpito.core.PHPitoManager;
import phpito.data.Project;
import phpito.data.Server;
import phpito.exception.ProjectException;
import phpito.view.listener.selection.launcher.LauncherEditorPhpiniSelectionAdapter;
import phpito.view.listener.selection.launcher.LauncherEnvironmentVarsSelectionAdapter;
import phpito.view.listener.selection.project.AddProjectSelectionAdapter;
import phpito.view.listener.selection.project.UpdateProjectSelctionAdapter;
import phpito.view.listener.selection.text.ResetTextSelectionAdapter;
import phpito.view.listener.selection.text.TextFocusSelectionAdapter;
import phpito.view.shell.ShellPHPito;

public class ShellDialogProject extends ShellDialogPHPitoAbstract {
	private HashMap<String, Text> textMap;
	private HashMap<String, String> envVars;
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

	/* ################################################################################# */
	/* START OVERRIDE */
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
				envVars = new HashMap<String, String>();
				actionSlctnAdptr = new AddProjectSelectionAdapter(this);
				phpiniSlctnAdptr = new SelectionAdapter() {};
				break;
			case UPDATE:
				title = "Edit Project";
				project = shellPHPito.getProjectSelect();
				envVars = project.getServer().getEnvironmentVariables();
				actionSlctnAdptr = new UpdateProjectSelctionAdapter(this);
				phpiniSlctnAdptr = new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						bttnEditor.setEnabled(phpiniCombo.getSelectionIndex() == 2);
					}
				};
				break;
			default:
				// TODO exception
				break;
		}
		
		this.setSize(370, 470);
		this.setText(title);
		Jaswt.getInstance().centerWindow(this);
		textMap = new HashMap<String, Text>();
		
		/* ciclo per label */
		String[] txtLbl = {"Id:", "Name:", "Path:", "Address:", "Port:", "php.ini"};
		Jaswt.getInstance().printLabelVertical(txtLbl, 20, 30, 70, 30, 20, this, SWT.NONE);
		
		/* ciclo per text */
		String[] keyList = Project.getArrayKeyProject();
		int[] width = {70, 160, 160, 160, 160};
		Jaswt.getInstance().printTextVertical(100, 30, width, 30, 20, this, keyList, textMap, new int[] {0});

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
		
		Button bttn;
		
		/* button to select path*/
		bttn = new Button(this, SWT.PUSH);
		bttn.addSelectionListener(new LauncherSelectPathSelectionAdapter(this, textMap.get(Project.K_PATH)));
		bttn.setBounds(270, 130, 80, 30);
		bttn.setText("Select");
		bttn.setCursor(new Cursor(shellPHPito.getDisplay(), SWT.CURSOR_HAND));

		/* add combo for select phpini file */ 
		String[] phpiniList = {"Development", "Default", "Custom"};
		phpiniCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		phpiniCombo.setBounds(100, 280, 130, 20);
		for (String ini : phpiniList) phpiniCombo.add(ini);
		phpiniCombo.select(project.getPhpini());
		phpiniCombo.addSelectionListener(phpiniSlctnAdptr);
		
		/* button to open the text editor for phpini */
		bttnEditor = new Button(this, SWT.PUSH);
		bttnEditor.addSelectionListener(new LauncherEditorPhpiniSelectionAdapter(this, project));
		bttnEditor.setBounds(240, 280, 100, 30);
		bttnEditor.setText("Open Editor");
		bttnEditor.setEnabled(project.getPhpini() == 2);

		/* check button to enable log */
		logActvChckBttn =new Button(this, SWT.CHECK);
		logActvChckBttn.setBounds(20, 345, 100, 20);
		logActvChckBttn.setText("Enable Log");
		logActvChckBttn.setSelection(project.isLogActive());

		/* button to show environment variables */
		bttn = new Button(this, SWT.PUSH);
		bttn.setBounds(200, 340, 140, 30);
		bttn.setText("Environment Vars");
		bttn.addSelectionListener(new LauncherEnvironmentVarsSelectionAdapter(this, project));

		/* loop for button save and cancel */
		selAdptList = new SelectionAdapter[] {
			new ResetTextSelectionAdapter(this, project),
			actionSlctnAdptr
		};
		String[] namesButton = new String[] {"Cancel", "Save"};
		Jaswt.getInstance().printButtonHorizontal(namesButton, 130, 400, 100, 30, 20, this, selAdptList);
	}

	/* ################################################################################# */
	/* START OVERRIDE */
	/* ################################################################################# */

	/* ################################################################################# */
	/* START GET SET */
	/* ################################################################################# */

	/* GET */
	public HashMap<String, Text> getTextMap() {
		return textMap;
	}
	public Button getLogActvChckBttn() {
		return logActvChckBttn;
	}
	public Combo getPhpiniCombo() {
		return phpiniCombo;
	}

	/* GET SET */
	public HashMap<String, String> getEnvVars() {
		return envVars;
	}
	public void setEnvVars(HashMap<String, String> envVars) {
		this.envVars = envVars;
	}
	
	/* ################################################################################# */
	/* END GET SET */
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

	/* method that get a project by text areas  on shell */
	public Project getProject() {
		Project project = new Project();
		try {
			long id = Long.valueOf(textMap.get(Project.K_ID).getText());
			if (id != 0l) project.setId(id);
			project.setName(textMap.get(Project.K_NAME).getText());
			project.setLogActive(logActvChckBttn.getSelection());
			project.setPhpini(phpiniCombo.getSelectionIndex());
			project.setServer(new Server());
			project.getServer().setPath(textMap.get(Project.K_PATH).getText());
			project.getServer().setAddress(textMap.get(Project.K_ADDRESS).getText());
			project.getServer().setPortString(textMap.get(Project.K_PORT).getText());
			project.getServer().setEnvironmentVariables(getEnvVars());
		} catch (ProjectException e) {
			Jaswt.getInstance().launchMBError(shellDialogPHPito, e, PHPitoManager.getInstance().getJoggerError());
		}
		return project;
	}

	/* ################################################################################# */
	/* END PUBLIC METHODS */
	/* ################################################################################# */
}
