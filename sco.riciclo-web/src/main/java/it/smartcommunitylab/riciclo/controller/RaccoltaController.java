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
import it.smartcommunitylab.riciclo.model.Raccolta;
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
public class RaccoltaController {
	private static final transient Logger logger = LoggerFactory.getLogger(RaccoltaController.class);

	@Autowired
	private RepositoryManager storage;

	@Autowired
	private AppSetup appSetup;

	@RequestMapping(value="api/raccolta/{ownerId}", method=RequestMethod.GET)
	public @ResponseBody List<Raccolta> getRaccolte(@PathVariable String ownerId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
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
				return new ArrayList<Raccolta>();
			}
			comuni = appInfo.getComuni();
		}
		//ricerca tutt le Raccolte che insistono su un'area appartenete al sotto-albero di ogni comune individuato
		//map <objectId, Raccolta>
		Map<String, Raccolta> resultMapRaccolta = new HashMap<String, Raccolta>();
		Map<String, Area> resultMapArea  = new HashMap<String, Area>();
		for(String comune : comuni) {
			Utils.findAree(comune, ownerId, draft, resultMapArea, storage);
		}
		Utils.findRaccolte(resultMapArea, ownerId, draft, resultMapRaccolta, storage);
		List<Raccolta> result = Lists.newArrayList(resultMapRaccolta.values());
		return result;
	}

	@RequestMapping(value="api/raccolta/{ownerId}", method=RequestMethod.POST)
	public @ResponseBody Raccolta addRaccolta(@RequestBody Raccolta raccolta,	@PathVariable String ownerId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		raccolta.setObjectId(UUID.randomUUID().toString());
		raccolta.setOwnerId(ownerId);
		storage.addRaccolta(raccolta, draft);
		return raccolta;
	}

	@RequestMapping(value="api/raccolta/{ownerId}/{objectId}", method=RequestMethod.PUT)
	public @ResponseBody Raccolta updateRaccolta(@RequestBody Raccolta raccolta,	@PathVariable String ownerId,
			@PathVariable String objectId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		raccolta.setObjectId(objectId);
		raccolta.setOwnerId(ownerId);
		storage.updateRaccolta(raccolta, draft);
		return raccolta;
	}

	@RequestMapping(value="api/raccolta/{ownerId}/{objectId}", method=RequestMethod.DELETE)
	public @ResponseBody void deleteRaccoltaById(@PathVariable String ownerId, @PathVariable String objectId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removeRaccolta(ownerId, objectId, draft);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleError(HttpServletRequest request, Exception exception) {
		return Utils.handleError(exception);
	}
}
