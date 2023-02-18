package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.model.customermasters.CustomerCoreBank;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.service.finance.impl.ManualAdviceUtil;
import com.pennant.backend.util.PennantConstants;

public class FinOverDueService {

	private ManualAdviseDAO manualAdviseDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private CustomerDAO customerDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private FinanceMainDAO financeMainDAO;

	public BigDecimal getDueAgnistLoan(long finID) {
		BigDecimal totalDue = BigDecimal.ZERO;
		BigDecimal advDue = BigDecimal.ZERO;

		BigDecimal pftDue = financeProfitDetailDAO.getOverDueAmount(finID);
		BigDecimal odDue = finODDetailsDAO.getOverDueAmount(finID);
		List<ManualAdvise> advList = manualAdviseDAO.getReceivableAdvises(finID);

		for (ManualAdvise ma : advList) {

			if (ma.getStatus() == null || PennantConstants.MANUALADVISE_MAINTAIN.equals(ma.getStatus())) {
				BigDecimal bal = ma.getAdviseAmount().subtract(ma.getPaidAmount()).subtract(ma.getWaivedAmount());
				if (bal.compareTo(BigDecimal.ZERO) > 0) {
					ManualAdviceUtil.calculateBalanceAmt(ma);
					advDue = advDue.add(ma.getBalanceAmt());
				}
			}
		}

		totalDue = pftDue.add(odDue).add(advDue);

		return totalDue;
	}

	public BigDecimal getDueAgnistCustomer(long finID) {
		return getDueAgnistCustomer(finID, true);
	}

	public BigDecimal getDueAgnistCustomer(long finID, boolean isInclude) {
		BigDecimal totalDue = BigDecimal.ZERO;
		CustomerCoreBank customerCoreBank = customerDAO.getCoreBankByFinID(finID);

		List<Long> reqFinIds = financeMainDAO.getFinIDsByCustomer(customerCoreBank);

		for (Long reqFinId : reqFinIds) {
			if (!(isInclude || !isInclude(reqFinId, finID))) {
				continue;
			}

			totalDue = totalDue.add(getDueAgnistLoan(reqFinId));
		}

		return totalDue;
	}

	private boolean isInclude(long reqfinId, long finID) {
		return reqfinId == finID;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

}
