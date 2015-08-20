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

package it.smartcommunitylab.riciclo.app.importer.converter;

import it.smartcommunitylab.riciclo.app.importer.model.Aree;
import it.smartcommunitylab.riciclo.app.importer.model.Gestori;
import it.smartcommunitylab.riciclo.app.importer.model.Istituzioni;
import it.smartcommunitylab.riciclo.app.importer.model.PuntiRaccolta;
import it.smartcommunitylab.riciclo.app.importer.model.TipologiaRaccolta;
import it.smartcommunitylab.riciclo.app.importer.model.TipologiaRifiuto;
import it.smartcommunitylab.riciclo.app.importer.model.TipologiaUtenza;
import it.smartcommunitylab.riciclo.controller.Utils;
import it.smartcommunitylab.riciclo.model.AppDataRifiuti;
import it.smartcommunitylab.riciclo.model.Area;
import it.smartcommunitylab.riciclo.model.CalendarioRaccolta;
import it.smartcommunitylab.riciclo.model.Crm;
import it.smartcommunitylab.riciclo.model.Categorie;
import it.smartcommunitylab.riciclo.model.Colore;
import it.smartcommunitylab.riciclo.model.Gestore;
import it.smartcommunitylab.riciclo.model.Istituzione;
import it.smartcommunitylab.riciclo.model.OrarioApertura;
import it.smartcommunitylab.riciclo.model.PuntoRaccolta;
import it.smartcommunitylab.riciclo.model.Raccolta;
import it.smartcommunitylab.riciclo.model.Riciclabolario;
import it.smartcommunitylab.riciclo.model.Rifiuto;
import it.smartcommunitylab.riciclo.model.Segnalazione;
import it.smartcommunitylab.riciclo.model.Tipologia;
import it.smartcommunitylab.riciclo.model.TipologiaProfilo;
import it.smartcommunitylab.riciclo.model.TipologiaPuntoRaccolta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class RifiutiConverter {
	private static final transient Logger logger = LoggerFactory.getLogger(RifiutiConverter.class);
	
	private static final String GETTONIERA = "GETTONIERA";
	private static final String RESIDUO = "RESIDUO";
	private static final String IMB_CARTA = "IMB_CARTA";
	private static final String IMB_PL_MET = "IMB_PL_MET";
	private static final String ORGANICO = "ORGANICO";
	private static final String IMB_VETRO = "IMB_VETRO";
	private static final String INDUMENTI = "INDUMENTI";
	
	private String defaultLang;
	
	public RifiutiConverter(String defaultLang) {
		this.defaultLang = defaultLang;
	}

	public AppDataRifiuti convert(Object input, String ownerId) throws Exception {
		it.smartcommunitylab.riciclo.app.importer.model.Rifiuti rifiuti = (it.smartcommunitylab.riciclo.app.importer.model.Rifiuti) input;
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		AppDataRifiuti output = new AppDataRifiuti();

		Categorie categorie = new Categorie();
		categorie.setOwnerId(ownerId);
		
		categorie.setCaratteristicaPuntoRaccolta(buildTipologieSet(new String[] { GETTONIERA, RESIDUO, IMB_CARTA, IMB_PL_MET, ORGANICO, IMB_VETRO, INDUMENTI }));

//		categorie.setColori(new HashSet<Tipologia>());
//		categorie.setTipologiaIstituzione(new HashSet<Tipologia>());
		categorie.setTipologiaRaccolta(new HashSet<Tipologia>());
		
		//TIPOLOGIAPUNTORACCOLTA
		List<TipologiaPuntoRaccolta> puntiRaccolta = Lists.newArrayList();
		categorie.setTipologiaPuntiRaccolta(new HashSet<Tipologia>());
		for (it.smartcommunitylab.riciclo.app.importer.model.TipologiaPuntoRaccolta tpr : rifiuti.getTipologiaPuntoRaccolta()) {
			Tipologia cat = new Tipologia(StringUtils.capitalize(tpr.getNome().toLowerCase()).replace("Crm", "CRM").replace("Crz", "CRZ"), tpr.getInfoPuntiRaccolta(), null);
			categorie.getTipologiaPuntiRaccolta().add(cat);
			
			TipologiaPuntoRaccolta tipologiaPuntoRaccolta = new TipologiaPuntoRaccolta();
			tipologiaPuntoRaccolta.setObjectId(cat.getNome());
			tipologiaPuntoRaccolta.getNome().put(defaultLang, tpr.getNome());
			tipologiaPuntoRaccolta.getInfo().put(defaultLang, tpr.getInfoPuntiRaccolta());
			tipologiaPuntoRaccolta.setType(tpr.getTipo());
			puntiRaccolta.add(tipologiaPuntoRaccolta);
		}
		output.setTipologiaPuntiRaccolta(puntiRaccolta);
		
		//TIPOLOGIARIFIUTO
		categorie.setTipologiaRifiuto(new HashSet<Tipologia>());
		for (TipologiaRifiuto tr : rifiuti.getTipologiaRifiuto()) {
			Tipologia cat = new Tipologia(StringUtils.capitalize(tr.getValore().toLowerCase()), null, null);
			categorie.getTipologiaRifiuto().add(cat);
		}
		
		//TIPOLOGIAUTENZA
		categorie.setTipologiaUtenza(new HashSet<Tipologia>());
		for (TipologiaUtenza tr : rifiuti.getTipologiaUtenza()) {
			Tipologia cat = new Tipologia(tr.getValore().toLowerCase(), null, null);
			categorie.getTipologiaUtenza().add(cat);
		}		

		output.setCategorie(categorie);
		
		//TIPOLOGIAPROFILO
		List<TipologiaProfilo> profili = Lists.newArrayList();
		for (it.smartcommunitylab.riciclo.app.importer.model.TipologiaProfilo pr : rifiuti.getTipologiaProfilo()) {
			TipologiaProfilo profilo = new TipologiaProfilo();
			profilo.setOwnerId(ownerId);
			profilo.setObjectId(pr.getNome());
			profilo.setTipologiaUtenza(pr.getTipologiaUtenza());
			profilo.getNome().put(defaultLang, pr.getNome());
			profilo.getDescrizione().put(defaultLang, pr.getDescrizione());
			profili.add(profilo);
		}
		output.setTipologiaProfili(profili);
		
		//AREE
		List<Area> aree = Lists.newArrayList();
		for (Aree ar : rifiuti.getAree()) {
			//Area area = mapper.convertValue(ar, Area.class);
			Area area = new Area();
			area.setObjectId(ar.getNome());
			area.setOwnerId(ownerId);
			area.setIstituzione(ar.getIstituzione());
			area.getNome().put(defaultLang, ar.getNome());
			area.setParent(ar.getParent());
			area.setGestore(ar.getGestore());
			area.getDescrizione().put(defaultLang, ar.getDescrizione());
			area.setEtichetta(ar.getEtichetta());
			area.setCodiceISTAT(ar.getCodiceIstat());
			
			Map<String, Boolean> utenza = Maps.newTreeMap();
			String[] utenze = null;
			boolean defaultValue = false;
			if (ar.getUtenze() == null) {
				defaultValue = true;
			} else {
				utenze = ar.getUtenze().split(";");
			}
			for (Tipologia ut: categorie.getTipologiaUtenza()) {
				utenza.put(ut.getNome(), defaultValue);
			}
			if (utenze != null) {
				for (String ut : utenze) {
					utenza.put(ut.trim(), true);
				}
			}
			area.setUtenza(utenza);
			
			aree.add(area);
		}
		output.setAree(aree);

		//GESTORI
		List<Gestore> gestori = Lists.newArrayList();
		for (Gestori gs : rifiuti.getGestori()) {
			Gestore gestore = new Gestore();
			gestore.setOwnerId(ownerId);
			gestore.setObjectId(gs.getRagioneSociale());
			gestore.setRagioneSociale(gs.getRagioneSociale());
			gestore.getDescrizione().put(defaultLang, gs.getDescrizione());
			gestore.setUfficio(gs.getUfficio());
			gestore.getIndirizzo().put(defaultLang, gs.getIndirizzo());
			gestore.getOrarioUfficio().put(defaultLang, gs.getOrarioUfficio());
			gestore.setSitoWeb(gs.getSitoWeb());
			gestore.setEmail(gs.getEmail());
			gestore.setTelefono(gs.getTelefono());
			gestore.setFax(gs.getFax());
			gestore.setGeocoding(Utils.convertLocalizzazione(gs.getLocalizzazione()));
			gestore.setFacebook(gs.getFacebook());
			gestori.add(gestore);
		}
		output.setGestori(gestori);
		
		//ISTITUZIONI
		List<Istituzione> istituzioni = Lists.newArrayList();
		for (Istituzioni is : rifiuti.getIstituzioni()) {
			Istituzione istituzione = new Istituzione();
			istituzione.setOwnerId(ownerId);
			istituzione.setObjectId(is.getNome() + " - " + is.getUfficio());
			istituzione.setNome(is.getNome());
			istituzione.getDescrizione().put(defaultLang, is.getDescrizione());
			istituzione.setUfficio(is.getUfficio());
			istituzione.getIndirizzo().put(defaultLang, is.getIndirizzo());
			istituzione.getOrarioUfficio().put(defaultLang, is.getOrarioUfficio());
			istituzione.setSito(is.getSito());
			istituzione.setPec(is.getPec());
			istituzione.setEmail(is.getEmail());
			istituzione.setTelefono(is.getTelefono());
			istituzione.setFax(is.getFax());
			istituzione.setFacebook(is.getFacebook());
			istituzione.setGeocoding(Utils.convertLocalizzazione(is.getLocalizzazione()));
			istituzioni.add(istituzione);
		}
		output.setIstituzioni(istituzioni);

		//RACCOLTE
		List<Raccolta> raccolte = Lists.newArrayList();
		for (it.smartcommunitylab.riciclo.app.importer.model.Raccolte rc : (List<it.smartcommunitylab.riciclo.app.importer.model.Raccolte>) rifiuti.getRaccolte()) {
			Raccolta raccolta = new Raccolta();
			raccolta.setOwnerId(ownerId);
			raccolta.setObjectId(UUID.randomUUID().toString());
			raccolta.setTipologiaPuntoRaccolta(StringUtils.capitalize(rc.getTipologiaPuntoRaccolta().toLowerCase()).replace("Crm", "CRM").replace("Crz", "CRZ").trim());
			raccolta.setTipologiaRifiuto(StringUtils.capitalize(rc.getTipologiaRifiuto().toLowerCase()).trim());
			raccolta.setTipologiaRaccolta(StringUtils.capitalize(rc.getTipologiaRaccolta().toLowerCase().replace("crm", "CRM").replace("crz", "CRZ")).trim());
			raccolta.setTipologiaUtenza(rc.getTipologiaUtenza());
			raccolta.setArea(rc.getArea());
			raccolta.setColore(rc.getColore());
			raccolta.getInfoRaccolta().put(defaultLang, rc.getInfoRaccolta());
			raccolte.add(raccolta);
			//categorie.getColori().add(new Tipologia(raccolta.getColore(), null, null));
		}
		output.setRaccolte(raccolte);

		//TIPOLOGIARACCOLTA
		for (TipologiaRaccolta tr : rifiuti.getTipologiaRaccolta()) {
			categorie.getTipologiaRaccolta().add(new Tipologia(StringUtils.capitalize(tr.getValore().toLowerCase().replace("crm", "CRM").replace("crz", "CRZ")).trim(), null, null));
		}
		
		//PUNTIRACCOLTA
		Map<String, Object> compactPuntiRaccolta = compactPuntiRaccolta(rifiuti.getPuntiRaccolta(), ownerId);
		output.setPuntiRaccolta((List<PuntoRaccolta>) compactPuntiRaccolta.get("puntiRaccolta"));
		output.setCrm((List<Crm>) compactPuntiRaccolta.get("crm"));
		output.setCalendariRaccolta((List<CalendarioRaccolta>) compactPuntiRaccolta.get("calendarioRaccolta"));
		
		//RIFIUTI e RICICLABOLARIO
		List<Rifiuto> rifiutoDescList = new ArrayList<Rifiuto>();
		List<Riciclabolario> riciclabolario = Lists.newArrayList();
		for (it.smartcommunitylab.riciclo.app.importer.model.Riciclabolario rc : 
			(List<it.smartcommunitylab.riciclo.app.importer.model.Riciclabolario>) rifiuti.getRiciclabolario()) {
			Rifiuto rifiuto = new Rifiuto();
			rifiuto.setOwnerId(ownerId);
			rifiuto.setObjectId(rc.getNome());
			rifiuto.getNome().put(defaultLang, rc.getNome());
			
			String[] split = StringUtils.split(rc.getTipologiaUtenza().trim().toLowerCase(),";");
			for(String tipologiaUtenza : split) {
				Riciclabolario ric = new Riciclabolario();
				ric.setObjectId(UUID.randomUUID().toString());
				ric.setOwnerId(ownerId);
				ric.setTipologiaRifiuto(StringUtils.capitalize(rc.getTipologiaRifiuto().trim().toLowerCase()));
				ric.setTipologiaUtenza(tipologiaUtenza.trim());
				ric.setArea(rc.getArea().trim());
				ric.setRifiuto(rifiuto.getObjectId());
				riciclabolario.add(ric);
			}
		}
		output.setRifiuti(rifiutoDescList);
		output.setRiciclabolario(riciclabolario);
		
		//COLORI
		List<Colore> colori = Lists.newArrayList();
		for (it.smartcommunitylab.riciclo.app.importer.model.Colori col: (List<it.smartcommunitylab.riciclo.app.importer.model.Colori>)rifiuti.getColori()) {
			Colore colore = mapper.convertValue(col, Colore.class);
			colori.add(colore);
		}
		output.setColore(colori);	
		
		//SEGNALAZIONI
		List<Segnalazione> segnalazioni = Lists.newArrayList();
		for (it.smartcommunitylab.riciclo.app.importer.model.Segnalazioni sgn: (List<it.smartcommunitylab.riciclo.app.importer.model.Segnalazioni>)rifiuti.getSegnalazioni()) {
			Segnalazione sg = mapper.convertValue(sgn, Segnalazione.class);
			segnalazioni.add(sg);
		}
		output.setSegnalazioni(segnalazioni);

		return output;
	}

	/**
	 * @param puntiRaccolta
	 * @param appId
	 * @return Map<String, Object> with</br>
	 * <ul>	
	 * 	<li>crm: List<Crm></li>
	 *	<li>puntiRaccolta: List&lt;PuntoRaccolta&gt;</li>
	 *	<li>calendarioRaccolta: List&lt;CalendarioRaccolta&gt;</li> 
	 * </ul>
	 * @throws Exception
	 */
	private Map<String, Object> compactPuntiRaccolta(List<PuntiRaccolta> puntiRaccolta, String appId) throws Exception {
		Multimap<String, PuntiRaccolta> puntiRaccoltaCRMMap = ArrayListMultimap.create();
		Multimap<String, PuntiRaccolta> puntiRaccoltaPPMap = ArrayListMultimap.create();
		for (PuntiRaccolta p : puntiRaccolta) {
			if(!Utils.isNull(p.getDettagliZona())) {
				String key = p.getArea() + "_" + p.getTipologiaPuntiRaccolta() + "_" + p.getTipologiaUtenza() + "_" + p.getZona() + "_" + p.getDettagliZona();
				puntiRaccoltaCRMMap.put(key, p);
			} else {
				String key = p.getArea() + "_" + p.getTipologiaPuntiRaccolta() + "_" + p.getTipologiaUtenza();
				puntiRaccoltaPPMap.put(key, p);
			}
		}

		Map<String, Crm> crmMap = new HashMap<String, Crm>();
		List<PuntoRaccolta> puntiRaccoltaList = Lists.newArrayList();
		for (String key : puntiRaccoltaCRMMap.keySet()) {
			if(logger.isInfoEnabled()) {
				logger.info("parse punto raccolta " + key);
			}
			List<OrarioApertura> orari = Lists.newArrayList();
			Crm crm = null;
			String tipologiaPuntoRaccolta = null;
			String tipologiaUtenza = null;
			String area = null;
			for (PuntiRaccolta pr : puntiRaccoltaCRMMap.get(key)) {
				area = pr.getArea();
				tipologiaUtenza = pr.getTipologiaUtenza();
				tipologiaPuntoRaccolta = StringUtils.capitalize(pr.getTipologiaPuntiRaccolta().toLowerCase()).replace("Crm", "CRM").replace("Crz", "CRZ");
				String crmKey = pr.getZona() + "_" + pr.getDettagliZona();
				crm = crmMap.get(crmKey);
				if(crm == null) {
					//new CRM
					crm = new Crm();
					crm.setObjectId(UUID.randomUUID().toString());
					crm.setOwnerId(appId);
					crm.getIndirizzo().put(defaultLang, pr.getZona() + " - " + pr.getDettagliZona());
					crm.setZona(pr.getZona());
					crm.setDettagliZona(pr.getDettagliZona());
					try {
						crm.setGeocoding(Utils.convertLocalizzazione(pr.getLocalizzazione()));
					} catch (Exception e) {
						logger.error("error parsing geocoding " + key);
						continue;
					}
					if(!Utils.isNull(pr.getNote())) {
						crm.getNote().put(defaultLang, pr.getNote());	
					}
					
					Map<String, Boolean> caratteristiche = Maps.newTreeMap();
					caratteristiche.put(GETTONIERA, Boolean.parseBoolean(pr.getGettoniera()));
					caratteristiche.put(RESIDUO, Boolean.parseBoolean(pr.getResiduo()));
					caratteristiche.put(IMB_CARTA, Boolean.parseBoolean(pr.getResiduo()));
					caratteristiche.put(IMB_PL_MET, Boolean.parseBoolean(pr.getResiduo()));
					caratteristiche.put(ORGANICO, Boolean.parseBoolean(pr.getResiduo()));
					caratteristiche.put(IMB_VETRO, Boolean.parseBoolean(pr.getResiduo()));
					caratteristiche.put(INDUMENTI, Boolean.parseBoolean(pr.getResiduo()));
					crm.setCaratteristiche(caratteristiche);
					
					crmMap.put(crmKey, crm);
				}
				if (pr.getDataDa() ==  null || pr.getDataDa().isEmpty() || pr.getDataA() ==  null || pr.getDataA().isEmpty() 
						|| pr.getIl() ==  null || pr.getIl().isEmpty()) {
					continue;
				}
				OrarioApertura oa = new OrarioApertura();
				oa.setAlle(pr.getAlle());
				oa.setDalle(pr.getDalle());
				oa.setDataA(pr.getDataA());
				oa.setDataDa(pr.getDataDa());
				oa.setIl(pr.getIl());
				oa.setEccezione(pr.getEccezione());
				orari.add(oa);
			}
			crm.setOrarioApertura(orari);
			//new PuntoRaccolta
			PuntoRaccolta puntoRaccolta = new PuntoRaccolta();
			puntoRaccolta.setOwnerId(appId);
			puntoRaccolta.setObjectId(UUID.randomUUID().toString());
			puntoRaccolta.setTipologiaPuntoRaccolta(tipologiaPuntoRaccolta);
			puntoRaccolta.setTipologiaUtenza(tipologiaUtenza);
			puntoRaccolta.setCrm(crm.getObjectId());
			puntoRaccolta.setArea(area);
			puntiRaccoltaList.add(puntoRaccolta);
		}
		
		List<CalendarioRaccolta> calendarioRaccoltaList = Lists.newArrayList();
		for(String key : puntiRaccoltaPPMap.keySet()) {
			if(logger.isInfoEnabled()) {
				logger.info("parse punto raccolta " + key);
			}
			List<OrarioApertura> orari = Lists.newArrayList();
			String tipologiaPuntoRaccolta = null;
			String tipologiaUtenza = null;
			String area = null;
			for (PuntiRaccolta pr : puntiRaccoltaPPMap.get(key)) {
				area = pr.getArea();
				tipologiaUtenza = pr.getTipologiaUtenza();
				tipologiaPuntoRaccolta = StringUtils.capitalize(pr.getTipologiaPuntiRaccolta().toLowerCase()).replace("Crm", "CRM").replace("Crz", "CRZ");
				if (pr.getDataDa() ==  null || pr.getDataDa().isEmpty() || pr.getDataA() ==  null || pr.getDataA().isEmpty() 
						|| pr.getIl() ==  null || pr.getIl().isEmpty()) {
					continue;
				}
				OrarioApertura oa = new OrarioApertura();
				oa.setAlle(pr.getAlle());
				oa.setDalle(pr.getDalle());
				oa.setDataA(pr.getDataA());
				oa.setDataDa(pr.getDataDa());
				oa.setIl(pr.getIl());
				oa.setEccezione(pr.getEccezione());
				if(!Utils.isNull(pr.getNote())) {
					oa.getNote().put(defaultLang, pr.getNote());
				}
				orari.add(oa);
			}
			if(!orari.isEmpty()) {
				CalendarioRaccolta calendarioRaccolta = new CalendarioRaccolta();
				calendarioRaccolta.setOwnerId(appId);
				calendarioRaccolta.setObjectId(UUID.randomUUID().toString());
				calendarioRaccolta.setTipologiaPuntoRaccolta(tipologiaPuntoRaccolta);
				calendarioRaccolta.setTipologiaUtenza(tipologiaUtenza);
				calendarioRaccolta.setArea(area);
				calendarioRaccolta.setOrarioApertura(orari);
				calendarioRaccoltaList.add(calendarioRaccolta);
			}
		}
		
		List<Crm> crmList = Lists.newArrayList();
		crmList.addAll(crmMap.values());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("crm", crmList);
		result.put("puntiRaccolta", puntiRaccoltaList);
		result.put("calendarioRaccolta", calendarioRaccoltaList);
		return result;
	}

	private static Set<Tipologia> buildTipologieSet(String[] cat) {
		Set<Tipologia> result = Sets.newHashSet();
		for (String c : cat) {
			Tipologia nc = new Tipologia(c, null, null);
			result.add(nc);
		}
		return result;
	}

}
