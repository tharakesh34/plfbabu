package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;

public interface FinanceScoreHeaderDAO {
	
	List<FinanceScoreHeader> getFinScoreHeaderList(String finReference, String type);
	long saveHeader(FinanceScoreHeader scoreHeader, String type);
	void deleteHeader(FinanceScoreHeader scoreHeader, String type);
	void deleteHeaderList(String finReferecne, String type);
	List<FinanceScoreDetail> getFinScoreDetailList(List<Long> headerIds, String type);
	void saveDetailList(List<FinanceScoreDetail> scoreDetails, String type);
	void deleteDetailList(List<FinanceScoreDetail> scoreDetails, String type);
	void deleteDetailList(String finReferecne, String type);
}
