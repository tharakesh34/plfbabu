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

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
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

/**
 * Service implementation for methods that depends on <b>SubSector</b>.<br>
 * 
 */
public class SubSectorServiceImpl extends GenericService<SubSector> implements SubSectorService {

	private static Logger logger = Logger.getLogger(SubSectorServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private SubSectorDAO subSectorDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	public SubSector getSubSector() {
		return getSubSectorDAO().getSubSector();
	}

	public SubSector getNewSubSector() {
		return getSubSectorDAO().getNewSubSector();
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
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		SubSector subSector = (SubSector) auditHeader.getAuditDetail().getModelData();
		if (subSector.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (subSector.isNew()) {
			getSubSectorDAO().save(subSector,tableType);
			auditHeader.getAuditDetail().setModelData(subSector);
			auditHeader.setAuditReference(String.valueOf(subSector.getSubSectorCode())
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

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		SubSector subSector = (SubSector) auditHeader.getAuditDetail().getModelData();
		getSubSectorDAO().delete(subSector, "");
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
	 * This method refresh the Record.
	 * 
	 * @param SubSector
	 *            (subSector)
	 * @return subSector
	 */
	@Override
	public SubSector refresh(SubSector subSector) {
		logger.debug("Entering");
		getSubSectorDAO().refresh(subSector);
		getSubSectorDAO().initialize(subSector);
		logger.debug("Leaving");
		return subSector;
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

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		SubSector subSector = new SubSector();
		BeanUtils.copyProperties((SubSector) auditHeader.getAuditDetail().getModelData(), subSector);

		if (subSector.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getSubSectorDAO().delete(subSector, "");
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
				getSubSectorDAO().save(subSector, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				subSector.setRecordType("");
				getSubSectorDAO().update(subSector, "");
			}
		}

		getSubSectorDAO().delete(subSector, "_TEMP");
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

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		SubSector subSector = (SubSector) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSubSectorDAO().delete(subSector, "_TEMP");

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
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),auditHeader.getUsrLanguage(), method);
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
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());

		SubSector subSector = (SubSector) auditDetail.getModelData();
		SubSector tempSubSector = null;
		SubSector tempSubSectorCode = null;
		if (subSector.isWorkflow()) {
			tempSubSector = getSubSectorDAO().getSubSectorById(subSector.getId(), subSector.getSubSectorCode(), "_Temp");
			tempSubSectorCode =  getSubSectorDAO().getSubSectorBySubSectorCode(subSector.getSubSectorCode(), "_Temp");
		}

		SubSector befSubSector = getSubSectorDAO().getSubSectorById(subSector.getId(), subSector.getSubSectorCode(), "");
		SubSector befsubSectorCode = getSubSectorDAO().getSubSectorBySubSectorCode(subSector.getSubSectorCode(), "");
		SubSector oldSubSector = subSector.getBefImage();
		
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		String[] errparm = new String[1];
		
		valueParm[0] = subSector.getSectorCode();
		valueParm[1] = subSector.getSubSectorCode();

		errParm[0] = PennantJavaUtil.getLabel("label_SectorCode") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_SubSectorCode") + ":"+ valueParm[1];
		errparm[0] = PennantJavaUtil.getLabel("label_SubSectorCode") + ":"+ valueParm[1];

		if (subSector.isNew()) { // for New record or new record into work flow

			if (!subSector.isWorkflow()) {// With out Work flow only new records
				if (befSubSector != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
				if (befsubSectorCode != null){
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001",errparm, null));
				}
			} else { // with work flow
				if (subSector.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befSubSector != null || tempSubSector != null) { // if  records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,null));
					}
					if (befsubSectorCode!= null || tempSubSectorCode != null) { // if  records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errparm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befSubSector == null || tempSubSector != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!subSector.isWorkflow()) { // With out Work flow for update and delete

				if (befSubSector == null) { // if records not exists in the main table
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002",errParm, null));
				} else {
					if (oldSubSector != null
							&& !oldSubSector.getLastMntOn().equals(befSubSector.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003",errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004",errParm, null));
						}
					}
				}
			} else {
				if (tempSubSector == null) { // if records not exists in the Work flow table
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}

				if (tempSubSector != null
						&& oldSubSector != null
						&& !oldSubSector.getLastMntOn().equals(tempSubSector.getLastMntOn())) {
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if (StringUtils.trimToEmpty(method).equals("doApprove")
				|| !subSector.isWorkflow()) {
			auditDetail.setBefImage(befSubSector);
		}
		logger.debug("Leaving");
		return auditDetail;
	}
}