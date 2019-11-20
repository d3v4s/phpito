package phpito.view.listener.selection.launcher;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

import jaswt.core.Jaswt;
import jaswt.exception.ParameterException;
import jaswt.label.ViewColorLabel;
import jaswt.listener.selection.CloserShellSelectionAdpter;
import jaswt.listener.selection.DisablerControlSelctionAdapter;
import phpito.core.PHPitoConf;
import phpito.core.PHPitoManager;
import phpito.view.listener.selection.SaveConfSelectionAdapter;
import phpito.view.listener.selection.rgb.ColorsLogMonListSelectionAdapter;
import phpito.view.listener.selection.rgb.ColorsScaleSelectionAdapter;
import phpito.view.shell.ShellPHPito;
import phpito.view.shell.dialog.ShellDialogSettings;
import swing2swt.layout.BorderLayout;

/**
 * Class SelectionListener to launch settings window
 * @author Andrea Serra
 *
 */
public class LauncherSettingSelctionAdapter implements SelectionListener {
	private ShellDialogSettings shellDialogSettings;
	private ShellPHPito shellPHPito;

	/* CONSTRUCT */
	public LauncherSettingSelctionAdapter(ShellPHPito shellPHPito) {
		this.shellPHPito = shellPHPito;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
		
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent se) {
		launchSettingPHPito();
	}

	/* metodo per lanciare finestra delle impostazioni */
	public void launchSettingPHPito() {
		shellDialogSettings = new ShellDialogSettings(shellPHPito);
		shellDialogSettings.setSize(370, 400);
		shellDialogSettings.setText("PHPito Settings");
		shellDialogSettings.setLayout(new BorderLayout(0, 0));
		shellDialogSettings.setConfChckBttnMap(new HashMap<String, Button>());
		shellDialogSettings.setConfSpinnerMap(new HashMap<String, Spinner>());
		shellDialogSettings.setColorScaleMap(new HashMap<String, Scale>());
		shellDialogSettings.setColorBackgrndLogMonMap(PHPitoConf.getInstance().getColorsBckgrndLogMonMap());
		shellDialogSettings.setColorForegrndLogMonMap(PHPitoConf.getInstance().getColorsForegrndLogMonMap());
		Jaswt.getInstance().centerWindow(shellDialogSettings);

		TabFolder tabFolder = new TabFolder(shellDialogSettings, SWT.NONE);
		tabFolder.setLayoutData(BorderLayout.CENTER);

		/* create tabfolder for log mon settings */
		createContentsLogMon(tabFolder);

		/* create tabfolder for system info settings */
		createContentsSystemInfo(tabFolder);

		Composite compositeBottom = new Composite(shellDialogSettings, SWT.NONE);
		compositeBottom.setLayoutData(BorderLayout.SOUTH);

		/* loop for button cancel and save*/
        String[] namesBttnList = {"Cancel", "Save"};
        SelectionAdapter[] selAdptrBttnList = {
    		new CloserShellSelectionAdpter(shellDialogSettings),
    		new SaveConfSelectionAdapter(shellDialogSettings)
        };
        Jaswt.getInstance().printButtonHorizontal(namesBttnList, 130, 20, 100, 30, 20, compositeBottom, selAdptrBttnList);
		new Label(compositeBottom, SWT.NONE).setBounds(270, 20, 0, 50);

		shellDialogSettings.open();
	}

	/* metodo che crea tabitem per impostazioni del log monitor */
	private void createContentsLogMon(TabFolder tabFolder) {
		TabItem tabItemLog = new TabItem(tabFolder, SWT.NONE);
		tabItemLog.setText("Log Monitor");
		CLabel lbl;

		Composite compositeLog = new Composite(tabFolder, SWT.NONE);
		tabItemLog.setControl(compositeLog);

		/* check button to enable log monitor */
		ArrayList<Control> logMonControlList = new ArrayList<Control>();
		Button chckBttnActiveLogMonitor = new Button(compositeLog, SWT.CHECK);
		chckBttnActiveLogMonitor.setBounds(20, 15, 170, shellPHPito.getFontHeight());
		chckBttnActiveLogMonitor.setText("Enable log monitor");
		chckBttnActiveLogMonitor.setSelection(PHPitoConf.getInstance().getActvtLogMonConf());
		shellDialogSettings.getConfChckBttnMap().put(PHPitoConf.K_CONF_ACTVT_LOG_MON, chckBttnActiveLogMonitor);
		chckBttnActiveLogMonitor.addSelectionListener(new DisablerControlSelctionAdapter(logMonControlList));
		boolean enable = chckBttnActiveLogMonitor.getSelection();

		/* spinner to select n. rows */
		lbl = new CLabel(compositeLog, SWT.NONE);
		lbl.setBounds(20, 60, 100, shellPHPito.getFontHeight());
		lbl.setText("N. rows of log");
		Spinner spinner = new Spinner(compositeLog, SWT.NONE);
		spinner.setMinimum(1);
		spinner.setMaximum(50);
		spinner.setIncrement(1);
		spinner.setBounds(140, 55, 150, 30);
		spinner.setSelection(PHPitoConf.getInstance().getRowLogConf());
		spinner.setEnabled(enable);
		shellDialogSettings.getConfSpinnerMap().put(PHPitoConf.K_CONF_ROW_LOG_MON, spinner);
		logMonControlList.add(spinner);

		/* list for select color background and foreground for log monitor */
		String[] elementLogStringList = {"Background", "Foreground"}; 
		shellDialogSettings.setElementLogList(new List(compositeLog, SWT.BORDER));
		shellDialogSettings.getElementLogList().setBounds(20, 110, 220, 52);
		shellDialogSettings.getElementLogList().addSelectionListener(new ColorsLogMonListSelectionAdapter(shellDialogSettings));
		for (String elmntStrng : elementLogStringList) shellDialogSettings.getElementLogList().add(elmntStrng);
		shellDialogSettings.getElementLogList().select(0);
		logMonControlList.add(shellDialogSettings.getElementLogList());
		shellDialogSettings.getElementLogList().setCursor(new Cursor(shellPHPito.getDisplay(), SWT.CURSOR_HAND));
		shellDialogSettings.getElementLogList().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				e.doit = false;
			}
		});
		shellDialogSettings.getElementLogList().setEnabled(enable);

		/* composite for set RGB color */
		Composite compositeRGB = new Composite(compositeLog, SWT.BORDER);
		compositeRGB.setBounds(20, 170, 320, 110);

		/* labels R G B */
		String[] namesLabel = {"R", "G", "B"};
		Jaswt.getInstance().printLabelVertical(namesLabel, 10, 20, 15, 20, 0, compositeRGB, SWT.NONE);

		/* set label view color */
		try {
			shellDialogSettings.setViewColorLabel(
				new ViewColorLabel(
					compositeRGB,
					SWT.BORDER,
					shellDialogSettings.getColorBackgrndLogMonMap().get(PHPitoConf.K_COLOR_RED),
					shellDialogSettings.getColorBackgrndLogMonMap().get(PHPitoConf.K_COLOR_GREEN),
					shellDialogSettings.getColorBackgrndLogMonMap().get(PHPitoConf.K_COLOR_BLUE)
			));
		} catch (ParameterException e) {
			Jaswt.getInstance().launchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
		}
		shellDialogSettings.getViewColorLabel().setBounds(240, 25, 50, 50);

		/* scale for set RGB */
		Scale scale;
		for (int i = 0, length = PHPitoConf.K_COLORS_LIST.length; i < length; i++) {
			scale = new Scale(compositeRGB, SWT.HORIZONTAL);
			scale.setMinimum(0);
			scale.setMaximum(255);
			scale.setIncrement(1);
			scale.setBounds(30, 20 * (i + 1), 180, 20);
			scale.setSelection(shellDialogSettings.getColorBackgrndLogMonMap().get(PHPitoConf.K_COLORS_LIST[i]));
			scale.addSelectionListener(new ColorsScaleSelectionAdapter(shellDialogSettings));
			scale.setCursor(new Cursor(shellDialogSettings.getDisplay(), SWT.CURSOR_SIZEWE));
			scale.setEnabled(enable);
			shellDialogSettings.getColorScaleMap().put(PHPitoConf.K_COLORS_LIST[i], scale);
			logMonControlList.add(scale);
		}

		/* label for view color in hex */
		shellDialogSettings.setHexColorLbl(new Label(compositeRGB, SWT.NONE));
		shellDialogSettings.getHexColorLbl().setText(shellDialogSettings.getHexColors());
		shellDialogSettings.getHexColorLbl().setBounds(240, 80, 70, 20);
	}

	
	/* metodo che crea tabitem per impostazioni del system info */
	private void createContentsSystemInfo(TabFolder tabFolder) {
		TabItem tabItemSys = new TabItem(tabFolder, SWT.NONE);
		tabItemSys.setText("System Info");
		CLabel lbl;

		Composite compositeSys = new Composite(tabFolder, SWT.NONE);
		tabItemSys.setControl(compositeSys);

		/* check button to enable system info */
		ArrayList<Control> sysInfoControlList = new ArrayList<Control>();
		Button chckBttnActiveSystemInfo = new Button(compositeSys, SWT.CHECK);
		chckBttnActiveSystemInfo.setBounds(20, 15, 170, shellPHPito.getFontHeight());
		chckBttnActiveSystemInfo.setText("Enable System Info");
		chckBttnActiveSystemInfo.setSelection(PHPitoConf.getInstance().getActvtSysInfoConf());
		chckBttnActiveSystemInfo.addSelectionListener(new DisablerControlSelctionAdapter(sysInfoControlList));
		shellDialogSettings.getConfChckBttnMap().put(PHPitoConf.K_CONF_ACTVT_SYS_INFO, chckBttnActiveSystemInfo);
		boolean enable = chckBttnActiveSystemInfo.getSelection();

		/* check button to enable cpu load average monitor */
		Button chckBttnActiveCPUMon = new Button(compositeSys, SWT.CHECK);
		chckBttnActiveCPUMon.setBounds(20, 55, 170, shellPHPito.getFontHeight());
		chckBttnActiveCPUMon.setText("View CPU load average");
		chckBttnActiveCPUMon.setEnabled(enable);
		chckBttnActiveCPUMon.setSelection(PHPitoConf.getInstance().getActvtCPUMon());
		shellDialogSettings.getConfChckBttnMap().put(PHPitoConf.K_CONF_ACTVT_CPU_MON, chckBttnActiveCPUMon);
		sysInfoControlList.add(chckBttnActiveCPUMon);

		/* check button to enable other system info */
		Button chckBttnViewOtherInfo = new Button(compositeSys, SWT.CHECK);
		chckBttnViewOtherInfo.setBounds(20, 80, 170, shellPHPito.getFontHeight());
		chckBttnViewOtherInfo.setText("View other sys info");
		chckBttnViewOtherInfo.setSelection(PHPitoConf.getInstance().getOthInfo());
		chckBttnViewOtherInfo.setEnabled(enable);
		shellDialogSettings.getConfChckBttnMap().put(PHPitoConf.K_CONF_OTH_INFO, chckBttnViewOtherInfo);
		sysInfoControlList.add(chckBttnViewOtherInfo);

		/* combo to select cpu monitor style */
		lbl = new CLabel(compositeSys, SWT.NONE);
		lbl.setText("Style CPU monitor");
		lbl.setBounds(20, 120, 130, 25);
		String[] styleList = {"1", "2", "3"};
		shellDialogSettings.setStyleLogMonCombo(new Combo(compositeSys, SWT.DROP_DOWN | SWT.READ_ONLY));
		shellDialogSettings.getStyleLogMonCombo().setBounds(160, 120, 60, 25);
		for (String st : styleList) shellDialogSettings.getStyleLogMonCombo().add(st);
		shellDialogSettings.getStyleLogMonCombo().select(PHPitoConf.getInstance().getStyleLogMonConf());
		shellDialogSettings.getStyleLogMonCombo().setEnabled(enable);
		sysInfoControlList.add(shellDialogSettings.getStyleLogMonCombo());
		
		
	}
}
