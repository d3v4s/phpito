package it.phpito.controller.lock;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import it.as.utils.core.LogErrorAS;
import it.as.utils.core.XMLManagerAS;
import it.as.utils.exception.FileException;
import it.phpito.controller.PHPitoConf;
import it.phpito.controller.PHPitoManager;
import it.phpito.data.Project;
import it.phpito.data.Server;
import it.phpito.exception.ProjectException;

public class ReentrantLockXMLServer {
	private ReentrantLock reentrantLock = new ReentrantLock();
	private final String PATH_FILE_XML = Paths.get(PHPitoConf.DIR_CONF, "server.xml").toString();
	private final String XML_SERVER = "server";
	private final String XML_NAME = "name";
	private final String XML_PATH = "path";
	private final String XML_ADDRESS = "address";
	private final String XML_PORT = "port";
	private final String XML_PID = "pid";
	private final String XML_LOG = "log";

	/* meotodo che ritorna hashmap dei progetti con key id */
	public HashMap<String, Project> getProjectsMap() {
		HashMap<String, Project> mapProjects = new HashMap<String, Project>();
		try {
			if (reentrantLock.tryLock(30, TimeUnit.SECONDS)) {
				try {
					Project project;
					Node node;
					String pid;
					XMLManagerAS xmlAS = XMLManagerAS.getInstance();
					HashMap<String, Node> mapNode = xmlAS.getMapIdElement(PATH_FILE_XML, XML_SERVER);
					for (String id : mapNode.keySet()) {
						node = mapNode.get(id);
						project = new Project();
						project.setIdString(id);
						project.setName(xmlAS.getArrayChildNode(node, XML_NAME).get(0).getTextContent());
						project.setLogActiveString(xmlAS.getArrayChildNode(node, XML_LOG).get(0).getTextContent());
						project.setServer(new Server());
						project.getServer().setAddress(xmlAS.getArrayChildNode(node, XML_ADDRESS).get(0).getTextContent());
						project.getServer().setPortString(xmlAS.getArrayChildNode(node, XML_PORT).get(0).getTextContent());
						project.getServer().setPath(xmlAS.getArrayChildNode(node, XML_PATH).get(0).getTextContent());
						if (!xmlAS.getArrayChildNode(node, XML_PID).isEmpty()) {
							pid = xmlAS.getArrayChildNode(node, XML_PID).get(0).getTextContent();
							project.getServer().setProcessIdString(pid);
						}
						mapProjects.put(id, project);
					}
					
				} catch (DOMException | ProjectException | FileException e) {
					try {
						LogErrorAS.getInstance().writeLog(e, PHPitoManager.NAME);
					} catch (FileException e1) {
						e1.printStackTrace();
					}
				} finally {
					reentrantLock.unlock();
				}
			}
		} catch (InterruptedException e) {
		}
		return mapProjects;
	}

	/* meotodo che ritorna il rpssimpo id da utilizzare */
	public String getNextProjectId() {
		String id = null;
		try {
			if (reentrantLock.tryLock(30, TimeUnit.SECONDS)) {
				try {
					XMLManagerAS xmlAS = XMLManagerAS.getInstance();
					Set<String> setId = xmlAS.getMapIdElement(PATH_FILE_XML, "server").keySet();
					long idLong = xmlAS.getGreatId(setId) + 1;
					id = String.valueOf((idLong < 1L) ? 1 : idLong);
				} catch (FileException e) {
					try {
						LogErrorAS.getInstance().writeLog(e, PHPitoManager.NAME);
					} catch (FileException e1) {
						e1.printStackTrace();
					}
				} finally {
					reentrantLock.unlock();
				}
			}
		} catch (InterruptedException e) {
		}
		return id;
	}

	/* metodo che aggiunge progetto */
	public void addProject(Project project) {
		try {
			if (reentrantLock.tryLock(30, TimeUnit.SECONDS)) {
				try {
					XMLManagerAS xmlAS = XMLManagerAS.getInstance();
					HashMap<String, String> mapChild = new HashMap<String, String>();
					mapChild.put(XML_NAME, project.getName());
					mapChild.put(XML_PATH, project.getServer().getPath());
					mapChild.put(XML_ADDRESS, project.getServer().getAddress());
					mapChild.put(XML_PORT, project.getServer().getPortString());
					mapChild.put(XML_LOG, project.isLogActiveString());
					mapChild.put(XML_PID, "");
					xmlAS.addElementWithChild(PATH_FILE_XML, "server", getNextProjectId(), mapChild);
				} catch (FileException e) {
					try {
						LogErrorAS.getInstance().writeLog(e, PHPitoManager.NAME);
					} catch (FileException e1) {
						e1.printStackTrace();
					}
				} finally {
					reentrantLock.unlock();
				}
			}
		} catch (InterruptedException e) {
		}
	}

	/* metodo che aggoirna progetto */
	public void updateProject(Project project) {
		try {
			if (reentrantLock.tryLock(30, TimeUnit.SECONDS)) {
				try {
					XMLManagerAS xmlAS = XMLManagerAS.getInstance();
					HashMap<String, Node> mapNode = xmlAS.getMapIdElement(PATH_FILE_XML, XML_SERVER);
					Node node = mapNode.get(project.getIdString());
					xmlAS.getArrayChildNode(node, XML_NAME).get(0).setTextContent(project.getName());
					xmlAS.getArrayChildNode(node, XML_PATH).get(0).setTextContent(project.getServer().getPath());
					xmlAS.getArrayChildNode(node, XML_ADDRESS).get(0).setTextContent(project.getServer().getAddress());
					xmlAS.getArrayChildNode(node, XML_PORT).get(0).setTextContent(project.getServer().getPortString());
					xmlAS.getArrayChildNode(node, XML_LOG).get(0).setTextContent(project.isLogActiveString());
					if (!xmlAS.getArrayChildNode(node, XML_PID).isEmpty())
						xmlAS.getArrayChildNode(node, XML_PID).get(0).setTextContent(project.getServer().getPIDString());
					else
						xmlAS.addChildElement(XML_PID, project.getServer().getPIDString(), node);
					xmlAS.flush(node.getOwnerDocument(), PATH_FILE_XML);
				} catch (DOMException | FileException e) {
					try {
						LogErrorAS.getInstance().writeLog(e, PHPitoManager.NAME);
					} catch (FileException e1) {
						e1.printStackTrace();
					}
				} finally {
					reentrantLock.unlock();
				}
			}
		} catch (InterruptedException e) {
		}
	}

	/* metodo che aggoirna progetto */
	public void deleteProject(String id) {
		try {
			if (reentrantLock.tryLock(30, TimeUnit.SECONDS))
				try {
					XMLManagerAS.getInstance().deleteNode(PATH_FILE_XML, XML_SERVER, id, true);
				} catch (FileException e) {
					try {
						LogErrorAS.getInstance().writeLog(e, PHPitoManager.NAME);
					} catch (FileException e1) {
						e1.printStackTrace();
					}
				} finally {
					reentrantLock.unlock();
				}
		} catch (InterruptedException e) {
		}
	}
}
