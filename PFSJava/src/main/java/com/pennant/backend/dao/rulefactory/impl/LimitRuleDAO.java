package com.pennant.backend.dao.rulefactory.impl;

import java.util.List;

import com.pennant.backend.model.rulefactory.BMTRBFldDetails;
import com.pennant.backend.model.rulefactory.LimitFilterQuery;
import com.pennant.backend.model.rulefactory.LimitFldCriterias;

public interface LimitRuleDAO {

	LimitFilterQuery getLimitRule();
	LimitFilterQuery getNewLimitRule();
	void update(LimitFilterQuery dedupParm, String type);
	void delete(LimitFilterQuery dedupParm,String type);
	long save(LimitFilterQuery dedupParm,String type);
	LimitFilterQuery getLimitRuleByID(String id, String queryModule,
			String querySubCode, String type);
	List<LimitFilterQuery> getLimitRuleByModule(String queryModule,
			String querySubCode, String type);
	List<BMTRBFldDetails> getFieldList(String module, String event);
	List<LimitFldCriterias> getOperatorsList();

}
