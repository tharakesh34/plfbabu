package com.pennant.backend.dao.finance.manual.schedule;

import java.util.List;

import com.pennant.backend.model.finance.manual.schedule.ManualScheduleDetail;
import com.pennant.backend.model.finance.manual.schedule.ManualScheduleHeader;
import com.pennanttech.pff.core.TableType;

public interface ManualScheduleDAO {

	boolean isFileNameExist(String fileName, String type);

	long saveHeaderDetails(ManualScheduleHeader scheduleHeader, String tableType);

	ManualScheduleHeader getManualSchdHeader(long finID, String finEvent, String tableType);

	List<ManualScheduleDetail> getManualSchdDetailsById(long uploadId, String tableType);

	void delete(ManualScheduleHeader uploadManualSchdHeader, TableType tableType);

	void deleteById(long uploadId, TableType tableType);

	void saveManualSchdDetails(List<ManualScheduleDetail> details, String tableType);

}
