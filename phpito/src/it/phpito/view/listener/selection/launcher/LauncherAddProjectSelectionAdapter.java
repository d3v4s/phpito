package it.phpito.view.listener.selection.launcher;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

import it.jaswt.core.Jaswt;
import it.jaswt.core.listener.selection.LuncherSelectPathSelectionAdapter;
import it.phpito.data.Project;
import it.phpito.view.listener.selection.project.AddProjectSelectionAdapter;
import it.phpito.view.listener.selection.text.CancelTextSelectionAdapter;
import it.phpito.view.listener.selection.text.TextFocusSelectionAdapter;
import it.phpito.view.shell.ShellDialogPHPito;
import it.phpito.view.shell.ShellPHPito;

public class LauncherAddProjectSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito;

	public LauncherAddProjectSelectionAdapter(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		ShellDialogPHPito shellDialog = new ShellDialogPHPito(shellPHPito);
		launchAddProject(shellDialog);
	}
	
	/* metodo per lanciare finestra che aggiunge il progetto */
	private void launchAddProject(ShellDialogPHPito shellDialog) {
		shellDialog.setSize(370, 350);
		shellDialog.setText("Nuovo Progetto");
		Jaswt.getInstance().centerWindow(shellDialog);
		shellDialog.setTextMap(new 	HashMap<String, Text>());

		/* ciclo per label */
		String[] txtLbl = {"Nome:", "Path:", "Indirizzo:", "Porta:" , "php.ini"};
		Jaswt.getInstance().printLabelVertical(txtLbl, 20, 30, 70, shellPHPito.getFontHeight(), 20, shellDialog, SWT.NONE);

		/* ciclo per text */
		String[] keyList = Project.getArrayKeyProjectNoId();
		int[] width = {160, 160, 160, 160};
		Jaswt.getInstance().printTextVertical(100, 30, width, shellPHPito.getFontHeight(), 20, shellDialog, keyList, shellDialog.getTextMap(), new int[] {});

		/* add listener ad aree di testo */
		SelectionAdapter[] selAdptList = new SelectionAdapter[] {
				new TextFocusSelectionAdapter(shellDialog.getTextMap().get(Project.K_PATH)),
				new TextFocusSelectionAdapter(shellDialog.getTextMap().get(Project.K_ADDRESS)),
				new TextFocusSelectionAdapter(shellDialog.getTextMap().get(Project.K_PORT)),
				new AddProjectSelectionAdapter(shellDialog)
		};
		for (int i = 0; i < keyList.length; i++)
			shellDialog.getTextMap().get(keyList[i]).addSelectionListener(selAdptList[i]);
		shellDialog.getTextMap().get(Project.K_ADDRESS).setText("127.0.0.1");

		String[] phpiniList = {"Development", "Default", "Custom"};
		shellDialog.setPhpiniCombo(new Combo(shellDialog, SWT.DROP_DOWN | SWT.READ_ONLY));
		shellDialog.getPhpiniCombo().setBounds(100, 190, 130, 20);
		for (String ini : phpiniList)
			shellDialog.getPhpiniCombo().add(ini);
		shellDialog.getPhpiniCombo().select(0);

		shellDialog.setLogActvChckBttn(new Button(shellDialog, SWT.CHECK));
		shellDialog.getLogActvChckBttn().setBounds(20, 240, 100, 20);
		shellDialog.getLogActvChckBttn().setText("Attiva Log");
		shellDialog.getLogActvChckBttn().setSelection(true);

		Button bttn = new Button(shellDialog, SWT.PUSH);
		bttn.addSelectionListener(new LuncherSelectPathSelectionAdapter(shellDialog, shellDialog.getTextMap().get(Project.K_PATH)));
		bttn.setBounds(270, 67, 80, 30);
		bttn.setText("Scegli");
		bttn.setCursor(new Cursor(shellPHPito.getDisplay(), SWT.CURSOR_HAND));
		
		selAdptList = new SelectionAdapter[] {
				new CancelTextSelectionAdapter(shellDialog),
				new AddProjectSelectionAdapter(shellDialog),
		};
		String[] namesButton = new String[] {"Annulla", "Aggiungi"};
		Jaswt.getInstance().printButtonHorizontal(namesButton, 130, 280, 100, 30, 20, shellDialog, selAdptList);

		shellDialog.open();
	}

}
