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
 * FileName    		:  EtihadCreditBureauDetailValidation.java                                                   * 	  
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
import com.pennant.backend.dao.finance.EtihadCreditBureauDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.EtihadCreditBureauDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class EtihadCreditBureauDetailValidation {
	
	private static final Logger logger = Logger.getLogger(EtihadCreditBureauDetailValidation.class);	
	private EtihadCreditBureauDetailDAO etihadCreditBureauDetailDAO;
	
	public EtihadCreditBureauDetailValidation(EtihadCreditBureauDetailDAO etihadCreditBureauDetailDAO) {
		this.etihadCreditBureauDetailDAO = etihadCreditBureauDetailDAO;
	}
	
	public EtihadCreditBureauDetailDAO getEtihadCreditBureauDetailDAO() {
		return etihadCreditBureauDetailDAO;
	}

	public AuditHeader etihadCreditBureauDetailValidation(AuditHeader auditHeader, String method){
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}
	
	public List<AuditDetail> etihadCreditBureauDetailListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){
		
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

	private AuditDetail validate(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		EtihadCreditBureauDetail etihadCreditBureauDetail = (EtihadCreditBureauDetail) auditDetail.getModelData();

		EtihadCreditBureauDetail tempEtihadCreditBureauDetail = null;
		if (etihadCreditBureauDetail.isWorkflow()) {
			tempEtihadCreditBureauDetail = getEtihadCreditBureauDetailDAO().getEtihadCreditBureauDetailByID(
					etihadCreditBureauDetail.getId(), "_Temp");
		}
		EtihadCreditBureauDetail befEtihadCreditBureauDetail = getEtihadCreditBureauDetailDAO().getEtihadCreditBureauDetailByID(
				etihadCreditBureauDetail.getId(), "");

		EtihadCreditBureauDetail oldEtihadCreditBureauDetail = etihadCreditBureauDetail.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(etihadCreditBureauDetail.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_LoanRefNumber") + ":" + valueParm[0];

		if (etihadCreditBureauDetail.isNew()) { // for New record or new record into work flow

			if (!etihadCreditBureauDetail.isWorkflow()) {// With out Work flow only new
												// records
				if (befEtihadCreditBureauDetail != null) { // Record Already Exists in the
													// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (etihadCreditBureauDetail.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befEtihadCreditBureauDetail != null || tempEtihadCreditBureauDetail != null) { 
						// if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD,
										"41001", errParm, valueParm),usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befEtihadCreditBureauDetail == null || tempEtihadCreditBureauDetail != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD,
										"41005", errParm, valueParm),usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!etihadCreditBureauDetail.isWorkflow()) { // With out Work flow for update
												// and delete

				if (befEtihadCreditBureauDetail == null) { // if records not exists in the
													// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldEtihadCreditBureauDetail != null
							&& !oldEtihadCreditBureauDetail.getLastMntOn().equals(
									befEtihadCreditBureauDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD,"41003", errParm, valueParm),usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD,"41004", errParm, valueParm),usrLanguage));
						}
					}
				}
			} else {

				if (tempEtihadCreditBureauDetail == null) { // if records not exists in
													// the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41005", errParm, valueParm), usrLanguage));
				}

				if (tempEtihadCreditBureauDetail != null && oldEtihadCreditBureauDetail != null
						&& !oldEtihadCreditBureauDetail.getLastMntOn().equals(
								tempEtihadCreditBureauDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !etihadCreditBureauDetail.isWorkflow()) {
			etihadCreditBureauDetail.setBefImage(befEtihadCreditBureauDetail);
		}
		logger.debug("Leaving");
		return auditDetail;
	}
	
}
