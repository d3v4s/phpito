package it.phpito.core.lock;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import it.jogger.exception.FileLogException;
import it.jogger.exception.LockLogException;
import it.jsx.core.JSX;
import it.jsx.exception.JSXLockException;
import it.jsx.exception.XMLException;
import it.phpito.core.PHPitoConf;
import it.phpito.core.PHPitoManager;
import it.phpito.data.Project;
import it.phpito.data.Server;
import it.phpito.exception.ProjectException;

public class ReentrantLockXMLServer extends JSX {
	private final String PATH_FILE_XML = Paths.get(PHPitoConf.DIR_CONF, "server.xml").toString();
	private final String XML_SERVER = "server";
	private final String XML_NAME = "name";
	private final String XML_PATH = "path";
	private final String XML_ADDRESS = "address";
	private final String XML_PORT = "port";
	private final String XML_PID = "pid";
	private final String XML_LOG = "log";

	public ReentrantLockXMLServer() {
		super();
		setPathFile(PATH_FILE_XML);
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
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Get Projects Map");
			} catch (FileLogException | LockLogException e) {
				e.printStackTrace();
			}
		HashMap<String, Project> mapProjects = new HashMap<String, Project>();
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Get Projects Map -- LOCK OK");
			} catch (FileLogException | LockLogException e) {
				e.printStackTrace();
			}
		try {
			Project project;
			Node node;
			HashMap<String, Node> mapNode = getMapIdElement(XML_SERVER);
			for (String id : mapNode.keySet()) {
				node = mapNode.get(id);
				project = new Project();
				project.setIdString(id);
				project.setName(getArrayChildNode(node, XML_NAME).get(0).getTextContent());
				project.setLogActiveString(getArrayChildNode(node, XML_LOG).get(0).getTextContent());
				project.setServer(new Server());
				project.getServer().setAddress(getArrayChildNode(node, XML_ADDRESS).get(0).getTextContent());
				project.getServer().setPortString(getArrayChildNode(node, XML_PORT).get(0).getTextContent());
				project.getServer().setPath(getArrayChildNode(node, XML_PATH).get(0).getTextContent());
				if (!getArrayChildNode(node, XML_PID).isEmpty()) {
					String pid = getArrayChildNode(node, XML_PID).get(0).getTextContent();
					project.getServer().setProcessIdString(pid);
				}
				mapProjects.put(id, project);
			}
			
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

	/* metodo che ritorna progetto da id */
	public Project getProject(String id) {
		Project project = null;
		try {
			Node node = getMapIdElement(XML_SERVER).get(id);
			if (node == null)
				return null;
			project = new Project();
			project.setIdString(id);
			project.setName(getArrayChildNode(node, XML_NAME).get(0).getTextContent());
			project.setLogActiveString(getArrayChildNode(node, XML_LOG).get(0).getTextContent());
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

	/* meotodo che ritorna il rpssimpo id da utilizzare */
	public String getNextProjectId() {
		String id = null;
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Next ID Project");
			} catch (FileLogException | LockLogException e1) {
				e1.printStackTrace();
			}
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Next ID Project -- LOCK OK");
			} catch (FileLogException | LockLogException e1) {
				e1.printStackTrace();
			}
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
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Add Project");
			} catch (FileLogException | LockLogException e1) {
				e1.printStackTrace();
			}
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Add Project -- LOCK OK");
			} catch (FileLogException | LockLogException e1) {
				e1.printStackTrace();
			}
		try {
			HashMap<String, String> mapChild = new HashMap<String, String>();
			mapChild.put(XML_NAME, project.getName());
			mapChild.put(XML_PATH, project.getServer().getPath());
			mapChild.put(XML_ADDRESS, project.getServer().getAddress());
			mapChild.put(XML_PORT, project.getServer().getPortString());
			mapChild.put(XML_LOG, project.isLogActiveString());
			mapChild.put(XML_PID, "");
			addElementWithChild(XML_SERVER, getNextProjectId(), mapChild);
		} catch (XMLException | JSXLockException e) {
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
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Update Project -- LOCK OK");
			} catch (FileLogException | LockLogException e1) {
				e1.printStackTrace();
			}
		try {
			HashMap<String, Node> mapNode = getMapIdElement(XML_SERVER);
			Node node = mapNode.get(project.getIdString());
			getArrayChildNode(node, XML_NAME).get(0).setTextContent(project.getName());
			getArrayChildNode(node, XML_PATH).get(0).setTextContent(project.getServer().getPath());
			getArrayChildNode(node, XML_ADDRESS).get(0).setTextContent(project.getServer().getAddress());
			getArrayChildNode(node, XML_PORT).get(0).setTextContent(project.getServer().getPortString());
			getArrayChildNode(node, XML_LOG).get(0).setTextContent(project.isLogActiveString());
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
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Delete Project");
			} catch (FileLogException | LockLogException e1) {
				e1.printStackTrace();
			}
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
