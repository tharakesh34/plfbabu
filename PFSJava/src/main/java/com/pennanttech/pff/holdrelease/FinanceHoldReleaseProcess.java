package com.pennanttech.pff.holdrelease;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinanceHoldReleaseProcess {

	private static final Logger logger = LogManager.getLogger(FinanceHoldReleaseProcess.class);

	private FinanceMainDAO financeMainDAO;

	/**
	 * Method for Processing Auto Removal Hold flag in EOD
	 */
	public void releaseHoldProcess(Date appdate) {
		logger.debug(Literal.ENTERING);

		int nDays = SysParamUtil.getValueAsInt(SMTParameterConstants.REMOVE_HOLD_FLAG_N_DAYS_CLOSED_LAN);

		Date maxClosedDate = DateUtility.addDays(appdate, -nDays);

		// Finding Loans with Hold Status & Marked as Closed(Matured/Early Settled)
		List<Long> finIds = financeMainDAO.getInactiveLoansOnHold(maxClosedDate); // Get Loans For Update

		// Marking Loan Hold Status from Hold to Release for Refund process
		if (CollectionUtils.isNotEmpty(finIds)) {
			financeMainDAO.releaseHoldOnLoans(finIds); // Updating the Loans
		}

		logger.debug(Literal.LEAVING);
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}
