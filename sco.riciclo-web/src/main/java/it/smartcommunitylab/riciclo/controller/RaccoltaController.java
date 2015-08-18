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
import it.smartcommunitylab.riciclo.model.Raccolta;
import it.smartcommunitylab.riciclo.storage.AppSetup;
import it.smartcommunitylab.riciclo.storage.DataSetInfo;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;


@Controller
public class RaccoltaController {

	@Autowired
	private RepositoryManager storage;
	
	@Autowired
	private AppSetup appSetup;	
	
	public @ResponseBody List<Raccolta> getRaccolte(@PathVariable String ownerId, @PathVariable Boolean draft) 
			throws ClassNotFoundException {
		//da appId estraggo lista comuni (codici ISTAT)
		DataSetInfo appInfo = appSetup.findAppById(ownerId);
		if(appInfo == null) {
			return new ArrayList<Raccolta>();
		}
		List<String> comuni = appInfo.getComuni();
		//ricerca tutt le Raccolte che insistono su un'area appartenete al sotto-albero di ogni comune individuato
		//map <objectId, Raccolta>
		Map<String, Raccolta> resultMapRaccolta = new HashMap<String, Raccolta>();
		Map<String, Area> resultMapArea  = new HashMap<String, Area>();
		for(String comune : comuni) {
			Utils.findAree(comune, ownerId, draft, resultMapArea, storage);
			Utils.findRaccolte(resultMapArea, ownerId, draft, resultMapRaccolta, storage);
		}
		List<Raccolta> result = Lists.newArrayList(resultMapRaccolta.values());
		return result;
	}
	
	@RequestMapping(value="/raccolta/{ownerId}/{draft}", method=RequestMethod.POST) 
	public @ResponseBody Raccolta addRaccolta(@RequestBody Raccolta raccolta, 
			@PathVariable String ownerId,	@PathVariable Boolean draft) {
		Date actualDate = new Date();
		raccolta.setObjectId(UUID.randomUUID().toString());
		raccolta.setCreationDate(actualDate);
		raccolta.setLastUpdate(actualDate);
		storage.addRaccolta(raccolta, draft);
		return raccolta;
	}
	
	@RequestMapping(value="/raccolta/{ownerId}/{objectId}/{draft}", method=RequestMethod.DELETE)
	public void deleteRaccoltaById(@PathVariable String ownerId, @PathVariable String objectId, 
			@PathVariable Boolean draft) throws EntityNotFoundException {
		storage.removeRaccolta(ownerId, objectId, draft);
	}
	
}
