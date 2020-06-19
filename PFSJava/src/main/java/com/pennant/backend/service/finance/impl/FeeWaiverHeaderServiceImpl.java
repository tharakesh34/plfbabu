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
 * FileName    		:  FinCovenantMaintanceServiceImpl.java                                 * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-06-2015    														*
 *                                                                  						*
 * Modified Date    :  11-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-06-2015       Pennant	                 0.1                                            * 
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FeeWaiverDetailDAO;
import com.pennant.backend.dao.finance.FeeWaiverHeaderDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.feetype.FeeType;
import com.pennant.backend.model.finance.FeeWaiverDetail;
import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FeeWaiverHeaderService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.service.finance.TaxHeaderDetailsService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>FeeWaiverHeader</b>.<br>
 * 
 */
public class FeeWaiverHeaderServiceImpl extends GenericService<FeeWaiverHeader> implements FeeWaiverHeaderService {

	private static Logger logger = Logger.getLogger(FeeWaiverHeaderServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FeeWaiverHeaderDAO feeWaiverHeaderDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FeeWaiverDetailDAO feeWaiverDetailDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FeeTypeDAO feeTypeDAO;
	private TaxHeaderDetailsService taxHeaderDetailsService;
	private GSTInvoiceTxnService gstInvoiceTxnService;
	private FinanceTypeDAO financeTypeDAO;

	private List<ManualAdvise> manualAdviseList; // TODO remove this
	private ReceiptCalculator receiptCalculator;
	private RepaymentPostingsUtil repayPostingUtil;
	private FinanceProfitDetailDAO profitDetailsDAO;

	public FeeWaiverHeaderServiceImpl() {
		super();
	}

	@Override
	public FeeWaiverHeader getFeeWaiverByFinRef(FeeWaiverHeader feeWaiverHeader) {
		logger.debug(Literal.ENTERING);

		String finReference = feeWaiverHeader.getFinReference();

		if (!feeWaiverHeader.isNew()) {
			feeWaiverHeader = getFeeWaiverHeaderByFinRef(finReference, "_TView");
		} else {
			FeeWaiverDetail feeWaiverDetail;
			BigDecimal receivableAmt = BigDecimal.ZERO;
			BigDecimal receivedAmt = BigDecimal.ZERO;
			BigDecimal waivedAmt = BigDecimal.ZERO;
			List<FeeWaiverDetail> detailList = new ArrayList<FeeWaiverDetail>();

			FinReceiptHeader receiptHeader = finReceiptHeaderDAO.getReceiptHeaderByRef(finReference,
					RepayConstants.RECEIPTTYPE_RECIPT, "_Temp");

			if (receiptHeader != null) {
				feeWaiverHeader.setAlwtoProceed(false);
				return feeWaiverHeader;
			}

			// For GST Calculations
			Map<String, BigDecimal> gstPercentages = getTaxPercentages(finReference);

			// Manual Advise and Bounce Waivers
			List<ManualAdvise> adviseList = manualAdviseDAO.getManualAdvise(finReference);

			if (CollectionUtils.isNotEmpty(adviseList)) {
				for (ManualAdvise manualAdvise : adviseList) {

					BigDecimal recAmount = manualAdvise.getAdviseAmount().subtract(manualAdvise.getWaivedAmount());

					if (manualAdvise.getBounceID() != 0) {
						receivableAmt = receivableAmt.add(recAmount);
						receivedAmt = receivedAmt.add(manualAdvise.getPaidAmount());
						waivedAmt = waivedAmt.add(manualAdvise.getWaivedAmount());
					} else {
						feeWaiverDetail = new FeeWaiverDetail();
						feeWaiverDetail.setFinReference(finReference);
						feeWaiverDetail.setNewRecord(true);
						feeWaiverDetail.setAdviseId(manualAdvise.getAdviseID());
						feeWaiverDetail.setFeeTypeCode(manualAdvise.getFeeTypeCode());
						feeWaiverDetail.setFeeTypeDesc(manualAdvise.getFeeTypeDesc());
						feeWaiverDetail.setReceivedAmount(manualAdvise.getPaidAmount());
						feeWaiverDetail.setWaivedAmount(manualAdvise.getWaivedAmount());
						feeWaiverDetail.setTaxApplicable(manualAdvise.isTaxApplicable());
						feeWaiverDetail.setTaxComponent(manualAdvise.getTaxComponent());

						prepareGST(feeWaiverDetail, recAmount, gstPercentages);

						feeWaiverDetail.setBalanceAmount(
								feeWaiverDetail.getReceivableAmount().subtract(feeWaiverDetail.getCurrWaiverAmount()));

						detailList.add(feeWaiverDetail);
					}
				}
				// get Bounce charges
				feeWaiverDetail = new FeeWaiverDetail();
				feeWaiverDetail.setFinReference(finReference);
				feeWaiverDetail.setNewRecord(true);
				feeWaiverDetail.setAdviseId(-3);
				feeWaiverDetail.setFeeTypeCode(RepayConstants.ALLOCATION_BOUNCE);
				FeeType bounce = this.feeTypeDAO.getApprovedFeeTypeByFeeCode(RepayConstants.ALLOCATION_BOUNCE);
				if (bounce != null) {
					feeWaiverDetail.setFeeTypeDesc(bounce.getFeeTypeDesc());
					feeWaiverDetail.setTaxApplicable(bounce.isTaxApplicable());
					feeWaiverDetail.setTaxComponent(bounce.getTaxComponent());
				} else {
					feeWaiverDetail.setFeeTypeDesc(Labels.getLabel("label_ReceiptDialog_BounceCharge.value"));
				}

				prepareGST(feeWaiverDetail, receivableAmt, gstPercentages);

				feeWaiverDetail.setReceivedAmount(receivedAmt);
				feeWaiverDetail.setWaivedAmount(waivedAmt);
				feeWaiverDetail.setBalanceAmount(
						feeWaiverDetail.getReceivableAmount().subtract(feeWaiverDetail.getCurrWaiverAmount()));
				detailList.add(feeWaiverDetail);
			}
			receivableAmt = BigDecimal.ZERO;
			receivedAmt = BigDecimal.ZERO;
			waivedAmt = BigDecimal.ZERO;
			Date reqMaxODDate = SysParamUtil.getAppDate();
			// Late Pay Penalty Waiver
			List<FinODDetails> finODPenaltyList = finODDetailsDAO.getFinODPenalityByFinRef(finReference, false, true);

			if (CollectionUtils.isNotEmpty(finODPenaltyList)) {
				for (FinODDetails finoddetails : finODPenaltyList) {
					//lpi amount getting  crossed schedule date.
					if (finoddetails.getFinODSchdDate().compareTo(reqMaxODDate) > 0) {
						break;
					}

					/**
					 * While doing back dated receipt, application set the TotPenaltyAmt, TotPenaltyPaid, TotPenaltyBal
					 * values to zero. But application not set TOTWAIVED to zero, If any waiver happened before doing
					 * the receipt. This is the issue need to address in Receipt level. Because of this issue in waiver
					 * screen net balance amount coming as zero.We handled this negative scenario with this below code.
					 */
					if ((finoddetails.getTotPenaltyAmt().compareTo(BigDecimal.ZERO) == 0)
							&& (finoddetails.getTotPenaltyPaid().compareTo(BigDecimal.ZERO) == 0)
							&& (finoddetails.getTotPenaltyBal().compareTo(BigDecimal.ZERO) == 0)) {
						continue;
					}

					receivableAmt = receivableAmt
							.add(finoddetails.getTotPenaltyAmt().subtract(finoddetails.getTotWaived()));

					receivedAmt = receivedAmt.add(finoddetails.getTotPenaltyPaid());
					waivedAmt = waivedAmt.add(finoddetails.getTotWaived());
				}
				feeWaiverDetail = new FeeWaiverDetail();
				feeWaiverDetail.setFinReference(finReference);
				feeWaiverDetail.setNewRecord(true);
				feeWaiverDetail.setAdviseId(-1);
				feeWaiverDetail.setFeeTypeCode(RepayConstants.ALLOCATION_ODC);

				FeeType lpp = this.feeTypeDAO.getApprovedFeeTypeByFeeCode(RepayConstants.ALLOCATION_ODC);
				if (lpp != null) {
					feeWaiverDetail.setFeeTypeDesc(lpp.getFeeTypeDesc());
					feeWaiverDetail.setTaxApplicable(lpp.isTaxApplicable());
					feeWaiverDetail.setTaxComponent(lpp.getTaxComponent());
				} else {
					feeWaiverDetail.setFeeTypeDesc(Labels.getLabel("label_feeWaiver_WaiverType_ODC"));
				}

				prepareGST(feeWaiverDetail, receivableAmt, gstPercentages);

				feeWaiverDetail.setReceivedAmount(receivedAmt);
				feeWaiverDetail.setWaivedAmount(waivedAmt);
				feeWaiverDetail.setBalanceAmount(
						feeWaiverDetail.getReceivableAmount().subtract(feeWaiverDetail.getCurrWaiverAmount()));
				detailList.add(feeWaiverDetail);
			}

			receivableAmt = BigDecimal.ZERO;
			receivedAmt = BigDecimal.ZERO;
			waivedAmt = BigDecimal.ZERO;
			List<FinODDetails> finODProfitList = finODDetailsDAO.getFinODPenalityByFinRef(finReference, true, true);

			// Late pay profit Waivers
			if (CollectionUtils.isNotEmpty(finODProfitList)) {
				for (FinODDetails finoddetails : finODProfitList) {
					// lpp amount getting crossed schedule date.
					if (finoddetails.getFinODSchdDate().compareTo(reqMaxODDate) > 0) {
						break;
					}
					receivableAmt = receivableAmt.add(finoddetails.getLPIAmt().subtract(finoddetails.getLPIWaived()));
					receivedAmt = receivedAmt.add(finoddetails.getLPIPaid());
					waivedAmt = waivedAmt.add(finoddetails.getLPIWaived());
				}

				feeWaiverDetail = new FeeWaiverDetail();
				feeWaiverDetail.setFinReference(finReference);
				feeWaiverDetail.setNewRecord(true);
				feeWaiverDetail.setAdviseId(-2);
				feeWaiverDetail.setFeeTypeCode(RepayConstants.ALLOCATION_LPFT);
				feeWaiverDetail.setFeeTypeDesc(Labels.getLabel("label_feeWaiver_WaiverType_LPFT"));
				feeWaiverDetail.setReceivableAmount(receivableAmt);
				feeWaiverDetail.setReceivedAmount(receivedAmt);
				feeWaiverDetail.setWaivedAmount(waivedAmt);

				prepareGST(feeWaiverDetail, receivableAmt, gstPercentages);

				feeWaiverDetail.setBalanceAmount(
						feeWaiverDetail.getReceivableAmount().subtract(feeWaiverDetail.getCurrWaiverAmount()));

				detailList.add(feeWaiverDetail);
			}

			// Schedule Profit Waiver
			if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_PROFIT_WAIVER)) {
				// Get Interest and profits.
				List<FinanceScheduleDetail> financeScheduleDetails = this.financeScheduleDetailDAO
						.getFinScheduleDetails(finReference, "", false);

				receivableAmt = BigDecimal.ZERO;
				receivedAmt = BigDecimal.ZERO;
				waivedAmt = BigDecimal.ZERO;

				Date appDate = SysParamUtil.getAppDate();
				for (FinanceScheduleDetail detail : financeScheduleDetails) {
					if (detail.getSchDate().compareTo(appDate) > 0) {
						break;
					}
					if ((detail.getProfitSchd()).compareTo(detail.getSchdPftPaid()) > 0) {
						receivableAmt = receivableAmt.add(detail.getProfitSchd());
						receivedAmt = receivedAmt.add(detail.getSchdPftPaid());
						waivedAmt = waivedAmt.add(detail.getSchdPftWaiver());
					}
				}

				feeWaiverDetail = new FeeWaiverDetail();
				feeWaiverDetail.setFinReference(finReference);
				feeWaiverDetail.setNewRecord(true);
				feeWaiverDetail.setAdviseId(-4);
				feeWaiverDetail.setFeeTypeCode(RepayConstants.ALLOCATION_PFT);
				feeWaiverDetail.setFeeTypeDesc(Labels.getLabel("label_feeWaiver_WaiverType_Interest"));
				feeWaiverDetail.setReceivableAmount(receivableAmt);
				feeWaiverDetail.setReceivedAmount(receivedAmt);
				feeWaiverDetail.setWaivedAmount(waivedAmt);

				prepareGST(feeWaiverDetail, receivableAmt, gstPercentages);

				feeWaiverDetail.setBalanceAmount(
						feeWaiverDetail.getReceivableAmount().subtract(feeWaiverDetail.getCurrWaiverAmount()));
				feeWaiverDetail.setWaiverType(RepayConstants.INTEREST_WAIVER);
				detailList.add(feeWaiverDetail);
			}

			feeWaiverHeader.setFeeWaiverDetails(detailList);
		}

		logger.debug(Literal.LEAVING);

		return feeWaiverHeader;
	}

	private void prepareGST(FeeWaiverDetail feeWaiverDetail, BigDecimal receivableAmt,
			Map<String, BigDecimal> gstPercentages) {
		logger.debug(Literal.ENTERING);

		if (feeWaiverDetail.isTaxApplicable()) {
			TaxAmountSplit taxSplit = null;
			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(feeWaiverDetail.getTaxComponent())) {
				taxSplit = GSTCalculator.getExclusiveGST(receivableAmt, gstPercentages);
				feeWaiverDetail.setActualReceivable(receivableAmt);
			} else {
				taxSplit = GSTCalculator.getInclusiveGST(receivableAmt, gstPercentages);
				feeWaiverDetail.setActualReceivable(receivableAmt.subtract(taxSplit.gettGST()));
			}
			feeWaiverDetail.setReceivableGST(taxSplit.gettGST());
			feeWaiverDetail.setReceivableAmount(taxSplit.getNetAmount());

			List<Taxes> taxes = new ArrayList<>();

			for (String gstType : gstPercentages.keySet()) {
				Taxes tax = new Taxes();

				if (RuleConstants.CODE_CGST.equals(gstType)) {
					tax.setActualTax(taxSplit.getcGST());
				} else if (RuleConstants.CODE_SGST.equals(gstType)) {
					tax.setActualTax(taxSplit.getsGST());
				} else if (RuleConstants.CODE_IGST.equals(gstType)) {
					tax.setActualTax(taxSplit.getiGST());
				} else if (RuleConstants.CODE_UGST.equals(gstType)) {
					tax.setActualTax(taxSplit.getuGST());
				} else if (RuleConstants.CODE_CESS.equals(gstType)) {
					tax.setActualTax(taxSplit.getCess());
				} else {
					continue;
				}

				tax.setTaxPerc(gstPercentages.get(gstType));
				tax.setNetTax(tax.getActualTax().subtract(tax.getWaivedTax()));
				tax.setRemFeeTax(tax.getNetTax().subtract(tax.getPaidTax()));

				tax.setTaxType(gstType);
				tax.setNewRecord(true);
				tax.setRecordType(PennantConstants.RCD_ADD);
				tax.setVersion(1);
				taxes.add(tax);
			}

			TaxHeader taxHeader = new TaxHeader();
			taxHeader.setNewRecord(true);
			taxHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			taxHeader.setVersion(1);
			taxHeader.setTaxDetails(taxes);
			feeWaiverDetail.setTaxHeader(taxHeader);
		} else {
			feeWaiverDetail.setActualReceivable(receivableAmt);
			feeWaiverDetail.setReceivableAmount(receivableAmt);
			feeWaiverDetail.setReceivableGST(BigDecimal.ZERO);
		}

		logger.debug(Literal.LEAVING);
	}

	public FeeWaiverHeader getFeeWiaverEnquiryList(FeeWaiverHeader feeWaiverHeader) {
		List<FeeWaiverDetail> details = feeWaiverDetailDAO.getFeeWaiverEnqDetailList(feeWaiverHeader.getFinReference());
		feeWaiverHeader.setFeeWaiverDetails(details);
		return feeWaiverHeader;
	}

	@Override
	public FeeWaiverHeader getFeeWaiverHeaderByFinRef(String finReference, String type) {
		FeeWaiverHeader feeWaiverHeader = feeWaiverHeaderDAO.getFeeWaiverHeaderByFinRef(finReference, type);

		if (feeWaiverHeader != null) {
			// Fetch Fee Waiver Details
			List<FeeWaiverDetail> feeWaiverDetails = feeWaiverDetailDAO
					.getFeeWaiverByWaiverId(feeWaiverHeader.getWaiverId(), "_Temp");
			feeWaiverHeader.setFeeWaiverDetails(feeWaiverDetails);
			if (CollectionUtils.isNotEmpty(feeWaiverDetails)) {
				for (FeeWaiverDetail feeWaiver : feeWaiverDetails) {
					feeWaiver.setFinReference(finReference);
					if (feeWaiver.getTaxHeaderId() > 0) {
						// Fetch Tax Details
						TaxHeader header = taxHeaderDetailsService.getTaxHeaderById(feeWaiver.getTaxHeaderId(),
								"_Temp");
						feeWaiver.setTaxHeader(header);
					}
				}
			}
		}

		return feeWaiverHeader;
	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public FeeWaiverDetailDAO getFeeWaiverDetailDAO() {
		return feeWaiverDetailDAO;
	}

	public void setFeeWaiverDetailDAO(FeeWaiverDetailDAO feeWaiverDetailDAO) {
		this.feeWaiverDetailDAO = feeWaiverDetailDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * FeeWaiverHeader/FeeWaiverHeader_Temp by using FeeWaiverHeaderDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using FeeWaiverHeaderDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtBMTFeeWaiverHeader by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FeeWaiverHeader feeWaiverHeader = (FeeWaiverHeader) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (feeWaiverHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (feeWaiverHeader.isNew()) {
			feeWaiverHeader.setWaiverId(Long.parseLong(getFeeWaiverHeaderDAO().save(feeWaiverHeader, tableType)));
			auditHeader.getAuditDetail().setModelData(feeWaiverHeader);
			auditHeader.setAuditReference(String.valueOf(feeWaiverHeader.getWaiverId()));
		} else {
			getFeeWaiverHeaderDAO().update(feeWaiverHeader, tableType);
		}

		if (feeWaiverHeader.getFeeWaiverDetails() != null && !feeWaiverHeader.getFeeWaiverDetails().isEmpty()) {
			List<AuditDetail> details = feeWaiverHeader.getAuditDetailMap().get("FeeWaiverDetails");
			for (FeeWaiverDetail feewaiver : feeWaiverHeader.getFeeWaiverDetails()) {
				feewaiver.setWaiverId(feeWaiverHeader.getWaiverId());
				TaxHeader taxHeader = feewaiver.getTaxHeader();
				if (taxHeader != null && CollectionUtils.isNotEmpty(taxHeader.getTaxDetails())) {
					taxHeaderDetailsService.saveOrUpdate(taxHeader, tableType.getSuffix(),
							auditHeader.getAuditTranType());
					feewaiver.setTaxHeaderId(taxHeader.getHeaderId());
				}
			}
			details = processingFeeWaiverdetails(details, tableType);
			auditDetails.addAll(details);
		}

		// Add Audit
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * Method For Preparing List of AuditDetails for Check List Details
	 * 
	 * @param auditDetails
	 * @param vasRecording
	 * @param tableType
	 * @return
	 */
	private List<AuditDetail> processingFeeWaiverdetails(List<AuditDetail> auditDetails, TableType type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			FeeWaiverDetail feeWaiverDetail = (FeeWaiverDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type.getSuffix())) {
				approveRec = true;
				feeWaiverDetail.setRoleCode("");
				feeWaiverDetail.setNextRoleCode("");
				feeWaiverDetail.setTaskId("");
				feeWaiverDetail.setNextTaskId("");
				feeWaiverDetail.setWorkflowId(0);
			}

			if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (feeWaiverDetail.isNewRecord()) {
				saveRecord = true;
				if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					feeWaiverDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					feeWaiverDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					feeWaiverDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (feeWaiverDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = feeWaiverDetail.getRecordType();
				recordStatus = feeWaiverDetail.getRecordStatus();
				feeWaiverDetail.setRecordType("");
				feeWaiverDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				feeWaiverDetailDAO.save(feeWaiverDetail, type);
			}

			if (updateRecord) {
				feeWaiverDetailDAO.update(feeWaiverDetail, type);
			}

			if (deleteRecord) {
				feeWaiverDetailDAO.delete(feeWaiverDetail, type);
			}

			if (approveRec) {
				feeWaiverDetail.setRecordType(rcdType);
				feeWaiverDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(feeWaiverDetail);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * FeeWaiverHeader by using FeeWaiverHeaderDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtFeeWaiverHeader by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "delete");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FeeWaiverHeader feeWaiverHeader = (FeeWaiverHeader) auditHeader.getAuditDetail().getModelData();
		getFeeWaiverHeaderDAO().delete(feeWaiverHeader, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFeeWaiverHeaderDAO().delete with
	 * parameters FeeWaiverHeader,"" b) NEW Add new record in to main table by using getFeeWaiverHeaderDAO().save with
	 * parameters FeeWaiverHeader,"" c) EDIT Update record in the main table by using getFeeWaiverHeaderDAO().update
	 * with parameters FeeWaiverHeader,"" 3) Delete the record from the workFlow table by using
	 * getFeeWaiverHeaderDAO().delete with parameters FeeWaiverHeader,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTFeeWaiverHeader by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtBMTFeeWaiverHeader by using auditHeaderDAO.addAudit(auditHeader) based on the transaction
	 * Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws Exception
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) throws Exception {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tranType = "";

		FeeWaiverHeader feeWaiverHeader = new FeeWaiverHeader();
		BeanUtils.copyProperties((FeeWaiverHeader) auditHeader.getAuditDetail().getModelData(), feeWaiverHeader);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(feeWaiverHeader.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(feeWaiverHeaderDAO.getFeeWaiverHeaderById(feeWaiverHeader.getWaiverId(), ""));
		}

		if (feeWaiverHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getFeeWaiverHeaderDAO().delete(feeWaiverHeader, TableType.MAIN_TAB);
		} else {
			feeWaiverHeader.setRoleCode("");
			feeWaiverHeader.setNextRoleCode("");
			feeWaiverHeader.setTaskId("");
			feeWaiverHeader.setNextTaskId("");
			feeWaiverHeader.setWorkflowId(0);

			// Fee Waivers List
			if (CollectionUtils.isNotEmpty(feeWaiverHeader.getFeeWaiverDetails())) {
				for (FeeWaiverDetail details : feeWaiverHeader.getFeeWaiverDetails()) {
					details.setWaiverId(feeWaiverHeader.getWaiverId());
					TaxHeader taxHeader = details.getTaxHeader();
					if (taxHeader != null && CollectionUtils.isNotEmpty(taxHeader.getTaxDetails())) {
						taxHeaderDetailsService.doApprove(taxHeader, TableType.MAIN_TAB.getSuffix(),
								auditHeader.getAuditTranType());
						details.setTaxHeaderId(taxHeader.getHeaderId());
					}

					if (feeWaiverHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						getFeeWaiverDetailDAO().save(details, TableType.MAIN_TAB);
					} else {
						getFeeWaiverDetailDAO().update(details, TableType.MAIN_TAB);
					}
					getFeeWaiverDetailDAO().delete(details, TableType.TEMP_TAB);
				}
			}

			if (feeWaiverHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				feeWaiverHeader.setRecordType("");
				getFeeWaiverHeaderDAO().save(feeWaiverHeader, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				feeWaiverHeader.setRecordType("");
				getFeeWaiverHeaderDAO().update(feeWaiverHeader, TableType.MAIN_TAB);
			}

			// update the waiver amounts to the respective tables
			allocateWaiverAmounts(feeWaiverHeader);
		}

		getFeeWaiverHeaderDAO().delete(feeWaiverHeader, TableType.TEMP_TAB);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(feeWaiverHeader);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	private void allocateWaiverAmounts(FeeWaiverHeader feeWaiverHeader) throws Exception {
		logger.debug("Entering");

		String finReference = feeWaiverHeader.getFinReference();
		List<FinODDetails> finodPftdetails = finODDetailsDAO.getFinODPenalityByFinRef(finReference, true, false);
		List<FinODDetails> finodPenalitydetails = finODDetailsDAO.getFinODPenalityByFinRef(finReference, false, false);

		List<ManualAdviseMovements> movements = new ArrayList<ManualAdviseMovements>();

		// Update ManualAdvise and Bounce Waivers
		movements.addAll(allocateWaiverToBounceAndAdvise(feeWaiverHeader));

		// Update Late Pay Penalty(LPP) and Late Pay Interest(LPI) waivers
		movements.addAll(allocateWaivedAmtToPenalities(feeWaiverHeader, finodPftdetails, finodPenalitydetails));

		FinanceDetail financeDetail = new FinanceDetail();
		FinanceMain financeMain = this.financeMainDAO.getFinanceMainById(finReference, "", false);
		financeDetail.getFinScheduleData().setFinanceMain(financeMain);
		financeDetail.getFinScheduleData()
				.setFinanceType(financeTypeDAO.getFinanceTypeByFinType(financeMain.getFinType()));
		financeDetail.setCustomerDetails(null);
		financeDetail.setFinanceTaxDetail(null);

		boolean isSchdUpdated = false;
		AEEvent aeEvent = null;
		if (CollectionUtils.isNotEmpty(movements)) {

			BigDecimal totPftBal = BigDecimal.ZERO;

			// Total Pft bal
			if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_PROFIT_WAIVER)) {
				totPftBal = getTotPftBal(feeWaiverHeader, financeDetail, aeEvent);
			}

			// Execute Accounting
			aeEvent = executeAcctProcessing(totPftBal, financeMain, feeWaiverHeader, aeEvent, movements);

			// Prepare GST Invoice for Bounce/LPP Waiver(when it is due base accounting)
			if (aeEvent.getLinkedTranId() > 0 && aeEvent.isPostingSucess()) {
				this.gstInvoiceTxnService.gstInvoicePreparation(aeEvent.getLinkedTranId(), financeDetail, null,
						movements, PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT, false, true);

				if (feeWaiverHeader.getRpyList() != null && !feeWaiverHeader.getRpyList().isEmpty()) {
					for (int i = 0; i < feeWaiverHeader.getRpyList().size(); i++) {
						FinanceRepayments repayment = feeWaiverHeader.getRpyList().get(i);
						repayment.setLinkedTranId(aeEvent.getLinkedTranId());
						this.financeRepaymentsDAO.save(repayment, TableType.MAIN_TAB.getSuffix());
					}
				}
			}

			// Profit Waiver GST Invoice Preparation and Schedule details updation
			if (aeEvent.getLinkedTranId() > 0 && aeEvent.isPostingSucess()) {
				financeDetail = allocateWaiverToSchduleDetails(feeWaiverHeader, financeDetail, aeEvent, false);
				isSchdUpdated = true;
			}
		} else {
			// Update Profit waivers
			if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_PROFIT_WAIVER)) {
				financeDetail = allocateWaiverToSchduleDetails(feeWaiverHeader, financeDetail, aeEvent, true);
				isSchdUpdated = true;
			}
		}

		// Loan Status Modifications
		if (isSchdUpdated && financeDetail != null) {

			// Profit Details
			FinanceProfitDetail pftDetail = profitDetailsDAO.getFinProfitDetailsById(finReference);

			// Overdue Details
			List<FinODDetails> overdueList = finODDetailsDAO.getFinODBalByFinRef(finReference);

			financeMain = repayPostingUtil.updateStatus(financeMain, SysParamUtil.getAppDate(),
					financeDetail.getFinScheduleData().getFinanceScheduleDetails(), pftDetail, overdueList, null,
					false);

			if (!financeMain.isFinIsActive()) {
				financeMainDAO.updateMaturity(finReference, FinanceConstants.CLOSE_STATUS_MATURED, false);
			}
		}
		logger.debug("Leaving");
	}

	private List<ManualAdviseMovements> allocateWaivedAmtToPenalities(FeeWaiverHeader feeWaiverHeader,
			List<FinODDetails> finodPftdetails, List<FinODDetails> finodPenalitydetails) {
		logger.debug(Literal.ENTERING);

		List<ManualAdviseMovements> movements = new ArrayList<ManualAdviseMovements>();
		Date appDate = SysParamUtil.getAppDate();
		List<FinanceRepayments> rpyList = new ArrayList<>();

		// For GST Calculations
		Map<String, BigDecimal> gstPercentages = GSTCalculator.getTaxPercentages(feeWaiverHeader.getFinReference());

		for (FeeWaiverDetail waiverdetail : feeWaiverHeader.getFeeWaiverDetails()) {

			if (waiverdetail.getCurrWaiverAmount().compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}

			// update late pay penalty waived amounts to the Finoddetails table.
			if (RepayConstants.ALLOCATION_ODC.equals(waiverdetail.getFeeTypeCode())) {

				BigDecimal curwaivedAmt = waiverdetail.getCurrWaiverAmount();
				BigDecimal curActualwaivedAmt = waiverdetail.getCurrActualWaiver();
				BigDecimal amountWaived = BigDecimal.ZERO;

				TaxAmountSplit taxSplit = null;
				TaxHeader taxHeader = null;

				for (FinODDetails oddetail : finodPenalitydetails) {

					BigDecimal penalWaived = BigDecimal.ZERO;

					if (oddetail.getTotPenaltyBal().compareTo(BigDecimal.ZERO) == 0) {
						continue;
					}

					if (waiverdetail.isTaxApplicable()) {
						if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(waiverdetail.getTaxComponent())) {
							if (curActualwaivedAmt.compareTo(BigDecimal.ZERO) == 0) {
								break;
							}
						} else {
							if (curwaivedAmt.compareTo(BigDecimal.ZERO) == 0) {
								break;
							}
						}
					} else {
						if (curwaivedAmt.compareTo(BigDecimal.ZERO) == 0) {
							break;
						}
					}

					if (waiverdetail.isTaxApplicable()) {
						if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(waiverdetail.getTaxComponent())) {
							if (oddetail.getTotPenaltyBal().compareTo(curActualwaivedAmt) >= 0) {
								oddetail.setTotWaived(oddetail.getTotWaived().add(curActualwaivedAmt));
								oddetail.setTotPenaltyBal(oddetail.getTotPenaltyBal().subtract(curActualwaivedAmt));
								penalWaived = curActualwaivedAmt;
								amountWaived = curActualwaivedAmt;
								curActualwaivedAmt = BigDecimal.ZERO;
							} else {
								oddetail.setTotWaived(oddetail.getTotWaived().add(oddetail.getTotPenaltyBal()));
								penalWaived = oddetail.getTotPenaltyBal();
								amountWaived = curActualwaivedAmt;
								curActualwaivedAmt = curActualwaivedAmt.subtract(oddetail.getTotPenaltyBal());
								oddetail.setTotPenaltyBal(BigDecimal.ZERO);
							}

							taxSplit = GSTCalculator.getExclusiveGST(amountWaived, gstPercentages);
						} else {
							if (oddetail.getTotPenaltyBal().compareTo(curwaivedAmt) >= 0) {
								oddetail.setTotWaived(oddetail.getTotWaived().add(curwaivedAmt));
								oddetail.setTotPenaltyBal(oddetail.getTotPenaltyBal().subtract(curwaivedAmt));
								penalWaived = curActualwaivedAmt;
								amountWaived = curwaivedAmt;
								curwaivedAmt = BigDecimal.ZERO;
							} else {
								oddetail.setTotWaived(oddetail.getTotWaived().add(oddetail.getTotPenaltyBal()));
								curwaivedAmt = curwaivedAmt.subtract(oddetail.getTotPenaltyBal());
								penalWaived = oddetail.getTotPenaltyBal();
								amountWaived = curwaivedAmt;
								oddetail.setTotPenaltyBal(BigDecimal.ZERO);
							}

							taxSplit = GSTCalculator.getInclusiveGST(amountWaived, gstPercentages);
						}

						// Taxes Splitting
						taxHeader = taxSplitting(gstPercentages, taxSplit);
					} else {

						if (oddetail.getTotPenaltyBal().compareTo(curwaivedAmt) >= 0) {
							oddetail.setTotWaived(oddetail.getTotWaived().add(curwaivedAmt));
							oddetail.setTotPenaltyBal(oddetail.getTotPenaltyBal().subtract(curwaivedAmt));
							penalWaived = curwaivedAmt;
							amountWaived = curwaivedAmt;
							curwaivedAmt = BigDecimal.ZERO;
						} else {
							oddetail.setTotWaived(oddetail.getTotWaived().add(oddetail.getTotPenaltyBal()));
							curwaivedAmt = curwaivedAmt.subtract(oddetail.getTotPenaltyBal());
							penalWaived = oddetail.getTotPenaltyBal();
							amountWaived = curwaivedAmt;
							oddetail.setTotPenaltyBal(BigDecimal.ZERO);
						}
					}

					finODDetailsDAO.updatePenaltyTotals(oddetail);

					if (penalWaived.compareTo(BigDecimal.ZERO) > 0) {
						FinanceRepayments repayment = new FinanceRepayments();
						repayment.setFinReference(oddetail.getFinReference());
						repayment.setFinPostDate(appDate);
						repayment.setFinRpyFor(oddetail.getFinODFor());
						repayment.setFinRpyAmount(penalWaived);
						repayment.setFinSchdDate(oddetail.getFinODSchdDate());
						repayment.setFinValueDate(appDate);
						repayment.setFinBranch(oddetail.getFinBranch());
						repayment.setFinType(oddetail.getFinType());
						repayment.setFinCustID(oddetail.getCustID());
						repayment.setPenaltyWaived(penalWaived);
						repayment.setWaiverId(feeWaiverHeader.getWaiverId());
						rpyList.add(repayment);
					}

					// TODO update LPP related GST Table data
					if (SysParamUtil.isAllowed("GST_INV_ON_DUE")
							&& waiverdetail.getCurrWaiverAmount().compareTo(BigDecimal.ZERO) > 0) {
						ManualAdviseMovements movement = new ManualAdviseMovements();
						if (amountWaived.compareTo(BigDecimal.ZERO) > 0) {
							movement.setMovementDate(DateUtility.getAppDate());
							movement.setMovementAmount(waiverdetail.getCurrWaiverAmount());
							movement.setPaidAmount(BigDecimal.ZERO);
							movement.setWaivedAmount(amountWaived);
							movement.setReceiptID(0);
							movement.setReceiptSeqID(0);
							movement.setWaiverID(waiverdetail.getWaiverId());
							movement.setFeeTypeCode(waiverdetail.getFeeTypeCode());
							movement.setFeeTypeDesc(waiverdetail.getFeeTypeDesc());
							movement.setTaxApplicable(waiverdetail.isTaxApplicable());
							movement.setTaxComponent(waiverdetail.getTaxComponent());

							if (taxHeader != null) {
								movement.setTaxHeaderId(taxHeader.getHeaderId());

								for (Taxes tax : taxHeader.getTaxDetails()) {
									String gstType = tax.getTaxType();

									if (RuleConstants.CODE_CGST.equals(gstType)) {
										movement.setWaivedCGST(movement.getWaivedCGST().add(taxSplit.getcGST()));
									} else if (RuleConstants.CODE_SGST.equals(gstType)) {
										movement.setWaivedSGST(movement.getWaivedSGST().add(taxSplit.getsGST()));
									} else if (RuleConstants.CODE_IGST.equals(gstType)) {
										movement.setWaivedIGST(movement.getWaivedIGST().add(taxSplit.getiGST()));
									} else if (RuleConstants.CODE_UGST.equals(gstType)) {
										movement.setWaivedUGST(movement.getWaivedUGST().add(taxSplit.getuGST()));
									}
								}
							}
						}
						movements.add(movement);
					}
				}
			}

			// update late pay profit waived amounts to the Finoddetails table.
			if (waiverdetail.getFeeTypeCode().equals(RepayConstants.ALLOCATION_LPFT)) {
				BigDecimal curwaivedAmt = waiverdetail.getCurrWaiverAmount();
				for (FinODDetails oddetail : finodPftdetails) {
					if (oddetail.getLPIBal().compareTo(curwaivedAmt) >= 0) {
						oddetail.setLPIWaived(curwaivedAmt);
						curwaivedAmt = BigDecimal.ZERO;
					} else {
						oddetail.setLPIWaived(oddetail.getLPIWaived().add(oddetail.getLPIBal()));
						curwaivedAmt = curwaivedAmt.subtract(oddetail.getLPIBal());
						oddetail.setLPIBal(BigDecimal.ZERO);
					}

					finODDetailsDAO.updateLatePftTotals(oddetail.getFinReference(), oddetail.getFinODSchdDate(),
							BigDecimal.ZERO, oddetail.getLPIWaived());
				}
			}
		}

		feeWaiverHeader.setRpyList(rpyList);
		logger.debug(Literal.LEAVING);
		return movements;
	}

	/**
	 * Updating the finSchdule details and insert into FINREPAYDETAILS, FinRepaySchdetails tables.
	 */
	private FinanceDetail allocateWaiverToSchduleDetails(FeeWaiverHeader feeWaiverHeader, FinanceDetail financeDetail,
			AEEvent aeEvent, boolean isAcctRequired) {
		logger.debug(Literal.ENTERING);

		List<FeeWaiverDetail> details = feeWaiverHeader.getFeeWaiverDetails();
		FeeWaiverDetail feeWaiverDetail = null;

		if (CollectionUtils.isEmpty(details)) {
			return null;
		}

		for (FeeWaiverDetail waiverDetail : details) {
			if (RepayConstants.ALLOCATION_PFT.equals(waiverDetail.getFeeTypeCode())
					&& waiverDetail.getCurrWaiverAmount().compareTo(BigDecimal.ZERO) > 0) {
				feeWaiverDetail = waiverDetail;
				break;
			}
		}

		if (feeWaiverDetail == null) {
			return null;
		}

		String finReference = feeWaiverHeader.getFinReference();

		List<FinanceScheduleDetail> scheduleDetails = this.financeScheduleDetailDAO.getFinScheduleDetails(finReference,
				"", false);

		if (CollectionUtils.isEmpty(scheduleDetails)) {
			return null;
		}

		Date appDate = SysParamUtil.getAppDate();

		BigDecimal curwaivedAmt = feeWaiverDetail.getCurrWaiverAmount();

		List<FinanceRepayments> finRepaymentList = new ArrayList<>();
		List<RepayScheduleDetail> repaySchDetList = new ArrayList<>();
		BigDecimal totPftBal = BigDecimal.ZERO;

		// TDS Parameters
		String tdsRoundMode = SysParamUtil.getValueAsString(CalculationConstants.TDS_ROUNDINGMODE);
		int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);
		BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValueAsString(CalculationConstants.TDS_PERCENTAGE));
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		for (FinanceScheduleDetail schDetail : scheduleDetails) {

			if (schDetail.getSchDate().compareTo(appDate) > 0) {
				break;
			}

			if (curwaivedAmt.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}

			if (schDetail.getProfitSchd().compareTo(schDetail.getSchdPftPaid()) <= 0) {
				continue;
			}

			BigDecimal profitBal = schDetail.getProfitSchd().subtract(schDetail.getSchdPftPaid());

			// Full adjustment
			if (profitBal.compareTo(curwaivedAmt) <= 0) {
				schDetail.setSchdPftWaiver(schDetail.getSchdPftWaiver().add(profitBal));
				schDetail.setSchdPftPaid(schDetail.getSchdPftPaid().add(profitBal));
				schDetail.setSchPftPaid(true);

				BigDecimal tds = curwaivedAmt.multiply(tdsPerc);
				tds = tds.divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_UP);
				tds = CalculationUtil.roundAmount(tds, tdsRoundMode, tdsRoundingTarget);

				schDetail.setTDSPaid(schDetail.getTDSPaid().add(tds));
				curwaivedAmt = curwaivedAmt.subtract(profitBal);
				finRepaymentList.add(prepareRepayments(profitBal, tds, schDetail, financeMain, feeWaiverHeader));
				repaySchDetList.add(prepareRepaySchDetails(profitBal, schDetail, financeMain, feeWaiverHeader, tds));
				totPftBal = totPftBal.add(totPftBal);
				// Partial Adjustment
			} else if (profitBal.compareTo(curwaivedAmt) > 0) {

				BigDecimal tds = curwaivedAmt.multiply(tdsPerc);
				tds = tds.divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_UP);
				tds = CalculationUtil.roundAmount(tds, tdsRoundMode, tdsRoundingTarget);

				schDetail.setTDSPaid(schDetail.getTDSPaid().add(tds));
				schDetail.setSchdPftWaiver(schDetail.getSchdPftWaiver().add(curwaivedAmt));
				schDetail.setSchdPftPaid(schDetail.getSchdPftPaid().add(curwaivedAmt));

				finRepaymentList.add(prepareRepayments(curwaivedAmt, tds, schDetail, financeMain, feeWaiverHeader));
				repaySchDetList.add(prepareRepaySchDetails(curwaivedAmt, schDetail, financeMain, feeWaiverHeader, tds));
				totPftBal = totPftBal.add(curwaivedAmt);
				curwaivedAmt = BigDecimal.ZERO;
			}
		}
		// Executing accounting
		if (isAcctRequired) {
			aeEvent = executeAcctProcessing(totPftBal, financeMain, feeWaiverHeader, aeEvent, null);
		}
		long linkedTranId = aeEvent.getLinkedTranId();

		// Schedule details
		this.financeScheduleDetailDAO.deleteByFinReference(finReference, TableType.MAIN_TAB.getSuffix(), false, 0);
		this.financeScheduleDetailDAO.saveList(scheduleDetails, TableType.MAIN_TAB.getSuffix(), false);

		// Repay Schedule details
		for (RepayScheduleDetail scheduleDetail : repaySchDetList) {
			scheduleDetail.setLinkedTranId(linkedTranId);
		}
		this.financeRepaymentsDAO.saveRpySchdList(repaySchDetList, TableType.MAIN_TAB);

		// Fin Repay Details
		for (FinanceRepayments repayments : finRepaymentList) {
			repayments.setLinkedTranId(linkedTranId);
			this.financeRepaymentsDAO.save(repayments, TableType.MAIN_TAB.getSuffix());
		}

		scheduleDetails = this.financeScheduleDetailDAO.getFinScheduleDetails(finReference, "", false);
		financeDetail.getFinScheduleData().setFinanceScheduleDetails(scheduleDetails);

		List<FinODDetails> overdueList = this.receiptCalculator.getValueDatePenalties(
				financeDetail.getFinScheduleData(), BigDecimal.ZERO, SysParamUtil.getAppDate(), null, true);

		if (CollectionUtils.isNotEmpty(overdueList)) {
			finODDetailsDAO.updateList(overdueList);
		}

		// Profit Waiver GST Invoice Preparation
		if (ImplementationConstants.ALW_PROFIT_SCHD_INVOICE && linkedTranId > 0) {
			gstInvoiceTxnService.createProfitScheduleInovice(linkedTranId, financeDetail, totPftBal,
					PennantConstants.GST_INVOICE_TRANSACTION_TYPE_EXEMPTED_TAX_CREDIT);
		}

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	/**
	 * Updating the finSchdule details and insert into FINREPAYDETAILS, FinRepaySchdetails tables.
	 */
	private BigDecimal getTotPftBal(FeeWaiverHeader feeWaiverHeader, FinanceDetail financeDetail, AEEvent aeEvent) {
		logger.debug(Literal.ENTERING);

		List<FeeWaiverDetail> details = feeWaiverHeader.getFeeWaiverDetails();
		FeeWaiverDetail feeWaiverDetail = null;

		if (CollectionUtils.isEmpty(details)) {
			return BigDecimal.ZERO;
		}

		for (FeeWaiverDetail waiverDetail : details) {
			if (RepayConstants.ALLOCATION_PFT.equals(waiverDetail.getFeeTypeCode())
					&& waiverDetail.getCurrWaiverAmount().compareTo(BigDecimal.ZERO) > 0) {
				feeWaiverDetail = waiverDetail;
				break;
			}
		}

		if (feeWaiverDetail == null) {
			return BigDecimal.ZERO;
		}

		String finReference = feeWaiverHeader.getFinReference();

		List<FinanceScheduleDetail> scheduleDetails = this.financeScheduleDetailDAO.getFinScheduleDetails(finReference,
				"", false);

		if (CollectionUtils.isEmpty(scheduleDetails)) {
			return BigDecimal.ZERO;
		}

		Date appDate = SysParamUtil.getAppDate();

		BigDecimal curwaivedAmt = feeWaiverDetail.getCurrWaiverAmount();

		List<FinanceRepayments> finRepaymentList = new ArrayList<>();
		List<RepayScheduleDetail> repaySchDetList = new ArrayList<>();
		BigDecimal totPftBal = BigDecimal.ZERO;

		// TDS Parameters
		String tdsRoundMode = SysParamUtil.getValueAsString(CalculationConstants.TDS_ROUNDINGMODE);
		int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);
		BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValueAsString(CalculationConstants.TDS_PERCENTAGE));
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		for (FinanceScheduleDetail schDetail : scheduleDetails) {

			if (schDetail.getSchDate().compareTo(appDate) > 0) {
				break;
			}

			if (curwaivedAmt.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}

			if (schDetail.getProfitSchd().compareTo(schDetail.getSchdPftPaid()) <= 0) {
				continue;
			}

			BigDecimal profitBal = schDetail.getProfitSchd().subtract(schDetail.getSchdPftPaid());

			// Full adjustment
			if (profitBal.compareTo(curwaivedAmt) <= 0) {
				schDetail.setSchdPftWaiver(schDetail.getSchdPftWaiver().add(profitBal));
				schDetail.setSchdPftPaid(schDetail.getSchdPftPaid().add(profitBal));
				schDetail.setSchPftPaid(true);

				BigDecimal tds = curwaivedAmt.multiply(tdsPerc);
				tds = tds.divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_UP);
				tds = CalculationUtil.roundAmount(tds, tdsRoundMode, tdsRoundingTarget);

				schDetail.setTDSPaid(schDetail.getTDSPaid().add(tds));
				curwaivedAmt = curwaivedAmt.subtract(profitBal);
				finRepaymentList.add(prepareRepayments(profitBal, tds, schDetail, financeMain, feeWaiverHeader));
				repaySchDetList.add(prepareRepaySchDetails(profitBal, schDetail, financeMain, feeWaiverHeader, tds));
				totPftBal = totPftBal.add(totPftBal);
				// Partial Adjustment
			} else if (profitBal.compareTo(curwaivedAmt) > 0) {

				BigDecimal tds = curwaivedAmt.multiply(tdsPerc);
				tds = tds.divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_UP);
				tds = CalculationUtil.roundAmount(tds, tdsRoundMode, tdsRoundingTarget);

				schDetail.setTDSPaid(schDetail.getTDSPaid().add(tds));
				schDetail.setSchdPftWaiver(schDetail.getSchdPftWaiver().add(curwaivedAmt));
				schDetail.setSchdPftPaid(schDetail.getSchdPftPaid().add(curwaivedAmt));

				finRepaymentList.add(prepareRepayments(curwaivedAmt, tds, schDetail, financeMain, feeWaiverHeader));
				repaySchDetList.add(prepareRepaySchDetails(curwaivedAmt, schDetail, financeMain, feeWaiverHeader, tds));
				totPftBal = totPftBal.add(curwaivedAmt);
				curwaivedAmt = BigDecimal.ZERO;
			}
		}
		logger.debug(Literal.LEAVING);

		return totPftBal;
	}

	private AEEvent executeAcctProcessing(BigDecimal totPftBal, FinanceMain financeMain,
			FeeWaiverHeader feeWaiverHeader, AEEvent aeEvent, List<ManualAdviseMovements> movements) {
		logger.debug(Literal.ENTERING);

		if (aeEvent == null) {
			aeEvent = prepareAEEvent(financeMain, feeWaiverHeader);
		}

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}
		amountCodes.setFinType(financeMain.getFinType());

		Map<String, Object> detailsMap = amountCodes.getDeclaredFieldValues();
		detailsMap.put("ae_pftWaiver", totPftBal);
		aeEvent.setDataMap(detailsMap);

		BigDecimal totBounce = BigDecimal.ZERO;
		BigDecimal bounceCGST = BigDecimal.ZERO;
		BigDecimal bounceSGST = BigDecimal.ZERO;
		BigDecimal bounceIGST = BigDecimal.ZERO;
		BigDecimal bounceUGST = BigDecimal.ZERO;

		BigDecimal totLPP = BigDecimal.ZERO;
		BigDecimal lppCGST = BigDecimal.ZERO;
		BigDecimal lppSGST = BigDecimal.ZERO;
		BigDecimal lppIGST = BigDecimal.ZERO;
		BigDecimal lppUGST = BigDecimal.ZERO;

		if (CollectionUtils.isNotEmpty(movements)) {

			for (ManualAdviseMovements movement : movements) {
				if (RepayConstants.ALLOCATION_BOUNCE.equals(movement.getFeeTypeCode())) {
					totBounce = totBounce.add(movement.getMovementAmount());
					bounceCGST = bounceCGST.add(movement.getWaivedCGST());
					bounceSGST = bounceSGST.add(movement.getWaivedSGST());
					bounceIGST = bounceIGST.add(movement.getWaivedIGST());
					bounceUGST = bounceUGST.add(movement.getWaivedUGST());
				} else if (RepayConstants.ALLOCATION_ODC.equals(movement.getFeeTypeCode())) {
					totLPP = totLPP.add(movement.getMovementAmount());
					lppCGST = lppCGST.add(movement.getWaivedCGST());
					lppSGST = lppSGST.add(movement.getWaivedSGST());
					lppIGST = lppIGST.add(movement.getWaivedIGST());
					lppUGST = lppUGST.add(movement.getWaivedUGST());
				} else {
					if (StringUtils.isNotEmpty(movement.getFeeTypeCode())) {
						FeeType feeType = this.feeTypeDAO.getApprovedFeeTypeByFeeCode(movement.getFeeTypeCode());
						if (feeType != null) {
							prepareFeetypeDataMap(feeType, detailsMap, movement);
						}
					}
				}
			}
		}

		detailsMap.put("bounceCharge_CGST_W", bounceCGST);
		detailsMap.put("bounceCharge_SGST_W", bounceSGST);
		detailsMap.put("bounceCharge_IGST_W", bounceIGST);
		detailsMap.put("bounceCharge_UGST_W", bounceUGST);
		detailsMap.put("bounceChargeWaived", totBounce);
		// TODO add Cess

		detailsMap.put("LPP_CGST_W", lppCGST);
		detailsMap.put("LPP_SGST_W", lppSGST);
		detailsMap.put("LPP_IGST_W", lppIGST);
		detailsMap.put("LPP_UGST_W", lppUGST);
		detailsMap.put("ae_penaltyWaived", totLPP);

		aeEvent = this.postingsPreparationUtil.postAccounting(aeEvent);

		logger.debug(Literal.LEAVING);
		return aeEvent;
	}

	/**
	 * Preparing Accounting set for manual advice fee types
	 * 
	 * @param feeType
	 * @param dataMap
	 * @param movement
	 */
	private void prepareFeetypeDataMap(FeeType feeType, Map<String, Object> dataMap, ManualAdviseMovements movement) {
		logger.debug(Literal.ENTERING);

		String feeTypeCode = feeType.getFeeTypeCode();

		dataMap.put(feeTypeCode + "_W", movement.getWaivedAmount());

		// Waiver GST Amounts (GST Waiver Changes)
		dataMap.put(feeTypeCode + "_CGST_W", movement.getWaivedCGST());
		dataMap.put(feeTypeCode + "_SGST_W", movement.getWaivedSGST());
		dataMap.put(feeTypeCode + "_IGST_W", movement.getWaivedIGST());
		dataMap.put(feeTypeCode + "_UGST_W", movement.getWaivedUGST());

		logger.debug(Literal.LEAVING);
	}

	private AEEvent prepareAEEvent(FinanceMain financeMain, FeeWaiverHeader feeWaiverHeader) {
		AEEvent aeEvent;
		aeEvent = new AEEvent();
		aeEvent.setPostingUserBranch(feeWaiverHeader.getUserDetails().getBranchCode());
		aeEvent.setEntityCode(financeMain.getEntityCode());
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_WAIVER);
		aeEvent.setFinReference(financeMain.getFinReference());
		aeEvent.setValueDate(SysParamUtil.getAppDate());
		aeEvent.setBranch(financeMain.getFinBranch());
		aeEvent.setCcy(financeMain.getFinCcy());
		aeEvent.setCustID(financeMain.getCustID());
		aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(financeMain.getFinType(),
				AccountEventConstants.ACCEVENT_WAIVER, FinanceConstants.MODULEID_FINTYPE));

		return aeEvent;
	}

	private FinanceRepayments prepareRepayments(BigDecimal profitBal, BigDecimal tds, FinanceScheduleDetail schDetail,
			FinanceMain fm, FeeWaiverHeader waiverHeader) {
		FinanceRepayments repayment = new FinanceRepayments();

		repayment.setWaiverId(waiverHeader.getWaiverId());
		repayment.setFinReference(schDetail.getFinReference());
		repayment.setFinSchdDate(schDetail.getSchDate());
		repayment.setFinRpyFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		repayment.setLinkedTranId(0);// FIXME
		repayment.setReceiptId(0);
		repayment.setFinPostDate(SysParamUtil.getAppDate());
		repayment.setFinValueDate(SysParamUtil.getAppValueDate());
		repayment.setFinBranch(fm.getFinBranch());
		repayment.setFinType(fm.getFinType());
		repayment.setFinCustID(fm.getCustID());

		repayment.setFinSchdPriPaid(schDetail.getSchdPriPaid());
		repayment.setFinSchdPftPaid(profitBal);
		repayment.setFinTotSchdPaid(schDetail.getSchdPftPaid().add(schDetail.getSchdPriPaid()));

		// Fee Details
		repayment.setFinFee(BigDecimal.ZERO);
		repayment.setFinWaiver(BigDecimal.ZERO);
		repayment.setFinRefund(BigDecimal.ZERO);
		repayment.setSchdFeePaid(BigDecimal.ZERO);

		repayment.setFinRpyAmount(profitBal);
		repayment.setFinSchdTdsPaid(tds);

		repayment.setSchdInsPaid(BigDecimal.ZERO);
		repayment.setSchdSuplRentPaid(BigDecimal.ZERO);
		repayment.setSchdIncrCostPaid(BigDecimal.ZERO);

		return repayment;
	}

	private RepayScheduleDetail prepareRepaySchDetails(BigDecimal profitBal, FinanceScheduleDetail schDetail,
			FinanceMain financeMain, FeeWaiverHeader waiverHeader, BigDecimal tdsSchdPayNow) {

		FinRepayHeader finRepayHeader = new FinRepayHeader();
		finRepayHeader.setFinReference(financeMain.getFinReference());
		finRepayHeader.setValueDate(DateUtility.getAppDate());
		finRepayHeader.setFinEvent(AccountEventConstants.ACCEVENT_WAIVER);
		long id = getFinanceRepaymentsDAO().saveFinRepayHeader(finRepayHeader, TableType.MAIN_TAB);

		RepayScheduleDetail rsd = new RepayScheduleDetail();
		rsd.setWaiverId(waiverHeader.getWaiverId());
		rsd.setFinReference(schDetail.getFinReference());
		rsd.setSchDate(schDetail.getSchDate());
		rsd.setSchdFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		rsd.setProfitSchdBal(schDetail.getProfitSchd());
		rsd.setPrincipalSchdBal(schDetail.getPrincipalSchd());
		rsd.setProfitSchdPayNow(profitBal);
		rsd.setPrincipalSchdPayNow(schDetail.getSchdPriPaid());
		rsd.setTdsSchdPayNow(tdsSchdPayNow);

		int daysLate = DateUtility.getDaysBetween(schDetail.getSchDate(), SysParamUtil.getAppValueDate());
		rsd.setDaysLate(daysLate);

		rsd.setRepayBalance(schDetail.getProfitSchd().add(schDetail.getPrincipalSchd()));
		rsd.setProfitSchd(schDetail.getProfitSchd());
		rsd.setProfitSchdPaid(profitBal);

		rsd.setPrincipalSchd(schDetail.getPrincipalSchd());
		rsd.setPrincipalSchdPaid(schDetail.getSchdPriPaid());
		rsd.setPenaltyPayNow(BigDecimal.ZERO);

		rsd.setRepayID(id);// wAIVERiD
		rsd.setRepaySchID(1);
		rsd.setLinkedTranId(0);// pOSTiD
		return rsd;
	}

	private List<ManualAdviseMovements> allocateWaiverToBounceAndAdvise(FeeWaiverHeader feeWaiverHeader) {
		logger.debug(Literal.ENTERING);

		List<ManualAdviseMovements> movements = new ArrayList<ManualAdviseMovements>();
		for (FeeWaiverDetail waiverdetail : feeWaiverHeader.getFeeWaiverDetails()) {

			if (!RepayConstants.ALLOCATION_ODC.equals(waiverdetail.getFeeTypeCode())
					&& !RepayConstants.ALLOCATION_LPFT.equals(waiverdetail.getFeeTypeCode())
					&& !RepayConstants.ALLOCATION_PFT.equals(waiverdetail.getFeeTypeCode())
					&& waiverdetail.getCurrWaiverAmount().compareTo(BigDecimal.ZERO) > 0) {

				for (ManualAdvise advise : manualAdviseList) {
					if (advise.getBounceID() != 0) {
						ManualAdviseMovements movement = prepareAdviseWaiver(waiverdetail, advise);
						if (movement != null) {
							movements.add(movement);
						}
					} else {
						if (advise.getAdviseID() == waiverdetail.getAdviseId()) {
							ManualAdviseMovements movement = prepareAdviseWaiver(waiverdetail, advise);
							if (movement != null) {
								movements.add(movement);
							}
						}
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);

		return movements;
	}

	private ManualAdviseMovements prepareAdviseWaiver(FeeWaiverDetail waiverdetail, ManualAdvise advise) {
		logger.debug(Literal.ENTERING);

		BigDecimal curwaivedAmt = waiverdetail.getCurrWaiverAmount();
		BigDecimal curActualwaivedAmt = waiverdetail.getCurrActualWaiver();
		BigDecimal amountWaived = BigDecimal.ZERO;
		Map<String, BigDecimal> gstPercentages = getTaxPercentages(waiverdetail.getFinReference());

		TaxAmountSplit taxSplit = null;
		TaxHeader taxHeader = null;

		if (waiverdetail.isTaxApplicable()) {
			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(waiverdetail.getTaxComponent())) {
				if (advise.getBalanceAmt().compareTo(curActualwaivedAmt) >= 0) {
					advise.setWaivedAmount(advise.getWaivedAmount().add(curActualwaivedAmt));
					advise.setBalanceAmt(advise.getBalanceAmt().subtract(curActualwaivedAmt));
					amountWaived = curActualwaivedAmt;
					curActualwaivedAmt = BigDecimal.ZERO;
				} else {
					advise.setWaivedAmount(advise.getWaivedAmount().add(advise.getBalanceAmt()));
					curActualwaivedAmt = curActualwaivedAmt.subtract(advise.getBalanceAmt());
					amountWaived = curActualwaivedAmt;
					advise.setBalanceAmt(BigDecimal.ZERO);
				}

				taxSplit = GSTCalculator.getExclusiveGST(amountWaived, gstPercentages);

			} else {
				if (advise.getBalanceAmt().compareTo(curwaivedAmt) >= 0) {
					advise.setWaivedAmount(advise.getWaivedAmount().add(curwaivedAmt));
					advise.setBalanceAmt(advise.getBalanceAmt().subtract(curwaivedAmt));
					amountWaived = curwaivedAmt;
					curwaivedAmt = BigDecimal.ZERO;
				} else {
					advise.setWaivedAmount(advise.getWaivedAmount().add(advise.getBalanceAmt()));
					curwaivedAmt = curwaivedAmt.subtract(advise.getBalanceAmt());
					amountWaived = curwaivedAmt;
					advise.setBalanceAmt(BigDecimal.ZERO);
				}

				taxSplit = GSTCalculator.getInclusiveGST(amountWaived, gstPercentages);
			}

			// Taxes Splitting
			taxHeader = taxSplitting(gstPercentages, taxSplit, advise);

		} else {

			if (advise.getBalanceAmt().compareTo(curwaivedAmt) >= 0) {
				advise.setWaivedAmount(advise.getWaivedAmount().add(curwaivedAmt));
				advise.setBalanceAmt(advise.getBalanceAmt().subtract(curwaivedAmt));
				amountWaived = curwaivedAmt;
				curwaivedAmt = BigDecimal.ZERO;
			} else {
				advise.setWaivedAmount(advise.getWaivedAmount().add(advise.getBalanceAmt()));
				curwaivedAmt = curwaivedAmt.subtract(advise.getBalanceAmt());
				amountWaived = advise.getBalanceAmt();
				advise.setBalanceAmt(BigDecimal.ZERO);

			}
		}

		advise.setVersion(advise.getVersion() + 1);
		manualAdviseDAO.update(advise, TableType.MAIN_TAB);

		ManualAdviseMovements movement = new ManualAdviseMovements();
		if (amountWaived.compareTo(BigDecimal.ZERO) > 0) {
			movement.setAdviseID(advise.getAdviseID());
			movement.setMovementDate(DateUtility.getAppDate());
			// movement.setMovementAmount(advise.getPaidAmount());
			movement.setMovementAmount(waiverdetail.getCurrWaiverAmount()); // TODO check here once if we give waived
																			// amount or Paid amount
			movement.setPaidAmount(advise.getPaidAmount());
			movement.setWaivedAmount(amountWaived);
			movement.setReceiptID(0);
			movement.setReceiptSeqID(0);
			movement.setWaiverID(waiverdetail.getWaiverId());
			movement.setFeeTypeCode(waiverdetail.getFeeTypeCode());
			movement.setFeeTypeDesc(waiverdetail.getFeeTypeDesc());
			movement.setTaxApplicable(waiverdetail.isTaxApplicable());
			movement.setTaxComponent(waiverdetail.getTaxComponent());

			if (taxHeader != null) {
				movement.setTaxHeaderId(taxHeader.getHeaderId());

				for (Taxes tax : taxHeader.getTaxDetails()) {
					String gstType = tax.getTaxType();

					if (RuleConstants.CODE_CGST.equals(gstType)) {
						movement.setWaivedCGST(movement.getWaivedCGST().add(taxSplit.getcGST()));
					} else if (RuleConstants.CODE_SGST.equals(gstType)) {
						movement.setWaivedSGST(movement.getWaivedSGST().add(taxSplit.getsGST()));
					} else if (RuleConstants.CODE_IGST.equals(gstType)) {
						movement.setWaivedIGST(movement.getWaivedIGST().add(taxSplit.getiGST()));
					} else if (RuleConstants.CODE_UGST.equals(gstType)) {
						movement.setWaivedUGST(movement.getWaivedUGST().add(taxSplit.getuGST()));
					}
				}
			}

			manualAdviseDAO.saveMovement(movement, "");
		}

		logger.debug(Literal.LEAVING);

		// GST Invoice data resetting based on Accounting Process
		if (SysParamUtil.isAllowed("GST_INV_ON_DUE") && (advise.getBounceID() != 0 || advise.isDueCreation())) {
			return movement;
		}

		return null;
	}

	private TaxHeader taxSplitting(Map<String, BigDecimal> gstPercentages, TaxAmountSplit taxSplit,
			ManualAdvise advise) {
		logger.debug(Literal.ENTERING);

		TaxHeader taxHeader = new TaxHeader();
		List<Taxes> taxes = new ArrayList<>();

		for (String gstType : gstPercentages.keySet()) {
			Taxes tax = new Taxes();

			if (RuleConstants.CODE_CGST.equals(gstType)) {
				tax.setWaivedTax(taxSplit.getcGST());
				advise.setWaivedCGST(advise.getWaivedCGST().add(taxSplit.getcGST()));
			} else if (RuleConstants.CODE_SGST.equals(gstType)) {
				tax.setWaivedTax(taxSplit.getsGST());
				advise.setWaivedSGST(advise.getWaivedSGST().add(taxSplit.getsGST()));
			} else if (RuleConstants.CODE_IGST.equals(gstType)) {
				tax.setWaivedTax(taxSplit.getiGST());
				advise.setWaivedIGST(advise.getWaivedIGST().add(taxSplit.getiGST()));
			} else if (RuleConstants.CODE_UGST.equals(gstType)) {
				tax.setWaivedTax(taxSplit.getuGST());
				advise.setWaivedUGST(advise.getWaivedUGST().add(taxSplit.getuGST()));
			} else {
				continue;
			}

			tax.setTaxPerc(gstPercentages.get(gstType));
			tax.setTaxType(gstType);
			tax.setNewRecord(true);
			tax.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			tax.setVersion(1);
			taxes.add(tax);
		}

		taxHeader.setNewRecord(true);
		taxHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		taxHeader.setVersion(1);
		taxHeader.setTaxDetails(taxes);

		// Saving the Tax Header and Tax Details
		taxHeaderDetailsService.doApprove(taxHeader, "", "");

		logger.debug(Literal.LEAVING);

		return taxHeader;
	}

	private TaxHeader taxSplitting(Map<String, BigDecimal> gstPercentages, TaxAmountSplit taxSplit) {
		logger.debug(Literal.ENTERING);

		TaxHeader taxHeader = new TaxHeader();
		List<Taxes> taxes = new ArrayList<>();

		for (String gstType : gstPercentages.keySet()) {
			Taxes tax = new Taxes();

			if (RuleConstants.CODE_CGST.equals(gstType)) {
				tax.setWaivedTax(taxSplit.getcGST());
			} else if (RuleConstants.CODE_SGST.equals(gstType)) {
				tax.setWaivedTax(taxSplit.getsGST());
			} else if (RuleConstants.CODE_IGST.equals(gstType)) {
				tax.setWaivedTax(taxSplit.getiGST());
			} else if (RuleConstants.CODE_UGST.equals(gstType)) {
				tax.setWaivedTax(taxSplit.getuGST());
			} else if (RuleConstants.CODE_CESS.equals(gstType)) {
				tax.setWaivedTax(taxSplit.getCess());
			} else {
				continue;
			}

			tax.setTaxPerc(gstPercentages.get(gstType));
			tax.setTaxType(gstType);
			tax.setNewRecord(true);
			tax.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			tax.setVersion(1);
			taxes.add(tax);
		}

		taxHeader.setNewRecord(true);
		taxHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		taxHeader.setVersion(1);
		taxHeader.setTaxDetails(taxes);

		// Saving the Tax Header and Tax Details
		taxHeaderDetailsService.doApprove(taxHeader, "", "");

		logger.debug(Literal.LEAVING);

		return taxHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFeeWaiverHeaderDAO().delete with parameters FeeWaiverHeader,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtBMTFeeWaiverHeader by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FeeWaiverHeader feeWaiverHeader = (FeeWaiverHeader) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		// List Delete
		auditHeader.setAuditDetails(
				processChildsAudit(deleteChilds(feeWaiverHeader, "_Temp", auditHeader.getAuditTranType())));

		getFeeWaiverHeaderDAO().delete(feeWaiverHeader, TableType.TEMP_TAB);

		// auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, feeWaiverHeader.getBefImage(),
		// feeWaiverHeader));

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	private List<AuditDetail> processChildsAudit(List<AuditDetail> list) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (list == null || list.isEmpty()) {
			return auditDetails;
		}

		for (AuditDetail detail : list) {
			String transType = "";
			String rcdType = "";
			Object object = detail.getModelData();

			if (object instanceof FeeWaiverDetail) {
				rcdType = ((FeeWaiverDetail) object).getRecordType();
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

		logger.debug("Leaving");

		return auditDetails;
	}

	public List<AuditDetail> deleteChilds(FeeWaiverHeader feeWaiverHeader, String tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (CollectionUtils.isNotEmpty(feeWaiverHeader.getFeeWaiverDetails())) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FeeWaiverDetail(),
					new FeeWaiverDetail().getExcludeFields());
			for (int i = 0; i < feeWaiverHeader.getFeeWaiverDetails().size(); i++) {
				FeeWaiverDetail feeDetail = feeWaiverHeader.getFeeWaiverDetails().get(i);
				if (StringUtils.isNotEmpty(feeDetail.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							feeDetail.getBefImage(), feeDetail));
				}
				if (feeDetail.getTaxHeader() != null) {
					taxHeaderDetailsService.doReject(feeDetail.getTaxHeader());
				}
				getFeeWaiverDetailDAO().delete(feeDetail, TableType.TEMP_TAB);
			}
		}

		logger.debug("Leaving");

		return auditDetails;
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
		logger.debug("Entering");

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FeeWaiverHeader feeWaiverHeader = (FeeWaiverHeader) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (feeWaiverHeader.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		List<FeeWaiverDetail> feeWaiverDetails = feeWaiverHeader.getFeeWaiverDetails();

		if (CollectionUtils.isNotEmpty(feeWaiverDetails)) {
			auditDetailMap.put("FeeWaiverDetails", setFeeWaiverAuditData(feeWaiverHeader, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FeeWaiverDetails"));
		}

		feeWaiverHeader.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(feeWaiverHeader);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");

		return auditHeader;
	}

	private List<AuditDetail> setFeeWaiverAuditData(FeeWaiverHeader feeWaiverHeader, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FeeWaiverDetail feeWaiver = new FeeWaiverDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(feeWaiver, feeWaiver.getExcludeFields());

		for (int i = 0; i < feeWaiverHeader.getFeeWaiverDetails().size(); i++) {
			FeeWaiverDetail feeWaiverDetail = feeWaiverHeader.getFeeWaiverDetails().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(feeWaiverDetail.getRecordType()))) {
				continue;
			}

			feeWaiverDetail.setWorkflowId(feeWaiverHeader.getWorkflowId());
			boolean isRcdType = false;

			if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				feeWaiverDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				feeWaiverDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (feeWaiverHeader.isWorkflow()) {
					isRcdType = true;
				}
			} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				feeWaiverDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				feeWaiverDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			feeWaiverDetail.setRecordStatus(feeWaiverHeader.getRecordStatus());
			feeWaiverDetail.setUserDetails(feeWaiverHeader.getUserDetails());
			feeWaiverDetail.setLastMntOn(feeWaiverHeader.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], feeWaiverDetail.getBefImage(),
					feeWaiverDetail));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getFeeWaiverHeaderDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		FeeWaiverHeader feeWaiverHeader = (FeeWaiverHeader) auditDetail.getModelData();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		BigDecimal totalPenalityBal = BigDecimal.ZERO;
		BigDecimal totalLPIBal = BigDecimal.ZERO;

		// update the waiver amounts to the tables.
		for (FeeWaiverDetail waiverDetail : feeWaiverHeader.getFeeWaiverDetails()) {

			if (!RepayConstants.ALLOCATION_ODC.equals(waiverDetail.getFeeTypeCode())
					&& !RepayConstants.ALLOCATION_LPFT.equals(waiverDetail.getFeeTypeCode())) {

				manualAdviseList = manualAdviseDAO.getManualAdvise(feeWaiverHeader.getFinReference());

				for (ManualAdvise manualAdvise : manualAdviseList) {

					// validate the current waived amount against the manual advise.
					if (manualAdvise.getAdviseID() == waiverDetail.getAdviseId()) {
						BigDecimal waiverAmount = BigDecimal.ZERO;
						if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(waiverDetail.getTaxComponent())) {
							waiverAmount = waiverDetail.getCurrActualWaiver();
						} else {
							waiverAmount = waiverDetail.getCurrWaiverAmount();
						}

						if (waiverAmount.compareTo(manualAdvise.getBalanceAmt()) > 0) {
							valueParm[0] = String.valueOf(waiverAmount);
							errParm[0] = waiverDetail.getFeeTypeDesc();
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "91136", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				List<FinODDetails> finodPenalitydetails = finODDetailsDAO
						.getFinODPenalityByFinRef(feeWaiverHeader.getFinReference(), false, false);
				for (FinODDetails oddetails : finodPenalitydetails) {
					totalPenalityBal = totalPenalityBal.add(oddetails.getTotPenaltyBal());
				}

				List<FinODDetails> finodprofitdetails = finODDetailsDAO
						.getFinODPenalityByFinRef(feeWaiverHeader.getFinReference(), true, false);
				for (FinODDetails oddetails : finodprofitdetails) {
					totalLPIBal = totalLPIBal.add(oddetails.getLPIBal());
				}

				// validate the current waived amount against Late pay penalty.
				if (waiverDetail.getFeeTypeCode().equals(RepayConstants.ALLOCATION_ODC)) {
					BigDecimal waiverAmount = BigDecimal.ZERO;
					if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(waiverDetail.getTaxComponent())) {
						waiverAmount = waiverDetail.getCurrActualWaiver();
					} else {
						waiverAmount = waiverDetail.getCurrWaiverAmount();
					}
					if (waiverAmount.compareTo(totalPenalityBal) > 0) {
						valueParm[0] = String.valueOf(
								PennantApplicationUtil.amountFormate(waiverAmount, PennantConstants.defaultCCYDecPos));
						errParm[0] = waiverDetail.getFeeTypeDesc() + ": " + valueParm[0];
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "91136", errParm, valueParm), usrLanguage));
					}
				}

				// validate the current waived amount against the late pay profit
				if (waiverDetail.getFeeTypeCode().equals(RepayConstants.ALLOCATION_LPFT)
						&& waiverDetail.getCurrWaiverAmount().compareTo(totalLPIBal) > 0) {
					valueParm[0] = String.valueOf(waiverDetail.getCurrWaiverAmount());
					errParm[0] = waiverDetail.getFeeTypeDesc() + ": " + valueParm[0];
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "91136", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");

		return auditDetail;
	}

	@Override
	public List<ManualAdvise> getManualAdviseByFinRef(String finReference) {
		return this.manualAdviseDAO.getManualAdvise(finReference);
	}

	@Override
	public List<FinODDetails> getFinODBalByFinRef(String finReference) {
		return getFinODDetailsDAO().getFinODBalByFinRef(finReference);
	}

	private Map<String, BigDecimal> getTaxPercentages(String finReference) {
		return GSTCalculator.getTaxPercentages(finReference);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public FeeWaiverHeaderDAO getFeeWaiverHeaderDAO() {
		return feeWaiverHeaderDAO;
	}

	public void setFeeWaiverHeaderDAO(FeeWaiverHeaderDAO feeWaiverHeaderDAO) {
		this.feeWaiverHeaderDAO = feeWaiverHeaderDAO;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public FeeTypeDAO getFeeTypeDAO() {
		return feeTypeDAO;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public TaxHeaderDetailsService getTaxHeaderDetailsService() {
		return taxHeaderDetailsService;
	}

	public void setTaxHeaderDetailsService(TaxHeaderDetailsService taxHeaderDetailsService) {
		this.taxHeaderDetailsService = taxHeaderDetailsService;
	}

	public GSTInvoiceTxnService getGstInvoiceTxnService() {
		return gstInvoiceTxnService;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public ReceiptCalculator getReceiptCalculator() {
		return receiptCalculator;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public FinanceProfitDetailDAO getProfitDetailsDAO() {
		return profitDetailsDAO;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public RepaymentPostingsUtil getRepayPostingUtil() {
		return repayPostingUtil;
	}

	public void setRepayPostingUtil(RepaymentPostingsUtil repayPostingUtil) {
		this.repayPostingUtil = repayPostingUtil;
	}

}