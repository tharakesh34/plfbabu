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
 * FileName    		:  SubSegmentServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.systemmasters.SubSegmentDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.SubSegment;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.SubSegmentService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>SubSegment</b>.<br>
 * 
 */
public class SubSegmentServiceImpl extends GenericService<SubSegment> implements	SubSegmentService {

	private static Logger logger = Logger.getLogger(SubSegmentServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private SubSegmentDAO subSegmentDAO;

	public SubSegmentServiceImpl() {
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

	public SubSegmentDAO getSubSegmentDAO() {
		return subSegmentDAO;
	}

	public void setSubSegmentDAO(SubSegmentDAO subSegmentDAO) {
		this.subSegmentDAO = subSegmentDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTSubSegments/BMTSubSegments_Temp by using SubSegmentDAO's save method
	 * b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using SubSegmentDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBMTSubSegments by using
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
		SubSegment subSegment = (SubSegment) auditHeader.getAuditDetail().getModelData();

		if (subSegment.isWorkflow()) {
			tableType = "_Temp";
		}

		if (subSegment.isNew()) {
			getSubSegmentDAO().save(subSegment,tableType);
			auditHeader.getAuditDetail().setModelData(subSegment);
			auditHeader.setAuditReference(subSegment.getSubSegmentCode()
					+PennantConstants.KEY_SEPERATOR+ subSegment.getSegmentCode());
		} else {
			getSubSegmentDAO().update(subSegment, tableType);
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTSubSegments by using SubSegmentDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBMTSubSegments by
	 * using auditHeaderDAO.addAudit(auditHeader)
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
		SubSegment subSegment = (SubSegment) auditHeader.getAuditDetail().getModelData();
		getSubSegmentDAO().delete(subSegment, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getSubSegmentById fetch the details by using SubSegmentDAO's
	 * getSubSegmentById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return SubSegment
	 */
	@Override
	public SubSegment getSubSegmentById(String id, String subSegmentCode) {
		return getSubSegmentDAO().getSubSegmentById(id, subSegmentCode, "_View");
	}

	/**
	 * getApprovedSubSegmentById fetch the details by using SubSegmentDAO's
	 * getSubSegmentById method . with parameter id and type as blank. it
	 * fetches the approved records from the BMTSubSegments.
	 * 
	 * @param id
	 *            (String)
	 * @return SubSegment
	 */
	public SubSegment getApprovedSubSegmentById(String id, String subSegmentCode) {
		return getSubSegmentDAO().getSubSegmentById(id, subSegmentCode, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getSubSegmentDAO().delete with parameters subSegment,"" b) NEW Add
	 * new record in to main table by using getSubSegmentDAO().save with
	 * parameters subSegment,"" c) EDIT Update record in the main table by using
	 * getSubSegmentDAO().update with parameters subSegment,"" 3) Delete the
	 * record from the workFlow table by using getSubSegmentDAO().delete with
	 * parameters subSegment,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTSubSegments by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTSubSegments by using
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

		SubSegment subSegment = new SubSegment();
		BeanUtils.copyProperties((SubSegment) auditHeader.getAuditDetail().getModelData(), subSegment);

		if (subSegment.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getSubSegmentDAO().delete(subSegment, "");

		} else {
			subSegment.setRoleCode("");
			subSegment.setNextRoleCode("");
			subSegment.setTaskId("");
			subSegment.setNextTaskId("");
			subSegment.setWorkflowId(0);

			if (subSegment.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				subSegment.setRecordType("");
				getSubSegmentDAO().save(subSegment, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				subSegment.setRecordType("");
				getSubSegmentDAO().update(subSegment, "");
			}
		}

		getSubSegmentDAO().delete(subSegment, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(subSegment);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getSubSegmentDAO().delete with parameters
	 * subSegment,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTSubSegments by using auditHeaderDAO.addAudit(auditHeader) for Work
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

		SubSegment subSegment = (SubSegment) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSubSegmentDAO().delete(subSegment, "_Temp");

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
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		SubSegment subSegment = (SubSegment) auditDetail.getModelData();
		SubSegment tempSubSegment = null;

		if (subSegment.isWorkflow()) {
			tempSubSegment = getSubSegmentDAO().getSubSegmentById(subSegment.getId(),subSegment.getSubSegmentCode(), "_Temp");
		}

		SubSegment befSubSegment = getSubSegmentDAO().getSubSegmentById(subSegment.getId(), subSegment.getSubSegmentCode(), "");
		SubSegment oldSubSegment = subSegment.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = subSegment.getSegmentCode();
		valueParm[1] = subSegment.getSubSegmentCode();

		errParm[0] = PennantJavaUtil.getLabel("label_SegmentCode") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_SubSegmentCode") + ":"+ valueParm[1];

		if (subSegment.isNew()) { // for New record or new record into work flow

			if (!subSegment.isWorkflow()) {// With out Work flow only new records
				if (befSubSegment != null) { // Record Already Exists in the table then error
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow
				if (subSegment.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befSubSegment != null || tempSubSegment != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befSubSegment == null || tempSubSegment != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!subSegment.isWorkflow()) { // With out Work flow for update and delete

				if (befSubSegment == null) { // if records not exists in the main table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002",errParm, null));
				} else {
					if (oldSubSegment != null
							&& !oldSubSegment.getLastMntOn().equals(befSubSegment.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003",errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004",errParm, null));
						}
					}
				}
			} else {
				if (tempSubSegment == null) { // if records not exists in the
					// Work flow table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}

				if (tempSubSegment != null
						&& oldSubSegment != null
						&& !oldSubSegment.getLastMntOn().equals(tempSubSegment.getLastMntOn())) {
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method))|| !subSegment.isWorkflow()) {
			auditDetail.setBefImage(befSubSegment);
		}
		logger.debug("Leaving");
		return auditDetail;
	}
}