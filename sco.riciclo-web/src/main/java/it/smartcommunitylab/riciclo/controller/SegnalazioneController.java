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
import it.smartcommunitylab.riciclo.model.Segnalazione;
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
public class SegnalazioneController {

	@Autowired
	private RepositoryManager storage;
	
	@Autowired
	private AppSetup appSetup;	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="api/segnalazione/{ownerId}", method=RequestMethod.GET)
	public @ResponseBody List<Segnalazione> getSegnalazioni(@PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response)	throws ClassNotFoundException {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		List<Segnalazione> result = (List<Segnalazione>) storage.findData(Segnalazione.class, null, ownerId, draft);
		return result;
	}
	
	@RequestMapping(value="api/segnalazione/{ownerId}", method=RequestMethod.POST) 
	public @ResponseBody Segnalazione addRifiuto(@RequestBody Segnalazione segnalazione, @PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response) {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		segnalazione.setObjectId(UUID.randomUUID().toString());
		segnalazione.setOwnerId(ownerId);
		storage.addSegnalazione(segnalazione, draft);
		return segnalazione;
	}
	
	@RequestMapping(value="api/segnalazione/{ownerId}/{objectId}", method=RequestMethod.PUT)
	public @ResponseBody void updateRifiuto(@RequestBody Segnalazione segnalazione, @PathVariable String ownerId, 
			@PathVariable String objectId, HttpServletRequest request,
			HttpServletResponse response) throws EntityNotFoundException {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		segnalazione.setObjectId(objectId);
		segnalazione.setOwnerId(ownerId);
		storage.updateSegnalazione(segnalazione, draft);
	}
	
	@RequestMapping(value="api/segnalazione/{ownerId}/{objectId}", method=RequestMethod.DELETE)
	public @ResponseBody void deleteRifiuto(@PathVariable String ownerId, @PathVariable String objectId, 
			HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		storage.removeSegnalazione(ownerId, objectId, draft);
	}
	
}
