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
 *******************************************************************************************************
 * FILE HEADER *
 *******************************************************************************************************
 *
 * FileName : ScheduleCalculator.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 10-05-2018 *
 * 
 * Description : *
 * 
 ********************************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************************
 * 26-04-2011 Pennant 0.1 * * 10-05-2018 Satya 0.2 PSD - Ticket : 126189 * While doing Add Disbursement getting *
 * ArthemeticException in AccrualService due to * NoofDays is ZERO in newly added Schedule * 01-08-2018 Mangapathi 0.3
 * PSD - Ticket : 125445, 125588 * Mail Sub : Freezing Period, Dt : 30-May-2018 * To address Freezing period case when
 * schedule * term is in Presentment. * *
 * 
 * 05-12-2018 Pradeep Varma 0.4 Schedules sent for presentment should and * waiting for fate should be untouched for any
 * * schedule change * 05-12-2018 Pradeep Varma 0.5 Interest should not be left for future * adjustments based on loan
 * type flag * schedule change * 05-12-2018 Pradeep Varma 0.6 Adjut Terms while Rate Change * * *
 ********************************************************************************************************
 */
package com.pennant.app.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennanttech.pennapps.core.util.DateUtil;

public class LookupMethods {
	// PROCESS METHODS IN SCHEDULE CALCULATOR
	public static final int LT = -2; // less than
	public static final int LE = -1; // less than or equal to
	public static final int EQ = 0; // equal to
	public static final int GE = 1; // greater than or equal to
	public static final int GT = 2; // greater than

	private LookupMethods() {
		super();
	}

	/**
	 * Searches for a date in a sorted list of dates based on a search filter condition.
	 *
	 * @param fsdList      The sorted list of finance schedule details by schedule date to search in.
	 * @param searchDate   The date to search for.
	 * @param searchFilter The search filter condition. Can be one of the constants defined in this class (LT, LE, EQ,
	 *                     GE, GT).
	 * @return Index of the FSD in the list that matches the search filter condition and is closest to the search date.
	 *         Returns -1 if no such date is found.
	 */

	public static int lookupFSD(List<FinanceScheduleDetail> fsdList, Date schDate, int searchFilter) {
		int iLow = 0;
		int iHigh = fsdList.size() - 1;
		int iReturn = -1;

		if (fsdList == null || fsdList.size() == 0 || schDate == null) {
			return iReturn;
		}

		if (!isValidSearchFilter(searchFilter)) {
			return iReturn;
		}

		// Loop while lower index is less than or equal to the higher index
		while (iLow <= iHigh) {

			// Find mid index
			int iMid = (iLow + iHigh) / 2;

			// Find if requested date found in the mid
			int cmp = DateUtil.compare(schDate, fsdList.get(iMid).getSchDate());

			// search date is less than the mid index element. So next search limit to the bottom portion
			if (cmp < 0) {
				iHigh = iMid - 1;
				if (searchFilter == LT || searchFilter == LE) {
					iReturn = iMid;
				}

				// search date is greater than the mid index element. So next search limit to the upper portion
			} else if (cmp > 0) {
				iLow = iMid + 1;
				if (searchFilter == GT || searchFilter == GE) {
					iReturn = iMid;
				}

				// search date exactly matches the mid index element. So exit
			} else {
				iReturn = iMid;
				if (searchFilter == EQ || searchFilter == LE || searchFilter == GE) {
					break;
				}
			}
		}

		if (iReturn >= 0) {
			switch (searchFilter) {
			case LT:
				if (iReturn > 0) {
					iReturn--;
				}
				break;
			case GT:
				if (iReturn < fsdList.size() - 1) {
					iReturn++;
				}
				break;
			default:
				// Do nothing
				break;
			}
		}

		return iReturn;
	}

	public static boolean isValidSearchFilter(int searchFilter) {
		if (searchFilter < LT || searchFilter > GT) {
			return false;
		}

		return true;
	}

	/**
	 * Sort RPD List by value date using selection sort.
	 *
	 * @param rpdList: RPD List to be sorted.
	 * @return rpdList: Sorted RPD List
	 */

	public static List<FinanceRepayments> sortRPDByValueDate(List<FinanceRepayments> rpdList) {

		// If list is not null and list has elements
		if (rpdList != null && rpdList.size() > 0) {

			Collections.sort(rpdList, new Comparator<FinanceRepayments>() {
				@Override
				public int compare(FinanceRepayments detail1, FinanceRepayments detail2) {
					return DateUtil.compare(detail1.getFinSchdDate(), detail2.getFinSchdDate());
				}
			});
		}

		return rpdList;
	}

	public static List<OverdueChargeRecovery> sortODCRByMvtDate(List<OverdueChargeRecovery> odcrList) {

		// If list is not null and list has elements
		if (odcrList != null && odcrList.size() > 0) {

			Collections.sort(odcrList, new Comparator<OverdueChargeRecovery>() {
				@Override
				public int compare(OverdueChargeRecovery detail1, OverdueChargeRecovery detail2) {
					return DateUtil.compare(detail1.getMovementDate(), detail2.getMovementDate());
				}
			});
		}

		return odcrList;
	}

}