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
 * FileName    		:  DedupValidation.java													*                           
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
package com.pennant.webui.dedup.dedupparm;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zul.Window;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * Used to specify Date type selection in <b>DedupValidation</b> class.
 */
public class DedupValidation implements Serializable {
	private static final long serialVersionUID = -4728201973665323130L;
	private static final Logger logger = LogManager.getLogger(DedupValidation.class);

	private FinanceReferenceDetailService financeReferenceDetailService;

	/**
	 * Do check and validate all types of dedup
	 * 
	 * @param aFinanceDetail
	 * @param role
	 * @param window
	 * @param curLoginUser
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public boolean doCheckDedup(FinanceDetail aFinanceDetail, String ref, String role, Window window,
			String curLoginUser) throws Exception {
		try {
			// Customer Dedup Process Check
			boolean processCompleted = doCustomerDedupe(aFinanceDetail.getCustomerDetails(),
					aFinanceDetail.getFinScheduleData().getFinanceMain().getFinType(), ref, role, window, curLoginUser);
			if (!processCompleted) {
				return false;
			}
			if (ImplementationConstants.DEDUP_BLACKLIST_COAPP) {
				List<JointAccountDetail> details = aFinanceDetail.getJointAccountDetailList();
				String finType = aFinanceDetail.getFinScheduleData().getFinanceMain().getFinType();
				if (CollectionUtils.isNotEmpty(details)) {
					for (JointAccountDetail coapplicant : details) {
						CustomerDetails customerDetails = coapplicant.getCustomerDetails();
						CustomerDetails customerDedup = FetchCustomerDedupDetails.getCustomerDedup(role,
								customerDetails, window, curLoginUser, finType);

						if (customerDedup.getCustomer().isDedupFound() && !customerDedup.getCustomer().isSkipDedup()) {
							return false;
						}
					}
				}
			}

			//Finance Dedup List Process Checking
			processCompleted = doFinanceDedupe(aFinanceDetail, role, window, curLoginUser);
			if (!processCompleted) {
				return false;
			}

			// Black List Process Check
			processCompleted = doBlacklistCheck(aFinanceDetail, role, window, curLoginUser);
			if (!processCompleted) {
				return false;
			}

			//Returned Cheque List
			processCompleted = doReturnChequeDedupe(aFinanceDetail, role, window);
			if (!processCompleted) {
				return false;
			}

			return true;

		} catch (InterfaceException pfe) {
			MessageUtil.showError(pfe);
			return false;
		}
	}

	/**
	 * Method for Process Checking of Customer Dedup Details
	 * 
	 * @param aFinanceDetail
	 * @param role
	 * @param window
	 * @param curLoginUser
	 * @return
	 * @throws Exception
	 */
	private boolean doCustomerDedupe(CustomerDetails details, String finType, String ref, String role, Window window,
			String curLoginUser) throws Exception {
		logger.debug("Entering");

		String corebank = details.getCustomer().getCustCoreBank();

		//If Core Bank ID is Exists then Customer is already existed in Core Banking System
		if ("Y".equalsIgnoreCase(SysParamUtil.getValueAsString("POSIDEX_DEDUP_REQD"))) {
			if (StringUtils.isBlank(corebank)) {
				details = FetchFinCustomerDedupDetails.getFinCustomerDedup(role, finType, ref, details, window,
						curLoginUser);

				if (details.getCustomer().isDedupFound() && !details.getCustomer().isSkipDedup()) {
					return false;
				} else {
					return true;
				}
			}

		}
		logger.debug("Leaving");
		return true;
	}

	/**
	 * Method for Process Checking of Finance Dedup Details
	 * 
	 * @param aFinanceDetail
	 * @param role
	 * @param window
	 * @param curLoginUser
	 * @return
	 */
	private boolean doFinanceDedupe(FinanceDetail aFinanceDetail, String role, Window window, String curLoginUser) {

		boolean isProcessCompleted;
		aFinanceDetail = FetchDedupDetails.getLoanDedup(role, aFinanceDetail, window, curLoginUser);

		if (aFinanceDetail.getFinScheduleData().getFinanceMain().isDedupFound()
				&& !aFinanceDetail.getFinScheduleData().getFinanceMain().isSkipDedup()) {
			isProcessCompleted = false;
		} else {
			isProcessCompleted = true;
		}
		return isProcessCompleted;
	}

	/**
	 * Method for Process check of Returned Cheque Details
	 * 
	 * @param aFinanceDetail
	 * @param role
	 * @param window
	 * @return
	 */
	private boolean doReturnChequeDedupe(FinanceDetail aFinanceDetail, String role, Window window) {
		logger.debug("Entering");

		boolean isProcessCompleted = true;
		String corebank = aFinanceDetail.getCustomerDetails().getCustomer().getCustCoreBank();

		//Dedupe Check is done if the customer exists in CoreBank
		if (StringUtils.isNotBlank(corebank)) {

			// Return Cheques display or not validation based on Process editor Details
			List<Long> list = getFinanceReferenceDetailService().getRefIdListByFinType(
					aFinanceDetail.getFinScheduleData().getFinanceMain().getFinType(),
					FinanceConstants.FINSER_EVENT_ORG, role, "_TRCView");

			//If List doesnot exists based on conditions , no need to display return cheques window.
			if (list == null || list.isEmpty()) {
				isProcessCompleted = true;
			} else {

				aFinanceDetail = FetchReturnedCheques.getReturnedChequeCustomer(aFinanceDetail, window);

				if (aFinanceDetail.getFinScheduleData().getFinanceMain().isChequeFound()) {
					if (aFinanceDetail.getFinScheduleData().getFinanceMain().isChequeOverride()) {
						isProcessCompleted = true;
					} else {
						isProcessCompleted = false;
					}
				} else {
					isProcessCompleted = true;
				}
			}
		}

		logger.debug("Leaving");
		return isProcessCompleted;
	}

	/**
	 * Method for Checking Process of Black List Details
	 * 
	 * @param aFinanceDetail
	 * @param role
	 * @param window
	 * @param curLoginUser
	 * @throws InterfaceException
	 */
	private boolean doBlacklistCheck(FinanceDetail aFinanceDetail, String role, Window window, String curLoginUser)
			throws InterfaceException {

		boolean isProcessCompleted;

		aFinanceDetail = FetchBlackListDetails.getBlackListCustomers(role, aFinanceDetail, window, curLoginUser);

		if (aFinanceDetail.getFinScheduleData().getFinanceMain().isBlacklisted()) {
			if (aFinanceDetail.getFinScheduleData().getFinanceMain().isBlacklistOverride()) {
				isProcessCompleted = true;
			} else {
				isProcessCompleted = false;
			}
		} else {
			isProcessCompleted = true;
		}

		return isProcessCompleted;
	}

	public FinanceReferenceDetailService getFinanceReferenceDetailService() {
		return financeReferenceDetailService;
	}

	public void setFinanceReferenceDetailService(FinanceReferenceDetailService financeReferenceDetailService) {
		this.financeReferenceDetailService = financeReferenceDetailService;
	}

}
