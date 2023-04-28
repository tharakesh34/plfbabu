package com.pennant.pff.receipt.validate;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.pff.receipt.model.ReceiptStatusUpload;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class ReceiptStatusUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(ReceiptStatusUploadValidateRecord.class);

	@Autowired
	private UploadService receiptStatusUploadService;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		ReceiptStatusUpload details = (ReceiptStatusUpload) ObjectUtil.valueAsObject(record, ReceiptStatusUpload.class);

		details.setReference(ObjectUtil.valueAsString(record.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		details.setHeaderId(header.getId());
		details.setAppDate(header.getAppDate());

		receiptStatusUploadService.doValidate(header, details);

		receiptStatusUploadService.updateProcess(header, details, record);

		logger.debug(Literal.LEAVING);
	}

}