package com.pennant.pff.blockautolettergenerate.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.blockautolettergenerate.upload.BlockAutoGenLetterUpload;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class BlockAutoGenLetterUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(BlockAutoGenLetterUploadValidateRecord.class);

	public BlockAutoGenLetterUploadValidateRecord() {
		super();
	}

	@Autowired
	private UploadService blockAutoGenLetterUploadService;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		BlockAutoGenLetterUpload details = new BlockAutoGenLetterUpload();
		details.setHeaderId(headerID);

		details.setReference(ObjectUtil.valueAsString(record.getValue("FINREFERENCE")));
		details.setAction(ObjectUtil.valueAsString(record.getValue("ACTION")));
		details.setRemarks(ObjectUtil.valueAsString(record.getValue("REMARKS")));

		blockAutoGenLetterUploadService.doValidate(header, details);

		if (details.getProgress() == EodConstants.PROGRESS_FAILED) {
			record.addValue("ERRORCODE", details.getErrorCode());
			record.addValue("ERRORDESC", details.getErrorDesc());
		}

		logger.debug(Literal.LEAVING);
	}
}