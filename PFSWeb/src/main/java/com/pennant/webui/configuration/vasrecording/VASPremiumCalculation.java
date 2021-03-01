package com.pennant.webui.configuration.vasrecording;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.configuration.VASPremiumCalcDetails;
import com.pennant.backend.service.configuration.VASRecordingService;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.extension.feature.CustomVASPremiumCalculation;

public class VASPremiumCalculation {
	private static final Logger logger = LogManager.getLogger(VASPremiumCalculation.class);

	private transient VASRecordingService vasRecordingService;

	@Autowired(required = false)
	@Qualifier("customVASPremiumCalculation")
	private CustomVASPremiumCalculation customVASPremiumCalculation;

	public VASPremiumCalcDetails getPrimiumPercentage(VASPremiumCalcDetails preDetails) {
		logger.debug(Literal.ENTERING);

		List<VASPremiumCalcDetails> calcDetails = getVasRecordingService().getPremiumCalcDeatils(preDetails);

		if (CollectionUtils.isEmpty(calcDetails)) {
			return null;
		}

		// If Custom VAS Premium Calculation available then go to custom process.
		if (customVASPremiumCalculation != null) {
			return customVASPremiumCalculation.calcPrimiumPercentage(calcDetails, preDetails);
		} else {
			for (VASPremiumCalcDetails details : calcDetails) {
				if ((details.getCustomerAge() == preDetails.getCustomerAge())
						&& (StringUtils.equalsIgnoreCase(details.getGender(), preDetails.getGender()))
						&& (details.getPolicyAge() == preDetails.getPolicyAge())
						&& (preDetails.getFinAmount().compareTo(details.getMinAmount()) > 0
								&& details.getMaxAmount().compareTo(preDetails.getFinAmount()) >= 0)) {
					return details;
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	public BigDecimal getGSTPercentage(BigDecimal vasFee) {
		if (customVASPremiumCalculation != null) {
			return customVASPremiumCalculation.getGSTPercentage(vasFee);
		}
		return BigDecimal.ZERO;
	}

	/**
	 * Calculating the medical status.
	 * 
	 * @param premiumCalcDetails
	 * @return
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

	public VASRecordingService getVasRecordingService() {
		return vasRecordingService;
	}

	public void setVasRecordingService(VASRecordingService vasRecordingService) {
		this.vasRecordingService = vasRecordingService;
	}

	public BigDecimal getVasFee(BigDecimal vasFee) {
		if (customVASPremiumCalculation != null) {
			return customVASPremiumCalculation.getvasFee(vasFee);
		}
		return vasFee;
	}
}
