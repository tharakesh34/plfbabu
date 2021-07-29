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
 * FileName    		:  CheckList.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-12-2011    														*
 *                                                                  						*
 * Modified Date    :  12-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.bmtmasters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>CheckList table</b>.<br>
 *
 */
public class CheckList extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -3060817228345423733L;

	private long checkListId = Long.MIN_VALUE;
	private String checkListDesc;
	private int checkMinCount;
	private int checkMaxCount;
	private String checkRule;
	private String moduleName;
	private String lovDescCheckRuleName;
	private boolean active;
	private String lovValue;
	private CheckList befImage;
	private LoggedInUser userDetails;
	private List<CheckListDetail> chkListList = new ArrayList<CheckListDetail>();
	private Map<String, List<AuditDetail>> lovDescAuditDetailMap = new HashMap<>();

	public boolean isNew() {
		return isNewRecord();
	}

	public CheckList() {
		super();
	}

	public CheckList(long id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return checkListId;
	}

	public void setId(long id) {
		this.checkListId = id;
	}

	public long getCheckListId() {
		return checkListId;
	}

	public void setCheckListId(long checkListId) {
		this.checkListId = checkListId;
	}

	public String getCheckListDesc() {
		return checkListDesc;
	}

	public void setCheckListDesc(String checkListDesc) {
		this.checkListDesc = checkListDesc;
	}

	public int getCheckMinCount() {
		return checkMinCount;
	}

	public void setCheckMinCount(int checkMinCount) {
		this.checkMinCount = checkMinCount;
	}

	public int getCheckMaxCount() {
		return checkMaxCount;
	}

	public void setCheckMaxCount(int checkMaxCount) {
		this.checkMaxCount = checkMaxCount;
	}

	public String getCheckRule() {
		return checkRule;
	}

	public void setCheckRule(String checkRule) {
		this.checkRule = checkRule;
	}

	public String getLovDescCheckRuleName() {
		return lovDescCheckRuleName;
	}

	public void setLovDescCheckRuleName(String lovDescCheckRuleName) {
		this.lovDescCheckRuleName = lovDescCheckRuleName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public CheckList getBefImage() {
		return this.befImage;
	}

	public void setBefImage(CheckList beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public List<CheckListDetail> getChkListList() {
		return chkListList;
	}

	public void setChkListList(List<CheckListDetail> chkListList) {
		this.chkListList = chkListList;
	}

	public Map<String, List<AuditDetail>> getLovDescAuditDetailMap() {
		return lovDescAuditDetailMap;
	}

	public void setLovDescAuditDetailMap(Map<String, List<AuditDetail>> lovDescAuditDetailMap) {
		this.lovDescAuditDetailMap = lovDescAuditDetailMap;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
}
