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


public class Aree {

	private String istituzione;
	
	private String nome;
	private String parent;
	private String gestore;
	private String descrizione;
	private String comune;
	private String localita;
	private String via;
	private String numero;
	
//	private String utenzaDomestica;
//	private String utenzaNonDomestica;
//	private String utenzaOccasionale;
//	private String utenzaCondominiale;
	
	private String utenze;
	
	public String getIstituzione() {
		return istituzione;
	}

	public void setIstituzione(String istituzione) {
		this.istituzione = istituzione;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getGestore() {
		return gestore;
	}

	public void setGestore(String gestore) {
		this.gestore = gestore;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getComune() {
		return comune;
	}

	public void setComune(String comune) {
		this.comune = comune;
	}

	public String getVia() {
		return via;
	}

	public void setVia(String via) {
		this.via = via;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getLocalita() {
		return localita;
	}

	public void setLocalita(String localita) {
		this.localita = localita;
	}

	public String getUtenze() {
		return utenze;
	}

	public void setUtenze(String utenze) {
		this.utenze = utenze;
	}

//	public String getUtenzaDomestica() {
//		return utenzaDomestica;
//	}
//
//	public void setUtenzaDomestica(String utenzaDomestica) {
//		this.utenzaDomestica = utenzaDomestica;
//	}
//
//	public String getUtenzaNonDomestica() {
//		return utenzaNonDomestica;
//	}
//
//	public void setUtenzaNonDomestica(String utenzaNonDomestica) {
//		this.utenzaNonDomestica = utenzaNonDomestica;
//	}
//
//	public String getUtenzaOccasionale() {
//		return utenzaOccasionale;
//	}
//
//	public void setUtenzaOccasionale(String utenzaOccasionale) {
//		this.utenzaOccasionale = utenzaOccasionale;
//	}
//
//	public String getUtenzaCondominiale() {
//		return utenzaCondominiale;
//	}
//
//	public void setUtenzaCondominiale(String utenzaCondominiale) {
//		this.utenzaCondominiale = utenzaCondominiale;
//	}
	
}
