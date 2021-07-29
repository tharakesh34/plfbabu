package com.pennant.backend.model.administration;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class ReportingManager extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long userId;
	private String userName;
	private Long businessVertical;
	private String businessVerticalCode;
	private String businessVerticalDesc;
	private String product;
	private String productDesc;
	private String finType;
	private String finTypeDesc;
	private String branch;
	private String branchDesc;
	private Long reportingTo;
	private String reportingToUserName;
	private String lovValue;
	private ReportingManager befImage;
	private LoggedInUser userDetails;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("businessVerticalCode");
		excludeFields.add("businessVerticalDesc");
		excludeFields.add("userName");
		excludeFields.add("productDesc");
		excludeFields.add("finTypeDesc");
		excludeFields.add("branchDesc");
		excludeFields.add("reportingToUserName");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getBusinessVertical() {
		return businessVertical;
	}

	public void setBusinessVertical(Long businessVertical) {
		this.businessVertical = businessVertical;
	}

	public String getBusinessVerticalCode() {
		return businessVerticalCode;
	}

	public void setBusinessVerticalCode(String businessVerticalCode) {
		this.businessVerticalCode = businessVerticalCode;
	}

	public String getBusinessVerticalDesc() {
		return businessVerticalDesc;
	}

	public void setBusinessVerticalDesc(String businessVerticalDesc) {
		this.businessVerticalDesc = businessVerticalDesc;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getBranchDesc() {
		return branchDesc;
	}

	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}

	public Long getReportingTo() {
		return reportingTo;
	}

	public void setReportingTo(Long reportingTo) {
		this.reportingTo = reportingTo;
	}

	public String getReportingToUserName() {
		return reportingToUserName;
	}

	public void setReportingToUserName(String reportingToUserName) {
		this.reportingToUserName = reportingToUserName;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public ReportingManager getBefImage() {
		return befImage;
	}

	public void setBefImage(ReportingManager befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

}
