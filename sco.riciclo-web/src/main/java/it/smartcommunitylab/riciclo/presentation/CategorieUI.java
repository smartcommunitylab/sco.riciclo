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

import java.util.Set;

public class CategorieUI extends BaseObjectUI {

	private Set<TipologiaUI> tipologiaUtenza;
//	private Set<Tipologia> tipologiaIstituzione;
	private Set<TipologiaUI> tipologiaRifiuto;
	private Set<TipologiaUI> tipologiaRaccolta;
	private Set<TipologiaUI> tipologiaPuntiRaccolta;
	private Set<TipologiaUI> caratteristicaPuntoRaccolta;
	
	public Set<TipologiaUI> getTipologiaUtenza() {
		return tipologiaUtenza;
	}
	public void setTipologiaUtenza(Set<TipologiaUI> tipologiaUtenza) {
		this.tipologiaUtenza = tipologiaUtenza;
	}
//	public Set<Tipologia> getTipologiaIstituzione() {
//		return tipologiaIstituzione;
//	}
//	public void setTipologiaIstituzione(Set<Tipologia> tipologiaIstituzione) {
//		this.tipologiaIstituzione = tipologiaIstituzione;
//	}
	public Set<TipologiaUI> getTipologiaRifiuto() {
		return tipologiaRifiuto;
	}
	public void setTipologiaRifiuto(Set<TipologiaUI> tipologiaRifiuto) {
		this.tipologiaRifiuto = tipologiaRifiuto;
	}
	public Set<TipologiaUI> getTipologiaRaccolta() {
		return tipologiaRaccolta;
	}
	public void setTipologiaRaccolta(Set<TipologiaUI> tipologiaRaccolta) {
		this.tipologiaRaccolta = tipologiaRaccolta;
	}
	public Set<TipologiaUI> getTipologiaPuntiRaccolta() {
		return tipologiaPuntiRaccolta;
	}
	public void setTipologiaPuntiRaccolta(Set<TipologiaUI> tipologiaPuntiRaccolta) {
		this.tipologiaPuntiRaccolta = tipologiaPuntiRaccolta;
	}
	public Set<TipologiaUI> getCaratteristicaPuntoRaccolta() {
		return caratteristicaPuntoRaccolta;
	}

	public void setCaratteristicaPuntoRaccolta(Set<TipologiaUI> caratteristicaPuntoRaccolta) {
		this.caratteristicaPuntoRaccolta = caratteristicaPuntoRaccolta;
	}

}
