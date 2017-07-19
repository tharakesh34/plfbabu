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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.AccountProcessUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.dao.finance.FinContributorDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.finance.SecondaryAccountDAO;
import com.pennant.backend.dao.financemanagement.PresentmentHeaderDAO;
import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.insurancedetails.FinInsurancesDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.applicationmaster.DPDBucket;
import com.pennant.backend.model.applicationmaster.DPDBucketConfiguration;
import com.pennant.backend.model.applicationmaster.NPABucketConfiguration;
import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.SecondaryAccount;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.cache.util.FinanceConfigCache;
import com.pennant.eod.dao.CustomerQueuingDAO;

abstract public class ServiceHelper implements Serializable {

	private static final long			serialVersionUID	= 4165353615228874397L;
	private static Logger				logger				= Logger.getLogger(ServiceHelper.class);

	private DataSource					dataSource;
	//customer
	private CustomerDAO					customerDAO;
	private CustomerStatusCodeDAO		customerStatusCodeDAO;
	private CustomerQueuingDAO			customerQueuingDAO;
	//Loan
	private FinanceTypeDAO				financeTypeDAO;
	private FinanceMainDAO				financeMainDAO;
	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private RepayInstructionDAO			repayInstructionDAO;
	private FinanceDisbursementDAO		financeDisbursementDAO;
	private FinanceRepaymentsDAO		financeRepaymentsDAO;
	private FinanceProfitDetailDAO		financeProfitDetailDAO;
	private FinFeeScheduleDetailDAO		finFeeScheduleDetailDAO;
	private FinInsurancesDAO			finInsurancesDAO;
	private PresentmentHeaderDAO		presentmentHeaderDAO;
	//accounting
	private FinContributorDetailDAO		finContributorDetailDAO;
	private FinTypeAccountingDAO		finTypeAccountingDAO;
	private PostingsDAO					postingsDAO;
	private SecondaryAccountDAO			secondaryAccountDAO;
	private AccountProcessUtil			accountProcessUtil;
	private PostingsPreparationUtil		postingsPreparationUtil;
	//over due
	private FinODDetailsDAO				finODDetailsDAO;
	private ProvisionDAO				provisionDAO;

	@Autowired
	private EODConfigDAO				eodConfigDAO;
	private static EODConfig			eodConfig;

	public long getAccountingID(FinanceMain main, String eventCode) {
		if (StringUtils.isNotBlank(main.getPromotionCode())) {
			return AccountingConfigCache.getAccountSetID(main.getPromotionCode(), eventCode,
					FinanceConstants.MODULEID_PROMOTION);
		} else {
			return AccountingConfigCache.getAccountSetID(main.getFinType(), eventCode,
					FinanceConstants.MODULEID_FINTYPE);
		}
	}

	public final AEEvent postAccountingEOD(AEEvent aeEvent) throws Exception {
		aeEvent.setPostingUserBranch("EOD");//FIXME
		aeEvent.setEOD(true);
		return getPostingsPreparationUtil().postAccountingEOD(aeEvent);
	}

	public final void saveAccountingEOD(List<ReturnDataSet> returnDataSets) throws Exception {
		getPostingsPreparationUtil().saveAccountingEOD(returnDataSets);
	}

	/**
	 * @param fintype
	 * @return
	 */
	public final FinanceType getFinanceType(String fintype) {
		return FinanceConfigCache.getFinanceType(StringUtils.trimToEmpty(fintype));

	}

	public String getBucket(long bucketID) {
		DPDBucket dpdBucket = FinanceConfigCache.getDPDBucket(bucketID);
		if (dpdBucket != null) {
			return dpdBucket.getBucketCode();
		}
		return "";
	}

	public Long getBucketID(String finStatus) {
		DPDBucket dpdBucket = FinanceConfigCache.getDPDBucketCode(StringUtils.trimToEmpty(finStatus));
		if (dpdBucket != null) {
			return dpdBucket.getBucketID();
		}
		return Long.valueOf(0);
	}

	public List<DPDBucketConfiguration> getBucketConfigurations(String productCode) {
		return FinanceConfigCache.getDPDBucketConfiguration(StringUtils.trimToEmpty(productCode));
	}

	public List<NPABucketConfiguration> getNPABucketConfigurations(String productCode) {
		return FinanceConfigCache.getNPABucketConfiguration(StringUtils.trimToEmpty(productCode));
	}

	public void sortBucketConfig(List<DPDBucketConfiguration> list) {

		if (list != null && !list.isEmpty()) {
			Collections.sort(list, new Comparator<DPDBucketConfiguration>() {
				@Override
				public int compare(DPDBucketConfiguration detail1, DPDBucketConfiguration detail2) {
					return detail1.getDueDays() - detail2.getDueDays();
				}
			});
		}

	}

	public void sortNPABucketConfig(List<NPABucketConfiguration> list) {

		if (list != null && !list.isEmpty()) {
			Collections.sort(list, new Comparator<NPABucketConfiguration>() {
				@Override
				public int compare(NPABucketConfiguration detail1, NPABucketConfiguration detail2) {
					return detail1.getDueDays() - detail2.getDueDays();
				}
			});
		}

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
			return DateUtility.getDate(DateUtility.formateDate(date, PennantConstants.DBDateFormat),
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
		return getSecondaryAccountDAO().getSecondaryAccountsByFinRef(finReference, "");
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

	public FinanceProfitDetail getFinPftDetailRef(String finMainRef, List<FinanceProfitDetail> listprofitDetails) {
		FinanceProfitDetail profitDetail = null;
		Iterator<FinanceProfitDetail> it = listprofitDetails.iterator();
		while (it.hasNext()) {
			FinanceProfitDetail financeProfitDetail = (FinanceProfitDetail) it.next();
			if (StringUtils.equals(financeProfitDetail.getFinReference(), finMainRef)) {
				profitDetail = financeProfitDetail;
				it.remove();
				break;
			}
		}
		return profitDetail;
	}

	public List<FinanceScheduleDetail> getFinSchdDetailRef(String finMainRef, List<FinanceScheduleDetail> finSchdlist) {
		List<FinanceScheduleDetail> finSchedulelist = new ArrayList<FinanceScheduleDetail>();
		Iterator<FinanceScheduleDetail> it = finSchdlist.iterator();
		while (it.hasNext()) {
			FinanceScheduleDetail financeProfitDetail = (FinanceScheduleDetail) it.next();
			if (StringUtils.equals(financeProfitDetail.getFinReference(), finMainRef)) {
				finSchedulelist.add(financeProfitDetail);
				it.remove();
			}
		}
		return finSchedulelist;
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

	public FinContributorDetailDAO getFinContributorDetailDAO() {
		return finContributorDetailDAO;
	}

	public void setFinContributorDetailDAO(FinContributorDetailDAO finContributorDetailDAO) {
		this.finContributorDetailDAO = finContributorDetailDAO;
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public SecondaryAccountDAO getSecondaryAccountDAO() {
		return secondaryAccountDAO;
	}

	public void setSecondaryAccountDAO(SecondaryAccountDAO secondaryAccountDAO) {
		this.secondaryAccountDAO = secondaryAccountDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public CustomerStatusCodeDAO getCustomerStatusCodeDAO() {
		return customerStatusCodeDAO;
	}

	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
		this.customerStatusCodeDAO = customerStatusCodeDAO;
	}

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
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

	public FinTypeAccountingDAO getFinTypeAccountingDAO() {
		return finTypeAccountingDAO;
	}

	public void setFinTypeAccountingDAO(FinTypeAccountingDAO finTypeAccountingDAO) {
		this.finTypeAccountingDAO = finTypeAccountingDAO;
	}

	public RepayInstructionDAO getRepayInstructionDAO() {
		return repayInstructionDAO;
	}

	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public FinFeeScheduleDetailDAO getFinFeeScheduleDetailDAO() {
		return finFeeScheduleDetailDAO;
	}

	public void setFinFeeScheduleDetailDAO(FinFeeScheduleDetailDAO finFeeScheduleDetailDAO) {
		this.finFeeScheduleDetailDAO = finFeeScheduleDetailDAO;
	}

	public FinInsurancesDAO getFinInsurancesDAO() {
		return finInsurancesDAO;
	}

	public void setFinInsurancesDAO(FinInsurancesDAO finInsurancesDAO) {
		this.finInsurancesDAO = finInsurancesDAO;
	}

	public PresentmentHeaderDAO getPresentmentHeaderDAO() {
		return presentmentHeaderDAO;
	}

	public void setPresentmentHeaderDAO(PresentmentHeaderDAO presentmentHeaderDAO) {
		this.presentmentHeaderDAO = presentmentHeaderDAO;
	}

	public AccountProcessUtil getAccountProcessUtil() {
		return accountProcessUtil;
	}

	public void setAccountProcessUtil(AccountProcessUtil accountProcessUtil) {
		this.accountProcessUtil = accountProcessUtil;
	}

	public CustomerQueuingDAO getCustomerQueuingDAO() {
		return customerQueuingDAO;
	}

	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}

	public ProvisionDAO getProvisionDAO() {
		return provisionDAO;
	}

	public void setProvisionDAO(ProvisionDAO provisionDAO) {
		this.provisionDAO = provisionDAO;
	}

	public EODConfigDAO getEodConfigDAO() {
		return eodConfigDAO;
	}

	public EODConfig getEodConfig() {
		return eodConfig;
	}


}
