package com.pennant.pff.mandate.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.mandate.MandateUpload;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class MandateUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(MandateUploadValidateRecord.class);

	@Autowired
	private UploadService mandateUploadService;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		MandateUpload detail = new MandateUpload();

		detail.setReference(ObjectUtil.valueAsString(record.getValue("orgReference")));

		Mandate mndts = new Mandate();

		mndts.setCustCIF(ObjectUtil.valueAsString(record.getValue("custCIF")));
		mndts.setMandateRef(ObjectUtil.valueAsString(record.getValue("mandateRef")));
		mndts.setMandateType(ObjectUtil.valueAsString(record.getValue("mandateType")));
		mndts.setAccNumber(ObjectUtil.valueAsString(record.getValue("accNumber")));
		mndts.setAccHolderName(ObjectUtil.valueAsString(record.getValue("accHolderName")));
		mndts.setJointAccHolderName(ObjectUtil.valueAsString(record.getValue("jointAccHolderName")));
		mndts.setAccType(ObjectUtil.valueAsString(record.getValue("accType")));
		mndts.setStrOpenMandate(ObjectUtil.valueAsString(record.getValue("openMandate")));
		mndts.setStartDate(ObjectUtil.valueAsDate(record.getValue("startDate")));
		mndts.setExpiryDate(ObjectUtil.valueAsDate(record.getValue("expiryDate")));
		mndts.setMaxLimit(ObjectUtil.valueAsBigDecimal(record.getValue("maxLimit")));
		mndts.setPeriodicity(ObjectUtil.valueAsString(record.getValue("periodicity")));
		mndts.setStatus(ObjectUtil.valueAsString(record.getValue("mandateStatus")));
		mndts.setReason(ObjectUtil.valueAsString(record.getValue("reason")));
		mndts.setStrSwapIsActive(ObjectUtil.valueAsString(record.getValue("swapIsActive")));
		mndts.setEntityCode(ObjectUtil.valueAsString(record.getValue("entityCode")));
		mndts.setPartnerBankId(ObjectUtil.valueAsLong(record.getValue("partnerBankId")));
		mndts.seteMandateSource(ObjectUtil.valueAsString(record.getValue("eMandateSource")));
		mndts.seteMandateReferenceNo(ObjectUtil.valueAsString(record.getValue("eMandateReferenceNo")));
		mndts.setSwapEffectiveDate(ObjectUtil.valueAsDate(record.getValue("swapEffectiveDate")));
		mndts.setEmployerID(ObjectUtil.valueAsLong(record.getValue("employerID")));
		mndts.setEmployeeNo(ObjectUtil.valueAsString(record.getValue("employeeNo")));
		mndts.setIFSC(ObjectUtil.valueAsString(record.getValue("iFSC")));
		mndts.setMICR(ObjectUtil.valueAsString(record.getValue("mICR")));
		mndts.setStrExternalMandate(ObjectUtil.valueAsString(record.getValue("externalMandate")));
		mndts.setStrSecurityMandate(ObjectUtil.valueAsString(record.getValue("securityMandate")));
		detail.setMandate(mndts);

		mandateUploadService.doValidate(header, detail);

		mandateUploadService.updateProcess(header, detail, record);

		logger.debug(Literal.LEAVING);
	}

}
