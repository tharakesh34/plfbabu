package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.financeservice.RateChangeService;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantStaticListUtil;

public class RateChangeServiceImpl extends GenericService<FinServiceInstruction> implements RateChangeService {
	private final static Logger logger = Logger.getLogger(RateChangeServiceImpl.class);
	
	private FinanceMainDAO financeMainDAO;

	/**
	 * Method for perform the Rate change action
	 * 
	 * @param finScheduleData
	 * @param finServiceInst
	 */
	public FinScheduleData getRateChangeDetails(FinScheduleData finScheduleData, FinServiceInstruction finServiceInst) {
		logger.debug("Entering");

		if (finScheduleData != null && finServiceInst != null) {
			FinanceMain financeMain = finScheduleData.getFinanceMain();

			// Profit Days Basis Setting for Calculation Process
			List<FinanceScheduleDetail> schDetailList = finScheduleData.getFinanceScheduleDetails();
			for (FinanceScheduleDetail curSchd : schDetailList) {
				if (curSchd.getSchDate().compareTo(financeMain.getEventFromDate()) >= 0
						&& curSchd.getSchDate().compareTo(financeMain.getEventToDate()) < 0) {
					curSchd.setPftDaysBasis(finServiceInst.getPftDaysBasis());
				}
			}
			FinScheduleData finSchData = null;
			finSchData = ScheduleCalculator.changeRate(finScheduleData, finServiceInst.getBaseRate(), finServiceInst.getSplRate(),
					finServiceInst.getMargin() == null ? BigDecimal.ZERO : finServiceInst.getMargin(),
							finServiceInst.getActualRate() == null ? BigDecimal.ZERO : finServiceInst.getActualRate(), true);

			return finSchData;
		}

		logger.debug("Leaving");

		return new FinScheduleData();
	}

	/**
	 * Validate service instruction data against addratechange service.
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
		
		// validate from date with finStart date
		if(finServiceInstruction.getFromDate().compareTo(financeMain.getFinStartDate()) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Finance start date:"+DateUtility.formatToShortDate(financeMain.getFinStartDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91101", "", valueParm), lang));
		}
		
		// validate from date with maturity date
		if(finServiceInstruction.getFromDate().compareTo(financeMain.getMaturityDate()) >= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(financeMain.getMaturityDate());
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
		
		// validate profit days basis
		if (StringUtils.isNotBlank(finServiceInstruction.getPftDaysBasis())) {
			List<ValueLabel> profitDayBasis = PennantStaticListUtil.getProfitDaysBasis();
			boolean profitDaysSts = false;
			for (ValueLabel value : profitDayBasis) {
				if (StringUtils.equals(value.getValue(), finServiceInstruction.getPftDaysBasis())) {
					profitDaysSts = true;
					break;
				}
			}
			if (!profitDaysSts) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getPftDaysBasis();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91103", "", valueParm), lang));
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
		if(StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)) {
			if(finServiceInstruction.getRecalFromDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91105", "", valueParm), lang));
			} else if(finServiceInstruction.getRecalFromDate().compareTo(finServiceInstruction.getToDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
				valueParm[1] = DateUtility.formatToShortDate(finServiceInstruction.getToDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91106", "", valueParm), lang));
			}
		}
		
		logger.debug("Leaving");
		return auditDetail;
	}
	
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}
