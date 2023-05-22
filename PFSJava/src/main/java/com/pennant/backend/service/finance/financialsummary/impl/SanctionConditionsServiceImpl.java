/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : CustomerPhoneNumberServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 *
 * * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.finance.financialsummary.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.financialSummary.SanctionConditionsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.financialsummary.SanctionConditions;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.financialsummary.SanctionConditionsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>CustomerPhoneNumber</b>.<br>
 * 
 */
public class SanctionConditionsServiceImpl extends GenericService<SanctionConditions>
		implements SanctionConditionsService {
	private static Logger logger = LogManager.getLogger(SanctionConditionsServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private SanctionConditionsDAO sanctionConditionsDAO;

	public SanctionConditionsServiceImpl() {
		super();
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

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		SanctionConditions sanctionConditions = (SanctionConditions) auditHeader.getAuditDetail().getModelData();

		if (sanctionConditions.isWorkflow()) {
			tableType = "_Temp";
		}

		if (sanctionConditions.isNewRecord()) {
			sanctionConditions.setId(getSanctionConditionsDAO().save(sanctionConditions, tableType));
			auditHeader.getAuditDetail().setModelData(sanctionConditions);
		} else {
			getSanctionConditionsDAO().update(sanctionConditions, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	@Override
	public void saveOrUpdate(FinanceDetail financeDetail, AuditHeader auditHeader, String tableType) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();
		List<SanctionConditions> sanctionConditionsList = financeDetail.getSanctionDetailsList();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		for (SanctionConditions sc : sanctionConditionsList) {
			sc.setTaskId(financeMain.getTaskId());
			sc.setNextTaskId(financeMain.getNextTaskId());
			sc.setRoleCode(financeMain.getRoleCode());
			sc.setNextRoleCode(financeMain.getNextRoleCode());
			sc.setRecordStatus(financeMain.getRecordStatus());
			sc.setWorkflowId(financeMain.getWorkflowId());
			sc.setFinID(financeMain.getFinID());
			sc.setFinReference(financeMain.getFinReference());
			if (sc.isNewRecord()) {
				getSanctionConditionsDAO().save(sc, tableType);
				auditDetails.add(getAuditDetails(sc, 1, PennantConstants.TRAN_ADD));
			} else if (StringUtils.trimToEmpty(sc.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				getSanctionConditionsDAO().update(sc, tableType);
				auditDetails.add(getAuditDetails(sc, 1, PennantConstants.TRAN_DEL));
			} else {
				getSanctionConditionsDAO().update(sc, tableType);
				auditDetails.add(getAuditDetails(sc, 1, PennantConstants.TRAN_UPD));
			}
		}
		addAudit(auditHeader, auditDetails);
		logger.debug(Literal.LEAVING);
	}

	private void addAudit(AuditHeader auditHeader, List<AuditDetail> auditDetails) {
		// Add audit if any changes
		if (auditDetails.isEmpty()) {
			return;
		}
		AuditHeader header = getAuditHeader(auditHeader);
		header.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(header);
	}

	public AuditDetail getAuditDetails(SanctionConditions sanctionConditions, int auditSeq, String transType) {
		String[] fields = PennantJavaUtil.getFieldDetails(new SanctionConditions(),
				new SanctionConditions().getExcludeFields());
		return new AuditDetail(transType, auditSeq, fields[0], fields[1], sanctionConditions.getBefImage(),
				sanctionConditions);
	}

	public AuditHeader getAuditHeader(AuditHeader auditHeader) {
		AuditHeader newauditHeader = new AuditHeader();
		newauditHeader.setAuditModule(ModuleUtil.getTableName(SanctionConditions.class.getSimpleName()));
		newauditHeader.setAuditReference(auditHeader.getAuditReference());
		newauditHeader.setAuditUsrId(auditHeader.getAuditUsrId());
		newauditHeader.setAuditBranchCode(auditHeader.getAuditBranchCode());
		newauditHeader.setAuditDeptCode(auditHeader.getAuditDeptCode());
		newauditHeader.setAuditSystemIP(auditHeader.getAuditSystemIP());
		newauditHeader.setAuditSessionID(auditHeader.getAuditSessionID());
		newauditHeader.setUsrLanguage(auditHeader.getUsrLanguage());
		return newauditHeader;
	}

	@Override
	public List<AuditDetail> delete(List<SanctionConditions> sanctionConditionsList, TableType tableType,
			String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<>();
		String[] fields = null;

		if (sanctionConditionsList != null && !sanctionConditionsList.isEmpty()) {
			int auditSeq = 1;
			for (SanctionConditions sanctionConditions : sanctionConditionsList) {
				getSanctionConditionsDAO().delete(sanctionConditions, tableType.toString());
				fields = PennantJavaUtil.getFieldDetails(sanctionConditions, sanctionConditions.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditSeq, fields[0], fields[1],
						sanctionConditions.getBefImage(), sanctionConditions));
				auditSeq++;
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> doApprove(List<SanctionConditions> sanctionConditions, TableType tableType,
			String auditTranType) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return doProcess(sanctionConditions, tableType, auditTranType, true);
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		SanctionConditions sanctionConditions = (SanctionConditions) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSanctionConditionsDAO().delete(sanctionConditions, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public int getVersion(long id, String typeCode) {
		return getSanctionConditionsDAO().getVersion(id, typeCode);

	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		// auditHeader =
		// getCustomerPhoneNumberValidation().phoneNumberValidation(auditHeader,
		// method);
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public List<AuditDetail> doProcess(List<SanctionConditions> sanctionConditions, TableType tableType,
			String auditTranType, boolean isApproveRcd) {
		List<AuditDetail> auditDetails = new ArrayList<>();
		auditDetails.addAll(processSanctionConditions(sanctionConditions, tableType, auditTranType, isApproveRcd));
		return auditDetails;
	}

	@Override
	public List<AuditDetail> processSanctionConditions(List<SanctionConditions> sanctionConditionsList,
			TableType tableType, String auditTranType, boolean isApproveRcd) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		if (CollectionUtils.isEmpty(sanctionConditionsList)) {
			return auditDetails;
		}

		int i = 0;
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (SanctionConditions sanctionConditions : sanctionConditionsList) {
			if (StringUtils.isEmpty(StringUtils.trimToEmpty(sanctionConditions.getRecordType()))) {
				continue;
			}

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = isApproveRcd;
			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(tableType.name())) {
				approveRec = true;
				sanctionConditions.setRoleCode("");
				sanctionConditions.setNextRoleCode("");
				sanctionConditions.setTaskId("");
				sanctionConditions.setNextTaskId("");
			}

			sanctionConditions.setWorkflowId(0);
			if (StringUtils.equalsIgnoreCase(sanctionConditions.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (sanctionConditions.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(sanctionConditions.getRecordType())) {
					sanctionConditions.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(sanctionConditions.getRecordType())) {
					sanctionConditions.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(sanctionConditions.getRecordType())) {
					sanctionConditions.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (StringUtils.equalsIgnoreCase(sanctionConditions.getRecordType(),
					(PennantConstants.RECORD_TYPE_NEW))) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (StringUtils.equalsIgnoreCase(sanctionConditions.getRecordType(),
					(PennantConstants.RECORD_TYPE_UPD))) {
				updateRecord = true;
			} else if (StringUtils.equalsIgnoreCase(sanctionConditions.getRecordType(),
					(PennantConstants.RECORD_TYPE_DEL))) {
				if (approveRec) {
					deleteRecord = true;
				} else if (sanctionConditions.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = sanctionConditions.getRecordType();
				recordStatus = sanctionConditions.getRecordStatus();
				sanctionConditions.setRecordType("");
				sanctionConditions.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				getSanctionConditionsDAO().delete(sanctionConditions, TableType.TEMP_TAB.getSuffix());
			}
			if (saveRecord) {
				getSanctionConditionsDAO().save(sanctionConditions, tableType.getSuffix());
			}

			if (updateRecord) {
				getSanctionConditionsDAO().update(sanctionConditions, tableType.getSuffix());
			}

			if (deleteRecord) {
				getSanctionConditionsDAO().delete(sanctionConditions, tableType.getSuffix());
			}

			if (approveRec) {
				sanctionConditions.setRecordType(rcdType);
				sanctionConditions.setRecordStatus(recordStatus);
			}

			String[] fields = PennantJavaUtil.getFieldDetails(sanctionConditions,
					sanctionConditions.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					sanctionConditions.getBefImage(), sanctionConditions));
			i++;
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public SanctionConditionsDAO getSanctionConditionsDAO() {
		return sanctionConditionsDAO;
	}

	public void setSanctionConditionsDAO(SanctionConditionsDAO sanctionConditionsDAO) {
		this.sanctionConditionsDAO = sanctionConditionsDAO;
	}

}