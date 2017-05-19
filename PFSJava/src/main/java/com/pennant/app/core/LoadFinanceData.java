package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.constants.ImplementationConstants;
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
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
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
		Date valueDate = custEODEvent.getEodValueDate();
		Date businesdate = DateUtility.addDays(custEODEvent.getEodValueDate(), 1);
		List<FinanceMain> custFinMains = getFinanceMainDAO().getFinMainsForEODByCustId(custID, true);

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

			//FINPROFIT DETAILS
			finEODEvent.setFinProfitDetail(getFinanceProfitDetailRef(finReference, listprofitDetails));

			//FINSCHDULE DETAILS
			List<FinanceScheduleDetail> finSchdDetails = getFinanceScheduleDetailDAO().getFinScheduleDetails(
					finReference, TableType.MAIN_TAB.getSuffix(), false);
			finEODEvent.setFinanceScheduleDetails(finSchdDetails);

			//Place schedule dates to Map
			for (int j = 0; j < finSchdDetails.size(); j++) {
				datesMap.put(finSchdDetails.get(j).getSchDate(), j);
			}

			finEODEvent.setDatesMap(datesMap);

			int idx = getIndexFromMap(datesMap, businesdate);

			if (idx != 0) {
				FinanceScheduleDetail curSchd = finSchdDetails.get(idx);

				//Disbursement Exist
				if (curSchd.isDisbOnSchDate()) {
					finEODEvent.setDisbExist(true);
				}

				//Fee Due Exist
				BigDecimal dueAmount = curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid());
				if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
					finEODEvent.setFeeDueExist(true);
				}

				//Insurance Due Exist
				dueAmount = curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid());

				if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
					finEODEvent.setInsuranceDueExist(true);
				}

				//Installment Due Exist
				dueAmount = curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()).subtract(curSchd.getSchdPriPaid())
						.subtract(curSchd.getSchdPftPaid());

				if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
					finEODEvent.setInstDueExist(true);
				}
				
				//Presentment Required
				if (curSchd.getDefSchdDate().compareTo(businesdate)==0 && curSchd.getPresentmentId() != 0) {
					finEODEvent.setCheckPresentment(true);
				}
				
				//Do not Include Today Late payment Calculation
				if (!finEODEvent.isPastDueExist() && curSchd.getSchDate().compareTo(valueDate) < 0) {
					boolean isAmountDue = false;
					//Paid Principal OR Paid Interest Less than scheduled amounts 
					if (curSchd.getSchdPriPaid().compareTo(curSchd.getPrincipalSchd()) < 0
							|| curSchd.getSchdPftPaid().compareTo(curSchd.getProfitSchd()) < 0) {
						isAmountDue = true;
					}

					//Islamic Implementation
					if (ImplementationConstants.IMPLEMENTATION_ISLAMIC) {
						//Paid Supplementary rent OR Paid Increase Cost less than scheduled amounts 
						if (curSchd.getSuplRentPaid().compareTo(curSchd.getSuplRent()) < 0
								|| curSchd.getIncrCostPaid().compareTo(curSchd.getIncrCost()) < 0) {
							isAmountDue = true;
						}
					}

					if (isAmountDue) {
						finEODEvent.setPastDueExist(true);
					}
	
				}
			}

			custEODEvent.getFinEODEvents().add(finEODEvent);

		}

		//clear temporary data
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
				//finEODEvent.getFinanceMain().setVersion(finEODEvent.getFinanceMain().getVersion() + 1);
				getFinanceMainDAO().updateFinanceInEOD(finEODEvent.getFinanceMain(),finEODEvent.getFinMainUpdateFields(),
						finEODEvent.isupdFinSchdForRateRvw());
			}

			//update profit details
			if (finEODEvent.isUpdFinPft()) {
				getFinanceProfitDetailDAO().update(finEODEvent.getFinProfitDetail(), false);
			}

			//update schedule details 
			if (finEODEvent.isupdFinSchdForRateRvw()) {
				getFinanceScheduleDetailDAO().updateForRateReview(finEODEvent.getFinanceScheduleDetails());
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

	public void updateStart(int threadId, long custId) {

		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setCustID(custId);
		customerQueuing.setThreadId(threadId);
		customerQueuing.setStartTime(DateUtility.getSysDate());
		customerQueuing.setProgress(EodConstants.PROGRESS_IN_PROCESS);
		getCustomerQueuingDAO().update(customerQueuing, true);

	}

	public void updateEnd(int threadId, long custId) {

		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setCustID(custId);
		customerQueuing.setThreadId(threadId);
		customerQueuing.setEndTime(DateUtility.getSysDate());
		customerQueuing.setProgress(EodConstants.PROGRESS_SUCCESS);
		getCustomerQueuingDAO().update(customerQueuing, false);

	}

	private FinanceProfitDetail getFinanceProfitDetailRef(String finMainRef, List<FinanceProfitDetail> listprofitDetails) {
		FinanceProfitDetail profitDetail = null;
		Iterator<FinanceProfitDetail> it = listprofitDetails.iterator();
		while (it.hasNext()) {
			FinanceProfitDetail financeProfitDetail = (FinanceProfitDetail) it.next();
			if (StringUtils.equals(financeProfitDetail.getFinReference(), finMainRef)) {
				profitDetail = financeProfitDetail;
				it.remove();
				break;
			}
		}
		return profitDetail;
	}

}