/**
 *    Copyright 2015 Fondazione Bruno Kessler - Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package it.smartcommunitylab.riciclo.app.importer.model;


public class PuntiRaccolta {
	
	private String area;
	private String tipologiaPuntiRaccolta;
	private String tipologiaUtenza;
	private String localizzazione;
	private String indirizzo;
	private String dettaglioIndirizzo;
	private String dataDa;
	private String dataA;
	private String il;
	private String dalle;
	private String alle;

	private String gettoniera;
	private String residuo;
	private String imbCarta;
	private String imbPlMet;
	private String organico;
	private String imbVetro;
	private String indumenti;
	private String note;
	
	private String eccezione;
	
	
	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getTipologiaPuntiRaccolta() {
		return tipologiaPuntiRaccolta;
	}

	public void setTipologiaPuntiRaccolta(String tipologiaPuntiRaccolta) {
		this.tipologiaPuntiRaccolta = tipologiaPuntiRaccolta;
	}

	public String getTipologiaUtenza() {
		return tipologiaUtenza;
	}

	public void setTipologiaUtenza(String tipologiaUtenza) {
		this.tipologiaUtenza = tipologiaUtenza;
	}

	public String getLocalizzazione() {
		return localizzazione;
	}

	public void setLocalizzazione(String localizzazione) {
		this.localizzazione = localizzazione;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public String getDettaglioIndirizzo() {
		return dettaglioIndirizzo;
	}

	public void setDettaglioIndirizzo(String dettaglioIndirizzo) {
		this.dettaglioIndirizzo = dettaglioIndirizzo;
	}

	public String getDataDa() {
		return dataDa;
	}

	public void setDataDa(String dataDa) {
		this.dataDa = dataDa;
	}

	public String getDataA() {
		return dataA;
	}

	public void setDataA(String dataA) {
		this.dataA = dataA;
	}

	public String getIl() {
		return il;
	}

	public void setIl(String il) {
		this.il = il;
	}

	public String getDalle() {
		return dalle;
	}

	public void setDalle(String dalle) {
		this.dalle = dalle;
	}

	public String getAlle() {
		return alle;
	}

	public void setAlle(String alle) {
		this.alle = alle;
	}

	public String getGettoniera() {
		return gettoniera;
	}

	public void setGettoniera(String gettoniera) {
		this.gettoniera = gettoniera;
	}

	public String getResiduo() {
		return residuo;
	}

	public void setResiduo(String residuo) {
		this.residuo = residuo;
	}

	public String getImbCarta() {
		return imbCarta;
	}

	public void setImbCarta(String imbCarta) {
		this.imbCarta = imbCarta;
	}

	public String getImbPlMet() {
		return imbPlMet;
	}

	public void setImbPlMet(String imbPlMet) {
		this.imbPlMet = imbPlMet;
	}

	public String getOrganico() {
		return organico;
	}

	public void setOrganico(String organico) {
		this.organico = organico;
	}

	public String getImbVetro() {
		return imbVetro;
	}

	public void setImbVetro(String imbVetro) {
		this.imbVetro = imbVetro;
	}

	public String getIndumenti() {
		return indumenti;
	}

	public void setIndumenti(String indumenti) {
		this.indumenti = indumenti;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getEccezione() {
		return eccezione;
	}

	public void setEccezione(String eccezione) {
		this.eccezione = eccezione;
	}

	@Override
	public String toString() {
		return "PuntiRaccolta [dettaglioIndirizzo=" + dettaglioIndirizzo + "]";
	}

	
	
}
