package com.pennant.backend.service.finance.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.commitment.CommitmentDAO;
import com.pennant.backend.dao.commitment.CommitmentMovementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinanceCancellationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.rits.cloning.Cloner;

public class FinanceCancellationServiceImpl  extends GenericService<FinanceMain>  implements FinanceCancellationService {

	private final static Logger logger = Logger.getLogger(FinanceCancellationServiceImpl.class);
	
	private AuditHeaderDAO 				auditHeaderDAO;
	
	private FinanceMainDAO 				financeMainDAO;
	private FinanceTypeDAO 				financeTypeDAO;
	private CommitmentDAO 				commitmentDAO;
	private CommitmentMovementDAO 		commitmentMovementDAO;
	private PostingsPreparationUtil 	postingsPreparationUtil;
	
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
		
		String finReference = financeMain.getFinReference();
		if (financeMain.isWorkflow()) {
			tableType = "_TEMP";
		}
		financeMain.setRcdMaintainSts(PennantConstants.CANCELFINANCE);
		if(tableType.equals("")){
			financeMain.setRcdMaintainSts("");
			financeMain.setFinIsActive(false);
			financeMain.setClosingStatus("C");
		}
		
		//Repayments Postings Details Process Execution
		if(!financeMain.isWorkflow()){
			List<Object> returnList = getPostingsPreparationUtil().processFinCanclPostings(finReference, "");
			if(!(Boolean) returnList.get(0)){
				throw new AccountNotFoundException(returnList.get(1).toString());
			}
		}
		
		// Finance Main Details Save And Update
		//=======================================
		if (financeMain.isNew()) {
			getFinanceMainDAO().save(financeMain, tableType, false);
		} else {
			getFinanceMainDAO().update(financeMain, tableType, false);
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
		getFinanceMainDAO().delete(financeMain, "_Temp", false);
		
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
		String finReference = financeMain.getFinReference();
		
		financeMain.setFinIsActive(false);
		financeMain.setClosingStatus("C");
		financeMain.setRcdMaintainSts("");
		financeMain.setRoleCode("");
		financeMain.setNextRoleCode("");
		financeMain.setTaskId("");
		financeMain.setNextTaskId("");
		financeMain.setWorkflowId(0);

		//Finance Cancellation Posting Process Execution
		//=====================================
		List<Object> returnList = getPostingsPreparationUtil().processFinCanclPostings(finReference, "");
		if(!(Boolean) returnList.get(0)){
			throw new AccountNotFoundException(returnList.get(1).toString());
		}
		
		// Finance Commitment Reference Posting Details
		Commitment commitment = null;
		if(!StringUtils.trimToEmpty(financeMain.getFinCommitmentRef()).equals("")){
			commitment = getCommitmentDAO().getCommitmentById(financeMain.getFinCommitmentRef().trim(), "");

			BigDecimal cmtUtlAmt = CalculationUtil.getConvertedAmount(financeMain.getFinCcy(), commitment.getCmtCcy(),
					financeMain.getFinAmount());
			getCommitmentDAO().updateCommitmentAmounts(commitment.getCmtReference(), cmtUtlAmt.negate(),commitment.getCmtExpDate());
			CommitmentMovement cmtMovement = prepareCommitMovement(commitment, financeMain, cmtUtlAmt, 0);
			if(cmtMovement != null){
				getCommitmentMovementDAO().save(cmtMovement, "");
			}
		}

		tranType = PennantConstants.TRAN_UPD;
		financeMain.setRecordType("");
		getFinanceMainDAO().update(financeMain, "", false);
		
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
	
	/**
	 * Method for Add a Movement Entry for Commitment Repayment Event, if Only for Revolving Commitment
	 * @param commitment
	 * @param dataSet
	 * @param postAmount
	 * @param linkedtranId
	 * @return
	 */
	private CommitmentMovement prepareCommitMovement(Commitment commitment, FinanceMain financeMain, 
			BigDecimal postAmount, long linkedtranId){

		CommitmentMovement movement = new CommitmentMovement();
		Date curBussDate =(Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");

		movement.setCmtReference(commitment.getCmtReference());
		movement.setFinReference(financeMain.getFinReference());
		movement.setFinBranch(financeMain.getFinBranch());
		movement.setFinType(financeMain.getFinType());
		movement.setMovementDate(curBussDate);
		movement.setMovementOrder(getCommitmentMovementDAO().getMaxMovementOrderByRef(commitment.getCmtReference())+1);
		movement.setMovementType("FC");//Finance Cancellation
		movement.setMovementAmount(postAmount);
		movement.setCmtAmount(commitment.getCmtAmount());
		movement.setCmtUtilizedAmount(commitment.getCmtUtilizedAmount().subtract(postAmount));
		if(commitment.getCmtExpDate().compareTo(curBussDate) < 0){
			movement.setCmtAvailable(BigDecimal.ZERO);
		}else{
			movement.setCmtAvailable(commitment.getCmtAvailable().add(postAmount));
		}
		movement.setCmtCharges(BigDecimal.ZERO);
		movement.setLinkedTranId(linkedtranId);
		movement.setVersion(1);
		movement.setLastMntBy(9999);
		movement.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		movement.setRecordStatus("Approved");
		movement.setRoleCode("");
		movement.setNextRoleCode("");
		movement.setTaskId("");
		movement.setNextTaskId("");
		movement.setRecordType("");
		movement.setWorkflowId(0);

		return movement;

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

	public PostingsPreparationUtil getPostingsPreparationUtil() {
	    return postingsPreparationUtil;
    }
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
	    this.postingsPreparationUtil = postingsPreparationUtil;
    }

	public FinanceTypeDAO getFinanceTypeDAO() {
	    return financeTypeDAO;
    }
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
	    this.financeTypeDAO = financeTypeDAO;
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

	
}
