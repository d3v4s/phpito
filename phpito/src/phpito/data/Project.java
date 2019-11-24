package phpito.data;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Pattern;

import jutilas.core.Jutilas;
import jutilas.core.JutilasSys;
import jutilas.exception.FileException;
import phpito.exception.ProjectException;

/**
 * Model class for Project
 * @author Andrea Serra
 *
 */
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
	public static final int INI_DEV = 0;
	public static final int INI_DEF = 1;
	public static final int INI_CUST = 2;

	/* ############################################################################# */
	/* START GET SET */
	/* ############################################################################# */
	
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
		this.name = name.replace(" ", "_");
	}
	public Server getServer() {
		return server;
	}
	public void setServer(Server server) {
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

	/* ############################################################################# */
	/* END GET SET */
	/* ############################################################################# */

	/* ############################################################################# */
	/* START GET SET STRING */
	/* ############################################################################# */

	public String getIdString() {
		if (id == null) return null;
		return String.valueOf(id);
	}
	public void setIdString(String id) throws ProjectException {
		if (!Pattern.matches("\\d{1,}", id)) throw new ProjectException("Invalid Id");
		this.id = Long.parseLong(id);
	}
	public void setLogActiveString(String logActive) {
		this.logActive = Boolean.parseBoolean(logActive);
	}
	public String isLogActiveString() {
		if (logActive == null) return null;
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

	/* ############################################################################# */
	/* END GET SET STRING */
	/* ############################################################################# */
	
	/* ############################################################################# */
	/* START STATIC METHODS */
	/* ############################################################################# */

	/* metodo che ritorna key per hashmap progetto */
	public static String[] getArrayKeyProject() {
		return new String[] {K_ID, K_NAME, K_PATH, K_ADDRESS, K_PORT};
	}
	
	/* metodo che ritorna key per hashmap progetto */
	public static String[] getArrayKeyProjectNoId() {
		return new String[] {K_NAME, K_PATH, K_ADDRESS, K_PORT};
	}

	/* method that return the path of phpini folder */
	public static String getCustmoPhpiniDirectory() {
		return Jutilas.getInstance().getStringPath("conf", "phpini");
	}

	/* static method that return the path of phpini custom */
	public static String getCustomPhpiniPath(String name) {
		return Jutilas.getInstance().getStringPath("conf", "phpini", getCustomPhpinName(name));
	}

	/* static method that return the name of phpini custom */
	public static String getCustomPhpinName(String name) {
		return name + "-php.ini";
	}

	public static Project getDefaultProject() {
		Project project = new Project();
		try {
			project.setId(0l);
			project.setName("");
			project.setLogActive(true);
			project.setPhpini(INI_DEV);
			project.setServer(new Server());
			project.getServer().setAddress("127.0.0.1");
			project.getServer().setPort(8080);
			project.getServer().setPath(JutilasSys.getInstance().getPathUsrHome());
			project.getServer().setEnvironmentVariables(new HashMap<String, String>());
		} catch (ProjectException e) {
			e.printStackTrace();
		}
		return project;
	}

	/* ############################################################################# */
	/* END STATIC METHODS */
	/* ############################################################################# */

	/* ############################################################################# */
	/* START PUBLIC METHODS */
	/* ############################################################################# */

	/* method that return the project HashMap */
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

	/* method that return the path of selected phpini */
	public String getPhpiniPath() throws ProjectException {
		String path = "";
		if (phpini == INI_DEV) path = new File(Jutilas.getInstance().getStringPath("templates", "phpini", "development-php.ini")).getAbsolutePath();
		else if (phpini == INI_DEV) path = getCreateCustomPhpiniPath();
		return path;
	}

	/* method that create (if not exists) and return the path of phpini custom */
	public String getCreateCustomPhpiniPath() throws ProjectException {
		String pathPhpini = getCustomPhpiniPath(getIdAndName());
		File file = new File(pathPhpini);
		if (file.exists()) return file.getAbsolutePath();
		File dir = new File(getCustmoPhpiniDirectory());
		if (!dir.exists()) dir.mkdirs();
		try {
			Jutilas.getInstance().copyFile(Jutilas.getInstance().getStringPath("templates", "phpini", "default-php.ini"), pathPhpini);
		} catch (FileException e) {
			throw new ProjectException("Unable to generate the custom php file!!! " + e.getMessage());
		}
		if (file.exists()) return file.getAbsolutePath();
		throw new ProjectException("Unable to generate the custom php file!!!");
	}

	/* method that return the path of phpini custom */
	public String getCustomPhpiniPath() {
		return getCustomPhpiniPath(getIdAndName());
	}
	
	/* method that get the project clone */
	public Project clone() {
		Project clone = new Project();
		clone.setId(Long.valueOf(id));
		clone.setPhpini(Integer.valueOf(phpini));
		clone.setName(new String(name));
		clone.setLogActive(Boolean.valueOf(logActive));
		clone.setServer(server.clone());
		return clone;
	}

	/* ############################################################################# */
	/* END PUBLIC METHODS */
	/* ############################################################################# */

	/* ############################################################################# */
	/* START OVERRIDE */
	/* ############################################################################# */

	/* override toString for get info of project */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer(id != null ? "Id: " + getIdString() + "\n" : "");
		buffer.append("Nome: " + name + "\n");
		buffer.append("Path: " + server.getPath() + "\n");
		buffer.append("Indirizzo: " + server.getAddressAndPort() + "\n");
		buffer.append("Log active: " + isLogActiveString() + "\n");
		buffer.append("php.ini: " + getPhpiniType());
		return buffer.toString();
	}

	/* override equals for compare two projects */
	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().equals(this.getClass())) return false;
		Project p = (Project) obj;
		return this.toString().equals(p.toString()) && equalsEnvVars(p);
	}

	/* ############################################################################# */
	/* END OVERRIDE */
	/* ############################################################################# */

	
	private boolean equalsEnvVars(Project project) {
		return server.getEnvironmentVariables().equals(project.server.getEnvironmentVariables());
	}

	/* method that get a name of phpini selected */
	private String getPhpiniType() {
		String type = "";
		switch (phpini) {
			case INI_DEV:
				type = "developer";
				break;
			case INI_CUST:
				type = "custom";
				break;
			case INI_DEF:
				type = "default";
				break;
			default:
				break;
		}
		return type;
	}
}
