package com.pennant.backend.dao.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinStatusDetail;

public interface FinStatusDetailDAO {

	void save(FinStatusDetail finStatusDetail);
	List<FinStatusDetail> getFinStatusDetailList(Date valueDate);
	void saveOrUpdateFinStatus(FinStatusDetail finStatusDetail);
	void updateCustStatuses(List<FinStatusDetail> custStatuses);
}
