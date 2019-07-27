package com.pennanttech.pff.settlement.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pff.settlementprocess.model.SettlementProcess;

public interface SettlementProcessDAO extends BasicCrudDao<SettlementProcess> {
	void saveSettlementProcessRequest(MapSqlParameterSource settlementMapdata);

	boolean isDuplicateHostReference(String hostReferance);

	boolean isDuplicateSettlementRef(String settlementRef);
}
