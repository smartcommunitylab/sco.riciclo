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

import it.smartcommunitylab.riciclo.exception.EntityNotFoundException;
import it.smartcommunitylab.riciclo.model.Area;
import it.smartcommunitylab.riciclo.model.CalendarioRaccolta;
import it.smartcommunitylab.riciclo.model.Crm;
import it.smartcommunitylab.riciclo.model.Categorie;
import it.smartcommunitylab.riciclo.model.Colore;
import it.smartcommunitylab.riciclo.model.Gestore;
import it.smartcommunitylab.riciclo.model.Istituzione;
import it.smartcommunitylab.riciclo.model.PuntoRaccolta;
import it.smartcommunitylab.riciclo.model.Raccolta;
import it.smartcommunitylab.riciclo.model.Riciclabolario;
import it.smartcommunitylab.riciclo.model.AppDataRifiuti;
import it.smartcommunitylab.riciclo.model.Rifiuto;
import it.smartcommunitylab.riciclo.model.Segnalazione;
import it.smartcommunitylab.riciclo.model.TipologiaProfilo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class RepositoryManager {

	private Class<?>[] classes = {
			Categorie.class, 
			Area.class, 
			Gestore.class, 
			Istituzione.class, 
			PuntoRaccolta.class, 
			Raccolta.class, 
			Riciclabolario.class, 
			TipologiaProfilo.class,
			Colore.class,
			Segnalazione.class
			};

	@Autowired
	private AppSetup appSetup;
	
	private MongoTemplate draftTemplate;
	private MongoTemplate finalTemplate;

	public RepositoryManager(MongoTemplate draftTemplate, MongoTemplate finalTemplate) {
		this.draftTemplate = draftTemplate;
		this.finalTemplate = finalTemplate;
	}
	
	public void save(AppDataRifiuti appDataRifiuti, String appId) {
		AppState oldDraft = getAppState(appId, true);
		cleanByAppId(appId, true);
		
		appDataRifiuti.getCategorie().setAppId(appId);
		draftTemplate.save(appDataRifiuti.getCategorie());

		for (Area area: appDataRifiuti.getAree()) {
			area.setAppId(appId);
			draftTemplate.save(area);
		}
		for (TipologiaProfilo profilo: appDataRifiuti.getTipologiaProfilo()) {
			profilo.setAppId(appId);
			draftTemplate.save(profilo);
		}		
		for (Gestore gestore: appDataRifiuti.getGestori()) {
			gestore.setAppId(appId);
			draftTemplate.save(gestore);
		}
		for (Istituzione istituzione: appDataRifiuti.getIstituzioni()) {
			istituzione.setAppId(appId);
			draftTemplate.save(istituzione);
		}		
		for (PuntoRaccolta puntoRaccolta: appDataRifiuti.getPuntiRaccolta()) {
			puntoRaccolta.setAppId(appId);
			draftTemplate.save(puntoRaccolta);
		}		
		for (Raccolta raccolta: appDataRifiuti.getRaccolta()) {
			raccolta.setAppId(appId);
			draftTemplate.save(raccolta);
		}	
		for (Riciclabolario riciclabolario: appDataRifiuti.getRiciclabolario()) {
			riciclabolario.setAppId(appId);
			draftTemplate.save(riciclabolario);
		}
		for (Colore colore: appDataRifiuti.getColore()) {
			colore.setAppId(appId);
			draftTemplate.save(colore);
		}
		for (Segnalazione segnalazione: appDataRifiuti.getSegnalazione()) {
			segnalazione.setAppId(appId);
			draftTemplate.save(segnalazione);
		}				
		for(Rifiuto rifiuto : appDataRifiuti.getRifiuti()) {
			rifiuto.setAppId(appId);
			draftTemplate.save(rifiuto);
		}
		for(Crm crm : appDataRifiuti.getCrm()) {
			crm.setAppId(appId);
			draftTemplate.save(crm);
		}
		for(CalendarioRaccolta calendarioRaccolta : appDataRifiuti.getCalendariRaccolta()) {
			calendarioRaccolta.setAppId(appId);
			draftTemplate.save(calendarioRaccolta);
		}
		saveAppVersion(appId, oldDraft.getVersion() + 1, true);
	}
	
	public void cleanByAppId(String appId, boolean draft) {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = appQuery(appId);
		
		for (Class<?> clazz: classes) {
			template.remove(query, clazz);
		}
	}
	
	public void publish(String appId) {
		Query query = appQuery(appId);
		for (Class<?> clazz: classes) {
			List<?> objects = draftTemplate.find(query, clazz);
			finalTemplate.remove(query, clazz);
			for (Object obj: objects) {
				finalTemplate.save(obj);
			}
		}

		AppState draft = getAppState(appId, true);
		saveAppVersion(appId, draft.getVersion(), false);
	}
	
	public void createApp(AppInfo appInfo) {
		saveAppState(appInfo.getAppId(), true);
		saveAppState(appInfo.getAppId(), false);
		saveAppInfo(appInfo, true);
		saveAppInfo(appInfo, false);
	}
	
	private void saveAppInfo(AppInfo appInfo, boolean draft) {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = appQuery(appInfo.getAppId());
		AppInfo appInfoDB = template.findOne(query, AppInfo.class);
		if(appInfoDB == null) {
			template.save(appInfo);
		} else {
			appInfoDB.setPassword(appInfo.getPassword());
			appInfoDB.setModelElements(appInfo.getModelElements());
			appInfoDB.setComuni(appInfo.getComuni());
			template.save(appInfoDB);
		}
	}

	public void saveAppState(String appId, boolean draft) {
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
	
	public AppDataRifiuti findRifiuti(String appId, boolean draft) {
		MongoTemplate template;
		if (draft) {
			template = draftTemplate;
		} else {
			template = finalTemplate;
		}
		
		
		AppDataRifiuti appDataRifiuti = new AppDataRifiuti();
		Query query = appQuery(appId);
		appDataRifiuti.setAree(template.find(query, Area.class));
		appDataRifiuti.setTipologiaProfilo(template.find(query, TipologiaProfilo.class));
		appDataRifiuti.setCategorie(template.findOne(query, Categorie.class));
		appDataRifiuti.setGestori(template.find(query, Gestore.class));
		appDataRifiuti.setIstituzioni(template.find(query, Istituzione.class));
		appDataRifiuti.setPuntiRaccolta(template.find(query, PuntoRaccolta.class));
		appDataRifiuti.setRaccolta(template.find(query, Raccolta.class));
		appDataRifiuti.setRiciclabolario(template.find(query, Riciclabolario.class));
		appDataRifiuti.setColore(template.find(query, Colore.class));
		appDataRifiuti.setSegnalazione(template.find(query, Segnalazione.class));
		appDataRifiuti.setAppId(appId);
		
		return appDataRifiuti;
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
	
	public List<String> getComuniList(String appId, boolean draft) {
		AppInfo appInfo = appSetup.findAppById(appId);
		if(appInfo != null) {
			return appInfo.getComuni();
		} else {
			return new ArrayList<String>();
		}
	}

	public List<?> findData(Class<?> entityClass, Criteria criteria, String appId, boolean draft) throws ClassNotFoundException {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = new Query(new Criteria("appId").is(appId).andOperator(criteria));
		List<?> result = template.find(query, entityClass);
		return result;
	}
	
	public <T> T findOneData(Class<T> entityClass, Criteria criteria, String appId, boolean draft) throws ClassNotFoundException {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = null;
		if(criteria != null) {
			query = new Query(new Criteria("appId").is(appId).andOperator(criteria));
		} else {
			query = new Query(new Criteria("appId").is(appId));
		}
		T result = template.findOne(query, entityClass);
		return result;
	}
	
	private Query appQuery(String appId) {
		return new Query(new Criteria("appId").is(appId));
	}
	
	public void addCRM(Crm crm, boolean draft) {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		template.save(crm);
	}

	public void updateCRM(Crm crm, boolean draft) throws EntityNotFoundException {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = new Query(new Criteria("appId").is(crm.getAppId()).and("objectId").is(crm.getObjectId()));
		Crm crmDB = template.findOne(query, Crm.class);
		if(crmDB == null) {
			throw new EntityNotFoundException(String.format("CRM with id %s not found", crm.getObjectId()));
		}
		Update update = new Update();
		update.set("lastUpdate", new Date());
		//TODO
		template.updateFirst(query, update, Crm.class);
	}
	
	public void removeCRM(String appId, String objectId, boolean draft) throws EntityNotFoundException {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = new Query(new Criteria("appId").is(appId).and("objectId").is(objectId));
		Crm crmDB = template.findOne(query, Crm.class);
		if(crmDB == null) {
			throw new EntityNotFoundException(String.format("CRM with id %s not found", objectId));
		}
		template.findAndRemove(query, Crm.class);
	}
	
	public void addRiciclabolario(Riciclabolario riciclabolario, boolean draft) {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		template.save(riciclabolario);
	}
	
	public void removeRiciclabolario(String appId, String objectId, boolean draft) throws EntityNotFoundException {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = new Query(new Criteria("appId").is(appId).and("objectId").is(objectId));
		Riciclabolario riciclabolarioDB = template.findOne(query, Riciclabolario.class);
		if(riciclabolarioDB == null) {
			throw new EntityNotFoundException(String.format("Riciclabolario with id %s not found", objectId));
		}
		template.findAndRemove(query, Riciclabolario.class);
	}
	
	public void removeRiciclabolario(Riciclabolario riciclabolario, boolean draft) throws EntityNotFoundException {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = new Query(new Criteria("appId").is(riciclabolario.getAppId()).and("area").is(riciclabolario.getArea())
				.and("rifiuto").is(riciclabolario.getRifiuto()).and("tipologiaRifiuto").is(riciclabolario.getTipologiaRifiuto())
				.and("tipologiaUtenza").is(riciclabolario.getTipologiaUtenza()));
		Riciclabolario riciclabolarioDB = template.findOne(query, Riciclabolario.class);
		if(riciclabolarioDB == null) {
			throw new EntityNotFoundException(String.format("Riciclabolario %s not found", riciclabolario));
		}
		template.findAndRemove(query, Riciclabolario.class);
	}
	
	public void addRaccolta(Raccolta raccolta, boolean draft) {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		template.save(raccolta);
	}
	
	public void removeRaccolta(String appId, String objectId, boolean draft) throws EntityNotFoundException {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = new Query(new Criteria("appId").is(appId).and("objectId").is(objectId));
		Raccolta raccoltaDB = template.findOne(query, Raccolta.class);
		if(raccoltaDB == null) {
			throw new EntityNotFoundException(String.format("Raccolta with id %s not found", objectId));
		}
		template.findAndRemove(query, Raccolta.class);
	}
	
	public void addRifiuto(Rifiuto rifiuto, boolean draft) {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		template.save(rifiuto);
	}
	
	public void removeRifiuto(String appId, String objectId, boolean draft) throws EntityNotFoundException {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = new Query(new Criteria("appId").is(appId).and("objectId").is(objectId));
		Rifiuto rifiutoDB = template.findOne(query, Rifiuto.class);
		if(rifiutoDB == null) {
			throw new EntityNotFoundException(String.format("Rifiuto with id %s not found", objectId));
		}
		template.findAndRemove(query, Rifiuto.class);
	}
	
	public void updateRifiuto(Rifiuto rifiuto, boolean draft) throws EntityNotFoundException {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = new Query(new Criteria("appId").is(rifiuto.getAppId()).and("objectId").is(rifiuto.getObjectId()));
		Rifiuto rifiutoDB = template.findOne(query, Rifiuto.class);
		if(rifiutoDB == null) {
			throw new EntityNotFoundException(String.format("Rifiuto with id %s not found", rifiuto.getObjectId()));
		}
		Update update = new Update();
		update.set("lastUpdate", new Date());
		//TODO
		template.updateFirst(query, update, Rifiuto.class);
	}
	

}
