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
import it.smartcommunitylab.riciclo.model.Gestore;
import it.smartcommunitylab.riciclo.storage.AppSetup;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class GestoreController {

	@Autowired
	private RepositoryManager storage;
	
	@Autowired
	private AppSetup appSetup;	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="api/gestore/{ownerId}", method=RequestMethod.GET) 
	public @ResponseBody List<Gestore> getGestore(@PathVariable String ownerId, HttpServletRequest request,
			HttpServletResponse response) throws ClassNotFoundException {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		List<Gestore> result = (List<Gestore>) storage.findData(Gestore.class, null, ownerId, draft);
		return result;
	}
	
	@RequestMapping(value="api/gestore/{ownerId}", method=RequestMethod.POST) 
	public @ResponseBody Gestore addGestore(@RequestBody Gestore gestore, @PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response) {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		gestore.setObjectId(UUID.randomUUID().toString());
		gestore.setOwnerId(ownerId);
		storage.addGestore(gestore, draft);
		return gestore;
	}
	
	@RequestMapping(value="api/gestore/{ownerId}/{objectId}", method=RequestMethod.PUT)
	public @ResponseBody void updateGestore(@RequestBody Gestore gestore, @PathVariable String ownerId,
			@PathVariable String objectId, HttpServletRequest request, 
			HttpServletResponse response)	throws EntityNotFoundException {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		gestore.setObjectId(objectId);
		gestore.setOwnerId(ownerId);
		storage.updateGestore(gestore, draft);
	}
	
	@RequestMapping(value="api/gestore/{ownerId}/{objectId}", method=RequestMethod.DELETE)
	public @ResponseBody void deleteGestore(@PathVariable String ownerId,	@PathVariable String objectId, 
			HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		storage.removeGestore(ownerId, objectId, draft);
	}
	
}
