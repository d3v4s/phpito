package it.phpito.controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

import org.w3c.dom.Node;

import it.as.utils.core.XMLManagerAS;
import it.as.utils.exception.FileException;
import it.phpito.data.Project;
import it.phpito.data.Server;

public class PHPitoManager {
	private static PHPitoManager phpItoManager;
	private final String PATH_FILE_XML = Paths.get("conf", "server.xml").toString();
	private final String XML_SERVER = "server";
	private final String XML_NAME = "name";
	private final String XML_PATH = "path";
	private final String XML_ADDRESS = "address";
	private final String XML_PORT = "port";
	private final String CMND_PHP = "php";
	private final String CMND_PHP_OPT_SERVER = "-S ";
	private final String CMND_PHP_OPT_DIR_ROOT = "-t ";
	public static final String NAME = "PHPito";

	private PHPitoManager() {
	}

	/* singleton */
	public static PHPitoManager getInstance() {
		phpItoManager = (phpItoManager == null) ? new PHPitoManager() : phpItoManager;
		return phpItoManager;
	}

	public HashMap<String, Project> getProjectsMap() throws FileException {
		HashMap<String, Project> mapProjects = new HashMap<String, Project>();
		Project p;
		Server s;
		Node n;
		HashMap<String, Node> mapNode = XMLManagerAS.getInstance().getMapIdElement(PATH_FILE_XML, XML_SERVER);
		XMLManagerAS xmlAS = XMLManagerAS.getInstance(); 
		for (String id : mapNode.keySet()) {
			n = mapNode.get(id);
			p = new Project();
			s = new Server();
			p.setName(xmlAS.getArrayChildNode(n, XML_NAME).get(0).getTextContent());
			s.setAddress(xmlAS.getArrayChildNode(n, XML_ADDRESS).get(0).getTextContent());
			s.setPort(Integer.parseInt(xmlAS.getArrayChildNode(n, XML_PORT).get(0).getTextContent()));
			s.setPath(xmlAS.getArrayChildNode(n, XML_PATH).get(0).getTextContent());
			p.setServer(s);
			
			mapProjects.put(id, p);
		}
		
		return mapProjects;
	}
	
	public Project getProjectById(Integer id) throws FileException {
		return getProjectsMap().get(String.valueOf(id));
//		HashMap<String, Project>
	}
	
	public void runServer(Server s) throws IOException {
		ProcessBuilder pb = new ProcessBuilder(new String[]{CMND_PHP, CMND_PHP_OPT_SERVER, s.getAddressAndPort(), CMND_PHP_OPT_DIR_ROOT, s.getPath()});
//		pb.directory(directory);  TODO
		pb.redirectErrorStream(true);
		Process prs = pb.start();
//		prs.getOutputStream()
		System.out.println(prs.getOutputStream());
		
	}

}
