package com.pennanttech.external.mandate;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.SysParamUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.mandate.ExtMandateExtension;

public class ExtMandateHandler implements ExtMandateExtension {
	protected final Logger logger = LogManager.getLogger(getClass());

	@Override
	public void processMandateData(Map<String, Object> rowMap) throws Exception {
		logger.debug(Literal.ENTERING);

		Date appDate = SysParamUtil.getAppDate();
		rowMap.put("MANDATE_DATE", appDate);

		rowMap.put("END_DATE", null);

		String finReference = StringUtils.trimToNull(rowMap.get("FINREFERENCE").toString());
		rowMap.put("IMAGE_NAME", "ACH-" + finReference);

		if (rowMap.get("SECURITYMANDATE") != null) {
			String secureMandate = rowMap.get("SECURITYMANDATE").toString();
			if (secureMandate != null && "1".equals(secureMandate.trim())) {
				rowMap.put("CAT_CODE", "O002");
			} else {
				rowMap.put("CAT_CODE", "O001");
			}
		}

		if (rowMap.get("CUSTOMER_PHONE") != null) {
			rowMap.put("CUSTOMER_PHONE", "+91" + rowMap.get("CUSTOMER_PHONE"));
		}

		if (rowMap.get("CUSTOMER_EMAIL") != null) {
			rowMap.put("CUSTOMER_EMAIL", "");
		}

		String frequency = null;
		if (rowMap.get("FREQUENCY") != null) {
			String temp = rowMap.get("FREQUENCY").toString();
			if (temp.startsWith("M") || temp.startsWith("As")) {
				frequency = "MNTH";
			} else if (temp.startsWith("Q")) {
				frequency = "QURT";
			} else if (temp.startsWith("H")) {
				frequency = "MIAN";
			} else if (temp.startsWith("Y")) {
				frequency = "YEARLY";
			} else {
				frequency = "";
			}
			rowMap.put("FREQUENCY", frequency);
		}

		if (rowMap.get("ACCT_TYPE") != null) {
			String temp = rowMap.get("ACCT_TYPE").toString();
			if (temp.trim().contains("NRO") || temp.trim().contains("NRE")) {
				rowMap.put("ACCT_TYPE", "");
			}
		}

		logger.debug(Literal.LEAVING);
	}

}
