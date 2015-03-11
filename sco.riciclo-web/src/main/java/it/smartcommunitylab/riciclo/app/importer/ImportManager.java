package it.smartcommunitylab.riciclo.app.importer;

import it.smartcommunitylab.riciclo.app.importer.converter.DataImporter;
import it.smartcommunitylab.riciclo.app.importer.converter.RifiutiConverter;
import it.smartcommunitylab.riciclo.app.importer.converter.RifiutiValidator;
import it.smartcommunitylab.riciclo.model.Rifiuti;
import it.smartcommunitylab.riciclo.storage.AppInfo;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImportManager {

	@Autowired
	private DataImporter importer;

	@Autowired
	private RifiutiConverter converter;
	
	@Autowired
	private RifiutiValidator validator;	

	@Autowired
	private RepositoryManager storage;

	public void uploadFiles(FileList fileList, AppInfo appInfo) throws ImportError {
		try {
			InputStream xlsIs = null;
			InputStream isoleIs = null;
			InputStream crmIs = null;

			validator.validateInput(appInfo, fileList);
			
			xlsIs = fileList.getModel().getInputStream();
			if (fileList.getIsole() != null) {
				isoleIs = fileList.getIsole().getInputStream();
			}
			if (fileList.getCrm() != null) {
				crmIs = fileList.getCrm().getInputStream();
			}

			it.smartcommunitylab.riciclo.app.importer.model.Rifiuti rifiuti = importer.importRifiuti(xlsIs, isoleIs, crmIs);

			Rifiuti convertedRifiuti = converter.convert(rifiuti, appInfo.getAppId());
			List<String> validationResult = validator.validate(convertedRifiuti);

			if (validationResult.isEmpty()) {
				storage.save(convertedRifiuti, appInfo.getAppId());
			} else {
				throw new ImportError(validationResult);
			}
		} catch (ImportError e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ImportError(e.getMessage());
		}
	}

}
