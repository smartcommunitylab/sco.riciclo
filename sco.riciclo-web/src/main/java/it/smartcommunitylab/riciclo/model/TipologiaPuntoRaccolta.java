package it.smartcommunitylab.riciclo.model;

import java.util.HashMap;
import java.util.Map;

public class TipologiaPuntoRaccolta extends BaseObject {
	private String objectId;
	private Map<String, String> nome = new HashMap<String, String>();
	private Map<String, String> info = new HashMap<String, String>();
	private String type;
	
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
	public Map<String, String> getInfo() {
		return info;
	}
	public void setInfo(Map<String, String> info) {
		this.info = info;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
