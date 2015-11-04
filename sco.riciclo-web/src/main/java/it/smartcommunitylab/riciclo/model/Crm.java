package it.smartcommunitylab.riciclo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Crm extends BaseObject {
	private String objectId;
	private String tipologiaPuntoRaccolta;
	private String zona;
	private String dettagliZona;
	private double[] geocoding;
	private Map<String, String> note = new HashMap<String, String>();
	private Map<String, Boolean> caratteristiche = new HashMap<String, Boolean>();
	private List<OrarioApertura> orarioApertura = new ArrayList<OrarioApertura>();

	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public double[] getGeocoding() {
		return geocoding;
	}
	public void setGeocoding(double[] geocoding) {
		this.geocoding = geocoding;
	}
	public Map<String, String> getNote() {
		return note;
	}
	public void setNote(Map<String, String> note) {
		this.note = note;
	}
	public Map<String, Boolean> getCaratteristiche() {
		return caratteristiche;
	}
	public void setCaratteristiche(Map<String, Boolean> caratteristiche) {
		this.caratteristiche = caratteristiche;
	}
	public List<OrarioApertura> getOrarioApertura() {
		return orarioApertura;
	}
	public void setOrarioApertura(List<OrarioApertura> orarioApertura) {
		this.orarioApertura = orarioApertura;
	}
	public String getTipologiaPuntoRaccolta() {
		return tipologiaPuntoRaccolta;
	}
	public void setTipologiaPuntoRaccolta(String tipologiaPuntoRaccolta) {
		this.tipologiaPuntoRaccolta = tipologiaPuntoRaccolta;
	}
	public String getZona() {
		return zona;
	}
	public void setZona(String zona) {
		this.zona = zona;
	}
	public String getDettagliZona() {
		return dettagliZona;
	}
	public void setDettagliZona(String dettagliZona) {
		this.dettagliZona = dettagliZona;
	}
}
