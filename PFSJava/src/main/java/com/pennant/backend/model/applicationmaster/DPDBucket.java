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
 * FileName    		:  DPDBucket.java                                                   * 	  
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

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>DPDBucket table</b>.<br>
 *
 */
@XmlType(propOrder = {"bucketID","bucketCode","bucketDesc","active"})
@XmlAccessorType(XmlAccessType.FIELD)
public class DPDBucket extends AbstractWorkflowEntity  implements Entity {
private static final long serialVersionUID = 1L;

	private long bucketID = Long.MIN_VALUE;
	private String bucketCode;
	private String bucketDesc;
	private boolean active;
	@XmlTransient
	private boolean newRecord=false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private DPDBucket befImage;
	@XmlTransient
	private  LoggedInUser userDetails;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public DPDBucket() {
		super();
	}

	public DPDBucket(long id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
	return excludeFields;
	}

	public long getId() {
		return bucketID;
	}
	
	public void setId (long id) {
		this.bucketID = id;
	}
	public long getBucketID() {
		return bucketID;
	}
	public void setBucketID(long bucketID) {
		this.bucketID = bucketID;
	}
	
	public String getBucketCode() {
		return bucketCode;
	}
	public void setBucketCode(String bucketCode) {
		this.bucketCode = bucketCode;
	}
	
	public String getBucketDesc() {
		return bucketDesc;
	}
	public void setBucketDesc(String bucketDesc) {
		this.bucketDesc = bucketDesc;
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

	public DPDBucket getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(DPDBucket beforeImage){
		this.befImage=beforeImage;
	}

	public  LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails( LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

}
