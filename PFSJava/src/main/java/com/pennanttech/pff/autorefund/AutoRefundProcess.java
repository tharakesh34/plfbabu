package com.pennanttech.pff.autorefund;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.AutoRefundLoan;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.payment.PaymentDetail;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.pff.autorefund.service.AutoRefundService;
import com.pennant.pff.fee.AdviseType;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class AutoRefundProcess {
	private static final Logger logger = LogManager.getLogger(AutoRefundProcess.class);

	private AutoRefundService autoRefundService;
	private RefundBeneficiary refundBeneficiary;

	/**
	 * Method for Auto Refund process Initiation on EOD execution
	 * 
	 * @param appDate
	 */
	public void startRefundProcess(Date appDate) {
		logger.debug(Literal.ENTERING);
		logger.debug("Auto Refund Process Initiating");

		int closed_Ndays = SysParamUtil.getValueAsInt(SMTParameterConstants.AUTO_REFUND_N_DAYS_CLOSED_LAN);
		int active_Ndays = SysParamUtil.getValueAsInt(SMTParameterConstants.AUTO_REFUND_N_DAYS_ACTIVE_LAN);
		int autoRefCheckDPD = SysParamUtil.getValueAsInt(SMTParameterConstants.AUTO_REFUND_HOLD_DPD);
		boolean isOverDueReq = SysParamUtil.isAllowed(SMTParameterConstants.AUTO_REFUND_OVERDUE_CHECK);
		boolean alwRefundByCheque = SysParamUtil.isAllowed(SMTParameterConstants.AUTO_REFUND_THROUGH_CHEQUE);

		Date closedNDate = DateUtil.addDays(appDate, -closed_Ndays);
		Date activeNDate = DateUtil.addDays(appDate, -active_Ndays);

		// Fetching all Auto Refund eligible records
		List<AutoRefundLoan> autoRefundsLoans = autoRefundService.autoRefundsLoanProcess(appDate);
		if (CollectionUtils.isEmpty(autoRefundsLoans)) {
			logger.debug("Auto Refund Process Completed with Zero Records");

			logger.debug(Literal.LEAVING);
			return;
		}

		List<FinExcessAmount> excessList = null;
		List<ManualAdvise> advList = null;
		BigDecimal overDueAmt = BigDecimal.ZERO;
		BigDecimal reserveAmount = BigDecimal.ZERO;
		List<AutoRefundLoan> updateRefundList = new ArrayList<AutoRefundLoan>();

		// Process of Execution for all Refund Loan records
		for (AutoRefundLoan refundLoan : autoRefundsLoans) {

			logger.debug("Auto Refund Process Initiating for Loan : " + refundLoan.getFinID());

			try {

				List<ErrorDetail> errors = autoRefundService.verifyRefundInitiation(refundLoan, autoRefCheckDPD);
				if (!errors.isEmpty()) {
					// Auto Refund Loan bean to be updated as failure with Reason
					refundLoan.setErrorCode(errors.get(0).getCode());
					refundLoan.setAppDate(appDate);
					refundLoan.setExecutionTime(new Timestamp(System.currentTimeMillis()));
					refundLoan.setStatus("F");
					continue;
				}

				// Overdue Amount verification Check required or not
				overDueAmt = null;
				if (isOverDueReq) {
					overDueAmt = autoRefundService.getOverDueAmountByLoan(refundLoan.getFinID());
				}

				reserveAmount = autoRefundService.findReserveAmountForAutoRefund(refundLoan.getFinID(), overDueAmt);
				if (overDueAmt == null) {
					overDueAmt = BigDecimal.ZERO;
				}
				if (reserveAmount.compareTo(BigDecimal.ZERO) > 0) {
					overDueAmt = overDueAmt.add(reserveAmount);
				}

				Date maxValueDate = null;
				if (refundLoan.isFinIsActive()) {
					maxValueDate = activeNDate;
				} else {
					maxValueDate = closedNDate;
				}

				// Fetch Excess List against Reference and Before Requested Value Date
				excessList = autoRefundService.getExcessRcdList(refundLoan.getFinID(), maxValueDate);

				// Fetch Payable List against Reference and Before Requested Value Date
				advList = autoRefundService.getPayableAdviseList(refundLoan.getFinID(), maxValueDate);

				BigDecimal refundAvail = BigDecimal.ZERO;
				List<PaymentDetail> payDtlList = new ArrayList<PaymentDetail>();

				// Considering Excess First on Verification List to Prepare Payment Details
				for (FinExcessAmount excess : excessList) {

					if (overDueAmt.compareTo(excess.getBalanceAmt()) > 0) {
						overDueAmt = overDueAmt.subtract(excess.getBalanceAmt());
						continue;
					} else {
						refundAvail = excess.getBalanceAmt().subtract(overDueAmt);
						overDueAmt = BigDecimal.ZERO;
					}

					// Payment Details List Addition
					payDtlList.add(
							preparePayDetail(excess.getAmountType(), excess.getExcessID(), null, null, refundAvail));
				}

				// Payable Advise List to Prepare Payment Details
				BigDecimal advBal = null;
				for (ManualAdvise adv : advList) {
					advBal = adv.getAdviseAmount().subtract(adv.getPaidAmount()).subtract(adv.getWaivedAmount());

					if (overDueAmt.compareTo(advBal) > 0) {
						overDueAmt = overDueAmt.subtract(advBal);
						continue;
					} else {
						refundAvail = advBal.subtract(overDueAmt);// GST Consideration FIXME
						overDueAmt = BigDecimal.ZERO;
					}

					// Payment Details List Addition
					payDtlList.add(preparePayDetail(String.valueOf(AdviseType.PAYABLE.id()), adv.getAdviseID(),
							adv.getFeeTypeCode(), adv.getFeeTypeDesc(), refundAvail));
				}

				if (refundAvail.compareTo(BigDecimal.ZERO) <= 0) {
					refundLoan.setErrorCode("REFUND006");
					refundLoan.setAppDate(appDate);
					refundLoan.setExecutionTime(new Timestamp(System.currentTimeMillis()));
					refundLoan.setStatus("F");
					// Auto Refund Loan bean to be updated as failure with Reason
					continue;
				}

				// Validation of Minimum & Maximum Amounts of Refund against Calculated Refund Amount to proceed further
				errors = autoRefundService.validateRefundAmt(refundAvail, refundLoan);
				if (errors.isEmpty()) {

					// Need to write bank details for the Payment type cheque
					PaymentInstruction payInst = refundBeneficiary.fetchBeneficiaryForRefund(refundLoan.getFinID(),
							appDate, alwRefundByCheque);
					if (payInst != null) {
						errors = autoRefundService.executeAutoRefund(refundLoan, payDtlList, payInst, appDate);
						if (CollectionUtils.isEmpty(errors)) {
							refundLoan.setErrorCode("REFUND008");
							refundLoan.setAppDate(appDate);
							refundLoan.setRefundAmt(refundAvail);
							refundLoan.setExecutionTime(new Timestamp(System.currentTimeMillis()));
							refundLoan.setStatus("S");
						} else {
							refundLoan.setErrorCode(errors.get(0).getCode());
							refundLoan.setAppDate(appDate);
							refundLoan.setExecutionTime(new Timestamp(System.currentTimeMillis()));
							refundLoan.setStatus("F");
						}
					} else {
						refundLoan.setErrorCode("REFUND007");
						refundLoan.setAppDate(appDate);
						refundLoan.setExecutionTime(new Timestamp(System.currentTimeMillis()));
						refundLoan.setStatus("F");
					}
				} else {
					refundLoan.setErrorCode(errors.get(0).getCode());
					refundLoan.setAppDate(appDate);
					refundLoan.setExecutionTime(new Timestamp(System.currentTimeMillis()));
					refundLoan.setStatus("F");

				}
			} catch (UnhandledException e) {
				logger.debug("Auto Refund Process failed for Loan : " + refundLoan.getFinID());
				logger.error(e.getMessage());
				refundLoan.setErrorCode("REFUND009");
				refundLoan.setAppDate(appDate);
				refundLoan.setExecutionTime(new Timestamp(System.currentTimeMillis()));
				refundLoan.setStatus("F");
			} catch (NullPointerException e) {
				logger.debug("Auto Refund Process failed for Loan : " + refundLoan.getFinID());
				logger.error(e.getMessage());
				refundLoan.setErrorCode("REFUND009");
				refundLoan.setAppDate(appDate);
				refundLoan.setExecutionTime(new Timestamp(System.currentTimeMillis()));
				refundLoan.setStatus("F");
			} catch (Exception e) {
				logger.debug("Auto Refund Process failed for Loan : " + refundLoan.getFinID());
				logger.error(e.getMessage());
				refundLoan.setErrorCode("REFUND009");
				refundLoan.setAppDate(appDate);
				refundLoan.setExecutionTime(new Timestamp(System.currentTimeMillis()));
				refundLoan.setStatus("F");
			} finally {
				logger.debug("Auto Refund Process completed for Loan : " + refundLoan.getFinID());
				updateRefundList.add(refundLoan);
				if (updateRefundList.size() == 500) {
					autoRefundService.saveRefundlist(updateRefundList);
					updateRefundList = new ArrayList<AutoRefundLoan>();
				}
			}
		}

		if (updateRefundList.size() > 0) {
			autoRefundService.saveRefundlist(updateRefundList);
		}
		updateRefundList = null;
		autoRefundsLoans = null;
		excessList = null;
		advList = null;

		logger.debug("Auto Refund Process Completed");

		logger.debug(Literal.LEAVING);

	}

	/**
	 * Method for preparation of Payment detail record
	 * 
	 * @param amountType
	 * @param amount
	 * @param referenceID
	 * @return
	 */
	private PaymentDetail preparePayDetail(String amountType, long refundAgainst, String feeTypeCode,
			String feeTypeDesc, BigDecimal amount) {

		// Payment Details preparation
		PaymentDetail pd = new PaymentDetail();
		pd.setReferenceId(refundAgainst);
		pd.setAmount(amount);
		pd.setAmountType(amountType);
		if (StringUtils.isNotBlank(feeTypeCode)) {
			pd.setFeeTypeCode(feeTypeCode);
			pd.setFeeTypeDesc(feeTypeDesc);
		}
		pd.setFinSource(UploadConstants.FINSOURCE_ID_AUTOPROCESS);

		return pd;
	}

	public void setAutoRefundService(AutoRefundService autoRefundService) {
		this.autoRefundService = autoRefundService;
	}

	public void setRefundBeneficiary(RefundBeneficiary refundBeneficiary) {
		this.refundBeneficiary = refundBeneficiary;
	}

}
