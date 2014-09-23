/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  SuspenseServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.backend.service.financemanagement.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FinanceProfitDetailFiller;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.commitment.CommitmentDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.financemanagement.SuspenseService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;

/**
 * Service implementation for methods that depends on <b>FinanceSuspHead</b>.<br>
 * 
 */
public class SuspenseServiceImpl extends GenericService<FinanceSuspHead> implements SuspenseService {
	
	private final static Logger logger = Logger.getLogger(SuspenseServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private FinanceProfitDetailFiller financeProfitDetailFiller;
	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private CommitmentDAO commitmentDAO;
	private CustomerStatusCodeDAO customerStatusCodeDAO;
	private PostingsDAO postingsDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	
	public PostingsDAO getPostingsDAO() {
    	return postingsDAO;
    }
	public void setPostingsDAO(PostingsDAO postingsDAO) {
    	this.postingsDAO = postingsDAO;
    }
	
	public FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
    	return financeSuspHeadDAO;
    }
	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
    	this.financeSuspHeadDAO = financeSuspHeadDAO;
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
	
	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
    	return financeProfitDetailDAO;
    }
	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
    	this.financeProfitDetailDAO = financeProfitDetailDAO;
    }
	
	public FinanceProfitDetailFiller getFinanceProfitDetailFiller() {
    	return financeProfitDetailFiller;
    }
	public void setFinanceProfitDetailFiller(FinanceProfitDetailFiller financeProfitDetailFiller) {
    	this.financeProfitDetailFiller = financeProfitDetailFiller;
    }
	
	public CommitmentDAO getCommitmentDAO() {
    	return commitmentDAO;
    }
	public void setCommitmentDAO(CommitmentDAO commitmentDAO) {
    	this.commitmentDAO = commitmentDAO;
    }
	
	public CustomerStatusCodeDAO getCustomerStatusCodeDAO() {
    	return customerStatusCodeDAO;
    }
	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
    	this.customerStatusCodeDAO = customerStatusCodeDAO;
    }
	@Override
	public FinanceSuspHead getFinanceSuspHead() {
		return getFinanceSuspHeadDAO().getFinanceSuspHead();
	}
	
	@Override
	public FinanceSuspHead getNewFinanceSuspHead() {
		return getFinanceSuspHeadDAO().getNewFinanceSuspHead();
	}

	/**
	 * getFinanceSuspHeadById fetch the details by using 
	 * FinanceSuspHeadDAO's getFinanceSuspHeadById method.
	 * 
	 * @param finRef
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceSuspHead
	 */
	@Override
	public FinanceSuspHead getFinanceSuspHeadById(String finRef,boolean isEnquiry) {
		FinanceSuspHead suspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(finRef,"_View");
		if(suspHead != null && isEnquiry){
			suspHead.setSuspDetailsList(getFinanceSuspHeadDAO().getFinanceSuspDetailsListById(finRef));
			suspHead.setSuspPostingsList(getPostingsDAO().getPostingsByFinRefAndEvent(
					suspHead.getFinReference(), "'M_AMZ','M_NONAMZ'",true));
		}
		return suspHead;
	}
	
	/**
	 * getSuspFinanceList fetch the FinReference details by using 
	 * FinanceSuspHeadDAO's .
	 * 
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceSuspHead
	 */
	@Override
	public List<String> getSuspFinanceList() {
		return getFinanceSuspHeadDAO().getSuspFinanceList();
	}
	
	/**
	 * This method refresh the Record.
	 * @param FinanceSuspHead (suspHead)
 	 * @return suspHead
	 */
	@Override
	public FinanceSuspHead refresh(FinanceSuspHead suspHead) {
		logger.debug("Entering");
		getFinanceSuspHeadDAO().refresh(suspHead);
		getFinanceSuspHeadDAO().initialize(suspHead);
		logger.debug("Leaving");
		return suspHead;
	}
	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FinFinanceSuspHeads/FinFinanceSuspHeads_Temp 
	 * 			by using FinanceSuspHeadDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using FinanceSuspHeadDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinFinanceSuspHeads by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");	
		
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		FinanceSuspHead financeSuspHead = (FinanceSuspHead) auditHeader.getAuditDetail().getModelData();

		if (financeSuspHead.isWorkflow()) {
			tableType="_TEMP";
		}

		if(!financeSuspHead.isWorkflow()){

			/*//Check Finance is RIA Finance Type or Not
			boolean isRIAFinance = getFinanceTypeDAO().checkRIAFinance(financeSuspHead.getFinType());

			Date dateValueDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_VALUE).toString());
			getFinanceSuspHeadCalculationUtil().processProvCalculations(financeSuspHead, dateValueDate, false, true, isRIAFinance);*/
		}

		if(financeSuspHead.isManualSusp()){
			financeSuspHead.setFinIsInSusp(true);
			financeSuspHead.setFinSuspTrfDate(financeSuspHead.getFinSuspDate());
		}
		
		if (financeSuspHead.isNew()) {
			getFinanceSuspHeadDAO().save(financeSuspHead,tableType);
			auditHeader.getAuditDetail().setModelData(financeSuspHead);
			auditHeader.setAuditReference(String.valueOf(financeSuspHead.getFinReference()));
		}else{
			getFinanceSuspHeadDAO().update(financeSuspHead,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FinFinanceSuspHeads by using FinanceSuspHeadDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFinFinanceSuspHeads by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceSuspHead financeSuspHead = (FinanceSuspHead) auditHeader.getAuditDetail().getModelData();
		getFinanceSuspHeadDAO().delete(financeSuspHead,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getFinanceSuspHeadDAO().delete with parameters financeSuspHead,""
	 * 		b)  NEW		Add new record in to main table by using getFinanceSuspHeadDAO().save with parameters financeSuspHead,""
	 * 		c)  EDIT	Update record in the main table by using getFinanceSuspHeadDAO().update with parameters financeSuspHead,""
	 * 3)	Delete the record from the workFlow table by using getFinanceSuspHeadDAO().delete with parameters financeSuspHead,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtFinFinanceSuspHeads by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtFinFinanceSuspHeads by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FinanceSuspHead financeSuspHead = new FinanceSuspHead();
		BeanUtils.copyProperties((FinanceSuspHead) auditHeader.getAuditDetail().getModelData(), financeSuspHead);
		
		if (financeSuspHead.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getFinanceSuspHeadDAO().delete(financeSuspHead, "");
		} else {
			financeSuspHead.setRoleCode("");
			financeSuspHead.setNextRoleCode("");
			financeSuspHead.setTaskId("");
			financeSuspHead.setNextTaskId("");
			financeSuspHead.setWorkflowId(0);
			
			if(financeSuspHead.isManualSusp()){
				financeSuspHead.setFinIsInSusp(true);
				financeSuspHead.setFinSuspTrfDate(financeSuspHead.getFinSuspDate());
			}

			if (financeSuspHead.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				financeSuspHead.setRecordType("");
				getFinanceSuspHeadDAO().save(financeSuspHead, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				financeSuspHead.setRecordType("");
				getFinanceSuspHeadDAO().update(financeSuspHead, "");
			}
		}
		
		//Finance Profit Details Updation
		String finReference = financeSuspHead.getFinReference();
		FinanceProfitDetail finPftDetail = getFinanceProfitDetailDAO().getFinPftDetailForBatch(finReference);
		AEAmountCodes aeAmountCodes = null;
		Date curBussDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR).toString());

		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(finReference, "", false);
		List<FinanceScheduleDetail> scheduleDetailList = getFinanceScheduleDetailDAO().getFinSchdDetailsForBatch(finReference);
		
		//Commitment Set Non-Performing Status
		if(!StringUtils.trimToEmpty(financeMain.getFinCommitmentRef()).equals("")){
			if(financeSuspHead.isManualSusp() || financeSuspHead.isFinIsInSusp()){
				getCommitmentDAO().updateNonPerformStatus(financeMain.getFinCommitmentRef());
			}
		}

		aeAmountCodes = AEAmounts.procAEAmounts(financeMain, scheduleDetailList, finPftDetail, curBussDate);
		finPftDetail = getFinanceProfitDetailFiller().prepareFinPftDetails(aeAmountCodes, finPftDetail, curBussDate);
		
		String worstSts = getCustomerStatusCodeDAO().getFinanceStatus(finReference, false);
		finPftDetail.setFinWorstStatus(worstSts);
		getFinanceProfitDetailDAO().update(finPftDetail, false);

		getFinanceSuspHeadDAO().delete(financeSuspHead,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(financeSuspHead);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getFinanceSuspHeadDAO().delete with parameters financeSuspHead,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFinFinanceSuspHeads by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		
		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceSuspHead financeSuspHead = (FinanceSuspHead) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinanceSuspHeadDAO().delete(financeSuspHead,"_TEMP");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * 5)	for any mismatch conditions Fetch the error details from getFinanceSuspHeadDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		FinanceSuspHead financeSuspHead= (FinanceSuspHead) auditDetail.getModelData();

		FinanceSuspHead tempFinanceSuspHead= null;
		if (financeSuspHead.isWorkflow()){
			tempFinanceSuspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(financeSuspHead.getId(), "_Temp");
		}
		FinanceSuspHead befFinanceSuspHead= getFinanceSuspHeadDAO().getFinanceSuspHeadById(financeSuspHead.getId(), "");
		FinanceSuspHead oldFinanceSuspHead= financeSuspHead.getBefImage();

		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=financeSuspHead.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

		if (financeSuspHead.isNew()){ // for New record or new record into work flow

			if (!financeSuspHead.isWorkflow()){// With out Work flow only new records  
				if (befFinanceSuspHead !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (financeSuspHead.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befFinanceSuspHead !=null || tempFinanceSuspHead!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befFinanceSuspHead ==null || tempFinanceSuspHead!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!financeSuspHead.isWorkflow()){	// With out Work flow for update and delete

				if (befFinanceSuspHead ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldFinanceSuspHead!=null && !oldFinanceSuspHead.getLastMntOn().equals(befFinanceSuspHead.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempFinanceSuspHead==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (oldFinanceSuspHead!=null && !oldFinanceSuspHead.getLastMntOn().equals(tempFinanceSuspHead.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !financeSuspHead.isWorkflow()){
			financeSuspHead.setBefImage(befFinanceSuspHead);	
		}

		return auditDetail;
	}

}