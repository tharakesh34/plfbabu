package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.RolledoverFinanceDetail;
import com.pennant.backend.model.finance.RolledoverFinanceHeader;

public interface RolledoverFinanceDAO {
	
	RolledoverFinanceHeader getRolledoverFinanceHeader(String finReference, String type);
	List<RolledoverFinanceDetail> getRolledoverDetailList(String finReference, String type);

	void saveHeader(RolledoverFinanceHeader header, String type);
	void deleteHeader(String finReference, String type);
	void updateHeader(RolledoverFinanceHeader header, String type);
	
	void saveDetailList(List<RolledoverFinanceDetail> details, String type);
	void deleteListByRef(String finreference, String type);
}
