package com.pennant.backend.service.finance.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.holdrefund.model.HoldRefundUploadDetail;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;

public class HoldRefundUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(HoldRefundUploadValidateRecord.class);

	@Autowired
	private UploadService holdRefundUploadService;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = (Long) attributes.getParameterMap().get("HEADER_ID");

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		String finReference = String.valueOf(record.getValue("FINREFERENCE"));
		boolean recordExist = holdRefundUploadService.isInProgress(headerID, finReference);

		if (recordExist) {
			throw new AppException("Record is already initiated, unable to proceed.");
		}

		HoldRefundUploadDetail detail = new HoldRefundUploadDetail();
		detail.setReference(finReference);
		detail.setHeaderId(headerID);
		detail.setHoldStatus((String) attributes.getParameterMap().get("HOLDSTATUS"));
		detail.setReason((String) attributes.getParameterMap().get("REASON"));
		detail.setRemarks((String) attributes.getParameterMap().get("REMARKS"));

		holdRefundUploadService.doValidate(header, detail);

		if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
			record.addValue("ERRORCODE", detail.getErrorCode());
			record.addValue("ERRORDESC", detail.getErrorDesc());
		}
	}

}
