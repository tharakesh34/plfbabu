package com.pennant.webui.configuration.vasconfiguration;

import javax.sql.DataSource;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.interfacebajaj.fileextract.service.AbstractFileExtractService;
import com.pennanttech.interfacebajaj.fileextract.service.FileExtractService;

public class VASPremiumCalcExtractService extends AbstractFileExtractService
		implements FileExtractService<VASPremiumCalcDetailExtract> {

	private DataSource dataSource;
	private PresentmentDetailService presentmentDetailService;

	@Override
	public VASPremiumCalcDetailExtract getFileExtract(long userId, String contentType) throws Exception {
		VASPremiumCalcDetailExtract extractDetails = new VASPremiumCalcDetailExtract(dataSource, presentmentDetailService);
		extractDetails.setDefaultDirectory(getLoacation("PRESENTMENT_RESPONSE_UPLOAD_FILEPATH"));
		extractDetails.setFileExtension(contentType);
		extractDetails.setFileNamePrefix("");
		extractDetails.setBatchType("BATCH_TYPE_VASPREMIUM_CALC_IMPORT");
		extractDetails.setUserId(userId);
		extractDetails.setLogStatus(false);

		return extractDetails;
	}

	@Override
	public void renderPannel(VASPremiumCalcDetailExtract extractDetails) {
		PennantConstants.BATCH_TYPE_VASPREMIUM_CALC_IMPORT.reset();
		PennantConstants.BATCH_TYPE_VASPREMIUM_CALC_IMPORT.setFileName(extractDetails.getFile().getName());
		PennantConstants.BATCH_TYPE_VASPREMIUM_CALC_IMPORT.setTotalRecords(extractDetails.getTotalRecords());
		PennantConstants.BATCH_TYPE_VASPREMIUM_CALC_IMPORT.setValueDate(DateUtility.getSysDate());
		PennantConstants.BATCH_TYPE_VASPREMIUM_CALC_IMPORT.setName("VAS Premium Calculation Details");
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public PresentmentDetailService getPresentmentDetailService() {
		return presentmentDetailService;
	}

	public void setPresentmentDetailService(PresentmentDetailService presentmentDetailService) {
		this.presentmentDetailService = presentmentDetailService;
	}

}
