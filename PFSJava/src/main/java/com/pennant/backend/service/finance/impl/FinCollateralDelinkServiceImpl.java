package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.dao.finance.FinMaintainInstructionDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinMaintainInstruction;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.collateral.impl.CollateralAssignmentValidation;
import com.pennant.backend.service.finance.FinCollateralDelinkService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class FinCollateralDelinkServiceImpl extends GenericService<FinMaintainInstruction>
		implements FinCollateralDelinkService {
	private static Logger logger = LogManager.getLogger(FinCollateralDelinkServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CollateralAssignmentDAO collateralAssignmentDAO;
	private FinMaintainInstructionDAO finMaintainInstructionDAO;
	private CollateralAssignmentValidation collateralAssignmentValidation;

	public FinCollateralDelinkServiceImpl() {
		super();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * FinMaintainInstructions/BMTFinMaintainInstructions_Temp by using FinMaintainInstructionDAO's save method b)
	 * Update the Record in the table. based on the module workFlow Configuration. by using FinMaintainInstructionDAO's
	 * update method 3) Audit the record in to AuditHeader and AdtBMTFinMaintainInstructions by using
	 * auditHeaderDAO.addAudit(auditHeader)
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

		List<AuditDetail> auditDetails = new ArrayList<>();
		FinMaintainInstruction fmi = (FinMaintainInstruction) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (fmi.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (fmi.isNewRecord()) {
			fmi.setFinMaintainId(Long.parseLong(finMaintainInstructionDAO.save(fmi, tableType)));
			auditHeader.getAuditDetail().setModelData(fmi);
			auditHeader.setAuditReference(String.valueOf(fmi.getFinMaintainId()));
		} else {
			finMaintainInstructionDAO.update(fmi, tableType);
		}

		// Add Audit
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * FinMaintainInstructions by using FinMaintainInstructionDAO's delete method with type as Blank 3) Audit the record
	 * in to AuditHeader and AdtBMTFinMaintainInstructions by using auditHeaderDAO.addAudit(auditHeader)
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

		FinMaintainInstruction fmi = (FinMaintainInstruction) auditHeader.getAuditDetail().getModelData();
		finMaintainInstructionDAO.delete(fmi, TableType.MAIN_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinMaintainInstructionDAO().delete
	 * with parameters FinMaintainInstruction,"" b) NEW Add new record in to main table by using
	 * getFinMaintainInstructionDAO().save with parameters FinMaintainInstruction,"" c) EDIT Update record in the main
	 * table by using getFinMaintainInstructionDAO().update with parameters FinMaintainInstruction,"" 3) Delete the
	 * record from the workFlow table by using getFinMaintainInstructionDAO().delete with parameters
	 * FinMaintainInstruction,"_Temp" 4) Audit the record in to AuditHeader and AdtBMTFinMaintainInstructions by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and
	 * AdtBMTFinMaintainInstructions by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<>();

		FinMaintainInstruction fmi = new FinMaintainInstruction();
		BeanUtils.copyProperties((FinMaintainInstruction) auditHeader.getAuditDetail().getModelData(), fmi);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(fmi.getRecordType())) {
			long id = fmi.getFinMaintainId();
			auditHeader.getAuditDetail().setBefImage(finMaintainInstructionDAO.getFinMaintainInstructionById(id, ""));
		}

		if (fmi.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			finMaintainInstructionDAO.delete(fmi, TableType.MAIN_TAB);
		} else {
			fmi.setRoleCode("");
			fmi.setNextRoleCode("");
			fmi.setTaskId("");
			fmi.setNextTaskId("");
			fmi.setWorkflowId(0);

			if (fmi.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				fmi.setRecordType("");
				finMaintainInstructionDAO.save(fmi, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				fmi.setRecordType("");
				finMaintainInstructionDAO.update(fmi, TableType.MAIN_TAB);
			}
		}

		getCollateralAssignmentValidation().saveCollateralMovements(fmi.getFinReference());
		finMaintainInstructionDAO.delete(fmi, TableType.TEMP_TAB);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,
				auditHeader.getAuditDetail().getBefImage(), auditHeader.getAuditDetail().getModelData()));

		auditHeaderDAO.addAudit(auditHeader);

		// Audit for Before And After Images
		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetails(auditDetails);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(fmi);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fmi.getBefImage(), fmi));

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinMaintainInstructionDAO().delete with parameters FinMaintainInstruction,"_Temp" 3)
	 * Audit the record in to AuditHeader and AdtBMTFinMaintainInstructions by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		FinMaintainInstruction fmi = (FinMaintainInstruction) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		finMaintainInstructionDAO.delete(fmi, TableType.TEMP_TAB);

		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fmi.getBefImage(), fmi));

		auditHeaderDAO.addAudit(auditHeader);

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

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method, false);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		List<AuditDetail> auditDetails = new ArrayList<>();

		for (AuditDetail ad : auditDetails) {
			auditHeader.setErrorList(ad.getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method,
			boolean isUniqueCheckReq) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		FinMaintainInstruction fmi = (FinMaintainInstruction) auditDetail.getModelData();

		// Check the unique keys.
		String event = fmi.getEvent();
		long finID = fmi.getFinID();
		String finReference = fmi.getFinReference();
		TableType tableType = fmi.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB;
		boolean duplicateKey = finMaintainInstructionDAO.isDuplicateKey(event, finID, tableType);

		if (isUniqueCheckReq && fmi.isNewRecord() && duplicateKey) {
			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_FinMaintainInstruction_Event") + ": " + event;
			parameters[1] = PennantJavaUtil.getLabel("label_FinReference") + " : " + finReference;

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	/**
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

		FinMaintainInstruction fmi = (FinMaintainInstruction) auditHeader.getAuditDetail().getModelData();

		fmi.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(fmi);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFinMaintainInstructionDAO(FinMaintainInstructionDAO finMaintainInstructionDAO) {
		this.finMaintainInstructionDAO = finMaintainInstructionDAO;
	}

	public void setCollateralAssignmentDAO(CollateralAssignmentDAO collateralAssignmentDAO) {
		this.collateralAssignmentDAO = collateralAssignmentDAO;
	}

	public CollateralAssignmentValidation getCollateralAssignmentValidation() {
		if (collateralAssignmentValidation == null) {
			this.collateralAssignmentValidation = new CollateralAssignmentValidation(collateralAssignmentDAO);
		}
		return collateralAssignmentValidation;
	}

}
