package it.phpito.view.listener.selection.launcher;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import it.jaswt.core.Jaswt;
import it.jaswt.core.label.ViewColorLabel;
import it.jaswt.core.listener.selection.CloserShellSelectionAdpter;
import it.jaswt.core.listener.selection.DisablerControlSelctionAdapter;
import it.jaswt.exception.ParameterException;
import it.phpito.core.PHPitoConf;
import it.phpito.core.PHPitoManager;
import it.phpito.view.listener.selection.SaveConfSelectionAdapter;
import it.phpito.view.listener.selection.rgb.ColorsLogMonListSelectionAdapter;
import it.phpito.view.listener.selection.rgb.ColorsScaleSelectionAdapter;
import it.phpito.view.shell.ShellDialogSettings;
import it.phpito.view.shell.ShellPHPito;
import swing2swt.layout.BorderLayout;

public class LauncherSettingSelctionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito;
	private ShellDialogSettings shellDialogSetting;

	public LauncherSettingSelctionAdapter(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}
	
	@Override
	public void widgetSelected(SelectionEvent se) {
		shellDialogSetting = new ShellDialogSettings(shellPHPito);
		launchSettingPHPito();
	}

	/* metodo per lanciare finestra delle impostazioni */
	public void launchSettingPHPito() {
		shellDialogSetting.setSize(370, 420);
		shellDialogSetting.setText("Impostazioni PHPito");
		shellDialogSetting.setLayout(new BorderLayout(0, 0));
		shellDialogSetting.setConfChckBttnMap(new HashMap<String, Button>());
		shellDialogSetting.setConfSpinnerMap(new HashMap<String, Spinner>());
		shellDialogSetting.setColorScaleMap(new HashMap<String, Scale>());
		shellDialogSetting.setColorBackgrndLogMonMap(PHPitoConf.getInstance().getColorsBckgrndLogMonMap());
		shellDialogSetting.setColorForegrndLogMonMap(PHPitoConf.getInstance().getColorsForegrndLogMonMap());
		Jaswt.getInstance().centerWindow(shellDialogSetting);

		TabFolder tabFolder = new TabFolder(shellDialogSetting, SWT.NONE);
		tabFolder.setLayoutData(BorderLayout.CENTER);

		createContentsLogMon(tabFolder);

		createContentsSystemInfo(tabFolder);

		Composite compositeBottom = new Composite(shellDialogSetting, SWT.NONE);
		compositeBottom.setLayoutData(BorderLayout.SOUTH);

        String[] namesBttnList = {"Annulla", "Salva"};
        SelectionAdapter[] selAdptrBttnList = {
        		new CloserShellSelectionAdpter(shellDialogSetting),
        		new SaveConfSelectionAdapter(shellDialogSetting)
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
		shellDialogSetting.open();
	}

	/* metodo che crea tabitem per impostazioni del log monitor */
	private void createContentsLogMon(TabFolder tabFolder) {
		TabItem tabItemLog = new TabItem(tabFolder, SWT.NONE);
		tabItemLog.setText("Log Monitor");
		Label lbl;

		Composite compositeLog = new Composite(tabFolder, SWT.NONE);
		tabItemLog.setControl(compositeLog);

		ArrayList<Control> logMonControlList = new ArrayList<Control>();
		Button chckBttnActiveLogMonitor = new Button(compositeLog, SWT.CHECK);
		chckBttnActiveLogMonitor.setBounds(20, 15, 170, shellPHPito.getFontHeight());
		chckBttnActiveLogMonitor.setText("Attiva Log Monitor");
		chckBttnActiveLogMonitor.setSelection(PHPitoConf.getInstance().getActvtLogMonConf());
		shellDialogSetting.getConfChckBttnMap().put(PHPitoConf.K_CONF_ACTVT_LOG_MON, chckBttnActiveLogMonitor);
		chckBttnActiveLogMonitor.addSelectionListener(new DisablerControlSelctionAdapter(logMonControlList));
		boolean enable = chckBttnActiveLogMonitor.getSelection();

		lbl = new Label(compositeLog, SWT.NONE);
		lbl.setBounds(20, 60, 75, shellPHPito.getFontHeight());
		lbl.setText("N. righe log");
		Spinner spinner = new Spinner(compositeLog, SWT.NONE);
		spinner.setMinimum(1);
		spinner.setMaximum(50);
		spinner.setIncrement(1);
		spinner.setBounds(110, 55, 150, 30);
		spinner.setSelection(PHPitoConf.getInstance().getRowLogConf());
		spinner.setEnabled(enable);
		shellDialogSetting.getConfSpinnerMap().put(PHPitoConf.K_CONF_ROW_LOG_MON, spinner);
		logMonControlList.add(spinner);

		String[] elementLogStringList = {"Background", "Foreground"}; 
		shellDialogSetting.setElementLogList(new List(compositeLog, SWT.BORDER));
		shellDialogSetting.getElementLogList().setBounds(20, 110, 220, 52);
		shellDialogSetting.getElementLogList().addSelectionListener(new ColorsLogMonListSelectionAdapter(shellDialogSetting));
		for (String elmntStrng : elementLogStringList)
			shellDialogSetting.getElementLogList().add(elmntStrng);
		shellDialogSetting.getElementLogList().select(0);
		logMonControlList.add(shellDialogSetting.getElementLogList());
		shellDialogSetting.getElementLogList().setCursor(new Cursor(shellPHPito.getDisplay(), SWT.CURSOR_HAND));
		shellDialogSetting.getElementLogList().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				e.doit = false;
			}
		});
		shellDialogSetting.getElementLogList().setEnabled(enable);

		Composite compositeRGB = new Composite(compositeLog, SWT.BORDER);
		compositeRGB.setBounds(20, 170, 320, 110);

		String[] namesLabel = {"R", "G", "B"};
		for (int i = 0; i < namesLabel.length; i++) {
			lbl = new Label(compositeRGB, SWT.NONE);
			lbl.setBounds(10, 20 + (20 * i), 10, 20);
			lbl.setText(namesLabel[i]);
		}

		try {
			shellDialogSetting.setViewColorLabel(new ViewColorLabel(compositeRGB, SWT.BORDER,
					shellDialogSetting.getColorBackgrndLogMonMap().get(PHPitoConf.K_COLOR_RED),
					shellDialogSetting.getColorBackgrndLogMonMap().get(PHPitoConf.K_COLOR_GREEN),
					shellDialogSetting.getColorBackgrndLogMonMap().get(PHPitoConf.K_COLOR_BLUE)
			));
		} catch (ParameterException e) {
			Jaswt.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.NAME);
		}
		shellDialogSetting.getViewColorLabel().setBounds(240, 25, 50, 50);

		Scale scale;
		for (int i = 0; i < PHPitoConf.K_COLORS_LIST.length ; i++) {
			scale = new Scale(compositeRGB, SWT.HORIZONTAL);
			scale.setMinimum(0);
			scale.setMaximum(255);
			scale.setIncrement(1);
			scale.setBounds(30, 20 * (i + 1), 180, 20);
			scale.setSelection(shellDialogSetting.getColorBackgrndLogMonMap().get(PHPitoConf.K_COLORS_LIST[i]));
			scale.addSelectionListener(new ColorsScaleSelectionAdapter(shellDialogSetting));
			scale.setCursor(new Cursor(shellDialogSetting.getDisplay(), SWT.CURSOR_SIZEWE));
			scale.setEnabled(enable);
			shellDialogSetting.getColorScaleMap().put(PHPitoConf.K_COLORS_LIST[i], scale);
			logMonControlList.add(scale);
		}
		
		shellDialogSetting.setHexColorLbl(new Label(compositeRGB, SWT.NONE));
		shellDialogSetting.getHexColorLbl().setText(shellDialogSetting.getHexColors());
		shellDialogSetting.getHexColorLbl().setBounds(240, 80, 70, 20);
	}

	/* metodo che crea tabitem per impostazioni del system info */
	private void createContentsSystemInfo(TabFolder tabFolder) {
		TabItem tabItemSys = new TabItem(tabFolder, SWT.NONE);
		tabItemSys.setText("System Info");

		Composite compositeSys = new Composite(tabFolder, SWT.NONE);
		tabItemSys.setControl(compositeSys);

		ArrayList<Control> sysInfoControlList = new ArrayList<Control>();
		Button chckBttnActiveSystemInfo = new Button(compositeSys, SWT.CHECK);
		chckBttnActiveSystemInfo.setBounds(20, 15, 170, shellPHPito.getFontHeight());
		chckBttnActiveSystemInfo.setText("Attiva System Info");
		chckBttnActiveSystemInfo.setSelection(PHPitoConf.getInstance().getActvtSysInfoConf());
		chckBttnActiveSystemInfo.addSelectionListener(new DisablerControlSelctionAdapter(sysInfoControlList));
		shellDialogSetting.getConfChckBttnMap().put(PHPitoConf.K_CONF_ACTVT_SYS_INFO, chckBttnActiveSystemInfo);
		boolean enable = chckBttnActiveSystemInfo.getSelection();
		
		Button chckBttnActiveCPUMon = new Button(compositeSys, SWT.CHECK);
		chckBttnActiveCPUMon.setBounds(20, 55, 170, shellPHPito.getFontHeight());
		chckBttnActiveCPUMon.setText("Visualizza carico CPU");
		chckBttnActiveCPUMon.setEnabled(enable);
		chckBttnActiveCPUMon.setSelection(PHPitoConf.getInstance().getActvtCPUMon());
		shellDialogSetting.getConfChckBttnMap().put(PHPitoConf.K_CONF_ACTVT_CPU_MON, chckBttnActiveCPUMon);
		sysInfoControlList.add(chckBttnActiveCPUMon);

		Button chckBttnViewOtherInfo = new Button(compositeSys, SWT.CHECK);
		chckBttnViewOtherInfo.setBounds(20, 80, 170, shellPHPito.getFontHeight());
		chckBttnViewOtherInfo.setText("Visualizza altre info");
		chckBttnViewOtherInfo.setSelection(PHPitoConf.getInstance().getOthInfo());
		chckBttnViewOtherInfo.setEnabled(enable);
		shellDialogSetting.getConfChckBttnMap().put(PHPitoConf.K_CONF_OTH_INFO, chckBttnViewOtherInfo);
		sysInfoControlList.add(chckBttnViewOtherInfo);

		String[] styleList = {"1", "2", "3"};
		shellDialogSetting.setStyleLogMonCombo(new Combo(compositeSys, SWT.DROP_DOWN | SWT.READ_ONLY));
		shellDialogSetting.getStyleLogMonCombo().setBounds(20, 120, 60, 25);
		shellDialogSetting.getStyleLogMonCombo().setText("Style CPU Monitor");
		for (String st : styleList)
			shellDialogSetting.getStyleLogMonCombo().add(st);
		shellDialogSetting.getStyleLogMonCombo().select(PHPitoConf.getInstance().getStyleLogMonConf());
		shellDialogSetting.getStyleLogMonCombo().setEnabled(enable);
		sysInfoControlList.add(shellDialogSetting.getStyleLogMonCombo());
		
		
	}
}
