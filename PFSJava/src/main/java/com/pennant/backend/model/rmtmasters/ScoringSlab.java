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
 * FileName    		:  ScoringSlab.java                                                   * 	  
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

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>ScoringSlab table</b>.<br>
 *
 */
public class ScoringSlab extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;
	private long scoreGroupId = Long.MIN_VALUE;
	private long scoringSlab;
	private String lovDescScoreGroupCode;
	private String creditWorthness;
	private boolean newRecord=false;
	private String lovValue;
	private ScoringSlab befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public ScoringSlab() {
		super();
	}

	public ScoringSlab(long id) {
		super();
		this.setId(id);
	}

	//Getter and Setter methods

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




	public long getScoringSlab() {
		return scoringSlab;
	}
	public void setScoringSlab(long scoringSlab) {
		this.scoringSlab = scoringSlab;
	}




	public String getCreditWorthness() {
		return creditWorthness;
	}
	public void setCreditWorthness(String creditWorthness) {
		this.creditWorthness = creditWorthness;
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

	public ScoringSlab getBefImage(){
		return this.befImage;
	}

	public void setBefImage(ScoringSlab beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setLovDescScoreGroupCode(String lovDescScoreGroupCode) {
		this.lovDescScoreGroupCode = lovDescScoreGroupCode;
	}

	public String getLovDescScoreGroupCode() {
		return lovDescScoreGroupCode;
	}
}
