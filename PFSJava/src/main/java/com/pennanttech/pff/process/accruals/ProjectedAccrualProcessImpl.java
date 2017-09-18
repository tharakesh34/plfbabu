package com.pennanttech.pff.process.accruals;

import java.util.ArrayList;
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
		String finReference = (String) params[0];
		Date startDate = (Date) params[1];
		Date maturityDate = (Date) params[2];
		Date appDate = (Date) params[3];
		List<ProjectedAccrual> list = (List) params[4];
		List<FinanceScheduleDetail> schList = new ArrayList<FinanceScheduleDetail>(list.size());

		FinanceMain finMain = new FinanceMain();
		finMain.setFinReference(finReference);
		finMain.setFinStartDate(startDate);
		finMain.setMaturityDate(maturityDate);

		for (ProjectedAccrual item : list) {
			FinanceScheduleDetail schedule = new FinanceScheduleDetail();
			schedule.setFinReference(item.getFinReference());
			schedule.setSchDate(item.getSchdDate());
			schedule.setSchSeq(item.getSchSeq());
			schedule.setPftOnSchDate(item.isPftOnSchDate());
			schedule.setCpzOnSchDate(item.isCpzOnSchDate());
			schedule.setRepayOnSchDate(item.isRepayOnSchDate());
			schedule.setRvwOnSchDate(item.isRvwOnSchDate());
			schedule.setBalanceForPftCal(item.getBalanceForPftCal());
			schedule.setCalculatedRate(item.getCalculatedRate());
			schedule.setNoOfDays(item.getNoOfDays());
			schedule.setProfitCalc(item.getProfitCalc());
			schedule.setProfitSchd(item.getProfitSchd());
			schedule.setPrincipalSchd(item.getPrincipalSchd());
			schedule.setDisbAmount(item.getDisbAmount());
			schedule.setDownPaymentAmount(item.getDownPaymentAmount());
			schedule.setCpzAmount(item.getCpzAmount());
			schedule.setFeeChargeAmt(item.getFeeChargeAmt());
			schedule.setSchdPriPaid(item.getSchdPriPaid());
			schedule.setSchdPftPaid(item.getSchdPftPaid());
			schedule.setSchPftPaid(item.isSchPftPaid());
			schedule.setSchPriPaid(item.isSchPriPaid());
			schedule.setSpecifier(item.getSpecifier());
			schList.add(schedule);

		}

		return AccrualService.calAccrualsOnMonthEnd(finMain, schList, appDate);
	}

}
