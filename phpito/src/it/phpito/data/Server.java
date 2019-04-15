package it.phpito.data;

public class Server {
	private String path;
	private String address;
	private Integer port;
	private String state;
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
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}

	/* metodo che ritorna indirizzo e porta */
	public String getAddressAndPort() {
		return address + ":" + port;
	}
}
