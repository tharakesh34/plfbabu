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
 * FileName    		:  ManualAdviseServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-04-2017    														*
 *                                                                  						*
 * Modified Date    :  21-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-04-2017       PENNANT	                 0.1                                            * 
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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.feetype.FeeType;
import com.pennant.backend.model.finance.AdviseDueTaxDetail;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinTaxDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>ManualAdvise</b>.<br>
 */
public class ManualAdviseServiceImpl extends GenericService<ManualAdvise> implements ManualAdviseService {
	private static final Logger logger = Logger.getLogger(ManualAdviseServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ManualAdviseDAO manualAdviseDAO;

	private FeeTypeService feeTypeService;
	private FinanceDetailService financeDetailService;
	private FinFeeDetailService finFeeDetailService;
	private PostingsPreparationUtil postingsPreparationUtil;
	private GSTInvoiceTxnService gstInvoiceTxnService;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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
	 * @return the manualAdviseDAO
	 */
	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}

	/**
	 * @param manualAdviseDAO
	 *            the manualAdviseDAO to set
	 */
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table ManualAdvise/ManualAdvise_Temp
	 * by using ManualAdviseDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using ManualAdviseDAO's update method 3) Audit the record in to AuditHeader and AdtManualAdvise
	 * by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ManualAdvise manualAdvise = (ManualAdvise) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (manualAdvise.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (manualAdvise.isNew()) {
			manualAdvise.setId(Long.parseLong(getManualAdviseDAO().save(manualAdvise, tableType)));
			auditHeader.getAuditDetail().setModelData(manualAdvise);
			auditHeader.setAuditReference(String.valueOf(manualAdvise.getAdviseID()));
		} else {
			getManualAdviseDAO().update(manualAdvise, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * ManualAdvise by using ManualAdviseDAO's delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and AdtManualAdvise by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ManualAdvise manualAdvise = (ManualAdvise) auditHeader.getAuditDetail().getModelData();
		getManualAdviseDAO().delete(manualAdvise, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getManualAdvise fetch the details by using ManualAdviseDAO's getManualAdviseById method.
	 * 
	 * @param adviseID
	 *            adviseID of the ManualAdvise.
	 * @return ManualAdvise
	 */
	@Override
	public ManualAdvise getManualAdviseById(long adviseID) {
		ManualAdvise manualAdvise = getManualAdviseDAO().getManualAdviseById(adviseID, "_View");
		getAdviceFeeType(manualAdvise);
		return manualAdvise;
	}

	/**
	 * getApprovedManualAdviseById fetch the details by using ManualAdviseDAO's getManualAdviseById method . with
	 * parameter id and type as blank. it fetches the approved records from the ManualAdvise.
	 * 
	 * @param adviseID
	 *            adviseID of the ManualAdvise. (String)
	 * @return ManualAdvise
	 */
	public ManualAdvise getApprovedManualAdvise(long adviseID) {
		return getManualAdviseDAO().getManualAdviseById(adviseID, "_AView");
	}

	@Override
	public String getTaxComponent(Long adviseID, String type) {
		return getManualAdviseDAO().getTaxComponent(adviseID, type);
	}

	/**
	 * Getting advice fee type.
	 * 
	 * @param manualAdvise
	 * @return
	 */
	@Override
	public ManualAdvise getAdviceFeeType(ManualAdvise manualAdvise) {
		FeeType javaFeeType = this.feeTypeService.getApprovedFeeTypeById(manualAdvise.getFeeTypeID());
		com.pennant.backend.model.finance.FeeType modelFeeType = new com.pennant.backend.model.finance.FeeType();
		BeanUtils.copyProperties(javaFeeType, modelFeeType);
		manualAdvise.setFeeType(modelFeeType);
		return manualAdvise;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getManualAdviseDAO().delete with
	 * parameters manualAdvise,"" b) NEW Add new record in to main table by using getManualAdviseDAO().save with
	 * parameters manualAdvise,"" c) EDIT Update record in the main table by using getManualAdviseDAO().update with
	 * parameters manualAdvise,"" 3) Delete the record from the workFlow table by using getManualAdviseDAO().delete with
	 * parameters manualAdvise,"_Temp" 4) Audit the record in to AuditHeader and AdtManualAdvise by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtManualAdvise by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ManualAdvise manualAdvise = new ManualAdvise();
		BeanUtils.copyProperties((ManualAdvise) auditHeader.getAuditDetail().getModelData(), manualAdvise);

		// Processing Accounting Details
		if (StringUtils.equals(manualAdvise.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			manualAdvise = executeAccountingProcess(manualAdvise, auditHeader.getAuditBranchCode());
		}

		if (StringUtils.equals(manualAdvise.getFinSource(), UploadConstants.FINSOURCE_ID_PFF)) {
			getManualAdviseDAO().delete(manualAdvise, TableType.TEMP_TAB);
		}

		if (!PennantConstants.RECORD_TYPE_NEW.equals(manualAdvise.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(manualAdviseDAO.getManualAdviseById(manualAdvise.getAdviseID(), ""));
		}

		if (manualAdvise.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getManualAdviseDAO().delete(manualAdvise, TableType.MAIN_TAB);
		} else {
			manualAdvise.setRoleCode("");
			manualAdvise.setNextRoleCode("");
			manualAdvise.setTaskId("");
			manualAdvise.setNextTaskId("");
			manualAdvise.setWorkflowId(0);

			if (manualAdvise.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				manualAdvise.setRecordType("");
				getManualAdviseDAO().save(manualAdvise, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				manualAdvise.setRecordType("");
				getManualAdviseDAO().update(manualAdvise, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(manualAdvise);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getManualAdviseDAO().delete with parameters manualAdvise,"_Temp" 3) Audit the record in
	 * to AuditHeader and AdtManualAdvise by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ManualAdvise manualAdvise = (ManualAdvise) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getManualAdviseDAO().delete(manualAdvise, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Method for Execute posting Details on Core Banking Side
	 * 
	 * @param auditHeader
	 * @param appDate
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	private ManualAdvise executeAccountingProcess(ManualAdvise advise, String postBranch) {
		logger.debug(Literal.ENTERING);

		AEEvent aeEvent = prepareAccSetData(advise, postBranch);
		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);

		if (!aeEvent.isPostingSucess()) {
			throw new InterfaceException("9998", "Advise Due accounting postings failed. Please ");
		}

		long linkedTranId = aeEvent.getLinkedTranId();

		// Resetting Advise Data
		advise.setLinkedTranId(linkedTranId);
		advise.setDueCreation(true);

		// GST Invoice Preparation for Receivable Advise/ Bounce
		ManualAdviseMovements advMovement = new ManualAdviseMovements();
		FinanceDetail financeDetailTemp = null;

		advMovement.setFeeTypeCode(advise.getFeeTypeCode());
		advMovement.setFeeTypeDesc(advise.getFeeTypeDesc());
		advMovement.setMovementAmount(advise.getAdviseAmount());
		advMovement.setTaxApplicable(advise.isTaxApplicable());
		advMovement.setTaxComponent(advise.getTaxComponent());

		AdviseDueTaxDetail detail = new AdviseDueTaxDetail();

		Map<String, Object> dataMap = aeEvent.getDataMap();

		if (dataMap.containsKey("ae_feeGST_TOT")) {
			detail.setAmount(new BigDecimal(dataMap.get("ae_feeGST_TOT").toString()));
		} else {
			detail.setAmount(BigDecimal.ZERO);
		}

		if (dataMap.containsKey("ae_feeCGST")) {
			detail.setCGST(new BigDecimal(dataMap.get("ae_feeCGST").toString()));
		} else {
			detail.setCGST(BigDecimal.ZERO);
		}

		if (dataMap.containsKey("ae_feeSGST")) {
			detail.setSGST(new BigDecimal(dataMap.get("ae_feeSGST").toString()));
		} else {
			detail.setSGST(BigDecimal.ZERO);
		}

		if (dataMap.containsKey("ae_feeUGST")) {
			detail.setUGST(new BigDecimal(dataMap.get("ae_feeUGST").toString()));
		} else {
			detail.setUGST(BigDecimal.ZERO);
		}

		if (dataMap.containsKey("ae_feeIGST")) {
			detail.setIGST(new BigDecimal(dataMap.get("ae_feeIGST").toString()));
		} else {
			detail.setIGST(BigDecimal.ZERO);
		}

		if (dataMap.containsKey("ae_feeCESS")) {
			detail.setCESS(new BigDecimal(dataMap.get("ae_feeCESS").toString()));
		} else {
			detail.setCESS(BigDecimal.ZERO);
		}

		BigDecimal gstAmount = detail.getCGST().add(detail.getSGST()).add(detail.getIGST()).add(detail.getUGST())
				.add(detail.getCESS());
		detail.setTotalGST(gstAmount);

		if (BigDecimal.ZERO.compareTo(gstAmount) == 0) {
			advMovement.setPaidAmount(advise.getAdviseAmount());
		} else {
			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(advMovement.getTaxComponent())) {
				advMovement.setPaidAmount(advise.getAdviseAmount().subtract(gstAmount));
			} else {
				advMovement.setPaidAmount(advise.getAdviseAmount());
			}
		}
		advMovement.setPaidCGST(detail.getCGST());
		advMovement.setPaidSGST(detail.getSGST());
		advMovement.setPaidIGST(detail.getIGST());
		advMovement.setPaidUGST(detail.getUGST());

		// GST Invoice data resetting based on Accounting Process
		String isGSTInvOnDue = SysParamUtil.getValueAsString("GST_INV_ON_DUE");

		if (gstAmount.compareTo(BigDecimal.ZERO) > 0 && StringUtils.equals(isGSTInvOnDue, PennantConstants.YES)) {
			List<ManualAdviseMovements> advMovements = new ArrayList<ManualAdviseMovements>();
			advMovements.add(advMovement);
			if (financeDetailTemp == null) {
				financeDetailTemp = financeDetailService.getFinSchdDetailById(advise.getFinReference(), "", false);
			}

			this.gstInvoiceTxnService.gstInvoicePreparation(linkedTranId, financeDetailTemp, null, advMovements,
					PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT, false, false);

			// saving Due tax advice details
			saveDueTaxDetail(advise, detail);
		}
		logger.debug(Literal.LEAVING);
		return advise;
	}

	/**
	 * Saving Due tax advice details
	 * 
	 * @param advise
	 */
	private void saveDueTaxDetail(ManualAdvise advise, AdviseDueTaxDetail detail) {

		detail.setAdviseID(advise.getAdviseID());
		detail.setTaxType(advise.getTaxComponent());
		detail.setAmount(advise.getAdviseAmount());

		detail.setTotalGST(detail.getCGST().add(detail.getSGST()).add(detail.getIGST()).add(detail.getUGST())
				.add(detail.getCESS()));

		// Saving Tax Details
		getManualAdviseDAO().saveDueTaxDetail(detail);
	}

	/**
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 * 
	 */
	@Override
	public List<ReturnDataSet> getAccountingSetEntries(ManualAdvise manualAdvise) throws Exception {
		logger.debug(Literal.ENTERING);

		AEEvent aeEvent = prepareAccSetData(manualAdvise, "");
		logger.debug(Literal.LEAVING);

		return postingsPreparationUtil.getAccounting(aeEvent).getReturnDataSet();
	}

	/**
	 * 
	 * @param manualAdvise
	 * @return
	 */
	private AEEvent prepareAccSetData(ManualAdvise manualAdvise, String postBranch) {
		logger.debug(Literal.ENTERING);

		boolean taxApplicable = manualAdvise.isTaxApplicable();
		String taxComp = manualAdvise.getTaxComponent();
		BigDecimal adviseAmount = manualAdvise.getAdviseAmount();

		AEEvent aeEvent = new AEEvent();

		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_ADVDUE);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		// Finance main
		FinanceMain financeMain = getFinanceDetails(manualAdvise.getFinReference());
		amountCodes.setFinType(financeMain.getFinType());

		aeEvent.setPostingUserBranch(postBranch);
		aeEvent.setValueDate(manualAdvise.getValueDate());
		aeEvent.setPostDate(manualAdvise.getPostDate());
		aeEvent.setEntityCode(financeMain.getEntityCode());

		aeEvent.setBranch(financeMain.getFinBranch());
		aeEvent.setCustID(financeMain.getCustID());
		aeEvent.setCcy(financeMain.getFinCcy());
		aeEvent.setFinReference(financeMain.getFinReference());
		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
		Map<String, Object> eventMapping = aeEvent.getDataMap();

		if (taxApplicable) {
			FinanceDetail financeDetail = this.financeDetailService.getFinSchdDetailById(financeMain.getFinReference(),
					"", false);
			Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(financeMain.getFinReference());

			if (financeDetail != null) {
				FinFeeDetail finFeeDetail = new FinFeeDetail();
				FinTypeFees finTypeFee = new FinTypeFees();

				finFeeDetail.setCalculatedAmount(adviseAmount);
				finFeeDetail.setTaxComponent(taxComp);
				finFeeDetail.setTaxApplicable(taxApplicable);
				finTypeFee.setTaxComponent(taxComp);
				finTypeFee.setTaxApplicable(taxApplicable);
				finTypeFee.setAmount(adviseAmount);

				this.finFeeDetailService.convertGSTFinTypeFees(finFeeDetail, finTypeFee, financeDetail, taxPercentages);
				this.finFeeDetailService.calculateFees(finFeeDetail, financeMain, taxPercentages);

				if (finFeeDetail.getFinTaxDetails() != null) {
					FinTaxDetails finTaxDetails = finFeeDetail.getFinTaxDetails();

					// CESS Amount
					List<Taxes> taxDetails = finFeeDetail.getTaxHeader().getTaxDetails();
					BigDecimal cessAmount = BigDecimal.ZERO;
					for (Taxes taxes : taxDetails) {
						if (StringUtils.isNotBlank(taxes.getTaxType())
								&& StringUtils.equals(taxes.getTaxType(), RuleConstants.CODE_CESS)) {
							cessAmount = cessAmount.add(taxes.getNetTax());
						}
					}

					// Total GST
					BigDecimal totalGstAmount = BigDecimal.ZERO;
					totalGstAmount = finTaxDetails.getNetCGST().add(finTaxDetails.getNetIGST())
							.add(finTaxDetails.getNetSGST()).add(finTaxDetails.getNetUGST().add(cessAmount));

					eventMapping.put("ae_feeCGST", finTaxDetails.getNetCGST());
					eventMapping.put("ae_feeSGST", finTaxDetails.getNetSGST());
					eventMapping.put("ae_feeUGST", finTaxDetails.getNetUGST());
					eventMapping.put("ae_feeIGST", finTaxDetails.getNetIGST());
					eventMapping.put("ae_feeCESS", cessAmount);
					eventMapping.put("ae_feeGST_TOT", totalGstAmount);
				}
			}
		}
		
		// GST parameters
		Map<String, Object> gstExecutionMap = GSTCalculator.getGSTDataMap(financeMain.getFinReference());
		if (gstExecutionMap != null) {
			for (String key : gstExecutionMap.keySet()) {
				if (StringUtils.isNotBlank(key)) {
					eventMapping.put(key, gstExecutionMap.get(key));
				}
			}
		}
		
		eventMapping.put("ae_feeAmount", adviseAmount);
		aeEvent.setDataMap(eventMapping);
		aeEvent.getAcSetIDList().add(manualAdvise.getFeeType().getDueAccSet());

		logger.debug(Literal.LEAVING);
		return aeEvent;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getManualAdviseDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public List<ManualAdviseMovements> getAdivseMovements(long id) {
		return getManualAdviseDAO().getAdviseMovements(id);
	}

	@Override
	public FinanceMain getFinanceDetails(String finReference) {
		return manualAdviseDAO.getFinanceDetails(finReference);
	}

	public FeeTypeService getFeeTypeService() {
		return feeTypeService;
	}

	public void setFeeTypeService(FeeTypeService feeTypeService) {
		this.feeTypeService = feeTypeService;
	}

	public FinFeeDetailService getFinFeeDetailService() {
		return finFeeDetailService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public GSTInvoiceTxnService getGstInvoiceTxnService() {
		return gstInvoiceTxnService;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

}