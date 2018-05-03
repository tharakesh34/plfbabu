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
 * FileName    		:  FinanceCheckListValidation.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-12-2011    														*
 *                                                                  						*
 * Modified Date    :  29-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-12-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.lmtmasters.FinanceCheckListReferenceDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class FinanceCheckListValidation {

	private static final Logger logger = Logger.getLogger(FinanceCheckListValidation.class);
	private FinanceCheckListReferenceDAO financeCheckListReferenceDAO;


	public FinanceCheckListValidation(FinanceCheckListReferenceDAO financeCheckListReferenceDAO) {
		this.financeCheckListReferenceDAO = financeCheckListReferenceDAO;
	}

	public FinanceCheckListReferenceDAO getFinanceCheckListReferenceDAO() {
		return financeCheckListReferenceDAO;
	}

	public AuditHeader financeCheckListValidation(AuditHeader auditHeader, String method){

		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> finCheckListDetailListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){

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
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		FinanceCheckListReference financeCheckListReference= (FinanceCheckListReference) auditDetail.getModelData();

		FinanceCheckListReference tempFinanceCheckListReference= null;
		if (financeCheckListReference.isWorkflow()){
			tempFinanceCheckListReference = getFinanceCheckListReferenceDAO().
			getFinanceCheckListReferenceById(financeCheckListReference.getId(),financeCheckListReference.getQuestionId(),
					financeCheckListReference.getAnswer(),"_Temp");
		}
		FinanceCheckListReference befFinanceCheckListReference= getFinanceCheckListReferenceDAO().
		getFinanceCheckListReferenceById(financeCheckListReference.getId(),financeCheckListReference.getQuestionId(),
				financeCheckListReference.getAnswer(), "");

		FinanceCheckListReference oldFinanceCheckListReference= financeCheckListReference.getBefImage();


		String[] errParm= new String[3];
		String[] valueParm= new String[3];
		valueParm[0]=financeCheckListReference.getFinReference();
		valueParm[1]=financeCheckListReference.getLovDescQuesDesc();
		valueParm[2]=financeCheckListReference.getLovDescAnswerDesc();
		errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];
		errParm[1]=PennantJavaUtil.getLabel("label_CheckList")+":"+valueParm[1];
		errParm[2]=PennantJavaUtil.getLabel("label_Answer")+":"+valueParm[2];

		if (financeCheckListReference.isNew()){ // for New record or new record into work flow

			if (!financeCheckListReference.isWorkflow()){// With out Work flow only new records  
				if (befFinanceCheckListReference !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41008"
							, errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (financeCheckListReference.getRecordType().equals(PennantConstants.RCD_ADD)){ // if records type is new
					if (befFinanceCheckListReference !=null || tempFinanceCheckListReference!=null ){ // if 
						//records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD
								, "41008", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!financeCheckListReference.isWorkflow()){	// With out Work flow for update and delete

				if (befFinanceCheckListReference ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldFinanceCheckListReference!=null 
							&& !oldFinanceCheckListReference.getLastMntOn().equals(befFinanceCheckListReference.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				if (tempFinanceCheckListReference==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
				if (tempFinanceCheckListReference!=null && oldFinanceCheckListReference!=null 
						&& !oldFinanceCheckListReference.getLastMntOn().equals(tempFinanceCheckListReference.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !financeCheckListReference.isWorkflow()){
			financeCheckListReference.setBefImage(befFinanceCheckListReference);	
		}

		return auditDetail;
	}
}
