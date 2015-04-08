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

package it.smartcommunitylab.riciclo.app.importer;

import org.springframework.web.multipart.MultipartFile;

public class FileList {

	private MultipartFile model;
	private MultipartFile isole;
	private MultipartFile crm;
	
	public MultipartFile getModel() {
		return model;
	}
	public void setModel(MultipartFile model) {
		this.model = model;
	}
	public MultipartFile getIsole() {
		return isole;
	}
	public void setIsole(MultipartFile isole) {
		this.isole = isole;
	}
	public MultipartFile getCrm() {
		return crm;
	}
	public void setCrm(MultipartFile crm) {
		this.crm = crm;
	}
	
}
