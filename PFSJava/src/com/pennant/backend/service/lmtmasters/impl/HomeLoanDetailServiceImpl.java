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
 * FileName    		:  HomeLoanDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-10-2011    														*
 *                                                                  						*
 * Modified Date    :  13-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.lmtmasters.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.lmtmasters.HomeLoanDetailDAO;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.HomeLoanDetail;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.validation.HomeLoanDetailValidation;
import com.pennant.backend.service.lmtmasters.HomeLoanDetailService;
import com.pennant.backend.util.PennantConstants;

/**
 * Service implementation for methods that depends on <b>HomeLoanDetail</b>.<br>
 * 
 */
public class HomeLoanDetailServiceImpl extends GenericService<HomeLoanDetail>
		implements HomeLoanDetailService {
	
	private final static Logger logger = Logger.getLogger(HomeLoanDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private HomeLoanDetailDAO homeLoanDetailDAO;
	
	private HomeLoanDetailValidation homeLoanDetailValidation;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public HomeLoanDetailDAO getHomeLoanDetailDAO() {
		return homeLoanDetailDAO;
	}
	public void setHomeLoanDetailDAO(HomeLoanDetailDAO homeLoanDetailDAO) {
		this.homeLoanDetailDAO = homeLoanDetailDAO;
	}

	@Override
	public HomeLoanDetail getHomeLoanDetail() {
		return getHomeLoanDetailDAO().getHomeLoanDetail();
	}

	@Override
	public HomeLoanDetail getNewHomeLoanDetail() {
		return getHomeLoanDetailDAO().getNewHomeLoanDetail();
	}
	
	/**
	 * @return the homeLoanDetailValidation
	 */
	public HomeLoanDetailValidation getHomeLoanDetailValidation() {
		if(homeLoanDetailValidation==null){
			this.homeLoanDetailValidation = new HomeLoanDetailValidation(homeLoanDetailDAO);
		}
		return this.homeLoanDetailValidation;
	}

	/**
	 * saveOrUpdate method method do the following steps. 
	 * 1) Do the Business validation by using businessValidation(auditHeader) method if there is
	 * 		any error or warning message then return the auditHeader. 
	 * 2) Do Add or Update the Record 
	 * 		a) Add new Record for the new record in the DB table 
	 * 			LMTHomeLoanDetail/LMTHomeLoanDetail_Temp by using HomeLoanDetailDAO's save method 
	 * 		b) Update the Record in the table. based on the module workFlow Configuration. 
	 * 			by using HomeLoanDetailDAO's update method 
	 * 3) Audit the record in to AuditHeader and AdtLMTHomeLoanDetail by using 
	 * 		auditHeaderDAO.addAudit(auditHeader)
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
		String tableType = "";
		HomeLoanDetail homeLoanDetail = (HomeLoanDetail) auditHeader.getAuditDetail().getModelData();

		if (homeLoanDetail.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (homeLoanDetail.isNew()) {
			homeLoanDetail.setId(getHomeLoanDetailDAO().save(homeLoanDetail,tableType));
			auditHeader.getAuditDetail().setModelData(homeLoanDetail);
			auditHeader.setAuditReference(homeLoanDetail.getId());
		} else {
			getHomeLoanDetailDAO().update(homeLoanDetail, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 
	 * 1) Do the Business validation by using businessValidation(auditHeader) method 
	 * 		if there is any error or warning message then return the auditHeader. 
	 * 2) delete Record for the DB table LMTHomeLoanDetail by using HomeLoanDetailDAO's 
	 * 		delete method with type as Blank 
	 * 3) Audit the record in to AuditHeader and AdtLMTHomeLoanDetail by using 
	 * 		auditHeaderDAO.addAudit(auditHeader)
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

		HomeLoanDetail homeLoanDetail = (HomeLoanDetail) auditHeader.getAuditDetail().getModelData();
		getHomeLoanDetailDAO().delete(homeLoanDetail, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getHomeLoanDetailById fetch the details by using HomeLoanDetailDAO's
	 * getHomeLoanDetailById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return HomeLoanDetail
	 */
	@Override
	public HomeLoanDetail getHomeLoanDetailById(String id) {
		return getHomeLoanDetailDAO().getHomeLoanDetailByID(id, "_View");
	}

	/**
	 * getApprovedHomeLoanDetailById fetch the details by using
	 * HomeLoanDetailDAO's getHomeLoanDetailById method . with parameter id and
	 * type as blank. it fetches the approved records from the
	 * LMTHomeLoanDetail.
	 * 
	 * @param id
	 *            (String)
	 * @return HomeLoanDetail
	 */

	public HomeLoanDetail getApprovedHomeLoanDetailById(String id) {
		return getHomeLoanDetailDAO().getHomeLoanDetailByID(id, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param HomeLoanDetail
	 *            (homeLoanDetail)
	 * @return homeLoanDetail
	 */
	@Override
	public HomeLoanDetail refresh(HomeLoanDetail homeLoanDetail) {
		logger.debug("Entering");
		getHomeLoanDetailDAO().refresh(homeLoanDetail);
		getHomeLoanDetailDAO().initialize(homeLoanDetail);
		logger.debug("Leaving");
		return homeLoanDetail;
	}
	
	/**
	 * getHomeCostOfConstruction fetch the details by using HomeLoanDetailDAO's
	 * getHomeCostOfConstruction().
	 * 
	 * @return list of genderCode values
	 */
	@Override
	public List<LovFieldDetail> getHomeConstructionStage() {
		return getHomeLoanDetailDAO().getHomeConstructionStage();
	}

	/**
	 * doApprove method do the following steps. 
	 * 1) Do the Business validation by using businessValidation(auditHeader) method 
	 * 		if there is any error or warning message then return the auditHeader. 
	 * 2) based on the Record type do following actions 
	 * 		a) DELETE Delete the record from the main table by using 
	 * 			getHomeLoanDetailDAO().delete with parameters homeLoanDetail,"" 
	 * 		b) NEW Add new record in to main table by using getHomeLoanDetailDAO().save
	 * 			with parameters homeLoanDetail,"" 
	 * 		c) EDIT Update record in the main table by using 
	 * 			getHomeLoanDetailDAO().update with parameters homeLoanDetail,""
	 * 3) Delete the record from the workFlow table by using getHomeLoanDetailDAO().delete 
	 * 		with parameters homeLoanDetail,"_Temp" 
	 * 4) Audit the record in to AuditHeader and AdtLMTHomeLoanDetail by using
	 * 		auditHeaderDAO.addAudit(auditHeader) for Work flow 
	 * 5) Audit the record in to AuditHeader and AdtLMTHomeLoanDetail by using
	 * 		auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		HomeLoanDetail homeLoanDetail = new HomeLoanDetail();
		BeanUtils.copyProperties((HomeLoanDetail) auditHeader.getAuditDetail()
				.getModelData(), homeLoanDetail);

		if (homeLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getHomeLoanDetailDAO().delete(homeLoanDetail, "");
		} else {
			homeLoanDetail.setRoleCode("");
			homeLoanDetail.setNextRoleCode("");
			homeLoanDetail.setTaskId("");
			homeLoanDetail.setNextTaskId("");
			homeLoanDetail.setWorkflowId(0);

			if (homeLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				homeLoanDetail.setRecordType("");
				getHomeLoanDetailDAO().save(homeLoanDetail, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				homeLoanDetail.setRecordType("");
				getHomeLoanDetailDAO().update(homeLoanDetail, "");
			}
		}

		getHomeLoanDetailDAO().delete(homeLoanDetail, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(homeLoanDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 
	 * 1) Do the Business validation by using businessValidation(auditHeader) method 
	 * 		if there is any error or warning message then return the auditHeader. 
	 * 2) Delete the record from the workFlow table by using 
	 * 		getHomeLoanDetailDAO().delete with parameters homeLoanDetail,"_Temp" 
	 * 3) Audit the record in to AuditHeader and AdtLMTHomeLoanDetail by using 
	 * 		auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		HomeLoanDetail homeLoanDetail = (HomeLoanDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getHomeLoanDetailDAO().delete(homeLoanDetail, "_TEMP");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 
	 * 1) get the details from the auditHeader.
	 * 2) fetch the details from the tables 
	 * 3) Validate the Record based on the record details. 
	 * 4) Validate for any business validation. 
	 * 5) for any mismatch conditions Fetch the error details from
	 * 		getHomeLoanDetailDAO().getErrorDetail with Error ID and language as parameters. 
	 * 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader,String method) {
		logger.debug("Entering");
		auditHeader = getHomeLoanDetailValidation().homeLoanDetailValidation(auditHeader, method);
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
}