package it.smartcommunitylab.riciclo.model;

import java.io.Serializable;

public class RiappConf implements Serializable {
	private static final long serialVersionUID = -6322137456645316427L;
	
	private String ownerId;
	private String comune;
	private String codiceISTAT;
	
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public String getComune() {
		return comune;
	}
	public void setComune(String comune) {
		this.comune = comune;
	}
	public String getCodiceISTAT() {
		return codiceISTAT;
	}
	public void setCodiceISTAT(String codiceISTAT) {
		this.codiceISTAT = codiceISTAT;
	}

}
