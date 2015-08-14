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

import java.util.List;

import javax.validation.Valid;

public class Rifiuti {

	@Valid
	private List<Aree> aree;
	@Valid
	private List<Gestori> gestori;
	@Valid
	private List<Istituzioni> istituzioni;
	@Valid
	private List<TipologiaProfilo> tipologiaProfilo;
	@Valid
	private List<PuntiRaccolta> puntiRaccolta;
	@Valid
	private List<TipologiaRifiuto> tipologiaRifiuto;
	@Valid
	private List<Riciclabolario> riciclabolario;
	@Valid
	private List<Raccolte> raccolte;
	@Valid
	private List<TipologiaPuntoRaccolta> tipologiaPuntoRaccolta;
	@Valid
	private List<TipologiaRaccolta> tipologiaRaccolta;
	@Valid
	private List<TipologiaUtenza> tipologiaUtenza;
	@Valid
	private List<Colori> colori;
	@Valid
	private List<Segnalazioni> segnalazioni;

	public List<Aree> getAree() {
		return aree;
	}

	public void setAree(List<Aree> aree) {
		this.aree = aree;
	}

	public List<Gestori> getGestori() {
		return gestori;
	}

	public void setGestori(List<Gestori> gestori) {
		this.gestori = gestori;
	}

	public List<Istituzioni> getIstituzioni() {
		return istituzioni;
	}

	public void setIstituzioni(List<Istituzioni> istituzioni) {
		this.istituzioni = istituzioni;
	}

	public List<TipologiaProfilo> getTipologiaProfilo() {
		return tipologiaProfilo;
	}

	public void setTipologiaProfilo(List<TipologiaProfilo> profili) {
		this.tipologiaProfilo = profili;
	}

	public List<PuntiRaccolta> getPuntiRaccolta() {
		return puntiRaccolta;
	}

	public void setPuntiRaccolta(List<PuntiRaccolta> puntiRaccolta) {
		this.puntiRaccolta = puntiRaccolta;
	}

	public List<TipologiaRifiuto> getTipologiaRifiuto() {
		return tipologiaRifiuto;
	}

	public void setTipologiaRifiuto(List<TipologiaRifiuto> tipologiaRifiuto) {
		this.tipologiaRifiuto = tipologiaRifiuto;
	}

	public List<Riciclabolario> getRiciclabolario() {
		return riciclabolario;
	}

	public void setRiciclabolario(List<Riciclabolario> riciclabolario) {
		this.riciclabolario = riciclabolario;
	}

	public List<Raccolte> getRaccolte() {
		return raccolte;
	}

	public void setRaccolte(List<Raccolte> raccolta) {
		this.raccolte = raccolta;
	}

	public List<TipologiaPuntoRaccolta> getTipologiaPuntoRaccolta() {
		return tipologiaPuntoRaccolta;
	}

	public void setTipologiaPuntoRaccolta(
			List<TipologiaPuntoRaccolta> tipologiaPuntiRaccolta) {
		this.tipologiaPuntoRaccolta = tipologiaPuntiRaccolta;
	}

	public List<TipologiaRaccolta> getTipologiaRaccolta() {
		return tipologiaRaccolta;
	}

	public void setTipologiaRaccolta(List<TipologiaRaccolta> tipologiaRaccolta) {
		this.tipologiaRaccolta = tipologiaRaccolta;
	}

	public List<TipologiaUtenza> getTipologiaUtenza() {
		return tipologiaUtenza;
	}

	public void setTipologiaUtenza(List<TipologiaUtenza> tipologiaUtenza) {
		this.tipologiaUtenza = tipologiaUtenza;
	}

	public List<Colori> getColori() {
		return colori;
	}
	public void setColori(List<Colori> colori) {
		this.colori = colori;
	}

	public List<Segnalazioni> getSegnalazioni() {
		return segnalazioni;
	}

	public void setSegnalazioni(List<Segnalazioni> segnalazioni) {
		this.segnalazioni = segnalazioni;
	}

}
