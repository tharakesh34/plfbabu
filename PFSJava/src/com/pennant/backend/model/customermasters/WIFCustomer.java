package com.pennant.backend.model.customermasters;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.util.PennantConstants;

public class WIFCustomer implements Serializable {

    private static final long serialVersionUID = 3847855909869736407L;
    
    private long custID = Long.MIN_VALUE;
    private long existCustID = 0;
    private String custCRCPR = "";
    private String custShrtName = "";
    private Date custDOB = DateUtility.getUtilDate("01/01/1900", PennantConstants.dateFormat);
    private String custGenderCode = "";
    private String custNationality = "";
    private String custBaseCcy = "";
    private String custEmpSts = "";
    private long custEmpAloc;
    private String custTypeCode = "";
    private String custCtgCode = "";
    private String custMaritalSts = "";
    private int noOfDependents = 0;
    private boolean custIsBlackListed = false;
    private Date custBlackListDate = DateUtility.getUtilDate("01/01/1900", PennantConstants.dateFormat);
    private String custSector = "";
    private String custSubSector = "";
    private boolean jointCust = false;
    private boolean elgRequired = false;
    private boolean isNewRecord = true;

    private String lovDescCustCtgType = PennantConstants.CUST_CAT_INDIVIDUAL;
    private String lovDescCustGenderCodeName;
    private String lovDescCustNationalityName;
    private String lovDescCustBaseCcyName;
    private String lovDescCustEmpStsName;
    private String lovDescCustEmpAlocName;
    private String lovDescCustEmpName;
    private String lovDescCustTypeCodeName;
    private String lovDescCustCtgCodeName;
    private String lovDescCustMaritalStsName;
    private String lovDescCustSectorName;
    private String lovDescCustSubSectorName;
    
    private BigDecimal custRepayOther = BigDecimal.ZERO;
	private BigDecimal custRepayBank = BigDecimal.ZERO;
	private BigDecimal custTotalExpense = BigDecimal.ZERO;
	private BigDecimal custTotalIncome = BigDecimal.ZERO;
    
    private List<CustomerIncome> customerIncomeList;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
    
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
	
	public String getLovDescCustBaseCcyName() {
    	return lovDescCustBaseCcyName;
    }
	public void setLovDescCustBaseCcyName(String lovDescCustBaseCcyName) {
    	this.lovDescCustBaseCcyName = lovDescCustBaseCcyName;
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
	public void setCustEmpAloc(long custEmpAloc) {
	    this.custEmpAloc = custEmpAloc;
    }
	public long getCustEmpAloc() {
	    return custEmpAloc;
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
	public String getLovDescCustEmpName() {
	    return lovDescCustEmpName;
    }

}
