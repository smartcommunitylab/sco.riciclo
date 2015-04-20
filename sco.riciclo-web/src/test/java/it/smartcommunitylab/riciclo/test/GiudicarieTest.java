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

import it.smartcommunitylab.riciclo.app.importer.FileList;
import it.smartcommunitylab.riciclo.app.importer.ImportConstants;
import it.smartcommunitylab.riciclo.app.importer.ImportError;
import it.smartcommunitylab.riciclo.app.importer.ImportManager;
import it.smartcommunitylab.riciclo.config.RifiutiConfig;
import it.smartcommunitylab.riciclo.model.Rifiuti;
import it.smartcommunitylab.riciclo.security.AppDetails;
import it.smartcommunitylab.riciclo.storage.App;
import it.smartcommunitylab.riciclo.storage.AppInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

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
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RifiutiConfig.class }, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration
public class GiudicarieTest {

	private static final String EXCEL_MODELLO_CONCETTUALE_XLS = "giudicarie/ExcelModelloConcettuale_NEW.xls";
	private static final String CRM_KML = "giudicarie/crm.kml";
	private static final String ISOLE_ESTESE_KML = "giudicarie/isole_estese.kml";

	private final static String APP_ID = "GIUDICARIE";
	@Autowired
	private WebApplicationContext wac;

	private MockMvc mocker;

	@Before
	public void setup() {
	}

	@Test
	public void testUpload() throws Exception {
		AppInfo credentials = new AppInfo();
		credentials.setAppId(APP_ID);
		credentials.setPassword(APP_ID);
		credentials.setModelElements(Arrays.asList(ImportConstants.MODEL,
				ImportConstants.CRM, ImportConstants.ISOLE));
		AppDetails details = new AppDetails(credentials);
		details.setApp(credentials);
		TestingAuthenticationToken auth = new TestingAuthenticationToken(
				details, null);
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		mocker = MockMvcBuilders.webAppContextSetup(wac).build();
		ObjectMapper mapper = new ObjectMapper();
		
		ResultActions result;
		MvcResult res;
		
		result = mocker.perform(MockMvcRequestBuilders.get("/appDescriptor/" + APP_ID).accept(MediaType.APPLICATION_JSON));
		res = result.andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
		App appDescriptor = mapper.readValue(res.getResponse().getContentAsString(), App.class);		
		
		System.out.println(appDescriptor);
		
		Long version = appDescriptor.getPublishState().getVersion();		
		
		
		ImportManager manager = wac.getBean(ImportManager.class);
		FileList fileList = readFiles();
		ImportError error = null;
		try {
			manager.uploadFiles(fileList, credentials);
		} catch (ImportError e) {
			error = e;
		}
		Assert.assertNull(error);

		result = mocker.perform(MockMvcRequestBuilders.get(
				"/rifiuti/" + APP_ID + "/draft").accept(
				MediaType.APPLICATION_JSON));
		res = result
				.andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
		Rifiuti rifiuti = mapper.readValue(res.getResponse()
				.getContentAsString(), Rifiuti.class);
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
		Assert.assertFalse(rifiuti.getColore().isEmpty());
		Assert.assertFalse(rifiuti.getSegnalazione().isEmpty());

		// result = mocker.perform(MockMvcRequestBuilders.put("/publish/" +
		// APP_ID).accept(MediaType.APPLICATION_JSON));
		result = mocker.perform(MockMvcRequestBuilders.put("/console/publish/")
				.accept(MediaType.APPLICATION_JSON));
		res = result.andExpect(MockMvcResultMatchers.status().is(200))
				.andReturn();
		result = mocker.perform(MockMvcRequestBuilders
				.get("/rifiuti/" + APP_ID).accept(MediaType.APPLICATION_JSON));
		res = result.andExpect(MockMvcResultMatchers.status().is(200))
				.andReturn();
		rifiuti = mapper.readValue(res.getResponse().getContentAsString(),
				Rifiuti.class);
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
		
		// result = mocker.perform(MockMvcRequestBuilders.post("/publish/" +
		// APP_ID).accept(MediaType.APPLICATION_JSON));
		result = mocker.perform(MockMvcRequestBuilders.put("/console/publish/")
				.accept(MediaType.APPLICATION_JSON));
		res = result.andExpect(MockMvcResultMatchers.status().is(200))
				.andReturn();
		result = mocker.perform(MockMvcRequestBuilders.get(
				"/appDescriptor/" + APP_ID).accept(MediaType.APPLICATION_JSON));
		res = result.andExpect(MockMvcResultMatchers.status().is(200))
				.andReturn();
		appDescriptor = mapper.readValue(
				res.getResponse().getContentAsString(), App.class);
		System.out.println(appDescriptor);
		
		
//		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
//		Validator validator = validatorFactory.getValidator();
//		GenericApplicationContext appCtx = new GenericApplicationContext();
//		AnnotationBeanValidationConfigurationLoader l = new AnnotationBeanValidationConfigurationLoader();
//		l.setCheckValidatableAnnotation(true);
//		l.setApplicationContext(appCtx);
//		l.loadConfiguration(Colore.class);
//
//		BeanValidator bv = new BeanValidator();
//		bv.setConfigurationLoader(l);
//
//		for (Colore colore: rifiuti.getColore()) {
//			BindException errors = new BindException(colore, "target");
//			bv.validate(colore, errors);	
//		}
		
		Assert.assertEquals((version + 1), (long) appDescriptor.getPublishState().getVersion());

	}

	private FileList readFiles() throws IOException {
		FileList fileList = new FileList();
		InputStream xlsIs = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(EXCEL_MODELLO_CONCETTUALE_XLS);
		InputStream isoleIs = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(ISOLE_ESTESE_KML);
		InputStream crmIs = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(CRM_KML);
		MockMultipartFile xlsFile = new MockMultipartFile(
				EXCEL_MODELLO_CONCETTUALE_XLS, xlsIs);
		MockMultipartFile isoleFile = new MockMultipartFile(ISOLE_ESTESE_KML,
				isoleIs);
		MockMultipartFile crmFile = new MockMultipartFile(CRM_KML, crmIs);
		fileList.setModel(xlsFile);
		fileList.setCrm(crmFile);
		fileList.setIsole(isoleFile);

		return fileList;
	}

	// @ModelAttribute("fileList") FileList fileList
}