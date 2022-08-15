package com.pennanttech.interfacebajaj.fileextract;

import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;

public class DefaultPresentmentRespValidation implements ValidateRecord {

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		for (Entry<String, Object> entry : record.getValues().entrySet()) {
			map.addValue(entry.getKey().toUpperCase(), entry.getValue());
		}

		Object aggrementNum = map.getValue("AGREEMENTNO");
		if (aggrementNum != null && aggrementNum.toString().length() > 14) {
			throw new Exception("Client Code  length should be less than 15.");
		}
		if (ImplementationConstants.PRESENTMENT_EXTRACT_DEALER_MAN) {
			Object bflreferenceno = map.getValue("BFLREFERENCENO");
			if (bflreferenceno != null && bflreferenceno.toString().length() > 3) {
				throw new Exception("Dealer Code  length should be less than 4.");
			}
		}

		Object batchid = map.getValue("BATCHID");
		if (batchid == null) {
			throw new Exception("Debit Ref should be mandatory.");
		} else if (batchid.toString().length() != 29) {
			throw new Exception("Debit Ref length should be 29.");
		}

		Object status = map.getValue("STATUS");
		if (status == null) {
			throw new Exception("Status should be mandatory.");
		} else {
			int statusLength = status.toString().length();
			int minLength = ImplementationConstants.PRESENTMENT_EXPORT_STATUS_MIN_LENGTH;
			int maxLength = ImplementationConstants.PRESENTMENT_EXPORT_STATUS_MAX_LENGTH;
			if (statusLength == 0 || (statusLength >= minLength && statusLength > maxLength)) {
				throw new Exception("Status length should be minimum" + minLength + "and maximum" + maxLength);
			}
		}

		Object reasonCode = map.getValue("REASONCODE");
		if (status != null && !(StringUtils.equals(RepayConstants.PAYMENT_PAID, status.toString())
				|| StringUtils.equals(RepayConstants.PAYMENT_SUCCESS, status.toString()))) {
			if (reasonCode == null) {
				throw new Exception("Failure Code should be mandatory.");
			} else if (StringUtils.isBlank(reasonCode.toString())) {
				throw new Exception("Failure Code should not be empty.");
			}
		}

	}

}
