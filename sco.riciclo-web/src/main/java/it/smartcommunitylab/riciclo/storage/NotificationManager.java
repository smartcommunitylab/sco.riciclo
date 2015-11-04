package it.smartcommunitylab.riciclo.storage;

import it.smartcommunitylab.riciclo.model.Notification;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class NotificationManager {

	private MongoTemplate template;

	public NotificationManager(MongoTemplate template) {
		this.template = template;
	}

	public Notification saveNotification(Notification n, String ownerId) {
		n.setOwnerId(ownerId);
		template.save(n);
		return n;
	}

	public List<Notification> getNotifications(String ownerId) {
		return template.find(Query.query(Criteria.where("ownerId").is(ownerId)), Notification.class);
	}
}
