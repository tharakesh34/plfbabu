package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.CashBackDetail;

public interface CashBackDetailDAO {

	public void save(List<CashBackDetail> cashBackDetail);

	public List<CashBackDetail> getCashBackDetails();

	public CashBackDetail getManualAdviseIdByFinReference(long finID, String type);

	public int updateCashBackDetail(long adviseId);

}
