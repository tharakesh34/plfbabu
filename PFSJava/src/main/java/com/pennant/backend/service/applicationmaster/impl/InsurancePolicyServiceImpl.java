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
 * FileName    		:  InsurancePolicyServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-02-2017    														*
 *                                                                  						*
 * Modified Date    :  06-02-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-02-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.InsurancePolicyDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeInsuranceDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.FinTypeInsurances;
import com.pennant.backend.model.applicationmaster.InsurancePolicy;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.InsurancePolicyService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>InsurancePolicy</b>.<br>
 * 
 */
public class InsurancePolicyServiceImpl extends GenericService<InsurancePolicy> implements InsurancePolicyService {
	private static final Logger	logger	= Logger.getLogger(InsurancePolicyServiceImpl.class);

	private AuditHeaderDAO		auditHeaderDAO;

	private InsurancePolicyDAO	insurancePolicyDAO;

	private FinTypeInsuranceDAO finTypeInsuranceDAO; 
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO
	 *            the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the insurancePolicyDAO
	 */
	public InsurancePolicyDAO getInsurancePolicyDAO() {
		return insurancePolicyDAO;
	}

	/**
	 * @param insurancePolicyDAO
	 *            the insurancePolicyDAO to set
	 */
	public void setInsurancePolicyDAO(InsurancePolicyDAO insurancePolicyDAO) {
		this.insurancePolicyDAO = insurancePolicyDAO;
	}
	
	public FinTypeInsuranceDAO getFinTypeInsuranceDAO() {
		return finTypeInsuranceDAO;
	}

	public void setFinTypeInsuranceDAO(FinTypeInsuranceDAO finTypeInsuranceDAO) {
		this.finTypeInsuranceDAO = finTypeInsuranceDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * InsurancePolicy/InsurancePolicy_Temp by using InsurancePolicyDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using InsurancePolicyDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtInsurancePolicy by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		InsurancePolicy insurancePolicy = (InsurancePolicy) auditHeader.getAuditDetail().getModelData();

		if (insurancePolicy.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (insurancePolicy.isNew()) {
			getInsurancePolicyDAO().save(insurancePolicy, tableType);
		} else {
			getInsurancePolicyDAO().update(insurancePolicy, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * InsurancePolicy by using InsurancePolicyDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtInsurancePolicy by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		InsurancePolicy insurancePolicy = (InsurancePolicy) auditHeader.getAuditDetail().getModelData();
		getInsurancePolicyDAO().delete(insurancePolicy, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getInsurancePolicyById fetch the details by using InsurancePolicyDAO's getInsurancePolicyById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return InsurancePolicy
	 */
	@Override
	public InsurancePolicy getInsurancePolicyById(String id) {
		return getInsurancePolicyDAO().getInsurancePolicyById(id, "_View");
	}

	/**
	 * getApprovedInsurancePolicyById fetch the details by using InsurancePolicyDAO's getInsurancePolicyById method .
	 * with parameter id and type as blank. it fetches the approved records from the InsurancePolicy.
	 * 
	 * @param id
	 *            (String)
	 * @return InsurancePolicy
	 */
	public InsurancePolicy getApprovedInsurancePolicyById(String id) {
		return getInsurancePolicyDAO().getInsurancePolicyById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getInsurancePolicyDAO().delete with
	 * parameters insurancePolicy,"" b) NEW Add new record in to main table by using getInsurancePolicyDAO().save with
	 * parameters insurancePolicy,"" c) EDIT Update record in the main table by using getInsurancePolicyDAO().update
	 * with parameters insurancePolicy,"" 3) Delete the record from the workFlow table by using
	 * getInsurancePolicyDAO().delete with parameters insurancePolicy,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtInsurancePolicy by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtInsurancePolicy by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		InsurancePolicy insurancePolicy = new InsurancePolicy("");
		BeanUtils.copyProperties((InsurancePolicy) auditHeader.getAuditDetail().getModelData(), insurancePolicy);

		if (PennantConstants.RECORD_TYPE_DEL.equals(insurancePolicy.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			getInsurancePolicyDAO().delete(insurancePolicy, "");
		} else {
			insurancePolicy.setRoleCode("");
			insurancePolicy.setNextRoleCode("");
			insurancePolicy.setTaskId("");
			insurancePolicy.setNextTaskId("");
			insurancePolicy.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(insurancePolicy.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				insurancePolicy.setRecordType("");
				getInsurancePolicyDAO().save(insurancePolicy, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				insurancePolicy.setRecordType("");
				getInsurancePolicyDAO().update(insurancePolicy, "");
			}
		}

		getInsurancePolicyDAO().delete(insurancePolicy, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(insurancePolicy);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getInsurancePolicyDAO().delete with parameters insurancePolicy,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtInsurancePolicy by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		InsurancePolicy insurancePolicy = (InsurancePolicy) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getInsurancePolicyDAO().delete(insurancePolicy, "_TEMP");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getInsurancePolicyDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		InsurancePolicy insurancePolicy = (InsurancePolicy) auditDetail.getModelData();

		InsurancePolicy tempInsurancePolicy = null;
		if (insurancePolicy.isWorkflow()) {
			tempInsurancePolicy = getInsurancePolicyDAO().getInsurancePolicyById(insurancePolicy.getId(), "_Temp");
		}
		InsurancePolicy befInsurancePolicy = getInsurancePolicyDAO()
				.getInsurancePolicyById(insurancePolicy.getId(), "");

		InsurancePolicy oldInsurancePolicy = insurancePolicy.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = insurancePolicy.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_PolicyCode") + ":" + valueParm[0];

		if (insurancePolicy.isNew()) { // for New record or new record into work flow

			if (!insurancePolicy.isWorkflow()) {// With out Work flow only new records  
				if (befInsurancePolicy != null) { // Record Already Exists in the table then error  
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (PennantConstants.RECORD_TYPE_NEW.equals(insurancePolicy.getRecordType())) { // if records type is new
					if (befInsurancePolicy != null || tempInsurancePolicy != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,
								valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befInsurancePolicy == null || tempInsurancePolicy != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,
								valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!insurancePolicy.isWorkflow()) { // With out Work flow for update and delete

				if (befInsurancePolicy == null) { // if records not exists in the main table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldInsurancePolicy != null
							&& !oldInsurancePolicy.getLastMntOn().equals(befInsurancePolicy.getLastMntOn())) {
						if (PennantConstants.TRAN_DEL.equalsIgnoreCase(StringUtils.trimToEmpty(auditDetail
								.getAuditTranType()))) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,
									valueParm));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,
									valueParm));
						}
					}
				}
			} else {

				if (tempInsurancePolicy == null) { // if records not exists in the Work flow table 
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (tempInsurancePolicy != null && oldInsurancePolicy != null
						&& !oldInsurancePolicy.getLastMntOn().equals(tempInsurancePolicy.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}

		//Validate if the insurance Policy assigned to any FinanceType
		if(StringUtils.equals(insurancePolicy.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED) 
				&& StringUtils.equals(insurancePolicy.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {

			List<FinTypeInsurances> finTypeInsurance = getFinTypeInsuranceDAO().getFinTypeInsurances(
					insurancePolicy.getPolicyCode(), FinanceConstants.MODULEID_FINTYPE, "_view");
			if (!finTypeInsurance.isEmpty()) {
				String[][] parms = new String[2][1];
				parms[1][0] = insurancePolicy.getPolicyCode();
				parms[0][0] = PennantJavaUtil.getLabel("label_PolicyCode") + ":" + parms[1][0];
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
						"41006", parms[0], parms[1]), usrLanguage));

			}

		}
		
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !insurancePolicy.isWorkflow()) {
			auditDetail.setBefImage(befInsurancePolicy);
		}

		return auditDetail;
	}

}