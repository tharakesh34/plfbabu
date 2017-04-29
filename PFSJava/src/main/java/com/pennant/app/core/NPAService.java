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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.applicationmaster.NPABucketConfiguration;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.eod.util.EODProperties;

public class NPAService extends ServiceHelper {

	private static final long	serialVersionUID	= 6161809223570900644L;
	private static Logger		logger				= Logger.getLogger(NPAService.class);
	private ProvisionDAO		provisionDAO;
	private FinODDetailsDAO		finODDetailsDAO;
	private RuleExecutionUtil	ruleExecutionUtil;
	private RuleDAO				ruleDAO;

	public static final String	NPA					= "SELECT FP.FinReference,FP.FINBRANCH,FP.FINTYPE,FM.FinStatus,FP.FinCategory ,FP.MAXODDAYS, "
															+ " FP.ODPRINCIPAL,FP.ODPROFIT,FP.FullPaidDate,FP.TOTALPFTBAL,FP.TOTALPRIBAL,FP.FinCcy,"
															+ " FM.SCHEDULEREGENERATED,FM.DUEBucket "
															+ " FROM FINPFTDETAILS FP INNER JOIN FInanceMain FM ON FP.FinReference=FM.FINREFERENCE"
															+ " Where FP.MAXODDAYS > 0 and FP.CustID = ?";

	/**
	 * Default constructor
	 */
	public NPAService() {
		super();
	}

	public void processNPABuckets(Connection connection, long custId, Date date) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		String finreference = "";

		try {
			String prvRule = "";
			Object object = SysParamUtil.getValue("PROVISION_RULE");
			if (object != null) {
				prvRule = (String) object;
			}

			Rule rule = ruleDAO.getRuleByID(prvRule, RuleConstants.MODULE_PROVSN, RuleConstants.EVENT_PROVSN, "");
			if (rule == null) {
				return;
			}

			//payments
			sqlStatement = connection.prepareStatement(NPA);
			sqlStatement.setLong(1, custId);
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {
				finreference = resultSet.getString("FinReference");
				String finStatus = resultSet.getString("FinStatus");
				String productCode = resultSet.getString("FinCategory");
				int maxDueDays = resultSet.getInt("MAXODDAYS");
				int dueBucket = resultSet.getInt("DueBucket");

				List<NPABucketConfiguration> list = EODProperties.getNPABucketConfigurations(productCode);
				//No configuration for NPA found then do nothing
				if (list == null || list.isEmpty()) {
					continue;
				}

				long npaBucket = 0;
				/*
				 * if current bucket status is empty no need to calculate the provision buckets, but if he is already in
				 * provision then provision should be update.
				 */
				if (StringUtils.isNotBlank(finStatus)) {
					long bucketId = EODProperties.getBucketID(finStatus);
					sortNPABucketConfig(list);
					for (NPABucketConfiguration configuration : list) {
						if (configuration.getBucketID() == bucketId && configuration.getDueDays() >= dueBucket) {
							npaBucket = configuration.getBucketID();
							break;
						}
					}
				}

				Provision provision = new Provision();
				provision.setFinReference(finreference);
				provision.setFinBranch(resultSet.getString("FINBRANCH"));
				provision.setFinType(resultSet.getString("FINTYPE"));
				provision.setCustID(custId);
				provision.setProvisionCalDate(date);
				provision.setPftBal(getDecimal(resultSet, "TOTALPFTBAL"));
				provision.setPriBal(getDecimal(resultSet, "TOTALPRIBAL"));
				provision.setPrincipalDue(getDecimal(resultSet, "ODPRINCIPAL"));
				provision.setProfitDue(getDecimal(resultSet, "ODPROFIT"));
				provision.setLastFullyPaidDate(resultSet.getDate("FullPaidDate"));
				provision.setDueFromDate(finODDetailsDAO.getFinDueFromDate(finreference));
				provision.setDuedays(maxDueDays);

				BigDecimal provisionRate = getDuePercentage(resultSet, rule.getSQLRule());
				BigDecimal provisonAmt = getProvisionAmt(provisionRate, provision);
				provision.setPrvovisionRate(provisionRate);
				provision.setProvisionAmtCal(provisonAmt);
				//Should be set after the postings
				provision.setProvisionedAmt(BigDecimal.ZERO);

				if (StringUtils.isNotBlank(finStatus)) {
					provision.setDpdBucketID(EODProperties.getBucketID(finStatus));
				} else {
					provision.setDpdBucketID(0);
				}
				provision.setNpaBucketID(npaBucket);

				Provision prvProvison = provisionDAO.getCurNPABucket(finreference);
				if (prvProvison != null) {
					provisionDAO.updateProvisonAmounts(provision);
				} else {
					if (provision.getNpaBucketID() != 0
							&& provision.getProvisionAmtCal().compareTo(BigDecimal.ZERO) != 0) {
						provisionDAO.save(provision, "");
					}
				}
			}

		} catch (Exception e) {
			logger.error("Exception: Finreference :" + finreference, e);
			throw new Exception("Exception: Finreference : " + finreference, e);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}
	}

	private BigDecimal getDuePercentage(ResultSet resultSet, String rule) throws SQLException {

		String finCcy = resultSet.getString("FinCcy");
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("RestructureLoan", resultSet.getBoolean("SCHEDULEREGENERATED"));
		dataMap.put("DueBucket", resultSet.getInt("DueBucket"));
		dataMap.put("ODDays", resultSet.getInt("MAXODDAYS"));

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

	private void sortNPABucketConfig(List<NPABucketConfiguration> list) {

		if (list != null && !list.isEmpty()) {
			Collections.sort(list, new Comparator<NPABucketConfiguration>() {
				@Override
				public int compare(NPABucketConfiguration detail1, NPABucketConfiguration detail2) {
					return detail1.getDueDays() - detail2.getDueDays();
				}
			});
		}

	}

	public void setProvisionDAO(ProvisionDAO provisionDAO) {
		this.provisionDAO = provisionDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

}
