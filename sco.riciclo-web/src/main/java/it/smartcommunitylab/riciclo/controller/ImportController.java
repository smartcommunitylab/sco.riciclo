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

import it.smartcommunitylab.riciclo.app.importer.converter.DataImporter;
import it.smartcommunitylab.riciclo.app.importer.converter.RifiutiConverter;
import it.smartcommunitylab.riciclo.app.importer.converter.RifiutiValidator;
import it.smartcommunitylab.riciclo.model.Rifiuti;
import it.smartcommunitylab.riciclo.security.AppDetails;
import it.smartcommunitylab.riciclo.security.AppSetup;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ImportController {

	@Autowired
	private DataImporter importer;

	@Autowired
	private RifiutiConverter converter;
	
	@Autowired
	private RifiutiValidator validator;	

	@Autowired
	private RepositoryManager storage;
	
	@Autowired
	private ServletContext context;
	
	@Autowired
	private AppSetup appSetup;	


	@RequestMapping(value = "/savefiles", method = RequestMethod.POST)
	public String upload(@ModelAttribute("fileList") FileList fileList, Model map) throws Exception {
		return uploadFiles(fileList, map);
	}
	
	private String uploadFiles(FileList fileList, Model map) throws Exception{
		try {
			String appId = getAppId();
			
			
			InputStream xlsIs = null;
			InputStream isoleIs = null;
			InputStream crmIs = null;

			xlsIs = fileList.getFiles().get(0).getInputStream();
			if (fileList.getFiles().size() == 3 && !fileList.getFiles().get(1).isEmpty()) {
				isoleIs = fileList.getFiles().get(1).getInputStream();
			}
			if (fileList.getFiles().size() == 3 && !fileList.getFiles().get(2).isEmpty()) {
				crmIs = fileList.getFiles().get(2).getInputStream();
			}

			it.smartcommunitylab.riciclo.app.importer.model.Rifiuti rifiuti = importer.importRifiuti(xlsIs, isoleIs, crmIs);

			Rifiuti convertedRifiuti = converter.convert(rifiuti, appId);
			List<String> validationResult = validator.validate(convertedRifiuti);

			if (validationResult.isEmpty()) {
				storage.save(convertedRifiuti, appId);
			} else {
				map.addAttribute("validationErrors", validationResult);
				return "error";
			}

			return "success";
		} catch (Exception e) {
			map.addAttribute("exception", e.getMessage());
			map.addAttribute("trace", e.getStackTrace());
			e.printStackTrace();
			return "error";
		}
	}

	private String getAppId() {
		AppDetails details = (AppDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String app = details.getUsername();
		return app;
	}		
	
}
