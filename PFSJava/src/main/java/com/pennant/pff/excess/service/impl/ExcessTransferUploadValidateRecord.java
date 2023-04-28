package com.pennant.pff.excess.service.impl;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.pff.excess.model.ExcessTransferUpload;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class ExcessTransferUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(ExcessTransferUploadValidateRecord.class);

	@Autowired
	private UploadService excessTransferUploadService;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		ExcessTransferUpload transfer = (ExcessTransferUpload) ObjectUtil.valueAsObject(record,
				ExcessTransferUpload.class);

		transfer.setReference(ObjectUtil.valueAsString(record.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		transfer.setHeaderId(header.getId());
		transfer.setAppDate(header.getAppDate());

		excessTransferUploadService.doValidate(header, transfer);

		excessTransferUploadService.updateProcess(header, transfer, record, "C", "R");

		logger.debug(Literal.LEAVING);
	}

}
