package com.pennant.app.util;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.insurancedetails.FinInsurancesDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueueHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.exception.PFFInterfaceException;
import com.pennanttech.pff.core.TableType;

public class RepaymentProcessUtil {
	private final static Logger			logger	= Logger.getLogger(RepaymentProcessUtil.class);

	private RepaymentPostingsUtil		repayPostingUtil;
	private FinODDetailsDAO				finODDetailsDAO;
	private FinExcessAmountDAO			finExcessAmountDAO;
	private FinReceiptHeaderDAO			finReceiptHeaderDAO;
	private FinReceiptDetailDAO			finReceiptDetailDAO;
	private FinanceRepaymentsDAO		financeRepaymentsDAO;

	private FinLogEntryDetailDAO		finLogEntryDetailDAO;
	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private FinanceDisbursementDAO		financeDisbursementDAO;
	private RepayInstructionDAO			repayInstructionDAO;
	private ManualAdviseDAO				manualAdviseDAO;
	private FinFeeScheduleDetailDAO		finFeeScheduleDetailDAO;
	private FinInsurancesDAO			finInsurancesDAO;
	private LimitManagement				limitManagement;

	public RepaymentProcessUtil() {
		super();
	}

	/**
	 * Method for Calculation of Schedule payment based on Allocated Details from Receipts
	 * 
	 * @param scheduleData
	 * 
	 * @param receiptData
	 * @param scheduleData
	 * @return
	 * @throws PFFInterfaceException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void calcualteAndPayReceipt(FinanceMain financeMain, Customer customer,
			List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail profitDetail,
			FinReceiptHeader receiptHeader, String repayHierarchy, Date valuedate) throws IllegalAccessException,
			InvocationTargetException, PFFInterfaceException {
		logger.debug("Entering");
		String finrefer = financeMain.getFinReference();
		//Prepare schedule data for log
		FinScheduleData scheduleData = new FinScheduleData();
		scheduleData.setFinanceScheduleDetails(scheduleDetails);
		scheduleData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finrefer, "",
				false));
		scheduleData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finrefer, "", false));
		List<FinReceiptDetail> receiptDetails = sortReceiptDetails(receiptHeader.getReceiptDetails());

		// Fetch total overdue details
		FinODDetails overdue = getFinODDetailsDAO().getFinODyFinRefSchDate(finrefer, valuedate);
		BigDecimal priPaynow = BigDecimal.ZERO;

		for (FinReceiptDetail receiptDetail : receiptDetails) {

			FinanceScheduleDetail curSchd = null;

			for (FinanceScheduleDetail financeScheduleDetail : scheduleDetails) {
				Date schdDate = financeScheduleDetail.getSchDate();
				// Skip if Repayment date after Current Business date
				if (schdDate.compareTo(valuedate) != 0) {
					continue;
				}
				curSchd = financeScheduleDetail;
				break;
			}

			// If no balance for repayment then return with out calculation
			BigDecimal totalReceiptAmt = receiptDetail.getAmount();
			if (totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
				return;
			}

			scheduleDetails = sortSchdDetails(scheduleDetails);

			List<FinRepayHeader> repayHeaderList = new ArrayList<>();
			List<RepayScheduleDetail> pastdueRpySchdList = new ArrayList<>();
			char[] rpyOrder = repayHierarchy.replace("CS", "C").toCharArray();
			BigDecimal totPriPaidNow = BigDecimal.ZERO;
			BigDecimal totPftPaidNow = BigDecimal.ZERO;
			BigDecimal totLPftPaidNow = BigDecimal.ZERO;
			BigDecimal totFeePaidNow = BigDecimal.ZERO;
			BigDecimal totInsPaidNow = BigDecimal.ZERO;
			BigDecimal totPenaltyPaidNow = BigDecimal.ZERO;
			Date valueDate = receiptDetail.getValueDate();

			// Load Pending Schedules until balance available for payment
			RepayScheduleDetail rsd = null;

			for (int j = 0; j < rpyOrder.length; j++) {

				char repayTo = rpyOrder[j];
				if (repayTo == RepayConstants.REPAY_PRINCIPAL) {

					BigDecimal balPri = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
					if (balPri.compareTo(BigDecimal.ZERO) > 0) {
						if (balPri.compareTo(totalReceiptAmt) > 0) {
							balPri = totalReceiptAmt;
						}
						priPaynow = priPaynow.add(balPri);
						rsd = prepareRpyRecord(curSchd, rsd, repayTo, balPri, valueDate);
						// Reset Total Receipt Amount
						totalReceiptAmt = totalReceiptAmt.subtract(balPri);
						totPriPaidNow = totPriPaidNow.add(balPri);
					}
				} else if (repayTo == RepayConstants.REPAY_PROFIT) {

					String profit = ImplementationConstants.REPAY_INTEREST_HIERARCHY;
					char[] pftPayOrder = profit.toCharArray();
					for (char pftPayTo : pftPayOrder) {
						if (pftPayTo == RepayConstants.REPAY_PROFIT) {

							BigDecimal balPft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
							if (balPft.compareTo(BigDecimal.ZERO) > 0) {
								if (balPft.compareTo(totalReceiptAmt) > 0) {
									balPft = totalReceiptAmt;
								}
								rsd = prepareRpyRecord(curSchd, rsd, repayTo, balPft, valueDate);
								// Reset Total Receipt Amount
								totalReceiptAmt = totalReceiptAmt.subtract(balPft);
								totPftPaidNow = totPftPaidNow.add(balPft);
							}

						} else if (pftPayTo == RepayConstants.REPAY_LATEPAY_PROFIT) {

							if (overdue != null) {
								BigDecimal balLatePft = overdue.getLPIBal();
								if (balLatePft.compareTo(BigDecimal.ZERO) > 0) {
									if (balLatePft.compareTo(totalReceiptAmt) > 0) {
										balLatePft = totalReceiptAmt;
									}
									rsd = prepareRpyRecord(curSchd, rsd, repayTo, balLatePft, valueDate);

									// Reset Total Receipt Amount
									totalReceiptAmt = totalReceiptAmt.subtract(balLatePft);
									totLPftPaidNow = totLPftPaidNow.add(balLatePft);
								}
							}
						}
					}

				} else if (repayTo == RepayConstants.REPAY_PENALTY) {
					if (overdue != null) {
						BigDecimal balPenalty = overdue.getTotPenaltyBal();
						if (balPenalty.compareTo(BigDecimal.ZERO) > 0) {
							if (balPenalty.compareTo(totalReceiptAmt) > 0) {
								balPenalty = totalReceiptAmt;
							}
							rsd = prepareRpyRecord(curSchd, rsd, repayTo, balPenalty, valueDate);

							// Reset Total Receipt Amount
							totalReceiptAmt = totalReceiptAmt.subtract(balPenalty);
							totPenaltyPaidNow = totPenaltyPaidNow.add(balPenalty);
						}
					}

				} else if (repayTo == RepayConstants.REPAY_OTHERS) {

					// If Schedule has Unpaid Fee Amount
					BigDecimal balFee = curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid());
					if (balFee.compareTo(BigDecimal.ZERO) > 0) {
						if (balFee.compareTo(totalReceiptAmt) > 0) {
							balFee = totalReceiptAmt;
						}
						rsd = prepareRpyRecord(curSchd, rsd, RepayConstants.REPAY_FEE, balFee, valueDate);

						// Reset Total Receipt Amount
						totalReceiptAmt = totalReceiptAmt.subtract(balFee);
						totFeePaidNow = totFeePaidNow.add(balFee);
					}

					// If Schedule has Unpaid Insurance Amount
					BigDecimal balIns = curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid());
					if (balIns.compareTo(BigDecimal.ZERO) > 0) {
						if (balIns.compareTo(totalReceiptAmt) > 0) {
							balIns = totalReceiptAmt;
						}
						rsd = prepareRpyRecord(curSchd, rsd, RepayConstants.REPAY_INS, balIns, valueDate);

						// Reset Total Receipt Amount
						totalReceiptAmt = totalReceiptAmt.subtract(balIns);
						totInsPaidNow = totInsPaidNow.add(balIns);
					}
				}

				// No more Receipt amount left for next schedules
				if (totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}

			// Add Repay Schedule detail List
			if (rsd != null) {
				pastdueRpySchdList.add(rsd);
			}

			FinRepayHeader repayHeader = null;
			if (receiptDetail.getAmount().compareTo(totalReceiptAmt) > 0) {
				// Prepare Repay Header Details
				repayHeader = new FinRepayHeader();
				repayHeader.setFinReference(financeMain.getFinReference());
				repayHeader.setValueDate(valueDate);
				repayHeader.setRepayAmount(receiptDetail.getAmount().subtract(totalReceiptAmt));
				repayHeader.setFinEvent(FinanceConstants.FINSER_EVENT_SCHDRPY);
				repayHeader.setPriAmount(totPriPaidNow);
				repayHeader.setPftAmount(totPftPaidNow);
				repayHeader.setLatePftAmount(totLPftPaidNow);
				repayHeader.setTotalPenalty(totPenaltyPaidNow);
				repayHeader.setTotalIns(totInsPaidNow);
				repayHeader.setTotalSchdFee(totFeePaidNow);
				repayHeader.setTotalWaiver(BigDecimal.ZERO);

				// Adding Repay Schedule Details to Repay Header
				repayHeader.setRepayScheduleDetails(pastdueRpySchdList);
				repayHeaderList.add(repayHeader);
			}

			if (totalReceiptAmt.compareTo(BigDecimal.ZERO) > 0) {
				receiptHeader.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
				// Prepare Repay Header Details
				repayHeader = new FinRepayHeader();
				repayHeader.setFinReference(financeMain.getFinReference());
				repayHeader.setValueDate(valueDate);
				repayHeader.setRepayAmount(totalReceiptAmt);
				repayHeader.setFinEvent(RepayConstants.EXCESSADJUSTTO_EXCESS);
				repayHeader.setPriAmount(totalReceiptAmt);
				repayHeader.setPftAmount(BigDecimal.ZERO);
				repayHeader.setLatePftAmount(BigDecimal.ZERO);
				repayHeader.setTotalPenalty(BigDecimal.ZERO);
				repayHeader.setTotalIns(BigDecimal.ZERO);
				repayHeader.setTotalSchdFee(BigDecimal.ZERO);
				repayHeader.setTotalWaiver(BigDecimal.ZERO);

				// Adding Repay Schedule Details to Repay Header
				repayHeader.setRepayScheduleDetails(null);
				repayHeaderList.add(repayHeader);
			}

			// Adding Repay Headers to Receipt Details
			receiptDetail.setRepayHeaders(repayHeaderList);
		}

		scheduleDetails = doProcessReceipts(financeMain, scheduleDetails, profitDetail, receiptHeader, scheduleData,
				valuedate);
		doSaveReceipts(receiptHeader);
		limitManagement.processLoanRepay(financeMain, customer, priPaynow, profitDetail.getFinCategory());
		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Payment details as per receipt details
	 * 
	 * @param receiptHeader
	 */
	@SuppressWarnings("unchecked")
	public List<FinanceScheduleDetail> doProcessReceipts(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail profitDetail, FinReceiptHeader receiptHeader, FinScheduleData logScheduleData, Date valueDate)
					throws IllegalAccessException, InvocationTargetException, PFFInterfaceException {
		logger.debug("Entering");

		List<FinReceiptDetail> receiptDetailList = sortReceiptDetails(receiptHeader.getReceiptDetails());

		// Find out Is there any schedule payment done or not, If exists Log will be captured
		boolean isSchdLogReq = false;
		for (int i = 0; i < receiptDetailList.size(); i++) {
			List<FinRepayHeader> repayHeaderList = receiptDetailList.get(i).getRepayHeaders();
			for (int j = 0; j < repayHeaderList.size(); j++) {
				FinRepayHeader repayHeader = repayHeaderList.get(j);
				if (StringUtils.equals(FinanceConstants.FINSER_EVENT_SCHDRPY, repayHeader.getFinEvent())
						|| StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYRPY, repayHeader.getFinEvent())
						|| StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE, repayHeader.getFinEvent())) {
					isSchdLogReq = true;
				}
			}
		}

		long linkedTranId = 0;
		String finReference = financeMain.getFinReference();
		//Create log entry for Action for Schedule Modification
		FinLogEntryDetail entryDetail = null;
		long logKey = 0;
		Date postDate = getPostDate(DateUtility.getAppDate());
		if (isSchdLogReq && ((receiptHeader.getAllocations() != null && !receiptHeader.getAllocations().isEmpty()) 
				|| receiptHeader.isLogSchInPresentment())) {
			entryDetail = new FinLogEntryDetail();
			entryDetail.setFinReference(finReference);
			entryDetail.setEventAction(receiptHeader.getReceiptPurpose());
			entryDetail.setSchdlRecal(false);
			entryDetail.setPostDate(postDate);
			entryDetail.setReversalCompleted(false);
			logKey = getFinLogEntryDetailDAO().save(entryDetail);

			//Save Schedule Details For Future Modifications
			FinScheduleData oldFinSchdData = getFinSchDataByFinRef(finReference, "");
			oldFinSchdData.setFinanceMain(financeMain);
			oldFinSchdData.setFinReference(finReference);
			listSave(oldFinSchdData, "_Log", logKey);
		}

		for (int i = 0; i < receiptDetailList.size(); i++) {
			
			FinReceiptDetail receiptDetail = receiptDetailList.get(i);

			// Repay Header list process individually based on List existence
			List<FinRepayHeader> repayHeaderList = receiptDetail.getRepayHeaders();

			if (i != 0) {
				postDate = getPostDate(DateUtility.getAppDate());
			}

			boolean rpyProcessed = false;
			for (int j = 0; j < repayHeaderList.size(); j++) {

				FinRepayHeader repayHeader = repayHeaderList.get(j);
				if (!StringUtils.equals(FinanceConstants.FINSER_EVENT_SCHDRPY, repayHeader.getFinEvent())
						&& !StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYRPY, repayHeader.getFinEvent())
						&& !StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE, repayHeader.getFinEvent())) {

					// Update Excess amount (Adding amount and balance updation)
					if(StringUtils.equals(repayHeader.getFinEvent(), RepayConstants.EXCESSADJUSTTO_EXCESS) ||
							StringUtils.equals(repayHeader.getFinEvent(), RepayConstants.EXCESSADJUSTTO_EMIINADV)){
						
						if (receiptDetail.getPayAgainstID() != 0 && receiptDetail.getPayAgainstID() != Long.MIN_VALUE) {
							getFinExcessAmountDAO().updateExcessBal(receiptDetail.getPayAgainstID(),
									repayHeader.getRepayAmount());
						} else {
							int recordCount = getFinExcessAmountDAO().updateExcessBalByRef(finReference,
									repayHeader.getFinEvent(), repayHeader.getRepayAmount());
							// If record Not found then record count should be zero. Need to create new Excess Record
							if (recordCount <= 0) {
								FinExcessAmount excess = new FinExcessAmount();
								excess.setFinReference(finReference);
								excess.setAmountType(repayHeader.getFinEvent());
								excess.setAmount(repayHeader.getRepayAmount());
								excess.setBalanceAmt(repayHeader.getRepayAmount());
								excess.setUtilisedAmt(BigDecimal.ZERO);
								excess.setReservedAmt(BigDecimal.ZERO);
								getFinExcessAmountDAO().saveExcess(excess);
							}
						}
					}
					
					// Create New Payable Amount
					if(StringUtils.equals(repayHeader.getFinEvent(), RepayConstants.EXCESSADJUSTTO_PAYABLE)){
						
						ManualAdvise manualAdvise = new ManualAdvise();
						manualAdvise.setAdviseType(FinanceConstants.MANUAL_ADVISE_PAYABLE);
						manualAdvise.setFinReference(receiptHeader.getReference());
						manualAdvise.setFeeTypeID(0);
						manualAdvise.setSequence(0);
						manualAdvise.setAdviseAmount(repayHeader.getRepayAmount());
						manualAdvise.setBalanceAmt(repayHeader.getRepayAmount());
						manualAdvise.setValueDate(receiptDetail.getValueDate());
						manualAdvise.setPostDate(receiptDetail.getValueDate());
						manualAdvise.setReceiptID(receiptHeader.getReceiptID());
						manualAdvise.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						getManualAdviseDAO().save(manualAdvise, TableType.MAIN_TAB);
					}

					continue;
				}

				//Create log entry for Action for Schedule Modification
				if (i != 0 && !rpyProcessed) {
					entryDetail = new FinLogEntryDetail();
					entryDetail.setFinReference(finReference);
					entryDetail.setEventAction(receiptHeader.getReceiptPurpose());
					entryDetail.setSchdlRecal(true);
					entryDetail.setPostDate(postDate);
					entryDetail.setReversalCompleted(false);
					logKey = getFinLogEntryDetailDAO().save(entryDetail);

					//Save Schedule Details For Future Modifications
					logScheduleData.setFinanceMain(financeMain);
					logScheduleData.setFinReference(finReference);
					logScheduleData.setFinanceScheduleDetails(scheduleDetails);
					listSave(logScheduleData, "_Log", logKey);
				}

				rpyProcessed = true;
				List<RepayScheduleDetail> repaySchdList = repayHeader.getRepayScheduleDetails();
				List<Object> returnList = doRepayPostings(financeMain, scheduleDetails, profitDetail, repaySchdList,
						getEventCode(repayHeader.getFinEvent()), valueDate, receiptDetail, receiptHeader.getPostBranch());

				if (!(Boolean) returnList.get(0)) {
					String errParm = (String) returnList.get(1);
					throw new PFFInterfaceException("9999", errParm);
				}

				//Update Linked Transaction ID
				linkedTranId = (long) returnList.get(1);
				repayHeader.setLinkedTranId(linkedTranId);
				repayHeader.setValueDate(postDate);
				financeMain.setFinRepaymentAmount(financeMain.getFinRepaymentAmount().add(repayHeader.getPriAmount()));
				scheduleDetails = (List<FinanceScheduleDetail>) returnList.get(2);
			}

			// Setting/Maintaining Log key for Last log of Schedule Details
			receiptDetailList.get(i).setLogKey(logKey);
		}
		logger.debug("Leaving");
		return scheduleDetails;

	}

	/**
	 * Method for Saving the Receipt records
	 * 
	 * @param receiptHeader
	 */
	public void doSaveReceipts(FinReceiptHeader receiptHeader) {
		long receiptID = getFinReceiptHeaderDAO().save(receiptHeader, TableType.MAIN_TAB);
		receiptHeader.setReceiptID(receiptID);

		// Save Receipt Detail List by setting Receipt Header ID
		List<FinReceiptDetail> receiptDetails = sortReceiptDetails(receiptHeader.getReceiptDetails());
		for (FinReceiptDetail receiptDetail : receiptDetails) {
			receiptDetail.setReceiptID(receiptID);
			receiptDetail.setStatus(RepayConstants.PAYSTATUS_APPROVED);
			long receiptSeqID = getFinReceiptDetailDAO().save(receiptDetail, TableType.MAIN_TAB);

			// Excess Amounts
			if (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EXCESS)
					|| StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EMIINADV)) {

				long payAgainstID = receiptDetail.getPayAgainstID();

				// Excess Amount make utilization
				if (payAgainstID != 0) {
					getFinExcessAmountDAO().updateUtilise(payAgainstID, receiptDetail.getAmount());

					// Delete Reserved Log against Excess and Receipt ID
					getFinExcessAmountDAO().deleteExcessReserve(receiptSeqID, payAgainstID);

					// Excess Movement Creation
					FinExcessMovement movement = new FinExcessMovement();
					movement.setExcessID(payAgainstID);
					movement.setReceiptID(receiptSeqID);
					movement.setMovementType(RepayConstants.RECEIPTTYPE_RECIPT);
					movement.setTranType(AccountConstants.TRANTYPE_DEBIT);
					movement.setAmount(receiptDetail.getAmount());
					getFinExcessAmountDAO().saveExcessMovement(movement);
				}
			}
			
			// Payable Advise Amounts
			if (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE)) {

				long payAgainstID = receiptDetail.getPayAgainstID();

				// Payable Advise Amount make utilization
				if (payAgainstID != 0) {
					getManualAdviseDAO().updateUtilise(payAgainstID, receiptDetail.getAmount());

					// Delete Reserved Log against Advise and Receipt Seq ID
					getManualAdviseDAO().deletePayableReserve(receiptSeqID, payAgainstID);

					// Payable Advise Movement Creation
					ManualAdviseMovements movement = new ManualAdviseMovements();
					movement.setAdviseID(payAgainstID);
					movement.setReceiptID(receiptID);
					movement.setReceiptSeqID(receiptSeqID);
					movement.setMovementDate(DateUtility.getAppDate());
					movement.setMovementAmount(receiptDetail.getAmount());
					movement.setPaidAmount(receiptDetail.getAmount());
					getManualAdviseDAO().saveMovement(movement, TableType.MAIN_TAB.getSuffix());
				}
			}

			// Manual Advise Movements
			for (ManualAdviseMovements movement : receiptDetail.getAdvMovements()) {
				movement.setReceiptID(receiptID);
				movement.setReceiptSeqID(receiptSeqID);
				getManualAdviseDAO().saveMovement(movement, TableType.MAIN_TAB.getSuffix());
			}

			List<FinRepayHeader> rpyHeaderList = receiptDetail.getRepayHeaders();
			for (FinRepayHeader rpyHeader : rpyHeaderList) {
				rpyHeader.setReceiptSeqID(receiptSeqID);

				//Save Repay Header details
				long repayID = getFinanceRepaymentsDAO().saveFinRepayHeader(rpyHeader, TableType.MAIN_TAB.getSuffix());

				List<RepayScheduleDetail> rpySchdList = rpyHeader.getRepayScheduleDetails();
				if (rpySchdList != null && !rpySchdList.isEmpty()) {
					for (int i = 0; i < rpySchdList.size(); i++) {

						RepayScheduleDetail rpySchd = rpySchdList.get(i);
						rpySchd.setRepayID(repayID);
						rpySchd.setRepaySchID(i + 1);
						rpySchd.setLinkedTranId(rpyHeader.getLinkedTranId());
						//update fee schedule details
						updateFeeDetails(rpySchd);
						//update insurance schedule details
						updateInsuranceDetails(rpySchd);
					}

					// Save Repayment Schedule Details
					getFinanceRepaymentsDAO().saveRpySchdList(rpySchdList, TableType.MAIN_TAB.getSuffix());
				}
			}
		}
	}

	/**
	 * Method for Sorting Receipt Details From Receipts
	 * 
	 * @param receipts
	 * @return
	 */
	private List<FinReceiptDetail> sortReceiptDetails(List<FinReceiptDetail> receipts) {

		if (receipts != null && !receipts.isEmpty()) {
			Collections.sort(receipts, new Comparator<FinReceiptDetail>() {
				@Override
				public int compare(FinReceiptDetail detail1, FinReceiptDetail detail2) {
					if (detail1.getPayOrder() > detail2.getPayOrder()) {
						return 1;
					} else if (detail1.getPayOrder() < detail2.getPayOrder()) {
						return -1;
					}
					return 0;
				}
			});
		}
		return receipts;
	}

	private void updateInsuranceDetails(RepayScheduleDetail rpySchd) {
		BigDecimal remBalPaidAmount = rpySchd.getSchdInsPayNow();

		if (remBalPaidAmount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		List<FinSchFrqInsurance> updateInsList = new ArrayList<>();
		List<FinSchFrqInsurance> list = finInsurancesDAO.getInsScheduleBySchDate(rpySchd.getFinReference(),
				rpySchd.getSchDate());

		for (FinSchFrqInsurance insSchd : list) {

			if (remBalPaidAmount.compareTo(BigDecimal.ZERO) == 0) {
				break;
			}
			BigDecimal insBal = insSchd.getAmount().subtract(insSchd.getInsurancePaid())
					.subtract(insSchd.getInsuranceWaived());
			if (insBal.compareTo(remBalPaidAmount) > 0) {
				insBal = remBalPaidAmount;
			}
			insSchd.setInsurancePaid(insSchd.getInsurancePaid().add(insBal));
			updateInsList.add(insSchd);
			remBalPaidAmount = remBalPaidAmount.subtract(insBal);

		}

		if (!updateInsList.isEmpty()) {
			finInsurancesDAO.updateInsPaids(updateInsList);
		}

	}

	private void updateFeeDetails(RepayScheduleDetail rpySchd) {
		BigDecimal remBalPaidAmount = rpySchd.getSchdFeePayNow();
		BigDecimal remBalWaivedAmount = rpySchd.getSchdFeeWaivedNow();

		if (remBalPaidAmount.compareTo(BigDecimal.ZERO) == 0 && remBalWaivedAmount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}
		List<FinFeeScheduleDetail> list = finFeeScheduleDetailDAO.getFeeSchedules(rpySchd.getFinReference(),
				rpySchd.getSchDate());
		List<FinFeeScheduleDetail> updateFeeList = new ArrayList<>();
		for (FinFeeScheduleDetail feeSchd : list) {

			if (remBalPaidAmount.compareTo(BigDecimal.ZERO) == 0 && remBalWaivedAmount.compareTo(BigDecimal.ZERO) == 0) {
				break;
			}
			BigDecimal feeBal = feeSchd.getSchAmount().subtract(
					feeSchd.getPaidAmount().subtract(feeSchd.getWaiverAmount()));
			if (feeBal.compareTo(remBalPaidAmount) > 0) {
				feeBal = remBalPaidAmount;
			}
			
			if(remBalPaidAmount.compareTo(BigDecimal.ZERO) == 0){
				if (feeBal.compareTo(remBalWaivedAmount) > 0) {
					feeBal = remBalWaivedAmount;
				}
				feeSchd.setWaiverAmount(feeSchd.getWaiverAmount().add(feeBal));
				remBalWaivedAmount = remBalWaivedAmount.subtract(feeBal);
			}else{
				feeSchd.setPaidAmount(feeSchd.getPaidAmount().add(feeBal));
				remBalPaidAmount = remBalPaidAmount.subtract(feeBal);
			}
			
			feeSchd.setOsAmount(feeSchd.getSchAmount().subtract(feeSchd.getPaidAmount()).subtract(feeSchd.getWaiverAmount()));
			updateFeeList.add(feeSchd);

		}
		if (!updateFeeList.isEmpty()) {
			finFeeScheduleDetailDAO.updateFeePaids(updateFeeList);
		}

	}

	/**
	 * Method for Status updation on Finance
	 * 
	 * @param financeMain
	 * @param valueDate
	 * @param scheduleDetails
	 * @param profitDetail
	 * @return
	 */
	public FinanceMain updateStatus(FinanceMain financeMain, Date valueDate,
			List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail profitDetail) {
		return getRepayPostingUtil().updateStatus(financeMain, valueDate, scheduleDetails, profitDetail);
	}

	/**
	 * Method for Repayment Details Posting Process
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param repaySchdList
	 * @param insRefund
	 * @return
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 */
	private List<Object> doRepayPostings(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail profitDetail, List<RepayScheduleDetail> repaySchdList, String eventCode,
			Date valuedate, FinReceiptDetail receiptDetail, String postBranch) throws IllegalAccessException,
			PFFInterfaceException, InvocationTargetException {
		logger.debug("Entering");

		List<Object> returnList = new ArrayList<Object>();
		try {

			List<FinRepayQueue> finRepayQueues = new ArrayList<FinRepayQueue>();
			FinRepayQueue finRepayQueue = null;
			FinRepayQueueHeader rpyQueueHeader = new FinRepayQueueHeader();

			if (repaySchdList != null && !repaySchdList.isEmpty()) {
				for (int i = 0; i < repaySchdList.size(); i++) {

					finRepayQueue = new FinRepayQueue();
					finRepayQueue.setFinReference(financeMain.getFinReference());
					finRepayQueue.setRpyDate(repaySchdList.get(i).getSchDate());
					finRepayQueue.setFinRpyFor(repaySchdList.get(i).getSchdFor());
					finRepayQueue.setRcdNotExist(true);
					finRepayQueue = doWriteDataToBean(finRepayQueue, financeMain, repaySchdList.get(i));

					finRepayQueue.setRefundAmount(repaySchdList.get(i).getRefundReq());
					finRepayQueue.setPenaltyPayNow(repaySchdList.get(i).getPenaltyPayNow());
					finRepayQueue.setWaivedAmount(repaySchdList.get(i).getWaivedAmt());
					finRepayQueue.setPenaltyBal(repaySchdList.get(i).getPenaltyAmt()
							.subtract(repaySchdList.get(i).getPenaltyPayNow()));
					finRepayQueue.setChargeType(repaySchdList.get(i).getChargeType());

					// Total Repayments Calculation for Principal, Profit 
					rpyQueueHeader.setPrincipal(rpyQueueHeader.getPrincipal().add(
							repaySchdList.get(i).getPrincipalSchdPayNow()));
					rpyQueueHeader
							.setProfit(rpyQueueHeader.getProfit().add(repaySchdList.get(i).getProfitSchdPayNow()));
					rpyQueueHeader.setTds(rpyQueueHeader.getTds().add(repaySchdList.get(i).getTdsSchdPayNow()));
					rpyQueueHeader.setLateProfit(rpyQueueHeader.getLateProfit().add(
							repaySchdList.get(i).getLatePftSchdPayNow()));
					rpyQueueHeader.setPenalty(rpyQueueHeader.getPenalty().add(repaySchdList.get(i).getPenaltyPayNow()));

					// Fee Details
					rpyQueueHeader.setFee(rpyQueueHeader.getFee().add(repaySchdList.get(i).getSchdFeePayNow()));
					rpyQueueHeader.setInsurance(rpyQueueHeader.getInsurance().add(
							repaySchdList.get(i).getSchdInsPayNow()));
					rpyQueueHeader.setSuplRent(rpyQueueHeader.getSuplRent().add(
							repaySchdList.get(i).getSchdSuplRentPayNow()));
					rpyQueueHeader.setIncrCost(rpyQueueHeader.getIncrCost().add(
							repaySchdList.get(i).getSchdIncrCostPayNow()));

					// Waiver Amounts
					rpyQueueHeader.setPriWaived(rpyQueueHeader.getPriWaived().add(finRepayQueue.getSchdPriWaivedNow()));
					rpyQueueHeader.setPftWaived(rpyQueueHeader.getPftWaived().add(finRepayQueue.getSchdPftWaivedNow()));
					rpyQueueHeader.setLatePftWaived(rpyQueueHeader.getLatePftWaived().add(
							finRepayQueue.getLatePayPftWaivedNow()));
					rpyQueueHeader.setPenaltyWaived(rpyQueueHeader.getPenaltyWaived().add(
							finRepayQueue.getWaivedAmount()));
					rpyQueueHeader.setFeeWaived(rpyQueueHeader.getFeeWaived().add(finRepayQueue.getSchdFeeWaivedNow()));
					rpyQueueHeader.setInsWaived(rpyQueueHeader.getInsWaived().add(finRepayQueue.getSchdInsWaivedNow()));
					rpyQueueHeader.setSuplRentWaived(rpyQueueHeader.getSuplRentWaived().add(
							finRepayQueue.getSchdSuplRentWaivedNow()));
					rpyQueueHeader.setIncrCostWaived(rpyQueueHeader.getIncrCostWaived().add(
							finRepayQueue.getSchdIncrCostWaivedNow()));

					finRepayQueues.add(finRepayQueue);
				}
			}

			//Repayments Process For Schedule Repay List	
			rpyQueueHeader.setQueueList(finRepayQueues);
			rpyQueueHeader.setPayType(receiptDetail.getPaymentType());
			rpyQueueHeader.setPostBranch(postBranch);
			rpyQueueHeader.setPartnerBankAc(receiptDetail.getPartnerBankAc());
			rpyQueueHeader.setPartnerBankAcType(receiptDetail.getPartnerBankAcType());

			returnList = getRepayPostingUtil().postingProcess(financeMain, scheduleDetails, profitDetail,
					rpyQueueHeader, eventCode, valuedate);

		} catch (PFFInterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (IllegalAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (InvocationTargetException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");
		return returnList;
	}

	/**
	 * Method for prepare RepayQueue data
	 * 
	 * @param resultSet
	 * @return
	 */
	private FinRepayQueue doWriteDataToBean(FinRepayQueue finRepayQueue, FinanceMain financeMain,
			RepayScheduleDetail rsd) {
		logger.debug("Entering");

		finRepayQueue.setBranch(financeMain.getFinBranch());
		finRepayQueue.setFinType(financeMain.getFinType());
		finRepayQueue.setCustomerID(financeMain.getCustID());
		finRepayQueue.setFinPriority(9999);

		// Principal Amount
		finRepayQueue.setSchdPft(rsd.getProfitSchd());
		finRepayQueue.setSchdPftPaid(rsd.getProfitSchdPaid());
		finRepayQueue.setSchdPftBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
		finRepayQueue.setSchdPftPayNow(rsd.getProfitSchdPayNow());
		finRepayQueue.setSchdPftWaivedNow(rsd.getPftSchdWaivedNow());
		finRepayQueue.setSchdTdsPayNow(rsd.getTdsSchdPayNow());

		// Profit Amount
		finRepayQueue.setSchdPri(rsd.getPrincipalSchd());
		finRepayQueue.setSchdPriBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));
		finRepayQueue.setSchdPriPaid(rsd.getPrincipalSchdPaid());
		finRepayQueue.setSchdPriPayNow(rsd.getPrincipalSchdPayNow());
		finRepayQueue.setSchdPriWaivedNow(rsd.getPriSchdWaivedNow());

		// Late Pay Profit Amount
		finRepayQueue.setLatePayPftPayNow(rsd.getLatePftSchdPayNow());
		finRepayQueue.setLatePayPftWaivedNow(rsd.getLatePftSchdWaivedNow());

		// Fee Details
		//	1. Schedule Fee Amount
		finRepayQueue.setSchdFee(rsd.getSchdFee());
		finRepayQueue.setSchdFeeBal(rsd.getSchdFeeBal());
		finRepayQueue.setSchdFeePayNow(rsd.getSchdFeePayNow());
		finRepayQueue.setSchdFeePaid(rsd.getSchdFeePaid());
		finRepayQueue.setSchdFeeWaivedNow(rsd.getSchdFeeWaivedNow());

		//	2. Schedule Insurance Amount
		finRepayQueue.setSchdIns(rsd.getSchdIns());
		finRepayQueue.setSchdInsBal(rsd.getSchdInsBal());
		finRepayQueue.setSchdInsPayNow(rsd.getSchdInsPayNow());
		finRepayQueue.setSchdInsPaid(rsd.getSchdInsPaid());
		finRepayQueue.setSchdInsWaivedNow(rsd.getSchdInsWaivedNow());

		//	3. Schedule Supplementary Rent Amount
		finRepayQueue.setSchdSuplRent(rsd.getSchdSuplRent());
		finRepayQueue.setSchdSuplRentBal(rsd.getSchdSuplRentBal());
		finRepayQueue.setSchdSuplRentPayNow(rsd.getSchdSuplRentPayNow());
		finRepayQueue.setSchdSuplRentPaid(rsd.getSchdSuplRentPaid());
		finRepayQueue.setSchdSuplRentWaivedNow(rsd.getSchdSuplRentWaivedNow());

		//	4. Schedule Increased Cost Amount
		finRepayQueue.setSchdIncrCost(rsd.getSchdIncrCost());
		finRepayQueue.setSchdIncrCostBal(rsd.getSchdIncrCostBal());
		finRepayQueue.setSchdIncrCostPayNow(rsd.getSchdIncrCostPayNow());
		finRepayQueue.setSchdIncrCostPaid(rsd.getSchdIncrCostPaid());
		finRepayQueue.setSchdIncrCostWaivedNow(rsd.getSchdIncrCostWaivedNow());

		logger.debug("Leaving");
		return finRepayQueue;
	}

	/**
	 * Method for Preparation of Repayment Schedule Details
	 * 
	 * @param curSchd
	 * @param rsd
	 * @param rpyTo
	 * @param balPayNow
	 * @return
	 */
	private RepayScheduleDetail prepareRpyRecord(FinanceScheduleDetail curSchd, RepayScheduleDetail rsd, char rpyTo,
			BigDecimal balPayNow, Date valueDate) {

		if (rsd == null) {
			rsd = new RepayScheduleDetail();
			rsd.setFinReference(curSchd.getFinReference());
			rsd.setSchDate(curSchd.getSchDate());
			rsd.setDefSchdDate(curSchd.getSchDate());

			rsd.setSchdFor(FinanceConstants.SCH_TYPE_SCHEDULE);
			rsd.setProfitSchd(curSchd.getProfitSchd());
			rsd.setProfitSchdPaid(curSchd.getSchdPftPaid());
			rsd.setProfitSchdBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));

			rsd.setPrincipalSchd(curSchd.getPrincipalSchd());
			rsd.setPrincipalSchdPaid(curSchd.getSchdPriPaid());
			rsd.setPrincipalSchdBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));

			rsd.setSchdIns(curSchd.getInsSchd());
			rsd.setSchdInsPaid(curSchd.getSchdInsPaid());
			rsd.setSchdInsBal(rsd.getSchdIns().subtract(rsd.getSchdInsPaid()));

			rsd.setSchdSuplRent(curSchd.getSuplRent());
			rsd.setSchdSuplRentPaid(curSchd.getSuplRentPaid());
			rsd.setSchdSuplRentBal(rsd.getSchdSuplRent().subtract(rsd.getSchdSuplRentPaid()));

			rsd.setSchdIncrCost(curSchd.getIncrCost());
			rsd.setSchdIncrCostPaid(curSchd.getIncrCostPaid());
			rsd.setSchdIncrCostBal(rsd.getSchdIncrCost().subtract(rsd.getSchdIncrCostPaid()));

			rsd.setSchdFee(curSchd.getFeeSchd());
			rsd.setSchdFeePaid(curSchd.getSchdFeePaid());
			rsd.setSchdFeeBal(rsd.getSchdFee().subtract(rsd.getSchdFeePaid()));

			rsd.setDaysLate(DateUtility.getDaysBetween(curSchd.getSchDate(), valueDate));
			rsd.setDaysEarly(0);
		}

		// Principal Payment 
		if (rpyTo == RepayConstants.REPAY_PRINCIPAL) {
			rsd.setPrincipalSchdPayNow(balPayNow);
		}

		// Profit Payment 
		if (rpyTo == RepayConstants.REPAY_PROFIT) {
			rsd.setProfitSchdPayNow(balPayNow);
		}

		// Late Payment Profit Payment 
		if (rpyTo == RepayConstants.REPAY_LATEPAY_PROFIT) {
			rsd.setLatePftSchdPayNow(balPayNow);
		}

		// Fee Detail Payment 
		if (rpyTo == RepayConstants.REPAY_FEE) {
			rsd.setSchdFeePayNow(balPayNow);

		}

		// Insurance Detail Payment 
		if (rpyTo == RepayConstants.REPAY_INS) {
			rsd.setSchdInsPayNow(balPayNow);
		}

		// Penalty Charge Detail Payment 
		if (rpyTo == RepayConstants.REPAY_PENALTY) {
			rsd.setPenaltyPayNow(balPayNow);
		}

		return rsd;

	}

	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 * **/
	public FinScheduleData getFinSchDataByFinRef(String finReference, String type) {
		logger.debug("Entering");
		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type,
				false));
		finSchData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type,
				false));
		finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false));
		logger.debug("Leaving");
		return finSchData;
	}

	public void listSave(FinScheduleData scheduleData, String tableType, long logKey) {
		logger.debug("Entering ");
		HashMap<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();

		// Finance Schedule Details
		for (int i = 0; i < scheduleData.getFinanceScheduleDetails().size(); i++) {

			FinanceScheduleDetail curSchd = scheduleData.getFinanceScheduleDetails().get(i);
			curSchd.setLastMntBy(scheduleData.getFinanceMain().getLastMntBy());
			curSchd.setFinReference(scheduleData.getFinReference());
			int seqNo = 0;

			if (mapDateSeq.containsKey(curSchd.getSchDate())) {
				seqNo = mapDateSeq.get(curSchd.getSchDate());
				mapDateSeq.remove(curSchd.getSchDate());
			}
			seqNo = seqNo + 1;
			mapDateSeq.put(curSchd.getSchDate(), seqNo);
			curSchd.setSchSeq(seqNo);
			curSchd.setLogKey(logKey);
		}

		getFinanceScheduleDetailDAO().saveList(scheduleData.getFinanceScheduleDetails(), tableType, false);

		if (logKey != 0) {
			// Finance Disbursement Details
			mapDateSeq = new HashMap<Date, Integer>();
			Date curBDay = DateUtility.getAppDate();
			for (int i = 0; i < scheduleData.getDisbursementDetails().size(); i++) {
				scheduleData.getDisbursementDetails().get(i).setFinReference(scheduleData.getFinReference());
				scheduleData.getDisbursementDetails().get(i).setDisbReqDate(curBDay);
				scheduleData.getDisbursementDetails().get(i).setDisbIsActive(true);
				scheduleData.getDisbursementDetails().get(i).setDisbDisbursed(true);
				scheduleData.getDisbursementDetails().get(i).setLogKey(logKey);
			}
			getFinanceDisbursementDAO().saveList(scheduleData.getDisbursementDetails(), tableType, false);

		}

		//Finance Repay Instruction Details
		if (scheduleData.getRepayInstructions() != null) {
			for (int i = 0; i < scheduleData.getRepayInstructions().size(); i++) {
				RepayInstruction curSchd = scheduleData.getRepayInstructions().get(i);

				curSchd.setFinReference(scheduleData.getFinReference());
				curSchd.setLogKey(logKey);
			}
			getRepayInstructionDAO().saveList(scheduleData.getRepayInstructions(), tableType, false);
		}

		logger.debug("Leaving ");
	}

	/**
	 * Method for Fetching Accounting Event Code based on Finance Event Action
	 * 
	 * @param finEvent
	 * @return
	 */
	private String getEventCode(String finEvent) {

		if (StringUtils.equals(finEvent, FinanceConstants.FINSER_EVENT_SCHDRPY)) {
			return AccountEventConstants.ACCEVENT_REPAY;
		} else if (StringUtils.equals(finEvent, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
			return AccountEventConstants.ACCEVENT_EARLYPAY;
		} else if (StringUtils.equals(finEvent, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			return AccountEventConstants.ACCEVENT_EARLYSTL;
		}
		return null;
	}

	private Date getPostDate(Date appDate) {
		Calendar cal = Calendar.getInstance();
		Calendar appCal = Calendar.getInstance();
		cal.setTime(DateUtility.getSysDate());
		appCal.setTime(appDate);
		cal.set(Calendar.YEAR, appCal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, appCal.get(Calendar.MONTH));
		cal.set(Calendar.DATE, appCal.get(Calendar.DATE));
		return cal.getTime();
	}

	/**
	 * Method for Sorting Schedule Details
	 * 
	 * @param financeScheduleDetail
	 * @return
	 */
	public List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public RepaymentPostingsUtil getRepayPostingUtil() {
		return repayPostingUtil;
	}

	public void setRepayPostingUtil(RepaymentPostingsUtil repayPostingUtil) {
		this.repayPostingUtil = repayPostingUtil;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public FinExcessAmountDAO getFinExcessAmountDAO() {
		return finExcessAmountDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public FinReceiptDetailDAO getFinReceiptDetailDAO() {
		return finReceiptDetailDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public FinLogEntryDetailDAO getFinLogEntryDetailDAO() {
		return finLogEntryDetailDAO;
	}

	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public RepayInstructionDAO getRepayInstructionDAO() {
		return repayInstructionDAO;
	}

	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setFinInsurancesDAO(FinInsurancesDAO finInsurancesDAO) {
		this.finInsurancesDAO = finInsurancesDAO;
	}

	public void setFinFeeScheduleDetailDAO(FinFeeScheduleDetailDAO finFeeScheduleDetailDAO) {
		this.finFeeScheduleDetailDAO = finFeeScheduleDetailDAO;
	}

	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

}
