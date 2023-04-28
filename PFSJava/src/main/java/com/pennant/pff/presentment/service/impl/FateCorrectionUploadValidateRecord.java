package com.pennant.pff.presentment.service.impl;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.model.presentment.PresentmentRespUpload;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class FateCorrectionUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(FateCorrectionUploadValidateRecord.class);

	@Autowired
	private UploadService fateCorrectionUploadService;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		PresentmentRespUpload presentment = (PresentmentRespUpload) ObjectUtil.valueAsObject(record,
				PresentmentRespUpload.class);

		presentment.setReference(ObjectUtil.valueAsString(record.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLAOD_HEADER");

		presentment.setHeaderId(header.getId());
		presentment.setAppDate(header.getAppDate());

		fateCorrectionUploadService.doValidate(header, presentment);

		fateCorrectionUploadService.updateProcess(header, presentment, record);

		logger.debug(Literal.LEAVING);
	}

}