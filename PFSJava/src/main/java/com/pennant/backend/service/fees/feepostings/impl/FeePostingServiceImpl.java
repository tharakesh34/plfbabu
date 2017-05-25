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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.fees.FeePostingsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.fees.FeePostings;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.fees.feepostings.FeePostingService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.exception.PFFInterfaceException;
import com.pennanttech.pff.core.Literal;
import com.rits.cloning.Cloner;

public class FeePostingServiceImpl extends GenericService<FeePostings> implements FeePostingService {
	private final static Logger		logger	= Logger.getLogger(FeePostingServiceImpl.class);

	private AuditHeaderDAO			auditHeaderDAO;
	private FeePostingsDAO			feePostingsDAO;
	private AccountEngineExecution	engineExecution;
	private PostingsDAO				postingsDAO;
	private FinanceMainDAO 		   	financeMainDAO;
	private AccountingSetDAO        accountingSetDAO;

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
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
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
							.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (feePostings.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is
					if (befFeePostings != null || tempfeePostings != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befFeePostings == null || tempfeePostings != null) {
						auditDetail.setErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!feePostings.isWorkflow()) { // With out Work flow for update and delete
				if (befFeePostings == null) { // if records not exists in the main table
					auditDetail
							.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldfeePostings != null
							&& !oldfeePostings.getLastMntOn().equals(befFeePostings.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm, valueParm));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm, valueParm));
						}
					}
				}
			} else {
				if (tempfeePostings == null) { // if records not exists in the Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (tempfeePostings != null && oldfeePostings != null
						&& !oldfeePostings.getLastMntOn().equals(tempfeePostings.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
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
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws PFFInterfaceException {
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
		getFeePostingsDAO().delete(feePostings, "_Temp");

		auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
				feePostings.getBefImage(), feePostings));
		auditHeader.setAuditDetails(auditDetailList);
		getAuditHeaderDAO().addAudit(auditHeader);

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
	public AuditHeader executeAccountingProcess(AuditHeader auditHeader, Date curBDay) throws PFFInterfaceException {
		logger.debug("Entering");

		long linkedTranId = Long.MIN_VALUE;
		List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();

		FeePostings feePostings = new FeePostings("");
		BeanUtils.copyProperties((FeePostings) auditHeader.getAuditDetail().getModelData(), feePostings);

		try {

			if (feePostings.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {

				AEEvent aeEvent = new AEEvent();
				aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_MANFEE);
				AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

				// If Fee postings Created Against Finance Reference
				if (StringUtils.equals(FinanceConstants.POSTING_AGAINST_LOAN, feePostings.getPostAgainst())) {
					FinanceMain financeMain=getFinanceMainDAO().getFinanceMainForBatch(feePostings.getReference());
					if (amountCodes == null) {
						amountCodes = new AEAmountCodes();
					}

					amountCodes.setFinType(financeMain.getFinType());
					amountCodes.setPartnerBankAc(getFeePostings().getPartnerBankAc());
					aeEvent.setBranch(financeMain.getFinBranch());
					aeEvent.setCustID(financeMain.getCustID());
				}
				
				//if fees created against customer
				if (StringUtils.equals(FinanceConstants.POSTING_AGAINST_CUST, feePostings.getPostAgainst())) {
					if (amountCodes == null) {
						amountCodes = new AEAmountCodes();
					}
					//FIXME:To send any additional data
				}
				
				//if fees created against Collateral
				if (StringUtils.equals(FinanceConstants.POSTING_AGAINST_COLLATERAL, feePostings.getPostAgainst())) {
					if (amountCodes == null) {
						amountCodes = new AEAmountCodes();
					}
					//FIXME:To send any additional data
				}
				
				amountCodes.setPartnerBankAc(feePostings.getPartnerBankAc());
				amountCodes.setPartnerBankAcType(feePostings.getPartnerBankAcType());
				aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
				aeEvent.setFinReference(feePostings.getReference());
				aeEvent.setCcy(feePostings.getCurrency());

				feePostings.getDeclaredFieldValues(aeEvent.getDataMap());
				aeEvent.getAcSetIDList().add(Long.valueOf(feePostings.getAccountSetId()));
				
				aeEvent.getAcSetIDList().add(getAccountingSetDAO().getAccountingSetId(AccountEventConstants.ACCEVENT_MANFEE,
						AccountEventConstants.ACCEVENT_MANFEE));
				list = getEngineExecution().getAccEngineExecResults(aeEvent).getReturnDataSet();

			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
			errorDetails.add(new ErrorDetails("Accounting Engine", PennantConstants.ERR_UNDEF, "E",
					"Accounting Engine Failed to Create Postings:" + e.getMessage(), new String[] {}, new String[] {}));
			auditHeader.setErrorList(errorDetails);
			list = null;
		}

		if (list != null && !list.isEmpty()) {

			// Method for validating Postings with interface program and
			// return results
			if (list.get(0).getLinkedTranId() == Long.MIN_VALUE || list.get(0).getLinkedTranId() == 0) {
				linkedTranId = getPostingsDAO().getLinkedTransId();
			} else {
				linkedTranId = list.get(0).getLinkedTranId();
			}

			//Method for Checking for Reverse Calculations Based upon Negative Amounts
			for (ReturnDataSet returnDataSet : list) {

				returnDataSet.setLinkedTranId(linkedTranId);

				if (returnDataSet.getPostAmount().compareTo(BigDecimal.ZERO) < 0) {

					String tranCode = returnDataSet.getTranCode();
					String revTranCode = returnDataSet.getRevTranCode();
					String debitOrCredit = returnDataSet.getDrOrCr();

					returnDataSet.setTranCode(revTranCode);
					returnDataSet.setRevTranCode(tranCode);

					returnDataSet.setPostAmount(returnDataSet.getPostAmount().negate());

					if (debitOrCredit.equals(AccountConstants.TRANTYPE_CREDIT)) {
						returnDataSet.setDrOrCr(AccountConstants.TRANTYPE_DEBIT);
					} else {
						returnDataSet.setDrOrCr(AccountConstants.TRANTYPE_CREDIT);
					}
				}
			}


			if (list != null && list.size() > 0) {
				ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
				for (int i = 0; i < list.size(); i++) {
					ReturnDataSet set = list.get(i);
					set.setLinkedTranId(linkedTranId);
					set.setPostDate(curBDay);
					if (!("0000".equals(StringUtils.trimToEmpty(set.getErrorId()))
							|| StringUtils.isEmpty(StringUtils.trimToEmpty(set.getErrorId())))) {

						errorDetails.add(new ErrorDetails(set.getAccountType(), set.getErrorId(), "E",
								set.getErrorMsg() + " " + PennantApplicationUtil.formatAccountNumber(set.getAccount()),
								new String[] {}, new String[] {}));
					} else {
						set.setPostStatus(AccountConstants.POSTINGS_SUCCESS);
					}
				}
				auditHeader.setErrorList(errorDetails);
			}
		}

		if (auditHeader.getErrorMessage() == null || auditHeader.getErrorMessage().size() == 0) {

			// save Postings
			if (list != null && !list.isEmpty()) {
				getPostingsDAO().saveBatch(list);
			}
		}

		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		// TODO Auto-generated method stub
		return null;
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

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
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

}