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
 * FileName    		:  LegalECDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-06-2018    														*
 *                                                                  						*
 * Modified Date    :  19-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-06-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.legal.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.legal.LegalECDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.model.legal.LegalECDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>LegalECDetail</b>.<br>
 */
public class LegalECDetailService extends GenericService<LegalECDetail> {
	private static final Logger logger = Logger.getLogger(LegalECDetailService.class);

	private LegalECDetailDAO legalECDetailDAO;

	public LegalECDetailDAO getLegalECDetailDAO() {
		return legalECDetailDAO;
	}

	public void setLegalECDetailDAO(LegalECDetailDAO legalECDetailDAO) {
		this.legalECDetailDAO = legalECDetailDAO;
	}
	
	public List<AuditDetail> vaildateDetails(List<AuditDetail> auditDetails, String method, String usrLanguage) {

		if (auditDetails != null && auditDetails.size() > 0) {
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail = validate(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail);
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
	}

	private AuditDetail validate(AuditDetail auditDetail, String method, String usrLanguage) {
		LegalECDetail legalECDetail = (LegalECDetail) auditDetail.getModelData();
		LegalECDetail templegalECDetail = null;

		if (legalECDetail.isWorkflow()) {
			templegalECDetail = getLegalECDetailDAO()
					.getLegalECDetail(legalECDetail.getLegalECId(), TableType.TEMP_TAB.getSuffix());
		}
		LegalECDetail befLegalECDetail = getLegalECDetailDAO()
				.getLegalECDetail(legalECDetail.getLegalECId(), TableType.MAIN_TAB.getSuffix());
		LegalECDetail oldLegalECDetail = legalECDetail.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(legalECDetail.getLegalECId());
		valueParm[1] = String.valueOf(legalECDetail.getEcDate());

		errParm[0] = PennantJavaUtil.getLabel("label_LegalReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_Title") + ":" + valueParm[1];

		if (legalECDetail.isNew()) {
			if (!legalECDetail.isWorkflow()) {
				if (befLegalECDetail != null) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else {
				if (legalECDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (befLegalECDetail != null || templegalECDetail != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befLegalECDetail == null || templegalECDetail != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			if (!legalECDetail.isWorkflow()) {
				if (befLegalECDetail == null) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldLegalECDetail != null
							&& !oldLegalECDetail.getLastMntOn().equals(befLegalECDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, null));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, null));
						}
					}
				}
			} else {

				if (templegalECDetail == null) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (templegalECDetail != null && oldLegalECDetail != null
						&& !oldLegalECDetail.getLastMntOn().equals(templegalECDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !legalECDetail.isWorkflow()) {
			legalECDetail.setBefImage(befLegalECDetail);
		}
		return auditDetail;
	}

	public List<AuditDetail> getDetailsAuditData(LegalDetail legalDetail, String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		String[] fields = PennantJavaUtil.getFieldDetails(new LegalECDetail(),
				new LegalECDetail().getExcludeFields());
		List<LegalECDetail> detailList = legalDetail.getEcdDetailsList();

		for (int i = 0; i < detailList.size(); i++) {

			LegalECDetail detail = detailList.get(i);
			if (StringUtils.isEmpty(StringUtils.trimToEmpty(detail.getRecordType()))) {
				continue;
			}

			boolean isRcdType = false;
			if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (legalDetail.isWorkflow()) {
					isRcdType = true;
				}
			} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				detail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], detail.getBefImage(), detail));
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public List<AuditDetail> processingDetails(LegalDetail legalDetail, List<AuditDetail> auditDetails,
			TableType tableType) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			LegalECDetail legalECDetail = (LegalECDetail) auditDetails.get(i).getModelData();
			legalECDetail.setLegalId(legalDetail.getLegalId());

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(tableType.getSuffix())) {
				approveRec = true;
				legalECDetail.setRoleCode("");
				legalECDetail.setNextRoleCode("");
				legalECDetail.setTaskId("");
				legalECDetail.setNextTaskId("");
			}

			legalECDetail.setWorkflowId(0);
			if (legalECDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (legalECDetail.isNewRecord()) {
				saveRecord = true;
				if (legalECDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					legalECDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (legalECDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					legalECDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (legalECDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					legalECDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (legalECDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (legalECDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (legalECDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (legalECDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = legalECDetail.getRecordType();
				recordStatus = legalECDetail.getRecordStatus();
				legalECDetail.setRecordType("");
				legalECDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getLegalECDetailDAO().save(legalECDetail, tableType);
			}

			if (updateRecord) {
				getLegalECDetailDAO().update(legalECDetail, tableType);
			}

			if (deleteRecord) {
				getLegalECDetailDAO().delete(legalECDetail, tableType);
			}

			if (approveRec) {
				legalECDetail.setRecordType(rcdType);
				legalECDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(legalECDetail);
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public List<AuditDetail> deleteDetails(List<AuditDetail> legalECDetails, String tableType, String auditTranType) {
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		LegalECDetail legalECDetail = null;
		String[] fields = PennantJavaUtil.getFieldDetails(new LegalECDetail(),
				new LegalECDetail().getExcludeFields());
		for (int i = 0; i < legalECDetails.size(); i++) {
			legalECDetail = (LegalECDetail) legalECDetails.get(i).getModelData();
			legalECDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], legalECDetail.getBefImage(),
					legalECDetail));
		}
		getLegalECDetailDAO().deleteList(legalECDetail, tableType);
		return auditList;
	}

	public List<LegalECDetail> getDetailsList(long legalId, String type) {
		return getLegalECDetailDAO().getLegalECDetailList(legalId, type);
	}

}