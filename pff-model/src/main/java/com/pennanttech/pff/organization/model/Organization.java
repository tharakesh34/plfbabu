package com.pennanttech.pff.organization.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class Organization extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long organizationId;
	private Long custId;
	private String cif;
	private String name;
	private Date date_Incorporation;
	private Integer type;
	private String code;
	private long createdBy;
	private Date createdOn;
	private String custShrtName;
	@XmlTransient
	private Organization befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	private ExtendedFieldHeader extendedFieldHeader;
	private ExtendedFieldRender extendedFieldRender;

	private IncomeExpenseDetail schoolIncomeExpense;
	private List<IncomeExpenseDetail> coreIncomeList = new ArrayList<>();
	private List<IncomeExpenseDetail> nonCoreIncomeList = new ArrayList<>();
	private List<IncomeExpenseDetail> expenseList = new ArrayList<>();

	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	public Organization() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("organizationId");
		excludeFields.add("cif");
		excludeFields.add("custShrtName");
		excludeFields.add("extendedFieldHeader");
		excludeFields.add("extendedFieldRender");
		excludeFields.add("schoolIncomeExpense");
		excludeFields.add("coreIncomeList");
		excludeFields.add("nonCoreIncomeList");
		excludeFields.add("expenseList");
		return excludeFields;
	}

	public long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(long organizationId) {
		this.organizationId = organizationId;
	}

	public Long getCustId() {
		return custId;
	}

	public void setCustId(Long custId) {
		this.custId = custId;
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate_Incorporation() {
		return date_Incorporation;
	}

	public void setDate_Incorporation(Date date_Incorporation) {
		this.date_Incorporation = date_Incorporation;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public long getId() {
		return organizationId;
	}

	public void setId(long id) {
		this.organizationId = id;
	}

	public Organization getBefImage() {
		return befImage;
	}

	public void setBefImage(Organization befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}

	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	public ExtendedFieldRender getExtendedFieldRender() {
		return extendedFieldRender;
	}

	public void setExtendedFieldRender(ExtendedFieldRender extendedFieldRender) {
		this.extendedFieldRender = extendedFieldRender;
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

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

}
