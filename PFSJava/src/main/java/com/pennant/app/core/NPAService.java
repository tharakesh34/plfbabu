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
 * 
 * FileName : NPAService.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.app.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.applicationmaster.NPABucketConfiguration;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;

public class NPAService extends ServiceHelper {

	private static final long	serialVersionUID	= 6161809223570900644L;
	private ProvisionDAO		provisionDAO;
	private RuleExecutionUtil	ruleExecutionUtil;
	private RuleDAO				ruleDAO;

	/**
	 * Default constructor
	 */
	public NPAService() {
		super();
	}

	public CustEODEvent processNPABuckets(CustEODEvent custEODEvent) throws Exception {
		String prvRule = "";
		Object object = SysParamUtil.getValue("PROVISION_RULE");
		if (object != null) {
			return custEODEvent;
		}

		prvRule = (String) object;

		Rule rule = ruleDAO.getRuleByID(prvRule, RuleConstants.MODULE_PROVSN, RuleConstants.EVENT_PROVSN, "");
		if (rule == null) {
			return custEODEvent;
		}

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		Date valueDate = custEODEvent.getEodValueDate();

		for (FinEODEvent finEODEvent : finEODEvents) {
			finEODEvent = findProvision(finEODEvent, valueDate, rule);

		}
		return custEODEvent;

	}

	private FinEODEvent findProvision(FinEODEvent finEODEvent, Date valueDate, Rule rule) throws SQLException {

		FinanceProfitDetail pftDetail = finEODEvent.getFinProfitDetail();
		String finReference = pftDetail.getFinReference();
		String productCode = pftDetail.getFinCategory();
		String finStatus = pftDetail.getFinStatus();
		int dueBucket = pftDetail.getDueBucket();

		Provision provision = new Provision();

		List<NPABucketConfiguration> list = getNPABucketConfigurations(productCode);

		//No configuration for NPA found then do nothing
		if (list == null || list.isEmpty()) {
			return finEODEvent;
		}

		long npaBucket = 0;

		/*
		 * if current bucket status is empty no need to calculate the provision buckets, but if he is already in
		 * provision then provision should be update.
		 */

		if (StringUtils.isNotBlank(finStatus)) {
			long bucketId = getBucketID(finStatus);
			sortNPABucketConfig(list);
			for (NPABucketConfiguration configuration : list) {
				if (configuration.getBucketID() == bucketId && configuration.getDueDays() >= dueBucket) {
					npaBucket = configuration.getBucketID();
					break;
				}
			}
		}

		provision.setFinReference(finReference);
		provision.setFinBranch(pftDetail.getFinBranch());
		provision.setFinType(pftDetail.getFinType());
		provision.setCustID(pftDetail.getCustId());
		provision.setProvisionCalDate(valueDate);
		provision.setPftBal(pftDetail.getTotalPftBal());
		provision.setPriBal(pftDetail.getTotalPriBal());
		provision.setPrincipalDue(pftDetail.getODPrincipal());
		provision.setProfitDue(pftDetail.getODProfit());
		provision.setLastFullyPaidDate(pftDetail.getFullPaidDate());
		provision.setDueFromDate(pftDetail.getPrvODDate());
		provision.setDuedays(pftDetail.getCurODDays());

		BigDecimal provisionRate = getDuePercentage(finEODEvent, rule.getSQLRule());
		BigDecimal provisonAmt = getProvisionAmt(provisionRate, provision);
		provision.setPrvovisionRate(provisionRate);
		provision.setProvisionAmtCal(provisonAmt);
		//Should be set after the postings
		provision.setProvisionedAmt(BigDecimal.ZERO);

		if (StringUtils.isNotBlank(finStatus)) {
			provision.setDpdBucketID(getBucketID(finStatus));
		} else {
			provision.setDpdBucketID(0);
		}
		provision.setNpaBucketID(npaBucket);

		Provision prvProvison = provisionDAO.getCurNPABucket(finReference);
		if (prvProvison != null) {
			provisionDAO.updateProvisonAmounts(provision);
		} else {
			if (provision.getNpaBucketID() != 0 && provision.getProvisionAmtCal().compareTo(BigDecimal.ZERO) != 0) {
				provisionDAO.save(provision, "");
			}
		}
		return finEODEvent;

	}

	private BigDecimal getDuePercentage(FinEODEvent finEODEvent, String rule) throws SQLException {

		FinanceProfitDetail pftDetail = finEODEvent.getFinProfitDetail();

		String finCcy = pftDetail.getFinCcy();
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("RestructureLoan", finEODEvent.getFinanceMain().isScheduleRegenerated());
		dataMap.put("DueBucket", pftDetail.getDueBucket());
		dataMap.put("ODDays", pftDetail.getCurODDays());
		dataMap.put("Product", pftDetail.getFinCategory());

		BigDecimal pecentage = (BigDecimal) ruleExecutionUtil
				.executeRule(rule, dataMap, finCcy, RuleReturnType.DECIMAL);

		pecentage = pecentage.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
		return pecentage;
	}

	private BigDecimal getProvisionAmt(BigDecimal percentage, Provision provision) throws SQLException {

		int total = 0;
		Object object = SysParamUtil.getValue("PROVISION_ON_TOTAL");
		if (object != null) {
			total = Integer.parseInt(object.toString());
		}
		BigDecimal dueAmount = provision.getPftBal();

		if (total == 1) {
			dueAmount = provision.getPftBal().add(provision.getPriBal());
		}
		BigDecimal provisonAmt = BigDecimal.ZERO;
		if (percentage.compareTo(BigDecimal.ZERO) != 0) {
			provisonAmt = dueAmount.multiply(percentage);
			provisonAmt = provisonAmt.setScale(0);

		}
		return provisonAmt;
	}

	public void setProvisionDAO(ProvisionDAO provisionDAO) {
		this.provisionDAO = provisionDAO;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

}
