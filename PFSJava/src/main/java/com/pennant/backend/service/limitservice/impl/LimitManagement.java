package com.pennant.backend.service.limitservice.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.commitment.CommitmentDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.limit.LimitDetailDAO;
import com.pennant.backend.dao.limit.LimitGroupLinesDAO;
import com.pennant.backend.dao.limit.LimitHeaderDAO;
import com.pennant.backend.dao.limit.LimitReferenceMappingDAO;
import com.pennant.backend.dao.limit.LimitTransactionDetailsDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.limit.LimitDetails;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.limit.LimitReferenceMapping;
import com.pennant.backend.model.limit.LimitTransactionDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleReturnType;

public class LimitManagement {

	private static Logger				logger			= Logger.getLogger(LimitManagement.class);
	private CommitmentDAO				commitmentDAO;
	private LimitDetailDAO				limitDetailDAO;
	private LimitHeaderDAO				limitHeaderDAO;
	private RuleExecutionUtil			ruleExecutionUtil;
	private LimitGroupLinesDAO			limitGroupLinesDAO;
	private LimitReferenceMappingDAO	limitReferenceMappingDAO;
	private LimitTransactionDetailsDAO	limitTransactionDetailDAO;
	private FinanceDisbursementDAO		financeDisbursementDAO;
	private FinanceMainDAO				financeMainDAO;

	public static final String			KEY_LIMITAMT	= "LIMITAMT";
	public static final String			KEY_LINEEXPIRY	= "LINEEXPIRY";

	/**
	 * @param financeDetail
	 * @param overide
	 * @param tranType
	 * @return
	 */
	public List<ErrorDetails> processLoanLimitOrgination(FinanceDetail financeDetail, boolean overide, String tranType,
			boolean validateOnly) {
		logger.debug(" Entering ");

		ArrayList<ErrorDetails> errors = new ArrayList<ErrorDetails>();
		FinScheduleData finschData = financeDetail.getFinScheduleData();
		FinanceMain finMain = finschData.getFinanceMain();
		FinanceType finType = finschData.getFinanceType();
		String finCcy = finMain.getFinCcy();
		String usrlang = finMain.getUserDetails().getUsrLanguage();
		Customer customer = financeDetail.getCustomerDetails().getCustomer();
		long custId = customer.getCustID();
		long groupId = customer.getCustGroupID();
		LimitHeader custHeader = null;
		LimitHeader groupHeader = null;
		boolean limitrequired = finType.isLimitRequired();
		boolean allowOverride = finType.isOverrideLimit();

		if (custId != 0) {
			custHeader = limitHeaderDAO.getLimitHeaderByCustomerId(custId, "_AView");
		}

		if (groupId != 0) {
			groupHeader = limitHeaderDAO.getLimitHeaderByCustomerGroupCode(groupId, "_AView");
		}

		// If limit required is true in Finance type			
		if (limitrequired) {
			if (custHeader == null) {
				errors.add(new ErrorDetails("60310", null));
				return ErrorUtil.getErrorDetails(errors, usrlang);
			} else {
				if (!custHeader.isActive()) {
					StringBuilder key = new StringBuilder(custHeader.getLimitStructureCode());
					key.append("-");
					key.append(custHeader.getStructureName());
					errors.add(new ErrorDetails("60313", new String[] { key.toString() }));
					return ErrorUtil.getErrorDetails(errors, usrlang);
				}
			}
		} else {
			//if limit is not required in the loan type
			allowOverride = true;
		}

		if (StringUtils.trimToEmpty(finMain.getFinSourceID()).equals(PennantConstants.FINSOURCE_ID_API)) {
			allowOverride = false;
		}

		Date dateToValidate = DateUtility.getAppDate();

		for (FinanceDisbursement disbursement : finschData.getDisbursementDetails()) {
			if (disbursement.getDisbDate().compareTo(dateToValidate) > 0) {
				dateToValidate = disbursement.getDisbDate();
			}
		}

		BigDecimal tranAmt = BigDecimal.ZERO;
		if (LimitConstants.BLOCK.equals(tranType)) {
			tranAmt = finMain.getFinAssetValue();
		} else if (LimitConstants.APPROVE.equals(tranType)) {
			for (FinanceDisbursement disbursement : finschData.getDisbursementDetails()) {
				tranAmt = tranAmt.add(disbursement.getDisbAmount()).add(disbursement.getFeeChargeAmt());
				if (disbursement.getDisbDate().getTime() == finMain.getFinStartDate().getTime()) {
					tranAmt = tranAmt.subtract(finMain.getDownPayment());
				}
			}
		}

		//loop through disbursements
		int disbSeq = 0;
		//Customer limit process
		if (custHeader != null) {
			// check already mapping available or not 
			LimitReferenceMapping mapping = identifyLine(finMain, finType, custHeader.getHeaderId());
			if (mapping != null) {
				if (mapping.isNewRecord()) {
					limitReferenceMappingDAO.save(mapping);
				}

				//in origination there should be one block
				LimitTransactionDetail limitTranDetail = getFinTransaction(finMain.getFinReference(),
						custHeader.getHeaderId(), LimitConstants.BLOCK, disbSeq);

				BigDecimal blockAmount = BigDecimal.ZERO;

				if (limitTranDetail != null) {
					blockAmount = limitTranDetail.getLimitAmount();
				}

				if (LimitConstants.UNBLOCK.equals(tranType)) {
					tranAmt = blockAmount;
				}

				BigDecimal limitAmount = CalculationUtil.getConvertedAmount(finCcy, custHeader.getLimitCcy(), tranAmt);
				errors.addAll(updateLimitOrgination(mapping, tranType, allowOverride, limitAmount, overide,
						validateOnly, dateToValidate, limitTranDetail, blockAmount));
				if (!errors.isEmpty()) {
					return ErrorUtil.getErrorDetails(errors, usrlang);
				}
				if (!mapping.isProceeed()) {
					return errors;
				}
				if (!validateOnly) {
					//log transaction
					logFinanceTransasction(finMain, custHeader, disbSeq, tranType, overide, tranAmt, limitAmount);
				}
			}
		}

		//Customer group limit process
		if (groupHeader != null) {
			// check already mapping available or not 
			LimitReferenceMapping mapping = identifyLine(finMain, finType, groupHeader.getHeaderId());
			if (mapping != null) {
				if (mapping.isNewRecord()) {
					limitReferenceMappingDAO.save(mapping);
				}
				//in origination there should be one block
				LimitTransactionDetail limitTranDetail = getFinTransaction(finMain.getFinReference(),
						groupHeader.getHeaderId(), LimitConstants.BLOCK, disbSeq);
				BigDecimal blockAmount = BigDecimal.ZERO;
				if (limitTranDetail != null) {
					blockAmount = limitTranDetail.getLimitAmount();
				}

				if (LimitConstants.UNBLOCK.equals(tranType)) {
					tranAmt = blockAmount;
				}

				BigDecimal limitAmount = CalculationUtil.getConvertedAmount(finCcy, groupHeader.getLimitCcy(), tranAmt);
				errors.addAll(updateLimitOrgination(mapping, tranType, allowOverride, limitAmount, overide,
						validateOnly, dateToValidate, limitTranDetail, blockAmount));

				if (!errors.isEmpty()) {
					return ErrorUtil.getErrorDetails(errors, usrlang);
				}

				if (!mapping.isProceeed()) {
					return errors;
				}

				if (!validateOnly) {
					//log transaction
					logFinanceTransasction(finMain, groupHeader, disbSeq, tranType, overide, tranAmt, limitAmount);
				}
			}
		}
		logger.debug(" Entering ");
		return errors;
	}

	/**
	 * @param mapping
	 * @param tranType
	 * @param allowOverride
	 * @param limitAmount
	 * @param disbSeq
	 * @param override
	 * @return
	 */
	private List<ErrorDetails> updateLimitOrgination(LimitReferenceMapping mapping, String tranType,
			boolean allowOverride, BigDecimal limitAmount, boolean override, boolean validateOnly, Date disbDate,
			LimitTransactionDetail limitTranDetail, BigDecimal blockAmount) {
		logger.debug(" Entering ");

		//get limit details by line and group associated with it
		List<LimitDetails> limitDetails = getCustomerLimitDetails(mapping);

		ArrayList<ErrorDetails> errors = new ArrayList<ErrorDetails>();

		//	validate
		if (StringUtils.equals(LimitConstants.BLOCK, tranType)) {
			if (limitTranDetail != null) {
				boolean block = removePreviuosBlockIfAny(limitTranDetail.getLimitAmount(), limitAmount, limitDetails,
						limitTranDetail.getId());
				if (!block) {
					mapping.setProceeed(false);
					return errors;
				}
			}

			if (!override) {
				errors.addAll(validate(limitDetails, limitAmount, blockAmount, allowOverride, disbDate));
				if (!errors.isEmpty()) {
					return errors;
				}
			}

		} else if (StringUtils.equals(LimitConstants.APPROVE, tranType)) {
			if (!override) {
				errors.addAll(validate(limitDetails, limitAmount, blockAmount, allowOverride, disbDate));
				if (!errors.isEmpty()) {
					return errors;
				}
			}
		}

		//if only validate then do not update
		if (validateOnly) {
			return errors;
		}

		//update limit details
		if (limitDetails != null) {
			switch (tranType) {
			case LimitConstants.BLOCK:

				for (LimitDetails details : limitDetails) {
					details.setVersion(details.getVersion() + 1);
					details.setReservedLimit(details.getReservedLimit().add(limitAmount));
					limitDetailDAO.updateReserveUtilise(details, "");
				}
				break;
			// loan rejected
			case LimitConstants.UNBLOCK:

				if (limitTranDetail != null) {
					for (LimitDetails details : limitDetails) {
						details.setVersion(details.getVersion() + 1);
						//reverse the utilization in case of loan reject
						details.setReservedLimit(details.getReservedLimit().subtract(blockAmount));
						limitDetailDAO.updateReserveUtilise(details, "");
					}
				}
				break;
			case LimitConstants.APPROVE:

				for (LimitDetails details : limitDetails) {
					details.setVersion(details.getVersion() + 1);
					if (blockAmount.compareTo(BigDecimal.ZERO) != 0) {
						if (blockAmount.compareTo(limitAmount) < 0) {
							details.setReservedLimit(details.getReservedLimit().subtract(blockAmount));
						} else {
							details.setReservedLimit(details.getReservedLimit().subtract(limitAmount));
						}
					}

					details.setUtilisedLimit(details.getUtilisedLimit().add(limitAmount));
					limitDetailDAO.updateReserveUtilise(details, "");
				}
				break;
			default:
				break;
			}
		}

		return errors;
	}

	public List<ErrorDetails> processLoanDisbursments(FinanceDetail financeDetail, boolean overide, String tranType,
			boolean validateOnly) {
		logger.debug(" Entering ");

		ArrayList<ErrorDetails> errors = new ArrayList<ErrorDetails>();
		FinScheduleData finschData = financeDetail.getFinScheduleData();
		FinanceMain finMain = finschData.getFinanceMain();
		FinanceType finType = finschData.getFinanceType();
		String finCcy = finMain.getFinCcy();
		String usrlang = finMain.getUserDetails().getUsrLanguage();
		Customer customer = financeDetail.getCustomerDetails().getCustomer();
		long custId = customer.getCustID();
		long groupId = customer.getCustGroupID();
		LimitHeader custHeader = null;
		LimitHeader groupHeader = null;
		boolean allowOverride = finType.isOverrideLimit();

		if (custId != 0) {
			custHeader = limitHeaderDAO.getLimitHeaderByCustomerId(custId, "_AView");
		}

		if (groupId != 0) {
			groupHeader = limitHeaderDAO.getLimitHeaderByCustomerGroupCode(groupId, "_AView");
		}

		if (StringUtils.trimToEmpty(finMain.getFinSourceID()).equals(PennantConstants.FINSOURCE_ID_API)) {
			allowOverride = false;
		}

		BigDecimal tranAmt = BigDecimal.ZERO;

		List<FinanceDisbursement> approvedDisbursments = financeDisbursementDAO
				.getFinanceDisbursementDetails(finMain.getFinReference(), "", false);
		Date datemaxDate = DateUtility.getAppDate();

		for (FinanceDisbursement disbursement : finschData.getDisbursementDetails()) {
			//check the current status is cancel if cancel check the approved status also as cancel
			if (StringUtils.equals(disbursement.getDisbStatus(), FinanceConstants.DISB_STATUS_CANCEL)
					&& isApprovedAndCancelled(disbursement, approvedDisbursments)) {
				continue;
			}

			if (!StringUtils.equals(disbursement.getDisbStatus(), FinanceConstants.DISB_STATUS_CANCEL)) {
				if (isApprovedDisbursments(disbursement, approvedDisbursments)) {
					continue;
				}
			}

			if (disbursement.getDisbDate().compareTo(datemaxDate) > 0) {
				datemaxDate = disbursement.getDisbDate();
			}

			tranAmt = tranAmt.add(disbursement.getDisbAmount()).add(disbursement.getFeeChargeAmt());
		}
		//servicing 1
		int disbSeq = 0;
		if (StringUtils.equals(LimitConstants.BLOCK, tranType)) {
			//to identify  current Disbursement amount we are maintaining the value as -1 
			disbSeq = -1;
		}
		//Customer limit process
		if (custHeader != null) {
			// check already mapping available or not 
			LimitReferenceMapping mapping = identifyLine(finMain, finType, custHeader.getHeaderId());

			if (mapping != null) {
				if (mapping.isNewRecord()) {
					limitReferenceMappingDAO.save(mapping);
				}

				BigDecimal limitAmount = CalculationUtil.getConvertedAmount(finCcy, custHeader.getLimitCcy(), tranAmt);
				errors.addAll(procesServicingLimits(mapping, tranType, allowOverride, limitAmount, disbSeq, overide,
						datemaxDate, validateOnly));

				if (!errors.isEmpty()) {
					return ErrorUtil.getErrorDetails(errors, usrlang);
				}

				if (!mapping.isProceeed()) {
					return errors;
				}

				if (!validateOnly) {
					//log transaction
					logFinanceTransasction(finMain, custHeader, disbSeq, tranType, overide, tranAmt, limitAmount);
				}
			}
		}

		//Customer group limit process
		if (groupHeader != null) {
			// check already mapping available or not 
			LimitReferenceMapping mapping = identifyLine(finMain, finType, groupHeader.getHeaderId());
			if (mapping != null) {

				if (mapping.isNewRecord()) {
					limitReferenceMappingDAO.save(mapping);
				}

				BigDecimal limitAmount = CalculationUtil.getConvertedAmount(finCcy, groupHeader.getLimitCcy(), tranAmt);
				errors.addAll(procesServicingLimits(mapping, tranType, allowOverride, limitAmount, disbSeq, overide,
						datemaxDate, validateOnly));
				if (!errors.isEmpty()) {
					return ErrorUtil.getErrorDetails(errors, usrlang);
				}

				if (!mapping.isProceeed()) {
					return errors;
				}

				if (!validateOnly) {
					//log transaction
					logFinanceTransasction(finMain, groupHeader, disbSeq, tranType, overide, tranAmt, limitAmount);
				}
			}
		}

		logger.debug(" Entering ");
		return errors;
	}

	private List<ErrorDetails> procesServicingLimits(LimitReferenceMapping mapping, String tranType,
			boolean allowOverride, BigDecimal limitAmount, int disbSeq, boolean override, Date appdate,
			boolean validateOnly) {
		logger.debug(" Entering ");

		String finref = mapping.getReferenceNumber();
		long headerId = mapping.getHeaderId();
		ArrayList<ErrorDetails> errors = new ArrayList<ErrorDetails>();
		LimitTransactionDetail prvblock = getFinTransaction(finref, headerId, LimitConstants.BLOCK, -1);
		//get limit details by line and group associated with it
		List<LimitDetails> limitDetails = getCustomerLimitDetails(mapping);

		LimitTransactionDetail blockAmt = limitTransactionDetailDAO.geLoantAvaliableReserve(finref,
				LimitConstants.BLOCK, headerId);
		LimitTransactionDetail approvedAmt = limitTransactionDetailDAO.geLoantAvaliableReserve(finref,
				LimitConstants.APPROVE, headerId);
		LimitTransactionDetail unblockkAmt = limitTransactionDetailDAO.geLoantAvaliableReserve(finref,
				LimitConstants.UNBLOCK, headerId);
		BigDecimal prvReserv = blockAmt.getLimitAmount()
				.subtract(approvedAmt.getLimitAmount().subtract(unblockkAmt.getLimitAmount()));

		//	validate
		if (StringUtils.equals(LimitConstants.BLOCK, tranType)) {
			if (prvblock != null) {
				boolean block = removePreviuosBlockIfAny(prvblock.getLimitAmount(), limitAmount, limitDetails,
						prvblock.getId());
				if (!block) {
					mapping.setProceeed(false);
					return errors;
				}
			}
			//	validate
			if (!override) {
				errors.addAll(validate(limitDetails, limitAmount, BigDecimal.ZERO, allowOverride, appdate));
				if (!errors.isEmpty()) {
					return errors;
				}
			}

		} else if (StringUtils.equals(LimitConstants.APPROVE, tranType)) {
			//	validate
			if (!override) {
				BigDecimal amoutToValidate = BigDecimal.ZERO;

				if (prvReserv.compareTo(BigDecimal.ZERO) > 0) {
					amoutToValidate = prvReserv;
				}
				errors.addAll(validate(limitDetails, limitAmount, amoutToValidate, allowOverride, appdate));
				if (!errors.isEmpty()) {
					return errors;
				}
			}
		}

		//if only validate then do not update
		if (validateOnly) {
			return errors;
		}

		//update
		if (limitDetails != null) {
			switch (tranType) {
			// Block will used in case of normal loan
			case LimitConstants.BLOCK:

				for (LimitDetails details : limitDetails) {
					details.setVersion(details.getVersion() + 1);
					details.setReservedLimit(details.getReservedLimit().add(limitAmount));
					limitDetailDAO.updateReserveUtilise(details, "");
				}
				break;
			// cancel disbursement
			case LimitConstants.UNBLOCK:
				for (LimitDetails details : limitDetails) {
					details.setVersion(details.getVersion() + 1);
					details.setReservedLimit(details.getReservedLimit().add(limitAmount));
					details.setUtilisedLimit(details.getUtilisedLimit().subtract(limitAmount));
					limitDetailDAO.updateReserveUtilise(details, "");
				}
				break;
			// add disbursement rejected
			case LimitConstants.CANCIL:

				if (prvblock != null) {
					for (LimitDetails details : limitDetails) {
						details.setVersion(details.getVersion() + 1);
						//reverse the reserve in case of loan reject
						details.setReservedLimit(details.getReservedLimit().subtract(prvblock.getLimitAmount()));
						limitDetailDAO.updateReserveUtilise(details, "");
					}
					limitTransactionDetailDAO.updateSeq(prvblock.getTransactionId(), 0);
				} else {
					//if there is no block now then nothing to cancel. Log not required
					mapping.setProceeed(false);
				}
				break;

			case LimitConstants.APPROVE:
				//get previous reserve amount

				for (LimitDetails details : limitDetails) {
					details.setVersion(details.getVersion() + 1);
					if (prvReserv.compareTo(BigDecimal.ZERO) > 0) {
						details.setReservedLimit(details.getReservedLimit().subtract(limitAmount));
					}
					details.setUtilisedLimit(details.getUtilisedLimit().add(limitAmount));
					limitDetailDAO.updateReserveUtilise(details, "");
				}

				if (prvblock != null) {
					limitTransactionDetailDAO.updateSeq(prvblock.getTransactionId(), 0);
				}

				break;

			default:
				break;
			}
		}
		return errors;
	}

	/**
	 * @param finref
	 * @param headerId
	 * @param disbursement
	 * @param limitAmount
	 * @param custLimitDetails
	 */
	public void processLoanCancel(FinanceDetail financeDetail, boolean overide) {
		logger.debug(" Entering ");

		FinScheduleData finschData = financeDetail.getFinScheduleData();
		FinanceMain finMain = finschData.getFinanceMain();
		FinanceType finType = finschData.getFinanceType();
		String finCcy = finMain.getFinCcy();
		Customer customer = financeDetail.getCustomerDetails().getCustomer();
		long custId = customer.getCustID();
		long groupId = customer.getCustGroupID();
		LimitHeader custHeader = null;
		LimitHeader groupHeader = null;

		if (custId != 0) {
			custHeader = limitHeaderDAO.getLimitHeaderByCustomerId(custId, "_AView");
		}

		if (groupId != 0) {
			groupHeader = limitHeaderDAO.getLimitHeaderByCustomerGroupCode(groupId, "_AView");
		}

		BigDecimal tranAmt = BigDecimal.ZERO;
		List<FinanceDisbursement> approvedDisbursments = financeDisbursementDAO
				.getFinanceDisbursementDetails(finMain.getFinReference(), "", false);

		for (FinanceDisbursement disbursement : approvedDisbursments) {
			if (StringUtils.trimToEmpty(disbursement.getDisbStatus()).equals(FinanceConstants.DISB_STATUS_CANCEL)) {
				continue;
			}
			tranAmt = tranAmt.add(disbursement.getDisbAmount()).add(disbursement.getFeeChargeAmt());
			if (disbursement.getDisbDate().getTime() == finMain.getFinStartDate().getTime()) {
				tranAmt = tranAmt.subtract(finMain.getDownPayment());
			}
		}
		//loop through disbursements
		int disbSeq = 0;
		//Customer limit process
		if (custHeader != null) {
			// check already mapping available or not 
			LimitReferenceMapping mapping = identifyLine(finMain, finType, custHeader.getHeaderId());

			if (mapping != null) {
				List<LimitDetails> limitDetails = getCustomerLimitDetails(mapping);
				BigDecimal limitAmount = CalculationUtil.getConvertedAmount(finCcy, custHeader.getLimitCcy(), tranAmt);
				for (LimitDetails details : limitDetails) {
					details.setVersion(details.getVersion() + 1);
					details.setUtilisedLimit(details.getUtilisedLimit().subtract(limitAmount));
					limitDetailDAO.updateReserveUtilise(details, "");
				}
				//log transaction
				logFinanceTransasction(finMain, custHeader, disbSeq, LimitConstants.CANCIL, overide, tranAmt,
						limitAmount);
			}
		}

		//Customer group limit process
		if (groupHeader != null) {
			// check already mapping available or not 
			LimitReferenceMapping mapping = identifyLine(finMain, finType, groupHeader.getHeaderId());
			if (mapping != null) {
				List<LimitDetails> limitDetails = getCustomerLimitDetails(mapping);
				BigDecimal limitAmount = CalculationUtil.getConvertedAmount(finCcy, groupHeader.getLimitCcy(), tranAmt);
				for (LimitDetails details : limitDetails) {
					details.setVersion(details.getVersion() + 1);
					details.setUtilisedLimit(details.getUtilisedLimit().subtract(limitAmount));
					limitDetailDAO.updateReserveUtilise(details, "");
				}
				//log transaction
				logFinanceTransasction(finMain, groupHeader, disbSeq, LimitConstants.CANCIL, overide, tranAmt,
						limitAmount);
			}
		}

		logger.debug(" Leaving ");
	}

	/**
	 * @param finref
	 * @param headerId
	 * @param disbursement
	 * @param limitAmount
	 * @param custLimitDetails
	 */
	public List<ErrorDetails> processLimitIncrease(FinanceDetail financeDetail, boolean override,
			boolean validateOnly) {
		logger.debug(" Entering ");

		ArrayList<ErrorDetails> errors = new ArrayList<ErrorDetails>();
		FinScheduleData finschData = financeDetail.getFinScheduleData();
		FinanceMain finMain = finschData.getFinanceMain();
		FinanceType finType = finschData.getFinanceType();
		String finCcy = finMain.getFinCcy();
		Customer customer = financeDetail.getCustomerDetails().getCustomer();
		long custId = customer.getCustID();
		long groupId = customer.getCustGroupID();
		LimitHeader custHeader = null;
		LimitHeader groupHeader = null;
		boolean allowOverride = finType.isOverrideLimit();
		String usrlang = finMain.getUserDetails().getUsrLanguage();

		if (custId != 0) {
			custHeader = limitHeaderDAO.getLimitHeaderByCustomerId(custId, "_AView");
		}

		if (groupId != 0) {
			groupHeader = limitHeaderDAO.getLimitHeaderByCustomerGroupCode(groupId, "_AView");
		}

		BigDecimal tranAmt = BigDecimal.ZERO;

		BigDecimal revReserved = financeMainDAO.getFinAssetValue(finMain.getFinReference());
		BigDecimal currAssestValue = finMain.getFinAssetValue();
		tranAmt = currAssestValue.subtract(revReserved);

		if (tranAmt.compareTo(BigDecimal.ZERO) <= 0) {
			return errors;
		}

		//loop through disbursements
		int disbSeq = 0;
		//Customer limit process
		if (custHeader != null) {
			// check already mapping available or not 
			LimitReferenceMapping mapping = limitReferenceMappingDAO.getLimitReferencemapping(finMain.getFinReference(),
					custHeader.getHeaderId());
			if (mapping != null) {
				List<LimitDetails> limitDetails = getCustomerLimitDetails(mapping);
				BigDecimal limitAmount = CalculationUtil.getConvertedAmount(finCcy, custHeader.getLimitCcy(), tranAmt);
				//	validate
				if (!override) {
					errors.addAll(validate(limitDetails, limitAmount, BigDecimal.ZERO, allowOverride,
							DateUtility.getAppDate()));
					if (!errors.isEmpty()) {
						return ErrorUtil.getErrorDetails(errors, usrlang);
					}
				}
				//if only validate then do not update
				if (validateOnly) {
					return errors;
				}

				for (LimitDetails details : limitDetails) {
					details.setVersion(details.getVersion() + 1);
					details.setReservedLimit(details.getReservedLimit().add(tranAmt));
					limitDetailDAO.updateReserveUtilise(details, "");
				}

				//log transaction
				logFinanceTransasction(finMain, custHeader, disbSeq, LimitConstants.BLOCK, override, tranAmt,
						limitAmount);
			}
		}

		//Customer group limit process
		if (groupHeader != null) {
			// check already mapping available or not 
			LimitReferenceMapping mapping = limitReferenceMappingDAO.getLimitReferencemapping(finMain.getFinReference(),
					groupHeader.getHeaderId());
			if (mapping != null) {
				List<LimitDetails> limitDetails = getCustomerLimitDetails(mapping);
				BigDecimal limitAmount = CalculationUtil.getConvertedAmount(finCcy, groupHeader.getLimitCcy(), tranAmt);
				//	validate
				if (!override) {
					errors.addAll(validate(limitDetails, limitAmount, BigDecimal.ZERO, allowOverride,
							DateUtility.getAppDate()));
					if (!errors.isEmpty()) {
						return ErrorUtil.getErrorDetails(errors, usrlang);
					}
				}
				//if only validate then do not update
				if (validateOnly) {
					return errors;
				}

				for (LimitDetails details : limitDetails) {
					details.setVersion(details.getVersion() + 1);
					details.setReservedLimit(details.getReservedLimit().add(tranAmt));
					limitDetailDAO.updateReserveUtilise(details, "");
				}
				//log transaction
				logFinanceTransasction(finMain, groupHeader, disbSeq, LimitConstants.BLOCK, override, tranAmt,
						limitAmount);
			}
		}
		logger.debug(" Leaving ");
		return errors;
	}

	/*
	 * Re payment
	 */

	/**
	 * @param financeDetail
	 * @param overide
	 * @param tranType
	 * @return
	 */
	public void processLoanRepay(FinanceMain finMain, Customer customer, BigDecimal transAmount, String prodCategory) {
		logger.debug(" Entering ");

		String finCcy = finMain.getFinCcy();
		long custId = customer.getCustID();
		long groupId = customer.getCustGroupID();
		LimitHeader custHeader = null;
		LimitHeader groupHeader = null;

		String tansType = LimitConstants.PRINPAY;
		if (prodCategory.equals(FinanceConstants.PRODUCT_ODFACILITY)) {
			tansType = LimitConstants.REPAY;
		}

		if (custId != 0) {
			custHeader = limitHeaderDAO.getLimitHeaderByCustomerId(custId, "");
		}

		if (groupId != 0) {
			groupHeader = limitHeaderDAO.getLimitHeaderByCustomerGroupCode(groupId, "");
		}

		//Customer limit process
		if (custHeader != null) {
			// check already mapping available or not 
			LimitReferenceMapping mapping = limitReferenceMappingDAO.getLimitReferencemapping(finMain.getFinReference(),
					custHeader.getHeaderId());
			if (mapping != null) {
				BigDecimal limitAmount = CalculationUtil.getConvertedAmount(finCcy, custHeader.getLimitCcy(),
						transAmount);
				List<LimitDetails> custLimitDetails = getCustomerLimitDetails(mapping);
				processRepay(mapping, limitAmount, custLimitDetails, tansType);
				//log transaction
				logFinanceTransasction(finMain, custHeader, 0, tansType, false, transAmount, limitAmount);
			}
		}

		//Customer group limit process
		if (groupHeader != null) {
			// check already mapping available or not 
			LimitReferenceMapping mapping = limitReferenceMappingDAO.getLimitReferencemapping(finMain.getFinReference(),
					groupHeader.getHeaderId());
			if (mapping != null) {
				BigDecimal limitAmount = CalculationUtil.getConvertedAmount(finCcy, groupHeader.getLimitCcy(),
						transAmount);
				List<LimitDetails> custLimitDetails = getCustomerLimitDetails(mapping);
				processRepay(mapping, limitAmount, custLimitDetails, tansType);
				//log transaction
				logFinanceTransasction(finMain, groupHeader, 0, tansType, false, transAmount, limitAmount);
			}
		}

		logger.debug(" Entering ");
	}

	/**
	 * @param mapping
	 * @param limitAmount
	 * @param custLimitDetails
	 */
	private void processRepay(LimitReferenceMapping mapping, BigDecimal limitAmount,
			List<LimitDetails> custLimitDetails, String tansType) {
		logger.debug(" Entering ");
		for (LimitDetails details : custLimitDetails) {
			details.setVersion(details.getVersion() + 1);
			if (StringUtils.equals(tansType, LimitConstants.REPAY)) {
				//Check need add it to reserved or not
				details.setUtilisedLimit(details.getUtilisedLimit().subtract(limitAmount));
				details.setReservedLimit(details.getReservedLimit().add(limitAmount));
				limitDetailDAO.updateReserveUtilise(details, "");
			} else if (StringUtils.equals(tansType, LimitConstants.PRINPAY)) {
				//Check need add it to reserved or not
				details.setUtilisedLimit(details.getUtilisedLimit().subtract(limitAmount));
				limitDetailDAO.updateReserveUtilise(details, "");
			}

		}
		logger.debug(" Leaving ");
	}

	/**
	 * @param financeDetail
	 * @param overide
	 * @param tranType
	 * @return
	 */
	public void processLoanRepayCancel(FinanceMain finMain, Customer customer, BigDecimal transAmount,
			String prodCategory) {
		logger.debug(" Entering ");

		String finCcy = finMain.getFinCcy();
		long custId = customer.getCustID();
		long groupId = customer.getCustGroupID();
		LimitHeader custHeader = null;
		LimitHeader groupHeader = null;

		String tansType = LimitConstants.PRINPAY;
		if (prodCategory.equals(FinanceConstants.PRODUCT_ODFACILITY)) {
			tansType = LimitConstants.REPAY;
		}

		if (custId != 0) {
			custHeader = limitHeaderDAO.getLimitHeaderByCustomerId(custId, "");
		}

		if (groupId != 0) {
			groupHeader = limitHeaderDAO.getLimitHeaderByCustomerGroupCode(groupId, "");
		}

		//Customer limit process
		if (custHeader != null) {
			// check already mapping available or not 
			LimitReferenceMapping mapping = limitReferenceMappingDAO.getLimitReferencemapping(finMain.getFinReference(),
					custHeader.getHeaderId());
			if (mapping != null) {
				BigDecimal limitAmount = CalculationUtil.getConvertedAmount(finCcy, custHeader.getLimitCcy(),
						transAmount);
				List<LimitDetails> custLimitDetails = getCustomerLimitDetails(mapping);
				processRepayCancel(mapping, limitAmount, custLimitDetails, tansType);
				//log transaction
				logFinanceTransasction(finMain, custHeader, 0, LimitConstants.CANCIL, false, transAmount, limitAmount);
			}
		}

		//Customer group limit process
		if (groupHeader != null) {
			// check already mapping available or not 
			LimitReferenceMapping mapping = limitReferenceMappingDAO.getLimitReferencemapping(finMain.getFinReference(),
					groupHeader.getHeaderId());
			if (mapping != null) {
				BigDecimal limitAmount = CalculationUtil.getConvertedAmount(finCcy, groupHeader.getLimitCcy(),
						transAmount);
				List<LimitDetails> custLimitDetails = getCustomerLimitDetails(mapping);
				processRepayCancel(mapping, limitAmount, custLimitDetails, tansType);
				//log transaction
				logFinanceTransasction(finMain, groupHeader, 0, LimitConstants.CANCIL, false, transAmount, limitAmount);
			}
		}

		logger.debug(" Entering ");
	}

	/**
	 * @param mapping
	 * @param limitAmount
	 * @param custLimitDetails
	 */
	private void processRepayCancel(LimitReferenceMapping mapping, BigDecimal limitAmount,
			List<LimitDetails> custLimitDetails, String tansType) {
		logger.debug(" Entering ");

		for (LimitDetails details : custLimitDetails) {
			details.setVersion(details.getVersion() + 1);
			if (StringUtils.equals(tansType, LimitConstants.REPAY)) {
				//Check need add it to reserved or not
				details.setUtilisedLimit(details.getUtilisedLimit().add(limitAmount));
				details.setReservedLimit(details.getReservedLimit().subtract(limitAmount));
				limitDetailDAO.updateReserveUtilise(details, "");
			} else if (StringUtils.equals(tansType, LimitConstants.PRINPAY)) {
				//Check need add it to reserved or not
				details.setUtilisedLimit(details.getUtilisedLimit().add(limitAmount));
				limitDetailDAO.updateReserveUtilise(details, "");
			}
		}
		logger.debug(" Leaving ");
	}

	/**
	 * @param prvBlockAmount
	 * @param limitAmount
	 * @param custLimitDetails
	 * @param tranId
	 */
	private boolean removePreviuosBlockIfAny(BigDecimal prvBlockAmount, BigDecimal limitAmount,
			List<LimitDetails> custLimitDetails, long tranId) {
		boolean block = false;
		if (prvBlockAmount.compareTo(BigDecimal.ZERO) != 0 && prvBlockAmount.compareTo(limitAmount) != 0) {
			//unblock previous 
			for (LimitDetails details : custLimitDetails) {
				details.setReservedLimit(details.getReservedLimit().subtract(prvBlockAmount));
				limitDetailDAO.updateReserveUtilise(details, "");
			}
			//delete the transaction
			limitTransactionDetailDAO.delete(tranId);
			block = true;
		}
		return block;
	}

	/**
	 * @param limitDetails
	 * @param limitAmount
	 * @param financeMain
	 * @return
	 */
	private List<ErrorDetails> validate(List<LimitDetails> limitDetails, BigDecimal limitAmount,
			BigDecimal preLimitAmount, boolean overrideAllowed, Date appdate) {

		ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
		for (LimitDetails detail : limitDetails) {
			ErrorDetails error = validateLimitDetail(detail, limitAmount, preLimitAmount, overrideAllowed, appdate);
			if (error != null) {
				errorDetails.add(error);
			}
		}

		return errorDetails;
	}

	/**
	 * @param limitDetail
	 * @param tranAmount
	 * @param preLimitAmount
	 * @param tranType
	 * @param overrideAllowed
	 * @return
	 */
	private ErrorDetails validateLimitDetail(LimitDetails limitDetail, BigDecimal tranAmount, BigDecimal preLimitAmount,
			boolean overrideAllowed, Date date) {

		// If limit expired then return error  
		if (limitDetail != null) {
			String param = limitDetail.getLimitLine();
			if (param == null) {
				param = limitDetail.getGroupCode();
			}

			if (limitDetail.getExpiryDate() != null) {
				if (limitDetail.getExpiryDate().compareTo(date) <= 0) {
					return new ErrorDetails(KEY_LINEEXPIRY, "60311", new String[] { param }, null);
				}
			}
			BigDecimal limitAmount = BigDecimal.ZERO;
			if (StringUtils.equals(LimitConstants.LIMIT_CHECK_RESERVED, limitDetail.getLimitChkMethod())) {
				limitAmount = limitDetail.getReservedexposure().add(tranAmount).subtract(preLimitAmount);
			} else if (StringUtils.equals(LimitConstants.LIMIT_CHECK_ACTUAL, limitDetail.getLimitChkMethod())) {
				limitAmount = limitDetail.getActualexposure().add(tranAmount);
			}
			//If limit check  is true in Limit details 
			if (limitDetail.isLimitCheck() && limitDetail.getLimitSanctioned().compareTo(limitAmount) == -1) {
				if (overrideAllowed) {
					//return new ErrorDetails("60312", new String[] { param });
					return new ErrorDetails(KEY_LIMITAMT, "60312", new String[] { param }, null);
				} else {
					return new ErrorDetails(KEY_LIMITAMT, "60314", new String[] { param }, null);
				}
			}
		}
		return null;
	}

	/**
	 * @param limitLine
	 * @param isCustomer
	 * @param header
	 * @return
	 */
	private List<LimitDetails> getCustomerLimitDetails(LimitReferenceMapping mapping) {
		long headerId = mapping.getHeaderId();
		String limitLine = mapping.getLimitLine();
		List<LimitDetails> list = new ArrayList<LimitDetails>();
		List<String> groupCodes = new ArrayList<>();
		groupCodes.add(LimitConstants.LIMIT_ITEM_TOTAL);
		if (!limitLine.equals(LimitConstants.LIMIT_ITEM_UNCLSFD)) {
			String groupCode = limitGroupLinesDAO.getGroupByLineAndHeader(limitLine, headerId);
			String parentGroup = groupCode;
			while (!StringUtils.isEmpty(parentGroup)) {
				groupCodes.add(parentGroup);
				parentGroup = limitGroupLinesDAO.getGroupByGroupAndHeader(parentGroup, headerId);
			}
		}
		list = limitDetailDAO.getLimitByLineAndgroup(headerId, limitLine, groupCodes);
		return list;
	}

	/**
	 * @param refCode
	 * @param finreference
	 * @param limitLine
	 * @return
	 */
	private LimitReferenceMapping getLimitRefMapping(String refCode, String finreference, String limitLine,
			long headerId) {
		LimitReferenceMapping limitReferenceMapping = new LimitReferenceMapping();
		limitReferenceMapping.setNewRecord(true);
		limitReferenceMapping.setReferenceNumber(finreference);
		limitReferenceMapping.setReferenceCode(refCode);
		limitReferenceMapping.setLimitLine(limitLine);
		limitReferenceMapping.setHeaderId(headerId);
		return limitReferenceMapping;
	}

	/**
	 * @param financeMain
	 * @param overide
	 * @param tranType
	 * @param transAmount
	 * @param cccy
	 */
	private void logFinanceTransasction(FinanceMain finMain, LimitHeader header, int disbSeq, String tranType,
			boolean overide, BigDecimal transAmount, BigDecimal limitAmount) {

		LimitTransactionDetail limittrans = new LimitTransactionDetail();
		limittrans.setReferenceCode(LimitConstants.FINANCE);
		limittrans.setTransactionType(tranType);
		limittrans.setOverrideFlag(overide);
		limittrans.setTransactionAmount(transAmount);
		limittrans.setTransactionCurrency(finMain.getFinCcy());
		limittrans.setLimitCurrency(header.getLimitCcy());
		limittrans.setLimitAmount(limitAmount);
		limittrans.setSchSeq(disbSeq);
		limittrans.setReferenceNumber(finMain.getFinReference());
		limittrans.setHeaderId(header.getHeaderId());
		LoggedInUser userDetails = finMain.getUserDetails();
		if (userDetails != null) {
			limittrans.setCreatedBy(userDetails.getLoginUsrID());
			limittrans.setLastMntBy(userDetails.getLoginUsrID());
		}
		limittrans.setTransactionDate(new Timestamp(DateUtility.getAppDate().getTime()));
		limittrans.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		limittrans.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		limitTransactionDetailDAO.save(limittrans);
	}

	/**
	 * @param financeMain
	 * @param customer
	 * @param financeType
	 * @return
	 */
	private HashMap<String, Object> getDataMap(FinanceMain financeMain, FinanceType financeType) {
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		if (financeMain != null) {
			dataMap.putAll(financeMain.getDeclaredFieldValues());
		}
		if (financeType != null) {
			dataMap.putAll(financeType.getDeclaredFieldValues());
		}
		return dataMap;
	}

	/**
	 * @param disbursement
	 * @param approvedDisbursments
	 * @return
	 */
	private boolean isApprovedDisbursments(FinanceDisbursement disbursement,
			List<FinanceDisbursement> approvedDisbursments) {
		if (approvedDisbursments != null && !approvedDisbursments.isEmpty()) {
			for (FinanceDisbursement approvedDisb : approvedDisbursments) {
				if (approvedDisb.getDisbSeq() == disbursement.getDisbSeq()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isApprovedAndCancelled(FinanceDisbursement disbursement,
			List<FinanceDisbursement> approvedDisbursments) {
		if (approvedDisbursments != null && !approvedDisbursments.isEmpty()) {
			for (FinanceDisbursement approvedDisb : approvedDisbursments) {
				if (approvedDisb.getDisbSeq() == disbursement.getDisbSeq()
						&& StringUtils.equals(approvedDisb.getDisbStatus(), FinanceConstants.DISB_STATUS_CANCEL)) {
					return true;
				}
			}
		}
		return false;
	}

	private LimitTransactionDetail getFinTransaction(String finref, long headerId, String transType, int schSeq) {
		return limitTransactionDetailDAO.getTransaction(LimitConstants.FINANCE, finref, transType, headerId, schSeq);
	}

	/**
	 * @param finMain
	 * @param financeType
	 * @param headerId
	 * @return
	 */
	private LimitReferenceMapping identifyLine(FinanceMain finMain, FinanceType financeType, long headerId) {

		String finRef = finMain.getFinReference();
		LimitReferenceMapping mapping = limitReferenceMappingDAO.getLimitReferencemapping(finRef, headerId);
		if (mapping == null) {
			HashMap<String, Object> dataMap = getDataMap(finMain, financeType);
			List<LimitDetails> limitDetailsList = null;
			limitDetailsList = limitDetailDAO.getLimitDetailsByCustID(headerId);

			if (limitDetailsList != null && limitDetailsList.size() > 0) {
				boolean uncalssifed = true;
				for (LimitDetails details : limitDetailsList) {
					boolean ruleResult = (boolean) ruleExecutionUtil.executeRule(details.getSqlRule(), dataMap, "",
							RuleReturnType.BOOLEAN);
					if (ruleResult) {
						mapping = getLimitRefMapping(LimitConstants.FINANCE, finRef, details.getLimitLine(), headerId);
						uncalssifed = false;
						break;
					}
				}

				if (uncalssifed) {
					mapping = getLimitRefMapping(LimitConstants.FINANCE, finRef, LimitConstants.LIMIT_ITEM_UNCLSFD,
							headerId);
				}
			}
		}
		// Return the Limit items
		return mapping;
	}

	/**
	 * *****************************************************************************************************************
	 * ********************************************* Commitment *******************************************************
	 * *****************************************************************************************************************
	 */

	/**
	 * @param commitment
	 * @param overide
	 * @param tranType
	 * @return
	 */
	public ArrayList<ErrorDetails> processCommitmentLimit(Commitment commitment, boolean overide, String tranType) {
		logger.debug(" Entering ");

		ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
		String cmtRef = commitment.getCmtReference();
		long limtiDetailID = commitment.getLimitLineId();
		String cmtCcy = commitment.getCmtCcy();
		//for maintain commitment difference amount will given in the commitment amount 
		BigDecimal transAmount = commitment.getCmtAmount();
		LimitDetails limitDetails = limitDetailDAO.getLimitLineByDetailId(limtiDetailID, "_AView");
		LimitHeader header = limitHeaderDAO.getLimitHeaderById(limitDetails.getLimitHeaderId(), "");
		String limitLine = limitDetails.getLimitLine();

		if (header != null) {
			BigDecimal limitAmount = CalculationUtil.getConvertedAmount(cmtCcy, header.getLimitCcy(), transAmount);
			LimitReferenceMapping mapping = limitReferenceMappingDAO.getLimitReferencemapping(cmtRef,
					header.getHeaderId());
			if (mapping == null) {
				mapping = getLimitRefMapping(LimitConstants.COMMITMENT, commitment.getCmtReference(), limitLine,
						header.getHeaderId());
			}

			List<LimitDetails> list = getCustomerLimitDetails(mapping);
			for (LimitDetails details : list) {
				details.setVersion(details.getVersion() + 1);
				switch (tranType) {
				case LimitConstants.BLOCK:
					details.setReservedLimit(details.getReservedLimit().add(limitAmount));
					break;
				case LimitConstants.UNBLOCK:
					details.setReservedLimit(details.getReservedLimit().subtract(limitAmount));
					break;
				default:
					break;
				}
				limitDetailDAO.updateReserveUtilise(details, "");
			}

			if (mapping.isNewRecord()) {
				limitReferenceMappingDAO.save(mapping);
			}

			LimitTransactionDetail ltd = prepareTransaction(LimitConstants.COMMITMENT, overide, tranType, transAmount,
					cmtCcy, limitAmount, header.getLimitCcy());

			logTransasction(commitment.getCmtReference(), header, ltd, commitment.getUserDetails());
		}

		logger.debug(" Entering ");
		return errorDetails;
	}

	/**
	 * @param refCode
	 * @param overide
	 * @param tranType
	 * @param transAmount
	 * @param transCcy
	 * @param limitAmount
	 * @param limtiCccy
	 * @return
	 */
	private LimitTransactionDetail prepareTransaction(String refCode, boolean overide, String tranType,
			BigDecimal transAmount, String transCcy, BigDecimal limitAmount, String limtiCccy) {
		logger.debug(" Entering ");
		LimitTransactionDetail limittrans = new LimitTransactionDetail();
		limittrans.setReferenceCode(refCode);
		limittrans.setTransactionType(tranType);
		limittrans.setOverrideFlag(overide);
		limittrans.setTransactionAmount(transAmount);
		limittrans.setTransactionCurrency(transCcy);
		limittrans.setLimitCurrency(limtiCccy);
		limittrans.setLimitAmount(limitAmount);
		logger.debug(" Leaving ");
		return limittrans;
	}

	/**
	 * @param financeMain
	 * @return
	 */
	@SuppressWarnings("unused")
	private LimitReferenceMapping getLimitLineBYCommitment(FinanceMain financeMain) {
		String finreference = financeMain.getFinReference();
		Commitment commitment = commitmentDAO.getCommitmentByRef(financeMain.getFinCommitmentRef(), "");
		if (commitment != null) {
			long limtiDetailID = commitment.getLimitLineId();
			LimitDetails limitDetails = limitDetailDAO.getLimitLineByDetailId(limtiDetailID, "_AView");
			if (limitDetails != null) {
				logger.debug(" Leaving ");
				return getLimitRefMapping(LimitConstants.FINANCE, finreference, limitDetails.getLimitLine(),
						limitDetails.getLimitHeaderId());
			}
		}
		return null;
	}

	/**
	 * @param refernce
	 * @param header
	 * @param transDet
	 * @param userDetails
	 */
	private void logTransasction(String refernce, LimitHeader header, LimitTransactionDetail transDet,
			LoggedInUser userDetails) {
		transDet.setReferenceNumber(refernce);
		transDet.setHeaderId(header.getHeaderId());
		if (userDetails != null) {
			transDet.setCreatedBy(userDetails.getLoginUsrID());
			transDet.setLastMntBy(userDetails.getLoginUsrID());
		}

		transDet.setTransactionDate(new Timestamp(System.currentTimeMillis()));
		transDet.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		transDet.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		limitTransactionDetailDAO.save(transDet);
	}
	
	public static BigDecimal getPreviousReservedAmt(List<LimitTransactionDetail> lmtTransDetails) { 
		logger.debug("Entering");
		BigDecimal blockAmount = BigDecimal.ZERO;

		if (lmtTransDetails != null && !lmtTransDetails.isEmpty()) {
			for (LimitTransactionDetail tansDetail : lmtTransDetails) {
				if (StringUtils.equals(tansDetail.getTransactionType(), LimitConstants.BLOCK)) {
					blockAmount = blockAmount.add(tansDetail.getLimitAmount());
				}
				if (StringUtils.equals(tansDetail.getTransactionType(), LimitConstants.APPROVE)) {
					blockAmount = blockAmount.subtract(tansDetail.getLimitAmount());
				}
				if (StringUtils.equals(tansDetail.getTransactionType(), LimitConstants.UNBLOCK)) {
					blockAmount = blockAmount.subtract(tansDetail.getLimitAmount());
				}
			}
		}

		logger.debug("Leaving");
		return blockAmount;
	}

	public void setLimitDetailDAO(LimitDetailDAO limitDetailDAO) {
		this.limitDetailDAO = limitDetailDAO;
	}

	public void setLimitReferenceMappingDAO(LimitReferenceMappingDAO limitReferenceMappingDAO) {
		this.limitReferenceMappingDAO = limitReferenceMappingDAO;
	}

	public void setLimitHeaderDAO(LimitHeaderDAO limitHeaderDAO) {
		this.limitHeaderDAO = limitHeaderDAO;
	}

	public void setLimitTransactionDetailDAO(LimitTransactionDetailsDAO limitTransactionDetailDAO) {
		this.limitTransactionDetailDAO = limitTransactionDetailDAO;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public void setLimitGroupLinesDAO(LimitGroupLinesDAO limitGroupLinesDAO) {
		this.limitGroupLinesDAO = limitGroupLinesDAO;
	}

	public void setCommitmentDAO(CommitmentDAO commitmentDAO) {
		this.commitmentDAO = commitmentDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
}
