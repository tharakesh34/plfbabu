package com.pennanttech.pff.autorefund;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.core.FinOverDueService;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
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

	private FinExcessAmountDAO finExcessAmountDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private AutoRefundService autoRefundService;
	private RefundBeneficiary refundBeneficiary;
	private FinOverDueService finOverDueService;

	public void startRefundProcess(Date appDate) {
		logger.debug(Literal.ENTERING);

		logger.debug("Auto Refund Process Initiating");

		int closedNdays = SysParamUtil.getValueAsInt(SMTParameterConstants.AUTO_REFUND_N_DAYS_CLOSED_LAN);
		int activeNdays = SysParamUtil.getValueAsInt(SMTParameterConstants.AUTO_REFUND_N_DAYS_ACTIVE_LAN);
		int autoRefCheckDPD = SysParamUtil.getValueAsInt(SMTParameterConstants.AUTO_REFUND_HOLD_DPD);
		boolean isOverDueReq = SysParamUtil.isAllowed(SMTParameterConstants.AUTO_REFUND_OVERDUE_CHECK);
		boolean alwRefundByCheque = SysParamUtil.isAllowed(SMTParameterConstants.AUTO_REFUND_THROUGH_CHEQUE);

		Date closedNDate = DateUtil.addDays(appDate, -closedNdays);
		Date activeNDate = DateUtil.addDays(appDate, -activeNdays);

		/* Fetching all Auto Refund eligible records */
		List<AutoRefundLoan> arlList = autoRefundService.getAutoRefunds();

		if (CollectionUtils.isEmpty(arlList)) {
			logger.debug("There are no loans for auto refund.");

			logger.debug(Literal.LEAVING);
			return;
		}

		logger.debug("{} lons are eligible for auto refund.", arlList.size());

		/* Process of Execution for all Refund Loan records */

		List<AutoRefundLoan> list = new ArrayList<>();

		for (AutoRefundLoan arl : arlList) {
			long finID = arl.getFinID();

			arl.setAutoRefCheckDPD(autoRefCheckDPD);
			arl.setOverDueReq(isOverDueReq);
			arl.setActiveNDate(activeNDate);
			arl.setClosedNDate(closedNDate);
			arl.setAlwRefundByCheque(alwRefundByCheque);
			arl.setAppDate(appDate);

			try {
				process(arl);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
				arl.setError(ErrorUtil.getErrorDetail(new ErrorDetail("REFUND_999")));
				arl.setAppDate(appDate);
				arl.setExecutionTime(new Timestamp(System.currentTimeMillis()));
				arl.setStatus("F");
			}

			logger.debug("Auto Refund Process completed for Loan {}", finID);

			if (arl.getRefundAmt().compareTo(BigDecimal.ZERO) > 0) {
				list.add(arl);
			}

			if (list.size() == 500) {
				autoRefundService.save(list);
				list.clear();
			}

		}

		if (list.size() > 0) {
			autoRefundService.save(list);
		}

		logger.debug("Auto Refund Process Completed");

		logger.debug(Literal.LEAVING);

	}

	private void process(AutoRefundLoan arl) {
		long finID = arl.getFinID();
		String finReference = arl.getFinReference();
		Date appDate = arl.getAppDate();

		ErrorDetail error = autoRefundService.validateRefund(arl, true);

		if (error != null) {
			/* Auto Refund Loan bean to be updated as failure with Reason */
			arl.setError(error);
			arl.setAppDate(appDate);
			arl.setExecutionTime(new Timestamp(System.currentTimeMillis()));
			arl.setStatus("F");
			return;
		}

		/* Overdue Amount verification Check required or not */
		BigDecimal overDueAmt = null;
		if (arl.isOverDueReq()) {
			overDueAmt = finOverDueService.getDueAgnistLoan(finID);
		}

		BigDecimal reserveAmount = autoRefundService.findReserveAmountForAutoRefund(finID, overDueAmt, appDate);

		if (overDueAmt == null) {
			overDueAmt = BigDecimal.ZERO;
		}

		if (reserveAmount.compareTo(BigDecimal.ZERO) > 0) {
			overDueAmt = overDueAmt.add(reserveAmount);
		}

		Date maxValueDate = null;
		if (arl.isFinIsActive()) {
			maxValueDate = arl.getActiveNDate();
		} else {
			maxValueDate = arl.getClosedNDate();
		}

		logger.debug("Auto Refund Process initiating for FinReference {}", finReference);

		/* Fetch Excess List against Reference and Before Requested Value Date */
		List<FinExcessAmount> excessList = finExcessAmountDAO.getExcessRcdList(finID, maxValueDate);

		/* Fetch Payable List against Reference and Before Requested Value Date */
		List<ManualAdvise> advList = manualAdviseDAO.getPayableAdviseList(finID, maxValueDate);

		BigDecimal refundAvail = BigDecimal.ZERO;
		List<PaymentDetail> payDtlList = new ArrayList<>();

		/* Considering Excess First on Verification List to Prepare Payment Details */
		for (FinExcessAmount excess : excessList) {
			if (overDueAmt.compareTo(excess.getBalanceAmt()) > 0) {
				overDueAmt = overDueAmt.subtract(excess.getBalanceAmt());
				continue;
			} else {
				refundAvail = excess.getBalanceAmt().subtract(overDueAmt);
				overDueAmt = BigDecimal.ZERO;
			}

			/* Payment Details List Addition */
			payDtlList.add(preparePayDetail(excess.getAmountType(), excess.getExcessID(), null, null, refundAvail));
		}

		/* Payable Advise List to Prepare Payment Details */
		BigDecimal advBal = null;
		for (ManualAdvise adv : advList) {
			advBal = adv.getAdviseAmount().subtract(adv.getPaidAmount()).subtract(adv.getWaivedAmount());

			if (overDueAmt.compareTo(advBal) > 0) {
				overDueAmt = overDueAmt.subtract(advBal);
				continue;
			} else {
				refundAvail = advBal.subtract(overDueAmt);
				overDueAmt = BigDecimal.ZERO;
			}

			/* Payment Details List Addition */
			payDtlList.add(preparePayDetail(String.valueOf(AdviseType.PAYABLE.id()), adv.getAdviseID(),
					adv.getFeeTypeCode(), adv.getFeeTypeDesc(), refundAvail));
		}

		arl.setRefundAmt(refundAvail);

		if (refundAvail.compareTo(BigDecimal.ZERO) <= 0) {
			arl.setError(ErrorUtil.getErrorDetail(new ErrorDetail("REFUND_006")));
			arl.setAppDate(appDate);
			arl.setExecutionTime(new Timestamp(System.currentTimeMillis()));
			arl.setStatus("F");
			/* Auto Refund Loan bean to be updated as failure with Reason */
			return;
		}

		/*
		 * Validation of Minimum & Maximum Amounts of Refund against Calculated Refund Amount to proceed further
		 */
		error = autoRefundService.validateRefundAmt(refundAvail, arl);

		if (error != null) {
			arl.setError(error);
			arl.setAppDate(appDate);
			arl.setExecutionTime(new Timestamp(System.currentTimeMillis()));
			arl.setStatus("F");

			return;
		}

		// Need to write bank details for the Payment type cheque
		PaymentInstruction payInst = refundBeneficiary.getBeneficiary(finID, appDate, arl.isAlwRefundByCheque());

		if (payInst == null) {
			arl.setError(ErrorUtil.getErrorDetail(new ErrorDetail("REFUND_007")));
			arl.setAppDate(appDate);
			arl.setExecutionTime(new Timestamp(System.currentTimeMillis()));
			arl.setStatus("F");

			return;
		}

		error = autoRefundService.executeAutoRefund(arl, payDtlList, payInst);

		if (error != null) {
			arl.setError(error);
			arl.setAppDate(appDate);
			arl.setExecutionTime(new Timestamp(System.currentTimeMillis()));
			arl.setStatus("F");

			return;
		}

		arl.setError(ErrorUtil.getErrorDetail(new ErrorDetail("REFUND_000")));
		arl.setAppDate(appDate);
		arl.setRefundAmt(refundAvail);
		arl.setExecutionTime(new Timestamp(System.currentTimeMillis()));
		arl.setStatus("S");

	}

	private PaymentDetail preparePayDetail(String amountType, long refundAgainst, String feeTypeCode,
			String feeTypeDesc, BigDecimal amount) {

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

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setAutoRefundService(AutoRefundService autoRefundService) {
		this.autoRefundService = autoRefundService;
	}

	@Autowired
	public void setRefundBeneficiary(RefundBeneficiary refundBeneficiary) {
		this.refundBeneficiary = refundBeneficiary;
	}

	@Autowired
	public void setFinOverDueService(FinOverDueService finOverDueService) {
		this.finOverDueService = finOverDueService;
	}
}
