package it.smartcommunitylab.riciclo.app.importer.model;

import org.hibernate.validator.constraints.NotEmpty;

public class Segnalazioni {

	@NotEmpty
	private String area;
	@NotEmpty
	private String tipologia;
	@NotEmpty
	private String email;
	
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getTipologia() {
		return tipologia;
	}
	public void setTipologia(String tipologia) {
		this.tipologia = tipologia;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
