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


public class RiciclabolarioUI extends BaseObjectUI {

	private String nome;
	
	private String area;
	private String tipologiaUtenza;
	private String tipologiaRifiuto;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

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
	
	@Override
	public String toString() {
		return "Riciclabolario [" + nome + "," + area + "," + tipologiaUtenza + "," + tipologiaRifiuto + "]";
	}		
	
	
}
