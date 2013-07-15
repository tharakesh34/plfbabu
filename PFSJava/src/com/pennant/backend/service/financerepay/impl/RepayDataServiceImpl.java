package com.pennant.backend.service.financerepay.impl;

import java.math.BigDecimal;

import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.financerepay.RepayDataService;

public class RepayDataServiceImpl extends GenericService<RepayData> implements RepayDataService{
	private OverdueChargeRecoveryDAO overdueChargeRecoveryDAO;
	
	@Override
	public BigDecimal getRepayData(String finReference) {
		return overdueChargeRecoveryDAO.getPendingODCAmount(finReference);
	}

	public OverdueChargeRecoveryDAO getOverdueChargeRecoveryDAO() {
		return overdueChargeRecoveryDAO;
	}
	public void setOverdueChargeRecoveryDAO(OverdueChargeRecoveryDAO overdueChargeRecoveryDAO) {
		this.overdueChargeRecoveryDAO = overdueChargeRecoveryDAO;
	}

}
