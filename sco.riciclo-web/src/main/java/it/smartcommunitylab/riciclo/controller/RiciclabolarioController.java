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
import it.smartcommunitylab.riciclo.storage.AppInfo;
import it.smartcommunitylab.riciclo.storage.AppSetup;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class RiciclabolarioController {

	@Autowired
	private RepositoryManager storage;
	
	@Autowired
	private AppSetup appSetup;	
	
	public @ResponseBody List<Riciclabolario> getRiciclabolario(@PathVariable String appId, 
			@PathVariable Boolean draft) throws ClassNotFoundException {
		//da appId estraggo lista comuni (codici ISTAT)
		AppInfo appInfo = appSetup.findAppById(appId);
		if(appInfo == null) {
			return new ArrayList<Riciclabolario>();
		}
		List<String> comuni = appInfo.getComuni();
		//ricerca tutti Riciclabolario che insistono su un'area appartenete al sotto-albero di ogni comune individuato
		//map <objectId, Riciclabolario>
		Map<String, Riciclabolario> resultMap = new HashMap<String, Riciclabolario>();
		for(String comune : comuni) {
			Criteria criteriaISTAT = new Criteria("codiceISTAT").is(comune);
			Area areaComune = storage.findOneData(Area.class, criteriaISTAT, appId, draft);
			List<Area> areaList = new ArrayList<Area>();
			areaList.add(areaComune);
			Utils.findRiciclabolario(areaList, appId, draft, resultMap, storage);
		}
		List<Riciclabolario> result = new ArrayList<Riciclabolario>(resultMap.size());
		result.addAll(resultMap.values());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/riciclabolario/{appId}/{draft}", method=RequestMethod.POST) 
	public @ResponseBody List<Riciclabolario> addRiciclabolario(@RequestBody Map<String, Object> data, 
			@PathVariable String appId,	@PathVariable Boolean draft) {
		String area = (String) data.get("area");
		String tipologiaRifiuto = (String) data.get("tipologiaRifiuto");
		String rifiuto = (String) data.get("rifiuto");
		List<String> tipologiaUtenzaList = (List<String>) data.get("tipologiaUtenza");
		List<Riciclabolario> response = new ArrayList<Riciclabolario>();
		Date actualDate = new Date();
		for(String tipologiaUtenza : tipologiaUtenzaList) {
			Riciclabolario riciclabolario = new Riciclabolario();
			riciclabolario.setObjectId(UUID.randomUUID().toString());
			riciclabolario.setAppId(appId);
			riciclabolario.setCreationDate(actualDate);
			riciclabolario.setLastUpdate(actualDate);
			riciclabolario.setArea(area);
			riciclabolario.setRifiuto(rifiuto);
			riciclabolario.setTipologiaRifiuto(tipologiaRifiuto);
			riciclabolario.setTipologiaUtenza(tipologiaUtenza);
			storage.addRiciclabolario(riciclabolario, draft);
			response.add(riciclabolario);
		}
		return response;
	}
	
	@RequestMapping(value="/riciclabolario/{appId}/{objectId}/{draft}", method=RequestMethod.DELETE)
	public void deleteRiciclabolarioById(@PathVariable String appId, @PathVariable String objectId, 
			@PathVariable Boolean draft) throws EntityNotFoundException {
		storage.removeRiciclabolario(appId, objectId, draft);
	}
	
	@RequestMapping(value="/riciclabolario/{appId}/{draft}", method=RequestMethod.DELETE)
	public void deleteRiciclabolario(@RequestBody Riciclabolario riciclabolario, @PathVariable String appId, 
			@PathVariable Boolean draft) throws EntityNotFoundException {
		storage.removeRiciclabolario(riciclabolario, draft);
	}
	
}
