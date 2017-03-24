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
 * FileName    		:  RepaymentService.java													*                           
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;

public class StatusService implements Serializable {

	private static final long			serialVersionUID	= 4165353615228874397L;
	private static Logger				logger				= Logger.getLogger(StatusService.class);

	private CustomerStatusCodeDAO		customerStatusCodeDAO;
	private FinanceMainDAO				financeMainDAO;
	private FinStatusDetailDAO			finStatusDetailDAO;
	private FinanceSuspHeadDAO			financeSuspHeadDAO;
	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private FinODDetailsDAO				finODDetailsDAO;

	public StatusService() {
		super();
	}

	public void processStatus(Date date, FinRepayQueue finRepayQueue) {
		logger.debug(" Entering ");

		checkMaturity(finRepayQueue.getFinReference());

		String curFinStatus = customerStatusCodeDAO.getFinanceStatus(finRepayQueue.getFinReference(), true);
		String finStsReason = finRepayQueue.getFinStsReason();
		boolean isStsChanged = false;

		if (!StringUtils.trimToEmpty(finRepayQueue.getFinStatus()).equals(curFinStatus)) {
			isStsChanged = true;
		}

		if (isStsChanged) {

			financeMainDAO.updateStatus(finRepayQueue.getFinReference(), curFinStatus,
					FinanceConstants.FINSTSRSN_SYSTEM);
			FinStatusDetail statusDetail = new FinStatusDetail();
			statusDetail.setFinReference(finRepayQueue.getFinReference());
			statusDetail.setValueDate(date);
			statusDetail.setCustId(finRepayQueue.getCustomerID());
			statusDetail.setFinStatus(curFinStatus);
			statusDetail.setFinStatusReason(finStsReason);

			finStatusDetailDAO.saveOrUpdateFinStatus(statusDetail);

		}

		// Customer Status Update  which depends on Finance Status
		List<FinStatusDetail> custStatuses = finStatusDetailDAO.getFinStatusDetailList(date);
		if (custStatuses != null && custStatuses.size() > 0) {

			// Customer Status Date Updation
			List<Long> custIdList = new ArrayList<Long>();
			for (FinStatusDetail finSts : custStatuses) {
				custIdList.add(finSts.getCustId());
			}

			List<FinStatusDetail> suspDateStsList = financeSuspHeadDAO.getCustSuspDate(custIdList);
			Map<Long, Date> suspDateMap = new HashMap<Long, Date>();
			for (FinStatusDetail suspDatests : suspDateStsList) {
				suspDateMap.put(suspDatests.getCustId(), suspDatests.getValueDate());
			}

			for (FinStatusDetail finSts : custStatuses) {
				if (suspDateMap.containsKey(finSts.getCustId())) {
					finSts.setValueDate(suspDateMap.get(finSts.getCustId()));
				} else {
					finSts.setValueDate(null);
				}
			}

			finStatusDetailDAO.updateCustStatuses(custStatuses);

		}

	}

	/**
	 * @param finReference
	 */
	private void checkMaturity(String finReference) {

		FinanceScheduleDetail totSchd = financeScheduleDetailDAO.getTotals(finReference);
		FinODDetails totOd = finODDetailsDAO.getTotals(finReference);

		BigDecimal totBal = getValue(totSchd.getPrincipalSchd()).subtract(getValue(totSchd.getSchdPriPaid()));
		totBal = totBal.add(getValue((totSchd.getProfitSchd()).subtract(getValue(totSchd.getSchdPftPaid()))));
		totBal = totBal.add(getValue(totOd.getTotPenaltyAmt()).subtract(getValue(totOd.getTotPenaltyPaid())));

		if (totBal.compareTo(BigDecimal.ZERO) <= 0) {
			// update Closing status
			financeMainDAO.updateMaturity(finReference, FinanceConstants.CLOSE_STATUS_MATURED, false);
		}

	}

	/**
	 * @param value
	 * @return
	 */
	private BigDecimal getValue(BigDecimal value) {
		if (value == null) {
			return BigDecimal.ZERO;
		} else {
			return value;
		}
	}

	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
		this.customerStatusCodeDAO = customerStatusCodeDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinStatusDetailDAO(FinStatusDetailDAO finStatusDetailDAO) {
		this.finStatusDetailDAO = finStatusDetailDAO;
	}

	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

}
