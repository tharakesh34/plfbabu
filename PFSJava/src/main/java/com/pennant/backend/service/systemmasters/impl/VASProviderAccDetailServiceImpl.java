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
 * FileName    		:  VASProviderAccDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-09-2018    														*
 *                                                                  						*
 * Modified Date    :  24-09-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-09-2018       PENNANT	                 0.1                                            * 
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.VASProviderAccDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.VASProviderAccDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.VASProviderAccDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>VASProviderAccDetail</b>.<br>
 */
public class VASProviderAccDetailServiceImpl extends GenericService<VASProviderAccDetail>
		implements VASProviderAccDetailService {
	private static final Logger logger = LogManager.getLogger(VASProviderAccDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private VASProviderAccDetailDAO vASProviderAccDetailDAO;

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
	 * @return the vASProviderAccDetailDAO
	 */
	public VASProviderAccDetailDAO getVASProviderAccDetailDAO() {
		return vASProviderAccDetailDAO;
	}

	/**
	 * @param vASProviderAccDetailDAO
	 *            the vASProviderAccDetailDAO to set
	 */
	public void setVASProviderAccDetailDAO(VASProviderAccDetailDAO vASProviderAccDetailDAO) {
		this.vASProviderAccDetailDAO = vASProviderAccDetailDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * VASManufacturerAccDetail/VASManufacturerAccDetail_Temp by using VASManufacturerAccDetailDAO's save method b)
	 * Update the Record in the table. based on the module workFlow Configuration. by using
	 * VASManufacturerAccDetailDAO's update method 3) Audit the record in to AuditHeader and AdtVASManufacturerAccDetail
	 * by using auditHeaderDAO.addAudit(auditHeader)
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

		VASProviderAccDetail vASProviderAccDetail = (VASProviderAccDetail) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (vASProviderAccDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (vASProviderAccDetail.isNew()) {
			vASProviderAccDetail
					.setId(Long.parseLong(getVASProviderAccDetailDAO().save(vASProviderAccDetail, tableType)));
			auditHeader.getAuditDetail().setModelData(vASProviderAccDetail);
			auditHeader.setAuditReference(String.valueOf(vASProviderAccDetail.getId()));
		} else {
			getVASProviderAccDetailDAO().update(vASProviderAccDetail, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * VASManufacturerAccDetail by using VASManufacturerAccDetailDAO's delete method with type as Blank 3) Audit the
	 * record in to AuditHeader and AdtVASManufacturerAccDetail by using auditHeaderDAO.addAudit(auditHeader)
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

		VASProviderAccDetail vASProviderAccDetail = (VASProviderAccDetail) auditHeader.getAuditDetail().getModelData();
		getVASProviderAccDetailDAO().delete(vASProviderAccDetail, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getVASManufacturerAccDetail fetch the details by using VASManufacturerAccDetailDAO's
	 * getVASManufacturerAccDetailById method.
	 * 
	 * @param id
	 *            id of the VASProviderAccDetail.
	 * @return VASManufacturerAccDetail
	 */
	@Override
	public VASProviderAccDetail getVASProviderAccDetail(long id) {
		return getVASProviderAccDetailDAO().getVASProviderAccDetail(id, "_View");
	}

	/**
	 * getApprovedVASManufacturerAccDetailById fetch the details by using VASManufacturerAccDetailDAO's
	 * getVASManufacturerAccDetailById method . with parameter id and type as blank. it fetches the approved records
	 * from the VASManufacturerAccDetail.
	 * 
	 * @param id
	 *            id of the VASProviderAccDetail. (String)
	 * @return VASManufacturerAccDetail
	 */
	public VASProviderAccDetail getApprovedVASProviderAccDetail(long id) {
		return getVASProviderAccDetailDAO().getVASProviderAccDetail(id, "_AView");
	}

	@Override
	public VASProviderAccDetail getVASProviderAccDetByPRoviderId(long providerId, String entityCode, String tableType) {
		return getVASProviderAccDetailDAO().getVASProviderAccDetByPRoviderId(providerId, entityCode, tableType);
	}

	@Override
	public VASProviderAccDetail getVASProviderAccDetByPRoviderId(long providerId, String tableType) {
		return getVASProviderAccDetailDAO().getVASProviderAccDetByPRoviderId(providerId, tableType);
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getVASProviderAccDetailDAO().delete
	 * with parameters vASProviderAccDetail,"" b) NEW Add new record in to main table by using
	 * getVASProviderAccDetailDAO().save with parameters vASProviderAccDetail,"" c) EDIT Update record in the main table
	 * by using getVASProviderAccDetailDAO().update with parameters vASProviderAccDetail,"" 3) Delete the record from
	 * the workFlow table by using getVASProviderAccDetailDAO().delete with parameters vASProviderAccDetail,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtVASManufacturerAccDetail by using auditHeaderDAO.addAudit(auditHeader)
	 * for Work flow 5) Audit the record in to AuditHeader and AdtVASManufacturerAccDetail by using
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

		VASProviderAccDetail vASProviderAccDetail = new VASProviderAccDetail();
		BeanUtils.copyProperties((VASProviderAccDetail) auditHeader.getAuditDetail().getModelData(),
				vASProviderAccDetail);

		getVASProviderAccDetailDAO().delete(vASProviderAccDetail, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(vASProviderAccDetail.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(vASProviderAccDetailDAO.getVASProviderAccDetail(vASProviderAccDetail.getId(), ""));
		}

		if (vASProviderAccDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getVASProviderAccDetailDAO().delete(vASProviderAccDetail, TableType.MAIN_TAB);
		} else {
			vASProviderAccDetail.setRoleCode("");
			vASProviderAccDetail.setNextRoleCode("");
			vASProviderAccDetail.setTaskId("");
			vASProviderAccDetail.setNextTaskId("");
			vASProviderAccDetail.setWorkflowId(0);

			if (vASProviderAccDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				vASProviderAccDetail.setRecordType("");
				getVASProviderAccDetailDAO().save(vASProviderAccDetail, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				vASProviderAccDetail.setRecordType("");
				getVASProviderAccDetailDAO().update(vASProviderAccDetail, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(vASProviderAccDetail);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getVASProviderAccDetailDAO().delete with parameters vASProviderAccDetail,"_Temp" 3) Audit
	 * the record in to AuditHeader and AdtVASManufacturerAccDetail by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow
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

		VASProviderAccDetail vASProviderAccDetail = (VASProviderAccDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getVASProviderAccDetailDAO().delete(vASProviderAccDetail, TableType.TEMP_TAB);

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
	 * from getVASProviderAccDetailDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		VASProviderAccDetail vASProviderAccDetail = (VASProviderAccDetail) auditDetail.getModelData();

		// Check the unique keys.
		if (vASProviderAccDetail.isNew() && vASProviderAccDetailDAO.isDuplicateKey(vASProviderAccDetail.getId(),
				vASProviderAccDetail.getProviderId(), vASProviderAccDetail.getEntityCode(),
				vASProviderAccDetail.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_ProviderId") + ": " + vASProviderAccDetail.getProviderId();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}