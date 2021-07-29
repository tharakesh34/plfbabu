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
 * FileName    		:  StorageDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-02-2018    														*
 *                                                                  						*
 * Modified Date    :  01-02-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-02-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.financemanagement;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>StorageDetail table</b>.<br>
 *
 */
public class StorageDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long storageId = Long.MIN_VALUE;
	private String finReference;
	private String finReferenceName;
	private String packetNumber;
	private String rackNumber;
	private long howAquired = 0;
	private String howAquiredName;
	private long whenAquired = 0;
	private String whenAquiredName;
	private String lovValue;
	private StorageDetail befImage;
	private LoggedInUser userDetails;
	private String dmaCode;
	private String dmaCodeDesc;
	private String finPurpose;
	private String lovDescFinPurposeName;
	private String branchCode;
	private String oldPacketNumber;
	private String oldRackNumber;
	private String rcdMaintainSts;

	public boolean isNew() {
		return isNewRecord();
	}

	public StorageDetail() {
		super();
	}

	public StorageDetail(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("finReferenceName");
		excludeFields.add("howAquiredName");
		excludeFields.add("whenAquiredName");
		excludeFields.add("dmaCodeDesc");
		excludeFields.add("branchCode");
		excludeFields.add("oldPacketNumber");
		;
		excludeFields.add("oldRackNumber");
		excludeFields.add("rcdMaintainSts");
		return excludeFields;
	}

	public long getId() {
		return storageId;
	}

	public void setId(long id) {
		this.storageId = id;
	}

	public long getStorageId() {
		return storageId;
	}

	public void setStorageId(long storageId) {
		this.storageId = storageId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinReferenceName() {
		return this.finReferenceName;
	}

	public void setFinReferenceName(String finReferenceName) {
		this.finReferenceName = finReferenceName;
	}

	public String getPacketNumber() {
		return packetNumber;
	}

	public void setPacketNumber(String packetNumber) {
		this.packetNumber = packetNumber;
	}

	public String getRackNumber() {
		return rackNumber;
	}

	public void setRackNumber(String rackNumber) {
		this.rackNumber = rackNumber;
	}

	public long getHowAquired() {
		return howAquired;
	}

	public void setHowAquired(long howAquired) {
		this.howAquired = howAquired;
	}

	public String getHowAquiredName() {
		return this.howAquiredName;
	}

	public void setHowAquiredName(String howAquiredName) {
		this.howAquiredName = howAquiredName;
	}

	public long getWhenAquired() {
		return whenAquired;
	}

	public void setWhenAquired(long whenAquired) {
		this.whenAquired = whenAquired;
	}

	public String getWhenAquiredName() {
		return this.whenAquiredName;
	}

	public void setWhenAquiredName(String whenAquiredName) {
		this.whenAquiredName = whenAquiredName;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public StorageDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(StorageDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public String getDmaCode() {
		return dmaCode;
	}

	public void setDmaCode(String dmaCode) {
		this.dmaCode = dmaCode;
	}

	public String getDmaCodeDesc() {
		return dmaCodeDesc;
	}

	public void setDmaCodeDesc(String dmaCodeDesc) {
		this.dmaCodeDesc = dmaCodeDesc;
	}

	public String getFinPurpose() {
		return finPurpose;
	}

	public void setFinPurpose(String finPurpose) {
		this.finPurpose = finPurpose;
	}

	public String getLovDescFinPurposeName() {
		return lovDescFinPurposeName;
	}

	public void setLovDescFinPurposeName(String lovDescFinPurposeName) {
		this.lovDescFinPurposeName = lovDescFinPurposeName;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getOldPacketNumber() {
		return oldPacketNumber;
	}

	public void setOldPacketNumber(String oldPacketNumber) {
		this.oldPacketNumber = oldPacketNumber;
	}

	public String getOldRackNumber() {
		return oldRackNumber;
	}

	public void setOldRackNumber(String oldRackNumber) {
		this.oldRackNumber = oldRackNumber;
	}

	public String getRcdMaintainSts() {
		return rcdMaintainSts;
	}

	public void setRcdMaintainSts(String rcdMaintainSts) {
		this.rcdMaintainSts = rcdMaintainSts;
	}

}
