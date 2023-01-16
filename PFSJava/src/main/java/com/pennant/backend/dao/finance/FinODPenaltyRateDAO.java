package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinLPIRateChange;
import com.pennant.backend.model.finance.FinODPenaltyRate;

public interface FinODPenaltyRateDAO {

	FinODPenaltyRate getFinODPenaltyRateByRef(long finID, String type);

	void update(FinODPenaltyRate penaltyRate, String type);

	void delete(long finID, String type);

	String save(FinODPenaltyRate penaltyRate, String type);

	void saveLog(FinODPenaltyRate finODPenaltyRate, String type);

	FinODPenaltyRate getDMFinODPenaltyRateByRef(long finID, String type);

	FinODPenaltyRate getFinODPenaltyRateForLMSEvent(long finID);

	int getExtnODGrcDays(long finID);

	List<FinLPIRateChange> getFinLPIRateChanges(long finID);
}
