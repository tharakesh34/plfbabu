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
 *
 * FileName    		:  ServiceHelper.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.app.core;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.AccountProcessUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.dao.finance.FinContributorDetailDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.finance.SecondaryAccountDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.financemanagement.ProvisionMovementDAO;
import com.pennant.backend.dao.insurancedetails.FinInsurancesDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.applicationmaster.DPDBucket;
import com.pennant.backend.model.applicationmaster.DPDBucketConfiguration;
import com.pennant.backend.model.applicationmaster.NPABucketConfiguration;
import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.SecondaryAccount;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.cache.util.FinanceConfigCache;
import com.pennant.eod.dao.CustomerQueuingDAO;

abstract public class ServiceHelper implements Serializable {

	private static final long serialVersionUID = 4165353615228874397L;
	protected static Logger logger = LogManager.getLogger(ServiceHelper.class.getClass());

	private DataSource dataSource;
	//customer
	protected CustomerDAO customerDAO;
	protected CustomerStatusCodeDAO customerStatusCodeDAO;
	private CustomerQueuingDAO customerQueuingDAO;
	//Loan
	protected FinanceTypeDAO financeTypeDAO;
	protected FinanceMainDAO financeMainDAO;
	protected FinanceScheduleDetailDAO financeScheduleDetailDAO;
	protected RepayInstructionDAO repayInstructionDAO;
	protected FinanceDisbursementDAO financeDisbursementDAO;
	protected FinanceRepaymentsDAO financeRepaymentsDAO;
	protected FinanceProfitDetailDAO financeProfitDetailDAO;
	protected FinFeeScheduleDetailDAO finFeeScheduleDetailDAO;
	protected FinInsurancesDAO finInsurancesDAO;
	protected PresentmentDetailDAO presentmentDetailDAO;
	protected FinServiceInstrutionDAO finServiceInstructionDAO;
	//accounting
	private FinContributorDetailDAO finContributorDetailDAO;
	private FinTypeAccountingDAO finTypeAccountingDAO;
	private PostingsDAO postingsDAO;
	private SecondaryAccountDAO secondaryAccountDAO;
	private AccountProcessUtil accountProcessUtil;
	protected PostingsPreparationUtil postingsPreparationUtil;
	//over due
	protected FinODDetailsDAO finODDetailsDAO;
	protected ProvisionDAO provisionDAO;
	protected ProvisionMovementDAO provisionMovementDAO;
	protected ProjectedAmortizationDAO projectedAmortizationDAO;
	protected RuleDAO ruleDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;
	protected ExtendedFieldDetailsService extendedFieldDetailsService;
	protected FinExcessAmountDAO finExcessAmountDAO;
	protected FinLogEntryDetailDAO finLogEntryDetailDAO;

	@Autowired
	protected EODConfigDAO eodConfigDAO;
	private static EODConfig eodConfig;

	public Long getAccountingID(FinanceMain main, String eventCode) {
		//FIXME: PV:  28AUG19. No Separate Accounting for Promotion
		/*
		 * if (StringUtils.isNotBlank(main.getPromotionCode())) { return
		 * AccountingConfigCache.getCacheAccountSetID(main.getPromotionCode(), eventCode,
		 * FinanceConstants.MODULEID_PROMOTION); } else { return
		 * AccountingConfigCache.getCacheAccountSetID(main.getFinType(), eventCode, FinanceConstants.MODULEID_FINTYPE);
		 * }
		 */

		return AccountingConfigCache.getCacheAccountSetID(main.getFinType(), eventCode,
				FinanceConstants.MODULEID_FINTYPE);
	}

	public final void postAccountingEOD(AEEvent aeEvent) throws Exception {
		aeEvent.setPostingUserBranch("EOD");
		aeEvent.setEOD(true);
		postingsPreparationUtil.postAccountingEOD(aeEvent);
	}

	public final void saveAccountingEOD(List<ReturnDataSet> returnDataSets) throws Exception {
		postingsPreparationUtil.saveAccountingEOD(returnDataSets);
	}

	/**
	 * @param fintype
	 * @return
	 */
	public final FinanceType getFinanceType(String fintype) {
		return FinanceConfigCache.getCacheFinanceType(StringUtils.trimToEmpty(fintype));

	}

	public String getBucket(long bucketID) {
		DPDBucket dpdBucket = FinanceConfigCache.getCacheDPDBucket(bucketID);
		if (dpdBucket != null) {
			return dpdBucket.getBucketCode();
		}
		return "";
	}

	public Long getBucketID(String finStatus) {
		DPDBucket dpdBucket = FinanceConfigCache.getCacheDPDBucketCode(StringUtils.trimToEmpty(finStatus));
		if (dpdBucket != null) {
			return dpdBucket.getBucketID();
		}
		return Long.valueOf(0);
	}

	public List<DPDBucketConfiguration> getBucketConfigurations(String productCode) {
		return FinanceConfigCache.getCacheDPDBucketConfiguration(StringUtils.trimToEmpty(productCode));
	}

	public List<NPABucketConfiguration> getNPABucketConfigurations(String productCode) {
		return FinanceConfigCache.getCacheNPABucketConfiguration(StringUtils.trimToEmpty(productCode));
	}

	public BigDecimal getDecimal(ResultSet resultSet, String name) throws SQLException {
		BigDecimal val = resultSet.getBigDecimal(name);
		if (val == null) {
			val = BigDecimal.ZERO;
		}
		return val;
	}

	public BigDecimal getDecimal(BigDecimal bigDecimal) {
		if (bigDecimal == null) {
			bigDecimal = BigDecimal.ZERO;
		}
		return bigDecimal;
	}

	public int getIndexFromMap(Map<Date, Integer> datesMap, Date date) {
		Date formatDate = formatDate(date);
		if (datesMap.containsKey(formatDate)) {
			return datesMap.get(formatDate);
		}
		return 0;
	}

	public Date getDateFromMap(Map<Date, Integer> datesMap, int index) {
		for (Entry<Date, Integer> entryset : datesMap.entrySet()) {
			if (entryset.getValue() == index) {
				return formatDate(entryset.getKey());
			}
		}
		return null;
	}

	public Date formatDate(Date date) {
		if (date != null) {
			return DateUtility.getDate(DateUtility.format(date, PennantConstants.DBDateFormat),
					PennantConstants.DBDateFormat);
		}
		return null;

	}

	/**
	 * @param finReference
	 * @return
	 */
	public final String getSecondaryAccountsAsString(String finReference) {
		return getSecordayAccounts(getSecondaryAccounts(finReference));
	}

	/**
	 * @param finReference
	 * @return
	 */
	public final List<SecondaryAccount> getSecondaryAccounts(String finReference) {
		return secondaryAccountDAO.getSecondaryAccountsByFinRef(finReference, "");
	}

	/**
	 * @param listSecondary
	 * @return
	 */
	public final String getSecordayAccounts(List<SecondaryAccount> listSecondary) {

		StringBuilder secordayAccounts = new StringBuilder(0);
		if (listSecondary != null && !listSecondary.isEmpty()) {
			for (SecondaryAccount secondaryAccount : listSecondary) {
				if (secordayAccounts.length() == 0) {
					secordayAccounts.append(secondaryAccount.getAccountNumber());
				} else {
					secordayAccounts.append(';');
					secordayAccounts.append(secondaryAccount.getAccountNumber());
				}
			}
		}

		return secordayAccounts.toString();

	}

	public void loadEODConfig() {
		try {
			List<EODConfig> list = eodConfigDAO.getEODConfig();
			if (!list.isEmpty()) {
				eodConfig = list.get(0);
			}

		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

	protected boolean isOldestDueOverDue(FinanceScheduleDetail curSchd) {
		if (ImplementationConstants.ALLOW_OLDEST_DUE) {
			FinODDetails od = finODDetailsDAO.getFinODByFinRef(curSchd.getFinReference(), curSchd.getSchDate());
			if (od != null && (od.getFinCurODAmt().compareTo(BigDecimal.ZERO) > 0
					&& (od.getFinCurODPft().compareTo(BigDecimal.ZERO) > 0
							|| od.getFinCurODPri().compareTo(BigDecimal.ZERO) > 0))) {
				return true;
			}
		}
		return false;
	}

	public void setFinContributorDetailDAO(FinContributorDetailDAO finContributorDetailDAO) {
		this.finContributorDetailDAO = finContributorDetailDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setSecondaryAccountDAO(SecondaryAccountDAO secondaryAccountDAO) {
		this.secondaryAccountDAO = secondaryAccountDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
		this.customerStatusCodeDAO = customerStatusCodeDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setFinTypeAccountingDAO(FinTypeAccountingDAO finTypeAccountingDAO) {
		this.finTypeAccountingDAO = finTypeAccountingDAO;
	}

	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setFinFeeScheduleDetailDAO(FinFeeScheduleDetailDAO finFeeScheduleDetailDAO) {
		this.finFeeScheduleDetailDAO = finFeeScheduleDetailDAO;
	}

	public void setFinInsurancesDAO(FinInsurancesDAO finInsurancesDAO) {
		this.finInsurancesDAO = finInsurancesDAO;
	}

	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	public void setAccountProcessUtil(AccountProcessUtil accountProcessUtil) {
		this.accountProcessUtil = accountProcessUtil;
	}

	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}

	public void setProvisionDAO(ProvisionDAO provisionDAO) {
		this.provisionDAO = provisionDAO;
	}

	public EODConfig getEodConfig() {
		return eodConfig;
	}

	public void setProjectedAmortizationDAO(ProjectedAmortizationDAO projectedAmortizationDAO) {
		this.projectedAmortizationDAO = projectedAmortizationDAO;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

	public FinLogEntryDetailDAO getFinLogEntryDetailDAO() {
		return finLogEntryDetailDAO;
	}

	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

	public void setProvisionMovementDAO(ProvisionMovementDAO provisionMovementDAO) {
		this.provisionMovementDAO = provisionMovementDAO;
	}

}
