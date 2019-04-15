package it.phpito.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.HashMap;

import org.w3c.dom.Node;

import it.as.utils.core.UtilsAS;
import it.as.utils.core.XMLManagerAS;
import it.as.utils.exception.FileException;
import it.phpito.data.Project;
import it.phpito.data.Server;

public class PHPitoManager {
	private static PHPitoManager phpItoManager;
	private final String DIR_LOG_SERVER = Paths.get(UtilsAS.getInstance().getLogDirPath(), "server").toString();
	private final String DIR_SCRIPT = Paths.get(UtilsAS.getInstance().getRunPath(), "script").toString();
	private final String SCRIPT_START_SERVER = "start-server".concat(
										(UtilsAS.getInstance().getOsName().contains("win")) ? ".bat" : ".sh");
	private final String PATH_FILE_XML = Paths.get("conf", "server.xml").toString();
	private final String FILE_LOG = "log_server-";
	private final String FILE_TYPE = ".log";
	private final Long MAX_SIZE_BYTE = 102400L;
	private final String XML_SERVER = "server";
	private final String XML_NAME = "name";
	private final String XML_PATH = "path";
	private final String XML_ADDRESS = "address";
	private final String XML_PORT = "port";
	public static final String NAME = "PHPito";
	public boolean stop = false;

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
		Node n;
		HashMap<String, Node> mapNode = XMLManagerAS.getInstance().getMapIdElement(PATH_FILE_XML, XML_SERVER);
		XMLManagerAS xmlAS = XMLManagerAS.getInstance(); 
		for (String id : mapNode.keySet()) {
			n = mapNode.get(id);
			p = new Project();
			p.setName(xmlAS.getArrayChildNode(n, XML_NAME).get(0).getTextContent());
			p.setServer(new Server());
			p.getServer().setAddress(xmlAS.getArrayChildNode(n, XML_ADDRESS).get(0).getTextContent());
			p.getServer().setPort(Integer.parseInt(xmlAS.getArrayChildNode(n, XML_PORT).get(0).getTextContent()));
			p.getServer().setPath(xmlAS.getArrayChildNode(n, XML_PATH).get(0).getTextContent());
			
			mapProjects.put(id, p);
		}
		
		return mapProjects;
	}
	
	public Project getProjectById(Integer id) throws FileException {
		return getProjectsMap().get(String.valueOf(id));
//		HashMap<String, Project>
	}
	
	public String getPathFileLog(String nameProject) throws IOException {
		String nameFileLog = (FILE_LOG + nameProject + "-000000" + FILE_TYPE).toLowerCase().replace(" ", "_");
		File dirLog = Paths.get(DIR_LOG_SERVER, nameFileLog).toFile();
		File fileLog = Paths.get(dirLog.getAbsolutePath(), nameFileLog).toFile();
		if (!dirLog.exists()) {
			dirLog.mkdirs();
			fileLog.createNewFile();
		} else if (!fileLog.exists()) {
			fileLog.createNewFile();
		}

		return fileLog.getAbsolutePath();
	}
	
	public void runServer(Project project) throws IOException {
		String run = (UtilsAS.getInstance().getOsName().contains("win")) ? "" : "./";
		String[] cmnd = new String[] {run + SCRIPT_START_SERVER,
											project.getServer().getAddressAndPort(),
											project.getServer().getPath()};
		ProcessBuilder pb = new ProcessBuilder(cmnd);
		
		pb.directory(Paths.get(DIR_SCRIPT).toFile());
		pb.redirectErrorStream(true);
		Process prs = pb.start();
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				InputStreamReader isr = new InputStreamReader(prs.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				try {
					while ((line = br.readLine()) != null) {
						System.out.println(line);
						if (stop)
							break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
		
//		Thread t2 = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				OutputStreamWriter osw = new OutputStreamWriter(prs.getOutputStream());
//				BufferedWriter bw = new BufferedWriter(osw);
//				while (true) {
//					if (stop) {
//						try {
//							System.out.println("PHPitoManager.runServer(...).new Runnable() {...}.run() STOPPEDDDDD");
//							bw.write(3);
//							break;
//						} catch (IOException e) {
//							e.printStackTrace();
//							break;
//						}
//					}
//				}
//			}
//		});
//		t2.start();
		
		

	}

}
