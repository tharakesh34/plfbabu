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
 * FileName    		:  SnapShotConfiguration.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-02-2018    														*
 *                                                                  						*
 * Modified Date    :  16-02-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-02-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.eodsnapshot;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>SnapShotConfiguration table</b>.<br>
 *
 */
public class SnapShotConfiguration extends AbstractWorkflowEntity  implements Entity {
private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private int type;
	private String typeName;
	private String fromSchema;
	private String fromTable;
	private String toTable;
	private BigInteger executionOrder= BigInteger.ZERO;
	private int executionType;
	private String executionTypeName;
	private String executionMethod;
	private int clearingType;
	private boolean active;
	private boolean newRecord=false;
	private String lovValue;
	private SnapShotConfiguration befImage;
	private  LoggedInUser userDetails;
	private Timestamp lastRunDate;
	
	private List<SnapShotCondition> conditions= new ArrayList<SnapShotCondition>();
	private List<SnapShotColumn> columns= new ArrayList<SnapShotColumn>();
	
	public boolean isNew() {
		return isNewRecord();
	}

	public SnapShotConfiguration() {
		super();
	}

	public SnapShotConfiguration(long id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
			excludeFields.add("typeName");
			excludeFields.add("executionTypeName");
	return excludeFields;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getTypeName() {
		return this.typeName;
	}

	public void setTypeName (String typeName) {
		this.typeName = typeName;
	}
	
	public String getFromSchema() {
		return fromSchema;
	}

	public void setFromSchema(String fromSchema) {
		this.fromSchema = fromSchema;
	}

	public String getFromTable() {
		return fromTable;
	}
	public void setFromTable(String fromTable) {
		this.fromTable = fromTable;
	}
	
	public String getToTable() {
		return toTable;
	}
	public void setToTable(String toTable) {
		this.toTable = toTable;
	}
	
	public BigInteger getExecutionOrder() {
		return executionOrder;
	}
	public void setExecutionOrder(BigInteger executionOrder) {
		this.executionOrder = executionOrder;
	}
	
	public int getExecutionType() {
		return executionType;
	}
	public void setExecutionType(int executionType) {
		this.executionType = executionType;
	}
	public String getExecutionTypeName() {
		return this.executionTypeName;
	}

	public void setExecutionTypeName (String executionTypeName) {
		this.executionTypeName = executionTypeName;
	}
	
	public String getExecutionMethod() {
		return executionMethod;
	}
	public void setExecutionMethod(String executionMethod) {
		this.executionMethod = executionMethod;
	}
	
	public int getClearingType() {
		return clearingType;
	}
	public void setClearingType(int clearingType) {
		this.clearingType= clearingType;
	}

	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
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

	public SnapShotConfiguration getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(SnapShotConfiguration beforeImage){
		this.befImage=beforeImage;
	}

	public  LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails( LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public List<SnapShotCondition> getConditions() {
		return conditions;
	}

	public void setConditions(List<SnapShotCondition> conditions) {
		this.conditions = conditions;
	}

	public List<SnapShotColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<SnapShotColumn> columns) {
		this.columns = columns;
	}

	public Timestamp getLastRunDate() {
		return lastRunDate;
	}

	public void setLastRunDate(Timestamp lastRunDate) {
		this.lastRunDate = lastRunDate;
	}
}
