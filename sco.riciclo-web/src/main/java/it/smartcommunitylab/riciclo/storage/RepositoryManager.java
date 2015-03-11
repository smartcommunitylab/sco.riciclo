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

package it.smartcommunitylab.riciclo.storage;

import it.smartcommunitylab.riciclo.model.Area;
import it.smartcommunitylab.riciclo.model.Categorie;
import it.smartcommunitylab.riciclo.model.Gestore;
import it.smartcommunitylab.riciclo.model.Istituzione;
import it.smartcommunitylab.riciclo.model.Profilo;
import it.smartcommunitylab.riciclo.model.PuntoRaccolta;
import it.smartcommunitylab.riciclo.model.Raccolta;
import it.smartcommunitylab.riciclo.model.Riciclabolario;
import it.smartcommunitylab.riciclo.model.Rifiuti;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class RepositoryManager {

	private Class<?>[] classes = {Categorie.class, Area.class, Gestore.class, Istituzione.class, PuntoRaccolta.class, Raccolta.class, Riciclabolario.class, Profilo.class};

	@Autowired
	private AppSetup appSetup;
	
	private MongoTemplate draftTemplate;
	private MongoTemplate finalTemplate;

	public RepositoryManager(MongoTemplate draftTemplate, MongoTemplate finalTemplate) {
		this.draftTemplate = draftTemplate;
		this.finalTemplate = finalTemplate;
	}
	
	public void save(Rifiuti rifiuti, String appId) {
		AppState oldDraft = getAppState(appId, true);
		cleanByAppId(appId, true);
		
		rifiuti.getCategorie().setAppId(appId);
		draftTemplate.save(rifiuti.getCategorie());

		for (Area area: rifiuti.getAree()) {
			area.setAppId(appId);
			draftTemplate.save(area);
		}
		for (Profilo profilo: rifiuti.getProfili()) {
			profilo.setAppId(appId);
			draftTemplate.save(profilo);
		}		
		for (Gestore gestore: rifiuti.getGestori()) {
			gestore.setAppId(appId);
			draftTemplate.save(gestore);
		}
		for (Istituzione istituzione: rifiuti.getIstituzioni()) {
			istituzione.setAppId(appId);
			draftTemplate.save(istituzione);
		}		
		for (PuntoRaccolta puntoRaccolta: rifiuti.getPuntiRaccolta()) {
			puntoRaccolta.setAppId(appId);
			draftTemplate.save(puntoRaccolta);
		}		
		for (Raccolta raccolta: rifiuti.getRaccolta()) {
			raccolta.setAppId(appId);
			draftTemplate.save(raccolta);
		}	
		for (Riciclabolario riciclabolario: rifiuti.getRiciclabolario()) {
			riciclabolario.setAppId(appId);
			draftTemplate.save(riciclabolario);
		}			
		saveAppVersion(appId, oldDraft.getVersion()+1, true);
	}
	
	public void cleanByAppId(String appId, boolean draft) {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = appQuery(appId);
		
		for (Class<?> clazz: classes) {
			template.remove(query, clazz);
		}
	}
	
	public void publish(String appId) {
		for (Class<?> clazz: classes) {
			List<?> objects = draftTemplate.findAll(clazz);
			finalTemplate.remove(appQuery(appId), clazz);
			for (Object obj: objects) {
				finalTemplate.save(obj);
			}
		}

		AppState draft = getAppState(appId, true);
		saveAppVersion(appId, draft.getVersion(), false);
	}
	
	public void createApp(String appId) {
		saveApp(appId, true);
		saveApp(appId, false);
	}

	public void saveApp(String appId, boolean draft) {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = appQuery(appId);
		AppState app = template.findOne(query, AppState.class);
		if (app == null) {
			AppState appDescr = new AppState();
			appDescr.setAppId(appId);
			appDescr.setVersion(0L);
			appDescr.setTimestamp(System.currentTimeMillis());
			template.save(appDescr);
		}
		
	}

	private void saveAppVersion(String appId, long version, boolean draft) {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = appQuery(appId);
		Update update = new Update();
		update.set("version", version);		
		update.set("timestamp", System.currentTimeMillis());
		template.upsert(query, update, AppState.class);		
	}
	

	public List<?> findRifiuti(String className, String appId, boolean draft) throws ClassNotFoundException {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		
		Query query = appQuery(appId);
		List<?> result = template.find(query, Class.forName(className));
		
		return result;
	}	
	
	public Rifiuti findRifiuti(String appId, boolean draft) {
		MongoTemplate template;
		if (draft) {
			template = draftTemplate;
		} else {
			template = finalTemplate;
		}
		
		
		Rifiuti rifiuti = new Rifiuti();
		Query query = appQuery(appId);
		rifiuti.setAree(template.find(query, Area.class));
		rifiuti.setProfili(template.find(query, Profilo.class));
		rifiuti.setCategorie(template.findOne(query, Categorie.class));
		rifiuti.setGestori(template.find(query, Gestore.class));
		rifiuti.setIstituzioni(template.find(query, Istituzione.class));
		rifiuti.setPuntiRaccolta(template.find(query, PuntoRaccolta.class));
		rifiuti.setRaccolta(template.find(query, Raccolta.class));
		rifiuti.setRiciclabolario(template.find(query, Riciclabolario.class));
		rifiuti.setAppId(appId);
		
		return rifiuti;
	}
	
	private AppState getAppState(String appId, boolean draft) {
		Query query = appQuery(appId);
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		return template.findOne(query, AppState.class);
	}
	
	public App getAppDescriptor(String appId) {
		AppState draft = getAppState(appId, true);
		AppState published = getAppState(appId, false);
		App app = new App();
		app.setAppInfo(appSetup.findAppById(appId));
		app.setDraftState(draft);
		app.setPublishState(published);
		return app;
	}

	private Query appQuery(String appId) {
		return new Query(new Criteria("appId").is(appId));
	}

}
