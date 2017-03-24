package com.pennant.backend.financeservice.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.financeservice.AddRepaymentService;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantStaticListUtil;

public class AddRepaymentServiceImpl extends GenericService<FinServiceInstruction> implements AddRepaymentService {
	private static Logger logger = Logger.getLogger(AddRepaymentServiceImpl.class);

	private FinanceMainDAO financeMainDAO;

	public AddRepaymentServiceImpl(){
		super();
	}
	
	/**
	 * Method for do AddRepayment process and generate schedule
	 * 
	 * @param finscheduleData
	 * @param finServiceInstruction
	 * @return FinScheduleData
	 */
	public FinScheduleData getAddRepaymentDetails(FinScheduleData finscheduleData, FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		FinScheduleData finSchdData = null;
		finSchdData = ScheduleCalculator.changeRepay(finscheduleData, finServiceInstruction.getAmount(), 
				finServiceInstruction.getSchdMethod());

		logger.debug("Leaving");

		return finSchdData;

	}

	/**
	 * Validate change repayment service instructions.
	 * 
	 * @param finServiceInstruction
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doValidations(FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");
		
		AuditDetail auditDetail = new AuditDetail();
		String lang = "EN";
		
		// validate Instruction details
		boolean isWIF = finServiceInstruction.isWif();
		String finReference = finServiceInstruction.getFinReference();
		
		FinanceMain financeMain = financeMainDAO.getFinanceMainById(finReference, "", isWIF);
		
		// validate from date
		if(finServiceInstruction.getFromDate().compareTo(financeMain.getMaturityDate()) >= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(finServiceInstruction.getFromDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91101", "", valueParm), lang));
		}
		
		// validate to date
		if(finServiceInstruction.getToDate().compareTo(financeMain.getMaturityDate()) > 0 
				|| finServiceInstruction.getToDate().compareTo(finServiceInstruction.getFromDate()) < 0) {
			
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finServiceInstruction.getFromDate());
			valueParm[1] = DateUtility.formatToShortDate(financeMain.getMaturityDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91102", "", valueParm), lang));
		}
		
		// validate schdMethod
		if (StringUtils.isNotBlank(finServiceInstruction.getSchdMethod())) {
			List<ValueLabel> schdMethods = PennantStaticListUtil.getScheduleMethods();
			boolean schdMethodSts = false;
			for (ValueLabel value : schdMethods) {
				if (StringUtils.equals(value.getValue(), finServiceInstruction.getSchdMethod())) {
					schdMethodSts = true;
					break;
				}
			}
			if (!schdMethodSts) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getSchdMethod();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91110", "", valueParm), lang));
			}
		}
		
		// validate recalType
		if(StringUtils.isNotBlank(finServiceInstruction.getRecalType())) {
			List<ValueLabel> recalTypes = PennantStaticListUtil.getSchCalCodes();
			boolean recalTypeSts = false;
			for (ValueLabel value : recalTypes) {
				if (StringUtils.equals(value.getValue(), finServiceInstruction.getRecalType())) {
					recalTypeSts = true;
					break;
				}
			}
			if (!recalTypeSts) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91104", "", valueParm), lang));
			}
		}
		
		// validate reCalFromDate
		if(StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLMDT) 
				|| StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
			if(finServiceInstruction.getRecalFromDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91105", "", valueParm), lang));
			} else if(finServiceInstruction.getRecalFromDate().compareTo(finServiceInstruction.getToDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
				valueParm[1] = DateUtility.formatToShortDate(finServiceInstruction.getToDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91106", "", valueParm), lang));
			} else if(finServiceInstruction.getRecalFromDate().compareTo(financeMain.getMaturityDate()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
				valueParm[1] = DateUtility.formatToShortDate(financeMain.getMaturityDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91114", "", valueParm), lang));
			}
		} else if(StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
			if(finServiceInstruction.getTerms() <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Number of Terms";
				valueParm[1] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91112", "", valueParm), lang));
			}
		}
		
		// validate reCalToDate
		if(StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
			if(finServiceInstruction.getRecalToDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91108", "", valueParm), lang));
			} else if(finServiceInstruction.getRecalToDate().compareTo(finServiceInstruction.getRecalFromDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finServiceInstruction.getRecalToDate());
				valueParm[1] = DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91109", "", valueParm), lang));
			}
		}
		
		logger.debug("Leaving");
		return auditDetail;
	}
	
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}
