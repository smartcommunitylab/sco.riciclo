package it.smartcommunitylab.riciclo.riapp.importer.model;

public class RiappOrario {
	private String giorno;
	private String num;
	private String mese;
	private String anno;
	private String ca;
	private String ve;
	private String or;
	private String pl;
	private String ind;
	private String al1;
	private String al2;
	private String al3;
	
	public String getGiorno() {
		return giorno;
	}
	public void setGiorno(String giorno) {
		this.giorno = giorno;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getMese() {
		return mese;
	}
	public void setMese(String mese) {
		this.mese = mese;
	}
	public String getAnno() {
		return anno;
	}
	public void setAnno(String anno) {
		this.anno = anno;
	}
	public String getCa() {
		return ca;
	}
	public void setCa(String ca) {
		this.ca = ca;
	}
	public String getVe() {
		return ve;
	}
	public void setVe(String ve) {
		this.ve = ve;
	}
	public String getOr() {
		return or;
	}
	public void setOr(String or) {
		this.or = or;
	}
	public String getPl() {
		return pl;
	}
	public void setPl(String pl) {
		this.pl = pl;
	}
	public String getInd() {
		return ind;
	}
	public void setInd(String ind) {
		this.ind = ind;
	}
	
	@Override
	public String toString() {
		return getNum() + "/" + getMese() + "/" + getAnno();
	}
	public String getAl1() {
		return al1;
	}
	public void setAl1(String al1) {
		this.al1 = al1;
	}
	public String getAl2() {
		return al2;
	}
	public void setAl2(String al2) {
		this.al2 = al2;
	}
	public String getAl3() {
		return al3;
	}
	public void setAl3(String al3) {
		this.al3 = al3;
	}
}
