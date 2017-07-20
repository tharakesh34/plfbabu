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
 * FileName    		:  SubSectorServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.systemmasters.SubSectorDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.SubSectorService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>SubSector</b>.<br>
 * 
 */
public class SubSectorServiceImpl extends GenericService<SubSector> implements SubSectorService {

	private static Logger logger = Logger.getLogger(SubSectorServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private SubSectorDAO subSectorDAO;

	public SubSectorServiceImpl() {
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

	public SubSectorDAO getSubSectorDAO() {
		return subSectorDAO;
	}

	public void setSubSectorDAO(SubSectorDAO subSectorDAO) {
		this.subSectorDAO = subSectorDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTSubSectors/BMTSubSectors_Temp by using SubSectorDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using SubSectorDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBMTSubSectors by using
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
		
		SubSector subSector = (SubSector) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;
		if (subSector.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (subSector.isNew()) {
			getSubSectorDAO().save(subSector,tableType);
			auditHeader.getAuditDetail().setModelData(subSector);
			auditHeader.setAuditReference(subSector.getSubSectorCode()
					+PennantConstants.KEY_SEPERATOR+ subSector.getSectorCode());
		} else {
			getSubSectorDAO().update(subSector, tableType);
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTSubSectors by using SubSectorDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBMTSubSectors by using
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
		SubSector subSector = (SubSector) auditHeader.getAuditDetail().getModelData();
		getSubSectorDAO().delete(subSector, TableType.MAIN_TAB);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getSubSectorById fetch the details by using SubSectorDAO's
	 * getSubSectorById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return SubSector
	 */
	@Override
	public SubSector getSubSectorById(String id, String subSectorCode) {
		return getSubSectorDAO().getSubSectorById(id, subSectorCode, "_View");
	}

	/**
	 * getApprovedSubSectorById fetch the details by using SubSectorDAO's
	 * getSubSectorById method . with parameter id and type as blank. it fetches
	 * the approved records from the BMTSubSectors.
	 * 
	 * @param id
	 *            (String)
	 * @return SubSector
	 */
	public SubSector getApprovedSubSectorById(String id, String subSectorCode) {
		return getSubSectorDAO().getSubSectorById(id, subSectorCode, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getSubSectorDAO().delete with parameters subSector,"" b) NEW Add
	 * new record in to main table by using getSubSectorDAO().save with
	 * parameters subSector,"" c) EDIT Update record in the main table by using
	 * getSubSectorDAO().update with parameters subSector,"" 3) Delete the
	 * record from the workFlow table by using getSubSectorDAO().delete with
	 * parameters subSector,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTSubSectors by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTSubSectors by using
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
		SubSector subSector = new SubSector();
		BeanUtils.copyProperties((SubSector) auditHeader.getAuditDetail().getModelData(), subSector);
		getSubSectorDAO().delete(subSector, TableType.TEMP_TAB);
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(subSector.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(subSectorDAO.getSubSectorById(subSector.getSectorCode(), subSector.getSubSectorCode(), ""));
		}

		if (subSector.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getSubSectorDAO().delete(subSector, TableType.MAIN_TAB);
		} else {
			subSector.setRoleCode("");
			subSector.setNextRoleCode("");
			subSector.setTaskId("");
			subSector.setNextTaskId("");
			subSector.setWorkflowId(0);

			if (subSector.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				subSector.setRecordType("");
				getSubSectorDAO().save(subSector, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				subSector.setRecordType("");
				getSubSectorDAO().update(subSector, TableType.MAIN_TAB);
			}
		}

		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(subSector);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getSubSectorDAO().delete with parameters
	 * subSector,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTSubSectors by using auditHeaderDAO.addAudit(auditHeader) for Work
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
		SubSector subSector = (SubSector) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSubSectorDAO().delete(subSector, TableType.TEMP_TAB);

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
	 * getAcademicDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		SubSector subSector = (SubSector) auditDetail.getModelData();

		// Check the unique keys.
		if (subSector.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(subSector.getRecordType())
				&& subSectorDAO.isDuplicateKey(subSector.getSectorCode(), subSector.getSubSectorCode(),
						subSector.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_SectorCode") + ": " + subSector.getSectorCode();
			parameters[1] = PennantJavaUtil.getLabel("label_SubSectorCode") + ": " + subSector.getSubSectorCode();

			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}
}