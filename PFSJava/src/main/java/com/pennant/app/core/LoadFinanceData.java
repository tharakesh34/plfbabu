package com.pennant.app.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.util.BusinessCalendar;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customerqueuing.CustomerQueuing;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pff.core.TableType;

public class LoadFinanceData extends ServiceHelper {

	private static Logger		logger				= Logger.getLogger(LoadFinanceData.class);

	private static final long	serialVersionUID	= -281578785120363314L;

	public CustEODEvent prepareFinEODEvents(CustEODEvent custEODEvent, long custId) throws Exception {
		logger.debug(" Entering ");

		custEODEvent.setCustomer(getCustomerDAO().getCustomerEOD(custId));

		long custID = custEODEvent.getCustomer().getCustID();

		//For SOD Operations
		Date businesdate = DateUtility.addDays(custEODEvent.getEodValueDate(), 1);
		List<FinanceMain> custFinMains = getFinanceMainDAO().getFinanceMainsByCustId(custID, true);

		//FIXME: PV 14MAY17 to be moved to finEODEvent related to presentment
		//List<PresentmentDetail> presentments = getPresentmentHeaderDAO().getPresentmenToPost(custID, businesdate);
		List<FinanceProfitDetail> listprofitDetails = getFinanceProfitDetailDAO().getFinProfitDetailsByCustId(custID,
				true);

		for (int i = 0; i < custFinMains.size(); i++) {
			FinEODEvent finEODEvent = new FinEODEvent();
			Map<Date, Integer> datesMap = new HashMap<Date, Integer>();

			//FINANCE MAIN
			finEODEvent.setFinanceMain(custFinMains.get(i));
			String finType = finEODEvent.getFinanceMain().getFinType();
			String finReference = finEODEvent.getFinanceMain().getFinReference();

			//FINANCE TYPE
			FinanceType financeType = getFinanceType(finType);
			finEODEvent.setFinType(financeType);
			//FIXME: PV 14MAY17 What about promotions

			//FINSCHDULE DETAILS
			List<FinanceScheduleDetail> finSchdDetails = getFinanceScheduleDetailDAO().getFinScheduleDetails(
					finReference, TableType.MAIN_TAB.getSuffix(), false);

			//Place schedule dates to Map
			for (int j = 0; j < finSchdDetails.size(); j++) {
				datesMap.put(finSchdDetails.get(j).getSchDate(), j);
			}

			finEODEvent.setDatesMap(datesMap);
			finEODEvent.setFinanceScheduleDetails(finSchdDetails);
			finEODEvent.setFinanceDisbursements(getFinanceDisbursementDAO().getDisbursementToday(finReference,
					businesdate));

			//fin fee schedule
			finEODEvent.setFinFeeScheduleDetails(getFinFeeScheduleDetailDAO()
					.getFeeSchdTPost(finReference, businesdate));

			// fin insurance schedule
			finEODEvent.setFinSchFrqInsurances(getFinInsurancesDAO().getInsSchdToPost(finReference, businesdate));

			//FINPROFIT DETAILS
			finEODEvent.setFinProfitDetail(getFinanceProfitDetailRef(finReference, listprofitDetails));

			//FIXME: PV 14MAY17 to be moved to finEODEvent related to presentment
			/*
			 * PresentmentDetail presentment = getPresentmentDetailbyRef(finReference, presentments); if (presentment !=
			 * null) { finEODEvent.getPresentmentDetails().add(presentment); }
			 */

			custEODEvent.getFinEODEvents().add(finEODEvent);

		}

		//clear temporary data
		//FIXME: PV 14MAY17 to be moved to finEODEvent related to presentment
		//presentments.clear();
		listprofitDetails.clear();
		custFinMains.clear();
		logger.debug(" Leaving ");
		return custEODEvent;
	}

	public void updateFinEODEvents(CustEODEvent custEODEvent) throws Exception {
		logger.debug(" Entering ");
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		List<ReturnDataSet> returnDataSets = new ArrayList<ReturnDataSet>(1);

		for (FinEODEvent finEODEvent : finEODEvents) {

			//update finance main
			if (finEODEvent.isUpdFinMain()) {
				finEODEvent.getFinanceMain().setVersion(finEODEvent.getFinanceMain().getVersion() + 1);
				getFinanceMainDAO().updateFinanceInEOD(finEODEvent.getFinanceMain());
			}

			//update profit details
			if (finEODEvent.isUpdFinPft()) {
				getFinanceProfitDetailDAO().update(finEODEvent.getFinProfitDetail(), false);
			}

			//update schedule details 
			if (finEODEvent.isUpdFinSchedule()) {
				getFinanceScheduleDetailDAO().updateList(finEODEvent.getFinanceScheduleDetails(), "");
			}

			//Update overdue details
			List<FinODDetails> odDetails = finEODEvent.getFinODDetails();
			if (odDetails != null && !odDetails.isEmpty()) {
				for (FinODDetails finODDetails : odDetails) {
					if (StringUtils.equals(finODDetails.getRcdAction(), PennantConstants.RECORD_INSERT)) {
						if (finODDetails.getFinCurODDays() != 0) {
							getFinODDetailsDAO().save(finODDetails);
						}
					} else if (StringUtils.equals(finODDetails.getRcdAction(), PennantConstants.RECORD_UPDATE)) {
						getFinODDetailsDAO().update(finODDetails);
					}
				}
			}

			//update repay instruction
			if (finEODEvent.isUpdRepayInstruct()) {

				getRepayInstructionDAO().deleteByFinReference(finEODEvent.getFinanceMain().getFinReference(), "",
						false, 0);
				//Add repay instructions
				List<RepayInstruction> lisRepayIns = finEODEvent.getRepayInstructions();
				for (RepayInstruction repayInstruction : lisRepayIns) {
					repayInstruction.setFinReference(finEODEvent.getFinanceMain().getFinReference());
				}
				getRepayInstructionDAO().saveList(lisRepayIns, "", false);
			}

			returnDataSets.addAll(finEODEvent.getReturnDataSet());
		}

		getAccountProcessUtil().procAccountUpdate(returnDataSets);

		if (custEODEvent.isUpdCustomer()) {
			Customer customer = custEODEvent.getCustomer();
			getCustomerDAO().updateCustStatus(customer.getCustSts(), customer.getCustStsChgDate(),
					custEODEvent.getCustomer().getCustID());
		}

		logger.debug(" Leaving ");
		returnDataSets.clear();
	}

	public void updateCustomerDate(long custId, Date date) {
		logger.debug(" Entering ");
		String localCcy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);
		Calendar nextBusDate = BusinessCalendar.getWorkingBussinessDate(localCcy, HolidayHandlerTypes.MOVE_NEXT, date);
		getCustomerDAO().updateCustAppDate(custId, nextBusDate.getTime());
		logger.debug(" Leaving ");
	}

	public void updateEnd(Date date, long custId) {

		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setCustID(custId);
		customerQueuing.setEodDate(date);
		customerQueuing.setProgress(EodConstants.PROGRESS_SUCCESS);
		getCustomerQueuingDAO().updateProgress(customerQueuing);

	}

	private PresentmentDetail getPresentmentDetailbyRef(String finMainRef, List<PresentmentDetail> presentments) {
		for (PresentmentDetail presentmentDetail : presentments) {
			if (StringUtils.equals(presentmentDetail.getFinReference(), finMainRef)) {
				return presentmentDetail;
			}
		}
		return null;
	}

	private FinanceProfitDetail getFinanceProfitDetailRef(String finMainRef, List<FinanceProfitDetail> listprofitDetails) {
		for (FinanceProfitDetail financeProfitDetail : listprofitDetails) {
			if (StringUtils.equals(financeProfitDetail.getFinReference(), finMainRef)) {
				return financeProfitDetail;
			}
		}
		return null;
	}

}