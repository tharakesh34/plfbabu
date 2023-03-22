package com.pennant.backend.service.limitservice.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.commitment.CommitmentDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.limit.LimitDetailDAO;
import com.pennant.backend.dao.limit.LimitGroupLinesDAO;
import com.pennant.backend.dao.limit.LimitHeaderDAO;
import com.pennant.backend.dao.limit.LimitReferenceMappingDAO;
import com.pennant.backend.dao.limit.LimitTransactionDetailsDAO;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.eventproperties.EventProperties;
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
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class LimitManagement {
	private static Logger logger = LogManager.getLogger(LimitManagement.class);

	private CommitmentDAO commitmentDAO;
	private LimitDetailDAO limitDetailDAO;
	private LimitHeaderDAO limitHeaderDAO;
	private LimitGroupLinesDAO limitGroupLinesDAO;
	private LimitReferenceMappingDAO limitReferenceMappingDAO;
	private LimitTransactionDetailsDAO limitTransactionDetailDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private FinanceMainDAO financeMainDAO;

	public static final String KEY_LIMITAMT = "LIMITAMT";
	public static final String KEY_LINEEXPIRY = "LINEEXPIRY";

	private static final String ORGINATION = "ORGINATION";
	private static final String SERVICING = "SERVICING";
	private static final String LIMIT_INCREASE = "LIMIT_INCREASE";
	private static final String CANCELATION = "CANCELATION";
	private static final String LOAN_REPAY = "LOAN_REPAY";
	private static final String LOAN_REPAY_CANCEL = "LOAN_REPAY_CANCEL";

	public List<ErrorDetail> processLoanLimitOrgination(FinanceDetail financeDetail, boolean overide, String tranType,
			boolean validateOnly) {
		logger.debug(Literal.ENTERING);
		List<ErrorDetail> errors = new ArrayList<>();

		FinScheduleData finschData = financeDetail.getFinScheduleData();
		Customer customer = financeDetail.getCustomerDetails().getCustomer();
		FinanceMain finMain = finschData.getFinanceMain();
		FinanceType finType = finschData.getFinanceType();
		Date maturityDate = finMain.getMaturityDate();
		Date valueDate = SysParamUtil.getAppDate();

		for (FinanceDisbursement disbursement : finschData.getDisbursementDetails()) {
			if (disbursement.getDisbDate().compareTo(valueDate) > 0) {
				valueDate = disbursement.getDisbDate();
			}
		}

		BigDecimal tranAmt = BigDecimal.ZERO;
		BigDecimal reservTranAmt = BigDecimal.ZERO;
		BigDecimal cmtReserve = null;

		if (LimitConstants.BLOCK.equals(tranType)) {
			tranAmt = finMain.getFinAssetValue();
		} else if (LimitConstants.APPROVE.equals(tranType)) {
			for (FinanceDisbursement disbursement : finschData.getDisbursementDetails()) {
				tranAmt = tranAmt.add(disbursement.getDisbAmount()).add(disbursement.getFeeChargeAmt());
				if (disbursement.getDisbDate().getTime() == finMain.getFinStartDate().getTime()) {
					tranAmt = tranAmt.subtract(finMain.getDownPayment());
				}
			}

			if (StringUtils.isNotEmpty(finMain.getFinCommitmentRef())) {
				Commitment commitment = commitmentDAO.getCommitmentById(finMain.getFinCommitmentRef(), "");
				if (commitment != null) {
					commitment.getCmtAvailable();
				}
			} else {
				reservTranAmt = finMain.getFinAssetValue().subtract(finMain.getFinCurrAssetValue());
			}
		}

		LimitHeader limitHeader = new LimitHeader();
		limitHeader.setLoanMaturityDate(maturityDate);
		limitHeader.setTranAmt(tranAmt);
		limitHeader.setReserveTranAmt(reservTranAmt);
		limitHeader.setDisbSeq(0);
		limitHeader.setLoanMaturityDate(maturityDate);
		limitHeader.setValueDate(valueDate);
		limitHeader.setValueDate(valueDate);
		limitHeader.setTranType(tranType);
		limitHeader.setOverride(overide);
		limitHeader.setValidateOnly(validateOnly);

		errors.addAll(processLimits(limitHeader, customer, finMain, finType, ORGINATION));

		logger.debug(Literal.LEAVING);
		return errors;
	}

	public List<ErrorDetail> processLoanDisbursments(FinanceDetail fd, boolean overide, String tranType,
			boolean validateOnly) {
		logger.debug(Literal.ENTERING);

		List<ErrorDetail> errors = new ArrayList<>();

		FinScheduleData schdData = fd.getFinScheduleData();
		Customer customer = fd.getCustomerDetails().getCustomer();
		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();
		Date maturityDate = fm.getMaturityDate();
		Date valueDate = SysParamUtil.getAppDate();
		BigDecimal tranAmt = BigDecimal.ZERO;

		List<FinanceDisbursement> approvedDisbursments = financeDisbursementDAO
				.getFinanceDisbursementDetails(fm.getFinID(), "", false);
		Date datemaxDate = SysParamUtil.getAppDate();

		int disbSeq = 0;
		for (FinanceDisbursement disbursement : schdData.getDisbursementDetails()) {
			/*
			 * Check the current status is cancel if cancel check the approved status also as cancel
			 */
			if (FinanceConstants.DISB_STATUS_CANCEL.equals(disbursement.getDisbStatus())
					&& isApprovedAndCancelled(disbursement, approvedDisbursments)) {
				continue;
			}

			if (!FinanceConstants.DISB_STATUS_CANCEL.equals(disbursement.getDisbStatus())) {
				if (isApprovedDisbursments(disbursement, approvedDisbursments)) {
					continue;
				}
			}

			if (disbursement.getDisbDate().compareTo(datemaxDate) > 0) {
				datemaxDate = disbursement.getDisbDate();
			}

			tranAmt = tranAmt.add(disbursement.getDisbAmount()).add(disbursement.getFeeChargeAmt());
			disbSeq = disbSeq + 1;
		}

		if (StringUtils.equals(LimitConstants.BLOCK, tranType)) {
			/*
			 * To identify current Disbursement amount we are maintaining the value as -1
			 */
			disbSeq = -1;
		}

		LimitHeader limitHeader = new LimitHeader();
		limitHeader.setLoanMaturityDate(maturityDate);
		limitHeader.setTranAmt(tranAmt);
		limitHeader.setReserveTranAmt(BigDecimal.ZERO);
		limitHeader.setDisbSeq(disbSeq);
		limitHeader.setLoanMaturityDate(maturityDate);
		limitHeader.setValueDate(valueDate);
		limitHeader.setTranType(tranType);
		limitHeader.setOverride(overide);
		limitHeader.setValidateOnly(validateOnly);

		errors.addAll(processLimits(limitHeader, customer, fm, finType, SERVICING));

		logger.debug(Literal.LEAVING);

		return errors;
	}

	public void processLoanCancel(FinanceDetail fd, boolean overide) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		Customer customer = fd.getCustomerDetails().getCustomer();
		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();
		Date maturityDate = fm.getMaturityDate();
		Date valueDate = SysParamUtil.getAppDate();
		BigDecimal tranAmt = BigDecimal.ZERO;

		List<FinanceDisbursement> approvedDisbursments = financeDisbursementDAO
				.getFinanceDisbursementDetails(fm.getFinID(), "", false);

		/* Loop through disbursements */
		for (FinanceDisbursement disbursement : approvedDisbursments) {
			if (StringUtils.trimToEmpty(disbursement.getDisbStatus()).equals(FinanceConstants.DISB_STATUS_CANCEL)) {
				continue;
			}
			tranAmt = tranAmt.add(disbursement.getDisbAmount()).add(disbursement.getFeeChargeAmt());
			if (disbursement.getDisbDate().getTime() == fm.getFinStartDate().getTime()) {
				tranAmt = tranAmt.subtract(fm.getDownPayment());
			}
		}

		/* FinAssetValue is not disbursed completely but loan cancelled */
		BigDecimal reservTranAmt = fm.getFinAssetValue().subtract(fm.getFinCurrAssetValue());

		LimitHeader limitHeader = new LimitHeader();
		limitHeader.setLoanMaturityDate(maturityDate);
		limitHeader.setTranAmt(tranAmt);
		limitHeader.setReserveTranAmt(reservTranAmt);
		limitHeader.setDisbSeq(0);
		limitHeader.setLoanMaturityDate(maturityDate);
		limitHeader.setValueDate(valueDate);
		limitHeader.setTranType(LimitConstants.CANCIL);
		limitHeader.setOverride(overide);
		limitHeader.setValidateOnly(false);

		processLimits(limitHeader, customer, fm, finType, CANCELATION);

		logger.debug(Literal.LEAVING);
	}

	public List<ErrorDetail> processLimitIncrease(FinanceDetail fd, boolean override, boolean validateOnly) {
		logger.debug(Literal.ENTERING);

		List<ErrorDetail> errors = new ArrayList<>();

		FinScheduleData schdData = fd.getFinScheduleData();
		Customer customer = fd.getCustomerDetails().getCustomer();
		FinanceMain finMain = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();
		Date maturityDate = finMain.getMaturityDate();
		Date valueDate = SysParamUtil.getAppDate();
		BigDecimal tranAmt = BigDecimal.ZERO;

		BigDecimal revReserved = financeMainDAO.getFinAssetValue(finMain.getFinID());
		BigDecimal currAssestValue = finMain.getFinAssetValue();
		tranAmt = currAssestValue.subtract(revReserved);

		if (tranAmt.compareTo(BigDecimal.ZERO) <= 0) {
			return errors;
		}

		LimitHeader limitHeader = new LimitHeader();
		limitHeader.setLoanMaturityDate(maturityDate);
		limitHeader.setTranAmt(tranAmt);
		limitHeader.setReserveTranAmt(BigDecimal.ZERO);
		limitHeader.setDisbSeq(0);
		limitHeader.setLoanMaturityDate(maturityDate);
		limitHeader.setValueDate(valueDate);
		limitHeader.setOverride(override);
		limitHeader.setValidateOnly(validateOnly);

		errors.addAll(processLimits(limitHeader, customer, finMain, finType, LIMIT_INCREASE));
		logger.debug(Literal.LEAVING);
		return errors;
	}

	public void processLoanRepay(FinanceMain fm, Customer customer, BigDecimal transAmount) {
		logger.debug(Literal.ENTERING);

		Date maturityDate = fm.getMaturityDate();

		EventProperties eventProperties = fm.getEventProperties();

		Date valueDate = null;
		if (eventProperties.isParameterLoaded()) {
			valueDate = eventProperties.getAppDate();
		} else {
			valueDate = SysParamUtil.getAppDate();
		}

		String tansType = LimitConstants.PRINPAY;
		if (FinanceConstants.PRODUCT_ODFACILITY.equals(fm.getProductCategory())) {
			tansType = LimitConstants.REPAY;
		}

		LimitHeader limitHeader = new LimitHeader();
		limitHeader.setLoanMaturityDate(maturityDate);
		limitHeader.setTranAmt(transAmount);
		limitHeader.setReserveTranAmt(BigDecimal.ZERO);
		limitHeader.setDisbSeq(0);
		limitHeader.setLoanMaturityDate(maturityDate);
		limitHeader.setValueDate(valueDate);
		limitHeader.setTranType(tansType);
		limitHeader.setOverride(false);
		limitHeader.setValidateOnly(false);

		processLimits(limitHeader, customer, fm, null, LOAN_REPAY);
		logger.debug(Literal.LEAVING);
	}

	public void processLoanRepayCancel(FinanceMain finMain, Customer customer, BigDecimal transAmount,
			String prodCategory) {
		logger.debug(Literal.ENTERING);

		Date maturityDate = finMain.getMaturityDate();
		Date valueDate = SysParamUtil.getAppDate();

		String tansType = LimitConstants.PRINPAY;
		if (prodCategory.equals(FinanceConstants.PRODUCT_ODFACILITY)) {
			tansType = LimitConstants.REPAY;
		}

		LimitHeader limitHeader = new LimitHeader();
		limitHeader.setLoanMaturityDate(maturityDate);
		limitHeader.setTranAmt(transAmount);
		limitHeader.setReserveTranAmt(BigDecimal.ZERO);
		limitHeader.setDisbSeq(0);
		limitHeader.setLoanMaturityDate(maturityDate);
		limitHeader.setValueDate(valueDate);
		limitHeader.setTranType(tansType);
		limitHeader.setOverride(false);
		limitHeader.setValidateOnly(false);

		processLimits(limitHeader, customer, finMain, null, LOAN_REPAY_CANCEL);

		logger.debug(Literal.LEAVING);
	}

	private List<ErrorDetail> processLimits(FinanceMain finMain, FinanceType finType, LimitHeader limitHeader,
			String requestFrom) {
		List<ErrorDetail> errorsList = new ArrayList<>();

		long headerId = limitHeader.getHeaderId();
		BigDecimal tranAmt = limitHeader.getTranAmt();
		BigDecimal reservTranAmt = limitHeader.getReserveTranAmt();
		int disbSeq = limitHeader.getDisbSeq();
		String limitCcy = limitHeader.getLimitCcy();
		String finCcy = finMain.getFinCcy();

		/* Check already mapping available or not */
		LimitReferenceMapping mapping = identifyLine(finMain, finType, headerId, requestFrom);

		if (mapping == null) {
			return errorsList;
		}

		BigDecimal blockAmount = BigDecimal.ZERO;
		BigDecimal reservLimitAmt = BigDecimal.ZERO;
		boolean logReservedTrans = false;
		boolean validateOnly = limitHeader.isValidateOnly();
		String tranType = limitHeader.getTranType();

		LimitTransactionDetail limitTranDetail = null;
		if (ORGINATION.equals(requestFrom) || SERVICING.equals(requestFrom)) {
			if (!validateOnly && mapping.isNewRecord()) {
				limitReferenceMappingDAO.save(mapping);
			}

			/* In origination there should be one block */
			limitTranDetail = getFinTransaction(finMain.getFinReference(), headerId, LimitConstants.BLOCK, disbSeq);

			if (limitTranDetail != null) {
				blockAmount = limitTranDetail.getLimitAmount();
			} else if (StringUtils.isNotBlank(finMain.getFinCommitmentRef())) {
				blockAmount = tranAmt;
			}

			if (LimitConstants.APPROVE.equals(tranType)) {
				/* Max. Disbursement Check available */
				if (reservTranAmt.compareTo(BigDecimal.ZERO) > 0) {
					reservLimitAmt = CalculationUtil.getConvertedAmount(finCcy, limitCcy, reservTranAmt);
					if (limitTranDetail == null) {
						logReservedTrans = true;
					}
				}
			} else if (LimitConstants.UNBLOCK.equals(tranType)) {
				/* In unblock then we should reverse the reserved amount */
				tranAmt = blockAmount;
			}
		} else if (CANCELATION.equals(requestFrom)) {
			reservLimitAmt = CalculationUtil.getConvertedAmount(finCcy, limitHeader.getLimitCcy(), reservTranAmt);
		}

		BigDecimal limitAmount = CalculationUtil.getConvertedAmount(finCcy, limitCcy, tranAmt);

		limitHeader.setLimitAmount(limitAmount);
		limitHeader.setReserveLimitAmt(reservLimitAmt);
		limitHeader.setBlockAmount(blockAmount);
		limitHeader.setTranAmt(tranAmt);

		switch (requestFrom) {
		case ORGINATION:
			errorsList = updateLimitOrgination(mapping, limitHeader, limitTranDetail);
			break;
		case SERVICING:
			errorsList = updateLimitServicing(mapping, limitHeader);
			break;
		case LIMIT_INCREASE:
			updateLimitIncrease(mapping, limitHeader);
			limitHeader.setTranType(LimitConstants.BLOCK);
			logReservedTrans = true;
			break;
		case CANCELATION:
			updateLimitCancelation(mapping, limitHeader);
			limitHeader.setTranType(LimitConstants.CANCIL);
			break;
		case LOAN_REPAY:
			updateLimitLoanRepay(mapping, limitHeader, finMain);
			break;
		case LOAN_REPAY_CANCEL:
			updateLimitLoanRepayCancel(mapping, limitHeader);
			break;
		default:
			break;
		}

		if (!errorsList.isEmpty() || !mapping.isProceeed() || validateOnly) {
			return errorsList;
		}

		/*
		 * Log transactions Max. Disbursement Check available but reserved limit not configured in process editor
		 */
		if (logReservedTrans) {
			limitHeader.setTranType(LimitConstants.BLOCK);
			limitHeader.setTranAmt(reservTranAmt);
			limitHeader.setLimitAmount(limitAmount);

			logFinanceTransasction(finMain, limitHeader);
		}

		logFinanceTransasction(finMain, limitHeader);

		return errorsList;
	}

	private void updateLimitCancelation(LimitReferenceMapping mapping, LimitHeader limitHeader) {
		/* Get limit details by line and group associated with it */
		List<LimitDetails> limitDetails = getCustomerLimitDetails(mapping);
		String limitLine = mapping.getLimitLine();

		BigDecimal reserveLimitAmt = limitHeader.getReserveLimitAmt();
		BigDecimal limitAmount = limitHeader.getLimitAmount();

		for (LimitDetails details : limitDetails) {
			boolean revolving = isRevolving(limitLine, details, limitDetails);

			details.setVersion(details.getVersion() + 1);
			/* Max. Disbursement Check available */
			if (reserveLimitAmt.compareTo(BigDecimal.ZERO) > 0) {
				details.setReservedLimit(details.getReservedLimit().subtract(reserveLimitAmt));
			}

			/* Check revolving or non revolving */
			if (revolving) {
				details.setUtilisedLimit(details.getUtilisedLimit().subtract(limitAmount));
			} else {
				details.setLimitSanctioned(details.getLimitSanctioned().add(limitAmount));
				details.setNonRvlUtilised(details.getNonRvlUtilised().subtract(limitAmount));
			}

			limitDetailDAO.updateReserveUtilise(details, "");
		}
	}

	private void updateLimitIncrease(LimitReferenceMapping mapping, LimitHeader limitHeader) {
		List<LimitDetails> limitDetails = getCustomerLimitDetails(mapping);
		for (LimitDetails details : limitDetails) {
			details.setVersion(details.getVersion() + 1);
			details.setReservedLimit(details.getReservedLimit().add(limitHeader.getTranAmt()));
			limitDetailDAO.updateReserveUtilise(details, "");
		}
	}

	private List<ErrorDetail> processLimits(LimitHeader limitHeader, Customer customer, FinanceMain finMain,
			FinanceType finType, String limitFrom) {
		logger.debug(Literal.ENTERING);
		List<ErrorDetail> errors = new ArrayList<>();

		long custId = customer.getCustID();
		long groupId = customer.getCustGroupID();

		String usrlang = PennantConstants.default_Language;

		if (finMain.getUserDetails() == null) {
			finMain.setUserDetails(new LoggedInUser());
			usrlang = finMain.getUserDetails().getLanguage();
		}

		LimitHeader custHeader = null;
		LimitHeader groupHeader = null;

		if (custId != 0) {
			custHeader = limitHeaderDAO.getLimitHeaderByCustomerId(custId, "_AView");
		}

		if (groupId != 0) {
			groupHeader = limitHeaderDAO.getLimitHeaderByCustomerGroupCode(groupId, "_AView");
		}

		boolean limitRequired = false;
		if (finType != null) {
			limitHeader.setAllowOverride(finType.isOverrideLimit());
			limitRequired = finType.isLimitRequired();
		}

		if (ORGINATION.equals(limitFrom) || SERVICING.equals(limitFrom)) {
			/* If limit required is true in Finance type */
			if (limitRequired && custHeader == null) {
				errors.add(new ErrorDetail("60310", null));
				return ErrorUtil.getErrorDetails(errors, usrlang);
			}

			if (custHeader != null && !custHeader.isActive()) {
				StringBuilder key = new StringBuilder(custHeader.getCustCIF());
				key.append("-");
				key.append(custHeader.getLimitStructureCode());
				errors.add(new ErrorDetail("60316", new String[] { key.toString() }));
				return ErrorUtil.getErrorDetails(errors, usrlang);
			}

			if (StringUtils.trimToEmpty(finMain.getFinSourceID()).equals(PennantConstants.FINSOURCE_ID_API)) {
				limitHeader.setAllowOverride(false);
			}
		}

		if (custHeader != null) {
			/* Customer limit process */
			custHeader.setTranAmt(limitHeader.getTranAmt());
			custHeader.setReserveTranAmt(limitHeader.getReserveTranAmt());
			custHeader.setDisbSeq(limitHeader.getDisbSeq());
			custHeader.setOverride(limitHeader.isOverride());
			custHeader.setAllowOverride(limitHeader.isAllowOverride());
			custHeader.setLoanMaturityDate(limitHeader.getLoanMaturityDate());
			custHeader.setValueDate(limitHeader.getValueDate());
			custHeader.setValidateOnly(limitHeader.isValidateOnly());
			custHeader.setTranType(limitHeader.getTranType());

			errors.addAll(processLimits(finMain, finType, custHeader, limitFrom));
		}

		if (groupHeader != null) {
			/* Customer group limit process */
			groupHeader.setTranAmt(limitHeader.getTranAmt());
			groupHeader.setReserveTranAmt(limitHeader.getReserveTranAmt());
			groupHeader.setDisbSeq(limitHeader.getDisbSeq());
			groupHeader.setOverride(limitHeader.isOverride());
			groupHeader.setAllowOverride(limitHeader.isAllowOverride());
			groupHeader.setLoanMaturityDate(limitHeader.getLoanMaturityDate());
			groupHeader.setValueDate(limitHeader.getValueDate());

			groupHeader.setValidateOnly(limitHeader.isValidateOnly());
			groupHeader.setTranType(limitHeader.getTranType());

			errors.addAll(processLimits(finMain, finType, groupHeader, limitFrom));

		}

		logger.debug(Literal.LEAVING);
		return errors;

	}

	private List<ErrorDetail> updateLimitOrgination(LimitReferenceMapping mapping, LimitHeader limitHeader,
			LimitTransactionDetail limitTranDetail) {
		logger.info(Literal.ENTERING);
		List<ErrorDetail> errors = new ArrayList<>();

		List<LimitDetails> limitDetails = getCustomerLimitDetails(mapping);
		String limitLine = mapping.getLimitLine();

		boolean override = limitHeader.isOverride();
		String tranType = limitHeader.getTranType();

		BigDecimal limitAmount = limitHeader.getLimitAmount();
		BigDecimal reservLimitAmt = limitHeader.getReserveLimitAmt();
		BigDecimal blockAmount = limitHeader.getBlockAmount();

		if (LimitConstants.BLOCK.equals(tranType)) {
			if (limitTranDetail != null) {
				boolean block = removePreviuosBlockIfAny(limitTranDetail.getLimitAmount(), limitAmount, limitDetails,
						limitTranDetail.getId());
				if (!block) {
					mapping.setProceeed(false);
					return errors;
				}
			}

			if (!override) {
				errors.addAll(validate(limitHeader, limitDetails));
				if (!errors.isEmpty()) {
					return errors;
				}
			}

		} else if (LimitConstants.APPROVE.equals(tranType) && !override) {
			BigDecimal amoutToValidate;
			if (reservLimitAmt.compareTo(BigDecimal.ZERO) > 0) {
				amoutToValidate = blockAmount.subtract(reservLimitAmt);
			} else {
				amoutToValidate = blockAmount;
			}

			limitHeader.setPrevLimitAmt(amoutToValidate);

			errors.addAll(validate(limitHeader, limitDetails));

			if (!errors.isEmpty()) {
				return errors;
			}
		}

		if (limitHeader.isValidateOnly() || limitDetails == null) {
			return errors;
		}

		/* Update limit details */
		switch (tranType) {
		case LimitConstants.BLOCK:
			for (LimitDetails details : limitDetails) {
				details.setVersion(details.getVersion() + 1);
				details.setReservedLimit(details.getReservedLimit().add(limitAmount));
				limitDetailDAO.updateReserveUtilise(details, "");
			}
			break;

		/* Loan rejected */
		case LimitConstants.UNBLOCK:
			if (limitTranDetail != null) {
				for (LimitDetails details : limitDetails) {
					details.setVersion(details.getVersion() + 1);

					/* Reverse the utilization in case of loan reject */
					details.setReservedLimit(details.getReservedLimit().subtract(blockAmount));
					details.setOsPriBal(details.getOsPriBal().subtract(limitAmount));
					limitDetailDAO.updateReserveUtilise(details, "");
				}
			}
			break;
		case LimitConstants.APPROVE:
			for (LimitDetails details : limitDetails) {
				boolean revolving = isRevolving(limitLine, details, limitDetails);

				details.setVersion(details.getVersion() + 1);

				/* Previous block amount */
				if (blockAmount.compareTo(BigDecimal.ZERO) > 0) {
					details.setReservedLimit(details.getReservedLimit().subtract(blockAmount));
				}

				/* Max. Disbursement Check available */
				if (reservLimitAmt.compareTo(BigDecimal.ZERO) > 0) {
					details.setReservedLimit(details.getReservedLimit().add(reservLimitAmt));
				}

				/* Check revolving or non revolving */
				if (revolving) {
					details.setUtilisedLimit(details.getUtilisedLimit().add(limitAmount));
				} else {
					details.setLimitSanctioned(details.getLimitSanctioned().subtract(limitAmount));
					details.setUtilisedLimit(details.getUtilisedLimit().add(limitAmount));
				}

				details.setOsPriBal(details.getOsPriBal().add(limitAmount));
				limitDetailDAO.updateReserveUtilise(details, "");
			}
			break;

		default:
			break;
		}

		logger.info(Literal.LEAVING);
		return errors;
	}

	private List<ErrorDetail> updateLimitServicing(LimitReferenceMapping mapping, LimitHeader limitHeader) {
		logger.debug(Literal.ENTERING);
		List<ErrorDetail> errors = new ArrayList<>();

		String finref = mapping.getReferenceNumber();
		long headerId = mapping.getHeaderId();
		LimitTransactionDetail prvblock = getFinTransaction(finref, headerId, LimitConstants.BLOCK, -1);

		/* Get limit details by line and group associated with it */
		List<LimitDetails> limitDetails = getCustomerLimitDetails(mapping);
		String limitLine = mapping.getLimitLine();

		BigDecimal prvReserv = getReserveLimit(finref, headerId);

		String tranType = limitHeader.getTranType();
		boolean override = limitHeader.isOverride();
		BigDecimal limitAmount = limitHeader.getLimitAmount();

		if (StringUtils.equals(LimitConstants.BLOCK, tranType)) {
			if (prvblock != null) {
				boolean block = removePreviuosBlockIfAny(prvblock.getLimitAmount(), limitAmount, limitDetails,
						prvblock.getId());
				if (!block) {
					mapping.setProceeed(false);
					return errors;
				}
			}
			// validate

			if (!override) {
				errors.addAll(validate(limitHeader, limitDetails));
				if (!errors.isEmpty()) {
					return errors;
				}
			}

		} else if (LimitConstants.APPROVE.equals(tranType) && !override) {
			BigDecimal amoutToValidate = BigDecimal.ZERO;

			if (prvReserv.compareTo(BigDecimal.ZERO) > 0) {
				amoutToValidate = prvReserv;
			} else {

				/*
				 * Loan origination without reserved limit and loan servicing with reserved limit
				 */
				if (prvblock != null && prvblock.getLimitAmount().compareTo(BigDecimal.ZERO) > 0) {
					amoutToValidate = prvblock.getLimitAmount();
				}
			}
			limitHeader.setPrevLimitAmt(amoutToValidate);
			errors.addAll(validate(limitHeader, limitDetails));
			if (!errors.isEmpty()) {
				return errors;
			}
		}

		/* If only validate then do not update */
		if (limitHeader.isValidateOnly() || limitDetails == null) {
			return errors;
		}

		switch (tranType) {
		/* Block will used in case of normal loan */
		case LimitConstants.BLOCK:
			for (LimitDetails details : limitDetails) {
				details.setVersion(details.getVersion() + 1);
				details.setReservedLimit(details.getReservedLimit().add(limitAmount));
				limitDetailDAO.updateReserveUtilise(details, "");
			}
			break;
		/* Cancel disbursement */
		case LimitConstants.UNBLOCK:
			for (LimitDetails details : limitDetails) {
				/* Check revolving or non revolving */
				boolean revolving = isRevolving(limitLine, details, limitDetails);

				details.setVersion(details.getVersion() + 1);
				details.setReservedLimit(details.getReservedLimit().add(limitAmount));

				if (revolving) {
					details.setUtilisedLimit(details.getUtilisedLimit().subtract(limitAmount));
				} else {
					details.setLimitSanctioned(details.getLimitSanctioned().add(limitAmount));
					details.setNonRvlUtilised(details.getNonRvlUtilised().subtract(limitAmount));
				}

				details.setOsPriBal(details.getOsPriBal().subtract(limitAmount));
				limitDetailDAO.updateReserveUtilise(details, "");
			}
			break;
		/* Add disbursement rejected */
		case LimitConstants.CANCIL:

			if (prvblock != null) {
				for (LimitDetails details : limitDetails) {
					details.setVersion(details.getVersion() + 1);
					/* Reverse the reserve in case of loan reject */
					details.setReservedLimit(details.getReservedLimit().subtract(prvblock.getLimitAmount()));
					limitDetailDAO.updateReserveUtilise(details, "");
				}
				limitTransactionDetailDAO.updateSeq(prvblock.getTransactionId(), 0);
			} else {
				/*
				 * If there is no block now then nothing to cancel. Log not required
				 */
				mapping.setProceeed(false);
			}
			break;

		case LimitConstants.APPROVE:
			/* Get previous reserve amount */

			for (LimitDetails details : limitDetails) {
				/* Check revolving or non revolving */
				boolean revolving = isRevolving(limitLine, details, limitDetails);

				details.setVersion(details.getVersion() + 1);

				/*
				 * Loan origination without reserved limit and loan servicing (add disbursement) with reserved limit,
				 * then prvReserv is negative
				 */
				BigDecimal prvblockAmt = prvblock != null ? prvblock.getLimitAmount() : BigDecimal.ZERO;

				if (prvReserv.compareTo(BigDecimal.ZERO) > 0 || prvblockAmt.compareTo(BigDecimal.ZERO) > 0) {
					details.setReservedLimit(details.getReservedLimit().subtract(limitAmount));
				}

				if (revolving) {
					details.setUtilisedLimit(details.getUtilisedLimit().add(limitAmount));
				} else {
					details.setLimitSanctioned(details.getLimitSanctioned().subtract(limitAmount));
					details.setNonRvlUtilised(details.getNonRvlUtilised().add(limitAmount));
				}

				details.setOsPriBal(details.getOsPriBal().subtract(limitAmount));
				limitDetailDAO.updateReserveUtilise(details, "");
			}

			if (prvblock != null) {
				limitTransactionDetailDAO.updateSeq(prvblock.getTransactionId(), 0);
			}

			break;

		default:
			break;
		}

		return errors;
	}

	private BigDecimal getReserveLimit(String finref, long headerId) {
		LimitTransactionDetail blockAmt = limitTransactionDetailDAO.geLoantAvaliableReserve(finref,
				LimitConstants.BLOCK, headerId);
		LimitTransactionDetail approvedAmt = limitTransactionDetailDAO.geLoantAvaliableReserve(finref,
				LimitConstants.APPROVE, headerId);
		LimitTransactionDetail unblockkAmt = limitTransactionDetailDAO.geLoantAvaliableReserve(finref,
				LimitConstants.UNBLOCK, headerId);

		return blockAmt.getLimitAmount().subtract(approvedAmt.getLimitAmount().subtract(unblockkAmt.getLimitAmount()));
	}

	private void updateLimitLoanRepay(LimitReferenceMapping mapping, LimitHeader limitHeader, FinanceMain finMain) {
		/* Get limit details by line and group associated with it */
		List<LimitDetails> custLimitDetails = getCustomerLimitDetails(mapping);
		String limitLine = mapping.getLimitLine();

		processRepay(limitLine, limitHeader, custLimitDetails);

		/*
		 * FinAssetValue is not disbursed completely but loan matured / inactive
		 */
		if (!finMain.isFinIsActive()) {
			processEarlyRepay(finMain, limitHeader.getLimitCcy(), custLimitDetails);
		}
	}

	private void updateLimitLoanRepayCancel(LimitReferenceMapping mapping, LimitHeader limitHeader) {
		// get limit details by line and group associated with it
		List<LimitDetails> custLimitDetails = getCustomerLimitDetails(mapping);
		String limitLine = mapping.getLimitLine();

		processRepayCancel(limitLine, limitHeader, custLimitDetails);

	}

	private void processRepay(String limitLine, LimitHeader limitHeader, List<LimitDetails> limitDetails) {
		logger.debug(Literal.ENTERING);

		BigDecimal limitAmount = limitHeader.getLimitAmount();
		String tansType = limitHeader.getTranType();

		for (LimitDetails details : limitDetails) {
			boolean revolving = isRevolving(limitLine, details, limitDetails);

			details.setVersion(details.getVersion() + 1);

			if (LimitConstants.PRINPAY.equals(tansType) || LimitConstants.REPAY.equals(tansType)) {

				if (LimitConstants.REPAY.equals(tansType)) {
					/* Check need add it to reserved or not */
					details.setReservedLimit(details.getReservedLimit().add(limitAmount));
				}

				/* Check revolving or non revolving */
				if (revolving) {
					details.setUtilisedLimit(details.getUtilisedLimit().subtract(limitAmount));
				} else {
					details.setNonRvlUtilised(details.getNonRvlUtilised().subtract(limitAmount));
				}

				details.setOsPriBal(details.getOsPriBal().subtract(limitAmount));
				limitDetailDAO.updateReserveUtilise(details, "");
			}

		}

		logger.debug(Literal.LEAVING);
	}

	/*
	 * EarlySattelement / Schedule last Re payment Loan got Matured / InActive
	 * 
	 */
	private void processEarlyRepay(FinanceMain finMain, String limitCcy, List<LimitDetails> custLimitDetails) {
		logger.debug(Literal.ENTERING);

		BigDecimal reservTranAmt = finMain.getFinAssetValue().subtract(finMain.getFinCurrAssetValue());
		BigDecimal reservLimitAmt = CalculationUtil.getConvertedAmount(finMain.getFinCcy(), limitCcy, reservTranAmt);

		/* Update Reversed limit */
		if (reservLimitAmt.compareTo(BigDecimal.ZERO) > 0) {
			for (LimitDetails lmtDetail : custLimitDetails) {
				lmtDetail.setReservedLimit(lmtDetail.getReservedLimit().subtract(reservLimitAmt));
				limitDetailDAO.updateReserveUtilise(lmtDetail, "");
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void processRepayCancel(String limitLine, LimitHeader limitHeader, List<LimitDetails> limitDetails) {
		logger.debug(Literal.ENTERING);

		BigDecimal limitAmount = limitHeader.getLimitAmount();
		String tansType = limitHeader.getTranType();

		for (LimitDetails details : limitDetails) {

			boolean revolving = isRevolving(limitLine, details, limitDetails);

			details.setVersion(details.getVersion() + 1);

			if (LimitConstants.REPAY.equals(tansType) || LimitConstants.PRINPAY.equals(tansType)) {
				if (LimitConstants.REPAY.equals(tansType)) {
					/* Check need add it to reserved or not */
					details.setReservedLimit(details.getReservedLimit().subtract(limitAmount));

				}

				/* Check revolving or non revolving */
				if (revolving) {
					details.setUtilisedLimit(details.getUtilisedLimit().add(limitAmount));
				} else {
					details.setNonRvlUtilised(details.getNonRvlUtilised().add(limitAmount));
				}

				limitDetailDAO.updateReserveUtilise(details, "");
			}

		}
		logger.debug(Literal.LEAVING);
	}

	private boolean removePreviuosBlockIfAny(BigDecimal prvBlockAmount, BigDecimal limitAmount,
			List<LimitDetails> custLimitDetails, long tranId) {
		boolean block = false;
		if (prvBlockAmount.compareTo(BigDecimal.ZERO) != 0 && prvBlockAmount.compareTo(limitAmount) != 0) {
			/* Unblock previous */
			for (LimitDetails details : custLimitDetails) {
				details.setReservedLimit(details.getReservedLimit().subtract(prvBlockAmount));
				limitDetailDAO.updateReserveUtilise(details, "");
			}
			/* Delete the transaction */
			limitTransactionDetailDAO.delete(tranId);
			block = true;
		}
		return block;
	}

	private List<ErrorDetail> validate(LimitHeader limitHeader, List<LimitDetails> limitDetails) {
		List<ErrorDetail> errorDetails = new ArrayList<>();

		for (LimitDetails detail : limitDetails) {
			ErrorDetail error = validateLimitDetail(limitHeader, detail);
			if (error != null) {
				errorDetails.add(error);
			}
		}

		return errorDetails;
	}

	private ErrorDetail validateLimitDetail(LimitHeader limitHeader, LimitDetails limitDetail) {
		if (limitDetail == null) {
			return null;
		}

		String param = limitDetail.getLimitLine();
		if (param == null) {
			param = limitDetail.getGroupCode();
		}

		Date expiryDate = limitDetail.getExpiryDate();
		boolean validateMaturityDate = limitDetail.isValidateMaturityDate();
		BigDecimal limitAmount = BigDecimal.ZERO;

		Date valueDate = limitHeader.getValueDate();
		Date loanMaturityDate = limitHeader.getLoanMaturityDate();
		BigDecimal tranAmount = limitHeader.getTranAmt();
		boolean overrideAllowed = limitHeader.isAllowOverride();
		BigDecimal prevLimitAmt = limitHeader.getPrevLimitAmt();

		if (expiryDate != null && expiryDate.compareTo(valueDate) < 0) {
			return ErrorUtil.getErrorDetail(new ErrorDetail(KEY_LINEEXPIRY, "60311", new String[] { param }, null));
		}

		if (expiryDate != null && validateMaturityDate) {
			DateUtil.compare(expiryDate, expiryDate);

			if (expiryDate.compareTo(loanMaturityDate) < 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtil.formatToLongDate(expiryDate);
				valueParm[1] = DateUtil.formatToLongDate(loanMaturityDate);
				return ErrorUtil.getErrorDetail(new ErrorDetail("60317", valueParm));
			}
		}

		if (LimitConstants.LIMIT_CHECK_RESERVED.equals(limitDetail.getLimitChkMethod())) {
			limitAmount = limitDetail.getReservedexposure().add(tranAmount).subtract(prevLimitAmt);
		} else if (LimitConstants.LIMIT_CHECK_ACTUAL.equals(limitDetail.getLimitChkMethod())) {
			limitAmount = limitDetail.getActualexposure().add(tranAmount);
		}

		if (limitDetail.isLimitCheck() && limitDetail.getLimitSanctioned().compareTo(limitAmount) == -1) {
			if (overrideAllowed) {
				return ErrorUtil.getErrorDetail(new ErrorDetail(KEY_LIMITAMT, "60312", new String[] { param }, null));
			} else {
				return ErrorUtil.getErrorDetail(new ErrorDetail(KEY_LIMITAMT, "60314", new String[] { param }, null));
			}
		}

		return null;
	}

	private List<LimitDetails> getCustomerLimitDetails(LimitReferenceMapping mapping) {
		long headerId = mapping.getHeaderId();
		String limitLine = mapping.getLimitLine();
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

		return limitDetailDAO.getLimitByLineAndgroup(headerId, limitLine, groupCodes);
	}

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

	private void logFinanceTransasction(FinanceMain finMain, LimitHeader header) {
		LimitTransactionDetail limittrans = new LimitTransactionDetail();

		limittrans.setReferenceCode(LimitConstants.FINANCE);
		limittrans.setTransactionType(header.getTranType());
		limittrans.setOverrideFlag(header.isOverride());
		limittrans.setTransactionAmount(header.getTranAmt());
		limittrans.setTransactionCurrency(finMain.getFinCcy());
		limittrans.setLimitCurrency(header.getLimitCcy());
		limittrans.setLimitAmount(header.getLimitAmount());
		limittrans.setSchSeq(header.getDisbSeq());
		limittrans.setReferenceNumber(finMain.getFinReference());
		limittrans.setHeaderId(header.getHeaderId());
		LoggedInUser userDetails = finMain.getUserDetails();

		if (userDetails != null) {
			limittrans.setCreatedBy(userDetails.getUserId());
			limittrans.setLastMntBy(userDetails.getUserId());
		}

		EventProperties eventProperties = finMain.getEventProperties();
		if (eventProperties.isParameterLoaded()) {
			limittrans.setTransactionDate(new Timestamp(eventProperties.getAppDate().getTime()));
		} else {
			limittrans.setTransactionDate(new Timestamp(SysParamUtil.getAppDate().getTime()));
		}

		limittrans.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		limittrans.setLastMntOn(limittrans.getCreatedOn());

		if (limittrans.getLimitAmount() != null && limittrans.getLimitAmount().compareTo(BigDecimal.ZERO) > 0) {
			limitTransactionDetailDAO.save(limittrans);
		}
	}

	/**
	 * @param financeMain
	 * @param customer
	 * @param financeType
	 * @return
	 */
	private Map<String, Object> getDataMap(FinanceMain financeMain, FinanceType financeType) {
		Map<String, Object> dataMap = new HashMap<>();
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

	private LimitReferenceMapping identifyLine(FinanceMain finMain, FinanceType financeType, long headerId,
			String requestFrom) {
		String finRef = finMain.getFinReference();
		LimitReferenceMapping mapping = limitReferenceMappingDAO.getLimitReferencemapping(finRef, headerId);

		if (mapping == null && ORGINATION.equals(requestFrom)) {
			Map<String, Object> dataMap = getDataMap(finMain, financeType);
			List<LimitDetails> limitDetailsList = null;
			limitDetailsList = limitDetailDAO.getLimitDetailsByCustID(headerId);

			if (limitDetailsList != null && !limitDetailsList.isEmpty()) {
				boolean uncalssifed = true;
				for (LimitDetails details : limitDetailsList) {
					boolean ruleResult = (boolean) RuleExecutionUtil.executeRule(details.getSqlRule(), dataMap, "",
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
	public ArrayList<ErrorDetail> processCommitmentLimit(Commitment commitment, boolean overide, String tranType) {
		logger.debug(" Entering ");

		ArrayList<ErrorDetail> errorDetails = new ArrayList<>();
		String cmtRef = commitment.getCmtReference();
		long limtiDetailID = commitment.getLimitLineId();
		String cmtCcy = commitment.getCmtCcy();
		// for maintain commitment difference amount will given in the
		// commitment amount
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

			// get limit details by line and group associated with it
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
			transDet.setCreatedBy(userDetails.getUserId());
			transDet.setLastMntBy(userDetails.getUserId());
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

	public void processLoanRepay(FinanceMain finMain, Customer customer, BigDecimal transAmount, String prodCategory) {
		logger.debug(Literal.ENTERING);

		Date maturityDate = finMain.getMaturityDate();

		EventProperties eventProperties = finMain.getEventProperties();

		Date valueDate = null;
		if (eventProperties.isParameterLoaded()) {
			valueDate = eventProperties.getAppDate();
		} else {
			valueDate = SysParamUtil.getAppDate();
		}

		String tansType = LimitConstants.PRINPAY;
		if (prodCategory.equals(FinanceConstants.PRODUCT_ODFACILITY)) {
			tansType = LimitConstants.REPAY;
		}

		LimitHeader limitHeader = new LimitHeader();
		limitHeader.setLoanMaturityDate(maturityDate);
		limitHeader.setTranAmt(transAmount);
		limitHeader.setReserveTranAmt(BigDecimal.ZERO);
		limitHeader.setDisbSeq(0);
		limitHeader.setLoanMaturityDate(maturityDate);
		limitHeader.setValueDate(valueDate);
		limitHeader.setTranType(tansType);
		limitHeader.setOverride(false);
		limitHeader.setValidateOnly(false);

		processLimits(limitHeader, customer, finMain, null, LOAN_REPAY);
		logger.debug(Literal.LEAVING);
	}

	public static boolean isRevolving(String limitLine, LimitDetails limitDetail, List<LimitDetails> limitDetails) {
		if (StringUtils.equals(limitDetail.getLimitLine(), limitLine) && limitDetail.isRevolving()) {
			return true;
		}

		for (LimitDetails item : limitDetails) {
			if (StringUtils.equals(item.getLimitLine(), limitLine)) {
				return limitDetail.isRevolving();
			}
		}

		return false;
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
