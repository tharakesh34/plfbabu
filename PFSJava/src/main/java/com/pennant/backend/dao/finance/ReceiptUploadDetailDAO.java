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
 * FileName    		:  UploadHeaderDAO.java                                                 * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2017    														*
 *                                                                  						*
 * Modified Date    :  17-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-12-2017       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;

public interface ReceiptUploadDetailDAO {

	long save(ReceiptUploadDetail receiptUploadDetail);

	void delete(long id);

	List<ReceiptUploadDetail> getUploadReceiptDetails(long id, boolean getsuccessRecords);

	void updateStatus(ReceiptUploadDetail receiptUploadDetailList);

	void updateReceiptId(long uploadDetailId, long receiptID);

	void updateRejectStatusById(String id, String errorMsg);

	String getLoanReferenc(String finReference, String fileName);

	List<Long> getListofReceiptUploadDetails(long uploadHeaderId);

	ReceiptUploadDetail getUploadReceiptDetail(long headerID, long detailID);

	List<Long> getReceiptDetails(List<Long> list);

	ReceiptUploadDetail getUploadReceiptDetail(long detailID);

}