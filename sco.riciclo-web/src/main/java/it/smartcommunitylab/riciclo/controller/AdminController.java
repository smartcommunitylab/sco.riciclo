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

package it.smartcommunitylab.riciclo.controller;

import it.smartcommunitylab.riciclo.exception.UnauthorizedException;
import it.smartcommunitylab.riciclo.model.AppDataRifiuti;
import it.smartcommunitylab.riciclo.model.Tipologia;
import it.smartcommunitylab.riciclo.storage.AppSetup;
import it.smartcommunitylab.riciclo.storage.DataSetInfo;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
public class AdminController {
	private static final transient Logger logger = LoggerFactory.getLogger(AdminController.class);
			
	@Autowired
	private RepositoryManager storage;

	@Autowired
	private AppSetup appSetup;

	@RequestMapping(value = "/dataset/{ownerId}", method = RequestMethod.POST)
	public @ResponseBody String updateDataSetInfo(@RequestBody DataSetInfo dataSetInfo, 
			@PathVariable String ownerId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, appSetup, false, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.saveAppToken(dataSetInfo.getOwnerId(), dataSetInfo.getToken(), true);
		storage.saveAppInfo(dataSetInfo, true);
		storage.saveAppState(dataSetInfo.getOwnerId(), true);
		Set<Tipologia> data = getIconeTipologiaPuntoRaccolta();
		storage.updateTipologie(dataSetInfo.getOwnerId(), data, "iconeTipologiaPuntoRaccolta", true);
		data = getIconeTipologiaRifiuto();
		storage.updateTipologie(dataSetInfo.getOwnerId(), data, "iconeTipologiaRifiuto", true);
		storage.saveAppToken(dataSetInfo.getOwnerId(), dataSetInfo.getToken(), false);
		storage.saveAppInfo(dataSetInfo, false);
		storage.saveAppState(dataSetInfo.getOwnerId(), false);
		appSetup.init();
		return new ObjectMapper().writeValueAsString(dataSetInfo);
	}
	
	@RequestMapping(value = "/reload/{ownerId}", method = RequestMethod.GET)
	public @ResponseBody String reload(@PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, appSetup, false, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		appSetup.init();
		if(logger.isInfoEnabled()) {
			logger.info("reload dataSet");
		}
		return "OK";
	}
	
	@RequestMapping(value = "/data/{ownerId}/{datasetId}", method = RequestMethod.GET)
	public @ResponseBody AppDataRifiuti exportData(@PathVariable String ownerId, 
			@PathVariable String datasetId, @RequestParam Boolean draft,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, appSetup, false, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		AppDataRifiuti appDataRifiuti = storage.exportData(datasetId, draft);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("exportData[%s]: %s", ownerId, datasetId));
		}
		return appDataRifiuti;
	}

	@RequestMapping(value = "/data/{ownerId}/{datasetId}", method = RequestMethod.POST)
	public @ResponseBody void importData(@PathVariable String ownerId, 
			@PathVariable String datasetId, @RequestParam Boolean draft,
			@RequestBody AppDataRifiuti appDataRifiuti,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, appSetup, false, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.importData(datasetId, draft, appDataRifiuti);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("importData[%s]: %s", ownerId, datasetId));
		}
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleError(HttpServletRequest request, Exception exception) {
		return Utils.handleError(exception);
	}
	
	private Set<Tipologia> getIconeTipologiaRifiuto() {
		Set<Tipologia> result = new HashSet<Tipologia>();
		try {
			InputStreamReader reader = new InputStreamReader(AdminController.class.getResourceAsStream("/json/icone-tipo-rifiuto.json"), 
					"utf-8");
			JsonNode rootNode = it.smartcommunitylab.riciclo.riapp.importer.Utils.readJsonFromReader(reader);
			Iterator<JsonNode> rootElements = rootNode.elements();
			while(rootElements.hasNext()) {
				JsonNode node = rootElements.next();
				Tipologia tipologia = it.smartcommunitylab.riciclo.riapp.importer.Utils.toObject(node, Tipologia.class);
				result.add(tipologia);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}
	
	private Set<Tipologia> getIconeTipologiaPuntoRaccolta() {
		Set<Tipologia> result = new HashSet<Tipologia>();
		try {
			InputStreamReader reader = new InputStreamReader(AdminController.class.getResourceAsStream("/json/icone-tipo-punto-raccolta.json"), 
					"utf-8");
			JsonNode rootNode = it.smartcommunitylab.riciclo.riapp.importer.Utils.readJsonFromReader(reader);
			Iterator<JsonNode> rootElements = rootNode.elements();
			while(rootElements.hasNext()) {
				JsonNode node = rootElements.next();
				Tipologia tipologia = it.smartcommunitylab.riciclo.riapp.importer.Utils.toObject(node, Tipologia.class);
				result.add(tipologia);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}
	
}
