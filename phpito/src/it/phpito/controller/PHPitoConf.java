package it.phpito.controller;

import java.nio.file.Paths;
import java.util.HashMap;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import it.as.utils.core.UtilsAS;
import it.as.utils.exception.FileException;

public class PHPitoConf {
	private static PHPitoConf phpItoConf;
	private final String FILE_CONF = "phpito.conf";
	private final String MSG_CONF = "This file contains the configuration of PHPito.";
	public static final String DIR_CONF = "conf";
	public static final String K_CONF_ACTVT_LOG_MON = "actvt-log-mon";
	public static final String K_CONF_ROW_LOG_MON = "row-log-mon";
	public static final String K_CONF_BCKGRND_LOG_MON = "bckgrnd-log-mon";
	public static final String K_CONF_FOREGRND_LOG_MON = "frgrnd-log-mon";
	public static final String K_CONF_ACTVT_SYS_INFO = "actvt-sys-info";
	public static final String K_CONF_ACTVT_CPU_MON = "actvt-cpu-mon";
	public static final String K_CONF_OTH_INFO = "oth-info";
	public static final String[] K_CONF_LIST = {
			K_CONF_ACTVT_LOG_MON,
			K_CONF_ROW_LOG_MON,
			K_CONF_BCKGRND_LOG_MON,
			K_CONF_FOREGRND_LOG_MON,
			K_CONF_ACTVT_SYS_INFO,
			K_CONF_ACTVT_CPU_MON,
			K_CONF_OTH_INFO
	};
	public static final String K_COLOR_RED = "red";
	public static final String K_COLOR_GREEN = "green";
	public static final String K_COLOR_BLUE = "blue";
	public static final String[] K_COLORS_LIST = {
			K_COLOR_RED,
			K_COLOR_GREEN,
			K_COLOR_BLUE
	};
	private final String FILE_CONF_PATH = Paths.get(DIR_CONF, FILE_CONF).toString();
	
	private PHPitoConf() {
	}

	/* singleton */
	public static PHPitoConf getInstance() {
		return (phpItoConf = (phpItoConf == null) ? new PHPitoConf() : phpItoConf);
	}

	/* get conf */
	public boolean getActvtLogMonConf() {
		try {
			return Boolean.valueOf(getConfMap().get(K_CONF_ACTVT_LOG_MON));
		} catch (Exception e) {
			return true;
		}
	}
	public Integer getRowLogConf() {
		try {
			return Integer.parseInt(getConfMap().get(K_CONF_ROW_LOG_MON));
		} catch (Exception e) {
			return 10;
		}
	}
	public Color getColorBckgrndLogMonConf() {
		Color color = new Color(Display.getDefault(), new RGB(0, 0, 0));
		try {
			String val = getConfMap().get(K_CONF_BCKGRND_LOG_MON);
			if (val != null) {
				String[] rgb = val.split(",");
				color = new Color(Display.getDefault(), new RGB(Integer.valueOf(rgb[0].trim()), Integer.valueOf(rgb[1].trim()), Integer.valueOf(rgb[2].trim())));
			}
		} catch (Exception e) {
		}
		return color;
	}
	public Color getColorForegrndLogMonConf() {
		Color color = new Color(Display.getDefault(), new RGB(20, 255, 20));
		try {
			String val = getConfMap().get(K_CONF_FOREGRND_LOG_MON);
			if (val != null) {
				String[] rgb = val.split(",");
				color = new Color(Display.getDefault(), new RGB(Integer.valueOf(rgb[0].trim()), Integer.valueOf(rgb[1].trim()), Integer.valueOf(rgb[2].trim())));
			}
		} catch (Exception e) {
		}
		return color;
	}
	public boolean getActvtSysInfoConf() {
		try {
			return Boolean.valueOf(getConfMap().get(K_CONF_ACTVT_SYS_INFO));
		} catch (Exception e) {
			return true;
		}
	}
	public boolean getActvtCPUMon() {
		try {
			return Boolean.valueOf(getConfMap().get(K_CONF_ACTVT_CPU_MON));
		} catch (Exception e) {
			return true;
		}
	}
	public boolean getOthInfo() {
		try {
			return Boolean.valueOf(getConfMap().get(K_CONF_OTH_INFO));
		} catch (Exception e) {
			return true;
		}
	}

	public HashMap<String, Integer> getColorsBckgrndLogMonMap() {
		HashMap<String, Integer> colorsMap = new HashMap<String, Integer>();
		int red = 0;
		int green = 0;
		int blue = 0;
		try {
			String val = getConfMap().get(K_CONF_BCKGRND_LOG_MON);
			if (val != null) {
				String[] rgb = val.split(",");
				red = Integer.valueOf(rgb[0].trim());
				green = Integer.valueOf(rgb[1].trim());
				blue = Integer.valueOf(rgb[2].trim());
			}
		} catch (Exception e) {
		}
		colorsMap.put(K_COLOR_RED, red);
		colorsMap.put(K_COLOR_GREEN, green);
		colorsMap.put(K_COLOR_BLUE, blue);
		return colorsMap;
	}
	
	public HashMap<String, Integer> getColorsForegrndLogMonMap() {
		HashMap<String, Integer> colorsMap = new HashMap<String, Integer>();
		int red = 20;
		int green = 255;
		int blue = 20;
		try {
			String val = getConfMap().get(K_CONF_FOREGRND_LOG_MON);
			if (val != null) {
				String[] rgb = val.split(",");
				red = Integer.valueOf(rgb[0].trim());
				green = Integer.valueOf(rgb[1].trim());
				blue = Integer.valueOf(rgb[2].trim());
			}
		} catch (Exception e) {
		}
		colorsMap.put(K_COLOR_RED, red);
		colorsMap.put(K_COLOR_GREEN, green);
		colorsMap.put(K_COLOR_BLUE, blue);
		return colorsMap;
	}
	public HashMap<String, String> getConfMap() throws FileException {
		HashMap<String, String> confMap = new HashMap<String, String>();
		for (String key : K_CONF_LIST)
			confMap.put(key,UtilsAS.getInstance().getConf(FILE_CONF_PATH, key));
		return confMap;
	}
	
	public void saveConf(HashMap<String, String> confMap) throws FileException {
		for (String key : confMap.keySet())
			UtilsAS.getInstance().setConf(FILE_CONF_PATH, key, confMap.get(key), MSG_CONF);
	}

}
