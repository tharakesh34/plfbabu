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
 * FileName    		:  GenGoodsLoanDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.lmtmasters;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>GenGoodsLoanDetail table</b>.<br>
 *
 */
public class GenGoodsLoanDetail implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String loanRefNumber;
	private String itemNumber;
	private String itemDescription;
	private BigDecimal unitPrice;
	private int quantity;
	private String addtional1;//purchageOdrNumber
	private String addtional2;//quoationNbr
	private int addtional3;
	private int addtional4;
	private Date addtional5;//purchaseDate
	private Date addtional6;//quoationDate
	private BigDecimal addtional7;
	private BigDecimal addtional8;
	private Long sellerID;
	private String lovDescSellerID;
	private String lovDescSellerPhone;
	private String lovDescSellerFax;
	private int version;
	@XmlTransient
	private long lastMntBy;
	private String lastMaintainedUser;
	@XmlTransient
	private Timestamp lastMntOn;
	@SuppressWarnings("unused")
	private XMLGregorianCalendar lastMaintainedOn;
	@XmlTransient
	private boolean newRecord=false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private GenGoodsLoanDetail befImage;
	@XmlTransient
	private LoginUserDetails userDetails;
	@XmlTransient
	private String recordStatus;
	@XmlTransient
	private String roleCode="";
	@XmlTransient
	private String nextRoleCode= "";
	@XmlTransient
	private String taskId="";
	@XmlTransient
	private String nextTaskId= "";
	@XmlTransient
	private String recordType;
	@XmlTransient
	private String userAction = "Save";
	@XmlTransient
	private long workflowId = 0;
	
	private List<GenGoodsLoanDetail> goodsLoanDetailList=new ArrayList<GenGoodsLoanDetail>();

	public boolean isNew() {
		return isNewRecord();
	}

	public GenGoodsLoanDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("GenGoodsLoanDetail");
	}

	public GenGoodsLoanDetail(String id) {
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("goodsLoanDetailList");
	return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
		// ++++++++++++++++++ getter / setter +++++++++++++++++++//
		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	@XmlTransient
	public String getId() {
		return loanRefNumber;
	}
	
	public void setId (String id) {
		this.loanRefNumber = id;
	}
	
	public String getLoanRefNumber() {
		return loanRefNumber;
	}
	public void setLoanRefNumber(String loanRefNumber) {
		this.loanRefNumber = loanRefNumber;
	}
	
	public String getItemNumber() {
		return itemNumber;
	}
	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}
	
	public Long getSellerID() {
    	return sellerID;
    }

	public void setSellerID(Long sellerID) {
    	this.sellerID = sellerID;
    }

	public String getLovDescSellerID() {
    	return lovDescSellerID;
    }

	public void setLovDescSellerID(String lovDescSellerID) {
    	this.lovDescSellerID = lovDescSellerID;
    }

	public String getItemDescription() {
		return itemDescription;
	}
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}
	
	
	
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	
		
	
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	
		
	
	public String getAddtional1() {
		return addtional1;
	}
	public void setAddtional1(String addtional1) {
		this.addtional1 = addtional1;
	}
	
	
		
	
	public String getAddtional2() {
		return addtional2;
	}
	public void setAddtional2(String addtional2) {
		this.addtional2 = addtional2;
	}
	
	
		
	
	public int getAddtional3() {
		return addtional3;
	}
	public void setAddtional3(int addtional3) {
		this.addtional3 = addtional3;
	}
	
	
		
	
	public int getAddtional4() {
		return addtional4;
	}
	public void setAddtional4(int addtional4) {
		this.addtional4 = addtional4;
	}
	
	
		
	
	public Date getAddtional5() {
		return addtional5;
	}
	public void setAddtional5(Date addtional5) {
		this.addtional5 = addtional5;
	}
	
	
		
	
	public Date getAddtional6() {
		return addtional6;
	}
	public void setAddtional6(Date addtional6) {
		this.addtional6 = addtional6;
	}
	
	
		
	
	public BigDecimal getAddtional7() {
		return addtional7;
	}
	public void setAddtional7(BigDecimal addtional7) {
		this.addtional7 = addtional7;
	}
	
	
		
	
	public BigDecimal getAddtional8() {
		return addtional8;
	}
	public void setAddtional8(BigDecimal addtional8) {
		this.addtional8 = addtional8;
	}
	
	
		
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	@XmlTransient
	public long getLastMntBy() {
		return lastMntBy;
	}
	
	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public String getLastMaintainedUser() {
		return lastMaintainedUser;
	}

	public void setLastMaintainedUser(String lastMaintainedUser) {
		this.lastMaintainedUser = lastMaintainedUser;
	}
	
	@XmlTransient
	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMaintainedOn(XMLGregorianCalendar xmlCalendar) {
		if (xmlCalendar != null) {
			lastMntOn = DateUtility.ConvertFromXMLTime(xmlCalendar);
			lastMaintainedOn = xmlCalendar;
		}
	}

	public XMLGregorianCalendar getLastMaintainedOn()
			throws DatatypeConfigurationException {

		if (lastMntOn == null) {
			return null;
		}
		return DateUtility.getXMLDate(lastMntOn);
	}

	
	public void setLastMntOn(Timestamp lastMntON) {
		this.lastMntOn = lastMntON;
	}

	@XmlTransient
	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	@XmlTransient
	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	@XmlTransient
	public GenGoodsLoanDetail getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(GenGoodsLoanDetail beforeImage){
		this.befImage=beforeImage;
	}

	@XmlTransient
	public LoginUserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	@XmlTransient
	public String getRecordStatus() {
		return recordStatus;
	}
	
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}
	
	@XmlTransient
	public String getRoleCode() {
		return roleCode;
	}
	
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
	@XmlTransient
	public String getNextRoleCode() {
		return nextRoleCode;
	}
	
	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}
	
	@XmlTransient
	public String getTaskId() {
		return taskId;
	}
	
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@XmlTransient
	public String getNextTaskId() {
		return nextTaskId;
	}
	
	
	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}
	
	@XmlTransient
	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	@XmlTransient
	public String getUserAction() {
		return userAction;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public boolean isWorkflow() {
		if (this.workflowId==0){
			return false;
		}
		return true;
	}

	public long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	// Overidden Equals method to handle the comparision
	public boolean equals(GenGoodsLoanDetail goodsLoanDetail) {
		return getId() == goodsLoanDetail.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof GenGoodsLoanDetail) {
			GenGoodsLoanDetail goodsLoanDetail = (GenGoodsLoanDetail) obj;
			return equals(goodsLoanDetail);
		}
		return false;
	}

	public void setGenGoodsLoanDetailList(List<GenGoodsLoanDetail> goodsLoanDetailList) {
	    this.goodsLoanDetailList = goodsLoanDetailList;
    }

	public List<GenGoodsLoanDetail> getGenGoodsLoanDetailList() {
	    return goodsLoanDetailList;
    }

	public void setLovDescSellerPhone(String lovDescSellerPhone) {
	    this.lovDescSellerPhone = lovDescSellerPhone;
    }

	public String getLovDescSellerPhone() {
	    return lovDescSellerPhone;
    }

	public void setLovDescSellerFax(String lovDescSellerFax) {
	    this.lovDescSellerFax = lovDescSellerFax;
    }

	public String getLovDescSellerFax() {
	    return lovDescSellerFax;
    }
}
