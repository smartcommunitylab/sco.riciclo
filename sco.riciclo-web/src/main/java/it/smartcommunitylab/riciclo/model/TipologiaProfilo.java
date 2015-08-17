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

import java.util.HashMap;
import java.util.Map;


public class TipologiaProfilo extends BaseObject {

	private String objectId;
	private Map<String, String> nome = new HashMap<String, String>();;
	private String tipologiaUtenza;
	private Map<String, String> descrizione = new HashMap<String, String>();;
	
	@Override
	public String toString() {
		return "Profilo [" + nome + "," + tipologiaUtenza + "]";
	}

	public Map<String, String> getNome() {
		return nome;
	}

	public void setNome(Map<String, String> nome) {
		this.nome = nome;
	}

	public String getTipologiaUtenza() {
		return tipologiaUtenza;
	}

	public void setTipologiaUtenza(String tipologiaUtenza) {
		this.tipologiaUtenza = tipologiaUtenza;
	}

	public Map<String, String> getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(Map<String, String> descrizione) {
		this.descrizione = descrizione;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}	
	

}
