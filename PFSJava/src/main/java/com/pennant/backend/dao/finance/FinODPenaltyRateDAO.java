package com.pennant.backend.dao.finance;

import com.pennant.backend.model.finance.FinODPenaltyRate;

public interface FinODPenaltyRateDAO {

	FinODPenaltyRate getFinODPenaltyRateByRef(String finReference,String type);
	void update(FinODPenaltyRate penaltyRate,String type);
	void delete(String finReference,String type);
	String save(FinODPenaltyRate penaltyRate,String type);
	void saveLog(FinODPenaltyRate finODPenaltyRate, String type);
}
