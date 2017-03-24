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
 * FileName    		:  RelationshipOfficerServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.RelationshipOfficerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.RelationshipOfficerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on
 * <b>RelationshipOfficer</b>.<br>
 * 
 */
public class RelationshipOfficerServiceImpl extends	GenericService<RelationshipOfficer> implements
										RelationshipOfficerService {
	private final static Logger logger = Logger.getLogger(RelationshipOfficerServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private RelationshipOfficerDAO relationshipOfficerDAO;

	public RelationshipOfficerServiceImpl() {
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

	public RelationshipOfficerDAO getRelationshipOfficerDAO() {
		return relationshipOfficerDAO;
	}

	public void setRelationshipOfficerDAO(
			RelationshipOfficerDAO relationshipOfficerDAO) {
		this.relationshipOfficerDAO = relationshipOfficerDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RelationshipOfficers/RelationshipOfficers_Temp by using
	 * RelationshipOfficerDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using
	 * RelationshipOfficerDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtRelationshipOfficers by using
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
		RelationshipOfficer relationshipOfficer = (RelationshipOfficer) auditHeader
				.getAuditDetail().getModelData();

		if (relationshipOfficer.isWorkflow()) {
			tableType = "_Temp";
		}

		if (relationshipOfficer.isNew()) {
			getRelationshipOfficerDAO().save(relationshipOfficer, tableType);
		} else {
			getRelationshipOfficerDAO().update(relationshipOfficer, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RelationshipOfficers by using RelationshipOfficerDAO's delete
	 * method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtRelationshipOfficers by using auditHeaderDAO.addAudit(auditHeader)
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

		RelationshipOfficer relationshipOfficer = (RelationshipOfficer) auditHeader
				.getAuditDetail().getModelData();
		getRelationshipOfficerDAO().delete(relationshipOfficer, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getRelationshipOfficerById fetch the details by using
	 * RelationshipOfficerDAO's getRelationshipOfficerById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return RelationshipOfficer
	 */

	@Override
	public RelationshipOfficer getRelationshipOfficerById(String id) {
		return getRelationshipOfficerDAO().getRelationshipOfficerById(id,
				"_View");
	}

	/**
	 * getApprovedRelationshipOfficerById fetch the details by using
	 * RelationshipOfficerDAO's getRelationshipOfficerById method . with
	 * parameter id and type as blank. it fetches the approved records from the
	 * RelationshipOfficers.
	 * 
	 * @param id
	 *            (String)
	 * @return RelationshipOfficer
	 */

	public RelationshipOfficer getApprovedRelationshipOfficerById(String id) {
		return getRelationshipOfficerDAO().getRelationshipOfficerById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getRelationshipOfficerDAO().delete with parameters
	 * relationshipOfficer,"" b) NEW Add new record in to main table by using
	 * getRelationshipOfficerDAO().save with parameters relationshipOfficer,""
	 * c) EDIT Update record in the main table by using
	 * getRelationshipOfficerDAO().update with parameters relationshipOfficer,""
	 * 3) Delete the record from the workFlow table by using
	 * getRelationshipOfficerDAO().delete with parameters
	 * relationshipOfficer,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtRelationshipOfficers by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and
	 * AdtRelationshipOfficers by using auditHeaderDAO.addAudit(auditHeader)
	 * based on the transaction Type.
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
			return auditHeader;
		}

		RelationshipOfficer relationshipOfficer = new RelationshipOfficer();
		BeanUtils.copyProperties((RelationshipOfficer) auditHeader
				.getAuditDetail().getModelData(), relationshipOfficer);

		if (relationshipOfficer.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getRelationshipOfficerDAO().delete(relationshipOfficer, "");

		} else {
			relationshipOfficer.setRoleCode("");
			relationshipOfficer.setNextRoleCode("");
			relationshipOfficer.setTaskId("");
			relationshipOfficer.setNextTaskId("");
			relationshipOfficer.setWorkflowId(0);

			if (relationshipOfficer.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				relationshipOfficer.setRecordType("");
				getRelationshipOfficerDAO().save(relationshipOfficer, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				relationshipOfficer.setRecordType("");
				getRelationshipOfficerDAO().update(relationshipOfficer, "");
			}
		}

		getRelationshipOfficerDAO().delete(relationshipOfficer, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(relationshipOfficer);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getRelationshipOfficerDAO().delete with
	 * parameters relationshipOfficer,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtRelationshipOfficers by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		RelationshipOfficer relationshipOfficer = (RelationshipOfficer) auditHeader
				.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getRelationshipOfficerDAO().delete(relationshipOfficer, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation. 5) for any mismatch conditions Fetch the error details from
	 * getRelationshipOfficerDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader,
			String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getRelationshipOfficerDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
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
		
		RelationshipOfficer relationshipOfficer = (RelationshipOfficer) auditDetail
				.getModelData();
		RelationshipOfficer tempRelationshipOfficer = null;
		
		if (relationshipOfficer.isWorkflow()) {
			tempRelationshipOfficer = getRelationshipOfficerDAO()
					.getRelationshipOfficerById(relationshipOfficer.getId(),
							"_Temp");
		}
		
		RelationshipOfficer befRelationshipOfficer = getRelationshipOfficerDAO()
				.getRelationshipOfficerById(relationshipOfficer.getId(), "");
		RelationshipOfficer oldRelationshipOfficer = relationshipOfficer
				.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		
		valueParm[0] = relationshipOfficer.getROfficerCode();
		errParm[0] = PennantJavaUtil.getLabel("label_ROfficerCode") + ":"
				+ valueParm[0];

		if (relationshipOfficer.isNew()) { // for New record or new record into
											// work flow

			if (!relationshipOfficer.isWorkflow()) {// With out Work flow only
													// new records
				if (befRelationshipOfficer != null) { // Record Already Exists
														// in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD,
									"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (relationshipOfficer.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befRelationshipOfficer != null
							|| tempRelationshipOfficer != null) { // if records already 
																	// exists in main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD,
										"41001", errParm, valueParm),
								usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befRelationshipOfficer == null
							|| tempRelationshipOfficer != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD,
										"41005", errParm, valueParm),
								usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!relationshipOfficer.isWorkflow()) { // With out Work flow for
														// update and delete

				if (befRelationshipOfficer == null) { // if records not exists
														// in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD,
									"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldRelationshipOfficer != null
							&& !oldRelationshipOfficer.getLastMntOn().equals(
									befRelationshipOfficer.getLastMntOn())) {
						if (StringUtils.trimToEmpty(
								auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil
									.getErrorDetail(new ErrorDetails(
											PennantConstants.KEY_FIELD,
											"41003", errParm, valueParm),
											usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil
									.getErrorDetail(new ErrorDetails(
											PennantConstants.KEY_FIELD,
											"41004", errParm, valueParm),
											usrLanguage));
						}
					}
				}
			} else {

				if (tempRelationshipOfficer == null) { // if records not exists
														// in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD,
									"41005", errParm, valueParm), usrLanguage));
				}

				if (tempRelationshipOfficer != null && oldRelationshipOfficer != null
						&& !oldRelationshipOfficer.getLastMntOn().equals(
								tempRelationshipOfficer.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD,
									"41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(
				auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method))
				|| !relationshipOfficer.isWorkflow()) {
			relationshipOfficer.setBefImage(befRelationshipOfficer);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}