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


public class Istituzione extends BaseObject {

	private String objectId;
	private String nome;
	private Map<String, String> descrizione;
	private String ufficio;
	private Map<String, String> indirizzo;
	private Map<String, String> orarioUfficio;
	private String sito;
	private String pec;
	private String email;
	private String telefono;
	private String fax;
	private String localizzazione;
	private String facebook;	
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getUfficio() {
		return ufficio;
	}
	public void setUfficio(String ufficio) {
		this.ufficio = ufficio;
	}
	public void setSito(String sitoIstituzionale) {
		this.sito = sitoIstituzionale;
	}
	public String getPec() {
		return pec;
	}
	public void setPec(String pec) {
		this.pec = pec;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getLocalizzazione() {
		return localizzazione;
	}
	public void setLocalizzazione(String localizzazione) {
		this.localizzazione = localizzazione;
	}
	public String getFacebook() {
		return facebook;
	}
	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}		
	
	@Override
	public String toString() {
		return "Istituzione [" + nome + "]";
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public Map<String, String> getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(Map<String, String> descrizione) {
		this.descrizione = descrizione;
	}
	public Map<String, String> getIndirizzo() {
		return indirizzo;
	}
	public void setIndirizzo(Map<String, String> indirizzo) {
		this.indirizzo = indirizzo;
	}
	public Map<String, String> getOrarioUfficio() {
		return orarioUfficio;
	}
	public void setOrarioUfficio(Map<String, String> orarioUfficio) {
		this.orarioUfficio = orarioUfficio;
	}
	public String getSito() {
		return sito;
	}
	

}
