package it.smartcommunitylab.riciclo.riapp.importer;

import it.smartcommunitylab.riciclo.riapp.importer.model.RiappIstruzioni;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

public class RiappImportIstruzioni {
	private static final transient Logger logger = LoggerFactory.getLogger(RiappImportIstruzioni.class);
	
	private String baseDir;
	
	public RiappImportIstruzioni(String baseDir) {
		this.baseDir = baseDir;
	}
	
	/**
	 * 
	 * @param fileId
	 * @return <tipologiaRaccolta, RiappIstruzioni>
	 * @throws Exception
	 */
	public Map<String, RiappIstruzioni> readIstruzioni(String fileId) throws Exception {
		String startString = "<ul><li>";
		String endString = "</li></ul>";
		int startIndex = -1;
		int endIndex = -1;
		Map<String, RiappIstruzioni> result = new HashMap<String, RiappIstruzioni>();
		FileReader fileReader = new FileReader(baseDir + "/ist/ist_" + fileId.toLowerCase() + ".json");
		JsonNode rootNode = Utils.readJsonFromReader(fileReader);
		Iterator<JsonNode> rootElements = rootNode.elements();
		while(rootElements.hasNext()) {
			JsonNode node = rootElements.next();
			RiappIstruzioni riappIstruzioni = new RiappIstruzioni();
			riappIstruzioni.setFrazione(node.path("nome_fraz").asText());
			String desc1 = node.path("descr1").asText();
			startIndex = desc1.indexOf(startString);
			if(startIndex != -1) {
				endIndex = desc1.indexOf(endString);
				if(endIndex != -1) {
					String comeConferire = desc1.substring(startIndex + startString.length(), endIndex);
					riappIstruzioni.setComeConferire(comeConferire);
				}
			}
			String desc2 = node.path("descr2").asText();
			startIndex = desc2.indexOf(startString);
			if(startIndex != -1) {
				endIndex = desc2.indexOf(endString);
				if(endIndex != -1) {
					String prestaAttenzione = desc2.substring(startIndex + startString.length(), endIndex);
					riappIstruzioni.setPrestaAttenzione(prestaAttenzione);
				}
			}
			result.put(riappIstruzioni.getFrazione(), riappIstruzioni);
		}
		return result;
	}
	
	public static RiappIstruzioni getIstruzioni(String tipologiaRaccolta, Map<String, RiappIstruzioni> istruzioniMap) {
		RiappIstruzioni result = null;
		for(String key : istruzioniMap.keySet()) {
			if(tipologiaRaccolta.contains(key)) {
				result = istruzioniMap.get(key);
				break;
			}
		}
		return result;
	}
	
}
