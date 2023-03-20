package com.pennant.backend.service.finance.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.crossloanknockoff.CrossLoanKnockoffUpload;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class CrossLoanKnockOffUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(CrossLoanKnockOffUploadValidateRecord.class);

	@Autowired
	private UploadService crossLoanKnockOffUploadService;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		CrossLoanKnockoffUpload detail = new CrossLoanKnockoffUpload();
		detail.setHeaderId(headerID);
		detail.setFromFinReference(ObjectUtil.valueAsString(record.getValue("FROMFINREFERENCE")));
		detail.setToFinReference(ObjectUtil.valueAsString(record.getValue("TOFINREFERENCE")));
		detail.setExcessType(ObjectUtil.valueAsString(record.getValue("EXCESSTYPE")));
		detail.setExcessAmount(ObjectUtil.valueAsBigDecimal(record.getValue("EXCESSAMOUNT")));
		detail.setAllocationType(ObjectUtil.valueAsString(record.getValue("ALLOCATIONTYPE")));

		crossLoanKnockOffUploadService.doValidate(header, detail);

		if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
			record.addValue("ERRORCODE", detail.getErrorCode());
			record.addValue("ERRORDESC", detail.getErrorDesc());
		}

		logger.debug(Literal.LEAVING);
	}

}
