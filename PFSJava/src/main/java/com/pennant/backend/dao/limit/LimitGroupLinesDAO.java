package com.pennant.backend.dao.limit;

import java.util.List;

import com.pennant.backend.model.limit.LimitGroupLines;

public interface LimitGroupLinesDAO {
	LimitGroupLines getLimitGroupLines();

	LimitGroupLines getNewLimitGroupLines();

	List<LimitGroupLines> getLimitGroupLinesById(String id, String type);

	void update(LimitGroupLines limitGroupLines, String type);

	void delete(String limitGroupCode, String type);

	String save(LimitGroupLines limitGroupLines, String type);

	void deleteLimitGroupLines(LimitGroupLines limitGroupLines, String type);

	String getLimitLines(LimitGroupLines limitGroupLines, String id);

	List<LimitGroupLines> getLimitGroupItemById(String id, String type);

	int validationCheck(String limitGroup, String type);

	int limitLineCheck(String limitLine, String limitCategory, String type);

	String getGroupByLineAndHeader(String limitLine, long headerID);

	String getGroupByGroupAndHeader(String groupCode, long headerID);


	List<LimitGroupLines> getGroupCodesByLimitGroup(String code, boolean limitLine, String type);

	String getGroupcodes(String code, boolean limitLine, String type);

	List<LimitGroupLines> getAllLimitLinesByGroup(String group, String type);

	int getLimitLinesByRuleCode(String ruleCode, String type);

}
