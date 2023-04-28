package com.pennant.pff.miscellaneouspostingupload.service.impl;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.miscellaneousposting.upload.MiscellaneousPostingUpload;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class MiscellaneousPostingUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(MiscellaneousPostingUploadValidateRecord.class);

	@Autowired
	private UploadService miscellaneouspostingUploadService;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		MiscellaneousPostingUpload details = (MiscellaneousPostingUpload) ObjectUtil.valueAsObject(record,
				MiscellaneousPostingUpload.class);

		details.setReference(ObjectUtil.valueAsString(record.getValue("reference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		details.setHeaderId(header.getId());
		details.setAppDate(header.getAppDate());

		miscellaneouspostingUploadService.doValidate(header, details);

		miscellaneouspostingUploadService.updateProcess(header, details, record);

		logger.debug(Literal.LEAVING);
	}

}
