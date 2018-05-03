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
 * FileName    		:  InterfaceMapping.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-04-2017    														*
 *                                                                  						*
 * Modified Date    :  24-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.interfacemapping;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>InterfaceMapping table</b>.<br>
 *
 */
public class InterfaceMapping extends AbstractWorkflowEntity {
	private static final long	serialVersionUID	= 1L;

	private long								interfaceMappingId		= Long.MIN_VALUE;
	private long                                interfaceId             = Long.MIN_VALUE;
	private String								interfaceName;
	private String								interfaceField;
	private String								mappingTable;
	private String								mappingColumn;
	private String								mappingValue;
	private boolean								active;
	private String								mappingType;
	private String								module;
	private boolean								newRecord;
	private String								lovValue;
	private InterfaceMapping					befImage;
	private LoggedInUser						userDetails;
	private List<MasterMapping>					masterMappingList;
	private HashMap<String, List<AuditDetail>>	lovDescAuditDetailMap	= new HashMap<String, List<AuditDetail>>();

	public boolean isNew() {
		return isNewRecord();
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("mappingid");
		excludeFields.add("mappingType");
		excludeFields.add("module");
		return excludeFields;
	}
	public InterfaceMapping() {
		super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getInterfaceField() {
		return interfaceField;
	}

	public void setInterfaceField(String interfaceField) {
		this.interfaceField = interfaceField;
	}


	public String getMappingTable() {
		return mappingTable;
	}

	public void setMappingTable(String mappingTable) {
		this.mappingTable = mappingTable;
	}

	public String getMappingColumn() {
		return mappingColumn;
	}

	public void setMappingColumn(String mappingColumn) {
		this.mappingColumn = mappingColumn;
	}

	public String getMappingValue() {
		return mappingValue;
	}

	public void setMappingValue(String mappingValue) {
		this.mappingValue = mappingValue;
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

	public InterfaceMapping getBefImage() {
		return this.befImage;
	}

	public void setBefImage(InterfaceMapping beforeImage) {
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

	public long getInterfaceMappingId() {
		return interfaceMappingId;
	}

	public void setInterfaceMappingId(long interfaceMappingId) {
		this.interfaceMappingId = interfaceMappingId;
	}

	public long getId() {
		return interfaceMappingId;
	}

	public void setId(long id) {
		this.interfaceMappingId = id;

	}

	public List<MasterMapping> getMasterMappingList() {
		return masterMappingList;
	}

	public void setMasterMappingList(List<MasterMapping> masterMappingList) {
		this.masterMappingList = masterMappingList;
	}

	public String getMappingType() {
		return mappingType;
	}

	public void setMappingType(String mappingType) {
		this.mappingType = mappingType;
	}

	public HashMap<String, List<AuditDetail>> getLovDescAuditDetailMap() {
		return lovDescAuditDetailMap;
	}

	public void setLovDescAuditDetailMap(HashMap<String, List<AuditDetail>> lovDescAuditDetailMap) {
		this.lovDescAuditDetailMap = lovDescAuditDetailMap;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public long getInterfaceId() {
		return interfaceId;
	}

	public void setInterfaceId(long interfaceId) {
		this.interfaceId = interfaceId;
	}

}
