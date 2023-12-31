/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : BusinessVerticalServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-12-2018 * *
 * Modified Date : 14-12-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-12-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.applicationmaster.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.applicationmaster.BusinessVerticalDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.BusinessVertical;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.BusinessVerticalService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>BusinessVertical</b>.<br>
 */
public class BusinessVerticalServiceImpl extends GenericService<BusinessVertical> implements BusinessVerticalService {
	private static final Logger logger = LogManager.getLogger(BusinessVerticalServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private BusinessVerticalDAO businessVerticalDAO;
	private SecurityUserDAO securityUserDAO;

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
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the businessVerticalDAO
	 */
	public BusinessVerticalDAO getBusinessVerticalDAO() {
		return businessVerticalDAO;
	}

	/**
	 * @param businessVerticalDAO the businessVerticalDAO to set
	 */
	public void setBusinessVerticalDAO(BusinessVerticalDAO businessVerticalDAO) {
		this.businessVerticalDAO = businessVerticalDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * business_vertical/business_vertical_Temp by using business_verticalDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using business_verticalDAO's update method 3) Audit the
	 * record in to AuditHeader and Adtbusiness_vertical by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		BusinessVertical businessVertical = (BusinessVertical) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (businessVertical.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (businessVertical.isNewRecord()) {
			businessVertical.setId(Long.parseLong(getBusinessVerticalDAO().save(businessVertical, tableType)));
			auditHeader.getAuditDetail().setModelData(businessVertical);
			auditHeader.setAuditReference(String.valueOf(businessVertical.getId()));
		} else {
			getBusinessVerticalDAO().update(businessVertical, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * business_vertical by using business_verticalDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and Adtbusiness_vertical by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
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

		BusinessVertical businessVertical = (BusinessVertical) auditHeader.getAuditDetail().getModelData();
		getBusinessVerticalDAO().delete(businessVertical, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getbusiness_vertical fetch the details by using business_verticalDAO's getbusiness_verticalById method.
	 * 
	 * @param id id of the BusinessVertical.
	 * @return business_vertical
	 */
	@Override
	public BusinessVertical getBusinessVertical(long id) {
		return getBusinessVerticalDAO().getBusinessVertical(id, "_View");
	}

	/**
	 * getApprovedbusiness_verticalById fetch the details by using business_verticalDAO's getbusiness_verticalById
	 * method . with parameter id and type as blank. it fetches the approved records from the business_vertical.
	 * 
	 * @param id id of the BusinessVertical. (String)
	 * @return business_vertical
	 */
	public BusinessVertical getApprovedBusinessVertical(long id) {
		return getBusinessVerticalDAO().getBusinessVertical(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getBusinessVerticalDAO().delete with
	 * parameters businessVertical,"" b) NEW Add new record in to main table by using getBusinessVerticalDAO().save with
	 * parameters businessVertical,"" c) EDIT Update record in the main table by using getBusinessVerticalDAO().update
	 * with parameters businessVertical,"" 3) Delete the record from the workFlow table by using
	 * getBusinessVerticalDAO().delete with parameters businessVertical,"_Temp" 4) Audit the record in to AuditHeader
	 * and Adtbusiness_vertical by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and Adtbusiness_vertical by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
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

		BusinessVertical businessVertical = new BusinessVertical();
		BeanUtils.copyProperties((BusinessVertical) auditHeader.getAuditDetail().getModelData(), businessVertical);

		getBusinessVerticalDAO().delete(businessVertical, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(businessVertical.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(businessVerticalDAO.getBusinessVertical(businessVertical.getId(), ""));
		}

		if (businessVertical.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getBusinessVerticalDAO().delete(businessVertical, TableType.MAIN_TAB);
		} else {
			businessVertical.setRoleCode("");
			businessVertical.setNextRoleCode("");
			businessVertical.setTaskId("");
			businessVertical.setNextTaskId("");
			businessVertical.setWorkflowId(0);

			if (businessVertical.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				businessVertical.setRecordType("");
				getBusinessVerticalDAO().save(businessVertical, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				businessVertical.setRecordType("");
				getBusinessVerticalDAO().update(businessVertical, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(businessVertical);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getBusinessVerticalDAO().delete with parameters businessVertical,"_Temp" 3) Audit the
	 * record in to AuditHeader and Adtbusiness_vertical by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
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

		BusinessVertical businessVertical = (BusinessVertical) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getBusinessVerticalDAO().delete(businessVertical, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
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
	 * from getBusinessVerticalDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		BusinessVertical businessVertical = (BusinessVertical) auditDetail.getModelData();

		// Check the unique keys.
		if (businessVertical.isNewRecord() && businessVerticalDAO.isDuplicateKey(businessVertical.getId(),
				businessVertical.getCode(), businessVertical.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_code") + ": " + businessVertical.getCode();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		if (PennantConstants.RECORD_TYPE_DEL.equals(businessVertical.getRecordType())) {
			boolean verticalexsist = securityUserDAO.isexisitvertical(businessVertical.getId());

			if (verticalexsist) {
				String[] parameters = new String[1];
				parameters[0] = PennantJavaUtil.getLabel("label_code") + ": " + businessVertical.getCode()
						+ " having child Records .It can't be Deleted";
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Autowired
	public void setSecurityUserDAO(SecurityUserDAO securityUserDAO) {
		this.securityUserDAO = securityUserDAO;
	}

}