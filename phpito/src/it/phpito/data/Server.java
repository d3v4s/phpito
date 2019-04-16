package it.phpito.data;

public class Server {
	private String path;
	private String address;
	private Integer port;
	private Integer processID;
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
	public Integer getProcessID() {
		return processID;
	}
	public void setProcessId(Integer processID) {
		this.processID = processID;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}

	public String getPortString() {
		return String.valueOf(port);
	}

	/* metodo che ritorna indirizzo e porta */
	public String getAddressAndPort() {
		return address + ":" + port;
	}

	/* metodo che ritorna il process id in stringa */
	public String getPIDString() {
		return String.valueOf(processID);
	}

	/* metodo che ritorna lo stato del server (ONLINE o OFFLINE) e il PID */ 
	public String getStatePIDString() {
		return  processID == null ? "OFFLINE" : "ONLINE PID: " + String.valueOf(processID);
	}
}
