package com.pennanttech.interfacebajaj.fileextract;

import javax.sql.DataSource;

import com.pennanttech.interfacebajaj.fileextract.service.AbstractFileExtractService;
import com.pennanttech.interfacebajaj.fileextract.service.FileExtractService;

public class PresentmentExtractService extends AbstractFileExtractService implements FileExtractService<PresentmentDetailExtract> {

	private DataSource dataSource;

	@Override
	public PresentmentDetailExtract getFileExtract(long userId) throws Exception {
		PresentmentDetailExtract extractDetails = new PresentmentDetailExtract(dataSource);
		extractDetails.setDefaultDirectory(getLoacation("PRESENTMENT_RESPONSE_UPLOAD_FILEPATH"));
		extractDetails.setFileExtension(".txt");
		extractDetails.setFileNamePrefix("");// CheckWithChiatanya
		extractDetails.setBatchType("PRESENTMENT_RESPONSE_IMPORT");
		extractDetails.setUserId(userId);
		extractDetails.batchStatus.setBatchReference("Manual File Upload.");
		extractDetails.setLogStatus(false);

		return extractDetails;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	

}
