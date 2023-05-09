package com.pennant.pff.mandate.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.mandate.MandateUpload;
import com.pennant.eod.constants.EodConstants;
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

		detail.setHeaderId(headerID);
		detail.setReference(ObjectUtil.valueAsString(record.getValue("ORGREFERENCE")));

		Mandate mndts = new Mandate();

		mndts.setCustCIF(ObjectUtil.valueAsString(record.getValue("CUSTCIF")));
		mndts.setMandateRef(ObjectUtil.valueAsString(record.getValue("MANDATEREF")));
		mndts.setMandateType(ObjectUtil.valueAsString(record.getValue("MANDATETYPE")));
		mndts.setAccNumber(ObjectUtil.valueAsString(record.getValue("ACCNUMBER")));
		mndts.setAccHolderName(ObjectUtil.valueAsString(record.getValue("ACCHOLDERNAME")));
		mndts.setJointAccHolderName(ObjectUtil.valueAsString(record.getValue("JOINTACCHOLDERNAME")));
		mndts.setAccType(ObjectUtil.valueAsString(record.getValue("ACCTYPE")));
		mndts.setStrOpenMandate(ObjectUtil.valueAsString(record.getValue("OPENMANDATE")));
		mndts.setStartDate(ObjectUtil.valueAsDate(record.getValue("STARTDATE")));
		mndts.setExpiryDate(ObjectUtil.valueAsDate(record.getValue("EXPIRYDATE")));
		mndts.setMaxLimit(ObjectUtil.valueAsBigDecimal(record.getValue("MAXLIMIT")));
		mndts.setPeriodicity(ObjectUtil.valueAsString(record.getValue("PERIODICITY")));
		mndts.setStatus(ObjectUtil.valueAsString(record.getValue("MANDATESTATUS")));
		mndts.setReason(ObjectUtil.valueAsString(record.getValue("REASON")));
		mndts.setStrSwapIsActive(ObjectUtil.valueAsString(record.getValue("SWAPISACTIVE")));
		mndts.setEntityCode(ObjectUtil.valueAsString(record.getValue("ENTITYCODE")));
		mndts.setPartnerBankId(ObjectUtil.valueAsLong(record.getValue("PARTNERBANKID")));
		mndts.seteMandateSource(ObjectUtil.valueAsString(record.getValue("EMANDATESOURCE")));
		mndts.seteMandateReferenceNo(ObjectUtil.valueAsString(record.getValue("EMANDATEREFERENCENO")));
		mndts.setSwapEffectiveDate(ObjectUtil.valueAsDate(record.getValue("SWAPEFFECTIVEDATE")));
		mndts.setEmployerID(ObjectUtil.valueAsLong(record.getValue("EMPLOYERID")));
		mndts.setEmployeeNo(ObjectUtil.valueAsString(record.getValue("EMPLOYEENO")));
		mndts.setIFSC(ObjectUtil.valueAsString(record.getValue("IFSC")));
		mndts.setMICR(ObjectUtil.valueAsString(record.getValue("MICR")));
		mndts.setStrExternalMandate(ObjectUtil.valueAsString(record.getValue("EXTERNALMANDATE")));
		mndts.setStrSecurityMandate(ObjectUtil.valueAsString(record.getValue("SECURITYMANDATE")));

		detail.setMandate(mndts);

		mandateUploadService.doValidate(header, detail);

		if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
			record.addValue("ERRORCODE", detail.getErrorCode());
			record.addValue("ERRORDESC", detail.getErrorDesc());
		}

		logger.debug(Literal.LEAVING);
	}

}
