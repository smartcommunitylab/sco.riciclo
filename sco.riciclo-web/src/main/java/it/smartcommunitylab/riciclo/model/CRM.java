package it.smartcommunitylab.riciclo.model;

import java.util.List;
import java.util.Map;

public class CRM extends BaseObject {
	private String objectId;
	private String tipologiaPuntiRaccolta;
	private Map<String, String> nome;
	private Map<String, String> indirizzo;
	private double[] geocoding;
	private Map<String, String> note;
	private Map<String, Boolean> caratteristiche;
	private List<OrarioApertura> orarioApertura;
	
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
	public Map<String, String> getIndirizzo() {
		return indirizzo;
	}
	public void setIndirizzo(Map<String, String> indirizzo) {
		this.indirizzo = indirizzo;
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
	public String getTipologiaPuntiRaccolta() {
		return tipologiaPuntiRaccolta;
	}
	public void setTipologiaPuntiRaccolta(String tipologiaPuntiRaccolta) {
		this.tipologiaPuntiRaccolta = tipologiaPuntiRaccolta;
	}
}
