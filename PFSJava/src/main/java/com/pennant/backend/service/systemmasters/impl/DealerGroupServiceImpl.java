package com.pennant.backend.service.systemmasters.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.DealerGroupDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.DealerGroup;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.DealerGroupService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class DealerGroupServiceImpl extends GenericService<DealerGroup> implements DealerGroupService {

	private static final Logger logger = Logger.getLogger(DealerGroupServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private DealerGroupDAO dealerGroupDAO;

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public DealerGroupDAO getDealerGroupDAO() {
		return dealerGroupDAO;
	}

	public void setDealerGroupDAO(DealerGroupDAO dealerGroupDAO) {
		this.dealerGroupDAO = dealerGroupDAO;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		DealerGroup dealerGroup = (DealerGroup) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (dealerGroup.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (dealerGroup.isNew()) {
			dealerGroup.setId(Long.parseLong(getDealerGroupDAO().save(dealerGroup, tableType)));
			auditHeader.getAuditDetail().setModelData(dealerGroup);
			auditHeader.setAuditReference(String.valueOf(dealerGroup.getId()));
		} else {
			getDealerGroupDAO().update(dealerGroup, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public DealerGroup getDealerGroup(long id) {
		return getDealerGroupDAO().getDealerGroup(id, "_View");
	}

	@Override
	public DealerGroup getApprovedDealerGroup(long id) {
		return getDealerGroupDAO().getDealerGroup(id, "_AView");
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		DealerGroup dealerGroup = (DealerGroup) auditHeader.getAuditDetail().getModelData();
		getDealerGroupDAO().delete(dealerGroup, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
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

		DealerGroup dealerGroup = new DealerGroup();
		BeanUtils.copyProperties((DealerGroup) auditHeader.getAuditDetail().getModelData(), dealerGroup);

		getDealerGroupDAO().delete(dealerGroup, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(dealerGroup.getRecordType())) {
			/*
			 * auditHeader.getAuditDetail().setBefImage(((DealerGroupDAO)
			 * dealerGroup).getDealerGroup(dealerGroup.getId(), ""));
			 */
			auditHeader.getAuditDetail().setBefImage(dealerGroupDAO.getDealerGroup(dealerGroup.getDealerGroupId(), ""));
		}

		if (dealerGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getDealerGroupDAO().delete(dealerGroup, TableType.MAIN_TAB);
		} else {
			dealerGroup.setRoleCode("");
			dealerGroup.setNextRoleCode("");
			dealerGroup.setTaskId("");
			dealerGroup.setNextTaskId("");
			dealerGroup.setWorkflowId(0);

			if (dealerGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				dealerGroup.setRecordType("");
				getDealerGroupDAO().save(dealerGroup, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				dealerGroup.setRecordType("");
				getDealerGroupDAO().update(dealerGroup, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(dealerGroup);
		getAuditHeaderDAO().addAudit(auditHeader);

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

		DealerGroup dealerGroup = (DealerGroup) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getDealerGroupDAO().delete(dealerGroup, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

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
		DealerGroup dealerGroup = (DealerGroup) auditDetail.getModelData();

		String[] parameters = new String[2];
		parameters[0] = PennantJavaUtil.getLabel("label_dealercode") + ": " + dealerGroup.getDealerCode();
		parameters[1] = PennantJavaUtil.getLabel("label_groupId") + ": " + dealerGroup.getDealerCategoryId();

		// Check the unique keys.
		if (dealerGroup.isNew() && getDealerGroupDAO().isDuplicateKey(dealerGroup.getId(), dealerGroup.getDealerCode(),
				dealerGroup.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}
