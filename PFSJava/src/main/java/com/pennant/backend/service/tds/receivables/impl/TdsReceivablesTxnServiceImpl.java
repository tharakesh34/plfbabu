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
 * * FileName : TdsReceivablesTxnServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-09-2020 * *
 * Modified Date : 03-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.tds.receivables.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;

import com.google.common.collect.ComparisonChain;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.tds.receivables.TdsReceivableDAO;
import com.pennant.backend.dao.tds.receivables.TdsReceivablesTxnDAO;
import com.pennant.backend.dao.tds.receivables.TdsReceivablesTxnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.tds.receivables.TdsReceivable;
import com.pennant.backend.model.tds.receivables.TdsReceivablesTxn;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.tds.receivables.TdsReceivablesTxnService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>TdsReceivablesTxn</b>.<br>
 */
public class TdsReceivablesTxnServiceImpl extends GenericService<TdsReceivablesTxn>
		implements TdsReceivablesTxnService {
	private static final Logger logger = LogManager.getLogger(TdsReceivablesTxnServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private TdsReceivablesTxnDAO tdsReceivablesTxnDAO;
	private TdsReceivableDAO tdsReceivableDAO;

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @param tdsReceivablesTxnDAO the tdsReceivablesTxnDAO to set
	 */

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * TDS_RECEIVABLE_DETAILS/TDS_RECEIVABLE_DETAILS_Temp by using TDS_RECEIVABLE_DETAILSDAO's save method b) Update the
	 * Record in the table. based on the module workFlow Configuration. by using TDS_RECEIVABLE_DETAILSDAO's update
	 * method 3) Audit the record in to AuditHeader and AdtTDS_RECEIVABLE_DETAILS by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		TdsReceivable tdsReceivable = (TdsReceivable) auditHeader.getAuditDetail().getModelData();
		TdsReceivablesTxn tdsReceivablesTxn = tdsReceivable.getTdsReceivablesTxn();

		TableType tableType = TableType.MAIN_TAB;
		if (tdsReceivablesTxn.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (tdsReceivablesTxn.isNewRecord()
				&& tdsReceivablesTxn.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			tdsReceivablesTxn.setTxnID(tdsReceivablesTxnDAO.getAdjustmentTxnSeq());
		}

		if (tdsReceivable.getTdsReceivablesTxnList() != null && !tdsReceivable.getTdsReceivablesTxnList().isEmpty()) {
			List<AuditDetail> details = tdsReceivable.getAuditDetailMap().get("TdsReceivablesTxns");
			details = processingTdsReceivablesTxn(details, tableType, tdsReceivablesTxn);
			auditDetails.addAll(details);
		}

		// Add Audit
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	private List<AuditDetail> processingTdsReceivablesTxn(List<AuditDetail> auditDetails, TableType type,
			TdsReceivablesTxn atdsReceivablesTxn) {
		logger.debug(Literal.ENTERING);

		for (int i = 0; i < auditDetails.size(); i++) {
			TdsReceivablesTxn tdsReceivablesTxn = (TdsReceivablesTxn) auditDetails.get(i).getModelData();
			TableType tableType = TableType.MAIN_TAB;
			if (atdsReceivablesTxn.isWorkflow()) {
				tableType = TableType.TEMP_TAB;
			}

			tdsReceivablesTxn.setNewRecord(atdsReceivablesTxn.isNewRecord());
			tdsReceivablesTxn.setRecordType(atdsReceivablesTxn.getRecordType());
			tdsReceivablesTxn.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			tdsReceivablesTxn.setUserDetails(atdsReceivablesTxn.getUserDetails());
			tdsReceivablesTxn.setRecordStatus(atdsReceivablesTxn.getRecordStatus());
			tdsReceivablesTxn.setTaskId(atdsReceivablesTxn.getTaskId());
			tdsReceivablesTxn.setNextTaskId(atdsReceivablesTxn.getNextTaskId());
			tdsReceivablesTxn.setRoleCode(atdsReceivablesTxn.getRoleCode());
			tdsReceivablesTxn.setNextRoleCode(atdsReceivablesTxn.getNextRoleCode());
			tdsReceivablesTxn.setWorkflowId(atdsReceivablesTxn.getWorkflowId());
			tdsReceivablesTxn.setLastMntBy(atdsReceivablesTxn.getUserDetails().getUserId());
			if (atdsReceivablesTxn.isNew()) {
				tdsReceivablesTxn.setTxnID(atdsReceivablesTxn.getTxnID());
				tdsReceivablesTxnDAO.save(tdsReceivablesTxn, type);

			} else {
				tdsReceivablesTxnDAO.update(tdsReceivablesTxn, tableType);
			}

			auditDetails.get(i).setModelData(tdsReceivablesTxn);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * TDS_RECEIVABLE_DETAILS by using TDS_RECEIVABLE_DETAILSDAO's delete method with type as Blank 3) Audit the record
	 * in to AuditHeader and AdtTDS_RECEIVABLE_DETAILS by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		TdsReceivablesTxn tdsReceivablesTxn = (TdsReceivablesTxn) auditHeader.getAuditDetail().getModelData();
		tdsReceivablesTxnDAO.delete(tdsReceivablesTxn, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getTDSReceivablesTxnDAO().delete with
	 * parameters TdsReceivablesTxn,"" b) NEW Add new record in to main table by using getTDSReceivablesTxnDAO().save
	 * with parameters TdsReceivablesTxn,"" c) EDIT Update record in the main table by using
	 * getTDSReceivablesTxnDAO().update with parameters TdsReceivablesTxn,"" 3) Delete the record from the workFlow
	 * table by using getTDSReceivablesTxnDAO().delete with parameters TdsReceivablesTxn,"_Temp" 4) Audit the record in
	 * to AuditHeader and AdtTDS_RECEIVABLE_DETAILS by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit
	 * the record in to AuditHeader and AdtTDS_RECEIVABLE_DETAILS by using auditHeaderDAO.addAudit(auditHeader) based on
	 * the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {

		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		String tranType = "";

		TdsReceivable tdsReceivable = new TdsReceivable();
		BeanUtils.copyProperties((TdsReceivable) auditHeader.getAuditDetail().getModelData(), tdsReceivable);
		TdsReceivablesTxn atdsReceivablesTxn = tdsReceivable.getTdsReceivablesTxn();

		if (!PennantConstants.RECORD_TYPE_NEW.equals(tdsReceivable.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(tdsReceivableDAO.getTdsReceivable(tdsReceivable.getId(), TableType.MAIN_TAB));
		}

		if (CollectionUtils.isNotEmpty(tdsReceivable.getTdsReceivablesTxnList())) {
			List<AuditDetail> details = tdsReceivable.getAuditDetailMap().get("TdsReceivablesTxns");
			details.forEach(auditdetails -> {
				TdsReceivablesTxn tdsReceivablesTxn = (TdsReceivablesTxn) auditdetails.getModelData();
				tdsReceivablesTxn.setReceivableID(tdsReceivable.getId());
				tdsReceivablesTxn.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				tdsReceivablesTxn.setUserDetails(atdsReceivablesTxn.getUserDetails());
				tdsReceivablesTxn.setRecordStatus(atdsReceivablesTxn.getRecordStatus());
				tdsReceivablesTxn.setTaskId("");
				tdsReceivablesTxn.setNextTaskId("");
				tdsReceivablesTxn.setRoleCode("");
				tdsReceivablesTxn.setNextRoleCode("");
				tdsReceivablesTxn.setWorkflowId(0);
				tdsReceivablesTxn.setRecordType("");
				if (atdsReceivablesTxn.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {

					if (tdsReceivablesTxn.getAdjustmentAmount().compareTo(BigDecimal.ZERO) != 0) {
						tdsReceivablesTxnDAO.save(tdsReceivablesTxn, TableType.MAIN_TAB);
					}

				} else
					tdsReceivablesTxnDAO.update(tdsReceivablesTxn, TableType.MAIN_TAB);

				tdsReceivablesTxnDAO.delete(tdsReceivablesTxn, TableType.TEMP_TAB);

			});
			auditDetails.addAll(details);
		}

		tdsReceivableDAO.updateReceivableBalances(tdsReceivable);

		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(tdsReceivable);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getTDSReceivablesTxnDAO().delete with parameters TdsReceivablesTxn,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtTDS_RECEIVABLE_DETAILS by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		TdsReceivable tdsReceivable = (TdsReceivable) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(
				processChildsAudit(deleteChilds(tdsReceivable, "_Temp", auditHeader.getAuditTranType())));

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private List<AuditDetail> processChildsAudit(List<AuditDetail> list) {

		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (list == null || list.isEmpty()) {
			return auditDetails;
		}

		for (AuditDetail detail : list) {
			String transType = "";
			String rcdType = "";
			Object object = detail.getModelData();

			if (object instanceof TdsReceivablesTxn) {
				rcdType = ((TdsReceivablesTxn) object).getRecordType();
			}

			if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
				transType = PennantConstants.TRAN_ADD;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType)
					|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
				transType = PennantConstants.TRAN_DEL;
			} else {
				transType = PennantConstants.TRAN_UPD;
			}

			auditDetails.add(new AuditDetail(transType, detail.getAuditSeq(), detail.getBefImage(), object));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public List<AuditDetail> deleteChilds(TdsReceivable tdsReceivable, String tableType, String auditTranType) {

		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (CollectionUtils.isNotEmpty(tdsReceivable.getTdsReceivablesTxnList())) {
			String[] fields = PennantJavaUtil.getFieldDetails(new TdsReceivable(),
					new TdsReceivable().getExcludeFields());
			for (int i = 0; i < tdsReceivable.getTdsReceivablesTxnList().size(); i++) {
				TdsReceivablesTxn TdsReceivable = tdsReceivable.getTdsReceivablesTxnList().get(i);
				if (StringUtils.isNotEmpty(TdsReceivable.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							TdsReceivable.getBefImage(), TdsReceivable));
				}
				tdsReceivablesTxnDAO.delete(TdsReceivable, TableType.TEMP_TAB);
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {

		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		TdsReceivable tdsReceivable = (TdsReceivable) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (tdsReceivable.getTdsReceivablesTxn().isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		List<TdsReceivablesTxn> tdsReceivablesTxnList = tdsReceivable.getTdsReceivablesTxnList();

		if (CollectionUtils.isNotEmpty(tdsReceivablesTxnList)) {
			auditDetailMap.put("TdsReceivablesTxns", setTdsReceivableAuditData(tdsReceivable, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("TdsReceivablesTxns"));
		}

		tdsReceivable.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(tdsReceivable);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private List<AuditDetail> setTdsReceivableAuditData(TdsReceivable tdsReceivable, String auditTranType,
			String method) {

		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		TdsReceivablesTxn aTdsReceivablesTxn = tdsReceivable.getTdsReceivablesTxn();

		String[] fields = PennantJavaUtil.getFieldDetails(aTdsReceivablesTxn, aTdsReceivablesTxn.getExcludeFields());

		for (int i = 0; i < tdsReceivable.getTdsReceivablesTxnList().size(); i++) {
			TdsReceivablesTxn tdsReceivablesTxn = tdsReceivable.getTdsReceivablesTxnList().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(aTdsReceivablesTxn.getRecordType()))) {
				continue;
			}

			tdsReceivablesTxn.setWorkflowId(aTdsReceivablesTxn.getWorkflowId());
			boolean isRcdType = false;

			if (aTdsReceivablesTxn.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				tdsReceivablesTxn.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (aTdsReceivablesTxn.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				tdsReceivablesTxn.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (tdsReceivable.isWorkflow()) {
					isRcdType = true;
				}
			} else if (aTdsReceivablesTxn.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				tdsReceivablesTxn.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				aTdsReceivablesTxn.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (aTdsReceivablesTxn.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (aTdsReceivablesTxn.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| aTdsReceivablesTxn.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			tdsReceivablesTxn.setRecordStatus(tdsReceivable.getRecordStatus());
			tdsReceivablesTxn.setUserDetails(tdsReceivable.getUserDetails());
			tdsReceivablesTxn.setLastMntOn(tdsReceivable.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					tdsReceivablesTxn.getBefImage(), tdsReceivablesTxn));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getTDSReceivablesTxnDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	public List<TdsReceivablesTxn> getTdsReceivablesTxnsByTanId(long tANId, Date fromDate, Date toDate) {
		logger.debug(Literal.ENTERING);

		int count = tdsReceivablesTxnDAO.isDuplicateTransaction(tANId, fromDate, toDate);

		if (count > 0) {
			MessageUtil.showMessage("Receipts Under the Current Selected Financial Year are in Pending");
			return null;
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<TdsReceivablesTxn>() {
			private static final long serialVersionUID = 5256798409298461299L;
			{
				List<TdsReceivablesTxn> receiptList = tdsReceivablesTxnDAO.getTdsReceivablesTxnsByTanId(tANId, fromDate,
						toDate);
				for (TdsReceivablesTxn tdsReceivablesTxn : receiptList) {
					if (StringUtils.equals(tdsReceivablesTxn.getReferenceType(), FinServiceEvent.EARLYRPY)) {
					}
				}
				addAll(receiptList);
				addAll(tdsReceivablesTxnDAO.getTdsReceivablesPostTxnsByTanId(tANId, fromDate, toDate));
			}
		};

	}

	@Override
	public List<TdsReceivablesTxn> getTdsReceivablesTxnsByTxnId(long txnId, TableType type, String module) {
		return new ArrayList<TdsReceivablesTxn>() {
			private static final long serialVersionUID = 140104139228215397L;
			{
				List<TdsReceivablesTxn> receiptList = tdsReceivablesTxnDAO.getTdsReceivablesTxnsByTxnId(txnId, type,
						module);
				for (TdsReceivablesTxn tdsReceivablesTxn : receiptList) {
					if (FinServiceEvent.EARLYRPY.equals(tdsReceivablesTxn.getReferenceType())) {
						tdsReceivablesTxn.setReferenceType(Labels.getLabel("label_TdsReceivable_Partial_Payment"));
					}
				}
				addAll(receiptList);
				addAll(tdsReceivablesTxnDAO.getTdsReceivablesPostTxnsByTxnId(txnId, type, module));
			}
		};
	}

	@Override
	public int getPendingTransactions(long receivableId) {

		return tdsReceivablesTxnDAO.getPendingTransactions(receivableId);
	}

	@Override
	public List<TdsReceivablesTxn> getTdsReceivablesTxnByReceivableId(long receivableID) {

		return tdsReceivablesTxnDAO.getTdsReceivablesTxnByReceivableId(receivableID, TableType.MAIN_TAB);
	}

	@Override
	public long getPendingReceipt(long receiptID, TableType type) {
		return tdsReceivablesTxnDAO.getPendingReceipt(receiptID, type);
	}

	@Override
	public void deleteTxnByReceiptId(long receiptId) {
		tdsReceivablesTxnDAO.deleteTxnByReceiptId(receiptId);
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);
		TdsReceivable tdsReceivable = (TdsReceivable) auditDetail.getModelData();

		if (tdsReceivable.getBalanceAmount().compareTo(BigDecimal.ZERO) < 0
				&& PennantConstants.RECORD_TYPE_NEW.equals(tdsReceivable.getRecordType())) {

			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_TdsReceivablesTxnDialog_AdjustmentAmount.value");
			parameters[1] = PennantJavaUtil.getLabel("label_TdsReceivablesTxnDialog_BalanceAmount.value");

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "90220", parameters, null));
			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public void cancelReceivablesTxnByReceiptId(long receiptId) {
		logger.debug(Literal.ENTERING);

		List<TdsReceivablesTxn> tdsReceivablesTxnlist = tdsReceivablesTxnDAO.getTdsReceivablesTxnByReceiptId(receiptId,
				TableType.MAIN_TAB);
		if (CollectionUtils.isNotEmpty(tdsReceivablesTxnlist)) {
			tdsReceivablesTxnlist.forEach(tdsReceivablesTxn -> {
				TdsReceivable tdsReceivable = tdsReceivableDAO.getTdsReceivable(tdsReceivablesTxn.getReceivableID(),
						TableType.MAIN_TAB);
				BigDecimal adjustmentAmount = tdsReceivablesTxn.getAdjustmentAmount();

				tdsReceivable.setUtilizedAmount(tdsReceivable.getUtilizedAmount().subtract(adjustmentAmount));
				tdsReceivable.setBalanceAmount(
						tdsReceivable.getCertificateAmount().subtract(tdsReceivable.getUtilizedAmount()));

				tdsReceivableDAO.updateReceivableBalances(tdsReceivable);
			});

			tdsReceivablesTxnDAO.updateReceivablesTxnStatus(receiptId, TdsReceivablesTxnStatus.RECEIPTCANCEL);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<ReturnDataSet> getPostingsByLinkTransId(long linkedTranId, TableType type, boolean showZeroBal) {
		return tdsReceivablesTxnDAO.getPostingsByLinkTransId(linkedTranId, type, showZeroBal);
	}

	@Override
	public Date getMaxFinancialDate(long tanId) {
		Date minRcptDate = tdsReceivablesTxnDAO.getMinRcptFinancialDate(tanId);
		Date minPostDate = tdsReceivablesTxnDAO.getMinPostFinancialDate(tanId);

		int compare = DateUtil.compare(minRcptDate, minPostDate);

		if (minRcptDate != null && minPostDate != null && compare == 0) {
			return minRcptDate;
		}

		if (minRcptDate == null) {
			return minPostDate;
		}

		if (minPostDate == null) {
			return minRcptDate;
		}

		if (compare == -1) {
			return minRcptDate;
		}

		if (compare == 1) {
			return minPostDate;
		}

		return null;
	}

	@Override
	public List<TdsReceivablesTxn> getTdsReceivablesTxnsByFinRef(String finReference, TableType type) {
		List<TdsReceivablesTxn> recTdsReceivablestxn = tdsReceivablesTxnDAO.getTdsReceiptTxnsByFinRef(finReference,
				type);
		for (TdsReceivablesTxn tdsReceivablesTxn : recTdsReceivablestxn) {
			if (tdsReceivablesTxn.getReceiptPurpose().equals(FinServiceEvent.EARLYRPY)) {
				tdsReceivablesTxn.setReceiptPurpose(Labels.getLabel("label_TdsReceivable_Partial_Payment"));
			}
		}
		List<TdsReceivablesTxn> jvTdsReceivables = tdsReceivablesTxnDAO.getTdsJvPostingsTxnsByFinRef(finReference,
				type);

		recTdsReceivablestxn.addAll(jvTdsReceivables);

		BigDecimal tdsAdjAmt = BigDecimal.ZERO;
		long receiptId = 0;

		for (TdsReceivablesTxn tds : recTdsReceivablestxn) {
			if (receiptId == 0) {
				receiptId = tds.getReceiptID();
			}

			if (receiptId != 0 && receiptId != tds.getReceiptID()) {
				receiptId = tds.getReceiptID();
				tdsAdjAmt = BigDecimal.ZERO;
			}

			if (tds.getStatus() == null) {
				tdsAdjAmt = tdsAdjAmt.add(tds.getTdsAdjusted());
				tds.setBalanceAmount(tds.getTdsReceivable().subtract(tdsAdjAmt));
			} else if (tds.getStatus().equals("RC")) {
				tds.setBalanceAmount(tds.getTdsReceivable());
			} else if (tds.getStatus().equals("AC")) {
				tds.setBalanceAmount(tds.getTdsReceivable().subtract(tdsAdjAmt));
			} else if (tds.getStatus().equals("CC")) {
				tds.setBalanceAmount(tds.getTdsReceivable().subtract(tdsAdjAmt));
			}
		}

		Collections.sort(recTdsReceivablestxn, new Comparator<TdsReceivablesTxn>() {
			public int compare(TdsReceivablesTxn t1, TdsReceivablesTxn t2) {
				return ComparisonChain.start().compare(t1.getTxnID(), t2.getTxnID()).result();
			}
		});
		return recTdsReceivablestxn;
	}

	public void setTdsReceivablesTxnDAO(TdsReceivablesTxnDAO tdsReceivablesTxnDAO) {
		this.tdsReceivablesTxnDAO = tdsReceivablesTxnDAO;
	}

	public void setTdsReceivableDAO(TdsReceivableDAO tdsReceivableDAO) {
		this.tdsReceivableDAO = tdsReceivableDAO;
	}

}