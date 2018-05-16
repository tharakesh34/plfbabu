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
 * FileName    		:  EtihadCreditBureauDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-10-2011    														*
 *                                                                  						*
 * Modified Date    :  13-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-10-2011       Pennant	                 0.1                                            * 
 * 16-05-2018       Pennant                  0.2           Added Property Alwotc.                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.backend.model.finance;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>EtihadCreditBureauDetail table</b>.<br>
 *
 */
@XmlType(propOrder = { "covenantType", "alwWaiver", "alwPostpone", "postponeDays", "description" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinCovenantType extends AbstractWorkflowEntity {
	
	private static final long serialVersionUID = -6234931333270161797L;

	private String finReference;
	@XmlElement
	private String covenantType;
	private String covenantTypeDesc;
	private String mandRole;
	private String mandRoleDesc;
	@XmlElement
	private String description;
	@XmlElement
	private boolean alwWaiver;
	@XmlElement
	private boolean alwPostpone;
	@XmlElement
	private boolean alwOtc;
	@XmlElement
	private int postponeDays;

	private boolean newRecord=false;
	private String lovValue;
	private FinCovenantType befImage;
	private LoggedInUser userDetails;
	
	private Date receivableDate;
	private Date docReceivedDate;
	private String categoryCode;

	// API validation purpose only
	@SuppressWarnings("unused")
	private FinCovenantType validateCovenantType = this;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public FinCovenantType() {
		super();
	}

	public FinCovenantType(String id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("covenantTypeDesc");
		excludeFields.add("mandRoleDesc");
		excludeFields.add("validateCovenantType");
		excludeFields.add("docReceivedDate");
		excludeFields.add("categoryCode");
		
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return finReference;
	}
	public void setId (String finReference) {
		this.finReference = finReference;
	}
	
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCovenantType() {
		return covenantType;
	}
	public void setCovenantType(String covenantType) {
		this.covenantType = covenantType;
	}

	public String getCovenantTypeDesc() {
		return covenantTypeDesc;
	}
	public void setCovenantTypeDesc(String covenantTypeDesc) {
		this.covenantTypeDesc = covenantTypeDesc;
	}

	public String getMandRoleDesc() {
		return mandRoleDesc;
	}
	public void setMandRoleDesc(String mandRoleDesc) {
		this.mandRoleDesc = mandRoleDesc;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getMandRole() {
		return mandRole;
	}
	public void setMandRole(String mandRole) {
		this.mandRole = mandRole;
	}

	public boolean isAlwWaiver() {
		return alwWaiver;
	}
	public void setAlwWaiver(boolean alwWaiver) {
		this.alwWaiver = alwWaiver;
	}

	public boolean isAlwPostpone() {
		return alwPostpone;
	}
	public void setAlwPostpone(boolean alwPostpone) {
		this.alwPostpone = alwPostpone;
	}

	public int getPostponeDays() {
		return postponeDays;
	}
	public void setPostponeDays(int postponeDays) {
		this.postponeDays = postponeDays;
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

	public FinCovenantType getBefImage(){
		return this.befImage;
	}
	public void setBefImage(FinCovenantType beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Date getReceivableDate() {
		return receivableDate;
	}

	public void setReceivableDate(Date receivableDate) {
		this.receivableDate = receivableDate;
	}

	public Date getDocReceivedDate() {
		return docReceivedDate;
	}

	public void setDocReceivedDate(Date docReceivedDate) {
		this.docReceivedDate = docReceivedDate;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public boolean isAlwOtc() {
		return alwOtc;
	}

	public void setAlwOtc(boolean alwOtc) {
		this.alwOtc = alwOtc;
	}

}
