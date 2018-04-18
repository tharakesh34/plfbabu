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
 * FileName    		:  AuthorizationLimitDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-04-2018    														*
 *                                                                  						*
 * Modified Date    :  06-04-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-04-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.authorization;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>AuthorizationLimitDetail table</b>.<br>
 *
 */
@XmlType(propOrder = {"id","authLimitId","code","limitAmount"})
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthorizationLimitDetail extends AbstractWorkflowEntity  implements Entity {
private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long authLimitId= 0;
	private String code;
	private String productDesc;
	private String collateralDesc;
	private BigDecimal limitAmount= BigDecimal.ZERO;
	@XmlTransient
	private boolean newRecord=false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private AuthorizationLimitDetail befImage;
	@XmlTransient
	private  LoggedInUser userDetails;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public AuthorizationLimitDetail() {
		super();
	}

	public AuthorizationLimitDetail(long id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields(){
			Set<String> excludeFields=new HashSet<String>();
			excludeFields.add("productDesc");
			excludeFields.add("collateralDesc");
			return excludeFields;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public long getAuthLimitId() {
		return authLimitId;
	}
	public void setAuthLimitId(long authLimitId) {
		this.authLimitId = authLimitId;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getCollateralDesc() {
		return collateralDesc;
	}

	public void setCollateralDesc(String collateralDesc) {
		this.collateralDesc = collateralDesc;
	}

	public String getCodeName() {
		if(StringUtils.trimToNull(this.productDesc)==null){
			return collateralDesc;
		}
		return productDesc;
	}

	
	public BigDecimal getLimitAmount() {
		return limitAmount;
	}
	public void setLimitAmount(BigDecimal limitAmount) {
		this.limitAmount = limitAmount;
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

	public AuthorizationLimitDetail getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(AuthorizationLimitDetail beforeImage){
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

}
