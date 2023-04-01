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
 * * FileName : FeePostingServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 02-12-2016 * *
 * Modified Date : 02-12-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 02-12-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.fees.feepostings.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.fees.FeePostingsDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.dao.limit.LimitHeaderDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.fees.FeePostings;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.fees.feepostings.FeePostingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.accounting.PostAgainst;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

/**
 * @author murthy.y
 *
 */
public class FeePostingServiceImpl extends GenericService<FeePostings> implements FeePostingService {
	private static final Logger logger = LogManager.getLogger(FeePostingServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FeePostingsDAO feePostingsDAO;
	private AccountEngineExecution engineExecution;
	private FinanceMainDAO financeMainDAO;
	private CustomerDAO customerDAO;
	private CollateralSetupDAO collateralSetupDAO;
	private PartnerBankDAO partnerBankDAO;
	private FeeTypeDAO feeTypeDAO;
	private LimitHeaderDAO limitHeaderDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FinanceWriteoffDAO financeWriteoffDAO;

	@Override
	public FeePostings getFeePostings() {
		return feePostingsDAO.getFeePostings();
	}

	@Override
	public FeePostings getNewFeePostings() {
		return feePostingsDAO.getNewFeePostings();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String tableType = "";
		FeePostings feePostings = (FeePostings) auditHeader.getAuditDetail().getModelData();

		if (feePostings.isWorkflow()) {
			tableType = "_Temp";
		}

		if (feePostings.isNewRecord()) {
			feePostingsDAO.save(feePostings, tableType);
		} else {
			feePostingsDAO.update(feePostings, tableType);
		}

		String rcdMaintainSts = FinServiceEvent.FEEPOSTING;
		financeMainDAO.updateMaintainceStatus(feePostings.getReference(), rcdMaintainSts);

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<>());
		FeePostings feePostings = (FeePostings) auditDetail.getModelData();

		FeePostings tempfeePostings = null;
		if (feePostings.isWorkflow()) {
			tempfeePostings = feePostingsDAO.getFeePostingsById(feePostings.getPostId(), "_Temp");
		}
		FeePostings befFeePostings = feePostingsDAO.getFeePostingsById(feePostings.getPostId(), "");

		FeePostings oldfeePostings = feePostings.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(feePostings.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_ID") + ":" + valueParm[0];

		if (feePostings.isNewRecord()) { // for New record or new record into work flow
			if (!feePostings.isWorkflow()) {// With out Work flow only new records
				if (befFeePostings != null) { // Record Already Exists in the table then error
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (feePostings.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is
					if (befFeePostings != null || tempfeePostings != null) { // if records already exists in the main
																				// table
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befFeePostings == null || tempfeePostings != null) {
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!feePostings.isWorkflow()) { // With out Work flow for update and delete
				if (befFeePostings == null) { // if records not exists in the main table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldfeePostings != null
							&& !oldfeePostings.getLastMntOn().equals(befFeePostings.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm));
						}
					}
				}
			} else {
				if (tempfeePostings == null) { // if records not exists in the Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (tempfeePostings != null && oldfeePostings != null
						&& !oldfeePostings.getLastMntOn().equals(tempfeePostings.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}

		// Validate Loan is INPROGRESS in any Other Servicing option or NOT ?

		if ("L".equals(feePostings.getPostAgainst())) {
			String reference = feePostings.getReference();
			FinanceMain fm = financeMainDAO.getFinanceMain(reference, TableType.VIEW);

			if (StringUtils.isNotEmpty(fm.getRcdMaintainSts())
					&& !FinServiceEvent.FEEPOSTING.equals(fm.getRcdMaintainSts())) {
				String[] valueParm1 = new String[1];
				valueParm1[0] = fm.getRcdMaintainSts();
				auditDetail.setErrorDetail(new ErrorDetail("LMS001", valueParm1));
			}

			if (financeWriteoffDAO.isWriteoffLoan(fm.getFinID(), "")) {
				String[] valueParm1 = new String[1];
				valueParm1[0] = " ";
				auditDetail.setErrorDetail(new ErrorDetail("FWF001", valueParm1));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !feePostings.isWorkflow()) {
			auditDetail.setBefImage(befFeePostings);
		}

		return auditDetail;
	}

	@Override
	public FeePostings getFeePostingsById(long id) {
		return feePostingsDAO.getFeePostingsById(id, "_View");
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {

		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		FeePostings feePostings = (FeePostings) auditHeader.getAuditDetail().getModelData();

		feePostingsDAO.delete(feePostings, "");

		String[] fields = PennantJavaUtil.getFieldDetails(new FeePostings(), feePostings.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				feePostings.getBefImage(), feePostings));

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FeePostings feePostings = new FeePostings("");
		BeanUtils.copyProperties((FeePostings) auditHeader.getAuditDetail().getModelData(), feePostings);

		// Processing Accounting Details
		if (StringUtils.equals(feePostings.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			auditHeader = executeAccountingProcess(auditHeader, SysParamUtil.getAppDate());
		}

		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		if (feePostings.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			feePostingsDAO.delete(feePostings, "");
		} else {
			feePostings.setRoleCode("");
			feePostings.setNextRoleCode("");
			feePostings.setTaskId("");
			feePostings.setNextTaskId("");
			feePostings.setWorkflowId(0);

			if (feePostings.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				feePostings.setRecordType("");
				feePostingsDAO.save(feePostings, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				feePostings.setRecordType("");
				feePostingsDAO.update(feePostings, "");
			}

		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FeePostings(), feePostings.getExcludeFields());

		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		if (!StringUtils.equals(feePostings.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			feePostingsDAO.delete(feePostings, "_Temp");

			auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
					feePostings.getBefImage(), feePostings));
			auditHeader.setAuditDetails(auditDetailList);
			auditHeaderDAO.addAudit(auditHeader);
		}

		financeMainDAO.updateMaintainceStatus(feePostings.getFinReference(), "");

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				feePostings.getBefImage(), feePostings));
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	public AuditHeader executeAccountingProcess(AuditHeader auditHeader, Date curBDay) {
		logger.debug(Literal.ENTERING);

		List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();

		FeePostings feePostings = new FeePostings("");
		BeanUtils.copyProperties((FeePostings) auditHeader.getAuditDetail().getModelData(), feePostings);

		try {

			if (feePostings.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {

				AEEvent aeEvent = new AEEvent();
				aeEvent.setAccountingEvent(AccountingEvent.MANFEE);
				AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

				if (amountCodes == null) {
					amountCodes = new AEAmountCodes();
				}

				// If Fee postings Created Against Finance Reference
				if (PostAgainst.isLoan(feePostings.getPostAgainst())) {
					FinanceMain fm = financeMainDAO.getFinanceMain(feePostings.getReference(), TableType.MAIN_TAB);
					amountCodes.setFinType(fm.getFinType());
					amountCodes.setPartnerBankAc(getFeePostings().getPartnerBankAc());
					aeEvent.setBranch(fm.getFinBranch());
					aeEvent.setCustID(fm.getCustID());
					aeEvent.setCcy(fm.getFinCcy());
				} else if (PostAgainst.isCustomer(feePostings.getPostAgainst())) {
					Customer customer = customerDAO.getCustomerByCIF(feePostings.getReference(), "");
					aeEvent.setBranch(customer.getCustDftBranch());
					aeEvent.setCustID(customer.getCustID());
					aeEvent.setCcy(customer.getCustBaseCcy());
				} else if (PostAgainst.isCollateral(feePostings.getPostAgainst())) {
					CollateralSetup collateralSetup = collateralSetupDAO
							.getCollateralSetupByRef(feePostings.getReference(), "");
					Customer customer = customerDAO.getCustomerByID(collateralSetup.getDepositorId(), "");
					aeEvent.setCustID(collateralSetup.getDepositorId());
					aeEvent.setBranch(customer.getCustDftBranch());
					aeEvent.setCcy(collateralSetup.getCollateralCcy());
				} else if (PostAgainst.isLimit(feePostings.getPostAgainst())) {
					LimitHeader header = limitHeaderDAO.getLimitHeaderById(Long.valueOf(feePostings.getReference()),
							"_View");
					aeEvent.setBranch(header.getCustDftBranch());
					aeEvent.setCustID(header.getCustomerId());
					aeEvent.setCcy(header.getLimitCcy());
				}

				amountCodes.setPartnerBankAc(feePostings.getPartnerBankAc());
				amountCodes.setPartnerBankAcType(feePostings.getPartnerBankAcType());
				aeEvent.setFinReference(String.valueOf(feePostings.getPostId()));
				aeEvent.setPostingUserBranch(auditHeader.getAuditBranchCode());
				aeEvent.setValueDate(feePostings.getValueDate());
				aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
				feePostings.getDeclaredFieldValues(aeEvent.getDataMap());
				aeEvent.getAcSetIDList().add(Long.valueOf(feePostings.getAccountSetId()));
				postingsPreparationUtil.postAccounting(aeEvent);

				engineExecution.getAccEngineExecResults(aeEvent);
				list = aeEvent.getReturnDataSet();

				validateCreditandDebitAmounts(aeEvent);
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			ArrayList<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
			errorDetails.add(new ErrorDetail("Accounting Engine", PennantConstants.ERR_UNDEF, "E",
					"Accounting Engine Failed to Create Postings:" + e.getMessage(), new String[] {}, new String[] {}));
			auditHeader.setErrorList(errorDetails);
			auditHeader.setNextProcess(false);
			return auditHeader;
		}

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FeePostings feePostings = (FeePostings) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		String[] fields = PennantJavaUtil.getFieldDetails(new FeePostings(), feePostings.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				feePostings.getBefImage(), feePostings));

		feePostingsDAO.delete(feePostings, "_Temp");

		financeMainDAO.updateMaintainceStatus(feePostings.getFinReference(), "");
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * Validate FeePostings.
	 * 
	 * @param feePostings
	 * 
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doValidations(FeePostings feePostings) {
		AuditDetail auditDetail = new AuditDetail();
		ErrorDetail errorDetail = new ErrorDetail();
		if (StringUtils.isBlank(feePostings.getCif()) && StringUtils.isBlank(feePostings.getFinReference())
				&& StringUtils.isBlank(feePostings.getCollateralRef()) && feePostings.getLimitId() <= 0) {
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90292", "", null));
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;
		} else {
			boolean isMultiValues = getPostingAgainst(feePostings);
			if (isMultiValues) {
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90293", "", null));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
		}

		switch (PostAgainst.valueOf(feePostings.getPostAgainst())) {
		case CUSTOMER:
			Customer customer = customerDAO.getCustomerByCIF(feePostings.getCif(), "");
			if (customer != null) {
				feePostings.setReference(feePostings.getCif());
				// validate currency
				if (StringUtils.isNotBlank(feePostings.getCurrency())) {
					if (!StringUtils.equalsIgnoreCase(feePostings.getCurrency(), customer.getCustBaseCcy())) {
						String[] valueParm = new String[2];
						valueParm[0] = feePostings.getCurrency();
						valueParm[1] = "Customer: " + customer.getCustBaseCcy();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90294", "", valueParm)));
					} else {
						feePostings.setCurrency(customer.getCustBaseCcy());
					}
				}
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = feePostings.getCif();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90101", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			break;
		case LOAN:
			Long finID = financeMainDAO.getFinID(feePostings.getFinReference());

			if (finID == null) {
				String[] valueParm = new String[1];
				valueParm[0] = feePostings.getFinReference();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90201", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}

			FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "", false);
			feePostings.setReference(feePostings.getFinReference());

			if (StringUtils.isNotBlank(feePostings.getCurrency())) {
				if (!StringUtils.equalsIgnoreCase(feePostings.getCurrency(), fm.getFinCcy())) {
					String[] valueParm = new String[2];
					valueParm[0] = feePostings.getCurrency();
					valueParm[1] = "Loan: " + fm.getFinCcy();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90294", "", valueParm)));
					return auditDetail;
				} else {
					feePostings.setCurrency(fm.getFinCcy());
				}
			}
			break;
		case LIMIT:
			LimitHeader limitHeader = limitHeaderDAO.getLimitHeaderById(feePostings.getLimitId(), "");
			if (limitHeader != null) {
				feePostings.setReference(String.valueOf(feePostings.getLimitId()));
				// validate currency
				if (StringUtils.isNotBlank(feePostings.getCurrency())) {
					if (!StringUtils.equalsIgnoreCase(feePostings.getCurrency(), limitHeader.getLimitCcy())) {
						String[] valueParm = new String[2];
						valueParm[0] = feePostings.getCurrency();
						valueParm[1] = "Limit: " + limitHeader.getLimitCcy();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90294", "", valueParm)));
					} else {
						feePostings.setCurrency(limitHeader.getLimitCcy());
					}
				}
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(feePostings.getLimitId());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90807", "", valueParm)));
				return auditDetail;
			}
			break;
		case COLLATERAL:
			CollateralSetup collateralSetup = collateralSetupDAO.getCollateralSetupByRef(feePostings.getCollateralRef(),
					"");
			if (collateralSetup != null) {
				feePostings.setReference(feePostings.getCollateralRef());
				// validate currency
				if (StringUtils.isNotBlank(feePostings.getCurrency())) {
					if (!StringUtils.equalsIgnoreCase(feePostings.getCurrency(), collateralSetup.getCollateralCcy())) {
						String[] valueParm = new String[2];
						valueParm[0] = feePostings.getCurrency();
						valueParm[1] = "Collateral: " + collateralSetup.getCollateralCcy();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90294", "", valueParm)));
					} else {
						feePostings.setCurrency(collateralSetup.getCollateralCcy());
					}
				}
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = feePostings.getCollateralRef();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90906", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			break;

		default:

			break;
		}
		// ValueDate
		if (feePostings.getValueDate() == null) {
			feePostings.setValueDate(SysParamUtil.getAppDate());
		} else {
			Date minReqPostingDate = DateUtil.addDays(SysParamUtil.getAppDate(),
					-SysParamUtil.getValueAsInt(SMTParameterConstants.FEE_POSTING_DATE_BACK_DAYS));
			if (feePostings.getValueDate().before(minReqPostingDate)
					|| feePostings.getValueDate().after(SysParamUtil.getAppDate())) {
				String[] valueParm = new String[3];
				valueParm[0] = "Value Date";
				valueParm[1] = DateUtil.formatToLongDate(minReqPostingDate);
				valueParm[2] = DateUtil.formatToLongDate(SysParamUtil.getAppDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
			}
		}

		PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(feePostings.getPartnerBankId(), "");
		if (partnerBank == null || !partnerBank.isActive()) {
			String[] valueParm = new String[2];
			valueParm[0] = "PartnerBank";
			valueParm[1] = String.valueOf(feePostings.getPartnerBankId());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90295", "", valueParm)));
		} else {
			feePostings.setPartnerBankAc(partnerBank.getAccountNo());
			feePostings.setPartnerBankAcType(partnerBank.getAcType());
			feePostings.setPartnerBankName(partnerBank.getPartnerBankName());
		}

		FeeType feeType = feeTypeDAO.getApprovedFeeTypeByFeeCode(feePostings.getFeeTyeCode());
		if (feeType == null || !feeType.isActive()) {
			String[] valueParm = new String[2];
			valueParm[0] = "Fee";
			valueParm[1] = feePostings.getFeeTyeCode();
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90295", "", valueParm)));
		} else {
			if (feeType.getAccountSetId() > 0) {
				feePostings.setAccountSetId(String.valueOf(feeType.getAccountSetId()));
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = feePostings.getFeeTyeCode();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90353", "", valueParm)));
			}
		}
		// validating amount
		if (feePostings.getPostingAmount().compareTo(BigDecimal.ZERO) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "Amount";
			valueParm[1] = "Zero";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm)));
		}
		return auditDetail;
	}

	private boolean getPostingAgainst(FeePostings feePostings) {
		boolean isMutiValues = false;

		// Posting against customer
		if (StringUtils.isNotBlank(feePostings.getCif())) {
			if (StringUtils.isNotBlank(feePostings.getFinReference())
					|| StringUtils.isNotBlank(feePostings.getCollateralRef()) || feePostings.getLimitId() > 0) {
				isMutiValues = true;
				return isMutiValues;
			} else {
				feePostings.setPostAgainst(PostAgainst.CUSTOMER.code());
			}
		}

		// Posting against Finance
		if (StringUtils.isNotBlank(feePostings.getFinReference())) {
			if (StringUtils.isNotBlank(feePostings.getCif()) || StringUtils.isNotBlank(feePostings.getCollateralRef())
					|| feePostings.getLimitId() > 0) {
				isMutiValues = true;
				return isMutiValues;
			} else {
				feePostings.setPostAgainst(PostAgainst.LOAN.code());
			}
		}

		// Posting against collateral
		if (StringUtils.isNotBlank(feePostings.getCollateralRef())) {
			if (StringUtils.isNotBlank(feePostings.getCif()) || StringUtils.isNotBlank(feePostings.getFinReference())
					|| feePostings.getLimitId() > 0) {
				isMutiValues = true;
				return isMutiValues;
			} else {
				feePostings.setPostAgainst(PostAgainst.COLLATERAL.code());
			}
		}

		// Posting against limit
		if (feePostings.getLimitId() > 0) {
			if (StringUtils.isNotBlank(feePostings.getCif()) || StringUtils.isNotBlank(feePostings.getFinReference())
					|| StringUtils.isNotBlank(feePostings.getCollateralRef())) {
				isMutiValues = true;
				return isMutiValues;
			} else {
				feePostings.setPostAgainst(PostAgainst.LIMIT.code());
			}
		}
		return isMutiValues;
	}

	public void validateCreditandDebitAmounts(AEEvent aeEvent) {

		BigDecimal creditAmt = BigDecimal.ZERO;
		BigDecimal debitAmt = BigDecimal.ZERO;

		List<ReturnDataSet> dataset = aeEvent.getReturnDataSet();

		for (ReturnDataSet returnDataSet : dataset) {
			if (StringUtils.equals(returnDataSet.getDrOrCr(), "C")) {
				creditAmt = creditAmt.add(returnDataSet.getPostAmount());
			} else {
				debitAmt = debitAmt.add(returnDataSet.getPostAmount());
			}
		}
		if (creditAmt.compareTo(debitAmt) != 0) {
			throw new InterfaceException("9998",
					"Total credits and Total debits are not matched.Please check accounting configuration.");
		}
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFeePostingsDAO(FeePostingsDAO feePostingsDAO) {
		this.feePostingsDAO = feePostingsDAO;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setCollateralSetupDAO(CollateralSetupDAO collateralSetupDAO) {
		this.collateralSetupDAO = collateralSetupDAO;
	}

	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public void setLimitHeaderDAO(LimitHeaderDAO limitHeaderDAO) {
		this.limitHeaderDAO = limitHeaderDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setFinanceWriteoffDAO(FinanceWriteoffDAO financeWriteoffDAO) {
		this.financeWriteoffDAO = financeWriteoffDAO;
	}
}