package com.pennanttech.pff.extension.spreadsheet;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.CreditReviewDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.service.spreadsheet.SpreadSheetService;

public class SpreadSheetServiceImpl implements SpreadSheetService {
	private SpreadSheetDataAccess preadSheetDataAccess;

	public SpreadSheetServiceImpl() {
		super();
	}

	@Override
	public Map<String, Object> setSpreadSheetData(Map<String, Object> screenData, FinanceDetail fd) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> dataMap = new HashMap<>();

		CreditReviewDetails crdd = new CreditReviewDetails();
		String parameters = SysParamUtil.getValueAsString(SMTParameterConstants.CREDIT_ELG_PARAMS);

		if (parameters == null) {
			return map;
		}

		String finReference = getStringValue("FinReference", screenData);

		String[] elgParameters = parameters.split(",");

		for (String elgParm : elgParameters) {
			elgParm = elgParm.trim().toUpperCase();
			if ("FINTYPE".equals(elgParm)) {
				crdd.setProduct(getStringValue("FinType", screenData));
			} else if ("ELIGIBILITYMETHOD".equals(elgParm)) {
				crdd.setEligibilityMethod(getStringValue("EligibilityMethod", screenData));
			} else if ("EMPLOYMENTTYPE".equals(elgParm)) {
				crdd.setEmploymentType(getStringValue("EmpType", screenData));
			}
		}

		crdd = preadSheetDataAccess.getCreditReviewDetailsByLoanType(crdd);

		if (crdd == null) {
			return map;
		}

		CreditReviewData crd = preadSheetDataAccess.getCreditReviewDataByRef(finReference, crdd);

		if (fd.isSpreadSheetloaded()) {
			// return map;
		}

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		CustomerDetails cd = fd.getCustomerDetails();
		Customer cu = cd.getCustomer();

		dataMap.put("FIN_START_DATE", fm.getFinStartDate());
		dataMap.put("CustMatAge", DateUtil.getYearsBetween(fm.getMaturityDate(), cu.getCustDOB()));

		fd.setSpreadSheetloaded(true);

		map.put("financeDetail", fd);
		map.put("userRole", getStringValue("UserRole", screenData));
		map.put("creditReviewDetails", crdd);
		map.put("Right_Eligibility", getBooleanValue("Right_Eligibility", screenData));
		map.put("creditReviewData", crd);

		map.put("dataMap", dataMap);

		return map;
	}

	private Boolean getBooleanValue(String key, Map<String, Object> screenData) {
		String value = getStringValue(key, screenData);

		if (StringUtils.isEmpty(value)) {
			value = "0";
		}

		return new Boolean(value);
	}

	private String getStringValue(String key, Map<String, Object> screenData) {
		if (!screenData.containsKey(key)) {
			return "";
		}

		return screenData.get(key).toString();
	}

	@Autowired
	public void setPreadSheetDataAccess(SpreadSheetDataAccess preadSheetDataAccess) {
		this.preadSheetDataAccess = preadSheetDataAccess;
	}

}