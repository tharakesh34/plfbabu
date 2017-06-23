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
 * FileName    		:  ManualAdviseServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-04-2017    														*
 *                                                                  						*
 * Modified Date    :  21-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinTaxUploadDetailDAO;
import com.pennant.backend.model.FinTaxUploadDetail;
import com.pennant.backend.model.FinTaxUploadHeader;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinTaxUploadDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.InterfaceException;
import com.pennanttech.pff.core.Literal;

/**
 * Service implementation for methods that depends on <b>ManualAdvise</b>.<br>
 */
public class FinTaxUploadDetailServiceImpl extends GenericService<FinTaxUploadHeader>
		implements FinTaxUploadDetailService {
	private static final Logger		logger	= Logger.getLogger(FinTaxUploadDetailServiceImpl.class);

	private AuditHeaderDAO			auditHeaderDAO;
	private FinTaxUploadDetailDAO	finTaxUploadDetailDAO;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public FinTaxUploadDetailDAO getFinTaxUploadDetailDAO() {
		return finTaxUploadDetailDAO;
	}

	public void setFinTaxUploadDetailDAO(FinTaxUploadDetailDAO finTaxUploadDetailDAO) {
		this.finTaxUploadDetailDAO = finTaxUploadDetailDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Override
	public List<FinTaxUploadDetail> getFinTaxDetailUploadById(long reference) {
		return null;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {

		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String tableType = "";
		FinTaxUploadHeader finTaxUploadHeader = (FinTaxUploadHeader) auditHeader.getAuditDetail().getModelData();

		if (finTaxUploadHeader.isWorkflow()) {
			tableType = "_Temp";
		}

		if (finTaxUploadHeader.isNew()) {
			getFinTaxUploadDetailDAO().save(finTaxUploadHeader, tableType);
		} else {
			getFinTaxUploadDetailDAO().update(finTaxUploadHeader, tableType);
		}

		
		
		if (finTaxUploadHeader.getFinTaxUploadDetailList() != null
				&& finTaxUploadHeader.getFinTaxUploadDetailList().size() > 0) {
			
		for (FinTaxUploadDetail finTaxDetail : finTaxUploadHeader.getFinTaxUploadDetailList()) {
			finTaxDetail.setBatchReference(String.valueOf(finTaxUploadHeader.getBatchReference()));
		}
			List<AuditDetail> details = finTaxUploadHeader.getAuditDetailMap().get("TaxDetail");
			details = processTaxDetails(details, tableType);
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * Method For Preparing List of AuditDetails for Customer Ratings
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processTaxDetails(List<AuditDetail> auditDetails, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			FinTaxUploadDetail finTaxUploadDetail = (FinTaxUploadDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finTaxUploadDetail.setRoleCode("");
				finTaxUploadDetail.setNextRoleCode("");
				finTaxUploadDetail.setTaskId("");
				finTaxUploadDetail.setNextTaskId("");
			}

			if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finTaxUploadDetail.isNewRecord()) {
				saveRecord = true;
				if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finTaxUploadDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finTaxUploadDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finTaxUploadDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finTaxUploadDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = finTaxUploadDetail.getRecordType();
				recordStatus = finTaxUploadDetail.getRecordStatus();
				finTaxUploadDetail.setRecordType("");
				finTaxUploadDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				getFinTaxUploadDetailDAO().saveFintaxDetail(finTaxUploadDetail, type);
			}

			if (updateRecord) {
				getFinTaxUploadDetailDAO().updateFintaxDetail(finTaxUploadDetail, type);
			}

			if (deleteRecord) {
				getFinTaxUploadDetailDAO().deleteFintaxDetail(finTaxUploadDetail, type);
			}

			if (approveRec) {
				finTaxUploadDetail.setRecordType(rcdType);
				finTaxUploadDetail.setRecordStatus(recordStatus);
			}

			auditDetails.get(i).setModelData(finTaxUploadDetail);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinTaxUploadHeader finTaxUploadHeader = (FinTaxUploadHeader) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (finTaxUploadHeader.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (finTaxUploadHeader.getFinTaxUploadDetailList() != null
				&& finTaxUploadHeader.getFinTaxUploadDetailList().size() > 0) {
			auditDetailMap.put("TaxDetail", setTaxDetailsData(finTaxUploadHeader, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("TaxDetail"));
		}

		finTaxUploadHeader.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(finTaxUploadHeader);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");
		return auditHeader;
	}

	private List<AuditDetail> setTaxDetailsData(FinTaxUploadHeader finTaxUploadHeader, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinTaxUploadDetail detail = new FinTaxUploadDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(detail, detail.getExcludeFields());

		for (int i = 0; i < finTaxUploadHeader.getFinTaxUploadDetailList().size(); i++) {
			FinTaxUploadDetail finTaxUploadDetail = finTaxUploadHeader.getFinTaxUploadDetailList().get(i);

			if (StringUtils.isEmpty(finTaxUploadDetail.getRecordType())) {
				continue;
			}

			finTaxUploadDetail.setWorkflowId(finTaxUploadDetail.getWorkflowId());

			boolean isRcdType = false;

			if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finTaxUploadDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finTaxUploadDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finTaxUploadDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				finTaxUploadDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			finTaxUploadDetail.setRecordStatus(finTaxUploadHeader.getRecordStatus());
			finTaxUploadDetail.setUserDetails(finTaxUploadHeader.getUserDetails());
			finTaxUploadDetail.setLastMntOn(finTaxUploadHeader.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					finTaxUploadDetail.getBefImage(), finTaxUploadDetail));
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	public AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		return auditDetail;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) throws InterfaceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		// TODO Auto-generated method stub
		return null;
	}

}