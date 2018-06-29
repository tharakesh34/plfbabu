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
 * FileName    		:  LegalApplicantDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-06-2018    														*
 *                                                                  						*
 * Modified Date    :  16-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-06-2018       PENNANT	                 0.1                                            * 
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
import com.pennant.backend.dao.legal.LegalApplicantDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.legal.LegalApplicantDetail;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on
 * <b>LegalApplicantDetail</b>.<br>
 */
public class LegalApplicantDetailService extends GenericService<LegalApplicantDetail> {
	private static final Logger logger = Logger.getLogger(LegalApplicantDetailService.class);

	private LegalApplicantDetailDAO legalApplicantDetailDAO;

	public LegalApplicantDetailDAO getLegalApplicantDetailDAO() {
		return legalApplicantDetailDAO;
	}

	public void setLegalApplicantDetailDAO(LegalApplicantDetailDAO legalApplicantDetailDAO) {
		this.legalApplicantDetailDAO = legalApplicantDetailDAO;
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

		LegalApplicantDetail applicantDetail = (LegalApplicantDetail) auditDetail.getModelData();
		LegalApplicantDetail tempApplicantDetail = null;

		if (applicantDetail.isWorkflow()) {
			tempApplicantDetail = getLegalApplicantDetailDAO().getLegalApplicantDetail(applicantDetail.getLegalId(),
					applicantDetail.getLegalApplicantId(), TableType.TEMP_TAB.getSuffix());
		}
		LegalApplicantDetail befApplicantDetail = getLegalApplicantDetailDAO().getLegalApplicantDetail(
				applicantDetail.getLegalId(), applicantDetail.getLegalApplicantId(), TableType.MAIN_TAB.getSuffix());
		LegalApplicantDetail oldApplicantDetail = applicantDetail.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(applicantDetail.getLegalReference());
		valueParm[1] = applicantDetail.getTitleName();

		errParm[0] = PennantJavaUtil.getLabel("label_LegalReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_Title") + ":" + valueParm[1];

		if (applicantDetail.isNew()) {
			if (!applicantDetail.isWorkflow()) {
				if (befApplicantDetail != null) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else {
				if (applicantDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (befApplicantDetail != null || tempApplicantDetail != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befApplicantDetail == null || tempApplicantDetail != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			if (!applicantDetail.isWorkflow()) {
				if (befApplicantDetail == null) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldApplicantDetail != null
							&& !oldApplicantDetail.getLastMntOn().equals(befApplicantDetail.getLastMntOn())) {
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

				if (tempApplicantDetail == null) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempApplicantDetail != null && oldApplicantDetail != null
						&& !oldApplicantDetail.getLastMntOn().equals(tempApplicantDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !applicantDetail.isWorkflow()) {
			applicantDetail.setBefImage(befApplicantDetail);
		}
		return auditDetail;
	}

	public List<AuditDetail> getApplicantDetailsAuditData(LegalDetail legalDetail, String auditTranType,
			String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		String[] fields = PennantJavaUtil.getFieldDetails(new LegalApplicantDetail(),
				new LegalApplicantDetail().getExcludeFields());
		List<LegalApplicantDetail> detailList = legalDetail.getApplicantDetailList();

		for (int i = 0; i < detailList.size(); i++) {

			LegalApplicantDetail detail = detailList.get(i);
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

	public List<AuditDetail> processingApplicantDetail(LegalDetail legalDetail, List<AuditDetail> auditDetails, TableType tableType) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			LegalApplicantDetail legalApplicantDetail = (LegalApplicantDetail) auditDetails.get(i).getModelData();
			legalApplicantDetail.setLegalId(legalDetail.getLegalId());
			
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(tableType.getSuffix())) {
				approveRec = true;
				legalApplicantDetail.setRoleCode("");
				legalApplicantDetail.setNextRoleCode("");
				legalApplicantDetail.setTaskId("");
				legalApplicantDetail.setNextTaskId("");
			}

			legalApplicantDetail.setWorkflowId(0);
			if (legalApplicantDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (legalApplicantDetail.isNewRecord()) {
				saveRecord = true;
				if (legalApplicantDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					legalApplicantDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (legalApplicantDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					legalApplicantDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (legalApplicantDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					legalApplicantDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (legalApplicantDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (legalApplicantDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (legalApplicantDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (legalApplicantDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = legalApplicantDetail.getRecordType();
				recordStatus = legalApplicantDetail.getRecordStatus();
				legalApplicantDetail.setRecordType("");
				legalApplicantDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getLegalApplicantDetailDAO().save(legalApplicantDetail, tableType);
			}

			if (updateRecord) {
				getLegalApplicantDetailDAO().update(legalApplicantDetail, tableType);
			}

			if (deleteRecord) {
				getLegalApplicantDetailDAO().delete(legalApplicantDetail, tableType);
			}

			if (approveRec) {
				legalApplicantDetail.setRecordType(rcdType);
				legalApplicantDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(legalApplicantDetail);
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public List<AuditDetail> deleteApplicantDetails(List<AuditDetail> applicantDetails, String tableType,
			String auditTranType) {
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		LegalApplicantDetail applicantDetail = null;
		String[] fields = PennantJavaUtil.getFieldDetails(new LegalApplicantDetail(),
				new LegalApplicantDetail().getExcludeFields());
		for (int i = 0; i < applicantDetails.size(); i++) {
			applicantDetail = (LegalApplicantDetail) applicantDetails.get(i).getModelData();
			applicantDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], applicantDetail.getBefImage(),
					applicantDetail));
		}
		getLegalApplicantDetailDAO().deleteList(applicantDetail, tableType);
		return auditList;
	}

	public List<LegalApplicantDetail> getApplicantDetailsList(long legalId, String type) {
		return getLegalApplicantDetailDAO().getApplicantDetailsList(legalId, type);
	}

}