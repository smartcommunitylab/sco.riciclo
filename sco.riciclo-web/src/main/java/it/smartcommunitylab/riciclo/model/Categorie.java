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

import java.util.Set;

public class Categorie extends BaseObject {

	private Set<Tipologia> tipologiaUtenza;
	private Set<Tipologia> tipologiaIstituzione;
	private Set<Tipologia> tipologiaRifiuto;
	private Set<Tipologia> colori;
	private Set<Tipologia> tipologiaRaccolta;
	private Set<Tipologia> tipologiaPuntiRaccolta;
	private Set<Tipologia> caratteristicaPuntoRaccolta;
	
	public Set<Tipologia> getTipologiaUtenza() {
		return tipologiaUtenza;
	}
	public void setTipologiaUtenza(Set<Tipologia> tipologiaUtenza) {
		this.tipologiaUtenza = tipologiaUtenza;
	}
	public Set<Tipologia> getTipologiaIstituzione() {
		return tipologiaIstituzione;
	}
	public void setTipologiaIstituzione(Set<Tipologia> tipologiaIstituzione) {
		this.tipologiaIstituzione = tipologiaIstituzione;
	}
	public Set<Tipologia> getTipologiaRifiuto() {
		return tipologiaRifiuto;
	}
	public void setTipologiaRifiuto(Set<Tipologia> tipologiaRifiuto) {
		this.tipologiaRifiuto = tipologiaRifiuto;
	}
	public Set<Tipologia> getColori() {
		return colori;
	}
	public void setColori(Set<Tipologia> colori) {
		this.colori = colori;
	}
	public Set<Tipologia> getTipologiaRaccolta() {
		return tipologiaRaccolta;
	}
	public void setTipologiaRaccolta(Set<Tipologia> tipologiaRaccolta) {
		this.tipologiaRaccolta = tipologiaRaccolta;
	}
	public Set<Tipologia> getTipologiaPuntiRaccolta() {
		return tipologiaPuntiRaccolta;
	}
	public void setTipologiaPuntiRaccolta(Set<Tipologia> tipologiaPuntiRaccolta) {
		this.tipologiaPuntiRaccolta = tipologiaPuntiRaccolta;
	}
	public Set<Tipologia> getCaratteristicaPuntoRaccolta() {
		return caratteristicaPuntoRaccolta;
	}

	public void setCaratteristicaPuntoRaccolta(Set<Tipologia> caratteristicaPuntoRaccolta) {
		this.caratteristicaPuntoRaccolta = caratteristicaPuntoRaccolta;
	}

}
