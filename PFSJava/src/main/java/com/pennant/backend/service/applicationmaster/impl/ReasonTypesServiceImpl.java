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
 * FileName    		:  ReasonTypesServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-12-2017    														*
 *                                                                  						*
 * Modified Date    :  19-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-12-2017       PENNANT	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.ReasonCodeDAO;
import com.pennant.backend.dao.applicationmaster.ReasonTypesDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.ReasonTypes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.ReasonTypesService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>ReasonTypes</b>.<br>
 */
public class ReasonTypesServiceImpl extends GenericService<ReasonTypes> implements ReasonTypesService {
	private static final Logger logger = Logger.getLogger(ReasonTypesServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ReasonTypesDAO reasonTypesDAO;
	private ReasonCodeDAO reasonCodeDAO;


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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
	 * @return the reasonTypesDAO
	 */
	public ReasonTypesDAO getReasonTypesDAO() {
		return reasonTypesDAO;
	}

	/**
	 * @param reasonTypesDAO
	 *            the reasonTypesDAO to set
	 */
	public void setReasonTypesDAO(ReasonTypesDAO reasonTypesDAO) {
		this.reasonTypesDAO = reasonTypesDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * ReasonTypes/ReasonTypes_Temp by using ReasonTypesDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using ReasonTypesDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtReasonTypes by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);	
		
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ReasonTypes reasonTypes = (ReasonTypes) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (reasonTypes.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (reasonTypes.isNew()) {
			reasonTypes.setId(Long.parseLong(getReasonTypesDAO().save(reasonTypes,tableType)));
			auditHeader.getAuditDetail().setModelData(reasonTypes);
			auditHeader.setAuditReference(String.valueOf(reasonTypes.getId()));
		} else {
			getReasonTypesDAO().update(reasonTypes, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table ReasonTypes by using ReasonTypesDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtReasonTypes by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);
		
		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		
		ReasonTypes reasonTypes = (ReasonTypes) auditHeader.getAuditDetail().getModelData();
		getReasonTypesDAO().delete(reasonTypes, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getReasonTypes fetch the details by using ReasonTypesDAO's
	 * getReasonTypesById method.
	 * 
	 * @param id
	 *            id of the ReasonTypes.
	 * @return ReasonTypes
	 */
	@Override
	public ReasonTypes getReasonTypes(long id) {
		return getReasonTypesDAO().getReasonTypes(id, "_View");
	}

	/**
	 * getApprovedReasonTypesById fetch the details by using ReasonTypesDAO's
	 * getReasonTypesById method . with parameter id and type as blank. it
	 * fetches the approved records from the ReasonTypes.
	 * 
	 * @param id
	 *            id of the ReasonTypes. (String)
	 * @return ReasonTypes
	 */
	public ReasonTypes getApprovedReasonTypes(long id) {
		return getReasonTypesDAO().getReasonTypes(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getReasonTypesDAO().delete with parameters reasonTypes,"" b) NEW
	 * Add new record in to main table by using getReasonTypesDAO().save with
	 * parameters reasonTypes,"" c) EDIT Update record in the main table by
	 * using getReasonTypesDAO().update with parameters reasonTypes,"" 3) Delete
	 * the record from the workFlow table by using getReasonTypesDAO().delete
	 * with parameters reasonTypes,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtReasonTypes by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtReasonTypes by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		ReasonTypes reasonTypes = new ReasonTypes();
		BeanUtils.copyProperties((ReasonTypes) auditHeader.getAuditDetail().getModelData(), reasonTypes);

		getReasonTypesDAO().delete(reasonTypes, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(reasonTypes.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(reasonTypesDAO.getReasonTypes(reasonTypes.getId(), ""));
		}

		if (reasonTypes.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getReasonTypesDAO().delete(reasonTypes, TableType.MAIN_TAB);
		} else {
			reasonTypes.setRoleCode("");
			reasonTypes.setNextRoleCode("");
			reasonTypes.setTaskId("");
			reasonTypes.setNextTaskId("");
			reasonTypes.setWorkflowId(0);

			if (reasonTypes.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				reasonTypes.setRecordType("");
				getReasonTypesDAO().save(reasonTypes, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				reasonTypes.setRecordType("");
				getReasonTypesDAO().update(reasonTypes, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(reasonTypes);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

	

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getReasonTypesDAO().delete with parameters
	 * reasonTypes,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtReasonTypes by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
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

		ReasonTypes reasonTypes = (ReasonTypes) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getReasonTypesDAO().delete(reasonTypes, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
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
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getReasonTypesDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		ReasonTypes reasonTypes = (ReasonTypes) auditDetail.getModelData();

		// Check the unique keys.
		if (reasonTypes.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(reasonTypes.getRecordType())
				&& reasonTypesDAO.isDuplicateKey(reasonTypes.getCode().trim(),
						reasonTypes.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_Code") + ": " + reasonTypes.getCode();

			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
		// If ReasonTypes Code is already utilized in ReasonCode
				if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, reasonTypes.getRecordType())) {
					boolean workflowExists = getReasonCodeDAO().isreasonTypeIDExists(reasonTypes.getId());
					if (workflowExists) {

						String[] parameters = new String[2];
						parameters[0] = PennantJavaUtil.getLabel("label_Code") + ": " + reasonTypes.getCode();

						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41006", parameters, null));
					}
				}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public ReasonCodeDAO getReasonCodeDAO() {
		return reasonCodeDAO;
	}

	public void setReasonCodeDAO(ReasonCodeDAO reasonCodeDAO) {
		this.reasonCodeDAO = reasonCodeDAO;
	}

	
}