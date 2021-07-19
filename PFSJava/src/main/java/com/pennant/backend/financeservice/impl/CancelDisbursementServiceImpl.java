package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.util.CollectionUtils;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.financeservice.CancelDisbursementService;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.impl.FinanceDataValidation;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.constants.FinServiceEvent;

public class CancelDisbursementServiceImpl extends GenericService<FinServiceInstruction>
		implements CancelDisbursementService {
	private static Logger logger = LogManager.getLogger(CancelDisbursementServiceImpl.class);

	private FinanceDataValidation financeDataValidation;
	private FinServiceInstrutionDAO finServiceInstructionDAO;

	public FinScheduleData getCancelDisbDetails(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		BigDecimal oldTotalPft = finScheduleData.getFinanceMain().getTotalGrossPft();

		// Schedule Recalculation Locking Period Applicability
		if (SysParamUtil.isAllowed(SMTParameterConstants.ALW_SCH_RECAL_LOCK)) {

			Date recalLockTill = finScheduleData.getFinanceMain().getRecalFromDate();
			if (recalLockTill == null) {
				recalLockTill = finScheduleData.getFinanceMain().getMaturityDate();
			}

			int sdSize = finScheduleData.getFinanceScheduleDetails().size();
			FinanceScheduleDetail curSchd = null;
			for (int i = 0; i <= sdSize - 1; i++) {

				curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
				if (DateUtility.compare(curSchd.getSchDate(), recalLockTill) < 0 && (i != sdSize - 1) && i != 0) {
					curSchd.setRecalLock(true);
				} else {
					curSchd.setRecalLock(false);
				}
			}
		}

		FinScheduleData finSchData = ScheduleCalculator.reCalSchd(finScheduleData, "");

		BigDecimal newTotalPft = finSchData.getFinanceMain().getTotalGrossPft();
		BigDecimal pftDiff = newTotalPft.subtract(oldTotalPft);
		finSchData.setPftChg(pftDiff);
		finSchData.getFinanceMain().setScheduleRegenerated(true);
		logger.debug("Leaving");
		return finSchData;
	}

	@Override
	public AuditDetail doValidations(FinanceDetail financeDetail, FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");
		AuditDetail auditDetail = new AuditDetail();

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		// validate disb amount
		if (finServiceInstruction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "Disbursement amount";
			valueParm[1] = String.valueOf(BigDecimal.ZERO);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
		}
		boolean isValidDate = false;
		List<FinAdvancePayments> finAdvancePayments = financeDetail.getAdvancePaymentsList();
		if (finAdvancePayments != null && !financeDetail.getAdvancePaymentsList().isEmpty()) {
			for (FinAdvancePayments finAdvancePayment : financeDetail.getAdvancePaymentsList()) {

				// Validate from date
				if (DateUtility.compare(finServiceInstruction.getFromDate(), finAdvancePayment.getLlDate()) == 0
						&& !(StringUtils.equals(finAdvancePayment.getStatus(), "CANCELED"))) {
					isValidDate = true;
				}
			}

			if (!isValidDate) {
				String[] valueParm = new String[1];
				valueParm[0] = "From date:" + DateUtility.formatToLongDate(finServiceInstruction.getFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30101", "", valueParm)));
				return auditDetail;
			}
		}

		List<ErrorDetail> errors = financeDataValidation.disbursementValidation(financeDetail);
		for (ErrorDetail errorDetails : errors) {
			auditDetail.setErrorDetail(errorDetails);
		}

		//validate fromDate and DisbDate
		isValidDate = false;
		if (DateUtility.compare(finServiceInstruction.getFromDate(),
				finServiceInstruction.getDisbursementDetails().get(0).getLlDate()) == 0) {
			isValidDate = true;
		}

		if (!isValidDate) {
			String[] valueParm = new String[2];
			valueParm[0] = "fromDate:" + DateUtility.formatToLongDate(finServiceInstruction.getFromDate());
			valueParm[1] = "disbDate:"
					+ DateUtility.formatToLongDate(finServiceInstruction.getDisbursementDetails().get(0).getLlDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("99017", "", valueParm)));
			return auditDetail;
		}

		//validate ServiceReqNo
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
				&& !(StringUtils.isEmpty(finServiceInstruction.getServiceReqNo()))) {
			List<FinServiceInstruction> finServiceInstructions = finServiceInstructionDAO.getFinServInstByServiceReqNo(
					finServiceInstruction.getFinReference(), finServiceInstruction.getFromDate(),
					finServiceInstruction.getServiceReqNo(), FinServiceEvent.ADDDISB);
			if (CollectionUtils.isNullOrEmpty(finServiceInstructions)) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getServiceReqNo();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("99026", "", valueParm)));
				return auditDetail;
			} else {
				List<FinServiceInstruction> finServInstCanReq = finServiceInstructionDAO.getFinServInstByServiceReqNo(
						finServiceInstruction.getFinReference(), finServiceInstruction.getFromDate(),
						finServiceInstruction.getServiceReqNo(), FinServiceEvent.CANCELDISB);

				if (!CollectionUtils.isNullOrEmpty(finServInstCanReq)) {
					String[] valueParm = new String[1];
					valueParm[0] = finServiceInstruction.getServiceReqNo();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("99027", "", valueParm)));
					return auditDetail;
				}
			}
		}

		logger.debug("Leaving");
		return auditDetail;
	}

	public void setFinanceDataValidation(FinanceDataValidation financeDataValidation) {
		this.financeDataValidation = financeDataValidation;
	}

	public FinServiceInstrutionDAO getFinServiceInstructionDAO() {
		return finServiceInstructionDAO;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

}
