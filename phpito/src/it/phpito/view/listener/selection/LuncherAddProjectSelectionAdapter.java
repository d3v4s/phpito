package it.phpito.view.listener.selection;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
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
		Label lbl;
		String[] txtLbl = {"Nome:", "Path:", "Indirizzo:", "Porta:"};
		for (int i = 0; i < txtLbl.length; i++) {
			lbl = new Label(shellDialog, SWT.NONE);
			lbl.getFont().getFontData()[0].setHeight(shellPHPito.getFontHeight());
			lbl.setBounds(40, 15 + shellPHPito.getFontHeight() + (50*i), 60, shellPHPito.getFontHeight());
			lbl.setText(txtLbl[i]);
		}
		
		
		/* ciclo per areee di testo */
		Text txt;
		textMap = new HashMap<String, Text>();
		String[] keyList = shellDialog.getArrayKeyAddProject();
		for (int i = 0; i < keyList.length; i++) {
			txt = new Text(shellDialog, SWT.NONE);
			txt.getFont().getFontData()[0].setHeight(shellPHPito.getFontHeight());
			txt.setBounds(120, 15 + shellPHPito.getFontHeight() + (50 * i), 140, shellPHPito.getFontHeight());
//			txt.addSelectionListener(selAdptList[i]);
			if (i == 0)
				txt.forceFocus();
			textMap.put(keyList[i], txt);
		}
		
		/* add listener ad aree di testo */
		SelectionAdapter[] selAdptList = new SelectionAdapter[] {
				new TextFocusSelectionAdapter(textMap.get(ShellDialogPHPito.K_PATH)),
				new TextFocusSelectionAdapter(textMap.get(ShellDialogPHPito.K_ADDRESS)),
				new TextFocusSelectionAdapter(textMap.get(ShellDialogPHPito.K_PORT)),
				new AddProjectSelectionAdapter(shellDialog, textMap)
		};
		for (int i = 0; i < keyList.length; i++)
			textMap.get(keyList[i]).addSelectionListener(selAdptList[i]);
		
//		selAdptList = new SelectionAdapter[] {
//				new CancelTextSelectionAdapter(shellDialog, textMap),
//				new AddProjectSelectionAdapter(shellDialog, textMap),
//		};

		Button bttn;
		
		/* pulsante annulla */
		bttn = new Button(shellDialog, SWT.NONE);
		bttn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (shellDialog.isTextsEmpty(textMap))
					shellDialog.dispose();
				else
					shellDialog.emptyingText(textMap);
			}
		});
		bttn.setBounds(60, 250, 100, 30);
		bttn.setText("Annulla");

		/* pulsante Aggiungi */
		bttn = new Button(shellDialog, SWT.NONE);
		bttn.addSelectionListener(new AddProjectSelectionAdapter(shellDialog, textMap));
		bttn.setBounds(180, 250, 100, 30);
		bttn.setText("Aggiungi");
		
		shellDialog.open();
	}

}
