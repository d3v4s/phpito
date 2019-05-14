package it.phpito.data;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Pattern;

import it.jutilas.core.Jutilas;
import it.jutilas.exception.FileException;
import it.phpito.exception.ProjectException;

public class Project {
	private Long id;
	private String name;
	private Server server;
	private Boolean logActive;
	private Integer phpini;
	public static final String K_ID = "id";
	public static final String K_NAME = "name";
	public static final String K_PATH = "path";
	public static final String K_ADDRESS = "address";
	public static final String K_PORT = "port";
	public static final String K_LOG = "log";
	public static final String K_INI = "ini";
	public static final Integer INI_DEV = 0;
	public static final Integer INI_DEF = 1;
	public static final Integer INI_CUST = 2;

	/* get e set */
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Server getServer() {
		return server;
	}
	public void setServer(Server server) {
		server.setProject(this);
		this.server = server;
	}
	public Boolean isLogActive() {
		return logActive;
	}
	public void setLogActive(Boolean log) {
		this.logActive = log;
	}
	public Integer getPhpini() {
		return phpini;
	}
	public void setPhpini(Integer phpini) {
		this.phpini = phpini;
	}
	public String getIdString() {
		if (id == null)
			return null;
		return String.valueOf(id);
	}
	public void setIdString(String id) throws ProjectException {
		if (!Pattern.matches("\\d{1,}", id))
			throw new ProjectException("Id non valido");
		this.id = Long.parseLong(id);
	}
	public void setLogActiveString(String logActive) {
		this.logActive = Boolean.parseBoolean(logActive);
	}
	public String isLogActiveString() {
		if (logActive == null)
			return null;
		return String.valueOf(logActive);
	}
	public String getIdAndName() {
		return String.format("%04d", id) + "-" + name;
	}
	public String getPhpiniString() {
		return String.valueOf(phpini);
	}
	public void setPhpiniString(String phpini) {
		this.phpini = Integer.valueOf(phpini);
	}
	/* metodo che ritorna key per hashmap progetto */
	public static String[] getArrayKeyProject() {
		return new String[]{K_ID, K_NAME, K_PATH, K_ADDRESS, K_PORT};
	}
	
	/* metodo che ritorna key per hashmap progetto */
	public static String[] getArrayKeyProjectNoId() {
		return new String[]{K_NAME, K_PATH, K_ADDRESS, K_PORT};
	}
	
	public HashMap<String, String> getHashMap() {
		HashMap<String, String> projectMap = new HashMap<String, String>();
		projectMap.put(K_ID, getIdString());
		projectMap.put(K_NAME, name);
		projectMap.put(K_PATH, server.getPath());
		projectMap.put(K_ADDRESS, server.getAddress());
		projectMap.put(K_PORT, server.getPortString());
		projectMap.put(K_LOG, isLogActiveString());
		return projectMap;
	}

	public String getPhpiniPath() throws ProjectException {
		String path = "";
		if (phpini == INI_DEV)
			path = new File(Jutilas.getInstance().getStringPath("templates", "phpini", "development-php.ini")).getAbsolutePath();
		else if (phpini == INI_CUST)
			path = getCustomPhpini();
		
		return path;
	}

	private String getCustomPhpini() throws ProjectException {
		String pathPhpini = getCustomPhpiniPath(getIdAndName());
		File file = new File(pathPhpini);
		if (file.exists())
			return file.getAbsolutePath();
		try {
			Jutilas.getInstance().copyFile(Jutilas.getInstance().getStringPath("templates", "phpini", "default-php.ini"), pathPhpini);
		} catch (FileException e) {
			throw new ProjectException("Impossibile generare il file php personalizzato!!! " + e.getMessage());
		}
		if (file.exists())
			return file.getAbsolutePath();
		throw new ProjectException("Impossibile generare il file php personalizzato!!!");
	}

	public String getCustomPhpiniPath() {
		return getCustomPhpiniPath(getIdAndName());
	}

	public static String getCustomPhpiniPath(String name) {
		return Jutilas.getInstance().getStringPath("conf", "phpini", getCustomPhpinName(name));
	}

	public static String getCustomPhpinName(String name) {
		return name + "-php.ini";
	}

	@Override
	public String toString() {
		String ret = (id != null ? "Id: " + getIdString() + "\n" : "").concat(
					"Nome: " + name + "\n"
					+ "Path: " + server.getPath() + "\n"
					+ "Indirizzo: " + server.getAddressAndPort());
		return ret;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().equals(this.getClass()))
			return false;
		Project p = (Project) obj;
		return this.toString().equals(p.toString());
	}

	public Project clone() {
		Project clone = new Project();
		clone.setId(new Long(id));
		clone.setName(new String(name));
		clone.setLogActive(new Boolean(logActive));
		clone.setServer(server.clone());
		return clone;
	}
}
