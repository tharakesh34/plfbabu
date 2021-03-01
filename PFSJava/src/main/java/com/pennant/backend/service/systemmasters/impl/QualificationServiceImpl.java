package com.pennant.backend.service.systemmasters.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.QualificationDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Qualification;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.QualificationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class QualificationServiceImpl extends GenericService<Qualification> implements QualificationService {
	private static Logger logger = LogManager.getLogger(QualificationServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private QualificationDAO qualificationDAO;

	public QualificationServiceImpl() {
		super();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table Qualification/Qualification_Temp
	 * by using qualificationDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using qualificationDAO's update method 3) Audit the record in to AuditHeader and
	 * AdtQualification by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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

		Qualification qualification = (Qualification) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (qualification.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
			;
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		}

		if (qualification.isNew()) {
			qualification.setCode(qualificationDAO.save(qualification, tableType));
			auditHeader.getAuditDetail().setModelData(qualification);
			auditHeader.setAuditReference(qualification.getCode());
		} else {
			qualificationDAO.update(qualification, tableType);
		}

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * Qualification by using qualificationDAO's delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and AdtQualification by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {

		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}
		Qualification qualification = (Qualification) auditHeader.getAuditDetail().getModelData();
		qualificationDAO.delete(qualification, TableType.MAIN_TAB);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getProfessionById fetch the details by using qualificationDAO's getProfessionById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Profession""
	 */
	@Override
	public Qualification getQualificationById(String id) {
		return qualificationDAO.getQualificationById(id, "_View");
	}

	/**
	 * getApprovedProfessionById fetch the details by using qualificationDAO's getProfessionById method . with parameter
	 * id and type as blank. it fetches the approved records from the Qualification.
	 * 
	 * @param id
	 *            (String)
	 * @return Profession
	 */
	public Qualification getApprovedQualificationById(String id) {
		return qualificationDAO.getQualificationById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using qualificationDAO.delete with
	 * parameters profession,"" b) NEW Add new record in to main table by using qualificationDAO.save with parameters
	 * profession,"" c) EDIT Update record in the main table by using qualificationDAO.update with parameters
	 * profession,"" 3) Delete the record from the workFlow table by using qualificationDAO.delete with parameters
	 * profession,"_Temp" 4) Audit the record in to AuditHeader and AdtQualification by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtQualification by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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
		Qualification qualification = new Qualification();
		BeanUtils.copyProperties((Qualification) auditHeader.getAuditDetail().getModelData(), qualification);

		qualificationDAO.delete(qualification, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(qualification.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(qualificationDAO.getQualificationById(qualification.getCode(), ""));
		}

		if (qualification.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			qualificationDAO.delete(qualification, TableType.MAIN_TAB);

		} else {
			qualification.setRoleCode("");
			qualification.setNextRoleCode("");
			qualification.setTaskId("");
			qualification.setNextTaskId("");
			qualification.setWorkflowId(0);

			if (qualification.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				qualification.setRecordType("");
				qualificationDAO.save(qualification, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				qualification.setRecordType("");
				qualificationDAO.update(qualification, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(qualification);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using qualificationDAO.delete with parameters profession,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtQualification by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		Qualification qualification = (Qualification) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		qualificationDAO.delete(qualification, TableType.TEMP_TAB);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
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
	 * from getAcademicDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		Qualification qualification = (Qualification) auditDetail.getModelData();
		String code = qualification.getCode();

		// Check the unique keys.
		if (qualification.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(qualification.getRecordType())
				&& qualificationDAO.isDuplicateKey(code,
						qualification.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_QualificationCode") + ": " + code;

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setQualificationDAO(QualificationDAO qualificationDAO) {
		this.qualificationDAO = qualificationDAO;
	}
}
