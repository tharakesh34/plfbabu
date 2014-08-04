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

	public FacilityReferenceDetail getFacilityReferenceDetail();
	public FacilityReferenceDetail getNewFacilityReferenceDetail();
	public FacilityReferenceDetail getFacilityReferenceDetailById(long id,String type);
	public void update(FacilityReferenceDetail FacilityReferenceDetail,String type);
	public void delete(FacilityReferenceDetail FacilityReferenceDetail,String type);
	public long save(FacilityReferenceDetail FacilityReferenceDetail,String type);
	public void initialize(FacilityReferenceDetail FacilityReferenceDetail);
	public void refresh(FacilityReferenceDetail entity);
	public List<FacilityReferenceDetail> getFacilityReferenceDetail(String financeType,String roleCode, String type);
	public List<FacilityReferenceDetail> getFinRefDetByRoleAndFinType(String financeType,
			String MandInputInStage, List<String> groupIds, String type);
	public void deleteByFinType(String finType, String type);
	public List<FacilityReferenceDetail> getFacilityReferenceDetailById(String finType);
}