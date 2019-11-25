package phpito.view.shell.dialog;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
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
import phpito.view.listener.selection.rgb.DrawerColorLogMonSelectionAdapter;
import phpito.view.shell.ShellPHPito;
import swing2swt.layout.BorderLayout;

/**
 * Class shell for PHPito settings shell
 * @author Andrea Serra
 *
 */
public class ShellDialogSettings extends ShellDialogPHPitoAbstract {
	private HashMap<String, Button> confChckBttnMap;
	private HashMap<String, Spinner> confSpinnerMap;
	private HashMap<String, Integer> colorBackgrndLogMonMap;
	private HashMap<String, Integer> colorForegrndLogMonMap;
	private HashMap<String, Scale> colorScaleMap;
	private List layerLogMonList;
	private ViewColorLabel viewColorLabel;
	private Label hexColorLbl;
	private Combo styleLogMonCombo;

	/* override to bypass check subclass error */
	@Override
	protected void checkSubclass() {
	}
	
	/* ################################################################################# */
	/* START CONSTRUCTORS */
	/* ################################################################################# */

	public ShellDialogSettings(ShellPHPito shellPHPito) {
		super(shellPHPito);
	}

	/* ################################################################################# */
	/* END CONSTRUCTORS */
	/* ################################################################################# */

	@Override
	protected void createContents() {
		this.setSize(370, 400);
		this.setText("PHPito Settings");
		this.setLayout(new BorderLayout(0, 0));
		confChckBttnMap = new HashMap<String, Button>();
		confSpinnerMap = new HashMap<String, Spinner>();
		colorScaleMap = new HashMap<String, Scale>();
		colorBackgrndLogMonMap = PHPitoConf.getInstance().getColorsBckgrndLogMonMap();
		colorForegrndLogMonMap = PHPitoConf.getInstance().getColorsForegrndLogMonMap();
		Jaswt.getInstance().centerWindow(this);

		/* create tab folder */
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(BorderLayout.CENTER);

		/* create tab item for log mon settings */
		createContentsLogMon(tabFolder);

		/* create tab item for system info settings */
		createContentsSystemInfo(tabFolder);

		Composite compositeBottom = new Composite(this, SWT.NONE);
		compositeBottom.setLayoutData(BorderLayout.SOUTH);

		/* loop for button cancel and save*/
        String[] namesBttnList = {"Cancel", "Save"};
        SelectionAdapter[] selAdptrBttnList = {
    		new CloserShellSelectionAdpter(this),
    		new SaveConfSelectionAdapter(this)
        };
        Jaswt.getInstance().printButtonHorizontal(namesBttnList, 130, 20, 100, 30, 20, compositeBottom, selAdptrBttnList);
		new Label(compositeBottom, SWT.NONE).setBounds(270, 20, 0, 50);
	}
	
	/* ################################################################################# */
	/* START GET AND SET */
	/* ################################################################################# */

	public HashMap<String, Button> getConfChckBttnMap() {
		return confChckBttnMap;
	}
	public void setConfChckBttnMap(HashMap<String, Button> confChckBttnMap) {
		this.confChckBttnMap = confChckBttnMap;
	}
	public HashMap<String, Spinner> getConfSpinnerMap() {
		return confSpinnerMap;
	}
	public void setConfSpinnerMap(HashMap<String, Spinner> confSpinnerMap) {
		this.confSpinnerMap = confSpinnerMap;
	}
	public HashMap<String, Integer> getColorBackgrndLogMonMap() {
		return colorBackgrndLogMonMap;
	}
	public void setColorBackgrndLogMonMap(HashMap<String, Integer> colorBackgrndLogMonMap) {
		this.colorBackgrndLogMonMap = colorBackgrndLogMonMap;
	}
	public HashMap<String, Integer> getColorForegrndLogMonMap() {
		return colorForegrndLogMonMap;
	}
	public void setColorForegrndLogMonMap(HashMap<String, Integer> colorForegrndLogMonMap) {
		this.colorForegrndLogMonMap = colorForegrndLogMonMap;
	}
	public HashMap<String, Scale> getColorScaleMap() {
		return colorScaleMap;
	}
	public void setColorScaleMap(HashMap<String, Scale> colorScaleMap) {
		this.colorScaleMap = colorScaleMap;
	}
	public List getLayerLogMonList() {
		return layerLogMonList;
	}
	public void setLayerLogMonList(List layerLogMonList) {
		this.layerLogMonList = layerLogMonList;
	}
	public ViewColorLabel getViewColorLabel() {
		return viewColorLabel;
	}
	public void setViewColorLabel(ViewColorLabel viewColorlabel) {
		this.viewColorLabel = viewColorlabel;
	}
	public Label getHexColorLbl() {
		return hexColorLbl;
	}
	public void setHexColorLbl(Label hexColorLbl) {
		this.hexColorLbl = hexColorLbl;
	}
	public Combo getStyleLogMonCombo() {
		return styleLogMonCombo;
	}
	public void setStyleLogMonCombo(Combo styleLogMonCombo) {
		this.styleLogMonCombo = styleLogMonCombo;
	}

	/* ################################################################################# */
	/* END GET AND SET */
	/* ################################################################################# */

	/* ################################################################################# */
	/* START PUBLIC METHODS */
	/* ################################################################################# */

	/* get colors RGB map */
	public HashMap<String, Integer> getColorsRGBMap() {
		HashMap<String, Integer> colorRGBMap = new HashMap<String, Integer>();
		for (String key : PHPitoConf.K_COLORS_LIST) colorRGBMap.put(key, Integer.valueOf(colorScaleMap.get(key).getSelection()));
		return colorRGBMap;
	}

	/* get hex colors */
	public String getHexColors() {
		HashMap<String, Integer> rgbMap = getColorsRGBMap();
		int red = rgbMap.get(PHPitoConf.K_COLOR_RED);
		int green = rgbMap.get(PHPitoConf.K_COLOR_GREEN);
		int blue = rgbMap.get(PHPitoConf.K_COLOR_BLUE);
		return String.format("#%02x%02x%02x", red, green, blue);
	}

	/* get conf color map */
	public HashMap<String, String> getConfColorLogMonMap(){
		HashMap<String, String> confColorMap = new HashMap<String, String>();
		String rgbBckgrnd = colorBackgrndLogMonMap.get(PHPitoConf.K_COLOR_RED) + "," +
							colorBackgrndLogMonMap.get(PHPitoConf.K_COLOR_GREEN) + "," +
							colorBackgrndLogMonMap.get(PHPitoConf.K_COLOR_BLUE);
		String rgbForegrnd = colorForegrndLogMonMap.get(PHPitoConf.K_COLOR_RED) + "," +
							colorForegrndLogMonMap.get(PHPitoConf.K_COLOR_GREEN) + "," +
							colorForegrndLogMonMap.get(PHPitoConf.K_COLOR_BLUE);
		confColorMap.put(PHPitoConf.K_CONF_BCKGRND_LOG_MON, rgbBckgrnd);
		confColorMap.put(PHPitoConf.K_CONF_FOREGRND_LOG_MON, rgbForegrnd);
		return confColorMap;
	}

	/* method that set RGB color on label by layer and redraw it */
	public void setColorByLayerAndRedraw() throws ParameterException {
		HashMap<String, Integer> colorsMap = null;

		/* select layer to be select color */
		switch (layerLogMonList.getSelectionIndex()) {
			case 0:
				/* case background */
				colorsMap = colorBackgrndLogMonMap;
				break;
			case 1:
				/* case foreground */
				colorsMap = colorForegrndLogMonMap;
				break;
			default:
				/* disable scale RGB */
				for (String key : colorScaleMap.keySet()) colorScaleMap.get(key).setEnabled(false);
				break;
		}

		/* set colors on label view color */
		setColorLabaelAndRedraw(colorsMap);

		/* set colors on scale */
		HashMap<String, Scale> colorsScaleMap = getColorScaleMap();
		for (String key : colorsScaleMap.keySet()) colorsScaleMap.get(key).setSelection(colorsMap.get(key));

		/* print color in hex format */
		hexColorLbl.setText(getHexColors());
	}

	/* method that set RGB color on label by scale and redraw it */
	public void setColorByScaleAndRedraw() throws ParameterException {
		HashMap<String, Integer> colorsMap = getColorsRGBMap();

		/* select layer to be set color */
		switch (layerLogMonList.getSelectionIndex()) {
			case 0:
				/* case background */
				colorBackgrndLogMonMap = colorsMap;
				break;
			case 1:
				/* case foreground */
				colorForegrndLogMonMap = colorsMap;
				break;
			default:
				/* disable scale RGB */
				for (String key : colorScaleMap.keySet()) colorScaleMap.get(key).setEnabled(false);
				break;
		}

		/* set colors on label view color */
		setColorLabaelAndRedraw(colorsMap);

		/* print color in hex format */
		hexColorLbl.setText(getHexColors());
	}

	/* method that get config map by element of shell */
	public HashMap<String, String> getConfigMapByShell() {
		HashMap<String, String> confMap = new HashMap<String, String>();

		/* get configuration by shell elements */
		HashMap<String, String> colorsLogMonMap = getConfColorLogMonMap(); 
		for (String key : confChckBttnMap.keySet()) confMap.put(key, String.valueOf(confChckBttnMap.get(key).getSelection()));
		for (String key : confSpinnerMap.keySet()) confMap.put(key, String.valueOf(confSpinnerMap.get(key).getSelection()));
		for (String key : colorsLogMonMap.keySet()) confMap.put(key, colorsLogMonMap.get(key));
		confMap.put(PHPitoConf.K_CONF_STYLE_LOG_MON, String.valueOf(styleLogMonCombo.getSelectionIndex()));
		
		return confMap;
	}

	/* ################################################################################# */
	/* END PUBLIC METHODS */
	/* ################################################################################# */

	/* ################################################################################# */
	/* START PRIVATE METHODS */
	/* ################################################################################# */

	/* method that set RGB colors on label */
	private void setColorLabaelAndRedraw(HashMap<String, Integer> colorsMap) throws ParameterException {
		viewColorLabel.setRed(colorsMap.get(PHPitoConf.K_COLOR_RED));
		viewColorLabel.setGreen(colorsMap.get(PHPitoConf.K_COLOR_GREEN));
		viewColorLabel.setBlue(colorsMap.get(PHPitoConf.K_COLOR_BLUE));
		viewColorLabel.redraw();
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
		chckBttnActiveLogMonitor.setBounds(20, 15, 170, 30);
		chckBttnActiveLogMonitor.setText("Enable log monitor");
		chckBttnActiveLogMonitor.setSelection(PHPitoConf.getInstance().getActvtLogMonConf());
		confChckBttnMap.put(PHPitoConf.K_CONF_ACTVT_LOG_MON, chckBttnActiveLogMonitor);
		chckBttnActiveLogMonitor.addSelectionListener(new DisablerControlSelctionAdapter(logMonControlList));
		boolean enable = chckBttnActiveLogMonitor.getSelection();

		/* spinner to select n. rows */
		lbl = new CLabel(compositeLog, SWT.NONE);
		lbl.setBounds(20, 60, 100, 20);
		lbl.setText("N. rows of log");
		Spinner spinner = new Spinner(compositeLog, SWT.NONE);
		spinner.setMinimum(1);
		spinner.setMaximum(50);
		spinner.setIncrement(1);
		spinner.setBounds(140, 55, 150, 30);
		spinner.setSelection(PHPitoConf.getInstance().getRowLogConf());
		spinner.setEnabled(enable);
		confSpinnerMap.put(PHPitoConf.K_CONF_ROW_LOG_MON, spinner);
		logMonControlList.add(spinner);

		/* list for select color background and foreground for log monitor */
		String[] elementLogStringList = {"Background", "Foreground"}; 
		layerLogMonList = new List(compositeLog, SWT.BORDER);
		layerLogMonList.setBounds(20, 110, 220, 52);
		layerLogMonList.addSelectionListener(new DrawerColorLogMonSelectionAdapter(this, DrawerColorLogMonSelectionAdapter.LAYER));
		for (String elmntStrng : elementLogStringList) layerLogMonList.add(elmntStrng);
		layerLogMonList.select(0);
		logMonControlList.add(this.getLayerLogMonList());
		layerLogMonList.setCursor(new Cursor(shellPHPito.getDisplay(), SWT.CURSOR_HAND));
		layerLogMonList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				e.doit = false;
			}
		});
		layerLogMonList.setEnabled(enable);

		/* composite for set RGB color */
		Composite compositeRGB = new Composite(compositeLog, SWT.BORDER);
		compositeRGB.setBounds(20, 170, 320, 110);

		/* labels R G B */
		String[] namesLabel = {"R", "G", "B"};
		Jaswt.getInstance().printLabelVertical(namesLabel, 10, 20, 15, 20, 0, compositeRGB, SWT.NONE);

		/* set label view color */
		try {
			viewColorLabel = new ViewColorLabel(
					compositeRGB,
					SWT.BORDER,
					colorBackgrndLogMonMap.get(PHPitoConf.K_COLOR_RED),
					colorBackgrndLogMonMap.get(PHPitoConf.K_COLOR_GREEN),
					colorBackgrndLogMonMap.get(PHPitoConf.K_COLOR_BLUE)
			);
		} catch (ParameterException e) {
			Jaswt.getInstance().launchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
		}
		viewColorLabel.setBounds(240, 25, 50, 50);

		/* scale for set RGB */
		Scale scale;
		for (int i = 0, length = PHPitoConf.K_COLORS_LIST.length; i < length; i++) {
			scale = new Scale(compositeRGB, SWT.HORIZONTAL);
			scale.setMinimum(0);
			scale.setMaximum(255);
			scale.setIncrement(1);
			scale.setBounds(30, 20 * (i + 1), 180, 20);
			scale.setSelection(colorBackgrndLogMonMap.get(PHPitoConf.K_COLORS_LIST[i]));
			scale.addSelectionListener(new DrawerColorLogMonSelectionAdapter(this, DrawerColorLogMonSelectionAdapter.SCALE));
			scale.setCursor(new Cursor(getDisplay(), SWT.CURSOR_SIZEWE));
			scale.setEnabled(enable);
			colorScaleMap.put(PHPitoConf.K_COLORS_LIST[i], scale);
			logMonControlList.add(scale);
		}

		/* label for view color in hex */
		hexColorLbl = new Label(compositeRGB, SWT.NONE);
		hexColorLbl.setText(getHexColors());
		hexColorLbl.setBounds(240, 80, 70, 20);
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
		chckBttnActiveSystemInfo.setBounds(20, 15, 170, 30);
		chckBttnActiveSystemInfo.setText("Enable System Info");
		chckBttnActiveSystemInfo.setSelection(PHPitoConf.getInstance().getActvtSysInfoConf());
		chckBttnActiveSystemInfo.addSelectionListener(new DisablerControlSelctionAdapter(sysInfoControlList));
		confChckBttnMap.put(PHPitoConf.K_CONF_ACTVT_SYS_INFO, chckBttnActiveSystemInfo);
		boolean enable = chckBttnActiveSystemInfo.getSelection();

		/* check button to enable cpu load average monitor */
		Button chckBttnActiveCPUMon = new Button(compositeSys, SWT.CHECK);
		chckBttnActiveCPUMon.setBounds(20, 55, 180, 30);
		chckBttnActiveCPUMon.setText("View CPU load average");
		chckBttnActiveCPUMon.setEnabled(enable);
		chckBttnActiveCPUMon.setSelection(PHPitoConf.getInstance().getActvtCPUMon());
		confChckBttnMap.put(PHPitoConf.K_CONF_ACTVT_CPU_MON, chckBttnActiveCPUMon);
		sysInfoControlList.add(chckBttnActiveCPUMon);

		/* check button to enable other system info */
		Button chckBttnViewOtherInfo = new Button(compositeSys, SWT.CHECK);
		chckBttnViewOtherInfo.setBounds(20, 80, 170, 30);
		chckBttnViewOtherInfo.setText("View other sys info");
		chckBttnViewOtherInfo.setSelection(PHPitoConf.getInstance().getOthInfo());
		chckBttnViewOtherInfo.setEnabled(enable);
		confChckBttnMap.put(PHPitoConf.K_CONF_OTH_INFO, chckBttnViewOtherInfo);
		sysInfoControlList.add(chckBttnViewOtherInfo);

		/* combo to select cpu monitor style */
		lbl = new CLabel(compositeSys, SWT.NONE);
		lbl.setText("Style CPU monitor");
		lbl.setBounds(20, 120, 130, 30);
		String[] styleList = {"1", "2", "3"};
		styleLogMonCombo = new Combo(compositeSys, SWT.DROP_DOWN | SWT.READ_ONLY);
		styleLogMonCombo.setBounds(160, 120, 60, 30);
		for (String st : styleList) styleLogMonCombo.add(st);
		styleLogMonCombo.select(PHPitoConf.getInstance().getStyleLogMonConf());
		styleLogMonCombo.setEnabled(enable);
		sysInfoControlList.add(styleLogMonCombo);
	}

	/* ################################################################################# */
	/* END PRIVATE METHODS */
	/* ################################################################################# */
}
