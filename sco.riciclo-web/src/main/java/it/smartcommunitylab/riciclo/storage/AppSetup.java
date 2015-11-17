package it.smartcommunitylab.riciclo.storage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppSetup {

//	@Value("classpath:/apps-info.yml")
//	private Resource resource;
	
	@Autowired
	private RepositoryManager storage;	

	@PostConstruct
	public void init() throws IOException {
		this.apps = storage.getAppInfoProduction();
		this.appsMap = null;
//		Yaml yaml = new Yaml(new Constructor(AppSetup.class));
//		AppSetup data = (AppSetup) yaml.load(resource.getInputStream());
//		this.apps = data.apps;
//		for (DataSetInfo cred: data.getApps()) {
//			storage.createApp(cred);
//		}
	}
	

	private List<DataSetInfo> apps;
	private Map<String,DataSetInfo> appsMap;

	public List<DataSetInfo> getApps() {
		return apps;
	}

	public void setApps(List<DataSetInfo> apps) {
		this.apps = apps;
	}

	@Override
	public String toString() {
		return "AppSetup [apps=" + apps + "]";
	}

	public DataSetInfo findAppById(String username) {
		if (appsMap == null) {
			appsMap = new HashMap<String, DataSetInfo>();
			for (DataSetInfo app : apps) {
				appsMap.put(app.getOwnerId(), app);
			}
		}
		return appsMap.get(username);
	}

}
