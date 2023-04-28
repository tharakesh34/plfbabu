package com.pennant.pff.holdrefund.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.pff.holdrefund.model.HoldRefundUploadDetail;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class HoldRefundUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(HoldRefundUploadValidateRecord.class);

	@Autowired
	private UploadService holdRefundUploadService;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		String finReference = ObjectUtil.valueAsString(record.getValue("finReference"));
		boolean recordExist = holdRefundUploadService.isInProgress(headerID, finReference);

		if (recordExist) {
			throw new AppException("Record is already initiated, unable to proceed.");
		}

		HoldRefundUploadDetail detail = new HoldRefundUploadDetail();
		detail.setHeaderId(headerID);
		detail.setReference(finReference);
		detail.setHoldStatus(ObjectUtil.valueAsString(record.getValue("holdStatus")));
		detail.setReason(ObjectUtil.valueAsString(record.getValue("reason")));
		detail.setRemarks(ObjectUtil.valueAsString(record.getValue("remarks")));

		holdRefundUploadService.doValidate(header, detail);

		holdRefundUploadService.updateProcess(header, detail, record);
	}
}
