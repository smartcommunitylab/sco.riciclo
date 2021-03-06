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


public class Gestore extends BaseObject {

	private String objectId;
	private String ragioneSociale;
	private Map<String, String> descrizione = new HashMap<String, String>();
	private String ufficio;
	private Map<String, String> indirizzo = new HashMap<String, String>();
	private Map<String, String> orarioUfficio = new HashMap<String, String>();
	private String sitoWeb;
	private String email;
	private String telefono;
	private String fax;
	private double[] geocoding;
	private String facebook;

	public String getRagioneSociale() {
		return ragioneSociale;
	}
	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}
	public String getUfficio() {
		return ufficio;
	}
	public void setUfficio(String ufficio) {
		this.ufficio = ufficio;
	}
	public String getSitoWeb() {
		return sitoWeb;
	}
	public void setSitoWeb(String sitoWeb) {
		this.sitoWeb = sitoWeb;
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
	public String getFacebook() {
		return facebook;
	}
	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	@Override
	public String toString() {
		return "Gestore [" + ragioneSociale + "]";
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
	public double[] getGeocoding() {
		return geocoding;
	}
	public void setGeocoding(double[] geocoding) {
		this.geocoding = geocoding;
	}


}
