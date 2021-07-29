package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.BlackListCustomerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.blacklist.NegativeReasoncodes;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.BlacklistCustomerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

public class BlacklistCustomerServiceImpl extends GenericService<BlackListCustomers>
		implements BlacklistCustomerService {

	private static Logger logger = LogManager.getLogger(BlacklistCustomerServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private BlackListCustomerDAO blacklistCustomerDAO;

	public BlacklistCustomerServiceImpl() {
		super();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {

		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		TableType tableType = TableType.MAIN_TAB;
		BlackListCustomers blackListCustomers = (BlackListCustomers) auditHeader.getAuditDetail().getModelData();

		if (blackListCustomers.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (blackListCustomers.isNewRecord()) {
			blackListCustomers.setCustCIF(getBlacklistCustomerDAO().save(blackListCustomers, tableType));
			auditHeader.getAuditDetail().setModelData(blackListCustomers);
			auditHeader.setAuditReference(String.valueOf(blackListCustomers.getCustCIF()));
		} else {
			getBlacklistCustomerDAO().update(blackListCustomers, tableType);
		}
		if (CollectionUtils.isNotEmpty(blackListCustomers.getNegativeReasoncodeList())) {

			getBlacklistCustomerDAO().deleteNegativeReasonList(blackListCustomers.getId(), tableType);
			List<AuditDetail> details = blackListCustomers.getAuditDetailMap().get("NegativeReasonCodes");
			details = processBlackListDetails(blackListCustomers, details, tableType);
			auditDetails.addAll(details);
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	private List<AuditDetail> processBlackListDetails(BlackListCustomers blackListCustomers, List<AuditDetail> details,
			TableType tableType) {
		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < details.size(); i++) {

			NegativeReasoncodes negativeReasoncodes = (NegativeReasoncodes) details.get(i).getModelData();
			negativeReasoncodes.setBlackListCIF(blackListCustomers.getId());
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(tableType.getSuffix())) {
				approveRec = true;
				negativeReasoncodes.setRoleCode("");
				negativeReasoncodes.setNextRoleCode("");
				negativeReasoncodes.setTaskId("");
				negativeReasoncodes.setNextTaskId("");
			}
			if (negativeReasoncodes.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (negativeReasoncodes.isNewRecord()) {
				saveRecord = true;
				if (negativeReasoncodes.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					negativeReasoncodes.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (negativeReasoncodes.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					negativeReasoncodes.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (negativeReasoncodes.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					negativeReasoncodes.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (negativeReasoncodes.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (negativeReasoncodes.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (negativeReasoncodes.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (negativeReasoncodes.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {

				rcdType = negativeReasoncodes.getRecordType();
				recordStatus = negativeReasoncodes.getRecordStatus();
				negativeReasoncodes.setRecordType("");
				negativeReasoncodes.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (true) {
				getBlacklistCustomerDAO().deleteNegativeReason(negativeReasoncodes.getId(), tableType);
			}
			if (true) {
				getBlacklistCustomerDAO().saveNegativeReason(negativeReasoncodes, tableType);
			}
			if (updateRecord) {
			}
			if (approveRec) {
				negativeReasoncodes.setRecordType(rcdType);
				negativeReasoncodes.setRecordStatus(recordStatus);
			}
			details.get(i).setModelData(negativeReasoncodes);

		}

		logger.debug("Leaving");
		return details;

	}

	@Override
	public BlackListCustomers getBlacklistCustomerById(String id) {
		return getBlacklistCustomerDAO().getBlacklistCustomerById(id, "_View");
	}

	@Override
	public BlackListCustomers getApprovedBlacklistById(String id) {
		return getBlacklistCustomerDAO().getBlacklistCustomerById(id, "");
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		BlackListCustomers blackListCustomers = (BlackListCustomers) auditHeader.getAuditDetail().getModelData();
		getBlacklistCustomerDAO().delete(blackListCustomers, TableType.MAIN_TAB);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		BlackListCustomers blackListCustomers = new BlackListCustomers();
		BeanUtils.copyProperties((BlackListCustomers) auditHeader.getAuditDetail().getModelData(), blackListCustomers);

		getBlacklistCustomerDAO().deleteNegativeReasonList(blackListCustomers.getCustCIF(), TableType.MAIN_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(blackListCustomers.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(blacklistCustomerDAO.getBlacklistCustomerById(blackListCustomers.getId(), ""));
		}
		if (blackListCustomers.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getBlacklistCustomerDAO().delete(blackListCustomers, TableType.MAIN_TAB);
		} else {
			blackListCustomers.setRoleCode("");
			blackListCustomers.setNextRoleCode("");
			blackListCustomers.setTaskId("");
			blackListCustomers.setNextTaskId("");
			blackListCustomers.setWorkflowId(0);

			if (blackListCustomers.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				blackListCustomers.setRecordType("");
				getBlacklistCustomerDAO().save(blackListCustomers, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				blackListCustomers.setRecordType("");
				getBlacklistCustomerDAO().update(blackListCustomers, TableType.MAIN_TAB);
			}
			if (CollectionUtils.isNotEmpty(blackListCustomers.getNegativeReasoncodeList())) {
				List<AuditDetail> details = blackListCustomers.getAuditDetailMap().get("NegativeReasonCodes");
				details = processBlackListDetails(blackListCustomers, details, TableType.MAIN_TAB);
				auditDetails.addAll(details);
			}
		}

		auditHeader.setAuditDetails(deleteChilds(blackListCustomers, TableType.TEMP_TAB, tranType));
		getBlacklistCustomerDAO().delete(blackListCustomers, TableType.TEMP_TAB);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(blackListCustomers);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private List<AuditDetail> deleteChilds(BlackListCustomers blackListCustomers, TableType tableType,
			String tranType) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (CollectionUtils.isNotEmpty(blackListCustomers.getNegativeReasoncodeList())) {
			String[] fields = PennantJavaUtil.getFieldDetails(new NegativeReasoncodes(),
					new NegativeReasoncodes().getExcludeFields());
			for (int i = 0; i < blackListCustomers.getNegativeReasoncodeList().size(); i++) {
				NegativeReasoncodes negativeReasoncodes = blackListCustomers.getNegativeReasoncodeList().get(i);
				if (StringUtils.isNotEmpty(negativeReasoncodes.getRecordType())
						|| StringUtils.isEmpty(tableType.getSuffix())) {
					auditDetails.add(new AuditDetail(tranType, i + 1, fields[0], fields[1],
							negativeReasoncodes.getBefImage(), negativeReasoncodes));
				}
			}
			getBlacklistCustomerDAO().deleteNegativeReasonList(blackListCustomers.getId(), tableType);
		}

		return auditDetails;

	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		BlackListCustomers blackListCustomers = (BlackListCustomers) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditDetails.addAll(deleteChilds(blackListCustomers, TableType.TEMP_TAB, PennantConstants.TRAN_WF));
		getBlacklistCustomerDAO().delete(blackListCustomers, TableType.TEMP_TAB);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		BlackListCustomers blackListCustomers = (BlackListCustomers) auditDetail.getModelData();
		// Check the unique keys.
		if (blackListCustomers.isNewRecord() && PennantConstants.RECORD_TYPE_NEW.equals(blackListCustomers.getRecordType())
				&& blacklistCustomerDAO.isDuplicateKey(blackListCustomers.getId(),
						blackListCustomers.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_CustCIF") + ":" + blackListCustomers.getCustCIF();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

	private List<AuditDetail> setNegativeReasonCodeAuditDetails(BlackListCustomers blackListCustomers,
			String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		NegativeReasoncodes NegReasonCode = new NegativeReasoncodes();

		String[] fields = PennantJavaUtil.getFieldDetails(new NegativeReasoncodes(), NegReasonCode.getExcludeFields());

		for (int i = 0; i < blackListCustomers.getNegativeReasoncodeList().size(); i++) {
			NegativeReasoncodes negativeReason = blackListCustomers.getNegativeReasoncodeList().get(i);
			if (StringUtils.isEmpty(negativeReason.getRecordType())) {
				continue;
			}

			negativeReason.setWorkflowId(blackListCustomers.getWorkflowId());

			boolean isRcdType = false;

			if (negativeReason.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				negativeReason.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (negativeReason.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				negativeReason.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (negativeReason.isWorkflow()) {
					isRcdType = true;
				}
			} else if (negativeReason.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				negativeReason.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				negativeReason.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (negativeReason.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (negativeReason.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| negativeReason.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			negativeReason.setRecordStatus(blackListCustomers.getRecordStatus());
			negativeReason.setUserDetails(blackListCustomers.getUserDetails());
			negativeReason.setLastMntOn(blackListCustomers.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], negativeReason.getBefImage(),
					negativeReason));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		BlackListCustomers blackListCustomers = (BlackListCustomers) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (blackListCustomers.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		if (CollectionUtils.isNotEmpty(blackListCustomers.getNegativeReasoncodeList())) {
			auditDetailMap.put("NegativeReasonCodes",
					setNegativeReasonCodeAuditDetails(blackListCustomers, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("NegativeReasonCodes"));
		}

		blackListCustomers.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(blackListCustomers);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving ");
		return auditHeader;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public BlackListCustomerDAO getBlacklistCustomerDAO() {
		return blacklistCustomerDAO;
	}

	public void setBlacklistCustomerDAO(BlackListCustomerDAO blacklistCustomerDAO) {
		this.blacklistCustomerDAO = blacklistCustomerDAO;
	}

}
