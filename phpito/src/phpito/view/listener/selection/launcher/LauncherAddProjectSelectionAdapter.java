package phpito.view.listener.selection.launcher;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

import jaswt.core.Jaswt;
import jaswt.listener.selection.LauncherSelectPathSelectionAdapter;
import phpito.data.Project;
import phpito.view.listener.selection.project.AddProjectSelectionAdapter;
import phpito.view.listener.selection.text.CancelTextSelectionAdapter;
import phpito.view.listener.selection.text.TextFocusSelectionAdapter;
import phpito.view.shell.ShellPHPito;
import phpito.view.shell.dialog.ShellDialogPHPito;

/**
 * Class SelectionAdapter to launch the window for add new project 
 * @author Andrea Serra
 *
 */
public class LauncherAddProjectSelectionAdapter implements SelectionListener {
	private ShellPHPito shellPHPito;

	/* CONSTRUCT */
	public LauncherAddProjectSelectionAdapter(ShellPHPito shellPHPito) {
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent e) {
		launchAddProject();
	}
	
	/* metodo per lanciare finestra che aggiunge il progetto */
	public void launchAddProject() {
		ShellDialogPHPito shellDialogPHPito = new ShellDialogPHPito(shellPHPito);
		shellDialogPHPito.setSize(370, 350);
		shellDialogPHPito.setText("New Project");
		Jaswt.getInstance().centerWindow(shellDialogPHPito);
		shellDialogPHPito.setTextMap(new HashMap<String, Text>());

		/* ciclo per label */
		String[] txtLbl = {"Name:", "Path:", "Address:", "Port:" , "php.ini"};
		Jaswt.getInstance().printLabelVertical(txtLbl, 20, 30, 70, shellPHPito.getFontHeight(), 20, shellDialogPHPito, SWT.NONE);

		/* ciclo per text */
		String[] keyList = Project.getArrayKeyProjectNoId();
		int[] width = {160, 160, 160, 160};
		Jaswt.getInstance().printTextVertical(100, 30, width, shellPHPito.getFontHeight(), 20, shellDialogPHPito, keyList, shellDialogPHPito.getTextMap(), new int[] {});

		/* add listener ad aree di testo */
		SelectionAdapter[] selAdptList = new SelectionAdapter[] {
				new TextFocusSelectionAdapter(shellDialogPHPito.getTextMap().get(Project.K_PATH)),
				new TextFocusSelectionAdapter(shellDialogPHPito.getTextMap().get(Project.K_ADDRESS)),
				new TextFocusSelectionAdapter(shellDialogPHPito.getTextMap().get(Project.K_PORT)),
				new AddProjectSelectionAdapter(shellDialogPHPito)
		};
		for (int i = 0; i < keyList.length; i++) shellDialogPHPito.getTextMap().get(keyList[i]).addSelectionListener(selAdptList[i]);
		shellDialogPHPito.getTextMap().get(Project.K_ADDRESS).setText("127.0.0.1");

		/* add combo for select a phpini */
		String[] phpiniList = {"Development", "Default", "Custom"};
		shellDialogPHPito.setPhpiniCombo(new Combo(shellDialogPHPito, SWT.DROP_DOWN | SWT.READ_ONLY));
		shellDialogPHPito.getPhpiniCombo().setBounds(100, 190, 130, 20);
		for (String ini : phpiniList) shellDialogPHPito.getPhpiniCombo().add(ini);
		shellDialogPHPito.getPhpiniCombo().select(0);

		/* check button active log */
		shellDialogPHPito.setLogActvChckBttn(new Button(shellDialogPHPito, SWT.CHECK));
		shellDialogPHPito.getLogActvChckBttn().setBounds(20, 240, 100, 20);
		shellDialogPHPito.getLogActvChckBttn().setText("Enable Log");
		shellDialogPHPito.getLogActvChckBttn().setSelection(true);

		/* button to select path */
		Button bttn = new Button(shellDialogPHPito, SWT.PUSH);
		bttn.addSelectionListener(new LauncherSelectPathSelectionAdapter(shellDialogPHPito, shellDialogPHPito.getTextMap().get(Project.K_PATH)));
		bttn.setBounds(270, 67, 80, 30);
		bttn.setText("Select");
		bttn.setCursor(new Cursor(shellPHPito.getDisplay(), SWT.CURSOR_HAND));

		/* loop for buttons cancel and save */
		selAdptList = new SelectionAdapter[] {
				new CancelTextSelectionAdapter(shellDialogPHPito),
				new AddProjectSelectionAdapter(shellDialogPHPito),
		};
		String[] namesButton = new String[] {"Cancel", "Save"};
		Jaswt.getInstance().printButtonHorizontal(namesButton, 130, 280, 100, 30, 20, shellDialogPHPito, selAdptList);

		shellDialogPHPito.open();
	}
}
