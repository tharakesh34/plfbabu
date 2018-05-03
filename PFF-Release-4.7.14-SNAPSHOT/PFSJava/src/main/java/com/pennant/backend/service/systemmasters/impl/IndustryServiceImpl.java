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
 * FileName    		:  IndustryServiceImpl.java                                                   * 	  
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

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.IndustryDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Industry;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.IndustryService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>Industry</b>.<br>
 * 
 */
public class IndustryServiceImpl extends GenericService<Industry> implements IndustryService {

	private static Logger logger = Logger.getLogger(IndustryServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private IndustryDAO industryDAO;

	public IndustryServiceImpl() {
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

	public IndustryDAO getIndustryDAO() {
		return industryDAO;
	}

	public void setIndustryDAO(IndustryDAO industryDAO) {
		this.industryDAO = industryDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTIndustries/BMTIndustries_Temp by using IndustryDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using IndustryDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBMTIndustries by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {

		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		Industry industry = (Industry) auditHeader.getAuditDetail()
		.getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (industry.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (industry.isNew()) {
			getIndustryDAO().save(industry, tableType);
			auditHeader.getAuditDetail().setModelData(industry);
			auditHeader.setAuditReference(industry.getIndustryCode() +PennantConstants.KEY_SEPERATOR+ industry.getSubSectorCode());
		} else {
			getIndustryDAO().update(industry, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTIndustries by using IndustryDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBMTIndustries by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {

		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		Industry industry = (Industry) auditHeader.getAuditDetail().getModelData();

		getIndustryDAO().delete(industry, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getIndustryById fetch the details by using IndustryDAO's getIndustryById
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Industry
	 */
	@Override
	public Industry getIndustryById(String id) {
		return getIndustryDAO().getIndustryById(id, "_View");
	}

	/**
	 * getApprovedIndustryById fetch the details by using IndustryDAO's
	 * getIndustryById method . with parameter id and type as blank. it fetches
	 * the approved records from the BMTIndustries.
	 * 
	 * @param id
	 *            (String)
	 * @return Industry
	 */
	public Industry getApprovedIndustryById(String id) {
		return getIndustryDAO().getIndustryById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getIndustryDAO().delete with parameters industry,"" b) NEW Add new
	 * record in to main table by using getIndustryDAO().save with parameters
	 * industry,"" c) EDIT Update record in the main table by using
	 * getIndustryDAO().update with parameters industry,"" 3) Delete the record
	 * from the workFlow table by using getIndustryDAO().delete with parameters
	 * industry,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTIndustries by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTIndustries by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {

		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader);

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		Industry industry = new Industry();
		BeanUtils.copyProperties((Industry) auditHeader.getAuditDetail()
				.getModelData(), industry);
		
		getIndustryDAO().delete(industry, TableType.TEMP_TAB);
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(industry.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(industryDAO.getIndustryById(industry.getIndustryCode(), ""));
		}

		if (industry.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getIndustryDAO().delete(industry, TableType.MAIN_TAB);

		} else {
			industry.setRoleCode("");
			industry.setNextRoleCode("");
			industry.setTaskId("");
			industry.setNextTaskId("");
			industry.setWorkflowId(0);

			if (industry.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				industry.setRecordType("");
				getIndustryDAO().save(industry, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				industry.setRecordType("");
				getIndustryDAO().update(industry, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(industry);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getIndustryDAO().delete with parameters
	 * industry,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTIndustries by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {

		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		Industry industry = (Industry) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getIndustryDAO().delete(industry, TableType.TEMP_TAB);

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
	private AuditHeader businessValidation(AuditHeader auditHeader) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),auditHeader.getUsrLanguage());
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
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage){
		logger.debug("Entering");

		// Get the model object.
		Industry industry = (Industry) auditDetail.getModelData();
		String code = industry.getIndustryCode();

		// Check the unique keys.
		if (industry.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(industry.getRecordType())
				&& industryDAO.isDuplicateKey(code, industry.isWorkflow() ? TableType.BOTH_TAB
						: TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_IndustryCode") + ": " + code;

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;	
	}

}