package com.pennant.backend.service.finance.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.applicationmaster.BounceReasonDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.insurancedetails.FinInsurancesDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.exception.PFFInterfaceException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

public class ReceiptCancellationServiceImpl extends GenericService<FinReceiptHeader>  implements ReceiptCancellationService {
	private final static Logger				logger	= Logger.getLogger(ReceiptCancellationServiceImpl.class);

	private FinReceiptHeaderDAO				finReceiptHeaderDAO;
	private FinReceiptDetailDAO				finReceiptDetailDAO;
	private FinanceRepaymentsDAO			financeRepaymentsDAO;
	private FinLogEntryDetailDAO			finLogEntryDetailDAO;
	private PostingsPreparationUtil			postingsPreparationUtil;
	private FinanceMainDAO					financeMainDAO;
	private FinanceScheduleDetailDAO		financeScheduleDetailDAO;
	private FinanceDisbursementDAO			financeDisbursementDAO;
	private RepayInstructionDAO				repayInstructionDAO;
	private FinanceProfitDetailDAO			financeProfitDetailDAO;
	private RepaymentPostingsUtil			repaymentPostingsUtil;
	private FinODDetailsDAO					finODDetailsDAO;
	private PostingsDAO						postingsDAO;
	private ManualAdviseDAO 				manualAdviseDAO;
	private FinExcessAmountDAO				finExcessAmountDAO;
	private FinFeeScheduleDetailDAO			finFeeScheduleDetailDAO;
	private FinInsurancesDAO				finInsurancesDAO;
	private AuditHeaderDAO 					auditHeaderDAO;
	private BounceReasonDAO 			    bounceReasonDAO;
	private RuleDAO 			     		ruleDAO;
	private RuleExecutionUtil 			    ruleExecutionUtil;
	
	
	public ReceiptCancellationServiceImpl() {
		super();
	}

	/**
	 * Method for Fetching Receipt Details , record is waiting for Realization
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
	public FinReceiptHeader getFinReceiptHeaderById(long receiptID, String type) {
		logger.debug("Entering");

		// Receipt Header Details
		FinReceiptHeader receiptHeader = null;
		receiptHeader = getFinReceiptHeaderDAO().getReceiptHeaderByID(receiptID, "_View");

		// Fetch Receipt Detail List
		if(receiptHeader != null){
			List<FinReceiptDetail> receiptDetailList = getFinReceiptDetailDAO().getReceiptHeaderByID(receiptID, "_AView");
			receiptHeader.setReceiptDetails(receiptDetailList);
			
			// Fetch Repay Headers List
			List<FinRepayHeader> rpyHeaderList = getFinanceRepaymentsDAO().getFinRepayHeadersByRef(receiptHeader.getReference(), "");
			
			// Fetch List of Repay Schedules
			List<RepayScheduleDetail> rpySchList = getFinanceRepaymentsDAO().getRpySchdList(receiptHeader.getReference(), "");
			for (FinRepayHeader finRepayHeader : rpyHeaderList) {
				for (RepayScheduleDetail repaySchd : rpySchList) {
					if(finRepayHeader.getRepayID() == repaySchd.getRepayID()){
						finRepayHeader.getRepayScheduleDetails().add(repaySchd);
					}
				}
			}
			
			// Repay Headers setting to Receipt Details
			for (FinReceiptDetail receiptDetail : receiptDetailList) {
				for (FinRepayHeader finRepayHeader : rpyHeaderList) {
					if(finRepayHeader.getReceiptSeqID() == receiptDetail.getReceiptSeqID()){
						receiptDetail.getRepayHeaders().add(finRepayHeader);
					}
				}
			}
			receiptHeader.setReceiptDetails(receiptDetailList);
			
			// Bounce reason Code
			if(StringUtils.isNotEmpty(receiptHeader.getRecordType())){
				receiptHeader.setManualAdvise(getManualAdviseDAO().getManualAdviseByReceiptId(receiptID,"_TView"));
			}
		}

		logger.debug("Leaving");
		return receiptHeader;
	}
	
	/**
	 * Method for fetching List of Postings details which are executed for the Transaction
	 */
	@Override
	public List<ReturnDataSet> getPostingsByTranIdList(List<Long> tranIdList) {
		return getPostingsDAO().getPostingsByTransIdList(tranIdList);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table FinReceiptHeader/FinReceiptHeader_Temp by
	 * using FinReceiptHeaderDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using FinReceiptHeaderDAO's update method 3) Audit the record in to AuditHeader and AdtFinReceiptHeader by using
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
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws PFFInterfaceException, IllegalAccessException,
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
		FinReceiptHeader receiptHeader = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (receiptHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		// Receipt Header Details Save And Update
		//=======================================
		if (receiptHeader.isNew()) {
			getFinReceiptHeaderDAO().save(receiptHeader, tableType);

			// Bounce reason Code
			if(receiptHeader.getManualAdvise() != null){
				getManualAdviseDAO().save(receiptHeader.getManualAdvise(),tableType);
			}

		} else {
			getFinReceiptHeaderDAO().update(receiptHeader, tableType);
			
			// Bounce reason Code
			if(receiptHeader.getManualAdvise() != null){
				getManualAdviseDAO().update(receiptHeader.getManualAdvise(),tableType);
			}
		}
		
		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], receiptHeader
				.getBefImage(), receiptHeader));

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinReceiptHeaderDAO().delete with parameters finReceiptHeader,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtFinReceiptHeader by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws PFFInterfaceException
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) throws PFFInterfaceException {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		FinReceiptHeader receiptHeader = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();

		// Bounce Reason Code
		if(receiptHeader.getManualAdvise() != null){
			getManualAdviseDAO().delete(receiptHeader.getManualAdvise(),TableType.TEMP_TAB);
		}
		
		// Delete Receipt Header
		getFinReceiptHeaderDAO().deleteByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], receiptHeader
				.getBefImage(), receiptHeader));
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. based on the Record type do
	 * following actions Update record in the main table by using getFinReceiptHeaderDAO().update with
	 * parameters FinReceiptHeader. Audit the record in to AuditHeader and AdtFinReceiptHeader by
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
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws PFFInterfaceException, IllegalAccessException,
	InvocationTargetException {
		logger.debug("Entering");

		String tranType = "";
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptHeader receiptHeader = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();

		//Finance Repayment Cancellation Posting Process Execution
		//=====================================
		String errorCode = procReceiptCancellation(receiptHeader);
		if (StringUtils.isNotBlank(errorCode)) {
			throw new PFFInterfaceException("9999", errorCode);
		}

		// Receipt Header Updation
		//=======================================
		tranType = PennantConstants.TRAN_UPD;
		receiptHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		receiptHeader.setRecordType("");
		receiptHeader.setRoleCode("");
		receiptHeader.setNextRoleCode("");
		receiptHeader.setTaskId("");
		receiptHeader.setNextTaskId("");
		receiptHeader.setWorkflowId(0);
		getFinReceiptHeaderDAO().update(receiptHeader, TableType.MAIN_TAB);
		
		// Bounce Reason Code
		if(receiptHeader.getManualAdvise() != null){
			getManualAdviseDAO().delete(receiptHeader.getManualAdvise(),TableType.TEMP_TAB);
		}

		// Delete Receipt Header
		getFinReceiptHeaderDAO().deleteByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB);

		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));

		// Adding audit as deleted from TEMP table
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], receiptHeader
				.getBefImage(), receiptHeader));

		// Adding audit as Insert/Update/deleted into main table
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getFinReceiptHeaderDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
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
		FinReceiptHeader receiptHeader = (FinReceiptHeader) auditDetail.getModelData();

		FinReceiptHeader tempReceiptHeader = null;
		if (receiptHeader.isWorkflow()) {
			tempReceiptHeader = getFinReceiptHeaderDAO().getReceiptHeaderByID(receiptHeader.getReceiptID(), "_Temp");
		}
		FinReceiptHeader beFinReceiptHeader = getFinReceiptHeaderDAO().getReceiptHeaderByID(receiptHeader.getReceiptID(), "");
		FinReceiptHeader oldReceiptHeader = receiptHeader.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(receiptHeader.getReceiptID());
		errParm[0] = PennantJavaUtil.getLabel("label_ReceiptID") + ":" + valueParm[0];

		if (receiptHeader.isNew()) { // for New record or new record into work flow

			if (!receiptHeader.isWorkflow()) {// With out Work flow only new
				// records
				if (beFinReceiptHeader != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (receiptHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
					// records type is new
					if (beFinReceiptHeader != null || tempReceiptHeader != null) { // if
						// records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (beFinReceiptHeader == null || tempReceiptHeader != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!receiptHeader.isWorkflow()) { // With out Work flow for update
				// and delete

				if (beFinReceiptHeader == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldReceiptHeader != null && !oldReceiptHeader.getLastMntOn().equals(beFinReceiptHeader.getLastMntOn())) {
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

				if (tempReceiptHeader == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}

				if (tempReceiptHeader != null && oldReceiptHeader != null
						&& !oldReceiptHeader.getLastMntOn().equals(tempReceiptHeader.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !receiptHeader.isWorkflow()) {
			receiptHeader.setBefImage(beFinReceiptHeader);
		}

		return auditDetail;
	}
	
	/**
	 * Method for Canceling the Presentment
	 * 
	 * @param receiptId
	 * @param returnCode
	 * @return errorMsg
	 */
	@Override
	public PresentmentDetail presentmentCancellation(PresentmentDetail presentmentDetail, String returnCode) throws Exception {
		logger.debug(Literal.ENTERING);

		FinReceiptHeader receiptHeader = getFinReceiptHeaderById(presentmentDetail.getReceiptID(), "");
		ManualAdvise manualAdvise = getManualAdvise(receiptHeader, returnCode, presentmentDetail.getMandateType());
		receiptHeader.setManualAdvise(manualAdvise);
		String errorMsg = procReceiptCancellation(receiptHeader);
		presentmentDetail.setErrMsg(errorMsg);
		presentmentDetail.setBounceID(manualAdvise.getBounceID());
		
		logger.debug(Literal.LEAVING);
		return presentmentDetail;
	}
	
	/**
	 * Method for preparing the ManualAdvise bean object
	 * 
	 * @param receiptHeader
	 * @param returnCode
	 * @param madateType 
	 * @return ManualAdvise
	 */
	private ManualAdvise getManualAdvise(FinReceiptHeader receiptHeader, String returnCode, String madateType) {
		logger.debug(Literal.ENTERING);

		FinReceiptDetail finReceiptDetail = null;
		
		if (receiptHeader.getReceiptDetails() != null && !receiptHeader.getReceiptDetails().isEmpty()) {
			for (FinReceiptDetail item : receiptHeader.getReceiptDetails()) {
				if (item.getPaymentType().equals(madateType)) {
					finReceiptDetail = item;
					break;
				}
			}
		}
		BounceReason bounceReason = getBounceReasonDAO().getBounceReasonByReturnCode(returnCode, "");
		BigDecimal bounceCharge = BigDecimal.ZERO;
		if (bounceReason != null) {
			Rule rule = getRuleDAO().getRuleByID(bounceReason.getRuleID(), "");
			BigDecimal bounceAmt = BigDecimal.ZERO;
			if (rule != null) {
				bounceAmt = (BigDecimal) getRuleExecutionUtil().executeRule(rule.getSQLRule(), finReceiptDetail.getDeclaredFieldValues(), receiptHeader.getFinCcy(), RuleReturnType.DECIMAL);
			}
			bounceCharge = PennantApplicationUtil.formateAmount(bounceAmt, CurrencyUtil.getFormat(receiptHeader.getFinCcy()));
		}
		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setAdviseType(FinanceConstants.MANUAL_ADVISE_RECEIVABLE);
		manualAdvise.setFinReference(receiptHeader.getReference());
		manualAdvise.setFeeTypeID(0);
		manualAdvise.setSequence(0);
		manualAdvise.setAdviseAmount(PennantApplicationUtil.unFormateAmount(bounceCharge, CurrencyUtil.getFormat(receiptHeader.getFinCcy())));
		manualAdvise.setPaidAmount(BigDecimal.ZERO);
		manualAdvise.setWaivedAmount(BigDecimal.ZERO);
		manualAdvise.setRemarks("");
		manualAdvise.setReceiptID(receiptHeader.getReceiptID());
		manualAdvise.setBounceID(bounceReason.getBounceID());

		logger.debug(Literal.LEAVING);

		return manualAdvise;
	}

	/**
	 * Method for Processing Repayments Cancellation Based on Log Entry Details
	 * @param receiptHeader
	 * @return String(Error Description)
	 * @throws PFFInterfaceException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private String procReceiptCancellation(FinReceiptHeader receiptHeader) throws PFFInterfaceException,
		IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		
		//Valid Check for Finance Reversal On Active Finance Or not with ValueDate CheckUp
		String finReference = receiptHeader.getReference();
		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainForBatch(finReference);
		if (!financeMain.isFinIsActive()) {
			ErrorDetails errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("60204", "", null), PennantConstants.default_Language);
			//Not Allowed for Inactive Finances
			return errorDetail.getErrorMessage();
		}
		
		boolean isRcdFound = false;
		boolean isBounceProcess = false;
		if (StringUtils.equals(receiptHeader.getReceiptModeStatus(), RepayConstants.PAYSTATUS_BOUNCE)) {
			isBounceProcess = true;
		}
		
		List<RepayScheduleDetail> rpySchdList = new ArrayList<>();
		long logKey = 0;
		BigDecimal totalPriAmount = BigDecimal.ZERO;
		List<FinReceiptDetail> receiptDetails = sortReceiptDetails(receiptHeader.getReceiptDetails());
		if(receiptDetails != null && !receiptDetails.isEmpty()){
			for (int i = receiptDetails.size() - 1; i >= 0; i--) {

				FinReceiptDetail receiptDetail = receiptDetails.get(i);

				if(isBounceProcess && (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EXCESS) || 
						StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EMIINADV) ||
						StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE))){
					continue;
				}
				
				//Fetch Log Entry Details Greater than this Repayments Entry , which are having Schedule Recalculation
				//If Any Exist Case after this Repayments with Schedule Recalculation then Stop Process
				//============================================
				List<FinLogEntryDetail> list = getFinLogEntryDetailDAO().getFinLogEntryDetailList(finReference, receiptDetail.getLogKey());
				if (list != null && !list.isEmpty()) {
					ErrorDetails errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("60206", "", null), PennantConstants.default_Language);
					return errorDetail.getErrorMessage();
				}

				// Finance Repayments Amount Updation if Principal Amount Exists
				long linkedTranId = 0;
				List<FinRepayHeader> rpyHeaders = receiptDetail.getRepayHeaders();
				for (int j = rpyHeaders.size() - 1; j >= 0; j--) {

					FinRepayHeader rpyHeader = rpyHeaders.get(j);
					if(!StringUtils.equals(rpyHeader.getFinEvent(), FinanceConstants.FINSER_EVENT_SCHDRPY) &&
							!StringUtils.equals(rpyHeader.getFinEvent(), FinanceConstants.FINSER_EVENT_EARLYRPY) &&
							!StringUtils.equals(rpyHeader.getFinEvent(), FinanceConstants.FINSER_EVENT_EARLYSETTLE)){

						// Fetch Excess Amount Details
						FinExcessAmount excess = getFinExcessAmountDAO().getExcessAmountsByRefAndType(finReference, 
								receiptHeader.getExcessAdjustTo());

						// Update Reserve Amount in FinExcessAmount
						if(excess == null || excess.getBalanceAmt().compareTo(rpyHeader.getRepayAmount()) < 0){
							ErrorDetails errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("60205", "", null), PennantConstants.default_Language);
							return errorDetail.getErrorMessage();
						}
						getFinExcessAmountDAO().updateExcessBal(excess.getExcessID(), rpyHeader.getRepayAmount().negate());
						
						continue;
					}

					if (rpyHeader.getPriAmount().compareTo(BigDecimal.ZERO) > 0) {
						totalPriAmount = totalPriAmount.add(rpyHeader.getPriAmount());
					}

					linkedTranId = rpyHeader.getLinkedTranId();
					isRcdFound = true;

					//Posting Reversal Case Program Calling in Equation
					//============================================
					getPostingsPreparationUtil().postReversalsByLinkedTranID(linkedTranId);

					//Remove Repayments Terms based on Linked Transaction ID
					//============================================
					getFinanceRepaymentsDAO().deleteRpyDetailbyLinkedTranId(linkedTranId, finReference);

					//Remove FinRepay Header Details
					//getFinanceRepaymentsDAO().deleteFinRepayHeaderByTranId(finReference, linkedTranId, "");

					//Remove Repayment Schedule Details 
					//getFinanceRepaymentsDAO().deleteFinRepaySchListByTranId(finReference, linkedTranId, "");
					
					// Gathering All repayments Schedule List
					if(rpyHeader.getRepayScheduleDetails() != null && 
							!rpyHeader.getRepayScheduleDetails().isEmpty()){
						rpySchdList.addAll(rpyHeader.getRepayScheduleDetails());
					}

				}

				// Update Log Entry Based on FinPostDate and Reference
				//============================================
				FinLogEntryDetail detail = getFinLogEntryDetailDAO().getFinLogEntryDetail(receiptDetail.getLogKey());
				if(detail == null){
					ErrorDetails errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("60207", "", null), PennantConstants.default_Language);
					return errorDetail.getErrorMessage();
				}
				logKey = detail.getLogKey();
				detail.setReversalCompleted(true);
				getFinLogEntryDetailDAO().updateLogEntryStatus(detail);

				// Manual Advise Movements Reversal
				List<ManualAdviseMovements> advMovements = getManualAdviseDAO().getAdvMovementsByReceiptSeq(receiptDetail.getReceiptID(), 
						receiptDetail.getReceiptSeqID(), "");
				if(advMovements != null && !advMovements.isEmpty()){
					for (ManualAdviseMovements movement : advMovements) {
						getManualAdviseDAO().updateAdvPayment(movement.getAdviseID(), movement.getPaidAmount().negate(),
								movement.getWaivedAmount().negate(), TableType.MAIN_TAB);
					}

					// Update Movement Status
					getManualAdviseDAO().updateMovementStatus(receiptDetail.getReceiptID(), receiptDetail.getReceiptSeqID(), receiptHeader.getReceiptModeStatus(), "");
				}

			}

			// Making Single Set of Repay Schedule Details and sent to Rendering
			Map<Date, RepayScheduleDetail> rpySchdMap = new HashMap<>();
			for (RepayScheduleDetail rpySchd : rpySchdList) {

				RepayScheduleDetail curRpySchd = null;
				if(rpySchdMap.containsKey(rpySchd.getSchDate())){
					curRpySchd = rpySchdMap.get(rpySchd.getSchDate());
					curRpySchd.setPrincipalSchdPayNow(curRpySchd.getPrincipalSchdPayNow().add(rpySchd.getPrincipalSchdPayNow()));
					curRpySchd.setProfitSchdPayNow(curRpySchd.getProfitSchdPayNow().add(rpySchd.getProfitSchdPayNow()));
					curRpySchd.setLatePftSchdPayNow(curRpySchd.getLatePftSchdPayNow().add(rpySchd.getLatePftSchdPayNow()));
					curRpySchd.setSchdFeePayNow(curRpySchd.getSchdFeePayNow().add(rpySchd.getSchdFeePayNow()));
					curRpySchd.setSchdInsPayNow(curRpySchd.getSchdInsPayNow().add(rpySchd.getSchdInsPayNow()));
					curRpySchd.setPenaltyPayNow(curRpySchd.getPenaltyPayNow().add(rpySchd.getPenaltyPayNow()));
					rpySchdMap.remove(rpySchd.getSchDate());
				}else{
					curRpySchd = rpySchd;
				}

				// Adding New Repay Schedule Object to Map after Summing data
				rpySchdMap.put(rpySchd.getSchDate(), curRpySchd);
			}

			rpySchdList = sortRpySchdDetails(new ArrayList<>(rpySchdMap.values()));
			List<FinFeeScheduleDetail> updateFeeList = new ArrayList<>();
			List<FinSchFrqInsurance> updateInsList = new ArrayList<>();
			for (RepayScheduleDetail rpySchd : rpySchdList) {

				//Overdue Recovery Details Reset Back to Original State , If any penalties Paid On this Repayments Process 
				//============================================
				if(rpySchd.getPenaltyPayNow().compareTo(BigDecimal.ZERO) > 0 || rpySchd.getLatePftSchdPayNow().compareTo(BigDecimal.ZERO) > 0){
					getFinODDetailsDAO().updateReversals(finReference, rpySchd.getSchDate(), rpySchd.getPenaltyPayNow(), rpySchd.getLatePftSchdPayNow());
				}

				// Update Fee Balance
				//============================================
				if(rpySchd.getSchdFeePayNow().compareTo(BigDecimal.ZERO) > 0){
					List<FinFeeScheduleDetail> feeList = getFinFeeScheduleDetailDAO().getFeeScheduleBySchDate(finReference, rpySchd.getSchDate());
					BigDecimal feebal = rpySchd.getSchdFeePayNow();
					for (int j = feeList.size() - 1; j >= 0; j--) {
						FinFeeScheduleDetail feeSchd = feeList.get(j);
						BigDecimal paidReverse = BigDecimal.ZERO;
						if(feebal.compareTo(BigDecimal.ZERO) == 0){
							continue;
						}
						if(feeSchd.getPaidAmount().compareTo(BigDecimal.ZERO) == 0){
							continue;
						}

						if(feebal.compareTo(feeSchd.getPaidAmount()) > 0){
							paidReverse = feeSchd.getPaidAmount();
						}else{
							paidReverse = feebal;
						}
						feebal = feebal.subtract(paidReverse);

						// Create list of updated objects to save one time
						FinFeeScheduleDetail updFeeSchd = new FinFeeScheduleDetail();
						updFeeSchd.setFeeID(feeSchd.getFeeID());
						updFeeSchd.setSchDate(feeSchd.getSchDate());
						updFeeSchd.setPaidAmount(paidReverse.negate());
						updateFeeList.add(updFeeSchd);
					}
				}

				// Update Insurance Balance
				//============================================
				if(rpySchd.getSchdInsPayNow().compareTo(BigDecimal.ZERO) > 0){
					List<FinSchFrqInsurance> insList = getFinInsurancesDAO().getInsScheduleBySchDate(finReference, rpySchd.getSchDate());
					BigDecimal insBal = rpySchd.getSchdInsPayNow();
					for (int j = insList.size() - 1; j >= 0; j--) {
						FinSchFrqInsurance insSchd = insList.get(j);
						BigDecimal paidReverse = BigDecimal.ZERO;

						if(insBal.compareTo(BigDecimal.ZERO) == 0){
							continue;
						}
						if(insSchd.getInsurancePaid().compareTo(BigDecimal.ZERO) == 0){
							continue;
						}

						if(insBal.compareTo(insSchd.getInsurancePaid()) > 0){
							paidReverse = insSchd.getInsurancePaid();
						}else{
							paidReverse = insBal;
						}
						insBal = insBal.subtract(paidReverse);

						// Create list of updated objects to save one time
						FinSchFrqInsurance updInsSchd = new FinSchFrqInsurance();
						updInsSchd.setInsId(insSchd.getInsId());
						updInsSchd.setInsSchDate(insSchd.getInsSchDate());
						updInsSchd.setInsurancePaid(paidReverse.negate());
						updateInsList.add(updInsSchd);
					}
				}
			}

			// Fee Schedule Details Updation
			if(!updateFeeList.isEmpty()){
				getFinFeeScheduleDetailDAO().updateFeeSchdPaids(updateFeeList);
			}

			// Insurance Schedule Details Updation
			if(!updateInsList.isEmpty()){
				getFinInsurancesDAO().updateInsSchdPaids(updateInsList);
			}

			rpySchdList = null;
			rpySchdMap = null;
			updateFeeList = null;
			updateInsList = null;

			//Deletion of Finance Schedule Related Details From Main Table
			listDeletion(finReference, "", false, 0);

			//Fetching Last Log Entry Finance Details
			FinanceProfitDetail pftDetail = getFinanceProfitDetailDAO().getFinPftDetailForBatch(finReference);
			FinScheduleData scheduleData = getFinSchDataByFinRef(finReference, logKey, "_Log");
			scheduleData.setFinanceMain(financeMain);

			//Re-Insert Log Entry Data before Repayments Process Recalculations
			listSave(scheduleData, "", 0);

			//Delete Data from Log Entry Tables After Inserting into Main Tables
			for (int i = 0; i < receiptDetails.size(); i++) {
				listDeletion(finReference, "_Log", false, receiptDetails.get(i).getLogKey());
			}

			//Check Current Finance Max Status For updation
			//============================================
			getRepaymentPostingsUtil().updateStatus(financeMain, DateUtility.getAppDate(), scheduleData.getFinanceScheduleDetails(), pftDetail);
			if (totalPriAmount.compareTo(BigDecimal.ZERO) > 0) {

				// Finance Main Details Update
				financeMain.setFinRepaymentAmount(financeMain.getFinRepaymentAmount().subtract(totalPriAmount));
				getFinanceMainDAO().updateRepaymentAmount(finReference, financeMain.getFinCurrAssetValue().add(
						financeMain.getFeeChargeAmt()).add(financeMain.getInsuranceAmt()), financeMain.getFinRepaymentAmount(), 
						financeMain.getFinStatus(), FinanceConstants.FINSTSRSN_MANUAL,true, false);
			}
		}
		
		if(!isRcdFound){
			ErrorDetails errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("60208", "", null), PennantConstants.default_Language);
			return errorDetail.getErrorMessage();
		}else{

			if(receiptHeader.getManualAdvise() != null){
				getManualAdviseDAO().save(receiptHeader.getManualAdvise(), TableType.MAIN_TAB);
			}
			
			// Update Receipt Details based on Receipt Mode 
			for (int i = 0; i < receiptHeader.getReceiptDetails().size(); i++) {
				FinReceiptDetail receiptDetail = receiptHeader.getReceiptDetails().get(i);
				if(!isBounceProcess || (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_CHEQUE) ||
						StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_DD))){
					getFinReceiptDetailDAO().updateReceiptStatus(receiptDetail.getReceiptID(), 
							receiptDetail.getReceiptSeqID(), receiptHeader.getReceiptModeStatus());
				}
			}
		}

		return null;
	}

	/**
	 * Sorting Repay Schedule Details
	 * 
	 * @param rpySchdList
	 * @return
	 */
	public List<RepayScheduleDetail> sortRpySchdDetails(List<RepayScheduleDetail> rpySchdList) {

		if (rpySchdList != null && rpySchdList.size() > 0) {
			Collections.sort(rpySchdList, new Comparator<RepayScheduleDetail>() {
				@Override
				public int compare(RepayScheduleDetail rpySchd1, RepayScheduleDetail rpySchd2) {
					return DateUtility.compare(rpySchd1.getSchDate(), rpySchd2.getSchDate());
				}
			});
		}

		return rpySchdList;
	}
	
	
	/**
	 * Method for Sorting Receipt Details From Receipts
	 * @param receipts
	 * @return
	 */
	private List<FinReceiptDetail> sortReceiptDetails(List<FinReceiptDetail> receipts){

		if (receipts != null && receipts.size() > 0) {
			Collections.sort(receipts, new Comparator<FinReceiptDetail>() {
				@Override
				public int compare(FinReceiptDetail detail1, FinReceiptDetail detail2) {
					if (detail1.getPayOrder() > detail2.getPayOrder()) {
						return 1;
					} else if(detail1.getPayOrder() < detail2.getPayOrder()) {
						return -1;
					} 
					return 0;
				}
			});
		}
		return receipts;
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
		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, false, logKey));
		finSchData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type, false, logKey));
		finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false, logKey));
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

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
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

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}
	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public RepaymentPostingsUtil getRepaymentPostingsUtil() {
		return repaymentPostingsUtil;
	}
	public void setRepaymentPostingsUtil(RepaymentPostingsUtil repaymentPostingsUtil) {
		this.repaymentPostingsUtil = repaymentPostingsUtil;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}
	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public FinExcessAmountDAO getFinExcessAmountDAO() {
		return finExcessAmountDAO;
	}
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public FinFeeScheduleDetailDAO getFinFeeScheduleDetailDAO() {
		return finFeeScheduleDetailDAO;
	}
	public void setFinFeeScheduleDetailDAO(FinFeeScheduleDetailDAO finFeeScheduleDetailDAO) {
		this.finFeeScheduleDetailDAO = finFeeScheduleDetailDAO;
	}

	public FinInsurancesDAO getFinInsurancesDAO() {
		return finInsurancesDAO;
	}
	public void setFinInsurancesDAO(FinInsurancesDAO finInsurancesDAO) {
		this.finInsurancesDAO = finInsurancesDAO;
	}

	public BounceReasonDAO getBounceReasonDAO() {
		return bounceReasonDAO;
	}

	public void setBounceReasonDAO(BounceReasonDAO bounceReasonDAO) {
		this.bounceReasonDAO = bounceReasonDAO;
	}

	public RuleDAO getRuleDAO() {
		return ruleDAO;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

}
