package it.smartcommunitylab.riciclo.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@Component
public class AppSetup {

	@Value("classpath:/apps-access.yml")
	private Resource resource;

	@PostConstruct
	public void init() throws IOException {
		Yaml yaml = new Yaml(new Constructor(AppSetup.class));
		AppSetup data = (AppSetup) yaml.load(resource.getInputStream());
		this.apps = data.apps;
	}
	

	private List<AppCredentials> apps;
	private Map<String,AppCredentials> appsMap;

	public List<AppCredentials> getApps() {
		return apps;
	}

	public void setApps(List<AppCredentials> apps) {
		System.out.println(apps);
		this.apps = apps;
	}

	@Override
	public String toString() {
		return "AppSetup [apps=" + apps + "]";
	}

	public AppCredentials findAppById(String username) {
		if (appsMap == null) {
			appsMap = new HashMap<String, AppCredentials>();
			for (AppCredentials app : apps) {
				appsMap.put(app.getId(), app);
			}
		}
		return appsMap.get(username);
	}
}
