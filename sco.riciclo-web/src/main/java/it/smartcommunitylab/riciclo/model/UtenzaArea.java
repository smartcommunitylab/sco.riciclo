package it.smartcommunitylab.riciclo.model;

public class UtenzaArea {

	private String area;
	private String tipologiaUtenza;

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getTipologiaUtenza() {
		return tipologiaUtenza;
	}

	public void setTipologiaUtenza(String tipologiaUtenza) {
		this.tipologiaUtenza = tipologiaUtenza;
	}

	@Override
	public String toString() {
		return "(" + area + "," + tipologiaUtenza + ")";
	}

}
