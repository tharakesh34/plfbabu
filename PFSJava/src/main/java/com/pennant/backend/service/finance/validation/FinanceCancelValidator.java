package com.pennant.backend.service.finance.validation;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.receipts.CrossLoanTransferDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.fincancelupload.exception.FinCancelUploadError;
import com.pennant.pff.settlement.service.SettlementService;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.util.FinanceUtil;

public class FinanceCancelValidator {
	private FinServiceInstrutionDAO finServiceInstructionDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private PaymentHeaderService paymentHeaderService;
	private SettlementService settlementService;
	private CrossLoanTransferDAO crossLoanTransferDAO;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private FinanceTypeDAO financeTypeDAO;
	private FinanceDetailService financeDetailService;

	public FinCancelUploadError validLoan(FinanceMain fm, List<FinanceScheduleDetail> schedules) {
		Date appDate = fm.getAppDate();
		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		Date backValueDate = DateUtil.addDays(appDate,
				SysParamUtil.getValueAsInt(SMTParameterConstants.MAINTAIN_CANFIN_BACK_DATE));

		if (DateUtil.compare(backValueDate, fm.getFinStartDate()) > 0) {
			return FinCancelUploadError.LANCLUP018;
		}

		if (DateUtil.compare(appDate, fm.getMaturityDate()) > 0) {
			return FinCancelUploadError.LANCLUP011;
		}

		if (fm.isWriteoffLoan()) {
			return FinCancelUploadError.LANCLUP013;
		}

		if (!ImplementationConstants.ALLOW_CANCEL_LOAN_AFTER_PAYMENTS) {
			FinCancelUploadError error = validateSchedules(fm, schedules);
			if (error != null) {
				return error;
			}
		}

		if (!financeTypeDAO.isAllowCancelFin(fm.getFinType())) {
			return FinCancelUploadError.LANCLUP017;
		}

		if (finReceiptHeaderDAO.isReceiptExists(finReference, "_Temp")) {
			return FinCancelUploadError.LANCLUP008;
		}

		if (ImplementationConstants.DISB_REVERSAL_REQ_BEFORE_LOAN_CANCEL) {
			List<String> advList = finAdvancePaymentsDAO.getFinAdvancePaymentsStatus(finID);

			for (String payments : advList) {
				if (FinanceUtil.isInValidDisbStatus(payments)) {
					return FinCancelUploadError.LANCLUP009;
				}
			}
		}

		if (paymentHeaderService.isRefundProvided(finID)) {
			return FinCancelUploadError.LANCLUP007;
		}

		if (finServiceInstructionDAO.isLMSActionPerformed(finID, FinServiceEvent.RESCHD)) {
			return FinCancelUploadError.LANCLUP010;
		}

		if (finServiceInstructionDAO.isLMSActionPerformed(finID, FinServiceEvent.RESTRUCTURE)) {
			return FinCancelUploadError.LANCLUP012;
		}

		if (settlementService.isSettlementInitiated(finID)) {
			return FinCancelUploadError.LANCLUP014;
		}

		if (crossLoanTransferDAO.isCrossLoanReceiptProcessed(finID)) {
			return FinCancelUploadError.LANCLUP015;
		}

		return null;
	}

	private FinCancelUploadError validateSchedules(FinanceMain fm, List<FinanceScheduleDetail> schedules) {
		Date appDate = fm.getAppDate();
		long finID = fm.getFinID();

		FinanceScheduleDetail bpiSchedule = null;
		String maintainSts = StringUtils.trimToEmpty(fm.getRcdMaintainSts());

		for (FinanceScheduleDetail curSchd : schedules) {
			if (FinanceConstants.FLAG_BPI.equals(curSchd.getBpiOrHoliday())) {
				bpiSchedule = curSchd;
				continue;
			}

			if (curSchd.getSchDate().compareTo(appDate) <= 0 && curSchd.isRepayOnSchDate()) {
				return FinCancelUploadError.LANCLUP005;
			}
		}

		if (StringUtils.isNotEmpty(maintainSts)) {
			return null;
		}

		List<FinanceRepayments> repayments = financeDetailService.getFinRepayList(finID);

		for (FinanceRepayments fr : repayments) {
			if (bpiSchedule == null) {
				return FinCancelUploadError.LANCLUP006;
			}

			if (fr.getFinSchdDate().compareTo(bpiSchedule.getSchDate()) != 0) {
				return FinCancelUploadError.LANCLUP006;
			}
		}

		return null;
	}

	public String getOverrideDescription(FinCancelUploadError error, FinanceMain fm) {
		String description = error.description();

		switch (error) {
		case LANCLUP018:
			Date backValueDate = DateUtil.addDays(fm.getAppDate(),
					SysParamUtil.getValueAsInt(SMTParameterConstants.MAINTAIN_CANFIN_BACK_DATE));
			return FinCancelUploadError.getOverrideDescription(error,
					DateUtil.format(backValueDate, DateFormat.LONG_DATE));
		default:
			return description;
		}
	}

	@Autowired
	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Autowired
	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

	@Autowired
	public void setSettlementService(SettlementService settlementService) {
		this.settlementService = settlementService;
	}

	@Autowired
	public void setCrossLoanTransferDAO(CrossLoanTransferDAO crossLoanTransferDAO) {
		this.crossLoanTransferDAO = crossLoanTransferDAO;
	}

	@Autowired
	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	@Autowired
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	@Autowired
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

}