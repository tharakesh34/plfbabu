package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.SanctionCondition;

public interface SanctionConditionDAO {

	String save(List<SanctionCondition> conditions);

	List<SanctionCondition> getSanctionConditions(String finReference);

}
