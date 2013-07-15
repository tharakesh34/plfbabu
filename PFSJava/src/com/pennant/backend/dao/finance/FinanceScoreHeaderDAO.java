package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;

public interface FinanceScoreHeaderDAO {
	
	public List<FinanceScoreHeader> getFinScoreHeaderList(String finReference, String type);
	public long saveHeader(FinanceScoreHeader scoreHeader, String type);
	public void deleteHeaderList(String finReferecne, String type);
	
	public List<FinanceScoreDetail> getFinScoreDetailList(List<Long> headerIds, String type);
	public void saveDetailList(List<FinanceScoreDetail> scoreDetails, String type);
	public void deleteDetailList(String finReferecne, String type);

}
