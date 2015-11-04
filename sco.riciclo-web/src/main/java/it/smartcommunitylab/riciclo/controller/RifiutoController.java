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
import it.smartcommunitylab.riciclo.model.Rifiuto;
import it.smartcommunitylab.riciclo.storage.AppSetup;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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


@Controller
public class RifiutoController {

	@Autowired
	private RepositoryManager storage;

	@Autowired
	private AppSetup appSetup;

	@SuppressWarnings("unchecked")
	@RequestMapping(value="api/rifiuto/{ownerId}", method=RequestMethod.GET)
	public @ResponseBody List<Rifiuto> getRifiuti(@PathVariable String ownerId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Rifiuto> result = (List<Rifiuto>) storage.findData(Rifiuto.class, null, ownerId, draft);
		return result;
	}

	@RequestMapping(value="api/rifiuto/{ownerId}", method=RequestMethod.POST)
	public @ResponseBody Rifiuto addRifiuto(@RequestBody Rifiuto rifiuto, @PathVariable String ownerId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		rifiuto.setObjectId(UUID.randomUUID().toString());
		rifiuto.setOwnerId(ownerId);
		storage.addRifiuto(rifiuto, draft);
		return rifiuto;
	}

	@RequestMapping(value="api/rifiuto/{ownerId}/{objectId}", method=RequestMethod.PUT)
	public @ResponseBody void updateRifiuto(@RequestBody Rifiuto rifiuto, @PathVariable String ownerId,
			@PathVariable String objectId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		rifiuto.setObjectId(objectId);
		rifiuto.setOwnerId(ownerId);
		storage.updateRifiuto(rifiuto, draft);
	}

	@RequestMapping(value="api/rifiuto/{ownerId}/{objectId}", method=RequestMethod.DELETE)
	public @ResponseBody void deleteRifiuto(@PathVariable String ownerId, @PathVariable String objectId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removeRifiuto(ownerId, objectId, draft);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleError(HttpServletRequest request, Exception exception) {
		return Utils.handleError(exception);
	}
}
