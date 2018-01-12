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

package it.smartcommunitylab.riciclo.app.importer.converter;

import it.smartcommunitylab.riciclo.app.importer.ImportError;
import it.smartcommunitylab.riciclo.app.importer.kml.KMLData;
import it.smartcommunitylab.riciclo.app.importer.kml.KMLReader;
import it.smartcommunitylab.riciclo.app.importer.model.PuntiRaccolta;
import it.smartcommunitylab.riciclo.app.importer.model.Rifiuti;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.WordUtils;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Component
public class DataImporter {

	private Set<String> expectedSheets;
//	private List<String> oneColumnAsMany;
	private Properties primaryKeys;
	
	private Map<String, Collection<KMLData>> isole;
	private Map<String, Collection<KMLData>> crm;

	public DataImporter() throws Exception {
		expectedSheets = Sets.newHashSet("TIPOLOGIA_PROFILO","TIPOLOGIA_UTENZA","GESTORI","ISTITUZIONI","AREE","SEGNALAZIONI","RICICLABOLARIO","TIPOLOGIA_RIFIUTO","COLORI","TIPOLOGIA_RACCOLTA","RACCOLTE","TIPOLOGIA_PUNTO_RACCOLTA","PUNTI_RACCOLTA");
//		String[] exSheets = new String[] { "TIPOLOGIA_RIFIUTO", "TIPOLOGIA_UTENZA", "TIPOLOGIA_RACCOLTA" };
		primaryKeys = new Properties();
		primaryKeys.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("primary_keys.txt"));
		
//		oneColumnAsMany = Arrays.asList(exSheets);
//		oneColumnAsMany = Lists.newArrayList();
	}
	
	public Rifiuti importRifiuti(InputStream xlsIs, InputStream isoleIs, InputStream crmIs) throws Exception {
		if (isoleIs != null) {
			isole = KMLReader.readIsole(isoleIs);
		}
		if (crmIs != null) {
			crm = KMLReader.readCRM(crmIs);
		}
		return readExcel(xlsIs);
	}

	private Rifiuti readExcel(InputStream inp) throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(inp));
		ExcelExtractor extractor = new ExcelExtractor(wb);

		extractor.setFormulasNotResults(true);
		extractor.setIncludeSheetNames(false);

		Rifiuti rifiuti = new Rifiuti();

		Set<String> sheetNames = Sets.newHashSet();
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			sheetNames.add(wb.getSheetAt(i).getSheetName());
		}
		
		Set missingExpected = Sets.newHashSet(expectedSheets);
		missingExpected.removeAll(sheetNames);
		
		Set additionalFound = Sets.newHashSet(sheetNames);
		additionalFound.removeAll(expectedSheets);		
		
		if (!missingExpected.isEmpty() || !additionalFound.isEmpty()) {
			throw new ImportError(Lists.newArrayList("Missing sheet(s) expected: " + missingExpected, "Additional sheet(s) found: " + additionalFound));
		}
		
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			Sheet sheet = wb.getSheetAt(i);
			Thread.sleep(1000);
//			System.out.println(sheet.getSheetName());
//			if (sheet.getRow(0).getLastCellNum() == 1 && !oneColumnAsMany.contains(sheet.getSheetName())) {
//			} else {
			{
				System.err.println(">" + sheet.getSheetName());
				List<Map<String, String>> result = getSheetMap(sheet);
				mapMap(rifiuti, sheet.getSheetName(), result);
			}
		}

		completePuntiRaccolta(rifiuti);
		
		return rifiuti;
	}

	private List<Map<String, String>> getSheetMap(Sheet sheet) {
		Row row = sheet.getRow(0);
		List<String> keys = new ArrayList<String>();
		int firstRow = 1;
		if (row.getLastCellNum() != 1) {
			for (int j = 0; j < row.getLastCellNum(); j++) {
				String key = WordUtils.capitalizeFully(" " + getCellValue(row.getCell(j)).replace(' ', '_'), new char[] { '_' }).replace("_", "").trim();
				keys.add(key);
			}
		} else {
			firstRow = 1;
			keys.add("valore");
		}

		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		for (int i = firstRow; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			Map<String, String> map = new TreeMap<String, String>();
			boolean add = false;
			for (int j = 0; j < row.getLastCellNum(); j++) {
				if (j >= keys.size()) {
					continue;
				}
				if (row.getCell(j) != null) {
					String value = getCellValue(row.getCell(j)).replace("_", " ").trim();
					if (!value.isEmpty()) {
						add = true;
					}
					try {
						map.put(keys.get(j), value);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					map.put(keys.get(j), "");
				}
			}
			if (add) {
				result.add(map);
			}
		}

		return result;
	}
	
	private String getCellValue(Cell cell) {
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		case Cell.CELL_TYPE_NUMERIC:
			String value;
			Calendar cal = new GregorianCalendar();
			cal.setTime(cell.getDateCellValue());
			if (cal.get(Calendar.YEAR) < 2014) {
				value = cal.get(Calendar.HOUR_OF_DAY) + ":" + (cal.get(Calendar.MINUTE) + "0").substring(0, 2);
			} else {
				String month = "0" + (1 + cal.get(Calendar.MONTH));
				String day = "0" + cal.get(Calendar.DAY_OF_MONTH);
				value = cal.get(Calendar.YEAR) + "-" + month.substring(month.length() - 2, month.length()) + "-" + day.substring(day.length() - 2, day.length());
			}
			return value;
		}
		return "";
	}

	private Rifiuti mapMap(Rifiuti rifiuti, String sheetName, List<Map<String, String>> data) throws Exception {
		String className = "it.smartcommunitylab.riciclo.app.importer.model." + WordUtils.capitalizeFully(sheetName.replace(' ', '_'), new char[] { '_' }).replace("_", "").trim();
		String field = WordUtils.capitalizeFully(" " + sheetName.replace(' ', '_'), new char[] { '_' }).replace("_", "").trim();

		System.err.println(className);
//		if ("it.smartcommunitylab.riciclo.app.importer.model.Colori".equals(className)) {
//			System.out.println();
//		}
		
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			System.err.println("No class found for " + sheetName);
			throw new ImportError(Lists.newArrayList("No class found for " + sheetName));
		}

		ObjectMapper mapper = new ObjectMapper();
		List<Object> fields = new ArrayList<Object>();

		for (Map<String, String> map : data) {
			Object o = mapper.convertValue(map, clazz);
			fields.add(o);
		}

		Method method2 = Rifiuti.class.getMethod("set" + WordUtils.capitalize(field), List.class);
		method2.invoke(rifiuti, fields);

		return rifiuti;
	}


	private void completePuntiRaccolta(Rifiuti rifiuti) throws Exception {
		List<PuntiRaccolta> toRemove = Lists.newArrayList();
		List<PuntiRaccolta> toAdd = Lists.newArrayList();		
		
		for (PuntiRaccolta puntoRaccolta : rifiuti.getPuntiRaccolta()) {

			String name;
			Map<String, Collection<KMLData>> kml;
			boolean isCrm = false;
			boolean hasDescription = puntoRaccolta.getZona() == null || puntoRaccolta.getZona().isEmpty();
			if (isCrmLike(puntoRaccolta.getTipologiaPuntiRaccolta().toLowerCase())) {
				kml = crm;
				name = puntoRaccolta.getZona().toLowerCase();
				isCrm = true;
			} else if ("isola ecologica".equals(puntoRaccolta.getTipologiaPuntiRaccolta().toLowerCase())) {
				kml = isole;
				if (puntoRaccolta.getZona() == null || puntoRaccolta.getZona().isEmpty()) {
					name = "";
				} else {
					name = puntoRaccolta.getZona().toLowerCase();
				}
			} else {
				continue;
			}

			if (kml != null) {
				if (!name.isEmpty() && (!kml.containsKey(name) || (isCrm && hasDescription == true))) {
					continue;
				}
				
				Collection<KMLData> data = kml.get(name);
				if (name.isEmpty()) {
					data = new HashSet<KMLData>();
					for (Collection<KMLData> colls : kml.values()) {
						data.addAll(colls);
					}
				}

				Iterator<KMLData> it = data.iterator();

				ObjectMapper mapper = new ObjectMapper();
				toRemove.add(puntoRaccolta);
				String prString = mapper.writeValueAsString(puntoRaccolta);
				while (it.hasNext()) {
					PuntiRaccolta newPuntoRaccolta = mapper.readValue(prString, PuntiRaccolta.class);
					toAdd.add(newPuntoRaccolta);
					KMLData next = (KMLData) it.next();
					newPuntoRaccolta.setLocalizzazione(next.getLat() + "," + next.getLon());
					if (name.isEmpty()) {
						newPuntoRaccolta.setZona(next.getName());
					}
					if (next.getAttributes() != null) {
						for (Integer key : next.getAttributes().keySet()) {
							String value = next.getAttributes().get(key);
							switch (key) {
							case 5:
								newPuntoRaccolta.setDettagliZona(value);
								break;
							case 11:
								newPuntoRaccolta.setGettoniera(value);
								break;
							case 12:
								newPuntoRaccolta.setResiduo(value);
								break;
							case 13:
								newPuntoRaccolta.setImbCarta(value);
								break;
							case 14:
								newPuntoRaccolta.setImbPlMet(value);
								break;
							case 15:
								newPuntoRaccolta.setOrganico(value);
								break;
							case 16:
								newPuntoRaccolta.setImbVetro(value);
								break;
							case 17:
								newPuntoRaccolta.setIndumenti(value);
								break;
							case 18:
								newPuntoRaccolta.setNote(value);
								break;
							}
						}
					}
				}
			}
		}
		
		rifiuti.getPuntiRaccolta().removeAll(toRemove);
		rifiuti.getPuntiRaccolta().addAll(toAdd);
	}

	private boolean isCrmLike(String tipologia) {
		return "crm".equals(tipologia.toLowerCase()) || "crz".equals(tipologia.toLowerCase()) || "ci".equals(tipologia.toLowerCase());
	}


}
