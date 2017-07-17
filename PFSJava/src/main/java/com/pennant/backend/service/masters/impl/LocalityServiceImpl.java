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
 * FileName    		:  LocalityServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-05-2017    														*
 *                                                                  						*
 * Modified Date    :  22-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-05-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.masters.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.masters.LocalityDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.masters.Locality;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.masters.LocalityService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>Locality</b>.<br>
 */
public class LocalityServiceImpl extends GenericService<Locality> implements LocalityService {
	private static final Logger logger = Logger.getLogger(LocalityServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private LocalityDAO localityDAO;

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
	 * @return the localityDAO
	 */
	public LocalityDAO getLocalityDAO() {
		return localityDAO;
	}

	/**
	 * @param localityDAO
	 *            the localityDAO to set
	 */
	public void setLocalityDAO(LocalityDAO localityDAO) {
		this.localityDAO = localityDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table Locality/Locality_Temp by using
	 * LocalityDAO's save method b) Update the Record in the table. based on the module workFlow Configuration. by using
	 * LocalityDAO's update method 3) Audit the record in to AuditHeader and AdtLocality by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		Locality locality = (Locality) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (locality.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (locality.isNew()) {
			locality.setId(Long.parseLong(getLocalityDAO().save(locality, tableType)));
			auditHeader.getAuditDetail().setModelData(locality);
			auditHeader.setAuditReference(String.valueOf(locality.getId()));
		} else {
			getLocalityDAO().update(locality, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * Locality by using LocalityDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtLocality by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		Locality locality = (Locality) auditHeader.getAuditDetail().getModelData();
		getLocalityDAO().delete(locality, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getLocality fetch the details by using LocalityDAO's getLocalityById method.
	 * 
	 * @param id
	 *            id of the Locality.
	 * @return Locality
	 */
	@Override
	public Locality getLocality(long id) {
		return getLocalityDAO().getLocality(id, "_View");
	}

	/**
	 * getApprovedLocalityById fetch the details by using LocalityDAO's getLocalityById method . with parameter id and
	 * type as blank. it fetches the approved records from the Locality.
	 * 
	 * @param id
	 *            id of the Locality. (String)
	 * @return Locality
	 */
	public Locality getApprovedLocality(long id) {
		return getLocalityDAO().getLocality(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getLocalityDAO().delete with
	 * parameters locality,"" b) NEW Add new record in to main table by using getLocalityDAO().save with parameters
	 * locality,"" c) EDIT Update record in the main table by using getLocalityDAO().update with parameters locality,""
	 * 3) Delete the record from the workFlow table by using getLocalityDAO().delete with parameters locality,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtLocality by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5)
	 * Audit the record in to AuditHeader and AdtLocality by using auditHeaderDAO.addAudit(auditHeader) based on the
	 * transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		Locality locality = new Locality();
		BeanUtils.copyProperties((Locality) auditHeader.getAuditDetail().getModelData(), locality);

		getLocalityDAO().delete(locality, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(locality.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(localityDAO.getLocality(locality.getId(), ""));
		}

		if (locality.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getLocalityDAO().delete(locality, TableType.MAIN_TAB);
		} else {
			locality.setRoleCode("");
			locality.setNextRoleCode("");
			locality.setTaskId("");
			locality.setNextTaskId("");
			locality.setWorkflowId(0);

			if (locality.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				locality.setRecordType("");
				getLocalityDAO().save(locality, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				locality.setRecordType("");
				getLocalityDAO().update(locality, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(locality);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getLocalityDAO().delete with parameters locality,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtLocality by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		Locality locality = (Locality) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getLocalityDAO().delete(locality, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getLocalityDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}