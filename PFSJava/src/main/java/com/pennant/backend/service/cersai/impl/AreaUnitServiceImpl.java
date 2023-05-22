package com.pennant.backend.service.cersai.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.cersai.AreaUnitDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cersai.AreaUnit;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.cersai.AreaUnitService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>AreaUnit</b>.<br>
 */
public class AreaUnitServiceImpl extends GenericService<AreaUnit> implements AreaUnitService {
	private static final Logger logger = LogManager.getLogger(AreaUnitServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AreaUnitDAO areaUnitDAO;

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
	 * @return the areaUnitDAO
	 */
	public AreaUnitDAO getAreaUnitDAO() {
		return areaUnitDAO;
	}

	/**
	 * @param areaUnitDAO the areaUnitDAO to set
	 */
	public void setAreaUnitDAO(AreaUnitDAO areaUnitDAO) {
		this.areaUnitDAO = areaUnitDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * CERSAI_AreaUnit/CERSAI_AreaUnit_Temp by using CERSAI_AreaUnitDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using CERSAI_AreaUnitDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtCERSAI_AreaUnit by using auditHeaderDAO.addAudit(auditHeader)
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

		AreaUnit areaUnit = (AreaUnit) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (areaUnit.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (areaUnit.isNew()) {
			getAreaUnitDAO().save(areaUnit, tableType);
		} else {
			getAreaUnitDAO().update(areaUnit, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * CERSAI_AreaUnit by using CERSAI_AreaUnitDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtCERSAI_AreaUnit by using auditHeaderDAO.addAudit(auditHeader)
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

		AreaUnit areaUnit = (AreaUnit) auditHeader.getAuditDetail().getModelData();
		getAreaUnitDAO().delete(areaUnit, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getCERSAI_AreaUnit fetch the details by using CERSAI_AreaUnitDAO's getCERSAI_AreaUnitById method.
	 * 
	 * @param id id of the AreaUnit.
	 * @return CERSAI_AreaUnit
	 */
	@Override
	public AreaUnit getAreaUnit(Long id) {
		return getAreaUnitDAO().getAreaUnit(id, "_View");
	}

	/**
	 * getApprovedCERSAI_AreaUnitById fetch the details by using CERSAI_AreaUnitDAO's getCERSAI_AreaUnitById method .
	 * with parameter id and type as blank. it fetches the approved records from the CERSAI_AreaUnit.
	 * 
	 * @param id id of the AreaUnit. (String)
	 * @return CERSAI_AreaUnit
	 */
	public AreaUnit getApprovedAreaUnit(Long id) {
		return getAreaUnitDAO().getAreaUnit(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getAreaUnitDAO().delete with
	 * parameters areaUnit,"" b) NEW Add new record in to main table by using getAreaUnitDAO().save with parameters
	 * areaUnit,"" c) EDIT Update record in the main table by using getAreaUnitDAO().update with parameters areaUnit,""
	 * 3) Delete the record from the workFlow table by using getAreaUnitDAO().delete with parameters areaUnit,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtCERSAI_AreaUnit by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtCERSAI_AreaUnit by using auditHeaderDAO.addAudit(auditHeader)
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

		AreaUnit areaUnit = new AreaUnit();
		BeanUtils.copyProperties((AreaUnit) auditHeader.getAuditDetail().getModelData(), areaUnit);

		getAreaUnitDAO().delete(areaUnit, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(areaUnit.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(areaUnitDAO.getAreaUnit(areaUnit.getId(), ""));
		}

		if (areaUnit.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getAreaUnitDAO().delete(areaUnit, TableType.MAIN_TAB);
		} else {
			areaUnit.setRoleCode("");
			areaUnit.setNextRoleCode("");
			areaUnit.setTaskId("");
			areaUnit.setNextTaskId("");
			areaUnit.setWorkflowId(0);

			if (areaUnit.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				areaUnit.setRecordType("");
				getAreaUnitDAO().save(areaUnit, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				areaUnit.setRecordType("");
				getAreaUnitDAO().update(areaUnit, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(areaUnit);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getAreaUnitDAO().delete with parameters areaUnit,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtCERSAI_AreaUnit by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		AreaUnit areaUnit = (AreaUnit) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAreaUnitDAO().delete(areaUnit, TableType.TEMP_TAB);

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
	 * from getAreaUnitDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over here.
		AreaUnit areaUnit = (AreaUnit) auditDetail.getModelData();
		String[] parameters = new String[1];
		parameters[0] = PennantJavaUtil.getLabel("label_AreaUnitList_Id.value") + ": " + areaUnit.getId();
		// Check the unique keys.
		if (areaUnit.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(areaUnit.getRecordType()) && areaUnitDAO
				.isDuplicateKey(areaUnit.getId(), areaUnit.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}