package com.pennant.pff.excess.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.excess.model.ExcessTransferUpload;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class ExcessTransferUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(ExcessTransferUploadValidateRecord.class);

	@Autowired
	private UploadService excessTransferUploadService;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		ExcessTransferUpload detail = new ExcessTransferUpload();
		detail.setReference(ObjectUtil.valueAsString(record.getValue("FINREFERENCE")));
		detail.setHeaderId(headerID);
		detail.setTransferFromType(ObjectUtil.valueAsString(record.getValue("TRANSFERFROMTYPE")));
		detail.setTransferToType(ObjectUtil.valueAsString(record.getValue("TRANSFERTOTYPE")));
		detail.setTransferAmount(ObjectUtil.valueAsBigDecimal(record.getValue("TRANSFERAMOUNT")));

		excessTransferUploadService.doValidate(header, detail);

		if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
			record.addValue("ERRORCODE", detail.getErrorCode());
			record.addValue("ERRORDESC", detail.getErrorDesc());
		}

		logger.debug(Literal.LEAVING);
	}

}
