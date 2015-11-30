package it.smartcommunitylab.riciclo.riapp.importer;

import it.smartcommunitylab.riciclo.riapp.importer.model.RiappOrario;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

public class RiappImportCalendario {
	private static final transient Logger logger = LoggerFactory.getLogger(RiappImportCalendario.class);
	
	private String baseDir;
	
	public RiappImportCalendario(String baseDir) {
		this.baseDir = baseDir;
	}
	
	/**
	 * 
	 * @param fileId
	 * @return <tipologiaRaccolta, colore>
	 * @throws Exception
	 */
	public Map<String, String> readTipologiaRaccolta(String fileId) throws Exception {
		Map<String, String> result = new HashMap<String, String>();
		FileReader fileReader = new FileReader(baseDir + "/cal/cal_" + fileId.toLowerCase() + ".json");
		JsonNode rootNode = Utils.readJsonFromReader(fileReader);
		Iterator<Entry<String, JsonNode>> fields = rootNode.path("frazioni").fields();
		while(fields.hasNext()) {
			Map.Entry<String, JsonNode> elt = fields.next();
			String tipologiaRaccolta = elt.getValue().path("nome").asText();
			String colore = elt.getValue().path("colore").asText();
			if(Utils.isNotEmpty(tipologiaRaccolta)) {
				result.put(tipologiaRaccolta, colore);
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param fileId
	 * @return <tipologiaRaccolta, tipologiaPuntoRaccolta>
	 * @throws Exception
	 */
	public Map<String, String> readTipologiaPuntoRaccolta(String fileId) throws Exception {
		Map<String, String> result = new HashMap<String, String>();
		FileReader fileReader = new FileReader(baseDir + "/cal/cal_" + fileId.toLowerCase() + ".json");
		JsonNode rootNode = Utils.readJsonFromReader(fileReader);
		Iterator<Entry<String, JsonNode>> fields = rootNode.path("frazioni").fields();
		while(fields.hasNext()) {
			Map.Entry<String, JsonNode> elt = fields.next();
			String tipologiaRaccolta = elt.getValue().path("nome").asText();
			String tipologiaPuntoRaccolta = "Porta a porta " + tipologiaRaccolta;
			if(Utils.isNotEmpty(tipologiaRaccolta)) {
				result.put(tipologiaRaccolta, tipologiaPuntoRaccolta);
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param fileId
	 * @return <frazione, tipologiaRaccolta>
	 * @throws Exception
	 */
	public Map<String, String> readFrazioni(String fileId) throws Exception {
		Map<String, String> result = new HashMap<String, String>();
		FileReader fileReader = new FileReader(baseDir + "/cal/cal_" + fileId.toLowerCase() + ".json");
		JsonNode rootNode = Utils.readJsonFromReader(fileReader);
		Iterator<Entry<String, JsonNode>> fields = rootNode.path("frazioni").fields();
		while(fields.hasNext()) {
			Map.Entry<String, JsonNode> elt = fields.next();
			String frazione = elt.getKey();
			String tipologiaRaccolta = elt.getValue().path("nome").asText();
			if(Utils.isNotEmpty(tipologiaRaccolta)) {
				result.put(frazione, tipologiaRaccolta);
			}
		}
		return result;
	}
	
	public List<RiappOrario> readCalendario(String fileId) throws Exception {
		List<RiappOrario> result = Lists.newArrayList();
		FileReader fileReader = new FileReader(baseDir + "/cal/cal_" + fileId.toLowerCase() + ".json");
		JsonNode rootNode = Utils.readJsonFromReader(fileReader);
		Iterator<JsonNode> elements = rootNode.path("giornate").elements();
		int index = 0;
		while(elements.hasNext()) {
			JsonNode node = elements.next();
			if(logger.isInfoEnabled()) {
				logger.info("parse object " + index);
				index++;
			}
			try {
				RiappOrario orario = Utils.toObject(node, RiappOrario.class);
				result.add(orario);
			} catch (JsonProcessingException e) {
				if(logger.isWarnEnabled()) {
					logger.warn("parsing exception:" + e.getMessage());
				}
			}
		}
		return result;
	}
	
}
