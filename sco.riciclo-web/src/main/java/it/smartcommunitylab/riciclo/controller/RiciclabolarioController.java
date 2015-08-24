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

import it.smartcommunitylab.riciclo.exception.EntityNotFoundException;
import it.smartcommunitylab.riciclo.model.Area;
import it.smartcommunitylab.riciclo.model.Riciclabolario;
import it.smartcommunitylab.riciclo.storage.AppSetup;
import it.smartcommunitylab.riciclo.storage.DataSetInfo;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;


@Controller
public class RiciclabolarioController {
	private static final transient Logger logger = LoggerFactory.getLogger(RiciclabolarioController.class);
	
	@Autowired
	private RepositoryManager storage;
	
	@Autowired
	private AppSetup appSetup;	
	
	@RequestMapping(value="/riciclabolario/{ownerId}", method=RequestMethod.GET)
	public @ResponseBody List<Riciclabolario> getRiciclabolario(@PathVariable String ownerId, 
			HttpServletRequest request) throws ClassNotFoundException {
		boolean draft = Utils.getDraft(request);
		List<String> comuni = Lists.newArrayList(); 
		String[] comuniArray = request.getParameterValues("comune[]");
		if(comuniArray!= null) {
			comuni = Arrays.asList(comuniArray);
		}
		if(comuni.isEmpty()) {
			//da appId estraggo lista comuni (codici ISTAT)
			DataSetInfo appInfo = appSetup.findAppById(ownerId);
			if(appInfo == null) {
				if(logger.isInfoEnabled()) {
					logger.info("ownerId not found:" + ownerId);
				}
				return new ArrayList<Riciclabolario>();
			}
			comuni = appInfo.getComuni();
		}
		//ricerca tutti Riciclabolario che insistono su un'area appartenete al sotto-albero di ogni comune individuato
		//map <objectId, Riciclabolario>
		Map<String, Riciclabolario> resultMapRiciclabolario = new HashMap<String, Riciclabolario>();
		Map<String, Area> resultMapArea  = new HashMap<String, Area>();
		for(String comune : comuni) {
			Utils.findAree(comune, ownerId, draft, resultMapArea, storage);
		}
		Utils.findRiciclabolario(resultMapArea, ownerId, draft, resultMapRiciclabolario, storage);
		List<Riciclabolario> result = Lists.newArrayList(resultMapRiciclabolario.values());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/riciclabolario/{ownerId}", method=RequestMethod.POST) 
	public @ResponseBody List<Riciclabolario> addRiciclabolario(@RequestBody Map<String, Object> data, 
			@PathVariable String ownerId,	HttpServletRequest request) {
		boolean draft = Utils.getDraft(request);
		String area = (String) data.get("area");
		String tipologiaRifiuto = (String) data.get("tipologiaRifiuto");
		String rifiuto = (String) data.get("rifiuto");
		List<String> tipologiaUtenzaList = (List<String>) data.get("tipologiaUtenza");
		List<Riciclabolario> response = new ArrayList<Riciclabolario>();
		for(String tipologiaUtenza : tipologiaUtenzaList) {
			Riciclabolario riciclabolario = new Riciclabolario();
			riciclabolario.setObjectId(UUID.randomUUID().toString());
			riciclabolario.setOwnerId(ownerId);
			riciclabolario.setArea(area);
			riciclabolario.setRifiuto(rifiuto);
			riciclabolario.setTipologiaRifiuto(tipologiaRifiuto);
			riciclabolario.setTipologiaUtenza(tipologiaUtenza);
			storage.addRiciclabolario(riciclabolario, draft);
			response.add(riciclabolario);
		}
		return response;
	}
	
	@RequestMapping(value="/riciclabolario/{ownerId}/{objectId}", method=RequestMethod.DELETE)
	public @ResponseBody void deleteRiciclabolarioById(@PathVariable String ownerId, 
			@PathVariable String objectId, HttpServletRequest request) throws EntityNotFoundException {
		boolean draft = Utils.getDraft(request);
		storage.removeRiciclabolario(ownerId, objectId, draft);
	}
	
	@RequestMapping(value="/riciclabolario/{ownerId}", method=RequestMethod.DELETE)
	public @ResponseBody void deleteRiciclabolario(@RequestBody Riciclabolario riciclabolario, 
			@PathVariable String ownerId, HttpServletRequest request) throws EntityNotFoundException {
		boolean draft = Utils.getDraft(request);
		riciclabolario.setOwnerId(ownerId);
		storage.removeRiciclabolario(riciclabolario, draft);
	}
	
}
