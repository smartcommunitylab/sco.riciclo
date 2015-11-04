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

public class Tipologia extends BaseObject {

	private String objectId;
	private Map<String, String> nome = new HashMap<String, String>();;
	private Map<String, String> descrizione = new HashMap<String, String>();
	private String icona;

	public Tipologia() {
	}

	public Tipologia(String id, String nome, String descrizione, String icona, String lang) {
		super();
		this.objectId = id;
		this.nome.put(lang, nome);
		this.descrizione.put(lang, descrizione);
		this.icona = icona;
	}

	public String getIcona() {
		return icona;
	}

	public void setIcona(String icona) {
		this.icona = icona;
	}

	@Override
	public String toString() {
		return nome + ((descrizione != null) ? "=" + descrizione : "");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
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
		Tipologia other = (Tipologia) obj;
		if (objectId == null) {
			if (other.objectId != null)
				return false;
		} else if (!objectId.equals(other.objectId))
			return false;
		return true;
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

}
