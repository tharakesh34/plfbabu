package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.financeservice.AddTermsService;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class AddTermsServiceImpl extends GenericService<FinServiceInstruction> implements AddTermsService {
	private static Logger logger = LogManager.getLogger(AddTermsServiceImpl.class);

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;

	public FinScheduleData getAddTermsDetails(FinScheduleData schdData, FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = schdData.getFinanceMain();
		BigDecimal oldTotalPft = fm.getTotalGrossPft();

		// Schedule Recalculation Locking Period Applicability.
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

		schdData = ScheduleCalculator.addTerm(schdData, fsi.getTerms());

		BigDecimal newTotalPft = fm.getTotalGrossPft();
		BigDecimal pftDiff = newTotalPft.subtract(oldTotalPft);

		schdData.setPftChg(pftDiff);
		fm.setScheduleRegenerated(true);

		logger.debug(Literal.LEAVING);
		return schdData;
	}

	/**
	 * 
	 * 
	 */
	@Override
	public AuditDetail doValidations(FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = new AuditDetail();
		String lang = "EN";

		// validate Instruction details
		boolean isWIF = fsi.isWif();
		long finID = fsi.getFinID();

		Date appDate = SysParamUtil.getAppDate();
		if (DateUtil.compare(fsi.getRecalFromDate(), appDate) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "Recal From Date";
			valueParm[1] = "application date:" + DateUtil.formatToLongDate(appDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30512", "", valueParm), lang));
			return auditDetail;
		}

		if (fsi.getRecalFromDate() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "Recal From Date";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
			return auditDetail;
		}
		if (fsi.getTerms() <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "Terms";
			valueParm[1] = String.valueOf(BigDecimal.ZERO);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm)));
			return auditDetail;
		}

		boolean isValidRecalFromDate = false;
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", isWIF);
		if (schedules != null) {
			for (FinanceScheduleDetail schDetail : schedules) {
				// FromDate
				if (DateUtil.compare(fsi.getRecalFromDate(), schDetail.getSchDate()) == 0) {
					isValidRecalFromDate = true;
				}
				// RecalFromDate
				if (DateUtil.compare(fsi.getRecalFromDate(), schDetail.getSchDate()) == 0) {
					isValidRecalFromDate = true;
					if (checkIsValidRepayDate(auditDetail, schDetail, "RecalFromDate") != null) {
						return auditDetail;
					}
				}
			}

			if (!isValidRecalFromDate) {
				String[] valueParm = new String[1];
				valueParm[0] = "RecalFromDate:" + DateUtil.formatToShortDate(fsi.getFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
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
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90261", "", valueParm)));
			return auditDetail;
		}
		return null;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}
}
