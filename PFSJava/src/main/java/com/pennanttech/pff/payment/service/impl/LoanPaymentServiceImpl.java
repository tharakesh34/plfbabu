package com.pennanttech.pff.payment.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennanttech.pff.payment.model.LoanPayment;
import com.pennanttech.pff.payment.service.LoanPaymentService;

public class LoanPaymentServiceImpl implements LoanPaymentService {
	private FinODDetailsDAO finODDetailsDAO;
	private ManualAdviseDAO manualAdviseDAO;

	@Override
	public boolean isSchdFullyPaid(LoanPayment loanPayment) {
		long finID = loanPayment.getFinID();
		List<FinanceScheduleDetail> schedules = loanPayment.getSchedules();
		Date valueDate = loanPayment.getValueDate();

		boolean fullyPaid = true;
		for (FinanceScheduleDetail curSchd : schedules) {
			// Profit
			if ((curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}

			// Principal
			if ((curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}

			// Fees
			if ((curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}
		}

		// Check Penalty Paid Fully or not
		if (fullyPaid) {
			FinODDetails overdue = finODDetailsDAO.getTotals(finID);
			if (overdue != null) {
				BigDecimal balPenalty = overdue.getTotPenaltyAmt().subtract(overdue.getTotPenaltyPaid())
						.subtract(overdue.getTotWaived())
						.add(overdue.getLPIAmt().subtract(overdue.getLPIPaid()).subtract(overdue.getLPIWaived()));

				// Penalty Not fully Paid
				if (balPenalty.compareTo(BigDecimal.ZERO) > 0) {
					fullyPaid = false;
				}
			}
		}

		// Check Receivable Advises paid Fully or not
		if (fullyPaid) {
			BigDecimal adviseBal = manualAdviseDAO.getBalanceAmt(finID, valueDate);

			// Penalty Not fully Paid
			if (adviseBal != null && adviseBal.compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
			}
		}

		return fullyPaid;
	}

	@Autowired
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}
}
