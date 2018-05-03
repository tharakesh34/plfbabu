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
 * FileName    		:  GenderServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.systemmasters.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.GenderDAO;
import com.pennant.backend.dao.systemmasters.SalutationDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Gender;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.GenderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>Gender</b>.<br>
 * 
 */
public class GenderServiceImpl extends GenericService<Gender> implements
		GenderService {

	private static Logger logger = Logger.getLogger(GenderServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private GenderDAO genderDAO;
	private SalutationDAO salutationDAO;

	public GenderServiceImpl() {
		super();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public GenderDAO getGenderDAO() {
		return genderDAO;
	}

	public void setGenderDAO(GenderDAO genderDAO) {
		this.genderDAO = genderDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTGenders/BMTGenders_Temp by using GenderDAO's save method b) Update the
	 * Record in the table. based on the module workFlow Configuration. by using
	 * GenderDAO's update method 3) Audit the record in to AuditHeader and
	 * AdtBMTGenders by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {

		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		Gender gender = (Gender) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;

		if (gender.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (gender.isNew()) {
			gender.setId(getGenderDAO().save(gender, tableType));
			auditHeader.getAuditDetail().setModelData(gender);
			auditHeader.setAuditReference(gender.getId());
		} else {
			getGenderDAO().update(gender, tableType);

			if (StringUtils.isEmpty("")) {
				resetSalutionSystemDefault(gender); 
	        }
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTGenders by using GenderDAO's delete method with type as Blank 3)
	 * Audit the record in to AuditHeader and AdtBMTGenders by using
	 * auditHeaderDAO.addAudit(auditHeader)
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
		Gender gender = (Gender) auditHeader.getAuditDetail().getModelData();

		getGenderDAO().delete(gender, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getGenderById fetch the details by using GenderDAO's getGenderById
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Gender
	 */

	@Override
	public Gender getGenderById(String id) {
		return getGenderDAO().getGenderById(id, "_View");
	}

	/**
	 * getApprovedGenderById fetch the details by using GenderDAO's
	 * getGenderById method . with parameter id and type as blank. it fetches
	 * the approved records from the BMTGenders.
	 * 
	 * @param id
	 *            (String)
	 * @return Gender
	 */

	public Gender getApprovedGenderById(String id) {
		return getGenderDAO().getGenderById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getGenderDAO().delete with parameters gender,"" b) NEW Add new
	 * record in to main table by using getGenderDAO().save with parameters
	 * gender,"" c) EDIT Update record in the main table by using
	 * getGenderDAO().update with parameters gender,"" 3) Delete the record from
	 * the workFlow table by using getGenderDAO().delete with parameters
	 * gender,"_Temp" 4) Audit the record in to AuditHeader and AdtBMTGenders by
	 * using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtBMTGenders by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {

		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		Gender gender = new Gender();
		BeanUtils.copyProperties(auditHeader.getAuditDetail().getModelData(),
				gender);
		
		getGenderDAO().delete(gender, TableType.TEMP_TAB);
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(gender.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(genderDAO.getGenderById(gender.getGenderCode(), ""));
		}

		if (gender.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getGenderDAO().delete(gender, TableType.MAIN_TAB);

		} else {
			gender.setRoleCode("");
			gender.setNextRoleCode("");
			gender.setTaskId("");
			gender.setNextTaskId("");
			gender.setWorkflowId(0);

			if (gender.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				gender.setRecordType("");
				getGenderDAO().save(gender, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				gender.setRecordType("");
				getGenderDAO().update(gender, TableType.MAIN_TAB);
				resetSalutionSystemDefault(gender);
			}
		}

		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(gender);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getGenderDAO().delete with parameters
	 * gender,"_Temp" 3) Audit the record in to AuditHeader and AdtBMTGenders by
	 * using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {

		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		Gender gender = (Gender) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getGenderDAO().delete(gender, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader,
			String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getGenderDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	 private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
			logger.debug("Entering");

			// Get the model object.
			Gender gender = (Gender) auditDetail.getModelData();
			String code = gender.getGenderCode();

			// Check the unique keys.
			if (gender.isNew()
					&& PennantConstants.RECORD_TYPE_NEW.equals(gender.getRecordType())
					&& genderDAO.isDuplicateKey(code, gender.isWorkflow() ? TableType.BOTH_TAB
							: TableType.MAIN_TAB)) {
				String[] parameters = new String[1];
				parameters[0] = PennantJavaUtil.getLabel("label_GenderCode") + ": " + code;

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}
			
			if (gender.isSystemDefault()) {
				String dftGenderCode = getGenderDAO().getSystemDefaultCount(gender.getGenderCode());
				if (StringUtils.isNotEmpty(dftGenderCode)) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "60501",
					        new String[]{dftGenderCode,PennantJavaUtil.getLabel("Gender")}, null));
				}
	        }

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

			logger.debug("Leaving");
			return auditDetail;
		}

	private void resetSalutionSystemDefault(Gender gender) {
		if (!gender.isSystemDefault()) {
			getSalutationDAO().updateSytemDefaultByGender(gender.getGenderCode(),false);
        }
    }

	public SalutationDAO getSalutationDAO() {
	    return salutationDAO;
    }

	public void setSalutationDAO(SalutationDAO salutationDAO) {
	    this.salutationDAO = salutationDAO;
    }
}