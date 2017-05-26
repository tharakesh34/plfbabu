package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pff.core.TableType;

public class LoadFinanceData extends ServiceHelper {

	private static Logger		logger				= Logger.getLogger(LoadFinanceData.class);

	private static final long	serialVersionUID	= -281578785120363314L;

	public CustEODEvent prepareFinEODEvents(CustEODEvent custEODEvent, long custID) throws Exception {
		logger.debug(" Entering ");

		List<FinanceMain> custFinMains = getFinanceMainDAO().getFinMainsForEODByCustId(custID, true);
		//List<FinanceScheduleDetail> custSchdDetails = getFinanceScheduleDetailDAO().getFinScheduleDetails(custID, true);
		List<FinanceProfitDetail> custpftDet = getFinanceProfitDetailDAO().getFinProfitDetailsByCustId(custID, true);

		for (FinanceMain main : custFinMains) {
			FinEODEvent finEODEvent = new FinEODEvent();

			//FINANCE MAIN
			finEODEvent.setFinanceMain(main);
			String finType = finEODEvent.getFinanceMain().getFinType();
			String finReference = finEODEvent.getFinanceMain().getFinReference();

			//FINANCE TYPE
			FinanceType financeType = getFinanceType(finType);
			finEODEvent.setFinType(financeType);

			//FINPROFIT DETAILS
			finEODEvent.setFinProfitDetail(getFinPftDetailRef(finReference, custpftDet));

			//FINSCHDULE DETAILS
			List<FinanceScheduleDetail> finSchdDetails = getFinanceScheduleDetailDAO()
					.getFinScheduleDetails(finReference, TableType.MAIN_TAB.getSuffix(), false);
			finEODEvent.setFinanceScheduleDetails(finSchdDetails);

			setEventFlags(custEODEvent, finEODEvent);
			custEODEvent.getFinEODEvents().add(finEODEvent);
		}

		//clear temporary data
		custpftDet.clear();
		custFinMains.clear();
//		custSchdDetails.clear();
		logger.debug(" Leaving ");
		return custEODEvent;
	}

	public void setEventFlags(CustEODEvent custEODEvent, FinEODEvent finEODEvent) throws Exception {

		Map<Date, Integer> datesMap = new HashMap<Date, Integer>();
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
				setDateRollover(custEODEvent, finEODEvent, curSchd.getSchDate(), i);
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

	/**
	 * @param custEODEvent
	 * @param finEODEvent
	 * @param schdDate
	 * @param iSchd
	 * @throws Exception
	 */
	public void setDateRollover(CustEODEvent custEODEvent, FinEODEvent finEODEvent, Date schdDate, int iSchd)
			throws Exception {
		FinanceMain finMain = finEODEvent.getFinanceMain();

		Date grcEndDate = finMain.getGrcPeriodEndDate();

		if (finMain.isAllowGrcPeriod() && schdDate.compareTo(grcEndDate) < 0) {
			//Set Next Grace Capitalization Date
			Date nextGrcCpzDate = finMain.getNextGrcCpzDate();
			if (finMain.isAllowGrcCpz() && nextGrcCpzDate.compareTo(grcEndDate) < 0
					&& schdDate.compareTo(nextGrcCpzDate) == 0) {
				finEODEvent.setIdxGrcCpz(iSchd);
				custEODEvent.setDateRollover(true);
			}

			//Set Next Grace Profit Date
			Date nextGrcPftDate = finMain.getNextGrcPftDate();
			if (nextGrcPftDate.compareTo(grcEndDate) < 0 && schdDate.compareTo(nextGrcPftDate) == 0) {
				finEODEvent.setIdxGrcPft(iSchd);
				custEODEvent.setDateRollover(true);
			}

			//Set Next Grace Profit Review Date
			Date nextGrcPftRvwDate = finMain.getNextGrcPftRvwDate();
			if (finMain.isAllowGrcPftRvw() && nextGrcPftRvwDate.compareTo(grcEndDate) < 0
					&& schdDate.compareTo(nextGrcPftRvwDate) == 0) {
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
		Date nextDepDate = finMain.getNextDepDate();
		String deprFrq = finMain.getDepreciationFrq();
		if (nextDepDate != null && schdDate.compareTo(nextDepDate) == 0) {
			if (!StringUtils.isEmpty(deprFrq)) {
				if (nextDepDate.compareTo(finMain.getMaturityDate()) < 0) {
					Date nextFrqDate = FrequencyUtil.getNextDate(deprFrq, 1, schdDate, "A", false)
							.getNextFrequencyDate();
					finMain.setNextDepDate(nextFrqDate);
				}

				if (nextDepDate.compareTo(finMain.getMaturityDate()) > 0) {
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

			FinanceMain finMain = finEODEvent.getFinanceMain();
			Map<Date, Integer> dateMap = finEODEvent.getDatesMap();

			String finRef = finMain.getFinReference();
			boolean rateReview = finEODEvent.isupdFinSchdForRateRvw();
			boolean monthEnd = finEODEvent.isUpdMonthEndPostings();
			boolean lbdPosted = finEODEvent.isUpdLBDPostings();

			//update finance main
			if (finEODEvent.isUpdFinMain()) {
				//finEODEvent.getFinanceMain().setVersion(finEODEvent.getFinanceMain().getVersion() + 1);
				getFinanceMainDAO().updateFinanceInEOD(finMain, finEODEvent.getFinMainUpdateFields(), rateReview);
			}

			//update profit details
			getFinanceProfitDetailDAO().updateEOD(finEODEvent.getFinProfitDetail(), lbdPosted, monthEnd);

			//update schedule details 
			if (rateReview) {
				getFinanceScheduleDetailDAO().updateForRateReview(finEODEvent.getFinanceScheduleDetails());
			}

			//Update overdue details
			List<FinODDetails> odDetails = finEODEvent.getFinODDetails();
			Date odDate = getDateFromMap(dateMap, finEODEvent.getIdxPD());

			if (odDate != null && odDetails != null && !odDetails.isEmpty()) {
				// delete and insert based on the current OD index
				getFinODDetailsDAO().deleteAfterODDate(finRef, odDate);
				getFinODDetailsDAO().saveList(odDetails);
			}

			//update repay instruction
			if (finEODEvent.isUpdRepayInstruct()) {
				//delete 
				getRepayInstructionDAO().deleteInEOD(finRef);
				//Add repay instructions
				List<RepayInstruction> lisRepayIns = finEODEvent.getRepayInstructions();
				for (RepayInstruction repayInstruction : lisRepayIns) {
					repayInstruction.setFinReference(finRef);
				}
				getRepayInstructionDAO().saveListInEOD(lisRepayIns);
			}
			
			//provisions
			if (!finEODEvent.getProvisions().isEmpty()) {
				for (Provision provision : finEODEvent.getProvisions()) {
					if (StringUtils.equals(provision.getRcdAction(), EodConstants.RECORD_INSERT)) {
						getProvisionDAO().updateProvisonAmounts(provision);
					}else if (StringUtils.equals(provision.getRcdAction(), EodConstants.RECORD_INSERT)) {
						getProvisionDAO().save(provision,"");
					}
					
				}
			}
			//disbursement postings
			for (FinanceDisbursement disbursement : finEODEvent.getFinanceDisbursements()) {
				if (disbursement.isPosted()) {
					getFinanceDisbursementDAO().updateBatchDisb(disbursement, "");
				}
			}
			
			// group all the posting
			returnDataSets.addAll(finEODEvent.getReturnDataSet());
		}

		//save postings
		saveAccountingEOD(returnDataSets);
		//update accounts
		//getAccountProcessUtil().procAccountUpdate(returnDataSets);

		//update customer
		if (custEODEvent.isUpdCustomer()) {
			Customer customer = custEODEvent.getCustomer();
			long custID = customer.getCustID();
			String custSts = customer.getCustSts();
			Date stsChgDate = customer.getCustStsChgDate();
			getCustomerDAO().updateCustStatus(custSts, stsChgDate, custID);
		}

		logger.debug(" Leaving ");
		returnDataSets.clear();
	}

	public void updateCustomerDate(long custId, Date date) {
		logger.debug(" Entering ");
		Date nextDate = SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_NEXT);
		getCustomerDAO().updateCustAppDate(custId, nextDate);
		logger.debug(" Leaving ");
	}
//
//	public void updateCustQueueStatus(int threadId, long custId, int progress, boolean start) {
//		CustomerQueuing customerQueuing = new CustomerQueuing();
//		customerQueuing.setCustID(custId);
//		customerQueuing.setThreadId(threadId);
//		customerQueuing.setStartTime(DateUtility.getSysDate());
//		customerQueuing.setEndTime(DateUtility.getSysDate());
//		customerQueuing.setProgress(progress);
//		getCustomerQueuingDAO().update(customerQueuing, start);
//	}
//
//	public void updateFailed(int threadId, long custId) {
//		CustomerQueuing customerQueuing = new CustomerQueuing();
//		customerQueuing.setCustID(custId);
//		customerQueuing.setEndTime(DateUtility.getSysDate());
//		//reset thread for reallocation
//		customerQueuing.setThreadId(0);
//		customerQueuing.setProgress(EodConstants.PROGRESS_WAIT);
//		getCustomerQueuingDAO().updateFailed(customerQueuing);
//	}

}