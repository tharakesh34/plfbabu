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
 * FileName    		:  ScoringGroup.java                                                   * 	  
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

package com.pennant.backend.model.rmtmasters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>ScoringGroup table</b>.<br>
 *
 */
public class ScoringGroup extends AbstractWorkflowEntity implements Entity {

    private static final long serialVersionUID = 3708216428751667675L;
    
	private long scoreGroupId = Long.MIN_VALUE;
	private String scoreGroupCode;
	private String scoreGroupName;
	private String categoryType;
	private int minScore;
	private boolean isOverride;
	private int overrideScore;
	private boolean newRecord;
	private String lovValue;
	private ScoringGroup befImage;
	private LoggedInUser userDetails;

	private long lovDescTotRetailScorPoints=0;
	private long lovDescTotFinScorPoints=0;
	private long lovDescTotNFScorPoints=0;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private List<ScoringSlab>             scoringSlabList = null;
	private List<ScoringMetrics>          scoringMetricsList = null;
	private List<ScoringMetrics>          finScoringMetricsList = null;
	private List<ScoringMetrics>          nonFinScoringMetricsList = null;
	private Map<Long,List<ScoringMetrics>> lovDescFinScoreMap = new HashMap<Long, List<ScoringMetrics>>();
	
	public boolean isNew() {
		return isNewRecord();
	}

	public ScoringGroup() {
		super();
	}

	public ScoringGroup(long id) {
		super();
		this.setId(id);
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public long getId() {
		return scoreGroupId;
	}
	public void setId (long id) {
		this.scoreGroupId = id;
	}
	
	public long getScoreGroupId() {
		return scoreGroupId;
	}
	public void setScoreGroupId(long scoreGroupId) {
		this.scoreGroupId = scoreGroupId;
	}
	
	public String getScoreGroupCode() {
		return scoreGroupCode;
	}
	public void setScoreGroupCode(String scoreGroupCode) {
		this.scoreGroupCode = scoreGroupCode;
	}
	
	public String getScoreGroupName() {
		return scoreGroupName;
	}
	public void setScoreGroupName(String scoreGroupName) {
		this.scoreGroupName = scoreGroupName;
	}
	
	public void setCategoryType(String categoryType) {
	    this.categoryType = categoryType;
    }
	public String getCategoryType() {
	    return categoryType;
    }

	public int getMinScore() {
		return minScore;
	}
	public void setMinScore(int minScore) {
		this.minScore = minScore;
	}
	
	public boolean isIsOverride() {
		return isOverride;
	}
	public void setIsOverride(boolean isOverride) {
		this.isOverride = isOverride;
	}
	
	public int getOverrideScore() {
		return overrideScore;
	}
	public void setOverrideScore(int overrideScore) {
		this.overrideScore = overrideScore;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	public String getLovValue() {
		return lovValue;
	}
	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public ScoringGroup getBefImage(){
		return this.befImage;
	}
	public void setBefImage(ScoringGroup beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setScoringSlabList(List<ScoringSlab> scoringSlabList) {
		this.scoringSlabList = scoringSlabList;
	}
	public List<ScoringSlab> getScoringSlabList() {
		return scoringSlabList;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}
	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setScoringMetricsList(List<ScoringMetrics> scoringMetricsList) {
		this.scoringMetricsList = scoringMetricsList;
	}
	public List<ScoringMetrics> getScoringMetricsList() {
		return scoringMetricsList;
	}

	public long getLovDescTotRetailScorPoints() {
    	return lovDescTotRetailScorPoints;
    }
	public void setLovDescTotRetailScorPoints(long lovDescTotRetailScorPoints) {
    	this.lovDescTotRetailScorPoints = lovDescTotRetailScorPoints;
    }

	public long getLovDescTotFinScorPoints() {
    	return lovDescTotFinScorPoints;
    }
	public void setLovDescTotFinScorPoints(long lovDescTotFinScorPoints) {
    	this.lovDescTotFinScorPoints = lovDescTotFinScorPoints;
    }

	public long getLovDescTotNFScorPoints() {
    	return lovDescTotNFScorPoints;
    }
	public void setLovDescTotNFScorPoints(long lovDescTotNFScorPoints) {
    	this.lovDescTotNFScorPoints = lovDescTotNFScorPoints;
    }

	public List<ScoringMetrics> getFinScoringMetricsList() {
    	return finScoringMetricsList;
    }
	public void setFinScoringMetricsList(List<ScoringMetrics> finScoringMetricsList) {
    	this.finScoringMetricsList = finScoringMetricsList;
    }

	public List<ScoringMetrics> getNonFinScoringMetricsList() {
    	return nonFinScoringMetricsList;
    }
	public void setNonFinScoringMetricsList(List<ScoringMetrics> nonFinScoringMetricsList) {
    	this.nonFinScoringMetricsList = nonFinScoringMetricsList;
    }

	public Map<Long, List<ScoringMetrics>> getLovDescFinScoreMap() {
    	return lovDescFinScoreMap;
    }
	public void setLovDescFinScoreMap(Map<Long, List<ScoringMetrics>> lovDescFinScoreMap) {
    	this.lovDescFinScoreMap = lovDescFinScoreMap;
    }

	
}
