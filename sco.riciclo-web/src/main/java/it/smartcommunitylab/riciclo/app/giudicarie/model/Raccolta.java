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


public class Raccolta {

	private String area;
	private String tipologiaUtenza;
	
	private String tipologiaRifiuto;
	private String tipologiaPuntoRaccolta;
	
	private String tipologiaRaccolta;
	private String colore;
	private String infoRaccolta;

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getTipologiaUtenza() {
		return tipologiaUtenza;
	}

	public void setTipologiaUtenza(String tipologiaUtenza) {
		this.tipologiaUtenza = tipologiaUtenza;
	}

	public String getTipologiaRifiuto() {
		return tipologiaRifiuto;
	}

	public void setTipologiaRifiuto(String tipologiaRifiuto) {
		this.tipologiaRifiuto = tipologiaRifiuto;
	}

	public String getTipologiaPuntoRaccolta() {
		return tipologiaPuntoRaccolta;
	}

	public void setTipologiaPuntoRaccolta(String tipologiaPuntoRaccolta) {
		this.tipologiaPuntoRaccolta = tipologiaPuntoRaccolta;
	}

	public String getTipologiaRaccolta() {
		return tipologiaRaccolta;
	}

	public void setTipologiaRaccolta(String tipologiaRaccolta) {
		this.tipologiaRaccolta = tipologiaRaccolta;
	}

	public String getColore() {
		return colore;
	}

	public void setColore(String colore) {
		this.colore = colore;
	}

	public String getInfoRaccolta() {
		return infoRaccolta;
	}

	public void setInfoRaccolta(String infoRaccolta) {
		this.infoRaccolta = infoRaccolta;
	}	
	
}
