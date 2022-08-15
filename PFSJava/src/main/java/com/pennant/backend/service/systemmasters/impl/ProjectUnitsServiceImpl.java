package com.pennant.backend.service.systemmasters.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.ProjectUnitsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.ProjectUnits;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.ProjectUnitsService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class ProjectUnitsServiceImpl extends GenericService<ProjectUnits> implements ProjectUnitsService {
	private static final Logger logger = LogManager.getLogger(ProjectUnitsServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ProjectUnitsDAO projectUnitsDAO;

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table ProjectUnit/ProjectUnit_Temp by
	 * using ProjectUnitDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using ProjectUnitDAO's update method 3) Audit the record in to AuditHeader and AdtProjectUnit by using
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
		ProjectUnits projectUnit = (ProjectUnits) auditHeader.getAuditDetail().getModelData();

		String tableType = TableType.MAIN_TAB.getSuffix();
		if (projectUnit.isWorkflow()) {
			tableType = TableType.TEMP_TAB.getSuffix();
		}

		if (projectUnit.isNewRecord()) {
			projectUnit.setId(projectUnitsDAO.save(projectUnit, tableType));
			auditHeader.getAuditDetail().setModelData(projectUnit);
			auditHeader.setAuditReference(String.valueOf(projectUnit.getId()));
		} else {
			projectUnitsDAO.update(projectUnit, tableType);
		}
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public ProjectUnits getApprovedProjectUnitsByID(long id) {
		return projectUnitsDAO.getProjectUnitsByID(id, "_View");
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * ProjectUnit by using ProjectUnitDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtProjectUnit by using auditHeaderDAO.addAudit(auditHeader)
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

		ProjectUnits projectUnit = (ProjectUnits) auditHeader.getAuditDetail().getModelData();
		projectUnitsDAO.delete(projectUnit, TableType.MAIN_TAB.getSuffix());

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using projectUnitsDAO.delete with parameters
	 * ProjectUnit,"" b) NEW Add new record in to main table by using projectUnitsDAO.save with parameters
	 * ProjectUnit,"" c) EDIT Update record in the main table by using projectUnitsDAO.update with parameters
	 * ProjectUnit,"" 3) Delete the record from the workFlow table by using projectUnitsDAO.delete with parameters
	 * ProjectUnit,"_Temp" 4) Audit the record in to AuditHeader and AdtProjectUnit by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtProjectUnit by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		ProjectUnits projectUnit = new ProjectUnits();
		BeanUtils.copyProperties((ProjectUnits) auditHeader.getAuditDetail().getModelData(), projectUnit);

		projectUnitsDAO.delete(projectUnit, TableType.TEMP_TAB.getSuffix());

		if (!PennantConstants.RECORD_TYPE_NEW.equals(projectUnit.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(projectUnitsDAO.getProjectUnitsByID(projectUnit.getId(), ""));
		}

		if (projectUnit.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			projectUnitsDAO.delete(projectUnit, TableType.MAIN_TAB.getSuffix());
		} else {
			projectUnit.setRoleCode("");
			projectUnit.setNextRoleCode("");
			projectUnit.setTaskId("");
			projectUnit.setNextTaskId("");
			projectUnit.setWorkflowId(0);

			if (projectUnit.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				projectUnit.setRecordType("");
				projectUnitsDAO.save(projectUnit, TableType.MAIN_TAB.getSuffix());
			} else {
				tranType = PennantConstants.TRAN_UPD;
				projectUnit.setRecordType("");
				projectUnitsDAO.update(projectUnit, TableType.MAIN_TAB.getSuffix());
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(projectUnit);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using projectUnitsDAO.delete with parameters ProjectUnit,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtProjectUnit by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		return null;
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
	 * from projectUnitsDAO.getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public List<ProjectUnits> getProjectUnitsByProjectID(long id) {
		return projectUnitsDAO.getProjectUnitsByProjectID(id, "_View");
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setProjectUnitsDAO(ProjectUnitsDAO projectUnitsDAO) {
		this.projectUnitsDAO = projectUnitsDAO;
	}

}
