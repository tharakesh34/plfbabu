package com.pennant.pff.presentment.service.impl;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.pff.presentment.model.RePresentmentUploadDetail;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class RepresentmentUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(RepresentmentUploadValidateRecord.class);

	@Autowired
	private UploadService rePresentmentUploadService;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		RePresentmentUploadDetail representment = (RePresentmentUploadDetail) ObjectUtil.valueAsObject(record,
				RePresentmentUploadDetail.class);

		representment.setReference(ObjectUtil.valueAsString(record.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLAOD_HEADER");

		representment.setHeaderId(header.getId());
		representment.setAppDate(header.getAppDate());

		rePresentmentUploadService.doValidate(header, representment);

		rePresentmentUploadService.updateProcess(header, representment, record);

		logger.debug(Literal.LEAVING);
	}

}