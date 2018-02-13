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
 * FileName    		:  LimitCheckFilterQuery.java                                           * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-07-2016    														*
 *                                                                  						*
 * Modified Date    :  23-07-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.rulefactory;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>DedupParm table</b>.<br>
 *
 */
public class LimitFilterQuery extends AbstractWorkflowEntity implements Entity{

	private static final long serialVersionUID = -5474091857496782851L;
	
	private long queryId = Long.MIN_VALUE;
	private String queryCode = null;
	private String queryDesc;
	private String queryModule;
	private String querySubCode;
	private String sQLQuery;
	private String actualBlock;
	private boolean active;
	private boolean newRecord=false;
	private String lovValue;
	private LimitFilterQuery befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public LimitFilterQuery() {
		super();
	}

	public LimitFilterQuery(long id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public long getId() {
		return queryId;
	}
	public void setId (long id) {
		this.queryId = id;
	}
	
	public long getQueryId() {
	    return queryId;
    }
	public void setQueryId(long queryId) {
	    this.queryId = queryId;
    }

	public String getQueryCode() {
		return queryCode;
	}
	public void setQueryCode(String queryCode) {
		this.queryCode = queryCode;
	}
	
	public String getQueryDesc() {
		return queryDesc;
	}

	public void setQueryDesc(String queryDesc) {
		this.queryDesc = queryDesc;
	}

	public String getQueryModule() {
		return queryModule;
	}
	public void setQueryModule(String queryModule) {
		this.queryModule = queryModule;
	}
	
	public void setQuerySubCode(String querySubCode) {
		this.querySubCode = querySubCode;
	}
	public String getQuerySubCode() {
		return querySubCode;
	}
	
	public String getSQLQuery() {
		return sQLQuery;
	}
	public void setSQLQuery(String sQLQuery) {
		this.sQLQuery = sQLQuery;
	}
	
	public String getActualBlock() {
		return actualBlock;
	}
	public void setActualBlock(String actualBlock) {
		this.actualBlock = actualBlock;
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

	public LimitFilterQuery getBefImage(){
		return this.befImage;
	}
	public void setBefImage(LimitFilterQuery beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	
}
