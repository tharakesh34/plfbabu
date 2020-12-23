package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.amortization.ProjectedAmortization;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.LMSServiceLog;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.ProvisionAmount;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

public class LoadFinanceData extends ServiceHelper {
	private static Logger logger = Logger.getLogger(LoadFinanceData.class);

	private static final long serialVersionUID = -281578785120363314L;

	public CustEODEvent prepareFinEODEvents(CustEODEvent custEODEvent, long custID) throws Exception {
		logger.debug(" Entering ");

		List<FinanceMain> custFinMains = getFinanceMainDAO().getFinMainsForEODByCustId(custID, true);
		// List<FinanceScheduleDetail> custSchdDetails =
		// getFinanceScheduleDetailDAO().getFinScheduleDetails(custID, true);
		List<FinanceProfitDetail> custpftDet = getFinanceProfitDetailDAO().getFinProfitDetailsByCustId(custID, true);
		Cloner cloner = null;

		for (FinanceMain main : custFinMains) {
			FinEODEvent finEODEvent = new FinEODEvent();

			// FINANCE MAIN
			finEODEvent.setFinanceMain(main);
			String finType = finEODEvent.getFinanceMain().getFinType();
			String finReference = finEODEvent.getFinanceMain().getFinReference();

			// FINANCE TYPE
			FinanceType financeType = getFinanceType(finType);
			finEODEvent.setFinType(financeType);

			// FINPROFIT DETAILS
			finEODEvent.setFinProfitDetail(getFinPftDetailRef(finReference, custpftDet));

			// FINSCHDULE DETAILS
			List<FinanceScheduleDetail> finSchdDetails = financeScheduleDetailDAO.getFinScheduleDetails(finReference,
					TableType.MAIN_TAB.getSuffix(), false);
			finEODEvent.setFinanceScheduleDetails(finSchdDetails);

			// Original Schedules
			cloner = new Cloner();
			finEODEvent.setOrgFinSchdDetails(cloner.deepClone(finSchdDetails));

			// Fin Excess Amounts
			finEODEvent.setFinExcessAmounts(
					finExcessAmountDAO.getAllExcessAmountsByRef(finReference, TableType.MAIN_TAB.getSuffix()));

			setEventFlags(custEODEvent, finEODEvent);
			custEODEvent.getFinEODEvents().add(finEODEvent);
		}

		// clear temporary data
		custpftDet.clear();
		custFinMains.clear();
		// custSchdDetails.clear();
		logger.debug(" Leaving ");
		return custEODEvent;
	}

	/**
	 * 
	 * @param custEODEvent
	 * @param finReference
	 * @param custID
	 * @return
	 * @throws Exception
	 */
	public CustEODEvent prepareInActiveFinEODEvents(CustEODEvent custEODEvent, String finReference) throws Exception {
		logger.debug(" Entering ");

		FinEODEvent finEODEvent = new FinEODEvent();

		FinanceMain finMain = getFinanceMainDAO().getFinMainsForEODByFinRef(finReference, false);
		FinanceProfitDetail finPftDetail = getFinanceProfitDetailDAO().getFinProfitDetailsByFinRef(finReference, false);

		// FINANCE MAIN
		finEODEvent.setFinanceMain(finMain);
		String finType = finEODEvent.getFinanceMain().getFinType();

		// FINANCE TYPE
		FinanceType financeType = getFinanceType(finType);
		finEODEvent.setFinType(financeType);

		// FINPROFIT DETAILS
		finEODEvent.setFinProfitDetail(finPftDetail);

		// FINSCHDULE DETAILS
		List<FinanceScheduleDetail> finSchdDetails = getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference,
				TableType.MAIN_TAB.getSuffix(), false);
		finEODEvent.setFinanceScheduleDetails(finSchdDetails);

		custEODEvent.getFinEODEvents().add(finEODEvent);

		logger.debug(" Leaving ");
		return custEODEvent;
	}

	public void setEventFlags(CustEODEvent custEODEvent, FinEODEvent finEODEvent) throws Exception {

		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();
		Date valueDate = custEODEvent.getEodValueDate();
		Date businessDate = DateUtility.addDays(custEODEvent.getEodValueDate(), 1);

		boolean isAmountDue = false;

		// Place schedule dates to Map
		for (int i = 0; i < finSchdDetails.size(); i++) {
			FinanceScheduleDetail curSchd = finSchdDetails.get(i);

			// Find various events required today or not
			if (curSchd.getSchDate().compareTo(businessDate) == 0) {
				// Disbursement Exist
				if (curSchd.isDisbOnSchDate()) {
					finEODEvent.setIdxDisb(i);
					custEODEvent.setDisbExist(true);
				}

				// Fee Due Exist
				BigDecimal dueAmount = curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid());
				if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
					finEODEvent.setIdxDue(i);
					custEODEvent.setDueExist(true);
				}

				// Insurance Due Exist
				dueAmount = curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid());

				if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
					finEODEvent.setIdxDue(i);
					custEODEvent.setDueExist(true);
				}

				// Installment Due Exist
				dueAmount = curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()).subtract(curSchd.getSchdPriPaid())
						.subtract(curSchd.getSchdPftPaid());

				if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
					finEODEvent.setIdxDue(i);
					custEODEvent.setDueExist(true);
				}

				// Installment Due Exist
				dueAmount = curSchd.getCpzAmount().subtract(curSchd.getCpzBalance());
				if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
					finEODEvent.setIdxDue(i);
					custEODEvent.setDueExist(true);
				}

				// Presentment Required
				if (curSchd.getDefSchdDate().compareTo(businessDate) == 0) {
					finEODEvent.setIdxPresentment(i);
					custEODEvent.setCheckPresentment(true);
				}
			}

			// Is Provision exists
			if (custEODEvent.isDueExist() && ImplementationConstants.ALLOW_NPA_PROVISION && provisionDAO
					.isProvisionExists(finEODEvent.getFinanceMain().getFinReference(), TableType.MAIN_TAB)) {
				finEODEvent.getFinProfitDetail().setProvision(true);
			}

			// Date Rollover Setting
			if (curSchd.getSchDate().compareTo(businessDate) == 0) {
				setDateRollover(custEODEvent, finEODEvent, curSchd.getSchDate(), i);
			}

			// PastDue Index Setting
			if (finEODEvent.getIdxPD() > 0) {
				continue;
			}

			// Do not Include Today Late payment Calculation
			if (ImplementationConstants.LP_MARK_FIRSTDAY) {
				if (curSchd.getSchDate().compareTo(valueDate) > 0) {
					continue;
				}
			} else {
				if (curSchd.getSchDate().compareTo(valueDate) >= 0) {
					continue;
				}
			}

			isAmountDue = isOldestDueOverDue(curSchd);

			// Paid Principal OR Paid Interest Less than scheduled amounts
			if (curSchd.getSchdPriPaid().compareTo(curSchd.getPrincipalSchd()) < 0
					|| curSchd.getSchdPftPaid().compareTo(curSchd.getProfitSchd()) < 0) {
				isAmountDue = true;
			}

			if (isAmountDue) {
				finEODEvent.setIdxPD(i);
				custEODEvent.setPastDueExist(true);
			}

			if (curSchd.getSchDate().compareTo(businessDate) >= 0) {
				break;
			}
		}

		// Check If LPP Method on capitalization basis and Due Index not exists
		if (finEODEvent.getIdxPD() <= 0) {
			if (isLPPCpzReq(finEODEvent)) {
				finEODEvent.setIdxPD(1);
				custEODEvent.setPastDueExist(true);
			}
		}
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
			// Set Next Grace Capitalization Date
			Date nextGrcCpzDate = finMain.getNextGrcCpzDate();
			if (finMain.isAllowGrcCpz() && nextGrcCpzDate.compareTo(grcEndDate) < 0
					&& schdDate.compareTo(nextGrcCpzDate) == 0) {
				finEODEvent.setIdxGrcCpz(iSchd);
				custEODEvent.setDateRollover(true);
			}

			// Set Next Grace Profit Date
			Date nextGrcPftDate = finMain.getNextGrcPftDate();
			if (nextGrcPftDate.compareTo(grcEndDate) < 0 && schdDate.compareTo(nextGrcPftDate) == 0) {
				finEODEvent.setIdxGrcPft(iSchd);
				custEODEvent.setDateRollover(true);
			}

			// Set Next Grace Profit Review Date
			Date nextGrcPftRvwDate = finMain.getNextGrcPftRvwDate();
			if (finMain.isAllowGrcPftRvw() && nextGrcPftRvwDate.compareTo(grcEndDate) < 0
					&& schdDate.compareTo(nextGrcPftRvwDate) == 0) {
				finEODEvent.setIdxGrcPftRvw(iSchd);
				custEODEvent.setDateRollover(true);
			}
		}

		// Set Next Repay Capitalization Date

		if (finMain.isAllowRepayCpz() && schdDate.compareTo(finMain.getNextRepayCpzDate()) == 0) {
			finEODEvent.setIdxRpyCpz(iSchd);
			custEODEvent.setDateRollover(true);
		}

		// Set Next Repayment Date
		if (schdDate.compareTo(finMain.getNextRepayDate()) == 0) {
			finEODEvent.setIdxRpy(iSchd);
			custEODEvent.setDateRollover(true);
		}

		// Set Next Repayment Profit Date
		if (schdDate.compareTo(finMain.getNextRepayPftDate()) == 0) {
			finEODEvent.setIdxRpyPft(iSchd);
			custEODEvent.setDateRollover(true);
		}

		// Set Next Repayment Profit Review Date
		if (finMain.isAllowRepayRvw()) {
			if (schdDate.compareTo(finMain.getNextRepayRvwDate()) == 0) {
				finEODEvent.setIdxRpyPftRvw(iSchd);
				custEODEvent.setDateRollover(true);
			}
		}

		// Set Next Depreciation Date
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

			String finRef = finMain.getFinReference();
			boolean rateReview = finEODEvent.isupdFinSchdForRateRvw();
			boolean monthEnd = finEODEvent.isUpdMonthEndPostings();
			boolean lbdPosted = finEODEvent.isUpdLBDPostings();
			boolean changeGraceEnd = finEODEvent.isUpdFinSchdForChangeGrcEnd();

			// update finance main
			if (finEODEvent.isUpdFinMain() && !changeGraceEnd) {
				// finEODEvent.getFinanceMain().setVersion(finEODEvent.getFinanceMain().getVersion()
				// + 1);
				getFinanceMainDAO().updateFinanceInEOD(finMain, finEODEvent.getFinMainUpdateFields(), rateReview);
			}

			// update profit details
			getFinanceProfitDetailDAO().updateEOD(finEODEvent.getFinProfitDetail(), lbdPosted, monthEnd);

			// update schedule details
			if (rateReview) {
				saveLMSServiceLog(finEODEvent);
				getFinanceScheduleDetailDAO().updateForRateReview(finEODEvent.getFinanceScheduleDetails());
			}

			// Update overdue details
			//			List<FinODDetails> odDetails = finEODEvent.getFinODDetails();
			//			if (odDetails != null && !odDetails.isEmpty()) {
			//				FinanceScheduleDetail odschd = finEODEvent.getFinanceScheduleDetails().get(finEODEvent.getIdxPD());
			//				// delete and insert based on the current OD index
			//				if (odschd != null) {
			//					getFinODDetailsDAO().deleteAfterODDate(finRef, odschd.getSchDate());
			//					List<FinODDetails> listSave = new ArrayList<FinODDetails>();
			//					for (FinODDetails finODDetails : odDetails) {
			//						if (finODDetails.getFinODSchdDate().compareTo(odschd.getSchDate()) >= 0) {
			//							listSave.add(finODDetails);
			//						}
			//					}
			//					if (!listSave.isEmpty()) {
			//						getFinODDetailsDAO().saveList(listSave);
			//					}
			//					listSave = null;
			//				}
			//			}

			// Save Schedule, Repay Instruction Details when Change Grace End
			if (changeGraceEnd) {
				finMain.setVersion(finMain.getVersion() + 1);
				financeMainDAO.update(finMain, TableType.MAIN_TAB, false);

				// Existing Schedule back up
				long logKey = saveFinLogEntryDetail(finMain);
				listSave(finEODEvent, "_Log", logKey);

				// Save Latest Schedule Details
				listDeletion(finEODEvent, FinanceConstants.FINSER_EVENT_CHGGRCEND, "");
				listSave(finEODEvent, "", 0);
			}

			//insert or Update
			List<FinODDetails> odDetails = finEODEvent.getFinODDetails();
			List<FinODDetails> odDetailsLBD = finEODEvent.getFinODDetailsLBD();
			if (odDetails != null && !odDetails.isEmpty()) {

				if (finEODEvent.getIdxPD() > 1) {
					if (isLPPCpzReq(finEODEvent)) {
						finEODEvent.setIdxPD(1);
					}
				}
				FinanceScheduleDetail odschd = finEODEvent.getFinanceScheduleDetails().get(finEODEvent.getIdxPD());

				// delete and insert based on the current OD index
				if (odschd != null) {
					List<FinODDetails> listSave = new ArrayList<FinODDetails>();
					List<FinODDetails> listupdate = new ArrayList<FinODDetails>();
					for (FinODDetails finODDetails : odDetails) {
						if (finODDetails.getFinODSchdDate().compareTo(odschd.getSchDate()) < 0) {
							continue;
						}

						boolean exists = checkExsistInList(finODDetails, odDetailsLBD);
						if (exists) {
							listupdate.add(finODDetails);
						} else {
							listSave.add(finODDetails);
						}
					}

					if (!listSave.isEmpty()) {
						getFinODDetailsDAO().saveList(listSave);
					}
					listSave = null;
					if (!listupdate.isEmpty()) {
						getFinODDetailsDAO().updateODDetailsBatch(listupdate);
					}
					listupdate = null;
				}
			}

			// update repay instruction
			if (finEODEvent.isUpdRepayInstruct()) {
				// delete
				getRepayInstructionDAO().deleteInEOD(finRef);
				// Add repay instructions
				List<RepayInstruction> lisRepayIns = finEODEvent.getRepayInstructions();
				for (RepayInstruction repayInstruction : lisRepayIns) {
					repayInstruction.setFinReference(finRef);
				}
				getRepayInstructionDAO().saveListInEOD(lisRepayIns);
			}

			// provisions
			if (!finEODEvent.getProvisions().isEmpty()) {
				saveProvisions(finEODEvent);
			}

			// disbursement postings
			for (FinanceDisbursement disbursement : finEODEvent.getFinanceDisbursements()) {
				if (disbursement.isPosted()) {
					getFinanceDisbursementDAO().updateBatchDisb(disbursement, "");
				}
			}

			// group all the posting
			returnDataSets.addAll(finEODEvent.getReturnDataSet());
		}

		// save or update ACCRUALS
		saveProjAccruals(custEODEvent);

		// save postings
		saveAccountingEOD(returnDataSets);

		// update accounts
		// getAccountProcessUtil().procAccountUpdate(returnDataSets);

		/*
		 * //update customer if (custEODEvent.isUpdCustomer()) { Customer customer = custEODEvent.getCustomer(); long
		 * custID = customer.getCustID(); String custSts = customer.getCustSts(); Date stsChgDate =
		 * customer.getCustStsChgDate(); getCustomerDAO().updateCustStatus(custSts, stsChgDate, custID); }
		 */
		logger.debug(" Leaving ");
		returnDataSets.clear();
	}

	private boolean checkExsistInList(FinODDetails finODDetails, List<FinODDetails> odDetails_PRV) {

		for (FinODDetails finODDet : odDetails_PRV) {
			if (finODDetails.getFinODSchdDate().compareTo(finODDet.getFinODSchdDate()) == 0) {
				return true;
			}
		}

		return false;
	}

	private boolean isLPPCpzReq(FinEODEvent finEODEvent) {
		List<FinODDetails> odDetails = finEODEvent.getFinODDetails();

		if (CollectionUtils.isEmpty(odDetails)) {
			return false;
		}

		for (FinODDetails finODDetail : odDetails) {
			if ((finODDetail.getFinCurODAmt().compareTo(BigDecimal.ZERO) == 0)
					&& (finODDetail.getTotPenaltyBal().compareTo(BigDecimal.ZERO) > 0)
					&& (StringUtils.equals(finODDetail.getODChargeCalOn(), FinanceConstants.ODCALON_PIPD_FRQ)
							|| StringUtils.equals(finODDetail.getODChargeCalOn(), FinanceConstants.ODCALON_PIPD_EOM))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Save Provisions and Provision Movements
	 * 
	 * @param finEODEvent
	 */
	private void saveProvisions(FinEODEvent finEODEvent) {
		for (Provision provision : finEODEvent.getProvisions()) {

			Provision oldProvision = provisionDAO.getProvisionById(provision.getFinReference(), TableType.MAIN_TAB,
					false);

			long provisionId = Long.MIN_VALUE;
			if (oldProvision != null) {
				provisionId = oldProvision.getId();
				provision.setId(provisionId);
				provisionDAO.update(provision, TableType.MAIN_TAB);
			} else {
				provisionId = provisionDAO.save(provision, TableType.MAIN_TAB);
			}

			provision.setId(Long.MIN_VALUE);
			provision.setProvisionId(provisionId);
			provisionDAO.saveMovements(provision, TableType.MAIN_TAB);

			List<ProvisionAmount> list = provision.getProvisionAmounts();
			for (ProvisionAmount provisionAmount : list) {
				provisionAmount.setProvisionId(provisionId);
			}

			if (oldProvision != null) {
				List<ProvisionAmount> oldAmounts = provisionDAO.getProvisionAmounts(oldProvision.getId(),
						TableType.MAIN_TAB);
				List<ProvisionAmount> provisionAmounts = provision.getProvisionAmounts();
				for (ProvisionAmount oldAmount : oldAmounts) {
					for (ProvisionAmount provisionAmount : provisionAmounts) {
						if (StringUtils.equals(oldAmount.getProvisionType(), provisionAmount.getProvisionType())) {
							provisionAmount.setId(oldAmount.getId());
						}
					}
				}
				provisionDAO.updateAmounts(provisionAmounts, TableType.MAIN_TAB);
			} else {
				provisionDAO.saveAmounts(list, TableType.MAIN_TAB, false);
			}
			provisionDAO.saveAmounts(list, TableType.MAIN_TAB, true);

		}
	}

	/**
	 * 
	 * @param custEODEvent
	 */
	public void saveProjAccruals(CustEODEvent custEODEvent) {

		for (FinEODEvent finEODEvent : custEODEvent.getFinEODEvents()) {

			List<ProjectedAccrual> projAccrualList = finEODEvent.getProjectedAccrualList();

			// Monthly ACCRUALS and POS
			if (projAccrualList != null && !projAccrualList.isEmpty()) {
				getProjectedAmortizationDAO().saveBatchProjAccruals(projAccrualList);
			}
		}
	}

	/**
	 * 
	 * @param custEODEvent
	 */
	public void saveOrUpdateIncomeAMZDetails(CustEODEvent custEODEvent) {

		for (FinEODEvent finEODEvent : custEODEvent.getFinEODEvents()) {

			List<ProjectedAmortization> projSaveAMZList = new ArrayList<ProjectedAmortization>(1);
			List<ProjectedAmortization> projUpdateAMZList = new ArrayList<ProjectedAmortization>(1);

			List<ProjectedAmortization> incomeAMZList = finEODEvent.getIncomeAMZList();

			// Income Amortizations
			if (incomeAMZList != null && !incomeAMZList.isEmpty()) {

				for (ProjectedAmortization projectedAMZ : incomeAMZList) {
					if (projectedAMZ.isUpdProjAMZ()) {
						projUpdateAMZList.add(projectedAMZ);

					} else if (projectedAMZ.isSaveProjAMZ()) {
						projSaveAMZList.add(projectedAMZ);
					}
				}
			}

			if (!projSaveAMZList.isEmpty()) {
				getProjectedAmortizationDAO().saveBatchIncomeAMZ(projSaveAMZList);
			}

			if (!projUpdateAMZList.isEmpty()) {
				getProjectedAmortizationDAO().updateBatchIncomeAMZ(projUpdateAMZList);
			}
		}
	}

	public void updateCustomerDate(long custId, Date date, String newCustStatus) {
		logger.debug(" Entering ");
		Date nextDate = SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_NEXT);
		getCustomerDAO().updateCustAppDate(custId, nextDate, newCustStatus);
		logger.debug(" Leaving ");
	}

	private long saveFinLogEntryDetail(FinanceMain financeMain) {
		FinLogEntryDetail entryDetail = new FinLogEntryDetail();

		entryDetail.setReversalCompleted(false);
		entryDetail.setPostDate(SysParamUtil.getAppDate());
		entryDetail.setFinReference(financeMain.getFinReference());
		entryDetail.setSchdlRecal(financeMain.isScheduleChange());
		entryDetail.setEventAction(AccountEventConstants.ACCEVENT_GRACEEND);

		return finLogEntryDetailDAO.save(entryDetail);
	}

	//
	// public void updateCustQueueStatus(int threadId, long custId, int
	// progress, boolean start) {
	// CustomerQueuing customerQueuing = new CustomerQueuing();
	// customerQueuing.setCustID(custId);
	// customerQueuing.setThreadId(threadId);
	// customerQueuing.setStartTime(DateUtility.getSysDate());
	// customerQueuing.setEndTime(DateUtility.getSysDate());
	// customerQueuing.setProgress(progress);
	// getCustomerQueuingDAO().update(customerQueuing, start);
	// }
	//
	// public void updateFailed(int threadId, long custId) {
	// CustomerQueuing customerQueuing = new CustomerQueuing();
	// customerQueuing.setCustID(custId);
	// customerQueuing.setEndTime(DateUtility.getSysDate());
	// //reset thread for reallocation
	// customerQueuing.setThreadId(0);
	// customerQueuing.setProgress(EodConstants.PROGRESS_WAIT);
	// getCustomerQueuingDAO().updateFailed(customerQueuing);
	// }

	/*
	 * Saving the LMS service log notifications , we will change this method once Service instructions saved in EOD.
	 */
	public void saveLMSServiceLog(FinEODEvent finEODEvent) {
		logger.debug(Literal.ENTERING);
		try {
			String lmsServiceLogReq = SysParamUtil.getValueAsString(SMTParameterConstants.LMS_SERVICE_LOG_REQ);
			if (!StringUtils.equals(lmsServiceLogReq, PennantConstants.YES)) {
				return;
			}

			List<LMSServiceLog> lmsServiceLogs = new ArrayList<>();

			String finReference = finEODEvent.getFinanceMain().getFinReference();
			Date appDate = DateUtility.getAppDate();
			FinanceScheduleDetail financeScheduleDetail = null;

			BigDecimal oldRate = finServiceInstructionDAO.getOldRate(finReference, appDate);

			List<FinanceScheduleDetail> scheduleDetail = finEODEvent.getFinanceScheduleDetails();

			if (CollectionUtils.isEmpty(scheduleDetail)) {
				return;
			}

			for (FinanceScheduleDetail detail : scheduleDetail) {
				if (detail.getSchDate().compareTo(appDate) >= 0) {
					financeScheduleDetail = detail;
					break;
				}
			}

			if (financeScheduleDetail == null) {
				return;
			}

			LMSServiceLog lmsServiceLog = new LMSServiceLog();
			lmsServiceLog.setOldRate(oldRate);
			lmsServiceLog.setNewRate(financeScheduleDetail.getCalculatedRate());
			lmsServiceLog.setEvent(FinanceConstants.FINSER_EVENT_RATECHG);
			lmsServiceLog.setFinReference(finReference);
			lmsServiceLog.setNotificationFlag(PennantConstants.NO);
			lmsServiceLog.setEffectiveDate(appDate);
			lmsServiceLogs.add(lmsServiceLog);

			if (CollectionUtils.isNotEmpty(lmsServiceLogs)) {
				finServiceInstructionDAO.saveLMSServiceLOGList(lmsServiceLogs);
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void listSave(FinEODEvent finEODEvent, String tableType, long logKey) {
		FinanceMain financeMain = finEODEvent.getFinanceMain();
		HashMap<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();

		// Finance Schedule Details
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();
		if (CollectionUtils.isNotEmpty(finSchdDetails)) {
			if (logKey != 0) {
				finSchdDetails = finEODEvent.getOrgFinSchdDetails();
			}

			for (FinanceScheduleDetail schd : finSchdDetails) {
				schd.setLastMntBy(financeMain.getLastMntBy());
				schd.setFinReference(financeMain.getFinReference());
				int seqNo = 0;

				if (mapDateSeq.containsKey(schd.getSchDate())) {
					seqNo = mapDateSeq.get(schd.getSchDate());
					mapDateSeq.remove(schd.getSchDate());
				}

				seqNo = seqNo + 1;
				mapDateSeq.put(schd.getSchDate(), seqNo);
				schd.setSchSeq(seqNo);
				schd.setLogKey(logKey);
			}

			financeScheduleDetailDAO.saveList(finSchdDetails, tableType, false);
		}

		// Finance Repay Instruction Details
		List<RepayInstruction> repayInstructions = finEODEvent.getRepayInstructions();
		if (CollectionUtils.isNotEmpty(repayInstructions) && finEODEvent.isUpdRepayInstruct()) {
			if (logKey != 0) {
				repayInstructions = finEODEvent.getOrgRepayInsts();
			}

			for (RepayInstruction rpayIns : repayInstructions) {
				rpayIns.setFinReference(financeMain.getFinReference());
				rpayIns.setLogKey(logKey);
			}

			repayInstructionDAO.saveList(repayInstructions, tableType, false);
		}

		// Finance Service Instructions
		List<FinServiceInstruction> finServiceInstructions = finEODEvent.getFinServiceInstructions();
		if (CollectionUtils.isNotEmpty(finServiceInstructions) && logKey == 0) {
			finServiceInstructionDAO.saveList(finServiceInstructions, tableType);
		}

	}

	private void listDeletion(FinEODEvent finEODEvent, String finEvent, String tableType) {
		FinanceMain financeMain = finEODEvent.getFinanceMain();
		String finReference = financeMain.getFinReference();

		List<FinanceScheduleDetail> financeScheduleDetails = finEODEvent.getFinanceScheduleDetails();
		if (CollectionUtils.isNotEmpty(financeScheduleDetails)) {
			financeScheduleDetailDAO.deleteByFinReference(finReference, tableType, false, 0);
		}

		List<RepayInstruction> repayInstructions = finEODEvent.getRepayInstructions();
		if (CollectionUtils.isNotEmpty(repayInstructions) && finEODEvent.isUpdRepayInstruct()) {
			repayInstructionDAO.deleteByFinReference(finReference, tableType, false, 0);
		}

	}
}