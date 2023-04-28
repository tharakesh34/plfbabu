package com.pennant.pff.writeoffupload.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennant.pff.writeoffupload.model.WriteOffUploadDetail;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class WriteOffUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(WriteOffUploadValidateRecord.class);

	@Autowired
	private UploadService writeOffUploadService;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		String finReference = ObjectUtil.valueAsString(record.getValue("FINREFERENCE"));
		boolean recordExist = writeOffUploadService.isInProgress(headerID, finReference);

		if (recordExist) {
			throw new AppException("Record is already initiated, unable to proceed.");
		}

		WriteOffUploadDetail detail = new WriteOffUploadDetail();
		detail.setHeaderId(headerID);
		detail.setReference(finReference);
		detail.setRemarks(ObjectUtil.valueAsString(record.getValue("REMARKS")));

		writeOffUploadService.doValidate(header, detail);

		writeOffUploadService.updateProcess(header, detail, record);

		logger.debug(Literal.LEAVING);
	}

}
