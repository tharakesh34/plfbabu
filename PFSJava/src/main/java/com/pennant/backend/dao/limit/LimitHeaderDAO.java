package com.pennant.backend.dao.limit;

import java.util.List;

import com.pennant.backend.model.limit.LimitHeader;

public interface LimitHeaderDAO {

	LimitHeader getLimitHeader();

	LimitHeader getNewLimitHeader();

	LimitHeader getLimitHeaderByCustomerGroupCode(long groupCode, String type);

	LimitHeader getLimitHeaderByRule(String ruleCode, String ruleValue,
			String type);

	long save(LimitHeader limitHeader, String tableType);

	void update(LimitHeader limitHeader, String tableType);

	void delete(LimitHeader limitHeader, String string);

	LimitHeader getLimitHeaderById(long headerId, String string);

	LimitHeader getLimitHeaderByCustomerId(long customerId, String type);

	boolean isCustomerExists(long customerId, String string);

	List<LimitHeader> getLimitHeaderByStructureCode(String code, String type);

	void updateRebuild(long headerId, boolean rebuild, String type);

	int getLimitHeaderCountById(long headerId, String type);
	
	int getLimitHeaderAndCustCountById(long headerId, long CustID);
	
	List<LimitHeader> getLimitHeaders(String type);

}
