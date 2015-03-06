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

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;



public class RepositoryManager {

	private Class[] classes = {Categorie.class, Area.class, Gestore.class, Istituzione.class, PuntoRaccolta.class, Raccolta.class, Riciclabolario.class, Profilo.class};
	
	private MongoTemplate draftTemplate;
	private MongoTemplate finalTemplate;

	public RepositoryManager(MongoTemplate draftTemplate, MongoTemplate finalTemplate) {
		this.draftTemplate = draftTemplate;
		this.finalTemplate = finalTemplate;
	}
	
	public void save(Rifiuti rifiuti, String appId) {
		cleanByAppId(appId, true);
		
		draftTemplate.save(rifiuti.getCategorie());

		for (Area area: rifiuti.getAree()) {
			draftTemplate.save(area);
		}
		for (Profilo profilo: rifiuti.getProfili()) {
			draftTemplate.save(profilo);
		}		
		for (Gestore gestore: rifiuti.getGestori()) {
			draftTemplate.save(gestore);
		}
		for (Istituzione istituzione: rifiuti.getIstituzioni()) {
			draftTemplate.save(istituzione);
		}		
		for (PuntoRaccolta puntoRaccolta: rifiuti.getPuntiRaccolta()) {
			draftTemplate.save(puntoRaccolta);
		}		
		for (Raccolta raccolta: rifiuti.getRaccolta()) {
			draftTemplate.save(raccolta);
		}	
		for (Riciclabolario riciclabolario: rifiuti.getRiciclabolario()) {
			draftTemplate.save(riciclabolario);
		}			
	}
	
	public void cleanByAppId(String appId, boolean draft) {
		MongoTemplate template;
		if (draft) {
			template = draftTemplate;
		} else {
			template = finalTemplate;
		}		
		
		Query query = new Query(new Criteria("appId").is(appId));
		
		for (Class clazz: classes) {
			template.remove(query, clazz);
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void publish(String appId) {
		for (Class clazz: classes) {
			List objects = draftTemplate.findAll(clazz);
			finalTemplate.remove(new Query(), clazz);
			for (Object obj: objects) {
				finalTemplate.save(obj);
			}
		}

		increaseAppId(appId);
	}
	
	public void createAppId(String appId) {
		Query query = new Query(new Criteria("appId").is(appId));
		List<AppDescriptor> apps = finalTemplate.find(query, AppDescriptor.class);
		if (apps == null || apps.isEmpty()) {
			AppDescriptor appDescr = new AppDescriptor();
			appDescr.setAppId(appId);
			appDescr.setVersion(0L);
			appDescr.setTimestamp(System.currentTimeMillis());
			finalTemplate.save(appDescr);
		}
		
	}
	
	public void increaseAppId(String appId) {
		Query query = new Query(new Criteria("appId").is(appId));
		Update update = new Update();
		update.inc("version", 1);		
		update.set("timestamp", System.currentTimeMillis());
		finalTemplate.upsert(query, update, AppDescriptor.class);		
	}
	
	public List findRifiuti(String className, String appId, boolean draft) throws ClassNotFoundException {
		MongoTemplate template;
		if (draft) {
			template = draftTemplate;
		} else {
			template = finalTemplate;
		}
		
		Query query = new Query(new Criteria("appId").is(appId));
		List result = template.find(query, Class.forName(className));
		
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
		Query query = new Query(new Criteria("appId").is(appId));
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
	
	public AppDescriptor getAppDescriptor(String appId) {
		Query query = new Query(new Criteria("appId").is(appId));
		return finalTemplate.findOne(query, AppDescriptor.class);
	}

}
