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
 * FileName    		:  PresentmentHeaderDAO.java                                                   * 	  
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
package com.pennant.backend.dao.financemanagement;

import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennanttech.pff.core.TableType;

public interface PresentmentHeaderDAO extends BasicCrudDao<PresentmentHeader> {

	PresentmentHeader getPresentmentHeader(long id, String type);
	
	boolean isDuplicateKey(long id, String reference, TableType tableType);

	long save(PresentmentDetail presentmentDetail, TableType tableType);

	long getSeqNumber(String tableName);

	ResultSet getPresentmentDetails(PresentmentHeader detailHeader) throws Exception;

	long savePresentmentHeader(PresentmentHeader presentmentHeader);

	void updatePresentmentDetailId(long presentmentId, List<Long> detaildList) throws Exception;

	void updatePresentmentDetailId(long presentmentId, long extractId);

	List<PresentmentDetail> getPresentmentDetailsList(long presentmentId, boolean isExclude, String type);

	void updatePresentmentDetials(long presentmentId, List<Long> list, int mnualExclude);

	void updatePresentmentHeader(long presentmentId, int pexcBatchCreated, long partnerBankId);

	void updateFinScheduleDetails(long id, String finReference, Date schDate, int schSeq);

}