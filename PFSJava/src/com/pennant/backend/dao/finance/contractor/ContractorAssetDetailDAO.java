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
 * FileName    		:  ContractorAssetDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-09-2013    														*
 *                                                                  						*
 * Modified Date    :  27-09-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-09-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance.contractor;
import java.util.List;

import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;

public interface ContractorAssetDetailDAO {

	public ContractorAssetDetail getContractorAssetDetail();
	public ContractorAssetDetail getNewContractorAssetDetail();
	public ContractorAssetDetail getContractorAssetDetailById(String finReference, long contractorId, String type);
	public List<ContractorAssetDetail> getContractorDetailDetailByFinRef(String id, String type);
	public void update(ContractorAssetDetail contractorAssetDetail,String type);
	public void delete(ContractorAssetDetail contractorAssetDetail,String type);
	public String save(ContractorAssetDetail contractorAssetDetail,String type);
	public void initialize(ContractorAssetDetail contractorAssetDetail);
	public void refresh(ContractorAssetDetail entity);
	public void deleteByFinRef(String finReference, String type);
}