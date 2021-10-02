package com.pennant.backend.service.extendedfieldsExtension.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.collateral.ExtendedFieldExtensionDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.extendedfield.ExtendedFieldExtension;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.extendedfieldsExtension.ExtendedFieldExtensionService;
import com.pennant.backend.service.solutionfactory.impl.ExtendedFieldDetailServiceImpl;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class ExtendedFieldExtensionServiceImpl extends GenericService<ExtendedFieldExtension>
		implements ExtendedFieldExtensionService {
	private static final Logger logger = LogManager.getLogger(ExtendedFieldDetailServiceImpl.class);

	private ExtendedFieldExtensionDAO extendedFieldExtensionDAO;

	public List<AuditDetail> processingExtendedFieldExtList(List<AuditDetail> details, FinReceiptData rceiptData,
			long serviceUID, TableType tableType) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < details.size(); i++) {

			if (details.get(i) != null) {
				ExtendedFieldExtension efe = (ExtendedFieldExtension) details.get(i).getModelData();
				if (StringUtils.isEmpty(efe.getRecordType())) {
					continue;
				}

				efe.setExtenrnalRef(Long.toString(rceiptData.getReceiptHeader().getReceiptID()));
				efe.setModeStatus(rceiptData.getReceiptHeader().getReceiptModeStatus());

				if (efe.getInstructionUID() == Long.MIN_VALUE) {
					efe.setInstructionUID(serviceUID);
				}

				if (tableType.equals(TableType.TEMP_TAB) && efe.isNewRecord()) {
					efe.setId(extendedFieldExtensionDAO.getExtFieldExtensionId());
				}

				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = false;
				String rcdType = "";
				String recordStatus = "";
				if (StringUtils.isEmpty(tableType.getSuffix())) {
					approveRec = true;
					efe.setRoleCode("");
					efe.setNextRoleCode("");
					efe.setTaskId("");
					efe.setNextTaskId("");
				}

				if (efe.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
				} else if (efe.isNewRecord()) {
					saveRecord = true;
					if (efe.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						efe.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (efe.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						efe.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (efe.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						efe.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (efe.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (efe.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (efe.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					if (approveRec) {
						deleteRecord = true;
					} else if (efe.isNewRecord()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}
				if (approveRec) {
					rcdType = efe.getRecordType();
					recordStatus = efe.getRecordStatus();
					efe.setRecordType("");
					efe.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}

				if (saveRecord) {
					extendedFieldExtensionDAO.save(efe, tableType);
				}

				if (updateRecord) {
					extendedFieldExtensionDAO.update(efe, tableType);
				}

				if (deleteRecord) {
					extendedFieldExtensionDAO.delete(efe, tableType);
				}
				if (approveRec) {
					efe.setRecordType(rcdType);
					efe.setRecordStatus(recordStatus);
				}

				efe.setBefImage(efe);
				details.get(i).setModelData(efe);
			}
		}

		logger.debug(Literal.LEAVING);
		return details;

	}

	@Override
	public List<AuditDetail> delete(List<AuditDetail> details, String tranType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		ExtendedFieldExtension efe;
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		for (int i = 0; i < details.size(); i++) {
			efe = (ExtendedFieldExtension) details.get(i).getModelData();

			if (StringUtils.isEmpty(tableType.getSuffix())) {
				efe.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			} else {
				efe.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}

			extendedFieldExtensionDAO.delete(efe, tableType);

			efe.setBefImage(efe);

			String[] fields = PennantJavaUtil.getFieldDetails(efe);
			AuditDetail auditDetail = new AuditDetail(tranType, i + 1, fields[0], fields[1], efe.getBefImage(), efe);
			auditList.add(auditDetail);
		}

		logger.debug(Literal.LEAVING);
		return auditList;
	}

	public List<AuditDetail> vaildateDetails(List<AuditDetail> deatils, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		if (deatils != null && deatils.size() > 0) {
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < deatils.size(); i++) {
				if (deatils.get(i) != null) {
					AuditDetail auditDetail = validate(deatils.get(i), usrLanguage);
					details.add(auditDetail);
				}
			}
			return details;
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<AuditDetail>();
	}

	protected AuditDetail validate(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		ExtendedFieldExtension efe = (ExtendedFieldExtension) auditDetail.getModelData();
		boolean tempExtension = false;

		if (extendedFieldExtensionDAO.isDuplicateKey(efe, TableType.VIEW)) {
			String[] errParm = new String[2];
			String[] valueParm = new String[2];
			valueParm[0] = efe.getExtenrnalRef();
			valueParm[1] = String.valueOf(efe.getSequence());

			errParm[0] = PennantJavaUtil.getLabel("label_ReceiptID") + ":" + valueParm[0];
			errParm[1] = PennantJavaUtil.getLabel("label_SeqNo") + ":" + valueParm[1];

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		}

		if (efe.isWorkflow()) {
			tempExtension = extendedFieldExtensionDAO.isExtenstionExist(efe, TableType.TEMP_TAB);
		}
		boolean befExtension = extendedFieldExtensionDAO.isExtenstionExist(efe, TableType.MAIN_TAB);

		if (tempExtension == false && befExtension == false) {
			efe.setNewRecord(true);
			efe.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		}
		if (tempExtension == true && befExtension == false) {
			efe.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		}
		if (tempExtension == false && befExtension == true) {
			efe.setNewRecord(false);
		}
		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public List<AuditDetail> setExtendedFieldExtAuditData(ExtendedFieldExtension extension, String tranType,
			String method) {
		logger.debug(Literal.ENTERING);
		int auditSeq = 1;
		List<AuditDetail> auditDetails = new ArrayList<>();
		AuditDetail auditDetail = setExtendedFieldExtAuditData(extension, tranType, method, auditSeq);
		if (auditDetail == null) {
			return auditDetails;
		}

		auditDetails.add(auditDetail);
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	protected AuditDetail setExtendedFieldExtAuditData(ExtendedFieldExtension efe, String tranType, String method,
			int auditSeq) {
		logger.debug(Literal.ENTERING);

		if (efe == null) {
			return null;
		}
		if (StringUtils.isEmpty(StringUtils.trimToEmpty(efe.getRecordType()))) {
			return null;
		}

		boolean isRcdType = false;
		if (efe.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
			efe.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			isRcdType = true;
		} else if (efe.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
			efe.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			if (efe.isWorkflow()) {
				isRcdType = true;
			}
		} else if (efe.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
			efe.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		}

		if ("saveOrUpdate".equals(method) && (isRcdType)) {
			efe.setNewRecord(true);
		}

		if (!tranType.equals(PennantConstants.TRAN_WF)) {
			if (efe.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (efe.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
					|| efe.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				tranType = PennantConstants.TRAN_DEL;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		String[] fields = PennantJavaUtil.getFieldDetails(efe);
		AuditDetail auditDetail = new AuditDetail(tranType, auditSeq, fields[0], fields[1], efe.getBefImage(), efe);

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public ExtendedFieldExtension getExtendedFieldExtension(String externalRef, String modeStatus, String finEvent,
			TableType tableType) {
		return extendedFieldExtensionDAO.getExtendedFieldExtension(externalRef, modeStatus, finEvent, tableType);
	}

	public void setExtendedFieldExtensionDAO(ExtendedFieldExtensionDAO extendedFieldExtensionDAO) {
		this.extendedFieldExtensionDAO = extendedFieldExtensionDAO;
	}

}
