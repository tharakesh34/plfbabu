package com.pennant.backend.service.finance.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.core.AccrualService;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.commitment.CommitmentDAO;
import com.pennant.backend.dao.commitment.CommitmentMovementDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.RepaymentCancellationService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.eod.dao.CustomerQueuingDAO;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

public class RepaymentCancellationServiceImpl extends GenericService<FinanceMain> implements
		RepaymentCancellationService {

	private static final Logger logger = Logger.getLogger(RepaymentCancellationServiceImpl.class);
	
	private AuditHeaderDAO 					auditHeaderDAO;
	
	private FinanceRepaymentsDAO			financeRepaymentsDAO;
	private FinLogEntryDetailDAO			finLogEntryDetailDAO;
	private PostingsDAO						postingsDAO;
	private PostingsPreparationUtil 		postingsPreparationUtil;
	private FinanceScheduleDetailDAO 		financeScheduleDetailDAO;
	private FinanceDisbursementDAO 			financeDisbursementDAO;
	private RepayInstructionDAO 			repayInstructionDAO; 
	private FinanceMainDAO	 				financeMainDAO;
	private FinanceProfitDetailDAO 			financeProfitDetailDAO;
	private CustomerDAO 					customerDAO;
	private CustomerStatusCodeDAO 			customerStatusCodeDAO;
	private FinStatusDetailDAO 				finStatusDetailDAO;
	private CommitmentDAO 					commitmentDAO;
	private CommitmentMovementDAO 			commitmentMovementDAO;
	private AccrualService 					accrualService;
	// EOD Process Checking
	private CustomerQueuingDAO customerQueuingDAO;
	
	public RepaymentCancellationServiceImpl() {
		super();
	}

	/**
	 * Method for Fetching FInance Details & Repay Schedule Details
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
	public FinanceDetail getFinanceDetailById(String finReference, String type) {
		logger.debug("Entering");

		//Finance Details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);
		scheduleData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, false));

		//Repayment Details
		scheduleData.setRepayDetails(getFinanceRepaymentsDAO().getFinRepayListByFinRef(finReference, true, ""));

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table FinanceMain/FinanceMain_Temp by
	 * using FinanceMainDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using FinanceMainDAO's update method 3) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		TableType tableType = TableType.MAIN_TAB;
		if (financeMain.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		financeMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_CANCELRPY);
		if (tableType == TableType.MAIN_TAB) {
			financeMain.setRcdMaintainSts("");
		}

		//Repayments Postings Details Process Execution
		if (!financeMain.isWorkflow()) {
			String errorCode = processRepayCancellation(financeMain);
			if (StringUtils.isNotBlank(errorCode)) {
				throw new InterfaceException("9999", errorCode);
			}
		} else {

			// Finance Main Details Save And Update
			//=======================================
			if (financeMain.isNew()) {
				getFinanceMainDAO().save(financeMain, tableType, false);
			} else {
				getFinanceMainDAO().update(financeMain, tableType, false);
			}
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain
				.getBefImage(), financeMain));

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinanceMainDAO().delete with parameters financeMain,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		// ScheduleDetails deletion
		getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, false, false);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain
				.getBefImage(), financeMain));
		getAuditHeaderDAO().addAudit(auditHeader);

		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(financeDetail);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinanceMainDAO().delete with
	 * parameters financeMain,"" b) NEW Add new record in to main table by using getFinanceMainDAO().save with
	 * parameters financeMain,"" c) EDIT Update record in the main table by using getFinanceMainDAO().update with
	 * parameters financeMain,"" 3) Delete the record from the workFlow table by using getFinanceMainDAO().delete with
	 * parameters financeMain,"_Temp" 4) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtFinanceMain by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");

		String tranType = "";
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

		//Execute Accounting Details Process
		//=======================================
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		//Finance Repayment Cancellation Posting Process Execution
		//=====================================
		String errorCode = processRepayCancellation(financeMain);
		if (StringUtils.isNotBlank(errorCode)) {
			throw new InterfaceException("9999", errorCode);
		}

		tranType = PennantConstants.TRAN_UPD;

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, false, true);
		auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
				financeMain.getBefImage(), financeMain));

		// Adding audit as deleted from TEMP table
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain
				.getBefImage(), financeMain));

		// Adding audit as Insert/Update/deleted into main table
		getAuditHeaderDAO().addAudit(auditHeader);

		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(financeDetail);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getFinanceMainDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for Validate Finance Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @param isWIF
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		FinanceDetail financeDetail = (FinanceDetail) auditDetail.getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		FinanceMain tempFinanceMain = null;
		if (financeMain.isWorkflow()) {
			tempFinanceMain = getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "_Temp", false);
		}
		FinanceMain befFinanceMain = getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "", false);
		FinanceMain oldFinanceMain = financeMain.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = financeMain.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (financeMain.isNew()) { // for New record or new record into work flow

			if (!financeMain.isWorkflow()) {// With out Work flow only new
				// records
				if (befFinanceMain != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
					// records type is new
					if (befFinanceMain != null || tempFinanceMain != null) { // if
						// records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinanceMain == null || tempFinanceMain != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!financeMain.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befFinanceMain == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinanceMain != null && !oldFinanceMain.getLastMntOn().equals(befFinanceMain.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41003", errParm, valueParm), usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41004", errParm, valueParm), usrLanguage));
						}
					}
				}
			} else {

				if (tempFinanceMain == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}

				if (tempFinanceMain != null && oldFinanceMain != null
						&& !oldFinanceMain.getLastMntOn().equals(tempFinanceMain.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}
			}
		}
		
		// Checking , if Customer is in EOD process or not. if Yes, not allowed to do an action
		int eodProgressCount = getCustomerQueuingDAO().getProgressCountByCust(financeMain.getCustID());

		// If Customer Exists in EOD Processing, Not allowed to Maintenance till completion
		if(eodProgressCount > 0){
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					PennantConstants.KEY_FIELD, "60203", errParm, valueParm), usrLanguage));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !financeMain.isWorkflow()) {
			auditDetail.setBefImage(befFinanceMain);
		}

		return auditDetail;
	}

	/**
	 * Method for Processing Repayments Cancellation Based on Log Entry Details
	 * 
	 * @param finReference
	 * @param branch
	 * @return
	 * @throws AccountNotFoundException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private String processRepayCancellation(FinanceMain financeMain) throws InterfaceException,
			IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		String finReference = financeMain.getFinReference();

		//Fetch Repayments Details List based on Finance Reference
		Date curAppDate = DateUtility.getAppDate();
		List<FinanceRepayments> repayList = getFinanceRepaymentsDAO().getFinRepayListByFinRef(finReference, true, "");
		if (repayList != null && !repayList.isEmpty()) {

			FinanceRepayments repayment = repayList.get(0);
			Date rpyValueDate = repayment.getFinValueDate();

			//Fetch Log Entry Details Greater than this Repayments Entry , which are having Schedule Recalculation
			//If Any Exist Case after this Repayments with Schedule Recalculation then Stop Process
			//============================================
			List<FinLogEntryDetail> list = getFinLogEntryDetailDAO().getFinLogEntryDetailList(finReference,
					0);
			if (list != null && !list.isEmpty()) {
				return "Finance is Maintained after this Repayment done.";
			}

			//Fetch Repay header Details If Repayment Done from System
			FinRepayHeader repayHeader = getFinanceRepaymentsDAO().getFinRepayHeader(finReference,
					repayment.getLinkedTranId(), "");
			String finEventCode = "";
			if (repayHeader == null) {
				finEventCode = FinanceConstants.FINSER_EVENT_EARLYRPY;
			} else {
				finEventCode = repayHeader.getFinEvent();
			}

			//Valid Check for Finance Reversal On Active Finance Or not with ValueDate CheckUp
			if (!financeMain.isFinIsActive()) {

				//Not Allowed for Inactive Finances
				return "Fiannce Cannot be Processed for Reversal of Payment. Finance is in InActive State.";
			}

			//Is Schedule Regenerated >>> Adjust Finance Details From Log Tables to Main Tables and remove data from Log Tables
			//Otherwise Only Schedule Change with Repayments Amount
			//============================================
			FinLogEntryDetail detail = getFinLogEntryDetailDAO().getFinLogEntryDetail(0);
			boolean isMigratedRepayment = false;
			if (detail == null) {
				logger.debug("Log Entry Details Missing. Cancellation process for Manual Reversal Payment Process");

				Date curBDay = DateUtility.getAppDate();

				detail = new FinLogEntryDetail();
				detail.setFinReference(finReference);
				detail.setEventAction(FinanceConstants.FINSER_EVENT_EARLYRPY);
				detail.setSchdlRecal(false);
				detail.setPostDate(curBDay);
				detail.setReversalCompleted(false);
				isMigratedRepayment = true;
			}

			//Posting Reversal Case Program Calling in Equation
			//============================================
			long linkedTranId = 0;
			if (!isMigratedRepayment) {
				linkedTranId = repayment.getLinkedTranId();
				getPostingsPreparationUtil().getReversalsByLinkedTranID(linkedTranId);
			}

			//Overdue Recovery Details Reset Back to Original State , If any penalties Paid On this Repayments Process
			//============================================

			//Calculate Total Penalty Amount Paid based on this Transaction 
			/*
			 * BigDecimal totalPenaltyPaid = getPostingsDAO().getPostAmtByTranIdandEvent(finReference,
			 * AccountEventConstants.ACCEVENT_LATEPAY , repayment.getLinkedTranId());
			 * if(totalPenaltyPaid.compareTo(BigDecimal.ZERO) > 0){ for (FinanceRepayments repay : repayList) {
			 * 
			 * } }
			 */

			if (!detail.isSchdlRecal()) {

				for (int i = 0; i < repayList.size(); i++) {

					FinanceRepayments financeRepayment = repayList.get(i);

					// Finance Schedule Details Update
					FinanceScheduleDetail scheduleDetail = getFinanceScheduleDetailDAO().getFinanceScheduleDetailById(
							finReference, financeRepayment.getFinSchdDate(), "", false);

					scheduleDetail = updateScheduleDetailsData(scheduleDetail, financeRepayment);
					getFinanceScheduleDetailDAO().updateForRpy(scheduleDetail);

				}

			} else {

				//Deletion of Finance Schedule Related Details From Main Table
				listDeletion(finReference, "", false, 0);

				//Fetching Last Log Entry Finance Details
				FinScheduleData scheduleData = getFinSchDataByFinRef(finReference, detail.getLogKey(), "_Log");
				scheduleData.setFinanceMain(financeMain);

				//Re-Insert Log Entry Data before Repayments Process Recalculations
				listSave(scheduleData, "", 0);

				//Delete Data from Log Entry Tables After Inserting into Main Tables
				listDeletion(finReference, "_Log", false, detail.getLogKey());

			}

			//Finance Repayments Amount Updation if Principal Amount Exists
			//============================================
			BigDecimal totalPriAmount = BigDecimal.ZERO;
			for (FinanceRepayments repay : repayList) {
				if (repay.getFinSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					totalPriAmount = totalPriAmount.add(repay.getFinSchdPriPaid());
				}
			}

			//Check Current Finance Max Status For updation
			//============================================
			if (totalPriAmount.compareTo(BigDecimal.ZERO) > 0) {
				boolean isStsChanged = false;
				String curFinStatus = getCustomerStatusCodeDAO().getFinanceStatus(finReference, true);
				if (curFinStatus != null && !financeMain.getFinStatus().equals(curFinStatus)) {
					isStsChanged = true;
				}

				// Finance Main Details Update
				financeMain.setClosingStatus(null);
				financeMain.setFinIsActive(true);
				financeMain.setFinRepaymentAmount(financeMain.getFinRepaymentAmount().subtract(totalPriAmount));
 
				getFinanceMainDAO().updateRepaymentAmount(finReference, financeMain.getFinCurrAssetValue().add(
						financeMain.getFeeChargeAmt() == null? BigDecimal.ZERO : financeMain.getFeeChargeAmt()).add(
								financeMain.getInsuranceAmt() == null? BigDecimal.ZERO : financeMain.getInsuranceAmt()), 
						financeMain.getFinRepaymentAmount(), curFinStatus, FinanceConstants.FINSTSRSN_MANUAL,true, false);

				//Finance Status Details insertion, if status modified then change to High Risk Level
				if (isStsChanged) {
					FinStatusDetail statusDetail = new FinStatusDetail();
					statusDetail.setFinReference(financeMain.getFinReference());
					statusDetail.setValueDate(curAppDate);
					statusDetail.setCustId(financeMain.getCustID());
					statusDetail.setFinStatus(curFinStatus);

					getFinStatusDetailDAO().saveOrUpdateFinStatus(statusDetail);
				}

				// Finance Commitment Reference Posting Details
				Commitment commitment = null;
				if (StringUtils.isNotBlank(financeMain.getFinCommitmentRef())) {
					commitment = getCommitmentDAO().getCommitmentById(financeMain.getFinCommitmentRef().trim(), "");

					if (commitment != null && commitment.isRevolving()) {

						BigDecimal cmtUtlAmt = CalculationUtil.getConvertedAmount(financeMain.getFinCcy(),
								commitment.getCmtCcy(), totalPriAmount);
						getCommitmentDAO().updateCommitmentAmounts(commitment.getCmtReference(), cmtUtlAmt,
								commitment.getCmtExpDate());
						CommitmentMovement cmtMovement = prepareCommitMovement(commitment, financeMain, cmtUtlAmt,
								linkedTranId);
						if (cmtMovement != null) {
							getCommitmentMovementDAO().save(cmtMovement, "");
						}
					}
				}
			}

			if (!isMigratedRepayment) {

				// Update Log Entry Based on FinPostDate and Reference
				//============================================
				getFinLogEntryDetailDAO().updateLogEntryStatus(detail);

				//Remove Repayments Terms based on Linked Transaction ID
				//============================================
				getFinanceRepaymentsDAO().deleteRpyDetailbyLinkedTranId(repayment.getLinkedTranId(), finReference);

				//Remove FinRepay Header Details
				getFinanceRepaymentsDAO().deleteFinRepayHeaderByTranId(finReference, repayment.getLinkedTranId(), "");

				//Remove Repayment Schedule Details 
				getFinanceRepaymentsDAO().deleteFinRepaySchListByTranId(finReference, repayment.getLinkedTranId(), "");

			} else {

				// Save Log Entry Based on FinPostDate and Reference
				//============================================
				detail.setReversalCompleted(true);
				getFinLogEntryDetailDAO().save(detail);

				//Remove Repayments Terms based on Linked Transaction ID
				//============================================
				getFinanceRepaymentsDAO().deleteRpyDetailbyMaxPostDate(rpyValueDate, finReference);

			}

			//Finance Accrual Calculations
			//============================================
			List<FinanceScheduleDetail> finSchedeuleDetails = getFinanceScheduleDetailDAO().getFinSchdDetailsForBatch(
					finReference);
			FinanceProfitDetail profitDetail = getFinanceProfitDetailDAO().getFinProfitDetailsById(finReference);
			profitDetail = accrualService.calProfitDetails(financeMain, finSchedeuleDetails, profitDetail, curAppDate);
			String worstSts = getCustomerStatusCodeDAO().getFinanceStatus(profitDetail.getFinReference(), false);
			profitDetail.setFinWorstStatus(worstSts);

			//Reset Back Repayments Details
			repayList = getFinanceRepaymentsDAO().getFinRepayListByFinRef(finReference, true, "");
			if (repayList != null && !repayList.isEmpty()) {

				BigDecimal totPri = BigDecimal.ZERO;
				BigDecimal totPft = BigDecimal.ZERO;
				for (FinanceRepayments repay : repayList) {
					totPri = totPri.add(repay.getFinSchdPriPaid());
					totPft = totPft.add(repay.getFinSchdPftPaid());
				}

				profitDetail.setLatestRpyDate(repayList.get(0).getFinPostDate());
				profitDetail.setLatestRpyPri(totPri);
				profitDetail.setLatestRpyPft(totPft);
			} else {
				profitDetail.setClosingStatus(financeMain.getClosingStatus());
				profitDetail.setLatestRpyDate(null);
				profitDetail.setLatestRpyPri(BigDecimal.ZERO);
				profitDetail.setLatestRpyPft(BigDecimal.ZERO);
			}
			profitDetail.setClosingStatus(null);
			profitDetail.setFinIsActive(financeMain.isFinIsActive());

			getFinanceProfitDetailDAO().update(profitDetail, true);

			return "";

		}
		return "No Repayment Details are Exists.";
	}

	/**
	 * Method for Updation of Schedule Details With Repayment Details Reversal
	 * 
	 * @param schedule
	 * @param repayment
	 * @return
	 */
	private FinanceScheduleDetail updateScheduleDetailsData(FinanceScheduleDetail schedule, FinanceRepayments repayment) {
		logger.debug("Entering");

		schedule.setSchdPftPaid(schedule.getSchdPftPaid().subtract(repayment.getFinSchdPftPaid()));
		schedule.setSchdPriPaid(schedule.getSchdPriPaid().subtract(repayment.getFinSchdPriPaid()));

		//Fee Details
		schedule.setSchdFeePaid(schedule.getSchdFeePaid().subtract(repayment.getSchdFeePaid()));
		schedule.setSchdInsPaid(schedule.getSchdInsPaid().subtract(repayment.getSchdInsPaid()));
		schedule.setSuplRentPaid(schedule.getSuplRentPaid().subtract(repayment.getSchdSuplRentPaid()));
		schedule.setIncrCostPaid(schedule.getIncrCostPaid().subtract(repayment.getSchdIncrCostPaid()));

		// Finance Schedule Profit Balance Check
		schedule.setSchPriPaid(false);
		schedule.setSchPftPaid(false);
		if ((schedule.getProfitSchd().subtract(schedule.getSchdPftPaid())).compareTo(BigDecimal.ZERO) == 0) {
			schedule.setSchPftPaid(true);

			// Finance Schedule Principal Balance Check
			if ((schedule.getPrincipalSchd().subtract(schedule.getSchdPriPaid())).compareTo(BigDecimal.ZERO) == 0) {
				schedule.setSchPriPaid(true);
			} else {
				schedule.setSchPriPaid(false);
			}
		} else {
			schedule.setSchPftPaid(false);
		}
		logger.debug("Leaving");
		return schedule;
	}

	/**
	 * Method for Add a Movement Entry for Commitment Repayment Event, if Only for Revolving Commitment
	 * 
	 * @param commitment
	 * @param dataSet
	 * @param postAmount
	 * @param linkedtranId
	 * @return
	 */
	private CommitmentMovement prepareCommitMovement(Commitment commitment, FinanceMain financeMain,
			BigDecimal postAmount, long linkedtranId) {

		CommitmentMovement movement = new CommitmentMovement();
		Date curBussDate = DateUtility.getAppDate();

		movement.setCmtReference(commitment.getCmtReference());
		movement.setFinReference(financeMain.getFinReference());
		movement.setFinBranch(financeMain.getFinBranch());
		movement.setFinType(financeMain.getFinType());
		movement.setMovementDate(curBussDate);
		movement.setMovementOrder(getCommitmentMovementDAO().getMaxMovementOrderByRef(commitment.getCmtReference()) + 1);
		movement.setMovementType("RR");//Repayment Reversal
		movement.setMovementAmount(postAmount);
		movement.setCmtAmount(commitment.getCmtAmount());
		movement.setCmtUtilizedAmount(commitment.getCmtUtilizedAmount().add(postAmount));
		if (commitment.getCmtExpDate().compareTo(curBussDate) < 0) {
			movement.setCmtAvailable(BigDecimal.ZERO);
		} else {
			movement.setCmtAvailable(commitment.getCmtAvailable().subtract(postAmount));
		}
		movement.setCmtCharges(BigDecimal.ZERO);
		movement.setLinkedTranId(linkedtranId);
		movement.setVersion(1);
		movement.setLastMntBy(9999);
		movement.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		movement.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		movement.setRoleCode("");
		movement.setNextRoleCode("");
		movement.setTaskId("");
		movement.setNextTaskId("");
		movement.setRecordType("");
		movement.setWorkflowId(0);

		return movement;

	}

	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 * **/
	private FinScheduleData getFinSchDataByFinRef(String finReference, long logKey, String type) {
		logger.debug("Entering");

		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type,
				false, logKey));
		finSchData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type,
				false, logKey));
		finSchData.setRepayInstructions(getRepayInstructionDAO()
				.getRepayInstructions(finReference, type, false, logKey));
		logger.debug("Leaving");
		return finSchData;
	}

	/**
	 * Method to delete schedule, disbursement, repayinstruction lists.
	 * 
	 * @param finDetail
	 * @param tableType
	 * @param isWIF
	 */
	private void listDeletion(String finReference, String tableType, boolean isWIF, long logKey) {
		logger.debug("Entering");

		getFinanceScheduleDetailDAO().deleteByFinReference(finReference, tableType, isWIF, logKey);
		getFinanceDisbursementDAO().deleteByFinReference(finReference, tableType, isWIF, logKey);
		getRepayInstructionDAO().deleteByFinReference(finReference, tableType, isWIF, logKey);

		logger.debug("Leaving");
	}

	private void listSave(FinScheduleData finDetail, String tableType, long logKey) {
		logger.debug("Entering ");
		HashMap<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();

		// Finance Schedule Details
		for (int i = 0; i < finDetail.getFinanceScheduleDetails().size(); i++) {
			finDetail.getFinanceScheduleDetails().get(i).setLastMntBy(finDetail.getFinanceMain().getLastMntBy());
			finDetail.getFinanceScheduleDetails().get(i).setFinReference(finDetail.getFinanceMain().getFinReference());
			int seqNo = 0;

			if (mapDateSeq.containsKey(finDetail.getFinanceScheduleDetails().get(i).getSchDate())) {
				seqNo = mapDateSeq.get(finDetail.getFinanceScheduleDetails().get(i).getSchDate());
				mapDateSeq.remove(finDetail.getFinanceScheduleDetails().get(i).getSchDate());
			}
			seqNo = seqNo + 1;
			mapDateSeq.put(finDetail.getFinanceScheduleDetails().get(i).getSchDate(), seqNo);
			finDetail.getFinanceScheduleDetails().get(i).setSchSeq(seqNo);
			finDetail.getFinanceScheduleDetails().get(i).setLogKey(logKey);
		}
		getFinanceScheduleDetailDAO().saveList(finDetail.getFinanceScheduleDetails(), tableType, false);

		// Finance Disbursement Details
		mapDateSeq = new HashMap<Date, Integer>();
		Date curBDay = DateUtility.getAppDate();
		for (int i = 0; i < finDetail.getDisbursementDetails().size(); i++) {
			finDetail.getDisbursementDetails().get(i).setFinReference(finDetail.getFinanceMain().getFinReference());
			finDetail.getDisbursementDetails().get(i).setDisbReqDate(curBDay);
			finDetail.getDisbursementDetails().get(i).setDisbIsActive(true);
			finDetail.getDisbursementDetails().get(i).setDisbDisbursed(true);
			finDetail.getDisbursementDetails().get(i).setLogKey(logKey);
		}
		getFinanceDisbursementDAO().saveList(finDetail.getDisbursementDetails(), tableType, false);

		//Finance Repay Instruction Details
		for (int i = 0; i < finDetail.getRepayInstructions().size(); i++) {
			finDetail.getRepayInstructions().get(i).setFinReference(finDetail.getFinanceMain().getFinReference());
			finDetail.getRepayInstructions().get(i).setLogKey(logKey);
		}
		getRepayInstructionDAO().saveList(finDetail.getRepayInstructions(), tableType, false);

		logger.debug("Leaving ");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}

	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

	public FinLogEntryDetailDAO getFinLogEntryDetailDAO() {
		return finLogEntryDetailDAO;
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
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

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public CustomerStatusCodeDAO getCustomerStatusCodeDAO() {
		return customerStatusCodeDAO;
	}

	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
		this.customerStatusCodeDAO = customerStatusCodeDAO;
	}

	public void setFinStatusDetailDAO(FinStatusDetailDAO finStatusDetailDAO) {
		this.finStatusDetailDAO = finStatusDetailDAO;
	}

	public FinStatusDetailDAO getFinStatusDetailDAO() {
		return finStatusDetailDAO;
	}

	public CommitmentDAO getCommitmentDAO() {
		return commitmentDAO;
	}

	public void setCommitmentDAO(CommitmentDAO commitmentDAO) {
		this.commitmentDAO = commitmentDAO;
	}

	public CommitmentMovementDAO getCommitmentMovementDAO() {
		return commitmentMovementDAO;
	}

	public void setCommitmentMovementDAO(CommitmentMovementDAO commitmentMovementDAO) {
		this.commitmentMovementDAO = commitmentMovementDAO;
	}

	public CustomerQueuingDAO getCustomerQueuingDAO() {
		return customerQueuingDAO;
	}

	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}


	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

}
