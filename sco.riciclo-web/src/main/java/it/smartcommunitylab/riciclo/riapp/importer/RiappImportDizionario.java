package it.smartcommunitylab.riciclo.riapp.importer;

import it.smartcommunitylab.riciclo.riapp.importer.model.RiappRifiuto;

import java.io.FileReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

public class RiappImportDizionario {
	private static final transient Logger logger = LoggerFactory.getLogger(RiappImportDizionario.class);
	
	private String baseDir;
	
	public RiappImportDizionario(String baseDir) {
		this.baseDir = baseDir;
	}
	
	public List<RiappRifiuto> readListaRifiuti(String fileId) throws Exception {
		List<RiappRifiuto> result = Lists.newArrayList();
		FileReader fileReader = new FileReader(baseDir + "\\diz\\diz_" + fileId.toLowerCase() + ".json");
		JsonNode rootNode = Utils.readJsonFromReader(fileReader);
		Iterator<JsonNode> rootElements = rootNode.elements();
		int index = 0;
		while(rootElements.hasNext()) {
			JsonNode node = rootElements.next();
			if(logger.isInfoEnabled()) {
				logger.info("parse object " + index);
				index++;
			}
			try {
				RiappRifiuto rifiuto = Utils.toObject(node, RiappRifiuto.class);
				result.add(rifiuto);
			} catch (JsonProcessingException e) {
				if(logger.isWarnEnabled()) {
					logger.warn("parsing exception:" + e.getMessage());
				}
			}
		}
		return result;
	}
	
	public static String getTipologiaRifiuto(RiappRifiuto riappRifiuto, Map<String, String> tipologiaRifiutoMap) {
		String tipologiaRifiuto = tipologiaRifiutoMap.get(riappRifiuto.getRifiuto().toLowerCase().trim());
		return tipologiaRifiuto;
	}
	
	public static List<String> getTipologieRaccolta(RiappRifiuto riappRifiuto, Map<String, String> tipologiaRaccoltaMap) {
		String contenitore = riappRifiuto.getContenitore();
		String dove = riappRifiuto.getDove();
		String fr = riappRifiuto.getFr();
		List<String> tipologieRaccolta = Lists.newArrayList();
		if(Utils.isNotEmpty(contenitore)) {
			for(String tr : tipologiaRaccoltaMap.keySet()) {
				if(tr.toLowerCase().contains(contenitore.toLowerCase())) {
					tipologieRaccolta.add(tr);
				}
			}
		}
		if((tipologieRaccolta.size() == 0) && Utils.isNotEmpty(dove)) {
			for(String tr : tipologiaRaccoltaMap.keySet()) {
				if(tr.toLowerCase().contains(dove.toLowerCase())) {
					tipologieRaccolta.add(tr);
				}
			}
		} 
		if((tipologieRaccolta.size() == 0) && Utils.isNotEmpty(fr)) {
			if(fr.equals("cen")) {
				tipologieRaccolta.add(Const.TRAC_CS);
			}
		}
		if(tipologieRaccolta.size() == 0) {
			if(logger.isWarnEnabled()) {
				logger.warn("getTipologieRaccolta - tipologiaRaccolta not found for " + riappRifiuto.getRifiuto());
			}
		}
		return tipologieRaccolta;
	}
}
