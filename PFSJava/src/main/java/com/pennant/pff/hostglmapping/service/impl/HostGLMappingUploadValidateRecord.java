
package com.pennant.pff.hostglmapping.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.hostglmapping.upload.HostGLMappingUpload;
import com.pennant.eod.constants.EodConstants;
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

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		HostGLMappingUpload details = new HostGLMappingUpload();
		details.setHeaderId(headerID);

		details.setHostGLCode(ObjectUtil.valueAsString(record.getValue("HOSTGLCODE")));
		details.setAccountType(ObjectUtil.valueAsString(record.getValue("ACCOUNTTYPE")));
		details.setLoanType(ObjectUtil.valueAsString(record.getValue("LOANTYPE")));
		details.setCostCentreCode(ObjectUtil.valueAsString(record.getValue("COSTCENTRECODE")));
		details.setProfitCentreCode(ObjectUtil.valueAsString(record.getValue("PROFITCENTRECODE")));
		details.setOpenedDate(ObjectUtil.valueAsDate(record.getValue("OPENEDDATE")));
		details.setAllowManualEntries(ObjectUtil.valueAsString(record.getValue("ALLOWMANUALENTRIES")));
		details.setGLDescription(ObjectUtil.valueAsString(record.getValue("GLDESCRIPTION")));

		hostglmappingUploadService.doValidate(header, details);

		if (details.getProgress() == EodConstants.PROGRESS_FAILED) {
			record.addValue("ERRORCODE", details.getErrorCode());
			record.addValue("ERRORDESC", details.getErrorDesc());
		}

		logger.debug(Literal.LEAVING);
	}
}
