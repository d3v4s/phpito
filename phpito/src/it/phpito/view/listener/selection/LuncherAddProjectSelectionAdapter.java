package it.phpito.view.listener.selection;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

import it.as.utils.view.UtilsViewAS;
import it.phpito.view.shell.ShellDialogPHPito;
import it.phpito.view.shell.ShellPHPito;

public class LuncherAddProjectSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito;
	private HashMap<String, Text> textMap;

	public LuncherAddProjectSelectionAdapter(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		ShellDialogPHPito shellDialog = new ShellDialogPHPito(shellPHPito);
		lunchAddProject(shellDialog);
	}
	
	/* metodo per lanciare finestra che aggiunge il progetto */
	private void lunchAddProject(ShellDialogPHPito shellDialog) {
		shellDialog.setSize(300, 350);
		shellDialog.setText("Nuovo Progetto");
		UtilsViewAS.getInstance().centerWindow(shellDialog);
		
		/* ciclo per label */
		String[] txtLbl = {"Nome:", "Path:", "Indirizzo:", "Porta:"};
		UtilsViewAS.getInstance().printLabelVertical(txtLbl, 40, 17, 60, shellPHPito.getFontHeight(), shellDialog);
		
		/* ciclo per text */
		String[] keyList = shellDialog.getArrayKeyAddProject();
		textMap = new HashMap<String, Text>();
		UtilsViewAS.getInstance().printTextVertical(120, 15, 140, shellPHPito.getFontHeight(), shellDialog, keyList, textMap);
		
		/* add listener ad aree di testo */
		SelectionAdapter[] selAdptList = new SelectionAdapter[] {
				new TextFocusSelectionAdapter(textMap.get(ShellDialogPHPito.K_PATH)),
				new TextFocusSelectionAdapter(textMap.get(ShellDialogPHPito.K_ADDRESS)),
				new TextFocusSelectionAdapter(textMap.get(ShellDialogPHPito.K_PORT)),
				new AddProjectSelectionAdapter(shellDialog, textMap)
		};
		for (int i = 0; i < keyList.length; i++)
			textMap.get(keyList[i]).addSelectionListener(selAdptList[i]);
		
		selAdptList = new SelectionAdapter[] {
				new CancelTextSelectionAdapter(shellDialog, textMap),
				new AddProjectSelectionAdapter(shellDialog, textMap),
		};
		String[] namesButton = new String[] {"Annulla", "Aggiungi"};
		
		Button bttn;
		for (int i = 0; i < namesButton.length; i++) {
			bttn = new Button(shellDialog, SWT.NONE);
			bttn.addSelectionListener(selAdptList[i]);
			bttn.setBounds(60 + (120 * i), 250, 100, 30);
			bttn.setText(namesButton[i]);
		}

		
		shellDialog.open();
	}

}
