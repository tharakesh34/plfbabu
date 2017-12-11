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
 * FileName    		:  City.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.licencefileupload;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;


/**
 * Model class for the <b>City table</b>.<br>
 *
 */
public class LicenceFile extends AbstractWorkflowEntity implements Entity {
		
	private static final long serialVersionUID = -306657295035931426L;
	
	private long fileID = Long.MIN_VALUE;
	private String name;
	private byte[] data;
	private Timestamp dateTime;
	private boolean active;
	private boolean newRecord;
	private LoggedInUser userDetails;
	

	public LicenceFile() {
		super();
	}

	public LicenceFile(long id) {
		super();
		this.setId(id);
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("newRecord");
	return excludeFields;
	}
	
	public long getId() {
		return fileID;
	}	
	public void setId (long id) {
		this.fileID = id;
	}

	public long getFileID() {
		return fileID;
	}

	public void setFileID(long fileID) {
		this.fileID = fileID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	@Override
	public boolean isNew() {
		return isNewRecord();
	}

	public Timestamp getDateTime() {
		return dateTime;
	}

	public void setDateTime(Timestamp dateTime) {
		this.dateTime = dateTime;
	}
	
}
