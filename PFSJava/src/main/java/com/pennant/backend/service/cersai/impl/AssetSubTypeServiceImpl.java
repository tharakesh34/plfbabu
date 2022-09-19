package com.pennant.backend.service.cersai.impl;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.cersai.AssetSubTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cersai.AssetSubType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.cersai.AssetSubTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>AssetSubType</b>.<br>
 */
public class AssetSubTypeServiceImpl extends GenericService<AssetSubType> implements AssetSubTypeService {
	private static final Logger logger = LogManager.getLogger(AssetSubTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AssetSubTypeDAO assetSubTypeDAO;

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
	 * @return the assetSubTypeDAO
	 */
	public AssetSubTypeDAO getAssetSubTypeDAO() {
		return assetSubTypeDAO;
	}

	/**
	 * @param assetSubTypeDAO the assetSubTypeDAO to set
	 */
	public void setAssetSubTypeDAO(AssetSubTypeDAO assetSubTypeDAO) {
		this.assetSubTypeDAO = assetSubTypeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * CERSAI_AssetSubType/CERSAI_AssetSubType_Temp by using CERSAI_AssetSubTypeDAO's save method b) Update the Record
	 * in the table. based on the module workFlow Configuration. by using CERSAI_AssetSubTypeDAO's update method 3)
	 * Audit the record in to AuditHeader and AdtCERSAI_AssetSubType by using auditHeaderDAO.addAudit(auditHeader)
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

		AssetSubType assetSubType = (AssetSubType) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (assetSubType.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (assetSubType.isNew()) {
			getAssetSubTypeDAO().save(assetSubType, tableType);
		} else {
			getAssetSubTypeDAO().update(assetSubType, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * CERSAI_AssetSubType by using CERSAI_AssetSubTypeDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtCERSAI_AssetSubType by using auditHeaderDAO.addAudit(auditHeader)
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

		AssetSubType assetSubType = (AssetSubType) auditHeader.getAuditDetail().getModelData();
		getAssetSubTypeDAO().delete(assetSubType, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getCERSAI_AssetSubType fetch the details by using CERSAI_AssetSubTypeDAO's getCERSAI_AssetSubTypeById method.
	 * 
	 * @param assetTypeId assetTypeId of the AssetSubType.
	 * @param id          id of the AssetSubType.
	 * @return CERSAI_AssetSubType
	 */
	@Override
	public AssetSubType getAssetSubType(String assetTypeId, int id) {
		return getAssetSubTypeDAO().getAssetSubType(assetTypeId, id, "_View");
	}

	/**
	 * getApprovedCERSAI_AssetSubTypeById fetch the details by using CERSAI_AssetSubTypeDAO's getCERSAI_AssetSubTypeById
	 * method . with parameter id and type as blank. it fetches the approved records from the CERSAI_AssetSubType.
	 * 
	 * @param assetTypeId assetTypeId of the AssetSubType.
	 * @param id          id of the AssetSubType. (String)
	 * @return CERSAI_AssetSubType
	 */
	public AssetSubType getApprovedAssetSubType(String assetTypeId, int id) {
		return getAssetSubTypeDAO().getAssetSubType(assetTypeId, id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getAssetSubTypeDAO().delete with
	 * parameters assetSubType,"" b) NEW Add new record in to main table by using getAssetSubTypeDAO().save with
	 * parameters assetSubType,"" c) EDIT Update record in the main table by using getAssetSubTypeDAO().update with
	 * parameters assetSubType,"" 3) Delete the record from the workFlow table by using getAssetSubTypeDAO().delete with
	 * parameters assetSubType,"_Temp" 4) Audit the record in to AuditHeader and AdtCERSAI_AssetSubType by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and
	 * AdtCERSAI_AssetSubType by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		AssetSubType assetSubType = new AssetSubType();
		BeanUtils.copyProperties((AssetSubType) auditHeader.getAuditDetail().getModelData(), assetSubType);

		getAssetSubTypeDAO().delete(assetSubType, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(assetSubType.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					assetSubTypeDAO.getAssetSubType(assetSubType.getAssetTypeId(), assetSubType.getId(), ""));
		}

		if (assetSubType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getAssetSubTypeDAO().delete(assetSubType, TableType.MAIN_TAB);
		} else {
			assetSubType.setRoleCode("");
			assetSubType.setNextRoleCode("");
			assetSubType.setTaskId("");
			assetSubType.setNextTaskId("");
			assetSubType.setWorkflowId(0);

			if (assetSubType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				assetSubType.setRecordType("");
				getAssetSubTypeDAO().save(assetSubType, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				assetSubType.setRecordType("");
				getAssetSubTypeDAO().update(assetSubType, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(assetSubType);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getAssetSubTypeDAO().delete with parameters assetSubType,"_Temp" 3) Audit the record in
	 * to AuditHeader and AdtCERSAI_AssetSubType by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		AssetSubType assetSubType = (AssetSubType) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAssetSubTypeDAO().delete(assetSubType, TableType.TEMP_TAB);

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
	 * from getAssetSubTypeDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.
		AssetSubType assetSubType = (AssetSubType) auditDetail.getModelData();
		String[] parameters = new String[1];
		parameters[0] = PennantJavaUtil.getLabel("label_AssetSubTypeList_AssetTypeId.value") + ": "
				+ assetSubType.getId();
		// Check the unique keys.
		if (assetSubType.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(assetSubType.getRecordType())
				&& assetSubTypeDAO.isDuplicateKey(assetSubType.getId(),
						assetSubType.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}