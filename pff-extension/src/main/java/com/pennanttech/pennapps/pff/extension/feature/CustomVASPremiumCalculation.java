package com.pennanttech.pennapps.pff.extension.feature;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.configuration.VASPremiumCalcDetails;

public interface CustomVASPremiumCalculation {

	VASPremiumCalcDetails calcPrimiumPercentage(List<VASPremiumCalcDetails> calcDetailsList,
			VASPremiumCalcDetails originalDetails);

	BigDecimal getGSTPercentage(BigDecimal vasFee);

	BigDecimal getvasFee(BigDecimal vasFee);

}
