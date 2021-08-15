package com.pennant.backend.dao.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinLogEntryDetail;

public interface FinLogEntryDetailDAO {

	long save(FinLogEntryDetail entryDetail);

	void updateLogEntryStatus(FinLogEntryDetail finLogEntryDetail);

	List<FinLogEntryDetail> getFinLogEntryDetailList(long finID, long logKey);

	FinLogEntryDetail getFinLogEntryDetailByLog(long logKey);

	Date getMaxPostDate(long finID);

	long getPrevSchedLogKey(long finID, Date date);

	Date getMaxPostDateByRef(long finID);

	FinLogEntryDetail getFinLogEntryDetail(long finID);
}
