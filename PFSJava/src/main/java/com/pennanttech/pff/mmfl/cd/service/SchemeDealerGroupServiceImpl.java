package com.pennanttech.pff.mmfl.cd.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.mmfl.cd.model.SchemeDealerGroup;
import com.pennattech.pff.mmfl.cd.dao.SchemeDealerGroupDAO;

public class SchemeDealerGroupServiceImpl extends GenericService<SchemeDealerGroup>
		implements SchemeDealerGroupService {
	private static final Logger logger = LogManager.getLogger(SchemeDealerGroupServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private SchemeDealerGroupDAO schemeDealerGroupDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		SchemeDealerGroup schemeDealerGroup = (SchemeDealerGroup) auditHeader.getAuditDetail().getModelData();
		List<SchemeDealerGroup> schDealerGrpList = new ArrayList<>();
		if (schemeDealerGroup.isSave()) {
			String promoIds = schemeDealerGroup.getPromotionId();

			if (StringUtils.contains(promoIds, PennantConstants.DELIMITER_COMMA)) {
				String[] promoIdsList = schemeDealerGroup.getPromotionId().split(PennantConstants.DELIMITER_COMMA);
				for (String id : promoIdsList) {
					SchemeDealerGroup schDealerGrp = new SchemeDealerGroup();

					BeanUtils.copyProperties(schemeDealerGroup, schDealerGrp);

					schDealerGrp.setPromotionId(id);
					auditHeader.getAuditDetail().setModelData(schDealerGrp);
					auditHeader = businessValidation(auditHeader, "saveOrUpdate");

					if (!auditHeader.isNextProcess()) {
						logger.info(Literal.LEAVING);
						return auditHeader;
					}
					schDealerGrpList.add(schDealerGrp);
				}
			} else {
				auditHeader = businessValidation(auditHeader, "saveOrUpdate");
				if (!auditHeader.isNextProcess()) {
					logger.info(Literal.LEAVING);
					return auditHeader;
				}
				schDealerGrpList.add(schemeDealerGroup);
			}
		} else {
			auditHeader = businessValidation(auditHeader, "saveOrUpdate");
			if (!auditHeader.isNextProcess()) {
				logger.info(Literal.LEAVING);
				return auditHeader;
			}
		}

		TableType tableType = TableType.MAIN_TAB;
		if (schemeDealerGroup.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		if (schemeDealerGroup.isSave()) {
			saveSchDealerGrpList(schDealerGrpList, schemeDealerGroup);
			auditHeader.getAuditDetail().setModelData(schemeDealerGroup);
			auditHeader.setAuditReference(String.valueOf(schemeDealerGroup.getSchemeDealerGroupId()));
		} else if (schemeDealerGroup.isNew()) {
			schemeDealerGroup
					.setSchemeDealerGroupId(Long.parseLong(schemeDealerGroupDAO.save(schemeDealerGroup, tableType)));
			auditHeader.getAuditDetail().setModelData(schemeDealerGroup);
			auditHeader.setAuditReference(String.valueOf(schemeDealerGroup.getSchemeDealerGroupId()));
		} else {
			schemeDealerGroupDAO.update(schemeDealerGroup, tableType);
		}
		auditHeaderDAO.addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		SchemeDealerGroup schemeDealerGroup = (SchemeDealerGroup) auditHeader.getAuditDetail().getModelData();
		schemeDealerGroupDAO.delete(schemeDealerGroup, TableType.MAIN_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public SchemeDealerGroup getSchemeDealerGroup(long id) {
		return schemeDealerGroupDAO.getSchemeDealerGroup(id, "_View");
	}

	public SchemeDealerGroup getApprovedSchemeDealerGroup(long id) {
		return schemeDealerGroupDAO.getSchemeDealerGroup(id, "_AView");
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		SchemeDealerGroup schemeDealerGroup = new SchemeDealerGroup();
		BeanUtils.copyProperties((SchemeDealerGroup) auditHeader.getAuditDetail().getModelData(), schemeDealerGroup);

		schemeDealerGroupDAO.delete(schemeDealerGroup, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(schemeDealerGroup.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					schemeDealerGroupDAO.getSchemeDealerGroup(schemeDealerGroup.getSchemeDealerGroupId(), ""));
		}

		if (schemeDealerGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			schemeDealerGroupDAO.delete(schemeDealerGroup, TableType.MAIN_TAB);
		} else {
			schemeDealerGroup.setRoleCode("");
			schemeDealerGroup.setNextRoleCode("");
			schemeDealerGroup.setTaskId("");
			schemeDealerGroup.setNextTaskId("");
			schemeDealerGroup.setWorkflowId(0);

			if (schemeDealerGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				schemeDealerGroup.setRecordType("");
				schemeDealerGroupDAO.save(schemeDealerGroup, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				schemeDealerGroup.setRecordType("");
				schemeDealerGroupDAO.update(schemeDealerGroup, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(schemeDealerGroup);
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

		SchemeDealerGroup schemeDealerGroup = (SchemeDealerGroup) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		schemeDealerGroupDAO.delete(schemeDealerGroup, TableType.TEMP_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		SchemeDealerGroup schemeDealerGroup = (SchemeDealerGroup) auditDetail.getModelData();
		long code = schemeDealerGroup.getDealerGroupCode();
		String id = schemeDealerGroup.getPromotionId();

		// Check the unique keys.
		if (schemeDealerGroup.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(schemeDealerGroup.getRecordType())
				&& schemeDealerGroupDAO.isDuplicateKey(schemeDealerGroup,
						schemeDealerGroup.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("listheader_SchemeId.label") + ": " + id + " and "
					+ PennantJavaUtil.getLabel("listheader_DealerGroupCode.label") + ": " + code;
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private void saveSchDealerGrpList(List<SchemeDealerGroup> schDealerGrpList, SchemeDealerGroup schemeDealerGroup) {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isNotEmpty(schDealerGrpList)) {
			for (SchemeDealerGroup schDealerGrp : schDealerGrpList) {
				schDealerGrp.setSchemeDealerGroupId(schemeDealerGroupDAO.getGrpIdSeq());
			}
			schemeDealerGroup.setSchemeDealerGroupId(schemeDealerGroupDAO.getGrpIdSeq());
			schemeDealerGroupDAO.saveDealerGrpBatch(schDealerGrpList, TableType.TEMP_TAB);
		}
		logger.debug(Literal.LEAVING);
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setSchemeDealerGroupDAO(SchemeDealerGroupDAO schemeDealerGroupDAO) {
		this.schemeDealerGroupDAO = schemeDealerGroupDAO;
	}

}
