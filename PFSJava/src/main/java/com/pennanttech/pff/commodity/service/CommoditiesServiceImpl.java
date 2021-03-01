package com.pennanttech.pff.commodity.service;

import org.apache.commons.lang.StringUtils;
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
import com.pennanttech.pff.commodity.dao.CommoditiesDAO;
import com.pennanttech.pff.commodity.model.Commodity;
import com.pennanttech.pff.commodity.model.CommodityType;
import com.pennanttech.pff.core.TableType;

public class CommoditiesServiceImpl extends GenericService<CommodityType> implements CommoditiesService {
	private static final Logger logger = LogManager.getLogger(CommoditiesServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CommoditiesDAO commoditiesDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		Commodity Commodity = (Commodity) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;
		if (Commodity.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		if (Commodity.isNew()) {
			Commodity.setId(Long.parseLong(commoditiesDAO.save(Commodity, tableType)));
			auditHeader.getAuditDetail().setModelData(Commodity);
			auditHeader.setAuditReference(String.valueOf(Commodity.getId()));
		} else {
			commoditiesDAO.update(Commodity, tableType);
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
		Commodity commodity = (Commodity) auditHeader.getAuditDetail().getModelData();
		commoditiesDAO.delete(commodity, TableType.MAIN_TAB);
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
		Commodity commodity = new Commodity();
		BeanUtils.copyProperties((CommodityType) auditHeader.getAuditDetail().getModelData(), commodity);
		if (!commodity.isUpload()) {
			commoditiesDAO.delete(commodity, TableType.TEMP_TAB);
		}
		if (!PennantConstants.RECORD_TYPE_NEW.equals(commodity.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(commoditiesDAO.getCommodities(commodity.getId(), ""));
		}
		if (commodity.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			commoditiesDAO.delete(commodity, TableType.MAIN_TAB);
		} else {
			commodity.setRoleCode("");
			commodity.setNextRoleCode("");
			commodity.setTaskId("");
			commodity.setNextTaskId("");
			commodity.setWorkflowId(0);
			if (commodity.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				commodity.setRecordType("");
				commoditiesDAO.save(commodity, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				commodity.setRecordType("");
				commoditiesDAO.update(commodity, TableType.MAIN_TAB);
			}
		}
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(commodity);
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

		Commodity commodity = (Commodity) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		commoditiesDAO.delete(commodity, TableType.TEMP_TAB);

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
		Commodity commodity = (Commodity) auditDetail.getModelData();
		String code = commodity.getCode();
		String hsnCode = commodity.getHSNCode();

		// Check the unique keys.

		if (commodity.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(commodity.getRecordType())) {
			if (commoditiesDAO.isDuplicateKey(commodity,
					commodity.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
				String[] parameters = new String[2];
				parameters[0] = PennantJavaUtil.getLabel("label_CommoditiesDialogue_CommodityCode.value");
				parameters[1] = PennantJavaUtil.getLabel("label_CommoditiesDialogue_HSNCode.value");

				String[] value = new String[2];
				value[0] = code;
				value[1] = hsnCode;
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41015", parameters, value));
			}

			String tempCode = commoditiesDAO.getCommodityCode(commodity.getHSNCode());

			if (StringUtils.isNotBlank(tempCode) && !StringUtils.equals(tempCode, code)) {
				String[] param = new String[2];
				param[0] = PennantJavaUtil.getLabel("label_CommoditiesDialogue_HSNCode.value") + ":" + hsnCode;
				param[1] = PennantJavaUtil.getLabel("label_CommoditiesDialogue_CommodityCode.value") + ":" + tempCode;
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41018", param, param));
			}

			String tempHsnCode = commoditiesDAO.getCommodityHSNCode(commodity.getCode());

			if (StringUtils.isNotBlank(tempHsnCode) && !StringUtils.equals(tempHsnCode, hsnCode)) {
				String[] param = new String[2];
				param[0] = PennantJavaUtil.getLabel("label_CommoditiesDialogue_CommodityCode.value") + ":" + code;
				param[1] = PennantJavaUtil.getLabel("label_CommoditiesDialogue_HSNCode.value") + ":" + tempHsnCode;
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41018", param, param));
			}
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
