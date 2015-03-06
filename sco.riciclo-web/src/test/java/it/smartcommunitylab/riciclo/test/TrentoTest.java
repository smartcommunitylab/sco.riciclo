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

package it.smartcommunitylab.riciclo.test;

import it.smartcommunitylab.riciclo.config.RifiutiConfig;
import it.smartcommunitylab.riciclo.controller.FileList;
import it.smartcommunitylab.riciclo.controller.ImportController;
import it.smartcommunitylab.riciclo.model.Rifiuti;
import it.smartcommunitylab.riciclo.security.AppCredentials;
import it.smartcommunitylab.riciclo.security.AppDetails;
import it.smartcommunitylab.riciclo.storage.AppDescriptor;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RifiutiConfig.class }, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration
public class TrentoTest {

	private static final String EXCEL_MODELLO_CONCETTUALE_XLS = "trento/ExcelModelloConcettuale_V0 03.xls";

	private final static String APP_ID = "TRENTO";
	
	@Autowired
	private WebApplicationContext wac;
	
	@Autowired
	private RepositoryManager storage;	

	private MockMvc mocker;
	
	@Before
	public void setup() {
    }	
	
	@Test
	public void testUpload() throws Exception {
		AppCredentials credentials = new AppCredentials();
		credentials.setId(APP_ID);
		credentials.setPassword(APP_ID);		
		AppDetails details = new AppDetails(credentials);
		details.setApp(credentials);
		TestingAuthenticationToken auth = new TestingAuthenticationToken(details, null);
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		ImportController controller = wac.getBean(ImportController.class);
		
		FileList fileList = readFiles();
		
		Model model = new ExtendedModelMap();
		controller.upload(fileList, model);
		
		Map<String, Object> modelMap = model.asMap();
		if (modelMap.containsKey("validationErrors")) {
			List<String> errors = (List<String>)modelMap.get("validationErrors");
			for (String error: errors) {
				System.err.println(error);
			}
		}
		
		Assert.assertTrue(model.asMap().keySet().isEmpty());
		
		mocker = MockMvcBuilders.webAppContextSetup(wac).build();
		ObjectMapper mapper = new ObjectMapper();

		ResultActions result = mocker.perform(MockMvcRequestBuilders.get("/rifiuti/" + APP_ID + "/draft").accept(MediaType.APPLICATION_JSON));
		MvcResult  res = result.andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
		Rifiuti rifiuti = mapper.readValue(res.getResponse().getContentAsString(), Rifiuti.class);
		Assert.assertEquals(APP_ID, rifiuti.getAppId());
		
		Assert.assertNotNull(rifiuti.getAree());
		Assert.assertNotNull(rifiuti.getGestori());
		Assert.assertNotNull(rifiuti.getIstituzioni());
		Assert.assertNotNull(rifiuti.getPuntiRaccolta());
		Assert.assertNotNull(rifiuti.getRaccolta());
		Assert.assertNotNull(rifiuti.getRiciclabolario());
		
		Assert.assertFalse(rifiuti.getAree().isEmpty());
		Assert.assertFalse(rifiuti.getGestori().isEmpty());
		Assert.assertFalse(rifiuti.getIstituzioni().isEmpty());
		Assert.assertFalse(rifiuti.getPuntiRaccolta().isEmpty());
		Assert.assertFalse(rifiuti.getRaccolta().isEmpty());
		Assert.assertFalse(rifiuti.getRiciclabolario().isEmpty());		

		result = mocker.perform(MockMvcRequestBuilders.post("/publish/" + APP_ID).accept(MediaType.APPLICATION_JSON));
		res = result.andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
		
		result = mocker.perform(MockMvcRequestBuilders.get("/rifiuti/" + APP_ID).accept(MediaType.APPLICATION_JSON));
		res = result.andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
		rifiuti = mapper.readValue(res.getResponse().getContentAsString(), Rifiuti.class);
		Assert.assertEquals(APP_ID, rifiuti.getAppId());
		
		Assert.assertNotNull(rifiuti.getAree());
		Assert.assertNotNull(rifiuti.getGestori());
		Assert.assertNotNull(rifiuti.getIstituzioni());
		Assert.assertNotNull(rifiuti.getPuntiRaccolta());
		Assert.assertNotNull(rifiuti.getRaccolta());
		Assert.assertNotNull(rifiuti.getRiciclabolario());
		
		Assert.assertFalse(rifiuti.getAree().isEmpty());
		Assert.assertFalse(rifiuti.getGestori().isEmpty());
		Assert.assertFalse(rifiuti.getIstituzioni().isEmpty());
		Assert.assertFalse(rifiuti.getPuntiRaccolta().isEmpty());
		Assert.assertFalse(rifiuti.getRaccolta().isEmpty());
		Assert.assertFalse(rifiuti.getRiciclabolario().isEmpty());		
		
		result = mocker.perform(MockMvcRequestBuilders.get("/appDescriptor/" + APP_ID).accept(MediaType.APPLICATION_JSON));
		res = result.andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
		AppDescriptor appDescriptor = mapper.readValue(res.getResponse().getContentAsString(), AppDescriptor.class);		
		
		System.out.println(appDescriptor);
		
		Long version = appDescriptor.getVersion();		
		
		result = mocker.perform(MockMvcRequestBuilders.post("/publish/" + APP_ID).accept(MediaType.APPLICATION_JSON));
		res = result.andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
		
		result = mocker.perform(MockMvcRequestBuilders.get("/appDescriptor/" + APP_ID).accept(MediaType.APPLICATION_JSON));
		res = result.andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
		appDescriptor = mapper.readValue(res.getResponse().getContentAsString(), AppDescriptor.class);
		
		System.out.println(appDescriptor);
		
		Assert.assertEquals((version + 1), (long)appDescriptor.getVersion());
		
	}
	
	private FileList readFiles() throws IOException {
		FileList fileList = new FileList();
		
		InputStream xlsIs = Thread.currentThread().getContextClassLoader().getResourceAsStream(EXCEL_MODELLO_CONCETTUALE_XLS);
//		InputStream isoleIs = Thread.currentThread().getContextClassLoader().getResourceAsStream(ISOLE_ESTESE_KML);
//		InputStream crmIs = Thread.currentThread().getContextClassLoader().getResourceAsStream(CRM_KML);		
		
		MockMultipartFile xlsFile = new MockMultipartFile(EXCEL_MODELLO_CONCETTUALE_XLS, xlsIs);
//		MockMultipartFile isoleFile = new MockMultipartFile(ISOLE_ESTESE_KML, isoleIs);
//		MockMultipartFile crmFile = new MockMultipartFile(CRM_KML, crmIs);
		
		List<MultipartFile> files = Lists.newArrayList();
		files.add(xlsFile);
//		files.add(isoleFile);
//		files.add(crmFile);
		
		fileList.setFiles(files);
		
		return fileList;
	}
	

//	@ModelAttribute("fileList") FileList fileList
	
}
