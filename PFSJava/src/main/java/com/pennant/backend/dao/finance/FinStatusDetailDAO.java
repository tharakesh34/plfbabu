package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinStatusDetail;

public interface FinStatusDetailDAO {

	void save(FinStatusDetail finStatusDetail);

	void saveOrUpdateFinStatus(FinStatusDetail finStatusDetail);

	void updateCustStatuses(List<FinStatusDetail> custStatuses);

	public List<FinStatusDetail> getFinStatusDetailByRefId(long finID);
}
