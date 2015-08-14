package it.smartcommunitylab.riciclo.storage;

import java.io.Serializable;
import java.util.List;

public class DataSetInfo implements Serializable {
	private static final long serialVersionUID = -130084868920590202L;

	private String ownerId;
	private String password;
	private List<String> modelElements;
	private List<String> comuni; //codice ISTAT dei comuni

	public List<String> getComuni() {
		return comuni;
	}

	public void setComuni(List<String> comuni) {
		this.comuni = comuni;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String appId) {
		this.ownerId = appId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return ownerId + "=" + password;
	}

	public List<String> getModelElements() {
		return modelElements;
	}

	public void setModelElements(List<String> modelElements) {
		this.modelElements = modelElements;
	}

}
