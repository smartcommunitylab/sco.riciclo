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
import it.smartcommunitylab.riciclo.model.CalendarioRaccolta;
import it.smartcommunitylab.riciclo.model.OrarioApertura;
import it.smartcommunitylab.riciclo.storage.AppSetup;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
public class CalendarioRaccoltaController {
	private static final transient Logger logger = LoggerFactory.getLogger(CalendarioRaccoltaController.class);

	@Autowired
	private RepositoryManager storage;

	@Autowired
	private AppSetup appSetup;

	@SuppressWarnings("unchecked")
	@RequestMapping(value="api/calraccolta/{ownerId}", method=RequestMethod.GET)
	public @ResponseBody List<CalendarioRaccolta> getPuntiRaccolta(@PathVariable String ownerId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<CalendarioRaccolta> result = (List<CalendarioRaccolta>) storage.findData(CalendarioRaccolta.class, null, ownerId, draft);
		if(logger.isDebugEnabled()) {
			logger.debug(String.format("result[%s]:%s", ownerId, result.size()));
		}
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
				return new ArrayList<CalendarioRaccolta>();
			}
			comuni = appInfo.getComuni();
		}
		//ricerca tutti i CalendarioRaccolta che insistono su un'area appartenete al sotto-albero di ogni comune individuato
		//map <objectId, CalendarioRaccolta>
		Map<String, CalendarioRaccolta> resultMapCalendario = new HashMap<String, CalendarioRaccolta>();
		Map<String, Area> resultMapArea  = new HashMap<String, Area>();
		for(String comune : comuni) {
			Utils.findAree(comune, ownerId, draft, resultMapArea, storage);
		}
		Utils.findCalendariRaccolta(resultMapArea, ownerId, draft, resultMapCalendario, storage);
		List<CalendarioRaccolta> result = Lists.newArrayList(resultMapCalendario.values());
		return result;
		*/
	}

	@RequestMapping(value="api/calraccolta/{ownerId}", method=RequestMethod.POST)
	public @ResponseBody CalendarioRaccolta addCalendario(@RequestBody CalendarioRaccolta calendario,
			@PathVariable String ownerId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		calendario.setObjectId(UUID.randomUUID().toString());
		calendario.setOwnerId(ownerId);
		storage.addCalendarioRaccolta(calendario, draft);
		return calendario;
	}

	@RequestMapping(value="api/calraccolta/{ownerId}/{objectId}", method=RequestMethod.PUT)
	public @ResponseBody void updateCalendario(@RequestBody CalendarioRaccolta calendario,
			@PathVariable String ownerId,	@PathVariable String objectId, HttpServletRequest request,
			HttpServletResponse response)	throws Exception {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		calendario.setObjectId(objectId);
		calendario.setOwnerId(ownerId);
		storage.updateCalendarioRaccolta(calendario, draft);
	}


	@RequestMapping(value="api/calraccolta/{ownerId}/{objectId}", method=RequestMethod.DELETE)
	public @ResponseBody void deleteCalendarioRaccoltaById(@PathVariable String ownerId,
			@PathVariable String objectId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removeCalendarioRaccolta(ownerId, objectId, draft);
	}

	@RequestMapping(value="api/calraccolta/{ownerId}", method=RequestMethod.DELETE)
	public @ResponseBody void deleteCalendarioRaccolta(@RequestBody CalendarioRaccolta calendario,
			@PathVariable String ownerId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		boolean draft = Utils.getDraft(request);
		calendario.setOwnerId(ownerId);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removeCalendarioRaccolta(calendario, draft);
	}

	@RequestMapping(value="api/calraccolta/{ownerId}/{objectId}/orario", method=RequestMethod.POST)
	public @ResponseBody void addOrarioApertura(@RequestBody OrarioApertura orario,
			@PathVariable String ownerId,	@PathVariable String objectId, HttpServletRequest request,
			HttpServletResponse response) throws ClassNotFoundException, Exception {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.updateCalendarioRaccoltaAddOrario(ownerId, objectId, orario, draft);
	}

	@RequestMapping(value="api/calraccolta/{ownerId}/{objectId}/orario/{position}", method=RequestMethod.DELETE)
	public @ResponseBody void deleteOrarioApertura(@PathVariable String ownerId,
			@PathVariable String objectId, @PathVariable int position, HttpServletRequest request,
			HttpServletResponse response) throws ClassNotFoundException, Exception {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.updateCalendarioRaccoltaRemoveOrario(ownerId, objectId, position, draft);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="api/calraccolta/{ownerId}/orario/copy", method=RequestMethod.GET)
	public @ResponseBody List<CalendarioRaccolta> copyOrarioApertura(@PathVariable String ownerId, 
			HttpServletRequest request,	HttpServletResponse response) throws Exception {
		boolean draft = Utils.getDraft(request);
		if(!Utils.validateAPIRequest(request, appSetup, draft, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		SimpleDateFormat sdfOrari = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		List<CalendarioRaccolta> calendarioList = (List<CalendarioRaccolta>) storage.findData(CalendarioRaccolta.class, null, ownerId, draft);
		for(CalendarioRaccolta calendario : calendarioList) {
			List<OrarioApertura> newOrarioList = Lists.newArrayList();
			for(OrarioApertura orario : calendario.getOrarioApertura()) {
				cal.setTime(sdfOrari.parse(orario.getDataDa()));
				cal.add(Calendar.YEAR, 1);
				Date dataDa = cal.getTime();
				cal.setTime(sdfOrari.parse(orario.getDataA()));
				cal.add(Calendar.YEAR, 1);
				Date dataA = cal.getTime();
				OrarioApertura newOrario = new OrarioApertura();
				newOrario.setDataDa(sdfOrari.format(dataDa));
				newOrario.setDataA(sdfOrari.format(dataA));
				newOrario.setIl(orario.getIl());
				newOrario.setDalle(orario.getDalle());
				newOrario.setAlle(orario.getAlle());
				newOrario.setEccezione(orario.getEccezione());
				newOrario.setNote(orario.getNote());
				newOrarioList.add(newOrario);
			}
			calendario.getOrarioApertura().addAll(newOrarioList);
			storage.updateCalendarioRaccoltaOrario(ownerId, calendario.getObjectId(), calendario.getOrarioApertura(), draft);
		}
		return calendarioList;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleError(HttpServletRequest request, Exception exception) {
		return Utils.handleError(exception);
	}
}
