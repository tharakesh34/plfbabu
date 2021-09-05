package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.FinTypeVASProducts;
import com.pennant.backend.model.rmtmasters.FinanceType;

public interface PricingDetailService {

	FinanceType getFinanceTypeById(String finType);

	List<FinTypeVASProducts> getVASProductsByFinType(String finType);

	List<FinFeeDetail> getFinFeeDetailById(long finID, boolean isWIF, String type);

	List<FinanceMain> getFinanceMains(long finID, String type);

	List<Long> getInvestmentRefifAny(String investmentRef, String type);

	String getConfiguredTopUpFintype(String finType);

	List<VASRecording> getVASRecordingsByLinkRef(String finID, String type);

	List<Long> getParentRefifAny(String parentRef, String type);

}
