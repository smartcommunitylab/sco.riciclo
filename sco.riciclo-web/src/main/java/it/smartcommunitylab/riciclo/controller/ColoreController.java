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
import it.smartcommunitylab.riciclo.model.Colore;
import it.smartcommunitylab.riciclo.storage.AppSetup;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.util.List;

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
public class ColoreController {

	@Autowired
	private RepositoryManager storage;
	
	@Autowired
	private AppSetup appSetup;	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="api/colore/{ownerId}", method=RequestMethod.GET)
	public @ResponseBody List<Colore> getColori(@PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response) 
			throws ClassNotFoundException {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		List<Colore> result = (List<Colore>) storage.findData(Colore.class, null, ownerId, draft);
		return result;
	}
	
	@RequestMapping(value="api/colore/{ownerId}", method=RequestMethod.POST) 
	public @ResponseBody Colore addColore(@RequestBody Colore colore, @PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response) {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		colore.setOwnerId(ownerId);
		storage.addColore(colore, draft);
		return colore;
	}
	
	@RequestMapping(value="api/colore/{ownerId}/{nome}", method=RequestMethod.PUT)
	public @ResponseBody void updateColore(@RequestBody Colore colore, @PathVariable String ownerId, 
			@PathVariable String nome, HttpServletRequest request,
			HttpServletResponse response) throws EntityNotFoundException {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		colore.setOwnerId(ownerId);
		colore.setNome(nome);
		storage.updateColore(colore, draft);
	}
	
	@RequestMapping(value="api/colore/{ownerId}/{nome}", method=RequestMethod.DELETE)
	public @ResponseBody void deleteColore(@PathVariable String ownerId, @PathVariable String nome, 
			HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		storage.removeColore(ownerId, nome, draft);
	}
	
}
