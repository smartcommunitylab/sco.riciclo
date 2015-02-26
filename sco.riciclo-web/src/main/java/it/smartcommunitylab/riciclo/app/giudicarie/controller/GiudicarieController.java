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

package it.smartcommunitylab.riciclo.app.giudicarie.controller;

import it.smartcommunitylab.riciclo.app.giudicarie.converter.GiudicarieImporter;
import it.smartcommunitylab.riciclo.app.giudicarie.converter.GiudicarieRifiutiConverter;
import it.smartcommunitylab.riciclo.controller.FileList;
import it.smartcommunitylab.riciclo.model.Rifiuti;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/giudicarie")
public class GiudicarieController {

	@Autowired
	private GiudicarieImporter importer;

	@Autowired
	private GiudicarieRifiutiConverter converter;

	@Autowired
	private RepositoryManager storage;
	
	@Autowired
	private ServletContext context;

	@RequestMapping(value = "/")
	public String giudicarie() {
		return "giudicarie/upload";
	}

	@RequestMapping(value = "/savefiles", method = RequestMethod.POST)
	public String upload(@ModelAttribute("fileList") FileList fileList, Model map) throws Exception {
		return uploadFiles(fileList, map);
	}
	
	private String uploadFiles(FileList fileList, Model map) throws Exception{
		try {

		InputStream xlsIs = fileList.getFiles().get(0).getInputStream();
		InputStream isoleIs = fileList.getFiles().get(1).getInputStream();
		InputStream crmIs = fileList.getFiles().get(2).getInputStream();		
		
		it.smartcommunitylab.riciclo.app.giudicarie.model.Rifiuti rifiuti = importer.importRifiuti(xlsIs, isoleIs, crmIs);
		
		Rifiuti convertedRifiuti = converter.convert(rifiuti);
		List<String> validationResult = converter.validate(convertedRifiuti);

		if (validationResult.isEmpty()) {
			storage.save(convertedRifiuti, converter.getAppId());
		} else {
			map.addAttribute("validationErrors", validationResult);
			return "error";
		}
		
		return "success";
		} catch (Exception e) {
			map.addAttribute("exception", e.getMessage());
			map.addAttribute("trace", e.getStackTrace());
			return "error";
		}
	}

}
