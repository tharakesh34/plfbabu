package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.OverdraftMovements;
import com.pennant.backend.model.finance.OverdraftScheduleDetail;

public interface OverdraftScheduleDetailDAO {

	void saveList(List<OverdraftScheduleDetail> overdraftScheduleDetail, String type);

	void saveOverdraftMovement(OverdraftMovements overdraftMovements);

	void deleteByFinReference(long finID, String type, boolean isWIF);

	List<OverdraftScheduleDetail> getOverdraftScheduleDetails(long finID, String type, boolean isWIF);

	List<OverdraftScheduleDetail> getOverdraftScheduleForLMSEvent(long finID);

}
