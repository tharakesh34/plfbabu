package com.pennant.backend.financeservice.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.financeservice.HoldEMIService;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.FinanceConstants;

public class HoldEMIServiceImpl extends GenericService<FinServiceInstruction> implements HoldEMIService {
	private static Logger logger = Logger.getLogger(AddRepaymentServiceImpl.class);

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;

	public HoldEMIServiceImpl(){
		super();
	}
	/***
	 * 	if the holdEmiFromDate and the ScheduleDetails fromDate are matched then set the Default ScheduleDate with todate
	 * 	and Set BPI Holiday as 'S'
	 */
	@Override
	public FinScheduleData getHoldEmiDetails(FinScheduleData finscheduleData,FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		for(FinanceScheduleDetail finschdDetail :finscheduleData.getFinanceScheduleDetails()){
			if(DateUtility.compare(finschdDetail.getSchDate(), finServiceInstruction.getFromDate())==0){
				finschdDetail.setDefSchdDate(finServiceInstruction.getToDate());
				finschdDetail.setBpiOrHoliday(FinanceConstants.FLAG_HOLDEMI);
				break;
			}
		}
		
		logger.debug("Leaving");
		return finscheduleData;
	}
	/***
	 *  Validation for the holdEmi From and ToDate
	 */
	@Override
	public AuditDetail doValidations(FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		AuditDetail auditDetail = new AuditDetail();
		String lang = "EN";
		boolean isValidCheck = true;
		int holdemidays = SysParamUtil.getValueAsInt("HOLDEMI_MAXDAYS");
		Date datehldEMIAlwd = DateUtility.addDays(finServiceInstruction.getFromDate(),holdemidays);
		// To Date cannot be greater than the HoldEMi ALwd Days
		if(DateUtility.compare(finServiceInstruction.getToDate(),datehldEMIAlwd) > 0) {
			isValidCheck = false;
			String[] valueParm = new String[2];
			valueParm[0] = "ToDate";
			valueParm[1] = DateUtility.formatToShortDate(datehldEMIAlwd);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("30551", "", valueParm), lang));
		}else if(DateUtility.compare(finServiceInstruction.getToDate(),finServiceInstruction.getFromDate()) < 0){
				isValidCheck = false;
				// To Date cannot be greater than the HoldEMi ALwd Days
				String[] valueParm = new String[2];
				valueParm[0] = "ToDate";
				valueParm[1] = DateUtility.formatToShortDate(finServiceInstruction.getFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91125", "", valueParm), lang));
		
		}
		//validation for Todate to check whether it is greater than next Schedule if yes then throw the validation
		if(isValidCheck){
			List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finServiceInstruction.getFinReference(), "", false);
			Date nextSchdDate = null ;
			for( int i=0;i<schedules.size();i++){
				if(DateUtility.compare(schedules.get(i).getSchDate(),finServiceInstruction.getFromDate())==0){
					nextSchdDate = schedules.get(i+1).getSchDate();
					break;
				}
			}
			if(DateUtility.compare(finServiceInstruction.getToDate(),nextSchdDate) > 0){
				isValidCheck = false;
				// To Date cannot be greater than the HoldEMi ALwd Days
				String[] valueParm = new String[2];
				valueParm[0] = "ToDate";
				valueParm[1] = DateUtility.formatToShortDate(nextSchdDate);
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("30551", "", valueParm), lang));

			}
		}
		logger.debug("Leaving");
		return auditDetail;
	}
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

}
