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

import it.smartcommunitylab.riciclo.model.Notification;
import it.smartcommunitylab.riciclo.model.Rifiuti;
import it.smartcommunitylab.riciclo.storage.App;
import it.smartcommunitylab.riciclo.storage.NotificationManager;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@RestController
public class RifiutiController {

	@Autowired
	private RepositoryManager storage;
	@Autowired
	private NotificationManager notificationManager;
	
	@Autowired
	private ServletContext context;

	@RequestMapping(method = RequestMethod.GET, value = "/ping")
	public @ResponseBody
	String ping(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		return "PONG";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/appDescriptor/{appId}")
	public App appState(HttpServletResponse response, @PathVariable String appId) {
		return storage.getAppDescriptor(appId);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/comuni/{appId}")
	public List<String> appComuni(HttpServletResponse response, @PathVariable String appId) {
		return storage.getComuniList(appId, false);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/comuni/{appId}/draft")
	public List<String> appComuniDraft(HttpServletResponse response, @PathVariable String appId) {
		return storage.getComuniList(appId, true);
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
	public List<?> get(HttpServletResponse response, @PathVariable String className, @PathVariable String appId) throws Exception {
		return storage.findRifiuti(className, appId, false);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/rifiuti/{className}/{appId}/draft")
	public List<?> getDraft(HttpServletResponse response, @PathVariable String className, @PathVariable String appId) throws Exception {
		return storage.findRifiuti(className, appId, true);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/notifications/{appId}")
	public @ResponseBody List<Notification> getNotificatons(@PathVariable String appId) throws Exception {
		return notificationManager.getNotifications(appId);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/notifications/{appId}")
	public @ResponseBody Notification saveNotificaton(@RequestBody Notification notification, @PathVariable String appId) throws Exception {
		return notificationManager.saveNotification(notification, appId);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/zip/{appId}")
	public void zip(HttpServletResponse response, @PathVariable String appId) throws Exception {
		response.setContentType("application/zip");
		response.addHeader("Content-Disposition", "attachment; filename=\"final-" + appId + ".zip\"");
		response.addHeader("Content-Transfer-Encoding", "binary");
		ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
		
		Rifiuti rifiuti = get(response, appId);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
		String r = mapper.writeValueAsString(rifiuti);
		r = r.replace("\"appId\":\"" + appId + "\",","");
		String r2 = new String(r.getBytes("UTF-8"));
		
		compress(outputBuffer, r2.getBytes(), "final-" + appId + ".json");
		response.getOutputStream().write(outputBuffer.toByteArray());
		response.getOutputStream().flush();
		outputBuffer.close();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/zip/{appId}/draft")
	public void zipDraft(HttpServletResponse response, @PathVariable String appId) throws Exception {
		response.setContentType("application/zip");
		response.addHeader("Content-Disposition", "attachment; filename=\"draft-" + appId + ".zip\"");
		response.addHeader("Content-Transfer-Encoding", "binary");
		ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
		
		Rifiuti rifiuti = getDraft(response, appId);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
		String r = mapper.writeValueAsString(rifiuti);
		r = r.replace("\"appId\":\"" + appId + "\",","");
		String r2 = new String(r.getBytes("UTF-8"));
		
		compress(outputBuffer, r2.getBytes(), "draft-" + appId + ".json");
		response.getOutputStream().write(outputBuffer.toByteArray());
		response.getOutputStream().flush();
		outputBuffer.close();
	}	

	void compress(final OutputStream out, byte[] b, String entryName) throws Exception {
		ZipOutputStream zos = new ZipOutputStream(out);
		zos.setLevel(ZipOutputStream.DEFLATED);

		ZipEntry ze = new ZipEntry(entryName);
		zos.putNextEntry(ze);
		zos.write(b, 0, b.length);

		zos.close();
	}

}
