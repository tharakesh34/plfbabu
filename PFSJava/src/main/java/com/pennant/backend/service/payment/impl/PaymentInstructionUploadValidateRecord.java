package com.pennant.backend.service.payment.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.payment.PaymentInstUploadDetail;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class PaymentInstructionUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(PaymentInstructionUploadValidateRecord.class);

	@Autowired
	private UploadService paymentInstructionUploadService;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		PaymentInstUploadDetail detail = new PaymentInstUploadDetail();
		detail.setHeaderId(headerID);
		detail.setReference(ObjectUtil.valueAsString(record.getValue("FINREFERENCE")));
		detail.setExcessType(ObjectUtil.valueAsString(record.getValue("EXCESSTYPE")));
		detail.setFeeType(ObjectUtil.valueAsString(record.getValue("FEETYPE")));
		detail.setPayAmount(ObjectUtil.valueAsBigDecimal(record.getValue("PAYAMOUNT")));
		detail.setRemarks(ObjectUtil.valueAsString(record.getValue("REMARKS")));
		detail.setOverRide(ObjectUtil.valueAsString(record.getValue("OVERRIDEOVERDUE")));

		paymentInstructionUploadService.doValidate(header, detail);

		if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
			record.addValue("ERRORCODE", detail.getErrorCode());
			record.addValue("ERRORDESC", detail.getErrorDesc());
		}

		logger.debug(Literal.LEAVING);
	}
}
