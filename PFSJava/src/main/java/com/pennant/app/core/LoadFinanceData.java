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
import com.pennant.app.util.FrequencyUtil;
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

public class LoadFinanceData extends ServiceHelper {

	private static Logger		logger				= Logger.getLogger(LoadFinanceData.class);

	private static final long	serialVersionUID	= -281578785120363314L;

	public CustEODEvent prepareFinEODEvents(CustEODEvent custEODEvent, long custId) throws Exception {
		logger.debug(" Entering ");

		custEODEvent.setCustomer(getCustomerDAO().getCustomerEOD(custId));

		long custID = custEODEvent.getCustomer().getCustID();
		List<FinanceMain> custFinMains = getFinanceMainDAO().getFinMainsForEODByCustId(custID, true);
		List<FinanceScheduleDetail> custfinSchdDetails = getFinanceScheduleDetailDAO().getFinScheduleDetails(custID,
				true);

		List<FinanceProfitDetail> listprofitDetails = getFinanceProfitDetailDAO().getFinProfitDetailsByCustId(custID,
				true);

		for (int i = 0; i < custFinMains.size(); i++) {
			FinEODEvent finEODEvent = new FinEODEvent();

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
			List<FinanceScheduleDetail> finSchdDetails = getFinanceScheduleDetailRef(finReference, custfinSchdDetails);
			finEODEvent.setFinanceScheduleDetails(finSchdDetails);

			custEODEvent.getFinEODEvents().add(finEODEvent);
			setEventFlags(custEODEvent, i);
		}

		//clear temporary data
		listprofitDetails.clear();
		custFinMains.clear();
		logger.debug(" Leaving ");
		return custEODEvent;
	}

	public void setEventFlags(CustEODEvent custEODEvent, int iFinEvent) throws Exception {

		Map<Date, Integer> datesMap = new HashMap<Date, Integer>();
		FinEODEvent finEODEvent = custEODEvent.getFinEODEvents().get(iFinEvent);
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();
		Date valueDate = custEODEvent.getEodValueDate();
		Date businessDate = DateUtility.addDays(custEODEvent.getEodValueDate(), 1);

		boolean isAmountDue = false;

		//Place schedule dates to Map
		for (int i = 0; i < finSchdDetails.size(); i++) {
			FinanceScheduleDetail curSchd = finSchdDetails.get(i);
			datesMap.put(finSchdDetails.get(i).getSchDate(), i);

			//Find various events required today or not
			if (curSchd.getSchDate().compareTo(valueDate) == 0) {
				//Disbursement Exist
				if (curSchd.isDisbOnSchDate()) {
					finEODEvent.setIdxDisb(i);
					custEODEvent.setDisbExist(true);
				}

				//Fee Due Exist
				BigDecimal dueAmount = curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid());
				if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
					finEODEvent.setIdxDue(i);
					custEODEvent.setDueExist(true);
				}

				//Insurance Due Exist
				dueAmount = curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid());

				if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
					finEODEvent.setIdxDue(i);
					custEODEvent.setDueExist(true);
				}

				//Installment Due Exist
				dueAmount = curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()).subtract(curSchd.getSchdPriPaid())
						.subtract(curSchd.getSchdPftPaid());

				if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
					finEODEvent.setIdxDue(i);
					custEODEvent.setDueExist(true);
				}

				//Presentment Required
				if (curSchd.getDefSchdDate().compareTo(businessDate) == 0 && curSchd.getPresentmentId() != 0) {
					finEODEvent.setIdxPresentment(i);
					custEODEvent.setCheckPresentment(true);
				}
			}

			//Date Rollover Setting
			if (curSchd.getSchDate().compareTo(businessDate) == 0) {
				setDateRollover(custEODEvent, iFinEvent, curSchd.getSchDate(), i);
			}

			//PastDue Index Setting
			if (finEODEvent.getIdxPD() > 0) {
				continue;
			}

			//Do not Include Today Late payment Calculation
			if (curSchd.getSchDate().compareTo(valueDate) >= 0) {
				continue;
			}

			//Paid Principal OR Paid Interest Less than scheduled amounts 
			if (curSchd.getSchdPriPaid().compareTo(curSchd.getPrincipalSchd()) < 0
					|| curSchd.getSchdPftPaid().compareTo(curSchd.getProfitSchd()) < 0) {
				isAmountDue = true;
			} else {
				//Islamic Implementation
				if (ImplementationConstants.IMPLEMENTATION_ISLAMIC) {
					//Paid Supplementary rent OR Paid Increase Cost less than scheduled amounts 
					if (curSchd.getSuplRentPaid().compareTo(curSchd.getSuplRent()) < 0
							|| curSchd.getIncrCostPaid().compareTo(curSchd.getIncrCost()) < 0) {
						isAmountDue = true;
					}
				}
			}

			if (isAmountDue) {
				finEODEvent.setIdxPD(i);
				custEODEvent.setPastDueExist(true);
			}

		}

		finEODEvent.setDatesMap(datesMap);

	}

	public void setDateRollover(CustEODEvent custEODEvent, int iFinEvent, Date schdDate, int iSchd) throws Exception {
		FinEODEvent finEODEvent = custEODEvent.getFinEODEvents().get(iFinEvent);
		FinanceMain finMain = finEODEvent.getFinanceMain();

		if (finMain.isAllowGrcPeriod() && schdDate.compareTo(finMain.getGrcPeriodEndDate()) < 0) {
			//Set Next Grace Capitalization Date
			if (finMain.isAllowGrcCpz() && finMain.getNextGrcCpzDate().compareTo(finMain.getGrcPeriodEndDate()) < 0
					&& schdDate.compareTo(finMain.getNextGrcCpzDate()) == 0) {
				finEODEvent.setIdxGrcCpz(iSchd);
				custEODEvent.setDateRollover(true);
			}

			//Set Next Grace Profit Date
			if (finMain.getNextGrcPftDate().compareTo(finMain.getGrcPeriodEndDate()) < 0
					&& schdDate.compareTo(finMain.getNextGrcPftDate()) == 0) {
				finEODEvent.setIdxGrcPft(iSchd);
				custEODEvent.setDateRollover(true);
			}

			//Set Next Grace Profit Review Date
			if (finMain.isAllowGrcPftRvw()
					&& finMain.getNextGrcPftRvwDate().compareTo(finMain.getGrcPeriodEndDate()) < 0
					&& schdDate.compareTo(finMain.getNextGrcPftRvwDate()) == 0) {
				finEODEvent.setIdxGrcPftRvw(iSchd);
				custEODEvent.setDateRollover(true);
			}
		}

		//Set Next Repay Capitalization Date

		if (finMain.isAllowRepayCpz() && schdDate.compareTo(finMain.getNextRepayCpzDate()) == 0) {
			finEODEvent.setIdxRpyCpz(iSchd);
			custEODEvent.setDateRollover(true);
		}

		//Set Next Repayment Date
		if (schdDate.compareTo(finMain.getNextRepayDate()) == 0) {
			finEODEvent.setIdxRpy(iSchd);
			custEODEvent.setDateRollover(true);
		}

		//Set Next Repayment Profit Date
		if (schdDate.compareTo(finMain.getNextRepayPftDate()) == 0) {
			finEODEvent.setIdxRpyPft(iSchd);
			custEODEvent.setDateRollover(true);
		}

		//Set Next Repayment Profit Review Date
		if (finMain.isAllowRepayRvw()) {
			if (schdDate.compareTo(finMain.getNextRepayRvwDate()) == 0) {
				finEODEvent.setIdxRpyPftRvw(iSchd);
				custEODEvent.setDateRollover(true);
			}
		}

		//Set Next Depreciation Date
		if (finMain.getNextDepDate() != null && schdDate.compareTo(finMain.getNextDepDate()) == 0) {
			if (!StringUtils.isEmpty(finMain.getDepreciationFrq())) {
				if (finMain.getNextDepDate().compareTo(finMain.getMaturityDate()) < 0) {
					finMain.setNextDepDate(FrequencyUtil
							.getNextDate(finMain.getDepreciationFrq(), 1, schdDate, "A", false).getNextFrequencyDate());
				}

				if (finMain.getNextDepDate().compareTo(finMain.getMaturityDate()) > 0) {
					finMain.setNextDepDate(finMain.getMaturityDate());
				}

				finEODEvent.setUpdFinMain(true);
				finEODEvent.addToFinMianUpdate("NextDepDate");
			}
		}

	}

	public void updateFinEODEvents(CustEODEvent custEODEvent) throws Exception {
		logger.debug(" Entering ");
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		List<ReturnDataSet> returnDataSets = new ArrayList<ReturnDataSet>(1);

		for (FinEODEvent finEODEvent : finEODEvents) {

			//update finance main
			if (finEODEvent.isUpdFinMain()) {
				//finEODEvent.getFinanceMain().setVersion(finEODEvent.getFinanceMain().getVersion() + 1);
				getFinanceMainDAO().updateFinanceInEOD(finEODEvent.getFinanceMain(),
						finEODEvent.getFinMainUpdateFields(), finEODEvent.isupdFinSchdForRateRvw());
			}

			//update profit details
			getFinanceProfitDetailDAO().update(finEODEvent.getFinProfitDetail(), false);

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

				getRepayInstructionDAO().deleteByFinReference(finEODEvent.getFinanceMain().getFinReference(), "", false,
						0);
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

	private FinanceProfitDetail getFinanceProfitDetailRef(String finMainRef,
			List<FinanceProfitDetail> listprofitDetails) {
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

	private List<FinanceScheduleDetail> getFinanceScheduleDetailRef(String finMainRef,
			List<FinanceScheduleDetail> financeScheduleDetails) {
		List<FinanceScheduleDetail> finSchedulelist = new ArrayList<FinanceScheduleDetail>();
		Iterator<FinanceScheduleDetail> it = financeScheduleDetails.iterator();
		while (it.hasNext()) {
			FinanceScheduleDetail financeProfitDetail = (FinanceScheduleDetail) it.next();
			if (StringUtils.equals(financeProfitDetail.getFinReference(), finMainRef)) {
				finSchedulelist.add(financeProfitDetail);
				it.remove();
			}
		}
		return finSchedulelist;
	}
}