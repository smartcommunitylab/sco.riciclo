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
import it.smartcommunitylab.riciclo.app.importer.model.TipologiaPuntoRaccolta;
import it.smartcommunitylab.riciclo.app.importer.model.TipologiaRaccolta;
import it.smartcommunitylab.riciclo.app.importer.model.TipologiaRifiuto;
import it.smartcommunitylab.riciclo.app.importer.model.TipologiaUtenza;
import it.smartcommunitylab.riciclo.model.Area;
import it.smartcommunitylab.riciclo.model.Categorie;
import it.smartcommunitylab.riciclo.model.Colore;
import it.smartcommunitylab.riciclo.model.Gestore;
import it.smartcommunitylab.riciclo.model.Istituzione;
import it.smartcommunitylab.riciclo.model.OrarioApertura;
import it.smartcommunitylab.riciclo.model.PuntoRaccolta;
import it.smartcommunitylab.riciclo.model.Raccolta;
import it.smartcommunitylab.riciclo.model.Riciclabolario;
import it.smartcommunitylab.riciclo.model.Rifiuti;
import it.smartcommunitylab.riciclo.model.Segnalazione;
import it.smartcommunitylab.riciclo.model.Tipologia;
import it.smartcommunitylab.riciclo.model.TipologiaProfilo;
import it.smartcommunitylab.riciclo.model.UtenzaArea;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class RifiutiConverter {

	private static final String GETTONIERA = "GETTONIERA";
	private static final String RESIDUO = "RESIDUO";
	private static final String IMB_CARTA = "IMB_CARTA";
	private static final String IMB_PL_MET = "IMB_PL_MET";
	private static final String ORGANICO = "ORGANICO";
	private static final String IMB_VETRO = "IMB_VETRO";
	private static final String INDUMENTI = "INDUMENTI";

	public Rifiuti convert(Object input, String appId) throws Exception {
		it.smartcommunitylab.riciclo.app.importer.model.Rifiuti rifiuti = (it.smartcommunitylab.riciclo.app.importer.model.Rifiuti) input;
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		Rifiuti output = new Rifiuti();

		Categorie categorie = new Categorie();
		categorie.setAppId(appId);
		
		categorie.setCaratteristicaPuntoRaccolta(buildTipologieSet(new String[] { GETTONIERA, RESIDUO, IMB_CARTA, IMB_PL_MET, ORGANICO, IMB_VETRO, INDUMENTI }));

//		categorie.setColori(new HashSet<Tipologia>());
//		categorie.setTipologiaIstituzione(new HashSet<Tipologia>());
		categorie.setTipologiaRaccolta(new HashSet<Tipologia>());
		
		categorie.setTipologiaPuntiRaccolta(new HashSet<Tipologia>());
		for (TipologiaPuntoRaccolta tpr : rifiuti.getTipologiaPuntoRaccolta()) {
			Tipologia cat = new Tipologia(StringUtils.capitalize(tpr.getNome().toLowerCase()).replace("Crm", "CRM").replace("Crz", "CRZ"), tpr.getInfoPuntiRaccolta(), null);
			categorie.getTipologiaPuntiRaccolta().add(cat);
		}

		categorie.setTipologiaRifiuto(new HashSet<Tipologia>());
		for (TipologiaRifiuto tr : rifiuti.getTipologiaRifiuto()) {
			Tipologia cat = new Tipologia(StringUtils.capitalize(tr.getValore().toLowerCase()), null, null);
			categorie.getTipologiaRifiuto().add(cat);
		}
		
		categorie.setTipologiaUtenza(new HashSet<Tipologia>());
		for (TipologiaUtenza tr : rifiuti.getTipologiaUtenza()) {
			Tipologia cat = new Tipologia(tr.getValore().toLowerCase(), null, null);
			categorie.getTipologiaUtenza().add(cat);
		}		
		

		output.setCategorie(categorie);

		List<TipologiaProfilo> profili = Lists.newArrayList();
		for (it.smartcommunitylab.riciclo.app.importer.model.TipologiaProfilo pr : rifiuti.getTipologiaProfilo()) {
			TipologiaProfilo profilo = mapper.convertValue(pr, TipologiaProfilo.class);
			profilo.setAppId(appId);
			profili.add(profilo);
		}
		output.setTipologiaProfilo(profili);
		
		List<Area> aree = Lists.newArrayList();
		for (Aree ar : rifiuti.getAree()) {
			Area area = mapper.convertValue(ar, Area.class);
			area.setAppId(appId);
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

		List<Gestore> gestori = Lists.newArrayList();
		for (Gestori gs : rifiuti.getGestori()) {
			Gestore gestore = mapper.convertValue(gs, Gestore.class);
			gestore.setAppId(appId);
			gestori.add(gestore);
		}
		output.setGestori(gestori);

		List<Istituzione> istituzioni = Lists.newArrayList();
		for (Istituzioni is : rifiuti.getIstituzioni()) {
			Istituzione istituzione = mapper.convertValue(is, Istituzione.class);
			istituzione.setAppId(appId);
			istituzioni.add(istituzione);
//			categorie.getTipologiaIstituzione().add(new Tipologia(is.getTipologia(), null, null));
		}
		output.setIstituzioni(istituzioni);

		List<Raccolta> raccolte = Lists.newArrayList();
		for (it.smartcommunitylab.riciclo.app.importer.model.Raccolte rc : (List<it.smartcommunitylab.riciclo.app.importer.model.Raccolte>) rifiuti.getRaccolte()) {
			Raccolta raccolta = mapper.convertValue(rc, Raccolta.class);
			raccolta.setTipologiaPuntoRaccolta(StringUtils.capitalize(raccolta.getTipologiaPuntoRaccolta().toLowerCase()).replace("Crm", "CRM").replace("Crz", "CRZ").trim());
			raccolta.setTipologiaRifiuto(StringUtils.capitalize(raccolta.getTipologiaRifiuto().toLowerCase()).trim());
			raccolta.setTipologiaRaccolta(StringUtils.capitalize(raccolta.getTipologiaRaccolta().toLowerCase().replace("crm", "CRM").replace("crz", "CRZ")).trim());
			raccolta.setAppId(appId);
			raccolte.add(raccolta);
//			categorie.getColori().add(new Tipologia(raccolta.getColore(), null, null));
		}
		output.setRaccolta(raccolte);

		for (TipologiaRaccolta tr : rifiuti.getTipologiaRaccolta()) {
			categorie.getTipologiaRaccolta().add(new Tipologia(StringUtils.capitalize(tr.getValore().toLowerCase().replace("crm", "CRM").replace("crz", "CRZ")).trim(), null, null));
		}

		output.setPuntiRaccolta(compactPuntiRaccolta(rifiuti.getPuntiRaccolta(), appId));

		List<Riciclabolario> riciclabolario = Lists.newArrayList();
		for (it.smartcommunitylab.riciclo.app.importer.model.Riciclabolario rc : (List<it.smartcommunitylab.riciclo.app.importer.model.Riciclabolario>) rifiuti.getRiciclabolario()) {
			Riciclabolario ric = mapper.convertValue(rc, Riciclabolario.class);
			ric.setTipologiaRifiuto(StringUtils.capitalize(ric.getTipologiaRifiuto().toLowerCase()));
			ric.setAppId(appId);
			String[] split = StringUtils.split(ric.getTipologiaUtenza(),";");
			for (String tu : split) {
				Riciclabolario newRic = new Riciclabolario();
				newRic.setAppId(appId);
				newRic.setArea(ric.getArea().trim());
				newRic.setNome(ric.getNome().trim());
				newRic.setTipologiaRifiuto(ric.getTipologiaRifiuto().trim());
				newRic.setTipologiaUtenza(tu.trim().trim());
				riciclabolario.add(newRic);
			}
		}
		output.setRiciclabolario(riciclabolario);
		
		
		List<Colore> colori = Lists.newArrayList();
		for (it.smartcommunitylab.riciclo.app.importer.model.Colori col: (List<it.smartcommunitylab.riciclo.app.importer.model.Colori>)rifiuti.getColori()) {
			Colore colore = mapper.convertValue(col, Colore.class);
			colori.add(colore);
		}
		output.setColore(colori);	
		
		List<Segnalazione> segnalazioni = Lists.newArrayList();
		for (it.smartcommunitylab.riciclo.app.importer.model.Segnalazioni sgn: (List<it.smartcommunitylab.riciclo.app.importer.model.Segnalazioni>)rifiuti.getSegnalazioni()) {
			Segnalazione sg = mapper.convertValue(sgn, Segnalazione.class);
			segnalazioni.add(sg);
		}
		output.setSegnalazione(segnalazioni);

		return output;
	}

	private List<PuntoRaccolta> compactPuntiRaccolta(List<PuntiRaccolta> puntiRaccolta, String appId) throws Exception {
		Multimap<String, PuntiRaccolta> puntiRaccoltaMap = ArrayListMultimap.create();
		for (PuntiRaccolta p : puntiRaccolta) {
			String key = p.getArea() + "_" + p.getTipologiaPuntiRaccolta() + "_" + p.getTipologiaUtenza() + "_" + p.getZona() + "_" + p.getDettagliZona();
			puntiRaccoltaMap.put(key, p);
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

		List<PuntoRaccolta> firstResult = Lists.newArrayList();
		for (String key : puntiRaccoltaMap.keySet()) {
			List<OrarioApertura> orari = Lists.newArrayList();
			PuntoRaccolta npr = null;
			for (PuntiRaccolta pr : puntiRaccoltaMap.get(key)) {
				if (npr == null) {
					npr = mapper.convertValue(pr, PuntoRaccolta.class);
					UtenzaArea ua = new UtenzaArea();
					ua.setArea(pr.getArea());
					ua.setTipologiaUtenza(pr.getTipologiaUtenza());
					List<UtenzaArea> lua = Lists.newArrayList();
					lua.add(ua);
					npr.setUtenzaArea(lua);
					
					Map<String, Boolean> caratteristiche = Maps.newTreeMap();
					caratteristiche.put(GETTONIERA, Boolean.parseBoolean(pr.getGettoniera()));
					caratteristiche.put(RESIDUO, Boolean.parseBoolean(pr.getResiduo()));
					caratteristiche.put(IMB_CARTA, Boolean.parseBoolean(pr.getImbCarta()));
					caratteristiche.put(IMB_PL_MET, Boolean.parseBoolean(pr.getImbPlMet()));
					caratteristiche.put(ORGANICO, Boolean.parseBoolean(pr.getOrganico()));
					caratteristiche.put(IMB_VETRO, Boolean.parseBoolean(pr.getImbVetro()));
					caratteristiche.put(INDUMENTI, Boolean.parseBoolean(pr.getIndumenti()));
					npr.setCaratteristiche(caratteristiche);
				}
				if (pr.getDataDa() ==  null || pr.getDataDa().isEmpty() || pr.getDataA() ==  null || pr.getDataA().isEmpty() || pr.getIl() ==  null || pr.getIl().isEmpty()) {
					continue;
				}
				OrarioApertura oa = new OrarioApertura();
				oa.setAlle(pr.getAlle());
				oa.setDalle(pr.getDalle());
				oa.setDataA(pr.getDataA());
				oa.setDataDa(pr.getDataDa());
				oa.setIl(pr.getIl());
				oa.setEccezione(pr.getEccezione());
				oa.setNote(pr.getNote());
				orari.add(oa);
			}
			if (!orari.isEmpty()) {
				npr.setOrarioApertura(orari);
			}
			npr.setOrarioApertura(orari);
			npr.setAppId(appId);
			firstResult.add(npr);
		}

		for (PuntoRaccolta pr: firstResult) {
			pr.setTipologiaPuntiRaccolta(StringUtils.capitalize(pr.getTipologiaPuntiRaccolta().toLowerCase()).replace("Crm", "CRM").replace("Crz", "CRZ"));
		}
		
		Multimap<PuntoRaccolta, PuntoRaccolta> puntoRaccoltaMap = ArrayListMultimap.create();
		for (PuntoRaccolta pr: firstResult) {
			puntoRaccoltaMap.put(pr, pr);
		}
		
		List<PuntoRaccolta> secondResult = Lists.newArrayList();
		
		for (PuntoRaccolta key: puntoRaccoltaMap.keySet()) {
			secondResult.add(key);
			for (PuntoRaccolta pr : puntoRaccoltaMap.get(key)) {
				if (pr == key) {
					continue;
				}
				key.getUtenzaArea().addAll(pr.getUtenzaArea());
			}
		}
		
		return secondResult;
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
