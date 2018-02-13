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
 * FileName    		:  BrokerCommodityDetail.java                                           * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-11-2011    														*
 *                                                                  						*
 * Modified Date    :  10-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.finance.commodity;

import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>BrokerCommodityDetail table</b>.<br>
 *
 */
public class BrokerCommodityDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 5232119312085110695L;
	
	private String     brokerCode = null;
	private String     commodityCode = null;

	private boolean    newRecord=false;
	private String     lovValue;
	private String     CommodityUnitCode;
	private String     CommodityUnitName;
	private String     lovDescCommodityDesc;
	
	private BrokerCommodityDetail befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public BrokerCommodityDetail() {
		super();
	}

	public BrokerCommodityDetail(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("lovDescCommodityDesc");
		excludeFields.add("CommodityUnitCode");
		excludeFields.add("CommodityUnitName");
		return excludeFields;
	}
	
	//Getter and Setter methods

	public String getId() {
		return brokerCode;
	}
	public void setId (String id) {
		this.brokerCode = id;
	}

	public String getBrokerCode() {
		return brokerCode;
	}
	public void setBrokerCode(String brokerCode) {
		this.brokerCode = brokerCode;
	}
	
	public String getCommodityCode() {
		return commodityCode;
	}
	public void setCommodityCode(String commodityCode) {
		this.commodityCode = commodityCode;
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

	public BrokerCommodityDetail getBefImage(){
		return this.befImage;
	}
	public void setBefImage(BrokerCommodityDetail beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getLovDescCommodityDesc() {
		return lovDescCommodityDesc;
	}

	public void setLovDescCommodityDesc(String lovDescCommodityDesc) {
		this.lovDescCommodityDesc = lovDescCommodityDesc;
	}

	public String getCommodityUnitCode() {
		return CommodityUnitCode;
	}

	public void setCommodityUnitCode(String commodityUnitCode) {
		CommodityUnitCode = commodityUnitCode;
	}

	public String getCommodityUnitName() {
		return CommodityUnitName;
	}

	public void setCommodityUnitName(String commodityUnitName) {
		CommodityUnitName = commodityUnitName;
	}
}
