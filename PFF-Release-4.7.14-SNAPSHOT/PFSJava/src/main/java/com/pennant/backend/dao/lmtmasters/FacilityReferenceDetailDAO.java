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
 * FileName    		:  FacilityReferenceDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-11-2011    														*
 *                                                                  						*
 * Modified Date    :  26-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.lmtmasters;
import java.util.List;

import com.pennant.backend.model.lmtmasters.FacilityReferenceDetail;

public interface FacilityReferenceDetailDAO {

	FacilityReferenceDetail getFacilityReferenceDetail();
	FacilityReferenceDetail getNewFacilityReferenceDetail();
	FacilityReferenceDetail getFacilityReferenceDetailById(long id,String type);
	void update(FacilityReferenceDetail facilityReferenceDetail,String type);
	void delete(FacilityReferenceDetail facilityReferenceDetail,String type);
	long save(FacilityReferenceDetail facilityReferenceDetail,String type);
	List<FacilityReferenceDetail> getFacilityReferenceDetail(String financeType,String roleCode, String type);
	List<FacilityReferenceDetail> getFinRefDetByRoleAndFinType(String financeType,
			String mandInputInStage, List<String> groupIds, String type);
	void deleteByFinType(String finType, String type);
	List<FacilityReferenceDetail> getFacilityReferenceDetailById(String finType);
}