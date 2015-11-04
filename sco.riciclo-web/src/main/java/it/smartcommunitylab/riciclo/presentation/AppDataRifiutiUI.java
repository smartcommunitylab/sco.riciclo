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

package it.smartcommunitylab.riciclo.presentation;

import java.util.List;

public class AppDataRifiutiUI extends BaseObjectUI {

	private List<TipologiaProfiloUI> tipologiaProfilo;
	private List<AreaUI> aree;
	private List<GestoreUI> gestori;
	private List<IstituzioneUI> istituzioni;
	private List<PuntoRaccoltaUI> puntiRaccolta;
	private List<RiciclabolarioUI> riciclabolario;
	private List<RaccoltaUI> raccolta;
	private List<ColoreUI> colore;
	private List<SegnalazioneUI> segnalazione;

	private CategorieUI categorie;

	public List<TipologiaProfiloUI> getTipologiaProfilo() {
		return tipologiaProfilo;
	}

	public void setTipologiaProfilo(List<TipologiaProfiloUI> profili) {
		this.tipologiaProfilo = profili;
	}

	public List<AreaUI> getAree() {
		return aree;
	}

	public void setAree(List<AreaUI> aree) {
		this.aree = aree;
	}

	public List<GestoreUI> getGestori() {
		return gestori;
	}

	public void setGestori(List<GestoreUI> gestori) {
		this.gestori = gestori;
	}

	public List<IstituzioneUI> getIstituzioni() {
		return istituzioni;
	}

	public void setIstituzioni(List<IstituzioneUI> istituzioni) {
		this.istituzioni = istituzioni;
	}

	public List<PuntoRaccoltaUI> getPuntiRaccolta() {
		return puntiRaccolta;
	}

	public void setPuntiRaccolta(List<PuntoRaccoltaUI> puntiRaccolta) {
		this.puntiRaccolta = puntiRaccolta;
	}

	public List<RiciclabolarioUI> getRiciclabolario() {
		return riciclabolario;
	}

	public void setRiciclabolario(List<RiciclabolarioUI> riciclabolario) {
		this.riciclabolario = riciclabolario;
	}

	public List<RaccoltaUI> getRaccolta() {
		return raccolta;
	}

	public void setRaccolta(List<RaccoltaUI> raccolta) {
		this.raccolta = raccolta;
	}

	public CategorieUI getCategorie() {
		return categorie;
	}

	public void setCategorie(CategorieUI categorie) {
		this.categorie = categorie;
	}

	public List<ColoreUI> getColore() {
		return colore;
	}

	public void setColore(List<ColoreUI> colore) {
		this.colore = colore;
	}

	public List<SegnalazioneUI> getSegnalazione() {
		return segnalazione;
	}

	public void setSegnalazione(List<SegnalazioneUI> segnalazione) {
		this.segnalazione = segnalazione;
	}

}
