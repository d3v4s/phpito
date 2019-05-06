package it.phpito.view.shell;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;

import it.jaswt.core.label.ViewColorLabel;
import it.phpito.controller.PHPitoConf;

public class ShellDialogSettings extends ShellDialogPHPito {
	private HashMap<String, Button> confChckBttnMap;
	private HashMap<String, Spinner> confSpinnerMap;
//	private HashMap<String, Scale> confScaleMap;
	private HashMap<String, Integer> colorBackgrndLogMonMap;
	private HashMap<String, Integer> colorForegrndLogMonMap;
	private HashMap<String, Scale> colorScaleMap;
	private ArrayList<Control> logMonControlList;
	private ArrayList<Control> sysInfoControlList;
	private List elementLogList;
	private ViewColorLabel viewColorLabel;
	private Label hexColorLbl;
	
	/* costruttore */
	public ShellDialogSettings(ShellPHPito shellPHPito, int style) {
		super(shellPHPito, style);
	}

	/* costruttore senza style */
	public ShellDialogSettings(ShellPHPito shellPHPito) {
		super(shellPHPito);
	}

	@Override
	/* override del metodo check - per evitare il controllo della subclass */
	protected void checkSubclass() {
	}

	/* get e set */
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
	public List getElementLogList() {
		return elementLogList;
	}
	public void setElementLogList(List elementLogList) {
		this.elementLogList = elementLogList;
	}
	public ViewColorLabel getViewColorLabel() {
		return viewColorLabel;
	}
	public void setViewColorLabel(ViewColorLabel viewColorlabel) {
		this.viewColorLabel = viewColorlabel;
	}
	public ArrayList<Control> getLogMonControlList() {
		return logMonControlList;
	}
	public void setLogMonControlList(ArrayList<Control> logMonControlList) {
		this.logMonControlList = logMonControlList;
	}
	public ArrayList<Control> getSysInfoControlList() {
		return sysInfoControlList;
	}
	public void setSysInfoControlList(ArrayList<Control> sysInfoControlList) {
		this.sysInfoControlList = sysInfoControlList;
	}
	public Label getHexColorLbl() {
		return hexColorLbl;
	}
	public void setHexColorLbl(Label hexColorLbl) {
		this.hexColorLbl = hexColorLbl;
	}

	public Control[] getLogMonControlArray() {
		Control[] controls = new Control[logMonControlList.size()];
		for (int i = 0; i < logMonControlList.size(); i++) {
			controls[i] = logMonControlList.get(i);
		}
		return controls;
	}
	public Control[] getSysInfoControlArray() {
		Control[] controls = new Control[sysInfoControlList.size()];
		for (int i = 0; i < sysInfoControlList.size(); i++) {
			controls[i] = sysInfoControlList.get(i);
		}
		return controls;
	}
	public HashMap<String, Integer> getColorsRGBMap() {
		HashMap<String, Integer> colorRGBMap = new HashMap<String, Integer>();
		for (String key : PHPitoConf.K_COLORS_LIST)
			colorRGBMap.put(key, Integer.valueOf(colorScaleMap.get(key).getSelection()));
		return colorRGBMap;
	}
	
	public String getHexColors() {
		int red = getColorsRGBMap().get(PHPitoConf.K_COLOR_RED);
		int green = getColorsRGBMap().get(PHPitoConf.K_COLOR_GREEN);
		int blue = getColorsRGBMap().get(PHPitoConf.K_COLOR_BLUE);
		return "#" + Integer.toHexString(red) + Integer.toHexString(green) + Integer.toHexString(blue);
	}

	public HashMap<String, String> getConfColorLogMonMap(){
		HashMap<String, String> confColorMap = new HashMap<String, String>();
		HashMap<String, Integer> colorBckgrndLogMap = getColorBackgrndLogMonMap();
		HashMap<String, Integer> colorForegrndLogMap = getColorForegrndLogMonMap();
		String rgbBckgrnd = colorBckgrndLogMap.get(PHPitoConf.K_COLOR_RED) + "," +
							colorBckgrndLogMap.get(PHPitoConf.K_COLOR_GREEN) + "," +
							colorBckgrndLogMap.get(PHPitoConf.K_COLOR_BLUE);
		String rgbForegrnd = colorForegrndLogMap.get(PHPitoConf.K_COLOR_RED) + "," +
							colorForegrndLogMap.get(PHPitoConf.K_COLOR_GREEN) + "," +
							colorForegrndLogMap.get(PHPitoConf.K_COLOR_BLUE);
		confColorMap.put(PHPitoConf.K_CONF_BCKGRND_LOG_MON, rgbBckgrnd);
		confColorMap.put(PHPitoConf.K_CONF_FOREGRND_LOG_MON, rgbForegrnd);
		return confColorMap;
	}
}
