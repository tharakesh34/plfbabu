package com.pennant.backend.service.payment.impl;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.payment.PaymentInstUploadDetail;
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

		PaymentInstUploadDetail paymentDetail = (PaymentInstUploadDetail) ObjectUtil.valueAsObject(record,
				PaymentInstUploadDetail.class);

		paymentDetail.setReference(ObjectUtil.valueAsString(record.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLAOD_HEADER");

		paymentDetail.setHeaderId(header.getId());
		paymentDetail.setAppDate(header.getAppDate());

		paymentInstructionUploadService.doValidate(header, paymentDetail);

		paymentInstructionUploadService.updateProcess(header, paymentDetail, record);

		logger.debug(Literal.LEAVING);
	}

}