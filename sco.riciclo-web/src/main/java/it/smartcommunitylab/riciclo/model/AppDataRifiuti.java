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

package it.smartcommunitylab.riciclo.model;

import java.util.List;

public class AppDataRifiuti extends BaseObject {

	private List<TipologiaProfilo> tipologiaProfilo;
	private List<Area> aree;
	private List<Gestore> gestori;
	private List<Istituzione> istituzioni;
	private List<Riciclabolario> riciclabolario;
	private List<Rifiuto> rifiuti;
	private List<Raccolta> raccolta;
	private List<PuntoRaccolta> puntiRaccolta;
	private List<CalendarioRaccolta> calendariRaccolta;
	private List<Colore> colore;
	private List<Segnalazione> segnalazione;
	private List<Crm> crm;

	private Categorie categorie;

	public List<TipologiaProfilo> getTipologiaProfilo() {
		return tipologiaProfilo;
	}

	public void setTipologiaProfilo(List<TipologiaProfilo> profili) {
		this.tipologiaProfilo = profili;
	}

	public List<Area> getAree() {
		return aree;
	}

	public void setAree(List<Area> aree) {
		this.aree = aree;
	}

	public List<Gestore> getGestori() {
		return gestori;
	}

	public void setGestori(List<Gestore> gestori) {
		this.gestori = gestori;
	}

	public List<Istituzione> getIstituzioni() {
		return istituzioni;
	}

	public void setIstituzioni(List<Istituzione> istituzioni) {
		this.istituzioni = istituzioni;
	}

	public List<PuntoRaccolta> getPuntiRaccolta() {
		return puntiRaccolta;
	}

	public void setPuntiRaccolta(List<PuntoRaccolta> puntiRaccolta) {
		this.puntiRaccolta = puntiRaccolta;
	}

	public List<Riciclabolario> getRiciclabolario() {
		return riciclabolario;
	}

	public void setRiciclabolario(List<Riciclabolario> riciclabolario) {
		this.riciclabolario = riciclabolario;
	}

	public List<Raccolta> getRaccolta() {
		return raccolta;
	}

	public void setRaccolta(List<Raccolta> raccolta) {
		this.raccolta = raccolta;
	}

	public Categorie getCategorie() {
		return categorie;
	}

	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
	}

	public List<Colore> getColore() {
		return colore;
	}

	public void setColore(List<Colore> colore) {
		this.colore = colore;
	}

	public List<Segnalazione> getSegnalazione() {
		return segnalazione;
	}

	public void setSegnalazione(List<Segnalazione> segnalazione) {
		this.segnalazione = segnalazione;
	}

	public List<Rifiuto> getRifiuti() {
		return rifiuti;
	}

	public void setRifiuti(List<Rifiuto> rifiuti) {
		this.rifiuti = rifiuti;
	}

	public List<CalendarioRaccolta> getCalendariRaccolta() {
		return calendariRaccolta;
	}

	public void setCalendariRaccolta(List<CalendarioRaccolta> calendariRaccolta) {
		this.calendariRaccolta = calendariRaccolta;
	}

	public List<Crm> getCrm() {
		return crm;
	}

	public void setCrm(List<Crm> crm) {
		this.crm = crm;
	}

}
