package com.pennanttech.pff.commodity.service;

import org.apache.log4j.Logger;
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
import com.pennanttech.pff.commodity.dao.CommoditiesDAO;
import com.pennanttech.pff.commodity.model.Commodity;
import com.pennanttech.pff.commodity.model.CommodityType;
import com.pennanttech.pff.core.TableType;

public class CommoditiesServiceImpl extends GenericService<CommodityType> implements CommoditiesService {
	private static final Logger logger = Logger.getLogger(CommoditiesServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CommoditiesDAO commoditiesDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		Commodity commodities = (Commodity) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (commodities.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (commodities.isNew()) {
			commodities.setId(Long.parseLong(commoditiesDAO.save(commodities, tableType)));
			auditHeader.getAuditDetail().setModelData(commodities);
			auditHeader.setAuditReference(String.valueOf(commodities.getId()));
		} else {
			commoditiesDAO.update(commodities, tableType);
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

		Commodity commodities = (Commodity) auditHeader.getAuditDetail().getModelData();
		commoditiesDAO.delete(commodities, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public Commodity getCommodities(long id) {
		return commoditiesDAO.getCommodities(id, "_View");
	}

	public Commodity getApprovedCommodities(long id) {
		return commoditiesDAO.getCommodities(id, "_AView");
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

		Commodity commodities = new Commodity();
		BeanUtils.copyProperties((CommodityType) auditHeader.getAuditDetail().getModelData(), commodities);

		if (!commodities.isUpload()) {
			commoditiesDAO.delete(commodities, TableType.TEMP_TAB);
		}

		if (!PennantConstants.RECORD_TYPE_NEW.equals(commodities.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(commoditiesDAO.getCommodities(commodities.getId(), ""));
		}

		if (commodities.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			commoditiesDAO.delete(commodities, TableType.MAIN_TAB);
		} else {
			commodities.setRoleCode("");
			commodities.setNextRoleCode("");
			commodities.setTaskId("");
			commodities.setNextTaskId("");
			commodities.setWorkflowId(0);

			if (commodities.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				commodities.setRecordType("");
				commoditiesDAO.save(commodities, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				commodities.setRecordType("");
				commoditiesDAO.update(commodities, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(commodities);
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

		Commodity commodities = (Commodity) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		commoditiesDAO.delete(commodities, TableType.TEMP_TAB);

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
		Commodity commodities = (Commodity) auditDetail.getModelData();
		String code = commodities.getCode();
		String hsnCode = commodities.getHSNCode();
		String commodityTYpeCode = commodities.getCommodityTypeCode();

		// Check the unique keys.
		if (commodities.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(commodities.getRecordType())
				&& commoditiesDAO.isDuplicateKey(commodities,
						commodities.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[3];
			parameters[0] = PennantJavaUtil.getLabel("label_CommoditiesDialogue_CommodityType.value") + ":"
					+ commodityTYpeCode;
			parameters[1] = PennantJavaUtil.getLabel("label_CommoditiesDialogue_CommodityCode.value") + ": " + code;
			parameters[2] = PennantJavaUtil.getLabel("label_CommoditiesDialogue_HSNCode.value") + ": " + hsnCode;

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setCommoditiesDAO(CommoditiesDAO commoditiesDAO) {
		this.commoditiesDAO = commoditiesDAO;
	}

}
