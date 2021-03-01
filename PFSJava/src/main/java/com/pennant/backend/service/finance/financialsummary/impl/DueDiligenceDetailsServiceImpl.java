/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  CustomerPhoneNumberServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
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
import com.pennant.backend.dao.finance.financialSummary.DueDiligenceDetailsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.financialsummary.DueDiligenceDetails;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.financialsummary.DueDiligenceDetailsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>CustomerPhoneNumber</b>.<br>
 * 
 */
public class DueDiligenceDetailsServiceImpl extends GenericService<DueDiligenceDetails>
		implements DueDiligenceDetailsService {
	private static Logger logger = LogManager.getLogger(DueDiligenceDetailsServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private DueDiligenceDetailsDAO dueDiligenceDetailsDAO;

	public DueDiligenceDetailsServiceImpl() {
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
		DueDiligenceDetails dueDiligenceDetails = (DueDiligenceDetails) auditHeader.getAuditDetail().getModelData();

		if (dueDiligenceDetails.isWorkflow()) {
			tableType = "_Temp";
		}

		if (dueDiligenceDetails.isNew()) {
			dueDiligenceDetails.setId(getDueDiligenceDetailsDAO().save(dueDiligenceDetails, tableType));
			auditHeader.getAuditDetail().setModelData(dueDiligenceDetails);
		} else {
			getDueDiligenceDetailsDAO().update(dueDiligenceDetails, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	@Override
	public void saveOrUpdate(FinanceDetail financeDetail, AuditHeader auditHeader, String tableType) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		List<DueDiligenceDetails> dueDiligenceDetailsList = financeDetail.getDueDiligenceDetailsList();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		for (DueDiligenceDetails dueDiligenceDetails : dueDiligenceDetailsList) {
			dueDiligenceDetails.setTaskId(financeMain.getTaskId());
			dueDiligenceDetails.setNextTaskId(financeMain.getNextTaskId());
			dueDiligenceDetails.setRoleCode(financeMain.getRoleCode());
			dueDiligenceDetails.setNextRoleCode(financeMain.getNextRoleCode());
			dueDiligenceDetails.setRecordStatus(financeMain.getRecordStatus());
			dueDiligenceDetails.setWorkflowId(financeMain.getWorkflowId());
			dueDiligenceDetails.setFinReference(financeMain.getFinReference());
			if (dueDiligenceDetails.isNew()) {
				getDueDiligenceDetailsDAO().save(dueDiligenceDetails, tableType);
				auditDetails.add(getAuditDetails(dueDiligenceDetails, 1, PennantConstants.TRAN_ADD));
			} else if (StringUtils.trimToEmpty(dueDiligenceDetails.getRecordType())
					.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				getDueDiligenceDetailsDAO().update(dueDiligenceDetails, tableType);
				auditDetails.add(getAuditDetails(dueDiligenceDetails, 1, PennantConstants.TRAN_DEL));
			} else {
				getDueDiligenceDetailsDAO().update(dueDiligenceDetails, tableType);
				auditDetails.add(getAuditDetails(dueDiligenceDetails, 1, PennantConstants.TRAN_UPD));
			}
		}
		addAudit(auditHeader, auditDetails);
		logger.debug(Literal.LEAVING);
	}

	private void addAudit(AuditHeader auditHeader, List<AuditDetail> auditDetails) {
		//Add audit if any changes
		if (auditDetails.isEmpty()) {
			return;
		}
		AuditHeader header = getAuditHeader(auditHeader);
		header.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(header);
	}

	public AuditDetail getAuditDetails(DueDiligenceDetails dueDiligenceDetails, int auditSeq, String transType) {
		String[] fields = PennantJavaUtil.getFieldDetails(new DueDiligenceDetails(),
				new DueDiligenceDetails().getExcludeFields());
		return new AuditDetail(transType, auditSeq, fields[0], fields[1], dueDiligenceDetails.getBefImage(),
				dueDiligenceDetails);
	}

	public AuditHeader getAuditHeader(AuditHeader auditHeader) {
		AuditHeader newauditHeader = new AuditHeader();
		newauditHeader.setAuditModule(ModuleUtil.getTableName(DueDiligenceDetails.class.getSimpleName()));
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
	public List<AuditDetail> delete(List<DueDiligenceDetails> dueDiligenceDetailsList, TableType tableType,
			String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<>();
		String[] fields = null;

		if (dueDiligenceDetailsList != null && !dueDiligenceDetailsList.isEmpty()) {
			int auditSeq = 1;
			for (DueDiligenceDetails dueDiligenceDetails : dueDiligenceDetailsList) {
				getDueDiligenceDetailsDAO().delete(dueDiligenceDetails, tableType.toString());
				fields = PennantJavaUtil.getFieldDetails(dueDiligenceDetails, dueDiligenceDetails.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditSeq, fields[0], fields[1],
						dueDiligenceDetails.getBefImage(), dueDiligenceDetails));
				auditSeq++;
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> doApprove(List<DueDiligenceDetails> dueDiligenceDetails, TableType tableType,
			String auditTranType) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return doProcess(dueDiligenceDetails, tableType, auditTranType, true);
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		DueDiligenceDetails dueDiligenceDetails = (DueDiligenceDetails) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getDueDiligenceDetailsDAO().delete(dueDiligenceDetails, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
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
	public List<AuditDetail> doProcess(List<DueDiligenceDetails> dueDiligenceDetails, TableType tableType,
			String auditTranType, boolean isApproveRcd) {
		List<AuditDetail> auditDetails = new ArrayList<>();
		auditDetails.addAll(processDueDiligenceDetails(dueDiligenceDetails, tableType, auditTranType, isApproveRcd));
		return auditDetails;
	}

	@Override
	public List<AuditDetail> processDueDiligenceDetails(List<DueDiligenceDetails> dueDiligenceDetailsList,
			TableType tableType, String auditTranType, boolean isApproveRcd) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		if (CollectionUtils.isEmpty(dueDiligenceDetailsList)) {
			return auditDetails;
		}

		int i = 0;
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (DueDiligenceDetails dueDiligenceDetails : dueDiligenceDetailsList) {
			if (StringUtils.isEmpty(StringUtils.trimToEmpty(dueDiligenceDetails.getRecordType()))) {
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
				dueDiligenceDetails.setRoleCode("");
				dueDiligenceDetails.setNextRoleCode("");
				dueDiligenceDetails.setTaskId("");
				dueDiligenceDetails.setNextTaskId("");
			}
			if (StringUtils.isEmpty(tableType.getSuffix())) {
				dueDiligenceDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				dueDiligenceDetails.setNewRecord(true);
			}

			dueDiligenceDetails.setWorkflowId(0);
			if (StringUtils.equalsIgnoreCase(dueDiligenceDetails.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (dueDiligenceDetails.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(dueDiligenceDetails.getRecordType())) {
					dueDiligenceDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(dueDiligenceDetails.getRecordType())) {
					dueDiligenceDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(dueDiligenceDetails.getRecordType())) {
					dueDiligenceDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (StringUtils.equalsIgnoreCase(dueDiligenceDetails.getRecordType(),
					(PennantConstants.RECORD_TYPE_NEW))) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (StringUtils.equalsIgnoreCase(dueDiligenceDetails.getRecordType(),
					(PennantConstants.RECORD_TYPE_UPD))) {
				updateRecord = true;
			} else if (StringUtils.equalsIgnoreCase(dueDiligenceDetails.getRecordType(),
					(PennantConstants.RECORD_TYPE_DEL))) {
				if (approveRec) {
					deleteRecord = true;
				} else if (dueDiligenceDetails.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = dueDiligenceDetails.getRecordType();
				recordStatus = dueDiligenceDetails.getRecordStatus();
				dueDiligenceDetails.setRecordType("");
				dueDiligenceDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				getDueDiligenceDetailsDAO().delete(dueDiligenceDetails, TableType.TEMP_TAB.getSuffix());
			}
			if (saveRecord) {
				getDueDiligenceDetailsDAO().save(dueDiligenceDetails, tableType.getSuffix());
			}

			if (updateRecord) {
				getDueDiligenceDetailsDAO().update(dueDiligenceDetails, tableType.getSuffix());
			}

			if (deleteRecord) {
				getDueDiligenceDetailsDAO().delete(dueDiligenceDetails, tableType.getSuffix());
			}

			if (approveRec) {
				dueDiligenceDetails.setRecordType(rcdType);
				dueDiligenceDetails.setRecordStatus(recordStatus);
			}

			String[] fields = PennantJavaUtil.getFieldDetails(dueDiligenceDetails,
					dueDiligenceDetails.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					dueDiligenceDetails.getBefImage(), dueDiligenceDetails));
			i++;
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public int getVersion(long id, String typeCode) {
		return getDueDiligenceDetailsDAO().getVersion(id, typeCode);

	}

	public DueDiligenceDetailsDAO getDueDiligenceDetailsDAO() {
		return dueDiligenceDetailsDAO;
	}

	public void setDueDiligenceDetailsDAO(DueDiligenceDetailsDAO dueDiligenceDetailsDAO) {
		this.dueDiligenceDetailsDAO = dueDiligenceDetailsDAO;
	}

}