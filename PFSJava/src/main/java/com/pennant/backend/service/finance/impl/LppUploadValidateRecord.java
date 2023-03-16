package com.pennant.backend.service.finance.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.lpp.upload.LPPUpload;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.resource.Literal;

public class LppUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(LppUploadValidateRecord.class);

	@Autowired
	private UploadService lPPUploadService;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = (Long) attributes.getParameterMap().get("HEADER_ID");

		if (headerID == null) {
			return;
		}

		String finReference = String.valueOf(record.getValue("FINREFERENCE"));

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		LPPUpload details = new LPPUpload();
		details.setReference(finReference);
		details.setHeaderId(headerID);
		details.setApplyOverDue((String) attributes.getParameterMap().get("APPLYOVERDUE"));
		details.setPenaltyType((String) attributes.getParameterMap().get("PENALTYTYPE"));
		details.setIncludeGraceDays((String) attributes.getParameterMap().get("INCLUDEGRACEDAYS"));
		details.setHoldStatus((String) attributes.getParameterMap().get("HOLDSTATUS"));
		details.setReason((String) attributes.getParameterMap().get("REASON"));
		details.setRemarks((String) attributes.getParameterMap().get("REMARKS"));

		lPPUploadService.doValidate(header, details);

		if (details.getProgress() == EodConstants.PROGRESS_FAILED) {
			record.addValue("ERRORCODE", details.getErrorCode());
			record.addValue("ERRORDESC", details.getErrorDesc());
		}

	}

}
