package it.smartcommunitylab.riciclo.riapp.importer;

import it.smartcommunitylab.riciclo.model.Area;
import it.smartcommunitylab.riciclo.model.CalendarioRaccolta;
import it.smartcommunitylab.riciclo.model.Categorie;
import it.smartcommunitylab.riciclo.model.Colore;
import it.smartcommunitylab.riciclo.model.Crm;
import it.smartcommunitylab.riciclo.model.OrarioApertura;
import it.smartcommunitylab.riciclo.model.PuntoRaccolta;
import it.smartcommunitylab.riciclo.model.Raccolta;
import it.smartcommunitylab.riciclo.model.Riciclabolario;
import it.smartcommunitylab.riciclo.model.Rifiuto;
import it.smartcommunitylab.riciclo.model.Tipologia;
import it.smartcommunitylab.riciclo.model.TipologiaProfilo;
import it.smartcommunitylab.riciclo.model.TipologiaPuntoRaccolta;
import it.smartcommunitylab.riciclo.model.TipologiaRaccolta;
import it.smartcommunitylab.riciclo.riapp.importer.model.RiappArea;
import it.smartcommunitylab.riciclo.riapp.importer.model.RiappAreaStruct;
import it.smartcommunitylab.riciclo.riapp.importer.model.RiappCentro;
import it.smartcommunitylab.riciclo.riapp.importer.model.RiappIstruzioni;
import it.smartcommunitylab.riciclo.riapp.importer.model.RiappOrario;
import it.smartcommunitylab.riciclo.riapp.importer.model.RiappRifiuto;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

public class RiappManager {
	private static final transient Logger logger = LoggerFactory.getLogger(RiappManager.class);
	
	@Autowired
	private RepositoryManager storage;
	
	private String defaultLang;
	private String baseDir;
	
	private RiappImportComuni riappImportComuni;
	private RiappImportCentri riappImportCentri;
	private RiappImportDizionario riappImportRifiuti;
	private RiappImportCalendario riappImportCalendario;
	private RiappImportTipologieRifiuto riappImportTipologieRifiuto;
	private RiappImportIstruzioni riappImportIstruzioni;
	
	public RiappManager(String defaultLang, String baseDir) {
		this.defaultLang = defaultLang;
		this.baseDir = baseDir;
		this.riappImportComuni = new RiappImportComuni(this.baseDir);
		this.riappImportCentri = new RiappImportCentri(this.baseDir);
		this.riappImportRifiuti = new RiappImportDizionario(this.baseDir);
		this.riappImportCalendario = new RiappImportCalendario(this.baseDir);
		this.riappImportTipologieRifiuto = new RiappImportTipologieRifiuto(this.baseDir);
		this.riappImportIstruzioni = new RiappImportIstruzioni(this.baseDir);
	}
	
	public void importData(String ownerId) {
		storage.resetDatasetDraft(ownerId);
		
		//<tipologiaRaccolta, colore>
		Map<String, String> tipologiaRaccoltaMap = new HashMap<String, String>();
		//<tipologiaRaccolta, tipologiaPuntoRaccolta>
		Map<String, String> tipologiaPuntoRaccoltaMap = new HashMap<String, String>();
		//<rifiuto, tipologiaRifiuto>
		Map<String, String> tipologiaRifiutoMap = new HashMap<String, String>();
		//<frazione, tipologiaRaccolta>
		Map<String, String> frazioneMap = new HashMap<String, String>();
		//<tipologiaRaccolta, RiappIstruzioni>
		Map<String, RiappIstruzioni> istruzioniMap = new HashMap<String, RiappIstruzioni>();
		try {
			List<String> fileCalendarioList = riappImportComuni.readListaValori(ownerId, "cal");
			for(String file : fileCalendarioList) {
				tipologiaRaccoltaMap.putAll(riappImportCalendario.readTipologiaRaccolta(file));
				tipologiaPuntoRaccoltaMap.putAll(riappImportCalendario.readTipologiaPuntoRaccolta(file));
				frazioneMap.putAll(riappImportCalendario.readFrazioni(file));
			}
			List<String> fileDizionarioList = riappImportComuni.readListaValori(ownerId, "diz");
			for(String file : fileDizionarioList) {
				tipologiaRifiutoMap.putAll(riappImportTipologieRifiuto.readListaRifiuti(file));
			}
			List<String> fileIstruzioniList = riappImportComuni.readListaValori(ownerId, "ist");
			for(String file : fileIstruzioniList) {
				istruzioniMap.putAll(riappImportIstruzioni.readIstruzioni(file));
			}
		} catch (Exception e) {
			logger.error("error", e);
		}
		
		Categorie categorie = importTipologie(ownerId, tipologiaRaccoltaMap, tipologiaPuntoRaccoltaMap, 
				tipologiaRifiutoMap, istruzioniMap);
		List<Colore> listaColori = importColori(ownerId);
		RiappAreaStruct areaStruct = importAree(ownerId);
		//<comuneEsteso, Area>
		Map<String, Area> comuneEstesoMap = areaStruct.getComuneEstesoMap();
		//<comune, Area>
		Map<String, Area> comuneSubAreaMap = areaStruct.getComuneSubAreaMap();
		//<cal, List<Area>>
		Map<String, List<Area>> calendarioAreaMap = areaStruct.getCalendarioAreaMap();
		//<objectId, Crm>
		Map<String, Crm> centriMap = importCentri(ownerId, comuneEstesoMap);
		//<obecjectId, Rifiuto>
		Map<String, RiappRifiuto> rifiutiMap = importRifiuti(ownerId, comuneEstesoMap, tipologiaRifiutoMap);
		importRaccolta(ownerId, comuneEstesoMap, tipologiaRaccoltaMap, tipologiaPuntoRaccoltaMap, tipologiaRifiutoMap, rifiutiMap);
		importCalendario(ownerId, calendarioAreaMap, frazioneMap, tipologiaPuntoRaccoltaMap);
	}
	
	private List<Colore> importColori(String ownerId) {
		List<Colore> result = Lists.newArrayList();
		
		Colore marr = new Colore();
		marr.setOwnerId(ownerId);
		marr.setNome("marr");
		marr.setCodice("#663300");
		storage.addColore(marr, true);
		result.add(marr);
		
		Colore blu = new Colore();
		blu.setOwnerId(ownerId);
		blu.setNome("blu");
		blu.setCodice("#0000FF");
		storage.addColore(blu, true);
		result.add(blu);
		
		Colore nero = new Colore();
		nero.setOwnerId(ownerId);
		nero.setNome("nero");
		nero.setCodice("#000000");
		storage.addColore(nero, true);
		result.add(nero);
		
		Colore giallo = new Colore();
		giallo.setOwnerId(ownerId);
		giallo.setNome("giallo");
		giallo.setCodice("#FFFF00");
		storage.addColore(giallo, true);
		result.add(giallo);
		
		Colore verde = new Colore();
		verde.setOwnerId(ownerId);
		verde.setNome("verde");
		verde.setCodice("#00CC00");
		storage.addColore(verde, true);
		result.add(verde);
		
		Colore bianco = new Colore();
		bianco.setOwnerId(ownerId);
		bianco.setNome("bianco");
		bianco.setCodice("#FFFFFF");
		storage.addColore(bianco, true);
		result.add(bianco);
		
		Colore azzu = new Colore();
		azzu.setOwnerId(ownerId);
		azzu.setNome("azzu");
		azzu.setCodice("#00FFFF");
		storage.addColore(azzu, true);
		result.add(azzu);
		
		return result;
	}
	
	private Categorie importTipologie(String ownerId, Map<String, String> tipologiaRaccoltaMap, 
			Map<String, String> tipologiaPuntoRaccoltaMap, Map<String, String> tipologiaRifiutoMap, 
			Map<String, RiappIstruzioni> istruzioniMap) {
		Categorie categorie = new Categorie();
		categorie.setOwnerId(ownerId);
		
		try {
			//tipologiaUtenza
			Tipologia tipologiaUtenzaDomestica = new Tipologia();
			tipologiaUtenzaDomestica.setObjectId(Const.UTENZA_DOMESTICA);
			tipologiaUtenzaDomestica.getNome().put(defaultLang, Const.UTENZA_DOMESTICA);
			categorie.getTipologiaUtenza().add(tipologiaUtenzaDomestica);
			storage.updateTipologie(ownerId, categorie.getTipologiaUtenza(), "tipologiaUtenza", true);
			
			//tipologiaRifiuto
			Tipologia rifiutoEcocentro = new Tipologia();
			rifiutoEcocentro.setObjectId(Const.TRIF_ECOCENTRO);
			rifiutoEcocentro.getNome().put(defaultLang, Const.TRIF_ECOCENTRO);
			rifiutoEcocentro.setIcona(RiappImportTipologieRifiuto.getIconaTipologiaRifiuto(Const.TRIF_ECOCENTRO));
			categorie.getTipologiaRifiuto().add(rifiutoEcocentro);
			for(String tipologiaRifiuto : tipologiaRifiutoMap.values()) {
				Tipologia tipologia = new Tipologia();
				tipologia.setObjectId(tipologiaRifiuto);
				tipologia.getNome().put(defaultLang, tipologiaRifiuto);
				tipologia.setIcona(RiappImportTipologieRifiuto.getIconaTipologiaRifiuto(tipologiaRifiuto));
				categorie.getTipologiaRifiuto().add(tipologia);
			}
			storage.updateTipologie(ownerId, categorie.getTipologiaRifiuto(), "tipologiaRifiuto", true);
			
			//tipologiaRaccolta
			TipologiaRaccolta tipologiaContenitoreSpecifico = new TipologiaRaccolta();
			tipologiaContenitoreSpecifico.setObjectId(Const.TRAC_CS);
			tipologiaContenitoreSpecifico.getNome().put(defaultLang, Const.TRAC_CS);
			categorie.getTipologiaRaccolta().add(tipologiaContenitoreSpecifico);
			for(String tipologiaRaccolta : tipologiaRaccoltaMap.keySet()) {
				TipologiaRaccolta tipologia = new TipologiaRaccolta();
				tipologia.setObjectId(tipologiaRaccolta);
				tipologia.getNome().put(defaultLang, tipologiaRaccolta);
				RiappIstruzioni istruzioni = RiappImportIstruzioni.getIstruzioni(tipologiaRaccolta, istruzioniMap);
				if(istruzioni != null) {
					tipologia.getComeConferire().put(defaultLang, istruzioni.getComeConferire());
					tipologia.getPrestaAttenzione().put(defaultLang, istruzioni.getPrestaAttenzione());
				}
				categorie.getTipologiaRaccolta().add(tipologia);
			}
			storage.updateTipologieRaccolta(ownerId, categorie.getTipologiaRaccolta(), "tipologiaRaccolta", true);
			
			//tipologiaPuntoRaccolta
			TipologiaPuntoRaccolta ecocentro = new TipologiaPuntoRaccolta();
			ecocentro.setOwnerId(ownerId);
			ecocentro.setObjectId(Const.TPR_ECOCENTRO);
			ecocentro.getNome().put(defaultLang, Const.TPR_ECOCENTRO);
			ecocentro.setType(it.smartcommunitylab.riciclo.controller.Const.PUNTO_RACCOLTA_CR);
			ecocentro.setIcona(Const.ICO_CRM);
			storage.addTipologiaPuntoRaccolta(ecocentro, true);
			for(String tipologiaPuntoRaccolta : tipologiaPuntoRaccoltaMap.values()) {
				TipologiaPuntoRaccolta tipologia = new TipologiaPuntoRaccolta();
				tipologia.setOwnerId(ownerId);
				tipologia.setObjectId(tipologiaPuntoRaccolta);
				tipologia.getNome().put(defaultLang, tipologiaPuntoRaccolta);
				tipologia.setType(it.smartcommunitylab.riciclo.controller.Const.PUNTO_RACCOLTA_PP);
				tipologia.setIcona(Const.ICO_PORTA_A_PORTA);
				storage.addTipologiaPuntoRaccolta(tipologia, true);
			}
			
			//tipologiaProfilo
			TipologiaProfilo tipologiaProfilo = new TipologiaProfilo();
			tipologiaProfilo.setOwnerId(ownerId);
			tipologiaProfilo.setObjectId(Const.UTENZA_DOMESTICA);
			tipologiaProfilo.getNome().put(defaultLang, Const.UTENZA_DOMESTICA);
			tipologiaProfilo.setTipologiaUtenza(Const.UTENZA_DOMESTICA);
			storage.addTipologiaProfilo(tipologiaProfilo, true);
			
		} catch (Exception e) {
			logger.error("error", e);
		}
		return categorie;
	}
	
	/**
	 * 
	 * @param ownerId
	 * @return Map<comuneEsteso, Area> 
	 */
	private RiappAreaStruct importAree(String ownerId) {
		//<comuneEsteso, Area>
		Map<String, Area> comuneEstesoMap = new HashMap<String, Area>();
		//<comune, Area>
		Map<String, Area> comuneSubAreaMap = new HashMap<String, Area>();
		//<cal, List<Area>>
		Map<String, List<Area>> calendarioAreaMap = new HashMap<String, List<Area>>();
		
		RiappAreaStruct result = new RiappAreaStruct();
		result.setComuneEstesoMap(comuneEstesoMap);
		result.setComuneSubAreaMap(comuneSubAreaMap);
		result.setCalendarioAreaMap(calendarioAreaMap);
		try {
			List<String> comuneEstesoList = riappImportComuni.readListaComuni(ownerId);
			List<RiappArea> areaList = riappImportComuni.readListaAree(ownerId);
			//create first level area
			for(String comuneEsteso : comuneEstesoList) {
				Area area = new Area();
				area.setObjectId(Utils.generateUUID());
				area.setOwnerId(ownerId);
				area.setNome(comuneEsteso);
				area.getUtenza().put(Const.UTENZA_DOMESTICA, true);
				//TODO area.setIstituzione(istituzione);
				//TODO area.setGestore(gestore);
				storage.addArea(area, true);
				comuneEstesoMap.put(comuneEsteso, area);
			}
			//create other area
			for(RiappArea riappArea : areaList) {
				Area area = new Area();
				area.setObjectId(riappArea.getComune());
				area.setOwnerId(ownerId);
				area.setNome(riappArea.getComune());
				Area parentArea = comuneEstesoMap.get(riappArea.getComuneEsteso());
				if(parentArea != null) {
					area.setParent(parentArea.getObjectId());
				}
				area.getEtichetta().put(defaultLang, riappArea.getComuneEsteso() + " " + riappArea.getZonaEsteso());
				storage.addArea(area, true);
				comuneSubAreaMap.put(riappArea.getComune(), area);
				List<Area> calendarioAreaList = calendarioAreaMap.get(riappArea.getCal());
				if(calendarioAreaList== null) {
					calendarioAreaList = Lists.newArrayList();
					calendarioAreaMap.put(riappArea.getCal(), calendarioAreaList);
				}
				calendarioAreaList.add(area);
			}
		} catch (Exception e) {
			logger.error("error", e);
		}
		return result;
	}
	
	private Map<String, Crm> importCentri(String ownerId, Map<String, Area> comuneEstesoMap) {
		Map<String, Crm> result = new HashMap<String, Crm>();
		try {
			List<String> fileList = riappImportComuni.readListaValori(ownerId, "cen");
			for(String file : fileList) {
				List<RiappCentro> riappCentroList = riappImportCentri.readListaCentri(file);
				for(RiappCentro riappCentro : riappCentroList) {
					//create crm
					Crm crm = new Crm();
					crm.setOwnerId(ownerId);
					crm.setObjectId(Utils.generateUUID());
					crm.setTipologiaPuntoRaccolta(Const.TPR_ECOCENTRO);
					crm.setZona(riappCentro.getNome());
					crm.setDettagliZona(riappCentro.getIndirizzo());
					crm.setGeocoding(new double[] { riappCentro.getLongitudine(), riappCentro.getLatitudine() });
					crm.getNote().put(defaultLang, riappCentro.getInfo());
					crm.getAccesso().put(defaultLang, riappCentro.getAccesso());
					//TODO crm.setCaratteristiche();
					crm.setOrarioApertura(RiappImportCentri.convertOrario(riappCentro));
					storage.addCRM(crm, true);
					result.put(crm.getObjectId(), crm);
					//create puntoRaccolta for every comune
					for(Area area : comuneEstesoMap.values()) {
						PuntoRaccolta puntoRaccolta = new PuntoRaccolta();
						puntoRaccolta.setOwnerId(ownerId);
						puntoRaccolta.setObjectId(Utils.generateUUID());
						puntoRaccolta.setTipologiaPuntoRaccolta(Const.TPR_ECOCENTRO);
						puntoRaccolta.setTipologiaUtenza(Const.UTENZA_DOMESTICA);
						puntoRaccolta.setArea(area.getObjectId());
						puntoRaccolta.setCrm(crm.getObjectId());
						storage.addPuntoRaccolta(puntoRaccolta, true);
					}
				}
			}
		} catch (Exception e) {
			logger.error("error", e);
		}
		return result;
	}
	
	/**
	 * 
	 * @param ownerId
	 * @param comuneEstesoMap
	 * @param tipologiaRifiutoMap 
	 * @return <rifiuto, RiappRifiuto>
	 */
	private Map<String, RiappRifiuto> importRifiuti(String ownerId, Map<String, Area> comuneEstesoMap, 
			Map<String, String> tipologiaRifiutoMap) {
		Map<String, RiappRifiuto> result = new HashMap<String, RiappRifiuto>();
		try {
			List<String> fileList = riappImportComuni.readListaValori(ownerId, "diz");
			for(String file : fileList) {
				List<RiappRifiuto> riappListaRifiuti = riappImportRifiuti.readListaRifiuti(file);
				for(RiappRifiuto riappRifiuto : riappListaRifiuti) {
					Rifiuto rifiuto = new Rifiuto();
					rifiuto.setOwnerId(ownerId);
					rifiuto.setObjectId(Utils.generateUUID());
					rifiuto.getNome().put(defaultLang, riappRifiuto.getRifiuto());
					storage.addRifiuto(rifiuto, true);
					result.put(riappRifiuto.getRifiuto(), riappRifiuto);
					//create Riciclabolario
					String tipologiaRifiuto = RiappImportDizionario.getTipologiaRifiuto(riappRifiuto, tipologiaRifiutoMap);
					if(Utils.isNotEmpty(tipologiaRifiuto)) {
						for(Area area : comuneEstesoMap.values()) {
							Riciclabolario riciclabolario = new Riciclabolario();
							riciclabolario.setOwnerId(ownerId);
							riciclabolario.setObjectId(Utils.generateUUID());
							riciclabolario.setRifiuto(rifiuto.getObjectId());
							riciclabolario.setArea(area.getObjectId());
							riciclabolario.setTipologiaUtenza(Const.UTENZA_DOMESTICA);
							riciclabolario.setTipologiaRifiuto(tipologiaRifiuto);
							storage.addRiciclabolario(riciclabolario, true);
						}
					} else {
						if(logger.isWarnEnabled()) {
							logger.warn("importRifiuti: tipologiaRifiuto not found for " + riappRifiuto.getRifiuto());
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("error", e);
		}
		return result;
	}
	
	private void importRaccolta(String ownerId, Map<String, Area> comuneEstesoMap, Map<String, String> tipologiaRaccoltaMap, 
			Map<String, String> tipologiaPuntoRaccoltaMap, Map<String, String> tipologiaRifiutoMap, 
			Map<String, RiappRifiuto> rifiutiMap) {
		try {
			//<tipologiaRifiuto, [tipologiaRaccolta]>
			Map<String, List<String>> raccoltaMap = new HashMap<String, List<String>>(); 
			for(RiappRifiuto riappRifiuto : rifiutiMap.values()) {
				String tipologiaRifiuto = RiappImportDizionario.getTipologiaRifiuto(riappRifiuto, tipologiaRifiutoMap);
				List<String> tipologieRaccolta = RiappImportDizionario.getTipologieRaccolta(riappRifiuto, tipologiaRaccoltaMap);
				if(Utils.isEmpty(tipologiaRifiuto) || (tipologieRaccolta.size() == 0)) {
					continue;
				}
				List<String> tipologiaRaccoltaList = raccoltaMap.get(tipologiaRifiuto);
				if(tipologiaRaccoltaList == null) {
					tipologiaRaccoltaList = Lists.newArrayList();
					raccoltaMap.put(tipologiaRifiuto, tipologiaRaccoltaList);
				}
				for(String tipologiaRaccolta : tipologieRaccolta) {
					if(!tipologiaRaccoltaList.contains(tipologiaRaccolta)) {
						tipologiaRaccoltaList.add(tipologiaRaccolta);
					}
				}
				if(logger.isInfoEnabled()) {
					logger.info(String.format("importRaccolta - %s;%s;%s", riappRifiuto.getRifiuto(), tipologiaRifiuto, tipologieRaccolta));
				}
			}
			Raccolta raccolta = null;
			String colore = null;
			for(Area area : comuneEstesoMap.values()) {
				//add relation for ecocentro
				raccolta = new Raccolta();
				raccolta.setOwnerId(ownerId);
				raccolta.setObjectId(Utils.generateUUID());
				raccolta.setArea(area.getObjectId());
				raccolta.setTipologiaUtenza(Const.UTENZA_DOMESTICA);
				raccolta.setTipologiaRifiuto(Const.TRIF_ECOCENTRO);
				raccolta.setTipologiaPuntoRaccolta(Const.TPR_ECOCENTRO);
				raccolta.setTipologiaRaccolta(Const.TRAC_CS);
				colore = tipologiaRaccoltaMap.get(Const.TRAC_CS);
				if(colore != null) {
					raccolta.setColore(colore);
				}
				storage.addRaccolta(raccolta, true);
				
				List<String> tipologiaRifiutoDone = Lists.newArrayList();
				for(String tipologiaRifiuto : tipologiaRifiutoMap.values()) {
					if(tipologiaRifiutoDone.contains(tipologiaRifiuto)) {
						continue;
					}
					tipologiaRifiutoDone.add(tipologiaRifiuto);
					List<String> tipologiaRaccoltaList = raccoltaMap.get(tipologiaRifiuto);
					if(tipologiaRaccoltaList != null) {
						for(String tipologiaRaccolta : tipologiaRaccoltaList) {
							String tipologiaPuntoRaccolta = null;
							if(Const.TRAC_CS.equals(tipologiaRaccolta)) {
								tipologiaPuntoRaccolta = Const.TPR_ECOCENTRO;
							} else {
								tipologiaPuntoRaccolta = tipologiaPuntoRaccoltaMap.get(tipologiaRaccolta); 
							}
							colore = tipologiaRaccoltaMap.get(tipologiaRaccolta);
							if(Utils.isNotEmpty(tipologiaPuntoRaccolta)) {
								raccolta = new Raccolta();
								raccolta.setOwnerId(ownerId);
								raccolta.setObjectId(Utils.generateUUID());
								raccolta.setArea(area.getObjectId());
								raccolta.setTipologiaUtenza(Const.UTENZA_DOMESTICA);
								raccolta.setTipologiaRifiuto(tipologiaRifiuto);
								raccolta.setTipologiaPuntoRaccolta(tipologiaPuntoRaccolta);
								raccolta.setTipologiaRaccolta(tipologiaRaccolta);
								if(colore != null) {
									raccolta.setColore(colore);
								}
								storage.addRaccolta(raccolta, true);
							}						
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("error", e);
		}
	}
	
	private void importCalendario(String ownerId, Map<String, List<Area>> calendarioAreaMap, 
			Map<String, String> frazioneMap, Map<String, String> tipologiaPuntoRaccoltaMap) {
		try {
			List<String> fileList = riappImportComuni.readListaValori(ownerId, "cal");
			for(String file : fileList) {
				List<RiappOrario> orarioList = riappImportCalendario.readCalendario(file);
				List<Area> calendarioAreaList = calendarioAreaMap.get(file);
				if(calendarioAreaList == null) {
					if(logger.isWarnEnabled()) {
						logger.warn("calendarioAreaList not found for " + file);
					}
				} else {
					for(Area area : calendarioAreaList) {
						if(logger.isInfoEnabled()) {
							logger.info(String.format("convert %s - %s", file, area.toString()));
						}
						//<tipologiaPuntoraccolta, CalendarioRaccolta>
						Map<String, CalendarioRaccolta> calendarioMap = new HashMap<String, CalendarioRaccolta>();
						for(RiappOrario riappOrario : orarioList) {
							if(logger.isInfoEnabled()) {
								logger.info(riappOrario.toString());
							}
							convertOrario(ownerId, riappOrario, calendarioMap, area, frazioneMap, tipologiaPuntoRaccoltaMap);
						}
						for(CalendarioRaccolta calendarioRaccolta : calendarioMap.values()) {
							storage.addCalendarioRaccolta(calendarioRaccolta, true);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("error", e);
		}
	}
	
	private void convertOrario(String ownerId, RiappOrario riappOrario, Map<String, CalendarioRaccolta> calendarioMap, 
			Area area, Map<String, String> frazioneMap, Map<String, String> tipologiaPuntoRaccoltaMap) {
		List<String> tipologiaPuntoRaccoltaList = Lists.newArrayList();
		if(Utils.isNotEmpty(riappOrario.getCa()) && riappOrario.getCa().equals("S")) {
			String tipologiaPuntoRaccolta = getTipologiaPuntoRaccolta("carta", frazioneMap, tipologiaPuntoRaccoltaMap);
			if(Utils.isNotEmpty(tipologiaPuntoRaccolta)) {
				tipologiaPuntoRaccoltaList.add(tipologiaPuntoRaccolta);
			}
		} 
		if(Utils.isNotEmpty(riappOrario.getVe()) && riappOrario.getVe().equals("S")) {
			String tipologiaPuntoRaccolta = getTipologiaPuntoRaccolta("vetro", frazioneMap, tipologiaPuntoRaccoltaMap);
			if(Utils.isNotEmpty(tipologiaPuntoRaccolta)) {
				tipologiaPuntoRaccoltaList.add(tipologiaPuntoRaccolta);
			}
		} 
		if(Utils.isNotEmpty(riappOrario.getOr()) && riappOrario.getOr().equals("S")) {
			String tipologiaPuntoRaccolta = getTipologiaPuntoRaccolta("organico", frazioneMap, tipologiaPuntoRaccoltaMap);
			if(Utils.isNotEmpty(tipologiaPuntoRaccolta)) {
				tipologiaPuntoRaccoltaList.add(tipologiaPuntoRaccolta);
			}
		} 
		if(Utils.isNotEmpty(riappOrario.getPl()) && riappOrario.getPl().equals("S")) {
			String tipologiaPuntoRaccolta = getTipologiaPuntoRaccolta("plastica", frazioneMap, tipologiaPuntoRaccoltaMap);
			if(Utils.isNotEmpty(tipologiaPuntoRaccolta)) {
				tipologiaPuntoRaccoltaList.add(tipologiaPuntoRaccolta);
			}
		} 
		if(Utils.isNotEmpty(riappOrario.getInd()) && riappOrario.getInd().equals("S")) {
			String tipologiaPuntoRaccolta = getTipologiaPuntoRaccolta("indiff", frazioneMap, tipologiaPuntoRaccoltaMap);
			if(Utils.isNotEmpty(tipologiaPuntoRaccolta)) {
				tipologiaPuntoRaccoltaList.add(tipologiaPuntoRaccolta);
			}
		} 
		if(Utils.isNotEmpty(riappOrario.getAl1()) && riappOrario.getAl1().equals("S")) {
			String tipologiaPuntoRaccolta = getTipologiaPuntoRaccolta("altro1", frazioneMap, tipologiaPuntoRaccoltaMap);
			if(Utils.isNotEmpty(tipologiaPuntoRaccolta)) {
				tipologiaPuntoRaccoltaList.add(tipologiaPuntoRaccolta);
			}
		} 
		if(Utils.isNotEmpty(riappOrario.getAl2()) && riappOrario.getAl2().equals("S")) {
			String tipologiaPuntoRaccolta = getTipologiaPuntoRaccolta("altro2", frazioneMap, tipologiaPuntoRaccoltaMap);
			if(Utils.isNotEmpty(tipologiaPuntoRaccolta)) {
				tipologiaPuntoRaccoltaList.add(tipologiaPuntoRaccolta);
			}
		} 
		if(Utils.isNotEmpty(riappOrario.getAl3()) && riappOrario.getAl3().equals("S")) {
			String tipologiaPuntoRaccolta = getTipologiaPuntoRaccolta("altro3", frazioneMap, tipologiaPuntoRaccoltaMap);
			if(Utils.isNotEmpty(tipologiaPuntoRaccolta)) {
				tipologiaPuntoRaccoltaList.add(tipologiaPuntoRaccolta);
			}
		}
		if(tipologiaPuntoRaccoltaList.size() == 0) {
			if(logger.isWarnEnabled()) {
				logger.warn("tipologiaPuntoRaccolta not found");
			}
			return;
		}
		
		for(String tipologiaPuntoRaccolta : tipologiaPuntoRaccoltaList) {
			CalendarioRaccolta calendarioRaccolta = calendarioMap.get(tipologiaPuntoRaccolta);
			if(calendarioRaccolta == null) {
				calendarioRaccolta = new CalendarioRaccolta();
				calendarioRaccolta.setOwnerId(ownerId);
				calendarioRaccolta.setObjectId(Utils.generateUUID());
				calendarioRaccolta.setTipologiaUtenza(Const.UTENZA_DOMESTICA);
				calendarioRaccolta.setArea(area.getObjectId());
				calendarioRaccolta.setTipologiaPuntoRaccolta(tipologiaPuntoRaccolta);
				calendarioMap.put(tipologiaPuntoRaccolta, calendarioRaccolta);
			}
			
			OrarioApertura orarioApertura = null;
			if(calendarioRaccolta.getOrarioApertura().size() == 0) {
				orarioApertura = new OrarioApertura();
				orarioApertura.setDataDa("2016-01-01");
				orarioApertura.setDataA("2016-12-31");
				orarioApertura.setIl("");
				calendarioRaccolta.getOrarioApertura().add(orarioApertura);
			} else {
				orarioApertura = calendarioRaccolta.getOrarioApertura().get(0);
			}
			
			String il = riappOrario.getNum() + "/" + riappOrario.getMese() + "/20" + riappOrario.getAnno() + " ";
			orarioApertura.setIl(orarioApertura.getIl() + il);
		}
	}

	private String getTipologiaPuntoRaccolta(String frazione, Map<String, String> frazioneMap,
			Map<String, String> tipologiaPuntoRaccoltaMap) {
		String tipologiaPuntoRaccolta = null;
		String tipologiaRaccolta = frazioneMap.get(frazione);
		if(tipologiaRaccolta != null) {
			tipologiaPuntoRaccolta = tipologiaPuntoRaccoltaMap.get(tipologiaRaccolta);
		}
		return tipologiaPuntoRaccolta;
	}
}
