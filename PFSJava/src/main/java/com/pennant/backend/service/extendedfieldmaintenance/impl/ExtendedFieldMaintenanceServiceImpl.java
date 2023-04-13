package com.pennant.backend.service.extendedfieldmaintenance.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.finance.ExtendedFieldMaintenanceDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.ExtendedFieldMaintenance;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.ExtendedFieldMaintenanceService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennapps.core.util.ObjectUtil;

public class ExtendedFieldMaintenanceServiceImpl extends GenericService<ExtendedFieldMaintenance>
		implements ExtendedFieldMaintenanceService {

	private static final Logger logger = LogManager.getLogger(ExtendedFieldMaintenanceServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ExtendedFieldMaintenanceDAO extendedFieldMaintenanceDAO;
	private ExtendedFieldRenderDAO extendedFieldRenderDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;

	public ExtendedFieldMaintenanceServiceImpl() {
		super();
	}

	@Override
	public AuditHeader delete(AuditHeader aAuditHeader) {
		return null;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		if (!aAuditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return aAuditHeader;
		}

		ExtendedFieldMaintenance efm = (ExtendedFieldMaintenance) aAuditHeader.getAuditDetail().getModelData();
		AuditHeader auditHeader = ObjectUtil.clone(aAuditHeader);

		TableType tableType = TableType.MAIN_TAB;
		if (efm.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		// Extended field Details
		if (efm.getExtFieldRenderList() != null) {
			List<AuditDetail> details = efm.getAuditDetailMap().get("LoanMaintExtendedFieldDetails");

			for (AuditDetail auditDetail : details) {
				ExtendedFieldRender efr = (ExtendedFieldRender) auditDetail.getModelData();
				extendedFieldDetailsService.processingExtendedFieldDetailList(efr,
						efm.getExtFieldRenderList().get(0).getTableName(), efm.getExtendedFieldHeader().getEvent(),
						tableType.getSuffix());
				auditDetail.setExtended(true);
				auditDetail.setModelData(efr);
				if (details != null) {
					auditDetails.add(auditDetail);
				}
			}
			auditHeader.setAuditDetails(auditDetails);
			getAuditHeaderDAO().addAudit(auditHeader);
			auditDetails.clear();
		}

		if (efm.isNewRecord()) {
			getExtendedFieldMaintenanceDAO().save(efm, tableType);
		} else {
			getExtendedFieldMaintenanceDAO().update(efm, tableType);
		}

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method does the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method, false);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		ExtendedFieldMaintenance efm = (ExtendedFieldMaintenance) auditHeader.getAuditDetail().getModelData();

		// Extended field details Validation
		if (efm.getExtFieldRenderList() != null) {
			List<AuditDetail> details = efm.getAuditDetailMap().get("LoanMaintExtendedFieldDetails");
			String usrLanguage = efm.getUserDetails().getLanguage();
			ExtendedFieldHeader efh = efm.getExtendedFieldHeader();
			details = extendedFieldDetailsService.validateExtendedDdetails(efh, details, method, usrLanguage);

			for (ExtendedFieldRender efr : efm.getExtFieldRenderList()) {
				efr.setTypeCode(efm.getExtendedFieldHeader().getSubModuleName());
				efr.setRecordStatus(efm.getRecordStatus());
			}
			auditDetails.addAll(details);
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		ExtendedFieldMaintenance efm = (ExtendedFieldMaintenance) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (efm.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Extended Field Details
		if (efm.getExtFieldRenderList() != null) {
			auditDetailMap.put("LoanMaintExtendedFieldDetails", extendedFieldDetailsService.setExtendedFieldsAuditData(
					efm.getExtFieldRenderList(), auditTranType, method, efm.getExtendedFieldHeader().getModuleName()));

			for (AuditDetail auditDetail : auditDetailMap.get("LoanMaintExtendedFieldDetails")) {
				ExtendedFieldRender extRender = (ExtendedFieldRender) auditDetail.getModelData();
				extRender.setRoleCode(efm.getRoleCode());
				extRender.setNextRoleCode(efm.getNextRoleCode());
				extRender.setTaskId(efm.getTaskId());
				extRender.setNextTaskId(efm.getNextTaskId());
				extRender.setRecordStatus(efm.getRecordStatus());
			}

			auditDetails.addAll(auditDetailMap.get("LoanMaintExtendedFieldDetails"));
		}

		efm.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(efm);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method,
			boolean isUniqueCheckReq) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		ExtendedFieldMaintenance extFieldsMaint = (ExtendedFieldMaintenance) auditDetail.getModelData();

		// Check the unique keys.
		if (isUniqueCheckReq && extFieldsMaint.isNewRecord() && getExtendedFieldMaintenanceDAO().isDuplicateKey(
				extFieldsMaint.getReference(), extFieldsMaint.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_FinReference") + " : " + extFieldsMaint.getReference();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@SuppressWarnings("unused")
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		String tranType = "";
		ExtendedFieldMaintenance efm = new ExtendedFieldMaintenance();
		BeanUtils.copyProperties((ExtendedFieldMaintenance) auditHeader.getAuditDetail().getModelData(), efm);

		if (efm.getExtFieldRenderList() != null) {
			List<AuditDetail> details = efm.getAuditDetailMap().get("LoanMaintExtendedFieldDetails");

			for (AuditDetail auditDetail : details) {
				ExtendedFieldRender efr = (ExtendedFieldRender) auditDetail.getModelData();
				extendedFieldDetailsService.processingExtendedFieldDetailList(efr,
						efm.getExtFieldRenderList().get(0).getTableName(), efm.getExtendedFieldHeader().getEvent(), "");
				auditDetail.setExtended(true);
				auditDetail.setModelData(efr);
				if (details != null) {
					auditDetails.add(auditDetail);
				}

				getExtendedFieldRenderDAO().delete(efr.getReference(), efr.getSeqNo(), TableType.TEMP_TAB.getSuffix(),
						efr.getTableName());
			}

			auditHeader.setAuditDetails(auditDetails);
			getAuditHeaderDAO().addAudit(auditHeader);
			auditDetails.clear();

		}

		if (efm.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getExtendedFieldMaintenanceDAO().delete(efm, TableType.MAIN_TAB);
		} else {
			efm.setRoleCode("");
			efm.setNextRoleCode("");
			efm.setTaskId("");
			efm.setNextTaskId("");
			efm.setWorkflowId(0);

			if (efm.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				efm.setRecordType("");
				getExtendedFieldMaintenanceDAO().save(efm, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				efm.setRecordType("");
				getExtendedFieldMaintenanceDAO().update(efm, TableType.MAIN_TAB);
			}
		}
		getExtendedFieldMaintenanceDAO().delete(efm, TableType.TEMP_TAB);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/*
	 * @param AuditHeader (auditHeader)
	 * 
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		ExtendedFieldMaintenance efm = (ExtendedFieldMaintenance) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		// Loan Extended field Details
		if (efm.getExtFieldRenderList() != null) {
			List<AuditDetail> details = efm.getAuditDetailMap().get("LoanMaintExtendedFieldDetails");
			auditDetails.addAll(extendedFieldDetailsService.delete(details, ExtendedFieldConstants.MODULE_LOAN,
					efm.getReference(), efm.getExtendedFieldHeader().getEvent(), "_Temp"));
		}

		getExtendedFieldMaintenanceDAO().delete(efm, TableType.TEMP_TAB);

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public ExtendedFieldMaintenanceDAO getExtendedFieldMaintenanceDAO() {
		return extendedFieldMaintenanceDAO;
	}

	public void setExtendedFieldMaintenanceDAO(ExtendedFieldMaintenanceDAO extendedFieldMaintenanceDAO) {
		this.extendedFieldMaintenanceDAO = extendedFieldMaintenanceDAO;
	}

	public ExtendedFieldRenderDAO getExtendedFieldRenderDAO() {
		return extendedFieldRenderDAO;
	}

	public void setExtendedFieldRenderDAO(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	@Override
	public ExtendedFieldMaintenance getExtendedFieldMaintenanceByFinRef(String finReference) {
		return getExtendedFieldMaintenanceDAO().getExtendedFieldMaintenanceByFinRef(finReference, "_VIEW");
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public ExtendedFieldDetailsService getExtendedFieldDetailsService() {
		return extendedFieldDetailsService;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

}
