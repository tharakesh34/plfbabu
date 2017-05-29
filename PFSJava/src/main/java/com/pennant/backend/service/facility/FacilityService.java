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
 * FileName    		:  FacilityService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  25-11-2013    														*
 *                                                                  						*
 * Modified Date    :  25-11-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 25-11-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.facility;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.Collateral;
import com.pennant.backend.model.collateral.FacilityDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.facility.Facility;

public interface FacilityService {

	Facility getFacility();
	Facility getNewFacility();
	
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	Facility getFacilityById( String id);
	Facility getApprovedFacilityById( String id);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	Facility getFacilityChildRecords(Facility facility);
	Facility setFacilityScoringDetails(Facility facility);
	CustomerEligibilityCheck getCustomerEligibility(Customer customer,long custID);
	Collateral getNewCollateral();
	FacilityDetail getNewFacilityDetail();
	List<CustomerRating> getCustomerRatingByCustomer(long custId);
	boolean doCheckBlackListedCustomer(AuditHeader auditHeader);
	Facility getTotalAmountsInUSDAndBHD(Facility facility);
	Facility setCustomerDocuments(Facility facility);
	boolean checkFirstTaskOwnerAccess(long loginUsrID);
	String getActualLevelAprroval(Facility facility);
	Facility getLatestFacilityByCustID(long custID);
}