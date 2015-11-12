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
import it.smartcommunitylab.riciclo.model.Area;
import it.smartcommunitylab.riciclo.model.PuntoRaccolta;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.Lists;


@Controller
public class PuntoRaccoltaController {
	private static final transient Logger logger = LoggerFactory.getLogger(PuntoRaccoltaController.class);

	@Autowired
	private RepositoryManager storage;

	@Autowired
	private AppSetup appSetup;

	@SuppressWarnings("unchecked")
	@RequestMapping(value="api/puntoraccolta/{ownerId}", method=RequestMethod.GET)
	public @ResponseBody List<PuntoRaccolta> getPuntiRaccolta(@PathVariable String ownerId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<PuntoRaccolta> result = (List<PuntoRaccolta>) storage.findData(PuntoRaccolta.class, null, ownerId, draft);
		return result;
		/*
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
				return new ArrayList<PuntoRaccolta>();
			}
			comuni = appInfo.getComuni();
		}
		//ricerca tutti i PuntoRaccolta che insistono su un'area appartenete al sotto-albero di ogni comune individuato
		//map <objectId, PuntoRaccolta>
		Map<String, PuntoRaccolta> resultMapRaccolta = new HashMap<String, PuntoRaccolta>();
		Map<String, Area> resultMapArea  = new HashMap<String, Area>();
		for(String comune : comuni) {
			Utils.findAree(comune, ownerId, draft, resultMapArea, storage);
		}
		Utils.findPuntiRaccolta(resultMapArea, ownerId, draft, resultMapRaccolta, storage);
		List<PuntoRaccolta> result = Lists.newArrayList(resultMapRaccolta.values());
		return result;
		*/
	}

	@RequestMapping(value="api/puntoraccolta/{ownerId}", method=RequestMethod.POST)
	public @ResponseBody PuntoRaccolta addRaccolta(@RequestBody PuntoRaccolta raccolta,
			@PathVariable String ownerId,	HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		raccolta.setObjectId(UUID.randomUUID().toString());
		raccolta.setOwnerId(ownerId);
		storage.addPuntoRaccolta(raccolta, draft);
		return raccolta;
	}

	@RequestMapping(value="api/puntoraccolta/{ownerId}/{objectId}", method=RequestMethod.DELETE)
	public @ResponseBody void deletePuntoRaccoltaById(@PathVariable String ownerId,
			@PathVariable String objectId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removePuntoRaccolta(ownerId, objectId, draft);
	}

	@RequestMapping(value="api/puntoraccolta/{ownerId}", method=RequestMethod.DELETE)
	public @ResponseBody void deletePuntoRaccolta(@RequestBody PuntoRaccolta raccolta,
			@PathVariable String ownerId,	HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		raccolta.setOwnerId(ownerId);
		storage.removePuntoRaccolta(raccolta, draft);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleError(HttpServletRequest request, Exception exception) {
		return Utils.handleError(exception);
	}
}
