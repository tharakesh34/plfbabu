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
 * FileName    		:  LegalPropertyTitleServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-06-2018    														*
 *                                                                  						*
 * Modified Date    :  18-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-06-2018       PENNANT	                 0.1                                            * 
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
import com.pennant.backend.dao.legal.LegalPropertyTitleDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.model.legal.LegalPropertyTitle;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on
 * <b>LegalPropertyTitle</b>.<br>
 */
public class LegalPropertyTitleService extends GenericService<LegalPropertyTitle> {
	private static final Logger logger = Logger.getLogger(LegalPropertyTitleService.class);

	private LegalPropertyTitleDAO legalPropertyTitleDAO;

	public LegalPropertyTitleDAO getLegalPropertyTitleDAO() {
		return legalPropertyTitleDAO;
	}

	public void setLegalPropertyTitleDAO(LegalPropertyTitleDAO legalPropertyTitleDAO) {
		this.legalPropertyTitleDAO = legalPropertyTitleDAO;
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

		LegalPropertyTitle propertyTitle = (LegalPropertyTitle) auditDetail.getModelData();
		LegalPropertyTitle tempPropertyTitle = null;

		if (propertyTitle.isWorkflow()) {
			tempPropertyTitle = getLegalPropertyTitleDAO().getLegalPropertyTitle(propertyTitle.getLegalPropertyTitleId(), TableType.TEMP_TAB.getSuffix());
		}
		LegalPropertyTitle befPropertyTitle = getLegalPropertyTitleDAO().getLegalPropertyTitle(propertyTitle.getLegalPropertyTitleId(), TableType.MAIN_TAB.getSuffix());
		LegalPropertyTitle oldPropertyTitleDetail = propertyTitle.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(propertyTitle.getLegalPropertyTitleId());
		valueParm[1] = propertyTitle.getTitle();

		errParm[0] = PennantJavaUtil.getLabel("label_LegalReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_Title") + ":" + valueParm[1];

		if (propertyTitle.isNew()) {
			if (!propertyTitle.isWorkflow()) {
				if (befPropertyTitle != null) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else {
				if (propertyTitle.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (befPropertyTitle != null || tempPropertyTitle != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befPropertyTitle == null || tempPropertyTitle != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			if (!propertyTitle.isWorkflow()) {
				if (befPropertyTitle == null) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldPropertyTitleDetail != null
							&& !oldPropertyTitleDetail.getLastMntOn().equals(befPropertyTitle.getLastMntOn())) {
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

				if (tempPropertyTitle == null) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempPropertyTitle != null && oldPropertyTitleDetail != null
						&& !oldPropertyTitleDetail.getLastMntOn().equals(tempPropertyTitle.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !propertyTitle.isWorkflow()) {
			propertyTitle.setBefImage(befPropertyTitle);
		}
		return auditDetail;
	}

	public List<AuditDetail> getDetailsAuditData(LegalDetail legalDetail, String auditTranType,
			String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		String[] fields = PennantJavaUtil.getFieldDetails(new LegalPropertyTitle(),
				new LegalPropertyTitle().getExcludeFields());
		List<LegalPropertyTitle> detailList = legalDetail.getPropertyTitleList();

		for (int i = 0; i < detailList.size(); i++) {

			LegalPropertyTitle detail = detailList.get(i);
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
			LegalPropertyTitle legalPropertyTitle = (LegalPropertyTitle) auditDetails.get(i).getModelData();
			legalPropertyTitle.setLegalId(legalDetail.getLegalId());

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(tableType.getSuffix())) {
				approveRec = true;
				legalPropertyTitle.setRoleCode("");
				legalPropertyTitle.setNextRoleCode("");
				legalPropertyTitle.setTaskId("");
				legalPropertyTitle.setNextTaskId("");
			}

			legalPropertyTitle.setWorkflowId(0);
			if (legalPropertyTitle.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (legalPropertyTitle.isNewRecord()) {
				saveRecord = true;
				if (legalPropertyTitle.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					legalPropertyTitle.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (legalPropertyTitle.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					legalPropertyTitle.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (legalPropertyTitle.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					legalPropertyTitle.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (legalPropertyTitle.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (legalPropertyTitle.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (legalPropertyTitle.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (legalPropertyTitle.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = legalPropertyTitle.getRecordType();
				recordStatus = legalPropertyTitle.getRecordStatus();
				legalPropertyTitle.setRecordType("");
				legalPropertyTitle.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getLegalPropertyTitleDAO().save(legalPropertyTitle, tableType);
			}

			if (updateRecord) {
				getLegalPropertyTitleDAO().update(legalPropertyTitle, tableType);
			}

			if (deleteRecord) {
				getLegalPropertyTitleDAO().delete(legalPropertyTitle, tableType);
			}

			if (approveRec) {
				legalPropertyTitle.setRecordType(rcdType);
				legalPropertyTitle.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(legalPropertyTitle);
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public List<AuditDetail> deleteDetails(List<AuditDetail> propertyTitles, String tableType,
			String auditTranType) {
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		LegalPropertyTitle propertyTitle = null;
		String[] fields = PennantJavaUtil.getFieldDetails(new LegalPropertyTitle(),
				new LegalPropertyTitle().getExcludeFields());
		for (int i = 0; i < propertyTitles.size(); i++) {
			propertyTitle = (LegalPropertyTitle) propertyTitles.get(i).getModelData();
			propertyTitle.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], propertyTitle.getBefImage(),
					propertyTitle));
		}
		getLegalPropertyTitleDAO().deleteList(propertyTitle, tableType);
		return auditList;
	}

	public List<LegalPropertyTitle> getDetailsList(long legalId, String type) {
		return getLegalPropertyTitleDAO().getLegalPropertyTitleList(legalId, type);
	}

}