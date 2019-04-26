package it.phpito.controller;

import java.nio.file.Paths;
import java.util.HashMap;

import it.as.utils.core.UtilsAS;
import it.as.utils.exception.FileException;

public class PHPitoConf {
	private static PHPitoConf phpItoConf;
	private final String FILE_CONF = "phpito.conf";
	private final String MSG_CONF = "This file contains the configuration of PHPito.";
	public static final String DIR_CONF = "conf";
	public static final String K_CONF_ACTVT_LOG_MON = "actvt-log-mon";
	public static final String K_CONF_ROW_LOG_MON = "row-log-mon";
	public static final String K_CONF_ACTVT_SYS_INFO = "actvt-sys-info";
	public static final String[] K_CONF_LIST = {
			K_CONF_ACTVT_LOG_MON,
			K_CONF_ROW_LOG_MON,
			K_CONF_ACTVT_SYS_INFO
	};
	private final String FILE_CONF_PATH = Paths.get(DIR_CONF, FILE_CONF).toString();
	
	private PHPitoConf() {
	}

	/* singleton */
	public static PHPitoConf getInstance() {
		return (phpItoConf = (phpItoConf == null) ? new PHPitoConf() : phpItoConf);
	}

	public boolean getActvtLogMonConf() {
		try {
			return Boolean.valueOf(getConfMap().get(K_CONF_ACTVT_LOG_MON));
		} catch (FileException e) {
			return true;
		}
	}
	
	public Integer getRowLog() {
		try {
			return Integer.parseInt(getConfMap().get(K_CONF_ROW_LOG_MON));
		} catch (NumberFormatException | FileException e) {
			return 10;
		}
	}
	
	public boolean getActvtSysInfoConf() {
		try {
			return Boolean.valueOf(getConfMap().get(K_CONF_ACTVT_SYS_INFO));
		} catch (FileException e) {
			return true;
		}
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
