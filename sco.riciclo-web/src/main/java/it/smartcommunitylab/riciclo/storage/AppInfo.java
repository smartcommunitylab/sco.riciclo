package it.smartcommunitylab.riciclo.storage;

import java.io.Serializable;
import java.util.List;

public class AppInfo implements Serializable {
	private static final long serialVersionUID = -130084868920590202L;

	private String appId;
	private String password;
	private List<String> modelElements;
	private List<String> comuni; //codice ISTAT dei comuni

	public List<String> getComuni() {
		return comuni;
	}

	public void setComuni(List<String> comuni) {
		this.comuni = comuni;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return appId + "=" + password;
	}

	public List<String> getModelElements() {
		return modelElements;
	}

	public void setModelElements(List<String> modelElements) {
		this.modelElements = modelElements;
	}

}
