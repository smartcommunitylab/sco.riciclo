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

import it.smartcommunitylab.riciclo.app.importer.FileList;
import it.smartcommunitylab.riciclo.app.importer.ImportConstants;
import it.smartcommunitylab.riciclo.app.importer.ImportError;
import it.smartcommunitylab.riciclo.app.importer.ImportManager;
import it.smartcommunitylab.riciclo.security.AppDetails;
import it.smartcommunitylab.riciclo.storage.App;
import it.smartcommunitylab.riciclo.storage.AppSetup;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
public class ConsoleController {

	@Autowired
	private ServletContext context;		
	
	@Autowired
	private RepositoryManager storage;
	
	@Autowired
	private AppSetup appSetup;	
	
	@Autowired
	private ImportManager manager;

	@RequestMapping(value = "/")
	public String root() {
		return "index";
	}		
	
	@RequestMapping(value = "/login")
	public String login() {
		return "login";
	}		
	
	@RequestMapping(value = "/console")
	public String console() {
		return "console";
	}		

	@RequestMapping(value = "/aree")
	public String aree() {
		return "aree";
	}
	
	@RequestMapping(value = "/rifiuti")
	public String rifiuti() {
		return "rifiuti";
	}
	
	@RequestMapping(value = "/crm")
	public String crm() {
		return "crm";
	}
	
	@RequestMapping(value = "/console/data")
	public @ResponseBody App data() {
		return storage.getAppDescriptor(getOwnerId());
	}		
	
	@RequestMapping(value = "/console/publish", method=RequestMethod.PUT)
	public @ResponseBody App publish() {
		String ownerId = getOwnerId();
		storage.publish(ownerId);
		App descr = storage.getAppDescriptor(ownerId);
		return descr;
	}	
	
	@RequestMapping(value = "/savefiles", method = RequestMethod.POST)
	public @ResponseBody String upload(MultipartHttpServletRequest req) throws Exception {
		MultiValueMap<String, MultipartFile> multiFileMap = req.getMultiFileMap();
		FileList fileList = new FileList();
		String res = "";
		for (String key : multiFileMap.keySet()) {
			if (ImportConstants.CRM.equals(key)) fileList.setCrm(multiFileMap.getFirst(key));
			if (ImportConstants.ISOLE.equals(key)) fileList.setIsole(multiFileMap.getFirst(key));
			if (ImportConstants.MODEL.equals(key)) fileList.setModel(multiFileMap.getFirst(key));
		}
		try {
			String ownerId = getOwnerId();
			manager.uploadFiles(fileList, storage.getAppDescriptor(ownerId).getAppInfo());
			res = new ObjectMapper().writeValueAsString(storage.getAppDescriptor(ownerId));
		} catch (ImportError e) {
			res = new ObjectMapper().writeValueAsString(e);
		}
		return res;
	}
	
	private String getOwnerId() {
		AppDetails details = (AppDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String app = details.getUsername();
		return app;
	}	
	
	
}
