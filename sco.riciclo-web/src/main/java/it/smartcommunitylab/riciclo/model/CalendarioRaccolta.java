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

import java.util.ArrayList;
import java.util.List;


public class CalendarioRaccolta extends BaseObject {

	private String objectId;
	private String tipologiaPuntoRaccolta;
	private String tipologiaUtenza;
	private String area;
	private List<OrarioApertura> orarioApertura = new ArrayList<OrarioApertura>();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orarioApertura == null) ? 0 : orarioApertura.hashCode());
		result = prime * result + ((area == null) ? 0 : area.hashCode());
		result = prime * result + ((tipologiaUtenza == null) ? 0 : tipologiaUtenza.hashCode());
		result = prime * result + ((tipologiaPuntoRaccolta == null) ? 0 : tipologiaPuntoRaccolta.hashCode());
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
		CalendarioRaccolta other = (CalendarioRaccolta) obj;
		if (orarioApertura == null) {
			if (other.orarioApertura != null)
				return false;
		} else if (!orarioApertura.equals(other.orarioApertura)) {
			return false;
		}
		if (area == null) {
			if (other.area != null)
				return false;
		} else if (!area.equals(other.area)) {
			return false;
		}
		if (tipologiaUtenza == null) {
			if (other.tipologiaUtenza != null)
				return false;
		} else if (!tipologiaUtenza.equals(other.tipologiaUtenza)) {
			return false;
		}
		if (tipologiaPuntoRaccolta == null) {
			if (other.tipologiaPuntoRaccolta != null)
				return false;
		} else if (!tipologiaPuntoRaccolta.equals(other.tipologiaPuntoRaccolta)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "CalendarioRaccolta [" + tipologiaUtenza + "," + tipologiaPuntoRaccolta + "," + area + "," + orarioApertura + "]";
	}

	public String getTipologiaPuntoRaccolta() {
		return tipologiaPuntoRaccolta;
	}

	public void setTipologiaPuntoRaccolta(String tipologiaPuntoRaccolta) {
		this.tipologiaPuntoRaccolta = tipologiaPuntoRaccolta;
	}

	public String getTipologiaUtenza() {
		return tipologiaUtenza;
	}

	public void setTipologiaUtenza(String tipologiaUtenza) {
		this.tipologiaUtenza = tipologiaUtenza;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public List<OrarioApertura> getOrarioApertura() {
		return orarioApertura;
	}

	public void setOrarioApertura(List<OrarioApertura> orarioApertura) {
		this.orarioApertura = orarioApertura;
	}

}
