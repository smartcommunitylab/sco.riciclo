package it.smartcommunitylab.riciclo.controller;

import it.smartcommunitylab.riciclo.model.Area;
import it.smartcommunitylab.riciclo.model.CalendarioRaccolta;
import it.smartcommunitylab.riciclo.model.Gestore;
import it.smartcommunitylab.riciclo.model.Istituzione;
import it.smartcommunitylab.riciclo.model.PuntoRaccolta;
import it.smartcommunitylab.riciclo.model.Raccolta;
import it.smartcommunitylab.riciclo.model.Riciclabolario;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.query.Criteria;

public class Utils {
	
	public static boolean isNull(String value) {
		if((value == null) || (value.isEmpty())) {
			return true;
		} else {
			return false;
		}
	}
	
	public static double[] convertLocalizzazione(String localizzazione) throws NumberFormatException {
		//input:"45.893170,11.036281"
		//output:[11.036281,45.893170]
		String[] values = localizzazione.split(",");
		double[] result = new double[2];
		result[0] = Double.valueOf(values[1].trim());
		result[1] = Double.valueOf(values[0].trim());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static void findRiciclabolario(List<Area> areaList, String appId, boolean draft, 
			Map<String, Riciclabolario> resultMap, RepositoryManager storage) throws ClassNotFoundException {
		List<Area> newAreaList = new ArrayList<Area>();
		for(Area area : areaList) {
			String areaId = area.getObjectId();
			//find Riciclabolario rows for specific area
			Criteria criteriaArea = new Criteria("area").is(areaId);
			List<Riciclabolario> dataRows = (List<Riciclabolario>) storage.findData(Riciclabolario.class, criteriaArea, appId, draft) ;
			//add data to result map
			for(Riciclabolario riciclabolario : dataRows) {
				resultMap.put(riciclabolario.getObjectId(), riciclabolario);
			}
			//search sub-area
			Criteria criteriaParent = new Criteria("parent").is(areaId);
			List<Area> subAreaList = (List<Area>) storage.findData(Area.class, criteriaParent, appId, draft);
			newAreaList.addAll(subAreaList);
		}
		if(!newAreaList.isEmpty()) {
			Utils.findRiciclabolario(newAreaList, appId, draft, resultMap, storage);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void findRaccolte(List<Area> areaList, String appId, boolean draft, 
			Map<String, Raccolta> resultMap, RepositoryManager storage) throws ClassNotFoundException {
		List<Area> newAreaList = new ArrayList<Area>();
		for(Area area : areaList) {
			String areaId = area.getObjectId();
			//find Raccolta rows for specific area
			Criteria criteriaArea = new Criteria("area").is(areaId);
			List<Raccolta> dataRows = (List<Raccolta>) storage.findData(Raccolta.class, criteriaArea, appId, draft) ;
			//add data to result map
			for(Raccolta raccolta : dataRows) {
				resultMap.put(raccolta.getObjectId(), raccolta);
			}
			//search sub-area
			Criteria criteriaParent = new Criteria("parent").is(areaId);
			List<Area> subAreaList = (List<Area>) storage.findData(Area.class, criteriaParent, appId, draft);
			newAreaList.addAll(subAreaList);
		}
		if(!newAreaList.isEmpty()) {
			Utils.findRaccolte(newAreaList, appId, draft, resultMap, storage);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void findGestori(List<Area> areaList, String appId, boolean draft, 
			Map<String, Gestore> resultMap, RepositoryManager storage) throws ClassNotFoundException {
		List<Area> newAreaList = new ArrayList<Area>();
		for(Area area : areaList) {
			String areaId = area.getObjectId();
			String gestoreId = area.getGestore();
			//find Gestore rows for specific area
			Criteria criteriaId = new Criteria("objectId").is(gestoreId);
			Gestore gestore = (Gestore) storage.findOneData(Gestore.class, criteriaId, appId, draft) ;
			//add data to result map
			if(gestore != null) {
				resultMap.put(gestoreId, gestore);
			}
			//search sub-area
			Criteria criteriaParent = new Criteria("parent").is(areaId);
			List<Area> subAreaList = (List<Area>) storage.findData(Area.class, criteriaParent, appId, draft);
			newAreaList.addAll(subAreaList);
		}
		if(!newAreaList.isEmpty()) {
			Utils.findGestori(newAreaList, appId, draft, resultMap, storage);
		}
	}

	@SuppressWarnings("unchecked")
	public static void findIstituzioni(List<Area> areaList, String appId, boolean draft, 
			Map<String, Istituzione> resultMap, RepositoryManager storage) throws ClassNotFoundException {
		List<Area> newAreaList = new ArrayList<Area>();
		for(Area area : areaList) {
			String areaId = area.getObjectId();
			String istituzioneId = area.getIstituzione();
			//find Istituzione rows for specific area
			Criteria criteriaId = new Criteria("objectId").is(istituzioneId);
			Istituzione istituzione = (Istituzione) storage.findOneData(Istituzione.class, criteriaId, appId, draft) ;
			//add data to result map
			if(istituzione != null) {
				resultMap.put(istituzioneId, istituzione);
			}
			//search sub-area
			Criteria criteriaParent = new Criteria("parent").is(areaId);
			List<Area> subAreaList = (List<Area>) storage.findData(Area.class, criteriaParent, appId, draft);
			newAreaList.addAll(subAreaList);
		}
		if(!newAreaList.isEmpty()) {
			Utils.findIstituzioni(newAreaList, appId, draft, resultMap, storage);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void findPuntiRaccolta(List<Area> areaList, String appId, boolean draft, 
			Map<String, PuntoRaccolta> resultMap, RepositoryManager storage) throws ClassNotFoundException {
		List<Area> newAreaList = new ArrayList<Area>();
		for(Area area : areaList) {
			String areaId = area.getObjectId();
			//find PuntoRaccolta rows for specific area
			Criteria criteriaArea = new Criteria("area").is(areaId);
			List<PuntoRaccolta> dataRows = (List<PuntoRaccolta>) storage.findData(PuntoRaccolta.class, criteriaArea, appId, draft) ;
			//add data to result map
			for(PuntoRaccolta puntoRaccolta : dataRows) { 
				resultMap.put(puntoRaccolta.getObjectId(), puntoRaccolta);
			}
			//search sub-area
			Criteria criteriaParent = new Criteria("parent").is(areaId);
			List<Area> subAreaList = (List<Area>) storage.findData(Area.class, criteriaParent, appId, draft);
			newAreaList.addAll(subAreaList);
		}
		if(!newAreaList.isEmpty()) {
			Utils.findPuntiRaccolta(newAreaList, appId, draft, resultMap, storage);
		}
	}

	@SuppressWarnings("unchecked")
	public static void findCalendariRaccolta(List<Area> areaList, String appId, boolean draft, 
			Map<String, CalendarioRaccolta> resultMap, RepositoryManager storage) throws ClassNotFoundException {
		List<Area> newAreaList = new ArrayList<Area>();
		for(Area area : areaList) {
			String areaId = area.getObjectId();
			//find CalendarioRaccolta rows for specific area
			Criteria criteriaArea = new Criteria("area").is(areaId);
			List<CalendarioRaccolta> dataRows = (List<CalendarioRaccolta>) storage.findData(CalendarioRaccolta.class, 
					criteriaArea, appId, draft) ;
			//add data to result map
			for(CalendarioRaccolta calendarioRaccolta : dataRows) { 
				resultMap.put(calendarioRaccolta.getObjectId(), calendarioRaccolta);
			}
			//search sub-area
			Criteria criteriaParent = new Criteria("parent").is(areaId);
			List<Area> subAreaList = (List<Area>) storage.findData(Area.class, criteriaParent, appId, draft);
			newAreaList.addAll(subAreaList);
		}
		if(!newAreaList.isEmpty()) {
			Utils.findCalendariRaccolta(newAreaList, appId, draft, resultMap, storage);
		}
	}

}
