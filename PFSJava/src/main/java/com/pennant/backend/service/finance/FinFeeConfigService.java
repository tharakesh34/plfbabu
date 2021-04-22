package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinFeeConfig;
import com.pennanttech.pff.core.TableType;

public interface FinFeeConfigService {

	public void save(FinFeeConfig finFeeDetailConfig, TableType tableType);

	void saveList(List<FinFeeConfig> finFeeDetailConfig, String type);

	List<FinFeeConfig> getFinFeeConfigList(String finReference, String eventCode, boolean origination, String type);

}
