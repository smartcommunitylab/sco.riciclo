package it.smartcommunitylab.riciclo.app.importer;

import it.smartcommunitylab.riciclo.app.importer.converter.DataImporter;
import it.smartcommunitylab.riciclo.app.importer.converter.RifiutiConverter;
import it.smartcommunitylab.riciclo.app.importer.converter.RifiutiValidator;
import it.smartcommunitylab.riciclo.model.AppDataRifiuti;
import it.smartcommunitylab.riciclo.storage.DataSetInfo;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

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

	public void uploadFiles(FileList fileList, DataSetInfo appInfo) throws ImportError {
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
			List<String> validationResult = validator.validateInputData(rifiuti);
			if (!validationResult.isEmpty()) {
				for (String error: validationResult) {
					System.err.println(error);
				}
				throw new ImportError(validationResult);
			}

			AppDataRifiuti convertedRifiuti = converter.convert(rifiuti, appInfo.getOwnerId());
			validationResult = validator.validate(convertedRifiuti);

			if (validationResult.isEmpty()) {
				storage.save(convertedRifiuti, appInfo.getOwnerId());
			} else {
				throw new ImportError(validationResult);
			}
		} catch (ImportError e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ImportError(Lists.newArrayList("Exception: " + ExceptionUtils.getMessage(e)));
		}
	}

}
