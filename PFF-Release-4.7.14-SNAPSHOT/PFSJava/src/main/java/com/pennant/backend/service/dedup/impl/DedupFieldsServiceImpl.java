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
 * FileName    		:  DedupFieldsServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-08-2011    														*
 *                                                                  						*
 * Modified Date    :  23-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.dedup.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.dedup.DedupFieldsDAO;
import com.pennant.backend.model.BuilderTable;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.dedup.DedupFields;
import com.pennant.backend.service.dedup.DedupFieldsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>DedupFields</b>.<br>
 * 
 */
public class DedupFieldsServiceImpl implements DedupFieldsService {
	private static final Logger logger = Logger.getLogger(DedupFieldsServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private DedupFieldsDAO dedupFieldsDAO;

	public DedupFieldsServiceImpl() {
		super();
	}
	
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public DedupFieldsDAO getDedupFieldsDAO() {
		return dedupFieldsDAO;
	}
	public void setDedupFieldsDAO(DedupFieldsDAO dedupFieldsDAO) {
		this.dedupFieldsDAO = dedupFieldsDAO;
	}


	@Override
	public DedupFields getDedupFields() {
		return getDedupFieldsDAO().getDedupFields();
	}

	@Override
	public DedupFields getNewDedupFields() {
		return getDedupFieldsDAO().getNewDedupFields();
	}


	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * DedupFields/DedupFields_Temp by using DedupFieldsDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using DedupFieldsDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtDedupFields by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");	

		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!nextProcess(auditHeader)){
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		DedupFields dedupFields = (DedupFields) auditHeader.getAuditDetail().getModelData();

		if (dedupFields.isWorkflow()) {
			tableType="_Temp";
		}

		if (dedupFields.isNew()) {
			getDedupFieldsDAO().save(dedupFields,tableType);
		}else{
			getDedupFieldsDAO().update(dedupFields,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table DedupFields by using DedupFieldsDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtDedupFields by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"delete");
		if (!nextProcess(auditHeader)){
			logger.debug("Leaving");
			return auditHeader;
		}

		DedupFields dedupFields = (DedupFields) auditHeader.getAuditDetail().getModelData();
		getDedupFieldsDAO().delete(dedupFields,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getDedupFieldsById fetch the details by using DedupFieldsDAO's
	 * getDedupFieldsById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return DedupFields
	 */
	@Override
	public DedupFields getDedupFieldsById(String id) {
		return getDedupFieldsDAO().getDedupFieldsByID(id,"_View");
	}

	/**
	 * getApprovedDedupFieldsById fetch the details by using DedupFieldsDAO's
	 * getDedupFieldsById method . with parameter id and type as blank. it
	 * fetches the approved records from the DedupFields.
	 * 
	 * @param id
	 *            (String)
	 * @return DedupFields
	 */
	public DedupFields getApprovedDedupFieldsById(String id) {
		return getDedupFieldsDAO().getDedupFieldsByID(id,"");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getDedupFieldsDAO().delete with parameters dedupFields,"" b) NEW
	 * Add new record in to main table by using getDedupFieldsDAO().save with
	 * parameters dedupFields,"" c) EDIT Update record in the main table by
	 * using getDedupFieldsDAO().update with parameters dedupFields,"" 3) Delete
	 * the record from the workFlow table by using getDedupFieldsDAO().delete
	 * with parameters dedupFields,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtDedupFields by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtDedupFields by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!nextProcess(auditHeader)){
			return auditHeader;
		}

		DedupFields dedupFields = new DedupFields();
		BeanUtils.copyProperties((DedupFields) auditHeader.getAuditDetail().getModelData(), dedupFields);

		if (dedupFields.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getDedupFieldsDAO().delete(dedupFields,"");

		} else {
			dedupFields.setRoleCode("");
			dedupFields.setNextRoleCode("");
			dedupFields.setTaskId("");
			dedupFields.setNextTaskId("");
			dedupFields.setWorkflowId(0);

			if (dedupFields.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				dedupFields.setRecordType("");
				getDedupFieldsDAO().save(dedupFields,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				dedupFields.setRecordType("");
				getDedupFieldsDAO().update(dedupFields,"");
			}
		}

		getDedupFieldsDAO().delete(dedupFields,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(dedupFields);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getDedupFieldsDAO().delete with parameters
	 * dedupFields,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtDedupFields by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"doReject");
		if (!nextProcess(auditHeader)){
			logger.debug("Leaving");
			return auditHeader;
		}

		DedupFields dedupFields = (DedupFields) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getDedupFieldsDAO().delete(dedupFields,"_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation. 5) for any mismatch conditions Fetch the error details from
	 * getDedupFieldsDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getDedupFieldsDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");

		DedupFields dedupFields= (DedupFields) auditDetail.getModelData();

		DedupFields tempDedupFields= null;
		if (dedupFields.isWorkflow()) {
			tempDedupFields = getDedupFieldsDAO().getDedupFieldsByID(
					dedupFields.getId(), "_Temp");
		}
		DedupFields befDedupFields = getDedupFieldsDAO().getDedupFieldsByID(
				dedupFields.getId(), "");

		DedupFields oldDedupFields = dedupFields.getBefImage();

		String[] errParm = new String[2];
		errParm[0] = PennantJavaUtil.getLabel("label_FieldName");
		errParm[1] = dedupFields.getId();

		if (dedupFields.isNew()) { // for New record or new record into work flow

			if (!dedupFields.isWorkflow()) {// With out Work flow only new records
				if (befDedupFields != null) { // Record Already Exists in the
												// table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
					
				}
			} else { // with work flow
				if (dedupFields.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befDedupFields != null) { // if records already exists
													// in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befDedupFields == null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
				if (tempDedupFields != null) { // if records already exists in
												// the Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!dedupFields.isWorkflow()) { // With out Work flow for update and delete

				if (befDedupFields == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,null));

				} else {
					if (oldDedupFields != null
							&& !oldDedupFields.getLastMntOn().equals(
									befDedupFields.getLastMntOn())) {
						if (StringUtils.trimToEmpty(
								auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004",errParm,null));

						}
					}
				}
			} else {

				if (tempDedupFields == null) { // if records not exists in the
												// Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));

				}

				if (tempDedupFields != null && oldDedupFields != null
						&& !oldDedupFields.getLastMntOn().equals(
								tempDedupFields.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));

				}
			}
		}

		if ("doApprove".equals(StringUtils.trimToEmpty(method))
				|| !dedupFields.isWorkflow()) {
			dedupFields.setBefImage(befDedupFields);
		}

		return auditDetail;
	}


	/**
	 * nextProcess method do the following steps. if errorMessage List or
	 * OverideMessage size is more than 0 then return False else return true.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return boolean
	 */

	private boolean nextProcess(AuditHeader auditHeader){

		if (auditHeader.getErrorMessage()!=null  && auditHeader.getErrorMessage().size()>0){
			return false;
		}
		if (auditHeader.getOverideMessage()!=null && auditHeader.getOverideMessage().size()>0 && !auditHeader.isOveride()){
			return false;
		}
		return true; 
	}

	
	@Override
	public List<BuilderTable> getFieldList(String queryModule) {
		return dedupFieldsDAO.getFieldList(queryModule);
	}


}