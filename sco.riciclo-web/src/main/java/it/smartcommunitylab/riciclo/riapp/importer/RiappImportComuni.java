package it.smartcommunitylab.riciclo.riapp.importer;

import it.smartcommunitylab.riciclo.riapp.importer.model.RiappArea;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

public class RiappImportComuni {
	private static final transient Logger logger = LoggerFactory.getLogger(RiappImportComuni.class);

	private String baseDir;
	
	public RiappImportComuni(String baseDir) {
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
	
	public List<String> readListaComuni(String fileId) throws Exception {
		List<String> comuneEstesoList = Lists.newArrayList();
		FileReader fileReader = new FileReader(baseDir + "/comuni/listacomuni_" + fileId.toLowerCase() + ".json");
		JsonNode rootNode = Utils.readJsonFromReader(fileReader);
		Iterator<JsonNode> rootElements = rootNode.elements();
		while(rootElements.hasNext()) {
			JsonNode nodeArea = rootElements.next();
			String comuneEsteso = nodeArea.path("comune_esteso").asText();
			if(!comuneEstesoList.contains(comuneEsteso)) {
				comuneEstesoList.add(comuneEsteso);
			}
		}
		return comuneEstesoList;
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
