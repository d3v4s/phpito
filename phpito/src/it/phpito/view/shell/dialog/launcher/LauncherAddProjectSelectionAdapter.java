package it.phpito.view.shell.dialog.launcher;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import it.phpito.view.shell.ShellPHPito;
import it.phpito.view.shell.dialog.ShellDialogPHPito;

public class LauncherAddProjectSelectionAdapter extends ShellDialogPHPito implements SelectionListener {

	public LauncherAddProjectSelectionAdapter(ShellPHPito shellPHPito) {
		super(shellPHPito);
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		launchAddProject();
	}
	
	/* metodo per lanciare finestra che aggiunge il progetto */
	private void launchAddProject() {
		this.setSize(370, 350);
		this.setText("Nuovo Progetto");
		Jaswt.getInstance().centerWindow(this);
		this.setTextMap(new 	HashMap<String, Text>());

		/* ciclo per label */
		String[] txtLbl = {"Nome:", "Path:", "Indirizzo:", "Porta:" , "php.ini"};
		Jaswt.getInstance().printLabelVertical(txtLbl, 20, 30, 70, shellPHPito.getFontHeight(), 20, this, SWT.NONE);

		/* ciclo per text */
		String[] keyList = Project.getArrayKeyProjectNoId();
		int[] width = {160, 160, 160, 160};
		Jaswt.getInstance().printTextVertical(100, 30, width, shellPHPito.getFontHeight(), 20, this, keyList, this.getTextMap(), new int[] {});

		/* add listener ad aree di testo */
		SelectionAdapter[] selAdptList = new SelectionAdapter[] {
				new TextFocusSelectionAdapter(this.getTextMap().get(Project.K_PATH)),
				new TextFocusSelectionAdapter(this.getTextMap().get(Project.K_ADDRESS)),
				new TextFocusSelectionAdapter(this.getTextMap().get(Project.K_PORT)),
				new AddProjectSelectionAdapter(this)
		};
		for (int i = 0; i < keyList.length; i++)
			this.getTextMap().get(keyList[i]).addSelectionListener(selAdptList[i]);
		this.getTextMap().get(Project.K_ADDRESS).setText("127.0.0.1");

		String[] phpiniList = {"Development", "Default", "Custom"};
		this.setPhpiniCombo(new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY));
		this.getPhpiniCombo().setBounds(100, 190, 130, 20);
		for (String ini : phpiniList)
			this.getPhpiniCombo().add(ini);
		this.getPhpiniCombo().select(0);

		this.setLogActvChckBttn(new Button(this, SWT.CHECK));
		this.getLogActvChckBttn().setBounds(20, 240, 100, 20);
		this.getLogActvChckBttn().setText("Attiva Log");
		this.getLogActvChckBttn().setSelection(true);

		Button bttn = new Button(this, SWT.PUSH);
		bttn.addSelectionListener(new LuncherSelectPathSelectionAdapter(this, this.getTextMap().get(Project.K_PATH)));
		bttn.setBounds(270, 67, 80, 30);
		bttn.setText("Scegli");
		bttn.setCursor(new Cursor(shellPHPito.getDisplay(), SWT.CURSOR_HAND));
		
		selAdptList = new SelectionAdapter[] {
				new CancelTextSelectionAdapter(this),
				new AddProjectSelectionAdapter(this),
		};
		String[] namesButton = new String[] {"Annulla", "Aggiungi"};
		Jaswt.getInstance().printButtonHorizontal(namesButton, 130, 280, 100, 30, 20, this, selAdptList);

		this.open();
	}
}
