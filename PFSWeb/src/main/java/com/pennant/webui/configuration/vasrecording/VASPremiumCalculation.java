package com.pennant.webui.configuration.vasrecording;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.configuration.VASPremiumCalcDetails;
import com.pennant.backend.service.configuration.VASRecordingService;
import com.pennanttech.pennapps.core.resource.Literal;

public class VASPremiumCalculation {
	private static final Logger logger = Logger.getLogger(VASPremiumCalculation.class);

	private transient VASRecordingService vasRecordingService;

	public VASPremiumCalcDetails getPrimiumPercentage(VASPremiumCalcDetails preDetails) {
		logger.debug(Literal.ENTERING);

		List<VASPremiumCalcDetails> calcDetails = getVasRecordingService().getPremiumCalcDeatils(preDetails);
		BigDecimal finAmount = preDetails.getFinAmount();

		if (CollectionUtils.isEmpty(calcDetails)) {
			return null;
		}
		
		for (VASPremiumCalcDetails details : calcDetails) {
			if ((details.getCustomerAge() == preDetails.getCustomerAge())
					&& (StringUtils.equalsIgnoreCase(details.getGender(), preDetails.getGender()))
					&& (details.getPolicyAge() == preDetails.getPolicyAge())
					&& (finAmount.compareTo(details.getMinAmount()) > 0
							&& details.getMaxAmount().compareTo(finAmount) >= 0)) {
				return details;

			}
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	/*
	 * Medical Status
	 */
	public boolean getMedicalStatus(VASPremiumCalcDetails premiumCalcDetails) {
		String values = SysParamUtil.getValueAsString("VAS_MEDICAL_STATUS_CALCULATION");

		BigDecimal loanAmt = BigDecimal.ZERO;
		int age = 0;
		List<String> loanTypeList = new ArrayList<>();

		if (values != null) {
			String[] valArray = values.split(",");
			for (int i = 0; i < valArray.length; i++) {
				if (i == 0) {
					loanAmt = new BigDecimal(valArray[0]);
				} else if (i == 1) {
					age = Integer.valueOf(valArray[1]);
				} else if (i > 1) {
					loanTypeList.add(valArray[i]);
				}
			}
		}
		if ((premiumCalcDetails.getFinAmount().compareTo(loanAmt) >= 0) && (premiumCalcDetails.getCustomerAge() > age)
				&& (loanTypeList.contains(premiumCalcDetails.getFinType()))) {
			return true;
		}
		return false;
	}

	private int getCcyFormat() {
		return CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
	}

	public VASRecordingService getVasRecordingService() {
		return vasRecordingService;
	}
	public void setVasRecordingService(VASRecordingService vasRecordingService) {
		this.vasRecordingService = vasRecordingService;
	}

}
