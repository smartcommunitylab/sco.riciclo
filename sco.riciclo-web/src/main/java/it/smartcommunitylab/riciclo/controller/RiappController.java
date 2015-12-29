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
import it.smartcommunitylab.riciclo.riapp.importer.RiappManager;
import it.smartcommunitylab.riciclo.storage.AppSetup;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@Controller
public class RiappController {

	@Autowired
	private RepositoryManager storage;
	
	@Autowired
	private RiappManager riappManager;

	@Autowired
	private AppSetup appSetup;

	@RequestMapping(value = "/riapp/{ownerId}/{datasetId}/import", method = RequestMethod.GET)
	public @ResponseBody String importDataSet(@PathVariable String ownerId, 
			@PathVariable String datasetId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, appSetup, false, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		riappManager.importData(datasetId);
		return "OK";
	}

	@RequestMapping(value = "/riapp/{ownerId}/{datasetId}/stradario", method = RequestMethod.GET)
	public @ResponseBody String writeStradario(@PathVariable String ownerId, 
			@PathVariable String datasetId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(!Utils.validateAPIRequest(request, appSetup, false, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		riappManager.writeStradario(datasetId);
		return "OK";
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleError(HttpServletRequest request, Exception exception) {
		return Utils.handleError(exception);
	}
}
