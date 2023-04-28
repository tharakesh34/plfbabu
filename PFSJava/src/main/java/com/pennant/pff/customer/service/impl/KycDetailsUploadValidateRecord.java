package com.pennant.pff.customer.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.bulkAddressUpload.CustomerKycDetail;
import com.pennant.pff.holdrefund.service.impl.HoldRefundUploadValidateRecord;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class KycDetailsUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(HoldRefundUploadValidateRecord.class);

	@Autowired
	private UploadService kycDetailsUploadService;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		String custCif = ObjectUtil.valueAsString(record.getValue("custCif"));

		String finReference = ObjectUtil.valueAsString(record.getValue("finReference"));

		boolean recordExist = kycDetailsUploadService.isInProgress(headerID, custCif);

		if (recordExist) {
			throw new AppException("Record is already initiated, unable to proceed.");
		}

		CustomerKycDetail detail = new CustomerKycDetail();
		detail.setHeaderId(headerID);
		detail.setFinReference(finReference);
		detail.setReference(custCif);
		detail.setCustAddrType(ObjectUtil.valueAsString(record.getValue("custAddrType")));
		detail.setCustAddrPriority(ObjectUtil.valueAsInt(record.getValue("custAddrPriority")));
		detail.setCustAddrLine3(ObjectUtil.valueAsString(record.getValue("custAddrLine3")));
		detail.setCustAddrHNbr(ObjectUtil.valueAsString(record.getValue("custAddrHNbr")));
		detail.setCustFlatNbr(ObjectUtil.valueAsString(record.getValue("custFlatNbr")));
		detail.setCustAddrStreet(ObjectUtil.valueAsString(record.getValue("custAddrStreet")));
		detail.setCustAddrLine1(ObjectUtil.valueAsString(record.getValue("custAddrLine1")));
		detail.setCustAddrLine2(ObjectUtil.valueAsString(record.getValue("custAddrLine2")));
		detail.setCustAddrCity(ObjectUtil.valueAsString(record.getValue("custAddrCity")));
		detail.setCustAddrLine4(ObjectUtil.valueAsString(record.getValue("custAddrLine4")));
		detail.setCustDistrict(ObjectUtil.valueAsString(record.getValue("custDistrict")));
		detail.setCustAddrProvince(ObjectUtil.valueAsString(record.getValue("custAddrProvince")));
		detail.setCustAddrCountry(ObjectUtil.valueAsString(record.getValue("custAddrCountry")));
		detail.setCustAddrZIP(ObjectUtil.valueAsString(record.getValue("custAddrZIP")));
		detail.setPhoneTypeCode(ObjectUtil.valueAsString(record.getValue("phoneTypeCode")));
		detail.setPhoneNumber(ObjectUtil.valueAsString(record.getValue("phoneNumber")));
		detail.setPhoneTypePriority(ObjectUtil.valueAsInt(record.getValue("phoneTypePriority")));
		detail.setCustEMailTypeCode(ObjectUtil.valueAsString(record.getValue("custEMailTypeCode")));
		detail.setCustEMail(ObjectUtil.valueAsString(record.getValue("custEMail")));
		detail.setCustEMailPriority(ObjectUtil.valueAsInt(record.getValue("custEMailPriority")));

		kycDetailsUploadService.doValidate(header, detail);

		kycDetailsUploadService.updateProcess(header, detail, record);

		logger.debug(Literal.LEAVING);
	}
}
