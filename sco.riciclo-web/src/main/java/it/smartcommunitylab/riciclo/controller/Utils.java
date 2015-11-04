package it.smartcommunitylab.riciclo.controller;

import it.smartcommunitylab.riciclo.model.Area;
import it.smartcommunitylab.riciclo.model.CalendarioRaccolta;
import it.smartcommunitylab.riciclo.model.Crm;
import it.smartcommunitylab.riciclo.model.Gestore;
import it.smartcommunitylab.riciclo.model.Istituzione;
import it.smartcommunitylab.riciclo.model.PuntoRaccolta;
import it.smartcommunitylab.riciclo.model.Raccolta;
import it.smartcommunitylab.riciclo.model.Riciclabolario;
import it.smartcommunitylab.riciclo.model.Segnalazione;
import it.smartcommunitylab.riciclo.security.Token;
import it.smartcommunitylab.riciclo.storage.AppSetup;
import it.smartcommunitylab.riciclo.storage.DataSetInfo;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.data.mongodb.core.query.Criteria;

public class Utils {

	public static boolean isNull(String value) {
		if((value == null) || (value.isEmpty())) {
			return true;
		} else {
			return false;
		}
	}

	public static String getString(Map<String, String> data, String lang, String defaultLang) {
		String result = null;
		if(data.containsKey(lang)) {
			result = data.get(lang);
		} else {
			result = data.get(defaultLang);
		}
		return result;
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

	public static String convertLocalizzazione(double[] geocoding) throws NumberFormatException {
		//output:"45.893170,11.036281"
		//input:[11.036281,45.893170]
		return String.valueOf(geocoding[1]) + "," + String.valueOf(geocoding[0]);
	}

	public static boolean getDraft(HttpServletRequest request) {
		String draftString = request.getParameter("draft");
		boolean draft = false;
		if(!Utils.isNull(draftString)) {
			draft = Boolean.valueOf(draftString);
		}
		return draft;
	}

	public static List<Area> findAreaByComune(String comune, String ownerId, boolean draft, RepositoryManager storage)
			throws ClassNotFoundException {
		Criteria criteriaISTAT = new Criteria("codiceISTAT").is(comune);
		Area areaComune = storage.findOneData(Area.class, criteriaISTAT, ownerId, draft);
		List<Area> areaList = new ArrayList<Area>();
		if(areaComune != null) {
			areaList.add(areaComune);
		}
		return areaList;
	}

	@SuppressWarnings("unchecked")
	public static void findRiciclabolario(Map<String, Area> mapArea, String ownerId, boolean draft,
			Map<String, Riciclabolario> resultMap, RepositoryManager storage) throws ClassNotFoundException {
		for(Area area : mapArea.values()) {
			String areaId = area.getObjectId();
			//find Riciclabolario rows for specific area
			Criteria criteriaArea = new Criteria("area").is(areaId);
			List<Riciclabolario> dataRows = (List<Riciclabolario>) storage.findData(Riciclabolario.class, criteriaArea, ownerId, draft) ;
			//add data to result map
			for(Riciclabolario riciclabolario : dataRows) {
				resultMap.put(riciclabolario.getObjectId(), riciclabolario);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void findRaccolte(Map<String, Area> mapArea, String ownerId, boolean draft,
			Map<String, Raccolta> resultMap, RepositoryManager storage) throws ClassNotFoundException {
		for(Area area : mapArea.values()) {
			String areaId = area.getObjectId();
			//find Raccolta rows for specific area
			Criteria criteriaArea = new Criteria("area").is(areaId);
			List<Raccolta> dataRows = (List<Raccolta>) storage.findData(Raccolta.class, criteriaArea, ownerId, draft) ;
			//add data to result map
			for(Raccolta raccolta : dataRows) {
				resultMap.put(raccolta.getObjectId(), raccolta);
			}
		}
	}

	public static void findGestori(Map<String, Area> mapArea, String ownerId, boolean draft,
			Map<String, Gestore> resultMap, RepositoryManager storage) throws ClassNotFoundException {
		for(Area area : mapArea.values()) {
			String gestoreId = area.getGestore();
			//find Gestore rows for specific area
			Criteria criteriaId = new Criteria("objectId").is(gestoreId);
			Gestore gestore = (Gestore) storage.findOneData(Gestore.class, criteriaId, ownerId, draft) ;
			//add data to result map
			if(gestore != null) {
				resultMap.put(gestoreId, gestore);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void findIstituzioni(Map<String, Area> mapArea, String ownerId, boolean draft,
			Map<String, Istituzione> resultMap, RepositoryManager storage) throws ClassNotFoundException {
		for(Area area : mapArea.values()) {
			String nome = area.getIstituzione();
			//find Istituzione rows for specific area
			Criteria criteriaNome = new Criteria("nome").is(nome);
			List<Istituzione> istituzioni = (List<Istituzione>) storage.findData(Istituzione.class, criteriaNome, ownerId, draft) ;
			//add data to result map
			for(Istituzione istituzione : istituzioni) {
				resultMap.put(istituzione.getObjectId(), istituzione);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void findPuntiRaccolta(Map<String, Area> mapArea, String ownerId, boolean draft,
			Map<String, PuntoRaccolta> resultMap, RepositoryManager storage) throws ClassNotFoundException {
		for(Area area : mapArea.values()) {
			String areaId = area.getObjectId();
			//find PuntoRaccolta rows for specific area
			Criteria criteriaArea = new Criteria("area").is(areaId);
			List<PuntoRaccolta> dataRows = (List<PuntoRaccolta>) storage.findData(PuntoRaccolta.class, criteriaArea, ownerId, draft) ;
			//add data to result map
			for(PuntoRaccolta puntoRaccolta : dataRows) {
				resultMap.put(puntoRaccolta.getObjectId(), puntoRaccolta);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void findCalendariRaccolta(Map<String, Area> mapArea, String ownerId, boolean draft,
			Map<String, CalendarioRaccolta> resultMap, RepositoryManager storage) throws ClassNotFoundException {
		for(Area area : mapArea.values()) {
			String areaId = area.getObjectId();
			//find CalendarioRaccolta rows for specific area
			Criteria criteriaArea = new Criteria("area").is(areaId);
			List<CalendarioRaccolta> dataRows = (List<CalendarioRaccolta>) storage.findData(CalendarioRaccolta.class,
					criteriaArea, ownerId, draft) ;
			//add data to result map
			for(CalendarioRaccolta calendarioRaccolta : dataRows) {
				resultMap.put(calendarioRaccolta.getObjectId(), calendarioRaccolta);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void findSegnalazioni(Map<String, Area> mapArea, String ownerId, boolean draft,
			Map<String, Segnalazione> resultMap, RepositoryManager storage) throws ClassNotFoundException {
		for(Area area : mapArea.values()) {
			String areaId = area.getObjectId();
			//find Segnalazione rows for specific area
			Criteria criteriaArea = new Criteria("area").is(areaId);
			List<Segnalazione> dataRows = (List<Segnalazione>) storage.findData(Segnalazione.class,
					criteriaArea, ownerId, draft) ;
			//add data to result map
			for(Segnalazione segnalazione : dataRows) {
				resultMap.put(segnalazione.getObjectId(), segnalazione);
			}
		}
	}

	public static void findAree(String comune, String ownerId, boolean draft,
			Map<String, Area> resultMap, RepositoryManager storage) throws ClassNotFoundException {
		//find Area by codice ISTAT
		List<Area> areaList = findAreaByComune(comune, ownerId, draft, storage);
		if(!areaList.isEmpty()) {
			Area comuneArea = areaList.get(0);
			Utils.findAree(areaList, ownerId, draft, resultMap, storage);
			//find parent Area
			String parentId = comuneArea.getParent();
			while(!isNull(parentId)) {
				Criteria criteriaId = new Criteria("objectId").is(parentId);
				Area area = storage.findOneData(Area.class, criteriaId, ownerId, draft);
				if(area != null) {
					resultMap.put(area.getObjectId(), area);
					parentId = area.getParent();
				} else {
					parentId = null;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void findAree(List<Area> areaList, String ownerId, boolean draft,
			Map<String, Area> resultMap, RepositoryManager storage) throws ClassNotFoundException {
		List<Area> newAreaList = new ArrayList<Area>();
		for(Area area : areaList) {
			resultMap.put(area.getObjectId(), area);
			String areaId = area.getObjectId();
			//search sub-area
			Criteria criteriaParent = new Criteria("parent").is(areaId);
			List<Area> subAreaList = (List<Area>) storage.findData(Area.class, criteriaParent, ownerId, draft);
			newAreaList.addAll(subAreaList);
		}
		if(!newAreaList.isEmpty()) {
			Utils.findAree(newAreaList, ownerId, draft, resultMap, storage);
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Crm> findCrm(Map<String, PuntoRaccolta> mapPuntoRaccolta, String ownerId,
			boolean draft, RepositoryManager storage) throws ClassNotFoundException {
		Map<String, Crm> resultMapCrm  = new HashMap<String, Crm>();
		List<Crm> dataRows = (List<Crm>) storage.findData(Crm.class, null, ownerId, draft) ;
		Map<String, Crm> completeMapCrm  = new HashMap<String, Crm>();
		for(Crm crm : dataRows) {
			completeMapCrm.put(crm.getObjectId(), crm);
		}
		for(PuntoRaccolta puntoRaccolta : mapPuntoRaccolta.values()) {
			Crm crm = completeMapCrm.get(puntoRaccolta.getCrm());
			if(crm != null) {
				resultMapCrm.put(crm.getObjectId(), crm);
			}
		}
		return resultMapCrm;
	}

	public static boolean validateAPIRequest(ServletRequest req, AppSetup appSetup,
			boolean draft, RepositoryManager storage) {
		boolean result = false;
		HttpServletRequest request = (HttpServletRequest) req;
		String uriPath = request.getRequestURI();
		if (uriPath != null && !uriPath.isEmpty()) {
			String tokenArrived = request.getHeader("X-ACCESS-TOKEN");
			if (tokenArrived != null && !tokenArrived.isEmpty()) {
				Token matchedToken = storage.findTokenByToken(tokenArrived, draft);
				if (matchedToken != null) {
					if (matchedToken.getExpiration() > 0) {
						//token exired
						if(matchedToken.getExpiration() < System.currentTimeMillis()) {
							return false;
						}
					}
					String ownerId = matchedToken.getName();
					DataSetInfo app = appSetup.findAppById(ownerId);
					//app config not found
					if(app == null) {
						return false;
					}
					//wrong API path
					if(!uriPath.contains(ownerId)) {
						return false;
					}
					// delegate resources to controller via request attribute map.
					if (matchedToken.getResources() != null	&& !matchedToken.getResources().isEmpty()) {
						req.setAttribute("resources",	matchedToken.getResources());
					}
					// check ( resources *)
					if (matchedToken.getPaths().contains("*") || matchedToken.getPaths().contains(uriPath)) {
						result = true;
					}
				}
			}
		}
		return result;
	}

	public static Map<String,String> handleError(Exception exception) {
		Map<String,String> errorMap = new HashMap<String,String>();
		errorMap.put(Const.ERRORTYPE, exception.getClass().toString());
		errorMap.put(Const.ERRORMSG, exception.getMessage());
		return errorMap;
	}
}
