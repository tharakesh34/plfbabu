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
 * FileName    		:  DefermentDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-12-2011    														*
 *                                                                  						*
 * Modified Date    :  02-12-2011    														*
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
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.DefermentDetail;

public interface DefermentDetailDAO {

	DefermentDetail getDefermentDetail(boolean isWIF);
	DefermentDetail getNewDefermentDetail(boolean isWIF);
	DefermentDetail getDefermentDetailById(String finReference, Date schdDate,String type,boolean isWIF);
	void update(DefermentDetail defermentDetail,String type,boolean isWIF);
	void deleteByFinReference(String id,String type,boolean isWIF, long logKey);
	String save(DefermentDetail defermentDetail,String type,boolean isWIF);
	void initialize(DefermentDetail defermentDetail);
	void refresh(DefermentDetail entity);
	List<DefermentDetail> getDefermentDetails(String id, String type,boolean isWIF);
	List<DefermentDetail> getDefermentDetails(String id, String type,boolean isWIF, long logKey);
	void delete(DefermentDetail defermentDetail,String type,boolean isWIF);
	void saveList(List<DefermentDetail> defermentDetail, String type,boolean isWIF);
	int getFinReferenceCount(String finReference, Date defSchdDate, Date defRpyDate);
	void updateList(List<DefermentDetail> defermentDetail, String type, boolean isWIF);
	void updateBatch(DefermentDetail defermentDetail);
	DefermentDetail getDefermentDetailForBatch(String id, Date schdDate);
}