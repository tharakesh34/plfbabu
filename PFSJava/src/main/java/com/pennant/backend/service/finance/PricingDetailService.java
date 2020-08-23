package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.FinTypeVASProducts;
import com.pennant.backend.model.rmtmasters.FinanceType;

public interface PricingDetailService {

	FinanceType getFinanceTypeById(String id);

	List<FinTypeVASProducts> getVASProductsByFinType(String finType);

	List<FinFeeDetail> getFinFeeDetailById(String id, boolean isWIF, String type);

	List<FinanceMain> getFinanceMains(String id, String type);

	List<String> getInvestmentRefifAny(String finReference, String type);

	String getConfiguredTopUpFintype(String finType);

	List<VASRecording> getVASRecordingsByLinkRef(String finReference, String type);

	List<String> getParentRefifAny(String finReference, String type);

}
