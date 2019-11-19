package phpito.core.lock;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import exception.FileLogException;
import exception.JSXLockException;
import exception.LockLogException;
import exception.XMLException;
import jsx.JSX;
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
	private final String PATH_FILE_XML = Paths.get(PHPitoConf.DIR_CONF, "projects.xml").toString();
	private final String XML_SERVER = "server";
	private final String XML_NAME = "name";
	private final String XML_PATH = "path";
	private final String XML_ADDRESS = "address";
	private final String XML_PORT = "port";
	private final String XML_PID = "pid";
	private final String XML_LOG = "log";
	private final String XML_INI = "ini";

	/* CONSTRUCT */
	public ReentrantLockProjectsXML() {
		super();
		setFilePath(PATH_FILE_XML);
		setLock(true);
		setAutoFlush(true);
		try {
			loadDocument();
		} catch (XMLException | JSXLockException e) {
			e.printStackTrace();
			try {
				PHPitoManager.getInstance().getJoggerError().writeLog(e);
			} catch (FileLogException | LockLogException e1) {
				e1.printStackTrace();
			}
		}
	}

	/* meotodo che ritorna hashmap dei progetti con key id */
	public HashMap<String, Project> getProjectsMap() {
		PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Get Projects Map - START");
		HashMap<String, Project> mapProjects = null;
		mapProjects = new HashMap<String, Project>();
		try {
			HashMap<String, Node> mapNode = getMapIdElement(XML_SERVER);
			for (String id : mapNode.keySet()) mapProjects.put(id, getProjectByNode(mapNode.get(id), id));
		} catch (DOMException | ProjectException | JSXLockException e) {
			e.printStackTrace();
			try {
				PHPitoManager.getInstance().getJoggerError().writeLog(e);
			} catch (FileLogException | LockLogException e1) {
				e1.printStackTrace();
			}
		}
		return mapProjects;
	}

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
		if (!getArrayChildNode(node, XML_PID).isEmpty()) {
			String pid = getArrayChildNode(node, XML_PID).get(0).getTextContent();
			project.getServer().setProcessIdString(pid);
		}
		return project;
	}

	/* metodo che ritorna progetto da id */
	public Project getProject(String id) {
		PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Get Project - START");
		Project project = null;
		try {
			Node node = getMapIdElement(XML_SERVER).get(id);
			if (node == null) {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Get Project - NULL");
				return null;
			}
			project = new Project();
			project.setIdString(id);
			project.setName(getArrayChildNode(node, XML_NAME).get(0).getTextContent());
			project.setLogActiveString(getArrayChildNode(node, XML_LOG).get(0).getTextContent());
			project.setPhpiniString(getArrayChildNode(node, XML_INI).get(0).getTextContent());
			project.setServer(new Server());
			project.getServer().setAddress(getArrayChildNode(node, XML_ADDRESS).get(0).getTextContent());
			project.getServer().setPortString(getArrayChildNode(node, XML_PORT).get(0).getTextContent());
			project.getServer().setPath(getArrayChildNode(node, XML_PATH).get(0).getTextContent());
			if (!getArrayChildNode(node, XML_PID).isEmpty()) {
				String pid = getArrayChildNode(node, XML_PID).get(0).getTextContent();
				project.getServer().setProcessIdString(pid);
			}
		} catch (DOMException | ProjectException | JSXLockException e) {
			e.printStackTrace();
			try {
				PHPitoManager.getInstance().getJoggerError().writeLog(e);
			} catch (FileLogException | LockLogException e1) {
				e1.printStackTrace();
			}
		}
		return project;
	}

	/* meotodo che ritorna il prossimo id da utilizzare */
	public String getNextProjectId() {
		PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Next ID Project - START");
		String id = null;
		try {
			Set<String> setId = getMapIdElement(XML_SERVER).keySet();
			long idLong = JSX.getGreatId(setId) + 1;
			id = String.valueOf((idLong < 1L) ? 1 : idLong);
		} catch (JSXLockException e) {
			e.printStackTrace();
			try {
				PHPitoManager.getInstance().getJoggerError().writeLog(e);
			} catch (FileLogException | LockLogException e1) {
				e1.printStackTrace();
			}
		}
		return id;
	}

	/* metodo che aggiunge progetto */
	public void addProject(Project project) {
		PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Add Project - START");
		try {
			String id = getNextProjectId();
			HashMap<String, String> mapChild = new HashMap<String, String>();
			mapChild.put(XML_NAME, project.getName());
			mapChild.put(XML_PATH, project.getServer().getPath());
			mapChild.put(XML_ADDRESS, project.getServer().getAddress());
			mapChild.put(XML_PORT, project.getServer().getPortString());
			mapChild.put(XML_LOG, project.isLogActiveString());
			mapChild.put(XML_INI, project.getPhpiniString());
			mapChild.put(XML_PID, "");
			addElementWithChild(XML_SERVER, id, mapChild);
			project.setIdString(id);
		} catch (XMLException | JSXLockException | ProjectException e) {
			e.printStackTrace();
			try {
				PHPitoManager.getInstance().getJoggerError().writeLog(e);
			} catch (FileLogException | LockLogException e1) {
				e1.printStackTrace();
			}
		}
	}

	/* metodo che aggoirna progetto */
	public void updateProject(Project project) {
		PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Update Project - START");
		try {
			HashMap<String, Node> mapNode = getMapIdElement(XML_SERVER);
			Node node = mapNode.get(project.getIdString());
			getArrayChildNode(node, XML_NAME).get(0).setTextContent(project.getName());
			getArrayChildNode(node, XML_PATH).get(0).setTextContent(project.getServer().getPath());
			getArrayChildNode(node, XML_ADDRESS).get(0).setTextContent(project.getServer().getAddress());
			getArrayChildNode(node, XML_PORT).get(0).setTextContent(project.getServer().getPortString());
			getArrayChildNode(node, XML_LOG).get(0).setTextContent(project.isLogActiveString());
			getArrayChildNode(node, XML_INI).get(0).setTextContent(project.getPhpiniString());
			if (!getArrayChildNode(node, XML_PID).isEmpty())
				getArrayChildNode(node, XML_PID).get(0).setTextContent(project.getServer().getPIDString());
			else
				addChildElement(node, XML_PID, project.getServer().getPIDString());
			flush(PATH_FILE_XML, true);
		} catch (DOMException | XMLException | JSXLockException e) {
			e.printStackTrace();
			try {
				PHPitoManager.getInstance().getJoggerError().writeLog(e);
			} catch (FileLogException | LockLogException e1) {
				e1.printStackTrace();
			}
		}
	}

	/* metodo che aggoirna progetto */
	public void deleteProject(String id) {
		PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Delete Project - START");
		try {
			deleteNode(XML_SERVER, id);
		} catch (XMLException | JSXLockException e) {
			e.printStackTrace();
			try {
				PHPitoManager.getInstance().getJoggerError().writeLog(e);
			} catch (FileLogException | LockLogException e1) {
				e1.printStackTrace();
			}
		}
	}
}
