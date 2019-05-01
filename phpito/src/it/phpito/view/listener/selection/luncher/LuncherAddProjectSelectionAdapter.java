package it.phpito.view.listener.selection.luncher;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

import it.as.utils.view.UtilsViewAS;
import it.as.utils.view.listener.selection.LuncherSelectPathSelectionAdapter;
import it.phpito.data.Project;
import it.phpito.view.listener.selection.project.AddProjectSelectionAdapter;
import it.phpito.view.listener.selection.text.CancelTextSelectionAdapter;
import it.phpito.view.listener.selection.text.TextFocusSelectionAdapter;
import it.phpito.view.shell.ShellDialogPHPito;
import it.phpito.view.shell.ShellPHPito;

public class LuncherAddProjectSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito;

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
		shellDialog.setSize(370, 330);
		shellDialog.setText("Nuovo Progetto");
		UtilsViewAS.getInstance().centerWindow(shellDialog);
		shellDialog.setTextMap(new 	HashMap<String, Text>());

		/* ciclo per label */
		String[] txtLbl = {"Nome:", "Path:", "Indirizzo:", "Porta:"};
		UtilsViewAS.getInstance().printLabelVertical(txtLbl, 30, 30, 60, shellPHPito.getFontHeight(), 30, shellDialog, SWT.NONE);

		/* ciclo per text */
		String[] keyList = Project.getArrayKeyProjectNoId();
		int[] width = {150, 150, 150, 150};
		UtilsViewAS.getInstance().printTextVertical(110, 28, width, shellPHPito.getFontHeight(), 30, shellDialog, keyList, shellDialog.getTextMap(), new int[] {});

		/* add listener ad aree di testo */
		SelectionAdapter[] selAdptList = new SelectionAdapter[] {
				new TextFocusSelectionAdapter(shellDialog.getTextMap().get(Project.K_PATH)),
				new TextFocusSelectionAdapter(shellDialog.getTextMap().get(Project.K_ADDRESS)),
				new TextFocusSelectionAdapter(shellDialog.getTextMap().get(Project.K_PORT)),
				new AddProjectSelectionAdapter(shellDialog)
		};
		for (int i = 0; i < keyList.length; i++)
			shellDialog.getTextMap().get(keyList[i]).addSelectionListener(selAdptList[i]);

		selAdptList = new SelectionAdapter[] {
				new CancelTextSelectionAdapter(shellDialog),
				new AddProjectSelectionAdapter(shellDialog),
		};
		String[] namesButton = new String[] {"Annulla", "Aggiungi"};

		Button bttn = new Button(shellDialog, SWT.PUSH);
		bttn.addSelectionListener(new LuncherSelectPathSelectionAdapter(shellDialog, shellDialog.getTextMap().get(Project.K_PATH)));
		bttn.setBounds(270, 75, 80, 30);
		bttn.setText("Scegli");
		bttn.setCursor(new Cursor(shellPHPito.getDisplay(), SWT.CURSOR_HAND));

		UtilsViewAS.getInstance().printButtonHorizontal(namesButton, 130, 250, 100, 30, 20, shellDialog, selAdptList);

		shellDialog.open();
	}

}
