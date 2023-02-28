package com.pennanttech.pff.holdrelease;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.holdrefund.dao.HoldRefundUploadDAO;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class FinanceHoldReleaseProcess {
	private static final Logger logger = LogManager.getLogger(FinanceHoldReleaseProcess.class);

	private HoldRefundUploadDAO holdRefundUploadDAO;

	public void releaseHoldProcess(Date appdate) {
		logger.debug(Literal.ENTERING);

		int nDays = SysParamUtil.getValueAsInt(SMTParameterConstants.REMOVE_HOLD_FLAG_N_DAYS_CLOSED_LAN) - 1;

		Date maxClosedDate = DateUtil.addDays(appdate, -nDays);

		holdRefundUploadDAO.releaseHoldOnLoans(maxClosedDate);

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setHoldRefundUploadDAO(HoldRefundUploadDAO holdRefundUploadDAO) {
		this.holdRefundUploadDAO = holdRefundUploadDAO;
	}

}
