package com.pennant.pff.lpp.service.impl;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.lpp.upload.LPPUpload;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class LppUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(LppUploadValidateRecord.class);

	@Autowired
	private UploadService lPPUploadService;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		LPPUpload lppUpload = (LPPUpload) ObjectUtil.valueAsObject(record, LPPUpload.class);

		lppUpload.setReference(ObjectUtil.valueAsString(record.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLAOD_HEADER");

		lppUpload.setHeaderId(header.getId());
		lppUpload.setAppDate(header.getAppDate());

		lPPUploadService.doValidate(header, lppUpload);

		lPPUploadService.updateProcess(header, lppUpload, record);

		logger.debug(Literal.LEAVING);
	}

}
