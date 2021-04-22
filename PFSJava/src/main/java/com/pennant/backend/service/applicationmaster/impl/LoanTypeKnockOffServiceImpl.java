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
import com.pennant.backend.dao.applicationmaster.LoanTypeKnockOffDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinTypeKnockOff;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.LoanTypeKnockOffService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class LoanTypeKnockOffServiceImpl extends GenericService<FinTypeKnockOff> implements LoanTypeKnockOffService {
	private static final Logger logger = LogManager.getLogger(LoanTypeKnockOffServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private LoanTypeKnockOffDAO loanTypeKnockOffDAO;

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setLoanTypeKnockOffDAO(LoanTypeKnockOffDAO loanTypeKnockOffDAO) {
		this.loanTypeKnockOffDAO = loanTypeKnockOffDAO;
	}

	public LoanTypeKnockOffServiceImpl() {
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
		FinTypeKnockOff knockOff = (FinTypeKnockOff) auditHeader.getAuditDetail().getModelData();

		if (knockOff.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (knockOff.getLoanTypeKonckOffMapping() != null && knockOff.getLoanTypeKonckOffMapping().size() > 0) {
			List<AuditDetail> details = knockOff.getAuditDetailMap().get("LoanTypeKnockOffCodeMapping");
			details = processingCodeMappingList(details, tableType.getSuffix());
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetail(null);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;

	}

	public void setPropertiesToChailds(AuditHeader auditHeader) {
		FinTypeKnockOff knockOff = (FinTypeKnockOff) auditHeader.getAuditDetail().getModelData();
		for (FinTypeKnockOff finTypeKnockOffData : knockOff.getLoanTypeKonckOffMapping()) {
			finTypeKnockOffData.setLastMntBy(knockOff.getLastMntBy());
			finTypeKnockOffData.setLastMntOn(knockOff.getLastMntOn());
			finTypeKnockOffData.setUserDetails(knockOff.getUserDetails());
			finTypeKnockOffData.setRecordStatus(knockOff.getRecordStatus());
			finTypeKnockOffData.setTaskId(knockOff.getTaskId());
			finTypeKnockOffData.setNextTaskId(knockOff.getNextTaskId());
			finTypeKnockOffData.setRoleCode(knockOff.getRoleCode());
			finTypeKnockOffData.setNextRoleCode(knockOff.getNextRoleCode());
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

		FinTypeKnockOff knock = (FinTypeKnockOff) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(knock, "", auditHeader.getAuditTranType())));
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

		FinTypeKnockOff knockOff = new FinTypeKnockOff();
		BeanUtils.copyProperties(auditHeader.getAuditDetail().getModelData(), knockOff);

		String recordType = knockOff.getRecordType();

		if (recordType.equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(listDeletion(knockOff, "", auditHeader.getAuditTranType()));
		} else {
			knockOff.setRoleCode("");
			knockOff.setNextRoleCode("");
			knockOff.setTaskId("");
			knockOff.setNextTaskId("");
			knockOff.setWorkflowId(0);
		}

		// Retrieving List of Audit Details For checkList details modules
		if (knockOff.getLoanTypeKonckOffMapping() != null && knockOff.getLoanTypeKonckOffMapping().size() > 0) {
			List<AuditDetail> details = knockOff.getAuditDetailMap().get("LoanTypeKnockOffCodeMapping");
			details = processingCodeMappingList(details, "");
			auditDetails.addAll(details);
		}
		auditHeader
				.setAuditDetails(getListAuditDetails(listDeletion(knockOff, "_Temp", auditHeader.getAuditTranType())));
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(knockOff);

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

		FinTypeKnockOff aKnockOff = (FinTypeKnockOff) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(aKnockOff, "", auditHeader.getAuditTranType())));
		auditHeaderDAO.addAudit(auditHeader);

		for (int i = 0; i < aKnockOff.getLoanTypeKonckOffMapping().size(); i++) {
			loanTypeKnockOffDAO.delete(aKnockOff.getLoanTypeKonckOffMapping().get(i), TableType.TEMP_TAB.getSuffix());
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

		FinTypeKnockOff finTypeKnockOff = (FinTypeKnockOff) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = finTypeKnockOff.getUserDetails().getLanguage();

		List<FinTypeKnockOff> loanTypeKnockOffMapping = finTypeKnockOff.getLoanTypeKonckOffMapping();
		if (CollectionUtils.isNotEmpty(loanTypeKnockOffMapping)) {
			List<AuditDetail> details = finTypeKnockOff.getAuditDetailMap().get("LoanTypeKnockOffCodeMapping");
			auditDetails.addAll(validateKnockOffMapping(details, usrLanguage, method));
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;

	}

	public List<AuditDetail> validateKnockOffMapping(List<AuditDetail> auditDetails, String usrLanguage,
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
		FinTypeKnockOff loantypeKnockOffmapping = (FinTypeKnockOff) auditDetail.getModelData();
		FinTypeKnockOff tempFinOption = null;

		if (loantypeKnockOffmapping.isWorkflow()) {
			tempFinOption = loanTypeKnockOffDAO.getLoanKnockOffMappingByID(loantypeKnockOffmapping,
					TableType.TEMP_TAB.getSuffix());
		}
		FinTypeKnockOff befFinOption = loanTypeKnockOffDAO.getLoanKnockOffMappingByID(loantypeKnockOffmapping, "");
		FinTypeKnockOff oldFinOption = loantypeKnockOffmapping.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = loantypeKnockOffmapping.getKnockOffCode();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (loantypeKnockOffmapping.isNewRecord()) {
			if (!loantypeKnockOffmapping.isWorkflow()) {
				if (befFinOption != null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else {
				if (loantypeKnockOffmapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
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
			if (!loantypeKnockOffmapping.isWorkflow()) {
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
				|| !loantypeKnockOffmapping.isWorkflow()) {
			auditDetail.setBefImage(befFinOption);
		}
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, boolean isUniqueCheckReq) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		FinTypeKnockOff finTypeKnockOff = (FinTypeKnockOff) auditDetail.getModelData();

		// Check the unique keys.
		if (isUniqueCheckReq && finTypeKnockOff.isNewRecord()
				&& loanTypeKnockOffDAO.isDuplicateKey(finTypeKnockOff.getLoanType(),
						finTypeKnockOff.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_FinMaintainInstruction_Event") + ": "
					+ finTypeKnockOff.getLoanType();
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
			FinTypeKnockOff loanKnockOffMapping = (FinTypeKnockOff) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			loanKnockOffMapping.setId(loanKnockOffMapping.getId());
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				loanKnockOffMapping.setVersion(loanKnockOffMapping.getVersion() + 1);
				loanKnockOffMapping.setRoleCode("");
				loanKnockOffMapping.setNextRoleCode("");
				loanKnockOffMapping.setTaskId("");
				loanKnockOffMapping.setNextTaskId("");
			}

			loanKnockOffMapping.setWorkflowId(0);

			String recordType = loanKnockOffMapping.getRecordType();
			if (recordType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (loanKnockOffMapping.isNewRecord()) {
				saveRecord = true;
				if (recordType.equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					loanKnockOffMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (recordType.equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					loanKnockOffMapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (recordType.equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					loanKnockOffMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
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
				} else if (loanKnockOffMapping.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = recordType;
				recordStatus = loanKnockOffMapping.getRecordStatus();
				loanKnockOffMapping.setRecordType("");
				loanKnockOffMapping.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				loanTypeKnockOffDAO.delete(loanKnockOffMapping, TableType.TEMP_TAB.getSuffix());

			}

			if (saveRecord) {
				loanTypeKnockOffDAO.save(loanKnockOffMapping, type);
			}

			if (updateRecord) {
				loanTypeKnockOffDAO.update(loanKnockOffMapping, type);
			}

			if (deleteRecord) {
				loanTypeKnockOffDAO.delete(loanKnockOffMapping, type);
			}

			if (approveRec) {
				loanKnockOffMapping.setRecordType(rcdType);
				loanKnockOffMapping.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(loanKnockOffMapping);
		}

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();
		FinTypeKnockOff knockOff = (FinTypeKnockOff) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ((PennantConstants.method_saveOrUpdate.equals(method) || PennantConstants.method_doApprove.equals(method)
				|| PennantConstants.method_doReject.equals(method)) && knockOff.isWorkflow()) {
			auditTranType = PennantConstants.TRAN_WF;
		}

		if (knockOff.getLoanTypeKonckOffMapping() != null && knockOff.getLoanTypeKonckOffMapping().size() > 0) {
			auditDetailMap.put("LoanTypeKnockOffCodeMapping", setCodeMappingAuditData(knockOff, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("LoanTypeKnockOffCodeMapping"));
		}

		knockOff.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(knockOff);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private List<AuditDetail> setCodeMappingAuditData(FinTypeKnockOff knockOff, String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeKnockOff(),
				new FinTypeKnockOff().getExcludeFields());

		for (int i = 0; i < knockOff.getLoanTypeKonckOffMapping().size(); i++) {
			FinTypeKnockOff feeMapping = knockOff.getLoanTypeKonckOffMapping().get(i);

			// Skipping the process of current iteration when the child was not
			// modified to avoid unnecessary processing
			String recordType = feeMapping.getRecordType();
			if (StringUtils.isEmpty(recordType)) {
				continue;
			}

			feeMapping.setWorkflowId(knockOff.getWorkflowId());

			boolean isRcdType = false;

			if (recordType.equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				feeMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (recordType.equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				feeMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (knockOff.isWorkflow()) {
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

	public List<AuditDetail> listDeletion(FinTypeKnockOff knockOff, String tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<>();
		if (knockOff.getLoanTypeKonckOffMapping() == null && knockOff.getLoanTypeKonckOffMapping().isEmpty()) {
			return auditList;
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeKnockOff());
		for (int i = 0; i < knockOff.getLoanTypeKonckOffMapping().size(); i++) {
			FinTypeKnockOff codeMapping = knockOff.getLoanTypeKonckOffMapping().get(i);
			if (!StringUtils.isEmpty(codeMapping.getRecordType()) || StringUtils.isEmpty(tableType)) {
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], codeMapping.getBefImage(),
						codeMapping));
			}
		}
		FinTypeKnockOff mapping = knockOff.getLoanTypeKonckOffMapping().get(0);
		loanTypeKnockOffDAO.delete(mapping.getId(), tableType);

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
				FinTypeKnockOff codeMapping = (FinTypeKnockOff) ((AuditDetail) list.get(i)).getModelData();
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
	public List<FinTypeKnockOff> getKnockOffMappingById(String finType) {
		return loanTypeKnockOffDAO.getLoanKnockOffMappingListByLoanType(finType, "_View");
	}

	@Override
	public List<FinTypeKnockOff> getApprovedKnockOffMappingById(String finType) {
		return loanTypeKnockOffDAO.getLoanKnockOffMappingListByLoanType(finType, "_AView");
	}

}
