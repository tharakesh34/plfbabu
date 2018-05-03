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
 * FileName    		:  FinAgreementDetailDAO.java                                           * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  25-02-2013    														*
 *                                                                  						*
 * Modified Date    :  25-02-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinAgreementDetail;

public interface FinAgreementDetailDAO {

	FinAgreementDetail getFinAgreementDetail();
	FinAgreementDetail getNewFinAgreementDetail();
	FinAgreementDetail getFinAgreementDetailById(String finReference, long agrId, String type);
	long save(FinAgreementDetail finAgreementDetail, String type);
	void update(FinAgreementDetail finAgreementDetail, String type);
	List<FinAgreementDetail> getFinAgreementDetailList(String finReference,String finType,
			boolean isAgrsExist, String agrIds, String type);
	void delete(FinAgreementDetail agreementDetail, String type);
	void deleteByFinRef(String finReference, String type);
	List<FinAgreementDetail> getFinAgrByFinRef(String finReference, String type);
}
