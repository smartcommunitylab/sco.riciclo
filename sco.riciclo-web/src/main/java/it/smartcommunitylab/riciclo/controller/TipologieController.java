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
import it.smartcommunitylab.riciclo.model.Categorie;
import it.smartcommunitylab.riciclo.model.Tipologia;
import it.smartcommunitylab.riciclo.model.TipologiaProfilo;
import it.smartcommunitylab.riciclo.model.TipologiaPuntoRaccolta;
import it.smartcommunitylab.riciclo.storage.AppSetup;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

import com.google.common.collect.Lists;


@Controller
public class TipologieController {

	@Autowired
	private RepositoryManager storage;
	
	@Autowired
	private AppSetup appSetup;	
	
	@RequestMapping(value="/tipologia/utenza/{ownerId}", method=RequestMethod.GET)
	public @ResponseBody List<Tipologia> getTipologiaUtenza(@PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response) 
			throws ClassNotFoundException {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		List<Tipologia> result= Lists.newArrayList();
		Categorie categorie = storage.findOneData(Categorie.class, null, ownerId, draft);
		if(categorie != null) {
			result.addAll(categorie.getTipologiaUtenza());
		}
		return result;
	}

	@RequestMapping(value="/tipologia/utenza/{ownerId}", method=RequestMethod.POST)
	public @ResponseBody void updateTipologiaUtenza(@RequestBody List<Tipologia> tipologia,	@PathVariable String ownerId, 
		HttpServletRequest request, HttpServletResponse response)	throws ClassNotFoundException, EntityNotFoundException {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		Set<Tipologia> data = new HashSet<Tipologia>(tipologia);
		storage.updateTipologie(ownerId, data, "tipologiaUtenza", draft);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/tipologia/puntoraccolta/{ownerId}", method=RequestMethod.GET)
	public @ResponseBody List<TipologiaPuntoRaccolta> getTipologiaPuntoRaccolta(@PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response)	throws ClassNotFoundException {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		List<TipologiaPuntoRaccolta> result = (List<TipologiaPuntoRaccolta>) storage.findData(TipologiaPuntoRaccolta.class, null, ownerId, draft);
		return result;
	}

	@RequestMapping(value="/tipologia/puntoraccolta/{ownerId}", method=RequestMethod.POST)
	public @ResponseBody TipologiaPuntoRaccolta addTipologiaPuntoRaccolta(@RequestBody TipologiaPuntoRaccolta data, 
			@PathVariable String ownerId,	HttpServletRequest request, HttpServletResponse response)	{
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		data.setObjectId(UUID.randomUUID().toString());
		data.setOwnerId(ownerId);
		storage.addTipologiaPuntoRaccolta(data, draft);
		return data;
	}
	
	@RequestMapping(value="/tipologia/puntoraccolta/{ownerId}/{objectId}", method=RequestMethod.PUT)
	public @ResponseBody void updateTipologiaPuntoRaccolta(TipologiaPuntoRaccolta data, @PathVariable String ownerId, 
			@PathVariable String objectId, HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException	{
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		data.setObjectId(objectId);
		data.setOwnerId(ownerId);
		storage.updateTipologiaPuntoRaccolta(data, draft);
	}
	
	@RequestMapping(value="/tipologia/puntoraccolta/{ownerId}/{objectId}", method=RequestMethod.DELETE)
	public @ResponseBody void deleteTipologiaPuntoRaccolta(@PathVariable String ownerId, @PathVariable String objectId, 
			HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException	{
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		storage.removeTipologiaPuntoRaccolta(ownerId, objectId, draft);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/tipologia/profilo/{ownerId}", method=RequestMethod.GET)
	public @ResponseBody List<TipologiaProfilo> getTipologiaProfilo(@PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response) 
			throws ClassNotFoundException {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		List<TipologiaProfilo> result = (List<TipologiaProfilo>) storage.findData(TipologiaProfilo.class, null, ownerId, draft);
		return result;
	}

	@RequestMapping(value="/tipologia/profilo/{ownerId}", method=RequestMethod.POST)
	public @ResponseBody TipologiaProfilo addTipologiaProfilo(@RequestBody TipologiaProfilo data, 
			@PathVariable String ownerId,	HttpServletRequest request, HttpServletResponse response) {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		data.setObjectId(UUID.randomUUID().toString());
		data.setOwnerId(ownerId);
		storage.addTipologiaProfilo(data, draft);
		return data;
	}
	
	@RequestMapping(value="/tipologia/profilo/{ownerId}/{objectId}", method=RequestMethod.PUT)
	public @ResponseBody void updateTipologiaProfilo(@RequestBody TipologiaProfilo data, @PathVariable String ownerId,	
			@PathVariable String objectId, HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		data.setObjectId(objectId);
		data.setOwnerId(ownerId);
		storage.updateTipologiaProfilo(data, draft);
	}
	
	@RequestMapping(value="/tipologia/profilo/{ownerId}/{objectId}", method=RequestMethod.DELETE)
	public @ResponseBody void deleteTipologiaProfilo(@PathVariable String ownerId, @PathVariable String objectId, 
			HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException	{
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		storage.removeTipologiaProfilo(ownerId, objectId, draft);
	}

	@RequestMapping(value="/tipologia/rifiuto/{ownerId}", method=RequestMethod.GET)
	public @ResponseBody List<Tipologia> getTipologiaRifiuto(@PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response) 
			throws ClassNotFoundException {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		List<Tipologia> result= Lists.newArrayList();
		Categorie categorie = storage.findOneData(Categorie.class, null, ownerId, draft);
		if(categorie != null) {
			result.addAll(categorie.getTipologiaRifiuto());
		}
		return result;
	}
	
	@RequestMapping(value="/tipologia/rifiuto/{ownerId}", method=RequestMethod.POST)
	public @ResponseBody void updateTipologiaRifiuto(@RequestBody List<Tipologia> tipologia,	@PathVariable String ownerId, 
		HttpServletRequest request, HttpServletResponse response)	throws ClassNotFoundException, EntityNotFoundException {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		Set<Tipologia> data = new HashSet<Tipologia>(tipologia);
		storage.updateTipologie(ownerId, data, "tipologiaRifiuto", draft);
	}

	@RequestMapping(value="/tipologia/raccolta/{ownerId}", method=RequestMethod.GET)
	public @ResponseBody List<Tipologia> getTipologiaRaccolta(@PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response) 
			throws ClassNotFoundException {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		List<Tipologia> result= Lists.newArrayList();
		Categorie categorie = storage.findOneData(Categorie.class, null, ownerId, draft);
		if(categorie != null) {
			result.addAll(categorie.getTipologiaRaccolta());
		}
		return result;
	}
	
	@RequestMapping(value="/tipologia/raccolta/{ownerId}", method=RequestMethod.POST)
	public @ResponseBody void updateTipologiaRaccolta(@RequestBody List<Tipologia> tipologia,	@PathVariable String ownerId, 
		HttpServletRequest request, HttpServletResponse response)	throws ClassNotFoundException, EntityNotFoundException {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		Set<Tipologia> data = new HashSet<Tipologia>(tipologia);
		storage.updateTipologie(ownerId, data, "tipologiaRaccolta", draft);
	}

}
