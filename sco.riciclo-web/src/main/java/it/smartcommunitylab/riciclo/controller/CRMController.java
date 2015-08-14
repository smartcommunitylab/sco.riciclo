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
import it.smartcommunitylab.riciclo.model.Crm;
import it.smartcommunitylab.riciclo.model.OrarioApertura;
import it.smartcommunitylab.riciclo.storage.AppSetup;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.util.Date;
import java.util.List;
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
public class CRMController {

	@Autowired
	private RepositoryManager storage;
	
	@Autowired
	private AppSetup appSetup;	
	
	@SuppressWarnings("unchecked")
	public @ResponseBody List<Crm> getCRM(@PathVariable String ownerId, @PathVariable Boolean draft) 
			throws ClassNotFoundException {
		List<Crm> result = (List<Crm>) storage.findData(Crm.class, null, ownerId, draft);
		return result;
	}
	
	@RequestMapping(value="/crm/{ownerId}/{draft}", method=RequestMethod.POST) 
	public @ResponseBody Crm addCRM(@RequestBody Crm crm, @PathVariable String ownerId, @PathVariable Boolean draft) {
		crm.setObjectId(UUID.randomUUID().toString());
		crm.setOwnerId(ownerId);
		Date actualDate = new Date();
		crm.setCreationDate(actualDate);
		crm.setLastUpdate(actualDate);
		storage.addCRM(crm, draft);
		return crm;
	}
	
	@RequestMapping(value="/crm/{ownerId}/{objectId}/{draft}", method=RequestMethod.PUT)
	public void updateCRM(@RequestBody Crm crm, @PathVariable String ownerId, 
			@PathVariable String objectId, @PathVariable Boolean draft) throws EntityNotFoundException {
		storage.updateCRM(crm, draft);
	}
	
	@RequestMapping(value="/crm/{ownerId}/{objectId}/{draft}", method=RequestMethod.DELETE)
	public void deleteCRM(@RequestBody Crm crm, @PathVariable String ownerId, 
			@PathVariable String objectId, @PathVariable Boolean draft) throws EntityNotFoundException {
		storage.removeCRM(ownerId, objectId, draft);
	}
	
	@RequestMapping(value="/crm/{ownerId}/{objectId}/{draft}", method=RequestMethod.POST)
	public @ResponseBody Crm addOrarioApertura(@RequestBody OrarioApertura orario, @PathVariable String ownerId, 
			@PathVariable String objectId, @PathVariable Boolean draft) throws ClassNotFoundException, EntityNotFoundException {
		Criteria criteriaId = new Criteria("objectId").is(objectId);
		Crm crmDB = storage.findOneData(Crm.class, criteriaId, ownerId, draft);
		if(crmDB == null) {
			throw new EntityNotFoundException(String.format("CRM with id %s not found", objectId));
		}
		crmDB.getOrarioApertura().add(orario);
		storage.updateCRM(crmDB, draft);
		return crmDB;
	}

	@RequestMapping(value="/crm/{ownerId}/{objectId}/{position}/{draft}", method=RequestMethod.DELETE)
	public @ResponseBody Crm deleteOrarioApertura(@PathVariable String ownerId, @PathVariable String objectId, 
			@PathVariable int position, @PathVariable Boolean draft) throws ClassNotFoundException, EntityNotFoundException {
		Criteria criteriaId = new Criteria("objectId").is(objectId);
		Crm crmDB = storage.findOneData(Crm.class, criteriaId, ownerId, draft);
		if(crmDB == null) {
			throw new EntityNotFoundException(String.format("CRM with id %s not found", objectId));
		}
		crmDB.getOrarioApertura().remove(position);
		storage.updateCRM(crmDB, draft);
		return crmDB;
	}

}
