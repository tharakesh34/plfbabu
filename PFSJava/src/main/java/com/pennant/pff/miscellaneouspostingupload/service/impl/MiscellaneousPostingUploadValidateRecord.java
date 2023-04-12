package com.pennant.pff.miscellaneouspostingupload.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.miscellaneousposting.upload.MiscellaneousPostingUpload;
import com.pennant.eod.constants.EodConstants;
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

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		MiscellaneousPostingUpload details = new MiscellaneousPostingUpload();
		details.setHeaderId(headerID);

		details.setBatchName(ObjectUtil.valueAsString(record.getValue("BATCHNAME")));
		details.setBatchPurpose(ObjectUtil.valueAsString(record.getValue("BATCHPURPOSE")));
		details.setReference(ObjectUtil.valueAsString(record.getValue("REFERENCE")));
		details.setCreditGL(ObjectUtil.valueAsString(record.getValue("CREDITGL")));
		details.setDebitGL(ObjectUtil.valueAsString(record.getValue("DEBITGL")));
		details.setTxnAmount(ObjectUtil.valueAsBigDecimal(record.getValue("TXNAMOUNT")));
		details.setValueDate(ObjectUtil.valueAsDate(record.getValue("VALUEDATE")));
		details.setNarrLine1(ObjectUtil.valueAsString(record.getValue("NARRLINE1")));
		details.setNarrLine2(ObjectUtil.valueAsString(record.getValue("NARRLINE2")));
		details.setNarrLine3(ObjectUtil.valueAsString(record.getValue("NARRLINE3")));
		details.setNarrLine4(ObjectUtil.valueAsString(record.getValue("NARRLINE4")));

		miscellaneouspostingUploadService.doValidate(header, details);

		if (details.getProgress() == EodConstants.PROGRESS_FAILED) {
			record.addValue("ERRORCODE", details.getErrorCode());
			record.addValue("ERRORDESC", details.getErrorDesc());
		}

		logger.debug(Literal.LEAVING);
	}
}
