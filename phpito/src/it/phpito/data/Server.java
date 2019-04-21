package it.phpito.data;

import java.io.IOException;
import java.util.regex.Pattern;

import it.phpito.controller.PHPitoManager;
import it.phpito.exception.ProjectException;

public class Server {
	private String path;
	private String address;
	private Integer port;
	private Long processID;
	private Project project;

	/* get e set */
	public String getPath() {
		return path;
	}
	public void setPath(String path) throws ProjectException {
		if (path == null || path.isEmpty())
			throw new ProjectException("Errore nella formatazzione della path");
		this.path = path;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) throws ProjectException {
		if (address == null || address.isEmpty() || !Pattern.matches("[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}", address))
			throw new ProjectException("Errore nella formatazzione dell'indirizzo");
		this.address = address;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) throws ProjectException {
		if (port == null)
			throw new ProjectException("Errore nella formatazzione della porta");
		this.port = port;
	}
	public Long getProcessID() {
		return processID;
	}
	public void setProcessId(Long processID) {
		this.processID = processID;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	
	/* metodo che setta porta da String */
	public void setPortString(String port) throws ProjectException {
		if (port == null || port.isEmpty() || !Pattern.matches("[\\d]{1,}", port))
			throw new ProjectException("Errore nella formatazzione della porta");
		this.port = Integer.valueOf(port);
	}

	/* metodo che ritorna porta String */
	public String getPortString() {
		return String.valueOf(port);
	}

	/* metodo che setta il id processo da String */  
	public void setProcessIdString(String processID) {
		this.processID =  !(processID.isEmpty() || processID == null || !Pattern.matches("[\\d]{1,}", processID)) ?
													Long.valueOf(processID) : null;
	}

	/* metodo che ritorna indirizzo e porta String */
	public String getAddressAndPort() {
		return address + ":" + port;
	}
	
	/* metodo che ritorna indirizzo e porta per regex */
	public String getAddressAndPortRegex() {
		return getAddressAndPort().replace(".", "\\.");
	}

	/* metodo che ritorna il process id in stringa */
	public String getPIDString() {
		return processID != null ? String.valueOf(processID) : "";
	}
	
	public boolean isRunnig() throws IOException {
		return PHPitoManager.getInstance().isServerRunning(this);
	}

	/* metodo che ritorna lo stato del server (ONLINE o OFFLINE) e il PID */ 
	public String getStatePIDString() {
		return  processID == null ? "OFFLINE" : "ONLINE PID: " + String.valueOf(processID);
	}
}
