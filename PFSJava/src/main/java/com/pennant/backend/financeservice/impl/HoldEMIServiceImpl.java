package com.pennant.backend.financeservice.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.financeservice.HoldEMIService;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;

public class HoldEMIServiceImpl extends GenericService<FinServiceInstruction> implements HoldEMIService {
	private static Logger logger = LogManager.getLogger(AddRepaymentServiceImpl.class);

	public HoldEMIServiceImpl() {
		super();
	}

	/***
	 * if the holdEmiFromDate and the ScheduleDetails fromDate are matched then set the Default ScheduleDate with todate
	 * and Set BPI Holiday as 'S'
	 */
	@Override
	public FinScheduleData getHoldEmiDetails(FinScheduleData finscheduleData,
			FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		for (FinanceScheduleDetail finschdDetail : finscheduleData.getFinanceScheduleDetails()) {
			if (DateUtil.compare(finschdDetail.getSchDate(), finServiceInstruction.getFromDate()) == 0) {
				finschdDetail.setDefSchdDate(finServiceInstruction.getToDate());
				finschdDetail.setBpiOrHoliday(FinanceConstants.FLAG_HOLDEMI);
				break;
			}
		}

		logger.debug("Leaving");
		return finscheduleData;
	}

	/***
	 * Validation for the holdEmi From and ToDate
	 */
	@Override
	public AuditDetail doValidations(FinScheduleData finscheduleData, FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		AuditDetail auditDetail = new AuditDetail();
		String lang = "EN";
		boolean isValidCheck = true;
		int holdemidays = SysParamUtil.getValueAsInt("HOLDEMI_MAXDAYS");
		Date datehldEMIAlwd = DateUtil.addDays(finServiceInstruction.getFromDate(), holdemidays);
		// To Date cannot be greater than the HoldEMi ALwd Days
		if (DateUtil.compare(finServiceInstruction.getToDate(), datehldEMIAlwd) > 0
				|| DateUtil.compare(finServiceInstruction.getToDate(), finServiceInstruction.getFromDate()) < 0) {
			isValidCheck = false;
			String[] valueParm = new String[3];
			valueParm[0] = Labels.getLabel("label_HoldEMIDialog_ToDate.value");
			valueParm[1] = DateUtil.formatToLongDate(finServiceInstruction.getFromDate());
			valueParm[2] = DateUtil.formatToLongDate(datehldEMIAlwd);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30567", "", valueParm), lang));
		}
		// validation for Todate to check whether it is greater than next Schedule if yes then throw the validation
		if (isValidCheck) {
			List<FinanceScheduleDetail> schedules = finscheduleData.getFinanceScheduleDetails();
			Date nextSchdDate = null;
			for (int i = 0; i < schedules.size(); i++) {
				if (DateUtil.compare(schedules.get(i).getSchDate(), finServiceInstruction.getFromDate()) == 0) {
					nextSchdDate = schedules.get(i + 1).getSchDate();
					break;
				}
			}
			if (DateUtil.compare(finServiceInstruction.getToDate(), nextSchdDate) > 0) {
				isValidCheck = false;
				// To Date cannot be greater than the HoldEMi ALwd Days
				String[] valueParm = new String[2];
				valueParm[0] = Labels.getLabel("label_HoldEMIDialog_ToDate.value");
				valueParm[1] = DateUtil.formatToShortDate(nextSchdDate);
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("65028", "", valueParm), lang));

			}
		}
		logger.debug("Leaving");
		return auditDetail;
	}
}
