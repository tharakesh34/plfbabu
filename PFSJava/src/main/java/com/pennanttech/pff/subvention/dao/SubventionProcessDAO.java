package com.pennanttech.pff.subvention.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public interface SubventionProcessDAO {
	void saveSubventionProcessRequest(MapSqlParameterSource subventionMapdata);

	boolean isDuplicateHostReference(String hostReferance);

	long getLinkedTranIdByHostReference(String hostReference);
}
