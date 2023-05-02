package com.pennant.backend.model.customermasters;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class WIFCustomer implements Serializable {

	private static final long serialVersionUID = 3847855909869736407L;

	private long custID = Long.MIN_VALUE;
	private long existCustID = 0;
	private String custCRCPR = "";
	private String custShrtName = "";
	private Date custDOB;
	private String custGenderCode = "";
	private String custNationality = "";
	private String custBaseCcy = "";
	private String custEmpSts = "";
	private String custTypeCode = "";
	private String custCtgCode = "";
	private String custMaritalSts = "";
	private int noOfDependents = 0;
	private boolean custIsBlackListed = false;
	private Date custBlackListDate;
	private String custSector = "";
	private String custSubSector = "";
	private String custSegment;
	private boolean jointCust = false;
	private boolean elgRequired = false;
	private boolean isNewRecord = false;
	private String custSalutationCode;
	private boolean salariedCustomer;
	private long empName;
	private String empDesg;
	private String empDept;
	private BigDecimal totalIncome;
	private BigDecimal totalExpense;

	private String lovDescCustCtgType = "RETAIL";// FIXME:How to use constants-PennantConstants.PFF_CUSTCTG_INDIV;
	private String lovDescCustGenderCodeName;
	private String lovDescCustNationalityName;
	private String lovDescCustEmpStsName;
	private String lovDescCustEmpAlocName;
	private String lovDescCustEmpName;
	private String lovDescCustTypeCodeName;
	private String lovDescCustCtgCodeName;
	private String lovDescCustMaritalStsName;
	private String lovDescCustSectorName;
	private String lovDescCustSubSectorName;
	private String lovDescEmpName;
	private String lovDescEmpDesg;
	private String lovDescEmpDept;
	private String lovDescCustSegmentName;
	private long createdBy;
	private Timestamp createdOn;
	private long approvedBy;
	private Timestamp approvedOn;

	private BigDecimal custRepayOther = BigDecimal.ZERO;
	private BigDecimal custRepayBank = BigDecimal.ZERO;
	private BigDecimal custTotalExpense = BigDecimal.ZERO;
	private BigDecimal custTotalIncome = BigDecimal.ZERO;
	private BigDecimal curFinRepayAmt = BigDecimal.ZERO;

	private List<CustomerIncome> customerIncomeList;

	public WIFCustomer() {

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getCustCRCPR() {
		return custCRCPR;
	}

	public void setCustCRCPR(String custCRCPR) {
		this.custCRCPR = custCRCPR;
	}

	public Date getCustDOB() {
		return custDOB;
	}

	public void setCustDOB(Date custDOB) {
		this.custDOB = custDOB;
	}

	public String getCustGenderCode() {
		return custGenderCode;
	}

	public void setCustGenderCode(String custGenderCode) {
		this.custGenderCode = custGenderCode;
	}

	public String getCustNationality() {
		return custNationality;
	}

	public void setCustNationality(String custNationality) {
		this.custNationality = custNationality;
	}

	public String getCustBaseCcy() {
		return custBaseCcy;
	}

	public void setCustBaseCcy(String custBaseCcy) {
		this.custBaseCcy = custBaseCcy;
	}

	public String getCustEmpSts() {
		return custEmpSts;
	}

	public void setCustEmpSts(String custEmpSts) {
		this.custEmpSts = custEmpSts;
	}

	public String getCustTypeCode() {
		return custTypeCode;
	}

	public void setCustTypeCode(String custTypeCode) {
		this.custTypeCode = custTypeCode;
	}

	public String getCustCtgCode() {
		return custCtgCode;
	}

	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}

	public String getCustMaritalSts() {
		return custMaritalSts;
	}

	public void setCustMaritalSts(String custMaritalSts) {
		this.custMaritalSts = custMaritalSts;
	}

	public int getNoOfDependents() {
		return noOfDependents;
	}

	public void setNoOfDependents(int noOfDependents) {
		this.noOfDependents = noOfDependents;
	}

	public boolean isCustIsBlackListed() {
		return custIsBlackListed;
	}

	public void setCustIsBlackListed(boolean custIsBlackListed) {
		this.custIsBlackListed = custIsBlackListed;
	}

	public Date getCustBlackListDate() {
		return custBlackListDate;
	}

	public void setCustBlackListDate(Date custBlackListDate) {
		this.custBlackListDate = custBlackListDate;
	}

	public String getCustSector() {
		return custSector;
	}

	public void setCustSector(String custSector) {
		this.custSector = custSector;
	}

	public String getCustSubSector() {
		return custSubSector;
	}

	public void setCustSubSector(String custSubSector) {
		this.custSubSector = custSubSector;
	}

	public boolean isJointCust() {
		return jointCust;
	}

	public void setJointCust(boolean jointCust) {
		this.jointCust = jointCust;
	}

	public void setLovDescCustCtgType(String lovDescCustCtgType) {
		this.lovDescCustCtgType = lovDescCustCtgType;
	}

	public String getLovDescCustCtgType() {
		return lovDescCustCtgType;
	}

	public String getLovDescCustGenderCodeName() {
		return lovDescCustGenderCodeName;
	}

	public void setLovDescCustGenderCodeName(String lovDescCustGenderCodeName) {
		this.lovDescCustGenderCodeName = lovDescCustGenderCodeName;
	}

	public String getLovDescCustNationalityName() {
		return lovDescCustNationalityName;
	}

	public void setLovDescCustNationalityName(String lovDescCustNationalityName) {
		this.lovDescCustNationalityName = lovDescCustNationalityName;
	}

	public String getLovDescCustEmpStsName() {
		return lovDescCustEmpStsName;
	}

	public void setLovDescCustEmpStsName(String lovDescCustEmpStsName) {
		this.lovDescCustEmpStsName = lovDescCustEmpStsName;
	}

	public String getLovDescCustTypeCodeName() {
		return lovDescCustTypeCodeName;
	}

	public void setLovDescCustTypeCodeName(String lovDescCustTypeCodeName) {
		this.lovDescCustTypeCodeName = lovDescCustTypeCodeName;
	}

	public String getLovDescCustCtgCodeName() {
		return lovDescCustCtgCodeName;
	}

	public void setLovDescCustCtgCodeName(String lovDescCustCtgCodeName) {
		this.lovDescCustCtgCodeName = lovDescCustCtgCodeName;
	}

	public String getLovDescCustMaritalStsName() {
		return lovDescCustMaritalStsName;
	}

	public void setLovDescCustMaritalStsName(String lovDescCustMaritalStsName) {
		this.lovDescCustMaritalStsName = lovDescCustMaritalStsName;
	}

	public String getLovDescCustSectorName() {
		return lovDescCustSectorName;
	}

	public void setLovDescCustSectorName(String lovDescCustSectorName) {
		this.lovDescCustSectorName = lovDescCustSectorName;
	}

	public String getLovDescCustSubSectorName() {
		return lovDescCustSubSectorName;
	}

	public void setLovDescCustSubSectorName(String lovDescCustSubSectorName) {
		this.lovDescCustSubSectorName = lovDescCustSubSectorName;
	}

	public void setCustomerIncomeList(List<CustomerIncome> customerIncomeList) {
		this.customerIncomeList = customerIncomeList;
	}

	public List<CustomerIncome> getCustomerIncomeList() {
		return customerIncomeList;
	}

	public void setNewRecord(boolean isNewRecord) {
		this.isNewRecord = isNewRecord;
	}

	public boolean isNewRecord() {
		return isNewRecord;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setExistCustID(long existCustID) {
		this.existCustID = existCustID;
	}

	public long getExistCustID() {
		return existCustID;
	}

	public BigDecimal getCustRepayOther() {
		return custRepayOther;
	}

	public void setCustRepayOther(BigDecimal custRepayOther) {
		this.custRepayOther = custRepayOther;
	}

	public BigDecimal getCustRepayBank() {
		return custRepayBank;
	}

	public void setCustRepayBank(BigDecimal custRepayBank) {
		this.custRepayBank = custRepayBank;
	}

	public BigDecimal getCustTotalExpense() {
		return custTotalExpense;
	}

	public void setCustTotalExpense(BigDecimal custTotalExpense) {
		this.custTotalExpense = custTotalExpense;
	}

	public BigDecimal getCustTotalIncome() {
		return custTotalIncome;
	}

	public void setCustTotalIncome(BigDecimal custTotalIncome) {
		this.custTotalIncome = custTotalIncome;
	}

	public void setElgRequired(boolean elgRequired) {
		this.elgRequired = elgRequired;
	}

	public boolean isElgRequired() {
		return elgRequired;
	}

	public void setLovDescCustEmpAlocName(String lovDescCustEmpAlocName) {
		this.lovDescCustEmpAlocName = lovDescCustEmpAlocName;
	}

	public String getLovDescCustEmpAlocName() {
		return lovDescCustEmpAlocName;
	}

	public void setLovDescCustEmpName(String lovDescCustEmpName) {
		this.lovDescCustEmpName = lovDescCustEmpName;
	}

	public String getCustSalutationCode() {
		return custSalutationCode;
	}

	public void setCustSalutationCode(String custSalutationCode) {
		this.custSalutationCode = custSalutationCode;
	}

	public boolean isSalariedCustomer() {
		return salariedCustomer;
	}

	public void setSalariedCustomer(boolean salariedCustomer) {
		this.salariedCustomer = salariedCustomer;
	}

	public long getEmpName() {
		return empName;
	}

	public void setEmpName(long empName) {
		this.empName = empName;
	}

	public String getEmpDesg() {
		return empDesg;
	}

	public void setEmpDesg(String empDesg) {
		this.empDesg = empDesg;
	}

	public String getEmpDept() {
		return empDept;
	}

	public void setEmpDept(String empDept) {
		this.empDept = empDept;
	}

	public BigDecimal getTotalIncome() {
		return totalIncome;
	}

	public void setTotalIncome(BigDecimal totalIncome) {
		this.totalIncome = totalIncome;
	}

	public BigDecimal getTotalExpense() {
		return totalExpense;
	}

	public void setTotalExpense(BigDecimal totalExpense) {
		this.totalExpense = totalExpense;
	}

	public String getLovDescEmpName() {
		return lovDescEmpName;
	}

	public void setLovDescEmpName(String lovDescEmpName) {
		this.lovDescEmpName = lovDescEmpName;
	}

	public String getLovDescEmpDesg() {
		return lovDescEmpDesg;
	}

	public void setLovDescEmpDesg(String lovDescEmpDesg) {
		this.lovDescEmpDesg = lovDescEmpDesg;
	}

	public String getLovDescEmpDept() {
		return lovDescEmpDept;
	}

	public void setLovDescEmpDept(String lovDescEmpDept) {
		this.lovDescEmpDept = lovDescEmpDept;
	}

	public String getLovDescCustEmpName() {
		return lovDescCustEmpName;
	}

	public BigDecimal getCurFinRepayAmt() {
		return curFinRepayAmt;
	}

	public void setCurFinRepayAmt(BigDecimal curFinRepayAmt) {
		this.curFinRepayAmt = curFinRepayAmt;
	}

	public String getCustSegment() {
		return custSegment;
	}

	public void setCustSegment(String custSegment) {
		this.custSegment = custSegment;
	}

	public String getLovDescCustSegmentName() {
		return lovDescCustSegmentName;
	}

	public void setLovDescCustSegmentName(String lovDescCustSegmentName) {
		this.lovDescCustSegmentName = lovDescCustSegmentName;
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

}
