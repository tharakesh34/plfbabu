package com.pennant.backend.service.finance.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinanceMaintenanceService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.rits.cloning.Cloner;

public class FinanceMaintenanceServiceImpl  extends GenericService<FinanceMain>  implements FinanceMaintenanceService {

	private final static Logger logger = Logger.getLogger(FinanceMaintenanceServiceImpl.class);
	
	private AuditHeaderDAO 				auditHeaderDAO;
	
	private FinanceMainDAO 				financeMainDAO;
	private FinanceTypeDAO 				financeTypeDAO;
	private FinODPenaltyRateDAO			finODPenaltyRateDAO;
	private DocumentDetailsDAO          documentDetailsDAO;
	private FinanceProfitDetailDAO		financeProfitDetailDAO;


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
	public FinanceDetail getFinanceDetailById(String finReference, String type) {
		logger.debug("Entering");

		//Finance Details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);
		scheduleData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, false));
		scheduleData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(scheduleData.getFinanceMain().getFinType(), "_AView"));

		//Finance Overdue Penalty Rate Details
		scheduleData.setFinODPenaltyRate(getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(finReference, type));
		financeDetail.setDocumentDetailsList(getDocumentDetailsDAO().getDocumentDetailsByRef(finReference, "_View"));
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
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws AccountNotFoundException {
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
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		
		if (financeMain.isWorkflow()) {
			tableType = "_TEMP";
		}
		financeMain.setRcdMaintainSts(PennantConstants.MNT_BASIC_DETAIL);
		if(tableType.equals("")){
			financeMain.setRcdMaintainSts("");
		}
		
		//Finance Penalty OD Rate Details
		FinODPenaltyRate penaltyRate = financeDetail.getFinScheduleData().getFinODPenaltyRate();
		if (penaltyRate == null) { 
			
			penaltyRate = new FinODPenaltyRate();
			penaltyRate.setApplyODPenalty(false);
			penaltyRate.setODIncGrcDays(false);
			penaltyRate.setODChargeType("");
			penaltyRate.setODChargeAmtOrPerc(BigDecimal.ZERO);
			penaltyRate.setODChargeCalOn("");
			penaltyRate.setODGraceDays(0);
			penaltyRate.setODAllowWaiver(false);
			penaltyRate.setODMaxWaiverPerc(BigDecimal.ZERO);
		}
		penaltyRate.setFinReference(financeMain.getFinReference());
		penaltyRate.setFinEffectDate(DateUtility.getUtilDate());
		
		// Finance Main Details Save And Update
		//=======================================
		if (financeMain.isNew()) {
			getFinanceMainDAO().save(financeMain, tableType, false);
			
			//Finance Penalty OD Rate Details
			if (penaltyRate != null) { 
				getFinODPenaltyRateDAO().save(penaltyRate, tableType);
			}
			
		} else {
			getFinanceMainDAO().update(financeMain, tableType, false);
			
			//Finance Penalty OD Rate Details
			if (penaltyRate != null) { 
				if(tableType.equals("")){
					getFinODPenaltyRateDAO().save(penaltyRate, tableType);
					getFinODPenaltyRateDAO().delete(financeMain.getFinReference(), "_Temp");
				}else{
					getFinODPenaltyRateDAO().update(penaltyRate, tableType);
				}
			}
			
		}
		
		// Save Document Details
		if (financeDetail.getDocumentDetailsList() != null
				&& financeDetail.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, tableType,
					financeMain.getFinReference(),financeDetail.getFinScheduleData().getFinanceMain());
			auditDetails.addAll(details);
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
		getFinODPenaltyRateDAO().delete(financeMain.getFinReference(), "_Temp");
		getFinanceMainDAO().delete(financeMain, "_Temp", false);
		
		// Save Document Details
		if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
			for(DocumentDetails docDetails : financeDetail.getDocumentDetailsList()){
				docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details,  "_Temp",
					financeMain.getFinReference(),financeDetail.getFinScheduleData().getFinanceMain());
			auditHeader.setAuditDetails(details);
			listDocDeletion(financeDetail, "_Temp");
		}
		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain.getBefImage(), financeMain));
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
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws AccountNotFoundException {
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
		
		financeMain.setRcdMaintainSts("");
		financeMain.setRoleCode("");
		financeMain.setNextRoleCode("");
		financeMain.setTaskId("");
		financeMain.setNextTaskId("");
		financeMain.setWorkflowId(0);

		tranType = PennantConstants.TRAN_UPD;
		financeMain.setRecordType("");
		getFinanceMainDAO().update(financeMain, "", false);
		
		//Finance Penalty OD Rate Details
		FinODPenaltyRate penaltyRate = financeDetail.getFinScheduleData().getFinODPenaltyRate();
		if (penaltyRate == null) { 
			penaltyRate = new FinODPenaltyRate();
			penaltyRate.setApplyODPenalty(false);
			penaltyRate.setODIncGrcDays(false);
			penaltyRate.setODChargeType("");
			penaltyRate.setODChargeAmtOrPerc(BigDecimal.ZERO);
			penaltyRate.setODChargeCalOn("");
			penaltyRate.setODGraceDays(0);
			penaltyRate.setODAllowWaiver(false);
			penaltyRate.setODMaxWaiverPerc(BigDecimal.ZERO);
		}
		penaltyRate.setFinReference(financeMain.getFinReference());
		penaltyRate.setFinEffectDate(DateUtility.getUtilDate());
		getFinODPenaltyRateDAO().save(penaltyRate, "");
		
		// Save Document Details
		if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "",
					financeMain.getFinReference(),financeDetail.getFinScheduleData().getFinanceMain());
			auditHeader.setAuditDetails(details);
		}
		
		//Finance Profit Details Updation - Repayment Account
		getFinanceProfitDetailDAO().updateRpyAccount(financeMain.getFinReference(), financeMain.getRepayAccountId());

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);
		getFinODPenaltyRateDAO().delete(financeMain.getFinReference(), "_Temp");
		getFinanceMainDAO().delete(financeMain, "_Temp", false);
		listDocDeletion(financeDetail, "_Temp");
		
		FinanceDetail tempfinanceDetail = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
		FinanceMain tempfinanceMain = tempfinanceDetail.getFinScheduleData().getFinanceMain();
		auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1], tempfinanceMain.getBefImage(), tempfinanceMain));
		
		// Adding audit as deleted from TEMP table
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],fields[1], financeMain.getBefImage(), financeMain));

		// Adding audit as Insert/Update/deleted into main table
		getAuditHeaderDAO().addAudit(auditHeader);

		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(financeDetail);

		logger.debug("Leaving");
		return auditHeader;
	}
    
    //Document Details List Maintenance
  	public void listDocDeletion(FinanceDetail custDetails, String tableType) {
  		getDocumentDetailsDAO().deleteList(
  				new ArrayList<DocumentDetails>(custDetails.getDocumentDetailsList()), tableType);
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
		auditHeader = getAuditDetails(auditHeader, method);
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
			financeMain.setBefImage(befFinanceMain);
		}

		return auditDetail;
	}

	public List<AuditDetail> processingDocumentDetailsList(List<AuditDetail> auditDetails,
			String type, String finReference, FinanceMain financeMain) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			DocumentDetails documentDetails = (DocumentDetails) auditDetails.get(i).getModelData();
			if(!documentDetails.isDocIsCustDoc()){
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
					} else if (documentDetails.getRecordType().equalsIgnoreCase(
							PennantConstants.RCD_DEL)) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (documentDetails.getRecordType().equalsIgnoreCase(
							PennantConstants.RCD_UPD)) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (documentDetails.getRecordType().equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (documentDetails.getRecordType().equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (documentDetails.getRecordType().equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)) {
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
					if (StringUtils.trimToEmpty(documentDetails.getReferenceId()).equals("")) {
						documentDetails.setReferenceId(finReference);
					}
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
		}
		logger.debug("Leaving");
		return auditDetails;

	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String auditTranType = "";
		if (method.equals("saveOrUpdate") || method.equals("doApprove") || method.equals("doReject")) {
			if (financeMain.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		//Finance Document Details
		if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		financeDetail.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(financeDetail);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;

	}
	
	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param detail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setDocumentDetailsAuditData(FinanceDetail detail,
			String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();		
		DocumentDetails object = new DocumentDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < detail.getDocumentDetailsList().size(); i++) {
			DocumentDetails documentDetails = detail.getDocumentDetailsList().get(i);
			documentDetails.setWorkflowId(detail.getWorkflowId());
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

			documentDetails.setRecordStatus(detail.getFinScheduleData().getFinanceMain().getRecordStatus());
			documentDetails.setUserDetails(detail.getFinScheduleData().getFinanceMain().getUserDetails());
			documentDetails.setLastMntOn(detail.getFinScheduleData().getFinanceMain().getLastMntOn());

			if (!documentDetails.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						documentDetails.getBefImage(), documentDetails));
			}
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

	public FinanceMainDAO getFinanceMainDAO() {
    	return financeMainDAO;
    }
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
    	this.financeMainDAO = financeMainDAO;
    }

	public FinanceTypeDAO getFinanceTypeDAO() {
	    return financeTypeDAO;
    }
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
	    this.financeTypeDAO = financeTypeDAO;
    }

	public FinODPenaltyRateDAO getFinODPenaltyRateDAO() {
	    return finODPenaltyRateDAO;
    }
	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
	    this.finODPenaltyRateDAO = finODPenaltyRateDAO;
    }
	public DocumentDetailsDAO getDocumentDetailsDAO() {
	    return documentDetailsDAO;
    }
	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
	    this.documentDetailsDAO = documentDetailsDAO;
    }

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
    	return financeProfitDetailDAO;
    }
	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
    	this.financeProfitDetailDAO = financeProfitDetailDAO;
    }

}
