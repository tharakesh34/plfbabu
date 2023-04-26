package com.pennant.pff.branchchange.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.branchchange.upload.BranchChangeUpload;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class BranchChangeUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(BranchChangeUploadValidateRecord.class);

	@Autowired
	private UploadService branchChangeUploadService;

	protected DataEngineStatus executionStatus;

	public void setExecutionStatus(DataEngineStatus executionStatus) {
		this.executionStatus = executionStatus;
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		BranchChangeUpload details = new BranchChangeUpload();
		details.setHeaderId(headerID);

		details.setReference(ObjectUtil.valueAsString(record.getValue("FINREFERENCE")));
		details.setBranchCode(ObjectUtil.valueAsString(record.getValue("BRANCHCODE")));
		details.setRemarks(ObjectUtil.valueAsString(record.getValue("REMARKS")));

		branchChangeUploadService.doValidate(header, details);

		if (details.getProgress() == EodConstants.PROGRESS_FAILED) {
			record.addValue("ERRORCODE", details.getErrorCode());
			record.addValue("ERRORDESC", details.getErrorDesc());
		}

		logger.debug(Literal.LEAVING);
	}
}
