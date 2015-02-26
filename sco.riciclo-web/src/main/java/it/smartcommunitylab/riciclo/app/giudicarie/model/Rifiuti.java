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

package it.smartcommunitylab.riciclo.app.giudicarie.model;

import java.util.List;

public class Rifiuti {

	private List<Aree>aree;
	private List<Gestori>gestori;
	private List<Istituzioni>istituzioni;
	private List<Profili>profili;
	private List<PuntiRaccolta>puntiRaccolta;
	private List<TipologiaRifiuto> tipologiaRifiuto;
	private List<Riciclabolario>riciclabolario;
	private List<Raccolta>raccolta;
	private List<TipologiaPuntiRaccolta>tipologiaPuntiRaccolta;
	private List<TipologiaRaccolta>tipologiaRaccolta;
	
	public List<Aree>getAree() {
		return aree;
	}

	public void setAree(List<Aree>aree) {
		this.aree = aree;
	}

	public List<Gestori>getGestori() {
		return gestori;
	}

	public void setGestori(List<Gestori>gestori) {
		this.gestori = gestori;
	}

	public List<Istituzioni>getIstituzioni() {
		return istituzioni;
	}

	public void setIstituzioni(List<Istituzioni>istituzioni) {
		this.istituzioni = istituzioni;
	}

	public List<Profili>getProfili() {
		return profili;
	}

	public void setProfili(List<Profili>profili) {
		this.profili = profili;
	}

	public List<PuntiRaccolta>getPuntiRaccolta() {
		return puntiRaccolta;
	}

	public void setPuntiRaccolta(List<PuntiRaccolta>puntiRaccolta) {
		this.puntiRaccolta = puntiRaccolta;
	}

	public List<TipologiaRifiuto> getTipologiaRifiuto() {
		return tipologiaRifiuto;
	}

	public void setTipologiaRifiuto(List<TipologiaRifiuto> tipologiaRifiuto) {
		this.tipologiaRifiuto = tipologiaRifiuto;
	}

	public List<Riciclabolario>getRiciclabolario() {
		return riciclabolario;
	}

	public void setRiciclabolario(List<Riciclabolario>riciclabolario) {
		this.riciclabolario = riciclabolario;
	}

	public List<Raccolta>getRaccolta() {
		return raccolta;
	}

	public void setRaccolta(List<Raccolta>raccolta) {
		this.raccolta = raccolta;
	}

	public List<TipologiaPuntiRaccolta> getTipologiaPuntiRaccolta() {
		return tipologiaPuntiRaccolta;
	}

	public void setTipologiaPuntiRaccolta(List<TipologiaPuntiRaccolta> tipologiaPuntiRaccolta) {
		this.tipologiaPuntiRaccolta = tipologiaPuntiRaccolta;
	}

	public List<TipologiaRaccolta>getTipologiaRaccolta() {
		return tipologiaRaccolta;
	}

	public void setTipologiaRaccolta(List<TipologiaRaccolta>tipologiaRaccolta) {
		this.tipologiaRaccolta = tipologiaRaccolta;
	}

}
