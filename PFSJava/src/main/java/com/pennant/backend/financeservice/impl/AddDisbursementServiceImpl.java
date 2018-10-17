package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.financemanagement.FinanceStepDetailDAO;
import com.pennant.backend.financeservice.AddDisbursementService;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.OverdraftScheduleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.impl.FinanceDataValidation;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class AddDisbursementServiceImpl extends GenericService<FinServiceInstruction> implements AddDisbursementService {
	private static Logger logger = Logger.getLogger(AddDisbursementServiceImpl.class);

	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private FinanceStepDetailDAO		financeStepDetailDAO;
	private FinanceDataValidation		financeDataValidation;

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
			BigDecimal addFeeFinance, boolean alwAssetUtilize, String moduleDefiner) {
		logger.debug("Entering");

		FinScheduleData finSchData = null;
		
		BigDecimal oldTotalPft = finScheduleData.getFinanceMain().getTotalGrossPft();
		
		if(finScheduleData.getFinanceScheduleDetails().size()>0){
			int sdSize = finScheduleData.getFinanceScheduleDetails().size();
			FinanceScheduleDetail curSchd = null;
			for (int i = 0; i <= sdSize - 1; i++) {
				
				curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
				curSchd.setSchdMethod(finScheduleData.getFinanceMain().getScheduleMethod());
				
				// Schedule Recalculation Locking Period Applicability
				if(ImplementationConstants.ALW_SCH_RECAL_LOCK){
					if(DateUtility.compare(curSchd.getSchDate(), finScheduleData.getFinanceMain().getRecalFromDate()) < 0 
							&& (i != sdSize - 1) && i != 0){
						curSchd.setRecalLock(true);
					}else{
						curSchd.setRecalLock(false);
					}
				}
			}
		}
		
		// Step POS Case , setting Step Details to Object
		if(StringUtils.isNotEmpty(moduleDefiner) && 
				StringUtils.equals(finScheduleData.getFinanceMain().getRecalType(), CalculationConstants.RPYCHG_STEPPOS)){
			finScheduleData.setStepPolicyDetails(getFinanceStepDetailDAO().getFinStepDetailListByFinRef(finScheduleData.getFinReference(),
					"", false));
		}
		
		finSchData = ScheduleCalculator.addDisbursement(finScheduleData, amount, addFeeFinance, alwAssetUtilize);

		// Plan EMI Holidays Resetting after Add Disbursement
		if(finSchData.getFinanceMain().isPlanEMIHAlw()){
			finSchData.getFinanceMain().setEventFromDate(finScheduleData.getFinanceMain().getRecalFromDate());
			finSchData.getFinanceMain().setEventToDate(finSchData.getFinanceMain().getMaturityDate());
			finSchData.getFinanceMain().setRecalFromDate(finScheduleData.getFinanceMain().getRecalFromDate());
			finSchData.getFinanceMain().setRecalToDate(finSchData.getFinanceMain().getMaturityDate());
			finSchData.getFinanceMain().setRecalSchdMethod(finSchData.getFinanceMain().getScheduleMethod());

			if(StringUtils.equals(finSchData.getFinanceMain().getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)){
				finSchData = ScheduleCalculator.getFrqEMIHoliday(finSchData);
			}else{
				finSchData = ScheduleCalculator.getAdhocEMIHoliday(finSchData);
			}
		}
		
		BigDecimal newTotalPft = finSchData.getFinanceMain().getTotalGrossPft();
		BigDecimal pftDiff = newTotalPft.subtract(oldTotalPft);
		finSchData.setPftChg(pftDiff);
		finSchData.getFinanceMain().setScheduleRegenerated(true);
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

		// validate Instruction details
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String finReference = financeMain.getFinReference();
		boolean isWIF = finServiceInstruction.isWif();
		Date fromDate = finServiceInstruction.getFromDate();
		BigDecimal actualDisbAmount = finServiceInstruction.getAmount();

		if(!finScheduleData.getFinanceType().isFinIsAlwMD()) {
			String[] valueParm = new String[2];
			valueParm[0] = financeMain.getFinReference();
			valueParm[1] = financeMain.getFinType();
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90276", valueParm)));
			return auditDetail;
		}
		// It shouldn't be past date when compare to appdate
		if (fromDate.compareTo(DateUtility.getAppDate()) != 0) {// || fromDate.compareTo(financeMain.getMaturityDate()) >= 0
			String[] valueParm = new String[2];
			valueParm[0] = "From Date:" + DateUtility.formatToShortDate(fromDate);
			valueParm[1] = "application Date:" + DateUtility.formatToShortDate(DateUtility.getAppDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90277", valueParm)));
		}
		
		// validate from date
		/*if (finServiceInstruction.getFromDate().compareTo(financeMain.getMaturityDate()) > 0) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(finServiceInstruction.getFromDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91101", valueParm)));
		}*/

		// validate disb amount
		if(finServiceInstruction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "Disbursement amount";
			valueParm[1] = String.valueOf(BigDecimal.ZERO);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
		}
		
		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			isOverdraft = true;
		}
		if(!isOverdraft && StringUtils.isEmpty(finServiceInstruction.getRecalType())){
			String[] valueParm = new String[1];
			valueParm[0] = "Recal Type";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30561", valueParm)));
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
			if (actualDisbAmount.compareTo(availableLimit) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = String.valueOf(actualDisbAmount);
				valueParm[1] = String.valueOf(availableLimit);
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91119", valueParm)));
			} else {
				// Checking Total Disbursed amount validate against New disbursement
				BigDecimal totDisbAmount = BigDecimal.ZERO;
				for (FinanceDisbursement finDisbursment : finScheduleData.getDisbursementDetails()) {
					totDisbAmount = totDisbAmount.add(finDisbursment.getDisbAmount());
				}
				totDisbAmount = actualDisbAmount.add(totDisbAmount);
				if (totDisbAmount.compareTo(financeMain.getFinAssetValue()) > 0) {
					String[] valueParm = new String[2];
					valueParm[0] = String.valueOf(totDisbAmount);
					valueParm[1] = String.valueOf(financeMain.getFinAssetValue());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91120", valueParm)));
				}
			}
		}

		// validate RecalType
		if (StringUtils.isNotBlank(finServiceInstruction.getRecalType())) {
			if(!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
					&& !StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLMDT) 
					&& !StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)
					&& !StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)
					&& !StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_ADDTERM)
					&& !StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91104", valueParm)));
			}else{
				if(!StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)){
					String[] valueParm = new String[2];
					valueParm[0] = "RecalType";
					valueParm[1] = finServiceInstruction.getRecalType();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
				}
			}
		}
		// validate reCalFromDate
			if (StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
					|| StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)
					|| StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
				if (!isOverdraft && finServiceInstruction.getRecalFromDate() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = finServiceInstruction.getRecalType();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91105", valueParm)));
					return auditDetail;
				}
			if (isOverdraft) {
				if (finServiceInstruction.getRecalFromDate() == null) {
					finServiceInstruction.setRecalFromDate(finServiceInstruction.getFromDate());
				} else {
					if(DateUtility.compare(finServiceInstruction.getFromDate(), finServiceInstruction.getRecalFromDate())!=0){
						String[] valueParm = new String[2];
						valueParm[0] = "RecalFromDate";
						valueParm[1] = "FromDate";
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90277", valueParm)));
						return auditDetail;
					}
				}
			} else {

				if (finServiceInstruction.getRecalFromDate().compareTo(financeMain.getMaturityDate()) > 0) {
					String[] valueParm = new String[2];
					valueParm[0] = DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
					valueParm[1] = DateUtility.formatToShortDate(financeMain.getMaturityDate());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91114", valueParm)));
				} else if (!isOverdraft && finServiceInstruction.getRecalFromDate()
						.compareTo(finServiceInstruction.getFromDate()) <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "RecalFromDate";
					valueParm[1] = "from date";
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91125", valueParm)));
					return auditDetail;
				}
			}
			}

		// validate reCalToDate
		if(!isOverdraft){
		if (StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
			if (finServiceInstruction.getRecalToDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91108", valueParm)));
				return auditDetail;
			}

			if (finServiceInstruction.getRecalToDate().compareTo(finServiceInstruction.getRecalFromDate()) < 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finServiceInstruction.getRecalToDate());
				valueParm[1] = DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91109", valueParm)));
			}
		}

		// term
		if (StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_ADDTERM)
				|| StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
			if (finServiceInstruction.getTerms() <= 0 ||finServiceInstruction.getTerms()> 99) {
				String[] valueParm = new String[3];
				valueParm[0] = "terms";
				valueParm[1] = "1";
				valueParm[2] = "99";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("65031", valueParm)));
			}
		} else {
			if (finServiceInstruction.getTerms() > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = CalculationConstants.RPYCHG_ADDTERM;
				valueParm[1] = CalculationConstants.RPYCHG_ADDRECAL;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91118", valueParm)));
			}
		}
		}else{
			if(finServiceInstruction.getRecalToDate()==null){
				finServiceInstruction.setRecalToDate(financeMain.getMaturityDate());
				}else{
					if(DateUtility.compare(financeMain.getMaturityDate(), finServiceInstruction.getRecalToDate())!=0){
						String[] valueParm = new String[2];
						valueParm[0] = "RecalToDate";
						valueParm[1] = "MaturityDate";
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90277", valueParm)));
						return auditDetail;
					}
				}
		}

		boolean isValidRecalFromDate = false;
		boolean isValidRecalToDate = false;
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finReference, "", isWIF);
		if(!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory()) &&schedules != null) {
			for(FinanceScheduleDetail schDetail: schedules) {
				if(DateUtility.compare(finServiceInstruction.getRecalFromDate(), schDetail.getSchDate()) == 0) {
					isValidRecalFromDate = true;
					if(checkIsValidRepayDate(auditDetail, schDetail, "RecalFromDate") != null) {
						return auditDetail;
					}
				}
				if(StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
					if(DateUtility.compare(finServiceInstruction.getRecalToDate(), schDetail.getSchDate()) == 0) {
						isValidRecalToDate = true;
						if(checkIsValidRepayDate(auditDetail, schDetail, "RecalToDate") != null) {
							return auditDetail;
						}
					}
				}
			}
			
			if (!isValidRecalFromDate
					&& (StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
							|| StringUtils.equals(finServiceInstruction.getRecalType(),CalculationConstants.RPYCHG_TILLDATE)
							|| StringUtils.equals(finServiceInstruction.getRecalType(),CalculationConstants.RPYCHG_ADDRECAL))) {
				String[] valueParm = new String[1];
				valueParm[0] = "RecalFromDate:"
						+ DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", valueParm)));
			}
			if(!isValidRecalToDate && (StringUtils.equals(finServiceInstruction.getRecalType(), 
					CalculationConstants.RPYCHG_TILLDATE))) {
				String[] valueParm = new String[1];
				valueParm[0] = "RecalToDate:"+DateUtility.formatToShortDate(finServiceInstruction.getRecalToDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", valueParm)));
			}
		}

		// validate disbursement amount
		if (finScheduleData.getFinanceType().isAlwMaxDisbCheckReq()) {
			BigDecimal totDisbAmount = BigDecimal.ZERO;
			for (FinanceDisbursement finDisbursment : finScheduleData.getDisbursementDetails()) {
				totDisbAmount = totDisbAmount.add(finDisbursment.getDisbAmount());
			}
			totDisbAmount = actualDisbAmount.add(totDisbAmount);
			if (totDisbAmount.compareTo(financeMain.getFinAssetValue()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = String.valueOf(totDisbAmount);
				valueParm[1] = String.valueOf(financeMain.getFinAssetValue());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90280", valueParm)));
			}
			
			BigDecimal finAssetValue = financeMain.getFinAssetValue();
			BigDecimal finCurAssetValue = financeMain.getFinCurrAssetValue();
			if (actualDisbAmount.compareTo(finAssetValue.subtract(finCurAssetValue)) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Disbursement amount:" + actualDisbAmount;
				valueParm[1] = "Remaining finAssetValue:" + finAssetValue.subtract(finCurAssetValue);
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
			}
		}
		
		BigDecimal totalDisbAmtFromInst = BigDecimal.ZERO;
		if(financeDetail.getAdvancePaymentsList() != null && !financeDetail.getAdvancePaymentsList().isEmpty()) {
			for(FinAdvancePayments finAdvancePayment: financeDetail.getAdvancePaymentsList()) {
				totalDisbAmtFromInst = totalDisbAmtFromInst.add(finAdvancePayment.getAmtToBeReleased());
				
				// Validate from date and disb date.
				if(DateUtility.compare(finServiceInstruction.getFromDate(), finAdvancePayment.getLlDate()) > 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Disb date:"+DateUtility.formatToLongDate(finAdvancePayment.getLlDate());;
					valueParm[1] = "From date:"+DateUtility.formatToLongDate(finServiceInstruction.getFromDate());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30509", "", valueParm)));
				}
			}
			
			List<ErrorDetail> errors = financeDataValidation.disbursementValidation(financeDetail);
			for (ErrorDetail errorDetails : errors) {
				auditDetail.setErrorDetail(errorDetails);
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
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90261", valueParm)));
			return auditDetail;
		}
		return null;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}
	
	public void setFinanceDataValidation(FinanceDataValidation financeDataValidation) {
		this.financeDataValidation = financeDataValidation;
	}

	public FinanceStepDetailDAO getFinanceStepDetailDAO() {
		return financeStepDetailDAO;
	}

	public void setFinanceStepDetailDAO(FinanceStepDetailDAO financeStepDetailDAO) {
		this.financeStepDetailDAO = financeStepDetailDAO;
	}

}
