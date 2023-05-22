package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.util.CollectionUtils;
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
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

public class CancelDisbursementServiceImpl extends GenericService<FinServiceInstruction>
		implements CancelDisbursementService {
	private static Logger logger = LogManager.getLogger(CancelDisbursementServiceImpl.class);

	private FinanceDataValidation financeDataValidation;
	private FinServiceInstrutionDAO finServiceInstructionDAO;

	public FinScheduleData getCancelDisbDetails(FinScheduleData schdData) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = schdData.getFinanceMain();
		BigDecimal oldTotalPft = fm.getTotalGrossPft();

		// Schedule Recalculation Locking Period Applicability
		if (SysParamUtil.isAllowed(SMTParameterConstants.ALW_SCH_RECAL_LOCK)) {
			Date recalLockTill = fm.getRecalFromDate();
			if (recalLockTill == null) {
				recalLockTill = fm.getMaturityDate();
			}

			int sdSize = schdData.getFinanceScheduleDetails().size();
			FinanceScheduleDetail curSchd = null;
			for (int i = 0; i <= sdSize - 1; i++) {

				curSchd = schdData.getFinanceScheduleDetails().get(i);
				if (DateUtil.compare(curSchd.getSchDate(), recalLockTill) < 0 && (i != sdSize - 1) && i != 0) {
					curSchd.setRecalLock(true);
				} else {
					curSchd.setRecalLock(false);
				}
			}
		}

		FinScheduleData finSchData = ScheduleCalculator.reCalSchd(schdData, "");

		BigDecimal newTotalPft = fm.getTotalGrossPft();
		BigDecimal pftDiff = newTotalPft.subtract(oldTotalPft);
		finSchData.setPftChg(pftDiff);
		finSchData.getFinanceMain().setScheduleRegenerated(true);

		logger.debug(Literal.LEAVING);
		return finSchData;
	}

	@Override
	public AuditDetail doValidations(FinanceDetail fd, FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = new AuditDetail();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		// validate disb amount
		if (fsi.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "Disbursement amount";
			valueParm[1] = String.valueOf(BigDecimal.ZERO);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
		}
		boolean isValidDate = false;
		List<FinAdvancePayments> finAdvancePayments = fd.getAdvancePaymentsList();
		Date fromDate = fsi.getFromDate();
		if (finAdvancePayments != null && !fd.getAdvancePaymentsList().isEmpty()) {
			for (FinAdvancePayments finAdvancePayment : fd.getAdvancePaymentsList()) {

				// Validate from date
				if (DateUtil.compare(fromDate, finAdvancePayment.getLlDate()) == 0
						&& !(StringUtils.equals(finAdvancePayment.getStatus(), "CANCELED"))) {
					isValidDate = true;
				}
			}

			if (!isValidDate) {
				String[] valueParm = new String[1];
				valueParm[0] = "From date:" + DateUtil.formatToLongDate(fromDate);
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30101", "", valueParm)));
				return auditDetail;
			}
		}

		List<ErrorDetail> errors = financeDataValidation.disbursementValidation(fd);
		for (ErrorDetail errorDetails : errors) {
			auditDetail.setErrorDetail(errorDetails);
		}

		// validate fromDate and DisbDate
		isValidDate = false;
		if (DateUtil.compare(fromDate, fsi.getDisbursementDetails().get(0).getLlDate()) == 0) {
			isValidDate = true;
		}

		if (!isValidDate) {
			String[] valueParm = new String[2];
			valueParm[0] = "fromDate:" + DateUtil.formatToLongDate(fromDate);
			valueParm[1] = "disbDate:" + DateUtil.formatToLongDate(fsi.getDisbursementDetails().get(0).getLlDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("99017", "", valueParm)));
			return auditDetail;
		}

		// validate ServiceReqNo
		String serviceReqNo = fsi.getServiceReqNo();
		long finID = fsi.getFinID();

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())
				&& !(StringUtils.isEmpty(serviceReqNo))) {

			List<FinServiceInstruction> finServiceInstructions = finServiceInstructionDAO
					.getFinServInstByServiceReqNo(finID, fromDate, serviceReqNo, FinServiceEvent.ADDDISB);
			if (CollectionUtils.isNullOrEmpty(finServiceInstructions)) {
				String[] valueParm = new String[1];
				valueParm[0] = serviceReqNo;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("99026", "", valueParm)));
				return auditDetail;
			} else {
				List<FinServiceInstruction> finServInstCanReq = finServiceInstructionDAO
						.getFinServInstByServiceReqNo(finID, fromDate, serviceReqNo, FinServiceEvent.CANCELDISB);

				if (!CollectionUtils.isNullOrEmpty(finServInstCanReq)) {
					String[] valueParm = new String[1];
					valueParm[0] = serviceReqNo;
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("99027", "", valueParm)));
					return auditDetail;
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public void setFinanceDataValidation(FinanceDataValidation financeDataValidation) {
		this.financeDataValidation = financeDataValidation;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

}
