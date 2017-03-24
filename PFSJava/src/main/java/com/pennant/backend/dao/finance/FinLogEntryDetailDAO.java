package com.pennant.backend.dao.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinLogEntryDetail;

public interface FinLogEntryDetailDAO {
	
	long save(FinLogEntryDetail entryDetail);
	List<FinLogEntryDetail> getFinLogEntryDetailList(String finReference , Date postDate);
	FinLogEntryDetail getFinLogEntryDetail(String finReference, String event, Date postDate);
	void updateLogEntryStatus(FinLogEntryDetail finLogEntryDetail);
}
