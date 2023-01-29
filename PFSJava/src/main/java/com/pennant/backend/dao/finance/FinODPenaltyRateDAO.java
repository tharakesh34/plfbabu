package com.pennant.backend.dao.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinLPIRateChange;
import com.pennant.backend.model.finance.FinODPenaltyRate;

public interface FinODPenaltyRateDAO {

	List<FinODPenaltyRate> getFinODPenaltyRateByRef(long finID, String type);

	FinODPenaltyRate getEffectivePenaltyRate(long finID, String type);

	void update(FinODPenaltyRate penaltyRate, String type);

	void delete(long finID, Date finEffectiveDate, String type);

	String save(FinODPenaltyRate penaltyRate, String type);

	void saveLog(FinODPenaltyRate finODPenaltyRate, String type);

	List<FinODPenaltyRate> getDMFinODPenaltyRateByRef(long finID, String type);

	List<FinODPenaltyRate> getFinODPenaltyRateForLMSEvent(long finID);

	int getExtnODGrcDays(long finID);

	List<FinLPIRateChange> getFinLPIRateChanges(long finID);
}
