package it.smartcommunitylab.riciclo.app.importer;

import java.util.List;

public class ImportError extends Exception {

	private static final long serialVersionUID = -542452957836505587L;

	private List<String> validationResults;

	public ImportError() {
		super();
	}
	public ImportError(List<String> validationResults) {
		this.validationResults = validationResults;
	}

	public ImportError(String error) {
		super(error);
	}
	public List<String> getValidationResults() {
		return validationResults;
	}
	public void setValidationResults(List<String> validationResults) {
		this.validationResults = validationResults;
	}
}
