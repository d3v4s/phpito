package it.phpito.view.listener.selection.launcher;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

import it.jaswt.core.Jaswt;
import it.jaswt.core.listener.selection.LuncherSelectPathSelectionAdapter;
import it.phpito.data.Project;
import it.phpito.view.listener.selection.project.UpdateProjectSelctionAdapter;
import it.phpito.view.listener.selection.text.ResetTextSelectionAdapter;
import it.phpito.view.listener.selection.text.TextFocusSelectionAdapter;
import it.phpito.view.shell.ShellDialogPHPito;
import it.phpito.view.shell.ShellPHPito;

public class LauncherModifyProjectSelectionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito;
	private Project project;

	public LauncherModifyProjectSelectionAdapter(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		launchModifyProject();
	}
	
	/* metodo per lanciare finestra che aggiunge il progetto */
	public void launchModifyProject() {
		ShellDialogPHPito shellDialog = new ShellDialogPHPito(shellPHPito);
		project = shellPHPito.getProjectSelect();
		shellDialog.setSize(370, 360);
		shellDialog.setText("Modifica Progetto");
		Jaswt.getInstance().centerWindow(shellDialog);
		shellDialog.setTextMap(new HashMap<String, Text>());
		
		/* ciclo per label */
		String[] txtLbl = {"Id:", "Nome:", "Path:", "Indirizzo:", "Porta:"};
		Jaswt.getInstance().printLabelVertical(txtLbl, 20, 30, 65, shellPHPito.getFontHeight(), 20, shellDialog, SWT.NONE);
		
		/* ciclo per text */
		String[] keyList = Project.getArrayKeyProject();
		int[] width = {70, 160, 160, 160, 160};
		Jaswt.getInstance().printTextVertical(100, 30, width, shellPHPito.getFontHeight(), 20, shellDialog, keyList, shellDialog.getTextMap(), new int[] {0});

		/* set aree di testo e aggiunta listener */
		SelectionAdapter[] selAdptList = new SelectionAdapter[] {
				null,
				new TextFocusSelectionAdapter(shellDialog.getTextMap().get(Project.K_PATH)),
				new TextFocusSelectionAdapter(shellDialog.getTextMap().get(Project.K_ADDRESS)),
				new TextFocusSelectionAdapter(shellDialog.getTextMap().get(Project.K_PORT)),
				new UpdateProjectSelctionAdapter(shellDialog)
		};
		HashMap<String, String> mapProject = project.getHashMap();
		for (int i = 0; i < keyList.length; i++) {
			if (selAdptList[i] != null)
				shellDialog.getTextMap().get(keyList[i]).addSelectionListener(selAdptList[i]);
			shellDialog.getTextMap().get(keyList[i]).setText(mapProject.get(keyList[i]));
		}

		shellDialog.setChckBttnLogActv(new Button(shellDialog, SWT.CHECK));
		shellDialog.getChckBttnLogActv().setBounds(20, 240, 100, 20);
		shellDialog.getChckBttnLogActv().setText("Attiva Log");
		shellDialog.getChckBttnLogActv().setSelection(project.isLogActive());
		
		Button bttn = new Button(shellDialog, SWT.PUSH);
		bttn.addSelectionListener(new LuncherSelectPathSelectionAdapter(shellDialog, shellDialog.getTextMap().get(Project.K_PATH)));
		bttn.setBounds(270, 107, 80, 30);
		bttn.setText("Scegli");
		
		selAdptList = new SelectionAdapter[] {
				new ResetTextSelectionAdapter(shellDialog),
				new UpdateProjectSelctionAdapter(shellDialog),
		};
		String[] namesButton = new String[] {"Annulla", "Salva"};
		Jaswt.getInstance().printButtonHorizontal(namesButton, 130, 280, 100, 30, 20, shellDialog, selAdptList);
		
		shellDialog.open();
	}
}
