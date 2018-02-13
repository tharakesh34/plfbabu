/**


 * Copyright 2011 - Pennant Technologies This file is part of Pennant Java Application Framework and related Products.
 * All components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies. Copyright and other intellectual property laws protect these materials. Reproduction or retransmission
 * of the materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * * FileName : FinanceType.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 30-06-2011 * * Modified Date :
 * 30-06-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 30-06-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.model.rmtmasters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.applicationmaster.FinTypeInsurances;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.financemanagement.FinTypeVASProducts;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/** Model class for the <b>FinanceType table</b>.<br> */
@XmlAccessorType(XmlAccessType.FIELD)
public class FinanceType extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -4098586745401583126L;
	
	//	Ordered Same AS Table please don't break It
	private String product = "";
	private String finType;
	private String finCategory;
	private String finCategoryDesc;
	private String finTypeDesc;
	private String finCcy;
	private String finDaysCalType;
	private String finAcType;
	private String finContingentAcType;
	private String finSuspAcType;
	private String finBankContingentAcType;
	private String finProvisionAcType;
	private String pftPayAcType;
	private boolean finIsOpenPftPayAcc;
	private String finDivision;
	private boolean finIsGenRef;
	private boolean finIsOpenNewFinAc;
	private BigDecimal finMaxAmount = BigDecimal.ZERO;
	private BigDecimal finMinAmount = BigDecimal.ZERO;
	private String finDftStmtFrq;
	private boolean finIsAlwMD;
	private int finHistRetension;
	private boolean equalRepayment = true;
	private String finAssetType;
	private boolean finIsDwPayRequired;
	private long downPayRule;
	private String downPayRuleDesc;
	private boolean fInIsAlwGrace;
	private String finRateType;
	private String finBaseRate;
	private String finSplRate;
	private BigDecimal finMargin = BigDecimal.ZERO;
	private BigDecimal finIntRate = BigDecimal.ZERO;
	private BigDecimal fInMinRate = BigDecimal.ZERO;
	private BigDecimal finMaxRate = BigDecimal.ZERO;
	private String finDftIntFrq;
	private String finSchdMthd;
	private boolean finIsIntCpz;
	private String finCpzFrq;
	private boolean finIsRvwAlw;
	private String finRvwFrq;
	private String finRvwRateApplFor;
	private boolean finAlwRateChangeAnyDate;
	private String finSchCalCodeOnRvw;
	private String finGrcRateType;
	private String finGrcBaseRate;
	private String finGrcSplRate;
	private BigDecimal finGrcMargin = BigDecimal.ZERO;
	private BigDecimal finGrcIntRate = BigDecimal.ZERO;
	private BigDecimal fInGrcMinRate = BigDecimal.ZERO;
	private BigDecimal finGrcMaxRate = BigDecimal.ZERO;
	private String finGrcDftIntFrq;
	private boolean finIsAlwGrcRepay;
	private String finGrcSchdMthd;
	private boolean finGrcIsIntCpz;
	private String finGrcCpzFrq;
	private boolean finGrcIsRvwAlw;
	private String finGrcRvwFrq;
	private boolean finIsIntCpzAtGrcEnd;
	private int finMinTerm;
	private int finMaxTerm;
	private int finDftTerms;
	private boolean finRepayPftOnFrq;
	private String finRpyFrq;
	private String finRepayMethod;
	private String alwdRpyMethods;
	private boolean finIsAlwPartialRpy;
	private boolean finIsAlwDifferment;
	private int finMaxDifferment;
	private boolean alwPlanDeferment;
	private int planDeferCount;
	private boolean finIsAlwEarlyRpy;
	private boolean finIsAlwEarlySettle;
	private int finODRpyTries;
	private String finDepositRestrictedTo;
	private int finAEBuyOrInception;
	private int finAESellOrMaturity;
	private boolean finIsActive;
	private String finScheduleOn;
	private String alwEarlyPayMethods;
	private String finGrcScheduleOn;
	private boolean finCommitmentReq;
	private boolean finCollateralReq;
	private String collateralType;
	private boolean finDepreciationReq;
	private String finDepreciationFrq;
	private boolean allowRIAInvestment;
	private boolean overrideLimit;
	private boolean limitRequired;
	private boolean finCommitmentOvrride;
	private boolean finCollateralOvrride;
	
	private boolean finPftUnChanged;
	private Date startDate;
	private Date endDate;
	private boolean allowDownpayPgm;
	private boolean alwAdvanceRent;
	private boolean manualSchedule;
	private boolean applyGrcPricing;
	private long grcPricingMethod;
	private boolean applyRpyPricing;
	private long rpyPricingMethod;
	private String rpyHierarchy;
	private String grcPricingMethodDesc;
	private String rpyPricingMethodDesc;
	private boolean droplineOD;
	private String droppingMethod;
	private boolean rateChgAnyDay;
	private String frequencyDays;
	// EMI Holiday Fields & BPI & Freezing Period
	private boolean alwBPI;
	private String	bpiTreatment;
	private String 	pftDueSchOn;
	private boolean	planEMIHAlw;
	private String	planEMIHMethod;
	private int planEMIHMaxPerYear;
	private int planEMIHMax;
	private int planEMIHLockPeriod;
	private boolean	planEMICpz;
	private int	unPlanEMIHLockPeriod;
	private boolean	unPlanEMICpz;
	private boolean alwReage;
	private boolean alwUnPlanEmiHoliday;
	private boolean	reAgeCpz;
	private int	fddLockPeriod;
	private int	maxUnplannedEmi;
	private int	maxReAgeHolidays;
	
	private String	roundingMode;
	private int	roundingTarget = 0;

	// Advised profit Rates
	private String grcAdvBaseRate;
	private String grcAdvBaseRateDesc;
	private BigDecimal grcAdvMargin = BigDecimal.ZERO;
	private BigDecimal grcAdvPftRate = BigDecimal.ZERO;
	private String rpyAdvBaseRate;
	private String rpyAdvBaseRateDesc;
	private BigDecimal rpyAdvMargin = BigDecimal.ZERO;
	private BigDecimal rpyAdvPftRate = BigDecimal.ZERO;
	private boolean alwMultiPartyDisb;
	private boolean tDSApplicable;
	private String addrLine1;
	
	//Profit on past Due
	private String  pastduePftCalMthd;
	private BigDecimal pastduePftMargin = BigDecimal.ZERO;
	
	//RollOver Details
	private boolean rollOverFinance;
	private String rollOverFrq;
	
	//Overdue Penalty Details
	private boolean applyODPenalty;
	private boolean oDIncGrcDays;
	private String oDChargeType;
	private int oDGraceDays;
	private String oDChargeCalOn;
	private BigDecimal oDChargeAmtOrPerc = BigDecimal.ZERO;
	private boolean oDAllowWaiver;
	private BigDecimal oDMaxWaiverPerc = BigDecimal.ZERO;
	
	// Step In Finance Details
	private boolean stepFinance;
	private boolean steppingMandatory;
	private boolean alwManualSteps;
	private String  alwdStepPolicies;
	private String  dftStepPolicy;
	private String  dftStepPolicyType;
	private String  lovDescDftStepPolicyName;
	private String remarks;
	private List<FinTypeVASProducts> finTypeVASProductsList;
	
	// Suspend details 
	private String  finSuspTrigger;
	private String  finSuspRemarks;
	
	private boolean newRecord;
	private String lovValue;
	private FinanceType befImage;
	private boolean alwMaxDisbCheckReq;
	private boolean quickDisb;
	private String promotionCode;
	private String promotionDesc;
	
	private long profitCenterID;
	private String profitcenterCode;
	private String profitCenterDesc;
	
	@XmlTransient
	private LoggedInUser userDetails;

	private HashMap<String, AccountingSet> lovDescAERule = new HashMap<String, AccountingSet>();
	
	@XmlTransient
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	//==================Not the Table
	private String lovDescFinDivisionName;
	private String lovDescFinAcTypeName;
	private String lovDescPftPayAcTypeName;
	private String lovDescFinContingentAcTypeName;
	private String lovDescFinSuspAcTypeName;
	private String lovDescFinBankContAcTypeName;
	private String lovDescFinProvisionAcTypeName;
	private String lovDescWorkFlowRolesName;
	private String lovDescWorkFlowTypeName;
	private String lovDescFinDepositRestrictedTo;
	private String lovDescFinAEBuyOrInceptionName;
	private String lovDescEVFinAEBuyOrInceptionName;
	private String lovDescFinAESellOrMaturityName;
	private String lovDescEVFinAESellOrMaturityName;
	private String lovDescPromoFinTypeDesc;
	private String productCategory;
	
	// only used for API
	private boolean promotionType = false;

	private List<FinTypeAccount> finTypeAccounts=new ArrayList<FinTypeAccount>();
	private List<FinTypeInsurances> finTypeInsurances=new ArrayList<FinTypeInsurances>();
	private List<FinTypeAccounting> finTypeAccountingList=new ArrayList<FinTypeAccounting>();
	private Map<String,Long> finTypeAccountingMap = new HashMap<String,Long>();
	private List<FinTypeFees> finTypeFeesList = new ArrayList<FinTypeFees>();
	private List<FinTypePartnerBank> finTypePartnerBankList = new ArrayList<FinTypePartnerBank>();
	
	public boolean isNew() {
		return isNewRecord();
	}

	public FinanceType() {
		super();
	}
	
	public FinanceType(String id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("finTypeAccounts");
		excludeFields.add("finTypeAccountingList");
		excludeFields.add("grcAdvBaseRateDesc");
		excludeFields.add("rpyAdvBaseRateDesc");
		excludeFields.add("downPayRuleDesc");
		excludeFields.add("finSuspTrigger");
		excludeFields.add("finSuspRemarks");
		excludeFields.add("grcPricingMethodDesc");
		excludeFields.add("rpyPricingMethodDesc");
		excludeFields.add("productCategory");
		excludeFields.add("dftStepPolicyType");
		excludeFields.add("finTypeInsurances");
		excludeFields.add("finTypeAccountingMap");
		excludeFields.add("finTypeFeesList");
		excludeFields.add("finTypeVASProductsList");
		excludeFields.add("addrLine1");
		excludeFields.add("promotionCode");
		excludeFields.add("promotionDesc");
		excludeFields.add("profitcenterCode");
		excludeFields.add("profitCenterDesc");
		excludeFields.add("promotionType");
		excludeFields.add("finCategoryDesc");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return finType;
	}

	public void setId(String id) {
		this.finType = id;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}
	
	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getFinCategory() {
		return finCategory;
	}

	public void setFinCategory(String finCategory) {
		this.finCategory = finCategory;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getFinDivision() {
    	return finDivision;
    }
	public void setFinDivision(String finDivision) {
    	this.finDivision = finDivision;
    }

	public String getLovDescFinDivisionName() {
    	return lovDescFinDivisionName;
    }
	public void setLovDescFinDivisionName(String lovDescFinDivisionName) {
    	this.lovDescFinDivisionName = lovDescFinDivisionName;
    }

	public String getFinDaysCalType() {
		return finDaysCalType;
	}

	public void setFinDaysCalType(String finDaysCalType) {
		this.finDaysCalType = finDaysCalType;
	}

	public String getFinAcType() {
		return finAcType;
	}

	public void setFinAcType(String finAcType) {
		this.finAcType = finAcType;
	}

	public String getLovDescFinAcTypeName() {
		return this.lovDescFinAcTypeName;
	}

	public void setLovDescFinAcTypeName(String lovDescFinAcTypeName) {
		this.lovDescFinAcTypeName = lovDescFinAcTypeName;
	}

	public String getFinContingentAcType() {
		return finContingentAcType;
	}

	public void setFinContingentAcType(String finContingentAcType) {
		this.finContingentAcType = finContingentAcType;
	}

	public String getLovDescFinContingentAcTypeName() {
		return this.lovDescFinContingentAcTypeName;
	}

	public void setLovDescFinContingentAcTypeName(String lovDescFinContingentAcTypeName) {
		this.lovDescFinContingentAcTypeName = lovDescFinContingentAcTypeName;
	}
	
	public String getFinSuspAcType() {
		return finSuspAcType;
	}

	public void setFinSuspAcType(String finSuspAcType) {
		this.finSuspAcType = finSuspAcType;
	}

	public String getLovDescFinSuspAcTypeName() {
		return this.lovDescFinSuspAcTypeName;
	}

	public void setLovDescFinSuspAcTypeName(String lovDescFinSuspAcTypeName) {
		this.lovDescFinSuspAcTypeName = lovDescFinSuspAcTypeName;
	}

	public String getFinBankContingentAcType() {
		return finBankContingentAcType;
	}

	public void setFinBankContingentAcType(String finBankContingentAcType) {
		this.finBankContingentAcType = finBankContingentAcType;
	}

	public String getLovDescFinBankContAcTypeName() {
		return lovDescFinBankContAcTypeName;
	}

	public void setLovDescFinBankContAcTypeName(String lovDescFinBankContAcTypeName) {
		this.lovDescFinBankContAcTypeName = lovDescFinBankContAcTypeName;
	}

	public String getFinProvisionAcType() {
		return finProvisionAcType;
	}

	public void setFinProvisionAcType(String finProvisionAcType) {
		this.finProvisionAcType = finProvisionAcType;
	}

	public String getLovDescFinProvisionAcTypeName() {
		return lovDescFinProvisionAcTypeName;
	}

	public void setLovDescFinProvisionAcTypeName(String lovDescFinProvisionAcTypeName) {
		this.lovDescFinProvisionAcTypeName = lovDescFinProvisionAcTypeName;
	}

	public boolean isFinIsGenRef() {
		return finIsGenRef;
	}

	public void setFinIsGenRef(boolean finIsGenRef) {
		this.finIsGenRef = finIsGenRef;
	}

	public BigDecimal getFinMaxAmount() {
		return finMaxAmount;
	}

	public void setFinMaxAmount(BigDecimal finMaxAmount) {
		this.finMaxAmount = finMaxAmount;
	}

	public BigDecimal getFinMinAmount() {
		return finMinAmount;
	}

	public void setFinMinAmount(BigDecimal finMinAmount) {
		this.finMinAmount = finMinAmount;
	}

	public boolean isFinIsOpenNewFinAc() {
		return finIsOpenNewFinAc;
	}

	public void setFinIsOpenNewFinAc(boolean finIsOpenNewFinAc) {
		this.finIsOpenNewFinAc = finIsOpenNewFinAc;
	}

	public String getFinDftStmtFrq() {
		return finDftStmtFrq;
	}

	public void setFinDftStmtFrq(String finDftStmtFrq) {
		this.finDftStmtFrq = finDftStmtFrq;
	}

	public boolean isFinIsAlwMD() {
		return finIsAlwMD;
	}

	public void setFinIsAlwMD(boolean finIsAlwMD) {
		this.finIsAlwMD = finIsAlwMD;
	}

	public String getFinSchdMthd() {
		return finSchdMthd;
	}

	public void setFinSchdMthd(String finSchdMthd) {
		this.finSchdMthd = finSchdMthd;
	}

	public boolean isFInIsAlwGrace() {
		return fInIsAlwGrace;
	}

	public void setFInIsAlwGrace(boolean fInIsAlwGrace) {
		this.fInIsAlwGrace = fInIsAlwGrace;
	}

	public int getFinHistRetension() {
		return finHistRetension;
	}

	public void setFinHistRetension(int finHistRetension) {
		this.finHistRetension = finHistRetension;
	}

	public String getFinRateType() {
		return finRateType;
	}

	public void setFinRateType(String finRateType) {
		this.finRateType = finRateType;
	}

	public String getFinBaseRate() {
		return finBaseRate;
	}

	public void setFinBaseRate(String finBaseRate) {
		this.finBaseRate = finBaseRate;
	}

	public String getFinSplRate() {
		return finSplRate;
	}

	public void setFinSplRate(String finSplRate) {
		this.finSplRate = finSplRate;
	}

	public BigDecimal getFinIntRate() {
		return finIntRate;
	}

	public void setFinIntRate(BigDecimal finIntRate) {
		this.finIntRate = finIntRate;
	}

	public BigDecimal getFInMinRate() {
		return fInMinRate;
	}

	public void setFInMinRate(BigDecimal fInMinRate) {
		this.fInMinRate = fInMinRate;
	}

	public BigDecimal getFinMaxRate() {
		return finMaxRate;
	}

	public void setFinMaxRate(BigDecimal finMaxRate) {
		this.finMaxRate = finMaxRate;
	}

	public String getFinDftIntFrq() {
		return finDftIntFrq;
	}

	public void setFinDftIntFrq(String finDftIntFrq) {
		this.finDftIntFrq = finDftIntFrq;
	}

	public boolean isFinIsIntCpz() {
		return finIsIntCpz;
	}

	public void setFinIsIntCpz(boolean finIsIntCpz) {
		this.finIsIntCpz = finIsIntCpz;
	}

	public String getFinCpzFrq() {
		return finCpzFrq;
	}

	public void setFinCpzFrq(String finCpzFrq) {
		this.finCpzFrq = finCpzFrq;
	}

	public boolean isFinIsRvwAlw() {
		return finIsRvwAlw;
	}

	public void setFinIsRvwAlw(boolean finIsRvwAlw) {
		this.finIsRvwAlw = finIsRvwAlw;
	}

	public String getFinRvwFrq() {
		return finRvwFrq;
	}

	public void setFinRvwFrq(String finRvwFrq) {
		this.finRvwFrq = finRvwFrq;
	}

	public String getFinGrcRateType() {
		return finGrcRateType;
	}

	public void setFinGrcRateType(String finGrcRateType) {
		this.finGrcRateType = finGrcRateType;
	}

	public String getFinGrcBaseRate() {
		return finGrcBaseRate;
	}

	public void setFinGrcBaseRate(String finGrcBaseRate) {
		this.finGrcBaseRate = finGrcBaseRate;
	}

	public String getFinGrcSplRate() {
		return finGrcSplRate;
	}

	public void setFinGrcSplRate(String finGrcSplRate) {
		this.finGrcSplRate = finGrcSplRate;
	}

	public BigDecimal getFinGrcIntRate() {
		return finGrcIntRate;
	}

	public void setFinGrcIntRate(BigDecimal finGrcIntRate) {
		this.finGrcIntRate = finGrcIntRate;
	}

	public BigDecimal getFInGrcMinRate() {
		return fInGrcMinRate;
	}

	public void setFInGrcMinRate(BigDecimal fInGrcMinRate) {
		this.fInGrcMinRate = fInGrcMinRate;
	}

	public BigDecimal getFinGrcMaxRate() {
		return finGrcMaxRate;
	}

	public void setFinGrcMaxRate(BigDecimal finGrcMaxRate) {
		this.finGrcMaxRate = finGrcMaxRate;
	}

	public String getFinGrcDftIntFrq() {
		return finGrcDftIntFrq;
	}

	public void setFinGrcDftIntFrq(String finGrcDftIntFrq) {
		this.finGrcDftIntFrq = finGrcDftIntFrq;
	}

	public boolean isFinGrcIsIntCpz() {
		return finGrcIsIntCpz;
	}

	public void setFinGrcIsIntCpz(boolean finGrcIsIntCpz) {
		this.finGrcIsIntCpz = finGrcIsIntCpz;
	}

	public String getFinGrcCpzFrq() {
		return finGrcCpzFrq;
	}

	public void setFinGrcCpzFrq(String finGrcCpzFrq) {
		this.finGrcCpzFrq = finGrcCpzFrq;
	}

	public boolean isFinGrcIsRvwAlw() {
		return finGrcIsRvwAlw;
	}

	public void setFinGrcIsRvwAlw(boolean finGrcIsRvwAlw) {
		this.finGrcIsRvwAlw = finGrcIsRvwAlw;
	}

	public String getFinGrcRvwFrq() {
		return finGrcRvwFrq;
	}

	public void setFinGrcRvwFrq(String finGrcRvwFrq) {
		this.finGrcRvwFrq = finGrcRvwFrq;
	}

	public int getFinMinTerm() {
		return finMinTerm;
	}

	public void setFinMinTerm(int finMinTerm) {
		this.finMinTerm = finMinTerm;
	}

	public int getFinMaxTerm() {
		return finMaxTerm;
	}

	public void setFinMaxTerm(int finMaxTerm) {
		this.finMaxTerm = finMaxTerm;
	}

	public int getFinDftTerms() {
		return finDftTerms;
	}

	public void setFinDftTerms(int finDftTerms) {
		this.finDftTerms = finDftTerms;
	}

	public String getFinRpyFrq() {
		return finRpyFrq;
	}

	public void setFinRpyFrq(String finRpyFrq) {
		this.finRpyFrq = finRpyFrq;
	}

	public String getFinRepayMethod() {
		return finRepayMethod;
	}

	public void setFinRepayMethod(String finRepayMethod) {
		this.finRepayMethod = finRepayMethod;
	}

	public boolean isFinIsAlwPartialRpy() {
		return finIsAlwPartialRpy;
	}

	public void setFinIsAlwPartialRpy(boolean finIsAlwPartialRpy) {
		this.finIsAlwPartialRpy = finIsAlwPartialRpy;
	}

	public boolean isFinIsAlwDifferment() {
		return finIsAlwDifferment;
	}

	public void setFinIsAlwDifferment(boolean finIsAlwDifferment) {
		this.finIsAlwDifferment = finIsAlwDifferment;
	}

	public boolean isFinIsAlwEarlyRpy() {
		return finIsAlwEarlyRpy;
	}

	public void setFinIsAlwEarlyRpy(boolean finIsAlwEarlyRpy) {
		this.finIsAlwEarlyRpy = finIsAlwEarlyRpy;
	}

	public boolean isFinIsAlwEarlySettle() {
		return finIsAlwEarlySettle;
	}

	public void setFinIsAlwEarlySettle(boolean finIsAlwEarlySettle) {
		this.finIsAlwEarlySettle = finIsAlwEarlySettle;
	}

	public int getFinODRpyTries() {
		return finODRpyTries;
	}

	public void setFinODRpyTries(int finODRpyTries) {
		this.finODRpyTries = finODRpyTries;
	}

	

	public boolean isFinIsActive() {
		return finIsActive;
	}

	public void setFinIsActive(boolean finIsActive) {
		this.finIsActive = finIsActive;
	}

	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public FinanceType getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinanceType beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	/**
	 * @param finFrEqrepayment
	 *            the finFrEqrepayment to set
	 */
	public void setEqualRepayment(boolean equalRepayment) {
		this.equalRepayment = equalRepayment;
	}

	/** @return the finFrEqrepayment */
	public boolean isEqualRepayment() {
		return equalRepayment;
	}

	/**
	 * @param finIsDwPayRequired
	 *            the finIsDwPayRequired to set
	 */
	public void setFinIsDwPayRequired(boolean finIsDwPayRequired) {
		this.finIsDwPayRequired = finIsDwPayRequired;
	}

	/** @return the finIsDwPayRequired */
	public boolean isFinIsDwPayRequired() {
		return finIsDwPayRequired;
	}

	public String getFinRvwRateApplFor() {
		return finRvwRateApplFor;
	}

	public void setFinRvwRateApplFor(String finRvwRateApplFor) {
		this.finRvwRateApplFor = finRvwRateApplFor;
	}

	public boolean isFinAlwRateChangeAnyDate() {
		return finAlwRateChangeAnyDate;
	}

	public void setFinAlwRateChangeAnyDate(boolean finAlwRateChangeAnyDate) {
		this.finAlwRateChangeAnyDate = finAlwRateChangeAnyDate;
	}

	public long getDownPayRule() {
		return downPayRule;
	}
	public void setDownPayRule(long downPayRule) {
		this.downPayRule = downPayRule;
	}

	public String getDownPayRuleDesc() {
		return downPayRuleDesc;
	}
	public void setDownPayRuleDesc(String downPayRuleDesc) {
		this.downPayRuleDesc = downPayRuleDesc;
	}

	public void setFinIsIntCpzAtGrcEnd(boolean finIsIntCpzAtGrcEnd) {
		this.finIsIntCpzAtGrcEnd = finIsIntCpzAtGrcEnd;
	}

	public boolean isFinIsIntCpzAtGrcEnd() {
		return finIsIntCpzAtGrcEnd;
	}

	public String getFinSchCalCodeOnRvw() {
		return finSchCalCodeOnRvw;
	}

	public void setFinSchCalCodeOnRvw(String finSchCalCodeOnRvw) {
		this.finSchCalCodeOnRvw = finSchCalCodeOnRvw;
	}

	public void setFinMaxDifferment(int finMaxDifferment) {
		this.finMaxDifferment = finMaxDifferment;
	}

	public int getFinMaxDifferment() {
		return finMaxDifferment;
	}

	public boolean isAlwPlanDeferment() {
		return alwPlanDeferment;
	}

	public void setAlwPlanDeferment(boolean alwPlanDeferment) {
		this.alwPlanDeferment = alwPlanDeferment;
	}

	public int getPlanDeferCount() {
		return planDeferCount;
	}

	public void setPlanDeferCount(int planDeferCount) {
		this.planDeferCount = planDeferCount;
	}


	public void setFinDepositRestrictedTo(String finDepositRestrictedTo) {
		this.finDepositRestrictedTo = finDepositRestrictedTo;
	}

	public String getFinDepositRestrictedTo() {
		return finDepositRestrictedTo;
	}

	public void setLovDescFinDepositRestrictedTo(String lovDescFinDepositRestrictedTo) {
		this.lovDescFinDepositRestrictedTo = lovDescFinDepositRestrictedTo;
	}

	public String getLovDescFinDepositRestrictedTo() {
		return lovDescFinDepositRestrictedTo;
	}

	public void setFinAEBuyOrInception(int finAEBuyOrInception) {
		this.finAEBuyOrInception = finAEBuyOrInception;
	}

	public int getFinAEBuyOrInception() {
		return finAEBuyOrInception;
	}

	public void setLovDescFinAEBuyOrInceptionName(String lovDescFinAEBuyOrInceptionName) {
		this.lovDescFinAEBuyOrInceptionName = lovDescFinAEBuyOrInceptionName;
	}

	public String getLovDescFinAEBuyOrInceptionName() {
		return lovDescFinAEBuyOrInceptionName;
	}

	public void setLovDescEVFinAEBuyOrInceptionName(String lovDescEVFinAEBuyOrInceptionName) {
		this.lovDescEVFinAEBuyOrInceptionName = lovDescEVFinAEBuyOrInceptionName;
	}

	public String getLovDescEVFinAEBuyOrInceptionName() {
		return lovDescEVFinAEBuyOrInceptionName;
	}

	public void setFinAESellOrMaturity(int finAESellOrMaturity) {
		this.finAESellOrMaturity = finAESellOrMaturity;
	}

	public int getFinAESellOrMaturity() {
		return finAESellOrMaturity;
	}

	public void setLovDescFinAESellOrMaturityName(String lovDescFinAESellOrMaturityName) {
		this.lovDescFinAESellOrMaturityName = lovDescFinAESellOrMaturityName;
	}

	public String getLovDescFinAESellOrMaturityName() {
		return lovDescFinAESellOrMaturityName;
	}

	public void setLovDescEVFinAESellOrMaturityName(String lovDescEVFinAESellOrMaturityName) {
		this.lovDescEVFinAESellOrMaturityName = lovDescEVFinAESellOrMaturityName;
	}

	public String getLovDescEVFinAESellOrMaturityName() {
		return lovDescEVFinAESellOrMaturityName;
	}

	public String getLovDescWorkFlowRolesName() {
		return lovDescWorkFlowRolesName;
	}

	public void setLovDescWorkFlowRolesName(String lovDescWorkFlowRolesName) {
		this.lovDescWorkFlowRolesName = lovDescWorkFlowRolesName;
	}

	public String getLovDescWorkFlowTypeName() {
		return lovDescWorkFlowTypeName;
	}

	public void setLovDescWorkFlowTypeName(String lovDescWorkFlowTypeName) {
		this.lovDescWorkFlowTypeName = lovDescWorkFlowTypeName;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public String getPftPayAcType() {
		return pftPayAcType;
	}
	public void setPftPayAcType(String pftPayAcType) {
		this.pftPayAcType = pftPayAcType;
	}

	public String getLovDescPftPayAcTypeName() {
		return lovDescPftPayAcTypeName;
	}
	public void setLovDescPftPayAcTypeName(String lovDescPftPayAcTypeName) {
		this.lovDescPftPayAcTypeName = lovDescPftPayAcTypeName;
	}

	public void setLovDescAERule(String aEEvent, AccountingSet lovDescAERule) {
		if (this.lovDescAERule == null) {
			this.lovDescAERule = new HashMap<String, AccountingSet>();
		} else {
			if (this.lovDescAERule.containsKey(aEEvent)) {
				this.lovDescAERule.remove(aEEvent);
			}
		}
		this.lovDescAERule.put(aEEvent, lovDescAERule);

	}

	

	public HashMap<String, AccountingSet> getLovDescAERule() {
		return lovDescAERule;
	}

	public void setFinIsOpenPftPayAcc(boolean finIsOpenPftPayAcc) {
		this.finIsOpenPftPayAcc = finIsOpenPftPayAcc;
	}

	public boolean isFinIsOpenPftPayAcc() {
		return finIsOpenPftPayAcc;
	}

	public boolean isFinIsAlwGrcRepay() {
		return finIsAlwGrcRepay;
	}

	public void setFinIsAlwGrcRepay(boolean finIsAlwGrcRepay) {
		this.finIsAlwGrcRepay = finIsAlwGrcRepay;
	}

	public String getFinGrcSchdMthd() {
		return finGrcSchdMthd;
	}

	public void setFinGrcSchdMthd(String finGrcSchdMthd) {
		this.finGrcSchdMthd = finGrcSchdMthd;
	}

	public BigDecimal getFinMargin() {
		return finMargin;
	}

	public void setFinMargin(BigDecimal finMargin) {
		this.finMargin = finMargin;
	}

	public BigDecimal getFinGrcMargin() {
		return finGrcMargin;
	}

	public void setFinGrcMargin(BigDecimal finGrcMargin) {
		this.finGrcMargin = finGrcMargin;
	}

	

	public void setFinGrcScheduleOn(String finGrcScheduleOn) {
		this.finGrcScheduleOn = finGrcScheduleOn;
	}

	public String getFinGrcScheduleOn() {
		return finGrcScheduleOn;
	}

	public void setFinScheduleOn(String finScheduleOn) {
		this.finScheduleOn = finScheduleOn;
	}

	public String getFinScheduleOn() {
		return finScheduleOn;
	}

	public String getAlwEarlyPayMethods() {
		return alwEarlyPayMethods;
	}

	public void setAlwEarlyPayMethods(String alwEarlyPayMethods) {
		this.alwEarlyPayMethods = alwEarlyPayMethods;
	}

	public void setFinCommitmentReq(boolean finCommitmentReq) {
		this.finCommitmentReq = finCommitmentReq;
	}

	public boolean isFinCommitmentReq() {
		return finCommitmentReq;
	}

	public void setFinCollateralReq(boolean finCollateralReq) {
		this.finCollateralReq = finCollateralReq;
	}

	public boolean isFinCollateralReq() {
		return finCollateralReq;
	}

	public String getCollateralType() {
		return collateralType;
	}
	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}

	public void setFinDepreciationReq(boolean finDepreciationReq) {
		this.finDepreciationReq = finDepreciationReq;
	}

	public boolean isFinDepreciationReq() {
		return finDepreciationReq;
	}

	public void setFinDepreciationFrq(String finDepreciationFrq) {
		this.finDepreciationFrq = finDepreciationFrq;
	}

	public String getFinDepreciationFrq() {
		return finDepreciationFrq;
	}


	public void setLovDescAERule(HashMap<String, AccountingSet> lovDescAERule) {
		this.lovDescAERule = lovDescAERule;
	}

	public void setAllowRIAInvestment(boolean allowRIAInvestment) {
		this.allowRIAInvestment = allowRIAInvestment;
	}

	public boolean isAllowRIAInvestment() {
		return allowRIAInvestment;
	}

	public void setFinCommitmentOvrride(boolean finCommitmentOvrride) {
		this.finCommitmentOvrride = finCommitmentOvrride;
	}

	public boolean isFinCommitmentOvrride() {
		return finCommitmentOvrride;
	}

	public void setFinCollateralOvrride(boolean finCollateralOvrride) {
		this.finCollateralOvrride = finCollateralOvrride;
	}

	public boolean isFinCollateralOvrride() {
		return finCollateralOvrride;
	}

	public void setOverrideLimit(boolean overrideLimit) {
		this.overrideLimit = overrideLimit;
	}

	public boolean isOverrideLimit() {
		return overrideLimit;
	}

	public void setLimitRequired(boolean limitRequired) {
		this.limitRequired = limitRequired;
	}

	public boolean isLimitRequired() {
		return limitRequired;
	}

	public void setFinRepayPftOnFrq(boolean finRepayPftOnFrq) {
		this.finRepayPftOnFrq = finRepayPftOnFrq;
	}

	public boolean isFinRepayPftOnFrq() {
		return finRepayPftOnFrq;
	}

	public void setFinPftUnChanged(boolean finPftUnChanged) {
	    this.finPftUnChanged = finPftUnChanged;
    }
	public boolean isFinPftUnChanged() {
	    return finPftUnChanged;
    }

	public boolean isApplyODPenalty() {
    	return applyODPenalty;
    }
	public void setApplyODPenalty(boolean applyODPenalty) {
    	this.applyODPenalty = applyODPenalty;
    }

	public boolean isODIncGrcDays() {
    	return oDIncGrcDays;
    }
	public void setODIncGrcDays(boolean oDIncGrcDays) {
    	this.oDIncGrcDays = oDIncGrcDays;
    }

	public String getODChargeType() {
    	return oDChargeType;
    }
	public void setODChargeType(String oDChargeType) {
    	this.oDChargeType = oDChargeType;
    }

	public int getODGraceDays() {
    	return oDGraceDays;
    }
	public void setODGraceDays(int oDGraceDays) {
    	this.oDGraceDays = oDGraceDays;
    }

	public String getODChargeCalOn() {
    	return oDChargeCalOn;
    }
	public void setODChargeCalOn(String oDChargeCalOn) {
    	this.oDChargeCalOn = oDChargeCalOn;
    }

	public BigDecimal getODChargeAmtOrPerc() {
    	return oDChargeAmtOrPerc;
    }
	public void setODChargeAmtOrPerc(BigDecimal oDChargeAmtOrPerc) {
    	this.oDChargeAmtOrPerc = oDChargeAmtOrPerc;
    }

	public boolean isODAllowWaiver() {
    	return oDAllowWaiver;
    }
	public void setODAllowWaiver(boolean oDAllowWaiver) {
    	this.oDAllowWaiver = oDAllowWaiver;
    }

	public BigDecimal getODMaxWaiverPerc() {
    	return oDMaxWaiverPerc;
    }
	public void setODMaxWaiverPerc(BigDecimal oDMaxWaiverPerc) {
    	this.oDMaxWaiverPerc = oDMaxWaiverPerc;
    }

	public List<FinTypeAccount> getFinTypeAccounts() {
    	return finTypeAccounts;
    }

	public void setFinTypeAccounts(List<FinTypeAccount> finTypeAccounts) {
    	this.finTypeAccounts = finTypeAccounts;
    }
	
	public List<FinTypeFees> getFinTypeFeesList() {
		return finTypeFeesList;
	}
	public void setFinTypeFeesList(List<FinTypeFees> finTypeFeesList) {
		this.finTypeFeesList = finTypeFeesList;
	}
	
	public boolean isStepFinance() {
		return stepFinance;
	}
	public void setStepFinance(boolean stepFinance) {
		this.stepFinance = stepFinance;
	}

	public boolean isSteppingMandatory() {
		return steppingMandatory;
	}
	public void setSteppingMandatory(boolean steppingMandatory) {
		this.steppingMandatory = steppingMandatory;
	}

	public boolean isAlwManualSteps() {
		return alwManualSteps;
	}
	public void setAlwManualSteps(boolean alwManualSteps) {
		this.alwManualSteps = alwManualSteps;
	}

	public String getAlwdStepPolicies() {
		return alwdStepPolicies;
	}
	public void setAlwdStepPolicies(String alwdStepPolicies) {
		this.alwdStepPolicies = alwdStepPolicies;
	}

	public String getDftStepPolicy() {
		return dftStepPolicy;
	}
	public void setDftStepPolicy(String dftStepPolicy) {
		this.dftStepPolicy = dftStepPolicy;
	}

	public String getLovDescDftStepPolicyName() {
	    return lovDescDftStepPolicyName;
    }

	public void setLovDescDftStepPolicyName(String lovDescDftStepPolicyName) {
	    this.lovDescDftStepPolicyName = lovDescDftStepPolicyName;
    }

	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getPastduePftCalMthd() {
		return pastduePftCalMthd;
	}
	public void setPastduePftCalMthd(String pastduePftCalMthd) {
		this.pastduePftCalMthd = pastduePftCalMthd;
	}

	public BigDecimal getPastduePftMargin() {
		return pastduePftMargin;
	}
	public void setPastduePftMargin(BigDecimal pastduePftMargin) {
		this.pastduePftMargin = pastduePftMargin;
	}

	public boolean isAllowDownpayPgm() {
	    return allowDownpayPgm;
    }
	public boolean isAlwAdvanceRent() {
	    return alwAdvanceRent;
    }

	public void setAlwAdvanceRent(boolean alwAdvanceRent) {
	    this.alwAdvanceRent = alwAdvanceRent;
    }
	public void setAllowDownpayPgm(boolean allowDownPayPgm) {
	    this.allowDownpayPgm = allowDownPayPgm;
    }

	public String getLovDescPromoFinTypeDesc() {
	    return lovDescPromoFinTypeDesc;
    }
	public void setLovDescPromoFinTypeDesc(String lovDescPromoFinTypeDesc) {
	    this.lovDescPromoFinTypeDesc = lovDescPromoFinTypeDesc;
    }

	public String getFinAssetType() {
	    return finAssetType;
    }
	public void setFinAssetType(String finAssetType) {
	    this.finAssetType = finAssetType;
    }
	
	public boolean isAlwMultiPartyDisb() {
		return alwMultiPartyDisb;
	}
	public void setAlwMultiPartyDisb(boolean alwMultiPartyDisb) {
		this.alwMultiPartyDisb = alwMultiPartyDisb;
	}

	public String getGrcAdvBaseRate() {
		return grcAdvBaseRate;
	}
	public void setGrcAdvBaseRate(String grcAdvBaseRate) {
		this.grcAdvBaseRate = grcAdvBaseRate;
	}

	public BigDecimal getGrcAdvMargin() {
		return grcAdvMargin;
	}
	public void setGrcAdvMargin(BigDecimal grcAdvMargin) {
		this.grcAdvMargin = grcAdvMargin;
	}

	public BigDecimal getGrcAdvPftRate() {
		return grcAdvPftRate;
	}
	public void setGrcAdvPftRate(BigDecimal grcAdvPftRate) {
		this.grcAdvPftRate = grcAdvPftRate;
	}

	public String getRpyAdvBaseRate() {
		return rpyAdvBaseRate;
	}
	public void setRpyAdvBaseRate(String rpyAdvBaseRate) {
		this.rpyAdvBaseRate = rpyAdvBaseRate;
	}

	public BigDecimal getRpyAdvMargin() {
		return rpyAdvMargin;
	}
	public void setRpyAdvMargin(BigDecimal rpyAdvMargin) {
		this.rpyAdvMargin = rpyAdvMargin;
	}

	public BigDecimal getRpyAdvPftRate() {
		return rpyAdvPftRate;
	}
	public void setRpyAdvPftRate(BigDecimal rpyAdvPftRate) {
		this.rpyAdvPftRate = rpyAdvPftRate;
	}
	
	public String getGrcAdvBaseRateDesc() {
	    return grcAdvBaseRateDesc;
    }
	public void setGrcAdvBaseRateDesc(String grcAdvBaseRateDesc) {
	    this.grcAdvBaseRateDesc = grcAdvBaseRateDesc;
    }

	public String getRpyAdvBaseRateDesc() {
	    return rpyAdvBaseRateDesc;
    }
	public void setRpyAdvBaseRateDesc(String rpyAdvBaseRateDesc) {
	    this.rpyAdvBaseRateDesc = rpyAdvBaseRateDesc;
    }
	
	public boolean isRollOverFinance() {
	    return rollOverFinance;
    }
	public void setRollOverFinance(boolean rollOverFinance) {
	    this.rollOverFinance = rollOverFinance;
    }
	
	public String getRollOverFrq() {
	    return rollOverFrq;
    }
	public void setRollOverFrq(String rollOverFrq) {
	    this.rollOverFrq = rollOverFrq;
    }
	
	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> financeTypeMap = new HashMap<String, Object>();	
		getDeclaredFieldValues(financeTypeMap);
		return financeTypeMap;
	}

	public void getDeclaredFieldValues(HashMap<String, Object> financeTypeMap){
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				//"ft_" Should be in small case only, if we want to change the case we need to update the configuration fields as well.
				financeTypeMap.put("ft_"+this.getClass().getDeclaredFields()[i].getName(),
						this.getClass().getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}
	}
	
	public String getFinSuspTrigger() {
		return finSuspTrigger;
	}

	public void setFinSuspTrigger(String finSuspTrigger) {
		this.finSuspTrigger = finSuspTrigger;
	}

	public String getFinSuspRemarks() {
		return finSuspRemarks;
	}

	public void setFinSuspRemarks(String finSuspRemarks) {
		this.finSuspRemarks = finSuspRemarks;
	}

	public boolean isTDSApplicable() {
		return tDSApplicable;
	}

	public void setTDSApplicable(boolean tDSApplicable) {
		this.tDSApplicable = tDSApplicable;
	}

	public boolean isApplyGrcPricing() {
		return applyGrcPricing;
	}

	public void setApplyGrcPricing(boolean applyGrcPricing) {
		this.applyGrcPricing = applyGrcPricing;
	}

	public long getGrcPricingMethod() {
		return grcPricingMethod;
	}

	public void setGrcPricingMethod(long grcPricingMethod) {
		this.grcPricingMethod = grcPricingMethod;
	}

	public boolean isApplyRpyPricing() {
		return applyRpyPricing;
	}

	public void setApplyRpyPricing(boolean applyRpyPricing) {
		this.applyRpyPricing = applyRpyPricing;
	}

	public long getRpyPricingMethod() {
		return rpyPricingMethod;
	}

	public void setRpyPricingMethod(long rpyPricingMethod) {
		this.rpyPricingMethod = rpyPricingMethod;
	}

	public String getRpyHierarchy() {
		return rpyHierarchy;
	}

	public void setRpyHierarchy(String rpyHierarchy) {
		this.rpyHierarchy = rpyHierarchy;
	}

	public String getGrcPricingMethodDesc() {
		return grcPricingMethodDesc;
	}

	public void setGrcPricingMethodDesc(String grcPricingMethodDesc) {
		this.grcPricingMethodDesc = grcPricingMethodDesc;
	}

	public String getRpyPricingMethodDesc() {
		return rpyPricingMethodDesc;
	}

	public void setRpyPricingMethodDesc(String rpyPricingMethodDesc) {
		this.rpyPricingMethodDesc = rpyPricingMethodDesc;
	}

	public boolean isDroplineOD() {
		return droplineOD;
	}

	public void setDroplineOD(boolean droplineOD) {
		this.droplineOD = droplineOD;
	}

	public String getDroppingMethod() {
		return droppingMethod;
	}

	public void setDroppingMethod(String droppingMethod) {
		this.droppingMethod = droppingMethod;
	}

	public boolean isRateChgAnyDay() {
		return rateChgAnyDay;
	}

	public void setRateChgAnyDay(boolean rateChgAnyDay) {
		this.rateChgAnyDay = rateChgAnyDay;
	}
	
	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getDftStepPolicyType() {
		return dftStepPolicyType;
	}

	public void setDftStepPolicyType(String dftStepPolicyType) {
		this.dftStepPolicyType = dftStepPolicyType;
	}

	public String getPftDueSchOn() {
		return pftDueSchOn;
	}
	public void setPftDueSchOn(String pftDueSchOn) {
		this.pftDueSchOn = pftDueSchOn;
	}

	public boolean isManualSchedule() {
		return manualSchedule;
	}
	public void setManualSchedule(boolean manualSchedule) {
		this.manualSchedule = manualSchedule;
	}

	public List<FinTypeAccounting> getFinTypeAccountingList() {
		return finTypeAccountingList;
	}
	public void setFinTypeAccountingList(List<FinTypeAccounting> finTypeAccountingList) {
		this.finTypeAccountingList = finTypeAccountingList;
		finTypeAccountingMap.clear();
		if(finTypeAccountingList != null){
			for (FinTypeAccounting finTypeAcc : finTypeAccountingList) {
				finTypeAccountingMap.put(finTypeAcc.getEvent(), finTypeAcc.getAccountSetID());
			}
		}
	}
	
	public Long getAccEventValue(String eventCode){
		if(finTypeAccountingMap.get(eventCode) == null){
			return Long.MIN_VALUE;
		}else{
			return finTypeAccountingMap.get(eventCode);
		}
	}
	
	public List<FinTypeInsurances> getFinTypeInsurances() {
		return finTypeInsurances;
	}
	public void setFinTypeInsurances(List<FinTypeInsurances> finTypeInsurances) {
		this.finTypeInsurances = finTypeInsurances;
	}

	public boolean isAlwBPI() {
		return alwBPI;
	}
	public void setAlwBPI(boolean alwBPI) {
		this.alwBPI = alwBPI;
	}

	public String getBpiTreatment() {
		return bpiTreatment;
	}
	public void setBpiTreatment(String bpiTreatment) {
		this.bpiTreatment = bpiTreatment;
	}

	public boolean isPlanEMIHAlw() {
		return planEMIHAlw;
	}
	public void setPlanEMIHAlw(boolean planEMIHAlw) {
		this.planEMIHAlw = planEMIHAlw;
	}

	public String getPlanEMIHMethod() {
		return planEMIHMethod;
	}
	public void setPlanEMIHMethod(String planEMIHMethod) {
		this.planEMIHMethod = planEMIHMethod;
	}

	public int getPlanEMIHMaxPerYear() {
		return planEMIHMaxPerYear;
	}
	public void setPlanEMIHMaxPerYear(int planEMIHMaxPerYear) {
		this.planEMIHMaxPerYear = planEMIHMaxPerYear;
	}

	public int getPlanEMIHMax() {
		return planEMIHMax;
	}
	public void setPlanEMIHMax(int planEMIHMax) {
		this.planEMIHMax = planEMIHMax;
	}

	public int getPlanEMIHLockPeriod() {
		return planEMIHLockPeriod;
	}
	public void setPlanEMIHLockPeriod(int planEMIHLockPeriod) {
		this.planEMIHLockPeriod = planEMIHLockPeriod;
	}

	public boolean isPlanEMICpz() {
		return planEMICpz;
	}
	public void setPlanEMICpz(boolean planEMICpz) {
		this.planEMICpz = planEMICpz;
	}

	public int getUnPlanEMIHLockPeriod() {
		return unPlanEMIHLockPeriod;
	}
	public void setUnPlanEMIHLockPeriod(int unPlanEMIHLockPeriod) {
		this.unPlanEMIHLockPeriod = unPlanEMIHLockPeriod;
	}

	public boolean isUnPlanEMICpz() {
		return unPlanEMICpz;
	}
	public void setUnPlanEMICpz(boolean unPlanEMICpz) {
		this.unPlanEMICpz = unPlanEMICpz;
	}

	public boolean isReAgeCpz() {
		return reAgeCpz;
	}
	public void setReAgeCpz(boolean reAgeCpz) {
		this.reAgeCpz = reAgeCpz;
	}

	public int getFddLockPeriod() {
		return fddLockPeriod;
	}
	public void setFddLockPeriod(int fddLockPeriod) {
		this.fddLockPeriod = fddLockPeriod;
	}

	public String getAlwdRpyMethods() {
		return alwdRpyMethods;
	}
	public void setAlwdRpyMethods(String alwdRpyMethods) {
		this.alwdRpyMethods = alwdRpyMethods;
	}

	public int getMaxUnplannedEmi() {
		return maxUnplannedEmi;
	}
	public void setMaxUnplannedEmi(int maxUnplannedEmi) {
		this.maxUnplannedEmi = maxUnplannedEmi;
	}

	public int getMaxReAgeHolidays() {
		return maxReAgeHolidays;
	}
	public void setMaxReAgeHolidays(int maxReAgeHolidays) {
		this.maxReAgeHolidays = maxReAgeHolidays;
	}

	public String getRoundingMode() {
		return roundingMode;
	}
	public void setRoundingMode(String roundingMode) {
		this.roundingMode = roundingMode;
	}

	public String getFrequencyDays() {
		return frequencyDays;
	}

	public void setFrequencyDays(String frequencyDays) {
		this.frequencyDays = frequencyDays;
	}

	public List<FinTypeVASProducts> getFinTypeVASProductsList() {
		return finTypeVASProductsList;
	}

	public void setFinTypeVASProductsList(List<FinTypeVASProducts> finTypeVASProductsList) {
		this.finTypeVASProductsList = finTypeVASProductsList;
	}

	public boolean isAlwReage() {
		return alwReage;
	}

	public void setAlwReage(boolean alwReage) {
		this.alwReage = alwReage;
	}

	public boolean isAlwUnPlanEmiHoliday() {
		return alwUnPlanEmiHoliday;
	}

	public void setAlwUnPlanEmiHoliday(boolean alwUnPlanEmiHoliday) {
		this.alwUnPlanEmiHoliday = alwUnPlanEmiHoliday;
	}

	public String getAddrLine1() {
		return addrLine1;
	}

	public void setAddrLine1(String addrLine1) {
		this.addrLine1 = addrLine1;
	}

	public boolean isAlwMaxDisbCheckReq() {
		return alwMaxDisbCheckReq;
	}

	public void setAlwMaxDisbCheckReq(boolean alwMaxDisbCheckReq) {
		this.alwMaxDisbCheckReq = alwMaxDisbCheckReq;
	}

	public boolean isQuickDisb() {
		return quickDisb;
	}

	public void setQuickDisb(boolean quickDisb) {
		this.quickDisb = quickDisb;
	}
	
	public String getPromotionCode() {
		return promotionCode;
	}
	
	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}
	
	/**
	 * Copy the Promotion Details to Finance Type 
	 * @param promotion
	 */
	public void setFInTypeFromPromotiion(Promotion promotion){
		setPromotionCode(promotion.getPromotionCode());
		setPromotionDesc(promotion.getPromotionDesc());
		setFinIsDwPayRequired(promotion.isFinIsDwPayRequired());
		setDownPayRule(promotion.getDownPayRule());
		if (promotion.getActualInterestRate() != null && promotion.getActualInterestRate().compareTo(BigDecimal.ZERO) != 0) {
			setFinIntRate(promotion.getActualInterestRate());
		} else if (promotion.getFinBaseRate() != null) {
			setFinBaseRate(promotion.getFinBaseRate());
			setFinSplRate(promotion.getFinSplRate());
			setFinMargin(promotion.getFinMargin());
		}
		
		setApplyRpyPricing(promotion.isApplyRpyPricing());
		setRpyPricingMethod(promotion.getRpyPricingMethod());
		
		setFinMinTerm(promotion.getFinMinTerm());
		setFinMaxTerm(promotion.getFinMaxTerm());
		setFinMinAmount(promotion.getFinMinAmount());
		setFinMaxAmount(promotion.getFinMaxAmount());
		setFInMinRate(promotion.getFinMinRate());
		setFinMaxRate(promotion.getFinMaxRate());
		setFinCcy(promotion.getFinCcy());
	}

	public String getPromotionDesc() {
		return promotionDesc;
	}

	public void setPromotionDesc(String promotionDesc) {
		this.promotionDesc = promotionDesc;
	}

	public List<FinTypePartnerBank> getFinTypePartnerBankList() {
		return finTypePartnerBankList;
	}

	public void setFinTypePartnerBankList(List<FinTypePartnerBank> finTypePartnerBankList) {
		this.finTypePartnerBankList = finTypePartnerBankList;
	}

	public String getProfitcenterCode() {
		return profitcenterCode;
	}

	public void setProfitcenterCode(String profitcenterCode) {
		this.profitcenterCode = profitcenterCode;
	}

	public String getProfitCenterDesc() {
		return profitCenterDesc;
	}

	public void setProfitCenterDesc(String profitCenterDesc) {
		this.profitCenterDesc = profitCenterDesc;
	}

	public long getProfitCenterID() {
		return profitCenterID;
	}

	public void setProfitCenterID(long profitCenterID) {
		this.profitCenterID = profitCenterID;
	}

	public boolean isPromotionType() {
		return promotionType;
	}

	public void setPromotionType(boolean promotionType) {
		this.promotionType = promotionType;
	}

	public int getRoundingTarget() {
		return roundingTarget;
	}

	public void setRoundingTarget(int roundingTarget) {
		this.roundingTarget = roundingTarget;
	}

	public String getFinCategoryDesc() {
		return finCategoryDesc;
	}

	public void setFinCategoryDesc(String finCategoryDesc) {
		this.finCategoryDesc = finCategoryDesc;
	}
}