package com.pennant.backend.dao.finance;

import com.pennant.backend.model.finance.FinODPenaltyRate;

public interface FinODPenaltyRateDAO {

	public FinODPenaltyRate getFinODPenaltyRateByRef(String finReference,String type);
	public void update(FinODPenaltyRate penaltyRate,String type);
	public void delete(String finReference,String type);
	public String save(FinODPenaltyRate penaltyRate,String type);
	public void initialize(FinODPenaltyRate finODPenaltyRate);
	public void refresh(FinODPenaltyRate entity);

}
