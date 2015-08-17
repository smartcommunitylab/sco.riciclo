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

import it.smartcommunitylab.riciclo.controller.Utils;
import it.smartcommunitylab.riciclo.exception.EntityNotFoundException;
import it.smartcommunitylab.riciclo.model.AppDataRifiuti;
import it.smartcommunitylab.riciclo.model.Area;
import it.smartcommunitylab.riciclo.model.CalendarioRaccolta;
import it.smartcommunitylab.riciclo.model.Categorie;
import it.smartcommunitylab.riciclo.model.Colore;
import it.smartcommunitylab.riciclo.model.Crm;
import it.smartcommunitylab.riciclo.model.Gestore;
import it.smartcommunitylab.riciclo.model.Istituzione;
import it.smartcommunitylab.riciclo.model.PuntoRaccolta;
import it.smartcommunitylab.riciclo.model.Raccolta;
import it.smartcommunitylab.riciclo.model.Riciclabolario;
import it.smartcommunitylab.riciclo.model.Rifiuto;
import it.smartcommunitylab.riciclo.model.Segnalazione;
import it.smartcommunitylab.riciclo.model.TipologiaProfilo;
import it.smartcommunitylab.riciclo.model.TipologiaPuntoRaccolta;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.google.common.collect.Lists;

public class RepositoryManager {
	private static final transient Logger logger = LoggerFactory.getLogger(RepositoryManager.class);
	
	private Class<?>[] classes = { Categorie.class, Area.class, Gestore.class, Istituzione.class, PuntoRaccolta.class,
			CalendarioRaccolta.class, Raccolta.class, Riciclabolario.class, TipologiaProfilo.class, Colore.class, 
			Segnalazione.class, Crm.class, TipologiaPuntoRaccolta.class, Rifiuto.class };

	@Autowired
	private AppSetup appSetup;

	private MongoTemplate draftTemplate;
	private MongoTemplate finalTemplate;

	public RepositoryManager(MongoTemplate draftTemplate, MongoTemplate finalTemplate) {
		this.draftTemplate = draftTemplate;
		this.finalTemplate = finalTemplate;
	}

	public void save(AppDataRifiuti appDataRifiuti, String ownerId) {
		AppState oldDraft = getAppState(ownerId, true);
		cleanByOwnerId(ownerId, true);
		Date actualDate = new Date();

		appDataRifiuti.getCategorie().setOwnerId(ownerId);
		draftTemplate.save(appDataRifiuti.getCategorie());

		for (Area area : appDataRifiuti.getAree()) {
			area.setOwnerId(ownerId);
			area.setCreationDate(actualDate);
			area.setLastUpdate(actualDate);
			draftTemplate.save(area);
		}
		for (TipologiaProfilo profilo : appDataRifiuti.getTipologiaProfili()) {
			profilo.setOwnerId(ownerId);
			profilo.setCreationDate(actualDate);
			profilo.setLastUpdate(actualDate);
			draftTemplate.save(profilo);
		}
		for (Gestore gestore : appDataRifiuti.getGestori()) {
			gestore.setOwnerId(ownerId);
			gestore.setCreationDate(actualDate);
			gestore.setLastUpdate(actualDate);
			draftTemplate.save(gestore);
		}
		for (Istituzione istituzione : appDataRifiuti.getIstituzioni()) {
			istituzione.setOwnerId(ownerId);
			istituzione.setCreationDate(actualDate);
			istituzione.setLastUpdate(actualDate);
			draftTemplate.save(istituzione);
		}
		for (PuntoRaccolta puntoRaccolta : appDataRifiuti.getPuntiRaccolta()) {
			puntoRaccolta.setOwnerId(ownerId);
			puntoRaccolta.setCreationDate(actualDate);
			puntoRaccolta.setLastUpdate(actualDate);
			draftTemplate.save(puntoRaccolta);
		}
		for(TipologiaPuntoRaccolta tipologiaPuntoRaccolta : appDataRifiuti.getTipologiaPuntiRaccolta()) {
			tipologiaPuntoRaccolta.setOwnerId(ownerId);
			tipologiaPuntoRaccolta.setCreationDate(actualDate);
			tipologiaPuntoRaccolta.setLastUpdate(actualDate);
			draftTemplate.save(tipologiaPuntoRaccolta);
		}
		for (Raccolta raccolta : appDataRifiuti.getRaccolte()) {
			raccolta.setOwnerId(ownerId);
			raccolta.setCreationDate(actualDate);
			raccolta.setLastUpdate(actualDate);
			draftTemplate.save(raccolta);
		}
		for (Riciclabolario riciclabolario : appDataRifiuti.getRiciclabolario()) {
			riciclabolario.setOwnerId(ownerId);
			riciclabolario.setCreationDate(actualDate);
			riciclabolario.setLastUpdate(actualDate);
			draftTemplate.save(riciclabolario);
		}
		for (Colore colore : appDataRifiuti.getColore()) {
			colore.setOwnerId(ownerId);
			colore.setCreationDate(actualDate);
			colore.setLastUpdate(actualDate);
			draftTemplate.save(colore);
		}
		for (Segnalazione segnalazione : appDataRifiuti.getSegnalazioni()) {
			segnalazione.setOwnerId(ownerId);
			segnalazione.setCreationDate(actualDate);
			segnalazione.setLastUpdate(actualDate);
			draftTemplate.save(segnalazione);
		}
		for (Rifiuto rifiuto : appDataRifiuti.getRifiuti()) {
			rifiuto.setOwnerId(ownerId);
			rifiuto.setCreationDate(actualDate);
			rifiuto.setLastUpdate(actualDate);
			draftTemplate.save(rifiuto);
		}
		for (Crm crm : appDataRifiuti.getCrm()) {
			crm.setOwnerId(ownerId);
			crm.setCreationDate(actualDate);
			crm.setLastUpdate(actualDate);
			draftTemplate.save(crm);
		}
		for (CalendarioRaccolta calendarioRaccolta : appDataRifiuti.getCalendariRaccolta()) {
			calendarioRaccolta.setOwnerId(ownerId);
			calendarioRaccolta.setCreationDate(actualDate);
			calendarioRaccolta.setLastUpdate(actualDate);
			draftTemplate.save(calendarioRaccolta);
		}
		saveAppVersion(ownerId, oldDraft.getVersion() + 1, true);
	}

	public void cleanByOwnerId(String ownerId, boolean draft) {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = appQuery(ownerId);

		for (Class<?> clazz : classes) {
			template.remove(query, clazz);
		}
	}

	public void publish(String ownerId) {
		Query query = appQuery(ownerId);
		for (Class<?> clazz : classes) {
			List<?> objects = draftTemplate.find(query, clazz);
			finalTemplate.remove(query, clazz);
			for (Object obj : objects) {
				finalTemplate.save(obj);
			}
		}

		AppState draft = getAppState(ownerId, true);
		saveAppVersion(ownerId, draft.getVersion(), false);
	}

	public void createApp(DataSetInfo appInfo) {
		saveAppState(appInfo.getOwnerId(), true);
		saveAppState(appInfo.getOwnerId(), false);
		saveAppInfo(appInfo, true);
		saveAppInfo(appInfo, false);
	}

	private void saveAppInfo(DataSetInfo appInfo, boolean draft) {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = appQuery(appInfo.getOwnerId());
		DataSetInfo appInfoDB = template.findOne(query, DataSetInfo.class);
		if (appInfoDB == null) {
			template.save(appInfo);
		} else {
			Update update = new Update();
			update.set("passowrd", appInfo.getPassword());
			update.set("modelElements", appInfo.getModelElements());
			update.set("comuni", appInfo.getComuni());
			template.updateFirst(query, update, DataSetInfo.class);
		}
	}

	public void saveAppState(String ownerId, boolean draft) {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = appQuery(ownerId);
		AppState app = template.findOne(query, AppState.class);
		if (app == null) {
			AppState appDescr = new AppState();
			appDescr.setOwenId(ownerId);
			appDescr.setVersion(0L);
			appDescr.setTimestamp(System.currentTimeMillis());
			template.save(appDescr);
		}

	}

	private void saveAppVersion(String ownerId, long version, boolean draft) {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = appQuery(ownerId);
		Update update = new Update();
		update.set("version", version);
		update.set("timestamp", System.currentTimeMillis());
		template.upsert(query, update, AppState.class);
	}

	public List<?> findRifiuti(String className, String ownerId, boolean draft) throws ClassNotFoundException {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;

		Query query = appQuery(ownerId);
		List<?> result = template.find(query, Class.forName(className));

		return result;
	}

	public AppDataRifiuti findRifiuti(String ownerId, boolean draft) {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		AppDataRifiuti appDataRifiuti = new AppDataRifiuti();
		appDataRifiuti.setOwnerId(ownerId);
		
		DataSetInfo appInfo = appSetup.findAppById(ownerId);
		if(appInfo!= null) {
			List<String> comuni = appInfo.getComuni();
			Map<String, Riciclabolario> resultMapRiciclabolario = new HashMap<String, Riciclabolario>();
			Map<String, Raccolta> resultMapRaccolta  = new HashMap<String, Raccolta>();
			Map<String, Area> resultMapArea  = new HashMap<String, Area>();
			Map<String, Crm> resultMapCrm  = new HashMap<String, Crm>();
			Map<String, PuntoRaccolta> resultMapPuntoRaccolta  = new HashMap<String, PuntoRaccolta>();
			Map<String, CalendarioRaccolta> resultMapCalendarioRaccolta  = new HashMap<String, CalendarioRaccolta>();
			Map<String, Segnalazione> resultMapSegnalazione  = new HashMap<String, Segnalazione>();
			Map<String, Gestore> resultMapGestore  = new HashMap<String, Gestore>();
			Map<String, Istituzione> resultMapIstituzione  = new HashMap<String, Istituzione>();
			try {
				for(String comune : comuni) {
					Utils.findAree(comune, ownerId, draft, resultMapArea, this);
					Utils.findRaccolte(comune, ownerId, draft, resultMapRaccolta, this);
					Utils.findGestori(comune, ownerId, draft, resultMapGestore, this);
					Utils.findIstituzioni(comune, ownerId, draft, resultMapIstituzione, this);
					Utils.findPuntiRaccolta(comune, ownerId, draft, resultMapPuntoRaccolta, this);
					Utils.findCalendariRaccolta(comune, ownerId, draft, resultMapCalendarioRaccolta, this);
					Utils.findRiciclabolario(comune, ownerId, draft, resultMapRiciclabolario, this);
					Utils.findSegnalazioni(comune, ownerId, draft, resultMapSegnalazione, this);
				}
				resultMapCrm = Utils.findCrm(resultMapPuntoRaccolta, ownerId, draft, this);
				
				Query query = appQuery(ownerId);
				appDataRifiuti.setTipologiaProfili(template.find(query, TipologiaProfilo.class));
				appDataRifiuti.setTipologiaPuntiRaccolta(template.find(query, TipologiaPuntoRaccolta.class));
				appDataRifiuti.setCategorie(template.findOne(query, Categorie.class));
				appDataRifiuti.setColore(template.find(query, Colore.class));
				appDataRifiuti.setRifiuti(template.find(query, Rifiuto.class));
				
				appDataRifiuti.setAree(Lists.newArrayList(resultMapArea.values()));
				appDataRifiuti.setRaccolte(Lists.newArrayList(resultMapRaccolta.values()));
				appDataRifiuti.setGestori(Lists.newArrayList(resultMapGestore.values()));
				appDataRifiuti.setIstituzioni(Lists.newArrayList(resultMapIstituzione.values()));
				appDataRifiuti.setCrm(Lists.newArrayList(resultMapCrm.values()));
				appDataRifiuti.setPuntiRaccolta(Lists.newArrayList(resultMapPuntoRaccolta.values()));
				appDataRifiuti.setCalendariRaccolta(Lists.newArrayList(resultMapCalendarioRaccolta.values()));
				appDataRifiuti.setRiciclabolario(Lists.newArrayList(resultMapRiciclabolario.values()));
				appDataRifiuti.setSegnalazioni(Lists.newArrayList(resultMapSegnalazione.values()));
			} catch (Exception e) {
				logger.error("error in findRifiuti:" + e.getMessage(), e);
			}
		}
		return appDataRifiuti;
	}

	private AppState getAppState(String ownerId, boolean draft) {
		Query query = appQuery(ownerId);
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		return template.findOne(query, AppState.class);
	}

	public App getAppDescriptor(String ownerId) {
		AppState draft = getAppState(ownerId, true);
		AppState published = getAppState(ownerId, false);
		App app = new App();
		app.setAppInfo(appSetup.findAppById(ownerId));
		app.setDraftState(draft);
		app.setPublishState(published);
		return app;
	}

	public List<String> getComuniList(String ownerId, boolean draft) {
		DataSetInfo appInfo = appSetup.findAppById(ownerId);
		if (appInfo != null) {
			return appInfo.getComuni();
		} else {
			return new ArrayList<String>();
		}
	}

	public List<?> findData(Class<?> entityClass, Criteria criteria, String ownerId, boolean draft)
			throws ClassNotFoundException {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = null;
		if (criteria != null) {
			query = new Query(new Criteria("ownerId").is(ownerId).andOperator(criteria));
		} else {
			query = new Query(new Criteria("ownerId").is(ownerId));
		}
		List<?> result = template.find(query, entityClass);
		return result;
	}

	public <T> T findOneData(Class<T> entityClass, Criteria criteria, String ownerId, boolean draft)
			throws ClassNotFoundException {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = null;
		if (criteria != null) {
			query = new Query(new Criteria("ownerId").is(ownerId).andOperator(criteria));
		} else {
			query = new Query(new Criteria("ownerId").is(ownerId));
		}
		T result = template.findOne(query, entityClass);
		return result;
	}

	private Query appQuery(String ownerId) {
		return new Query(new Criteria("ownerId").is(ownerId));
	}

	public void addCRM(Crm crm, boolean draft) {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		template.save(crm);
	}

	public void updateCRM(Crm crm, boolean draft) throws EntityNotFoundException {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = new Query(new Criteria("ownerId").is(crm.getOwnerId()).and("objectId").is(crm.getObjectId()));
		Crm crmDB = template.findOne(query, Crm.class);
		if (crmDB == null) {
			throw new EntityNotFoundException(String.format("CRM with id %s not found", crm.getObjectId()));
		}
		Update update = new Update();
		update.set("lastUpdate", new Date());
		// TODO
		template.updateFirst(query, update, Crm.class);
	}

	public void removeCRM(String ownerId, String objectId, boolean draft) throws EntityNotFoundException {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("objectId").is(objectId));
		Crm crmDB = template.findOne(query, Crm.class);
		if (crmDB == null) {
			throw new EntityNotFoundException(String.format("CRM with id %s not found", objectId));
		}
		template.findAndRemove(query, Crm.class);
	}

	public void addRiciclabolario(Riciclabolario riciclabolario, boolean draft) {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		template.save(riciclabolario);
	}

	public void removeRiciclabolario(String ownerId, String objectId, boolean draft) throws EntityNotFoundException {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("objectId").is(objectId));
		Riciclabolario riciclabolarioDB = template.findOne(query, Riciclabolario.class);
		if (riciclabolarioDB == null) {
			throw new EntityNotFoundException(String.format("Riciclabolario with id %s not found", objectId));
		}
		template.findAndRemove(query, Riciclabolario.class);
	}

	public void removeRiciclabolario(Riciclabolario riciclabolario, boolean draft) throws EntityNotFoundException {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = new Query(new Criteria("ownerId").is(riciclabolario.getOwnerId()).and("area")
				.is(riciclabolario.getArea()).and("rifiuto").is(riciclabolario.getRifiuto()).and("tipologiaRifiuto")
				.is(riciclabolario.getTipologiaRifiuto()).and("tipologiaUtenza").is(riciclabolario.getTipologiaUtenza()));
		Riciclabolario riciclabolarioDB = template.findOne(query, Riciclabolario.class);
		if (riciclabolarioDB == null) {
			throw new EntityNotFoundException(String.format("Riciclabolario %s not found", riciclabolario));
		}
		template.findAndRemove(query, Riciclabolario.class);
	}

	public void addRaccolta(Raccolta raccolta, boolean draft) {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		template.save(raccolta);
	}

	public void removeRaccolta(String ownerId, String objectId, boolean draft) throws EntityNotFoundException {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("objectId").is(objectId));
		Raccolta raccoltaDB = template.findOne(query, Raccolta.class);
		if (raccoltaDB == null) {
			throw new EntityNotFoundException(String.format("Raccolta with id %s not found", objectId));
		}
		template.findAndRemove(query, Raccolta.class);
	}

	public void addRifiuto(Rifiuto rifiuto, boolean draft) {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		template.save(rifiuto);
	}

	public void removeRifiuto(String ownerId, String objectId, boolean draft) throws EntityNotFoundException {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("objectId").is(objectId));
		Rifiuto rifiutoDB = template.findOne(query, Rifiuto.class);
		if (rifiutoDB == null) {
			throw new EntityNotFoundException(String.format("Rifiuto with id %s not found", objectId));
		}
		template.findAndRemove(query, Rifiuto.class);
	}

	public void updateRifiuto(Rifiuto rifiuto, boolean draft) throws EntityNotFoundException {
		MongoTemplate template = draft ? draftTemplate : finalTemplate;
		Query query = new Query(new Criteria("ownerId").is(rifiuto.getOwnerId()).and("objectId").is(rifiuto.getObjectId()));
		Rifiuto rifiutoDB = template.findOne(query, Rifiuto.class);
		if (rifiutoDB == null) {
			throw new EntityNotFoundException(String.format("Rifiuto with id %s not found", rifiuto.getObjectId()));
		}
		Update update = new Update();
		update.set("lastUpdate", new Date());
		// TODO
		template.updateFirst(query, update, Rifiuto.class);
	}

}
