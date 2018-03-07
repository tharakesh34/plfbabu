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
 * FileName    		:  IRRFinanceType.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2017    														*
 *                                                                  						*
 * Modified Date    :  21-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2017       PENNANT	                 0.1                                            * 
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>IRRFinanceType table</b>.<br>
 *
 */
@XmlType(propOrder = {"iRRID","finType"})
@XmlAccessorType(XmlAccessType.FIELD)
public class IRRFinanceType extends AbstractWorkflowEntity {
private static final long serialVersionUID = 1L;

	private long iRRID = Long.MIN_VALUE;
	private String finType;
	private String iRRIDName;
	private String finTypeName;
	private String irrCode;
	private String irrCodeDesc;
	@XmlTransient
	private boolean newRecord=false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private IRRFinanceType befImage;
	@XmlTransient
	private  LoggedInUser userDetails;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public IRRFinanceType() {
		super();
	}

	public IRRFinanceType(long id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
			excludeFields.add("iRRIDName");
			excludeFields.add("finTypeName");
			excludeFields.add("iRRIDName");
			excludeFields.add("irrCode");
			excludeFields.add("irrCodeDesc");
	return excludeFields;
	}

	public long getId() {
		return iRRID;
	}
	
	public void setId (long id) {
		this.iRRID = id;
	}
	public long getIRRID() {
		return iRRID;
	}
	public void setIRRID(long iRRID) {
		this.iRRID = iRRID;
	}
	
	public String getFinType() {
		return finType;
	}
	public void setFinType(String finType) {
		this.finType = finType;
	}
	public String getFinTypeName() {
		return this.finTypeName;
	}

	public void setFinTypeName (String finTypeName) {
		this.finTypeName = finTypeName;
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

	public IRRFinanceType getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(IRRFinanceType beforeImage){
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

	public long getiRRID() {
		return iRRID;
	}

	public void setiRRID(long iRRID) {
		this.iRRID = iRRID;
	}

	public String getIRRIDName() {
		return iRRIDName;
	}

	public void setIRRIDName(String IRRIDName) {
		this.iRRIDName = IRRIDName;
	}

	public String getIrrCode() {
		return irrCode;
	}

	public void setIrrCode(String irrCode) {
		this.irrCode = irrCode;
	}

	public String getIrrCodeDesc() {
		return irrCodeDesc;
	}

	public void setIrrCodeDesc(String irrCodeDesc) {
		this.irrCodeDesc = irrCodeDesc;
	}
	
}
