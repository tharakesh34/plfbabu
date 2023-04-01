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
		detail.setFinReference(valueOf(record, "FINREFERENCE"));
		detail.setReference(custCif);
		detail.setCustAddrType(valueOf(record, "CUSTADDRTYPE"));
		detail.setCustAddrPriority(ObjectUtil.valueAsint(record.getValue("CUSTADDRPRIORITY")));
		detail.setCustAddrLine3(valueOf(record, "CUSTADDRLINE3"));
		detail.setCustAddrHNbr(valueOf(record, "CUSTADDRHNBR"));
		detail.setCustFlatNbr(valueOf(record, "CUSTFLATNBR"));
		detail.setCustAddrStreet(valueOf(record, "CUSTADDRSTREET"));
		detail.setCustAddrLine1(valueOf(record, "CUSTADDRLINE1"));
		detail.setCustAddrLine2(valueOf(record, "CUSTADDRLINE2"));
		detail.setCustAddrCity(valueOf(record, "CUSTADDRCITY"));
		detail.setCustAddrLine4(valueOf(record, "CUSTADDRLINE4"));
		detail.setCustDistrict(valueOf(record, "CUSTDISTRICT"));
		detail.setCustAddrProvince(valueOf(record, "CUSTADDRPROVINCE"));
		detail.setCustAddrCountry(valueOf(record, "CUSTADDRCOUNTRY"));
		detail.setCustAddrZIP(valueOf(record, "CUSTADDRZIP"));
		detail.setPhoneTypeCode(valueOf(record, "PHONETYPECODE"));
		detail.setPhoneNumber(valueOf(record, "PHONENUMBER"));
		detail.setPhoneTypePriority(ObjectUtil.valueAsint(record.getValue("PHONETYPEPRIORITY")));
		detail.setCustEMailTypeCode(valueOf(record, "CUSTEMAILTYPECODE"));
		detail.setCustEMail(valueOf(record, "CUSTEMAIL"));
		detail.setCustEMailPriority(ObjectUtil.valueAsint(record.getValue("CUSTEMAILPRIORITY")));

		kycDetailsUploadService.doValidate(header, detail);

		if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
			record.addValue("ERRORCODE", detail.getErrorCode());
			record.addValue("ERRORDESC", detail.getErrorDesc());
		}

		logger.debug(Literal.LEAVING);
	}

	private String valueOf(MapSqlParameterSource record, String value) {
		return ObjectUtil.valueAsString(record.getValue(value));
	}

}
