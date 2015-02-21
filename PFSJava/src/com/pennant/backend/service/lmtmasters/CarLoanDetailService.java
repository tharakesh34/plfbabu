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
 * FileName    		:  CarLoanDetailService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.lmtmasters;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.CarLoanDetail;

/**
 * Service declaration for methods that depends on <b>CarLoanDetail</b>.<br>
 * 
 */
public interface CarLoanDetailService {
	CarLoanDetail getCarLoanDetail();
	CarLoanDetail getNewCarLoanDetail();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	CarLoanDetail getCarLoanDetailById(String loanRef,int itemNumber);
	CarLoanDetail getApprovedCarLoanDetailById(String loanRef,int ItemNumber);
	CarLoanDetail refresh(CarLoanDetail carLoanDetail);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	AuditDetail saveOrUpdate(CarLoanDetail carLoanDetail, String tableType, String auditTranType);
	AuditDetail doApprove(CarLoanDetail carLoanDetail, String tableType, String auditTranType);
	AuditDetail validate(CarLoanDetail carLoanDetail, String method, String auditTranType, String  usrLanguage);
	AuditDetail delete(CarLoanDetail carLoanDetail, String tableType, String auditTranType);
	//Fleet Vehicle Finance 
	CarLoanDetail getVehicleLoanDetailById(String id);
	List<AuditDetail> saveOrUpdate(List<CarLoanDetail> vehicleLoanDetailList, String tableType, String auditTranType);
	List<AuditDetail> doApprove(List<CarLoanDetail> vehicleLoanDetailList, String tableType, String auditTranType);
	List<AuditDetail> validate(List<CarLoanDetail> vehicleLoanDetailList, long workflowId, String method, String auditTranType, String  usrLanguage);
	List<AuditDetail> delete(List<CarLoanDetail> vehicleLoanDetailList, String tableType, String auditTranType);
}