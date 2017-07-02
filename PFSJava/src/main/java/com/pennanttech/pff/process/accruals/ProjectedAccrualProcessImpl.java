package com.pennanttech.pff.process.accruals;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.core.AccrualService;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennanttech.pff.core.process.ProjectedAccrualProcess;

public class ProjectedAccrualProcessImpl implements ProjectedAccrualProcess {
	
	@Autowired
	FinanceScheduleDetailDAO financeScheduleDetailDAO;

	@Override
	public List<ProjectedAccrual> calculateAccrualsOnMonthEnd(Object... params) throws Exception {
		String finReference = (String)params[0];
		Date startDate = (Date)params[1];
		Date maturityDate = (Date)params[2];
		Date monthEndDate = (Date)params[3];
		
		FinanceMain finMain = new FinanceMain();
		
		finMain.setFinReference(finReference);
		finMain.setFinStartDate(startDate);
		finMain.setMaturityDate(maturityDate);
		
		List<FinanceScheduleDetail> schdDetails = financeScheduleDetailDAO.getFinSchdDetailsForBatch(finReference);
		
		return AccrualService.calAccrualsOnMonthEnd(finMain, schdDetails, monthEndDate);
	}

}
