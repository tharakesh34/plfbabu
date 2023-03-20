package com.pennant.pff.manualknockoff.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.model.knockoff.ManualKnockOffUpload;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class ManualKnockOffUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(ManualKnockOffUploadValidateRecord.class);

	@Autowired
	private UploadService manualKnockOffUploadServiceImpl;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		ManualKnockOffUpload detail = new ManualKnockOffUpload();
		detail.setHeaderId(headerID);
		detail.setReference(ObjectUtil.valueAsString(record.getValue("FINREFERENCE")));
		detail.setExcessType(ObjectUtil.valueAsString(record.getValue("EXCESSTYPE")));
		detail.setAllocationType(ObjectUtil.valueAsString(record.getValue("ALLOCATIONTYPE")));
		detail.setReceiptAmount(ObjectUtil.valueAsBigDecimal(record.getValue("RECEIPTAMOUNT")));

		manualKnockOffUploadServiceImpl.doValidate(header, detail);

		if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
			record.addValue("ERRORCODE", detail.getErrorCode());
			record.addValue("ERRORDESC", detail.getErrorDesc());
		}

		logger.debug(Literal.LEAVING);
	}
}
