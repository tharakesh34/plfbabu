package com.pennant.backend.service.cersai.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.cersai.AssetTypDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cersai.AssetTyp;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.cersai.AssetTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>AssetType</b>.<br>
 */
public class AssetTypeServiceImpl extends GenericService<AssetTyp> implements AssetTypeService {
	private static final Logger logger = LogManager.getLogger(AssetTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AssetTypDAO assetTypDAO;

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
	 * @return the assetTypeDAO
	 */

	public AssetTypDAO getAssetTypDAO() {
		return assetTypDAO;
	}

	/**
	 * @param assetTypeDAO the assetTypeDAO to set
	 */
	public void setAssetTypDAO(AssetTypDAO assetTypDAO) {
		this.assetTypDAO = assetTypDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * CERSAI_AssetType/CERSAI_AssetType_Temp by using CERSAI_AssetTypeDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using CERSAI_AssetTypeDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtCERSAI_AssetType by using auditHeaderDAO.addAudit(auditHeader)
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

		AssetTyp assetType = (AssetTyp) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (assetType.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (assetType.isNew()) {
			getAssetTypDAO().save(assetType, tableType);
		} else {
			getAssetTypDAO().update(assetType, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * CERSAI_AssetType by using CERSAI_AssetTypeDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtCERSAI_AssetType by using auditHeaderDAO.addAudit(auditHeader)
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

		AssetTyp assetType = (AssetTyp) auditHeader.getAuditDetail().getModelData();
		getAssetTypDAO().delete(assetType, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getCERSAI_AssetType fetch the details by using CERSAI_AssetTypeDAO's getCERSAI_AssetTypeById method.
	 * 
	 * @param assetCategoryId assetCategoryId of the AssetType.
	 * @param id              id of the AssetType.
	 * @return CERSAI_AssetType
	 */
	@Override
	public AssetTyp getAssetTyp(String assetCategoryId, int id) {
		return getAssetTypDAO().getAssetTyp(assetCategoryId, id, "_View");
	}

	/**
	 * getApprovedCERSAI_AssetTypeById fetch the details by using CERSAI_AssetTypeDAO's getCERSAI_AssetTypeById method .
	 * with parameter id and type as blank. it fetches the approved records from the CERSAI_AssetType.
	 * 
	 * @param assetCategoryId assetCategoryId of the AssetType.
	 * @param id              id of the AssetType. (String)
	 * @return CERSAI_AssetType
	 */
	public AssetTyp getApprovedAssetTyp(String assetCategoryId, int id) {
		return getAssetTypDAO().getAssetTyp(assetCategoryId, id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getAssetTypeDAO().delete with
	 * parameters assetType,"" b) NEW Add new record in to main table by using getAssetTypeDAO().save with parameters
	 * assetType,"" c) EDIT Update record in the main table by using getAssetTypeDAO().update with parameters
	 * assetType,"" 3) Delete the record from the workFlow table by using getAssetTypeDAO().delete with parameters
	 * assetType,"_Temp" 4) Audit the record in to AuditHeader and AdtCERSAI_AssetType by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtCERSAI_AssetType
	 * by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		AssetTyp assetType = new AssetTyp();
		BeanUtils.copyProperties((AssetTyp) auditHeader.getAuditDetail().getModelData(), assetType);

		getAssetTypDAO().delete(assetType, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(assetType.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					assetTypDAO.getAssetTyp(String.valueOf(assetType.getAssetCategoryId()), assetType.getId(), ""));
		}

		if (assetType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getAssetTypDAO().delete(assetType, TableType.MAIN_TAB);
		} else {
			assetType.setRoleCode("");
			assetType.setNextRoleCode("");
			assetType.setTaskId("");
			assetType.setNextTaskId("");
			assetType.setWorkflowId(0);

			if (assetType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				assetType.setRecordType("");
				getAssetTypDAO().save(assetType, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				assetType.setRecordType("");
				getAssetTypDAO().update(assetType, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(assetType);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getAssetTypeDAO().delete with parameters assetType,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtCERSAI_AssetType by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		AssetTyp assetType = (AssetTyp) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAssetTypDAO().delete(assetType, TableType.TEMP_TAB);

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
	 * from getAssetTypeDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		AssetTyp assetTyp = (AssetTyp) auditDetail.getModelData();
		String[] parameters = new String[1];
		parameters[0] = PennantJavaUtil.getLabel("label_AssetTypeList_Id.value") + ": " + assetTyp.getId();
		// Check the unique keys.
		if (assetTyp.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(assetTyp.getRecordType()) && assetTypDAO
				.isDuplicateKey(assetTyp.getId(), assetTyp.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}
