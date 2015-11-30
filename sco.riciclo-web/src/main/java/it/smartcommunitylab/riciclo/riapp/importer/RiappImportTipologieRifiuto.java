package it.smartcommunitylab.riciclo.riapp.importer;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

public class RiappImportTipologieRifiuto {
	private static final transient Logger logger = LoggerFactory.getLogger(RiappImportTipologieRifiuto.class);
	
	private String baseDir;
	
	public RiappImportTipologieRifiuto(String baseDir) {
		this.baseDir = baseDir;
	}
	
	/**
	 * 
	 * @return <rifiuto, tipologiaRifiuto>
	 * @throws Exception
	 */
	public Map<String, String> readListaRifiuti(String diz) throws Exception {
		Map<String, String> result = new HashMap<String, String>();
		Map<String, String> specificMap = new HashMap<String, String>();
		Map<String, String> defaultMap = new HashMap<String, String>();
		List<String> rifiutiList = Lists.newArrayList();
		FileReader fileReader = new FileReader(baseDir + "/diz-mod.json");
		JsonNode rootNode = Utils.readJsonFromReader(fileReader);
		Iterator<JsonNode> rootElements = rootNode.elements();
		while(rootElements.hasNext()) {
			JsonNode node = rootElements.next();
			String rifiuto = node.path("RIFIUTO").asText();
			if(!rifiutiList.contains(rifiuto)) {
				rifiutiList.add(rifiuto);
			}
			String tipologia = Utils.capitalize(node.path("TIPOLOGIA").asText().toLowerCase());
			String calendarValue = node.path(diz).asText();
			if(Utils.isEmpty(calendarValue) || calendarValue.equals("0")) {
				defaultMap.put(rifiuto, tipologia);
			} else {
				specificMap.put(rifiuto, tipologia);
			}
			if(logger.isInfoEnabled()) {
				logger.info("parse object " + rifiuto);
			}
		}
		for(String rifiuto : rifiutiList) {
			String tipologiaRifiuto = specificMap.get(rifiuto);
			if(tipologiaRifiuto != null) {
				result.put(rifiuto, tipologiaRifiuto);
			} else {
				tipologiaRifiuto = defaultMap.get(rifiuto);
				if(tipologiaRifiuto != null) {
					result.put(rifiuto, tipologiaRifiuto);
				}
			}
		}
		return result;
	}
	
	
	public static String getIconaTipologiaRifiuto(String tipologiaRifiuto) {
		if(Const.TRIF_ECOCENTRO.equals(tipologiaRifiuto)) {
			return "Rifiuti particolari";
		} else {
			return tipologiaRifiuto;
		} 
	}
}
