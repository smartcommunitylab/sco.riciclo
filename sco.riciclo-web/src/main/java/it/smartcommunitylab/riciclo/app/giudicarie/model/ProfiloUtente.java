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

import java.util.List;

public class ProfiloUtente {

	private String utenza;
	private String comune;
	private String indirizzo;
	private List<Aree> aree;
	
	public String getUtenza() {
		return utenza;
	}
	public void setUtenza(String utenza) {
		this.utenza = utenza;
	}
	public String getComune() {
		return comune;
	}
	public void setComune(String comune) {
		this.comune = comune;
	}
	public String getIndirizzo() {
		return indirizzo;
	}
	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}
	public List<Aree> getAree() {
		return aree;
	}
	public void setAree(List<Aree> aree) {
		this.aree = aree;
	}


	
	
}
