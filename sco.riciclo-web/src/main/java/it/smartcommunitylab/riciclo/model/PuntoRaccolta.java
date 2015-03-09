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

	private String tipologiaPuntiRaccolta;
	private List<UtenzaArea> utenzaArea;
	private String localizzazione;
	private String indirizzo;
	private String dettaglioIndirizzo;

	private List<OrarioApertura> orarioApertura;

	private Map<String, Boolean> caratteristiche;
	
	private String note;

	public String getTipologiaPuntiRaccolta() {
		return tipologiaPuntiRaccolta;
	}

	public void setTipologiaPuntiRaccolta(String tipologiaPuntiRaccolta) {
		this.tipologiaPuntiRaccolta = tipologiaPuntiRaccolta;
	}

	public List<UtenzaArea> getUtenzaArea() {
		return utenzaArea;
	}

	public void setUtenzaArea(List<UtenzaArea> utenzaArea) {
		this.utenzaArea = utenzaArea;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dettaglioIndirizzo == null) ? 0 : dettaglioIndirizzo.hashCode());
		result = prime * result + ((indirizzo == null) ? 0 : indirizzo.hashCode());
		result = prime * result + ((orarioApertura == null) ? 0 : orarioApertura.hashCode());
		result = prime * result + ((tipologiaPuntiRaccolta == null) ? 0 : tipologiaPuntiRaccolta.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PuntoRaccolta other = (PuntoRaccolta) obj;
		if (dettaglioIndirizzo == null) {
			if (other.dettaglioIndirizzo != null)
				return false;
		} else if (!dettaglioIndirizzo.equals(other.dettaglioIndirizzo))
			return false;
		if (indirizzo == null) {
			if (other.indirizzo != null)
				return false;
		} else if (!indirizzo.equals(other.indirizzo))
			return false;
		if (orarioApertura == null) {
			if (other.orarioApertura != null)
				return false;
		} else if (!orarioApertura.equals(other.orarioApertura))
			return false;
		if (tipologiaPuntiRaccolta == null) {
			if (other.tipologiaPuntiRaccolta != null)
				return false;
		} else if (!tipologiaPuntiRaccolta.equals(other.tipologiaPuntiRaccolta))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PuntoRaccolta [" + utenzaArea + "," + tipologiaPuntiRaccolta + "," + indirizzo + "," + dettaglioIndirizzo + "->" + orarioApertura + "]";
	}

}
