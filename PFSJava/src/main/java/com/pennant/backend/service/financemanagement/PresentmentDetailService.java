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
 *																							*
 * FileName    		:  presentmentDetailService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-05-2017    														*
 *                                                                  						*
 * Modified Date    :  01-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-05-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.financemanagement;

import java.util.List;

import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public interface PresentmentDetailService {

	PresentmentHeader getPresentmentHeader(long id);

	String savePresentmentDetails(PresentmentHeader presentmentHeader) throws Exception;

	List<PresentmentDetail> getPresentmentDetailsList(long presentmentId, boolean isExclude, boolean isApprove, String type);

	void updatePresentmentDetails(List<Long> excludeList, List<Long> includeList, String userAction, long presentmentId, long partnerBankId, LoggedInUser loggedInUser, boolean isPDC) throws Exception;

	PresentmentDetail presentmentCancellation(String presentmentRef, String bounceCode) throws Exception;
	
	void updatePresentmentDetails(String presentmentRef, String status, long bounceId, long manualAdviseId, String errorDesc);

	void updatePresentmentDetails(String presentmentRef, String status, String errorCode, String errorDesc);

	void updatePresentmentIdAsZero(long presentmentId);

	void updateFinanceDetails(String presentmentRef);
	
	long getSeqNumber(String tableNme);
	
	void processReceipts(PresentmentDetail detail, LoggedInUser userDetails) throws Exception;

	String getPaymenyMode(String presentmentRef);

	PresentmentDetail getPresentmentDetailsByMode(String presentmentRef, String paymentMode);
	
	void processReceipts(PresentmentDetail presentmentDetail) throws Exception;


}