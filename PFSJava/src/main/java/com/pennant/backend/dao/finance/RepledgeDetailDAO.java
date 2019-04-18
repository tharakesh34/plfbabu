package com.pennant.backend.dao.finance;

import com.pennant.backend.model.finance.RepledgeDetail;
import com.pennanttech.pff.core.TableType;

public interface RepledgeDetailDAO {

	RepledgeDetail getRepledgeDetailById(final String finReference, String type);

	void save(RepledgeDetail repledgeDetail, TableType tableType);

	void update(RepledgeDetail repledgeDetail, TableType tableType);

	void delete(RepledgeDetail repledgeDetail, TableType tableType);

}
