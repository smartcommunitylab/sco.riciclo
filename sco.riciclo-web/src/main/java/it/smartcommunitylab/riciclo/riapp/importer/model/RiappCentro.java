package it.smartcommunitylab.riciclo.riapp.importer.model;

import java.util.HashMap;
import java.util.Map;

public class RiappCentro {
	private String nome;
	private String accesso;
	private String indirizzo;
	private double latitudine;
	private double longitudine;
	private Map<String, String> orario = new HashMap<String, String>();
	private String cosa;
	private String info;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getAccesso() {
		return accesso;
	}
	public void setAccesso(String accesso) {
		this.accesso = accesso;
	}
	public String getIndirizzo() {
		return indirizzo;
	}
	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}
	public double getLatitudine() {
		return latitudine;
	}
	public void setLatitudine(double latitudine) {
		this.latitudine = latitudine;
	}
	public double getLongitudine() {
		return longitudine;
	}
	public void setLongitudine(double longitudine) {
		this.longitudine = longitudine;
	}
	public Map<String, String> getOrario() {
		return orario;
	}
	public void setOrario(Map<String, String> orario) {
		this.orario = orario;
	}
	public String getCosa() {
		return cosa;
	}
	public void setCosa(String cosa) {
		this.cosa = cosa;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
}
