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
 * FileName    		:  PenaltyServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.rmtmasters.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.PenaltyDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.Penalty;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.PenaltyService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>Penalty</b>.<br>
 * 
 */
public class PenaltyServiceImpl extends GenericService<Penalty> implements PenaltyService {

	private final static Logger logger = Logger
			.getLogger(PenaltyServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PenaltyDAO penaltyDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public PenaltyDAO getPenaltyDAO() {
		return penaltyDAO;
	}
	public void setPenaltyDAO(PenaltyDAO penaltyDAO) {
		this.penaltyDAO = penaltyDAO;
	}

	public Penalty getPenalty() {
		return getPenaltyDAO().getPenalty();
	}
	public Penalty getNewPenalty() {
		return getPenaltyDAO().getNewPenalty();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTPenalties/RMTPenalties_Temp by using PenaltyDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using PenaltyDAO's update method 3) Audit the record in
	 * to AuditHeader and AdtRMTPenalties by using
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
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		Penalty penalty = (Penalty) auditHeader.getAuditDetail().getModelData();

		if (penalty.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (penalty.isNew()) {
			penalty.setPenaltyType(getPenaltyDAO().save(penalty, tableType));
			auditHeader.getAuditDetail().setModelData(penalty);
			auditHeader.setAuditReference(String.valueOf(penalty
					.getPenaltyType()));
		} else {
			getPenaltyDAO().update(penalty, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTPenalties by using PenaltyDAO's delete method with type as Blank
	 * 3) Audit the record in to AuditHeader and AdtRMTPenalties by using
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
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		Penalty penalty = (Penalty) auditHeader.getAuditDetail().getModelData();
		getPenaltyDAO().delete(penalty, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getPenaltyById fetch the details by using PenaltyDAO's getPenaltyById
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Penalty
	 */
	@Override
	public Penalty getPenaltyById(String id) {
		return getPenaltyDAO().getPenaltyById(id, "_View");
	}

	/**
	 * getApprovedPenaltyById fetch the details by using PenaltyDAO's
	 * getPenaltyById method . with parameter id and type as blank. it fetches
	 * the approved records from the RMTPenalties.
	 * 
	 * @param id
	 *            (String)
	 * @return Penalty
	 */
	public Penalty getApprovedPenaltyById(String id) {
		return getPenaltyDAO().getPenaltyById(id, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param Penalty
	 *            (penalty)
	 * @return penalty
	 */
	@Override
	public Penalty refresh(Penalty penalty) {
		logger.debug("Entering");
		getPenaltyDAO().refresh(penalty);
		getPenaltyDAO().initialize(penalty);
		logger.debug("Leaving");
		return penalty;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getPenaltyDAO().delete with parameters penalty,"" b) NEW Add new
	 * record in to main table by using getPenaltyDAO().save with parameters
	 * penalty,"" c) EDIT Update record in the main table by using
	 * getPenaltyDAO().update with parameters penalty,"" 3) Delete the record
	 * from the workFlow table by using getPenaltyDAO().delete with parameters
	 * penalty,"_Temp" 4) Audit the record in to AuditHeader and AdtRMTPenalties
	 * by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtRMTPenalties by using
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
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		Penalty penalty = new Penalty();
		BeanUtils.copyProperties((Penalty) auditHeader.getAuditDetail()
				.getModelData(), penalty);

		if (penalty.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getPenaltyDAO().delete(penalty, "");
		} else {
			penalty.setRoleCode("");
			penalty.setNextRoleCode("");
			penalty.setTaskId("");
			penalty.setNextTaskId("");
			penalty.setWorkflowId(0);

			if (penalty.getRecordType()
					.equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				penalty.setRecordType("");
				getPenaltyDAO().save(penalty, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				penalty.setRecordType("");
				getPenaltyDAO().update(penalty, "");
			}
		}

		getPenaltyDAO().delete(penalty, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(penalty);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getPenaltyDAO().delete with parameters
	 * penalty,"_Temp" 3) Audit the record in to AuditHeader and AdtRMTPenalties
	 * by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		Penalty penalty = (Penalty) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getPenaltyDAO().delete(penalty, "_TEMP");

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
				auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getPenaltyDAO().getErrorDetail with Error ID and language as parameters.
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
		Penalty penalty = (Penalty) auditDetail.getModelData();
		Penalty tempPenalty = null;
		if (penalty.isWorkflow()) {
			tempPenalty = getPenaltyDAO().getPenaltyById(penalty.getId(),
					"_Temp");
		}
		Penalty befPenalty = getPenaltyDAO()
				.getPenaltyById(penalty.getId(), "");

		Penalty oldPenalty = penalty.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm= new String[2];

		valueParm[0] = penalty.getPenaltyType();
		valueParm[1] = penalty.getPenaltyEffDate().toString();

		errParm[0] = PennantJavaUtil.getLabel("label_PenaltyType") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_PenaltyEffDate") + ":"+valueParm[1];

		if (penalty.isNew()) { // for New record or new record into work flow

			if (!penalty.isWorkflow()) {// With out Work flow only new records
				if (befPenalty != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}
			} else { // with work flow
				
				if (penalty.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befPenalty != null || tempPenalty != null) { // if records already exists in
												// the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befPenalty == null || tempPenalty != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!penalty.isWorkflow()) { // With out Work flow for update and delete 
				if (befPenalty == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{
					if (oldPenalty != null
							&& !oldPenalty.getLastMntOn().equals(
									befPenalty.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}
			} else {
				if (tempPenalty == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
				if (tempPenalty != null && oldPenalty != null
						&& !oldPenalty.getLastMntOn().equals(
								tempPenalty.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if (StringUtils.trimToEmpty(method).equals("doApprove")
				|| !penalty.isWorkflow()) {
			auditDetail.setBefImage(befPenalty);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}