package com.pennanttech.pff.npa.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import com.pennanttech.pff.npa.dao.AssetClassSetupDAO;
import com.pennanttech.pff.npa.model.AssetClassSetupDetail;
import com.pennanttech.pff.npa.model.AssetClassSetupHeader;
import com.pennanttech.pff.npa.service.AssetClassSetupService;

public class AssetClassSetupServiceImpl extends GenericService<AssetClassSetupHeader>
		implements AssetClassSetupService {
	private static final Logger logger = LogManager.getLogger(AssetClassSetupServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AssetClassSetupDAO assetClassSetupDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();
		businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AssetClassSetupHeader assetClassSetupHeader = (AssetClassSetupHeader) auditHeader.getAuditDetail()
				.getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (assetClassSetupHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (assetClassSetupHeader.isNewRecord()) {
			assetClassSetupHeader.setCreatedBy(assetClassSetupHeader.getLastMntBy());
			assetClassSetupHeader.setCreatedOn(assetClassSetupHeader.getLastMntOn());

			assetClassSetupHeader.setId(Long.parseLong(assetClassSetupDAO.save(assetClassSetupHeader, tableType)));
			auditHeader.getAuditDetail().setModelData(assetClassSetupHeader);
			auditHeader.setAuditReference(String.valueOf(assetClassSetupHeader.getId()));
		} else {
			assetClassSetupDAO.update(assetClassSetupHeader, tableType);
		}

		if (assetClassSetupHeader.getDetails() != null && assetClassSetupHeader.getDetails().size() > 0) {
			List<AuditDetail> details = assetClassSetupHeader.getAuditDetailMap().get("AssetSetupDetailList");
			for (int i = 0; i < details.size(); i++) {
				AssetClassSetupDetail acsd = (AssetClassSetupDetail) details.get(i).getModelData();
				acsd.setSetupID(assetClassSetupHeader.getId());
			}
			details = processingAssetSetupDetailList(details, tableType.getSuffix());
			auditDetails.addAll(details);
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

		AssetClassSetupHeader assetClassSetupHeader = (AssetClassSetupHeader) auditHeader.getAuditDetail()
				.getModelData();
		assetClassSetupDAO.delete(assetClassSetupHeader, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AssetClassSetupHeader getAssetClassSetup(long id) {
		AssetClassSetupHeader acsh = assetClassSetupDAO.getAssetClassSetupHeader(id, TableType.VIEW.getSuffix());
		TableType tableType = TableType.MAIN_TAB;
		if (StringUtils.isNotEmpty(acsh.getRecordType())) {
			tableType = TableType.TEMP_TAB;
		}
		List<AssetClassSetupDetail> acsdList = assetClassSetupDAO.getAssetClassSetupDetailBySetupID(id,
				tableType.getSuffix());
		if (CollectionUtils.isNotEmpty(acsdList)) {
			acsh.setDetails(acsdList);
		}
		return acsh;
	}

	public AssetClassSetupHeader getApprovedAssetClassSetup(long id) {
		return assetClassSetupDAO.getAssetClassSetupHeader(id, TableType.AVIEW.getSuffix());
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		String tranType = "";
		businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AssetClassSetupHeader assetClassSetupHeader = ((AssetClassSetupHeader) auditHeader.getAuditDetail()
				.getModelData()).copyEntity();

		String recordType = assetClassSetupHeader.getRecordType();

		if (PennantConstants.RECORD_TYPE_DEL.equals(recordType)) {
			tranType = PennantConstants.TRAN_DEL;
			assetClassSetupDAO.softDelete(assetClassSetupHeader.getId(), TableType.MAIN_TAB);
		} else {
			assetClassSetupHeader.setRoleCode("");
			assetClassSetupHeader.setNextRoleCode("");
			assetClassSetupHeader.setTaskId("");
			assetClassSetupHeader.setNextTaskId("");
			assetClassSetupHeader.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(recordType)) {
				tranType = PennantConstants.TRAN_ADD;
				assetClassSetupHeader.setRecordType("");

				assetClassSetupHeader.setApprovedBy(assetClassSetupHeader.getLastMntBy());
				assetClassSetupHeader.setApprovedOn(assetClassSetupHeader.getLastMntOn());
				assetClassSetupDAO.save(assetClassSetupHeader, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				assetClassSetupHeader.setRecordType("");
				assetClassSetupDAO.update(assetClassSetupHeader, TableType.MAIN_TAB);
			}
		}

		// Retrieving List of Audit Details For checkList details modules
		if (assetClassSetupHeader.getDetails() != null && assetClassSetupHeader.getDetails().size() > 0) {
			List<AuditDetail> details = assetClassSetupHeader.getAuditDetailMap().get("AssetSetupDetailList");
			details = processingAssetSetupDetailList(details, "");
			auditDetails.addAll(details);
		}

		assetClassSetupDAO.delete(assetClassSetupHeader, TableType.TEMP_TAB);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(assetClassSetupHeader);
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

		AssetClassSetupHeader assetClassSetupHeader = (AssetClassSetupHeader) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		assetClassSetupDAO.deleteDetailBySetupID(assetClassSetupHeader.getId(), TableType.TEMP_TAB.getSuffix());
		assetClassSetupDAO.delete(assetClassSetupHeader, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	private void businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);

		AssetClassSetupHeader assetClassSetupHeader = (AssetClassSetupHeader) auditDetail.getModelData();

		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();
		List<AuditDetail> auditDetails = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(assetClassSetupHeader.getDetails())) {
			auditDetailMap.put("AssetSetupDetailList",
					setAuditDataForDetail(assetClassSetupHeader, auditDetail.getAuditTranType(), method));
			auditDetails.addAll(auditDetailMap.get("AssetSetupDetailList"));
		}

		assetClassSetupHeader.setAuditDetailMap(auditDetailMap);

		if (CollectionUtils.isNotEmpty(assetClassSetupHeader.getDetails())) {
			List<AuditDetail> details = assetClassSetupHeader.getAuditDetailMap().get("AssetSetupDetailList");
			auditDetails.addAll(validateAssetClassSetupDetails(details, auditHeader.getUsrLanguage(), method));
		}

		auditHeader.setErrorList(auditDetail.getErrorDetails());

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		AssetClassSetupHeader ach = (AssetClassSetupHeader) auditDetail.getModelData();

		String recordType = ach.getRecordType();
		String entityCode = ach.getEntityCode();
		boolean newRecord = ach.isNewRecord();

		if (newRecord && PennantConstants.RECORD_TYPE_NEW.equals(recordType)) {
			if (newRecord && assetClassSetupDAO.isAssetEntityCodeExists(entityCode, ach.getCode(), TableType.VIEW)) {
				String[] parameters = new String[2];
				parameters[0] = PennantJavaUtil.getLabel("label_EntityCode") + ": " + entityCode;
				parameters[1] = PennantJavaUtil.getLabel("label_AssetClassSetupDialog_AssetClassSetupCode") + ": "
						+ ach.getCode();
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41015", parameters, null));
			}
		}

		boolean checkDependency = assetClassSetupDAO.checkDependency(ach.getId());

		if (PennantConstants.RECORD_TYPE_DEL.equals(ach.getRecordType())) {
			if (checkDependency) {
				auditDetail.setErrorDetail(new ErrorDetail("90290", null));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private List<AuditDetail> processingAssetSetupDetailList(List<AuditDetail> auditDetails, String type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			AssetClassSetupDetail assetClassSetupDetail = (AssetClassSetupDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				assetClassSetupDetail.setVersion(assetClassSetupDetail.getVersion() + 1);
				assetClassSetupDetail.setRoleCode("");
				assetClassSetupDetail.setNextRoleCode("");
				assetClassSetupDetail.setTaskId("");
				assetClassSetupDetail.setNextTaskId("");
				assetClassSetupDetail.setWorkflowId(0);
			}

			String recordType = assetClassSetupDetail.getRecordType();
			if (recordType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (assetClassSetupDetail.isNewRecord()) {
				saveRecord = true;
				if (recordType.equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					assetClassSetupDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (recordType.equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					assetClassSetupDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (recordType.equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					assetClassSetupDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (recordType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (recordType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (recordType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (assetClassSetupDetail.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = recordType;
				recordStatus = assetClassSetupDetail.getRecordStatus();
				assetClassSetupDetail.setRecordType("");
				assetClassSetupDetail.setApprovedOn(assetClassSetupDetail.getLastMntOn());
				assetClassSetupDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				assetClassSetupDAO.deleteDetail(assetClassSetupDetail, TableType.TEMP_TAB.getSuffix());

			}

			if (saveRecord) {
				assetClassSetupDAO.saveDetail(assetClassSetupDetail, type);
			}

			if (updateRecord) {
				assetClassSetupDAO.updateDetail(assetClassSetupDetail, type);
			}

			if (approveRec) {
				assetClassSetupDetail.setRecordType(rcdType);
				assetClassSetupDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(assetClassSetupDetail);
		}

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	private List<AuditDetail> setAuditDataForDetail(AssetClassSetupHeader acsh, String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		String[] fields = PennantJavaUtil.getFieldDetails(new AssetClassSetupDetail(),
				new AssetClassSetupDetail().getExcludeFields());

		for (int i = 0; i < acsh.getDetails().size(); i++) {
			AssetClassSetupDetail acsd = acsh.getDetails().get(i);

			// Skipping the process of current iteration when the child was not
			// modified to avoid unnecessary processing
			String recordType = acsd.getRecordType();
			if (StringUtils.isEmpty(recordType)) {
				continue;
			}

			acsd.setWorkflowId(acsh.getWorkflowId());

			boolean isRcdType = false;

			if (recordType.equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				acsd.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (recordType.equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				acsd.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (acsh.isWorkflow()) {
					isRcdType = true;
				}
			} else if (recordType.equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				acsd.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (PennantConstants.method_saveOrUpdate.equals(method) && isRcdType) {
				acsd.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (recordType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (recordType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| recordType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			acsd.setRecordStatus(acsh.getRecordStatus());
			acsd.setLastMntOn(acsh.getLastMntOn());
			acsd.setLastMntBy(acsh.getLastMntBy());
			acsd.setCreatedBy(acsh.getLastMntBy());
			acsd.setCreatedOn(acsh.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], acsd.getBefImage(), acsd));
		}

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	public List<AuditDetail> validateAssetClassSetupDetails(List<AuditDetail> auditDetails, String usrLanguage,
			String method) {
		List<AuditDetail> aAuditDetails = new ArrayList<>();
		logger.debug(Literal.ENTERING);

		for (AuditDetail auditDetail : auditDetails) {
			validate(auditDetail, usrLanguage, method);
			aAuditDetails.add(auditDetail);
		}

		return aAuditDetails;
	}

	private void validate(AuditDetail auditDetail, String usrLanguage, String method) {
		auditDetail.setErrorDetails(new ArrayList<>());
		AssetClassSetupDetail acsd = (AssetClassSetupDetail) auditDetail.getModelData();
		AssetClassSetupDetail acsdTemp = null;

		if (acsd.isWorkflow()) {
			acsdTemp = assetClassSetupDAO.getAssetClassSetupDetailByID(acsd, TableType.TEMP_TAB.getSuffix());
		}
		AssetClassSetupDetail befacsd = assetClassSetupDAO.getAssetClassSetupDetailByID(acsd, "");
		AssetClassSetupDetail oldacsd = acsd.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = acsd.getClassCode();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (acsd.isNewRecord()) {
			if (!acsd.isWorkflow()) {
				if (befacsd != null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else {
				if (acsd.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (befacsd != null || acsdTemp != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else {
					if (befacsd == null || acsdTemp != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			if (!acsd.isWorkflow()) {
				if (befacsd == null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldacsd != null && !oldacsd.getLastMntOn().equals(befacsd.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				if (acsdTemp == null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (acsdTemp != null && oldacsd != null && !oldacsd.getLastMntOn().equals(acsdTemp.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (PennantConstants.method_doApprove.equals(StringUtils.trimToEmpty(method)) || !acsd.isWorkflow()) {
			auditDetail.setBefImage(befacsd);
		}
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setAssetClassSetupDAO(AssetClassSetupDAO assetClassSetupDAO) {
		this.assetClassSetupDAO = assetClassSetupDAO;
	}

}