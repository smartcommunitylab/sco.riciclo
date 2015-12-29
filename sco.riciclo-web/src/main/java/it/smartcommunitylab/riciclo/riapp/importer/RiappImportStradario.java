package it.smartcommunitylab.riciclo.riapp.importer;

import it.smartcommunitylab.riciclo.riapp.importer.model.RiappArea;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

public class RiappImportStradario {
	private static final transient Logger logger = LoggerFactory.getLogger(RiappImportStradario.class);

	private String baseDir;
	
	public RiappImportStradario(String baseDir) {
		this.baseDir = baseDir;
	}
	
	public Map<String, String> readCodiciIstat() throws Exception {
		Map<String, String> codiceIstatMap = new HashMap<String, String>();
		FileReader fileReader = new FileReader(baseDir + "/riapp_comuni.json");
		JsonNode rootNode = Utils.readJsonFromReader(fileReader);
		Iterator<JsonNode> rootElements = rootNode.elements();
		while(rootElements.hasNext()) {
			JsonNode node = rootElements.next();
			String comune = node.path("COMUNE").asText();
			String codiceIstat = node.path("COD ISTAT").asText();
			codiceIstatMap.put(comune, codiceIstat);
		}
		return codiceIstatMap;
	}
	
	public List<JsonNode> readStradario(String fileId) throws Exception {
		String comuneEsteso = Utils.capitalize(fileId.toLowerCase());
		List<JsonNode> result = Lists.newArrayList();
		Map<String, ObjectNode> comuneMap = new HashMap<String, ObjectNode>();
		FileReader fileReader = new FileReader(baseDir + "/stradario/strade_" + fileId.toLowerCase() + ".json");
		JsonNode rootNode = Utils.readJsonFromReader(fileReader);
		//read zone
		Iterator<Entry<String, JsonNode>> zoneFields = rootNode.path("zone").fields();
		while(zoneFields.hasNext()) {
			Map.Entry<String, JsonNode> elt = zoneFields.next();
			String zona = elt.getKey().trim();
			ObjectNode zonaNode = Utils.createObjectNode();
			zonaNode.put("comune_esteso", comuneEsteso);
			zonaNode.put("diz", elt.getValue().path("diz").asText());
			zonaNode.put("cal", elt.getValue().path("cal").asText());
			zonaNode.put("ist", elt.getValue().path("ist").asText());
			zonaNode.put("cen", elt.getValue().path("cen").asText());
			comuneMap.put(zona, zonaNode);
		}
		//read strade
		Iterator<JsonNode> stradeElements = rootNode.path("strade").elements();
		while(stradeElements.hasNext()) {
			JsonNode stradaNode = stradeElements.next();
			String zona = stradaNode.path("zona").asText();
			String via = stradaNode.path("via").asText().trim();
			ObjectNode zonaNode = comuneMap.get(zona);
			if(zonaNode == null) {
				logger.warn(String.format("zona not found for %s %s", zona, via));
				continue;
			}
			ObjectNode areaNode = Utils.createObjectNode();
			areaNode.put("comune_esteso", comuneEsteso);
			areaNode.put("comune", comuneEsteso + "-" + via);
			areaNode.put("zona_esteso", via);
			areaNode.put("diz", zonaNode.path("diz").asText());
			areaNode.put("cal", zonaNode.path("cal").asText());
			areaNode.put("ist", zonaNode.path("ist").asText());
			areaNode.put("cen", zonaNode.path("cen").asText());
			result.add(areaNode);
		}
		return result;
	}	
	
	public List<String> readListaValori(String fileId, String field) throws Exception {
		List<String> result = Lists.newArrayList();
		FileReader fileReader = new FileReader(baseDir + "/comuni/listacomuni_" + fileId.toLowerCase() + ".json");
		JsonNode rootNode = Utils.readJsonFromReader(fileReader);
		Iterator<JsonNode> rootElements = rootNode.elements();
		while(rootElements.hasNext()) {
			JsonNode nodeArea = rootElements.next();
			String name = nodeArea.path(field).asText();
			if(!result.contains(name)) {
				result.add(name);
			}
		}
		return result;
	}
	
	public List<RiappArea> readListaAree(String fileId) throws Exception {
		List<RiappArea> areaList = Lists.newArrayList();
		FileReader fileReader = new FileReader(baseDir + "/comuni/listacomuni_" + fileId.toLowerCase() + ".json");
		JsonNode rootNode = Utils.readJsonFromReader(fileReader);
		Iterator<JsonNode> rootElements = rootNode.elements();
		while(rootElements.hasNext()) {
			JsonNode nodeArea = rootElements.next();
			String comuneEsteso = nodeArea.path("comune_esteso").asText();
			RiappArea riappArea = new RiappArea();
			riappArea.setComuneEsteso(comuneEsteso);
			riappArea.setArac(nodeArea.path("arac").asText());
			riappArea.setCal(nodeArea.path("cal").asText());
			riappArea.setCen(nodeArea.path("cen").asText());
			//TODO riappArea.setCodiceISTAT());
			riappArea.setComune(nodeArea.path("comune").asText());
			riappArea.setDiz(nodeArea.path("diz").asText());
			riappArea.setIst(nodeArea.path("ist").asText());
			riappArea.setVarie(nodeArea.path("varie").asText());
			riappArea.setZonaEsteso(nodeArea.path("zona_esteso").asText());
			areaList.add(riappArea);
		}
		return areaList;
	}
}
