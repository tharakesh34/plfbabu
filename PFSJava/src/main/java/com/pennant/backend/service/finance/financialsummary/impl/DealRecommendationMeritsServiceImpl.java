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
import com.pennant.backend.dao.finance.financialSummary.DealRecommendationMeritsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.financialsummary.DealRecommendationMerits;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.financialsummary.DealRecommendationMeritsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>CustomerPhoneNumber</b>.<br>
 * 
 */
public class DealRecommendationMeritsServiceImpl extends GenericService<DealRecommendationMerits>
		implements DealRecommendationMeritsService {
	private static Logger logger = LogManager.getLogger(DealRecommendationMeritsServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private DealRecommendationMeritsDAO dealRecommendationMeritsDAO;

	public DealRecommendationMeritsServiceImpl() {
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
		DealRecommendationMerits dealRecommendationMerits = (DealRecommendationMerits) auditHeader.getAuditDetail()
				.getModelData();

		if (dealRecommendationMerits.isWorkflow()) {
			tableType = "_Temp";
		}

		if (dealRecommendationMerits.isNew()) {
			dealRecommendationMerits.setId(getDealRecommendationMeritsDAO().save(dealRecommendationMerits, tableType));
			auditHeader.getAuditDetail().setModelData(dealRecommendationMerits);
		} else {
			getDealRecommendationMeritsDAO().update(dealRecommendationMerits, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	@Override
	public void saveOrUpdate(FinanceDetail financeDetail, AuditHeader auditHeader, String tableType) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		List<DealRecommendationMerits> dealRecommendationMeritsList = financeDetail
				.getDealRecommendationMeritsDetailsList();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		for (DealRecommendationMerits dealRecommendationMerits : dealRecommendationMeritsList) {
			dealRecommendationMerits.setTaskId(financeMain.getTaskId());
			dealRecommendationMerits.setNextTaskId(financeMain.getNextTaskId());
			dealRecommendationMerits.setRoleCode(financeMain.getRoleCode());
			dealRecommendationMerits.setNextRoleCode(financeMain.getNextRoleCode());
			dealRecommendationMerits.setRecordStatus(financeMain.getRecordStatus());
			dealRecommendationMerits.setWorkflowId(financeMain.getWorkflowId());
			dealRecommendationMerits.setFinReference(financeMain.getFinReference());
			if (dealRecommendationMerits.isNew()) {
				getDealRecommendationMeritsDAO().save(dealRecommendationMerits, tableType);
				auditDetails.add(getAuditDetails(dealRecommendationMerits, 1, PennantConstants.TRAN_ADD));
			} else if (StringUtils.trimToEmpty(dealRecommendationMerits.getRecordType())
					.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				getDealRecommendationMeritsDAO().update(dealRecommendationMerits, tableType);
				auditDetails.add(getAuditDetails(dealRecommendationMerits, 1, PennantConstants.TRAN_DEL));
			} else {
				getDealRecommendationMeritsDAO().update(dealRecommendationMerits, tableType);
				auditDetails.add(getAuditDetails(dealRecommendationMerits, 1, PennantConstants.TRAN_UPD));
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

	public AuditDetail getAuditDetails(DealRecommendationMerits dealRecommendationMerits, int auditSeq,
			String transType) {
		String[] fields = PennantJavaUtil.getFieldDetails(new DealRecommendationMerits(),
				new DealRecommendationMerits().getExcludeFields());
		return new AuditDetail(transType, auditSeq, fields[0], fields[1], dealRecommendationMerits.getBefImage(),
				dealRecommendationMerits);
	}

	public AuditHeader getAuditHeader(AuditHeader auditHeader) {
		AuditHeader newauditHeader = new AuditHeader();
		newauditHeader.setAuditModule(ModuleUtil.getTableName(DealRecommendationMerits.class.getSimpleName()));
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
	public List<AuditDetail> delete(List<DealRecommendationMerits> dealRecommendationMeritsList, TableType tableType,
			String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<>();
		String[] fields = null;

		if (dealRecommendationMeritsList != null && !dealRecommendationMeritsList.isEmpty()) {
			int auditSeq = 1;
			for (DealRecommendationMerits dealRecommendationMerits : dealRecommendationMeritsList) {
				getDealRecommendationMeritsDAO().delete(dealRecommendationMerits, tableType.toString());
				fields = PennantJavaUtil.getFieldDetails(dealRecommendationMerits,
						dealRecommendationMerits.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditSeq, fields[0], fields[1],
						dealRecommendationMerits.getBefImage(), dealRecommendationMerits));
				auditSeq++;
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> doApprove(List<DealRecommendationMerits> dealRecommendationMerits, TableType tableType,
			String auditTranType) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return doProcess(dealRecommendationMerits, tableType, auditTranType, true);
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		DealRecommendationMerits dealRecommendationMerits = (DealRecommendationMerits) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getDealRecommendationMeritsDAO().delete(dealRecommendationMerits, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public int getVersion(long id, String typeCode) {
		return getDealRecommendationMeritsDAO().getVersion(id, typeCode);

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
	public List<AuditDetail> doProcess(List<DealRecommendationMerits> dealRecommendationMerits, TableType tableType,
			String auditTranType, boolean isApproveRcd) {
		List<AuditDetail> auditDetails = new ArrayList<>();
		auditDetails.addAll(
				processDealRecommendationMerits(dealRecommendationMerits, tableType, auditTranType, isApproveRcd));
		return auditDetails;
	}

	@Override
	public List<AuditDetail> processDealRecommendationMerits(
			List<DealRecommendationMerits> dealRecommendationMeritsList, TableType tableType, String auditTranType,
			boolean isApproveRcd) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		if (CollectionUtils.isEmpty(dealRecommendationMeritsList)) {
			return auditDetails;
		}

		int i = 0;
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (DealRecommendationMerits dealRecommendationMerits : dealRecommendationMeritsList) {
			if (StringUtils.isEmpty(StringUtils.trimToEmpty(dealRecommendationMerits.getRecordType()))) {
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
				dealRecommendationMerits.setRoleCode("");
				dealRecommendationMerits.setNextRoleCode("");
				dealRecommendationMerits.setTaskId("");
				dealRecommendationMerits.setNextTaskId("");
			}

			dealRecommendationMerits.setWorkflowId(0);
			if (StringUtils.equalsIgnoreCase(dealRecommendationMerits.getRecordType(),
					PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (dealRecommendationMerits.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(dealRecommendationMerits.getRecordType())) {
					dealRecommendationMerits.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(dealRecommendationMerits.getRecordType())) {
					dealRecommendationMerits.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(dealRecommendationMerits.getRecordType())) {
					dealRecommendationMerits.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (StringUtils.equalsIgnoreCase(dealRecommendationMerits.getRecordType(),
					(PennantConstants.RECORD_TYPE_NEW))) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (StringUtils.equalsIgnoreCase(dealRecommendationMerits.getRecordType(),
					(PennantConstants.RECORD_TYPE_UPD))) {
				updateRecord = true;
			} else if (StringUtils.equalsIgnoreCase(dealRecommendationMerits.getRecordType(),
					(PennantConstants.RECORD_TYPE_DEL))) {
				if (approveRec) {
					deleteRecord = true;
				} else if (dealRecommendationMerits.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = dealRecommendationMerits.getRecordType();
				recordStatus = dealRecommendationMerits.getRecordStatus();
				dealRecommendationMerits.setRecordType("");
				dealRecommendationMerits.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				getDealRecommendationMeritsDAO().delete(dealRecommendationMerits, TableType.TEMP_TAB.getSuffix());
			}
			if (saveRecord) {
				getDealRecommendationMeritsDAO().save(dealRecommendationMerits, tableType.getSuffix());
			}

			if (updateRecord) {
				getDealRecommendationMeritsDAO().update(dealRecommendationMerits, tableType.getSuffix());
			}

			if (deleteRecord) {
				getDealRecommendationMeritsDAO().delete(dealRecommendationMerits, tableType.getSuffix());
			}

			if (approveRec) {
				dealRecommendationMerits.setRecordType(rcdType);
				dealRecommendationMerits.setRecordStatus(recordStatus);
			}

			String[] fields = PennantJavaUtil.getFieldDetails(dealRecommendationMerits,
					dealRecommendationMerits.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					dealRecommendationMerits.getBefImage(), dealRecommendationMerits));
			i++;
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public DealRecommendationMeritsDAO getDealRecommendationMeritsDAO() {
		return dealRecommendationMeritsDAO;
	}

	public void setDealRecommendationMeritsDAO(DealRecommendationMeritsDAO dealRecommendationMeritsDAO) {
		this.dealRecommendationMeritsDAO = dealRecommendationMeritsDAO;
	}

}