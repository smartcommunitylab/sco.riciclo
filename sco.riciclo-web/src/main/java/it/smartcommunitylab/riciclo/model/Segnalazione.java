package it.smartcommunitylab.riciclo.model;

import java.util.HashMap;
import java.util.Map;

public class Segnalazione extends BaseObject {

	private String objectId;
	private String area;
	private Map<String, String> tipologia = new HashMap<String, String>();
	private String email;
	
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public Map<String, String> getTipologia() {
		return tipologia;
	}
	public void setTipologia(Map<String, String> tipologia) {
		this.tipologia = tipologia;
	}
	
}
