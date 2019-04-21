package it.phpito.view.listener.selection;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

import it.as.utils.view.UtilsViewAS;
import it.phpito.data.Project;
import it.phpito.view.shell.ShellDialogPHPito;
import it.phpito.view.shell.ShellPHPito;

public class LuncherModifyProjectSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito;

	public LuncherModifyProjectSelectionAdapter(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}
	
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		ShellDialogPHPito shellDialog = new ShellDialogPHPito(shellPHPito);
		lunchModifyProject(shellDialog);
	}



	@Override
	public void widgetSelected(SelectionEvent e) {
		ShellDialogPHPito shellDialog = new ShellDialogPHPito(shellPHPito);
		lunchModifyProject(shellDialog);
	}
	
	/* metodo per lanciare finestra che aggiunge il progetto */
	public void lunchModifyProject(ShellDialogPHPito shellDialog) {
		shellDialog.setSize(370, 330);
		shellDialog.setText("Modifica Progetto");
		UtilsViewAS.getInstance().centerWindow(shellDialog);
		
		/* ciclo per label */
		String[] txtLbl = {"Nome:", "Path:", "Indirizzo:", "Porta:"};
		UtilsViewAS.getInstance().printLabelVertical(txtLbl, 30, 17, 60, shellPHPito.getFontHeight(), 50, shellDialog);
		
		/* ciclo per text */
		String[] keyList = Project.getArrayKeyProject();
		shellDialog.setTextMap(new HashMap<String, Text>());
		UtilsViewAS.getInstance().printTextVertical(110, 15, 150, shellPHPito.getFontHeight(), 50, shellDialog, keyList, shellDialog.getTextMap());
		
		/* add listener ad aree di testo */
		SelectionAdapter[] selAdptList = new SelectionAdapter[] {
				new TextFocusSelectionAdapter(shellDialog.getTextMap().get(Project.K_PATH)),
				new TextFocusSelectionAdapter(shellDialog.getTextMap().get(Project.K_ADDRESS)),
				new TextFocusSelectionAdapter(shellDialog.getTextMap().get(Project.K_PORT)),
				new UpdateProjectSelctionAdapter(shellDialog)
		};
		HashMap<String, String> mapProject = shellPHPito.getProjectSeclect().getHashMap();
		for (int i = 0; i < keyList.length; i++) {
			shellDialog.getTextMap().get(keyList[i]).addSelectionListener(selAdptList[i]);
			shellDialog.getTextMap().get(keyList[i]).setText(mapProject.get(keyList[i]));
		}
		
		selAdptList = new SelectionAdapter[] {
				new CancelTextSelectionAdapter(shellDialog),
				new UpdateProjectSelctionAdapter(shellDialog),
		};
		String[] namesButton = new String[] {"Annulla", "Salva"};
		
		Button bttn = new Button(shellDialog, SWT.PUSH);
		bttn.addSelectionListener(new LuncherSelectPathSelectionAdapter(shellDialog));
		bttn.setBounds(270, 83, 80, 30);
		bttn.setText("Scegli");

		UtilsViewAS.getInstance().printButtonHorizontal(namesButton, 130, 250, 100, 30, 20, shellDialog, selAdptList);
		
		shellDialog.open();
	}
}
