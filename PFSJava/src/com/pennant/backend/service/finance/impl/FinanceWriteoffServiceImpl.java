package com.pennant.backend.service.finance.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FinanceProfitDetailFiller;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.DefermentDetailDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceWriteoff;
import com.pennant.backend.model.finance.FinanceWriteoffHeader;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinanceWriteoffService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.rits.cloning.Cloner;

public class FinanceWriteoffServiceImpl  extends GenericService<FinanceMain>  implements FinanceWriteoffService {

	private final static Logger logger = Logger.getLogger(FinanceWriteoffServiceImpl.class);
	
	private AuditHeaderDAO 				auditHeaderDAO;
	
	private FinanceWriteoffDAO			financeWriteoffDAO;
	private FinanceRepaymentsDAO	 	financeRepaymentsDAO;
	private OverdueChargeRecoveryDAO 	recoveryDAO;
	private PostingsDAO 				postingsDAO;
	private ProvisionDAO				provisionDAO;
	private DefermentDetailDAO 			defermentDetailDAO;
	private FinanceProfitDetailDAO 		financeProfitDetailDAO;
	private FinanceTypeDAO 				financeTypeDAO;
	private FinanceMainDAO 				financeMainDAO;
	private FinanceScheduleDetailDAO 	financeScheduleDetailDAO;
	private FinanceDisbursementDAO 		financeDisbursementDAO;
	private PostingsPreparationUtil 	postingsPreparationUtil;
	private FinLogEntryDetailDAO	 	finLogEntryDetailDAO;
	private FinanceProfitDetailFiller   financeProfitDetailFiller;
	
	
	private String excludeFields = "calculateRepay,equalRepay,eventFromDate,eventToDate,increaseTerms,"
			+ "allowedDefRpyChange,availedDefRpyChange,allowedDefFrqChange,availedDefFrqChange,recalFromDate,recalToDate,excludeDeferedDates,"
			+ "financeScheduleDetails,disbDate, disbursementDetails,repayInstructions, rateChanges, defermentHeaders,addTermAfter,"
			+ "defermentDetails,scheduleMap,reqTerms,errorDetails,carLoanDetail,educationalLoan,homeLoanDetail,"
			+ "mortgageLoanDetail,proceedDedup,actionSave, finRvwRateApplFor,finGrcRvwRateApplFor,curDisbursementAmt,amount,"
			+ "exception,amountBD,amountUSD,maturity,availCommitAmount,guarantorIDTypeName,curFeeChargeAmt,"
			+ "name,lovCustCIFName,primaryExposure,secondaryExposure,guarantorExposure,worstStatus,status,sumPrimaryDetails,sumSecondaryDetails,"
			+ "sumGurantorDetails, isExtSource, commidityLoanDetails, limitStatus,fundsAvailConfirmed";
	
	/**
	 * Method for Fetching FInance Details & Repay Schedule Details
	 * @param finReference
	 * @return
	 */
	@Override
	public FinanceWriteoffHeader getFinanceWriteoffDetailById(String finReference, String type) {
		logger.debug("Entering");

		//Finance Details
		FinanceWriteoffHeader writeoffHeader = new FinanceWriteoffHeader();
		writeoffHeader.setFinReference(finReference);
		writeoffHeader.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference,type, false));

		if (writeoffHeader.getFinanceMain() != null) {

			//Finance Schedule Details
			writeoffHeader.setScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, false));
			writeoffHeader.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, "", false));
			writeoffHeader.setDefermentDetails(getDefermentDetailDAO().getDefermentDetails(finReference,"", false));

			writeoffHeader.setFeeRules(getPostingsDAO().getFeeChargesByFinRef(finReference,false, ""));
			writeoffHeader.setFinanceRepayments(getFinanceRepaymentsDAO().getFinRepayListByFinRef(finReference, false, ""));
			writeoffHeader.setPenaltyDetails(getRecoveryDAO().getFinancePenaltysByFinRef(finReference, ""));
			
			writeoffHeader.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(writeoffHeader.getFinanceMain().getFinType(), "_AView"));

			if(!StringUtils.trimToEmpty(writeoffHeader.getFinanceMain().getRecordType()).equals("")){
				
				//Finance Writeoff Details
				writeoffHeader.setFinanceWriteoff(getFinanceWriteoffDAO().getFinanceWriteoffById(finReference, "_Temp"));
				
			}else{
				
				writeoffHeader.getFinanceMain().setNewRecord(true);
				
				//Finance Writeoff Details
				FinanceWriteoff financeWriteoff = getFinanceScheduleDetailDAO().getWriteoffTotals(finReference);
				FinanceProfitDetail detail = getFinanceProfitDetailDAO().getProfitDetailForWriteOff(finReference);
				financeWriteoff.setCurODPri(detail.getODPrincipal());
				financeWriteoff.setCurODPft(detail.getODProfit());
				financeWriteoff.setPenaltyAmount(detail.getPenaltyDue());
				Provision provision = getProvisionDAO().getProvisionById(finReference, "");
				if(provision != null){
					financeWriteoff.setProvisionedAmount(provision.getProvisionedAmt());
				}
				
				writeoffHeader.setFinanceWriteoff(financeWriteoff);
			}
		}

		logger.debug("Leaving");
		return writeoffHeader;
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
		FinanceWriteoffHeader header = (FinanceWriteoffHeader) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = header.getFinanceMain();
		
		String finReference = financeMain.getFinReference();
		Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");

		if (financeMain.isWorkflow()) {
			tableType = "_TEMP";
		}
		financeMain.setRcdMaintainSts(PennantConstants.WRITEOFF);
		if(tableType.equals("")){
			financeMain.setRcdMaintainSts("");
		}
		
		//Repayments Postings Details Process Execution
		long linkedTranId = 0;
		boolean isRIAFinance = false;
		FinanceProfitDetail profitDetail = null;
		
		FinScheduleData scheduleData = new FinScheduleData();
		scheduleData.setFinReference(finReference);
		scheduleData.setFinanceMain(financeMain);
		scheduleData.setFinanceScheduleDetails(header.getScheduleDetails());
		
		if(!financeMain.isWorkflow()){
			
			isRIAFinance = getFinanceTypeDAO().checkRIAFinance(financeMain.getFinType());
			profitDetail = getFinanceProfitDetailDAO().getFinPftDetailForBatch(finReference);
			
			AEAmountCodes amountCodes = null;
			DataSet dataSet = AEAmounts.createDataSet(financeMain, "WRITEOFF", curBDay, financeMain.getMaturityDate());		
			amountCodes = AEAmounts.procAEAmounts(financeMain, header.getScheduleDetails(),profitDetail, curBDay);
			
			List<Object> returnList = getPostingsPreparationUtil().processPostingDetails(dataSet, amountCodes, false,
					isRIAFinance, "Y", curBDay, false, Long.MIN_VALUE);

			if(!(Boolean) returnList.get(0)){
				String errParm = (String) returnList.get(3);
				throw new AccountNotFoundException(errParm);
			}
			
			linkedTranId = (Long) returnList.get(1);
		}
		
		//Linked Transaction Id Updation
		FinanceWriteoff financeWriteoff = header.getFinanceWriteoff();
		financeWriteoff.setLinkedTranId(linkedTranId);
		
		// Finance Main Details Save And Update
		//=======================================
		if (financeMain.isNew()) {
			
			getFinanceMainDAO().save(financeMain, tableType, false);
			
			//Save Finance Writeoff Details
			int seqNo = getFinanceWriteoffDAO().getMaxFinanceWriteoffSeq(finReference, financeWriteoff.getWriteoffDate(), "");
			financeWriteoff.setSeqNo(seqNo+1);
			getFinanceWriteoffDAO().save(financeWriteoff, tableType);
			
		} else {
			getFinanceMainDAO().update(financeMain, tableType, false);
			
			//Update Writeoff Details depends on Workflow
			getFinanceWriteoffDAO().update(financeWriteoff, tableType);
		}
		
		// Save schedule details
		//=======================================
		if (!financeMain.isNewRecord()) {

			/*if(tableType.equals("") && financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
				//Fetch Existing data before Modification
				
				FinScheduleData old_finSchdData = null;
				if(finRepayHeader.isSchdRegenerated()){
					old_finSchdData = getFinSchDataByFinRef(finReference, "");
					old_finSchdData.setFinanceMain(financeMain);
					old_finSchdData.setFinReference(finReference);
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
					listSave(old_finSchdData, "_Log", logKey);
				}
			}*/

			listDeletion(finReference, tableType);
			listSave(scheduleData, tableType, 0);
		} else {
			listDeletion(finReference, tableType);
			listSave(scheduleData, tableType, 0);
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
	 * Method to delete schedule lists.
	 * 
	 * @param finDetail
	 * @param tableType
	 * @param isWIF
	 */
	public void listDeletion(String finReference, String tableType) {
		getFinanceScheduleDetailDAO().deleteByFinReference(finReference, tableType, false, 0);
	}
	
	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(String finReference){
		return getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, "", false);
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

		FinanceWriteoffHeader header = (FinanceWriteoffHeader) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = header.getFinanceMain();
		
		//Finance Writeoff Details
		getFinanceWriteoffDAO().delete(financeMain.getFinReference(), "_Temp");

		// ScheduleDetails deletion
		listDeletion(financeMain.getFinReference(), "_Temp");
		getFinanceMainDAO().delete(financeMain, "_Temp", false);
		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain.getBefImage(), financeMain));
		getAuditHeaderDAO().addAudit(auditHeader);

		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(header);

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
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		String tranType = "";
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		
		FinanceWriteoffHeader header = (FinanceWriteoffHeader) auditHeader.getAuditDetail().getModelData();
		Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
		
		//Execute Accounting Details Process
		//=======================================
		FinanceMain financeMain = header.getFinanceMain();
		String finReference = financeMain.getFinReference();
		
		financeMain.setRcdMaintainSts("");
		financeMain.setRoleCode("");
		financeMain.setNextRoleCode("");
		financeMain.setTaskId("");
		financeMain.setNextTaskId("");
		financeMain.setWorkflowId(0);

		FinScheduleData scheduleData = new FinScheduleData();
		scheduleData.setFinReference(financeMain.getFinReference());
		scheduleData.setFinanceMain(financeMain);
		scheduleData.setFinanceScheduleDetails(header.getScheduleDetails());
		
		//Finance Writeoff Posting Process Execution
		//=====================================
		boolean isRIAFinance = getFinanceTypeDAO().checkRIAFinance(financeMain.getFinType());
		FinanceProfitDetail profitDetail = getFinanceProfitDetailDAO().getFinPftDetailForBatch(finReference);
		
		AEAmountCodes amountCodes = null;
		DataSet dataSet = AEAmounts.createDataSet(financeMain, "WRITEOFF", curBDay, financeMain.getMaturityDate());		
		amountCodes = AEAmounts.procAEAmounts(financeMain, header.getScheduleDetails(),profitDetail, curBDay);
		
		List<Object> returnList = getPostingsPreparationUtil().processPostingDetails(dataSet, amountCodes, false,
				isRIAFinance, "Y", curBDay, false, Long.MIN_VALUE);

		if(!(Boolean) returnList.get(0)){
			String errParm = (String) returnList.get(3);
			throw new AccountNotFoundException(errParm);
		}
		
		long linkedTranId = (Long) returnList.get(1);
		
		tranType = PennantConstants.TRAN_UPD;
		financeMain.setRecordType("");
		getFinanceMainDAO().update(financeMain, "", false);
		
		//Save Finance WriteOff Details
		FinanceWriteoff financeWriteoff = header.getFinanceWriteoff();
		financeWriteoff.setLinkedTranId(linkedTranId);
		getFinanceWriteoffDAO().save(financeWriteoff, "");

		/*FinScheduleData old_finSchdData = null;
		if(finRepayHeader.isSchdRegenerated()){
			old_finSchdData = getFinSchDataByFinRef(finReference, "");
			old_finSchdData.setFinanceMain(financeMain);
			old_finSchdData.setFinReference(finReference);
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
			listSave(old_finSchdData, "_Log", logKey);
		}*/

		// ScheduleDetails delete and save
		//=======================================
		listDeletion(finReference, "");
		listSave(scheduleData, "", 0);

		//Update Profit Details 
		profitDetail = getFinanceProfitDetailFiller().prepareFinPftDetails(amountCodes, profitDetail, curBDay);
		getFinanceProfitDetailDAO().update(profitDetail, false);
		
		// ScheduleDetails delete
		//=======================================
		listDeletion(finReference, "_Temp");
		
		//Finance Writeoff Details
		getFinanceWriteoffDAO().delete(financeMain.getFinReference(), "_Temp");

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);
		getFinanceMainDAO().delete(financeMain, "_Temp", false);
		auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain.getBefImage(), financeMain));

		// Adding audit as deleted from TEMP table
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],fields[1], financeMain.getBefImage(), financeMain));

		// Adding audit as Insert/Update/deleted into main table
		getAuditHeaderDAO().addAudit(auditHeader);

		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(header);

		logger.debug("Leaving");
		return auditHeader;
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
			
			/*// Finance Disbursement Details
			mapDateSeq = new HashMap<Date, Integer>();
			Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
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

			//Finance Repay Instruction Details
			if(scheduleData.getRepayInstructions() != null){
				for (int i = 0; i < scheduleData.getRepayInstructions().size(); i++) {
					RepayInstruction curSchd = scheduleData.getRepayInstructions().get(i);

					curSchd.setFinReference(scheduleData.getFinReference());
					curSchd.setLogKey(logKey);
				}
				getRepayInstructionDAO().saveList(scheduleData.getRepayInstructions(), tableType, false);
			}*/
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
		FinanceWriteoffHeader header = (FinanceWriteoffHeader) auditDetail.getModelData();
		FinanceMain financeMain = header.getFinanceMain();

		FinanceMain tempFinanceMain = null;
		if (financeMain.isWorkflow()) {
			tempFinanceMain = getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "_Temp", false);
		}
		FinanceMain befFinanceMain = getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "", false);
		FinanceMain old_FinanceMain = financeMain.getBefImage();

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
					if (old_FinanceMain != null && !old_FinanceMain.getLastMntOn()
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

				if (tempFinanceMain != null && old_FinanceMain != null
						&& !old_FinanceMain.getLastMntOn().equals(tempFinanceMain.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !financeMain.isWorkflow()) {
			auditDetail.setBefImage(befFinanceMain);
		}

		return auditDetail;
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

	public OverdueChargeRecoveryDAO getRecoveryDAO() {
	    return recoveryDAO;
    }
	public void setRecoveryDAO(OverdueChargeRecoveryDAO recoveryDAO) {
	    this.recoveryDAO = recoveryDAO;
    }

	public PostingsPreparationUtil getPostingsPreparationUtil() {
	    return postingsPreparationUtil;
    }
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
	    this.postingsPreparationUtil = postingsPreparationUtil;
    }

	public ProvisionDAO getProvisionDAO() {
	    return provisionDAO;
    }
	public void setProvisionDAO(ProvisionDAO provisionDAO) {
	    this.provisionDAO = provisionDAO;
    }

	public FinanceWriteoffDAO getFinanceWriteoffDAO() {
	    return financeWriteoffDAO;
    }
	public void setFinanceWriteoffDAO(FinanceWriteoffDAO financeWriteoffDAO) {
	    this.financeWriteoffDAO = financeWriteoffDAO;
    }
	public FinanceProfitDetailFiller getFinanceProfitDetailFiller() {
    	return financeProfitDetailFiller;
    }
	public void setFinanceProfitDetailFiller(FinanceProfitDetailFiller financeProfitDetailFiller) {
    	this.financeProfitDetailFiller = financeProfitDetailFiller;
    }
	
}
