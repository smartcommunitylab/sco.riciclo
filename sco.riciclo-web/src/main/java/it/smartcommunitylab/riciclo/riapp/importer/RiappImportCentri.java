package it.smartcommunitylab.riciclo.riapp.importer;

import it.smartcommunitylab.riciclo.app.importer.converter.RifiutiConverter;
import it.smartcommunitylab.riciclo.model.OrarioApertura;
import it.smartcommunitylab.riciclo.riapp.importer.model.RiappCentro;

import java.io.FileReader;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

public class RiappImportCentri {
	private static final transient Logger logger = LoggerFactory.getLogger(RiappImportCentri.class);

	private String baseDir;
	
	public RiappImportCentri(String baseDir) {
		this.baseDir = baseDir;
	}
	
	public List<RiappCentro> readListaCentri(String fileId) throws Exception {
		List<RiappCentro> result = Lists.newArrayList();
		FileReader fileReader = new FileReader(baseDir + "/cen/cen_" + fileId.toLowerCase() + ".json");
		JsonNode rootNode = Utils.readJsonFromReader(fileReader);
		Iterator<JsonNode> rootElements = rootNode.elements();
		while(rootElements.hasNext()) {
			JsonNode node = rootElements.next();
			RiappCentro centro = new RiappCentro();
			centro.setNome(node.path("nome_centro").asText());
			centro.setAccesso(node.path("accesso_centro").asText());
			centro.setIndirizzo(node.path("indirizzo_centro").asText());
			String[] loc = node.path("loc_centro").asText().split(",");
			double lat = Double.valueOf(loc[0]);
			double lnt = Double.valueOf(loc[1]);
			centro.setLatitudine(lat);
			centro.setLongitudine(lnt);
			centro.setCosa(node.path("cosa_centro").asText());
			centro.setInfo(node.path("info_centro").asText());
			centro.getOrario().put(RifiutiConverter.LUN, node.path("centro_lun").asText());
			centro.getOrario().put(RifiutiConverter.MAR, node.path("centro_mar").asText());
			centro.getOrario().put(RifiutiConverter.MER, node.path("centro_mer").asText());
			centro.getOrario().put(RifiutiConverter.GIO, node.path("centro_gio").asText());
			centro.getOrario().put(RifiutiConverter.VEN, node.path("centro_ven").asText());
			centro.getOrario().put(RifiutiConverter.SAB, node.path("centro_sab").asText());
			centro.getOrario().put(RifiutiConverter.DOM, node.path("centro_dom").asText());
			result.add(centro);
		}
		return result;
	}
	
	public static List<OrarioApertura> convertOrario(RiappCentro centro) {
		String[] giorniSettimana = new String[] {RifiutiConverter.LUN, RifiutiConverter.MAR,
				RifiutiConverter.MER, RifiutiConverter.GIO, RifiutiConverter.VEN,
				RifiutiConverter.SAB, RifiutiConverter.DOM};
		List<OrarioApertura> result = Lists.newArrayList();
		for(String giornoSettimana : giorniSettimana) {
			String orario = centro.getOrario().get(giornoSettimana);
			if((orario == null) || (orario.equalsIgnoreCase("CHIUSO"))) {
				continue;
			}
			OrarioApertura orarioApertura = new OrarioApertura();
			orarioApertura.setDataA("2016-01-01");
			orarioApertura.setDataA("2016-12-31");
			orarioApertura.setIl(giornoSettimana);
			String[] oreApertura = orario.split("[-–]");
			if(oreApertura.length != 2) {
				continue;
			}
			orarioApertura.setDalle(oreApertura[0].replace(".", ":").trim());
			orarioApertura.setAlle(oreApertura[1].replace(".", ":").trim());
			result.add(orarioApertura);
		}
		return result;
	}
	
}
