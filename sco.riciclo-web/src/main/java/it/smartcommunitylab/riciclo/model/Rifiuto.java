package it.smartcommunitylab.riciclo.model;

import java.util.HashMap;
import java.util.Map;

public class Rifiuto extends BaseObject {
	private String objectId;
	private Map<String, String> nome = new HashMap<String, String>();
	
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public Map<String, String> getNome() {
		return nome;
	}
	public void setNome(Map<String, String> nome) {
		this.nome = nome;
	}
}
