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
 * FileName    		:  ExtendedFieldHeader.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-12-2011    														*
 *                                                                  						*
 * Modified Date    :  28-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.extendedfield;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>ExtendedFieldHeader table</b>.<br>
 *
 */
@XmlType(propOrder = { "moduleName", "subModuleName", "extendedFieldDetailList","returnStatus" })
@XmlRootElement(name = "extendedDetail")
@XmlAccessorType(XmlAccessType.NONE)
public class ExtendedFieldHeader extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = 5219478530421796781L;

	private long moduleId = Long.MIN_VALUE;
	@XmlElement(name = "module")
	private String moduleName="";
	@XmlElement(name = "subModule")
	private String subModuleName="";
	private String tabHeading;
	private String numberOfColumns;
	private boolean newRecord;
	private String lovValue;
	private boolean preValidationReq;
	private boolean postValidationReq;
	private String preValidation;
	private String postValidation;
	private ExtendedFieldHeader befImage;
	private LoggedInUser userDetails;

	@XmlElementWrapper(name = "extendedFields")
	@XmlElement(name = "extendedField")
	private List<ExtendedFieldDetail> extendedFieldDetailList;

	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	@XmlElement
	private WSReturnStatus returnStatus;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public ExtendedFieldHeader() {
		super();
	}

	public ExtendedFieldHeader(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("returnStatus");
		return excludeFields;
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return moduleId;
	}

	public void setId(long id) {
		this.moduleId = id;
	}

	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getSubModuleName() {
		return subModuleName;
	}

	public void setSubModuleName(String subModuleName) {
		this.subModuleName = subModuleName;
	}

	public String getTabHeading() {
		return tabHeading;
	}

	public void setTabHeading(String tabHeading) {
		this.tabHeading = tabHeading;
	}

	public String getNumberOfColumns() {
		return numberOfColumns;
	}

	public void setNumberOfColumns(String numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
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

	public ExtendedFieldHeader getBefImage() {
		return this.befImage;
	}

	public void setBefImage(ExtendedFieldHeader beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public List<ExtendedFieldDetail> getExtendedFieldDetails() {
		return extendedFieldDetailList;
	}

	public void setExtendedFieldDetails(List<ExtendedFieldDetail> extendedFieldDetails) {
		this.extendedFieldDetailList = extendedFieldDetails;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public boolean isPreValidationReq() {
		return preValidationReq;
	}

	public void setPreValidationReq(boolean preValidationReq) {
		this.preValidationReq = preValidationReq;
	}

	public boolean isPostValidationReq() {
		return postValidationReq;
	}

	public void setPostValidationReq(boolean postValidationReq) {
		this.postValidationReq = postValidationReq;
	}

	public String getPreValidation() {
		return preValidation;
	}

	public void setPreValidation(String preValidation) {
		this.preValidation = preValidation;
	}

	public String getPostValidation() {
		return postValidation;
	}

	public void setPostValidation(String postValidation) {
		this.postValidation = postValidation;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}