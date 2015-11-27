package it.smartcommunitylab.riciclo.model;

import java.util.HashMap;
import java.util.Map;

public class TipologiaRaccolta extends Tipologia {
	private Map<String, String> comeConferire = new HashMap<String, String>();
	private Map<String, String> prestaAttenzione = new HashMap<String, String>();
	
	public Map<String, String> getComeConferire() {
		return comeConferire;
	}
	public void setComeConferire(Map<String, String> comeConferire) {
		this.comeConferire = comeConferire;
	}
	public Map<String, String> getPrestaAttenzione() {
		return prestaAttenzione;
	}
	public void setPrestaAttenzione(Map<String, String> prestaAttenzione) {
		this.prestaAttenzione = prestaAttenzione;
	}


}
