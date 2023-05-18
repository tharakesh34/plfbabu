/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related ChequeHeaders. All
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
 * * FileName : ChequeHeaderServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-10-2017 * *
 * Modified Date : 18-10-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-10-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.pdc.ChequeHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinChequeHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.StageTabConstants;
import com.pennant.pff.mandate.ChequeSatus;
import com.pennant.pff.mandate.InstrumentType;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>ChequeHeader</b>.<br>
 */
public class FinChequeHeaderServiceImpl extends GenericService<ChequeHeader> implements FinChequeHeaderService {
	private static final Logger logger = LogManager.getLogger(FinChequeHeaderServiceImpl.class);

	private ChequeHeaderDAO chequeHeaderDAO;
	private ChequeDetailDAO chequeDetailDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader, TableType tableType) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, PennantConstants.method_saveOrUpdate);
		/**
		 * Commented because the business validation not required it is calling from Finance detail service impl save or
		 * update Need to change the code.
		 */
		/*
		 * if (!auditHeader.isNextProcess()) { logger.info(Literal.LEAVING); return auditHeader; }
		 */

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		ChequeHeader chequeHeader = (ChequeHeader) auditHeader.getAuditDetail().getModelData();
		if (chequeHeader.isNewRecord()) {
			processDocument(chequeHeader);
			chequeHeader.setId(Long.parseLong(chequeHeaderDAO.save(chequeHeader, tableType)));
			auditHeader.getAuditDetail().setModelData(chequeHeader);
			auditHeader.setAuditReference(String.valueOf(chequeHeader.getHeaderID()));
		} else {
			processDocument(chequeHeader);
			chequeHeaderDAO.update(chequeHeader, tableType);
		}

		// ChequeHeaderModule Details Processing
		if (chequeHeader.getChequeDetailList() != null && !chequeHeader.getChequeDetailList().isEmpty()) {
			List<AuditDetail> details = chequeHeader.getAuditDetailMap().get("ChequeDetail");
			details = processingChequeDetailList(details, tableType, chequeHeader.getHeaderID());
			auditDetails.addAll(details);
		}
		auditHeader.setAuditDetails(auditDetails);
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	public List<AuditDetail> processingChequeDetailList(List<AuditDetail> auditDetails, TableType type, long headerID) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		ChequeDetail chequeDetail = null;
		for (int i = 0; i < auditDetails.size(); i++) {
			chequeDetail = (ChequeDetail) auditDetails.get(i).getModelData();
			if (StringUtils.isEmpty(chequeDetail.getRecordType())) {
				continue;
			}
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type.getSuffix())) {
				approveRec = true;
				chequeDetail.setRoleCode("");
				chequeDetail.setNextRoleCode("");
				chequeDetail.setTaskId("");
				chequeDetail.setNextTaskId("");
				chequeDetail.setWorkflowId(0);
			}
			chequeDetail.setHeaderID(headerID);
			if (chequeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (chequeDetail.isNewRecord()) {
				saveRecord = true;
				if (chequeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					chequeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (chequeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					chequeDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (chequeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					chequeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (chequeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (chequeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (chequeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				deleteRecord = true;
			}
			if (approveRec) {
				rcdType = chequeDetail.getRecordType();
				recordStatus = chequeDetail.getRecordStatus();
				chequeDetail.setRecordType("");
				chequeDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				chequeDetailDAO.save(chequeDetail, type);
			}

			if (updateRecord) {
				chequeDetailDAO.update(chequeDetail, type);
			}

			if (deleteRecord) {
				chequeDetailDAO.delete(chequeDetail, type);
			}

			if (approveRec) {
				chequeDetail.setRecordType(rcdType);
				chequeDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(chequeDetail);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;

	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinanceDetail detail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		ChequeHeader chequeHeader = detail.getChequeHeader();

		String auditTranType = "";

		if (StringUtils.equals(PennantConstants.method_saveOrUpdate, method)
				|| StringUtils.equals(PennantConstants.method_doApprove, method)
				|| StringUtils.equals(PennantConstants.method_doReject, method)) {
			if (chequeHeader.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		// chequeHeader details
		if (chequeHeader.getChequeDetailList() != null && chequeHeader.getChequeDetailList().size() > 0) {
			auditDetailMap.put("ChequeDetail", setChequeDetailAuditData(chequeHeader, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("ChequeDetail"));
		}
		chequeHeader.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(chequeHeader);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public List<AuditDetail> setChequeDetailAuditData(ChequeHeader chequeHeader, String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		ChequeDetail chequeDetail = new ChequeDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(chequeDetail, chequeDetail.getExcludeFields());
		ChequeDetail detail = null;

		for (int i = 0; i < chequeHeader.getChequeDetailList().size(); i++) {

			detail = chequeHeader.getChequeDetailList().get(i);
			if (StringUtils.isEmpty(detail.getRecordType())) {
				continue;
			}

			detail.setWorkflowId(chequeHeader.getWorkflowId());
			detail.setHeaderID(chequeHeader.getHeaderID());

			boolean isRcdType = false;

			if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (chequeHeader.isWorkflow()) {
					isRcdType = true;
				}
			} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (StringUtils.equals(PennantConstants.method_saveOrUpdate, method) && (isRcdType)) {
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

			detail.setRecordStatus(chequeHeader.getRecordStatus());
			detail.setUserDetails(chequeHeader.getUserDetails());
			detail.setLastMntOn(chequeHeader.getLastMntOn());
			detail.setLastMntBy(chequeHeader.getLastMntBy());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], detail.getBefImage(), detail));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ChequeHeader chequeHeader = (ChequeHeader) auditHeader.getAuditDetail().getModelData();
		chequeHeaderDAO.delete(chequeHeader, TableType.MAIN_TAB);
		auditHeader.setAuditDetails(listDeletion(chequeHeader, TableType.MAIN_TAB, PennantConstants.TRAN_WF));

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public ChequeHeader getChequeHeader(long finID) {
		ChequeHeader chequeHeader = chequeHeaderDAO.getChequeHeader(finID, "_View");
		if (chequeHeader != null) {
			chequeHeader.setChequeDetailList(chequeDetailDAO.getChequeDetailList(chequeHeader.getHeaderID(), "_View"));
		}
		return chequeHeader;
	}

	@Override
	public ChequeHeader getChequeHeaderByRef(long finID) {
		ChequeHeader chequeHeader = chequeHeaderDAO.getChequeHeaderByRef(finID, "_View");
		if (chequeHeader != null) {
			chequeHeader.setChequeDetailList(chequeDetailDAO.getChequeDetailList(chequeHeader.getHeaderID(), "_View"));
		}
		return chequeHeader;
	}

	public ChequeHeader getApprovedChequeHeader(long finID) {
		ChequeHeader chequeHeader = chequeHeaderDAO.getChequeHeader(finID, "_AView");
		if (chequeHeader != null) {
			chequeHeader.setChequeDetailList(chequeDetailDAO.getChequeDetailList(chequeHeader.getHeaderID(), "_AView"));
		}
		return chequeHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, PennantConstants.method_doApprove);
		/**
		 * Commented because the business validation not required it is calling from Finance detail service impl save or
		 * update Need to change the code.
		 */
		/*
		 * if (!auditHeader.isNextProcess()) { return auditHeader; }
		 */

		ChequeHeader chequeHeader = new ChequeHeader();
		BeanUtils.copyProperties((ChequeHeader) auditHeader.getAuditDetail().getModelData(), chequeHeader);

		if (chequeHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			chequeHeaderDAO.delete(chequeHeader, TableType.MAIN_TAB);
			auditDetails.addAll(listDeletion(chequeHeader, TableType.MAIN_TAB, auditHeader.getAuditTranType()));

		} else {
			chequeHeader.setRoleCode("");
			chequeHeader.setNextRoleCode("");
			chequeHeader.setTaskId("");
			chequeHeader.setNextTaskId("");
			chequeHeader.setWorkflowId(0);

			processDocument(chequeHeader);

			if (chequeHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				chequeHeader.setRecordType("");
				chequeHeaderDAO.save(chequeHeader, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				chequeHeader.setRecordType("");
				chequeHeaderDAO.update(chequeHeader, TableType.TEMP_TAB);
			}
		}
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		// Cheque Details
		if (chequeHeader.getChequeDetailList() != null && chequeHeader.getChequeDetailList().size() > 0) {
			List<AuditDetail> details = chequeHeader.getAuditDetailMap().get("ChequeDetail");
			details = processingChequeDetailList(details, TableType.MAIN_TAB, chequeHeader.getHeaderID());
			auditDetails.addAll(details);
		}
		auditHeader.setAuditDetails(
				getListAuditDetails(listDeletion(chequeHeader, TableType.TEMP_TAB, auditHeader.getAuditTranType())));// FIXME
		if (auditHeader.getApiHeader() == null) {
			chequeHeaderDAO.delete(chequeHeader, TableType.TEMP_TAB);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(chequeHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * 
	 * @param chequeDetails
	 */
	private void processDocument(ChequeHeader chequeHeader) {
		List<ChequeDetail> chequeDetails = chequeHeader.getChequeDetailList();

		if (CollectionUtils.isEmpty(chequeDetails)) {
			return;
		}

		for (ChequeDetail detail : chequeDetails) {
			DocumentDetails dd = new DocumentDetails();
			dd.setFinReference(chequeHeader.getFinReference());

			if (!detail.isNewRecord()) {
				if (detail.getDocImage() != null) {
					dd.setUserDetails(detail.getUserDetails());
					byte[] arr1 = null;
					if (detail.getDocumentRef() != null && detail.getDocumentRef() > 0) {
						arr1 = getDocumentImage(detail.getDocumentRef());
					}
					byte[] arr2 = detail.getDocImage();

					if (!Arrays.equals(arr1, arr2)) {
						dd.setDocImage(arr2);
						saveDocument(DMSModule.FINANCE, DMSModule.CHEQUE, dd);
						detail.setDocumentRef(dd.getDocRefId());
					}
				}
			} else {
				if (detail.getDocImage() != null) {
					dd.setDocImage(detail.getDocImage());
					saveDocument(DMSModule.FINANCE, DMSModule.CHEQUE, dd);
					detail.setDocumentRef(dd.getDocRefId());

				}
			}
		}
	}

	// Method for Deleting all records related to ChequeHeaderModule and ChequeHeaderLOB in _Temp/Main tables depend on
	// method type
	private List<AuditDetail> listDeletion(ChequeHeader chequeHeader, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		if (chequeHeader.getChequeDetailList() != null && chequeHeader.getChequeDetailList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new ChequeDetail());
			for (int i = 0; i < chequeHeader.getChequeDetailList().size(); i++) {
				ChequeDetail chequeDetail = chequeHeader.getChequeDetailList().get(i);
				if (!StringUtils.isEmpty(chequeDetail.getRecordType()) || StringUtils.isEmpty(tableType.getSuffix())) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							chequeDetail.getBefImage(), chequeDetail));
				}
				ChequeDetail detail = chequeHeader.getChequeDetailList().get(i);
				chequeDetailDAO.delete(detail, tableType);
			}
		}

		logger.debug(Literal.LEAVING);
		return auditList;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, PennantConstants.method_doReject);
		/**
		 * Commented because the business validation not required it is calling from Finance detail service impl save or
		 * update Need to change the code.
		 */
		/*
		 * if (!auditHeader.isNextProcess()) { logger.info(Literal.LEAVING); return auditHeader; }
		 */

		ChequeHeader chequeHeader = (ChequeHeader) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(
				getListAuditDetails(listDeletion(chequeHeader, TableType.TEMP_TAB, PennantConstants.TRAN_WF)));
		chequeHeaderDAO.delete(chequeHeader, TableType.TEMP_TAB);
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		if (auditDetail.getErrorDetails() != null) {
			auditHeader.setErrorList(auditDetail.getErrorDetails());
		}
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				Object object = ((AuditDetail) list.get(i)).getModelData();
				try {
					rcdType = object.getClass().getMethod("getRecordType").invoke(object).toString();
					if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
						transType = PennantConstants.TRAN_ADD;
					} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType)
							|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
						transType = PennantConstants.TRAN_DEL;
					} else {
						transType = PennantConstants.TRAN_UPD;
					}

					if (StringUtils.isNotEmpty(transType)) {
						// check and change below line for Complete code
						//
						Object befImg = object.getClass().getMethod("getBefImage", object.getClass().getClasses())
								.invoke(object, object.getClass().getClasses());
						auditDetailsList.add(
								new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(), befImg, object));
					}
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return auditDetailsList;
	}

	@Override
	public AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		FinanceDetail fd = (FinanceDetail) auditDetail.getModelData();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		ChequeHeader chequeHeader = fd.getChequeHeader();
		chequeHeader.setRecordStatus(fm.getRecordStatus());

		// PSD#163298 Issue addressed for mandatory validations While Resubmitting.And Without tab validations are
		// coming issue fixed.
		String strTabId = StringUtils.leftPad(String.valueOf(StageTabConstants.Cheque), 3, "0");
		boolean isTabVisible = true;
		String roles = "";

		if (fd.getShowTabDetailMap().containsKey(strTabId)) {
			roles = fd.getShowTabDetailMap().get(strTabId);
			if (!StringUtils.contains(roles, fm.getRoleCode() + ",")) {
				isTabVisible = false;
			}
		}

		if (isTabVisible) {
			// Check the unique keys.
			if (chequeHeader.isNewRecord() && chequeHeaderDAO.isDuplicateKey(chequeHeader.getHeaderID(), fm.getFinID(),
					chequeHeader.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
				String[] parameters = new String[2];

				parameters[0] = PennantJavaUtil.getLabel("label_FinReference") + ": " + chequeHeader.getFinReference();
				parameters[1] = PennantJavaUtil.getLabel("label_HeaderID") + ": " + chequeHeader.getHeaderID();

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}
			List<ChequeDetail> chequeDetailList = chequeHeader.getChequeDetailList();
			boolean isListContainsPDC = false;
			if (chequeDetailList != null && !chequeDetailList.isEmpty()) {
				for (ChequeDetail chequeDetail : chequeDetailList) {
					if (InstrumentType.isPDC(chequeDetail.getChequeType())
							&& !ChequeSatus.CANCELLED.equals(chequeDetail.getChequeStatus())) {
						isListContainsPDC = true;
					}
					if (chequeDetail.isNewRecord() && chequeDetailDAO.isDuplicateKey(chequeDetail.getChequeDetailsID(),
							chequeDetail.getBankBranchID(), chequeDetail.getAccountNo(),
							chequeDetail.getChequeSerialNumber(), TableType.BOTH_TAB)) {

						String[] parameters = new String[3];

						parameters[0] = PennantJavaUtil.getLabel("label_ChequeDetailDialog_BankBranchID.value") + ": "
								+ chequeDetail.getBankBranchID();
						parameters[1] = PennantJavaUtil.getLabel("label_ChequeDetailDialog_AccNumber.value") + ": "
								+ chequeDetail.getAccountNo();
						parameters[2] = PennantJavaUtil.getLabel("label_ChequeDetailDialog_ChequeSerialNo.value") + ": "
								+ chequeDetail.getChequeSerialNumber();

						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41008", parameters, null));
					}
				}
			}
			// if finance Payment Method is PDC and there is no PDC cheques.
			String finRepayMethod = fd.getFinScheduleData().getFinanceMain().getFinRepayMethod();
			if (InstrumentType.isPDC(finRepayMethod) && !PennantConstants.FINSOURCE_ID_API
					.equals(fd.getFinScheduleData().getFinanceMain().getFinSourceID())) {
				// PSD#163298 Issue addressed for validation raised While Resubmitting.
				if (!isListContainsPDC && !StringUtils.contains(chequeHeader.getRecordStatus(), "Resubmit")) {
					String[] parameters = new String[2];
					parameters[0] = PennantJavaUtil.getLabel("label_FinReference") + ": "
							+ chequeHeader.getFinReference();
					parameters[1] = PennantJavaUtil.getLabel("label_PDC_Cheque");
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", parameters, null));
				}
			}
			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		}
		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public void setChequeHeaderDAO(ChequeHeaderDAO chequeHeaderDAO) {
		this.chequeHeaderDAO = chequeHeaderDAO;
	}

	public void setChequeDetailDAO(ChequeDetailDAO chequeDetailDAO) {
		this.chequeDetailDAO = chequeDetailDAO;
	}
}