package phpito.core.lock;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import exception.JSXLockException;
import exception.LockLogException;
import jsx.JSX;
import jutilas.core.Jutilas;
import jutilas.exception.FileException;
import phpito.core.PHPitoConf;
import phpito.core.PHPitoManager;
import phpito.data.Project;
import phpito.data.Server;
import phpito.exception.ProjectException;

/**
 * Class for write and read the projects XML file and implementing the ReentrantLock
 * @author Andrea Serra
 *
 */
public class ReentrantLockProjectsXML extends JSX {
	private final String FILE_NAME_XML = "projects.xml";
	private final String PATH_FILE_XML = Paths.get(PHPitoConf.DIR_CONF, FILE_NAME_XML).toString();
	private final String XML_SERVER = "server";
	private final String XML_NAME = "name";
	private final String XML_PATH = "path";
	private final String XML_ADDRESS = "address";
	private final String XML_PORT = "port";
	private final String XML_PID = "pid";
	private final String XML_LOG = "log";
	private final String XML_INI = "ini";
	private final String XML_VAR = "env-var";
	private final String XML_VAR_KEY = "key";
	private final String XML_VAR_VAL = "val";

	/* CONSTRUCT */
	public ReentrantLockProjectsXML() {
		super();
		setFilePath(PATH_FILE_XML);
		setLock(true);
//		setAutoFlush(false);
		try {
			loadDocument();
		} catch (JSXLockException e) {
			PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Construct Load Document - LOCK ERROR");
			try {
				PHPitoManager.getInstance().getJoggerError().writeLog(e);
			} catch (LockLogException e1) {
				e1.printStackTrace();
			}
		}
	}

	/* ################################################################################# */
	/* START PUBLIC METHODS */
	/* ################################################################################# */

	/* meotodo che ritorna hashmap dei progetti con key id */
	public HashMap<String, Project> getProjectsMap() throws ProjectException {
		PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Get Projects Map - START");
		HashMap<String, Project> mapProjects = new HashMap<String, Project>();
		try {
			HashMap<String, Node> mapNode = getMapIdElement(XML_SERVER);
			Set<String> idSet = mapNode.keySet();
			for (String id : idSet) mapProjects.put(id, getProjectByNode(mapNode.get(id), id));
			PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Get Project Map - SUCCESFULLY");
		} catch (JSXLockException e) {
			PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Get Project Map - LOCK ERROR");
			try {
				PHPitoManager.getInstance().getJoggerError().writeLog(e);
			} catch (LockLogException e1) {
				e1.printStackTrace();
			}
		}
		return mapProjects;
	}

	/* method that get projects array list */
	public ArrayList<Project> getProjectsArray() throws ProjectException {
		PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Get Projects ArrayList - START");
		ArrayList<Project> projects = new ArrayList<Project>();
		try {
			HashMap<String, Node> nodes = getMapIdElement(XML_SERVER);
			Set<String> idSet = nodes.keySet();
			for (String id : idSet) projects.add(getProjectByNode(nodes.get(id), id));
			PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Get Projects ArrayList - SUCCESSFULLY");
		} catch (JSXLockException e) {
			PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Get Projects ArrayList - LOCK ERROR");
			try {
				PHPitoManager.getInstance().getJoggerError().writeLog(e);
			} catch (LockLogException e1) {
				e1.printStackTrace();
			}
		}
		return projects;
	}

	/* metodo che ritorna progetto da id */
	public Project getProject(String id) throws ProjectException {
		PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Get Project - START");
		Project project = null;
		try {
			Node node = getMapIdElement(XML_SERVER).get(id);
			if (node == null) {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Get Project - NULL");
				return null;
			}
			project = getProjectByNode(node, id);
			PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Get Project - SUCCESFULLY");
		} catch (JSXLockException e) {
			PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Get Project - LOCK ERROR");
			try {
				PHPitoManager.getInstance().getJoggerError().writeLog(e);
			} catch (LockLogException e1) {
				e1.printStackTrace();
			}
		}
		return project;
	}

	/* meotodo che ritorna il prossimo id da utilizzare */
	public String getNextIdProject() {
		PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Next ID Project - START");
		String id = null;
		try {
			Set<String> setId = getMapIdElement(XML_SERVER).keySet();
			long idLong = JSX.getGreatId(setId) + 1;
			id = String.valueOf((idLong < 1L) ? 1 : idLong);
			PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Next ID Project - SUCCESFULLY");
		} catch (JSXLockException e) {
			PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Next ID Project - LOCK ERROR");
			try {
				PHPitoManager.getInstance().getJoggerError().writeLog(e);
			} catch (LockLogException e1) {
				e1.printStackTrace();
			}
		}
		return id;
	}

	/* metodo che aggiunge progetto */
	public void addProject(Project project) throws ProjectException {
		PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Add Project - START");
		try {
			String id = getNextIdProject();
			project.setIdString(id);
			createProject(project);
			flush(PATH_FILE_XML, true);
			PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Add Project - SUCCESFULLY");
		} catch (JSXLockException e) {
			PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Add Project - LOCK ERROR");
			try {
				PHPitoManager.getInstance().getJoggerError().writeLog(e);
			} catch (LockLogException e1) {
				e1.printStackTrace();
			}
		}
	}

	/* metodo che aggoirna progetto */
	public void updateProject(Project project) {
		PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Update Project - START");
		try {
			deleteProject(project.getIdString());
			createProject(project);
			flush(PATH_FILE_XML, true);
			PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Update Project - SUCCESFULLY");
		} catch (JSXLockException e) {
			PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Update Project - LOCK ERROR");
			try {
				PHPitoManager.getInstance().getJoggerError().writeLog(e);
			} catch (LockLogException e1) {
				e1.printStackTrace();
			}
		}
	}

	/* metodo che aggoirna progetto */
	public void deleteProject(String id) {
		PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Delete Project - START");
		try {
			deleteNode(XML_SERVER, id);
			flush(PATH_FILE_XML, true);
			PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Delete Project - SUCCESFULLY");
		} catch (JSXLockException e) {
			PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Delete Project - LOCK ERROR");
			try {
				PHPitoManager.getInstance().getJoggerError().writeLog(e);
			} catch (LockLogException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * method that export XML projects on destination path
	 * @param destPath destination path
	 * @throws FileException 
	 */
	public void exportXML(String destPath) throws FileException {
		PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Export Project - START");
		try {
			if (tryLock()) {
				destPath = Paths.get(destPath, System.currentTimeMillis() + "-" + FILE_NAME_XML).toString();
				Jutilas.getInstance().copyFile(PATH_FILE_XML, destPath);
				PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Export Project - SUCCESSFULLY");
				tryUnlock();
			}
		} catch (JSXLockException e) {
			PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Export Project - LOCK ERROR");
			try {
				PHPitoManager.getInstance().getJoggerError().writeLog(e);
			} catch (LockLogException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * method that import XML projects by source path
	 * @param sourcePath source path
	 * @throws FileException 
	 */
	public void importXML(String sourcePath) throws FileException {
		PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Import Project - START");
		try {
			if (tryLock()) {
				Jutilas.getInstance().recursiveDelete(PATH_FILE_XML);
				Jutilas.getInstance().copyFile(sourcePath, PATH_FILE_XML);
				loadDocument();
				PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Import Project - SUCCESSFULLY");
				tryUnlock();
			}
		} catch (JSXLockException e) {
			PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Import Project - LOCK ERROR");
			try {
				PHPitoManager.getInstance().getJoggerError().writeLog(e);
			} catch (LockLogException e1) {
				e1.printStackTrace();
			}
		}
	}

	/* ################################################################################# */
	/* END PUBLIC METHODS */
	/* ################################################################################# */

	/* ################################################################################# */
	/* START PRIVATE METHODS */
	/* ################################################################################# */

	/* method that get a Project by node */
	private Project getProjectByNode(Node node, String id) throws ProjectException, DOMException, JSXLockException {
		Project project = new Project();
		project.setIdString(id);
		project.setName(getArrayChildNode(node, XML_NAME).get(0).getTextContent());
		project.setLogActiveString(getArrayChildNode(node, XML_LOG).get(0).getTextContent());
		project.setPhpiniString(getArrayChildNode(node, XML_INI).get(0).getTextContent());
		project.setServer(new Server());
		project.getServer().setAddress(getArrayChildNode(node, XML_ADDRESS).get(0).getTextContent());
		project.getServer().setPortString(getArrayChildNode(node, XML_PORT).get(0).getTextContent());
		project.getServer().setPath(getArrayChildNode(node, XML_PATH).get(0).getTextContent());
		project.getServer().setEnvironmentVariables(getEnvironmentVariables(node));
		if (!getArrayChildNode(node, XML_PID).isEmpty()) {
			String pid = getArrayChildNode(node, XML_PID).get(0).getTextContent();
			project.getServer().setProcessIdString(pid);
		}
		return project;
	}

	/* method that get a environment variables by project node */
	private HashMap<String, String> getEnvironmentVariables(Node node) throws JSXLockException {
		HashMap<String, String> variablesMap = new HashMap<String, String>();
		ArrayList<Node> nodeList = getArrayChildNode(node, XML_VAR);
		String key = "";
		String val = "";
		for (Node nodeVar : nodeList) {
			key = getArrayChildNode(nodeVar, XML_VAR_KEY).get(0).getTextContent();
			val = getArrayChildNode(nodeVar, XML_VAR_VAL).get(0).getTextContent();
			variablesMap.put(key, val);
		}
		return variablesMap;
	}

	/* method that create a project on document */
	private void createProject(Project project) throws JSXLockException {
		HashMap<String, String> mapChild = new HashMap<String, String>();
		mapChild.put(XML_NAME, project.getName());
		mapChild.put(XML_PATH, project.getServer().getPath());
		mapChild.put(XML_ADDRESS, project.getServer().getAddress());
		mapChild.put(XML_PORT, project.getServer().getPortString());
		mapChild.put(XML_LOG, project.isLogActiveString());
		mapChild.put(XML_INI, project.getPhpiniString());
		mapChild.put(XML_PID, project.getServer().getPIDString());
		addElementWithChild(XML_SERVER, project.getIdString(), mapChild);
		addEnvironmentVariables(project, getMapIdElement(XML_SERVER).get(project.getIdString()));
	}

	/* method that add the environment variables */
	private void addEnvironmentVariables(Project project, Node node) throws JSXLockException {
		HashMap<String, String> varsMap = project.getServer().getEnvironmentVariables();
		Set<String> keys = varsMap.keySet();
		HashMap<String, String> var;
		for (String key : keys) {
			var = new HashMap<String, String>();
			var.put(XML_VAR_KEY, key);
			var.put(XML_VAR_VAL, varsMap.get(key));
			appendElementWithChild(node, XML_VAR, var);
		}
	}

	/* ################################################################################# */
	/* END PRIVATE METHODS */
	/* ################################################################################# */
}
