package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinLogEntryDetail;

public interface FinLogEntryDetailDAO {
	
	long save(FinLogEntryDetail entryDetail);
	void updateLogEntryStatus(FinLogEntryDetail finLogEntryDetail);
	List<FinLogEntryDetail> getFinLogEntryDetailList(String finReference, long logKey);
	FinLogEntryDetail getFinLogEntryDetail(long logKey);
}
