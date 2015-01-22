package com.pennant.backend.service.finance.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.OverDueRecoveryPostingsUtil;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.FinRepayQueue.FinRepayQueueDAO;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.commitment.CommitmentDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.finance.DefermentDetailDAO;
import com.pennant.backend.dao.finance.DefermentHeaderDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceRepayPriorityDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceRepayPriority;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.rits.cloning.Cloner;

public class ManualPaymentServiceImpl  extends GenericService<FinanceMain>  implements ManualPaymentService {

	private final static Logger logger = Logger.getLogger(ManualPaymentServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private FinanceRepayPriorityDAO financeRepayPriorityDAO;
	private FinanceRepaymentsDAO	 financeRepaymentsDAO;
	private FinRepayQueueDAO finRepayQueueDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private DefermentDetailDAO defermentDetailDAO;
	private DefermentHeaderDAO defermentHeaderDAO;
	private FinanceRepayPriority financeRepayPriority = null;
	private RepaymentPostingsUtil repayPostingUtil;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private FinanceTypeDAO financeTypeDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private RepayInstructionDAO repayInstructionDAO; 
	private TransactionEntryDAO transactionEntryDAO;
	private AccountingSetDAO accountingSetDAO;
	private CommitmentDAO commitmentDAO;
	private PostingsDAO postingsDAO;
	private FinLogEntryDetailDAO	 finLogEntryDetailDAO;
	private DocumentDetailsDAO documentDetailsDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private OverDueRecoveryPostingsUtil recoveryPostingsUtil;
	
	
	private String excludeFields = "calculateRepay,equalRepay,eventFromDate,eventToDate,increaseTerms,"
			+ "allowedDefRpyChange,availedDefRpyChange,allowedDefFrqChange,availedDefFrqChange,recalFromDate,recalToDate,excludeDeferedDates,"
			+ "financeScheduleDetails,disbDate, disbursementDetails,repayInstructions, rateChanges, defermentHeaders,addTermAfter,"
			+ "defermentDetails,scheduleMap,reqTerms,errorDetails,carLoanDetail,educationalLoan,homeLoanDetail,"
			+ "mortgageLoanDetail,proceedDedup,actionSave, finRvwRateApplFor,finGrcRvwRateApplFor,curDisbursementAmt,amount,"
			+ "exception,amountBD,amountUSD,maturity,availCommitAmount,guarantorIDTypeName,curFeeChargeAmt,"
			+ "name,lovCustCIFName,primaryExposure,secondaryExposure,guarantorExposure,worstStatus,status,sumPrimaryDetails,sumSecondaryDetails,"
			+ "sumGurantorDetails, isExtSource, commidityLoanDetails, limitStatus,fundsAvailConfirmed,pftIntact,adjTerms";
	
	/**
	 * Method for Fetching FInance Details & Repay Schedule Details
	 * @param finReference
	 * @return
	 */
	@Override
	public RepayData getRepayDataById(String finReference, String eventCode) {
		logger.debug("Entering");

		//Finance Details
		RepayData repayData = new RepayData();
		repayData.setFinReference(finReference);
		repayData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, "_View", false));

		if (repayData.getFinanceMain() != null) {

			//Finance Schedule Details
			repayData.setScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference,"_View", false));

			//Finance Repayments Instruction Details
			repayData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference,"_View", false));
			
			//Finance Type Details
			FinanceType financeType = getFinanceTypeDAO().getFinanceTypeByID(repayData.getFinanceMain().getFinType(), "_AView");
			repayData.setFinanceType(financeType);
			
			//Get Agreement Details
			if("EARLYSTL".equals(eventCode) && repayData.getFinanceMain().getLovDescAssetCodeName().
					equalsIgnoreCase(PennantConstants.CARLOAN)){
				repayData.setAggrementList(getFinanceReferenceDetailDAO().getAgreementListByCode(PennantConstants.EARLYSTL_AGGCODE));
			}
			
			if(!StringUtils.trimToEmpty(repayData.getFinanceMain().getRecordType()).equals("")){
				
				//Repay Header Details
				repayData.setFinRepayHeader(getFinanceRepaymentsDAO().getFinRepayHeader(finReference, "_Temp"));
				
				//Repay Schedule Details
				repayData.setRepayScheduleDetails(getFinanceRepaymentsDAO().getRpySchdList(finReference, "_Temp"));
				
				//Fee Rule Details
				repayData.setFeeRuleList(getPostingsDAO().getFeeChargesByFinRef(finReference,false, "_Temp"));
				
				//Finance Document Details
				repayData.setDocumentDetailList(getDocumentDetailsDAO().getDocumentDetailsByRef(finReference, "_Temp"));
				
			}else{
				
				//Repay Header Details
				repayData.setFinRepayHeader(null);
				
				//Repay Schedule Details
				repayData.setRepayScheduleDetails(null);
				
				if("EARLYSTL".equals(eventCode)){
					
					//Finance Fee Charge Details
					//=======================================
					repayData.setFeeCharges(getTransactionEntryDAO().getListFeeChargeRules(
							Long.valueOf(financeType.getFinAEEarlySettle()), "EARLYSTL", "_AView",0));
				}
				
			}
		}

		logger.debug("Leaving");
		return repayData;
	}
	
	/**
	 * Method for Fetching Accounting Entries
	 * @param financeDetail
	 * @return
	 */
	@Override
	public FinanceDetail getAccountingDetail(FinanceDetail financeDetail, String eventCodeRef){
		logger.debug("Entering");
		
		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();
		
		String accountSetId = "";
		if("EARLYSTL".equals(eventCodeRef)){
			accountSetId = financeType.getFinAEEarlySettle();
		}else{
			accountSetId = financeType.getFinAERepay();
		}
		
		financeDetail.setTransactionEntries(getTransactionEntryDAO().getListTransactionEntryById(Long.valueOf(accountSetId), "_AEView", true));
		
		String commitmentRef = financeDetail.getFinScheduleData().getFinanceMain().getFinCommitmentRef();
		
		if ((commitmentRef).equals("")) {
			
			Commitment commitment = getCommitmentDAO().getCommitmentById(commitmentRef, "");
			if(commitment != null && commitment.isRevolving()){
				long accountingSetId = getAccountingSetDAO().getAccountingSetId("CMTRPY","CMTRPY");
				if (accountingSetId != 0) {
					financeDetail.setCmtFinanceEntries(getTransactionEntryDAO().getListTransactionEntryById(accountingSetId, "_AEView", true));
				}
			}
		}
		
		logger.debug("Leaving");
		return financeDetail;
	}
	
	@Override
    public FinanceProfitDetail getPftDetailForEarlyStlReport(String finReference) {
	    return getFinanceProfitDetailDAO().getPftDetailForEarlyStlReport(finReference);
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
	@SuppressWarnings("unchecked")
    @Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		String tableType = "";
		RepayData repayData = (RepayData) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = repayData.getFinanceMain();
		FinRepayHeader finRepayHeader = repayData.getFinRepayHeader();
		String finReference = financeMain.getFinReference();
		Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
		
		String actualRepayAcc = financeMain.getRepayAccountId();

		if (financeMain.isWorkflow()) {
			tableType = "_TEMP";
		}
		financeMain.setRcdMaintainSts(finRepayHeader.getFinEvent());
		if(tableType.equals("")){
			financeMain.setRcdMaintainSts("");
		}
		
		//Repayments Postings Details Process Execution
		long linkedTranId = 0;
		boolean partialPay = false;
		boolean isRIAFinance = false;
		FinanceProfitDetail profitDetail = null;
		AEAmountCodes aeAmountCodes = null;
		String finAccount = null;
		
		List<FinRepayQueue> finRepayQueues = new ArrayList<FinRepayQueue>();
		FinScheduleData scheduleData = new FinScheduleData();
		scheduleData.setFinReference(finReference);
		scheduleData.setFinanceMain(financeMain);
		scheduleData.setFinanceScheduleDetails(repayData.getScheduleDetails());
		scheduleData.setRepayInstructions(repayData.getRepayInstructions());
		
		boolean emptyRepayInstructions = repayData.getRepayInstructions() == null ?  true : false;
		
		if(!financeMain.isWorkflow()){
			financeMain.setRepayAccountId(finRepayHeader.getRepayAccountId());
			
			isRIAFinance = getFinanceTypeDAO().checkRIAFinance(financeMain.getFinType());
			profitDetail = getFinanceProfitDetailDAO().getFinPftDetailForBatch(finReference);
			
			List<RepayScheduleDetail> repaySchdList = repayData.getRepayScheduleDetails();
			List<Object> returnList = processRepaymentPostings(financeMain, scheduleData.getFinanceScheduleDetails(), 
					profitDetail, repaySchdList, finRepayHeader.getInsRefund(), isRIAFinance, repayData.getEventCodeRef(), 
					repayData.getFeeRuleList(), repayData.getFinanceType().getFinDivision());

			if(!(Boolean) returnList.get(0)){
				String errParm = (String) returnList.get(1);
				throw new AccountNotFoundException(errParm);
			}
			
			linkedTranId = (Long) returnList.get(1);
			partialPay = (Boolean) returnList.get(2);
			aeAmountCodes = (AEAmountCodes) returnList.get(3);
			finAccount = (String) returnList.get(4);
			finRepayQueues = (List<FinRepayQueue>) returnList.get(5);
		}
		
		// Finance Main Details Save And Update
		//=======================================
		financeMain.setRepayAccountId(actualRepayAcc);
		if (financeMain.isNew()) {
			getFinanceMainDAO().save(financeMain, tableType, false);
			
			//Save FinRepayHeader Details
			finRepayHeader.setLinkedTranId(linkedTranId);
			getFinanceRepaymentsDAO().saveFinRepayHeader(finRepayHeader, tableType);
			
			//Save Repay Schedule Details
			getFinanceRepaymentsDAO().saveRpySchdList(repayData.getRepayScheduleDetails(), tableType);
			
		} else {
			getFinanceMainDAO().update(financeMain, tableType, false);
			
			//Save/Update FinRepayHeader Details depends on Workflow
			if(!tableType.equals("")){
				finRepayHeader.setLinkedTranId(linkedTranId);
				getFinanceRepaymentsDAO().updateFinRepayHeader(finRepayHeader, tableType);
				getFinanceRepaymentsDAO().deleteRpySchdList(finReference, tableType);
				getFinanceRepaymentsDAO().saveRpySchdList(repayData.getRepayScheduleDetails(), tableType);
			}
		}
		
		// Save Document Details
		if (repayData.getDocumentDetailList() != null && repayData.getDocumentDetailList().size() > 0) {
			List<AuditDetail> details = repayData.getAuditDetailMap().get("DocumentDetails");
			processingDocumentDetailsList(details, tableType, financeMain.getFinReference(),repayData.getFinanceMain());
		}
		
		// Save schedule details
		//=======================================
		if (!financeMain.isNewRecord()) {

			if(tableType.equals("") && financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
				//Fetch Existing data before Modification
				
				FinScheduleData oldFinSchdData = null;
				if(finRepayHeader.isSchdRegenerated()){
					oldFinSchdData = getFinSchDataByFinRef(finReference, "");
					oldFinSchdData.setFinanceMain(financeMain);
					oldFinSchdData.setFinReference(finReference);
				}

				//Create log entry for Action for Schedule Modification
				FinLogEntryDetail entryDetail = new FinLogEntryDetail();
				entryDetail.setFinReference(finReference);
				entryDetail.setEventAction(finRepayHeader.getFinEvent());
				entryDetail.setSchdlRecal(finRepayHeader.isSchdRegenerated());
				entryDetail.setPostDate(curBDay);
				entryDetail.setReversalCompleted(false);
				long logKey = getFinLogEntryDetailDAO().save(entryDetail);

				//Save Schedule Details For Future Modifications
				if(finRepayHeader.isSchdRegenerated()){
					listSave(oldFinSchdData, "_Log", logKey);
				}
			}

		}
		
		//Finance Schedule Details
		listDeletion(finReference, tableType, emptyRepayInstructions);
		listSave(scheduleData, tableType, 0);

		//Fee Charge Details Clearing before 
		if (!StringUtils.trimToEmpty(tableType).equals("")) {
			getPostingsDAO().deleteChargesBatch(finReference, false, tableType);
		}
		saveFeeChargeList(repayData, tableType);
		
		//Process Updations For Postings
		if(!financeMain.isWorkflow()){
			financeMain.setRepayAccountId(finRepayHeader.getRepayAccountId());
			
			getRepayPostingUtil().UpdateScreenPaymentsProcess(financeMain, scheduleData.getFinanceScheduleDetails(), profitDetail, 
					finRepayQueues, linkedTranId, partialPay, isRIAFinance, aeAmountCodes);
			
			getFinanceRepaymentsDAO().saveFinRepayHeader(finRepayHeader, tableType);

			//Update Linked Transaction ID after Repayments Postings Process if workflow not found
			for (RepayScheduleDetail rpySchd : repayData.getRepayScheduleDetails()) {
				rpySchd.setLinkedTranId(linkedTranId);
			}
			getFinanceRepaymentsDAO().saveRpySchdList(repayData.getRepayScheduleDetails(), tableType);
		}
		
		//Reset Repay Account ID On Finance Main for Correcting Audit Data
		financeMain.setRepayAccountId(actualRepayAcc);
		if(finAccount != null){
			financeMain.setFinAccount(finAccount);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
				fields[1], financeMain.getBefImage(), financeMain));

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}
	
	/**
	 * Method to delete schedule, disbursement, deferementheader, defermentdetail,repayinstruction, ratechanges lists.
	 * 
	 * @param finDetail
	 * @param tableType
	 * @param isWIF
	 */
	public void listDeletion(String finReference, String tableType, boolean emptyRepayInstructions) {
		logger.debug("Entering ");
		getFinanceScheduleDetailDAO().deleteByFinReference(finReference, tableType, false, 0);
		if(!emptyRepayInstructions){
			getRepayInstructionDAO().deleteByFinReference(finReference, tableType, false, 0);
		}
		logger.debug("Leaving ");
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

		RepayData repayData = (RepayData) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = repayData.getFinanceMain();

		// ScheduleDetails deletion
		listDeletion(financeMain.getFinReference(), "_Temp", false);
		getPostingsDAO().deleteChargesBatch(financeMain.getFinReference(), false, "_Temp");
		getFinanceMainDAO().delete(financeMain, "_Temp", false);
		
		//Delete Finance Repay Header
		getFinanceRepaymentsDAO().deleteFinRepayHeader(repayData.getFinRepayHeader(), "_Temp");
		getFinanceRepaymentsDAO().deleteRpySchdList(financeMain.getFinReference(), "_Temp");
		if (repayData.getDocumentDetailList() != null && repayData.getDocumentDetailList().size() > 0) {
			getDocumentDetailsDAO().deleteList(new ArrayList<DocumentDetails>(repayData.getDocumentDetailList()), "_Temp");
		}
		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain.getBefImage(), financeMain));
		getAuditHeaderDAO().addAudit(auditHeader);

		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(repayData);

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
	@SuppressWarnings("unchecked")
    @Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		String tranType = "";
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		
		RepayData repayData = (RepayData) auditHeader.getAuditDetail().getModelData();
		Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
		
		//Repayment Postings Details Process Execution
		long linkedTranId = 0;
		boolean partialPay = false;
		boolean isRIAFinance = false;
		FinanceProfitDetail profitDetail = null;
		List<FinRepayQueue> finRepayQueues = new ArrayList<FinRepayQueue>();

		//Execute Accounting Details Process
		//=======================================
		FinanceMain financeMain = repayData.getFinanceMain();
		String finReference = financeMain.getFinReference();
		String actualRepayAcc = financeMain.getRepayAccountId();
		FinRepayHeader finRepayHeader = repayData.getFinRepayHeader();
		AEAmountCodes aeAmountCodes = null;
		String finAccount = null;
		
		financeMain.setRcdMaintainSts("");
		financeMain.setRoleCode("");
		financeMain.setNextRoleCode("");
		financeMain.setTaskId("");
		financeMain.setNextTaskId("");
		financeMain.setWorkflowId(0);

		FinScheduleData scheduleData = new FinScheduleData();
		scheduleData.setFinReference(financeMain.getFinReference());
		scheduleData.setFinanceMain(financeMain);
		scheduleData.setFinanceScheduleDetails(repayData.getScheduleDetails());
		scheduleData.setRepayInstructions(repayData.getRepayInstructions());
		
		boolean emptyRepayInstructions = repayData.getRepayInstructions() == null ?  true : false;
		
		//Repayments Posting Process Execution
		//=====================================
		financeMain.setRepayAccountId(finRepayHeader.getRepayAccountId());
		isRIAFinance = getFinanceTypeDAO().checkRIAFinance(financeMain.getFinType());
		profitDetail = getFinanceProfitDetailDAO().getFinPftDetailForBatch(finReference);
		
		List<RepayScheduleDetail> repaySchdList = repayData.getRepayScheduleDetails();
		List<Object> returnList = processRepaymentPostings(financeMain, scheduleData.getFinanceScheduleDetails(), 
				profitDetail, repaySchdList, finRepayHeader.getInsRefund(), isRIAFinance, 
				repayData.getEventCodeRef(), repayData.getFeeRuleList(), repayData.getFinanceType().getFinDivision());

		if(!(Boolean) returnList.get(0)){
			String errParm = (String) returnList.get(1);
			throw new AccountNotFoundException(errParm);
		}
		
		linkedTranId = (Long) returnList.get(1);
		partialPay = (Boolean) returnList.get(2);
		aeAmountCodes = (AEAmountCodes) returnList.get(3);
		finAccount = (String) returnList.get(4);
		finRepayQueues = (List<FinRepayQueue>) returnList.get(5);

		tranType = PennantConstants.TRAN_UPD;
		financeMain.setRecordType("");

		FinScheduleData oldFinSchdData = null;
		if(finRepayHeader.isSchdRegenerated()){
			oldFinSchdData = getFinSchDataByFinRef(finReference, "");
			oldFinSchdData.setFinanceMain(financeMain);
			oldFinSchdData.setFinReference(finReference);
		}

		//Create log entry for Action for Schedule Modification
		FinLogEntryDetail entryDetail = new FinLogEntryDetail();
		entryDetail.setFinReference(finReference);
		entryDetail.setEventAction(finRepayHeader.getFinEvent());
		entryDetail.setSchdlRecal(finRepayHeader.isSchdRegenerated());
		entryDetail.setPostDate(curBDay);
		entryDetail.setReversalCompleted(false);
		long logKey = getFinLogEntryDetailDAO().save(entryDetail);

		//Save Schedule Details For Future Modifications
		if(finRepayHeader.isSchdRegenerated()){
			listSave(oldFinSchdData, "_Log", logKey);
		}

		//Repayment Postings Details Process
		returnList = getRepayPostingUtil().UpdateScreenPaymentsProcess(financeMain, scheduleData.getFinanceScheduleDetails(), profitDetail, finRepayQueues,
				linkedTranId, partialPay, isRIAFinance, aeAmountCodes);		
		
		//Finance Main Updation
		//=======================================
		financeMain = (FinanceMain) returnList.get(3);
		financeMain.setRepayAccountId(actualRepayAcc);
		if(finAccount != null){
			financeMain.setFinAccount(finAccount);
		}
		getFinanceMainDAO().update(financeMain, "", false);
		
		// ScheduleDetails delete and save
		//=======================================
		listDeletion(finReference, "", emptyRepayInstructions);
		scheduleData.setFinanceScheduleDetails((List<FinanceScheduleDetail>)returnList.get(4));
		listSave(scheduleData, "", 0);
		saveFeeChargeList(repayData, "");
		
		//Save Finance Repay Header Details
		finRepayHeader.setLinkedTranId(linkedTranId);
		getFinanceRepaymentsDAO().saveFinRepayHeader(finRepayHeader, "");
		
		//Update Linked Transaction ID after Repayment Postings Process if workflow not found
		for (RepayScheduleDetail rpySchd : repayData.getRepayScheduleDetails()) {
			rpySchd.setLinkedTranId(linkedTranId);
        }
		getFinanceRepaymentsDAO().saveRpySchdList(repayData.getRepayScheduleDetails(), "");
		
		// Save Document Details
		if (repayData.getDocumentDetailList() != null && repayData.getDocumentDetailList().size() > 0) {
			List<AuditDetail> details = repayData.getAuditDetailMap().get("DocumentDetails");
			processingDocumentDetailsList(details, "", financeMain.getFinReference(),repayData.getFinanceMain());
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);

		// ScheduleDetails delete
		//=======================================
		listDeletion(finReference, "_Temp", false);
		
		//Fee Charge Details Clearing before 
		//=======================================
		getPostingsDAO().deleteChargesBatch(finReference, false, "_Temp");
				
		//Delete Finance Repay Header
		getFinanceRepaymentsDAO().deleteFinRepayHeader(repayData.getFinRepayHeader(), "_Temp");
		getFinanceRepaymentsDAO().deleteRpySchdList(financeMain.getFinReference(), "_Temp");
		if (repayData.getDocumentDetailList() != null && repayData.getDocumentDetailList().size() > 0) {
			getDocumentDetailsDAO().deleteList(new ArrayList<DocumentDetails>(repayData.getDocumentDetailList()), "_Temp");
		}
		
		//Reset Repay Account ID On Finance Main for Correcting Audit Data
		getFinanceMainDAO().delete(financeMain, "_Temp", false);
		
		RepayData tempRepayData = (RepayData)  aAuditHeader.getAuditDetail().getModelData();
		FinanceMain tempfinanceMain = tempRepayData.getFinanceMain();
		financeMain.setRepayAccountId(actualRepayAcc);
		auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1], tempfinanceMain.getBefImage(), tempfinanceMain));

		// Adding audit as deleted from TEMP table
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
				fields[1], financeMain.getBefImage(), financeMain));

		// Adding audit as Insert/Update/deleted into main table
		getAuditHeaderDAO().addAudit(auditHeader);

		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(repayData);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for Repayment Details Posting Process
	 * @param financeMain
	 * @param scheduleDetails
	 * @param repaySchdList
	 * @param insRefund
	 * @return
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 * @throws InvocationTargetException 
	 */
	public List<Object> processRepaymentPostings(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail profitDetail,
			List<RepayScheduleDetail> repaySchdList, BigDecimal insRefund, boolean isRIAFinance, String eventCodeRef, List<FeeRule> feeRuleList,
			String finDivision) throws IllegalAccessException, AccountNotFoundException, InvocationTargetException {

		List<Object> returnList = new ArrayList<Object>();
		try {
			
			Map<String, FeeRule> feeRuleDetailsMap = null;
			if (feeRuleList != null && !feeRuleList.isEmpty()) {

				feeRuleDetailsMap = new HashMap<String, FeeRule>();
				for (FeeRule feeRule : feeRuleList) {
					if (!feeRuleDetailsMap.containsKey(feeRule.getFeeCode())) {
						feeRuleDetailsMap.put(feeRule.getFeeCode(), feeRule);
					}
				}
			}
			
			// FETCH Finance type Repayment Priority
			financeRepayPriority = getFinanceRepayPriorityDAO().getFinanceRepayPriorityById(financeMain.getFinType(),"");
			
			//Check Finance is RIA Finance Type or Not
			BigDecimal totRpyPri = BigDecimal.ZERO;
			BigDecimal totRpyPft = BigDecimal.ZERO;
			BigDecimal totRefund = BigDecimal.ZERO;
			
			List<FinRepayQueue> finRepayQueues = new ArrayList<FinRepayQueue>();
			Map<String,BigDecimal> totalsMap = new HashMap<String, BigDecimal>();
			FinRepayQueue finRepayQueue = null;
			Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");

			for(int i = 0; i < repaySchdList.size(); i++) {

				finRepayQueue = new FinRepayQueue();
				finRepayQueue.setFinReference(financeMain.getFinReference());
				finRepayQueue.setRpyDate(repaySchdList.get(i).getSchDate());
				finRepayQueue.setFinRpyFor(repaySchdList.get(i).getSchdFor());
				finRepayQueue.setRcdNotExist(true);
				finRepayQueue = doWriteDataToBean(finRepayQueue,financeMain,repaySchdList.get(i));

				//Overdue Details preparation
				getRecoveryPostingsUtil().recoveryProcess(financeMain, finRepayQueue, curBDay, 
						isRIAFinance, false, false, Long.MIN_VALUE, null, false);
				
				finRepayQueue.setRefundAmount(repaySchdList.get(i).getRefundReq());
				finRepayQueue.setPenaltyAmount(repaySchdList.get(i).getPenaltyAmt());
				finRepayQueue.setWaivedAmount(repaySchdList.get(i).getWaivedAmt());
				finRepayQueue.setChargeType(repaySchdList.get(i).getChargeType());
				
				//Total Repayments Calculation for Principal, Profit & Refunds
				totRpyPri = totRpyPri.add(repaySchdList.get(i).getPrincipalSchdPayNow());
				totRpyPft = totRpyPft.add(repaySchdList.get(i).getProfitSchdPayNow());
				totRefund = totRefund.add(repaySchdList.get(i).getRefundReq());
				
				finRepayQueues.add(finRepayQueue);

			}
			
			totalsMap.put("totRpyTot", totRpyPri.add(totRpyPft));
			totalsMap.put("totRpyPri", totRpyPri);
			totalsMap.put("totRpyPft", totRpyPft);
			totalsMap.put("totRefund", totRefund);
			//Schedule Early Settlement Insurance Refund
			totalsMap.put("INSREFUND", insRefund);
			
			//Repayments Process For Schedule Repay List			
			returnList = getRepayPostingUtil().postingsScreenRepayProcess(financeMain, scheduleDetails, 
					profitDetail, finRepayQueues, totalsMap,isRIAFinance,eventCodeRef, feeRuleDetailsMap, finDivision);
			
			if((Boolean) returnList.get(0)){
				returnList.add(finRepayQueues);
			}
			
		} catch (AccountNotFoundException e) {
			e.printStackTrace();
			throw e;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw e;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw e;
		}

		return returnList;
	}

	/**
	 * Method for prepare RepayQueue data
	 * 
	 * @param resultSet
	 * @return
	 */
	private FinRepayQueue doWriteDataToBean(FinRepayQueue finRepayQueue, FinanceMain financeMain, RepayScheduleDetail rsd) {
		logger.debug("Entering");

		finRepayQueue.setBranch(financeMain.getFinBranch());
		finRepayQueue.setFinType(financeMain.getFinType());
		finRepayQueue.setCustomerID(financeMain.getCustID());

		if(financeRepayPriority != null){
			finRepayQueue.setFinPriority(financeRepayPriority.getFinPriority());
		}else{
			finRepayQueue.setFinPriority(9999);
		}

		finRepayQueue.setSchdPft(rsd.getProfitSchd());
		finRepayQueue.setSchdPri(rsd.getPrincipalSchd());
		finRepayQueue.setSchdPftBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
		finRepayQueue.setSchdPriBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));
		finRepayQueue.setSchdPriPayNow(rsd.getPrincipalSchdPayNow());
		finRepayQueue.setSchdPftPayNow(rsd.getProfitSchdPayNow());
		finRepayQueue.setSchdPriPaid(rsd.getPrincipalSchdPaid());
		finRepayQueue.setSchdPftPaid(rsd.getProfitSchdPaid());

		logger.debug("Leaving");
		return finRepayQueue;
	}

	@Override
	public List<FinanceRepayments> getFinRepayListByFinRef(String finRef, boolean isRpyCancelProc,String type) {
		return getFinanceRepaymentsDAO().getFinRepayListByFinRef(finRef,isRpyCancelProc, type);
	}

	@Override
	public FinanceProfitDetail getFinProfitDetailsById(String finReference) {
		return getFinanceProfitDetailDAO().getFinProfitDetailsById(finReference);
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
		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, false));
		finSchData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type, false));
		finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference,type, false));
		finSchData.setDefermentHeaders(getDefermentHeaderDAO().getDefermentHeaders(finReference,type, false));
		finSchData.setDefermentDetails(getDefermentDetailDAO().getDefermentDetails(finReference,type, false));
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
		
		if(logKey != 0){
			// Finance Disbursement Details
			mapDateSeq = new HashMap<Date, Integer>();
			Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
			for (int i = 0; i < scheduleData.getDisbursementDetails().size(); i++) {
				scheduleData.getDisbursementDetails().get(i).setFinReference(scheduleData.getFinReference());
				scheduleData.getDisbursementDetails().get(i).setDisbReqDate(curBDay);
				int seqNo = 0;

				if (mapDateSeq.containsKey(scheduleData.getDisbursementDetails().get(i).getDisbDate())) {
					seqNo = mapDateSeq.get(scheduleData.getDisbursementDetails().get(i).getDisbDate());
					mapDateSeq.remove(scheduleData.getDisbursementDetails().get(i).getDisbDate());
				} 
				seqNo = seqNo + 1;

				mapDateSeq.put(scheduleData.getDisbursementDetails().get(i).getDisbDate(), seqNo);
				scheduleData.getDisbursementDetails().get(i).setDisbSeq(seqNo);
				scheduleData.getDisbursementDetails().get(i).setDisbIsActive(true);
				scheduleData.getDisbursementDetails().get(i).setDisbDisbursed(true);
				scheduleData.getDisbursementDetails().get(i).setLogKey(logKey);
			}
			getFinanceDisbursementDAO().saveList(scheduleData.getDisbursementDetails(), tableType, false);

			//Finance Defferment Header Details
			for (int i = 0; i < scheduleData.getDefermentHeaders().size(); i++) {
				scheduleData.getDefermentHeaders().get(i).setFinReference(scheduleData.getFinReference());
				scheduleData.getDefermentHeaders().get(i).setLogKey(logKey);
			}
			getDefermentHeaderDAO().saveList(scheduleData.getDefermentHeaders(), tableType, false);

			//Finance Defferment Details
			for (int i = 0; i < scheduleData.getDefermentDetails().size(); i++) {
				scheduleData.getDefermentDetails().get(i).setFinReference(scheduleData.getFinReference());
				scheduleData.getDefermentDetails().get(i).setLogKey(logKey);
			}
			getDefermentDetailDAO().saveList(scheduleData.getDefermentDetails(), tableType, false);
		}
		
		//Finance Repay Instruction Details
		if(scheduleData.getRepayInstructions() != null){
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
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		
		RepayData repayData = (RepayData) auditDetail.getModelData();
		String auditTranType = "";
		if (method.equals("saveOrUpdate") || method.equals("doApprove") || method.equals("doReject")) {
			if (repayData.getFinanceMain().isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		
		//Finance Document Details
		if (repayData.getDocumentDetailList() != null && repayData.getDocumentDetailList().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(repayData, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		repayData.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(repayData);
		auditHeader.setAuditDetails(auditDetails);
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
		RepayData repayData = (RepayData) auditDetail.getModelData();
		FinanceMain financeMain = repayData.getFinanceMain();

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
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
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
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinanceMain != null && !oldFinanceMain.getLastMntOn()
							.equals(befFinanceMain.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
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
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempFinanceMain != null && oldFinanceMain != null
						&& !oldFinanceMain.getLastMntOn().equals(tempFinanceMain.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		//Checking For Commitment , Is it In Maintenance Or not
		if(StringUtils.trimToEmpty(financeMain.getRecordType()).equals(PennantConstants.RECORD_TYPE_NEW) && method.equals("doApprove")
				&& !financeMain.getFinCommitmentRef().equals("")){

			Commitment tempcommitment = getCommitmentDAO().getCommitmentById(financeMain.getFinCommitmentRef(), "_Temp");
			if(tempcommitment != null && tempcommitment.isRevolving()){
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
						PennantConstants.KEY_FIELD, "E0041", errParm, valueParm), usrLanguage));
			}
		}
				
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !financeMain.isWorkflow()) {
			financeMain.setBefImage(befFinanceMain);
		}

		return auditDetail;
	}
	
	/**
	 * Method for saving List of Fee Charge details
	 * 
	 * @param finDetail
	 * @param tableType
	 */
	public void saveFeeChargeList(RepayData repayData, String tableType) {
		logger.debug("Entering");

		if (repayData.getFeeRuleList() != null && repayData.getFeeRuleList().size() > 0) {
			//Finance Fee Charge Details
			for (int i = 0; i < repayData.getFeeRuleList().size(); i++) {
				repayData.getFeeRuleList().get(i).setFinReference(repayData.getFinanceMain().getFinReference());
			}
			getPostingsDAO().saveChargesBatch(repayData.getFeeRuleList(),false, tableType);
		}

		logger.debug("Leaving");
	}
	
	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param detail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setDocumentDetailsAuditData(RepayData detail,
			String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();		
		DocumentDetails object = new DocumentDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < detail.getDocumentDetailList().size(); i++) {
			DocumentDetails documentDetails = detail.getDocumentDetailList().get(i);
			documentDetails.setWorkflowId(detail.getFinanceMain().getWorkflowId());
			boolean isRcdType = false;

			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				documentDetails.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			documentDetails.setRecordStatus(detail.getFinanceMain().getRecordStatus());
			documentDetails.setUserDetails(detail.getFinanceMain().getUserDetails());
			documentDetails.setLastMntOn(detail.getFinanceMain().getLastMntOn());

			if (!documentDetails.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						documentDetails.getBefImage(), documentDetails));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}
	
	/**
	 * Method For Preparing List of AuditDetails for Contributor Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	public List<AuditDetail> processingDocumentDetailsList(List<AuditDetail> auditDetails,
			String type, String finReference, FinanceMain financeMain) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			DocumentDetails documentDetails = (DocumentDetails) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			
			if (type.equals("")) {
				approveRec = true;
				documentDetails.setRoleCode("");
				documentDetails.setNextRoleCode("");
				documentDetails.setTaskId("");
				documentDetails.setNextTaskId("");
			}
			
			documentDetails.setReferenceId(finReference);
			documentDetails.setLastMntBy(financeMain.getLastMntBy());
			documentDetails.setWorkflowId(0);

			if(documentDetails.isDocIsCustDoc()){
				approveRec = true;
			}

			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (documentDetails.isNewRecord()) {
				saveRecord = true;
				if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (documentDetails.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = documentDetails.getRecordType();
				recordStatus = documentDetails.getRecordStatus();
				documentDetails.setRecordType("");
				documentDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getDocumentDetailsDAO().save(documentDetails, type);
			}

			if (updateRecord) {
				getDocumentDetailsDAO().update(documentDetails, type);
			}

			if (deleteRecord) {
				getDocumentDetailsDAO().delete(documentDetails, type);
			}

			if (approveRec) {
				documentDetails.setRecordType(rcdType);
				documentDetails.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(documentDetails);
		}
		logger.debug("Leaving");
		return auditDetails;

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
	    return auditHeaderDAO;
    }
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
	    this.auditHeaderDAO = auditHeaderDAO;
    }
	
	public FinanceRepayPriorityDAO getFinanceRepayPriorityDAO() {
		return financeRepayPriorityDAO;
	}
	public void setFinanceRepayPriorityDAO(FinanceRepayPriorityDAO financeRepayPriorityDAO) {
		this.financeRepayPriorityDAO = financeRepayPriorityDAO;
	}

	public RepaymentPostingsUtil getRepayPostingUtil() {
		return repayPostingUtil;
	}
	public void setRepayPostingUtil(RepaymentPostingsUtil repayPostingUtil) {
		this.repayPostingUtil = repayPostingUtil;
	}

	public FinRepayQueueDAO getFinRepayQueueDAO() {
		return finRepayQueueDAO;
	}
	public void setFinRepayQueueDAO(FinRepayQueueDAO finRepayQueueDAO) {
		this.finRepayQueueDAO = finRepayQueueDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}
	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
	    this.financeProfitDetailDAO = financeProfitDetailDAO;
    }
	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
	    return financeProfitDetailDAO;
    }

	public FinanceTypeDAO getFinanceTypeDAO() {
    	return financeTypeDAO;
    }
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
    	this.financeTypeDAO = financeTypeDAO;
    }

	public FinanceMainDAO getFinanceMainDAO() {
    	return financeMainDAO;
    }
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
    	this.financeMainDAO = financeMainDAO;
    }

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
    	return financeScheduleDetailDAO;
    }
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
    	this.financeScheduleDetailDAO = financeScheduleDetailDAO;
    }

	public RepayInstructionDAO getRepayInstructionDAO() {
    	return repayInstructionDAO;
    }
	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
    	this.repayInstructionDAO = repayInstructionDAO;
    }
	
	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}
	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public DefermentDetailDAO getDefermentDetailDAO() {
		return defermentDetailDAO;
	}
	public void setDefermentDetailDAO(DefermentDetailDAO defermentDetailDAO) {
		this.defermentDetailDAO = defermentDetailDAO;
	}

	public DefermentHeaderDAO getDefermentHeaderDAO() {
		return defermentHeaderDAO;
	}
	public void setDefermentHeaderDAO(DefermentHeaderDAO defermentHeaderDAO) {
		this.defermentHeaderDAO = defermentHeaderDAO;
	}

	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
	    this.finLogEntryDetailDAO = finLogEntryDetailDAO;
    }
	public FinLogEntryDetailDAO getFinLogEntryDetailDAO() {
	    return finLogEntryDetailDAO;
    }

	public TransactionEntryDAO getTransactionEntryDAO() {
	    return transactionEntryDAO;
    }
	public void setTransactionEntryDAO(TransactionEntryDAO transactionEntryDAO) {
	    this.transactionEntryDAO = transactionEntryDAO;
    }

	public AccountingSetDAO getAccountingSetDAO() {
	    return accountingSetDAO;
    }
	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
	    this.accountingSetDAO = accountingSetDAO;
    }

	public CommitmentDAO getCommitmentDAO() {
	    return commitmentDAO;
    }
	public void setCommitmentDAO(CommitmentDAO commitmentDAO) {
	    this.commitmentDAO = commitmentDAO;
    }

	public PostingsDAO getPostingsDAO() {
	    return postingsDAO;
    }
	public void setPostingsDAO(PostingsDAO postingsDAO) {
	    this.postingsDAO = postingsDAO;
    }

	public DocumentDetailsDAO getDocumentDetailsDAO() {
	    return documentDetailsDAO;
    }
	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
	    this.documentDetailsDAO = documentDetailsDAO;
    }

	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
    	return financeReferenceDetailDAO;
    }
	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
    	this.financeReferenceDetailDAO = financeReferenceDetailDAO;
    }
	
	public OverDueRecoveryPostingsUtil getRecoveryPostingsUtil() {
	    return recoveryPostingsUtil;
    }
	public void setRecoveryPostingsUtil(OverDueRecoveryPostingsUtil recoveryPostingsUtil) {
	    this.recoveryPostingsUtil = recoveryPostingsUtil;
    }


}
