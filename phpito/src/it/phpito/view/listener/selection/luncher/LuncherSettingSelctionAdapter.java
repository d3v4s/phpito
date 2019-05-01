package it.phpito.view.listener.selection.luncher;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import it.as.utils.exception.FormatException;
import it.as.utils.view.UtilsViewAS;
import it.as.utils.view.canvas.ViewerColorCanvas;
import it.as.utils.view.listener.selection.CloserShellSelectionAdpter;
import it.as.utils.view.listener.selection.DisablerControlSelctionAdapter;
import it.phpito.controller.PHPitoConf;
import it.phpito.controller.PHPitoManager;
import it.phpito.view.listener.selection.SaveConfSelectionAdapter;
import it.phpito.view.listener.selection.rgb.ColorsLogMonListSelectionAdapter;
import it.phpito.view.listener.selection.rgb.ColorsScaleSelectionAdapter;
import it.phpito.view.shell.ShellDialogSettings;
import it.phpito.view.shell.ShellPHPito;
import swing2swt.layout.BorderLayout;

public class LuncherSettingSelctionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito;
	private ShellDialogSettings shellDialog;

	public LuncherSettingSelctionAdapter(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}
	
	@Override
	public void widgetSelected(SelectionEvent se) {
		shellDialog = new ShellDialogSettings(shellPHPito);
		lunchSettingPHPito();
	}

	/* metodo per lanciare finestra delle impostazioni */
	public void lunchSettingPHPito() {
		shellDialog.setSize(370, 420);
		shellDialog.setText("Impostazioni PHPito");
		shellDialog.setLayout(new BorderLayout(0, 0));
		shellDialog.setConfChckBttnMap(new HashMap<String, Button>());
		shellDialog.setConfSpinnerMap(new HashMap<String, Spinner>());
		shellDialog.setColorScaleMap(new HashMap<String, Scale>());
		shellDialog.setColorBackgrndLogMonMap(PHPitoConf.getInstance().getColorsBckgrndLogMonMap());
		shellDialog.setColorForegrndLogMonMap(PHPitoConf.getInstance().getColorsForegrndLogMonMap());
		shellDialog.setLogMonControlList(new ArrayList<Control>());
		shellDialog.setSysInfoControlList(new ArrayList<Control>());
		UtilsViewAS.getInstance().centerWindow(shellDialog);

		TabFolder tabFolder = new TabFolder(shellDialog, SWT.NONE);
		tabFolder.setLayoutData(BorderLayout.CENTER);

		createContentsLogMon(tabFolder);

		createContentsSystemInfo(tabFolder);

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

	/* metodo che crea tabitem per impostazioni del log monitor */
	private void createContentsLogMon(TabFolder tabFolder) {
		TabItem tabItemLog = new TabItem(tabFolder, SWT.NONE);
		tabItemLog.setText("Log Monitor");
		Label lbl;

		Composite compositeLog = new Composite(tabFolder, SWT.NONE);
		tabItemLog.setControl(compositeLog);

		Button chckBttnActiveLogMonitor = new Button(compositeLog, SWT.CHECK);
		chckBttnActiveLogMonitor.setBounds(20, 15, 170, shellPHPito.getFontHeight());
		chckBttnActiveLogMonitor.setText("Attiva Log Monitor");
		chckBttnActiveLogMonitor.setSelection(PHPitoConf.getInstance().getActvtLogMonConf());
		shellDialog.getConfChckBttnMap().put(PHPitoConf.K_CONF_ACTVT_LOG_MON, chckBttnActiveLogMonitor);
		boolean enable = chckBttnActiveLogMonitor.getSelection();

		lbl = new Label(compositeLog, SWT.NONE);
		lbl.setBounds(20, 60, 100, shellPHPito.getFontHeight());
		lbl.setText("N. righe log");
		Spinner spinner = new Spinner(compositeLog, SWT.NONE);
		spinner.setMinimum(1);
		spinner.setMaximum(10);
		spinner.setIncrement(1);
		spinner.setBounds(110, 55, 150, 30);
		spinner.setSelection(PHPitoConf.getInstance().getRowLogConf());
		spinner.setEnabled(enable);
		shellDialog.getConfSpinnerMap().put(PHPitoConf.K_CONF_ROW_LOG_MON, spinner);
		shellDialog.getLogMonControlList().add(spinner);

		String[] elementLogStringList = {"Background", "Foreground"}; 
		shellDialog.setElementLogList(new List(compositeLog, SWT.BORDER));
		shellDialog.getElementLogList().setBounds(20, 110, 220, 52);
		shellDialog.getElementLogList().addSelectionListener(new ColorsLogMonListSelectionAdapter(shellDialog));
		for (String elmntStrng : elementLogStringList)
			shellDialog.getElementLogList().add(elmntStrng);
		shellDialog.getElementLogList().select(0);
		shellDialog.getLogMonControlList().add(shellDialog.getElementLogList());
		shellDialog.getElementLogList().setCursor(new Cursor(shellPHPito.getDisplay(), SWT.CURSOR_HAND));
		shellDialog.getElementLogList().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				e.doit = false;
			}
		});
		shellDialog.getElementLogList().setEnabled(enable);

		Composite compositeRGB = new Composite(compositeLog, SWT.BORDER);
		compositeRGB.setBounds(20, 170, 320, 110);

		String[] namesLabel = {"R", "G", "B"};
		for (int i = 0; i < namesLabel.length; i++) {
			lbl = new Label(compositeRGB, SWT.NONE);
			lbl.setBounds(10, 20 + (20 * i), 10, 20);
			lbl.setText(namesLabel[i]);
		}

		try {
			shellDialog.setViewerColorCanvas(new ViewerColorCanvas(compositeRGB, SWT.BORDER,
					shellDialog.getColorBackgrndLogMonMap().get(PHPitoConf.K_COLOR_RED),
					shellDialog.getColorBackgrndLogMonMap().get(PHPitoConf.K_COLOR_GREEN),
					shellDialog.getColorBackgrndLogMonMap().get(PHPitoConf.K_COLOR_BLUE)
			));
		} catch (FormatException e) {
			UtilsViewAS.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.NAME);
		}
		shellDialog.getViewerColorCanvas().setBounds(240, 25, 50, 50);

		Scale scale;
		for (int i = 0; i < PHPitoConf.K_COLORS_LIST.length ; i++) {
			scale = new Scale(compositeRGB, SWT.HORIZONTAL);
			scale.setMinimum(0);
			scale.setMaximum(255);
			scale.setIncrement(1);
			scale.setBounds(30, 20 * (i + 1), 180, 20);
			scale.setSelection(shellDialog.getColorBackgrndLogMonMap().get(PHPitoConf.K_COLORS_LIST[i]));
			scale.addSelectionListener(new ColorsScaleSelectionAdapter(shellDialog));
			scale.setCursor(new Cursor(shellDialog.getDisplay(), SWT.CURSOR_SIZEWE));
			scale.setEnabled(enable);
			shellDialog.getColorScaleMap().put(PHPitoConf.K_COLORS_LIST[i], scale);
			shellDialog.getLogMonControlList().add(scale);
		}
		chckBttnActiveLogMonitor.addSelectionListener(new DisablerControlSelctionAdapter(shellDialog.getLogMonControlArray()));
		
		shellDialog.setHexColorLbl(new Label(compositeRGB, SWT.NONE));
		shellDialog.getHexColorLbl().setText(shellDialog.getHexColors());
		shellDialog.getHexColorLbl().setBounds(240, 80, 70, 20);
	}

	/* metodo che crea tabitem per impostazioni del system info */
	private void createContentsSystemInfo(TabFolder tabFolder) {
		TabItem tabItemSys = new TabItem(tabFolder, SWT.NONE);
		tabItemSys.setText("System Info");

		Composite compositeSys = new Composite(tabFolder, SWT.NONE);
		tabItemSys.setControl(compositeSys);

		Button chckBttnActiveSystemInfo = new Button(compositeSys, SWT.CHECK);
		chckBttnActiveSystemInfo.setBounds(20, 15, 170, shellPHPito.getFontHeight());
		chckBttnActiveSystemInfo.setText("Attiva System Info");
		chckBttnActiveSystemInfo.setSelection(PHPitoConf.getInstance().getActvtSysInfoConf());
		shellDialog.getConfChckBttnMap().put(PHPitoConf.K_CONF_ACTVT_SYS_INFO, chckBttnActiveSystemInfo);
		
		boolean enable = chckBttnActiveSystemInfo.getSelection();
		
		Button chckBttnActiveCPUMon = new Button(compositeSys, SWT.CHECK);
		chckBttnActiveCPUMon.setBounds(20, 55, 170, shellPHPito.getFontHeight());
		chckBttnActiveCPUMon.setText("Visualizza carico CPU");
		chckBttnActiveCPUMon.setEnabled(enable);
		chckBttnActiveCPUMon.setSelection(PHPitoConf.getInstance().getActvtCPUMon());
		shellDialog.getConfChckBttnMap().put(PHPitoConf.K_CONF_ACTVT_CPU_MON, chckBttnActiveCPUMon);
		shellDialog.getSysInfoControlList().add(chckBttnActiveCPUMon);

		Button chckBttnViewOtherInfo = new Button(compositeSys, SWT.CHECK);
		chckBttnViewOtherInfo.setBounds(20, 80, 170, shellPHPito.getFontHeight());
		chckBttnViewOtherInfo.setText("Visualizza altre info");
		chckBttnViewOtherInfo.setSelection(PHPitoConf.getInstance().getOthInfo());
		chckBttnViewOtherInfo.setEnabled(enable);
		shellDialog.getConfChckBttnMap().put(PHPitoConf.K_CONF_OTH_INFO, chckBttnViewOtherInfo);
		shellDialog.getSysInfoControlList().add(chckBttnViewOtherInfo);
		
		chckBttnActiveSystemInfo.addSelectionListener(new DisablerControlSelctionAdapter(shellDialog.getSysInfoControlArray()));
	}
}
