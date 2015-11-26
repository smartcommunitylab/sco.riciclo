package it.smartcommunitylab.riciclo.riapp.importer.model;

import it.smartcommunitylab.riciclo.model.Area;

import java.util.List;
import java.util.Map;

public class RiappAreaStruct {
	private Map<String, Area> comuneEstesoMap;
	private Map<String, Area> comuneSubAreaMap;
	private Map<String, List<Area>> calendarioAreaMap;
	
	public Map<String, Area> getComuneEstesoMap() {
		return comuneEstesoMap;
	}
	public void setComuneEstesoMap(Map<String, Area> comuneEstesoMap) {
		this.comuneEstesoMap = comuneEstesoMap;
	}
	public Map<String, Area> getComuneSubAreaMap() {
		return comuneSubAreaMap;
	}
	public void setComuneSubAreaMap(Map<String, Area> comuneSubAreaMap) {
		this.comuneSubAreaMap = comuneSubAreaMap;
	}
	public Map<String, List<Area>> getCalendarioAreaMap() {
		return calendarioAreaMap;
	}
	public void setCalendarioAreaMap(Map<String, List<Area>> calendarioAreaMap) {
		this.calendarioAreaMap = calendarioAreaMap;
	}
}
