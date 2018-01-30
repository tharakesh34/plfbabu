package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.financeservice.AddTermsService;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.GenericService;

public class AddTermsServiceImpl extends GenericService<FinServiceInstruction> implements AddTermsService {

	private static Logger logger = Logger.getLogger(AddTermsServiceImpl.class);

	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;


	public FinScheduleData getAddTermsDetails(FinScheduleData finscheduleData,FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");
		
		finscheduleData = ScheduleCalculator.addTerm(finscheduleData,finServiceInstruction.getTerms());

		finscheduleData.getFinanceMain().setScheduleRegenerated(true);
		logger.debug("Leaving");
		return finscheduleData;
	}

	/**
	 * 
	 * 
	 */
	@Override
	public AuditDetail doValidations(FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");
		
		AuditDetail auditDetail = new AuditDetail();
		String lang = "EN";
		
		// validate Instruction details
		boolean isWIF = finServiceInstruction.isWif();
		String finReference = finServiceInstruction.getFinReference();
		
		if(DateUtility.compare(finServiceInstruction.getRecalFromDate(), DateUtility.getAppDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "Recal From Date";
			valueParm[1] = "application date:"+DateUtility.formatToLongDate(DateUtility.getAppDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30512", "", valueParm), lang));
			return auditDetail;
		}
		
		if(finServiceInstruction.getRecalFromDate() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "Recal From Date";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
			return auditDetail;
		}
		if(finServiceInstruction.getTerms() <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "Terms";
			valueParm[1] = String.valueOf(BigDecimal.ZERO);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm)));
			return auditDetail;
		}
		
		boolean isValidRecalFromDate = false;
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finReference, "", isWIF);
		if(schedules != null) {
			for (FinanceScheduleDetail schDetail : schedules) {
				// FromDate
				if (DateUtility.compare(finServiceInstruction.getRecalFromDate(), schDetail.getSchDate()) == 0) {
					isValidRecalFromDate = true;
				}
				// RecalFromDate
				if (DateUtility.compare(finServiceInstruction.getRecalFromDate(), schDetail.getSchDate()) == 0) {
					isValidRecalFromDate = true;
					if (checkIsValidRepayDate(auditDetail, schDetail, "RecalFromDate") != null) {
						return auditDetail;
					}
				}
			}

			if(!isValidRecalFromDate) {
				String[] valueParm = new String[1];
				valueParm[0] = "RecalFromDate:"+DateUtility.formatToShortDate(finServiceInstruction.getFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
			}
		}
		
		logger.debug("Leaving");
		return auditDetail;
	}
	
	/**
	 * Method for validate current schedule date is valid schedule or not
	 * 
	 * @param auditDetail
	 * @param curSchd
	 * @param label
	 * @return
	 */
	private AuditDetail checkIsValidRepayDate(AuditDetail auditDetail, FinanceScheduleDetail curSchd, String label) {
		if (!((curSchd.isRepayOnSchDate() || (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) 
				&& ((curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) >= 0 && curSchd.isRepayOnSchDate() 
				&& !curSchd.isSchPftPaid()) || (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) >= 0
				&& curSchd.isRepayOnSchDate() && !curSchd.isSchPriPaid())))) {
			String[] valueParm = new String[1];
			valueParm[0] = label;
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90261", "", valueParm)));
			return auditDetail;
		}
		return null;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}
}
