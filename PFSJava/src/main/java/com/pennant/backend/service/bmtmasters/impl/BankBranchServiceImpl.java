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
 * FileName    		:  BankBranchServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-10-2016    														*
 *                                                                  						*
 * Modified Date    :  17-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-10-2016       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.bmtmasters.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.beneficiary.BeneficiaryDAO;
import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>BankBranch</b>.<br>
 * 
 */
public class BankBranchServiceImpl extends GenericService<BankBranch> implements BankBranchService {
	private static final Logger logger = Logger.getLogger(BankBranchServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private BankBranchDAO bankBranchDAO;
	private MandateDAO mandateDAO;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private BeneficiaryDAO beneficiaryDAO;

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	
	/**
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	/**
	 * @return the bankBranchDAO
	 */
	public BankBranchDAO getBankBranchDAO() {
		return bankBranchDAO;
	}
	/**
	 * @param bankBranchDAO the bankBranchDAO to set
	 */
	public void setBankBranchDAO(BankBranchDAO bankBranchDAO) {
		this.bankBranchDAO = bankBranchDAO;
	}

	/**
	 * @return the bankBranch
	 */
	@Override
	public BankBranch getBankBranch() {
		return getBankBranchDAO().getBankBranch();
	}
	/**
	 * @return the bankBranch for New Record
	 */
	@Override
	public BankBranch getNewBankBranch() {
		return getBankBranchDAO().getNewBankBranch();
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table BankBranches/BankBranches_Temp 
	 * 			by using BankBranchDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using BankBranchDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtBankBranches by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */
	 
	@Override	
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		BankBranch bankBranch = (BankBranch) auditHeader.getAuditDetail().getModelData();
		
		if (bankBranch.isWorkflow()) {
			tableType="_Temp";
		}

		if (bankBranch.isNew()) {
			bankBranch.setId(getBankBranchDAO().save(bankBranch,tableType));
			auditHeader.getAuditDetail().setModelData(bankBranch);
			auditHeader.setAuditReference(String.valueOf(bankBranch.getBankBranchID()));
		}else{
			getBankBranchDAO().update(bankBranch,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table BankBranches by using BankBranchDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtBankBranches by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		BankBranch bankBranch = (BankBranch) auditHeader.getAuditDetail().getModelData();
		getBankBranchDAO().delete(bankBranch,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getBankBranchById fetch the details by using BankBranchDAO's getBankBranchById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return BankBranch
	 */
	
	@Override
	public BankBranch getBankBranchById(long id) {
		return getBankBranchDAO().getBankBranchById(id,"_View");
	}
	/**
	 * getApprovedBankBranchById fetch the details by using BankBranchDAO's getBankBranchById method .
	 * with parameter id and type as blank. it fetches the approved records from the BankBranches.
	 * @param id (int)
	 * @return BankBranch
	 */
	
	public BankBranch getApprovedBankBranchById(long id) {
		return getBankBranchDAO().getBankBranchById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getBankBranchDAO().delete with parameters bankBranch,""
	 * 		b)  NEW		Add new record in to main table by using getBankBranchDAO().save with parameters bankBranch,""
	 * 		c)  EDIT	Update record in the main table by using getBankBranchDAO().update with parameters bankBranch,""
	 * 3)	Delete the record from the workFlow table by using getBankBranchDAO().delete with parameters bankBranch,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtBankBranches by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtBankBranches by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		BankBranch bankBranch = new BankBranch();
		BeanUtils.copyProperties((BankBranch) auditHeader.getAuditDetail().getModelData(), bankBranch);

		if (bankBranch.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tranType=PennantConstants.TRAN_DEL;

				getBankBranchDAO().delete(bankBranch,"");
				
			} else {
				bankBranch.setRoleCode("");
				bankBranch.setNextRoleCode("");
				bankBranch.setTaskId("");
				bankBranch.setNextTaskId("");
				bankBranch.setWorkflowId(0);
				
				if (bankBranch.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
				{	
					tranType=PennantConstants.TRAN_ADD;
					bankBranch.setRecordType("");
					getBankBranchDAO().save(bankBranch,"");
				} else {
					tranType=PennantConstants.TRAN_UPD;
					bankBranch.setRecordType("");
					getBankBranchDAO().update(bankBranch,"");
				}
			}
			
			getBankBranchDAO().delete(bankBranch,"_Temp");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);

			auditHeader.setAuditTranType(tranType);
			auditHeader.getAuditDetail().setAuditTranType(tranType);
			auditHeader.getAuditDetail().setModelData(bankBranch);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");		
		
			return auditHeader;
		}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getBankBranchDAO().delete with parameters bankBranch,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtBankBranches by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.debug("Entering");
			auditHeader = businessValidation(auditHeader,"doReject");
			if (!auditHeader.isNextProcess()) {
				return auditHeader;
			}

			BankBranch bankBranch = (BankBranch) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getBankBranchDAO().delete(bankBranch,"_Temp");
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");
			
			return auditHeader;
		}

		/**
		 * businessValidation method do the following steps.
		 * 1)	validate the audit detail 
		 * 2)	if any error/Warnings  then assign the to auditHeader
		 * 3)   identify the nextprocess
		 *  
		 * @param AuditHeader (auditHeader)
		 * @param boolean onlineRequest
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

		/**
		 * Validation method do the following steps.
		 * 1)	get the details from the auditHeader. 
		 * 2)	fetch the details from the tables
		 * 3)	Validate the Record based on the record details. 
		 * 4) 	Validate for any business validation.
		 * 5)	for any mismatch conditions Fetch the error details from getBankBranchDAO().getErrorDetail with Error ID and language as parameters.
		 * 6)	if any error/Warnings  then assign the to auditHeader 
		 * @param AuditHeader (auditHeader)
		 * @param boolean onlineRequest
		 * @return auditHeader
		 */
		
		private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
			logger.debug("Entering");
			auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
			BankBranch bankBranch= (BankBranch) auditDetail.getModelData();
			
			BankBranch tempBankBranch= null;
			if (bankBranch.isWorkflow()){
				tempBankBranch = getBankBranchDAO().getBankBranchById(bankBranch.getId(), "_Temp");
			}
			BankBranch befBankBranch= getBankBranchDAO().getBankBranchById(bankBranch.getId(), "");
			
			BankBranch oldBankBranch= bankBranch.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=bankBranch.getIFSC();
			errParm[0]=PennantJavaUtil.getLabel("label_IFSC")+":"+valueParm[0];
			
			if (bankBranch.isNew()){ // for New record or new record into work flow
				
				if (!bankBranch.isWorkflow()){// With out Work flow only new records  
					if (befBankBranch !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (bankBranch.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befBankBranch !=null || tempBankBranch!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befBankBranch ==null || tempBankBranch!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!bankBranch.isWorkflow()){	// With out Work flow for update and delete
				
					if (befBankBranch ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldBankBranch!=null && !oldBankBranch.getLastMntOn().equals(befBankBranch.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempBankBranch==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (tempBankBranch!=null && oldBankBranch!=null && !oldBankBranch.getLastMntOn().equals(tempBankBranch.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
			int count = getBankBranchDAO().getBankBranchByIFSC(bankBranch.getIFSC(),bankBranch.getId(), "_View");
			if(count != 0){
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", errParm,valueParm), usrLanguage));

			}
			if (StringUtils.trimToEmpty(bankBranch.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
				int mandateCount = getMandateDAO().getBranch(bankBranch.getBankBranchID(),"");
				int disbursementCount = getFinAdvancePaymentsDAO().getBranch(bankBranch.getBankBranchID(),"");
				int beneficiaryCount = getBeneficiaryDAO().getBranch(bankBranch.getBankBranchID(),"");
				if (mandateCount != 0 && disbursementCount != 0 && beneficiaryCount != 0) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", errParm, valueParm), usrLanguage));
				}
			}
			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if(StringUtils.trimToEmpty(method).equals("doApprove") || !bankBranch.isWorkflow()){
				auditDetail.setBefImage(befBankBranch);	
			}

			return auditDetail;
		}

		/**
		 * fetch Bank Branch details by IFSC code.
		 * 
		 * @param ifsc
		 * @return BankBranch
		 */
		@Override
		public BankBranch getBankBrachByIFSC(String ifsc) {
			return getBankBranchDAO().getBankBrachByIFSC(ifsc, "");
		}

		/**
		 * fetch Bank Branch details by bankCode and branchCode.
		 * 
		 * @param bankCode
		 * @param branchCode
		 * @return BankBranch
		 */
		@Override
		public BankBranch getBankBrachByCode(String bankCode, String branchCode) {
			return getBankBranchDAO().getBankBrachByCode(bankCode, branchCode, "");
		}

		public MandateDAO getMandateDAO() {
			return mandateDAO;
		}

		public void setMandateDAO(MandateDAO mandateDAO) {
			this.mandateDAO = mandateDAO;
		}

		public FinAdvancePaymentsDAO getFinAdvancePaymentsDAO() {
			return finAdvancePaymentsDAO;
		}

		public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
			this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
		}

		public BeneficiaryDAO getBeneficiaryDAO() {
			return beneficiaryDAO;
		}

		public void setBeneficiaryDAO(BeneficiaryDAO beneficiaryDAO) {
			this.beneficiaryDAO = beneficiaryDAO;
		}

}