package com.pennant.pff.receipt.upload.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.receiptstatus.upload.ReceiptStatusUpload;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class ReceiptStatusUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(ReceiptStatusUploadValidateRecord.class);

	@Autowired
	private UploadService receiptStatusUploadService;

	protected DataEngineStatus executionStatus;

	public void setExecutionStatus(DataEngineStatus executionStatus) {
		this.executionStatus = executionStatus;
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		ReceiptStatusUpload details = new ReceiptStatusUpload();
		details.setHeaderId(headerID);
		details.setReceiptId(ObjectUtil.valueAslong(record.getValue("RECEIPTID")));
		details.setStatusRM(ObjectUtil.valueAsString(record.getValue("STATUSRM")));
		details.setRealizationDate(ObjectUtil.valueAsDate(record.getValue("REALIZATIONDATE")));
		details.setBounceDate(ObjectUtil.valueAsDate(record.getValue("BOUNCEDATE")));
		details.setBorcReason(ObjectUtil.valueAsString(record.getValue("BORCREASON")));
		details.setBorcRemarks(ObjectUtil.valueAsString(record.getValue("BORCREMARKS")));

		receiptStatusUploadService.doValidate(header, details);

		if (details.getProgress() == EodConstants.PROGRESS_FAILED) {
			record.addValue("ERRORCODE", details.getErrorCode());
			record.addValue("ERRORDESC", details.getErrorDesc());
		}

		logger.debug(Literal.LEAVING);
	}

}