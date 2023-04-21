package com.pennant.pff.bulkfeewaiverupload.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.app.util.SysParamUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.bulkfeewaiverupload.dao.BulkFeeWaiverUploadDAO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.model.bulkfeewaiverupload.BulkFeeWaiverUpload;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class BulkFeeWaiverUploadValidateRecord implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(BulkFeeWaiverUploadValidateRecord.class);

	@Autowired
	private UploadService bulkFeeWaiverUploadService;
	private BulkFeeWaiverUploadDAO bulkFeeWaiverUploadDAO;

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		BulkFeeWaiverUpload bfee = new BulkFeeWaiverUpload();

		bfee.setHeaderId(headerID);
		bfee.setAppDate(SysParamUtil.getAppDate());
		bfee.setReference(ObjectUtil.valueAsString(record.getValue("FINREFERENCE")));
		bfee.setFeeTypeCode(ObjectUtil.valueAsString(record.getValue("FEETYPECODE")));
		bfee.setWaivedAmount(ObjectUtil.valueAsBigDecimal(record.getValue("WAIVEDAMOUNT")));

		bulkFeeWaiverUploadService.doValidate(header, bfee);

		if (bfee.getProgress() == EodConstants.PROGRESS_FAILED) {
			record.addValue("ERRORCODE", bfee.getErrorCode());
			record.addValue("ERRORDESC", bfee.getErrorDesc());
			List<BulkFeeWaiverUpload> details = new ArrayList<>();
			details.add(bfee);

			bulkFeeWaiverUploadDAO.update(details);
		}

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setBulkFeeWaiverUploadDAO(BulkFeeWaiverUploadDAO feeWaiverUploadDAO) {
		this.bulkFeeWaiverUploadDAO = feeWaiverUploadDAO;
	}

}
