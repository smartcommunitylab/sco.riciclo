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


public class TipologiaRaccolta {
	
	private String nome;
	private String icona;
//	private String colore;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getIcona() {
		return icona;
	}

	public void setIcona(String icona) {
		this.icona = icona;
	}

//	public String getColore() {
//		return colore;
//	}
//
//	public void setColore(String colore) {
//		this.colore = colore;
//	}
}
