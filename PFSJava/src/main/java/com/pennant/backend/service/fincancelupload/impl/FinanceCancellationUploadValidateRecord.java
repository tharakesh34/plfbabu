package com.pennant.backend.service.fincancelupload.impl;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.finance.FinCancelUploadDetail;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class FinanceCancellationUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(FinanceCancellationUploadValidateRecord.class);

	@Autowired
	private UploadService financeCancellationUploadService;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		FinCancelUploadDetail details = (FinCancelUploadDetail) ObjectUtil.valueAsObject(record,
				FinCancelUploadDetail.class);

		details.setReference(ObjectUtil.valueAsString(record.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		details.setHeaderId(header.getId());
		details.setAppDate(header.getAppDate());

		financeCancellationUploadService.doValidate(header, details);

		financeCancellationUploadService.updateProcess(header, details, record);

		logger.debug(Literal.LEAVING);
	}

}
