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
import java.util.Map;

public class PuntoRaccolta extends BaseObject {

	private String area;
	private String tipologiaPuntiRaccolta;
	private String tipologiaUtenza;
	private String localizzazione;
	private String indirizzo;
	private String dettaglioIndirizzo;

	private List<OrarioApertura> orarioApertura;

	private Map<String, Boolean> caratteristiche;
	
	private String note;

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

	public List<OrarioApertura> getOrarioApertura() {
		return orarioApertura;
	}

	public void setOrarioApertura(List<OrarioApertura> orarioApertura) {
		this.orarioApertura = orarioApertura;
	}

	public Map<String, Boolean> getCaratteristiche() {
		return caratteristiche;
	}

	public void setCaratteristiche(Map<String, Boolean> caratteristiche) {
		this.caratteristiche = caratteristiche;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String toString() {
		return "PuntoRaccolta [" + area + "," + tipologiaPuntiRaccolta + "," + tipologiaUtenza + "," + indirizzo + "," + dettaglioIndirizzo + "]";
	}

}
