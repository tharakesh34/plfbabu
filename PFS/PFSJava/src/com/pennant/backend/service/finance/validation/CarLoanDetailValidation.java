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
 * FileName    		:  CarLoanDetailValidation.java                                                   * 	  
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
import com.pennant.backend.dao.lmtmasters.CarLoanDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class CarLoanDetailValidation {
	
	private final static Logger logger = Logger.getLogger(CarLoanDetailValidation.class);
	private CarLoanDetailDAO carLoanDetailDAO;
	
	public CarLoanDetailValidation(CarLoanDetailDAO carLoanDetailDAO) {
		this.carLoanDetailDAO = carLoanDetailDAO;
	}

	public CarLoanDetailDAO getCarLoanDetailDAO() {
		return carLoanDetailDAO;
	}

	public AuditHeader carLoanDetailValidation(AuditHeader auditHeader, String method){
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}
	
	public List<AuditDetail> carLoanDetailListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){
		
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
	
	private AuditDetail validate(AuditDetail auditDetail, String method,String  usrLanguage){
		logger.debug("Entering");
		CarLoanDetail carLoanDetail= (CarLoanDetail) auditDetail.getModelData();
		CarLoanDetail tempCarLoanDetail= null;
		
		if (carLoanDetail.isWorkflow()){
			tempCarLoanDetail = getCarLoanDetailDAO().getCarLoanDetailByID(
					carLoanDetail.getLoanRefNumber(), "_Temp");
		}
		
		CarLoanDetail befCarLoanDetail= getCarLoanDetailDAO().getCarLoanDetailByID(
				carLoanDetail.getLoanRefNumber(), "");

		CarLoanDetail old_CarLoanDetail= carLoanDetail.getBefImage();
		
		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=carLoanDetail.getLoanRefNumber();
		errParm[0]=PennantJavaUtil.getLabel("label_CarLoanRefNumber")+":"+valueParm[0];
		
		
		
		if (carLoanDetail.isNew()) { // for New record or new record into work flow

			if (!carLoanDetail.isWorkflow()) {// With out Work flow only new
				// records
				if (befCarLoanDetail != null) { // Record Already Exists in the
												// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD,
									"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (carLoanDetail.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befCarLoanDetail != null || tempCarLoanDetail != null) { // if
																// records already
																// exists in the
																// main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD,
										"41001", errParm, valueParm),usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befCarLoanDetail == null || tempCarLoanDetail != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD,
										"41005", errParm, valueParm),usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!carLoanDetail.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befCarLoanDetail == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD,
									"41002", errParm, valueParm), usrLanguage));
				} else {
					if (old_CarLoanDetail != null
							&& !old_CarLoanDetail.getLastMntOn().equals(
									befCarLoanDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD,"41003", errParm, valueParm),usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41004", errParm, valueParm),usrLanguage));
						}
					}
				}
			} else {

				if (tempCarLoanDetail == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD,
									"41005", errParm, valueParm), usrLanguage));
				}

				if (tempCarLoanDetail != null && old_CarLoanDetail != null
						&& !old_CarLoanDetail.getLastMntOn().equals(
								tempCarLoanDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD,
									"41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(
				auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !carLoanDetail.isWorkflow()) {
			carLoanDetail.setBefImage(befCarLoanDetail);
		}
		return auditDetail;
	}
}
