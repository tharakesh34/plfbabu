/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * * FileName : JountAccountDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-09-2013 * * Modified
 * Date : 10-09-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 10-09-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.model.finance;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>JountAccountDetail table</b>.<br>
 * 
 */
@XmlType(propOrder = { "custCIF", "lovDescCIFName", "includeRepay", "repayAccountId","authoritySignatory","sequence" })
@XmlAccessorType(XmlAccessType.NONE)
public class JointAccountDetail extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long jointAccountId = Long.MIN_VALUE;
	private String finReference;
	@XmlElement(name = "cif")
	private String custCIF;
	@XmlElement(name ="shortName")
	private String lovDescCIFName;

	@XmlElement
	private boolean includeRepay;
	@XmlElement
	private String repayAccountId;
	private String primaryExposure;
	private String secondaryExposure;
	private String guarantorExposure;
	private String worstStatus;
	private String status;
	private String catOfcoApplicant;
	@XmlElement
	private boolean					authoritySignatory;
	@XmlElement
	private int						sequence;

	private List<FinanceExposure> primaryList = null;
	private List<FinanceExposure> secoundaryList = null;
	private List<FinanceExposure> guarantorList = null;

	private boolean newRecord = false;
	private String lovValue;
	private JointAccountDetail befImage;
	private LoggedInUser userDetails;
	private CustomerDetails customerDetails;
	
	private long custID;

	public boolean isNew() {
		return isNewRecord();
	}

	public JointAccountDetail() {
		super();
	}

	public JointAccountDetail(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custCIFName");
		excludeFields.add("primaryList");
		excludeFields.add("secoundaryList");
		excludeFields.add("guarantorList");
		excludeFields.add("primaryExposure");
		excludeFields.add("secondaryExposure");
		excludeFields.add("guarantorExposure");
		excludeFields.add("worstStatus");
		excludeFields.add("status");
		excludeFields.add("custID");
		excludeFields.add("customerDetails");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return jointAccountId;
	}

	public void setId(long id) {
		this.jointAccountId = id;
	}

	public long getJointAccountId() {
		return jointAccountId;
	}

	public void setJointAccountId(long jointAccountId) {
		this.jointAccountId = jointAccountId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public boolean isIncludeRepay() {
		return includeRepay;
	}

	public void setIncludeRepay(boolean includeRepay) {
		this.includeRepay = includeRepay;
	}

	public String getRepayAccountId() {
		return repayAccountId;
	}

	public void setRepayAccountId(String repayAccountId) {
		this.repayAccountId = repayAccountId;
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

	public JointAccountDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(JointAccountDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getLovDescCIFName() {
		return lovDescCIFName;
	}

	public void setLovDescCIFName(String lovDescCIFName) {
		this.lovDescCIFName = lovDescCIFName;
	}

	public String getPrimaryExposure() {
		return primaryExposure;
	}

	public void setPrimaryExposure(String primaryExposure) {
		this.primaryExposure = primaryExposure;
	}

	public String getSecondaryExposure() {
		return secondaryExposure;
	}

	public void setSecondaryExposure(String secondaryExposure) {
		this.secondaryExposure = secondaryExposure;
	}

	public String getGuarantorExposure() {
		return guarantorExposure;
	}

	public void setGuarantorExposure(String guarantorExposure) {
		this.guarantorExposure = guarantorExposure;
	}

	public String getWorstStatus() {
		return worstStatus;
	}

	public void setWorstStatus(String worstStatus) {
		this.worstStatus = worstStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<FinanceExposure> getPrimaryList() {
		return primaryList;
	}

	public void setPrimaryList(List<FinanceExposure> primaryList) {
		this.primaryList = primaryList;
	}

	public List<FinanceExposure> getSecoundaryList() {
		return secoundaryList;
	}

	public void setSecoundaryList(List<FinanceExposure> secoundaryList) {
		this.secoundaryList = secoundaryList;
	}

	public List<FinanceExposure> getGuarantorList() {
		return guarantorList;
	}

	public void setGuarantorList(List<FinanceExposure> guarantorList) {
		this.guarantorList = guarantorList;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getCatOfcoApplicant() {
		return catOfcoApplicant;
	}

	public void setCatOfcoApplicant(String catOfcoApplicant) {
		this.catOfcoApplicant = catOfcoApplicant;
	}

	public boolean isAuthoritySignatory() {
		return authoritySignatory;
	}

	public void setAuthoritySignatory(boolean authoritySignatory) {
		this.authoritySignatory = authoritySignatory;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public CustomerDetails getCustomerDetails() {
		return customerDetails;
	}

	public void setCustomerDetails(CustomerDetails customerDetails) {
		this.customerDetails = customerDetails;
	}

}
