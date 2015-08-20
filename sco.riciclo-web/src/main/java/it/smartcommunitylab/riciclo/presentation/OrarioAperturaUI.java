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

public class OrarioAperturaUI {

	private String dataDa;
	private String dataA;
	private String il;
	private String dalle;
	private String alle;
	private String eccezione;
	private String note;
	
	public String getDataDa() {
		return dataDa;
	}
	public void setDataDa(String dataDa) {
		this.dataDa = dataDa;
	}
	public String getDataA() {
		return dataA;
	}
	public void setDataA(String dataA) {
		this.dataA = dataA;
	}
	public String getIl() {
		return il;
	}
	public void setIl(String il) {
		this.il = il;
	}
	public String getDalle() {
		return dalle;
	}
	public void setDalle(String dalle) {
		this.dalle = dalle;
	}
	public String getAlle() {
		return alle;
	}
	public void setAlle(String alle) {
		this.alle = alle;
	}
	public String getEccezione() {
		return eccezione;
	}
	public void setEccezione(String eccezione) {
		this.eccezione = eccezione;
	}
	
	@Override
	public String toString() {
		return "(" + dataDa + "," + dataA + "," + dalle + "," + alle + "," + il + ")";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alle == null) ? 0 : alle.hashCode());
		result = prime * result + ((dalle == null) ? 0 : dalle.hashCode());
		result = prime * result + ((dataA == null) ? 0 : dataA.hashCode());
		result = prime * result + ((dataDa == null) ? 0 : dataDa.hashCode());
		result = prime * result + ((eccezione == null) ? 0 : eccezione.hashCode());
		result = prime * result + ((il == null) ? 0 : il.hashCode());
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
		OrarioAperturaUI other = (OrarioAperturaUI) obj;
		if (alle == null) {
			if (other.alle != null)
				return false;
		} else if (!alle.equals(other.alle))
			return false;
		if (dalle == null) {
			if (other.dalle != null)
				return false;
		} else if (!dalle.equals(other.dalle))
			return false;
		if (dataA == null) {
			if (other.dataA != null)
				return false;
		} else if (!dataA.equals(other.dataA))
			return false;
		if (dataDa == null) {
			if (other.dataDa != null)
				return false;
		} else if (!dataDa.equals(other.dataDa))
			return false;
		if (eccezione == null) {
			if (other.eccezione != null)
				return false;
		} else if (!eccezione.equals(other.eccezione))
			return false;
		if (il == null) {
			if (other.il != null)
				return false;
		} else if (!il.equals(other.il))
			return false;
		return true;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}	
	
	
	
}
