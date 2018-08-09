package com.pennanttech.pff.organization.school.model;

import java.math.BigDecimal;
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

public class IncomeExpenseDetail extends AbstractWorkflowEntity{
	private static final long serialVersionUID = 1L;
	
	private long id;
	private Long headerId;
	private String incomeExpense;
	private String type;
	private String category;
	private int units;
	private BigDecimal unitPrice = BigDecimal.ZERO;
	private int frequency;
	private Long loockUpId;
	private boolean consider;
	private long createdBy;
	private Date createdOn;
	private long orgId;
	private Long custId;
	private String custCif;
	private String custShrtName;
	private int financialYear;
	private int incomeType;
	private boolean coreIncome;
	
	private String name;
	
	@XmlTransient
	private IncomeExpenseDetail befImage;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private LoggedInUser userDetails;
	private IncomeExpenseDetail schoolIncomeExpense;
	private List<IncomeExpenseDetail> coreIncomeList = new ArrayList<>();
	private List<IncomeExpenseDetail> nonCoreIncomeList = new ArrayList<>();
	private List<IncomeExpenseDetail> expenseList = new ArrayList<>();
	
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	
	public IncomeExpenseDetail() {
		super();
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("orgId");
		excludeFields.add("name");
		excludeFields.add("custId");
		excludeFields.add("custCif");
		excludeFields.add("custShrtName");
		excludeFields.add("incomeType");
		excludeFields.add("financialYear");
		excludeFields.add("coreIncome");
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

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
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
	
	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getIncomeType() {
		return incomeType;
	}

	public void setIncomeType(int incomeType) {
		this.incomeType = incomeType;
	}

	public int getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(int financialYear) {
		this.financialYear = financialYear;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public boolean isCoreIncome() {
		return coreIncome;
	}

	public void setCoreIncome(boolean coreIncome) {
		this.coreIncome = coreIncome;
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public boolean isConsider() {
		return consider;
	}

	public void setConsider(boolean consider) {
		this.consider = consider;
	}

	public Long getLoockUpId() {
		return loockUpId;
	}

	public void setLoockUpId(Long loockUpId) {
		this.loockUpId = loockUpId;
	}

	public Long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(Long headerId) {
		this.headerId = headerId;
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

	public String getIncomeExpense() {
		return incomeExpense;
	}

	public void setIncomeExpense(String incomeExpense) {
		this.incomeExpense = incomeExpense;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public IncomeExpenseDetail getBefImage() {
		return befImage;
	}

	public void setBefImage(IncomeExpenseDetail befImage) {
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

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}
	
	public void setLoginDetails(LoggedInUser userDetails){
		setLastMntBy(userDetails.getUserId());
		this.userDetails=userDetails;
	}
	
}
