package com.pennant.backend.dao.limit;

import java.util.List;
import java.util.Set;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennanttech.pff.core.TableType;

public interface LimitHeaderDAO {

	LimitHeader getLimitHeader();

	LimitHeader getNewLimitHeader();

	LimitHeader getLimitHeaderByCustomerGroupCode(long groupCode, String type);

	LimitHeader getLimitHeaderByRule(String ruleCode, String ruleValue, String type);

	long save(LimitHeader limitHeader, String tableType);

	void update(LimitHeader limitHeader, String tableType);

	void delete(LimitHeader limitHeader, String string);

	LimitHeader getLimitHeaderById(long headerId, String string);

	LimitHeader getLimitHeaderByCustomerId(long customerId, String type);

	boolean isCustomerExists(long customerId, String string);

	List<LimitHeader> getLimitHeaderByStructureCode(String code, String type);

	void updateRebuild(long headerId, boolean rebuild, String type);

	int getLimitHeaderCountById(long headerId, String type);

	int getLimitHeaderAndCustCountById(long headerId, long CustID);

	List<LimitHeader> getLimitHeaders(String type);

	int getLimitHeaderAndCustGrpCountById(long headerId, long CustGrpID);

	long isLimitBlock(long custID, String type, boolean limitBlock);

	int updateBlockLimit(long custId, long headerId, boolean blockLimit);

	List<String> getLimitRuleFields();

	Customer getLimitFieldsByCustId(long custId, Set<String> ruleFields);

	List<FinanceMain> getLimitFieldsByCustId(long custId, Set<String> ruleFields, boolean orgination);

	List<FinanceMain> getInstitutionLimitFields(Set<String> ruleFields, String whereClause, boolean orgination);

	FinanceType getLimitFieldsByFinTpe(String finType, Set<String> fields);

	boolean isDuplicateKey(String ruleCode, String limitStructureCode, TableType tableType);
}
