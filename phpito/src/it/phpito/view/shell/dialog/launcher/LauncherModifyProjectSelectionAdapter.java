package it.phpito.view.shell.dialog.launcher;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

import it.jaswt.core.Jaswt;
import it.jaswt.core.listener.selection.LuncherSelectPathSelectionAdapter;
import it.phpito.data.Project;
import it.phpito.view.listener.selection.project.UpdateProjectSelctionAdapter;
import it.phpito.view.listener.selection.text.ResetTextSelectionAdapter;
import it.phpito.view.listener.selection.text.TextFocusSelectionAdapter;
import it.phpito.view.shell.ShellPHPito;
import it.phpito.view.shell.dialog.ShellDialogPHPito;

public class LauncherModifyProjectSelectionAdapter extends ShellDialogPHPito implements SelectionListener {

	public LauncherModifyProjectSelectionAdapter(ShellPHPito shellPHPito) {
		super(shellPHPito);
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		launchModifyProject();
	}
	
	/* metodo per lanciare finestra che aggiunge il progetto */
	public void launchModifyProject() {
		ShellDialogPHPito shellDialog = new ShellDialogPHPito(shellPHPito);
		Project project = shellPHPito.getProjectSelect();
		shellDialog.setSize(370, 380);
		shellDialog.setText("Modifica Progetto");
		Jaswt.getInstance().centerWindow(shellDialog);
		shellDialog.setTextMap(new HashMap<String, Text>());
		
		/* ciclo per label */
		String[] txtLbl = {"Id:", "Nome:", "Path:", "Indirizzo:", "Porta:", "php.ini"};
		Jaswt.getInstance().printLabelVertical(txtLbl, 20, 30, 70, shellPHPito.getFontHeight(), 20, shellDialog, SWT.NONE);
		
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

		String[] phpiniList = {"Development", "Default", "Custom"};
		shellDialog.setPhpiniCombo(new Combo(shellDialog, SWT.DROP_DOWN | SWT.READ_ONLY));
		shellDialog.getPhpiniCombo().setBounds(100, 230, 130, 20);
		for (String ini : phpiniList)
			shellDialog.getPhpiniCombo().add(ini);
		shellDialog.getPhpiniCombo().select(project.getPhpini());

		shellDialog.setLogActvChckBttn(new Button(shellDialog, SWT.CHECK));
		shellDialog.getLogActvChckBttn().setBounds(20, 280, 100, 20);
		shellDialog.getLogActvChckBttn().setText("Attiva Log");
		shellDialog.getLogActvChckBttn().setSelection(project.isLogActive());
		
		Button bttn;

		bttn = new Button(shellDialog, SWT.PUSH);
		bttn.addSelectionListener(new LuncherSelectPathSelectionAdapter(shellDialog, shellDialog.getTextMap().get(Project.K_PATH)));
		bttn.setBounds(270, 107, 80, 30);
		bttn.setText("Scegli");

		Button bttnEditor = new Button(shellDialog, SWT.PUSH);
		bttnEditor.addSelectionListener(new LauncherEditorPhpini(shellDialog, project));
		bttnEditor.setBounds(240, 230, 80, 30);
		bttnEditor.setText("Apri Editor");
		bttnEditor.setEnabled(project.getPhpini() == 2);
		
		shellDialog.getPhpiniCombo().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				bttnEditor.setEnabled(shellDialog.getPhpiniCombo().getSelectionIndex() == 2);
			}
		});

		selAdptList = new SelectionAdapter[] {
				new ResetTextSelectionAdapter(shellDialog),
				new UpdateProjectSelctionAdapter(shellDialog),
		};
		String[] namesButton = new String[] {"Annulla", "Salva"};
		Jaswt.getInstance().printButtonHorizontal(namesButton, 130, 310, 100, 30, 20, shellDialog, selAdptList);
		
		shellDialog.open();
	}
}
