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

public class AppDescriptor {

	private String appId;
	private Long version;
	private Long timestamp;
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public String toString() {
		return appId + "." + version;
	}
	
	
}
