package it.phpito.data;

import java.util.HashMap;

public class Project {
	private Long id;
	private String name;
	private Server server;
	public static final String K_ID = "id";
	public static final String K_NAME = "name";
	public static final String K_PATH = "path";
	public static final String K_ADDRESS = "address";
	public static final String K_PORT = "port";

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
		if (id == null)
			return null;
		return String.valueOf(id);
	}
	
	public String getIdAndName() {
		return String.format("%04d", id) + "-" + name;
	}
	
	/* metodo che ritorna key per hashmap progetto */
	public static String[] getArrayKeyProject() {
		return new String[]{K_ID, K_NAME, K_PATH, K_ADDRESS, K_PORT};
	}
	
	/* metodo che ritorna key per hashmap progetto */
	public static String[] getArrayKeyProjectNoId() {
		return new String[]{K_NAME, K_PATH, K_ADDRESS, K_PORT};
	}
	
	public HashMap<String, String> getHashMap() {
		HashMap<String, String> projectMap = new HashMap<String, String>();
		projectMap.put(K_ID, getIdString());
		projectMap.put(K_NAME, name);
		projectMap.put(K_PATH, server.getPath());
		projectMap.put(K_ADDRESS, server.getAddress());
		projectMap.put(K_PORT, server.getPortString());
		return projectMap;
	}

	@Override
	public String toString() {
		String ret = (id != null ? "Id:\t\t\t" + getIdString() + "\n" : "").concat(
					"Nome:\t\t" + name + "\n"
					+ "Path:\t\t" + server.getPath() + "\n"
					+ "Indirizzo:\t" + server.getAddressAndPort());
		return ret;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().equals(this.getClass()))
			return false;
		Project p = (Project) obj;
		return this.toString().equals(p.toString());
	}
	public Project clone() {
		Project clone = new Project();
		clone.setId(new Long(id));
		clone.setName(new String(name));
		clone.setServer(server.clone());
		return clone;
	}
}
