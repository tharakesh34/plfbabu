package com.pennanttech.pff.logging.dao;

import java.util.Map;

import com.pennant.backend.model.finance.FinAutoApprovalDetails;

public interface FinAutoApprovalDetailDAO {

	void save(FinAutoApprovalDetails autoAppList);

	void update(FinAutoApprovalDetails autoAppList);

	void delete(FinAutoApprovalDetails autoAppList);

	Map<String, Integer> loadQDPValidityDays();

	boolean getFinanceIfApproved(long finID);

	boolean getFinanceServiceInstruction(long finID);

	boolean isQuickDisb(long finID);

}
