package com.pennant.pff.cheques.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.pdc.upload.ChequeUpload;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class ChequeUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(ChequeUploadValidateRecord.class);

	@Autowired
	private UploadService chequeUploadService;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		ChequeUpload detail = new ChequeUpload();

		detail.setReference(ObjectUtil.valueAsString(record.getValue("finReference")));
		detail.setAction(ObjectUtil.valueAsString(record.getValue("action")));

		ChequeDetail cd = new ChequeDetail();

		cd.setChequeType(ObjectUtil.valueAsString(record.getValue("chequeType")));
		cd.setChequeSerialNumber(ObjectUtil.valueAsString(record.getValue("chequeSerialNo")));
		cd.setAccountType(ObjectUtil.valueAsString(record.getValue("accountType")));
		cd.setAccHolderName(ObjectUtil.valueAsString(record.getValue("accHolderName")));
		cd.setAccountNo(ObjectUtil.valueAsString(record.getValue("accountNo")));
		cd.setIfsc(ObjectUtil.valueAsString(record.getValue("ifsc")));
		cd.setMicr(ObjectUtil.valueAsString(record.getValue("micr")));
		cd.setAmount(ObjectUtil.valueAsBigDecimal(record.getValue("amount")));
		cd.setChequeDate(ObjectUtil.valueAsDate(record.getValue("chequeDate")));

		detail.setChequeDetail(cd);

		chequeUploadService.doValidate(header, detail);

		chequeUploadService.updateProcess(header, detail, record);
	}

}
