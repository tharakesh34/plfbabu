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
 * * FileName : CustomerEmploymentDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.customermasters;

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
 * Model class for the <b>CustomerEmploymentDetail table</b>.<br>
 * 
 */
@XmlType(propOrder = { "custEmpId", "custEmpName", "custEmpType", "custEmpDesg", "custEmpDept", "custEmpFrom",
		"custEmpTo", "occupation", "companyName", "industryDesc" })
@XmlAccessorType(XmlAccessType.NONE)
public class CustomerEmploymentDetail extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -5317225672461108680L;

	private long custID;
	@XmlElement(name = "employmentId")
	private long custEmpId = Long.MIN_VALUE;
	@XmlElement(name = "employerId")
	private Long custEmpName;
	@XmlElement(name = "compName")
	private String lovDesccustEmpName;
	@XmlElement(name = "startDate")
	private Date custEmpFrom;
	@XmlElement(name = "endDate")
	private Date custEmpTo;
	private boolean currentEmployer;
	@XmlElement(name = "designation")
	private String custEmpDesg;
	@XmlElement(name = "occupation")
	private String lovDescCustEmpDesgName;
	@XmlElement(name = "department")
	private String custEmpDept;
	@XmlElement(name = "industryDesc")
	private String lovDescCustEmpDeptName;
	@XmlElement
	private String custEmpType;
	private String lovDescCustEmpTypeName;
	private String lovValue;
	private String lovDescCustShrtName;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;
	private String lovDescEmpCategory;

	private String sourceId;

	private CustomerEmploymentDetail befImage;
	private LoggedInUser userDetails;
	private String companyName;
	private String lovDescEmpIndustry;

	public CustomerEmploymentDetail() {
		super();
	}

	public CustomerEmploymentDetail(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("returnStatus");
		excludeFields.add("sourceId");
		excludeFields.add("lovDescEmpIndustry");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return custID;
	}

	public void setId(long id) {
		this.custID = id;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public Long getCustEmpName() {
		return custEmpName;
	}

	public void setCustEmpName(Long custEmpName) {
		this.custEmpName = custEmpName;
	}

	public Date getCustEmpFrom() {
		return custEmpFrom;
	}

	public void setCustEmpFrom(Date custEmpFrom) {
		this.custEmpFrom = custEmpFrom;
	}

	public String getCustEmpDesg() {
		return custEmpDesg;
	}

	public void setCustEmpDesg(String custEmpDesg) {
		this.custEmpDesg = custEmpDesg;
	}

	public String getLovDescCustEmpDesgName() {
		return this.lovDescCustEmpDesgName;
	}

	public void setLovDescCustEmpDesgName(String lovDescCustEmpDesgName) {
		this.lovDescCustEmpDesgName = lovDescCustEmpDesgName;
	}

	public String getCustEmpDept() {
		return custEmpDept;
	}

	public void setCustEmpDept(String custEmpDept) {
		this.custEmpDept = custEmpDept;
	}

	public String getLovDescCustEmpDeptName() {
		return this.lovDescCustEmpDeptName;
	}

	public void setLovDescCustEmpDeptName(String lovDescCustEmpDeptName) {
		this.lovDescCustEmpDeptName = lovDescCustEmpDeptName;
	}

	public String getCustEmpType() {
		return custEmpType;
	}

	public void setCustEmpType(String custEmpType) {
		this.custEmpType = custEmpType;
	}

	public String getLovDescCustEmpTypeName() {
		return this.lovDescCustEmpTypeName;
	}

	public void setLovDescCustEmpTypeName(String lovDescCustEmpTypeName) {
		this.lovDescCustEmpTypeName = lovDescCustEmpTypeName;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public String getLovDescCustRecordType() {
		return lovDescCustRecordType;
	}

	public void setLovDescCustRecordType(String lovDescCustRecordType) {
		this.lovDescCustRecordType = lovDescCustRecordType;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}

	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

	public CustomerEmploymentDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(CustomerEmploymentDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setLoginDetails(LoggedInUser userDetails) {
		setLastMntBy(userDetails.getUserId());
		this.userDetails = userDetails;
	}

	public void setCustEmpTo(Date custEmpTo) {
		this.custEmpTo = custEmpTo;
	}

	public Date getCustEmpTo() {
		return custEmpTo;
	}

	public void setLovDesccustEmpName(String lovDesccustEmpName) {
		this.lovDesccustEmpName = lovDesccustEmpName;
	}

	public String getLovDesccustEmpName() {
		return lovDesccustEmpName;
	}

	public void setCurrentEmployer(boolean currentEmployer) {
		this.currentEmployer = currentEmployer;
	}

	public boolean isCurrentEmployer() {
		return currentEmployer;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public long getCustEmpId() {
		return custEmpId;
	}

	public void setCustEmpId(long custEmpId) {
		this.custEmpId = custEmpId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getLovDescEmpIndustry() {
		return lovDescEmpIndustry;
	}

	public void setLovDescEmpIndustry(String lovDescEmpIndustry) {
		this.lovDescEmpIndustry = lovDescEmpIndustry;
	}

	public String getLovDescEmpCategory() {
		return lovDescEmpCategory;
	}

	public void setLovDescEmpCategory(String lovDescEmpCategory) {
		this.lovDescEmpCategory = lovDescEmpCategory;
	}

}
