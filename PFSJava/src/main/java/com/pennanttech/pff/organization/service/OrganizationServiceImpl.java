package com.pennanttech.pff.organization.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.organization.dao.OrganizationDAO;
import com.pennanttech.pff.organization.model.Organization;

public class OrganizationServiceImpl extends GenericService<Organization> implements OrganizationService {
	private static final Logger logger = Logger.getLogger(OrganizationServiceImpl.class);

	@Autowired
	private AuditHeaderDAO auditHeaderDAO;
	@Autowired
	private OrganizationDAO organizationDAO;
	@Autowired
	private ExtendedFieldDetailsService extendedFieldDetailsService;

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		Organization org = (Organization) auditHeader.getAuditDetail().getModelData();

		List<AuditDetail> auditDetails = new ArrayList<>();
		TableType tableType = TableType.MAIN_TAB;
		if (org.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (org.isNew()) {
			org.setId(organizationDAO.save(org, tableType));
			auditHeader.getAuditDetail().setModelData(org);
			auditHeader.setAuditReference(String.valueOf(org.getId()));
		} else {
			organizationDAO.update(org, tableType);
		}
		// Extended field Details
		if (org.getExtendedFieldRender() != null) {
			List<AuditDetail> details = org.getAuditDetailMap().get("ExtendedFieldDetails");
			StringBuilder tableName = new StringBuilder();
			tableName.append(ExtendedFieldConstants.MODULE_ORGANIZATION);
			tableName.append("_");
			tableName.append(org.getExtendedFieldHeader().getSubModuleName());
			tableName.append("_ED");
			ExtendedFieldRender extendedFieldRender = (ExtendedFieldRender) details.get(0).getModelData();
			if (Long.parseLong(extendedFieldRender.getReference()) == 0){
				extendedFieldRender.setReference(String.valueOf(org.getId()));
			}
			details.get(0).setModelData(extendedFieldRender);
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details, tableName.toString(),
					tableType.getSuffix());
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public Organization getOrganization(long id, String type) {
		return organizationDAO.getOrganization(id, type);
	}

	@Override
	public Organization getApprovedOrganization(long id) {
		return organizationDAO.getOrganization(id, "");
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		Organization organization = (Organization) auditHeader.getAuditDetail().getModelData();
		auditDetails.addAll(deleteChilds(organization, "", auditHeader.getAuditTranType()));
		organizationDAO.delete(organization, TableType.MAIN_TAB);

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";

		List<AuditDetail> auditDetails = new ArrayList<>();
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		Organization org = new Organization();
		BeanUtils.copyProperties((Organization) auditHeader.getAuditDetail().getModelData(), org);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(org.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(organizationDAO.getOrganization(org.getId(), ""));
		}

		if (org.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(deleteChilds(org, "", tranType));
			organizationDAO.delete(org, TableType.MAIN_TAB);
		} else {
			org.setRoleCode("");
			org.setNextRoleCode("");
			org.setTaskId("");
			org.setNextTaskId("");
			org.setWorkflowId(0);

			if (org.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				org.setRecordType("");
				organizationDAO.save(org, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				org.setRecordType("");
				organizationDAO.update(org, TableType.MAIN_TAB);
			}

			// Extended field Details
			if (org.getExtendedFieldRender() != null) {
				List<AuditDetail> details = org.getAuditDetailMap().get("ExtendedFieldDetails");

				// Table Name
				StringBuilder tableName = new StringBuilder();
				tableName.append(ExtendedFieldConstants.MODULE_ORGANIZATION);
				tableName.append("_");
				tableName.append(org.getExtendedFieldHeader().getSubModuleName());
				tableName.append("_ed");

				details = extendedFieldDetailsService.processingExtendedFieldDetailList(details, tableName.toString(),
						TableType.MAIN_TAB.getSuffix());
				auditDetails.addAll(details);
			}
		}

		List<AuditDetail> auditDetailList = new ArrayList<>();

		String[] fields = PennantJavaUtil.getFieldDetails(new Organization(), org.getExcludeFields());
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		auditDetailList.addAll(deleteChilds(org, "_Temp", auditHeader.getAuditTranType()));
		organizationDAO.delete(org, TableType.TEMP_TAB);

		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], org.getBefImage(), org));
		auditHeader.setAuditDetails(auditDetailList);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(org);
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		List<AuditDetail> auditDetails = new ArrayList<>();
		Organization organization = (Organization) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		String[] fields = PennantJavaUtil.getFieldDetails(new Organization(), organization.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				organization.getBefImage(), organization));

		auditDetails.addAll(deleteChilds(organization, "_Temp", auditHeader.getAuditTranType()));
		organizationDAO.delete(organization, TableType.TEMP_TAB);

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	// Method for Deleting all records related to Organization setup in _Temp/Main tables depend on method type
	public List<AuditDetail> deleteChilds(Organization org, String tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<>();

		// Extended field Render Details.
		List<AuditDetail> extendedDetails = org.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && !extendedDetails.isEmpty()) {
			// Table Name
			StringBuilder tableName = new StringBuilder();
			tableName.append(ExtendedFieldConstants.MODULE_ORGANIZATION);
			tableName.append("_");
			tableName.append(org.getExtendedFieldHeader().getSubModuleName());
			tableName.append("_ED");
			auditList.addAll(extendedFieldDetailsService.delete(org.getExtendedFieldHeader(),
					String.valueOf(org.getOrganizationId()), tableName.toString(), tableType, auditTranType,
					extendedDetails));
		}

		logger.debug(Literal.LEAVING);
		return auditList;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		getAuditDetails(auditHeader, method);
		Organization organization = (Organization) auditDetail.getModelData();
		String usrLanguage = organization.getUserDetails().getLanguage();

		// Extended field details Validation
		if (organization.getExtendedFieldRender() != null) {
			List<AuditDetail> details = organization.getAuditDetailMap().get("ExtendedFieldDetails");
			ExtendedFieldHeader extHeader = organization.getExtendedFieldHeader();

			StringBuilder tableName = new StringBuilder();
			tableName.append(ExtendedFieldConstants.MODULE_ORGANIZATION);
			tableName.append("_");
			tableName.append(extHeader.getSubModuleName());
			tableName.append("_ED");

			details = extendedFieldDetailsService.vaildateDetails(details, method, usrLanguage, tableName.toString());
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		Organization organization = (Organization) auditDetail.getModelData();

		// Check the unique keys.
		if (organization.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(organization.getRecordType())
				&& organizationDAO.isDuplicateKey(organization.getCustId(), organization.getCode(),
						organization.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_OrganizationDialog_CIF.value") + ": " + organization.getCif();
			parameters[1] = PennantJavaUtil.getLabel("label_OrganizationDialog_Code.value") + ": "
					+ organization.getCode();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));


		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

		Organization organization = (Organization) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (organization.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Extended Field Details
		if (organization.getExtendedFieldRender() != null) {
			auditDetailMap.put("ExtendedFieldDetails", extendedFieldDetailsService
					.setExtendedFieldsAuditData(organization.getExtendedFieldRender(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		organization.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(organization);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");
		return auditHeader;
	}
}
