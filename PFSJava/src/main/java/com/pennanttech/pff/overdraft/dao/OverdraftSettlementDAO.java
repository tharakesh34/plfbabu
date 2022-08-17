package com.pennanttech.pff.overdraft.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public interface OverdraftSettlementDAO {
	void saveODSettlementProcessRequest(MapSqlParameterSource settlementMapdata);

	boolean isDuplicateReference(String Reference, String odSettlementRef);

	boolean isDuplicateODSettlementRef(String odSettlementRef);
}