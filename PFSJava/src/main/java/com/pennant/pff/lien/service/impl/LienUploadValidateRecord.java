package com.pennant.pff.lien.service.impl;

import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.model.lien.LienUpload;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class LienUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(LienUploadValidateRecord.class);

	@Autowired
	private UploadService lienUploadService;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		LienUpload details = (LienUpload) ObjectUtil.valueAsObject(record, LienUpload.class);

		details.setReference(ObjectUtil.valueAsString(record.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		details.setHeaderId(header.getId());
		details.setAppDate(header.getAppDate());

		lienUploadService.doValidate(header, details);

		lienUploadService.updateProcess(header, details, record);

		logger.debug(Literal.LEAVING);
	}

}
