package com.pennanttech.pff.mmfl.cd.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
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
import com.pennanttech.pff.mmfl.cd.model.SchemeProductGroup;
import com.pennattech.pff.mmfl.cd.dao.SchemeProductGroupDAO;

public class SchemeProductGroupServiceImpl extends GenericService<SchemeProductGroup>
		implements SchemeProductGroupService {
	private static final Logger logger = LogManager.getLogger(SchemeProductGroupServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private SchemeProductGroupDAO schemeProductGroupDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);
		SchemeProductGroup schemeProductGroup = (SchemeProductGroup) auditHeader.getAuditDetail().getModelData();
		List<SchemeProductGroup> schemeProductGroupList = new ArrayList<>();
		if (schemeProductGroup.isSave()) {

			String promotionId = schemeProductGroup.getPromotionId();

			if (StringUtils.contains(promotionId, PennantConstants.DELIMITER_COMMA)) {
				String[] promotionIdList = schemeProductGroup.getPromotionId().split(PennantConstants.DELIMITER_COMMA);
				for (String id : promotionIdList) {
					SchemeProductGroup schProductGrp = new SchemeProductGroup();

					BeanUtils.copyProperties(schemeProductGroup, schProductGrp);
					schProductGrp.setPromotionId(id);
					auditHeader.getAuditDetail().setModelData(schProductGrp);
					auditHeader = businessValidation(auditHeader, "saveOrUpdate");

					if (!auditHeader.isNextProcess()) {
						logger.info(Literal.LEAVING);
						return auditHeader;
					}
					schemeProductGroupList.add(schProductGrp);

				}
			} else {
				auditHeader = businessValidation(auditHeader, "saveOrUpdate");
				if (!auditHeader.isNextProcess()) {
					logger.info(Literal.LEAVING);
					return auditHeader;
				}
				schemeProductGroupList.add(schemeProductGroup);
			}
		} else {
			auditHeader = businessValidation(auditHeader, "saveOrUpdate");
			if (!auditHeader.isNextProcess()) {
				logger.info(Literal.LEAVING);
				return auditHeader;
			}
		}

		TableType tableType = TableType.MAIN_TAB;
		if (schemeProductGroup.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (schemeProductGroup.isSave()) {
			saveSchemeProductGroupList(schemeProductGroupList, schemeProductGroup);
			auditHeader.getAuditDetail().setModelData(schemeProductGroup);
			auditHeader.setAuditReference(String.valueOf(schemeProductGroup.getSchemeProductGroupId()));
		} else if (schemeProductGroup.isNewRecord()) {
			schemeProductGroup
					.setSchemeProductGroupId(Long.parseLong(schemeProductGroupDAO.save(schemeProductGroup, tableType)));
			auditHeader.getAuditDetail().setModelData(schemeProductGroup);
			auditHeader.setAuditReference(String.valueOf(schemeProductGroup.getSchemeProductGroupId()));
		} else {
			schemeProductGroupDAO.update(schemeProductGroup, tableType);
		}
		auditHeaderDAO.addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	private void saveSchemeProductGroupList(List<SchemeProductGroup> schemeProductGroupList,
			SchemeProductGroup schemeProductGroup) {

		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isNotEmpty(schemeProductGroupList)) {
			for (SchemeProductGroup schProdGroup : schemeProductGroupList) {
				schProdGroup.setSchemeProductGroupId(schemeProductGroupDAO.getGrpIdSeq());
			}
			schemeProductGroup.setSchemeProductGroupId(schemeProductGroupDAO.getGrpIdSeq());
			schemeProductGroupDAO.saveProductGrpBatch(schemeProductGroupList, TableType.TEMP_TAB);

			logger.debug(Literal.LEAVING);
		}
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		SchemeProductGroup schemeProductGroup = (SchemeProductGroup) auditHeader.getAuditDetail().getModelData();
		schemeProductGroupDAO.delete(schemeProductGroup, TableType.MAIN_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public SchemeProductGroup getSchemeProductGroup(long id) {
		return schemeProductGroupDAO.getSchemeProductGroup(id, "_View");
	}

	public SchemeProductGroup getApprovedSchemeProductGroup(long id) {
		return schemeProductGroupDAO.getSchemeProductGroup(id, "_AView");
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

		SchemeProductGroup schemeProductGroup = new SchemeProductGroup();
		BeanUtils.copyProperties((SchemeProductGroup) auditHeader.getAuditDetail().getModelData(), schemeProductGroup);

		schemeProductGroupDAO.delete(schemeProductGroup, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(schemeProductGroup.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					schemeProductGroupDAO.getSchemeProductGroup(schemeProductGroup.getSchemeProductGroupId(), ""));
		}

		if (schemeProductGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			schemeProductGroupDAO.delete(schemeProductGroup, TableType.MAIN_TAB);
		} else {
			schemeProductGroup.setRoleCode("");
			schemeProductGroup.setNextRoleCode("");
			schemeProductGroup.setTaskId("");
			schemeProductGroup.setNextTaskId("");
			schemeProductGroup.setWorkflowId(0);

			if (schemeProductGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				schemeProductGroup.setRecordType("");
				schemeProductGroupDAO.save(schemeProductGroup, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				schemeProductGroup.setRecordType("");
				schemeProductGroupDAO.update(schemeProductGroup, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(schemeProductGroup);
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

		SchemeProductGroup schemeProductGroup = (SchemeProductGroup) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		schemeProductGroupDAO.delete(schemeProductGroup, TableType.TEMP_TAB);
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
		SchemeProductGroup schemeProductGroup = (SchemeProductGroup) auditDetail.getModelData();
		long code = schemeProductGroup.getProductGroupCode();
		String id = schemeProductGroup.getPromotionId();

		// Check the unique keys.
		if (schemeProductGroup.isNewRecord()
				&& PennantConstants.RECORD_TYPE_NEW.equals(schemeProductGroup.getRecordType())
				&& schemeProductGroupDAO.isDuplicateKey(schemeProductGroup,
						schemeProductGroup.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {

			String[] parameters = new String[1];

			parameters[0] = PennantJavaUtil.getLabel("listheader_SchemeId.label") + ": " + id + " and "
					+ PennantJavaUtil.getLabel("listheader_ProductGroupCode.label") + ": " + code;
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));

		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setSchemeProductGroupDAO(SchemeProductGroupDAO schemeProductGroupDAO) {
		this.schemeProductGroupDAO = schemeProductGroupDAO;
	}

}
