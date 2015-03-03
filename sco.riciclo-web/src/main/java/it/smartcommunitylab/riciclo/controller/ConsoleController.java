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

import it.smartcommunitylab.riciclo.security.AppSetup;
import it.smartcommunitylab.riciclo.security.CustomAuthenticationProvider.AppDetails;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class ConsoleController {

	@Autowired
	private ServletContext context;		
	
	@Autowired
	private AppSetup appSetup;	
	
	@RequestMapping(value = "/")
	public String root() {
		String dir = getAppId().toLowerCase();
		return dir + "/upload";
	}		
	
	@RequestMapping(value = "/login")
	public String login() {
		return "loginForm";
	}		
	
	private String getAppId() {
		AppDetails details = (AppDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String app = details.getUsername();
		return app;
	}	
	
	
}
