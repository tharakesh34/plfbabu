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
import com.pennant.backend.dao.finance.financialSummary.RecommendationNotesDetailsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.financialsummary.RecommendationNotes;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.financialsummary.RecommendationNotesDetailsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>CustomerPhoneNumber</b>.<br>
 * 
 */
public class RecommendationNotesDetailsServiceImpl extends GenericService<RecommendationNotes>
		implements RecommendationNotesDetailsService {
	private static Logger logger = LogManager.getLogger(RecommendationNotesDetailsServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private RecommendationNotesDetailsDAO recommendationNotesDetailsDAO;

	public RecommendationNotesDetailsServiceImpl() {
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
		RecommendationNotes recommendationNotesDetails = (RecommendationNotes) auditHeader.getAuditDetail()
				.getModelData();

		if (recommendationNotesDetails.isWorkflow()) {
			tableType = "_Temp";
		}

		if (recommendationNotesDetails.isNewRecord()) {
			recommendationNotesDetails
					.setId(getRecommendationNotesDetailsDAO().save(recommendationNotesDetails, tableType));
			auditHeader.getAuditDetail().setModelData(recommendationNotesDetails);
		} else {
			getRecommendationNotesDetailsDAO().update(recommendationNotesDetails, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	@Override
	public void saveOrUpdate(FinanceDetail financeDetail, AuditHeader auditHeader, String tableType) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		List<RecommendationNotes> recommendationNotesDetailsList = financeDetail.getRecommendationNoteList();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		for (RecommendationNotes recommendationNotes : recommendationNotesDetailsList) {
			recommendationNotes.setTaskId(financeMain.getTaskId());
			recommendationNotes.setNextTaskId(financeMain.getNextTaskId());
			recommendationNotes.setRoleCode(financeMain.getRoleCode());
			recommendationNotes.setNextRoleCode(financeMain.getNextRoleCode());
			recommendationNotes.setRecordStatus(financeMain.getRecordStatus());
			recommendationNotes.setWorkflowId(financeMain.getWorkflowId());
			recommendationNotes.setFinReference(financeMain.getFinReference());
			if (recommendationNotes.isNewRecord()) {
				getRecommendationNotesDetailsDAO().save(recommendationNotes, tableType);
				auditDetails.add(getAuditDetails(recommendationNotes, 1, PennantConstants.TRAN_ADD));
			} else if (StringUtils.trimToEmpty(recommendationNotes.getRecordType())
					.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				getRecommendationNotesDetailsDAO().update(recommendationNotes, tableType);
				auditDetails.add(getAuditDetails(recommendationNotes, 1, PennantConstants.TRAN_DEL));
			} else {
				getRecommendationNotesDetailsDAO().update(recommendationNotes, tableType);
				auditDetails.add(getAuditDetails(recommendationNotes, 1, PennantConstants.TRAN_UPD));
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

	public AuditDetail getAuditDetails(RecommendationNotes recommendationNotesDetails, int auditSeq, String transType) {
		String[] fields = PennantJavaUtil.getFieldDetails(new RecommendationNotes(),
				new RecommendationNotes().getExcludeFields());
		return new AuditDetail(transType, auditSeq, fields[0], fields[1], recommendationNotesDetails.getBefImage(),
				recommendationNotesDetails);
	}

	public AuditHeader getAuditHeader(AuditHeader auditHeader) {
		AuditHeader newauditHeader = new AuditHeader();
		newauditHeader.setAuditModule(ModuleUtil.getTableName(RecommendationNotes.class.getSimpleName()));
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
	public List<AuditDetail> delete(List<RecommendationNotes> recommendationNotesDetailsList, TableType tableType,
			String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<>();
		String[] fields = null;

		if (recommendationNotesDetailsList != null && !recommendationNotesDetailsList.isEmpty()) {
			int auditSeq = 1;
			for (RecommendationNotes recommendationNotesDetails : recommendationNotesDetailsList) {
				getRecommendationNotesDetailsDAO().delete(recommendationNotesDetails, tableType.toString());
				fields = PennantJavaUtil.getFieldDetails(recommendationNotesDetails,
						recommendationNotesDetails.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditSeq, fields[0], fields[1],
						recommendationNotesDetails.getBefImage(), recommendationNotesDetails));
				auditSeq++;
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> doApprove(List<RecommendationNotes> recommendationNotesDetails, TableType tableType,
			String auditTranType) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return doProcess(recommendationNotesDetails, tableType, auditTranType, true);
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		RecommendationNotes recommendationNotesDetails = (RecommendationNotes) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getRecommendationNotesDetailsDAO().delete(recommendationNotesDetails, "_Temp");

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
	public List<AuditDetail> doProcess(List<RecommendationNotes> recommendationNotesDetails, TableType tableType,
			String auditTranType, boolean isApproveRcd) {
		List<AuditDetail> auditDetails = new ArrayList<>();
		auditDetails.addAll(
				processRecommendationNotesDetails(recommendationNotesDetails, tableType, auditTranType, isApproveRcd));
		return auditDetails;
	}

	@Override
	public List<AuditDetail> processRecommendationNotesDetails(List<RecommendationNotes> recommendationNotesDetailsList,
			TableType tableType, String auditTranType, boolean isApproveRcd) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		if (CollectionUtils.isEmpty(recommendationNotesDetailsList)) {
			return auditDetails;
		}

		int i = 0;
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (RecommendationNotes recommendationNotesDetails : recommendationNotesDetailsList) {
			if (StringUtils.isEmpty(StringUtils.trimToEmpty(recommendationNotesDetails.getRecordType()))) {
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
				recommendationNotesDetails.setRoleCode("");
				recommendationNotesDetails.setNextRoleCode("");
				recommendationNotesDetails.setTaskId("");
				recommendationNotesDetails.setNextTaskId("");
			}
			if (StringUtils.isEmpty(tableType.getSuffix())) {
				recommendationNotesDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				recommendationNotesDetails.setNewRecord(true);
			}

			recommendationNotesDetails.setWorkflowId(0);
			if (StringUtils.equalsIgnoreCase(recommendationNotesDetails.getRecordType(),
					PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (recommendationNotesDetails.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(recommendationNotesDetails.getRecordType())) {
					recommendationNotesDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(recommendationNotesDetails.getRecordType())) {
					recommendationNotesDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(recommendationNotesDetails.getRecordType())) {
					recommendationNotesDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (StringUtils.equalsIgnoreCase(recommendationNotesDetails.getRecordType(),
					(PennantConstants.RECORD_TYPE_NEW))) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (StringUtils.equalsIgnoreCase(recommendationNotesDetails.getRecordType(),
					(PennantConstants.RECORD_TYPE_UPD))) {
				updateRecord = true;
			} else if (StringUtils.equalsIgnoreCase(recommendationNotesDetails.getRecordType(),
					(PennantConstants.RECORD_TYPE_DEL))) {
				if (approveRec) {
					deleteRecord = true;
				} else if (recommendationNotesDetails.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = recommendationNotesDetails.getRecordType();
				recordStatus = recommendationNotesDetails.getRecordStatus();
				recommendationNotesDetails.setRecordType("");
				recommendationNotesDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				getRecommendationNotesDetailsDAO().delete(recommendationNotesDetails, TableType.TEMP_TAB.getSuffix());
			}
			if (saveRecord) {
				getRecommendationNotesDetailsDAO().save(recommendationNotesDetails, tableType.getSuffix());
			}

			if (updateRecord) {
				getRecommendationNotesDetailsDAO().update(recommendationNotesDetails, tableType.getSuffix());
			}

			if (deleteRecord) {
				getRecommendationNotesDetailsDAO().delete(recommendationNotesDetails, tableType.getSuffix());
			}

			if (approveRec) {
				recommendationNotesDetails.setRecordType(rcdType);
				recommendationNotesDetails.setRecordStatus(recordStatus);
			}

			String[] fields = PennantJavaUtil.getFieldDetails(recommendationNotesDetails,
					recommendationNotesDetails.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					recommendationNotesDetails.getBefImage(), recommendationNotesDetails));
			i++;
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public int getVersion(long id, String typeCode) {
		return getRecommendationNotesDetailsDAO().getVersion(id, typeCode);

	}

	public RecommendationNotesDetailsDAO getRecommendationNotesDetailsDAO() {
		return recommendationNotesDetailsDAO;
	}

	public void setRecommendationNotesDetailsDAO(RecommendationNotesDetailsDAO recommendationNotesDetailsDAO) {
		this.recommendationNotesDetailsDAO = recommendationNotesDetailsDAO;
	}

}