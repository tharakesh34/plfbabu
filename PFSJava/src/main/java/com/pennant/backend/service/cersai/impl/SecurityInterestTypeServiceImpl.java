package com.pennant.backend.service.cersai.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.cersai.SecurityInterestTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cersai.SecurityInterestType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.cersai.SecurityInterestTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>SecurityInterestType</b>.<br>
 */
public class SecurityInterestTypeServiceImpl extends GenericService<SecurityInterestType>
		implements SecurityInterestTypeService {
	private static final Logger logger = LogManager.getLogger(SecurityInterestTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private SecurityInterestTypeDAO securityInterestTypeDAO;

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
	 * @return the securityInterestTypeDAO
	 */
	public SecurityInterestTypeDAO getSecurityInterestTypeDAO() {
		return securityInterestTypeDAO;
	}

	/**
	 * @param securityInterestTypeDAO the securityInterestTypeDAO to set
	 */
	public void setSecurityInterestTypeDAO(SecurityInterestTypeDAO securityInterestTypeDAO) {
		this.securityInterestTypeDAO = securityInterestTypeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table CERSAI_SIType/CERSAI_SIType_Temp
	 * by using CERSAI_SITypeDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using CERSAI_SITypeDAO's update method 3) Audit the record in to AuditHeader and
	 * AdtCERSAI_SIType by using auditHeaderDAO.addAudit(auditHeader)
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

		SecurityInterestType securityInterestType = (SecurityInterestType) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (securityInterestType.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (securityInterestType.isNew()) {
			getSecurityInterestTypeDAO().save(securityInterestType, tableType);
		} else {
			getSecurityInterestTypeDAO().update(securityInterestType, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * CERSAI_SIType by using CERSAI_SITypeDAO's delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and AdtCERSAI_SIType by using auditHeaderDAO.addAudit(auditHeader)
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

		SecurityInterestType securityInterestType = (SecurityInterestType) auditHeader.getAuditDetail().getModelData();
		getSecurityInterestTypeDAO().delete(securityInterestType, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getCERSAI_SIType fetch the details by using CERSAI_SITypeDAO's getCERSAI_SITypeById method.
	 * 
	 * @param assetCategoryId assetCategoryId of the SecurityInterestType.
	 * @param id              id of the SecurityInterestType.
	 * @return CERSAI_SIType
	 */
	@Override
	public SecurityInterestType getSecurityInterestType(Long assetCategoryId, int id) {
		return getSecurityInterestTypeDAO().getSecurityInterestType(assetCategoryId, id, "_View");
	}

	/**
	 * getApprovedCERSAI_SITypeById fetch the details by using CERSAI_SITypeDAO's getCERSAI_SITypeById method . with
	 * parameter id and type as blank. it fetches the approved records from the CERSAI_SIType.
	 * 
	 * @param assetCategoryId assetCategoryId of the SecurityInterestType.
	 * @param id              id of the SecurityInterestType. (String)
	 * @return CERSAI_SIType
	 */
	public SecurityInterestType getApprovedSecurityInterestType(Long assetCategoryId, int id) {
		return getSecurityInterestTypeDAO().getSecurityInterestType(assetCategoryId, id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getSecurityInterestTypeDAO().delete
	 * with parameters securityInterestType,"" b) NEW Add new record in to main table by using
	 * getSecurityInterestTypeDAO().save with parameters securityInterestType,"" c) EDIT Update record in the main table
	 * by using getSecurityInterestTypeDAO().update with parameters securityInterestType,"" 3) Delete the record from
	 * the workFlow table by using getSecurityInterestTypeDAO().delete with parameters securityInterestType,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtCERSAI_SIType by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtCERSAI_SIType by using auditHeaderDAO.addAudit(auditHeader)
	 * based on the transaction Type.
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

		SecurityInterestType securityInterestType = new SecurityInterestType();
		BeanUtils.copyProperties((SecurityInterestType) auditHeader.getAuditDetail().getModelData(),
				securityInterestType);

		getSecurityInterestTypeDAO().delete(securityInterestType, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(securityInterestType.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(securityInterestTypeDAO.getSecurityInterestType(
					securityInterestType.getAssetCategoryId(), securityInterestType.getId(), ""));
		}

		if (securityInterestType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getSecurityInterestTypeDAO().delete(securityInterestType, TableType.MAIN_TAB);
		} else {
			securityInterestType.setRoleCode("");
			securityInterestType.setNextRoleCode("");
			securityInterestType.setTaskId("");
			securityInterestType.setNextTaskId("");
			securityInterestType.setWorkflowId(0);

			if (securityInterestType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				securityInterestType.setRecordType("");
				getSecurityInterestTypeDAO().save(securityInterestType, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				securityInterestType.setRecordType("");
				getSecurityInterestTypeDAO().update(securityInterestType, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(securityInterestType);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getSecurityInterestTypeDAO().delete with parameters securityInterestType,"_Temp" 3) Audit
	 * the record in to AuditHeader and AdtCERSAI_SIType by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		SecurityInterestType securityInterestType = (SecurityInterestType) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSecurityInterestTypeDAO().delete(securityInterestType, TableType.TEMP_TAB);

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
	 * from getSecurityInterestTypeDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.
		SecurityInterestType securityInterestType = (SecurityInterestType) auditDetail.getModelData();
		String[] parameters = new String[1];
		parameters[0] = PennantJavaUtil.getLabel("label_AssetCategoryList_Id.value") + ": "
				+ securityInterestType.getId();
		// Check the unique keys.
		if (securityInterestType.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(securityInterestType.getRecordType())
				&& securityInterestTypeDAO.isDuplicateKey(securityInterestType.getId(),
						securityInterestType.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}