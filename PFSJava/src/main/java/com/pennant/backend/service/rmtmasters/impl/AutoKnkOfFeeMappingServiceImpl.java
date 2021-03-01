package com.pennant.backend.service.rmtmasters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.AutoKnkOfFeeMappingDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.AutoKnockOffFeeMapping;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.AutoKnkOfFeeMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class AutoKnkOfFeeMappingServiceImpl extends GenericService<AutoKnockOffFeeMapping>
		implements AutoKnkOfFeeMappingService {
	private static Logger logger = LogManager.getLogger(AutoKnkOfFeeMappingServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AutoKnkOfFeeMappingDAO autoKnkOfFeeMappingDAO;

	public AutoKnkOfFeeMappingServiceImpl() {
		super();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, PennantConstants.method_saveOrUpdate);

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		TableType tableType = null;
		AutoKnockOffFeeMapping feeMapping = (AutoKnockOffFeeMapping) auditHeader.getAuditDetail().getModelData();

		if (feeMapping.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (feeMapping.isNew()) {
			feeMapping.setId(autoKnkOfFeeMappingDAO.save(feeMapping, tableType));
			auditHeader.getAuditDetail().setModelData(feeMapping);
			auditHeader.setAuditReference(String.valueOf(feeMapping.getId()));
		} else {
			autoKnkOfFeeMappingDAO.update(feeMapping, tableType);
		}

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";

		AutoKnockOffFeeMapping feeMapping = new AutoKnockOffFeeMapping("");
		BeanUtils.copyProperties(auditHeader.getAuditDetail().getModelData(), feeMapping);

		if (PennantConstants.RECORD_TYPE_DEL.equals(feeMapping.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			autoKnkOfFeeMappingDAO.delete(feeMapping, TableType.MAIN_TAB);
		} else {
			feeMapping.setRoleCode("");
			feeMapping.setNextRoleCode("");
			feeMapping.setTaskId("");
			feeMapping.setNextTaskId("");
			feeMapping.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(feeMapping.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				feeMapping.setRecordType("");
				autoKnkOfFeeMappingDAO.save(feeMapping, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				feeMapping.setRecordType("");
				autoKnkOfFeeMappingDAO.update(feeMapping, TableType.MAIN_TAB);
			}
		}

		if (feeMapping.isWorkflow()) {
			autoKnkOfFeeMappingDAO.delete(feeMapping, TableType.TEMP_TAB);
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(feeMapping);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, PennantConstants.method_doReject);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		AutoKnockOffFeeMapping feeMapping = (AutoKnockOffFeeMapping) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		autoKnkOfFeeMappingDAO.delete(feeMapping, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	@Override
	public List<AuditDetail> setKnockOffMappingAuditData(List<AutoKnockOffFeeMapping> knkOffMappingList,
			String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		String[] fields = PennantJavaUtil.getFieldDetails(new AutoKnockOffFeeMapping(),
				new AutoKnockOffFeeMapping().getExcludeFields());

		for (int i = 0; i < knkOffMappingList.size(); i++) {
			AutoKnockOffFeeMapping feeMapping = knkOffMappingList.get(i);

			String recordType = feeMapping.getRecordType();
			if (StringUtils.isEmpty(recordType)) {
				continue;
			}

			boolean isRcdType = false;
			if (PennantConstants.RCD_ADD.equalsIgnoreCase(recordType)) {
				feeMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(recordType)) {
				feeMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(recordType)) {
				feeMapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if (PennantConstants.method_saveOrUpdate.equals(method) && isRcdType) {
				feeMapping.setNewRecord(true);
			}
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(recordType)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(recordType)
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(recordType)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			auditDetails.add(
					new AuditDetail(auditTranType, i + 1, fields[0], fields[1], feeMapping.getBefImage(), feeMapping));
		}

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	@Override
	public List<AuditDetail> processKnockOffMappingDetails(List<AuditDetail> auditDetails, TableType type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			AutoKnockOffFeeMapping feeMapping = (AutoKnockOffFeeMapping) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(type.getSuffix())) {
				approveRec = true;
				feeMapping.setRoleCode("");
				feeMapping.setNextRoleCode("");
				feeMapping.setTaskId("");
				feeMapping.setNextTaskId("");
				feeMapping.setWorkflowId(0);
			}
			if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(feeMapping.getRecordType())) {
				deleteRecord = true;
			} else if (feeMapping.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(feeMapping.getRecordType())) {
					feeMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(feeMapping.getRecordType())) {
					feeMapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(feeMapping.getRecordType())) {
					feeMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(feeMapping.getRecordType())) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(feeMapping.getRecordType())) {
				updateRecord = true;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(feeMapping.getRecordType())) {
				if (approveRec) {
					deleteRecord = true;
				} else if (feeMapping.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = feeMapping.getRecordType();
				recordStatus = feeMapping.getRecordStatus();
				feeMapping.setRecordType("");
				feeMapping.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				autoKnkOfFeeMappingDAO.save(feeMapping, type);
			}

			if (updateRecord) {
				autoKnkOfFeeMappingDAO.update(feeMapping, type);
			}

			if (deleteRecord) {
				autoKnkOfFeeMappingDAO.delete(feeMapping, type);
			}

			if (approveRec) {
				feeMapping.setRecordType(rcdType);
				feeMapping.setRecordStatus(recordStatus);
			}

			auditDetails.get(i).setModelData(feeMapping);
		}

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	@Override
	public AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		AutoKnockOffFeeMapping feeMapping = (AutoKnockOffFeeMapping) auditDetail.getModelData();

		// Check the unique keys.
		if (feeMapping.isNew() && autoKnkOfFeeMappingDAO.isDuplicatefeeTypeId(feeMapping.getId(),
				feeMapping.getFeeTypeId(), feeMapping.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_FeeTypeId") + ": " + feeMapping.getFeeTypeId();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		if (feeMapping.isNew() && autoKnkOfFeeMappingDAO.isDuplicatefeeTypeOrder(feeMapping.getId(),
				feeMapping.getFeeOrder(), feeMapping.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_FeeTypeOrder") + ": " + feeMapping.getFeeOrder();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);

		return auditDetail;
	}

	@Override
	public List<AuditDetail> delete(List<AutoKnockOffFeeMapping> feeMappingList, TableType tableType,
			String auditTranType, String payableType) {
		List<AuditDetail> auditDetails = new ArrayList<>();

		if (feeMappingList == null || feeMappingList.isEmpty()) {
			return auditDetails;
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new AutoKnockOffFeeMapping(),
				new AutoKnockOffFeeMapping().getExcludeFields());
		for (int i = 0; i < feeMappingList.size(); i++) {
			AutoKnockOffFeeMapping autoKnockOffFeeMapping = feeMappingList.get(i);
			if (StringUtils.isNotEmpty(autoKnockOffFeeMapping.getRecordType())
					|| StringUtils.isEmpty(tableType.getSuffix())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						autoKnockOffFeeMapping.getBefImage(), autoKnockOffFeeMapping));
			}
		}
		autoKnkOfFeeMappingDAO.deleteByPayableType(payableType, tableType);

		return auditDetails;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setAutoKnkOfFeeMappingDAO(AutoKnkOfFeeMappingDAO autoKnkOfFeeMappingDAO) {
		this.autoKnkOfFeeMappingDAO = autoKnkOfFeeMappingDAO;
	}

}
