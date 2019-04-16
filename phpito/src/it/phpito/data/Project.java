package it.phpito.data;

public class Project {
	private Integer id;
	private String name;
	private Server server;

	/* get e set */
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Server getServer() {
		return server;
	}
	public void setServer(Server server) {
		server.setProject(this);
		this.server = server;
	}

	public String getIdString() {
		return String.valueOf(id);
	}
	
	public String getIdAndName() {
		return id + "-" + name;
	}
}
