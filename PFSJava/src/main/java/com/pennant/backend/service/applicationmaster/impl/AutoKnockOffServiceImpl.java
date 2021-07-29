package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.AutoKnkOfFeeMappingDAO;
import com.pennant.backend.dao.applicationmaster.AutoKnockOffDAO;
import com.pennant.backend.dao.applicationmaster.LoanTypeKnockOffDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.AutoKnockOff;
import com.pennant.backend.model.finance.AutoKnockOffFeeMapping;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.AutoKnockOffService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class AutoKnockOffServiceImpl extends GenericService<AutoKnockOff> implements AutoKnockOffService {
	private static Logger logger = LogManager.getLogger(AutoKnockOffServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AutoKnockOffDAO autoKnockOffDAO;
	private AutoKnkOfFeeMappingDAO autoKnkOfFeeMappingDAO;
	private LoanTypeKnockOffDAO loanTypeKnockOffDAO;

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		auditHeader = businessValidation(auditHeader, PennantConstants.method_saveOrUpdate);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		TableType tableType = TableType.MAIN_TAB;
		AutoKnockOff knockOff = (AutoKnockOff) auditHeader.getAuditDetail().getModelData();

		if (knockOff.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (knockOff.isNewRecord()) {
			knockOff.setId(Long.valueOf(autoKnockOffDAO.save(knockOff, tableType)));
			auditHeader.getAuditDetail().setModelData(knockOff);
			auditHeader.setAuditReference(String.valueOf(knockOff.getId()));
		} else {
			autoKnockOffDAO.update(knockOff, tableType);
		}

		//Retrieving List of Audit Details For check list detail  related modules
		if (knockOff.getMappingList() != null && knockOff.getMappingList().size() > 0) {
			List<AuditDetail> details = knockOff.getLovDescAuditDetailMap().get("AutoKnockOffFeeMapping");
			details = processingFeeMappingList(details, tableType, knockOff.getId());
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, PennantConstants.RECORD_TYPE_DEL);
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AutoKnockOff knock = (AutoKnockOff) auditHeader.getAuditDetail().getModelData();
		autoKnockOffDAO.delete(knock, TableType.MAIN_TAB);
		auditHeader.setAuditDetails(
				getListAuditDetails(listDeletion(knock, TableType.MAIN_TAB, auditHeader.getAuditTranType())));
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);

		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<>();

		auditHeader = businessValidation(auditHeader, PennantConstants.method_doApprove);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}
		TableType tableType = TableType.MAIN_TAB;
		AutoKnockOff knockOff = new AutoKnockOff();
		BeanUtils.copyProperties(auditHeader.getAuditDetail().getModelData(), knockOff);

		if (knockOff.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			List<AuditDetail> listDeletion = listDeletion(knockOff, tableType, auditHeader.getAuditTranType());
			autoKnockOffDAO.delete(knockOff, tableType);
			auditDetails.addAll(listDeletion);
		} else {
			knockOff.setRoleCode("");
			knockOff.setNextRoleCode("");
			knockOff.setTaskId("");
			knockOff.setNextTaskId("");
			knockOff.setWorkflowId(0);

			if (knockOff.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				knockOff.setRecordType("");
				autoKnockOffDAO.save(knockOff, tableType);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				knockOff.setRecordType("");
				autoKnockOffDAO.update(knockOff, tableType);
			}
		}

		//Retrieving List of Audit Details For checkList details modules
		if (knockOff.getMappingList() != null && knockOff.getMappingList().size() > 0) {
			List<AuditDetail> details = knockOff.getLovDescAuditDetailMap().get("AutoKnockOffFeeMapping");
			details = processingFeeMappingList(details, tableType, knockOff.getId());
			auditDetails.addAll(details);
		}
		auditHeader.setAuditDetails(
				getListAuditDetails(listDeletion(knockOff, TableType.TEMP_TAB, auditHeader.getAuditTranType())));
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(knockOff);

		auditHeaderDAO.addAudit(auditHeader);

		autoKnockOffDAO.delete(knockOff, TableType.TEMP_TAB);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, PennantConstants.method_doApprove);
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AutoKnockOff aKnockOff = (AutoKnockOff) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(
				getListAuditDetails(listDeletion(aKnockOff, TableType.TEMP_TAB, auditHeader.getAuditTranType())));
		autoKnockOffDAO.delete(aKnockOff, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);

		return auditHeader;
	}

	@Override
	public AutoKnockOff getAutoKnockOffCode(long id) {
		AutoKnockOff knockOff = autoKnockOffDAO.getAutoKnockOffCode(id, TableType.VIEW);
		knockOff.setMappingList(getAutoKnkOfFeeMappingDAO().getKnockOffMappingListByPayableName(id, TableType.VIEW));
		return knockOff;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private List<AuditDetail> processingFeeMappingList(List<AuditDetail> auditDetails, TableType type,
			long knockOffId) {
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
			feeMapping.setKnockOffId(knockOffId);
			if (StringUtils.isEmpty(type.getSuffix())) {
				approveRec = true;
				feeMapping.setVersion(feeMapping.getVersion() + 1);
				feeMapping.setRoleCode("");
				feeMapping.setNextRoleCode("");
				feeMapping.setTaskId("");
				feeMapping.setNextTaskId("");
			}

			feeMapping.setWorkflowId(0);

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
				} else if (feeMapping.isNewRecord()) {
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

				getAutoKnkOfFeeMappingDAO().save(feeMapping, type);
			}

			if (updateRecord) {
				getAutoKnkOfFeeMappingDAO().update(feeMapping, type);
			}

			if (deleteRecord) {
				getAutoKnkOfFeeMappingDAO().delete(feeMapping, type);
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

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		AutoKnockOff knockOff = (AutoKnockOff) auditDetail.getModelData();
		if (knockOff.isNewRecord() && autoKnockOffDAO.isDuplicateKey(knockOff.getId(), knockOff.getCode(),
				knockOff.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_knocofCode") + ": " + knockOff.getCode();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		if (StringUtils.equals(knockOff.getRecordType(), PennantConstants.RECORD_TYPE_DEL)
				&& loanTypeKnockOffDAO.isExistKnockoffCode(knockOff.getId(), TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_knocofCode") + ": " + knockOff.getCode()
					+ " having child Records in LoanType KnockOff Master.It can't be Deleted";
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);

		return auditDetail;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();
		AutoKnockOff knockOff = (AutoKnockOff) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ((PennantConstants.method_saveOrUpdate.equals(method) || PennantConstants.method_doApprove.equals(method)
				|| PennantConstants.method_doReject.equals(method)) && knockOff.isWorkflow()) {
			auditTranType = PennantConstants.TRAN_WF;
		}

		if (knockOff.getMappingList() == null && knockOff.getMappingList().isEmpty()) {
			return auditHeader;
		}

		auditDetailMap.put("AutoKnockOffFeeMapping", setFeeMappingAuditData(knockOff, auditTranType, method));
		auditDetails.addAll(auditDetailMap.get("AutoKnockOffFeeMapping"));

		knockOff.setLovDescAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(knockOff);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private List<AuditDetail> setFeeMappingAuditData(AutoKnockOff knockOff, String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		String[] fields = PennantJavaUtil.getFieldDetails(new AutoKnockOffFeeMapping(),
				new AutoKnockOffFeeMapping().getExcludeFields());

		for (int i = 0; i < knockOff.getMappingList().size(); i++) {
			AutoKnockOffFeeMapping feeMapping = knockOff.getMappingList().get(i);

			// Skipping the process of current iteration when the child was not modified to avoid unnecessary processing
			if (StringUtils.isEmpty(feeMapping.getRecordType())) {
				continue;
			}

			feeMapping.setWorkflowId(knockOff.getWorkflowId());
			feeMapping.setKnockOffId(knockOff.getId());

			boolean isRcdType = false;

			if (PennantConstants.RCD_ADD.equalsIgnoreCase(feeMapping.getRecordType())) {
				feeMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(feeMapping.getRecordType())) {
				feeMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (knockOff.isWorkflow()) {
					isRcdType = true;
				}
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(feeMapping.getRecordType())) {
				feeMapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (PennantConstants.method_saveOrUpdate.equals(method) && isRcdType) {
				feeMapping.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(feeMapping.getRecordType())) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(feeMapping.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(feeMapping.getRecordType())) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			feeMapping.setRecordStatus(knockOff.getRecordStatus());
			feeMapping.setUserDetails(knockOff.getUserDetails());
			feeMapping.setLastMntOn(knockOff.getLastMntOn());
			feeMapping.setLastMntBy(knockOff.getLastMntBy());
			auditDetails.add(
					new AuditDetail(auditTranType, i + 1, fields[0], fields[1], feeMapping.getBefImage(), feeMapping));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public List<AuditDetail> listDeletion(AutoKnockOff knockOff, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<>();
		if (knockOff.getMappingList() == null && knockOff.getMappingList().isEmpty()) {
			return auditList;
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new AutoKnockOffFeeMapping());
		for (int i = 0; i < knockOff.getMappingList().size(); i++) {
			AutoKnockOffFeeMapping feeMapping = knockOff.getMappingList().get(i);
			if (!StringUtils.isEmpty(feeMapping.getRecordType()) || StringUtils.isEmpty(tableType.getSuffix())) {
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], feeMapping.getBefImage(),
						feeMapping));
			}
		}
		AutoKnockOffFeeMapping child = knockOff.getMappingList().get(0);
		getAutoKnkOfFeeMappingDAO().delete(child.getKnockOffId(), tableType);

		logger.debug(Literal.LEAVING);

		return auditList;
	}

	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetailsList = new ArrayList<>();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				String transType = "";
				String rcdType = "";
				AutoKnockOffFeeMapping feeMappin = (AutoKnockOffFeeMapping) ((AuditDetail) list.get(i)).getModelData();
				rcdType = feeMappin.getRecordType();

				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
					transType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType)
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
					transType = PennantConstants.TRAN_DEL;
				} else {
					transType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isNotEmpty(transType)) {
					//check and change below line for Complete code
					auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(),
							feeMappin.getBefImage(), feeMappin));
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return auditDetailsList;
	}

	public List<AutoKnockOff> getKnockOffDetails(String finreference) {
		return autoKnockOffDAO.getKnockOffDetails(finreference);
	}

	public void setAutoKnockOffDAO(AutoKnockOffDAO autoKnockOffDAO) {
		this.autoKnockOffDAO = autoKnockOffDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public AutoKnkOfFeeMappingDAO getAutoKnkOfFeeMappingDAO() {
		return autoKnkOfFeeMappingDAO;
	}

	public void setAutoKnkOfFeeMappingDAO(AutoKnkOfFeeMappingDAO autoKnkOfFeeMappingDAO) {
		this.autoKnkOfFeeMappingDAO = autoKnkOfFeeMappingDAO;
	}

	public LoanTypeKnockOffDAO getLoanTypeKnockOffDAO() {
		return loanTypeKnockOffDAO;
	}

	public void setLoanTypeKnockOffDAO(LoanTypeKnockOffDAO loanTypeKnockOffDAO) {
		this.loanTypeKnockOffDAO = loanTypeKnockOffDAO;
	}

}
