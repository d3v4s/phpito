package it.phpito.view.listener.selection;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import it.as.utils.view.UtilsViewAS;
import it.as.utils.view.listener.selection.CloserShellSelectionAdpter;
import it.phpito.controller.PHPitoConf;
import it.phpito.view.shell.ShellDialogPHPito;
import it.phpito.view.shell.ShellPHPito;
import swing2swt.layout.BorderLayout;

public class LuncherSettingSelctionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito;

	public LuncherSettingSelctionAdapter(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}
	
	@Override
	public void widgetSelected(SelectionEvent se) {
		ShellDialogPHPito shellDialog = new ShellDialogPHPito(shellPHPito);
		lunchSettingPHPito(shellDialog);
	}

	/* metodo per lanciare finestra che aggiunge il progetto */
	public void lunchSettingPHPito(ShellDialogPHPito shellDialog) {
		shellDialog.setSize(370, 360);
		shellDialog.setText("Impostazioni PHPito");
		shellDialog.setLayout(new BorderLayout(0, 0));
		shellDialog.setConfChckBttnMap(new HashMap<String, Button>());
		shellDialog.setConfSpinnerMap(new HashMap<String, Spinner>());
		UtilsViewAS.getInstance().centerWindow(shellDialog);
		Label lbl;

		TabFolder tabFolder = new TabFolder(shellDialog, SWT.NONE);
		tabFolder.setLayoutData(BorderLayout.CENTER);
		
		TabItem tabItemLog = new TabItem(tabFolder, SWT.NONE);
		tabItemLog.setText("Log Monitor");
		
		Composite compositeLog = new Composite(tabFolder, SWT.NONE);
		tabItemLog.setControl(compositeLog);

		Button chckBttnActiveLogMonitor = new Button(compositeLog, SWT.CHECK);
		chckBttnActiveLogMonitor.setBounds(20, 15 + shellPHPito.getFontHeight(), 170, shellPHPito.getFontHeight());
		chckBttnActiveLogMonitor.setText("Attiva Log Monitor");
		chckBttnActiveLogMonitor.setSelection(PHPitoConf.getInstance().getActvtLogMonConf());
//		try {
//		} catch (FileException e) {
//			UtilsViewAS.getInstance().lunchMBError(shellDialog, e, PHPitoManager.NAME);
//			chckBttnActiveLogMonitor.setSelection(true);
//		}
		shellDialog.getConfChckBttnMap().put(PHPitoConf.K_CONF_ACTVT_LOG_MON, chckBttnActiveLogMonitor);

		lbl = new Label(compositeLog, SWT.NONE);
		lbl.setBounds(20, 70, 100, shellPHPito.getFontHeight());
		lbl.setText("N. righe log");
		Spinner spinner = new Spinner(compositeLog, SWT.BORDER);
		spinner.setMinimum(1);
		spinner.setMaximum(10);
		spinner.setIncrement(1);
		spinner.setBounds(110, 65, 150, 30);
		spinner.setSelection(PHPitoConf.getInstance().getRowLog());
//		try {
//		} catch (FileException e) {
//			UtilsViewAS.getInstance().lunchMBError(shellDialog, e, PHPitoManager.NAME);
//			spinner.setSelection(10);
//		}
		shellDialog.getConfSpinnerMap().put(PHPitoConf.K_CONF_ROW_LOG_MON, spinner);
		
		TabItem tabItemSys = new TabItem(tabFolder, SWT.NONE);
		tabItemSys.setText("System Info");
		
		Composite compositeSys = new Composite(tabFolder, SWT.NONE);
		tabItemSys.setControl(compositeSys);
		
		Button chckBttnActiveSystemInfo = new Button(compositeSys, SWT.CHECK);
		chckBttnActiveSystemInfo.setBounds(20, 15 + shellPHPito.getFontHeight(), 170, shellPHPito.getFontHeight());
		chckBttnActiveSystemInfo.setText("Attiva System Info");
		chckBttnActiveSystemInfo.setSelection(PHPitoConf.getInstance().getActvtSysInfoConf());
//		try {
//		} catch (FileException e) {
//			UtilsViewAS.getInstance().lunchMBError(shellDialog, e, PHPitoManager.NAME);
//			chckBttnActiveSystemInfo.setSelection(true);
//		}
		shellDialog.getConfChckBttnMap().put(PHPitoConf.K_CONF_ACTVT_SYS_INFO, chckBttnActiveSystemInfo);

		Composite compositeBottom = new Composite(shellDialog, SWT.NONE);
		compositeBottom.setLayoutData(BorderLayout.SOUTH);
		
        String[] namesBttnList = {"Annulla", "Salva"};
        SelectionAdapter[] selAdptrBttnList = {
        		new CloserShellSelectionAdpter(shellDialog),
        		new SaveConfSelectionAdapter(shellDialog)
        };
        Button bttn;
		for (int i = 0; i < namesBttnList.length; i++) {
			bttn = new Button(compositeBottom, SWT.PUSH);
			bttn.setBounds(130 + (100 + 20) * i, 20, 100, 30);
			if (selAdptrBttnList[i] != null)
				bttn.addSelectionListener(selAdptrBttnList[i]);
			if (namesBttnList[i] != null)
				bttn.setText(namesBttnList[i]);
			bttn.setCursor(new Cursor(shellPHPito.getDisplay(), SWT.CURSOR_HAND));
		}
		
		new Label(compositeBottom, SWT.NONE).setBounds(270, 20, 0, 50);
		shellDialog.open();
	}
}
