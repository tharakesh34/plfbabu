package com.pennant.pff.lpp.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.lpp.upload.LPPUpload;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class LppUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(LppUploadValidateRecord.class);

	@Autowired
	private UploadService lPPUploadService;

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

		LPPUpload details = new LPPUpload();
		details.setHeaderId(headerID);
		details.setReference(ObjectUtil.valueAsString(record.getValue("FINREFERENCE")));
		details.setApplyToExistingLoans(ObjectUtil.valueAsString(record.getValue("APPLYTOEXISTINGLOANS")));
		details.setLoanType(ObjectUtil.valueAsString(record.getValue("LOANTYPE")));
		details.setApplyOverDue(ObjectUtil.valueAsString(record.getValue("APPLYOVERDUE")));
		details.setPenaltyType(ObjectUtil.valueAsString(record.getValue("PENALTYTYPE")));
		details.setIncludeGraceDays(ObjectUtil.valueAsString(record.getValue("INCLUDEGRACEDAYS")));
		details.setGraceDays(ObjectUtil.valueAsInt(record.getValue("GRACEDAYS")));
		details.setCalculatedOn(ObjectUtil.valueAsString(record.getValue("CALCULATEDON")));
		details.setAmountOrPercent(ObjectUtil.valueAsBigDecimal(record.getValue("AMOUNTORPERCENT")));
		details.setAllowWaiver(ObjectUtil.valueAsString(record.getValue("ALLOWWAIVER")));
		details.setMaxWaiver(ObjectUtil.valueAsBigDecimal(record.getValue("MAXWAIVER")));

		lPPUploadService.doValidate(header, details);

		if (details.getProgress() == EodConstants.PROGRESS_FAILED) {
			record.addValue("ERRORCODE", details.getErrorCode());
			record.addValue("ERRORDESC", details.getErrorDesc());
		}

		logger.debug(Literal.LEAVING);
	}

}
