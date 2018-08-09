package com.pennanttech.pff.organization.school.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class IncomeExpenseHeader extends AbstractWorkflowEntity{
	private static final long serialVersionUID = 1L;
	
	private long id;
	private Long orgId;
	private Long custId;
	private String custCif;
	private int financialYear;
	private long createdBy;
	private Date createdOn;
	private String type;
	private String name;
	@XmlTransient
	private IncomeExpenseHeader befImage;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private LoggedInUser userDetails;
	private IncomeExpenseDetail schoolIncomeExpense;
	private List<IncomeExpenseDetail> coreIncomeList = new ArrayList<>();
	private List<IncomeExpenseDetail> nonCoreIncomeList = new ArrayList<>();
	private List<IncomeExpenseDetail> expenseList = new ArrayList<>();
	
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	
	public IncomeExpenseHeader() {
		super();
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("type");
		excludeFields.add("custId");
		excludeFields.add("custCif");
		excludeFields.add("name");
		excludeFields.add("schoolIncomeExpense");
		excludeFields.add("coreIncomeList");
		excludeFields.add("nonCoreIncomeList");
		excludeFields.add("expenseList");
		return excludeFields;
	}
	
	public boolean isNew() {
		return isNewRecord();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public Long getCustId() {
		return custId;
	}

	public void setCustId(Long custId) {
		this.custId = custId;
	}
	
	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public int getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(int financialYear) {
		this.financialYear = financialYear;
	}
	
	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IncomeExpenseHeader getBefImage() {
		return befImage;
	}

	public void setBefImage(IncomeExpenseHeader befImage) {
		this.befImage = befImage;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public IncomeExpenseDetail getSchoolIncomeExpense() {
		return schoolIncomeExpense;
	}

	public void setSchoolIncomeExpense(IncomeExpenseDetail schoolIncomeExpense) {
		this.schoolIncomeExpense = schoolIncomeExpense;
	}

	public List<IncomeExpenseDetail> getCoreIncomeList() {
		return coreIncomeList;
	}

	public void setCoreIncomeList(List<IncomeExpenseDetail> coreIncomeList) {
		this.coreIncomeList = coreIncomeList;
	}

	public List<IncomeExpenseDetail> getNonCoreIncomeList() {
		return nonCoreIncomeList;
	}

	public void setNonCoreIncomeList(List<IncomeExpenseDetail> nonCoreIncomeList) {
		this.nonCoreIncomeList = nonCoreIncomeList;
	}

	public List<IncomeExpenseDetail> getExpenseList() {
		return expenseList;
	}

	public void setExpenseList(List<IncomeExpenseDetail> expenseList) {
		this.expenseList = expenseList;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}
}
