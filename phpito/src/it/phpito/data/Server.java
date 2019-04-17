package it.phpito.data;

import java.util.regex.Pattern;

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
	public void setPath(String path) {
		this.path = path;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
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

	/* metodo che ritorna lo stato del server (ONLINE o OFFLINE) e il PID */ 
	public String getStatePIDString() {
		return  processID == null ? "OFFLINE" : "ONLINE PID: " + String.valueOf(processID);
	}
}
