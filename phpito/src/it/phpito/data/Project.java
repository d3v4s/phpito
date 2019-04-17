package it.phpito.data;

public class Project {
	private Long id;
	private String name;
	private Server server;

	/* get e set */
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
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
