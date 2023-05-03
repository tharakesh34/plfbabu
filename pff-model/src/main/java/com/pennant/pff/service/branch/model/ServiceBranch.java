package com.pennant.pff.service.branch.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class ServiceBranch extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1899379260622872657L;

	private long id;
	private String code;
	private String description;
	private String ofcOrHouseNum;
	private String flatNum;
	private String street;
	private String addrLine1;
	private String addrLine2;
	private String poBox;
	private String city;
	private String country;
	private String cpProvince;
	private Long pinCodeId = Long.MIN_VALUE;
	private String pinCode;
	private boolean active;
	private String folderPath;
	private ServiceBranch befImage;
	private LoggedInUser userDetails;
	private long createdBy;
	private Timestamp createdOn;
	private long approvedBy;
	private Timestamp approvedOn;

	private List<ServiceBranchesLoanType> serviceBranchLoanTypeList = new ArrayList<>();
	private Map<String, List<AuditDetail>> lovDescAuditDetailMap = new HashMap<>();

	public ServiceBranch() {
		super();
	}

	public ServiceBranch copyEntity() {
		ServiceBranch entity = new ServiceBranch();

		entity.setId(this.id);
		entity.setCode(this.code);
		entity.setDescription(this.description);
		entity.setOfcOrHouseNum(this.ofcOrHouseNum);
		entity.setFlatNum(this.flatNum);
		entity.setStreet(this.street);
		entity.setAddrLine1(this.addrLine1);
		entity.setAddrLine2(this.addrLine2);
		entity.setPoBox(this.poBox);
		entity.setCity(this.city);
		entity.setCountry(this.country);
		entity.setCpProvince(this.cpProvince);
		entity.setPinCodeId(this.pinCodeId);
		entity.setPinCode(this.pinCode);
		entity.setActive(this.active);
		entity.setFolderPath(this.folderPath);
		entity.setBefImage(this.befImage);
		entity.setUserDetails(this.userDetails);
		entity.setCreatedBy(this.createdBy);
		entity.setCreatedOn(this.createdOn);
		entity.setApprovedBy(this.approvedBy);
		entity.setApprovedOn(this.approvedOn);
		entity.setServiceBranchLoanTypeList(this.serviceBranchLoanTypeList);
		entity.setLovDescAuditDetailMap(this.lovDescAuditDetailMap);

		return entity;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("serviceBranchLoanTypeList");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOfcOrHouseNum() {
		return ofcOrHouseNum;
	}

	public void setOfcOrHouseNum(String ofcOrHouseNum) {
		this.ofcOrHouseNum = ofcOrHouseNum;
	}

	public String getFlatNum() {
		return flatNum;
	}

	public void setFlatNum(String flatNum) {
		this.flatNum = flatNum;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getAddrLine1() {
		return addrLine1;
	}

	public void setAddrLine1(String addrLine1) {
		this.addrLine1 = addrLine1;
	}

	public String getAddrLine2() {
		return addrLine2;
	}

	public void setAddrLine2(String addrLine2) {
		this.addrLine2 = addrLine2;
	}

	public String getPoBox() {
		return poBox;
	}

	public void setPoBox(String poBox) {
		this.poBox = poBox;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCpProvince() {
		return cpProvince;
	}

	public void setCpProvince(String cpProvince) {
		this.cpProvince = cpProvince;
	}

	public Long getPinCodeId() {
		return pinCodeId;
	}

	public void setPinCodeId(Long pinCodeId) {
		this.pinCodeId = pinCodeId;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public ServiceBranch getBefImage() {
		return befImage;
	}

	public void setBefImage(ServiceBranch befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Timestamp getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn;
	}

	public List<ServiceBranchesLoanType> getServiceBranchLoanTypeList() {
		return serviceBranchLoanTypeList;
	}

	public void setServiceBranchLoanTypeList(List<ServiceBranchesLoanType> serviceBranchLoanTypeList) {
		this.serviceBranchLoanTypeList = serviceBranchLoanTypeList;
	}

	public Map<String, List<AuditDetail>> getLovDescAuditDetailMap() {
		return lovDescAuditDetailMap;
	}

	public void setLovDescAuditDetailMap(Map<String, List<AuditDetail>> lovDescAuditDetailMap) {
		this.lovDescAuditDetailMap = lovDescAuditDetailMap;
	}
}
