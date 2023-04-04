package com.pennant.pff.customer.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.bulkAddressUpload.CustomerKycDetail;
import com.pennant.eod.constants.EodConstants;
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

		String custCif = ObjectUtil.valueAsString(record.getValue("CUSTCIF"));

		boolean recordExist = kycDetailsUploadService.isInProgress(headerID, custCif);

		if (recordExist) {
			throw new AppException("Record is already initiated, unable to proceed.");
		}

		CustomerKycDetail detail = new CustomerKycDetail();
		detail.setHeaderId(headerID);
		detail.setFinReference(getValue(record, "FINREFERENCE"));
		detail.setReference(custCif);
		detail.setCustAddrType(getValue(record, "CUSTADDRTYPE"));
		detail.setCustAddrPriority(ObjectUtil.valueAsInt(record.getValue("CUSTADDRPRIORITY")));
		detail.setCustAddrLine3(getValue(record, "CUSTADDRLINE3"));
		detail.setCustAddrHNbr(getValue(record, "CUSTADDRHNBR"));
		detail.setCustFlatNbr(getValue(record, "CUSTFLATNBR"));
		detail.setCustAddrStreet(getValue(record, "CUSTADDRSTREET"));
		detail.setCustAddrLine1(getValue(record, "CUSTADDRLINE1"));
		detail.setCustAddrLine2(getValue(record, "CUSTADDRLINE2"));
		detail.setCustAddrCity(getValue(record, "CUSTADDRCITY"));
		detail.setCustAddrLine4(getValue(record, "CUSTADDRLINE4"));
		detail.setCustDistrict(getValue(record, "CUSTDISTRICT"));
		detail.setCustAddrProvince(getValue(record, "CUSTADDRPROVINCE"));
		detail.setCustAddrCountry(getValue(record, "CUSTADDRCOUNTRY"));
		detail.setCustAddrZIP(getValue(record, "CUSTADDRZIP"));
		detail.setPhoneTypeCode(getValue(record, "PHONETYPECODE"));
		detail.setPhoneNumber(getValue(record, "PHONENUMBER"));
		detail.setPhoneTypePriority(ObjectUtil.valueAsInt(record.getValue("PHONETYPEPRIORITY")));
		detail.setCustEMailTypeCode(getValue(record, "CUSTEMAILTYPECODE"));
		detail.setCustEMail(getValue(record, "CUSTEMAIL"));
		detail.setCustEMailPriority(ObjectUtil.valueAsInt(record.getValue("CUSTEMAILPRIORITY")));

		kycDetailsUploadService.doValidate(header, detail);

		if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
			record.addValue("ERRORCODE", detail.getErrorCode());
			record.addValue("ERRORDESC", detail.getErrorDesc());
		}

		logger.debug(Literal.LEAVING);
	}

	private String getValue(MapSqlParameterSource record, String value) {
		return ObjectUtil.valueAsString(record.getValue(value));
	}

}
