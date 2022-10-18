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
package com.pennant.backend.service.systemmasters.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.systemmasters.CustTypePANMappingDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.CustTypePANMapping;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.CustTypePANMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>CustTypePANMapping</b>.<br>
 * 
 */
public class CustTypePANMappingServiceImpl extends GenericService<CustTypePANMapping>
		implements CustTypePANMappingService {
	private static Logger logger = LogManager.getLogger(CustTypePANMappingServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CustTypePANMappingDAO custTypePANMappingDAO;
	private CustomerDAO customerDAO;

	public CustTypePANMappingServiceImpl() {
		super();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * CustTypePANMapping/CustTypePANMapping_Temp by using CustTypePANMappingDAO's save method b) Update the Record in
	 * the table. based on the module workFlow Configuration. by using CustTypePANMappingDAO's update method 3) Audit
	 * the record in to AuditHeader and AdtCustTypePANMapping by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		CustTypePANMapping custTypePANMapping = (CustTypePANMapping) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (custTypePANMapping.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (custTypePANMapping.isNewRecord()) {
			custTypePANMapping
					.setMappingID(Long.parseLong(getCustTypePANMappingDAO().save(custTypePANMapping, tableType)));
			auditHeader.getAuditDetail().setModelData(custTypePANMapping);
			auditHeader.setAuditReference(String.valueOf(custTypePANMapping.getMappingID()));
		} else {
			getCustTypePANMappingDAO().update(custTypePANMapping, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * CustTypePANMapping by using CustTypePANMappingDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtCustTypePANMapping by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		CustTypePANMapping custTypePANMapping = (CustTypePANMapping) auditHeader.getAuditDetail().getModelData();
		getCustTypePANMappingDAO().delete(custTypePANMapping, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getCustTypePANMappingById fetch the details by using CustTypePANMappingDAO's getCustTypePANMappingById method.
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return CustTypePANMapping
	 */
	@Override
	public CustTypePANMapping getPANMappingById(long mappingID) {
		return getCustTypePANMappingDAO().getCustTypePANMappingById(mappingID, "_View");
	}

	/**
	 * getApprovedPANMappingById fetch the details by using CustTypePANMappingDAO's getCustTypePANMappingById method .
	 * with parameter id and type as blank. it fetches the approved records from the CustTypePANMapping.
	 * 
	 * @param id (String)
	 * @return CustTypePANMapping
	 */
	public CustTypePANMapping getApprovedPANMappingById(long mappingID) {
		return getCustTypePANMappingDAO().getCustTypePANMappingById(mappingID, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getCustTypePANMappingDAO().delete with
	 * parameters custTypePANMapping,"" b) NEW Add new record in to main table by using getCustTypePANMappingDAO().save
	 * with parameters custTypePANMapping,"" c) EDIT Update record in the main table by using
	 * getCustTypePANMappingDAO().update with parameters custTypePANMapping,"" 3) Delete the record from the workFlow
	 * table by using getCustTypePANMappingDAO().delete with parameters custTypePANMapping,"_Temp" 4) Audit the record
	 * in to AuditHeader and AdtCustTypePANMapping by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit
	 * the record in to AuditHeader and AdtCustTypePANMapping by using auditHeaderDAO.addAudit(auditHeader) based on the
	 * transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		CustTypePANMapping custTypePANMapping = new CustTypePANMapping();
		BeanUtils.copyProperties((CustTypePANMapping) auditHeader.getAuditDetail().getModelData(), custTypePANMapping);

		getCustTypePANMappingDAO().delete(custTypePANMapping, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(custTypePANMapping.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					getCustTypePANMappingDAO().getCustTypePANMappingById(custTypePANMapping.getMappingID(), ""));
		}

		if (custTypePANMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getCustTypePANMappingDAO().delete(custTypePANMapping, TableType.MAIN_TAB);
		} else {
			custTypePANMapping.setRoleCode("");
			custTypePANMapping.setNextRoleCode("");
			custTypePANMapping.setTaskId("");
			custTypePANMapping.setNextTaskId("");
			custTypePANMapping.setWorkflowId(0);

			if (custTypePANMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				custTypePANMapping.setRecordType("");
				getCustTypePANMappingDAO().save(custTypePANMapping, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				custTypePANMapping.setRecordType("");
				getCustTypePANMappingDAO().update(custTypePANMapping, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(custTypePANMapping);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getCustTypePANMappingDAO().delete with parameters custTypePANMapping,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtCustTypePANMapping by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		CustTypePANMapping custTypePANMapping = (CustTypePANMapping) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustTypePANMappingDAO().delete(custTypePANMapping, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
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
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getCustTypePANMappingDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		CustTypePANMapping custTypePANMapping = (CustTypePANMapping) auditDetail.getModelData();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(custTypePANMapping.getCustType());
		errParm[0] = PennantJavaUtil.getLabel("label_CustType") + " : " + valueParm[0];

		// Check the unique keys.
		if (custTypePANMapping.isNewRecord() && custTypePANMappingDAO.isDuplicateKey(custTypePANMapping.getMappingID(),
				custTypePANMapping.getCustType(), custTypePANMapping.getPanLetter(),
				custTypePANMapping.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_CustType") + ": " + custTypePANMapping.getCustType();
			parameters[1] = PennantJavaUtil.getLabel("label_PANLetter") + ": " + custTypePANMapping.getPanLetter();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		// Checking Dependency Validation
		if (!StringUtils.equals(method, PennantConstants.method_doReject)
				&& PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(custTypePANMapping.getRecordType())) {

			// Customer Type
			boolean isCustTypeExists = getCustomerDAO().isCustTypeExists(custTypePANMapping.getCustType(), "_View");
			if (isCustTypeExists) {
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", errParm, null));
			}

		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	/**
	 * The application will compare 4th letter of the entered customer PAN number with customer Type level PAN 4th
	 * letter mapped in the masters.
	 * 
	 * @param panMapping
	 */
	@Override
	public boolean isValidPANLetter(String custType, String custCategory, String panLetter) {
		return custTypePANMappingDAO.isValidPANLetter(custType, custCategory, panLetter);
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

	public CustTypePANMappingDAO getCustTypePANMappingDAO() {
		return custTypePANMappingDAO;
	}

	public void setCustTypePANMappingDAO(CustTypePANMappingDAO custTypePANMappingDAO) {
		this.custTypePANMappingDAO = custTypePANMappingDAO;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

}