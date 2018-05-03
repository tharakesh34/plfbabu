package com.pennant.backend.model.mail;

import java.util.HashMap;

public class MailTemplateData {

	private String finReference;
	private String finAmount;
	private String finCcy;
	private String finStartDate;
	private String maturityDate;
	private String numberOfTerms;
	private String graceTerms;
	private String effectiveRate;
	private String custShrtName;
	private String downPayment;
	private String feeAmount;
	private String insAmount;
	private String finType;

	private String usrName;
	private String usrRole;
	private String prevUsrName;
	private long   prevUsrRole=0;
	private String nextUsrName;
	private String nextUsrRole;
	private String nextUsrRoleCode;
	private String workFlowType;
	private int	  priority;
	
	//facility
	private String totAmountBD;
	private String totAmountUSD;
	private String cafReference;
	private String countryOfDomicileName;
	private String countryOfRiskName;
	private String countryManagerName;
	private String customerGroupName;
	private String natureOfBusinessName;
	
	private String recommendations;
	private String finPurpose;
	private String finCommitmentRef;
	private String finBranch;
	
	//Credit Review
	private String custCIF;
	private String auditors;
	private String location;
	private String auditType;
	private String auditedDate;
	private String auditYear;
	private int auditPeriod;
	
	//Treasury Investment
	private String investmentRef;
	private String totPrincipalAmt;
	private String startDate;
	private String principalInvested;
	private String principalMaturity;
	private String principalDueToInvest;
	private String avgPftRate;
	
	//Provision 
	private String principalDue;
	private String profitDue;
	private String totalDue;
	private String dueFromDate;
	private String nonFormulaProv;
	private String provisionedAmt;
	private String provisionedAmtCal;
	
	//Manual Suspense
	private String manualSusp;
	private String finSuspDate;
	private String finSuspAmt;
	private String finCurSuspAmt;
	
	//PO Authorization
	private String bankName;
	private String product;
	private String takeoverAmount;
	private String rate;
	private String custPortion;
	
	//External Usage
	private String roleCode = "";
	private long custId = 0;
	private String rcdMaintainSts;
	private String recieveMail;
	private String facilityType;
	private String finCurODAmt;
	private int finCurODDays;
	
	public MailTemplateData() {
		
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }
	
	public String getFinAmount() {
    	return finAmount;
    }
	public void setFinAmount(String finAmount) {
    	this.finAmount = finAmount;
    }
	
	public String getFinCcy() {
    	return finCcy;
    }
	public void setFinCcy(String finCcy) {
    	this.finCcy = finCcy;
    }
	
	public String getFinStartDate() {
    	return finStartDate;
    }
	public void setFinStartDate(String finStartDate) {
    	this.finStartDate = finStartDate;
    }
	
	public String getMaturityDate() {
    	return maturityDate;
    }
	public void setMaturityDate(String maturityDate) {
    	this.maturityDate = maturityDate;
    }
	
	public String getNumberOfTerms() {
    	return numberOfTerms;
    }
	public void setNumberOfTerms(String numberOfTerms) {
    	this.numberOfTerms = numberOfTerms;
    }
	
	public String getEffectiveRate() {
    	return effectiveRate;
    }
	public void setEffectiveRate(String effectiveRate) {
    	this.effectiveRate = effectiveRate;
    }
	
	public String getCustShrtName() {
    	return custShrtName;
    }
	public void setCustShrtName(String custShrtName) {
    	this.custShrtName = custShrtName;
    }
	
	public String getDownPayment() {
    	return downPayment;
    }
	public void setDownPayment(String downPayment) {
    	this.downPayment = downPayment;
    }
	
	public String getFeeAmount() {
    	return feeAmount;
    }
	public void setFeeAmount(String feeAmount) {
    	this.feeAmount = feeAmount;
    }

	public void setCustId(long custId) {
	    this.custId = custId;
    }
	public long getCustId() {
	    return custId;
    }
	
	public void setRoleCode(String roleCode) {
	    this.roleCode = roleCode;
    }
	public String getRoleCode() {
	    return roleCode;
    }
	
	public String getUsrName() {
    	return usrName;
    }
	public void setUsrName(String usrName) {
    	this.usrName = usrName;
    }
	public String getUsrRole() {
    	return usrRole;
    }
	public void setUsrRole(String usrRole) {
    	this.usrRole = usrRole;
    }
	public String getPrevUsrName() {
    	return prevUsrName;
    }
	public void setPrevUsrName(String prevUsrName) {
    	this.prevUsrName = prevUsrName;
    }
	public long getPrevUsrRole() {
    	return prevUsrRole;
    }
	public void setPrevUsrRole(long prevUsrRole) {
    	this.prevUsrRole = prevUsrRole;
    }
	public String getNextUsrName() {
    	return nextUsrName;
    }
	public void setNextUsrName(String nextUsrName) {
    	this.nextUsrName = nextUsrName;
    }
	public String getNextUsrRole() {
    	return nextUsrRole;
    }
	public void setNextUsrRole(String nextUsrRole) {
    	this.nextUsrRole = nextUsrRole;
    }

	public String getTotAmountBD() {
	    return totAmountBD;
    }
	public void setTotAmountBD(String totAmountBD) {
	    this.totAmountBD = totAmountBD;
    }
	public String getTotAmountUSD() {
	    return totAmountUSD;
    }
	public void setTotAmountUSD(String totAmountUSD) {
	    this.totAmountUSD = totAmountUSD;
    }
	public void setCafReference(String cAFReference) {
	    this.cafReference = cAFReference;
    }
	public String getCafReference() {
	    return cafReference;
    }
	public String getWorkflowType() {
		return workFlowType;
	}
	public void setWorkflowType(String workflowType) {
		this.workFlowType = workflowType;
	}
	public String getNextUsrRoleCode() {
    	return nextUsrRoleCode;
    }
	public void setNextUsrRoleCode(String nextUsrRoleCode) {
    	this.nextUsrRoleCode = nextUsrRoleCode;
    }
	public String getFacilityType() {
	    return facilityType;
    }
	public void setFacilityType(String facilityType) {
	    this.facilityType = facilityType;
    }
	public String getCountryOfDomicileName() {
	    return countryOfDomicileName;
    }
	public void setCountryOfDomicileName(String countryOfDomicileName) {
	    this.countryOfDomicileName = countryOfDomicileName;
    }
	public String getCountryOfRiskName() {
	    return countryOfRiskName;
    }
	public void setCountryOfRiskName(String countryOfRiskName) {
	    this.countryOfRiskName = countryOfRiskName;
    }
	public String getCountryManagerName() {
	    return countryManagerName;
    }
	public void setCountryManagerName(String countryManagerName) {
	    this.countryManagerName = countryManagerName;
    }
	public String getCustomerGroupName() {
	    return customerGroupName;
    }
	public void setCustomerGroupName(String customerGroupName) {
	    this.customerGroupName = customerGroupName;
    }
	public String getNatureOfBusinessName() {
	    return natureOfBusinessName;
    }
	public void setNatureOfBusinessName(String natureOfBusinessName) {
	    this.natureOfBusinessName = natureOfBusinessName;
    }
	public String getRecommendations() {
    	return recommendations;
    }
	public void setRecommendations(String recommendations) {
    	this.recommendations = recommendations;
    }
	public String getFinPurpose() {
    	return finPurpose;
    }
	public void setFinPurpose(String finPurpose) {
    	this.finPurpose = finPurpose;
    }
	public String getFinCommitmentRef() {
    	return finCommitmentRef;
    }
	public void setFinCommitmentRef(String finCommitmentRef) {
    	this.finCommitmentRef = finCommitmentRef;
    }
	public String getFinBranch() {
    	return finBranch;
    }
	public void setFinBranch(String finBranch) {
    	this.finBranch = finBranch;
    }
	public String getCustCIF() {
    	return custCIF;
    }
	public void setCustCIF(String custCIF) {
    	this.custCIF = custCIF;
    }
	public String getAuditors() {
    	return auditors;
    }
	public void setAuditors(String auditors) {
    	this.auditors = auditors;
    }
	public String getLocation() {
    	return location;
    }
	public void setLocation(String location) {
    	this.location = location;
    }
	public String getAuditType() {
    	return auditType;
    }
	public void setAuditType(String auditType) {
    	this.auditType = auditType;
    }
	public String getAuditedDate() {
    	return auditedDate;
    }
	public void setAuditedDate(String auditedDate) {
    	this.auditedDate = auditedDate;
    }
	public String getAuditYear() {
    	return auditYear;
    }
	public void setAuditYear(String auditYear) {
    	this.auditYear = auditYear;
    }
	public int getAuditPeriod() {
    	return auditPeriod;
    }
	public void setAuditPeriod(int auditPeriod) {
    	this.auditPeriod = auditPeriod;
    }
	public String getInvestmentRef() {
    	return investmentRef;
    }
	public void setInvestmentRef(String investmentRef) {
    	this.investmentRef = investmentRef;
    }
	public String getTotPrincipalAmt() {
    	return totPrincipalAmt;
    }
	public void setTotPrincipalAmt(String totPrincipalAmt) {
    	this.totPrincipalAmt = totPrincipalAmt;
    }
	public String getStartDate() {
    	return startDate;
    }
	public void setStartDate(String startDate) {
    	this.startDate = startDate;
    }
	public String getPrincipalInvested() {
    	return principalInvested;
    }
	public void setPrincipalInvested(String principalInvested) {
    	this.principalInvested = principalInvested;
    }
	public String getPrincipalMaturity() {
    	return principalMaturity;
    }
	public void setPrincipalMaturity(String principalMaturity) {
    	this.principalMaturity = principalMaturity;
    }
	public String getPrincipalDueToInvest() {
    	return principalDueToInvest;
    }
	public void setPrincipalDueToInvest(String principalDueToInvest) {
    	this.principalDueToInvest = principalDueToInvest;
    }
	public String getAvgPftRate() {
    	return avgPftRate;
    }
	public void setAvgPftRate(String avgPftRate) {
    	this.avgPftRate = avgPftRate;
    }
	
	public String getGraceTerms() {
	    return graceTerms;
    }
	public void setGraceTerms(String graceTerms) {
	    this.graceTerms = graceTerms;
    }
	
	public String getRcdMaintainSts() {
    	return rcdMaintainSts;
    }
	public void setRcdMaintainSts(String rcdMaintainSts) {
    	this.rcdMaintainSts = rcdMaintainSts;
    }
	
	public String getPrincipalDue() {
    	return principalDue;
    }
	public void setPrincipalDue(String principalDue) {
    	this.principalDue = principalDue;
    }
	
	public String getProfitDue() {
    	return profitDue;
    }
	public void setProfitDue(String profitDue) {
    	this.profitDue = profitDue;
    }
	
	public String getTotalDue() {
    	return totalDue;
    }
	public void setTotalDue(String totalDue) {
    	this.totalDue = totalDue;
    }
	
	public String getDueFromDate() {
    	return dueFromDate;
    }
	public void setDueFromDate(String dueFromDate) {
    	this.dueFromDate = dueFromDate;
    }
	
	public String getNonFormulaProv() {
    	return nonFormulaProv;
    }
	public void setNonFormulaProv(String nonFormulaProv) {
    	this.nonFormulaProv = nonFormulaProv;
    }
	
	public String getProvisionedAmt() {
    	return provisionedAmt;
    }
	public void setProvisionedAmt(String provisionedAmt) {
    	this.provisionedAmt = provisionedAmt;
    }
	
	public String getProvisionedAmtCal() {
    	return provisionedAmtCal;
    }
	public void setProvisionedAmtCal(String provisionedAmtCal) {
    	this.provisionedAmtCal = provisionedAmtCal;
    }
	
	public String getManualSusp() {
    	return manualSusp;
    }
	public void setManualSusp(String manualSusp) {
    	this.manualSusp = manualSusp;
    }
	
	public String getFinSuspDate() {
    	return finSuspDate;
    }
	public void setFinSuspDate(String finSuspDate) {
    	this.finSuspDate = finSuspDate;
    }
	
	public String getFinSuspAmt() {
    	return finSuspAmt;
    }
	public void setFinSuspAmt(String finSuspAmt) {
    	this.finSuspAmt = finSuspAmt;
    }
	
	public String getFinCurSuspAmt() {
		return finCurSuspAmt;
	}
	public void setFinCurSuspAmt(String finCurSuspAmt) {
		this.finCurSuspAmt = finCurSuspAmt;
	}
	
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}

	public String getTakeoverAmount() {
		return takeoverAmount;
	}
	public void setTakeoverAmount(String takeoverAmount) {
		this.takeoverAmount = takeoverAmount;
	}

	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getCustPortion() {
		return custPortion;
	}
	public void setCustPortion(String custPortion) {
		this.custPortion = custPortion;
	}

	public String getRecieveMail() {
	    return recieveMail;
    }
	public void setRecieveMail(String recieveMail) {
	    this.recieveMail = recieveMail;
    }
	
	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> customerScoringMap = new HashMap<String, Object>();	
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				customerScoringMap.put(this.getClass().getDeclaredFields()[i].getName(),
						this.getClass().getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}
		return customerScoringMap;
	}
	public int getPriority() {
	    return priority;
    }
	public void setPriority(int priority) {
	    this.priority = priority;
    }

	public String getFinCurODAmt() {
		return finCurODAmt;
	}

	public void setFinCurODAmt(String finCurODAmt) {
		this.finCurODAmt = finCurODAmt;
	}

	public int getFinCurODDays() {
		return finCurODDays;
	}

	public void setFinCurODDays(int finCurODDays) {
		this.finCurODDays = finCurODDays;
	}
	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getInsAmount() {
		return insAmount;
	}

	public void setInsAmount(String insAmount) {
		this.insAmount = insAmount;
	}
}
	
