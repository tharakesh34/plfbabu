package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.service.reports.CashFlowService;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class CashFlowPreperation implements Tasklet {
	private Logger logger = LogManager.getLogger(CashFlowPreperation.class);

	private CashFlowService cashFlowService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		// Return if CashFlowReport is not required
		if (!SysParamUtil.isAllowed(SMTParameterConstants.ALW_CASHFLOW_REPORT)) {
			return RepeatStatus.FINISHED;
		}
		try {
			Date eodDate = SysParamUtil.getLastBusinessdate();
			Date monthEnd = DateUtil.getMonthEnd(eodDate);
			if (DateUtil.compare(eodDate, monthEnd) == 0) {
				getCashFlowService().processCashFlowDetails();
			} else {
				return RepeatStatus.FINISHED;
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + " While executing CashFlow Report");
		}
		return RepeatStatus.FINISHED;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public CashFlowService getCashFlowService() {
		return cashFlowService;
	}

	public void setCashFlowService(CashFlowService cashFlowService) {
		this.cashFlowService = cashFlowService;
	}
}
