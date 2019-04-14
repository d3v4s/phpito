package it.phpito.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.as.utils.core.UtilsAS;
import it.as.utils.exception.FileException;

public class LogServer {
	private static LogServer logError;
	private final String dirLogError = Paths.get(UtilsAS.getInstance().getLogDirPath(), "server").toString();
	private final String fileLog = "log_error-";
	private final String fileType = ".log";
	private final Long maxSizeByte = 102400L;

	/* singleton */
	public static LogServer getInstance() {
		logError = (logError == null) ? new LogServer() : logError;
		return logError;
	}
	
	private LogServer() {
		super();
	}
	
	/* metodo che ritorna la cartella dei log */
	public String getDirLogError() {
		return dirLogError;
	}

	/* metodo per scrivere sul file di log un'eccezione */
	public void writeLog(FileOutputStream fos, String nameLog) throws FileException	 {
		File fdLog = new File(dirLogError);
		boolean res = false;
		if (fdLog.exists() && fdLog.isFile())
			throw new FileException("Impossibile lavorare sulla cartella 'log', controllare i permessi o che non ci sia un file con lo stesso nome.");
		else if (!fdLog.exists())
			res = fdLog.mkdirs();

		if (res || fdLog.isDirectory()) {
			String regex = fileLog + "([\\d]{6})" + fileType;
			ArrayList<String> listFileLog = new ArrayList<String>();
			String[] lfl = fdLog.list();
			for (String fname : lfl)
				if (Pattern.matches(regex, fname))
						listFileLog.add(fname);

			File fLog;
			if (listFileLog.isEmpty()) {
				fLog = Paths.get(dirLogError, fileLog + "000000" + fileType).toFile();
				try {
					if (!fLog.createNewFile())
						throw new FileException("Impossibile lavorare sul file log, controllare i permessi o che non ci sia un file con lo stesso nome.");
				} catch (IOException e) {
					throw new FileException("Impossibile lavorare sul file log.\n\t"
												+ "Message error: " + e.getMessage());
				}
			} else {
				Collections.sort(listFileLog);
				Collections.reverse(listFileLog);
				fLog = Paths.get(dirLogError, (listFileLog.isEmpty()) ? fileLog + "000000" + fileType : listFileLog.get(0)).toFile();
			}

			Long sizeLogByte = fLog.length();
			if (sizeLogByte > maxSizeByte) {
				Matcher m = Pattern.compile(regex).matcher(listFileLog.get(0));
				m.find();
				String nLog = String.format("%06d", Integer.valueOf(m.group(1)) + 1);
				
				fLog = Paths.get(dirLogError, fileLog + nLog + fileType).toFile();
				try {
					if (!fLog.createNewFile())
						throw new FileException("Impossibile lavorare sul file log, controllare i permessi o che non ci sia un file con lo stesso nome.");
				} catch (IOException e) {
					throw new FileException("Impossibile lavorare sul file log.\n\t"
												+ "Message error: " + e.getMessage());
				}
			}
			BufferedReader br = null;
			char[] textOut = null;
			try {
				br = new BufferedReader(new FileReader(fLog));
				textOut = new char[(int) fLog.length()];
				br.read(textOut);
			} catch (IOException e) {
				throw new FileException("Impossibile lavorare sul file log.\n\t"
											+ "Message error: " + e.getMessage());
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					throw new FileException("Impossibile lavorare sul file log.\n\t"
												+ "Message error: " + e.getMessage());
				}
			}
			
			PrintWriter pwLog = null;
			try {
				pwLog = new PrintWriter(fLog);
				pwLog.append(String.valueOf(textOut) +  "\nDate: " + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + " - Time: " + LocalDateTime.now().format(DateTimeFormatter.ISO_TIME));
//				fos.
//				pwLog.append(" -- Error message: " + exception.getMessage() + "\n\t");
//				exception.printStackTrace(pwLog);
			} catch (FileNotFoundException e) {
				throw new FileException("Impossibile lavorare sul file log.\n\t"
											+ "Message error: " + e.getMessage());
			} finally {
				pwLog.close();
			}
		}
	}
}
