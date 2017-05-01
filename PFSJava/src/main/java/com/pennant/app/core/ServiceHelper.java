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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.applicationmaster.DPDBucketConfigurationDAO;
import com.pennant.backend.dao.applicationmaster.DPDBucketDAO;
import com.pennant.backend.dao.finance.FinContributorDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.SecondaryAccountDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.applicationmaster.DPDBucket;
import com.pennant.backend.model.applicationmaster.DPDBucketConfiguration;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.SecondaryAccount;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.util.EODProperties;

abstract public class ServiceHelper implements Serializable {

	private static final long			serialVersionUID	= 4165353615228874397L;
	private static Logger				logger				= Logger.getLogger(ServiceHelper.class);

	private DataSource					dataSource;
	private FinanceTypeDAO				financeTypeDAO;
	private FinanceMainDAO				financeMainDAO;
	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private SecondaryAccountDAO			secondaryAccountDAO;
	private PostingsDAO					postingsDAO;
	private FinanceProfitDetailDAO		financeProfitDetailDAO;
	private CustomerStatusCodeDAO		customerStatusCodeDAO;

	private FinContributorDetailDAO		finContributorDetailDAO;
	private AccountEngineExecution		engineExecution;
	private FinTypeAccountingDAO		finTypeAccountingDAO;

	private DPDBucketConfigurationDAO	dPDBucketConfigurationDAO;
	private DPDBucketDAO				dPDBucketDAO;

	/**
	 * @param dataSet
	 * @param amountCodes
	 * @param financeType
	 * @return
	 * @throws Exception
	 */
	public final List<ReturnDataSet> prepareAccounting(HashMap<String, Object> executingMap, FinanceType financeType)
			throws Exception {
		logger.debug(" Entering ");
		List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();

		try {
			//String product = "";

			financeType.getDeclaredFieldValues(executingMap);
			list = getEngineExecution().getAccEngineExecResults("Y", executingMap, false);
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		}

		logger.debug(" Leaving ");
		return list;
	}

	public List<ReturnDataSet> processAccountingByEvent(HashMap<String, Object> executingMap) throws Exception {
		try {
			return getEngineExecution().processAccountingByEvent(executingMap, "Y");
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		}
	}

	public Date formatDate(Date date) {
		if (date != null) {
			return DateUtility.getDate(DateUtility.formateDate(date, PennantConstants.DBDateFormat),
					PennantConstants.DBDateFormat);
		}
		return null;

	}

	/**
	 * @param list
	 * @return
	 */
	public final long saveAccounting(List<ReturnDataSet> list) {
		long linkedTranId = Long.MIN_VALUE;
		if (list == null) {
			return 0;
		}
		// Amount with zero balance should not posted
		Iterator<ReturnDataSet> it = list.iterator();
		while (it.hasNext()) {
			if (linkedTranId == Long.MIN_VALUE) {
				linkedTranId = getPostingsDAO().getLinkedTransId();
			}
			ReturnDataSet returnDataSet = it.next();
			returnDataSet.setLinkedTranId(linkedTranId);
			if (returnDataSet.getPostAmount().compareTo(BigDecimal.ZERO) == 0) {
				it.remove();
			}
		}
		if (!list.isEmpty()) {
			getPostingsDAO().saveHeader(list.get(0), list.get(0).getPostStatus(), "");
			getPostingsDAO().saveEODBatch(list, "", "N");
			return linkedTranId;
		}
		return 0;
	}

	/**
	 * @param list
	 * @param listSecondary
	 * @param finRepay
	 * @return
	 */
	public final List<ReturnDataSet> setOtherDetails(List<ReturnDataSet> list, List<SecondaryAccount> listSecondary,
			FinRepayQueue finRepay) {

		for (ReturnDataSet returnDataSet : list) {
			returnDataSet.setSecondaryAccounts(getSecordayAccounts(listSecondary));
			returnDataSet.setFinRpyFor(finRepay.getFinRpyFor());
			returnDataSet.setValueDate(finRepay.getRpyDate());

		}
		return list;
	}

	/**
	 * @param fintype
	 * @return
	 */
	public final FinanceType getFinanceType(String fintype) {

		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
		if (phase.equals(PennantConstants.APP_PHASE_DAY)) {
			return getFinanceTypeDAO().getFinanceTypeByFinType(fintype);
		} else {
			return EODProperties.getFinanceType(fintype);
		}

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
	public final FinanceMain getFinanceMain(String finReference) {
		return getFinanceMainDAO().getFinanceMainForBatch(finReference);
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

	public List<DPDBucketConfiguration> getBucketConfigurations(String productCode) {
		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
		if (phase.equals(PennantConstants.APP_PHASE_DAY)) {
			return dPDBucketConfigurationDAO.getDPDBucketConfigurations(productCode);
		} else {
			return EODProperties.getBucketConfigurations(productCode);
		}
	}

	public String getBucket(long bucketID) {
		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
		if (phase.equals(PennantConstants.APP_PHASE_DAY)) {
			DPDBucket dpdBucket = dPDBucketDAO.getDPDBucket(bucketID, "");
			if (dpdBucket != null) {
				return dpdBucket.getBucketCode();
			}
		} else {
			return EODProperties.getBucket(bucketID);
		}

		return "";
	}

	public Long getBucketID(String finStatus) {
		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
		if (phase.equals(PennantConstants.APP_PHASE_DAY)) {
			DPDBucket dpdBucket = dPDBucketDAO.getDPDBucket(finStatus, "");
			if (dpdBucket != null) {
				return dpdBucket.getBucketID();
			}

		} else {
			return EODProperties.getBucketID(finStatus);

		}
		return Long.valueOf(0);
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

	/**
	 * @param scheduleDetail
	 * @return
	 */
	public final static BigDecimal getPaymentDueBySchedule(FinanceScheduleDetail scheduleDetail) {
		BigDecimal paidAmount = BigDecimal.ZERO;
		if (scheduleDetail == null) {
			return paidAmount;
		}
		paidAmount = paidAmount.add(scheduleDetail.getProfitSchd().add(scheduleDetail.getPrincipalSchd()));
		paidAmount = paidAmount.subtract(scheduleDetail.getSchdPftPaid().add(scheduleDetail.getSchdPriPaid()));
		paidAmount = paidAmount.add(scheduleDetail.getFeeSchd().subtract(scheduleDetail.getSchdFeePaid()));
		paidAmount = paidAmount.add(scheduleDetail.getInsSchd().subtract(scheduleDetail.getSchdInsPaid()));
		paidAmount = paidAmount.add(scheduleDetail.getSuplRent().subtract(scheduleDetail.getSuplRentPaid()));
		paidAmount = paidAmount.add(scheduleDetail.getIncrCost().subtract(scheduleDetail.getIncrCostPaid()));

		return paidAmount;
	}

	public BigDecimal getDecimal(ResultSet resultSet, String name) throws SQLException {
		BigDecimal val = resultSet.getBigDecimal(name);
		if (val == null) {
			val = BigDecimal.ZERO;
		}
		return val;
	}
	
	public int getIndexFromMap(Map<Date, Integer> datesMap, Date date) {
		Date formatDate = formatDate(date);
		if (datesMap.containsKey(formatDate)) {
			return datesMap.get(formatDate);
		}
		return 0;
	}


	public FinContributorDetailDAO getFinContributorDetailDAO() {
		return finContributorDetailDAO;
	}

	public void setFinContributorDetailDAO(FinContributorDetailDAO finContributorDetailDAO) {
		this.finContributorDetailDAO = finContributorDetailDAO;
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

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
		AccrualService.financeTypeDAO = financeTypeDAO;
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

	public void setdPDBucketConfigurationDAO(DPDBucketConfigurationDAO dPDBucketConfigurationDAO) {
		this.dPDBucketConfigurationDAO = dPDBucketConfigurationDAO;
	}

	public void setdPDBucketDAO(DPDBucketDAO dPDBucketDAO) {
		this.dPDBucketDAO = dPDBucketDAO;
	}

}
