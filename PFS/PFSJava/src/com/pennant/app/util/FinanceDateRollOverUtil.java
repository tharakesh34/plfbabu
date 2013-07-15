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
 * 
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  FinanceDateRollOverUtil.java													*                           
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
package com.pennant.app.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zkplus.spring.SpringUtil;

import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;

public class FinanceDateRollOverUtil implements Serializable {

    private static final long serialVersionUID = 3074291666770414426L;
    
	private static FinanceMainDAO	financeMainDAO;
	private static boolean	      isChanged	= false;

	public static void doDateRollOver() {

		ArrayList<FinanceMain> finMainToUpdate = new ArrayList<FinanceMain>();
		JdbcSearchObject<FinanceMain> jdbcSearchObject = new JdbcSearchObject<FinanceMain>(FinanceMain.class);
		jdbcSearchObject.addTabelName("FinanceMain");
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		List<FinanceMain> financeMains = pagedListService.getBySearchObject(jdbcSearchObject);
		for (FinanceMain financeMain : financeMains) {
			isChanged = false;
			financeMain = changeRepayDates(financeMain);
			financeMain = changeProfitDates(financeMain);
			financeMain = changeReviewDates(financeMain);
			financeMain = changeCaptalizationDates(financeMain);
			if (isChanged) {
				finMainToUpdate.add(financeMain);
			}
		}

		if (finMainToUpdate.size() > 0) {
			getFinanceMainDAO().listUpdate(finMainToUpdate, "");
		}

	}

	private static FinanceMain changeRepayDates(FinanceMain financeMain) {

		if (financeMain.getNextRepayDate().compareTo((Date) SystemParameterDetails.getSystemParameterValue("APP_DATE")) >= 0
		        && financeMain.getNextRepayDate().compareTo((Date) SystemParameterDetails.getSystemParameterValue("APP_NEXT_BUS_DATE")) < 0) {

			financeMain.setLastRepayDate(financeMain.getNextRepayDate());
			Date nextdate = FrequencyUtil.getNextDate(financeMain.getRepayFrq(), 1, financeMain.getNextRepayDate(), HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate();
			financeMain.setNextRepayDate(nextdate);
			isChanged = true;
		}
		return financeMain;
	}

	private static FinanceMain changeProfitDates(FinanceMain financeMain) {
		if (financeMain.getNextRepayPftDate().compareTo((Date) SystemParameterDetails.getSystemParameterValue("APP_DATE")) >= 0
		        && financeMain.getNextRepayPftDate().compareTo((Date) SystemParameterDetails.getSystemParameterValue("APP_NEXT_BUS_DATE")) < 0) {

			financeMain.setLastRepayPftDate(financeMain.getNextRepayPftDate());
			Date nextdate = FrequencyUtil.getNextDate(financeMain.getRepayPftFrq(), 1, financeMain.getNextRepayPftDate(), HolidayHandlerTypes.MOVE_NONE, false)
			        .getNextFrequencyDate();
			financeMain.setNextRepayPftDate(nextdate);
			isChanged = true;
		}
		return financeMain;
	}

	private static FinanceMain changeReviewDates(FinanceMain financeMain) {
		if (financeMain.getNextRepayRvwDate().compareTo((Date) SystemParameterDetails.getSystemParameterValue("APP_DATE")) >= 0
		        && financeMain.getNextRepayRvwDate().compareTo((Date) SystemParameterDetails.getSystemParameterValue("APP_NEXT_BUS_DATE")) < 0) {

			financeMain.setLastRepayRvwDate(financeMain.getNextRepayRvwDate());
			Date nextdate = FrequencyUtil.getNextDate(financeMain.getRepayRvwFrq(), 1, financeMain.getNextRepayRvwDate(), HolidayHandlerTypes.MOVE_NONE, false)
			        .getNextFrequencyDate();
			financeMain.setNextRepayRvwDate(nextdate);
			isChanged = true;
		}
		return financeMain;
	}

	private static FinanceMain changeCaptalizationDates(FinanceMain financeMain) {
		if (financeMain.getNextRepayCpzDate().compareTo((Date) SystemParameterDetails.getSystemParameterValue("APP_DATE")) >= 0
		        && financeMain.getNextRepayCpzDate().compareTo((Date) SystemParameterDetails.getSystemParameterValue("APP_NEXT_BUS_DATE")) < 0) {

			financeMain.setLastRepayCpzDate(financeMain.getNextRepayCpzDate());
			Date nextdate = FrequencyUtil.getNextDate(financeMain.getRepayCpzFrq(), 1, financeMain.getNextRepayCpzDate(), HolidayHandlerTypes.MOVE_NONE, false)
			        .getNextFrequencyDate();
			financeMain.setNextRepayCpzDate(nextdate);
			isChanged = true;
		}
		return financeMain;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		FinanceDateRollOverUtil.financeMainDAO = financeMainDAO;
	}
	private static FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

}
