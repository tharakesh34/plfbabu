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
 * FileName    		:  FinSuspHoldServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.financemanagement.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.financemanagement.FinSuspHoldDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.financemanagement.FinSuspHold;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.financemanagement.FinSuspHoldService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on
 * <b>FinSuspHold</b>.<br>
 * 
 */
public class FinSuspHoldServiceImpl extends
		GenericService<FinSuspHold> implements FinSuspHoldService {

	private static Logger logger = Logger.getLogger(FinSuspHoldServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinSuspHoldDAO finSuspHoldDAO;

	public FinSuspHoldServiceImpl() {
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

	public FinSuspHoldDAO getFinSuspHoldDAO() {
		return finSuspHoldDAO;
	}

	public void setFinSuspHoldDAO(
			FinSuspHoldDAO finSuspHoldDAO) {
		this.finSuspHoldDAO = finSuspHoldDAO;
	}

	public FinSuspHold getFinSuspHold() {
		return getFinSuspHoldDAO().getFinSuspHold();
	}

	public FinSuspHold getNewFinSuspHold() {
		return getFinSuspHoldDAO().getNewFinSuspHold();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTFinSuspHold/BMTFinSuspHold_Temp by using
	 * FinSuspHoldDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using
	 * FinSuspHoldDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtBMTFinSuspHold by using
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
		FinSuspHold finSuspHold = (FinSuspHold) auditHeader.getAuditDetail().getModelData();

		if (finSuspHold.isWorkflow()) {
			tableType = "_Temp";
		}

		if (finSuspHold.isNew()) {
			finSuspHold.setId(getFinSuspHoldDAO().save(finSuspHold, tableType));
			auditHeader.getAuditDetail().setModelData(finSuspHold);
			auditHeader.setAuditReference(Long.toString(finSuspHold.getId()));
		} else {
			getFinSuspHoldDAO().update(finSuspHold,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTFinSuspHold by using FinSuspHoldDAO's
	 * delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and AdtBMTFinSuspHold by using
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
		FinSuspHold finSuspHold = (FinSuspHold) auditHeader.getAuditDetail().getModelData();
		getFinSuspHoldDAO().delete(finSuspHold, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getFinSuspHoldById fetch the details by using
	 * FinSuspHoldDAO's getFinSuspHoldById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinSuspHold
	 */
	@Override
	public FinSuspHold getFinSuspHoldById(long id) {
		return getFinSuspHoldDAO().getFinSuspHoldById(id,	"_View");
	}

	/**
	 * getApprovedFinSuspHoldById fetch the details by using
	 * FinSuspHoldDAO's getFinSuspHoldById method . with
	 * parameter id and type as blank. it fetches the approved records from the
	 * BMTFinSuspHold.
	 * 
	 * @param id
	 *            (String)
	 * @return FinSuspHold
	 */

	public FinSuspHold getApprovedFinSuspHoldById(long id) {
		return getFinSuspHoldDAO().getFinSuspHoldById(id,	"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getFinSuspHoldDAO().delete with parameters
	 * finSuspHold,"" b) NEW Add new record in to main table by using
	 * getFinSuspHoldDAO().save with parameters
	 * finSuspHold,"" c) EDIT Update record in the main table by
	 * using getFinSuspHoldDAO().update with parameters
	 * finSuspHold,"" 3) Delete the record from the workFlow table by
	 * using getFinSuspHoldDAO().delete with parameters
	 * finSuspHold,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTFinSuspHold by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtBMTFinSuspHold by using
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
		FinSuspHold finSuspHold = new FinSuspHold();
		BeanUtils.copyProperties((FinSuspHold) auditHeader.getAuditDetail().getModelData(), finSuspHold);

		if (finSuspHold.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getFinSuspHoldDAO().delete(finSuspHold, "");
		} else {
			finSuspHold.setRoleCode("");
			finSuspHold.setNextRoleCode("");
			finSuspHold.setTaskId("");
			finSuspHold.setNextTaskId("");
			finSuspHold.setWorkflowId(0);

			if (finSuspHold.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				finSuspHold.setRecordType("");
				getFinSuspHoldDAO().save(finSuspHold, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				finSuspHold.setRecordType("");
				getFinSuspHoldDAO().update(finSuspHold,"");
			}
		}

		getFinSuspHoldDAO().delete(finSuspHold, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(finSuspHold);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getFinSuspHoldDAO().delete with
	 * parameters finSuspHold,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtBMTFinSuspHold by using
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
		FinSuspHold finSuspHold = (FinSuspHold) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinSuspHoldDAO().delete(finSuspHold, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation. 5) for any mismatch conditions Fetch the error details from
	 * getFinSuspHoldDAO().getErrorDetail with Error ID and language
	 * as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader,	String method) {
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
	 * getFinSuspHoldDAO().getErrorDetail with Error ID and language
	 * as parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,	String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		FinSuspHold finSuspHold = (FinSuspHold) auditDetail.getModelData();
		FinSuspHold tempFinSuspHold = null;

		if (finSuspHold.isWorkflow()) {
			tempFinSuspHold = getFinSuspHoldDAO().getFinSuspHoldById(finSuspHold.getId(), "_Temp");
		}

		FinSuspHold befFinSuspHold = getFinSuspHoldDAO().getFinSuspHoldById(finSuspHold.getId(),	"");
		FinSuspHold oldFinSuspHold = finSuspHold.getBefImage();

		String[] errParm = {getValidationMsg(finSuspHold)};

		if (finSuspHold.isNew()) { // for New record or new record
			// into work flow

			if (!finSuspHold.isWorkflow()) {// With out Work flow
				// only new records
				if (befFinSuspHold != null) { // Record Already
					// Exists in the
					// table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow

				if (finSuspHold.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befFinSuspHold != null || tempFinSuspHold != null) { // if
						  						// records already exists
							 					// in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befFinSuspHold == null || tempFinSuspHold != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!finSuspHold.isWorkflow()) { // With out Work flow
				// for update and delete

				if (befFinSuspHold == null) { // if records not
					// exists in the
					// main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002",errParm, null));
				} else {
					if (oldFinSuspHold != null
							&& !oldFinSuspHold.getLastMntOn().equals(befFinSuspHold.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003",errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004",errParm, null));
						}
					}
				}

			} else {

				if (tempFinSuspHold == null) { // if records not
					// exists in the
					// Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}

				if (tempFinSuspHold != null && oldFinSuspHold != null
						&& !oldFinSuspHold.getLastMntOn().equals(tempFinSuspHold.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
			}
		}
		
		FinSuspHold finSuspHoldTemp = getFinSuspHoldDAO().getFinSuspHoldByDetails(finSuspHold, "_View");
		if(finSuspHoldTemp != null && finSuspHoldTemp.getId() != finSuspHold.getId()){
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014",new String[]{getValidationMsg(finSuspHold)}, null));
		}
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finSuspHold.isWorkflow()) {
			auditDetail.setBefImage(befFinSuspHold);
		}
		logger.debug("Leaving");
		return auditDetail;
	}
	
	private String getValidationMsg(FinSuspHold finSuspHold){
		logger.debug("Entering");
		String errMsg = "";
		if(StringUtils.isNotEmpty(finSuspHold.getProduct())){
			errMsg = Labels.getLabel("label_FinSuspHold_Product")+" : "+ finSuspHold.getProduct();
		}
		if(StringUtils.isNotEmpty(finSuspHold.getFinType())){
			if(StringUtils.isEmpty(errMsg)){
				errMsg = Labels.getLabel("label_FinSuspHold_FinType")+" : "+ finSuspHold.getFinType();
			}else{
				errMsg = errMsg +","+Labels.getLabel("label_FinSuspHold_FinType")+" : "+ finSuspHold.getFinType();
			}
		}
		if(StringUtils.isNotEmpty(finSuspHold.getFinReference())){
			if(StringUtils.isEmpty(errMsg)){
				errMsg = Labels.getLabel("label_FinSuspHold_FinReference")+" : "+ finSuspHold.getFinReference();
			}else{
				errMsg = errMsg +","+Labels.getLabel("label_FinSuspHold_FinReference")+" : "+ finSuspHold.getFinReference();
			}
		}
		if(StringUtils.isNotEmpty(finSuspHold.getCustCIF())){
			if(StringUtils.isEmpty(errMsg)){
				errMsg = Labels.getLabel("label_FinSuspHold_CustCIF")+" : "+ finSuspHold.getCustCIF();
			}else{
				errMsg = errMsg +","+Labels.getLabel("label_FinSuspHold_CustCIF")+" : "+ finSuspHold.getCustCIF();
			}
		}
		logger.debug("Leaving");
		return errMsg;
	}
	

}