package com.pennant.pff.settlement.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.settlement.dao.SettlementDAO;
import com.pennant.pff.settlement.dao.SettlementTypeDetailDAO;
import com.pennant.pff.settlement.model.SettlementTypeDetail;
import com.pennant.pff.settlement.service.SettlementTypeDetailService;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class SettlementTypeDetailServiceImpl extends GenericService<SettlementTypeDetail>
		implements SettlementTypeDetailService {

	private static Logger logger = LogManager.getLogger(SettlementTypeDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private SettlementTypeDetailDAO settlementTypeDetailDAO;
	private SettlementDAO settlementDAO;

	public SettlementTypeDetailServiceImpl() {
		super();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		SettlementTypeDetail std = (SettlementTypeDetail) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;

		if (std.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (std.isNewRecord()) {
			std.setId(settlementTypeDetailDAO.save(std, tableType));
			auditHeader.getAuditDetail().setModelData(std);
			auditHeader.setAuditReference(String.valueOf(std.getId()));
		} else {
			settlementTypeDetailDAO.update(std, tableType);
		}

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		SettlementTypeDetail std = (SettlementTypeDetail) auditHeader.getAuditDetail().getModelData();

		settlementTypeDetailDAO.delete(std, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public SettlementTypeDetail getSettlementByCode(String code) {
		return settlementTypeDetailDAO.getSettlementByCode(code, "_View");
	}

	@Override
	public SettlementTypeDetail getSettlementById(long id) {
		return settlementTypeDetailDAO.getSettlementById(id, "_View");
	}

	@Override
	public AuditHeader doApprove(AuditHeader ah) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		ah = businessValidation(ah);

		if (!ah.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return ah;
		}
		SettlementTypeDetail std = new SettlementTypeDetail();
		BeanUtils.copyProperties(ah.getAuditDetail().getModelData(), std);

		settlementTypeDetailDAO.delete(std, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(std.getRecordType())) {
			ah.getAuditDetail().setBefImage(settlementTypeDetailDAO.getSettlementById(std.getId(), ""));
		}

		if (std.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			settlementTypeDetailDAO.delete(std, TableType.MAIN_TAB);
		} else {
			std.setRoleCode("");
			std.setNextRoleCode("");
			std.setTaskId("");
			std.setNextTaskId("");
			std.setWorkflowId(0);

			if (std.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				std.setRecordType("");
				settlementTypeDetailDAO.save(std, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				std.setRecordType("");
				settlementTypeDetailDAO.update(std, TableType.MAIN_TAB);
			}
		}

		ah.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(ah);

		ah.setAuditTranType(tranType);
		ah.getAuditDetail().setAuditTranType(tranType);
		ah.getAuditDetail().setModelData(std);

		logger.debug(Literal.LEAVING);
		return ah;
	}

	@Override
	public AuditHeader doReject(AuditHeader ah) {
		logger.debug(Literal.ENTERING);

		ah = businessValidation(ah);
		if (!ah.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return ah;
		}

		SettlementTypeDetail std = (SettlementTypeDetail) ah.getAuditDetail().getModelData();

		ah.setAuditTranType(PennantConstants.TRAN_WF);
		settlementTypeDetailDAO.delete(std, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(ah);

		logger.debug(Literal.LEAVING);
		return ah;
	}

	private AuditHeader businessValidation(AuditHeader ah) {
		logger.debug(Literal.ENTERING);

		AuditDetail ad = validation(ah.getAuditDetail(), ah.getUsrLanguage());
		ah.setAuditDetail(ad);
		ah.setErrorList(ad.getErrorDetails());
		ah = nextProcess(ah);

		logger.debug(Literal.LEAVING);
		return ah;
	}

	private AuditDetail validation(AuditDetail ad, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		SettlementTypeDetail std = (SettlementTypeDetail) ad.getModelData();
		String code = std.getSettlementCode();
		long id = std.getId();

		if (std.isNewRecord() && PennantConstants.RECORD_TYPE_NEW.equals(std.getRecordType()) && settlementTypeDetailDAO
				.isDuplicateKey(code, id, std.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_SettlementCode") + ": " + code;

			ad.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}

		if (PennantConstants.RECORD_TYPE_DEL.equals(std.getRecordType())) {
			boolean countrycount = settlementDAO.isSettlementTypeUsed(std.getId(), TableType.BOTH_TAB);
			if (countrycount) {
				String[] parameters = new String[1];
				parameters[0] = Labels.getLabel("label_SettlementCode") + ": " + code;

				ad.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", parameters, null));

			}
		}

		ad.setErrorDetails(ErrorUtil.getErrorDetails(ad.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return ad;
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setSettlementTypeDetailDAO(SettlementTypeDetailDAO settlementTypeDetailDAO) {
		this.settlementTypeDetailDAO = settlementTypeDetailDAO;
	}

	@Autowired
	public void setSettlementDAO(SettlementDAO settlementDAO) {
		this.settlementDAO = settlementDAO;
	}

}
