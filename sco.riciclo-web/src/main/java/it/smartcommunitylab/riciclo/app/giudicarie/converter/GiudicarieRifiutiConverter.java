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

package it.smartcommunitylab.riciclo.app.giudicarie.converter;

import it.smartcommunitylab.riciclo.app.giudicarie.model.Aree;
import it.smartcommunitylab.riciclo.app.giudicarie.model.Gestori;
import it.smartcommunitylab.riciclo.app.giudicarie.model.Istituzioni;
import it.smartcommunitylab.riciclo.app.giudicarie.model.Profili;
import it.smartcommunitylab.riciclo.app.giudicarie.model.PuntiRaccolta;
import it.smartcommunitylab.riciclo.app.giudicarie.model.TipologiaPuntiRaccolta;
import it.smartcommunitylab.riciclo.app.giudicarie.model.TipologiaRaccolta;
import it.smartcommunitylab.riciclo.app.giudicarie.model.TipologiaRifiuto;
import it.smartcommunitylab.riciclo.converter.AbstractConverter;
import it.smartcommunitylab.riciclo.model.Area;
import it.smartcommunitylab.riciclo.model.Categorie;
import it.smartcommunitylab.riciclo.model.Gestore;
import it.smartcommunitylab.riciclo.model.Istituzione;
import it.smartcommunitylab.riciclo.model.OrarioApertura;
import it.smartcommunitylab.riciclo.model.Profilo;
import it.smartcommunitylab.riciclo.model.PuntoRaccolta;
import it.smartcommunitylab.riciclo.model.Raccolta;
import it.smartcommunitylab.riciclo.model.Riciclabolario;
import it.smartcommunitylab.riciclo.model.Rifiuti;
import it.smartcommunitylab.riciclo.model.Tipologia;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class GiudicarieRifiutiConverter extends AbstractConverter {

	private static final String UTENZA_DOMESTICA = "utenza domestica";
	private static final String UTENZA_NON_DOMESTICA = "utenza non domestica";
	private static final String UTENZA_OCCASIONALE = "utenza occasionale";

	private static final String GETTONIERA = "GETTONIERA";
	private static final String RESIDUO = "RESIDUO";
	private static final String IMB_CARTA = "IMB_CARTA";
	private static final String IMB_PL_MET = "IMB_PL_MET";
	private static final String ORGANICO = "ORGANICO";
	private static final String IMB_VETRO = "IMB_VETRO";
	private static final String INDUMENTI = "INDUMENTI";

	@Autowired
	@Value("${giudicarie.appId}")
	private String appId;

	@Override
	public void setAppId(String appId) {
		this.appId = appId;
	}

	@Override
	public String getAppId() {
		return appId;
	}

	public Rifiuti convert(Object input) throws Exception {
		it.smartcommunitylab.riciclo.app.giudicarie.model.Rifiuti rifiuti = (it.smartcommunitylab.riciclo.app.giudicarie.model.Rifiuti) input;
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		Rifiuti output = new Rifiuti();

		Categorie categorie = new Categorie();
		categorie.setAppId(appId);
		categorie.setTipologiaUtenza(buildTipologieSet(new String[] { UTENZA_DOMESTICA, UTENZA_NON_DOMESTICA, UTENZA_OCCASIONALE }));
		categorie.setCaratteristicaPuntoRaccolta(buildTipologieSet(new String[] { GETTONIERA, RESIDUO, IMB_CARTA, IMB_PL_MET, ORGANICO, IMB_VETRO, INDUMENTI }));

		categorie.setColori(new HashSet<Tipologia>());
		categorie.setTipologiaIstituzione(new HashSet<Tipologia>());
		categorie.setTipologiaRaccolta(new HashSet<Tipologia>());
		
		categorie.setTipologiaPuntiRaccolta(new HashSet());
		for (TipologiaPuntiRaccolta tpr : rifiuti.getTipologiaPuntiRaccolta()) {
			Tipologia cat = new Tipologia(StringUtils.capitalize(tpr.getNome().toLowerCase()).replace("Crm", "CRM"), tpr.getInfoPuntiRaccolta(), null);
			categorie.getTipologiaPuntiRaccolta().add(cat);
		}

		categorie.setTipologiaRifiuto(new HashSet());
		for (TipologiaRifiuto tr : rifiuti.getTipologiaRifiuto()) {
			Tipologia cat = new Tipologia(StringUtils.capitalize(tr.getValore().toLowerCase()), null, null);
			categorie.getTipologiaRifiuto().add(cat);
		}

		output.setCategorie(categorie);

		List<Profilo> profili = Lists.newArrayList();
		for (Profili pr : rifiuti.getProfili()) {
			Profilo profilo = mapper.convertValue(pr, Profilo.class);
			profilo.setAppId(appId);
			profili.add(profilo);
		}
		output.setProfili(profili);
		
		List<Area> aree = Lists.newArrayList();
		for (Aree ar : rifiuti.getAree()) {
			Area area = mapper.convertValue(ar, Area.class);
			area.setAppId(appId);
			Map<String, Boolean> utenza = Maps.newTreeMap();
			if (ar.getUtenzaDomestica() != null && !ar.getUtenzaDomestica().isEmpty()) {
				utenza.put(UTENZA_DOMESTICA, Boolean.parseBoolean(ar.getUtenzaDomestica()));
			} else {
				utenza.put(UTENZA_DOMESTICA, true);
			}
			if (ar.getUtenzaNonDomestica() != null && !ar.getUtenzaNonDomestica().isEmpty()) {
				utenza.put(UTENZA_NON_DOMESTICA, Boolean.parseBoolean(ar.getUtenzaNonDomestica()));
			} else {
				utenza.put(UTENZA_NON_DOMESTICA, true);
			}
			if (ar.getUtenzaOccasionale() != null && !ar.getUtenzaOccasionale().isEmpty()) {
				utenza.put(UTENZA_OCCASIONALE, Boolean.parseBoolean(ar.getUtenzaOccasionale()));
			} else {
				utenza.put(UTENZA_OCCASIONALE, true);
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
			categorie.getTipologiaIstituzione().add(new Tipologia(is.getTipologia(), null, null));
		}
		output.setIstituzioni(istituzioni);

		List<Raccolta> raccolte = Lists.newArrayList();
		for (it.smartcommunitylab.riciclo.app.giudicarie.model.Raccolta rc : (List<it.smartcommunitylab.riciclo.app.giudicarie.model.Raccolta>) rifiuti.getRaccolta()) {
			Raccolta raccolta = mapper.convertValue(rc, Raccolta.class);
			raccolta.setTipologiaPuntoRaccolta(StringUtils.capitalize(raccolta.getTipologiaPuntoRaccolta().toLowerCase()).replace("Crm", "CRM"));
			raccolta.setTipologiaRifiuto(StringUtils.capitalize(raccolta.getTipologiaRifiuto().toLowerCase()));
			raccolta.setTipologiaRaccolta(StringUtils.capitalize(raccolta.getTipologiaRaccolta().toLowerCase().replace("crm", "CRM")));
			raccolta.setAppId(appId);
			raccolte.add(raccolta);
			categorie.getColori().add(new Tipologia(raccolta.getColore(), null, null));
		}
		output.setRaccolta(raccolte);

		for (TipologiaRaccolta tr : rifiuti.getTipologiaRaccolta()) {
			categorie.getTipologiaRaccolta().add(new Tipologia(StringUtils.capitalize(tr.getNome().toLowerCase().replace("crm", "CRM")), null, tr.getIcona()));
		}

		output.setPuntiRaccolta(compactPuntiRaccolta(rifiuti.getPuntiRaccolta(), appId));

		List<Riciclabolario> riciclabolario = Lists.newArrayList();
		for (it.smartcommunitylab.riciclo.app.giudicarie.model.Riciclabolario rc : (List<it.smartcommunitylab.riciclo.app.giudicarie.model.Riciclabolario>) rifiuti.getRiciclabolario()) {
			Riciclabolario ric = mapper.convertValue(rc, Riciclabolario.class);
			ric.setTipologiaRifiuto(StringUtils.capitalize(ric.getTipologiaRifiuto().toLowerCase()));
			ric.setAppId(appId);
			riciclabolario.add(ric);
		}
		output.setRiciclabolario(riciclabolario);

		return output;
	}

	private List<PuntoRaccolta> compactPuntiRaccolta(List<PuntiRaccolta> puntiRaccolta, String appId) throws Exception {
		Multimap<String, PuntiRaccolta> map = ArrayListMultimap.create();
		for (PuntiRaccolta p : puntiRaccolta) {
			String key = p.getArea() + "_" + p.getTipologiaPuntiRaccolta() + "_" + p.getTipologiaUtenza() + "_" + p.getIndirizzo() + "_" + p.getDettaglioIndirizzo();
			map.put(key, p);
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

		List<PuntoRaccolta> result = Lists.newArrayList();
		for (String key : map.keySet()) {
			List<OrarioApertura> orari = Lists.newArrayList();
			PuntoRaccolta npr = null;
			for (PuntiRaccolta pr : map.get(key)) {
				if (npr == null) {
					npr = mapper.convertValue(pr, PuntoRaccolta.class);
					Map<String, Boolean> caratteristiche = Maps.newTreeMap();
					caratteristiche.put(GETTONIERA, Boolean.parseBoolean(pr.getGettoniera()));
					caratteristiche.put(RESIDUO, Boolean.parseBoolean(pr.getResiduo()));
					caratteristiche.put(IMB_CARTA, Boolean.parseBoolean(pr.getResiduo()));
					caratteristiche.put(IMB_PL_MET, Boolean.parseBoolean(pr.getResiduo()));
					caratteristiche.put(ORGANICO, Boolean.parseBoolean(pr.getResiduo()));
					caratteristiche.put(IMB_VETRO, Boolean.parseBoolean(pr.getResiduo()));
					caratteristiche.put(INDUMENTI, Boolean.parseBoolean(pr.getResiduo()));
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
				orari.add(oa);
			}
			if (!orari.isEmpty()) {
				npr.setOrarioApertura(orari);
			}
			npr.setOrarioApertura(orari);
			npr.setAppId(appId);
			result.add(npr);
		}

		for (PuntoRaccolta pr: result) {
			pr.setTipologiaPuntiRaccolta(StringUtils.capitalize(pr.getTipologiaPuntiRaccolta().toLowerCase()).replace("Crm", "CRM"));
		}
		
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

	@Override
	public List<String> specificValidate(Rifiuti rifiuti) throws Exception {
		List<String> problems = Lists.newArrayList();
		
		Set<String> comuni = flattenList(rifiuti.getAree(), Area.class, "comune");
		
		for (PuntoRaccolta puntoRaccolta : rifiuti.getPuntiRaccolta()) {
			if (!comuni.contains(puntoRaccolta.getIndirizzo())) {
				if (puntoRaccolta.getIndirizzo() != null && !puntoRaccolta.getIndirizzo().isEmpty()) {
					String s = "Comune <" + puntoRaccolta.getIndirizzo() + "> not found for " + puntoRaccolta;
					problems.add(s);
				}
			}
		}
		
		return problems;
	}

}
