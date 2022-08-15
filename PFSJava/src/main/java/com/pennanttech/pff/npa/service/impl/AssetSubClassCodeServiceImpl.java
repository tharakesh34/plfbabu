package com.pennanttech.pff.npa.service.impl;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

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
import com.pennanttech.pff.npa.dao.AssetSubClassCodeDAO;
import com.pennanttech.pff.npa.model.AssetSubClassCode;
import com.pennanttech.pff.npa.service.AssetSubClassCodeService;

public class AssetSubClassCodeServiceImpl extends GenericService<AssetSubClassCode>
		implements AssetSubClassCodeService {
	private static final Logger logger = LogManager.getLogger(AssetSubClassCodeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AssetSubClassCodeDAO assetSubClassCodeDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AssetSubClassCode assetClassCode = (AssetSubClassCode) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (assetClassCode.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (assetClassCode.isNewRecord()) {
			assetClassCode.setCreatedBy(assetClassCode.getLastMntBy());
			assetClassCode.setCreatedOn(assetClassCode.getLastMntOn());

			assetClassCode.setId(Long.parseLong(assetSubClassCodeDAO.save(assetClassCode, tableType)));
			auditHeader.getAuditDetail().setModelData(assetClassCode);
			auditHeader.setAuditReference(String.valueOf(assetClassCode.getId()));
		} else {
			assetSubClassCodeDAO.update(assetClassCode, tableType);
		}

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		businessValidation(auditHeader, "delete");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AssetSubClassCode assetClassCode = (AssetSubClassCode) auditHeader.getAuditDetail().getModelData();
		assetSubClassCodeDAO.delete(assetClassCode, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AssetSubClassCode getAssetClassCode(long id) {
		return assetSubClassCodeDAO.getAssetClassCode(id, TableType.VIEW.getSuffix());
	}

	public AssetSubClassCode getApprovedAssetClassCode(long id) {
		return assetSubClassCodeDAO.getAssetClassCode(id, TableType.AVIEW.getSuffix());
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AssetSubClassCode assetClassCode = ((AssetSubClassCode) auditHeader.getAuditDetail().getModelData())
				.copyEntity();

		String recordType = assetClassCode.getRecordType();

		if (PennantConstants.RECORD_TYPE_DEL.equals(recordType)) {
			tranType = PennantConstants.TRAN_DEL;
			assetSubClassCodeDAO.delete(assetClassCode, TableType.MAIN_TAB);
		} else {
			assetClassCode.setRoleCode("");
			assetClassCode.setNextRoleCode("");
			assetClassCode.setTaskId("");
			assetClassCode.setNextTaskId("");
			assetClassCode.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(recordType)) {
				tranType = PennantConstants.TRAN_ADD;
				assetClassCode.setRecordType("");

				assetClassCode.setApprovedBy(assetClassCode.getLastMntBy());
				assetClassCode.setApprovedOn(assetClassCode.getLastMntOn());
				assetSubClassCodeDAO.save(assetClassCode, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				assetClassCode.setRecordType("");
				assetSubClassCodeDAO.update(assetClassCode, TableType.MAIN_TAB);
			}
		}

		assetSubClassCodeDAO.delete(assetClassCode, TableType.TEMP_TAB);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(assetClassCode);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AssetSubClassCode assetClassCode = (AssetSubClassCode) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		assetSubClassCodeDAO.delete(assetClassCode, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	private void businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		AssetSubClassCode assetClassCode = (AssetSubClassCode) auditDetail.getModelData();

		String recordType = assetClassCode.getRecordType();
		String code = assetClassCode.getCode();
		boolean newRecord = assetClassCode.isNewRecord();

		if (newRecord && PennantConstants.RECORD_TYPE_NEW.equals(recordType)) {

			if (newRecord && assetSubClassCodeDAO.checkUniqueKey(code, TableType.VIEW)) {
				String[] parameters = new String[1];
				parameters[0] = PennantJavaUtil.getLabel("label_AssetCode") + ": " + code;
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}

			if (newRecord
					&& assetSubClassCodeDAO.checkUniqueKey(code, assetClassCode.getAssetClassId(), TableType.VIEW)) {
				String[] parameters = new String[2];
				parameters[0] = PennantJavaUtil.getLabel("label_AssetClassCode") + ": " + assetClassCode.getClassCode()
						+ " + ";
				parameters[1] = PennantJavaUtil.getLabel("label_AssetCode") + ": " + code;
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}
		}

		boolean checkDependency = assetSubClassCodeDAO.checkDependency(code);

		if (!assetClassCode.isActive() && checkDependency) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_AssetSubClassCodes_Class_Code") + ": " + code;
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41017", parameters, null));
		}

		if (PennantConstants.RECORD_TYPE_DEL.equals(assetClassCode.getRecordType())) {

			if (checkDependency) {
				auditDetail.setErrorDetail(new ErrorDetail("90290", null));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setAssetSubClassCode(AssetSubClassCodeDAO assetSubClassCodeDAO) {
		this.assetSubClassCodeDAO = assetSubClassCodeDAO;
	}

}