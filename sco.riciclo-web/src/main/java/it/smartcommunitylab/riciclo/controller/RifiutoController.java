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
import it.smartcommunitylab.riciclo.model.Rifiuto;
import it.smartcommunitylab.riciclo.storage.AppSetup;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class RifiutoController {

	@Autowired
	private RepositoryManager storage;
	
	@Autowired
	private AppSetup appSetup;	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/rifiuto/{ownerId}/{draft}", method=RequestMethod.GET)
	public @ResponseBody List<Rifiuto> getRifiuti(@PathVariable String ownerId, @PathVariable Boolean draft) 
			throws ClassNotFoundException {
		List<Rifiuto> result = (List<Rifiuto>) storage.findData(Rifiuto.class, null, ownerId, draft);
		return result;
	}
	
	@RequestMapping(value="/rifiuto/{ownerId}/{draft}", method=RequestMethod.POST) 
	public @ResponseBody Rifiuto addRifiuto(@RequestBody Rifiuto rifiuto, @PathVariable String ownerId, @PathVariable Boolean draft) {
		rifiuto.setObjectId(UUID.randomUUID().toString());
		rifiuto.setOwnerId(ownerId);
		storage.addRifiuto(rifiuto, draft);
		return rifiuto;
	}
	
	@RequestMapping(value="/rifiuto/{ownerId}/{objectId}/{draft}", method=RequestMethod.PUT)
	public void updateRifiuto(@RequestBody Rifiuto rifiuto, @PathVariable String ownerId, 
			@PathVariable String objectId, @PathVariable Boolean draft) throws EntityNotFoundException {
		storage.updateRifiuto(rifiuto, draft);
	}
	
	@RequestMapping(value="/rifiuto/{ownerId}/{objectId}/{draft}", method=RequestMethod.DELETE)
	public void deleteRifiuto(@PathVariable String ownerId, @PathVariable String objectId, 
			@PathVariable Boolean draft) throws EntityNotFoundException {
		storage.removeRifiuto(ownerId, objectId, draft);
	}
	
}
