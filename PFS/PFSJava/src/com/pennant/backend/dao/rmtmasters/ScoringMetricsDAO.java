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
 * FileName    		:  ScoringMetricsDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-12-2011    														*
 *                                                                  						*
 * Modified Date    :  05-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.rmtmasters;
import java.util.List;

import com.pennant.backend.model.rmtmasters.ScoringMetrics;

public interface ScoringMetricsDAO {

	public ScoringMetrics getScoringMetrics();
	public ScoringMetrics getNewScoringMetrics();
	public ScoringMetrics getScoringMetricsById(String id,String type);
	public void update(ScoringMetrics scoringMetrics,String type);
	public void delete(ScoringMetrics scoringMetrics,String type);
	public String save(ScoringMetrics scoringMetrics,String type);
	public void initialize(ScoringMetrics scoringMetrics);
	public void refresh(ScoringMetrics scoringMetrics);
	public List<ScoringMetrics> getScoringMetricsByScoreGrpId(final long scoreGrpId, String categoryType, String type);
	public void delete(long scoreGroupId , String type);
	
}