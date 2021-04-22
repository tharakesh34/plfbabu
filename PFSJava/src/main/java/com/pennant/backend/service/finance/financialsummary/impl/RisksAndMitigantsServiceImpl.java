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
import com.pennant.backend.dao.finance.financialSummary.RisksAndMitigantsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.financialsummary.RisksAndMitigants;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.financialsummary.RisksAndMitigantsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>CustomerPhoneNumber</b>.<br>
 * 
 */
public class RisksAndMitigantsServiceImpl extends GenericService<RisksAndMitigants>
		implements RisksAndMitigantsService {
	private static Logger logger = LogManager.getLogger(RisksAndMitigantsServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private RisksAndMitigantsDAO risksAndMitigantsDAO;

	public RisksAndMitigantsServiceImpl() {
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
		RisksAndMitigants risksAndMitigants = (RisksAndMitigants) auditHeader.getAuditDetail().getModelData();

		if (risksAndMitigants.isWorkflow()) {
			tableType = "_Temp";
		}

		if (risksAndMitigants.isNew()) {
			risksAndMitigants.setId(getRisksAndMitigantsDAO().save(risksAndMitigants, tableType));
			auditHeader.getAuditDetail().setModelData(risksAndMitigants);
		} else {
			getRisksAndMitigantsDAO().update(risksAndMitigants, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	@Override
	public void saveOrUpdate(FinanceDetail financeDetail, AuditHeader auditHeader, String tableType) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		List<RisksAndMitigants> risksAndMitigantsList = financeDetail.getRisksAndMitigantsList();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		for (RisksAndMitigants risksAndMitigants : risksAndMitigantsList) {
			risksAndMitigants.setTaskId(financeMain.getTaskId());
			risksAndMitigants.setNextTaskId(financeMain.getNextTaskId());
			risksAndMitigants.setRoleCode(financeMain.getRoleCode());
			risksAndMitigants.setNextRoleCode(financeMain.getNextRoleCode());
			risksAndMitigants.setRecordStatus(financeMain.getRecordStatus());
			risksAndMitigants.setWorkflowId(financeMain.getWorkflowId());
			risksAndMitigants.setFinReference(financeMain.getFinReference());
			if (risksAndMitigants.isNew()) {
				getRisksAndMitigantsDAO().save(risksAndMitigants, tableType);
				auditDetails.add(getAuditDetails(risksAndMitigants, 1, PennantConstants.TRAN_ADD));
			} else if (StringUtils.trimToEmpty(risksAndMitigants.getRecordType())
					.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				getRisksAndMitigantsDAO().update(risksAndMitigants, tableType);
				auditDetails.add(getAuditDetails(risksAndMitigants, 1, PennantConstants.TRAN_DEL));
			} else {
				getRisksAndMitigantsDAO().update(risksAndMitigants, tableType);
				auditDetails.add(getAuditDetails(risksAndMitigants, 1, PennantConstants.TRAN_UPD));
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

	public AuditDetail getAuditDetails(RisksAndMitigants risksAndMitigants, int auditSeq, String transType) {
		String[] fields = PennantJavaUtil.getFieldDetails(new RisksAndMitigants(),
				new RisksAndMitigants().getExcludeFields());
		return new AuditDetail(transType, auditSeq, fields[0], fields[1], risksAndMitigants.getBefImage(),
				risksAndMitigants);
	}

	public AuditHeader getAuditHeader(AuditHeader auditHeader) {
		AuditHeader newauditHeader = new AuditHeader();
		newauditHeader.setAuditModule(ModuleUtil.getTableName(RisksAndMitigants.class.getSimpleName()));
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
	public List<AuditDetail> delete(List<RisksAndMitigants> risksAndMitigantsList, TableType tableType,
			String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<>();
		String[] fields = null;

		if (risksAndMitigantsList != null && !risksAndMitigantsList.isEmpty()) {
			int auditSeq = 1;
			for (RisksAndMitigants finOption : risksAndMitigantsList) {
				getRisksAndMitigantsDAO().delete(finOption, tableType.getSuffix());
				fields = PennantJavaUtil.getFieldDetails(finOption, finOption.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditSeq, fields[0], fields[1], finOption.getBefImage(),
						finOption));
				auditSeq++;
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> doApprove(List<RisksAndMitigants> risksAndMitigants, TableType tableType,
			String auditTranType) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return doProcess(risksAndMitigants, tableType, auditTranType, true);
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		RisksAndMitigants risksAndMitigants = (RisksAndMitigants) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getRisksAndMitigantsDAO().delete(risksAndMitigants, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public int getVersion(long id, String typeCode) {
		return getRisksAndMitigantsDAO().getVersion(id, typeCode);

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
	public List<AuditDetail> doProcess(List<RisksAndMitigants> risksAndMitigants, TableType tableType,
			String auditTranType, boolean isApproveRcd) {
		List<AuditDetail> auditDetails = new ArrayList<>();
		auditDetails.addAll(processRisksAndMitigants(risksAndMitigants, tableType, auditTranType, isApproveRcd));
		return auditDetails;
	}

	@Override
	public List<AuditDetail> processRisksAndMitigants(List<RisksAndMitigants> risksAndMitigantsList,
			TableType tableType, String auditTranType, boolean isApproveRcd) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		if (CollectionUtils.isEmpty(risksAndMitigantsList)) {
			return auditDetails;
		}

		int i = 0;
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (RisksAndMitigants risksAndMitigants : risksAndMitigantsList) {
			if (StringUtils.isEmpty(StringUtils.trimToEmpty(risksAndMitigants.getRecordType()))) {
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
				risksAndMitigants.setRoleCode("");
				risksAndMitigants.setNextRoleCode("");
				risksAndMitigants.setTaskId("");
				risksAndMitigants.setNextTaskId("");
			}

			risksAndMitigants.setWorkflowId(0);
			if (StringUtils.equalsIgnoreCase(risksAndMitigants.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (risksAndMitigants.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(risksAndMitigants.getRecordType())) {
					risksAndMitigants.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(risksAndMitigants.getRecordType())) {
					risksAndMitigants.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(risksAndMitigants.getRecordType())) {
					risksAndMitigants.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (StringUtils.equalsIgnoreCase(risksAndMitigants.getRecordType(),
					(PennantConstants.RECORD_TYPE_NEW))) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (StringUtils.equalsIgnoreCase(risksAndMitigants.getRecordType(),
					(PennantConstants.RECORD_TYPE_UPD))) {
				updateRecord = true;
			} else if (StringUtils.equalsIgnoreCase(risksAndMitigants.getRecordType(),
					(PennantConstants.RECORD_TYPE_DEL))) {
				if (approveRec) {
					deleteRecord = true;
				} else if (risksAndMitigants.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = risksAndMitigants.getRecordType();
				recordStatus = risksAndMitigants.getRecordStatus();
				risksAndMitigants.setRecordType("");
				risksAndMitigants.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				getRisksAndMitigantsDAO().delete(risksAndMitigants, TableType.TEMP_TAB.getSuffix());
			}
			if (saveRecord) {
				getRisksAndMitigantsDAO().save(risksAndMitigants, tableType.getSuffix());
			}

			if (updateRecord) {
				getRisksAndMitigantsDAO().update(risksAndMitigants, tableType.getSuffix());
			}

			if (deleteRecord) {
				getRisksAndMitigantsDAO().delete(risksAndMitigants, tableType.getSuffix());
			}

			if (approveRec) {
				risksAndMitigants.setRecordType(rcdType);
				risksAndMitigants.setRecordStatus(recordStatus);
			}

			String[] fields = PennantJavaUtil.getFieldDetails(risksAndMitigants, risksAndMitigants.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					risksAndMitigants.getBefImage(), risksAndMitigants));
			i++;
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public RisksAndMitigantsDAO getRisksAndMitigantsDAO() {
		return risksAndMitigantsDAO;
	}

	public void setRisksAndMitigantsDAO(RisksAndMitigantsDAO risksAndMitigantsDAO) {
		this.risksAndMitigantsDAO = risksAndMitigantsDAO;
	}

}