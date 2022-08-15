package com.pennanttech.pff.cd.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public interface CDSettlementDAO {
	void saveSettlementProcessRequest(MapSqlParameterSource settlementMapdata);

	boolean isDuplicateHostReference(String hostReferance);

	boolean isDuplicateSettlementRef(String settlementRef);
}
