package it.smartcommunitylab.riciclo.app.importer.converter;

import it.smartcommunitylab.riciclo.app.importer.FileList;
import it.smartcommunitylab.riciclo.app.importer.ImportConstants;
import it.smartcommunitylab.riciclo.app.importer.ImportError;
import it.smartcommunitylab.riciclo.model.Area;
import it.smartcommunitylab.riciclo.model.Gestore;
import it.smartcommunitylab.riciclo.model.Istituzione;
import it.smartcommunitylab.riciclo.model.Profilo;
import it.smartcommunitylab.riciclo.model.PuntoRaccolta;
import it.smartcommunitylab.riciclo.model.Raccolta;
import it.smartcommunitylab.riciclo.model.Riciclabolario;
import it.smartcommunitylab.riciclo.model.Rifiuti;
import it.smartcommunitylab.riciclo.model.Tipologia;
import it.smartcommunitylab.riciclo.model.UtenzaArea;
import it.smartcommunitylab.riciclo.storage.AppInfo;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.WordUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class RifiutiValidator {

	public List<String> validate(Rifiuti rifiuti) throws Exception {
		List<String> problems = Lists.newArrayList();

		Set<String> tipologiaIstituzione = flattenList(rifiuti.getCategorie().getTipologiaIstituzione());
		Set<String> tipologiaPuntoRaccolta = flattenList(rifiuti.getCategorie().getTipologiaPuntiRaccolta());
		Set<String> tipologiaUtenza = flattenList(rifiuti.getCategorie().getTipologiaUtenza());
		Set<String> tipologiaRaccolta = flattenList(rifiuti.getCategorie().getTipologiaRaccolta());
		Set<String> tipologiaRifiuto = flattenList(rifiuti.getCategorie().getTipologiaRifiuto());
		Set<String> aree = flattenList(rifiuti.getAree(), Area.class);
		Set<String> colori = flattenList(rifiuti.getCategorie().getColori());
		Set<String> caratteristiche = flattenList(rifiuti.getCategorie().getCaratteristicaPuntoRaccolta());
		Set<String> istituzioni = flattenList(rifiuti.getIstituzioni(), Istituzione.class);
		
		Set<String> gestori = flattenList(rifiuti.getGestori(), Gestore.class, "ragioneSociale");
		Set<String> comuni = flattenList(rifiuti.getAree(), Area.class, "comune");
		
		for (Istituzione istituzione: rifiuti.getIstituzioni()) {
			if (!tipologiaIstituzione.contains(istituzione.getTipologia())) {
				String s = "Tipologia Istituzione <" + istituzione.getTipologia() + "> not found for " + istituzione;
				problems.add(s);
			}
		}			
		
		List<String> parentErrors = Lists.newArrayList();
		for (Area area : rifiuti.getAree()) {
			if (area.getParent().isEmpty()) {
				String s = "Empty parent for " + area;
				parentErrors.add(s);
			}
			if (!istituzioni.contains(area.getIstituzione())) {
				String s = "Istituzione <" + area.getIstituzione() + "> not found for " + area;
				problems.add(s);				
			}	
			
			if (!gestori.contains(area.getGestore())) {
				String s = "Gestore <" + area.getIstituzione() + "> not found for " + area;
				problems.add(s);				
			}				
		}
		if (parentErrors.size() > 2) {
			problems.addAll(parentErrors);
		} else if (parentErrors.isEmpty()) {
			String s = "No root for Aree";
			problems.add(s);					
		}
		
		for (Profilo profilo: rifiuti.getProfili()) {
			if (!tipologiaUtenza.contains(profilo.getTipologiaUtenza())) {
				String s = "Tipologia Utenza <" + profilo.getTipologiaUtenza() + "> not found for " + profilo;
				problems.add(s);				
			}
		}
		
		for (PuntoRaccolta puntoRaccolta: rifiuti.getPuntiRaccolta()) {
			if (!tipologiaPuntoRaccolta.contains(puntoRaccolta.getTipologiaPuntiRaccolta())) {
				String s = "Tipologia Punto Raccolta <" + puntoRaccolta.getTipologiaPuntiRaccolta() + "> not found for " + puntoRaccolta;
				problems.add(s);				
			}
			
			for (UtenzaArea ua : puntoRaccolta.getUtenzaArea()) {
				if (!tipologiaUtenza.contains(ua.getTipologiaUtenza())) {
					String s = "Tipologia Utenza <" + ua.getTipologiaUtenza() + "> not found for " + puntoRaccolta;
					problems.add(s);
				}
				if (!aree.contains(ua.getArea())) {
					String s = "Area <" + ua.getArea() + "> not found for " + puntoRaccolta;
					problems.add(s);
				}
			}
			for (String caratt : puntoRaccolta.getCaratteristiche().keySet()) {
				if (!caratteristiche.contains(caratt)) {
					String s = "Caratteristica <" + caratt + "> not found for " + puntoRaccolta;
					problems.add(s);
				}
			}
			
			if (!comuni.contains(puntoRaccolta.getIndirizzo())) {
				if (puntoRaccolta.getIndirizzo() != null && !puntoRaccolta.getIndirizzo().isEmpty()) {
					String s = "Comune <" + puntoRaccolta.getIndirizzo() + "> not found for " + puntoRaccolta;
					problems.add(s);
				}
			}
			boolean needAddress = "crm".equals(puntoRaccolta.getTipologiaPuntiRaccolta().toLowerCase()) || "crz".equals(puntoRaccolta.getTipologiaPuntiRaccolta().toLowerCase()) || "isola ecologica".equals(puntoRaccolta.getTipologiaPuntiRaccolta().toLowerCase());
			
			if (needAddress) {
				if (puntoRaccolta.getIndirizzo() == null || puntoRaccolta.getIndirizzo().isEmpty()) {
					String s = "Missing 'indirizzo' for " + puntoRaccolta;
					problems.add(s);
				}
				if (puntoRaccolta.getDettaglioIndirizzo() == null || puntoRaccolta.getDettaglioIndirizzo().isEmpty()) {
					String s = "Missing 'dettaglioIndirizzo' for " + puntoRaccolta;
					problems.add(s);
				}
				if (puntoRaccolta.getLocalizzazione() == null || puntoRaccolta.getLocalizzazione().isEmpty()) {
					String s = "Missing 'localizzazione' for " + puntoRaccolta;
					problems.add(s);
				}
			}			
			
		}
		
		for (Raccolta raccolta: rifiuti.getRaccolta()) {
			if (!tipologiaPuntoRaccolta.contains(raccolta.getTipologiaPuntoRaccolta())) {
				String s = "Tipologia Punto Raccolta <" + raccolta.getTipologiaPuntoRaccolta() + "> not found for " + raccolta;
				problems.add(s);				
			}
			if(!tipologiaUtenza.contains(raccolta.getTipologiaUtenza())) {
				String s = "Tipologia Utenza <" + raccolta.getTipologiaUtenza() + "> not found for " + raccolta;
				problems.add(s);				
			}	
			if(!tipologiaRaccolta.contains(raccolta.getTipologiaRaccolta())) {
				String s = "Tipologia Raccolta <" + raccolta.getTipologiaRaccolta() + "> not found for " + raccolta;
				problems.add(s);				
			}		
			if(!tipologiaRifiuto.contains(raccolta.getTipologiaRifiuto())) {
				String s = "Tipologia Rifiuto <" + raccolta.getTipologiaRifiuto() + "> not found for " + raccolta;
				problems.add(s);				
			}					
			if(!aree.contains(raccolta.getArea())) {
				String s = "Area <" + raccolta.getArea() + "> not found for " + raccolta;
				problems.add(s);				
			}
			if(!colori.contains(raccolta.getColore())) {
				String s = "Colore <" + raccolta.getColore() + "> not found for " + raccolta;
				problems.add(s);				
			}			
		}
		
		for (Riciclabolario riciclabolario: rifiuti.getRiciclabolario()) {
			if(!tipologiaUtenza.contains(riciclabolario.getTipologiaUtenza())) {
				String s = "Tipologia Utenza <" + riciclabolario.getTipologiaUtenza() + "> not found for " + riciclabolario;
				problems.add(s);				
			}		
			if(!tipologiaRifiuto.contains(riciclabolario.getTipologiaRifiuto())) {
				String s = "Tipologia Rifiuto <" + riciclabolario.getTipologiaRifiuto() + "> not found for " + riciclabolario;
				problems.add(s);				
			}		
			if(!aree.contains(riciclabolario.getArea())) {
				String s = "Area <" + riciclabolario.getArea() + "> not found for " + riciclabolario;
				problems.add(s);				
			}
		}
		
		return problems;
	}
	
	protected Set<String> flattenList(Collection<?> objects) throws Exception {
		return flattenList(objects, Tipologia.class, "nome");
	}		
	
	protected Set<String> flattenList(Collection<?> objects, Class<?> clz) throws Exception {
		return flattenList(objects, clz, "nome");
	}		
	
	protected Set<String> flattenList(Collection<?> objects, Class<?> clz, String field) throws Exception {
		Set<String> result = Sets.newHashSet();
		for (Object element: objects) {
			Method m = clz.getMethod("get" + WordUtils.capitalize(field));
			String value = (String)m.invoke(element);
			result.add(value);
		}
		return result;
	}

	public void validateInput(AppInfo appInfo, FileList fileList) throws ImportError {
		if (fileList.getModel() == null || fileList.getModel().isEmpty()) {
			throw new ImportError("Missing model file");
		}
		if (appInfo.getModelElements().contains(ImportConstants.CRM) && (fileList.getCrm() == null || fileList.getCrm().isEmpty())) {
			throw new ImportError("Missing CRM file");
		}
		if (appInfo.getModelElements().contains(ImportConstants.ISOLE) && (fileList.getIsole() == null || fileList.getModel().isEmpty())) {
			throw new ImportError("Missing 'isole' file");
		}
	}			
	
}
