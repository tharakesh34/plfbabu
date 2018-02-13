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
 * FileName    		:  FeePostingServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-12-2016    														*
 *                                                                  						*
 * Modified Date    :  02-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-12-2016       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.fees.feepostings.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.fees.FeePostingsDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.limit.LimitHeaderDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.fees.FeePostings;
import com.pennant.backend.model.feetype.FeeType;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.fees.feepostings.FeePostingService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.rits.cloning.Cloner;

public class FeePostingServiceImpl extends GenericService<FeePostings> implements FeePostingService {
	private static final Logger		logger	= Logger.getLogger(FeePostingServiceImpl.class);

	private AuditHeaderDAO			auditHeaderDAO;
	private FeePostingsDAO			feePostingsDAO;
	private AccountEngineExecution	engineExecution;
	private PostingsDAO				postingsDAO;
	private FinanceMainDAO 		   	financeMainDAO;
	private AccountingSetDAO        accountingSetDAO;
	private CustomerDAO				customerDAO;
	private CollateralSetupDAO		collateralSetupDAO;
	private PartnerBankDAO			partnerBankDAO;
	private FeeTypeDAO				feeTypeDAO;
	private LimitHeaderDAO 			limitHeaderDAO;  
	private PostingsPreparationUtil			postingsPreparationUtil;

	@Override
	public FeePostings getFeePostings() {
		return getFeePostingsDAO().getFeePostings();
	}

	@Override
	public FeePostings getNewFeePostings() {
		return getFeePostingsDAO().getNewFeePostings();
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
		FeePostings feePostings = (FeePostings) auditHeader.getAuditDetail().getModelData();

		if (feePostings.isWorkflow()) {
			tableType = "_Temp";
		}

		if (feePostings.isNew()) {
			getFeePostingsDAO().save(feePostings, tableType);
		} else {
			getFeePostingsDAO().update(feePostings, tableType);
		}

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
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
	
	
	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getFinTypePartnerBankDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	public AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FeePostings feePostings = (FeePostings) auditDetail.getModelData();

		FeePostings tempfeePostings = null;
		if (feePostings.isWorkflow()) {
			tempfeePostings = getFeePostingsDAO().getFeePostingsById(feePostings.getPostId(), "_Temp");
		}
		FeePostings befFeePostings = getFeePostingsDAO().getFeePostingsById(feePostings.getPostId(), "");

		FeePostings oldfeePostings = feePostings.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(feePostings.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_ID") + ":" + valueParm[0];

		if (feePostings.isNew()) { // for New record or new record into work flow
			if (!feePostings.isWorkflow()) {// With out Work flow only new records
				if (befFeePostings != null) { // Record Already Exists in the table then error
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (feePostings.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is
					if (befFeePostings != null || tempfeePostings != null) { // if records already exists in the main table
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

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !feePostings.isWorkflow()) {
			auditDetail.setBefImage(befFeePostings);
		}

		return auditDetail;
	}

	@Override
	public FeePostings getFeePostingsById(long id) {
		return getFeePostingsDAO().getFeePostingsById(id, "_View");
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {

		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		FeePostings feePostings = (FeePostings) auditHeader.getAuditDetail().getModelData();

		getFeePostingsDAO().delete(feePostings, "");

		String[] fields = PennantJavaUtil.getFieldDetails(new FeePostings(), feePostings.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				feePostings.getBefImage(), feePostings));

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;

	}

	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws InterfaceException {
		logger.debug("Entering");

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
			auditHeader = executeAccountingProcess(auditHeader, DateUtility.getAppDate());
		}

		if (feePostings.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getFeePostingsDAO().delete(feePostings, "");
		} else {
			feePostings.setRoleCode("");
			feePostings.setNextRoleCode("");
			feePostings.setTaskId("");
			feePostings.setNextTaskId("");
			feePostings.setWorkflowId(0);

			if (feePostings.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				feePostings.setRecordType("");
				getFeePostingsDAO().save(feePostings, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				feePostings.setRecordType("");
				getFeePostingsDAO().update(feePostings, "");
			}

		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FeePostings(), feePostings.getExcludeFields());

		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		if(!StringUtils.equals(feePostings.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			getFeePostingsDAO().delete(feePostings, "_Temp");

			auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
					feePostings.getBefImage(), feePostings));
			auditHeader.setAuditDetails(auditDetailList);
			getAuditHeaderDAO().addAudit(auditHeader);
		}


		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				feePostings.getBefImage(), feePostings));
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * Method for Execute posting Details on Core Banking Side
	 * @param auditHeader
	 * @param curBDay
	 * @return
	 * @throws AccountNotFoundException
	 */
	public AuditHeader executeAccountingProcess(AuditHeader auditHeader, Date curBDay) throws InterfaceException {
		logger.debug("Entering");

		FeePostings feePostings = new FeePostings("");
		BeanUtils.copyProperties((FeePostings) auditHeader.getAuditDetail().getModelData(), feePostings);

		try {

			if (feePostings.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {

				AEEvent aeEvent = new AEEvent();
				aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_MANFEE);
				AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
				
				if (amountCodes == null) {
					amountCodes = new AEAmountCodes();
				}

				// If Fee postings Created Against Finance Reference
				if (StringUtils.equals(FinanceConstants.POSTING_AGAINST_LOAN, feePostings.getPostAgainst())) {
					FinanceMain financeMain=financeMainDAO.getFinanceMainForBatch(feePostings.getReference());
					amountCodes.setFinType(financeMain.getFinType());
					amountCodes.setPartnerBankAc(getFeePostings().getPartnerBankAc());
					aeEvent.setBranch(financeMain.getFinBranch());
					aeEvent.setCustID(financeMain.getCustID());
					aeEvent.setCcy(financeMain.getFinCcy());
				}else if (StringUtils.equals(FinanceConstants.POSTING_AGAINST_CUST, feePostings.getPostAgainst())) {
					Customer customer = customerDAO.getCustomerByCIF(feePostings.getReference(), "");
					aeEvent.setBranch(customer.getCustDftBranch());
					aeEvent.setCustID(customer.getCustID());
					aeEvent.setCcy(customer.getCustBaseCcy());
				}else if (StringUtils.equals(FinanceConstants.POSTING_AGAINST_COLLATERAL, feePostings.getPostAgainst())) {
					CollateralSetup collateralSetup = collateralSetupDAO.getCollateralSetupByRef(feePostings.getReference(),"");
					Customer customer = customerDAO.getCustomerByID(collateralSetup.getDepositorId(), "");
					aeEvent.setCustID(collateralSetup.getDepositorId());
					aeEvent.setBranch(customer.getCustDftBranch());
					aeEvent.setCcy(collateralSetup.getCollateralCcy());
				} else if (StringUtils.equals(FinanceConstants.POSTING_AGAINST_LIMIT, feePostings.getPostAgainst())) {
					LimitHeader header = limitHeaderDAO.getLimitHeaderById(Long.valueOf(feePostings.getReference()),"_View");
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
				getPostingsPreparationUtil().postAccounting(aeEvent);
				
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			ArrayList<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
			errorDetails.add(new ErrorDetail("Accounting Engine", PennantConstants.ERR_UNDEF, "E",
					"Accounting Engine Failed to Create Postings:" + e.getMessage(), new String[] {}, new String[] {}));
			auditHeader.setErrorList(errorDetails);
		}

		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {

		logger.debug("Entering");
		
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FeePostings feePostings = (FeePostings) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		String[] fields = PennantJavaUtil.getFieldDetails(new FeePostings(), feePostings.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], feePostings.getBefImage(), feePostings));

		getFeePostingsDAO().delete(feePostings, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.debug("Leaving");
		return auditHeader;


	}
	
	/**
	 * Validate FeePostings.
	 * @param feePostings
	 * 
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doValidations(FeePostings feePostings) {
		AuditDetail auditDetail = new AuditDetail();
		ErrorDetail errorDetail = new ErrorDetail();
		if(StringUtils.isBlank(feePostings.getCif()) && StringUtils.isBlank(feePostings.getFinReference())
				&& StringUtils.isBlank(feePostings.getCollateralRef()) && feePostings.getLimitId() <= 0) {
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90292", "", null));
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;
		} else {
			boolean isMultiValues = getPostingAgainst(feePostings);
			if(isMultiValues) {
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90293", "", null));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
		}
		
		switch (feePostings.getPostAgainst()) {
		case FinanceConstants.POSTING_AGAINST_CUST:
			Customer customer = customerDAO.getCustomerByCIF(feePostings.getCif(), "");
			if (customer != null) {
				feePostings.setReference(feePostings.getCif());
				// validate currency
				if (StringUtils.isNotBlank(feePostings.getCurrency())) {
					if (!StringUtils.equalsIgnoreCase(feePostings.getCurrency(),  customer.getCustBaseCcy())) {
						String[] valueParm = new String[2];
						valueParm[0] = feePostings.getCurrency();
						valueParm[1] = "Customer: "+customer.getCustBaseCcy();
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
		case FinanceConstants.POSTING_AGAINST_LOAN:
			FinanceMain financeMain = financeMainDAO.getFinanceMainById(feePostings.getFinReference(), "", false);
			if (financeMain != null) {
				feePostings.setReference(feePostings.getFinReference());
				// validate currency
				if (StringUtils.isNotBlank(feePostings.getCurrency())) {
					if (!StringUtils.equalsIgnoreCase(feePostings.getCurrency(), financeMain.getFinCcy())) {
						String[] valueParm = new String[2];
						valueParm[0] = feePostings.getCurrency();
						valueParm[1] = "Loan: "+financeMain.getFinCcy();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90294", "", valueParm)));
						return auditDetail;
					} else {
						feePostings.setCurrency(financeMain.getFinCcy());
					}
				}
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = feePostings.getFinReference();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90201", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			break;
		case FinanceConstants.POSTING_AGAINST_LIMIT:
			LimitHeader limitHeader = limitHeaderDAO.getLimitHeaderById(feePostings.getLimitId(), "");
			if(limitHeader != null ) {
				feePostings.setReference(String.valueOf(feePostings.getLimitId()));
				// validate currency
				if (StringUtils.isNotBlank(feePostings.getCurrency())) {
					if (!StringUtils.equalsIgnoreCase(feePostings.getCurrency(), limitHeader.getLimitCcy())) {
						String[] valueParm = new String[2];
						valueParm[0] = feePostings.getCurrency();
						valueParm[1] = "Limit: "+limitHeader.getLimitCcy();
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
		case FinanceConstants.POSTING_AGAINST_COLLATERAL:
			CollateralSetup collateralSetup = collateralSetupDAO.getCollateralSetupByRef(feePostings.getCollateralRef(),"");
			if (collateralSetup != null) {
				feePostings.setReference(feePostings.getCollateralRef());
				// validate currency
				if (StringUtils.isNotBlank(feePostings.getCurrency())) {
					if (!StringUtils.equalsIgnoreCase(feePostings.getCurrency(), collateralSetup.getCollateralCcy())) {
						String[] valueParm = new String[2];
						valueParm[0] = feePostings.getCurrency();
						valueParm[1] = "Collateral: "+collateralSetup.getCollateralCcy();
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
		//ValueDate
		if(feePostings.getValueDate() == null) {
		feePostings.setValueDate(DateUtility.getAppDate());
		} else {
			Date minReqPostingDate = DateUtility.addDays(DateUtility.getAppDate(),
					-SysParamUtil.getValueAsInt("BACKDAYS_STARTDATE"));
			if (feePostings.getValueDate().before(minReqPostingDate)
					|| feePostings.getValueDate().after(DateUtility.getAppDate())) {
				String[] valueParm = new String[3];
				valueParm[0] = "Value Date";
				valueParm[1] = DateUtility.formatToLongDate(minReqPostingDate);
				valueParm[2] = DateUtility.formatToLongDate(DateUtility.getAppDate());
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
			feePostings.setAccountSetId(String.valueOf(feeType.getAccountSetId()));
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
				feePostings.setPostAgainst(FinanceConstants.POSTING_AGAINST_CUST);
			}
		}
		
		// Posting against Finance
		if (StringUtils.isNotBlank(feePostings.getFinReference())) {
			if (StringUtils.isNotBlank(feePostings.getCif())
					|| StringUtils.isNotBlank(feePostings.getCollateralRef()) || feePostings.getLimitId() > 0) {
				isMutiValues = true;
				return isMutiValues;
			} else {
				feePostings.setPostAgainst(FinanceConstants.POSTING_AGAINST_LOAN);
			}
		}
		
		// Posting against collateral
		if (StringUtils.isNotBlank(feePostings.getCollateralRef())) {
			if (StringUtils.isNotBlank(feePostings.getCif())
					|| StringUtils.isNotBlank(feePostings.getFinReference()) || feePostings.getLimitId() > 0) {
				isMutiValues = true;
				return isMutiValues;
			} else {
				feePostings.setPostAgainst(FinanceConstants.POSTING_AGAINST_COLLATERAL);
			}
		}
		
		// Posting against limit
		if (feePostings.getLimitId() > 0) {
			if (StringUtils.isNotBlank(feePostings.getCif())
					|| StringUtils.isNotBlank(feePostings.getFinReference()) || StringUtils.isNotBlank(feePostings.getCollateralRef())) {
				isMutiValues = true;
				return isMutiValues;
			} else {
				feePostings.setPostAgainst(FinanceConstants.POSTING_AGAINST_LIMIT);
			}
		}
		return isMutiValues;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public FeePostingsDAO getFeePostingsDAO() {
		return feePostingsDAO;
	}

	public void setFeePostingsDAO(FeePostingsDAO feePostingsDAO) {
		this.feePostingsDAO = feePostingsDAO;
	}

	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public AccountingSetDAO getAccountingSetDAO() {
		return accountingSetDAO;
	}

	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setCollateralSetupDAO(CollateralSetupDAO collateralSetupDAO) {
		this.collateralSetupDAO = collateralSetupDAO;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	public void setLimitHeaderDAO(LimitHeaderDAO limitHeaderDAO) {
		this.limitHeaderDAO = limitHeaderDAO;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}
	
	
}