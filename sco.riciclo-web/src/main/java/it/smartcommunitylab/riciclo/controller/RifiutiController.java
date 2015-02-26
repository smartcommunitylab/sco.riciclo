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


import it.smartcommunitylab.riciclo.model.Rifiuti;
import it.smartcommunitylab.riciclo.storage.AppDescriptor;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

	@RestController
	public class RifiutiController {
		
		@Autowired
		private RepositoryManager storage;
		
		@Autowired
		private ServletContext context;		
		
		@RequestMapping(method = RequestMethod.GET, value = "/ping")
		public @ResponseBody
		void ping(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
			System.out.println("PING");
		}		
		
		@RequestMapping(method = RequestMethod.POST, value = "/publish/{appId}")
		public @ResponseBody
		void publish(HttpServletResponse response, @PathVariable String appId) {
			storage.publish(appId);
		}	
		
		@RequestMapping(method = RequestMethod.GET, value = "/appDescriptor/{appId}")
		public AppDescriptor appDescriptor(HttpServletResponse response, @PathVariable String appId) {
			return storage.getAppDescriptor(appId);
		}		
		
		@RequestMapping(method = RequestMethod.GET, value = "/rifiuti/{appId}")
		public Rifiuti get(HttpServletResponse response, @PathVariable String appId) {
			return storage.findRifiuti(appId, false);
		}		
		
		@RequestMapping(method = RequestMethod.GET, value = "/rifiuti/{appId}/draft")
		public Rifiuti getDraft(HttpServletResponse response, @PathVariable String appId) {
			return storage.findRifiuti(appId, true);
		}			
		
		@RequestMapping(method = RequestMethod.GET, value = "/rifiuti/{className}/{appId}")
		public List get(HttpServletResponse response, @PathVariable String className, @PathVariable String appId) throws Exception {
			return storage.findRifiuti(className, appId, false);
		}			
		
		@RequestMapping(method = RequestMethod.GET, value = "/rifiuti/{className}/{appId}/draft")
		public List getDraft(HttpServletResponse response, @PathVariable String className, @PathVariable String appId) throws Exception {
			return storage.findRifiuti(className, appId, true);
		}			
		
}
