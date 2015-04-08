package it.smartcommunitylab.riciclo.storage;

public class App {
	private AppInfo appInfo;
	private AppState publishState;
	private AppState draftState;
	
	public AppInfo getAppInfo() {
		return appInfo;
	}
	public void setAppInfo(AppInfo appInfo) {
		this.appInfo = appInfo;
	}
	public AppState getPublishState() {
		return publishState;
	}
	public void setPublishState(AppState publishState) {
		this.publishState = publishState;
	}
	public AppState getDraftState() {
		return draftState;
	}
	public void setDraftState(AppState draftState) {
		this.draftState = draftState;
	}
	
}
