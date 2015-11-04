package it.smartcommunitylab.riciclo.presentation;

import it.smartcommunitylab.riciclo.controller.Utils;
import it.smartcommunitylab.riciclo.model.Area;
import it.smartcommunitylab.riciclo.model.CalendarioRaccolta;
import it.smartcommunitylab.riciclo.model.Categorie;
import it.smartcommunitylab.riciclo.model.Colore;
import it.smartcommunitylab.riciclo.model.Crm;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class UIConverter {
	private static final transient Logger logger = LoggerFactory.getLogger(UIConverter.class);

	public static List<TipologiaProfiloUI> convertTipologiaProfilo(List<TipologiaProfilo> modelData,
			String lang, String defaultLang) {
		List<TipologiaProfiloUI> result = Lists.newArrayList();
		for(TipologiaProfilo profilo : modelData) {
			TipologiaProfiloUI profiloUI = new TipologiaProfiloUI();
			profiloUI.setAppId(profilo.getOwnerId());
			profiloUI.setId(profilo.getObjectId());
			profiloUI.setNome(Utils.getString(profilo.getNome(), lang, defaultLang));
			profiloUI.setDescrizione(Utils.getString(profilo.getDescrizione(), lang, defaultLang));
			profiloUI.setTipologiaUtenza(profilo.getTipologiaUtenza());
			result.add(profiloUI);
		}
		return result;
	}

	public static List<GestoreUI> convertGestore(List<Gestore> modelData,
			String lang, String defaultLang) {
		List<GestoreUI> result = Lists.newArrayList();
		for(Gestore gestore : modelData) {
			GestoreUI gestoreUI = new GestoreUI();
			gestoreUI.setAppId(gestore.getOwnerId());
			gestoreUI.setId(gestore.getObjectId());
			gestoreUI.setRagioneSociale(gestore.getRagioneSociale());
			gestoreUI.setDescrizione(Utils.getString(gestore.getDescrizione(), lang, defaultLang));
			gestoreUI.setUfficio(gestore.getUfficio());
			gestoreUI.setIndirizzo(Utils.getString(gestore.getIndirizzo(), lang, defaultLang));
			gestoreUI.setOrarioUfficio(Utils.getString(gestore.getOrarioUfficio(), lang, defaultLang));
			gestoreUI.setSitoWeb(gestore.getSitoWeb());
			gestoreUI.setEmail(gestore.getEmail());
			gestoreUI.setTelefono(gestore.getTelefono());
			gestoreUI.setFax(gestore.getFax());
			gestoreUI.setFacebook(gestore.getFacebook());
			gestoreUI.setLocalizzazione(Utils.convertLocalizzazione(gestore.getGeocoding()));
			result.add(gestoreUI);
		}
		return result;
	}

	public static List<IstituzioneUI> convertIstituzione(List<Istituzione> modelData,
			String lang, String defaultLang) {
		List<IstituzioneUI> result = Lists.newArrayList();
		for(Istituzione istituzione : modelData) {
			IstituzioneUI istituzioneUI = new IstituzioneUI();
			istituzioneUI.setAppId(istituzione.getOwnerId());
			istituzioneUI.setId(istituzione.getObjectId());
			istituzioneUI.setNome(istituzione.getNome());
			istituzioneUI.setDescrizione(Utils.getString(istituzione.getDescrizione(), lang, defaultLang));
			istituzioneUI.setUfficio(istituzione.getUfficio());
			istituzioneUI.setIndirizzo(Utils.getString(istituzione.getIndirizzo(), lang, defaultLang));
			istituzioneUI.setOrarioUfficio(Utils.getString(istituzione.getOrarioUfficio(), lang, defaultLang));
			istituzioneUI.setSito(istituzione.getSito());
			istituzioneUI.setPec(istituzione.getPec());
			istituzioneUI.setEmail(istituzione.getEmail());
			istituzioneUI.setTelefono(istituzione.getTelefono());
			istituzioneUI.setFax(istituzione.getFax());
			istituzioneUI.setFacebook(istituzione.getFacebook());
			istituzioneUI.setLocalizzazione(Utils.convertLocalizzazione(istituzione.getGeocoding()));
			result.add(istituzioneUI);
		}
		return result;
	}

	public static List<ColoreUI> convertColore(List<Colore> modelData,
			String lang, String defaultLang) {
		List<ColoreUI> result = Lists.newArrayList();
		for(Colore colore : modelData) {
			ColoreUI coloreUI = new ColoreUI();
			coloreUI.setAppId(colore.getOwnerId());
			coloreUI.setNome(colore.getNome());
			coloreUI.setCodice(colore.getCodice());
			result.add(coloreUI);
		}
		return result;
	}

	public static List<SegnalazioneUI> convertSegnalazione(List<Segnalazione> modelData,
			String lang, String defaultLang) {
		List<SegnalazioneUI> result = Lists.newArrayList();
		for(Segnalazione segnalazione : modelData) {
			SegnalazioneUI segnalazioneUI = new SegnalazioneUI();
			segnalazioneUI.setAppId(segnalazione.getOwnerId());
			segnalazioneUI.setId(segnalazione.getObjectId());
			segnalazioneUI.setArea(segnalazione.getArea());
			segnalazioneUI.setEmail(segnalazione.getEmail());
			segnalazioneUI.setTipologia(Utils.getString(segnalazione.getTipologia(), lang, defaultLang));
			result.add(segnalazioneUI);
		}
		return result;
	}

	public static List<RiciclabolarioUI> convertRiciclabolario(List<Riciclabolario> modelData,
			List<Rifiuto> rifiuti, String lang, String defaultLang) {
		List<RiciclabolarioUI> result = Lists.newArrayList();
		//map <objectId, nome>
		Map<String, String> nomeRifiutoMap = new HashMap<String, String>();
		for(Rifiuto rifiuto : rifiuti) {
			nomeRifiutoMap.put(rifiuto.getObjectId(), Utils.getString(rifiuto.getNome(), lang, defaultLang));
		}
		for(Riciclabolario riciclabolario : modelData) {
			RiciclabolarioUI riciclabolarioUI = new RiciclabolarioUI();
			riciclabolarioUI.setAppId(riciclabolario.getOwnerId());
			riciclabolarioUI.setNome(nomeRifiutoMap.get(riciclabolario.getRifiuto()));
			riciclabolarioUI.setArea(riciclabolario.getArea());
			riciclabolarioUI.setTipologiaRifiuto(riciclabolario.getTipologiaRifiuto());
			riciclabolarioUI.setTipologiaUtenza(riciclabolario.getTipologiaUtenza());
			result.add(riciclabolarioUI);
		}
		return result;
	}

	public static List<AreaUI> convertArea(List<Area> modelData,
			String lang, String defaultLang) {
		List<AreaUI> result = Lists.newArrayList();
		for(Area area : modelData) {
			AreaUI areaUI = new AreaUI();
			areaUI.setAppId(area.getOwnerId());
			areaUI.setId(area.getObjectId());
			areaUI.setParent(area.getParent());
			areaUI.setIstituzione(area.getIstituzione());
			areaUI.setGestore(area.getGestore());
			areaUI.setNome(area.getNome());
			areaUI.setDescrizione(Utils.getString(area.getDescrizione(), lang, defaultLang));
			areaUI.setEtichetta(Utils.getString(area.getEtichetta(), lang, defaultLang));
			areaUI.setUtenza(area.getUtenza());
			result.add(areaUI);
		}
		return result;
	}

	public static List<RaccoltaUI> convertRaccolta(List<Raccolta> modelData,
			String lang, String defaultLang) {
		List<RaccoltaUI> result = Lists.newArrayList();
		for(Raccolta raccolta : modelData) {
			RaccoltaUI raccoltaUI = new RaccoltaUI();
			raccoltaUI.setAppId(raccolta.getOwnerId());
			raccoltaUI.setArea(raccolta.getArea());
			raccoltaUI.setTipologiaUtenza(raccolta.getTipologiaUtenza());
			raccoltaUI.setTipologiaRifiuto(raccolta.getTipologiaRifiuto());
			raccoltaUI.setTipologiaPuntoRaccolta(raccolta.getTipologiaPuntoRaccolta());
			raccoltaUI.setTipologiaRaccolta(raccolta.getTipologiaRaccolta());
			raccoltaUI.setColore(raccolta.getColore());
			raccoltaUI.setInfoRaccolta(Utils.getString(raccolta.getInfoRaccolta(), lang, defaultLang));
			result.add(raccoltaUI);
		}
		return result;
	}

	public static List<OrarioAperturaUI> convertOrarioApertura(List<OrarioApertura> modelData,
			String lang, String defaultLang) {
		List<OrarioAperturaUI> result = Lists.newArrayList();
		for(OrarioApertura orario : modelData) {
			OrarioAperturaUI orarioUI = new OrarioAperturaUI();
			orarioUI.setDataDa(orario.getDataDa());
			orarioUI.setDataA(orario.getDataA());
			orarioUI.setIl(orario.getIl());
			orarioUI.setDalle(orario.getDalle());
			orarioUI.setAlle(orario.getAlle());
			orarioUI.setEccezione(orario.getEccezione());
			orarioUI.setNote(Utils.getString(orario.getNote(), lang, defaultLang));
			result.add(orarioUI);
		}
		return result;
	}

	public static List<PuntoRaccoltaUI> convertPuntoRaccolta(List<PuntoRaccolta> modelPuntoRaccolta, List<Crm> modelCrm,
			List<CalendarioRaccolta> modelCalendario, String lang, String defaultLang) {
		List<PuntoRaccoltaUI> result = Lists.newArrayList();
		//convert CalendarioRaccolta
		for(CalendarioRaccolta calendario : modelCalendario) {
			PuntoRaccoltaUI newPR = new PuntoRaccoltaUI();
			newPR.setAppId(calendario.getOwnerId());
			newPR.setTipologiaPuntiRaccolta(calendario.getTipologiaPuntoRaccolta());
			newPR.setOrarioApertura(convertOrarioApertura(calendario.getOrarioApertura(), lang, defaultLang));
			PuntoRaccoltaUI existingPR = containsPR(newPR, result);
			if(existingPR == null) {
				result.add(newPR);
				existingPR = newPR;
			}
			UtenzaAreaUI utenzaArea = new UtenzaAreaUI();
			utenzaArea.setArea(calendario.getArea());
			utenzaArea.setTipologiaUtenza(calendario.getTipologiaUtenza());
			existingPR.getUtenzaArea().add(utenzaArea);
		}
		Map<String, Crm> crmMap = new HashMap<String, Crm>();
		for(Crm crm : modelCrm) {
			crmMap.put(crm.getObjectId(), crm);
		}
		//convert PuntoRaccolta
		for(PuntoRaccolta raccolta : modelPuntoRaccolta) {
			Crm crm = crmMap.get(raccolta.getCrm());
			if(crm == null) {
				logger.warn("CRM not found:" + raccolta);
				continue;
			}
			PuntoRaccoltaUI newPR = new PuntoRaccoltaUI();
			newPR.setAppId(raccolta.getOwnerId());
			newPR.setTipologiaPuntiRaccolta(raccolta.getTipologiaPuntoRaccolta());
			newPR.setLocalizzazione(Utils.convertLocalizzazione(crm.getGeocoding()));
			newPR.setZona(crm.getZona());
			newPR.setDettagliZona(crm.getDettagliZona());
			newPR.setOrarioApertura(convertOrarioApertura(crm.getOrarioApertura(), lang, defaultLang));
			newPR.setCaratteristiche(crm.getCaratteristiche());
			newPR.setNote(Utils.getString(crm.getNote(), lang, defaultLang));
			PuntoRaccoltaUI existingPR = containsPR(newPR, result);
			if(existingPR == null) {
				result.add(newPR);
				existingPR = newPR;
			}
			UtenzaAreaUI utenzaArea = new UtenzaAreaUI();
			utenzaArea.setArea(raccolta.getArea());
			utenzaArea.setTipologiaUtenza(raccolta.getTipologiaUtenza());
			existingPR.getUtenzaArea().add(utenzaArea);
		}
		return result;
	}

	public static PuntoRaccoltaUI containsPR(PuntoRaccoltaUI toCheck, List<PuntoRaccoltaUI> list) {
		PuntoRaccoltaUI result = null;
		for(PuntoRaccoltaUI other : list) {
			if(toCheck.getOrarioApertura() == null) {
				if(other.getOrarioApertura() != null) {
					continue;
				}
			} else if (!toCheck.getOrarioApertura().equals(other.getOrarioApertura())){
				continue;
			}
			if (toCheck.getDettagliZona() == null) {
				if (other.getDettagliZona() != null) {
					continue;
				}
			} else if (!toCheck.getDettagliZona().equals(other.getDettagliZona())) {
				continue;
			}
			if (toCheck.getZona() == null) {
				if (other.getZona() != null) {
					continue;
				}
			} else if (!toCheck.getZona().equals(other.getZona())) {
				continue;
			}
			if (toCheck.getTipologiaPuntiRaccolta() == null) {
				if (other.getTipologiaPuntiRaccolta() != null) {
					continue;
				}
			} else if (!toCheck.getTipologiaPuntiRaccolta().equals(other.getTipologiaPuntiRaccolta())) {
				continue;
			}
			if (toCheck.getNote() == null) {
				if (other.getNote() != null) {
					continue;
				}
			} else if (!toCheck.getNote().equals(other.getNote())) {
				continue;
			}
			result = other;
			break;
		}
		return result;
	}

	public static Set<TipologiaUI> convertTipologiaPuntoRaccolta(List<TipologiaPuntoRaccolta> modelData,
		String lang, String defaultLang) {
		Set<TipologiaUI> result = new HashSet<TipologiaUI>();
		for(TipologiaPuntoRaccolta tpr : modelData) {
			TipologiaExtUI tipologiaUI = new TipologiaExtUI();
			tipologiaUI.setAppId(tpr.getOwnerId());
			tipologiaUI.setId(tpr.getObjectId());
			tipologiaUI.setNome(Utils.getString(tpr.getNome(), lang, defaultLang));
			tipologiaUI.setDescrizione(Utils.getString(tpr.getInfo(), lang, defaultLang));
			tipologiaUI.setIcona(tpr.getIcona());
			tipologiaUI.setType(tpr.getType());
			result.add(tipologiaUI);
		}
		return result;
	}

	public static CategorieUI convertCategorie(Categorie categorie, String lang, String defaultLang) {
		CategorieUI result = new CategorieUI();
		result.setAppId(categorie.getOwnerId());
		result.setCaratteristicaPuntoRaccolta(convertTipologia(categorie.getCaratteristicaPuntoRaccolta(), categorie.getOwnerId(),
				lang, defaultLang));
		result.setTipologiaRaccolta(convertTipologia(categorie.getTipologiaRaccolta(), categorie.getOwnerId(),
				lang, defaultLang));
		result.setTipologiaRifiuto(convertTipologia(categorie.getTipologiaRifiuto(), categorie.getOwnerId(),
				lang, defaultLang));
		result.setTipologiaUtenza(convertTipologia(categorie.getTipologiaUtenza(), categorie.getOwnerId(),
				lang, defaultLang));
		return result;
	}

	public static Set<TipologiaUI> convertTipologia(Set<Tipologia> tipoligie, String ownerId, String lang, String defaultLang) {
		Set<TipologiaUI> result = new LinkedHashSet<TipologiaUI>();
		for(Tipologia tipologia : tipoligie) {
			TipologiaUI tipologiaUI = new TipologiaUI();
			tipologiaUI.setAppId(ownerId);
			tipologiaUI.setId(tipologia.getObjectId());
			tipologiaUI.setNome(Utils.getString(tipologia.getNome(), lang, defaultLang));
			tipologiaUI.setDescrizione(Utils.getString(tipologia.getDescrizione(), lang, defaultLang));
			tipologiaUI.setIcona(tipologia.getIcona());
			result.add(tipologiaUI);
		}
		return result;
	}
}
