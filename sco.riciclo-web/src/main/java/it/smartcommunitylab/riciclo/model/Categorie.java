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

import java.util.HashSet;
import java.util.Set;

public class Categorie extends BaseObject {

	private Set<Tipologia> tipologiaUtenza = new HashSet<Tipologia>();
//	private Set<Tipologia> tipologiaIstituzione;
	private Set<Tipologia> tipologiaRifiuto = new HashSet<Tipologia>();
	private Set<TipologiaRaccolta> tipologiaRaccolta = new HashSet<TipologiaRaccolta>();
//	private Set<Tipologia> tipologiaPuntiRaccolta;
	private Set<Tipologia> caratteristicaPuntoRaccolta = new HashSet<Tipologia>();
	private Set<Tipologia> iconeTipologiaRifiuto = new HashSet<Tipologia>();
	private Set<Tipologia> iconeTipologiaPuntoRaccolta = new HashSet<Tipologia>();
	
	public Set<Tipologia> getTipologiaUtenza() {
		return tipologiaUtenza;
	}
	public void setTipologiaUtenza(Set<Tipologia> tipologiaUtenza) {
		this.tipologiaUtenza = tipologiaUtenza;
	}
//	public Set<Tipologia> getTipologiaIstituzione() {
//		return tipologiaIstituzione;
//	}
//	public void setTipologiaIstituzione(Set<Tipologia> tipologiaIstituzione) {
//		this.tipologiaIstituzione = tipologiaIstituzione;
//	}
	public Set<Tipologia> getTipologiaRifiuto() {
		return tipologiaRifiuto;
	}
	public void setTipologiaRifiuto(Set<Tipologia> tipologiaRifiuto) {
		this.tipologiaRifiuto = tipologiaRifiuto;
	}
	public Set<TipologiaRaccolta> getTipologiaRaccolta() {
		return tipologiaRaccolta;
	}
	public void setTipologiaRaccolta(Set<TipologiaRaccolta> tipologiaRaccolta) {
		this.tipologiaRaccolta = tipologiaRaccolta;
	}
//	public Set<Tipologia> getTipologiaPuntiRaccolta() {
//		return tipologiaPuntiRaccolta;
//	}
//	public void setTipologiaPuntiRaccolta(Set<Tipologia> tipologiaPuntiRaccolta) {
//		this.tipologiaPuntiRaccolta = tipologiaPuntiRaccolta;
//	}
	public Set<Tipologia> getCaratteristicaPuntoRaccolta() {
		return caratteristicaPuntoRaccolta;
	}
	public Set<Tipologia> getIconeTipologiaRifiuto() {
		return iconeTipologiaRifiuto;
	}
	public void setIconeTipologiaRifiuto(Set<Tipologia> iconeTipologiaRifiuto) {
		this.iconeTipologiaRifiuto = iconeTipologiaRifiuto;
	}
	public Set<Tipologia> getIconeTipologiaPuntoRaccolta() {
		return iconeTipologiaPuntoRaccolta;
	}
	public void setIconeTipologiaPuntoRaccolta(Set<Tipologia> iconeTipologiaPuntoRaccolta) {
		this.iconeTipologiaPuntoRaccolta = iconeTipologiaPuntoRaccolta;
	}
	public void setCaratteristicaPuntoRaccolta(Set<Tipologia> caratteristicaPuntoRaccolta) {
		this.caratteristicaPuntoRaccolta = caratteristicaPuntoRaccolta;
	}



}
