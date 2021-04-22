package com.pennant.app.core;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.FactoryConfigurationError;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.batchProcessStatus.BatchProcessStatusDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinAutoApprovalDetails;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.logging.dao.FinAutoApprovalDetailDAO;

public class FinAutoApprovalProcess extends GenericService<FinAutoApprovalDetails> {

	private static Logger logger = LogManager.getLogger(FinAutoApprovalProcess.class);

	protected transient WorkflowEngine workFlow = null;

	Date minReqFinStartDate = DateUtility.addDays(DateUtility.getAppDate(),
			-SysParamUtil.getValueAsInt("BACKDAYS_STARTDATE") + 1);
	Date maxReqFinStartDate = DateUtility.addDays(DateUtility.getAppDate(),
			+SysParamUtil.getValueAsInt("FUTUREDAYS_STARTDATE") + 1);

	private Map<String, Integer> qdpValidityDays = new HashMap<>();

	private List<FinAutoApprovalDetails> nonQDPList = new ArrayList<>();

	@Autowired
	private transient FinanceDetailService financeDetailService;
	@Autowired
	private transient FinAutoApprovalDetailDAO finAutoApprovalDetailDAO;

	@Autowired
	private transient BatchProcessStatusDAO batchProcessStatusDAO;
	private PlatformTransactionManager transactionManager;
	protected HashMap<String, ArrayList<ErrorDetail>> overideMap = new HashMap<String, ArrayList<ErrorDetail>>();

	public PlatformTransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/**
	 * Method to Check Any Records Are There For Auto Approval. After successful upload of Disbursements It Will Call
	 */

	public void checkForAutoApproval(LoggedInUser userId, long batchId) {
		List<FinAutoApprovalDetails> autoAppDisbs = finAutoApprovalDetailDAO
				.getUploadedDisbursementsWithBatchId(batchId);
		if (CollectionUtils.isEmpty(autoAppDisbs)) {
			return;
		}
		loadQDPValidityDays();
		batchProcessStatusDAO.saveBatchStatus("QDP", new Timestamp(System.currentTimeMillis()), "I");

		Iterator<FinAutoApprovalDetails> batchIterator = autoAppDisbs.iterator();
		FinanceDetail financeDetail = null;

		while (batchIterator.hasNext()) {
			FinAutoApprovalDetails appDetails = batchIterator.next();
			Map<String, String> tempMap = new HashMap<String, String>();

			if (tempMap.isEmpty()) {
				tempMap.put(appDetails.getFinReference(), "");
			} else {
				if (!tempMap.containsKey(appDetails.getFinReference())) {
					tempMap.put(appDetails.getFinReference(), "");
				} else {
					continue;
				}
			}

			boolean finisQuickDisb = checkForQuickDisbInDisbursements(appDetails.getFinReference());
			if (!finisQuickDisb) {
				updateStatus(appDetails, null);
				continue;
			}

			boolean servicing = checkForServicing(appDetails.getFinReference());

			String finMain = financeDetailService.getNextRoleCodeByRef(appDetails.getFinReference(), "_View");

			if (!servicing && !checkForPreviousApproval(appDetails.getFinReference())) {
				financeDetail = financeDetailService.getOriginationFinance(appDetails.getFinReference(), finMain,
						FinanceConstants.FINSER_EVENT_ORG, "");
				financeDetail.setModuleDefiner(FinanceConstants.FINSER_EVENT_ORG);

			} else {
				financeDetail = financeDetailService.getServicingFinanceForQDP(appDetails.getFinReference(),
						AccountEventConstants.ACCEVENT_ADDDBSN, FinanceConstants.FINSER_EVENT_ADDDISB, finMain);
			}

			if (financeDetail != null) {
				processAndApprove(userId, batchIterator, appDetails, financeDetail);
			}

		}

		// finAutoApprovalDetailDAO.deleteNonQDPRecords(nonQDPList);
	}

	/**
	 * This Method Process and Approves the Finance which are in For Auto Approval. Prepares new Schedule based on the
	 * Realization Date and Validate and Approves the Record.
	 */

	private void processAndApprove(LoggedInUser userDetails, Iterator<FinAutoApprovalDetails> batchIterator,
			FinAutoApprovalDetails appDetails, FinanceDetail financeDetail) {

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();

		List<FinanceDisbursement> financeDisbursements = financeDetail.getFinScheduleData().getDisbursementDetails();
		List<FinanceDisbursement> processedDisbursementList = new ArrayList<>(financeDisbursements.size());
		List<FinAdvancePayments> finAdvancePayments = financeDetail.getAdvancePaymentsList();

		try {
			int noOfDisbursements = financeDetail.getFinScheduleData().getDisbursementDetails().size();
			for (int i = 0; i < noOfDisbursements; i++) {
				FinanceDisbursement disbursement = financeDisbursements.get(i);

				if (!recordToBeProcessed(finAdvancePayments, disbursement, appDetails)) {
					processedDisbursementList.add(disbursement);
					continue;
				}

				txStatus = transactionManager.getTransaction(txDef);

				if (financeDetail.getModuleDefiner().equals(FinanceConstants.FINSER_EVENT_ORG)) {
					if (disbursement.isQuickDisb() && financeType.isAutoApprove()) {
						boolean validDisb = validateQDPDays(appDetails.getPaymentType(), appDetails.getDownloadedon(),
								appDetails.getRealizedDate());

						if (validDisb) {
							String tranType = "";
							if (financeMain.isWorkflow()) {
								tranType = PennantConstants.TRAN_WF;
								if (StringUtils.isEmpty(financeMain.getRecordType())) {
									financeMain.setVersion(financeMain.getVersion() + 1);
									if (financeMain.isNew()) {
										financeMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
									} else {
										financeMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
										financeMain.setNewRecord(true);
									}
								}
							} else {
								financeMain.setVersion(financeMain.getVersion() + 1);
								if (financeMain.isNew()) {
									tranType = PennantConstants.TRAN_ADD;
								} else {
									tranType = PennantConstants.TRAN_UPD;
								}
							}
							disbursement.setDisbStatus("D");
							disbursement.setDisbDate(appDetails.getRealizedDate());
							processedDisbursementList.add(disbursement);
							financeDetail.getFinScheduleData().setDisbursementDetails(processedDisbursementList);

							financeMain.setFinStartDate(appDetails.getRealizedDate());
							financeMain.setLastRepayDate(appDetails.getRealizedDate());
							financeMain.setLastRepayPftDate(appDetails.getRealizedDate());

							financeMain.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
							if (!financeMain.isAllowGrcPeriod()) {
								financeMain.setGrcPeriodEndDate(appDetails.getRealizedDate());
							}

							financeMain.setRecalType("");
							financeMain.setCalculateRepay(true);
							financeDetail.getFinScheduleData().setFinanceMain(financeMain);

							financeDetail.getFinScheduleData().getRepayInstructions().clear();

							prepareSchedule(financeDetail, financeMain, financeType);
							financeMain.setUserDetails(userDetails);

							AuditHeader auditheader = doProcess(financeDetail, tranType, userDetails);
							// If validation fails while approving the record
							if (!auditheader.isNextProcess()) {
								updateStatus(appDetails, auditheader, txStatus);
							}
						} else {
							updateStatus(appDetails, txStatus);
						}
					} else {
						updateStatus(batchIterator, appDetails, txStatus, disbursement);
					}
				} else if (checkForServicing(financeMain.getFinReference())) {

					if (disbursement.isQuickDisb() && financeType.isAutoApprove()) {
						financeMain.setNewRecord(false);

						boolean validDisb = validateQDPDays(appDetails.getPaymentType(), appDetails.getDownloadedon(),
								appDetails.getRealizedDate());

						if (validDisb) {
							if (financeDetail.getFinScheduleData().getFinanceScheduleDetails().size() != 0) {
								List<FinServiceInstruction> finServiceInstructions = financeDetail.getFinScheduleData()
										.getFinServiceInstructions();

								for (FinServiceInstruction finServiceInstruction : finServiceInstructions) {

									String tranType = "";
									if (financeMain.isWorkflow()) {
										tranType = PennantConstants.TRAN_WF;
										//financeMain.setVersion(financeMain.getVersion());
										if (StringUtils.isEmpty(financeMain.getRecordType())) {
											financeMain.setVersion(financeMain.getVersion() + 1);
											if (financeMain.isNew()) {
												financeMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
											} else {
												financeMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
												financeMain.setNewRecord(true);
											}
										}
									} else {
										financeMain.setVersion(financeMain.getVersion() + 1);
										if (financeMain.isNew()) {
											tranType = PennantConstants.TRAN_ADD;
										} else {
											tranType = PennantConstants.TRAN_UPD;
										}
									}

									financeDetail.getFinScheduleData().getDisbursementDetails().clear();
									financeDetail.getFinScheduleData()
											.setDisbursementDetails(processedDisbursementList);

									finServiceInstruction.setRecalFromDate(appDetails.getRealizedDate());
									financeMain.setEventFromDate(appDetails.getRealizedDate());
									financeMain.setRecalToDate(finServiceInstruction.getRecalToDate());
									financeMain.setScheduleMethod(finServiceInstruction.getSchdMethod());
									financeMain.setRecalType(finServiceInstruction.getRecalType());

									financeDetail.setFinScheduleData(
											ScheduleCalculator.addDisbursement(financeDetail.getFinScheduleData(),
													finServiceInstruction.getAmount(), BigDecimal.ZERO, false));
									financeDetail.getFinScheduleData().setSchduleGenerated(true);

									// Update Disb Status as D for the disbursement that was processed.
									for (FinanceDisbursement financeDisbursement : financeDetail.getFinScheduleData()
											.getDisbursementDetails()) {
										for (FinAdvancePayments finAdvancePayment : finAdvancePayments) {
											if ((StringUtils.equals(DisbursementConstants.STATUS_REALIZED,
													finAdvancePayment.getStatus()))
													|| (StringUtils.equals(DisbursementConstants.STATUS_PAID,
															finAdvancePayment.getStatus()))) {
												financeDisbursement.setDisbStatus("D");
												financeDisbursement.setQuickDisb(true);
											}
										}
									}

									financeMain.setUserDetails(userDetails);
									financeMain.getFinCurrAssetValue().add(finServiceInstruction.getAmount());

									financeMain.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
									financeDetail.getFinScheduleData().setFinanceMain(financeMain);

									doProcess(financeDetail, tranType, userDetails);
								}
							}

						} else {
							updateStatus(appDetails, txStatus);
						}

					} else {
						updateStatus(batchIterator, appDetails, txStatus, disbursement);
					}

				} else {
					appDetails.setErrorDesc("Unable to Process");
					appDetails.setStatus(DisbursementConstants.AUTODISB_STATUS_FAILED);
					finAutoApprovalDetailDAO.updateFinAutoApprovals(appDetails);
					transactionManager.commit(txStatus);

				}

			}
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			appDetails.setErrorDesc(StringUtils.substring(e.toString(), 0, 900));
			logger.info(e.toString());
			logger.debug(Literal.EXCEPTION, e);
			appDetails.setStatus(DisbursementConstants.AUTODISB_STATUS_FAILED);

			DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
			txDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

			TransactionStatus txSts = null;
			finAutoApprovalDetailDAO.updateFinAutoApprovals(appDetails);

			txSts = transactionManager.getTransaction(txDefinition);
			transactionManager.commit(txSts);

		}

		if (DisbursementConstants.AUTODISB_STATUS_FAILED != appDetails.getStatus()) {
			appDetails.setStatus(DisbursementConstants.AUTODISB_STATUS_SUCCESS);
			finAutoApprovalDetailDAO.updateFinAutoApprovals(appDetails);
			if (txStatus != null) {
				transactionManager.commit(txStatus);
			}
		}

		batchProcessStatusDAO.updateBatchStatus("QDP", new Timestamp(System.currentTimeMillis()), "C");

	}

	private void updateStatus(FinAutoApprovalDetails appDetails, AuditHeader auditheader, TransactionStatus txStatus) {

		appDetails.setErrorDesc(auditheader.getAuditOveride());
		appDetails.setStatus(DisbursementConstants.AUTODISB_STATUS_FAILED);
		finAutoApprovalDetailDAO.updateFinAutoApprovals(appDetails);
		transactionManager.commit(txStatus);

	}

	private void updateStatus(Iterator<FinAutoApprovalDetails> batchIterator, FinAutoApprovalDetails appDetails,
			TransactionStatus txStatus, FinanceDisbursement disbursement) {
		nonQDPList.add(appDetails);
		if (!disbursement.isQuickDisb()) {
			appDetails.setErrorDesc("Disbursement is not QDP");
		} else {
			appDetails.setErrorDesc("Auto Approval Flag is set to False in Loan Type");
		}
		appDetails.setStatus(DisbursementConstants.AUTODISB_STATUS_FAILED);
		finAutoApprovalDetailDAO.updateFinAutoApprovals(appDetails);
		transactionManager.commit(txStatus);
		batchIterator.remove();
	}

	private void updateStatus(FinAutoApprovalDetails appDetails, TransactionStatus txStatus) {
		// update status and error description in BatchTable
		appDetails.setErrorDesc("Payment Type Validity Expired");
		appDetails.setStatus(DisbursementConstants.AUTODISB_STATUS_FAILED);

		if (txStatus == null) {
			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
			txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			txStatus = transactionManager.getTransaction(txDef);
			appDetails.setErrorDesc("Disbursement is not QDP.");
		}

		finAutoApprovalDetailDAO.updateFinAutoApprovals(appDetails);
		transactionManager.commit(txStatus);
	}

	/**
	 * Prepares the New Schedule based on Realization Date
	 * 
	 * @param financeDetail
	 * @param financeMain
	 * @param financeType
	 */
	private void prepareSchedule(FinanceDetail financeDetail, FinanceMain financeMain, FinanceType financeType) {

		int fddLockPeriod = financeType.getFddLockPeriod();
		if (financeMain.isAllowGrcPeriod() && !ImplementationConstants.APPLY_FDDLOCKPERIOD_AFTERGRACE) {
			fddLockPeriod = 0;
		}

		if (!ImplementationConstants.ALLOW_FDD_ON_RVW_DATE) {
			fddLockPeriod = 0;
		}

		// grace period details
		if (financeMain.isAllowGrcPeriod()) {

			List<Calendar> scheduleDateList = FrequencyUtil
					.getNextDate(financeMain.getGrcPftFrq(), financeMain.getGraceTerms(),
							financeMain.getNextGrcPftDate(), HolidayHandlerTypes.MOVE_NONE, true, 0)
					.getScheduleList();

			Date grcEndDate = null;
			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				grcEndDate = calendar.getTime();
			}
			scheduleDateList = null;

			financeMain.setGrcPeriodEndDate(DateUtility.getDate(
					DateUtility.format(grcEndDate, PennantConstants.DBDateFormat), PennantConstants.DBDateFormat));

			if (financeMain.isAllowGrcPftRvw()) {

				RateDetail rateDetail = RateUtil.rates(financeMain.getGraceBaseRate(), financeMain.getFinCcy(),
						financeMain.getGraceSpecialRate(), financeMain.getGrcMargin(), financeType.getFInGrcMinRate(),
						financeType.getFinGrcMaxRate());
				Date baseDate = DateUtility.addDays(financeMain.getFinStartDate(), rateDetail.getLockingPeriod());

				// Next Grace profit Review Date
				if (ImplementationConstants.ALLOW_FDD_ON_RVW_DATE) {
					financeMain
							.setNextGrcPftRvwDate(FrequencyUtil
									.getNextDate(financeMain.getGrcPftRvwFrq(), 1, baseDate,
											HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
									.getNextFrequencyDate());
				} else {
					financeMain.setNextGrcPftRvwDate(FrequencyUtil.getNextDate(financeMain.getGrcPftRvwFrq(), 1,
							baseDate, HolidayHandlerTypes.MOVE_NONE, false, 0).getNextFrequencyDate());
				}
				financeMain.setNextGrcPftRvwDate(DateUtility
						.getDate(DateUtility.format(financeMain.getNextGrcPftRvwDate(), PennantConstants.dateFormat)));
				if (financeMain.getNextGrcPftRvwDate().after(financeMain.getGrcPeriodEndDate())) {
					financeMain.setNextGrcPftRvwDate(financeMain.getGrcPeriodEndDate());
				}
			}

			// Allow Grace Capitalization
			if (financeMain.isAllowGrcCpz()) {
				financeMain.setAllowGrcCpz(true);
				financeMain.setNextGrcCpzDate(FrequencyUtil
						.getNextDate(financeMain.getGrcCpzFrq(), 1, financeMain.getFinStartDate(),
								HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
						.getNextFrequencyDate());
				financeMain.setNextGrcCpzDate(DateUtility
						.getDate(DateUtility.format(financeMain.getNextGrcCpzDate(), PennantConstants.dateFormat)));
				if (financeMain.getNextGrcCpzDate().after(financeMain.getGrcPeriodEndDate())) {
					financeMain.setNextGrcCpzDate(financeMain.getGrcPeriodEndDate());
				}
			}

			financeMain.setNextGrcPftDate(DateUtility.getDate(DateUtility.format(FrequencyUtil
					.getNextDate(financeMain.getGrcPftFrq(), 1, financeMain.getFinStartDate(),
							HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
					.getNextFrequencyDate(), PennantConstants.dateFormat)));
		}

		// Repay period details
		if (financeMain.getRepayPftFrq() != null) {
			financeMain
					.setNextRepayPftDate(
							FrequencyUtil
									.getNextDate(financeMain.getRepayPftFrq(), 1, financeMain.getGrcPeriodEndDate(),
											HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod)
									.getNextFrequencyDate());
		}

		if (financeMain.getNextRepayPftDate() != null) {
			financeMain.setNextRepayPftDate(DateUtility
					.getDate(DateUtility.format(financeMain.getNextRepayPftDate(), PennantConstants.dateFormat)));
		}

		// Allow Repay Review
		if (financeType.isFinIsRvwAlw()) {
			financeMain.setAllowRepayRvw(financeType.isFinIsRvwAlw());

			RateDetail rateDetail = RateUtil.rates(financeMain.getRepayBaseRate(), financeMain.getFinCcy(),
					financeMain.getRepaySpecialRate(), financeMain.getRepayMargin(), financeType.getFInMinRate(),
					financeType.getFinMaxRate());
			Date baseDate = DateUtility.addDays(financeMain.getGrcPeriodEndDate(), rateDetail.getLockingPeriod());

			financeMain.setNextRepayRvwDate(FrequencyUtil.getNextDate(financeMain.getRepayRvwFrq(), 1, baseDate,
					HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod).getNextFrequencyDate());

			financeMain.setNextRepayRvwDate(DateUtility
					.getDate(DateUtility.format(financeMain.getNextRepayRvwDate(), PennantConstants.dateFormat)));
		} else {
			financeMain.setRepayRvwFrq("");
		}

		// Allow Repay Capitalization
		if (financeType.isFinIsIntCpz()) {
			financeMain.setAllowRepayCpz(financeType.isFinIsIntCpz());
			financeMain
					.setNextRepayCpzDate(
							FrequencyUtil
									.getNextDate(financeMain.getRepayCpzFrq(), 1, financeMain.getGrcPeriodEndDate(),
											HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod)
									.getNextFrequencyDate());

			financeMain.setNextRepayCpzDate(DateUtility
					.getDate(DateUtility.format(financeMain.getNextRepayCpzDate(), PennantConstants.dateFormat)));
		} else {
			financeMain.setRepayCpzFrq("");
		}

		// Repay Frequency
		if (financeMain.getRepayFrq() != null) {
			financeMain
					.setNextRepayDate(
							FrequencyUtil
									.getNextDate(financeMain.getRepayFrq(), 1, financeMain.getGrcPeriodEndDate(),
											HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod)
									.getNextFrequencyDate());
		}
		if (financeMain.getNextRepayDate() != null) {
			financeMain.setNextRepayDate(DateUtility
					.getDate(DateUtility.format(financeMain.getNextRepayDate(), PennantConstants.dateFormat)));
		}

		// Maturity Date
		if (financeMain.getRepayFrq() != null && financeMain.getNextRepayDate() != null) {
			List<Calendar> scheduleDateList = FrequencyUtil
					.getNextDate(financeMain.getRepayFrq(), financeMain.getNumberOfTerms(),
							financeMain.getNextRepayDate(), HolidayHandlerTypes.MOVE_NONE, true, 0)
					.getScheduleList();
			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				financeMain.setMaturityDate(calendar.getTime());
				financeMain.setMaturityDate(DateUtility
						.getDate(DateUtility.format(financeMain.getMaturityDate(), PennantConstants.dateFormat)));
			}
		}

		financeDetail.setFinScheduleData(ScheduleGenerator.getNewSchd(financeDetail.getFinScheduleData()));

		if (financeDetail.getFinScheduleData().getFinanceScheduleDetails().size() != 0) {

			financeDetail.setFinScheduleData(
					ScheduleCalculator.getCalSchd(financeDetail.getFinScheduleData(), BigDecimal.ZERO));
			financeDetail.getFinScheduleData().setSchduleGenerated(true);
		}
	}

	private boolean checkForPreviousApproval(String finReference) {
		return finAutoApprovalDetailDAO.getFinanceIfApproved(finReference);
	}

	private boolean checkForServicing(String finReference) {
		return finAutoApprovalDetailDAO.getFinanceServiceInstruction(finReference);
	}

	private boolean checkForQuickDisbInDisbursements(String finReference) {
		return finAutoApprovalDetailDAO.CheckDisbForQDP(finReference);
	}

	private boolean validateQDPDays(String paymentType, Date disbDate, Date realizedDate) {
		int days = qdpValidityDays.get(paymentType);
		Date diffDate = DateUtility.addDays(disbDate, days);

		if (realizedDate.compareTo(diffDate) > 0) {
			return false;
		}
		return true;
	}

	private void loadQDPValidityDays() {
		qdpValidityDays = finAutoApprovalDetailDAO.loadQDPValidityDays();

	}

	// Skip the records already updated with status "D" or those were not uploaded in this batch.
	private boolean recordToBeProcessed(List<FinAdvancePayments> finAdvancePayments, FinanceDisbursement disbursement,
			FinAutoApprovalDetails appDetails) {
		for (FinAdvancePayments finAdvancePayment : finAdvancePayments) {
			if (StringUtils.equals(null, disbursement.getDisbStatus())
					&& (finAdvancePayment.getPaymentId() == appDetails.getDisbId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * 
	 * @param afinanceMain
	 * @param tranType
	 * @param userDetails
	 */
	private AuditHeader doProcess(FinanceDetail financeDetail, String tranType, LoggedInUser userDetails)
			throws Exception {
		AuditHeader auditHeader = null;
		FinanceMain afinanceMain = financeDetail.getFinScheduleData().getFinanceMain();

		afinanceMain.setLastMntBy(userDetails.getUserId());
		afinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		afinanceMain.setAutoApprove(true);

		afinanceMain.setUserDetails(userDetails);
		financeDetail.setUserDetails(userDetails);
		financeDetail.getCustomerDetails().setUserDetails(userDetails);

		auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
		auditHeader = financeDetailService.doApprove(auditHeader, false);

		if (auditHeader.getOverideMessage() != null) {
			for (int i = 0; i < auditHeader.getOverideMessage().size(); i++) {
				ErrorDetail overideDetail = auditHeader.getOverideMessage().get(i);
				if (!isOverride(overideMap, overideDetail)) {
					setOverideMap(overideMap, overideDetail);
				}
			}

			auditHeader.setOverideMap(overideMap);
			setOverideMap(overideMap);
			auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
			auditHeader = financeDetailService.doApprove(auditHeader, false);
		}

		return auditHeader;

	}

	private HashMap<String, ArrayList<ErrorDetail>> setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap,
			ErrorDetail errorDetail) {

		if (StringUtils.isNotBlank(errorDetail.getField())) {

			ArrayList<ErrorDetail> errorDetails = null;

			if (overideMap.containsKey(errorDetail.getField())) {
				errorDetails = overideMap.get(errorDetail.getField());

				for (int i = 0; i < errorDetails.size(); i++) {
					if (errorDetails.get(i).getCode().equals(errorDetail.getCode())) {
						errorDetails.remove(i);
						break;
					}
				}

				overideMap.remove(errorDetail.getField());

			} else {
				errorDetails = new ArrayList<ErrorDetail>();

			}

			errorDetail.setOveride(true);
			errorDetails.add(errorDetail);

			overideMap.put(errorDetail.getField(), errorDetails);

		}
		return overideMap;
	}

	private boolean isOverride(HashMap<String, ArrayList<ErrorDetail>> overideMap, ErrorDetail errorDetail) {

		if (overideMap.containsKey(errorDetail.getField())) {

			ArrayList<ErrorDetail> errorDetails = overideMap.get(errorDetail.getField());

			for (int i = 0; i < errorDetails.size(); i++) {

				if (errorDetails.get(i).getCode().equals(errorDetail.getCode())) {
					return errorDetails.get(i).isOveride();
				}
			}

		}

		return false;
	}

	public void doLoadWorkFlow(boolean workFlowEnabled, long workFlowId, String nextTaskID)
			throws FactoryConfigurationError {
		if (workFlowEnabled) {
			setWorkFlow(new WorkflowEngine(WorkFlowUtil.getWorkflow(workFlowId).getWorkFlowXml()));
		}
	}

	private AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
				afinanceDetail.getUserDetails(), getOverideMap());
	}

	public WorkflowEngine getWorkFlow() {
		return workFlow;
	}

	public void setWorkFlow(WorkflowEngine workFlow) {
		this.workFlow = workFlow;
	}

	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public FinAutoApprovalDetailDAO getFinAutoApprovalDetailDAO() {
		return finAutoApprovalDetailDAO;
	}

	public void setFinAutoApprovalDetailDAO(FinAutoApprovalDetailDAO finAutoApprovalDetailDAO) {
		this.finAutoApprovalDetailDAO = finAutoApprovalDetailDAO;
	}

}
