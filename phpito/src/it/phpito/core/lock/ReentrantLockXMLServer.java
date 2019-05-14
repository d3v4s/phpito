package it.phpito.core.lock;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

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
	private final ReentrantLock reentrantLock = new ReentrantLock();
	private final String XML_SERVER = "server";
	private final String XML_NAME = "name";
	private final String XML_PATH = "path";
	private final String XML_ADDRESS = "address";
	private final String XML_PORT = "port";
	private final String XML_PID = "pid";
	private final String XML_LOG = "log";
	private final String XML_INI = "ini";

	public ReentrantLockXMLServer() {
		super();
		setPathFile(PATH_FILE_XML);
//		setLock(true);
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
				PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Get Projects Map - START");
			} catch (FileLogException | LockLogException e) {
				e.printStackTrace();
			}

		HashMap<String, Project> mapProjects = null;
		try {
			if (reentrantLock.tryLock(30, TimeUnit.SECONDS)) {
				mapProjects = new HashMap<String, Project>();
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
						project.setPhpiniString(getArrayChildNode(node, XML_INI).get(0).getTextContent());
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
				} finally {
					if (PHPitoManager.getInstance().isDebug())
						try {
							PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Get Projects Map - OK - UNLOCK");
						} catch (FileLogException | LockLogException e) {
							e.printStackTrace();
						}

					reentrantLock.unlock();
				}
			}
		} catch (InterruptedException e) {
		}
		return mapProjects;
	}

	// TODO
//	private Project getProjectByNode(Node node) {
//	}

	/* metodo che ritorna progetto da id */
	public Project getProject(String id) {
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Get Project - START");
			} catch (FileLogException | LockLogException e) {
				e.printStackTrace();
			}

		Project project = null;
		try {
			if (reentrantLock.tryLock(30, TimeUnit.SECONDS)) {
				try {
					Node node = getMapIdElement(XML_SERVER).get(id);
					if (node == null) {
						
						if (PHPitoManager.getInstance().isDebug()) {
							try {
								PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Get Project - NULL");
							} catch (FileLogException | LockLogException e) {
								e.printStackTrace();
							}
						}
						
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
				} finally {
					if (PHPitoManager.getInstance().isDebug())
						try {
							PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Get Project - OK - UNLOCK");
						} catch (FileLogException | LockLogException e) {
							e.printStackTrace();
						}

					reentrantLock.unlock();
				}
				
			}
		} catch (InterruptedException e) {
		}
		return project;
	}

	/* meotodo che ritorna il rpssimpo id da utilizzare */
	public String getNextProjectId() {
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Next ID Project - START");
			} catch (FileLogException | LockLogException e1) {
				e1.printStackTrace();
			}

		String id = null;
		try {
			if (reentrantLock.tryLock(30, TimeUnit.SECONDS)) {
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
				} finally {
					if (PHPitoManager.getInstance().isDebug())
						try {
							PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Next ID Project - OK - UNLOCK");
						} catch (FileLogException | LockLogException e1) {
							e1.printStackTrace();
						}

					reentrantLock.unlock();
				}
			}
		} catch (InterruptedException e) {
		}

		return id;
	}

	/* metodo che aggiunge progetto */
	public void addProject(Project project) {
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Add Project - START");
			} catch (FileLogException | LockLogException e1) {
				e1.printStackTrace();
			}

		try {
			if (reentrantLock.tryLock(30, TimeUnit.SECONDS)) {
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
				} finally {
					if (PHPitoManager.getInstance().isDebug())
						try {
							PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Add Project - OK - UNLOCK");
						} catch (FileLogException | LockLogException e1) {
							e1.printStackTrace();
						}

					reentrantLock.unlock();
				}
			}
		} catch (InterruptedException e) {
		}
	}

	/* metodo che aggoirna progetto */
	public void updateProject(Project project) {
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Update Project - START");
			} catch (FileLogException | LockLogException e1) {
				e1.printStackTrace();
			}

		try {
			if (reentrantLock.tryLock(30, TimeUnit.SECONDS)) {
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
				} finally {
					if (PHPitoManager.getInstance().isDebug())
						try {
							PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Update Project - OK - UNLOCK");
						} catch (FileLogException | LockLogException e1) {
							e1.printStackTrace();
						}

					reentrantLock.unlock();
				}
			}
		} catch (InterruptedException e) {
		}
	}

	/* metodo che aggoirna progetto */
	public void deleteProject(String id) {
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Delete Project - START");
			} catch (FileLogException | LockLogException e1) {
				e1.printStackTrace();
			}

		try {
			if (reentrantLock.tryLock(30, TimeUnit.SECONDS)) {
				try {
					deleteNode(XML_SERVER, id);
				} catch (XMLException | JSXLockException e) {
					e.printStackTrace();
					try {
						PHPitoManager.getInstance().getJoggerError().writeLog(e);
					} catch (FileLogException | LockLogException e1) {
						e1.printStackTrace();
					}
				} finally {
					if (PHPitoManager.getInstance().isDebug())
						try {
							PHPitoManager.getInstance().getJoggerDebug().writeLog("XML Delete Project - OK - UNLOCK");
						} catch (FileLogException | LockLogException e1) {
							e1.printStackTrace();
						}

					reentrantLock.unlock();
				}
			}
		} catch (InterruptedException e) {
		}
	}
}
