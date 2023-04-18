/**
 * 
 */
package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.LoanTypeWriteOffDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinTypeWriteOff;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.LoanTypeWriteOffService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class LoanTypeWriteOffServiceImpl extends GenericService<FinTypeWriteOff> implements LoanTypeWriteOffService {
	private static final Logger logger = LogManager.getLogger(LoanTypeWriteOffServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private LoanTypeWriteOffDAO loanTypeWriteOffDAO;

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setLoanTypeWriteOffDAO(LoanTypeWriteOffDAO loanTypeWriteOffDAO) {
		this.loanTypeWriteOffDAO = loanTypeWriteOffDAO;
	}

	public LoanTypeWriteOffServiceImpl() {
		super();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		setPropertiesToChailds(auditHeader);
		List<AuditDetail> auditDetails = new ArrayList<>();
		auditHeader = businessValidation(auditHeader, PennantConstants.method_saveOrUpdate);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		TableType tableType = TableType.MAIN_TAB;
		FinTypeWriteOff writeOff = (FinTypeWriteOff) auditHeader.getAuditDetail().getModelData();

		if (writeOff.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (writeOff.getLoanTypeWriteOffMapping() != null && writeOff.getLoanTypeWriteOffMapping().size() > 0) {
			List<AuditDetail> details = writeOff.getAuditDetailMap().get("LoanTypeWriteOffCodeMapping");
			details = processingCodeMappingList(details, tableType.getSuffix());
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetail(null);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;

	}

	public void setPropertiesToChailds(AuditHeader auditHeader) {
		FinTypeWriteOff writeOff = (FinTypeWriteOff) auditHeader.getAuditDetail().getModelData();
		for (FinTypeWriteOff finTypeWriteOffData : writeOff.getLoanTypeWriteOffMapping()) {
			finTypeWriteOffData.setLastMntBy(writeOff.getLastMntBy());
			finTypeWriteOffData.setLastMntOn(writeOff.getLastMntOn());
			finTypeWriteOffData.setUserDetails(writeOff.getUserDetails());
			finTypeWriteOffData.setRecordStatus(writeOff.getRecordStatus());
			finTypeWriteOffData.setTaskId(writeOff.getTaskId());
			finTypeWriteOffData.setNextTaskId(writeOff.getNextTaskId());
			finTypeWriteOffData.setRoleCode(writeOff.getRoleCode());
			finTypeWriteOffData.setNextRoleCode(writeOff.getNextRoleCode());
		}
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, PennantConstants.RCD_DEL);
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinTypeWriteOff write = (FinTypeWriteOff) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(write, "", auditHeader.getAuditTranType())));
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditHeader = businessValidation(auditHeader, PennantConstants.method_doApprove);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FinTypeWriteOff writeOff = new FinTypeWriteOff();
		BeanUtils.copyProperties(auditHeader.getAuditDetail().getModelData(), writeOff);

		String recordType = writeOff.getRecordType();

		if (recordType.equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(listDeletion(writeOff, "", auditHeader.getAuditTranType()));
		} else {
			writeOff.setRoleCode("");
			writeOff.setNextRoleCode("");
			writeOff.setTaskId("");
			writeOff.setNextTaskId("");
			writeOff.setWorkflowId(0);
		}

		// Retrieving List of Audit Details For checkList details modules
		if (writeOff.getLoanTypeWriteOffMapping() != null && writeOff.getLoanTypeWriteOffMapping().size() > 0) {
			List<AuditDetail> details = writeOff.getAuditDetailMap().get("LoanTypeWriteOffCodeMapping");
			details = processingCodeMappingList(details, "");
			auditDetails.addAll(details);
		}
		auditHeader
				.setAuditDetails(getListAuditDetails(listDeletion(writeOff, "_Temp", auditHeader.getAuditTranType())));
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(writeOff);

		auditHeader.setAuditDetail(null);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, PennantConstants.method_doApprove);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FinTypeWriteOff aWriteOff = (FinTypeWriteOff) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(aWriteOff, "", auditHeader.getAuditTranType())));
		auditHeaderDAO.addAudit(auditHeader);

		for (int i = 0; i < aWriteOff.getLoanTypeWriteOffMapping().size(); i++) {
			loanTypeWriteOffDAO.delete(aWriteOff.getLoanTypeWriteOffMapping().get(i), TableType.TEMP_TAB.getSuffix());
		}

		logger.info(Literal.LEAVING);

		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), false);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		List<AuditDetail> auditDetails = new ArrayList<>();

		FinTypeWriteOff finTypeWriteOff = (FinTypeWriteOff) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = finTypeWriteOff.getUserDetails().getLanguage();

		List<FinTypeWriteOff> loanTypeWriteOffMapping = finTypeWriteOff.getLoanTypeWriteOffMapping();
		if (CollectionUtils.isNotEmpty(loanTypeWriteOffMapping)) {
			List<AuditDetail> details = finTypeWriteOff.getAuditDetailMap().get("LoanTypeWriteOffCodeMapping");
			auditDetails.addAll(validateWriteOffMapping(details, usrLanguage, method));
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;

	}

	public List<AuditDetail> validateWriteOffMapping(List<AuditDetail> auditDetails, String usrLanguage,
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
		FinTypeWriteOff loantypeWriteOffmapping = (FinTypeWriteOff) auditDetail.getModelData();
		FinTypeWriteOff tempFinOption = null;

		if (loantypeWriteOffmapping.isWorkflow()) {
			tempFinOption = loanTypeWriteOffDAO.getLoanWriteOffMappingByID(loantypeWriteOffmapping,
					TableType.TEMP_TAB.getSuffix());
		}
		FinTypeWriteOff befFinOption = loanTypeWriteOffDAO.getLoanWriteOffMappingByID(loantypeWriteOffmapping, "");
		FinTypeWriteOff oldFinOption = loantypeWriteOffmapping.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = loantypeWriteOffmapping.getPslCode();
		errParm[0] = PennantJavaUtil.getLabel("label_WriteOffCode") + ":" + valueParm[0];

		if (loantypeWriteOffmapping.isNewRecord()) {
			if (!loantypeWriteOffmapping.isWorkflow()) {
				if (befFinOption != null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else {
				if (loantypeWriteOffmapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (befFinOption != null || tempFinOption != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else {
					if (befFinOption == null || tempFinOption != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			if (!loantypeWriteOffmapping.isWorkflow()) {
				if (befFinOption == null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinOption != null && !oldFinOption.getLastMntOn().equals(befFinOption.getLastMntOn())) {
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

				if (tempFinOption == null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempFinOption != null && oldFinOption != null
						&& !oldFinOption.getLastMntOn().equals(tempFinOption.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (PennantConstants.method_doApprove.equals(StringUtils.trimToEmpty(method))
				|| !loantypeWriteOffmapping.isWorkflow()) {
			auditDetail.setBefImage(befFinOption);
		}
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, boolean isUniqueCheckReq) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		FinTypeWriteOff finTypeWriteOff = (FinTypeWriteOff) auditDetail.getModelData();

		// Check the unique keys.
		if (isUniqueCheckReq && finTypeWriteOff.isNewRecord()
				&& loanTypeWriteOffDAO.isDuplicateKey(finTypeWriteOff.getLoanType(),
						finTypeWriteOff.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_FinMaintainInstruction_Event") + ": "
					+ finTypeWriteOff.getLoanType();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);

		return auditDetail;
	}

	private List<AuditDetail> processingCodeMappingList(List<AuditDetail> auditDetails, String type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			FinTypeWriteOff loanWriteOffMapping = (FinTypeWriteOff) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			loanWriteOffMapping.setId(loanWriteOffMapping.getId());
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				loanWriteOffMapping.setVersion(loanWriteOffMapping.getVersion() + 1);
				loanWriteOffMapping.setRoleCode("");
				loanWriteOffMapping.setNextRoleCode("");
				loanWriteOffMapping.setTaskId("");
				loanWriteOffMapping.setNextTaskId("");
			}

			loanWriteOffMapping.setWorkflowId(0);

			String recordType = loanWriteOffMapping.getRecordType();
			if (recordType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (loanWriteOffMapping.isNewRecord()) {
				saveRecord = true;
				if (recordType.equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					loanWriteOffMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (recordType.equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					loanWriteOffMapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (recordType.equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					loanWriteOffMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
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
				} else if (loanWriteOffMapping.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = recordType;
				recordStatus = loanWriteOffMapping.getRecordStatus();
				loanWriteOffMapping.setRecordType("");
				loanWriteOffMapping.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				loanTypeWriteOffDAO.delete(loanWriteOffMapping, TableType.TEMP_TAB.getSuffix());

			}

			if (saveRecord) {
				loanTypeWriteOffDAO.save(loanWriteOffMapping, type);
			}

			if (updateRecord) {
				loanTypeWriteOffDAO.update(loanWriteOffMapping, type);
			}

			if (deleteRecord) {
				loanTypeWriteOffDAO.delete(loanWriteOffMapping, type);
			}

			if (approveRec) {
				loanWriteOffMapping.setRecordType(rcdType);
				loanWriteOffMapping.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(loanWriteOffMapping);
		}

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();
		FinTypeWriteOff writeOff = (FinTypeWriteOff) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ((PennantConstants.method_saveOrUpdate.equals(method) || PennantConstants.method_doApprove.equals(method)
				|| PennantConstants.method_doReject.equals(method)) && writeOff.isWorkflow()) {
			auditTranType = PennantConstants.TRAN_WF;
		}

		if (writeOff.getLoanTypeWriteOffMapping() != null && writeOff.getLoanTypeWriteOffMapping().size() > 0) {
			auditDetailMap.put("LoanTypeWriteOffCodeMapping", setCodeMappingAuditData(writeOff, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("LoanTypeWriteOffCodeMapping"));
		}

		writeOff.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(writeOff);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private List<AuditDetail> setCodeMappingAuditData(FinTypeWriteOff writeOff, String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeWriteOff(),
				new FinTypeWriteOff().getExcludeFields());

		for (int i = 0; i < writeOff.getLoanTypeWriteOffMapping().size(); i++) {
			FinTypeWriteOff feeMapping = writeOff.getLoanTypeWriteOffMapping().get(i);

			// Skipping the process of current iteration when the child was not
			// modified to avoid unnecessary processing
			String recordType = feeMapping.getRecordType();
			if (StringUtils.isEmpty(recordType)) {
				continue;
			}

			feeMapping.setWorkflowId(writeOff.getWorkflowId());

			boolean isRcdType = false;

			if (recordType.equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				feeMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (recordType.equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				feeMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (writeOff.isWorkflow()) {
					isRcdType = true;
				}
			} else if (recordType.equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				feeMapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (PennantConstants.method_saveOrUpdate.equals(method) && isRcdType) {
				feeMapping.setNewRecord(true);
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

			feeMapping.setRecordStatus(writeOff.getRecordStatus());
			feeMapping.setUserDetails(writeOff.getUserDetails());
			feeMapping.setLastMntOn(writeOff.getLastMntOn());
			feeMapping.setLastMntBy(writeOff.getLastMntBy());
			auditDetails.add(
					new AuditDetail(auditTranType, i + 1, fields[0], fields[1], feeMapping.getBefImage(), feeMapping));
		}

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	public List<AuditDetail> listDeletion(FinTypeWriteOff writeOff, String tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<>();
		if (writeOff.getLoanTypeWriteOffMapping() == null && writeOff.getLoanTypeWriteOffMapping().isEmpty()) {
			return auditList;
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeWriteOff());
		for (int i = 0; i < writeOff.getLoanTypeWriteOffMapping().size(); i++) {
			FinTypeWriteOff codeMapping = writeOff.getLoanTypeWriteOffMapping().get(i);
			if (!StringUtils.isEmpty(codeMapping.getRecordType()) || StringUtils.isEmpty(tableType)) {
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], codeMapping.getBefImage(),
						codeMapping));
			}
		}
		FinTypeWriteOff mapping = writeOff.getLoanTypeWriteOffMapping().get(0);
		loanTypeWriteOffDAO.delete(mapping.getId(), tableType);

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
				FinTypeWriteOff codeMapping = (FinTypeWriteOff) ((AuditDetail) list.get(i)).getModelData();
				rcdType = codeMapping.getRecordType();

				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
					transType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType)
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
					transType = PennantConstants.TRAN_DEL;
				} else {
					transType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isNotEmpty(transType)) {
					// check and change below line for Complete code
					auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(),
							codeMapping.getBefImage(), codeMapping));
				}
			}
		}

		logger.debug(Literal.LEAVING);

		return auditDetailsList;
	}

	@Override
	public List<FinTypeWriteOff> getWriteOffMappingById(String finType) {
		return loanTypeWriteOffDAO.getLoanWriteOffMappingListByLoanType(finType, "_View");
	}

	@Override
	public List<FinTypeWriteOff> getApprovedWriteOffMappingById(String finType) {
		return loanTypeWriteOffDAO.getLoanWriteOffMappingListByLoanType(finType, "_AView");
	}

}
