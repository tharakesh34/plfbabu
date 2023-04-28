
package com.pennant.pff.hostglmapping.service.impl;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.hostglmapping.upload.HostGLMappingUpload;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class HostGLMappingUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(HostGLMappingUploadValidateRecord.class);

	@Autowired
	private UploadService hostglmappingUploadService;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		HostGLMappingUpload details = (HostGLMappingUpload) ObjectUtil.valueAsObject(record, HostGLMappingUpload.class);

		details.setReference(ObjectUtil.valueAsString(record.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		details.setHeaderId(header.getId());
		details.setAppDate(header.getAppDate());

		hostglmappingUploadService.doValidate(header, details);

		hostglmappingUploadService.updateProcess(header, details, record);

		logger.debug(Literal.LEAVING);
	}

}
