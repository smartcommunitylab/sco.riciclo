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

import java.util.Map;


public class Area extends BaseObject {

	private String objectId;
	private String istituzione;
	private Map<String, String> nome;
	private String parent;
	private String gestore;
	private Map<String, String> descrizione;
	private String etichetta;
	private Map<String, Boolean> utenza;
	private String codiceISTAT;

	public String getIstituzione() {
		return istituzione;
	}

	public void setIstituzione(String istituzione) {
		this.istituzione = istituzione;
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

	public String getEtichetta() {
		return etichetta;
	}

	public void setEtichetta(String etichetta) {
		this.etichetta = etichetta;
	}	

	public Map<String, Boolean> getUtenza() {
		return utenza;
	}

	public void setUtenza(Map<String, Boolean> utenza) {
		this.utenza = utenza;
	}
	
	@Override
	public String toString() {
		return "Area [" + nome + "," + parent + "," + gestore + "]";
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public Map<String, String> getNome() {
		return nome;
	}

	public void setNome(Map<String, String> nome) {
		this.nome = nome;
	}

	public Map<String, String> getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(Map<String, String> descrizione) {
		this.descrizione = descrizione;
	}

	public String getCodiceISTAT() {
		return codiceISTAT;
	}

	public void setCodiceISTAT(String codiceISTAT) {
		this.codiceISTAT = codiceISTAT;
	}			
	
}
