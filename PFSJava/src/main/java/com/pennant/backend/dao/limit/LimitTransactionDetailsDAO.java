package com.pennant.backend.dao.limit;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.limit.LimitTransactionDetail;

public interface LimitTransactionDetailsDAO {

	long save(LimitTransactionDetail limitTransactionDetail);

	LimitTransactionDetail getTransaction(String referenceCode, String referenceNumber, String tranType,
			long headerId,int schSeq);

	List<LimitTransactionDetail> getLimitTranDetails(String code, String ref,long headerId);

	void delete(long transactionId);

	long saveLimitRuleTransactiondetails(LimitTransactionDetail limitTranDetail, String string);

	BigDecimal getUtilizedSumByRulecode(String ruleCode, String limitLine, String type);

	void deleteAllRuleTransactions(String type);

}
