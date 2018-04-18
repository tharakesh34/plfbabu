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
 * FileName    		:  AuthorizationLimit.java                                                   * 	  
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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>AuthorizationLimit table</b>.<br>
 *
 */
public class AuthorizationLimit extends AbstractWorkflowEntity  implements Entity {
private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private int limitType;
	private long userID= 0;
	private long roleId= 0;
	private String module;
	private BigDecimal limitAmount= BigDecimal.ZERO;
	private Date startDate;
	private Date expiryDate;
	private Date holdStartDate;
	private Date holdExpiryDate;
	private boolean active= false;
	private boolean newRecord=false;
	private String lovValue;
	private AuthorizationLimit befImage;
	private  LoggedInUser userDetails;
	private List<AuthorizationLimitDetail> authorizationLimitDetails;
	private String usrLogin;
	private String UsrFName;
	private String UsrMName;
	private String UsrLName;
	private String roleCd;
	private String roleName;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public AuthorizationLimit() {
		super();
	}

	public AuthorizationLimit(long id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("authorizationLimitDetails");
		excludeFields.add("usrLogin");
		excludeFields.add("UsrFName");
		excludeFields.add("UsrMName");
		excludeFields.add("UsrLName");
		excludeFields.add("roleCd");
		excludeFields.add("roleName");
	return excludeFields;
	}

	public long getId() {
		return id;
	}
	
	public void setId (long id) {
		this.id = id;
	}
	
	public int getLimitType() {
		return limitType;
	}
	public void setLimitType(int limitType) {
		this.limitType = limitType;
	}
	
	public long getUserID() {
		return userID;
	}
	public void setUserID(long userID) {
		this.userID = userID;
	}
	
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	
	public BigDecimal getLimitAmount() {
		return limitAmount;
	}
	public void setLimitAmount(BigDecimal limitAmount) {
		this.limitAmount = limitAmount;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	public Date getHoldStartDate() {
		return holdStartDate;
	}
	public void setHoldStartDate(Date holdStartDate) {
		this.holdStartDate = holdStartDate;
	}
	
	public Date getHoldExpiryDate() {
		return holdExpiryDate;
	}
	public void setHoldExpiryDate(Date holdExpiryDate) {
		this.holdExpiryDate = holdExpiryDate;
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

	public AuthorizationLimit getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(AuthorizationLimit beforeImage){
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

	public List<AuthorizationLimitDetail> getAuthorizationLimitDetails() {
		return authorizationLimitDetails;
	}

	public void setAuthorizationLimitDetails(List<AuthorizationLimitDetail> authorizationLimitDetails) {
		this.authorizationLimitDetails = authorizationLimitDetails;
	}

	public String getUsrLogin() {
		return usrLogin;
	}

	public void setUsrLogin(String usrLogin) {
		this.usrLogin = usrLogin;
	}

	public String getUsrFName() {
		return UsrFName;
	}

	public void setUsrFName(String usrFName) {
		UsrFName = usrFName;
	}

	public String getUsrMName() {
		return UsrMName;
	}

	public void setUsrMName(String usrMName) {
		UsrMName = usrMName;
	}

	public String getUsrLName() {
		return UsrLName;
	}

	public void setUsrLName(String usrLName) {
		UsrLName = usrLName;
	}

	public String getRoleCd() {
		return roleCd;
	}

	public void setRoleCd(String roleCd) {
		this.roleCd = roleCd;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
