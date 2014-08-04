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
 * FileName    		:  EduExpenseDetailValidation.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-12-2011    														*
 *                                                                  						*
 * Modified Date    :  22-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-12-2011       Pennant	                 0.1                                            * 
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


package com.pennant.backend.service.finance.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.lmtmasters.CommidityLoanDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.CommidityLoanDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class CommidityLoanDetailsValidation {
	
	private final static Logger logger = Logger.getLogger(CommidityLoanDetailsValidation.class);
	private CommidityLoanDetailDAO commidityLoanDetailDAO;
	
	public CommidityLoanDetailsValidation(CommidityLoanDetailDAO educationalExpenseDAO) {
		this.commidityLoanDetailDAO = educationalExpenseDAO;
	}
	
	public CommidityLoanDetailDAO getCommidityLoanDetailDAO() {
		return commidityLoanDetailDAO;
	}

	public AuditHeader commidityLoanDetailsValidation(AuditHeader auditHeader, String method){
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}
	
	public List<AuditDetail> commidityLoanDetailsListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){
		
		if(auditDetails!=null && auditDetails.size()>0){
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail =   validate(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail); 		
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
	}
	
	private AuditDetail validate(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		CommidityLoanDetail commidityLoanDetail= (CommidityLoanDetail) auditDetail.getModelData();
		CommidityLoanDetail tempCommidityLoanDetail= null;
		if (commidityLoanDetail.isWorkflow()){
			tempCommidityLoanDetail = getCommidityLoanDetailDAO().getCommidityLoanDetailById(commidityLoanDetail.getLoanRefNumber(),commidityLoanDetail.getItemType(), "_Temp");
		}
		CommidityLoanDetail befCommidityLoanDetail= getCommidityLoanDetailDAO().getCommidityLoanDetailById(commidityLoanDetail.getLoanRefNumber(),commidityLoanDetail.getItemType(), "");
		
		CommidityLoanDetail old_CommidityLoanDetail= commidityLoanDetail.getBefImage();
		
		
		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(commidityLoanDetail.getLoanRefNumber());
		errParm[0]=PennantJavaUtil.getLabel("label_loanReferenceNumber")+":"+valueParm[0];
		
		if (commidityLoanDetail.isNew()){ // for New record or new record into work flow
			
			if (!commidityLoanDetail.isWorkflow()){// With out Work flow only new records  
				if (befCommidityLoanDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (commidityLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befCommidityLoanDetail !=null || tempCommidityLoanDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befCommidityLoanDetail ==null || tempCommidityLoanDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!commidityLoanDetail.isWorkflow()){	// With out Work flow for update and delete
			
				if (befCommidityLoanDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (old_CommidityLoanDetail!=null && !old_CommidityLoanDetail.getLastMntOn().equals(befCommidityLoanDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
			
				if (tempCommidityLoanDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
				
				if (tempCommidityLoanDetail!=null && old_CommidityLoanDetail!=null && !old_CommidityLoanDetail.getLastMntOn().equals(tempCommidityLoanDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if(StringUtils.trimToEmpty(method).equals("doApprove") || !commidityLoanDetail.isWorkflow()){
			auditDetail.setBefImage(befCommidityLoanDetail);	
		}
		return auditDetail;
	}
	

}
