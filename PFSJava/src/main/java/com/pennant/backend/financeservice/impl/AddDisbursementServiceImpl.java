package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.core.ChangeGraceEndService;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.financemanagement.FinanceStepDetailDAO;
import com.pennant.backend.financeservice.AddDisbursementService;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.OverdraftScheduleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.finance.impl.FinanceDataValidation;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;

public class AddDisbursementServiceImpl extends GenericService<FinServiceInstruction>
		implements AddDisbursementService {
	private static Logger logger = LogManager.getLogger(AddDisbursementServiceImpl.class);

	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceStepDetailDAO financeStepDetailDAO;
	private FinanceDataValidation financeDataValidation;
	private FinServiceInstrutionDAO finServiceInstrutionDAO;
	private ChangeGraceEndService changeGraceEndService;
	private FinAdvancePaymentsService finAdvancePaymentsService;

	public FinScheduleData getAddDisbDetails(FinScheduleData schdData, BigDecimal amount, BigDecimal addFeeFinance,
			boolean alwAssetUtilize, String moduleDefiner) {
		logger.debug(Literal.ENTERING);

		FinScheduleData finSchData = null;

		FinanceMain fm = schdData.getFinanceMain();
		long finID = fm.getFinID();

		BigDecimal oldTotalPft = fm.getTotalGrossPft();
		BigDecimal disbAmount = BigDecimal.ZERO;

		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();

		if (schedules.size() > 0) {
			Date recalLockTill = fm.getRecalFromDate();
			if (recalLockTill == null) {
				recalLockTill = fm.getMaturityDate();
			}

			int sdSize = schedules.size();
			FinanceScheduleDetail curSchd = null;
			for (int i = 0; i <= sdSize - 1; i++) {
				curSchd = schedules.get(i);

				if (DateUtility.compare(curSchd.getSchDate(), fm.getGrcPeriodEndDate()) <= 0) {
					curSchd.setSchdMethod(fm.getGrcSchdMthd());
				} else {
					curSchd.setSchdMethod(fm.getScheduleMethod());
				}

				// Schedule Recalculation Locking Period Applicability
				if (SysParamUtil.isAllowed(SMTParameterConstants.ALW_SCH_RECAL_LOCK)) {
					if (DateUtility.compare(curSchd.getSchDate(), recalLockTill) < 0 && (i != sdSize - 1) && i != 0) {
						if (DateUtility.compare(curSchd.getSchDate(), fm.getEventFromDate()) == 0) {
							curSchd.setRecalLock(false);
						} else {
							curSchd.setRecalLock(true);
						}
					} else {
						curSchd.setRecalLock(false);
					}
				}
			}
		}

		/* Commented the below since not required */
		/*
		 * //overdraft loan changes fix me if (financeScheduleDetails.size() > 0) { for (FinanceScheduleDetail finSchd :
		 * financeScheduleDetails) { finSchd.setSchdMethod(financeMain.getScheduleMethod()); } }
		 */

		// Step POS Case , setting Step Details to Object
		if (StringUtils.isNotEmpty(moduleDefiner) && fm.isStepFinance()) {
			schdData.setStepPolicyDetails(financeStepDetailDAO.getFinStepDetailListByFinRef(finID, "", false), true);
		}

		// financeMain.setCalRoundingMode(finScheduleData.getFinanceType().getRoundingMode());
		// financeMain.setRoundingTarget(finScheduleData.getFinanceType().getRoundingTarget());
		finSchData = ScheduleCalculator.addDisbursement(schdData, amount, addFeeFinance, alwAssetUtilize);

		for (FinanceDisbursement curDisb : schdData.getDisbursementDetails()) {

			/*
			 * if (StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, curDisb.getDisbStatus()) ||
			 * StringUtils.equals(DisbursementConstants.DISBTYPE_FLEXI, curDisb.getDisbType())) { continue; }
			 */
			disbAmount = disbAmount.add(curDisb.getDisbAmount());
		}
		FinanceMain finMain = schdData.getFinanceMain();
		if (finMain.isAllowGrcPeriod() && finMain.isEndGrcPeriodAftrFullDisb()) {
			if (finMain.getFinAssetValue().compareTo(disbAmount.add(amount)) == 0
					&& finMain.getGrcPeriodEndDate().compareTo(finMain.getEventFromDate()) > 0) {

				// reset grace n repay fields and end grace period
				changeGraceEndService.changeGraceEnd(finSchData, true);
			}
		} else {

			// Plan EMI Holidays Resetting after Add Disbursement
			if (fm.isPlanEMIHAlw()) {
				fm.setEventFromDate(schdData.getFinanceMain().getRecalFromDate());
				fm.setEventToDate(fm.getMaturityDate());
				fm.setRecalFromDate(schdData.getFinanceMain().getRecalFromDate());
				fm.setRecalToDate(fm.getMaturityDate());
				fm.setRecalSchdMethod(fm.getScheduleMethod());

				if (StringUtils.equals(fm.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
					finSchData = ScheduleCalculator.getFrqEMIHoliday(finSchData);
				} else {
					finSchData = ScheduleCalculator.getAdhocEMIHoliday(finSchData);
				}
			}
		}

		BigDecimal newTotalPft = fm.getTotalGrossPft();
		BigDecimal pftDiff = newTotalPft.subtract(oldTotalPft);

		finSchData.setPftChg(pftDiff);
		fm.setScheduleRegenerated(true);

		logger.debug(Literal.LEAVING);

		return finSchData;
	}

	@Override
	public AuditDetail doValidations(FinanceDetail fd, FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = new AuditDetail();
		boolean servNoExist = false;

		// validate Instruction details
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		List<ExtendedField> extendedDetailsList = fsi.getExtendedDetails();
		List<ErrorDetail> errorDetailList = null;

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		boolean isWIF = fsi.isWif();
		Date fromDate = fsi.getFromDate();
		BigDecimal actualDisbAmount = fsi.getAmount();
		int ccyFormat = CurrencyUtil.getFormat(fm.getFinCcy());

		if (!schdData.getFinanceType().isFinIsAlwMD()) {
			String[] valueParm = new String[2];
			valueParm[0] = fm.getFinReference();
			valueParm[1] = fm.getFinType();
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90276", valueParm)));
			return auditDetail;
		}
		// It shouldn't be past date when compare to appdate
		Date appDate = SysParamUtil.getAppDate();
		if (fromDate.compareTo(appDate) != 0) {// || fromDate.compareTo(financeMain.getMaturityDate()) >= 0
			String[] valueParm = new String[2];
			valueParm[0] = "From Date:" + DateUtility.formatToShortDate(fromDate);
			valueParm[1] = "application Date:" + DateUtility.formatToShortDate(appDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90277", valueParm)));
		}

		// validate from date
		/*
		 * if (finServiceInstruction.getFromDate().compareTo(financeMain.getMaturityDate()) > 0) { String[] valueParm =
		 * new String[1]; valueParm[0] = String.valueOf(finServiceInstruction.getFromDate());
		 * auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91101", valueParm))); }
		 */

		// validate disb amount
		if (fsi.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "Disbursement amount";
			valueParm[1] = String.valueOf(BigDecimal.ZERO);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
		}

		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())) {
			isOverdraft = true;
		}
		if (!isOverdraft && StringUtils.isEmpty(fsi.getRecalType())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Recal Type";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30561", valueParm)));
		}
		if (isOverdraft) {
			List<OverdraftScheduleDetail> odSchdDetail = schdData.getOverdraftScheduleDetails();
			BigDecimal availableLimit = BigDecimal.ZERO;
			if (odSchdDetail != null && odSchdDetail.size() > 0) {
				for (int i = 0; i < odSchdDetail.size(); i++) {
					if (odSchdDetail.get(i).getDroplineDate().compareTo(fromDate) > 0) {
						break;
					}
					availableLimit = odSchdDetail.get(i).getODLimit();
				}
			} else {
				availableLimit = fm.getFinAssetValue();
			}

			// Schedule Outstanding amount calculation
			List<FinanceScheduleDetail> schList = schdData.getFinanceScheduleDetails();
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
				valueParm[0] = PennantApplicationUtil.amountFormate(actualDisbAmount, ccyFormat);
				valueParm[1] = PennantApplicationUtil.amountFormate(availableLimit, ccyFormat);
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91119", valueParm)));
			} else {
				// Checking Total Disbursed amount validate against New disbursement
				BigDecimal totDisbAmount = BigDecimal.ZERO;
				/*
				 * for (FinanceDisbursement finDisbursment : finScheduleData.getDisbursementDetails()) { totDisbAmount =
				 * totDisbAmount.add(finDisbursment.getDisbAmount()); }
				 */
				for (int i = 0; i < schList.size(); i++) {
					FinanceScheduleDetail curSchd = schList.get(i);
					totDisbAmount = totDisbAmount.add(curSchd.getDisbAmount().subtract(curSchd.getSchdPriPaid()));
				}

				totDisbAmount = actualDisbAmount.add(totDisbAmount);
				if (totDisbAmount.compareTo(fm.getFinAssetValue()) > 0) {
					String[] valueParm = new String[2];
					valueParm[0] = PennantApplicationUtil.amountFormate(totDisbAmount, ccyFormat);
					valueParm[1] = PennantApplicationUtil.amountFormate(fm.getFinAssetValue(), ccyFormat);
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91120", valueParm)));
				}
			}
		}

		// validate RecalType
		if (StringUtils.isNotBlank(fsi.getRecalType())) {
			if (!StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
					&& !StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)
					&& !StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)
					&& !StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_ADDTERM)
					&& !StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
				String[] valueParm = new String[1];
				valueParm[0] = fsi.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91104", valueParm)));
			}
		}
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())
				&& !StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
			String[] valueParm = new String[2];
			valueParm[0] = "RecalType : " + fsi.getRecalType();
			valueParm[1] = "Over Draft Loan";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
		}

		// validate reCalFromDate
		if (StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
				|| StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)
				|| StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
			if (!isOverdraft && fsi.getRecalFromDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = fsi.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91105", valueParm)));
				return auditDetail;
			}
			if (isOverdraft) {
				if (fsi.getRecalFromDate() == null) {
					fsi.setRecalFromDate(fsi.getFromDate());
				} else {
					if (DateUtility.compare(fsi.getFromDate(), fsi.getRecalFromDate()) != 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "RecalFromDate";
						valueParm[1] = "FromDate";
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90277", valueParm)));
						return auditDetail;
					}
				}
			} else {

				if (fsi.getRecalFromDate().compareTo(fm.getMaturityDate()) > 0) {
					String[] valueParm = new String[2];
					valueParm[0] = DateUtility.formatToShortDate(fsi.getRecalFromDate());
					valueParm[1] = DateUtility.formatToShortDate(fm.getMaturityDate());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91114", valueParm)));
				} else if (!isOverdraft && fsi.getRecalFromDate().compareTo(fsi.getFromDate()) <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "RecalFromDate";
					valueParm[1] = "from date";
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91125", valueParm)));
					return auditDetail;
				}
			}
		}

		// ExtendedFieldDetails Validation
		if (extendedDetailsList != null && extendedDetailsList.size() > 0) {
			String subModule = fd.getFinScheduleData().getFinanceType().getFinCategory();
			errorDetailList = extendedFieldDetailsService.validateExtendedFieldDetails(fsi.getExtendedDetails(),
					ExtendedFieldConstants.MODULE_LOAN, subModule, FinServiceEvent.ADDDISB);
			if (errorDetailList != null && !errorDetailList.isEmpty()) {
				for (ErrorDetail errorDetails : errorDetailList) {
					auditDetail.setErrorDetail(errorDetails);
				}
				return auditDetail;
			}
		}

		// ### 02-05-2018-END
		// validate serviceReqNo
		if (isOverdraft) {
			if (StringUtils.isNotBlank(fsi.getServiceReqNo())) {
				servNoExist = finServiceInstrutionDAO.getFinServInstDetails(fsi.getFinEvent(), fsi.getServiceReqNo());
				if (servNoExist) {
					String[] valueParm = new String[2];
					valueParm[0] = "serviceReqNo with ";
					valueParm[1] = fsi.getServiceReqNo();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41001", valueParm)));
					return auditDetail;
				}
			}
		}

		// validate reCalToDate
		if (!isOverdraft) {
			if (StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
				if (fsi.getRecalToDate() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = fsi.getRecalType();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91108", valueParm)));
					return auditDetail;
				}

				if (fsi.getRecalToDate().compareTo(fsi.getRecalFromDate()) < 0) {
					String[] valueParm = new String[2];
					valueParm[0] = DateUtility.formatToShortDate(fsi.getRecalToDate());
					valueParm[1] = DateUtility.formatToShortDate(fsi.getRecalFromDate());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91109", valueParm)));
				}
			}

			// term
			if (StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_ADDTERM)
					|| StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
				if (fsi.getTerms() <= 0 || fsi.getTerms() > 99) {
					String[] valueParm = new String[3];
					valueParm[0] = "terms";
					valueParm[1] = "1";
					valueParm[2] = "99";
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("65031", valueParm)));
				}
			} else {
				if (fsi.getTerms() > 0) {
					String[] valueParm = new String[2];
					valueParm[0] = CalculationConstants.RPYCHG_ADDTERM;
					valueParm[1] = CalculationConstants.RPYCHG_ADDRECAL;
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91118", valueParm)));
				}
			}
		} else {
			if (fsi.getRecalToDate() == null) {
				fsi.setRecalToDate(fm.getMaturityDate());
			} else {
				if (DateUtility.compare(fm.getMaturityDate(), fsi.getRecalToDate()) != 0) {
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
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", isWIF);
		if (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory()) && schedules != null) {
			for (FinanceScheduleDetail schDetail : schedules) {
				if (DateUtility.compare(fsi.getRecalFromDate(), schDetail.getSchDate()) == 0) {
					isValidRecalFromDate = true;
					if (checkIsValidRepayDate(auditDetail, schDetail, "RecalFromDate") != null) {
						return auditDetail;
					}
				}
				if (StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
					if (DateUtility.compare(fsi.getRecalToDate(), schDetail.getSchDate()) == 0) {
						isValidRecalToDate = true;
						if (checkIsValidRepayDate(auditDetail, schDetail, "RecalToDate") != null) {
							return auditDetail;
						}
					}
				}
			}

			if (!isValidRecalFromDate && (StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
					|| StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)
					|| StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL))) {
				String[] valueParm = new String[1];
				valueParm[0] = "RecalFromDate:" + DateUtility.formatToShortDate(fsi.getRecalFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", valueParm)));
			}
			if (!isValidRecalToDate && (StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLDATE))) {
				String[] valueParm = new String[1];
				valueParm[0] = "RecalToDate:" + DateUtility.formatToShortDate(fsi.getRecalToDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", valueParm)));
			}
		}

		// validate disbursement amount
		if (schdData.getFinanceType().isAlwMaxDisbCheckReq()) {
			BigDecimal totDisbAmount = BigDecimal.ZERO;
			for (FinanceDisbursement finDisbursment : schdData.getDisbursementDetails()) {
				totDisbAmount = totDisbAmount.add(finDisbursment.getDisbAmount());
			}
			totDisbAmount = actualDisbAmount.add(totDisbAmount);
			if (totDisbAmount.compareTo(fm.getFinAssetValue()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = String.valueOf(totDisbAmount);
				valueParm[1] = String.valueOf(fm.getFinAssetValue());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90280", valueParm)));
			}

			BigDecimal finAssetValue = fm.getFinAssetValue();
			BigDecimal finCurAssetValue = fm.getFinCurrAssetValue();
			if (actualDisbAmount.compareTo(finAssetValue.subtract(finCurAssetValue)) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Disbursement amount:" + actualDisbAmount;
				valueParm[1] = "Remaining finAssetValue:" + finAssetValue.subtract(finCurAssetValue);
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
			}
		}

		// validate and allow only single instruction where instruction based schedule
		if (fm.isInstBasedSchd() && fsi.isQuickDisb()) {
			if (CollectionUtils.isNotEmpty(fd.getAdvancePaymentsList()) && fd.getAdvancePaymentsList().size() > 1) {
				String[] valueParm = new String[1];
				valueParm[0] = "Only one Instruction allowed";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
			}
		}

		BigDecimal totalDisbAmtFromInst = BigDecimal.ZERO;
		if (fd.getAdvancePaymentsList() != null && !fd.getAdvancePaymentsList().isEmpty()) {
			for (FinAdvancePayments finAdvancePayment : fd.getAdvancePaymentsList()) {
				totalDisbAmtFromInst = totalDisbAmtFromInst.add(finAdvancePayment.getAmtToBeReleased());

				// Validate from date and disb date.
				if (DateUtility.compare(fsi.getFromDate(), finAdvancePayment.getLlDate()) > 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Disb date:" + DateUtility.formatToLongDate(finAdvancePayment.getLlDate());
					;
					valueParm[1] = "From date:" + DateUtility.formatToLongDate(fsi.getFromDate());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30509", "", valueParm)));
				}
			}

			errorDetailList = financeDataValidation.disbursementValidation(fd);
			for (ErrorDetail errorDetails : errorDetailList) {
				auditDetail.setErrorDetail(errorDetails);
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private AuditDetail checkIsValidRepayDate(AuditDetail auditDetail, FinanceScheduleDetail curSchd, String label) {
		if (!((curSchd.isRepayOnSchDate()
				|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))
				&& ((curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) >= 0 && curSchd.isRepayOnSchDate()
						&& !curSchd.isSchPftPaid())
						|| (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) >= 0
								&& curSchd.isRepayOnSchDate() && !curSchd.isSchPriPaid())))) {
			String[] valueParm = new String[1];
			valueParm[0] = label;
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90261", valueParm)));
			return auditDetail;
		}
		return null;
	}

	@Override
	public AuditDetail doCancelDisbValidations(FinanceDetail financeDetail) {
		logger.info(Literal.ENTERING);
		AuditDetail auditDetail = new AuditDetail();
		FinScheduleData schd = financeDetail.getFinScheduleData();
		FinanceMain fm = schd.getFinanceMain();
		List<ErrorDetail> errorDetailList = financeDataValidation.disbursementValidation(financeDetail);

		if (CollectionUtils.isNotEmpty(errorDetailList)) {
			for (ErrorDetail errorDetails : errorDetailList) {
				auditDetail.setErrorDetail(errorDetails);
			}
			return auditDetail;
		}

		List<FinAdvancePayments> advancePayments = financeDetail.getAdvancePaymentsList();
		if (advancePayments == null) {
			return auditDetail;
		}

		List<FinanceDisbursement> disbursements = schd.getDisbursementDetails();
		for (FinAdvancePayments advPayments : advancePayments) {
			advPayments.setDisbSeq(disbursements.size());
		}

		List<ErrorDetail> errors = finAdvancePaymentsService.validateFinAdvPayments(advancePayments, disbursements, fm,
				true);
		for (ErrorDetail erroDetails : errors) {
			auditDetail.setErrorDetail(
					ErrorUtil.getErrorDetail(new ErrorDetail(erroDetails.getCode(), erroDetails.getParameters())));
		}

		logger.info(Literal.LEAVING);
		return auditDetail;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setFinanceStepDetailDAO(FinanceStepDetailDAO financeStepDetailDAO) {
		this.financeStepDetailDAO = financeStepDetailDAO;
	}

	public void setFinanceDataValidation(FinanceDataValidation financeDataValidation) {
		this.financeDataValidation = financeDataValidation;
	}

	public void setFinServiceInstrutionDAO(FinServiceInstrutionDAO finServiceInstrutionDAO) {
		this.finServiceInstrutionDAO = finServiceInstrutionDAO;
	}

	public void setChangeGraceEndService(ChangeGraceEndService changeGraceEndService) {
		this.changeGraceEndService = changeGraceEndService;
	}

	public void setFinAdvancePaymentsService(FinAdvancePaymentsService finAdvancePaymentsService) {
		this.finAdvancePaymentsService = finAdvancePaymentsService;
	}

}
