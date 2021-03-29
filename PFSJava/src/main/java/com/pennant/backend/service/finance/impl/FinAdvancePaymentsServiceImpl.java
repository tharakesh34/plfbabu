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
 * FileName    		:  FinAdvancePaymentsServiceImpl.java                                                   * 	  
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
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.finance.limits.LimitCheckDetails;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.configuration.VASConfigurationDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.payorderissue.PayOrderIssueHeaderDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.dao.systemmasters.VASProviderAccDetailDAO;
import com.pennant.backend.model.applicationmaster.InstrumentwiseLimit;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.PaymentTransaction;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.payorderissue.PayOrderIssueHeader;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.systemmasters.VASProviderAccDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.InstrumentwiseLimitService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.finance.covenant.CovenantsService;
import com.pennant.backend.service.partnerbank.PartnerBankService;
import com.pennant.backend.service.payment.PaymentsProcessService;
import com.pennant.backend.service.payorderissue.impl.DisbursementPostings;
import com.pennant.backend.service.pennydrop.PennyDropService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.external.BankAccountValidationService;

/**
 * Service implementation for methods that depends on <b>FinancePurposeDetail</b>.<br>
 * 
 */
public class FinAdvancePaymentsServiceImpl extends GenericService<FinAdvancePayments>
		implements FinAdvancePaymentsService {
	private static final Logger logger = LogManager.getLogger(FinAdvancePaymentsServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PostingsDAO postingsDAO;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private PayOrderIssueHeaderDAO payOrderIssueHeaderDAO;
	private LimitCheckDetails limitCheckDetails;
	private PartnerBankService partnerBankService;
	private MandateDAO mandateDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private CovenantsService covenantsService;
	private transient InstrumentwiseLimitService instrumentwiseLimitService;
	protected FinanceDisbursementDAO financeDisbursementDAO;
	private transient DisbursementPostings disbursementPostings;
	private PaymentsProcessService paymentsProcessService;
	private transient PennyDropService pennyDropService;
	@Autowired(required = false)
	private transient BankAccountValidationService bankAccountValidationService;
	private DocumentDetailsDAO documentDetailsDAO;

	private VASProviderAccDetailDAO vASProviderAccDetailDAO;
	private VASConfigurationDAO vASConfigurationDAO;

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

	/**
	 * Fetch FinAdvancePayments details by finReference
	 * 
	 * @param finReference
	 * 
	 * @return List<FinAdvancePayments>
	 */

	@Override
	public List<FinAdvancePayments> getFinAdvancePaymentByFinRef(String finRefernce) {
		return finAdvancePaymentsDAO.getFinAdvancePaymentsByFinRef(finRefernce, "");
	}

	@Override
	public List<FinAdvancePayments> getFinAdvancePaymentsById(String id, String type) {
		return finAdvancePaymentsDAO.getFinAdvancePaymentsByFinRef(id, type);
	}

	@Override
	public List<AuditDetail> saveOrUpdate(List<FinAdvancePayments> finAdvancePayments, String tableType,
			String auditTranType, boolean disbStp) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditDetails
				.addAll(processFinAdvancePaymentDetails(finAdvancePayments, tableType, auditTranType, false, disbStp));

		logger.debug("Leaving");
		return auditDetails;
	}

	private List<AuditDetail> processFinAdvancePaymentDetails(List<FinAdvancePayments> finAdvancePayments,
			String tableType, String auditTranType, boolean isApproveRcd, boolean disbStp) {
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
					if (!StringUtils.equals(finPayment.getStatus(), DisbursementConstants.STATUS_AWAITCON)) {
						finPayment.setStatus(DisbursementConstants.STATUS_APPROVED);
					}
					finPayment.setpOIssued(true);
				}

				if (finPayment.isHoldDisbursement()) {
					finPayment.setStatus(DisbursementConstants.STATUS_HOLD);
				}

				if (disbStp) {
					finPayment.setStatus(DisbursementConstants.STATUS_AWAITCON);
				}

				if (saveRecord) {
					if (approveRec) {
						finPayment.setOnlineProcReq(true);
					}
					getFinAdvancePaymentsDAO().save(finPayment, tableType);
					if (finPayment.getDocImage() != null) {
						saveDocumentDetails(finPayment);
					}
				}

				if (updateRecord) {
					getFinAdvancePaymentsDAO().update(finPayment, tableType);
					if (finPayment.getDocImage() != null) {
						saveDocumentDetails(finPayment);
					}
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
			String auditTranType, boolean disbStp) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditDetails
				.addAll(processFinAdvancePaymentDetails(finAdvancePayments, tableType, auditTranType, true, disbStp));

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> delete(List<FinAdvancePayments> finAdvancePayments, String tableType,
			String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = null;

		if (finAdvancePayments != null && !finAdvancePayments.isEmpty()) {
			int auditSeq = 1;
			for (FinAdvancePayments finPayment : finAdvancePayments) {
				getFinAdvancePaymentsDAO().delete(finPayment, tableType);
				fields = PennantJavaUtil.getFieldDetails(finPayment, finPayment.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditSeq, fields[0], fields[1],
						finPayment.getBefImage(), finPayment));
				auditSeq++;
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<ReturnDataSet> getPostingsByLinkedTranId(List<Long> tranIdList, String finReference) {
		return postingsDAO.getPostingsByLinkTransId(tranIdList, finReference);
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

			if ("doApprove".equals(method) && !StringUtils.trimToEmpty(finAdvancePay.getRecordStatus())
					.equals(PennantConstants.RCD_STATUS_SAVED)) {
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
			String auditTranType, String usrLanguage, FinanceDetail financeDetail, boolean isApi) {
		return doValidation(finAdvancePayments, workflowId, method, auditTranType, usrLanguage, financeDetail, isApi);
	}

	private List<AuditDetail> doValidation(List<FinAdvancePayments> finAdvancePayments, long workflowId, String method,
			String auditTranType, String usrLanguage, FinanceDetail financeDetail, boolean isApi) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (finAdvancePayments != null && !finAdvancePayments.isEmpty()) {
			List<AuditDetail> advancePayAuditDetails = getAdvancePaymentAuditDetail(finAdvancePayments, auditTranType,
					method, workflowId);
			for (AuditDetail auditDetail : advancePayAuditDetails) {
				validateAdvancePayment(auditDetail, method, usrLanguage, financeDetail, isApi);
			}
			auditDetails.addAll(advancePayAuditDetails);
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * split the each transaction based on the Imps Maximum amount
	 * 
	 * @param finAdvancePayments
	 * @param instrumentwiseLimit
	 * @return
	 */
	@Override
	public List<FinAdvancePayments> splitRequest(List<FinAdvancePayments> finAdvancePayments) {
		logger.debug("Entering");

		List<FinAdvancePayments> finalPaymentsList = new ArrayList<FinAdvancePayments>();
		InstrumentwiseLimit instrumentwiseLimit = getInstrumentwiseLimitService()
				.getInstrumentWiseModeLimit(DisbursementConstants.PAYMENT_TYPE_NEFT);

		if (instrumentwiseLimit != null) {

			int seqNo = getNextPaymentSequence(finAdvancePayments);

			for (FinAdvancePayments finAdvancePay : finAdvancePayments) {

				if (!PennantConstants.RECORD_TYPE_DEL.equals(finAdvancePay.getRecordType())
						&& !PennantConstants.RECORD_TYPE_CAN.equals(finAdvancePay.getRecordType())
						&& PennantConstants.RECORD_TYPE_NEW.equals(finAdvancePay.getRecordType())
						&& DisbursementConstants.PAYMENT_TYPE_NEFT.equals(finAdvancePay.getPaymentType())
						&& !DisbursementConstants.STATUS_AWAITCON.equals(finAdvancePay.getStatus())
						&& !DisbursementConstants.STATUS_PAID.equals(finAdvancePay.getStatus())
						&& !DisbursementConstants.STATUS_REALIZED.equals(finAdvancePay.getStatus())
						&& !DisbursementConstants.STATUS_REJECTED.equals(finAdvancePay.getStatus())
						&& !DisbursementConstants.STATUS_CANCEL.equals(finAdvancePay.getStatus())) {

					BigDecimal maxAmtPerInstruction = instrumentwiseLimit.getMaxAmtPerInstruction();
					if (finAdvancePay.getAmtToBeReleased().compareTo(maxAmtPerInstruction) > 0) {

						if (BigDecimal.ZERO.compareTo(maxAmtPerInstruction) == 0) {
							throw new InterfaceException("NEFT",
									"For NEFT requests Max Amount Per Instruction should be greater than zero.");
						}

						BigDecimal noOfRecords = finAdvancePay.getAmtToBeReleased().divide(maxAmtPerInstruction, 0,
								RoundingMode.UP);
						int records = noOfRecords.intValueExact();
						BigDecimal totAmount = BigDecimal.ZERO;

						for (int i = 1; i <= records; i++) {
							FinAdvancePayments finAdvPay = new FinAdvancePayments();
							BeanUtils.copyProperties(finAdvancePay, finAdvPay);
							finAdvPay.setPaymentSeq(seqNo);
							if (records == i) {
								finAdvPay.setAmtToBeReleased(finAdvancePay.getAmtToBeReleased().subtract(totAmount));
							} else {
								finAdvPay.setAmtToBeReleased(maxAmtPerInstruction);
							}
							finAdvPay.setPaymentId(Long.MIN_VALUE);
							finAdvPay.setNewRecord(true);
							finAdvPay.setLLDate(SysParamUtil.getAppDate());
							totAmount = totAmount.add(maxAmtPerInstruction);
							seqNo++;
							finalPaymentsList.add(finAdvPay);
						}
					} else {
						finAdvancePay.setLLDate(SysParamUtil.getAppDate());
						finalPaymentsList.add(finAdvancePay);
					}
				} else {
					finalPaymentsList.add(finAdvancePay);
				}
			}
		} else {
			finalPaymentsList.addAll(finAdvancePayments);
		}

		logger.debug("Leaving");

		return finalPaymentsList;
	}

	/**
	 * get the Next Payment Sequence No
	 * 
	 * @param finAdvancePayDetails
	 * @return
	 */
	private int getNextPaymentSequence(List<FinAdvancePayments> finAdvancePayDetails) {
		int seqNo = 0;
		if (finAdvancePayDetails != null && !finAdvancePayDetails.isEmpty()) {
			for (FinAdvancePayments advancePayments : finAdvancePayDetails) {
				int tempId = advancePayments.getPaymentSeq();
				if (tempId > seqNo) {
					seqNo = tempId;
				}
			}
		}
		return seqNo + 1;
	}

	private AuditDetail validateAdvancePayment(AuditDetail auditDetail, String usrLanguage, String method,
			FinanceDetail financeDetail, boolean isApi) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinAdvancePayments finAdvancePay = (FinAdvancePayments) auditDetail.getModelData();
		FinAdvancePayments tempFinAdvancePay = null;
		boolean isOverDraft = false;
		int maxODDays = SysParamUtil.getValueAsInt(SMTParameterConstants.MAX_ODDAYS_ADDDISB);

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				financeDetail.getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverDraft = true;
		}
		if (finAdvancePay.isWorkflow()) {
			tempFinAdvancePay = getFinAdvancePaymentsDAO().getFinAdvancePaymentsById(finAdvancePay, "_Temp");
		}
		FinAdvancePayments befFinAdvancePay = getFinAdvancePaymentsDAO().getFinAdvancePaymentsById(finAdvancePay, "");
		FinAdvancePayments oldFinAdvancePay = finAdvancePay.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(finAdvancePay.getPaymentId());
		errParm[0] = PennantJavaUtil.getLabel("label_PaymentId") + ": " + valueParm[0];

		if (finAdvancePay.isNew()) { // for New record or new record into work flow

			if (!finAdvancePay.isWorkflow()) {// With out Work flow only new records  
				if (befFinAdvancePay != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (finAdvancePay.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFinAdvancePay != null || tempFinAdvancePay != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinAdvancePay == null || tempFinAdvancePay != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finAdvancePay.isWorkflow()) { // With out Work flow for update and delete

				if (befFinAdvancePay == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinAdvancePay != null
							&& !oldFinAdvancePay.getLastMntOn().equals(befFinAdvancePay.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				if (tempFinAdvancePay == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempFinAdvancePay != null && oldFinAdvancePay != null
						&& !oldFinAdvancePay.getLastMntOn().equals(tempFinAdvancePay.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		//validation related to Scheduled Disbursement date And Disbursement date should be same.
		boolean noValidation = isnoValidationUserAction(financeDetail.getUserAction());
		if (!noValidation && !isDeleteRecord(finAdvancePay) && !finAdvancePay.ispOIssued()) {
			List<FinanceDisbursement> finDisbursementDetails = financeDetail.getFinScheduleData()
					.getDisbursementDetails();
			for (FinanceDisbursement finDisbursmentDetail : finDisbursementDetails) {
				if (finAdvancePay.getDisbSeq() == finDisbursmentDetail.getDisbSeq() && finAdvancePay.getLlDate() != null
						&& DateUtility.compare(finDisbursmentDetail.getDisbDate(), finAdvancePay.getLlDate()) != 0) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "65032", errParm, valueParm), usrLanguage));
				}
			}

			String partnerBankBankcode = partnerBankService.getBankCodeById(finAdvancePay.getPartnerBankID());
			if (ImplementationConstants.VALIDATE_BENFICIARY_ACCOUNT && bankAccountValidationService != null) {
				int count = pennyDropService.getPennyDropCount(finAdvancePay.getBeneficiaryAccNo(),
						finAdvancePay.getiFSC());

				if (count == 0) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41020", errParm, valueParm), usrLanguage));
				}
			}

			if (!StringUtils.equals(finAdvancePay.getBranchBankCode(), partnerBankBankcode)) {
				if (StringUtils.equals(finAdvancePay.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_IFT)) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "65033", errParm, valueParm), usrLanguage));
				}
			} else if (!StringUtils.equals(finAdvancePay.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_IFT)
					&& !isApi) {
				String[] errParam = new String[1];
				errParam[0] = finAdvancePay.getPaymentType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "65038", errParam, valueParm), usrLanguage));
			}

		}

		// for Overdraft loan types checking for mandate is approved or not. and validate for Expiry date.
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		if (isOverDraft && StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ADDDISB)
				&& financeDetail.getFinScheduleData().getFinanceMain().getMandateID() != 0) {

			Mandate mandate = new Mandate();
			long mandateID = financeDetail.getFinScheduleData().getFinanceMain().getMandateID();

			mandate = getMandateDAO().getMandateStatusById(finReference, mandateID);
			if (mandate != null && !StringUtils.isEmpty(mandate.getStatus())
					&& !StringUtils.equals(MandateConstants.STATUS_APPROVED, mandate.getStatus())) {
				String[] errParam = new String[1];
				errParam[0] = String.valueOf(mandateID);
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "ADM001", errParam, valueParm), usrLanguage));
			}

			if (mandate != null && StringUtils.equals(MandateConstants.STATUS_APPROVED, mandate.getStatus())
					&& mandate.getExpiryDate() != null
					&& DateUtility.compare(SysParamUtil.getAppDate(), mandate.getExpiryDate()) > 0) {
				String[] errParam = new String[1];
				errParam[0] = String.valueOf(mandateID);
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "ADM002", errParam, valueParm), usrLanguage));
			}
		}

		//Validation for Automatic Blocking of Add Disbursement after N due days from Overdue schedules. 
		if (isOverDraft) {
			int currODDays = getProfitDetailsDAO().getCurOddays(finReference, "");
			if (currODDays != 0 && currODDays > maxODDays) {
				String[] errParam = new String[2];
				errParam[0] = finReference;
				errParam[1] = String.valueOf(maxODDays);
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "ADM003", errParam, valueParm), usrLanguage));
			}
		}

		if (covenantsService != null) {
			auditDetail.setErrorDetails(
					covenantsService.validatePDDDocuments(finReference, auditDetail.getErrorDetails()));
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
		PayOrderIssueHeader payOrderIssueHeader = getPayOrderIssueHeaderDAO()
				.getPayOrderIssueByHeaderRef(finMain.getFinReference(), "");
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
				if (disbursement.getLinkedDisbId() != 0) {
					continue;
				}
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
	public List<AuditDetail> processQuickDisbursment(FinanceDetail financeDetail, String tableType,
			String auditTranType) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		List<FinAdvancePayments> finAdvancePayList = financeDetail.getAdvancePaymentsList();
		if (finAdvancePayList == null || finAdvancePayList.isEmpty()) {
			return auditDetails;
		}

		if (isQDPProcess(financeDetail)
				|| StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ADDDISB)) {
			processDisbursments(financeDetail);
			// Postings preparation
			if (!SysParamUtil.isAllowed(SMTParameterConstants.HOLD_DISB_INST_POST)) {
				if (!ImplementationConstants.ALW_QDP_CUSTOMIZATION) {
					generateAccounting(financeDetail);
				}
			}
			auditDetails.addAll(doApprove(finAdvancePayList, "", PennantConstants.TRAN_ADD, financeDetail.isDisbStp()));
			delete(financeDetail.getAdvancePaymentsList(), "_Temp", "");
			return auditDetails;
		} else if (ImplementationConstants.ALW_QDP_CUSTOMIZATION
				&& StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ORG)
				&& StringUtils.equals(financeDetail.getFinScheduleData().getFinanceMain().getRecordStatus(),
						PennantConstants.RCD_STATUS_APPROVED)
				&& financeDetail.getFinScheduleData().getFinanceMain().isQuickDisb()) {
			for (FinAdvancePayments finAdPayment : finAdvancePayList) {
				int i = 0;
				if (finAdPayment.getStatus().equalsIgnoreCase(DisbursementConstants.STATUS_PAID)) {
					if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(finAdPayment.getPaymentType())
							|| DisbursementConstants.PAYMENT_TYPE_DD.equals(finAdPayment.getPaymentType())) {
						finAdvancePaymentsDAO.updateLLDate(finAdPayment, "");
						String[] fields = PennantJavaUtil.getFieldDetails(finAdPayment,
								finAdPayment.getExcludeFields());
						auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
								finAdPayment.getBefImage(), finAdPayment));
					}
				}
			}
			return auditDetails;
		} else {
			auditDetails.addAll(saveOrUpdate(finAdvancePayList, tableType, auditTranType, financeDetail.isDisbStp()));
		}

		return auditDetails;
	}

	private boolean isQDPProcess(FinanceDetail financeDetail) {
		FinanceMain finmain = financeDetail.getFinScheduleData().getFinanceMain();
		String nextrole = finmain.getNextRoleCode();
		String role = finmain.getRoleCode();
		boolean process = false;

		if (!financeDetail.isActionSave() && !StringUtils.equals(nextrole, role)) {
			// Checking Authority i.e Is current Role contains authority (or) Not
			List<FinanceReferenceDetail> limitCheckList = getLimitCheckDetails().doLimitChek(role,
					finmain.getFinType());

			if (CollectionUtils.isNotEmpty(limitCheckList)) {
				for (FinanceReferenceDetail finRefDetail : limitCheckList) {
					if (StringUtils.equals(finRefDetail.getLovDescNamelov(), FinanceConstants.QUICK_DISBURSEMENT)) {
						process = true;
						break;
					}
				}
			}
		}

		return process;
	}

	private void generateAccounting(FinanceDetail financeDetail) {
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		HashMap<String, Object> gldataMap = new HashMap<>();

		gldataMap.put("emptype", financeDetail.getCustomerDetails().getCustomer().getSubCategory());
		gldataMap.put("fincollateralreq", financeDetail.getFinScheduleData().getFinanceType().isFinCollateralReq());
		gldataMap.put("division", financeDetail.getFinScheduleData().getFinanceType().getFinDivision());

		Map<Integer, Long> finAdvanceMap = disbursementPostings.prepareDisbPostingApproval(
				financeDetail.getAdvancePaymentsList(), financeMain, financeMain.getFinBranch());

		List<FinAdvancePayments> advPayList = financeDetail.getAdvancePaymentsList();

		//loop through the disbursements.
		if (CollectionUtils.isNotEmpty(advPayList)) {
			for (int i = 0; i < advPayList.size(); i++) {
				FinAdvancePayments advPayment = advPayList.get(i);
				if (finAdvanceMap.containsKey(advPayment.getDisbSeq() + "_" + advPayment.getPaymentSeq())) {
					advPayment.setLinkedTranId(
							finAdvanceMap.get(advPayment.getDisbSeq() + "_" + advPayment.getPaymentSeq()));
				}
			}
		}
	}

	@Override
	public List<AuditDetail> processAPIQuickDisbursment(FinanceDetail financeDetail, String tableType,
			String auditTranType) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		List<FinAdvancePayments> finAdvancePayList = financeDetail.getAdvancePaymentsList();
		if (finAdvancePayList == null || finAdvancePayList.isEmpty()) {
			return auditDetails;
		}
		processDisbursments(financeDetail);
		auditDetails.addAll(doApprove(finAdvancePayList, "", PennantConstants.TRAN_ADD, financeDetail.isDisbStp()));
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

	public List<ErrorDetail> validateFinAdvPayments(List<FinAdvancePayments> list,
			List<FinanceDisbursement> financeDisbursement, FinanceMain financeMain, boolean loanApproved) {
		logger.debug(" Entering ");

		int ccyFormat = CurrencyUtil.getFormat(financeMain.getFinCcy());
		FinanceDisbursement totDisb = getTotal(financeDisbursement, financeMain, 0, false);

		BigDecimal netFinAmount = totDisb.getDisbAmount();
		//Since this is moved Fees should not be used here. 
		//	if (StringUtils.equals(financeMain.getAdvType(), AdvanceType.AE.name())) {
		//		netFinAmount = netFinAmount.subtract(financeMain.getAdvanceEMI());
		//	}

		List<ErrorDetail> errorList = new ArrayList<>();
		boolean checkMode = true;

		BigDecimal totDisbAmt = BigDecimal.ZERO;
		Map<Integer, BigDecimal> map = new HashMap<>();

		/*
		 * if (financeMain.isQuickDisb() && financeDisbursement != null && financeDisbursement.size() > 1) { //Multiple
		 * disbursement not allowed in quick disbursement ErrorDetail error = new ErrorDetail("60405", null);
		 * errorList.add(error); return errorList; }
		 */

		for (FinAdvancePayments finAdvPayment : list) {
			/*
			 * if (financeMain.isQuickDisb() && checkMode) { if (!StringUtils.equals(finAdvPayment.getPaymentType(),
			 * DisbursementConstants.PAYMENT_TYPE_CHEQUE)) { checkMode = false; } }
			 */

			//Skip the instruction if it is related to VAS
			if (StringUtils.equals(DisbursementConstants.PAYMENT_DETAIL_VAS, finAdvPayment.getPaymentDetail())) {
				continue;
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
			if (loanApproved || financeMain.isQuickDisb()) {
				//Total amount should match with disbursement amount.
				String[] valueParm = new String[2];
				valueParm[0] = PennantApplicationUtil.amountFormate(totDisbAmt, ccyFormat);
				valueParm[1] = PennantApplicationUtil.amountFormate(netFinAmount, ccyFormat);
				ErrorDetail error = new ErrorDetail("60401", valueParm);
				errorList.add(error);
				return errorList;
			}
		}

		if (!checkMode && financeMain.isQuickDisb()) {
			//For quick disbursement, only payment type cheque is allowed.
			ErrorDetail error = new ErrorDetail("60402", null);
			errorList.add(error);
			return errorList;
		}

		//since if the loan not approved then user can cancel the instruction and resubmit the record in loan origination
		if (loanApproved) {
			// Get approved disbursements.
			List<FinanceDisbursement> approvedDisbursments = getFinanceDisbursementDAO()
					.getFinanceDisbursementDetails(financeMain.getFinReference(), "", false);
			boolean approvedDisbursement;

			for (FinanceDisbursement disbursement : financeDisbursement) {
				approvedDisbursement = false;

				if (FinanceConstants.DISB_STATUS_CANCEL.equals(disbursement.getDisbStatus())) {
					continue;
				}

				// If approved disbursement, continue.
				if (approvedDisbursments != null && !approvedDisbursments.isEmpty()) {
					for (FinanceDisbursement aprovedDisbursement : approvedDisbursments) {
						if (disbursement.getDisbSeq() == aprovedDisbursement.getDisbSeq()) {
							approvedDisbursement = true;

							break;
						}
					}

					if (approvedDisbursement) {
						continue;
					}
				}

				Date disbDate = disbursement.getDisbDate();
				BigDecimal singletDisbursment = disbursement.getDisbAmount();

				if (disbDate.compareTo(financeMain.getFinStartDate()) == 0 && disbursement.getDisbSeq() == 1) {
					singletDisbursment = singletDisbursment.subtract(financeMain.getDownPayment());
					singletDisbursment = singletDisbursment.subtract(financeMain.getDeductFeeDisb());
					singletDisbursment = singletDisbursment.subtract(financeMain.getDeductInsDisb());
					if (FinanceConstants.BPI_DISBURSMENT.equals(financeMain.getBpiTreatment())) {
						singletDisbursment = singletDisbursment.subtract(financeMain.getBpiAmount());
					}
				} else if (disbursement.getDisbSeq() > 1) {
					singletDisbursment = singletDisbursment.subtract(disbursement.getDeductFeeDisb());
					singletDisbursment = singletDisbursment.subtract(disbursement.getDeductInsDisb());

				}
				int key = disbursement.getDisbSeq();

				BigDecimal totalGroupAmt = map.get(key);
				if (totalGroupAmt == null) {
					totalGroupAmt = BigDecimal.ZERO;
				}
				if (singletDisbursment.compareTo(totalGroupAmt) != 0) {
					String errorDesc = DateUtility.formatToLongDate(disbDate) + " , " + disbursement.getDisbSeq();
					//Total amount should match with disbursement amount dated :{0}.
					ErrorDetail error = new ErrorDetail("60404", new String[] { errorDesc });
					errorList.add(error);
					return errorList;
				}
			}
		}

		return errorList;

	}

	private static FinanceDisbursement getTotal(List<FinanceDisbursement> list, FinanceMain main, int seq,
			boolean group) {
		BigDecimal totdisbAmt = BigDecimal.ZERO;
		Date date = null;
		if (list != null && !list.isEmpty()) {
			for (FinanceDisbursement financeDisbursement : list) {
				if (group && seq != financeDisbursement.getDisbSeq()) {
					continue;
				}

				// exclude inst based schedules
				if (financeDisbursement.getLinkedDisbId() != 0) {
					continue;
				}

				if (!group) {
					if (StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, financeDisbursement.getDisbStatus())) {
						continue;
					}
				}

				date = financeDisbursement.getDisbDate();

				//check is first disbursement to make sure the we deducted from first disbursement date
				if (financeDisbursement.getDisbDate().getTime() == main.getFinStartDate().getTime()
						&& financeDisbursement.getDisbSeq() == 1) {
					totdisbAmt = totdisbAmt.subtract(main.getDownPayment());
					totdisbAmt = totdisbAmt.subtract(main.getDeductFeeDisb());
					totdisbAmt = totdisbAmt.subtract(main.getDeductInsDisb());
					if (StringUtils.trimToEmpty(main.getBpiTreatment()).equals(FinanceConstants.BPI_DISBURSMENT)) {
						totdisbAmt = totdisbAmt.subtract(main.getBpiAmount());
					}
				} else if (financeDisbursement.getDisbSeq() > 1) {
					totdisbAmt = totdisbAmt.subtract(financeDisbursement.getDeductFeeDisb());
					totdisbAmt = totdisbAmt.subtract(financeDisbursement.getDeductInsDisb());

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

	/**
	 * Method for validating VAS Instructions.<br>
	 * Every VAS if the fee is greater than 0 then it is having an instruction <br>
	 * Every VAS Instruction must be in the fee with amount greater than 0.<br>
	 * 
	 * @param advPaymentsList
	 * @param validate
	 * @return
	 */
	public List<ErrorDetail> validateVasInstructions(List<VASRecording> vasRecordingList,
			List<FinAdvancePayments> advPaymentsList, boolean validate) {
		logger.debug(Literal.ENTERING);
		List<ErrorDetail> errorList = new ArrayList<ErrorDetail>();
		if (validate) {
			//If the fee is greater than 0 then it is having an instruction.
			if (CollectionUtils.isNotEmpty(vasRecordingList)) {

				for (VASRecording vasRecording : vasRecordingList) {

					if (vasRecording.getFee().compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}

					//if the given VAS fee has no Instruction
					FinAdvancePayments advancePayments = getVasInstruction(advPaymentsList,
							vasRecording.getVasReference());
					if (advancePayments == null) {
						String[] errParm = new String[1];
						String[] valueParm = new String[1];
						errParm[0] = vasRecording.getVasReference();
						valueParm[0] = vasRecording.getVasReference();
						ErrorDetail error = new ErrorDetail(PennantConstants.KEY_FIELD, "VINST01", errParm, valueParm);
						errorList.add(error);
						//if the given VAS fee has not matched with Instruction amount
					} else if (vasRecording.getFee().compareTo(advancePayments.getAmtToBeReleased()) != 0) {
						String[] errParm = new String[1];
						String[] valueParm = new String[1];
						errParm[0] = vasRecording.getVasReference();
						valueParm[0] = vasRecording.getVasReference();
						ErrorDetail error = new ErrorDetail(PennantConstants.KEY_FIELD, "VINST03", errParm, valueParm);
						errorList.add(error);
					}
				}
			}

			//VAS Instruction must be in the fee with amount greater than 0
			if (CollectionUtils.isNotEmpty(advPaymentsList)) {

				for (FinAdvancePayments finAdvancePayments : advPaymentsList) {

					if (!StringUtils.equals(finAdvancePayments.getPaymentDetail(),
							DisbursementConstants.PAYMENT_DETAIL_VAS)) {
						continue;
					}

					if (isDeleteRecord(finAdvancePayments)) {
						continue;
					}
					//if instruction is there and finance having no VasRecording
					VASRecording vasRecording = getVasInst(finAdvancePayments.getVasReference(), vasRecordingList);
					if (vasRecording == null) {
						String[] errParm = new String[1];
						String[] valueParm = new String[1];
						errParm[0] = finAdvancePayments.getVasReference();
						valueParm[0] = finAdvancePayments.getVasReference();
						ErrorDetail error = new ErrorDetail(PennantConstants.KEY_FIELD, "VINST02", errParm, valueParm);
						errorList.add(error);
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return errorList;
	}

	/**
	 * Method to get the AdvancePayment instruction for the given VasReference.
	 * 
	 * @param list
	 * @param vasReference
	 * @return
	 */
	private FinAdvancePayments getVasInstruction(List<FinAdvancePayments> list, String vasReference) {
		FinAdvancePayments advancePayments = null;
		if (CollectionUtils.isEmpty(list)) {
			return advancePayments;
		}
		for (FinAdvancePayments finAdvancePayments : list) {

			if (!StringUtils.equalsIgnoreCase(finAdvancePayments.getPaymentDetail(),
					DisbursementConstants.PAYMENT_DETAIL_VAS)) {
				continue;
			}
			if (!StringUtils.equalsIgnoreCase(vasReference, finAdvancePayments.getVasReference())) {
				continue;
			}

			if (isDeleteRecord(finAdvancePayments)) {
				continue;
			}

			return finAdvancePayments;
		}
		return advancePayments;
	}

	@Override
	public List<FinAdvancePayments> processVasInstructions(List<VASRecording> vasRecordingList,
			List<FinAdvancePayments> curAdvPaymentsList, String entityCode) {
		logger.debug(Literal.ENTERING);
		if (curAdvPaymentsList == null) {
			curAdvPaymentsList = new ArrayList<>(1);
		}

		//Process VASRecording
		if (CollectionUtils.isNotEmpty(vasRecordingList)) {
			for (VASRecording vasRecording : vasRecordingList) {
				if (isVasInstExists(curAdvPaymentsList, vasRecording.getVasReference())) {
					continue;
				}
				VASConfiguration configuration = vasRecording.getVasConfiguration();
				if (configuration == null) {
					configuration = this.vASConfigurationDAO.getVASConfigurationByCode(vasRecording.getProductCode(),
							"");
				}
				if (configuration == null) {
					continue;
				}
				VASProviderAccDetail vasProvAccDetail = vASProviderAccDetailDAO
						.getVASProviderAccDetByPRoviderId(configuration.getManufacturerId(), entityCode, "_Aview");
				if (vasProvAccDetail == null) {
					continue;
				}
				FinAdvancePayments advancePayments = new FinAdvancePayments();
				advancePayments.setFinReference(vasRecording.getFinReference());
				advancePayments.setVasReference(vasRecording.getVasReference());
				advancePayments.setNewRecord(true);
				advancePayments.setWorkflowId(0);
				advancePayments.setPaymentSeq(getNextPaymentSequence(curAdvPaymentsList));
				advancePayments.setPaymentDetail(DisbursementConstants.PAYMENT_DETAIL_VAS);
				advancePayments.setRecordType(PennantConstants.RCD_ADD);
				advancePayments.setVersion(1);
				advancePayments.setRecordStatus("");
				advancePayments.setDisbCCy("INR");
				advancePayments.setDisbSeq(1);
				advancePayments.setStatus(DisbursementConstants.STATUS_NEW);
				advancePayments.setLLDate(SysParamUtil.getAppDate());
				advancePayments.setAmtToBeReleased(vasRecording.getFee());
				advancePayments.setPaymentType(vasProvAccDetail.getPaymentMode());
				advancePayments.setPartnerBankID(vasProvAccDetail.getPartnerBankId());
				advancePayments.setPartnerbankCode(vasProvAccDetail.getPartnerBankCode());
				advancePayments.setPartnerBankName(vasProvAccDetail.getPartnerBankName());

				switch (advancePayments.getPaymentType()) {

				case DisbursementConstants.PAYMENT_TYPE_CHEQUE:
				case DisbursementConstants.PAYMENT_TYPE_DD:
					advancePayments.setBankCode(vasProvAccDetail.getBankCode());
					advancePayments.setBankName(vasProvAccDetail.getBankName());
					advancePayments.setLiabilityHoldName(vasProvAccDetail.getProviderDesc());
					advancePayments.setPrintingLoc(vasProvAccDetail.getBranchCode());
					advancePayments.setPrintingLocDesc(vasProvAccDetail.getBranchDesc());
					//SIZE not matched
					//advancePayments.setLLReferenceNo(vasProvAccDetail.getAccountNumber());
				case DisbursementConstants.PAYMENT_TYPE_IMPS:
				case DisbursementConstants.PAYMENT_TYPE_NEFT:
				case DisbursementConstants.PAYMENT_TYPE_RTGS:
				case DisbursementConstants.PAYMENT_TYPE_IFT:
				case DisbursementConstants.PAYMENT_TYPE_IST:
					advancePayments.setBankBranchID(vasProvAccDetail.getBankBranchID());
					advancePayments.setCity(vasProvAccDetail.getBranchCity());
					advancePayments.setBranchBankCode(vasProvAccDetail.getBankCode());
					advancePayments.setBranchBankName(vasProvAccDetail.getBankName());
					advancePayments.setBranchDesc(vasProvAccDetail.getBranchDesc());
					advancePayments.setiFSC(vasProvAccDetail.getIfscCode());
					advancePayments.setBeneficiaryAccNo(vasProvAccDetail.getAccountNumber());
					advancePayments.setBeneficiaryName(vasProvAccDetail.getProviderDesc());
				default:
					break;
				}

				curAdvPaymentsList.add(advancePayments);
			}
		}
		//Process Existing instructions
		Iterator<FinAdvancePayments> itr = curAdvPaymentsList.iterator();
		while (itr.hasNext()) {
			FinAdvancePayments finAdvancePayments = itr.next();
			if (StringUtils.isBlank(finAdvancePayments.getVasReference())
					|| PennantConstants.List_Select.equals(finAdvancePayments.getVasReference())) {
				continue;
			}
			VASRecording vasRecording = getVasInst(finAdvancePayments.getVasReference(), vasRecordingList);
			if (vasRecording == null) {
				//need to remove that instruction;
				if (PennantConstants.RCD_ADD.equals(finAdvancePayments.getRecordType())) {
					itr.remove();
				} else {
					finAdvancePayments.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				}
				continue;
			}
			//Check VAS Amount(Update)
			if (finAdvancePayments.getAmtToBeReleased().compareTo(vasRecording.getFee()) != 0) {
				//TODO: Need to check data set req or not
				//finAdvancePayments.setAmtToBeReleased(vasRecording.getFee());
			}

		}
		logger.debug(Literal.LEAVING);
		return curAdvPaymentsList;
	}

	private VASRecording getVasInst(String vasReference, List<VASRecording> vasRecordingList) {
		if (CollectionUtils.isEmpty(vasRecordingList)) {
			return null;
		}
		for (VASRecording vasRecording : vasRecordingList) {
			if (StringUtils.equals(vasReference, vasRecording.getVasReference())) {
				if (PennantConstants.RECORD_TYPE_CAN.equals(vasRecording.getRecordType())) {
					return null;
				}
				return vasRecording;
			}
		}
		return null;
	}

	private boolean isVasInstExists(List<FinAdvancePayments> curAdvPaymentsList, String vasReference) {
		logger.debug(Literal.ENTERING);
		if (CollectionUtils.isEmpty(curAdvPaymentsList)) {
			return false;
		}
		for (FinAdvancePayments finAdvancePayments : curAdvPaymentsList) {
			if (StringUtils.equals(vasReference, finAdvancePayments.getVasReference())) {
				return true;
			}
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

	private boolean isDeleteRecord(FinAdvancePayments aFinAdvancePayments) {
		String recordType = aFinAdvancePayments.getRecordType();
		String status = aFinAdvancePayments.getStatus();
		if (PennantConstants.RECORD_TYPE_CAN.equals(recordType) || PennantConstants.RECORD_TYPE_DEL.equals(recordType)
				|| DisbursementConstants.STATUS_CANCEL.equals(status)
				|| DisbursementConstants.STATUS_REJECTED.equals(status)
				|| DisbursementConstants.STATUS_PAID_BUT_CANCELLED.equals(status)) {
			return true;
		}
		return false;
	}

	private boolean isnoValidationUserAction(String userAction) {
		boolean noValidation = false;
		if (userAction != null) {
			if ("Cancel".equalsIgnoreCase(userAction) || userAction.contains("Reject")
					|| userAction.contains("Resubmit") || userAction.contains("Decline")) {
				noValidation = true;
			}
		}
		return noValidation;
	}

	private void saveDocumentDetails(FinAdvancePayments finPayment) {
		DocumentDetails details = new DocumentDetails();
		details.setDocModule(DisbursementConstants.DISB_MODULE);
		details.setDoctype(finPayment.getDocType());
		details.setDocCategory(DisbursementConstants.DISB_DOC_TYPE);
		details.setDocName(finPayment.getDocumentName());
		details.setLastMntBy(finPayment.getLastMntBy());
		details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		details.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		details.setFinEvent(FinanceConstants.FINSER_EVENT_ORG);
		details.setDocImage(finPayment.getDocImage());
		details.setReferenceId(String.valueOf(finPayment.getPaymentId()));
		details.setFinReference(finPayment.getFinReference());

		saveDocument(DMSModule.FINANCE, DMSModule.DISBINST, details);
		DocumentDetails oldDocumentDetails = documentDetailsDAO.getDocumentDetails(details.getReferenceId(),
				details.getDocCategory(), details.getDocModule(), TableType.MAIN_TAB.getSuffix());

		if (oldDocumentDetails != null && oldDocumentDetails.getDocId() != Long.MIN_VALUE) {
			details.setDocId(oldDocumentDetails.getDocId());
			documentDetailsDAO.update(details, TableType.MAIN_TAB.getSuffix());
		} else {
			documentDetailsDAO.save(details, TableType.MAIN_TAB.getSuffix());
		}
	}

	@Override
	public void processPayments(PaymentTransaction paymentTransaction) {
		this.paymentsProcessService.processPayments(paymentTransaction);
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

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	@Override
	public void Update(long paymentId, long linkedTranId) {
		finAdvancePaymentsDAO.update(paymentId, linkedTranId);

	}

	@Override
	public int getMaxPaymentSeq(String finReference) {
		return finAdvancePaymentsDAO.getMaxPaymentSeq(finReference);
	}

	@Override
	public int getFinAdvCountByRef(String finReference, String type) {
		return finAdvancePaymentsDAO.getFinAdvCountByRef(finReference, type);
	}

	@Override
	public int getCountByFinReference(String finReference) {
		return finAdvancePaymentsDAO.getCountByFinReference(finReference);
	}

	public void setPartnerBankService(PartnerBankService partnerBankService) {
		this.partnerBankService = partnerBankService;
	}

	@Override
	public List<FinAdvancePayments> getFinAdvancePaymentByFinRef(String finRefernce, Date toDate) {
		return finAdvancePaymentsDAO.getFinAdvancePaymentByFinRef(finRefernce, toDate, "");
	}

	@Override
	public void updateStatus(FinAdvancePayments finAdvancePayment, String type) {
		finAdvancePaymentsDAO.updateStatus(finAdvancePayment, type);
	}

	@Override
	public void updatePaymentStatus(FinAdvancePayments finAdvancePayment, String type) {
		finAdvancePaymentsDAO.updatePaymentStatus(finAdvancePayment, type);
	}

	@Override
	public FinAdvancePayments getFinAdvancePaymentsById(FinAdvancePayments finAdvancePayments, String type) {
		return finAdvancePaymentsDAO.getFinAdvancePaymentsById(finAdvancePayments, type);
	}

	@Override
	public int getCountByPaymentId(String finReference, long paymentId) {
		return finAdvancePaymentsDAO.getCountByPaymentId(finReference, paymentId);
	}

	public MandateDAO getMandateDAO() {
		return mandateDAO;
	}

	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	public FinanceProfitDetailDAO getProfitDetailsDAO() {
		return profitDetailsDAO;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public void setCovenantsService(CovenantsService covenantsService) {
		this.covenantsService = covenantsService;
	}

	public InstrumentwiseLimitService getInstrumentwiseLimitService() {
		return instrumentwiseLimitService;
	}

	public void setInstrumentwiseLimitService(InstrumentwiseLimitService instrumentwiseLimitService) {
		this.instrumentwiseLimitService = instrumentwiseLimitService;
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public void setPaymentsProcessService(PaymentsProcessService paymentsProcessService) {
		this.paymentsProcessService = paymentsProcessService;
	}

	public DisbursementPostings getDisbursementPostings() {
		return disbursementPostings;
	}

	public void setDisbursementPostings(DisbursementPostings disbursementPostings) {
		this.disbursementPostings = disbursementPostings;
	}

	public void setPennyDropService(PennyDropService pennyDropService) {
		this.pennyDropService = pennyDropService;
	}

	public void setvASProviderAccDetailDAO(VASProviderAccDetailDAO vASProviderAccDetailDAO) {
		this.vASProviderAccDetailDAO = vASProviderAccDetailDAO;
	}

	public void setvASConfigurationDAO(VASConfigurationDAO vASConfigurationDAO) {
		this.vASConfigurationDAO = vASConfigurationDAO;
	}

	public DocumentDetailsDAO getDocumentDetailsDAO() {
		return documentDetailsDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}
}