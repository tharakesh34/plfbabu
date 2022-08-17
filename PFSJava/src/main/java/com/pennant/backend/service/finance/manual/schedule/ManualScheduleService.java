package com.pennant.backend.service.finance.manual.schedule;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.manual.schedule.ManualScheduleHeader;
import com.pennanttech.pff.core.TableType;

public interface ManualScheduleService {

	boolean isFileNameExist(String fileName);

	ManualScheduleHeader getManualScheduleDetails(long finID, String finEvent, TableType tableType);

	void saveOrUpdate(FinanceDetail financeDetail, String moduleDefiner, TableType tableType);

	void doApprove(FinanceDetail financeDetail);

	void doReject(FinanceDetail financeDetail);
}
