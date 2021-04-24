package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinMaintainInstructionDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.putcall.FinOptionDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinMaintainInstruction;
import com.pennant.backend.model.finance.finoption.FinOption;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinOptionMaintanceService;
import com.pennant.backend.service.finance.putcall.FinOptionService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class FinOptionMaintanceServiceImpl extends GenericService<FinMaintainInstruction>
		implements FinOptionMaintanceService {

	private static Logger logger = LogManager.getLogger(FinOptionMaintanceServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinOptionDAO finOptionDAO;
	private FinMaintainInstructionDAO finMaintainInstructionDAO;
	private FinOptionService finOptionService;
	private FinanceMainDAO financeMainDAO;

	@Override
	public FinMaintainInstruction getFinMaintainInstructionByFinRef(String finreference, String event) {
		// TODO Auto-generated method stub
		return getFinMaintainInstructionDAO().getFinMaintainInstructionByFinRef(finreference, event, "_Temp");
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinMaintainInstruction finMaintainInstruction = (FinMaintainInstruction) auditHeader.getAuditDetail()
				.getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (finMaintainInstruction.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (finMaintainInstruction.isNew()) {
			finMaintainInstruction.setFinMaintainId(
					Long.parseLong(getFinMaintainInstructionDAO().save(finMaintainInstruction, tableType)));
			auditHeader.getAuditDetail().setModelData(finMaintainInstruction);
			auditHeader.setAuditReference(String.valueOf(finMaintainInstruction.getFinMaintainId()));
		} else {
			getFinMaintainInstructionDAO().update(finMaintainInstruction, tableType);
		}

		List<FinOption> finOptions = finMaintainInstruction.getFinOptions();
		if (CollectionUtils.isNotEmpty(finOptions)) {
			auditDetails.addAll(finOptionService.processFinOptions(finOptions, TableType.TEMP_TAB,
					auditHeader.getAuditTranType(), false));
		}
		
		String rcdMaintainSts = FinanceConstants.FINSER_EVENT_PUTCALL;
		financeMainDAO.updateMaintainceStatus(finMaintainInstruction.getFinReference(), rcdMaintainSts);

		// Add Audit
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	private List<AuditDetail> processingFinOptionList(List<AuditDetail> auditDetails, String finReference,
			TableType type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); ++i) {

			FinOption finOption = (FinOption) auditDetails.get(i).getModelData();

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;

			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(type.getSuffix())) {
				approveRec = true;
				finOption.setRoleCode("");
				finOption.setNextRoleCode("");
				finOption.setTaskId("");
				finOption.setNextTaskId("");
			}

			finOption.setFinReference(finReference);
			finOption.setWorkflowId(0);

			if (finOption.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				// getFinCovenantsDAO().delete(FinCovenants,
				// TableType.TEMP_TAB);
				deleteRecord = true;
			} else if (finOption.isNewRecord()) {
				saveRecord = true;
				if (finOption.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finOption.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finOption.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finOption.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finOption.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finOption.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (finOption.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finOption.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finOption.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finOption.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = finOption.getRecordType();
				recordStatus = finOption.getRecordStatus();
				finOption.setRecordType("");
				finOption.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				finOptionDAO.save(finOption, type);
			}

			if (updateRecord) {
				finOptionDAO.update(finOption, type);
			}

			if (deleteRecord) {
				finOptionDAO.delete(finOption, type);
			}

			if (approveRec) {
				finOption.setRecordType(rcdType);
				finOption.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finOption);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinMaintainInstruction finMaintainInstruction = (FinMaintainInstruction) auditHeader.getAuditDetail()
				.getModelData();
		getFinMaintainInstructionDAO().delete(finMaintainInstruction, TableType.MAIN_TAB);

		auditHeader.setAuditDetails(
				getListAuditDetails(listDeletion(finMaintainInstruction, "", auditHeader.getAuditTranType())));

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public List<AuditDetail> listDeletion(FinMaintainInstruction finMaintainInstruction, String tableType,
			String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		List<FinCovenantType> covenantTypes = finMaintainInstruction.getFinCovenantTypeList();
		if (CollectionUtils.isNotEmpty(covenantTypes)) {
			FinCovenantType finCovenantType = new FinCovenantType();
			String[] fields = PennantJavaUtil.getFieldDetails(finCovenantType, finCovenantType.getExcludeFields());

			for (FinCovenantType covenant : covenantTypes) {
				auditList.add(new AuditDetail(auditTranType, auditList.size() + 1, fields[0], fields[1],
						covenant.getBefImage(), covenant));
			}
			finOptionDAO.deleteByFinRef(finMaintainInstruction.getFinReference(), tableType);
		}

		logger.debug(Literal.LEAVING);
		return auditList;
	}

	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				Object object = ((AuditDetail) list.get(i)).getModelData();
				try {

					rcdType = object.getClass().getMethod("getRecordType").invoke(object).toString();

					if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						transType = PennantConstants.TRAN_ADD;
					} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						transType = PennantConstants.TRAN_DEL;
					} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
						transType = PennantConstants.TRAN_UPD;
					} else {
						auditDetailsList.remove(object);
					}

					if (StringUtils.isNotEmpty(transType)) {

						// check and change below line for Complete code
						Object befImg = object.getClass().getMethod("getBefImage", object.getClass().getClasses())
								.invoke(object, object.getClass().getClasses());

						auditDetailsList.add(
								new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(), befImg, object));
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetailsList;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		FinMaintainInstruction finMaintainInstruction = new FinMaintainInstruction();
		BeanUtils.copyProperties((FinMaintainInstruction) auditHeader.getAuditDetail().getModelData(),
				finMaintainInstruction);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(finMaintainInstruction.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(finMaintainInstructionDAO
					.getFinMaintainInstructionById(finMaintainInstruction.getFinMaintainId(), ""));
		}

		if (finMaintainInstruction.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {

			tranType = PennantConstants.TRAN_DEL;
			getFinMaintainInstructionDAO().delete(finMaintainInstruction, TableType.MAIN_TAB);
			auditDetails.addAll(listDeletion(finMaintainInstruction, "", auditHeader.getAuditTranType()));

		} else {

			finMaintainInstruction.setRoleCode("");
			finMaintainInstruction.setNextRoleCode("");
			finMaintainInstruction.setTaskId("");
			finMaintainInstruction.setNextTaskId("");
			finMaintainInstruction.setWorkflowId(0);

			if (finMaintainInstruction.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				finMaintainInstruction.setRecordType("");
				getFinMaintainInstructionDAO().save(finMaintainInstruction, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				finMaintainInstruction.setRecordType("");
				getFinMaintainInstructionDAO().update(finMaintainInstruction, TableType.MAIN_TAB);
			}
		}

		List<FinCovenantType> covenantTypes = finMaintainInstruction.getFinCovenantTypeList();
		if (CollectionUtils.isNotEmpty(covenantTypes)) {
			List<AuditDetail> details = finMaintainInstruction.getAuditDetailMap().get("FinCovenants");
			details = processingFinOptionList(details, finMaintainInstruction.getFinReference(), TableType.VIEW);
			auditDetails.addAll(details);
		}

		List<FinOption> finOptions = finMaintainInstruction.getFinOptions();
		if (CollectionUtils.isNotEmpty(finOptions)) {
			auditDetails.addAll(finOptionService.processFinOptions(finOptions, TableType.MAIN_TAB,
					auditHeader.getAuditTranType(), true));
		}

		getFinMaintainInstructionDAO().delete(finMaintainInstruction, TableType.TEMP_TAB);
		
		financeMainDAO.updateMaintainceStatus(finMaintainInstruction.getFinReference(), "");

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(
				getListAuditDetails(listDeletion(finMaintainInstruction, "_Temp", auditHeader.getAuditTranType())));
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,
				auditHeader.getAuditDetail().getBefImage(), auditHeader.getAuditDetail().getModelData()));

		getAuditHeaderDAO().addAudit(auditHeader);

		// Audit for Before And After Images
		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetails(auditDetails);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(finMaintainInstruction);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,
				finMaintainInstruction.getBefImage(), finMaintainInstruction));

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinMaintainInstruction finMaintainInstruction = (FinMaintainInstruction) auditHeader.getAuditDetail()
				.getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		finMaintainInstructionDAO.delete(finMaintainInstruction, TableType.TEMP_TAB);
		financeMainDAO.updateMaintainceStatus(finMaintainInstruction.getFinReference(), "");

		String auditTranType = auditHeader.getAuditTranType();
		auditHeader.setAuditDetail(
				new AuditDetail(auditTranType, 1, finMaintainInstruction.getBefImage(), finMaintainInstruction));
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(finMaintainInstruction, "_Temp", auditTranType)));

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public FinOptionDAO getFinOptionDAO() {
		return finOptionDAO;
	}

	public void setFinOptionDAO(FinOptionDAO finOptionDAO) {
		this.finOptionDAO = finOptionDAO;
	}

	public FinMaintainInstructionDAO getFinMaintainInstructionDAO() {
		return finMaintainInstructionDAO;
	}

	public void setFinMaintainInstructionDAO(FinMaintainInstructionDAO finMaintainInstructionDAO) {
		this.finMaintainInstructionDAO = finMaintainInstructionDAO;
	}

	public void setFinOptionService(FinOptionService finOptionService) {
		this.finOptionService = finOptionService;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method, false);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		FinMaintainInstruction finMaintainInstruction = (FinMaintainInstruction) auditHeader.getAuditDetail()
				.getModelData();
		String usrLanguage = finMaintainInstruction.getUserDetails().getLanguage();

		List<FinOption> finOptions = finMaintainInstruction.getFinOptions();
		if (CollectionUtils.isNotEmpty(finOptions)) {
			List<AuditDetail> details = finMaintainInstruction.getAuditDetailMap().get("FinOptions");
			auditDetails.addAll(finOptionService.validateFinOptions(details, usrLanguage, method));
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method,
			boolean isUniqueCheckReq) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		FinMaintainInstruction finMaintainInstruction = (FinMaintainInstruction) auditDetail.getModelData();

		// Check the unique keys.
		if (isUniqueCheckReq && finMaintainInstruction.isNew()
				&& finMaintainInstructionDAO.isDuplicateKey(finMaintainInstruction.getEvent(),
						finMaintainInstruction.getFinReference(),
						finMaintainInstruction.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_FinMaintainInstruction_Event") + ": "
					+ finMaintainInstruction.getEvent();
			parameters[1] = PennantJavaUtil.getLabel("label_FinReference") + " : "
					+ finMaintainInstruction.getFinReference();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinMaintainInstruction finMaintainInstruction = (FinMaintainInstruction) auditHeader.getAuditDetail()
				.getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (finMaintainInstruction.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (finMaintainInstruction.getFinOptions() != null && finMaintainInstruction.getFinOptions().size() > 0) {
			auditDetailMap.put("FinOptions", setFinOptionAuditData(finMaintainInstruction, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinOptions"));
		}

		finMaintainInstruction.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(finMaintainInstruction);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private List<AuditDetail> setFinOptionAuditData(FinMaintainInstruction finMaintainInstruction, String auditTranType,
			String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinOption finOption = new FinOption();

		String[] fields = PennantJavaUtil.getFieldDetails(finOption, finOption.getExcludeFields());

		for (int i = 0; i < finMaintainInstruction.getFinOptions().size(); i++) {
			FinOption finOptions = finMaintainInstruction.getFinOptions().get(i);

			if (StringUtils.isEmpty(finOptions.getRecordType())) {
				continue;
			}

			finOptions.setFinReference(finMaintainInstruction.getFinReference());
			finOptions.setWorkflowId(finMaintainInstruction.getWorkflowId());

			boolean isRcdType = false;

			if (finOptions.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finOptions.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finOptions.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finOptions.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (finMaintainInstruction.isWorkflow()) {
					isRcdType = true;
				}
			} else if (finOptions.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finOptions.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				finOptions.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finOptions.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finOptions.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finOptions.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			finOptions.setRecordStatus(finMaintainInstruction.getRecordStatus());
			finOptions.setUserDetails(finMaintainInstruction.getUserDetails());
			finOptions.setLastMntOn(finMaintainInstruction.getLastMntOn());
			finOptions.setLastMntBy(finMaintainInstruction.getLastMntBy());

			auditDetails.add(
					new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finOptions.getBefImage(), finOptions));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}
