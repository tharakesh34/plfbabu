package com.pennanttech.pff.logging.dao;

import com.pennant.backend.model.finance.InstBasedSchdDetails;

public interface InstBasedSchdDetailDAO {

	void save(InstBasedSchdDetails instBasedSchd);

	void update(InstBasedSchdDetails instBasedSchd);

	void delete(InstBasedSchdDetails instBasedSchd);

	boolean getFinanceIfApproved(long finID);

}
