package com.pennanttech.pff.commodity.service;

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
import com.pennanttech.pff.commodity.dao.CommodityTypeDAO;
import com.pennanttech.pff.commodity.model.CommodityType;
import com.pennanttech.pff.core.TableType;

public class CommodityTypeServiceImpl extends GenericService<CommodityType> implements CommodityTypeService {
	private static final Logger logger = LogManager.getLogger(CommodityTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CommodityTypeDAO commodityTypeDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		CommodityType commodityType = (CommodityType) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;
		if (commodityType.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		if (commodityType.isNew()) {
			commodityType.setId(Long.parseLong(commodityTypeDAO.save(commodityType, tableType)));
			auditHeader.getAuditDetail().setModelData(commodityType);
			auditHeader.setAuditReference(String.valueOf(commodityType.getId()));
		} else {
			commodityTypeDAO.update(commodityType, tableType);
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

		CommodityType commodityTYpe = (CommodityType) auditHeader.getAuditDetail().getModelData();
		commodityTypeDAO.delete(commodityTYpe, TableType.MAIN_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public CommodityType getCommodityType(long id) {
		return commodityTypeDAO.getCommodityType(id, "_View");
	}

	public CommodityType getApprovedCommodityType(long id) {
		return commodityTypeDAO.getCommodityType(id, "_AView");
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

		CommodityType commodityType = new CommodityType();
		BeanUtils.copyProperties((CommodityType) auditHeader.getAuditDetail().getModelData(), commodityType);

		commodityTypeDAO.delete(commodityType, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(commodityType.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(commodityTypeDAO.getCommodityType(commodityType.getId(), ""));
		}

		if (commodityType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			commodityTypeDAO.delete(commodityType, TableType.MAIN_TAB);
		} else {
			commodityType.setRoleCode("");
			commodityType.setNextRoleCode("");
			commodityType.setTaskId("");
			commodityType.setNextTaskId("");
			commodityType.setWorkflowId(0);

			if (commodityType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				commodityType.setRecordType("");
				commodityTypeDAO.save(commodityType, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				commodityType.setRecordType("");
				commodityTypeDAO.update(commodityType, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(commodityType);
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

		CommodityType commodityType = (CommodityType) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		commodityTypeDAO.delete(commodityType, TableType.TEMP_TAB);
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
		CommodityType commodityType = (CommodityType) auditDetail.getModelData();
		String code = commodityType.getCode();

		// Check the unique keys.
		if (commodityType.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(commodityType.getRecordType())
				&& commodityTypeDAO.isDuplicateKey(commodityType,
						commodityType.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_StockCompanyDialogue_CompanyCode.value") + ": " + code;
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setCommodityTypeDAO(CommodityTypeDAO commodityTypeDAO) {
		this.commodityTypeDAO = commodityTypeDAO;
	}
}
