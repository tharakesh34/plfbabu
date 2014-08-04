package com.pennant.backend.dao.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinStatusDetail;

public interface FinStatusDetailDAO {
	public void save(FinStatusDetail finStatusDetail);
	public List<FinStatusDetail> getFinStatusDetailList(Date valueDate);
	public void saveOrUpdateFinStatus(FinStatusDetail finStatusDetail);
	public void updateCustStatuses(List<FinStatusDetail> custStatuses);
}
