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
 * FileName    		:  DPDBucketConfiguration.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-04-2017    														*
 *                                                                  						*
 * Modified Date    :  21-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>DPDBucketConfiguration table</b>.<br>
 *
 */
public class DPDBucketConfiguration extends AbstractWorkflowEntity  implements Entity {
private static final long serialVersionUID = 1L;

	private long configID = Long.MIN_VALUE;
	private String productCode;
	private String productCodeName;
	private long bucketID;
	private String bucketIDName;
	private String bucketCode;
	private int dueDays;
	private boolean suspendProfit;
	private boolean newRecord=false;
	private String lovValue;
	private DPDBucketConfiguration befImage;
	private  LoggedInUser userDetails;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public DPDBucketConfiguration() {
		super();
	}

	public DPDBucketConfiguration(long id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
			excludeFields.add("productCodeName");
			excludeFields.add("bucketIDName");
			excludeFields.add("bucketCode");
	return excludeFields;
	}

	public long getId() {
		return configID;
	}
	
	public void setId (long id) {
		this.configID = id;
	}
	public long getConfigID() {
		return configID;
	}
	public void setConfigID(long configID) {
		this.configID = configID;
	}
	
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getProductCodeName() {
		return this.productCodeName;
	}

	public void setProductCodeName (String productCodeName) {
		this.productCodeName = productCodeName;
	}
	
	public long getBucketID() {
		return bucketID;
	}
	public void setBucketID(long bucketID) {
		this.bucketID = bucketID;
	}
	public String getBucketIDName() {
		return this.bucketIDName;
	}

	public void setBucketIDName (String bucketIDName) {
		this.bucketIDName = bucketIDName;
	}
	
	public int getDueDays() {
		return dueDays;
	}
	public void setDueDays(int dueDays) {
		this.dueDays = dueDays;
	}
	
	public boolean isSuspendProfit() {
		return suspendProfit;
	}
	public void setSuspendProfit(boolean suspendProfit) {
		this.suspendProfit = suspendProfit;
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

	public DPDBucketConfiguration getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(DPDBucketConfiguration beforeImage){
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

	public String getBucketCode() {
		return bucketCode;
	}

	public void setBucketCode(String bucketCode) {
		this.bucketCode = bucketCode;
	}
}
