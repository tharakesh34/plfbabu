package com.pennant.backend.service.limitservice.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
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
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.limit.LimitDetails;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.limit.LimitReferenceMapping;
import com.pennant.backend.model.limit.LimitTransactionDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleReturnType;

public class LimitManagement {

	private static Logger				logger	= Logger.getLogger(LimitManagement.class);
	private CommitmentDAO				commitmentDAO;
	private LimitDetailDAO				limitDetailDAO;
	private LimitHeaderDAO				limitHeaderDAO;
	private RuleExecutionUtil			ruleExecutionUtil;
	private LimitGroupLinesDAO			limitGroupLinesDAO;
	private LimitReferenceMappingDAO	limitReferenceMappingDAO;
	private LimitTransactionDetailsDAO	limitTransactionDetailDAO;
	private FinanceDisbursementDAO		financeDisbursementDAO;

	/**
	 * @param financeDetail
	 * @param overide
	 * @param tranType
	 * @return
	 */
	public List<ErrorDetails> processLoanLimit(FinanceDetail financeDetail, boolean overide, String tranType) {
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
		List<FinanceDisbursement> approvedDisbursments = financeDisbursementDAO.getFinanceDisbursementDetails(
				finMain.getFinReference(), "", false);
		//loop through disbursements
		for (FinanceDisbursement disbursement : finschData.getDisbursementDetails()) {

			if (LimitConstants.LIMIT_TYPE_BLOCK.equals(tranType) || LimitConstants.LIMIT_TYPE_APPROVE.equals(tranType)) {
				if (isApprovedDisbursments(disbursement, approvedDisbursments)) {
					continue;
				}
			}

			Date disbDate = disbursement.getDisbDate();
			int disbSeq = disbursement.getDisbSeq();

			BigDecimal transAmount = disbursement.getDisbAmount().add(disbursement.getFeeChargeAmt());

			if (disbDate.getTime() == finMain.getFinStartDate().getTime()) {
				transAmount = transAmount.subtract(finMain.getDownPayment());
			}

			//Customer limit process
			if (custHeader != null) {
				// check already mapping available or not 
				LimitReferenceMapping mapping = identifyLine(finMain, finType, custHeader.getHeaderId());

				if (mapping != null) {

					if (mapping.isNewRecord()) {
						limitReferenceMappingDAO.save(mapping);
					}

					BigDecimal limitAmount = CalculationUtil.getConvertedAmount(finCcy, custHeader.getLimitCcy(),
							transAmount);

					errors.addAll(processLimits(mapping, tranType, allowOverride, limitAmount, disbursement, overide));

					if (!errors.isEmpty()) {
						return ErrorUtil.getErrorDetails(errors, usrlang);
					}

					if (!mapping.isProceeed()) {
						continue;
					}

					//log transaction
					logFinanceTransasction(finMain, custHeader, disbSeq, tranType, overide, transAmount, limitAmount);

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

					BigDecimal limitAmount = CalculationUtil.getConvertedAmount(finCcy, groupHeader.getLimitCcy(),
							transAmount);

					errors.addAll(processLimits(mapping, tranType, allowOverride, limitAmount, disbursement, overide));

					if (!errors.isEmpty()) {
						return ErrorUtil.getErrorDetails(errors, usrlang);
					}

					if (!mapping.isProceeed()) {
						return errors;
					}

					//log transaction
					logFinanceTransasction(finMain, groupHeader, disbSeq, tranType, overide, transAmount, limitAmount);
				}
			}

		}

		logger.debug(" Entering ");
		return errors;
	}

	/**
	 * @param financeDetail
	 * @param overide
	 * @param tranType
	 * @return
	 */
	public List<ErrorDetails> processLoanRepay(RepayData repayData, boolean overide, String tranType) {
		logger.debug(" Entering ");

		ArrayList<ErrorDetails> errors = new ArrayList<ErrorDetails>();

		FinanceDetail findetails = repayData.getFinanceDetail();

		FinanceMain finMain = findetails.getFinScheduleData().getFinanceMain();
		String finCcy = finMain.getFinCcy();
		Customer customer = findetails.getCustomerDetails().getCustomer();
		long custId = customer.getCustID();
		long groupId = customer.getCustGroupID();
		LimitHeader custHeader = null;
		LimitHeader groupHeader = null;

		if (custId != 0) {
			custHeader = limitHeaderDAO.getLimitHeaderByCustomerId(custId, "");
		}

		if (groupId != 0) {
			groupHeader = limitHeaderDAO.getLimitHeaderByCustomerGroupCode(groupId, "");
		}

		BigDecimal transAmount = repayData.getFinRepayHeader().getPriAmount();

		//Customer limit process
		if (custHeader != null) {
			// check already mapping available or not 
			LimitReferenceMapping mapping = limitReferenceMappingDAO.getLimitReferencemapping(
					finMain.getFinReference(), custHeader.getHeaderId());

			if (mapping != null) {

				BigDecimal limitAmount = CalculationUtil.getConvertedAmount(finCcy, custHeader.getLimitCcy(),
						transAmount);

				List<LimitDetails> custLimitDetails = getCustomerLimitDetails(mapping);

				processRepay(mapping, limitAmount, custLimitDetails);

				//log transaction
				logFinanceTransasction(finMain, custHeader, 0, tranType, overide, transAmount, limitAmount);

			}
		}

		//Customer group limit process
		if (groupHeader != null) {
			// check already mapping available or not 
			LimitReferenceMapping mapping = limitReferenceMappingDAO.getLimitReferencemapping(
					finMain.getFinReference(), groupHeader.getHeaderId());
			if (mapping != null) {

				BigDecimal limitAmount = CalculationUtil.getConvertedAmount(finCcy, groupHeader.getLimitCcy(),
						transAmount);

				List<LimitDetails> custLimitDetails = getCustomerLimitDetails(mapping);

				processRepay(mapping, limitAmount, custLimitDetails);

				//log transaction
				logFinanceTransasction(finMain, groupHeader, 0, tranType, overide, transAmount, limitAmount);
			}
		}

		logger.debug(" Entering ");
		return errors;
	}

	/**
	 * @param finMain
	 * @param financeType
	 * @param headerId
	 * @return
	 */
	private LimitReferenceMapping identifyLine(FinanceMain finMain, FinanceType financeType, long headerId) {
		logger.debug(" Entering ");
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
						mapping = getLimitRefMapping(LimitConstants.LIMIT_FINANCE, finRef, details.getLimitLine(),
								headerId);
						uncalssifed = false;
						break;
					}
				}

				if (uncalssifed) {
					mapping = getLimitRefMapping(LimitConstants.LIMIT_FINANCE, finRef,
							LimitConstants.LIMIT_ITEM_UNCLSFD, headerId);
				}
			}

		}

		logger.debug(" Leaving ");

		// Return the Limit items
		return mapping;
	}

	/**
	 * @param isCustomer
	 * @param header
	 * @param finMain
	 * @param data
	 * @param overide
	 * @param tranType
	 * @return
	 */
	private List<ErrorDetails> processLimits(LimitReferenceMapping mapping, String tranType, boolean allowOverride,
			BigDecimal limitAmount, FinanceDisbursement disbursement, boolean override) {
		logger.debug(" Entering ");

		//get limit details by line and group associated with it
		List<LimitDetails> limitDetails = getCustomerLimitDetails(mapping);
		if (limitDetails != null) {

			if (LimitConstants.LIMIT_TYPE_BLOCK.equals(tranType)) {
				return processBlock(mapping, disbursement, limitAmount, limitDetails, allowOverride, override);
			} else if (LimitConstants.LIMIT_TYPE_UNBLOCK.equals(tranType)) {
				processUnBlock(mapping, disbursement, limitAmount, limitDetails);
			} else if (LimitConstants.LIMIT_TYPE_APPROVE.equals(tranType)) {
				return processApprove(mapping, disbursement, limitAmount, limitDetails, allowOverride, override);
			} else if (LimitConstants.LIMIT_TYPE_CANCIL.equals(tranType)) {
				processCancel(mapping, disbursement, limitAmount, limitDetails);
			}
		}

		return Collections.emptyList();
	}

	/**
	 * @param finref
	 * @param headerId
	 * @param disbursement
	 * @param limitAmount
	 * @param limitDetails
	 * @return
	 */
	private ArrayList<ErrorDetails> processBlock(LimitReferenceMapping mapping, FinanceDisbursement disbursement,
			BigDecimal limitAmount, List<LimitDetails> limitDetails, boolean allowOverride, boolean overide) {
		logger.debug(" Entering ");

		String finref = mapping.getReferenceNumber();
		long headerId = mapping.getHeaderId();

		ArrayList<ErrorDetails> errors = new ArrayList<ErrorDetails>();

		LimitTransactionDetail limitTranDetail = getFinTransaction(finref, headerId, LimitConstants.LIMIT_TYPE_BLOCK,
				disbursement.getDisbSeq());
		if (limitTranDetail != null) {
			boolean block = removePreviuosBlock(limitTranDetail.getLimitAmount(), limitAmount, limitDetails,
					limitTranDetail.getId());
			if (!block) {
				mapping.setProceeed(false);
				return errors;
			}
		}

		//	validate
		if (!overide) {
			errors.addAll(validate(limitDetails, limitAmount, BigDecimal.ZERO, allowOverride));
			if (!errors.isEmpty()) {
				return errors;
			}
		}

		for (LimitDetails details : limitDetails) {
			details.setVersion(details.getVersion() + 1);
			details.setReservedLimit(details.getReservedLimit().add(limitAmount));
			limitDetailDAO.updateReserveUtilise(details, "");
		}

		logger.debug(" Leaving ");
		return errors;
	}

	/**
	 * @param finref
	 * @param headerId
	 * @param disbursement
	 * @param limitAmount
	 * @param custLimitDetails
	 */
	private void processUnBlock(LimitReferenceMapping mapping, FinanceDisbursement disbursement,
			BigDecimal limitAmount, List<LimitDetails> custLimitDetails) {
		logger.debug(" Entering ");
		String finref = mapping.getReferenceNumber();
		long headerId = mapping.getHeaderId();

		LimitTransactionDetail limitTranDetail = getFinTransaction(finref, headerId, LimitConstants.LIMIT_TYPE_BLOCK,
				disbursement.getDisbSeq());

		if (limitTranDetail != null) {
			for (LimitDetails details : custLimitDetails) {
				details.setVersion(details.getVersion() + 1);
				details.setReservedLimit(details.getReservedLimit().subtract(limitTranDetail.getLimitAmount()));
				limitDetailDAO.updateReserveUtilise(details, "");
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
	 * @return
	 */
	private ArrayList<ErrorDetails> processApprove(LimitReferenceMapping mapping, FinanceDisbursement disbursement,
			BigDecimal limitAmount, List<LimitDetails> custLimitDetails, boolean allowOverride, boolean overide) {
		logger.debug(" Entering ");

		String finref = mapping.getReferenceNumber();
		long headerId = mapping.getHeaderId();

		ArrayList<ErrorDetails> errors = new ArrayList<ErrorDetails>();

		LimitTransactionDetail limitTranDetail = getFinTransaction(finref, headerId, LimitConstants.LIMIT_TYPE_BLOCK,
				disbursement.getDisbSeq());

		BigDecimal prvBlockAmount = BigDecimal.ZERO;
		if (limitTranDetail != null) {
			prvBlockAmount = limitTranDetail.getLimitAmount();
		}

		//	validate
		if (!overide) {
			errors.addAll(validate(custLimitDetails, limitAmount, prvBlockAmount, allowOverride));
			if (!errors.isEmpty()) {
				return errors;
			}

		}

		for (LimitDetails details : custLimitDetails) {
			details.setVersion(details.getVersion() + 1);
			if (prvBlockAmount.compareTo(BigDecimal.ZERO) != 0) {
				if (prvBlockAmount.compareTo(limitAmount) != 0) {
					details.setReservedLimit(details.getReservedLimit().subtract(prvBlockAmount));
					details.setUtilisedLimit(details.getUtilisedLimit().add(limitAmount));
				} else {
					details.setReservedLimit(details.getReservedLimit().subtract(limitAmount));
					details.setUtilisedLimit(details.getUtilisedLimit().add(limitAmount));
				}
			} else {
				details.setUtilisedLimit(details.getUtilisedLimit().add(limitAmount));
			}
			limitDetailDAO.updateReserveUtilise(details, "");
		}

		logger.debug(" Leaving ");
		return errors;
	}

	/**
	 * @param finref
	 * @param headerId
	 * @param disbursement
	 * @param limitAmount
	 * @param custLimitDetails
	 */
	private void processCancel(LimitReferenceMapping mapping, FinanceDisbursement disbursement, BigDecimal limitAmount,
			List<LimitDetails> custLimitDetails) {
		logger.debug(" Entering ");

		for (LimitDetails details : custLimitDetails) {
			details.setVersion(details.getVersion() + 1);
			details.setUtilisedLimit(details.getUtilisedLimit().subtract(limitAmount));
			limitDetailDAO.updateReserveUtilise(details, "");
		}

		logger.debug(" Leaving ");
	}

	/**
	 * @param mapping
	 * @param limitAmount
	 * @param custLimitDetails
	 */
	private void processRepay(LimitReferenceMapping mapping, BigDecimal limitAmount, List<LimitDetails> custLimitDetails) {
		logger.debug(" Entering ");
		boolean revolvingLine = false;
		for (LimitDetails details : custLimitDetails) {
			if (details.isRevolving() && StringUtils.endsWith(mapping.getLimitLine(), details.getLimitLine())) {
				revolvingLine = true;
				break;
			}
		}

		if (!revolvingLine) {
			return;
		}
		for (LimitDetails details : custLimitDetails) {
			if (revolvingLine) {
				details.setVersion(details.getVersion() + 1);
				details.setUtilisedLimit(details.getUtilisedLimit().subtract(limitAmount));
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
	private boolean removePreviuosBlock(BigDecimal prvBlockAmount, BigDecimal limitAmount,
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
			BigDecimal preLimitAmount, boolean overrideAllowed) {

		ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();

		for (LimitDetails detail : limitDetails) {
			ErrorDetails error = validateLimitDetail(detail, limitAmount, preLimitAmount, overrideAllowed);
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
	private ErrorDetails validateLimitDetail(LimitDetails limitDetail, BigDecimal tranAmount,
			BigDecimal preLimitAmount, boolean overrideAllowed) {
		Date date = DateUtility.getAppDate();

		// If limit expired then return error  
		if (limitDetail != null) {
			String param = limitDetail.getLimitLine();
			if (param == null) {
				param = limitDetail.getGroupCode();
			}

			if (limitDetail.getExpiryDate() != null) {
				if (limitDetail.getExpiryDate().compareTo(date) <= 0) {
					return new ErrorDetails("60311", new String[] { param });
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
					return new ErrorDetails(param, "60312", new String[] { param }, null);
				} else {
					return new ErrorDetails("60314", new String[] { param });
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
		logger.debug(" Entering ");

		LimitReferenceMapping limitReferenceMapping = new LimitReferenceMapping();
		limitReferenceMapping.setNewRecord(true);
		limitReferenceMapping.setReferenceNumber(finreference);
		limitReferenceMapping.setReferenceCode(refCode);
		limitReferenceMapping.setLimitLine(limitLine);
		limitReferenceMapping.setHeaderId(headerId);

		logger.debug(" Leaving ");
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
		logger.debug(" Entering ");

		LimitTransactionDetail limittrans = new LimitTransactionDetail();
		limittrans.setReferenceCode(LimitConstants.LIMIT_FINANCE);
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
		limittrans.setTransactionDate(new Timestamp(System.currentTimeMillis()));
		limittrans.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		limittrans.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		limitTransactionDetailDAO.save(limittrans);

		logger.debug(" Leaving ");
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

	private LimitTransactionDetail getFinTransaction(String finref, long headerId, String transType, int schSeq) {
		return limitTransactionDetailDAO.getTransaction(LimitConstants.LIMIT_FINANCE, finref, transType, headerId,
				schSeq);
	}

	/**
	 * *****************************************************************************************************************
	 * ********************************************* Commitment *******************************************************
	 * *****************************************************************************************************************
	 * */

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
				mapping = getLimitRefMapping(LimitConstants.LIMIT_COMMITMENT, commitment.getCmtReference(), limitLine,
						header.getHeaderId());
			}

			List<LimitDetails> list = getCustomerLimitDetails(mapping);

			for (LimitDetails details : list) {
				details.setVersion(details.getVersion() + 1);
				switch (tranType) {
				case LimitConstants.LIMIT_TYPE_BLOCK:
					details.setReservedLimit(details.getReservedLimit().add(limitAmount));
					break;
				case LimitConstants.LIMIT_TYPE_UNBLOCK:
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

			LimitTransactionDetail ltd = prepareTransaction(LimitConstants.LIMIT_COMMITMENT, overide, tranType,
					transAmount, cmtCcy, limitAmount, header.getLimitCcy());

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
	public LimitReferenceMapping getLimitLineBYCommitment(FinanceMain financeMain) {
		logger.debug(" Entering ");
		String finreference = financeMain.getFinReference();
		Commitment commitment = commitmentDAO.getCommitmentByRef(financeMain.getFinCommitmentRef(), "");
		if (commitment != null) {
			long limtiDetailID = commitment.getLimitLineId();
			LimitDetails limitDetails = limitDetailDAO.getLimitLineByDetailId(limtiDetailID, "_AView");
			if (limitDetails != null) {
				logger.debug(" Leaving ");
				return getLimitRefMapping(LimitConstants.LIMIT_FINANCE, finreference, limitDetails.getLimitLine(),
						limitDetails.getLimitHeaderId());
			}
		}
		return null;
	}

	/**
	 * @param financeMain
	 * @param overide
	 * @param tranType
	 * @param transAmount
	 * @param cccy
	 */
	private void logTransasction(String refernce, LimitHeader header, LimitTransactionDetail transDet,
			LoggedInUser userDetails) {
		logger.debug(" Entering ");

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

		logger.debug(" Leaving ");

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
}
