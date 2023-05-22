package com.pennant.backend.service.cersai.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.cersai.AssetCategoryDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cersai.AssetCategory;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.cersai.AssetCategoryService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>AssetCategory</b>.<br>
 */
public class AssetCategoryServiceImpl extends GenericService<AssetCategory> implements AssetCategoryService {
	private static final Logger logger = LogManager.getLogger(AssetCategoryServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AssetCategoryDAO assetCategoryDAO;

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
	 * @return the assetCategoryDAO
	 */
	public AssetCategoryDAO getAssetCategoryDAO() {
		return assetCategoryDAO;
	}

	/**
	 * @param assetCategoryDAO the assetCategoryDAO to set
	 */
	public void setAssetCategoryDAO(AssetCategoryDAO assetCategoryDAO) {
		this.assetCategoryDAO = assetCategoryDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * CERSAI_AssetCategory/CERSAI_AssetCategory_Temp by using CERSAI_AssetCategoryDAO's save method b) Update the
	 * Record in the table. based on the module workFlow Configuration. by using CERSAI_AssetCategoryDAO's update method
	 * 3) Audit the record in to AuditHeader and AdtCERSAI_AssetCategory by using auditHeaderDAO.addAudit(auditHeader)
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

		AssetCategory assetCategory = (AssetCategory) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (assetCategory.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (assetCategory.isNew()) {
			getAssetCategoryDAO().save(assetCategory, tableType);
		} else {
			getAssetCategoryDAO().update(assetCategory, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * CERSAI_AssetCategory by using CERSAI_AssetCategoryDAO's delete method with type as Blank 3) Audit the record in
	 * to AuditHeader and AdtCERSAI_AssetCategory by using auditHeaderDAO.addAudit(auditHeader)
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

		AssetCategory assetCategory = (AssetCategory) auditHeader.getAuditDetail().getModelData();
		getAssetCategoryDAO().delete(assetCategory, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getCERSAI_AssetCategory fetch the details by using CERSAI_AssetCategoryDAO's getCERSAI_AssetCategoryById method.
	 * 
	 * @param id id of the AssetCategory.
	 * @return CERSAI_AssetCategory
	 */
	@Override
	public AssetCategory getAssetCategory(int id) {
		return getAssetCategoryDAO().getAssetCategory(id, "_View");
	}

	/**
	 * getApprovedCERSAI_AssetCategoryById fetch the details by using CERSAI_AssetCategoryDAO's
	 * getCERSAI_AssetCategoryById method . with parameter id and type as blank. it fetches the approved records from
	 * the CERSAI_AssetCategory.
	 * 
	 * @param id id of the AssetCategory. (String)
	 * @return CERSAI_AssetCategory
	 */
	public AssetCategory getApprovedAssetCategory(int id) {
		return getAssetCategoryDAO().getAssetCategory(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getAssetCategoryDAO().delete with
	 * parameters assetCategory,"" b) NEW Add new record in to main table by using getAssetCategoryDAO().save with
	 * parameters assetCategory,"" c) EDIT Update record in the main table by using getAssetCategoryDAO().update with
	 * parameters assetCategory,"" 3) Delete the record from the workFlow table by using getAssetCategoryDAO().delete
	 * with parameters assetCategory,"_Temp" 4) Audit the record in to AuditHeader and AdtCERSAI_AssetCategory by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and
	 * AdtCERSAI_AssetCategory by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		AssetCategory assetCategory = new AssetCategory();
		BeanUtils.copyProperties((AssetCategory) auditHeader.getAuditDetail().getModelData(), assetCategory);

		getAssetCategoryDAO().delete(assetCategory, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(assetCategory.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(assetCategoryDAO.getAssetCategory(assetCategory.getId(), ""));
		}

		if (assetCategory.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getAssetCategoryDAO().delete(assetCategory, TableType.MAIN_TAB);
		} else {
			assetCategory.setRoleCode("");
			assetCategory.setNextRoleCode("");
			assetCategory.setTaskId("");
			assetCategory.setNextTaskId("");
			assetCategory.setWorkflowId(0);

			if (assetCategory.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				assetCategory.setRecordType("");
				getAssetCategoryDAO().save(assetCategory, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				assetCategory.setRecordType("");
				getAssetCategoryDAO().update(assetCategory, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(assetCategory);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getAssetCategoryDAO().delete with parameters assetCategory,"_Temp" 3) Audit the record in
	 * to AuditHeader and AdtCERSAI_AssetCategory by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		AssetCategory assetCategory = (AssetCategory) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAssetCategoryDAO().delete(assetCategory, TableType.TEMP_TAB);

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
	 * from getAssetCategoryDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		AssetCategory assetCategory = (AssetCategory) auditDetail.getModelData();
		String[] parameters = new String[1];
		parameters[0] = PennantJavaUtil.getLabel("label_AssetCategoryList_Id.value") + ": " + assetCategory.getId();
		// Check the unique keys.
		if (assetCategory.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(assetCategory.getRecordType())
				&& assetCategoryDAO.isDuplicateKey(assetCategory.getId(),
						assetCategory.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}