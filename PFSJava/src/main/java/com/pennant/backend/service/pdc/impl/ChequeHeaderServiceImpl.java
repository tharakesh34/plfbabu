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
package com.pennant.backend.service.pdc.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.APIHeader;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.dao.customermasters.CustEmployeeDetailDAO;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.customermasters.CustomerBankInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerCardSalesInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerChequeInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.customermasters.CustomerEMailDAO;
import com.pennant.backend.dao.customermasters.CustomerEmploymentDetailDAO;
import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.dao.customermasters.CustomerGstDetailDAO;
import com.pennant.backend.dao.customermasters.CustomerPhoneNumberDAO;
import com.pennant.backend.dao.customermasters.CustomerRatingDAO;
import com.pennant.backend.dao.customermasters.DirectorDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.JointAccountDetailDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.pdc.ChequeHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.PrimaryAccount;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.BankInfoDetail;
import com.pennant.backend.model.customermasters.CustCardSales;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerGST;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.pdc.ChequeHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.mandate.AccountTypes;
import com.pennant.pff.mandate.ChequeSatus;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateUtil;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.dao.customer.income.IncomeDetailDAO;
import com.pennanttech.pff.dao.customer.liability.ExternalLiabilityDAO;
import com.pennanttech.pff.external.pan.dao.PrimaryAccountDAO;

/**
 * Service implementation for methods that depends on <b>ChequeHeader</b>.<br>
 */
public class ChequeHeaderServiceImpl extends GenericService<ChequeHeader> implements ChequeHeaderService {
	private static final Logger logger = LogManager.getLogger(ChequeHeaderServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ChequeHeaderDAO chequeHeaderDAO;
	private ChequeDetailDAO chequeDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceTypeDAO financeTypeDAO;
	private JointAccountDetailDAO jointAccountDetailDAO;
	private BankBranchDAO bankBranchDAO;
	private CustomerDAO customerDAO;
	private PrimaryAccountDAO primaryAccountDAO;
	private CustomerEmploymentDetailDAO customerEmploymentDetailDAO;
	private CustEmployeeDetailDAO custEmployeeDetailDAO;
	private IncomeDetailDAO incomeDetailDAO;
	private DirectorDetailDAO directorDetailDAO;
	private CustomerRatingDAO customerRatingDAO;
	private CustomerPhoneNumberDAO customerPhoneNumberDAO;
	private CustomerEMailDAO customerEMailDAO;
	private CustomerBankInfoDAO customerBankInfoDAO;
	private CustomerGstDetailDAO customerGstDetailDAO;
	private CustomerChequeInfoDAO customerChequeInfoDAO;
	private ExternalLiabilityDAO externalLiabilityDAO;
	private CustomerCardSalesInfoDAO customerCardSalesInfoDAO;
	private CustomerExtLiabilityDAO customerExtLiabilityDAO;
	private CustomerAddresDAO customerAddresDAO;
	private CustomerDocumentDAO customerDocumentDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<>();
		ChequeHeader ch = (ChequeHeader) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (ch.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (ch.isNewRecord()) {
			processDocument(ch);
			ch.setId(Long.parseLong(chequeHeaderDAO.save(ch, tableType)));
			auditHeader.getAuditDetail().setModelData(ch);
			auditHeader.setAuditReference(String.valueOf(ch.getHeaderID()));
		} else {
			processDocument(ch);
			chequeHeaderDAO.update(ch, tableType);
		}

		if (CollectionUtils.isNotEmpty(ch.getChequeDetailList())) {

			List<AuditDetail> details = ch.getAuditDetailMap().get("ChequeDetail");
			details = processingChequeDetailList(details, tableType, ch.getHeaderID());
			auditDetails.addAll(details);
		}

		financeMainDAO.updateMaintainceStatus(ch.getFinID(), FinServiceEvent.CHEQUEDETAILS);

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	private void processDocument(ChequeHeader ch) {
		List<ChequeDetail> cdList = ch.getChequeDetailList();
		if (CollectionUtils.isEmpty(cdList)) {
			return;
		}

		DocumentDetails dd = new DocumentDetails();
		dd.setFinReference(ch.getFinReference());

		for (ChequeDetail detail : cdList) {
			if (!detail.isNewRecord()) {
				if (detail.getDocImage() != null) {
					byte[] arr1 = null;
					if (detail.getDocumentRef() != null && detail.getDocumentRef() > 0) {
						arr1 = getDocumentImage(detail.getDocumentRef());
					}
					byte[] arr2 = detail.getDocImage();
					if (!Arrays.equals(arr1, arr2)) {
						dd.setDocImage(detail.getDocImage());
						saveDocument(DMSModule.FINANCE, DMSModule.CHEQUE, dd);
						detail.setDocumentRef(dd.getDocRefId());
					}
				}
			} else {
				dd.setDocImage(detail.getDocImage());
				dd.setUserDetails(detail.getUserDetails());
				saveDocument(DMSModule.FINANCE, DMSModule.CHEQUE, dd);
				detail.setDocumentRef(dd.getDocRefId());
			}
		}
	}

	private List<AuditDetail> processingChequeDetailList(List<AuditDetail> auditDetails, TableType type,
			long headerID) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			ChequeDetail cd = (ChequeDetail) auditDetails.get(i).getModelData();

			if (StringUtils.isEmpty(cd.getRecordType())) {
				continue;
			}

			if (StringUtils.isEmpty(type.getSuffix())
					&& PennantConstants.RCD_STATUS_CANCELLED.equals(cd.getRecordStatus())) {
				chequeDetailDAO.delete(cd, type);
			} else {
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = false;
				String rcdType = "";
				String recordStatus = "";
				if (StringUtils.isEmpty(type.getSuffix())) {
					approveRec = true;
					cd.setRoleCode("");
					cd.setNextRoleCode("");
					cd.setTaskId("");
					cd.setNextTaskId("");
					cd.setWorkflowId(0);
				}
				cd.setHeaderID(headerID);
				if (cd.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
				} else if (cd.isNewRecord()) {
					saveRecord = true;
					if (cd.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						cd.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (cd.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						cd.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (cd.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						cd.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (cd.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (cd.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (cd.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					deleteRecord = true;
				}
				if (approveRec) {
					rcdType = cd.getRecordType();
					recordStatus = cd.getRecordStatus();
					cd.setRecordType("");
					cd.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (saveRecord) {
					chequeDetailDAO.save(cd, type);
				}

				if (updateRecord) {
					chequeDetailDAO.update(cd, type);
				}

				if (deleteRecord) {
					chequeDetailDAO.delete(cd, type);
				}

				if (approveRec) {
					cd.setRecordType(rcdType);
					cd.setRecordStatus(recordStatus);
				}
				auditDetails.get(i).setModelData(cd);
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;

	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		ChequeHeader ch = (ChequeHeader) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (ch.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		// chequeHeader details
		if (ch.getChequeDetailList() != null && ch.getChequeDetailList().size() > 0) {
			auditDetailMap.put("ChequeDetail", setChequeDetailAuditData(ch, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("ChequeDetail"));
		}

		ch.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(ch);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private List<AuditDetail> setChequeDetailAuditData(ChequeHeader chequeHeader, String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		ChequeDetail cd = new ChequeDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(cd, cd.getExcludeFields());
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

		ChequeHeader ch = (ChequeHeader) auditHeader.getAuditDetail().getModelData();
		chequeHeaderDAO.delete(ch, TableType.MAIN_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditDetails(listDeletion(ch, TableType.MAIN_TAB, PennantConstants.TRAN_WF));

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public ChequeHeader getChequeHeader(long headerId) {
		ChequeHeader ch = chequeHeaderDAO.getChequeHeader(headerId, "_View");
		if (ch != null) {
			ch.setChequeDetailList(chequeDetailDAO.getChequeDetailList(ch.getHeaderID(), "_View"));
		}
		return ch;
	}

	@Override
	public ChequeHeader getChequeHeaderByRef(long finID) {
		ChequeHeader ch = chequeHeaderDAO.getChequeHeaderByRef(finID, "_View");
		if (ch != null) {
			ch.setChequeDetailList(chequeDetailDAO.getChequeDetailList(ch.getHeaderID(), "_View"));
		}
		return ch;
	}

	@Override
	public ChequeHeader getApprovedChequeHeader(long headerId) {
		ChequeHeader ch = chequeHeaderDAO.getChequeHeader(headerId, "_AView");
		if (ch != null) {
			ch.setChequeDetailList(chequeDetailDAO.getChequeDetailList(ch.getHeaderID(), "_AView"));
		}
		return ch;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		ChequeHeader ch = new ChequeHeader();
		BeanUtils.copyProperties((ChequeHeader) auditHeader.getAuditDetail().getModelData(), ch);

		if (PennantConstants.RECORD_TYPE_DEL.equals(ch.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			chequeHeaderDAO.delete(ch, TableType.MAIN_TAB);
			auditDetails.addAll(listDeletion(ch, TableType.MAIN_TAB, auditHeader.getAuditTranType()));

		} else {
			ch.setRoleCode("");
			ch.setNextRoleCode("");
			ch.setTaskId("");
			ch.setNextTaskId("");
			ch.setWorkflowId(0);

			processDocument(ch);

			if (PennantConstants.RECORD_TYPE_NEW.equals(ch.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				ch.setRecordType("");
				chequeHeaderDAO.save(ch, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				ch.setRecordType("");
				chequeHeaderDAO.update(ch, TableType.MAIN_TAB);
			}
		}

		financeMainDAO.updateMaintainceStatus(ch.getFinID(), "");

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		// Cheque Details
		if (ch.getChequeDetailList() != null && ch.getChequeDetailList().size() > 0) {
			List<AuditDetail> details = ch.getAuditDetailMap().get("ChequeDetail");
			details = processingChequeDetailList(details, TableType.MAIN_TAB, ch.getHeaderID());
			auditDetails.addAll(details);
		}
		if (!PennantConstants.FINSOURCE_ID_API.equals(ch.getSourceId())
				&& !RequestSource.UPLOAD.name().equals(ch.getSourceId())) {
			auditHeader.setAuditDetails(
					getListAuditDetails(listDeletion(ch, TableType.TEMP_TAB, auditHeader.getAuditTranType())));// FIXME
			chequeHeaderDAO.delete(ch, TableType.TEMP_TAB);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(ch);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	// Method for Deleting all records related to ChequeHeaderModule and
	// ChequeHeaderLOB in _Temp/Main tables depend on method type
	private List<AuditDetail> listDeletion(ChequeHeader chequeHeader, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		List<AuditDetail> details = chequeHeader.getAuditDetailMap().get("ChequeDetail");
		List<ChequeDetail> chequeDetailList = new ArrayList<ChequeDetail>();

		for (int i = 0; i < details.size(); i++) {
			ChequeDetail chequeDetail = (ChequeDetail) details.get(i).getModelData();

			if (StringUtils.isEmpty(chequeDetail.getRecordType())) {
				continue;
			}
			chequeDetailList.add(chequeDetail);
		}

		if (CollectionUtils.isNotEmpty(chequeDetailList)) {
			String[] fields = PennantJavaUtil.getFieldDetails(new ChequeDetail());
			for (int i = 0; i < chequeDetailList.size(); i++) {
				ChequeDetail chequeDetail = chequeDetailList.get(i);
				if (!StringUtils.isEmpty(chequeDetail.getRecordType()) || StringUtils.isEmpty(tableType.getSuffix())) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							chequeDetail.getBefImage(), chequeDetail));
				}
				ChequeDetail detail = chequeDetailList.get(i);
				chequeDetailDAO.delete(detail, tableType);
			}
		}

		logger.debug(Literal.LEAVING);
		return auditList;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ChequeHeader chequeHeader = (ChequeHeader) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(
				getListAuditDetails(listDeletion(chequeHeader, TableType.TEMP_TAB, PennantConstants.TRAN_WF)));
		financeMainDAO.updateMaintainceStatus(chequeHeader.getFinID(), "");
		chequeHeaderDAO.delete(chequeHeader, TableType.TEMP_TAB);
		auditHeaderDAO.addAudit(auditHeader);

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
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (CollectionUtils.isEmpty(list)) {
			logger.debug(Literal.LEAVING);
			return auditDetails;
		}

		for (int i = 0; i < list.size(); i++) {

			String transType = "";
			String rcdType = "";
			Object object = ((AuditDetail) list.get(i)).getModelData();
			try {

				// ChequeHeaderModule module = (ChequeHeaderModule) ((AuditDetail)list.get(i)).getModelData();
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
					auditDetails
							.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(), befImg, object));
				}
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		ChequeHeader ch = (ChequeHeader) auditDetail.getModelData();

		// Check the unique keys.
		if (ch.isNewRecord() && chequeHeaderDAO.isDuplicateKey(ch.getHeaderID(), ch.getFinID(),
				ch.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_FinReference") + ": " + ch.getFinReference();
			parameters[1] = PennantJavaUtil.getLabel("label_HeaderID") + ": " + ch.getHeaderID();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		List<ChequeDetail> cdList = ch.getChequeDetailList();

		for (ChequeDetail cd : cdList) {
			if (cd.isNewRecord() && chequeDetailDAO.isDuplicateKey(cd.getChequeDetailsID(), cd.getBankBranchID(),
					cd.getAccountNo(), cd.getChequeSerialNumber(), TableType.BOTH_TAB)) {

				String[] parameters = new String[3];

				parameters[0] = PennantJavaUtil.getLabel("label_ChequeDetailDialog_BankBranchID.value") + ": "
						+ cd.getBankBranchID();
				parameters[1] = PennantJavaUtil.getLabel("label_ChequeDetailDialog_AccNumber.value") + ": "
						+ cd.getAccountNo();
				parameters[2] = PennantJavaUtil.getLabel("label_ChequeDetailDialog_ChequeSerialNo.value") + ": "
						+ cd.getChequeSerialNumber();

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41008", parameters, null));
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public FinanceDetail getFinanceDetailById(long finID) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "_View", false);
		FinanceType financeType = financeTypeDAO.getFinanceTypeByID(fm.getFinType(), "_AView");

		schdData.setFinanceMain(fm);
		schdData.setFinanceType(financeType);

		fd.setCustomerDetails(getCustomerDetailsbyID(fm.getCustID(), true, "_View"));
		fd.setJointAccountDetailList(jointAccountDetailDAO.getJointAccountDetailByFinRef(finID));

		logger.debug(Literal.LEAVING);

		return fd;
	}

	private CustomerDetails getCustomerDetailsbyID(long id, boolean reqChildDetails, String type) {
		logger.debug(Literal.ENTERING);

		CustomerDetails cd = new CustomerDetails();
		cd.setCustomer(customerDAO.getCustomerByID(id, type));
		cd.setCustID(id);

		Customer customer = cd.getCustomer();
		PrimaryAccount primaryAccount = primaryAccountDAO.getPrimaryAccountDetails(customer.getCustCRCPR());

		if (primaryAccount != null) {
			customer.setPrimaryIdName(primaryAccount.getDocumentName());
		}

		if (!reqChildDetails) {
			return cd;
		}
		if (ImplementationConstants.ALLOW_MULTIPLE_EMPLOYMENTS) {
			cd.setEmploymentDetailsList(customerEmploymentDetailDAO.getCustomerEmploymentDetailsByID(id, type));
		} else {
			cd.setCustEmployeeDetail(custEmployeeDetailDAO.getCustEmployeeDetailById(id, type));
		}
		if (ImplementationConstants.ALLOW_CUSTOMER_INCOMES) {
			cd.setCustomerIncomeList(incomeDetailDAO.getIncomesByCustomer(id, type));
		}
		// ### Ticket 126612 LMS > PDE > newly added shareholder are not
		// displayed in PDE. Changed the condition to
		// non individual.
		if (StringUtils.isNotEmpty(customer.getCustCtgCode())
				&& !StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
			if (ImplementationConstants.ALLOW_CUSTOMER_SHAREHOLDERS) {
				cd.setCustomerDirectorList(directorDetailDAO.getCustomerDirectorByCustomer(id, type));
			}
			if (ImplementationConstants.ALLOW_CUSTOMER_RATINGS) {
				cd.setRatingsList(customerRatingDAO.getCustomerRatingByCustomer(id, type));
			}
		}

		cd.setCustomerDocumentsList(customerDocumentDAO.getCustomerDocumentByCustomer(id, type));
		cd.setAddressList(customerAddresDAO.getCustomerAddresByCustomer(id, type));
		cd.setCustomerPhoneNumList(customerPhoneNumberDAO.getCustomerPhoneNumberByCustomer(id, type));
		cd.setCustomerEMailList(customerEMailDAO.getCustomerEmailByCustomer(id, type));
		cd.setCustomerBankInfoList(customerBankInfoDAO.getBankInfoByCustomer(id, type));

		if (cd.getCustomerBankInfoList() != null && cd.getCustomerBankInfoList().size() > 0) {
			for (CustomerBankInfo customerBankInfo : cd.getCustomerBankInfoList()) {
				customerBankInfo.setBankInfoDetails(
						customerBankInfoDAO.getBankInfoDetailById(customerBankInfo.getBankId(), type));

				if (CollectionUtils.isNotEmpty(customerBankInfo.getBankInfoDetails())) {
					for (BankInfoDetail bankInfoDetail : customerBankInfo.getBankInfoDetails()) {
						bankInfoDetail.setBankInfoSubDetails(customerBankInfoDAO.getBankInfoSubDetailById(
								bankInfoDetail.getBankId(), bankInfoDetail.getMonthYear(), type));
					}
				}
			}
		}
		cd.setCustomerGstList(customerGstDetailDAO.getCustomerGSTById(id, type));

		if (cd.getCustomerGstList() != null && cd.getCustomerGstList().size() > 0) {
			for (CustomerGST customerGST : cd.getCustomerGstList()) {
				customerGST.setCustomerGSTDetailslist(
						customerGstDetailDAO.getCustomerGSTDetailsByCustomer(customerGST.getId(), type));
			}
		}
		cd.setCustomerChequeInfoList(customerChequeInfoDAO.getChequeInfoByCustomer(id, type));

		CustomerExtLiability liability = new CustomerExtLiability();
		liability.setCustId(id);
		cd.setCustomerExtLiabilityList(externalLiabilityDAO.getLiabilities(liability.getCustId(), type));

		if (CollectionUtils.isNotEmpty(cd.getCustomerExtLiabilityList())) {
			for (CustomerExtLiability extLiability : cd.getCustomerExtLiabilityList()) {
				extLiability.setExtLiabilitiesPayments(
						customerExtLiabilityDAO.getExtLiabilitySubDetailById(extLiability.getId(), type));
			}
		}

		cd.setCustCardSales(customerCardSalesInfoDAO.getCardSalesInfoByCustomer(id, type));
		if (cd.getCustCardSales() != null && cd.getCustCardSales().size() > 0) {
			for (CustCardSales customerCardSalesInfo : cd.getCustCardSales()) {
				customerCardSalesInfo.setCustCardMonthSales(
						customerCardSalesInfoDAO.getCardSalesInfoSubDetailById(customerCardSalesInfo.getId(), type));
			}
		}
		cd.setCustFinanceExposureList(customerDAO.getCustomerFinanceDetailById(cd.getCustomer()));

		logger.debug(Literal.LEAVING);
		return cd;
	}

	@Override
	public ErrorDetail chequeValidation(FinanceDetail fd, String methodName, String tableType) {
		ChequeHeader ch = fd.getChequeHeader();

		ErrorDetail error = validateCheques(ch);

		if (error != null) {
			return error;
		}

		if (!PennantConstants.method_Update.equalsIgnoreCase(methodName) && ch.getNoOfCheques() <= 2) {
			return getError("30569", "NoOfCheques ", "Three");
		}

		error = validateBranchDetails(ch);

		if (error != null) {
			return error;
		}

		error = validateChequeSerialNo(ch);

		if (error != null) {
			return error;
		}

		return validateChequeDetails(ch.getChequeDetailList());
	}

	@Override
	public ErrorDetail chequeValidationInMaintainence(FinanceDetail fd, String methodName, String tableType) {
		ChequeHeader ch = fd.getChequeHeader();

		ChequeHeader existingCH = chequeHeaderDAO.getChequeHeaderByRef(fd.getFinID(), tableType);

		if (existingCH == null) {
			return getError("RU0040", "Header id ");
		}

		if (ch.getHeaderID() == 0) {
			return getError("90502", "chequeHeader Id");
		}

		ErrorDetail error = validateCheques(ch);

		if (error != null) {
			return error;
		}

		error = validateChequeHeaderDetails(ch, existingCH, tableType);

		if (error != null) {
			return error;
		}

		error = validateBranchDetails(ch);

		if (error != null) {
			return error;
		}

		return validateChequeDetails(ch.getChequeDetailList());
	}

	@Override
	public ErrorDetail chequeValidationForUpdate(FinanceDetail fd, String methodName, String tableType) {
		ChequeHeader ch = fd.getChequeHeader();

		ChequeHeader existingCH = chequeHeaderDAO.getChequeHeaderByRef(fd.getFinID(), tableType);

		if (existingCH == null) {
			return getError("RU0040", "Header id ");
		}

		if (ch.getHeaderID() == 0) {
			return getError("90502", "chequeHeader Id");
		}

		ErrorDetail error = validateCheques(ch);

		if (error != null) {
			return error;
		}
		boolean chequeType = false;

		List<ChequeDetail> cheques = ch.getChequeDetailList();
		for (ChequeDetail cd : cheques) {
			String sourceId = ch.getSourceId();
			if (!RequestSource.UPLOAD.name().equals(sourceId) && InstrumentType.isPDC(cd.getChequeType())) {
				chequeType = true;
			}

			// FIXME this should come from loan type
			if (existingCH.getNoOfCheques() + ch.getNoOfCheques() <= 2 && chequeType) {
				return getError("30569", "NoOfCheques ", "Three");
			}
		}

		error = validateBranchDetails(ch);

		if (error != null) {
			return error;
		}

		error = validateChequeSerialNo(ch);

		if (error != null) {
			return error;
		}

		ch.setNoOfCheques(ch.getNoOfCheques() + existingCH.getNoOfCheques());
		ch.setTotalAmount(existingCH.getTotalAmount());

		return validateChequeDetails(ch.getChequeDetailList());
	}

	@Override
	public ErrorDetail processChequeDetail(FinanceDetail fd, String tableType, LoggedInUser loggedInUser) {
		logger.debug(Literal.ENTERING);

		ChequeHeader ch = fd.getChequeHeader();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		ch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		if (StringUtils.isNotBlank(tableType)) {
			ch.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
			ch.setNewRecord(true);
			ch.setVersion(1);
		} else {
			ch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			ch.setNewRecord(false);
			ch.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		}

		prepareChequeHeader(ch, fm, loggedInUser);

		List<ChequeDetail> cheques = ch.getChequeDetailList();
		String serialNum = String.valueOf(ch.getChequeSerialNumber());

		String ccy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);

		for (ChequeDetail cheque : cheques) {
			prepareChequeDetails(tableType, ch, fm, loggedInUser, ccy, cheque);

			cheque.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			cheque.setNewRecord(true);

			if (RequestSource.API.name().equals(ch.getSourceId())) {
				serialNum = serialNum + 1;
				cheque.setChequeSerialNumber(StringUtils.leftPad("" + serialNum, 6, "0"));
			}

			if ("PDC".equals(cheque.getChequeType())) {
				ch.setTotalAmount(ch.getTotalAmount().add(cheque.getAmount()));
			}
		}

		return processCheques(tableType, ch);
	}

	private void prepareChequeHeader(ChequeHeader ch, FinanceMain fm, LoggedInUser loggedInUser) {
		ch.setLastMntBy(loggedInUser.getUserId());
		ch.setRecordStatus(fm.getRecordStatus());
		ch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		ch.setTaskId(fm.getTaskId());
		ch.setNextTaskId(fm.getNextTaskId());
		ch.setRoleCode(fm.getRoleCode());
		ch.setNextRoleCode(fm.getNextRoleCode());
		ch.setWorkflowId(fm.getWorkflowId());
		ch.setActive(true);
		ch.setSourceId(StringUtils.isEmpty(ch.getSourceId()) ? RequestSource.API.name() : ch.getSourceId());
		ch.setFinID(fm.getFinID());
		ch.setFinReference(fm.getFinReference());
	}

	private void prepareChequeDetails(String type, ChequeHeader ch, FinanceMain fm, LoggedInUser loggedInUser,
			String ccy, ChequeDetail cheque) {
		if (StringUtils.isNotBlank(type)) {
			cheque.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			cheque.setNewRecord(true);
		} else {
			cheque.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			cheque.setNewRecord(false);
		}

		cheque.setLastMntBy(loggedInUser.getUserId());
		cheque.setRecordStatus(fm.getRecordStatus());
		cheque.setVersion(fm.getVersion());
		cheque.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		cheque.setTaskId(fm.getTaskId());
		cheque.setNextTaskId(fm.getNextTaskId());
		cheque.setRoleCode(fm.getRoleCode());
		cheque.setNextRoleCode(fm.getNextRoleCode());
		cheque.setWorkflowId(fm.getWorkflowId());

		if (RequestSource.UPLOAD.name().equals(ch.getSourceId())) {
			cheque.setBankBranchID(ch.getBankBranchID());
			cheque.setAccHolderName(ch.getAccHolderName());
			cheque.setAccountNo(ch.getAccountNo());
		}

		cheque.setStatus(ChequeSatus.NEW);
		cheque.setChequeStatus(ChequeSatus.NEW);
		cheque.setChequeCcy(ccy);
		cheque.setActive(true);
	}

	private ErrorDetail processCheques(String tableType, ChequeHeader ch) {
		AuditHeader auditHeader = getAuditHeader(ch, PennantConstants.TRAN_WF);

		if (StringUtils.isNotBlank(tableType)) {
			auditHeader = saveOrUpdate((auditHeader));
		} else {
			auditHeader = doApprove(auditHeader);
		}

		List<ErrorDetail> errors = auditHeader.getErrorMessage();
		if (CollectionUtils.isEmpty(errors)) {
			return null;
		}

		ErrorDetail error = errors.get(errors.size() - 1);

		return error;
	}

	protected AuditHeader getAuditHeader(ChequeHeader ch, String tranType) {
		AuditHeader ah = new AuditHeader(ch.getFinReference(), null, null, null, new AuditDetail(tranType, 1, null, ch),
				ch.getUserDetails(), new HashMap<>());

		if (RequestSource.API.name().equals(ch.getSourceId())) {
			ah.setApiHeader(PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY));
		}

		return ah;
	}

	@Override
	public ChequeHeader getChequeDetails(String finReference) {
		ChequeHeader response = new ChequeHeader();
		if (StringUtils.isBlank(finReference)) {
			response.setError(getError("90502", "finReference"));
			logger.debug(Literal.LEAVING);
			return response;
		}

		Long finID = financeMainDAO.getActiveFinID(finReference);
		if (finID == null) {
			response.setError(getError("90201", finReference));
			logger.debug(Literal.LEAVING);
			return response;
		}

		response = chequeHeaderDAO.getChequeHeaderByRef(finID, "_View");
		if (response == null) {
			response = new ChequeHeader();
			response.setError(getError("90201", "No Cheque Details"));
			logger.debug(Literal.LEAVING);
			return response;
		}

		response.setChequeDetailList(chequeDetailDAO.getChequeDetailList(response.getHeaderID(), "_View"));
		return response;
	}

	@Override
	public ErrorDetail validateBasicDetails(FinanceDetail fd, String type) {
		String finReference = fd.getFinReference();

		ChequeHeader chequeHeader = fd.getChequeHeader();

		if (chequeHeader == null) {
			return getError("90502", "Cheque Details ");
		}

		FinScheduleData schdData = fd.getFinScheduleData();

		if (finReference == null) {
			return getError("90502", "FinReference");
		}

		Long finID = financeMainDAO.getActiveFinID(finReference);

		if (finID == null) {
			return getError("90201", finReference);
		}

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, type, false);

		if (StringUtils.isNotEmpty(fm.getRcdMaintainSts())) {
			return getError("90201", finReference);
		}

		if (fm.isWriteoffLoan()) {
			return getError("FWF001", "");
		}
		String repaymethod = financeMainDAO.getApprovedRepayMethod(finID, "");
		if (InstrumentType.isPDC(chequeHeader.getChequeDetailList().get(0).getChequeType())
				&& !InstrumentType.isPDC(repaymethod)) {

			return getError("90204", "Cheques", "finRepayMethod is " + repaymethod);
		}

		schdData.setFinanceMain(fm);
		fd.setFinID(finID);

		return validateChequeDetails(fd, type, true);
	}

	private ErrorDetail validateChequeDetails(FinanceDetail fd, String type, boolean validReq) {
		boolean date = true;

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();

		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, type, false));

		ChequeHeader ch = fd.getChequeHeader();
		List<ChequeDetail> cheques = ch.getChequeDetailList();

		for (ChequeDetail cheque : cheques) {
			if (InstrumentType.isPDC(cheque.getChequeType())) {
				List<FinanceScheduleDetail> schedules = fd.getFinScheduleData().getFinanceScheduleDetails();
				for (FinanceScheduleDetail schedule : schedules) {
					date = false;

					if (DateUtil.compare(schedule.getSchDate(), cheque.getChequeDate()) == 0) {
						date = true;
						cheque.seteMIRefNo(schedule.getInstNumber());

						validate(schdData, schedule, cheque, validReq, ch.getHeaderID());
						break;
					}
				}

				if (!date) {
					setError(schdData, "30570", "Cheque Date", "ScheduleDates");
					break;
				}
			}
		}

		List<ErrorDetail> errors = schdData.getErrorDetails();
		if (CollectionUtils.isNotEmpty(errors)) {
			ErrorDetail ed = errors.get(0);
			return getError(ed.getCode(), ed.getError());
		}

		return null;
	}

	private void validate(FinScheduleData schdData, FinanceScheduleDetail schedule, ChequeDetail cheque,
			boolean validReq, long headerID) {
		BigDecimal repayAmount = schedule.getRepayAmount();
		Date schDate = schedule.getSchDate();

		if (repayAmount.compareTo(cheque.getAmount()) != 0) {
			setError(schdData, "30570", DateUtil.formatToLongDate(schDate), String.valueOf(repayAmount + "INR"));
			return;
		}

		if (validReq && chequeDetailDAO.isChequeExists(headerID, schDate)) {
			setError(schdData, "41018", "Cheque ", "Cheque Date : " + schDate);
		}
	}

	private void setError(FinScheduleData schdData, String code, String... parm) {
		ErrorDetail error = ErrorUtil.getError(code, parm);

		StringBuilder logMsg = new StringBuilder();
		logMsg.append("\n");
		logMsg.append("=======================================================\n");
		logMsg.append("Error-Code: ").append(error.getCode()).append("\n");
		logMsg.append("Error-Message: ").append(error.getMessage()).append("\n");
		logMsg.append("=======================================================");
		logMsg.append("\n");

		logger.error(Literal.EXCEPTION, logMsg);

		schdData.setErrorDetail(error);
	}

	private ErrorDetail validateCheques(ChequeHeader ch) {
		List<ChequeDetail> cheques = ch.getChequeDetailList();

		boolean spdcCount = isSpdcAvail(cheques);

		int chequeSize = 0;

		for (ChequeDetail cd : cheques) {
			if (InstrumentType.isPDC(cd.getChequeType())) {
				chequeSize++;
			}
		}

		if (chequeSize == 0 && spdcCount) {
			return getError("90502", "NoOfCheques");
		}

		if (spdcCount && chequeSize != ch.getNoOfCheques()) {
			return getError("30540", "ChequeDetails ", " total no cheques");
		}

		return null;
	}

	private boolean isSpdcAvail(List<ChequeDetail> cheques) {
		boolean spdcCount = true;
		for (ChequeDetail cd : cheques) {
			if (InstrumentType.isSPDC(cd.getChequeType())) {
				spdcCount = false;
			}
		}
		return spdcCount;
	}

	private ErrorDetail validateChequeSerialNo(ChequeHeader ch) {
		if (Integer.valueOf(ch.getChequeSerialNumber()) == 0) {
			return getError("90502", "ChequeSerialNo");
		}

		if (String.valueOf(ch.getChequeSerialNumber()).length() > 6) {
			return getError("30565", "ChequeSerialNo", "or Equal to size Six");
		}

		return null;
	}

	private ErrorDetail validateChequeHeaderDetails(ChequeHeader ch, ChequeHeader existingCH, String tableType) {
		long headerID = existingCH.getHeaderID();

		if (ch.getHeaderID() != headerID) {
			return getError("RU0040", "chequeHeader Id");
		}

		for (ChequeDetail chquDetailsID : ch.getChequeDetailList()) {
			if (chquDetailsID.getChequeDetailsID() == 0) {
				return getError("90502", "chequeDetails ID");
			}
		}

		List<ChequeDetail> existingCheques = chequeDetailDAO.getChequeDetailList(headerID, tableType);

		List<Long> chequeDetailsId = new ArrayList<>();
		existingCheques.forEach(cd -> chequeDetailsId.add(cd.getChequeDetailsID()));

		List<Long> chequeDtl = new ArrayList<>();
		List<ChequeDetail> chequeDetails = ch.getChequeDetailList();

		chequeDetails.forEach(cd -> chequeDtl.add(cd.getChequeDetailsID()));

		for (ChequeDetail cheque : chequeDetails) {
			if (!chequeDetailsId.contains(cheque.getChequeDetailsID())) {
				return getError("RU0040", "chequeDetails");
			}
		}

		ErrorDetail error = validateDeleteChequeCount(ch);

		if (error != null) {
			return error;
		}

		for (ChequeDetail cd : existingCheques) {
			for (ChequeDetail cheque : ch.getChequeDetailList()) {
				if (cheque.isDelete() && !ChequeSatus.NEW.equals(cd.getChequeStatus())) {
					return getError("49002", "For " + cd.getChequeDetailsID(), cd.getChequeStatus() + "Status");
				}
			}
		}

		return null;
	}

	private ErrorDetail validateDeleteChequeCount(ChequeHeader ch) {
		int count = 0;
		for (ChequeDetail cheque : ch.getChequeDetailList()) {
			if (cheque.isDelete()) {
				count++;
			}
		}

		if (count == ch.getChequeDetailList().size()) {
			return getError("30569", "All ChequeDetails Not be Deleted", "Greather than 0");
		}

		return null;
	}

	private ErrorDetail validateBranchDetails(ChequeHeader ch) {
		if (RequestSource.UPLOAD.name().equals(ch.getSourceId())) {
			for (ChequeDetail cd : ch.getChequeDetailList()) {

				ErrorDetail error = validateBranch(cd.getBankBranchID(), cd.getAccHolderName(), cd.getAccountNo());

				if (error != null) {
					return error;
				}
			}
		}

		return validateBranch(ch.getBankBranchID(), ch.getAccHolderName(), ch.getAccountNo());
	}

	private ErrorDetail validateBranch(long bankBranchID, String accountHolderName, String accountNo) {
		if (Long.valueOf(bankBranchID) == 0) {
			return getError("90502", "BankBranchID");
		}

		BankBranch bankBranch = bankBranchDAO.getBankBranchById(bankBranchID, "");

		if (bankBranch == null) {
			return getError("RU0040", "BankBranchID");
		}

		if (bankBranch.getBankBranchID() != bankBranchID) {
			return getError("RU0040", "BankBranch");
		}

		if (StringUtils.isBlank(accountHolderName)) {
			return getError("90502", "AccHolderName");
		}

		if (StringUtils.isBlank(accountNo)) {
			return getError("90502", "AccountNo");
		}

		if (accountNo.length() > 15) {
			return getError("30565", "AccountLength", "or Equal to 15");
		}

		return null;
	}

	private ErrorDetail validateChequeDetails(List<ChequeDetail> cheques) {
		for (ChequeDetail cheque : cheques) {
			ErrorDetail error = validate(cheque);

			if (error != null) {
				return error;
			}

			error = validateChequeDate(cheque);

			if (error != null) {
				return error;
			}
		}

		return null;
	}

	private ErrorDetail validate(ChequeDetail cheque) {
		if (StringUtils.isBlank(cheque.getChequeType())) {
			return getError("90502", "chequeType");
		}

		List<String> chequeType = new ArrayList<>();
		MandateUtil.getChequeTypes().forEach(c1 -> chequeType.add(c1.getValue()));

		if (!(chequeType.contains(cheque.getChequeType()))) {
			return getError("RU0040", "chequeType");
		}

		if (StringUtils.isBlank(cheque.getAccountType())) {
			return getError("90502", "accountType");
		}

		List<String> accType = new ArrayList<>();
		AccountTypes.getList().forEach(c1 -> accType.add(c1.getValue()));

		if (!(accType.contains(cheque.getAccountType()))) {
			return getError("RU0040", "accountType");
		}

		if (cheque.getAmount() == null) {
			if (chequeType.contains(cheque.getChequeType()) && "PDC".equals(cheque.getChequeType()))
				return getError("90502", "Amount");
		}

		if (chequeType.contains(cheque.getChequeType()) && "PDC".equals(cheque.getChequeType())) {
			if (cheque.getAmount().toString().length() >= 18) {
				return getError("RU0039", "chequeDate for UDC");
			}
		}

		if (InstrumentType.isPDC(cheque.getChequeType()) && cheque.getChequeDate() == null) {
			return getError("90502", "chequeDate for pdc");
		}

		if (InstrumentType.isUDC(cheque.getChequeType()) && cheque.getChequeDate() != null) {
			return getError("RU0039", "chequeDate for UDC");
		}

		return null;
	}

	private ErrorDetail validateChequeDate(ChequeDetail cheque) {
		List<Date> chequeDate = new ArrayList<>();

		if (cheque.getChequeDate() != null) {
			chequeDate.add(cheque.getChequeDate());
			int compareDate = 0;
			for (int i = 0; i < chequeDate.size(); i++) {
				Date d1 = chequeDate.get(i);
				for (int j = i; j < chequeDate.size(); j++) {
					Date d2 = chequeDate.get(j);
					if (d1.compareTo(d2) == 0) {
						compareDate++;
					}
				}
			}

			if (compareDate > chequeDate.size()) {
				return getError("90273", "Cheque Dates");
			}
		}

		return null;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setChequeHeaderDAO(ChequeHeaderDAO chequeHeaderDAO) {
		this.chequeHeaderDAO = chequeHeaderDAO;
	}

	public void setChequeDetailDAO(ChequeDetailDAO chequeDetailDAO) {
		this.chequeDetailDAO = chequeDetailDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setJointAccountDetailDAO(JointAccountDetailDAO jointAccountDetailDAO) {
		this.jointAccountDetailDAO = jointAccountDetailDAO;
	}

	public void setBankBranchDAO(BankBranchDAO bankBranchDAO) {
		this.bankBranchDAO = bankBranchDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setPrimaryAccountDAO(PrimaryAccountDAO primaryAccountDAO) {
		this.primaryAccountDAO = primaryAccountDAO;
	}

	public void setCustomerEmploymentDetailDAO(CustomerEmploymentDetailDAO customerEmploymentDetailDAO) {
		this.customerEmploymentDetailDAO = customerEmploymentDetailDAO;
	}

	public void setCustEmployeeDetailDAO(CustEmployeeDetailDAO custEmployeeDetailDAO) {
		this.custEmployeeDetailDAO = custEmployeeDetailDAO;
	}

	public void setIncomeDetailDAO(IncomeDetailDAO incomeDetailDAO) {
		this.incomeDetailDAO = incomeDetailDAO;
	}

	public void setDirectorDetailDAO(DirectorDetailDAO directorDetailDAO) {
		this.directorDetailDAO = directorDetailDAO;
	}

	public void setCustomerRatingDAO(CustomerRatingDAO customerRatingDAO) {
		this.customerRatingDAO = customerRatingDAO;
	}

	public void setCustomerPhoneNumberDAO(CustomerPhoneNumberDAO customerPhoneNumberDAO) {
		this.customerPhoneNumberDAO = customerPhoneNumberDAO;
	}

	public void setCustomerEMailDAO(CustomerEMailDAO customerEMailDAO) {
		this.customerEMailDAO = customerEMailDAO;
	}

	public void setCustomerBankInfoDAO(CustomerBankInfoDAO customerBankInfoDAO) {
		this.customerBankInfoDAO = customerBankInfoDAO;
	}

	public void setCustomerGstDetailDAO(CustomerGstDetailDAO customerGstDetailDAO) {
		this.customerGstDetailDAO = customerGstDetailDAO;
	}

	public void setCustomerChequeInfoDAO(CustomerChequeInfoDAO customerChequeInfoDAO) {
		this.customerChequeInfoDAO = customerChequeInfoDAO;
	}

	public void setExternalLiabilityDAO(ExternalLiabilityDAO externalLiabilityDAO) {
		this.externalLiabilityDAO = externalLiabilityDAO;
	}

	public void setCustomerCardSalesInfoDAO(CustomerCardSalesInfoDAO customerCardSalesInfoDAO) {
		this.customerCardSalesInfoDAO = customerCardSalesInfoDAO;
	}

	public void setCustomerExtLiabilityDAO(CustomerExtLiabilityDAO customerExtLiabilityDAO) {
		this.customerExtLiabilityDAO = customerExtLiabilityDAO;
	}

	public void setCustomerAddresDAO(CustomerAddresDAO customerAddresDAO) {
		this.customerAddresDAO = customerAddresDAO;
	}

	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Override
	public ChequeHeader getApprovedChequeHeaderForEnq(long finID) {
		ChequeHeader ch = chequeHeaderDAO.getChequeHeaderForEnq(finID);
		if (ch != null) {
			ch.setChequeDetailList(chequeDetailDAO.getChequeDetailList(ch.getHeaderID(), "_AView"));
		}
		return ch;

	}
}