package com.pennant.backend.dao.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinLogEntryDetail;

public interface FinLogEntryDetailDAO {
	public long save(FinLogEntryDetail entryDetail);
	public List<FinLogEntryDetail> getFinLogEntryDetailList(String finReference , Date postDate);
	public FinLogEntryDetail getFinLogEntryDetail(String finReference, String event, Date postDate);
	public void updateLogEntryStatus(FinLogEntryDetail finLogEntryDetail);
}
