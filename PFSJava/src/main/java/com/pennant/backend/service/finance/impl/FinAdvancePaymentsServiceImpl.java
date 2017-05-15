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
 * FileName    		:  FinancePurposeDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.finance.limits.LimitCheckDetails;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.payorderissue.PayOrderIssueHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.payorderissue.PayOrderIssueHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>FinancePurposeDetail</b>.<br>
 * 
 */
public class FinAdvancePaymentsServiceImpl extends GenericService<FinAdvancePayments> implements
		FinAdvancePaymentsService {
	private final static Logger		logger	= Logger.getLogger(FinAdvancePaymentsServiceImpl.class);

	private AuditHeaderDAO			auditHeaderDAO;

	private FinAdvancePaymentsDAO	finAdvancePaymentsDAO;
	private PayOrderIssueHeaderDAO	payOrderIssueHeaderDAO;
	private LimitCheckDetails		limitCheckDetails;

	public FinAdvancePaymentsServiceImpl() {
		super();
	}

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO
	 *            the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the finAdvancePaymentsDAO
	 */
	public FinAdvancePaymentsDAO getFinAdvancePaymentsDAO() {
		return finAdvancePaymentsDAO;
	}

	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	@Override
	public List<FinAdvancePayments> getFinAdvancePaymentsById(String id, String type) {
		logger.debug("Entering");
		List<FinAdvancePayments> finAdvancePayments = getFinAdvancePaymentsDAO()
				.getFinAdvancePaymentsByFinRef(id, type);
		logger.debug("Leaving");
		return finAdvancePayments;
	}

	@Override
	public List<AuditDetail> saveOrUpdate(List<FinAdvancePayments> finAdvancePayments, String tableType,
			String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditDetails.addAll(processFinAdvancePaymentDetails(finAdvancePayments, tableType, auditTranType, false));

		logger.debug("Leaving");
		return auditDetails;
	}

	private List<AuditDetail> processFinAdvancePaymentDetails(List<FinAdvancePayments> finAdvancePayments,
			String tableType, String auditTranType, boolean isApproveRcd) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (finAdvancePayments != null && !finAdvancePayments.isEmpty()) {
			int i = 0;
			boolean saveRecord = false;
			boolean updateRecord = false;
			boolean deleteRecord = false;
			boolean approveRec = false;

			for (FinAdvancePayments finPayment : finAdvancePayments) {
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = isApproveRcd;
				String rcdType = "";
				String recordStatus = "";
				if (finPayment.ispOIssued() || StringUtils.isEmpty(finPayment.getRecordType())) {
					continue;
				}

				if (StringUtils.isEmpty(tableType)
						|| StringUtils.equals(tableType, PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
					approveRec = true;
					finPayment.setRoleCode("");
					finPayment.setNextRoleCode("");
					finPayment.setTaskId("");
					finPayment.setNextTaskId("");
				}

				finPayment.setWorkflowId(0);
				if (finPayment.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
				} else if (finPayment.isNewRecord()) {
					saveRecord = true;
					if (finPayment.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						finPayment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (finPayment.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						finPayment.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (finPayment.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						finPayment.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (finPayment.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (finPayment.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (finPayment.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					if (approveRec) {
						deleteRecord = true;
					} else if (finPayment.isNew()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}

				if (approveRec) {
					rcdType = finPayment.getRecordType();
					recordStatus = finPayment.getRecordStatus();
					finPayment.setRecordType("");
					finPayment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					finPayment.setStatus(DisbursementConstants.STATUS_APPROVED);
					finPayment.setpOIssued(true);
				}
				if (saveRecord) {
					getFinAdvancePaymentsDAO().save(finPayment, tableType);
				}

				if (updateRecord) {

					getFinAdvancePaymentsDAO().update(finPayment, tableType);
				}

				if (deleteRecord) {
					getFinAdvancePaymentsDAO().delete(finPayment, tableType);
				}

				if (approveRec) {
					finPayment.setRecordType(rcdType);
					finPayment.setRecordStatus(recordStatus);
				}

				String[] fields = PennantJavaUtil.getFieldDetails(finPayment, finPayment.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finPayment.getBefImage(),
						finPayment));
				i++;
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> doApprove(List<FinAdvancePayments> finAdvancePayments, String tableType,
			String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditDetails.addAll(processFinAdvancePaymentDetails(finAdvancePayments, tableType, auditTranType, true));

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> delete(List<FinAdvancePayments> finAdvancePayments, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = null;

		if (finAdvancePayments != null && !finAdvancePayments.isEmpty()) {
			int auditSeq = 1;
			for (FinAdvancePayments finPayment : finAdvancePayments) {
				getFinAdvancePaymentsDAO().delete(finPayment, tableType);
				fields = PennantJavaUtil.getFieldDetails(finPayment, finPayment.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditSeq, fields[0], fields[1], finPayment
						.getBefImage(), finPayment));
				auditSeq++;
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	private List<AuditDetail> getAdvancePaymentAuditDetail(List<FinAdvancePayments> finAdvancePayments,
			String auditTranType, String method, long workFlowId) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = null;
		for (FinAdvancePayments finAdvancePay : finAdvancePayments) {

			if (StringUtils.isEmpty(finAdvancePay.getRecordType())) {
				continue;
			}

			if ("doApprove".equals(method)
					&& !StringUtils.trimToEmpty(finAdvancePay.getRecordStatus()).equals(
							PennantConstants.RCD_STATUS_SAVED)) {
				finAdvancePay.setWorkflowId(0);
				finAdvancePay.setNewRecord(true);
			} else {
				finAdvancePay.setWorkflowId(workFlowId);
			}

			boolean isRcdType = false;

			if (finAdvancePay.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finAdvancePay.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finAdvancePay.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finAdvancePay.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (finAdvancePay.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finAdvancePay.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				finAdvancePay.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finAdvancePay.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finAdvancePay.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finAdvancePay.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			fields = PennantJavaUtil.getFieldDetails(finAdvancePay, finAdvancePay.getExcludeFields());
			if (StringUtils.isNotEmpty(finAdvancePay.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						finAdvancePay.getBefImage(), finAdvancePay));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> validate(List<FinAdvancePayments> finAdvancePayments, long workflowId, String method,
			String auditTranType, String usrLanguage) {
		return doValidation(finAdvancePayments, workflowId, method, auditTranType, usrLanguage);
	}

	private List<AuditDetail> doValidation(List<FinAdvancePayments> finAdvancePayments, long workflowId, String method,
			String auditTranType, String usrLanguage) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (finAdvancePayments != null && !finAdvancePayments.isEmpty()) {
			List<AuditDetail> advancePayAuditDetails = getAdvancePaymentAuditDetail(finAdvancePayments, auditTranType,
					method, workflowId);
			for (AuditDetail auditDetail : advancePayAuditDetails) {
				validateAdvancePayment(auditDetail, method, usrLanguage);
			}
			auditDetails.addAll(advancePayAuditDetails);
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	private AuditDetail validateAdvancePayment(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		FinAdvancePayments finAdvancePay = (FinAdvancePayments) auditDetail.getModelData();
		FinAdvancePayments tempFinAdvancePay = null;
		if (finAdvancePay.isWorkflow()) {
			tempFinAdvancePay = getFinAdvancePaymentsDAO().getFinAdvancePaymentsById(finAdvancePay, "_Temp");
		}
		FinAdvancePayments befFinAdvancePay = getFinAdvancePaymentsDAO().getFinAdvancePaymentsById(finAdvancePay, "");
		FinAdvancePayments oldFinAdvancePay = finAdvancePay.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = finAdvancePay.getFinReference();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (finAdvancePay.isNew()) { // for New record or new record into work flow

			if (!finAdvancePay.isWorkflow()) {// With out Work flow only new records  
				if (befFinAdvancePay != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (finAdvancePay.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFinAdvancePay != null || tempFinAdvancePay != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinAdvancePay == null || tempFinAdvancePay != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finAdvancePay.isWorkflow()) { // With out Work flow for update and delete

				if (befFinAdvancePay == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinAdvancePay != null
							&& !oldFinAdvancePay.getLastMntOn().equals(befFinAdvancePay.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41003", errParm, valueParm), usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41004", errParm, valueParm), usrLanguage));
						}
					}
				}
			} else {

				if (tempFinAdvancePay == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}

				if (tempFinAdvancePay != null && oldFinAdvancePay != null
						&& !oldFinAdvancePay.getLastMntOn().equals(tempFinAdvancePay.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finAdvancePay.isWorkflow()) {
			auditDetail.setBefImage(befFinAdvancePay);
		}
		return auditDetail;
	}

	@Override
	public void processDisbursments(FinanceDetail financeDetail) {
		logger.debug("Entering");

		List<FinAdvancePayments> finAdvancePayList = financeDetail.getAdvancePaymentsList();

		if (finAdvancePayList == null || finAdvancePayList.isEmpty()) {
			return;
		}

		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		boolean save = false;
		PayOrderIssueHeader payOrderIssueHeader = getPayOrderIssueHeaderDAO().getPayOrderIssueByHeaderRef(
				finMain.getFinReference(), "");
		if (payOrderIssueHeader == null) {
			save = true;
			payOrderIssueHeader = new PayOrderIssueHeader();
			payOrderIssueHeader.setFinReference(finMain.getFinReference());
			payOrderIssueHeader.setVersion(1);
			payOrderIssueHeader.setLastMntBy(finMain.getLastMntBy());
			payOrderIssueHeader.setLastMntOn(finMain.getLastMntOn());
			payOrderIssueHeader.setRoleCode("");
			payOrderIssueHeader.setNextRoleCode("");
			payOrderIssueHeader.setTaskId("");
			payOrderIssueHeader.setNextTaskId("");
			payOrderIssueHeader.setRecordType("");
			payOrderIssueHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		}
		//get total amount from disbursement details
		BigDecimal totPOAmount = BigDecimal.ZERO;
		FinScheduleData schd = financeDetail.getFinScheduleData();
		if (schd != null && schd.getDisbursementDetails() != null) {
			for (FinanceDisbursement disbursement : schd.getDisbursementDetails()) {
				totPOAmount = totPOAmount.add(disbursement.getDisbAmount());
			}
		}

		BigDecimal totPOdueAmt = BigDecimal.ZERO;
		int totpoCount = 0;
		int totdueCount = 0;

		for (FinAdvancePayments finAdvancePayment : finAdvancePayList) {
			if (!finAdvancePayment.ispOIssued()) {
				totPOdueAmt = totPOdueAmt.add(finAdvancePayment.getAmtToBeReleased());
				totdueCount++;
			}

			totpoCount++;
		}

		payOrderIssueHeader.setTotalPOCount(totpoCount);
		payOrderIssueHeader.setpODueCount(totdueCount);
		payOrderIssueHeader.setTotalPOAmount(totPOAmount);
		payOrderIssueHeader.setpODueAmount(totPOdueAmt);
		if (save) {
			getPayOrderIssueHeaderDAO().save(payOrderIssueHeader, "");
		} else {
			payOrderIssueHeader.setVersion(payOrderIssueHeader.getVersion() + 1);
			getPayOrderIssueHeaderDAO().update(payOrderIssueHeader, "");
		}

		logger.debug("Leaving");
	}

	@Override
	public List<AuditDetail> processQuickDisbursment(FinanceDetail financeDetail, String tableType, String auditTranType) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		List<FinAdvancePayments> finAdvancePayList = financeDetail.getAdvancePaymentsList();
		if (finAdvancePayList == null || finAdvancePayList.isEmpty()) {
			return auditDetails;
		}

		FinanceMain finmain = financeDetail.getFinScheduleData().getFinanceMain();
		String nextrole = finmain.getNextRoleCode();
		String role = finmain.getRoleCode();
		boolean process = false;

		if (!financeDetail.isActionSave() && !StringUtils.equals(nextrole, role)) {
			// Checking Authority i.e Is current Role contains authority (or) Not
			List<FinanceReferenceDetail> limitCheckList = getLimitCheckDetails()
					.doLimitChek(role, finmain.getFinType());

			if (limitCheckList != null && !limitCheckList.isEmpty()) {

				for (FinanceReferenceDetail finRefDetail : limitCheckList) {
					if (StringUtils.equals(finRefDetail.getLovDescNamelov(), FinanceConstants.QUICK_DISBURSEMENT)) {
						process = true;
						break;
					}
				}
			}
		}

		if (process) {
			processDisbursments(financeDetail);
			auditDetails.addAll(doApprove(finAdvancePayList, "", PennantConstants.TRAN_ADD));
			delete(financeDetail.getAdvancePaymentsList(), "_Temp", "");
		} else {
			auditDetails.addAll(saveOrUpdate(finAdvancePayList, tableType, auditTranType));
		}

		return auditDetails;
	}

	@Override
	public void doCancel(FinanceDetail financeDetail) {
		FinScheduleData schdata = financeDetail.getFinScheduleData();
		FinanceMain finMain = schdata.getFinanceMain();
		List<FinanceDisbursement> list = schdata.getDisbursementDetails();
		List<FinanceDisbursement> canceldDisbList = new ArrayList<FinanceDisbursement>();
		for (FinanceDisbursement financeDisbursement : list) {
			if (StringUtils.equals(financeDisbursement.getDisbStatus(), FinanceConstants.DISB_STATUS_CANCEL)) {
				canceldDisbList.add(financeDisbursement);
			}
		}

		for (FinanceDisbursement financeDisbursement : canceldDisbList) {
			FinAdvancePayments advancePayments = new FinAdvancePayments();
			advancePayments.setFinReference(finMain.getFinReference());
			advancePayments.setDisbSeq(financeDisbursement.getDisbSeq());
			advancePayments.setStatus(DisbursementConstants.STATUS_CANCEL);
			getFinAdvancePaymentsDAO().updateStatus(advancePayments, "");
		}

	}

	public List<ErrorDetails> validateFinAdvPayments(List<FinAdvancePayments> list, List<FinanceDisbursement> financeDisbursement, 
			FinanceMain financeMain, boolean loanApproved) {
		logger.debug(" Entering ");

		FinanceDisbursement totDisb = getTotal(financeDisbursement, financeMain, 0, false);

		BigDecimal netFinAmount = totDisb.getDisbAmount();
		List<ErrorDetails> errorList = new ArrayList<ErrorDetails>();
		boolean checkMode = true;

		BigDecimal totDisbAmt = BigDecimal.ZERO;
		Map<Integer, BigDecimal> map = new HashMap<Integer, BigDecimal>();

		if (financeMain.isQuickDisb() && financeDisbursement != null && financeDisbursement.size() > 1) {
			//Multiple disbursement not allowed in quick disbursement
			ErrorDetails error = new ErrorDetails("60405", null);
			errorList.add(error);
			return errorList;
		}

		for (FinAdvancePayments finAdvPayment : list) {
			if (financeMain.isQuickDisb() && checkMode) {
				if (!StringUtils.equals(finAdvPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_CHEQUE)) {
					checkMode = false;
				}
			}

			if (!isDeleteRecord(finAdvPayment)) {
				int key = finAdvPayment.getDisbSeq();

				BigDecimal totalGroupAmt = map.get(key);
				if (totalGroupAmt == null) {
					totalGroupAmt = BigDecimal.ZERO;
				}
				totalGroupAmt = totalGroupAmt.add(finAdvPayment.getAmtToBeReleased());
				map.put(key, totalGroupAmt);
				totDisbAmt = totDisbAmt.add(finAdvPayment.getAmtToBeReleased());
			}
		}

		if (netFinAmount.compareTo(totDisbAmt) != 0) {
			//since if the loan not approved then user can cancel the instruction and resubmit the record in loan origination
			if (loanApproved) {
				//Total amount should match with disbursement amount.
				String[] valueParm = new String[2];
				valueParm[0] = String.valueOf(totDisbAmt);
				valueParm[1] = String.valueOf(netFinAmount);
				ErrorDetails error = new ErrorDetails("60401", valueParm);
				errorList.add(error);
				return errorList;
			}
		}

		if (!checkMode) {
			//For quick disbursement, only payment type cheque is allowed.
			ErrorDetails error = new ErrorDetails("60402", null);
			errorList.add(error);
			return errorList;
		}

		//since if the loan not approved then user can cancel the instruction and resubmit the record in loan origination
		if (loanApproved) {
			for (FinanceDisbursement disbursement : financeDisbursement) {

				if(StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, disbursement.getDisbStatus())){
					continue;
				}

				Date disbDate = disbursement.getDisbDate();
				BigDecimal singletDisbursment = disbursement.getDisbAmount();

				if (disbDate.compareTo(financeMain.getFinStartDate()) == 0) {
					singletDisbursment = singletDisbursment.subtract(financeMain.getDownPayment());
					singletDisbursment = singletDisbursment.subtract(financeMain.getDeductFeeDisb());
					singletDisbursment = singletDisbursment.subtract(financeMain.getDeductInsDisb());
					if (StringUtils.trimToEmpty(financeMain.getBpiTreatment()).equals(FinanceConstants.BPI_DISBURSMENT)) {
						singletDisbursment = singletDisbursment.subtract(financeMain.getBpiAmount());
					}
				}
				int key = disbursement.getDisbSeq();

				BigDecimal totalGroupAmt = map.get(key);
				if (totalGroupAmt == null) {
					totalGroupAmt = BigDecimal.ZERO;
				}
				if (singletDisbursment.compareTo(totalGroupAmt) != 0) {
					String errorDesc = DateUtility.formatToLongDate(disbDate) + " , " + disbursement.getDisbSeq();
					//Total amount should match with disbursement amount dated :{0}.
					ErrorDetails error = new ErrorDetails("60404", new String[] { errorDesc });
					errorList.add(error);
					return errorList;
				}
			}
		}

		return errorList;

	}
	
	private static FinanceDisbursement getTotal(List<FinanceDisbursement> list, FinanceMain main, int seq, boolean group) {
		BigDecimal totdisbAmt = BigDecimal.ZERO;
		Date date = null;
		if (list != null && !list.isEmpty()) {
			for (FinanceDisbursement financeDisbursement : list) {
				if (group && seq != financeDisbursement.getDisbSeq()) {
					continue;
				}
				
				if (!group) {
					if(StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, financeDisbursement.getDisbStatus())){
						continue;
					}
				}
				
				date = financeDisbursement.getDisbDate();
				
				//check is first disbursement
				if (financeDisbursement.getDisbDate().getTime() == main.getFinStartDate().getTime()) {
					totdisbAmt = totdisbAmt.subtract(main.getDownPayment());
					totdisbAmt = totdisbAmt.subtract(main.getDeductFeeDisb());
					totdisbAmt = totdisbAmt.subtract(main.getDeductInsDisb());
					if (StringUtils.trimToEmpty(main.getBpiTreatment()).equals(FinanceConstants.BPI_DISBURSMENT)) {
						totdisbAmt = totdisbAmt.subtract(main.getBpiAmount());
					}
				}
				totdisbAmt = totdisbAmt.add(financeDisbursement.getDisbAmount());
			}
		}

		FinanceDisbursement disbursement = new FinanceDisbursement();
		disbursement.setDisbAmount(totdisbAmt);
		if (date != null) {
			disbursement.setDisbDate(date);
		}
		return disbursement;

	}
	
	private boolean isDeleteRecord(FinAdvancePayments aFinAdvancePayments) {
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, aFinAdvancePayments.getRecordType())
				|| StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, aFinAdvancePayments.getRecordType())
				|| StringUtils.equals(DisbursementConstants.STATUS_CANCEL, aFinAdvancePayments.getStatus())) {
			return true;
		}
		return false;
	}
	
	public PayOrderIssueHeaderDAO getPayOrderIssueHeaderDAO() {
		return payOrderIssueHeaderDAO;
	}

	public void setPayOrderIssueHeaderDAO(PayOrderIssueHeaderDAO payOrderIssueHeaderDAO) {
		this.payOrderIssueHeaderDAO = payOrderIssueHeaderDAO;
	}

	public LimitCheckDetails getLimitCheckDetails() {
		return limitCheckDetails;
	}

	public void setLimitCheckDetails(LimitCheckDetails limitCheckDetails) {
		this.limitCheckDetails = limitCheckDetails;
	}

	@Override
	public void Update(long paymentId, long linkedTranId) {
		finAdvancePaymentsDAO.update(paymentId, linkedTranId);
		
		
	}

	@Override
	public int getCountByFinReference(String finReference) {
		return finAdvancePaymentsDAO.getCountByFinReference(finReference);
	}

}