package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.financeservice.AddDisbursementService;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.OverdraftScheduleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantStaticListUtil;

public class AddDisbursementServiceImpl extends GenericService<FinServiceInstruction> implements AddDisbursementService {
	private static Logger logger = Logger.getLogger(AddDisbursementServiceImpl.class);

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	
	/**
	 * Method for perform add disbursement action
	 * 
	 * @param finScheduleData
	 * @param amount
	 * @param addFeeFinance
	 * @param alwAssetUtilize
	 * 
	 *@return FinScheduleData 
	 */
	public FinScheduleData getAddDisbDetails(FinScheduleData finScheduleData, BigDecimal amount,
			BigDecimal addFeeFinance, boolean alwAssetUtilize) {
		logger.debug("Entering");

		FinScheduleData finSchData = null;
		
		if(finScheduleData.getFinanceScheduleDetails().size()>0){
			for(FinanceScheduleDetail finSchd:finScheduleData.getFinanceScheduleDetails()){
				finSchd.setSchdMethod(finScheduleData.getFinanceMain().getScheduleMethod());
			}
		}
		finSchData = ScheduleCalculator.addDisbursement(finScheduleData, amount, addFeeFinance, alwAssetUtilize);

		logger.debug("Leaving");

		return finSchData;
	}

	/**
	 * Validate Add disbursement instructions
	 * 
	 * @param finServiceInstruction
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doValidations(FinanceDetail financeDetail, FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		AuditDetail auditDetail = new AuditDetail();
		String lang = "EN";

		// validate Instruction details
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		Date fromDate = finServiceInstruction.getFromDate();
		BigDecimal disbAmount = finServiceInstruction.getAmount();

		// It shouldn't be past date when compare to appdate
		if (fromDate.compareTo(DateUtility.getAppDate()) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "From Date:" + DateUtility.formatToShortDate(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111", "", valueParm), lang));
		}

		// validate from date
		if (finServiceInstruction.getFromDate().compareTo(financeMain.getMaturityDate()) > 0) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(finServiceInstruction.getFromDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91101", "", valueParm), lang));
		}

		// validate disb amount
		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			isOverdraft = true;
		}

		if (isOverdraft) {
			List<OverdraftScheduleDetail> odSchdDetail = finScheduleData.getOverdraftScheduleDetails();
			BigDecimal availableLimit = BigDecimal.ZERO;
			if (odSchdDetail != null && odSchdDetail.size() > 0) {
				for (int i = 0; i < odSchdDetail.size(); i++) {
					if (odSchdDetail.get(i).getDroplineDate().compareTo(fromDate) > 0) {
						break;
					}
					availableLimit = odSchdDetail.get(i).getODLimit();
				}
			} else {
				availableLimit = financeMain.getFinAssetValue();
			}

			// Schedule Outstanding amount calculation
			List<FinanceScheduleDetail> schList = finScheduleData.getFinanceScheduleDetails();
			BigDecimal closingbal = BigDecimal.ZERO;
			for (int i = 0; i < schList.size(); i++) {
				if (DateUtility.compare(schList.get(i).getSchDate(), fromDate) > 0) {
					break;
				}
				closingbal = schList.get(i).getClosingBalance();
			}

			// Actual Available Limit
			availableLimit = availableLimit.subtract(closingbal);

			// Validating against Available Limit amount
			if (disbAmount.compareTo(availableLimit) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = String.valueOf(disbAmount);
				valueParm[1] = String.valueOf(availableLimit);
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91119", "", valueParm), lang));
			} else {
				// Checking Total Disbursed amount validate against New disbursement
				BigDecimal totDisbAmount = BigDecimal.ZERO;
				for (FinanceDisbursement finDisbursment : finScheduleData.getDisbursementDetails()) {
					totDisbAmount = totDisbAmount.add(finDisbursment.getDisbAmount());
				}
				totDisbAmount = disbAmount.add(totDisbAmount);
				if (totDisbAmount.compareTo(financeMain.getFinAssetValue()) > 0) {
					String[] valueParm = new String[2];
					valueParm[0] = String.valueOf(totDisbAmount);
					valueParm[1] = String.valueOf(financeMain.getFinAssetValue());
					auditDetail
					.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91120", "", valueParm), lang));
				}
			}
		}

		// validate recalType
		if (StringUtils.isNotBlank(finServiceInstruction.getRecalType())) {
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
		if (StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
				|| StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
			if (finServiceInstruction.getRecalFromDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91105", "", valueParm), lang));
				return auditDetail;
			} else {
				// It should be valid schedule date
				String finReference = financeMain.getFinReference();
				boolean wif = finServiceInstruction.isWif();
				Date recalFromDate = finServiceInstruction.getRecalFromDate();
				boolean isExists = financeScheduleDetailDAO.getFinScheduleCountByDate(finReference, recalFromDate, wif);
				if(!isExists) {
					String[] valueParm = new String[1];
					valueParm[0] = "Recal From Date:"+DateUtility.formatToShortDate(recalFromDate);
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111", "", valueParm), lang));
				}
			}
		 if (finServiceInstruction.getRecalFromDate().compareTo(financeMain.getMaturityDate()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
				valueParm[1] = DateUtility.formatToShortDate(financeMain.getMaturityDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91114", "", valueParm), lang));
			} else if (finServiceInstruction.getRecalFromDate().compareTo(finServiceInstruction.getFromDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "RecalFromDate";
				valueParm[1] = "from date";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91121", "", valueParm), lang));
				return auditDetail;
			}
		}

		// validate reCalToDate
		if (StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
			if (finServiceInstruction.getRecalToDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91108", "", valueParm), lang));
				return auditDetail;
			} else {
				String finReference = financeMain.getFinReference();
				boolean wif = finServiceInstruction.isWif();
				Date recalToDate = finServiceInstruction.getRecalToDate();
				boolean isExists = financeScheduleDetailDAO.getFinScheduleCountByDate(finReference, recalToDate, wif);
				if(!isExists) {
					String[] valueParm = new String[1];
					valueParm[0] = "Recal To Date:"+DateUtility.formatToShortDate(recalToDate);
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111", "", valueParm), lang));
				}
			}
			
			if (finServiceInstruction.getRecalToDate().compareTo(finServiceInstruction.getRecalFromDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finServiceInstruction.getRecalToDate());
				valueParm[1] = DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91109", "", valueParm), lang));
			}
		}

		// terms
		if (StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_ADDTERM)
				|| StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
			if (finServiceInstruction.getTerms() <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "terms";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm), lang));
			}
		} else {
			if (finServiceInstruction.getTerms() > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = CalculationConstants.RPYCHG_ADDTERM;
				valueParm[1] = CalculationConstants.RPYCHG_ADDRECAL;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91118", "", valueParm), lang));
			}
		}

		logger.debug("Leaving");
		return auditDetail;
	}
	
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}
}
