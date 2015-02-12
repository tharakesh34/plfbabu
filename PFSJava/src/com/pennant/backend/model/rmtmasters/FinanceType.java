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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.util.WorkFlowUtil;

/** Model class for the <b>FinanceType table</b>.<br> */
public class FinanceType implements java.io.Serializable {

	private static final long serialVersionUID = -4098586745401583126L;
	
	//	Ordered Same AS Table please don't break It
	private String product = "";
	private String finType;
	private String finCategory;
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
	private BigDecimal finMaxAmount;
	private BigDecimal finMinAmount;
	private String finDftStmtFrq;
	private boolean finIsAlwMD;
	private int finHistRetension;
	private boolean finFrEqrepayment;
	private long finAssetType;
	private boolean finIsDwPayRequired;
	private BigDecimal finMinDownPayAmount;
	private boolean fInIsAlwGrace;
	private String finRateType;
	private String finBaseRate;
	private String finSplRate;
	private BigDecimal finMargin;
	private BigDecimal finIntRate;
	private BigDecimal fInMinRate;
	private BigDecimal finMaxRate;
	private String finDftIntFrq;
	private String finSchdMthd;
	private boolean finIsIntCpz;
	private String finCpzFrq;
	private boolean finIsRvwAlw;
	private String finRvwFrq;
	private String finRvwRateApplFor;
	private boolean finAlwRateChangeAnyDate;
	private boolean finAlwIndRate;
	private String finIndBaseRate;
	private String finSchCalCodeOnRvw;
	private String finGrcRateType;
	private String finGrcBaseRate;
	private String finGrcSplRate;
	private BigDecimal finGrcMargin;
	private BigDecimal finGrcIntRate;
	private BigDecimal fInGrcMinRate;
	private BigDecimal finGrcMaxRate;
	private String finGrcDftIntFrq;
	private boolean finIsAlwGrcRepay;
	private String finGrcSchdMthd;
	private boolean finGrcIsIntCpz;
	private String finGrcCpzFrq;
	private boolean finGrcIsRvwAlw;
	private String finGrcRvwFrq;
	private String finGrcRvwRateApplFor;
	private boolean finGrcAlwIndRate;
	private String finGrcIndBaseRate;
	private boolean finIsIntCpzAtGrcEnd;
	private boolean finGrcAlwRateChgAnyDate;
	private int finMinTerm;
	private int finMaxTerm;
	private int finDftTerms;
	private boolean finRepayPftOnFrq;
	private String finRpyFrq;
	private String fInRepayMethod;
	private boolean finIsAlwPartialRpy;
	private boolean finIsAlwDifferment;
	private int finMaxDifferment;
	private boolean alwPlanDeferment;
	private int planDeferCount;
	private boolean finIsAlwEarlyRpy;
	private boolean finIsAlwEarlySettle;
	private int finODRpyTries;
	private String finLatePayRule;
	private String finAEAddDsbOD;
	private String finAEAddDsbFD;
	private String finAEAddDsbFDA;
	private String finAEAmzNorm;
	private String finAEAmzSusp;
	private String finAEToNoAmz;
	private String finToAmz;
	private String finAEMAmz;
	private String finAERateChg;
	private String finAERepay;
	private String finAEEarlyPay;
	private String finAEEarlySettle;
	private String finAEWriteOff;
	private String finAEWriteOffBK;
	private String finAEGraceEnd;
	private String finProvision;
	private String finSchdChange;
	private String finAECapitalize;
	private String finDepositRestrictedTo;
	private String finAEBuyOrInception;
	private String finAESellOrMaturity;
	private boolean finIsActive;
	private String finAEPlanDef;
	private String finDefRepay;
	private String finScheduleOn;
	private String finGrcScheduleOn;
	private boolean finCommitmentReq;
	private boolean finCollateralReq;
	private boolean finDepreciationReq;
	private String finDepreciationFrq;
	private String finDepreciationRule;
	private boolean finOrgPrfUnchanged;
	private boolean allowRIAInvestment;
	private boolean allowParllelFinance;
	private boolean overrideLimit;
	private boolean limitRequired;
	private boolean finCommitmentOvrride;
	private boolean finCollateralOvrride;
	private String finInstDate;
	private String finAEProgClaim;
	private String finAEMaturity;
	private boolean finPftUnChanged;
	private Date startDate;
	private Date endDate;
	private boolean takafulMandatory;
	private boolean takafulReq;
	private String remFeeSchdMethod;
	
	//Overdue Penalty Details
	private boolean applyODPenalty;
	private boolean oDIncGrcDays;
	private String oDChargeType;
	private int oDGraceDays;
	private String oDChargeCalOn;
	private BigDecimal oDChargeAmtOrPerc;
	private boolean oDAllowWaiver;
	private BigDecimal oDMaxWaiverPerc;
	
	// Step In Finance Details
	private boolean stepFinance;
	private boolean steppingMandatory;
	private boolean alwManualSteps;
	private String  alwdStepPolicies;
	private String  dftStepPolicy;
	private String  lovDescDftStepPolicyName;
	
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord = false;
	private String lovValue;
	private FinanceType befImage;
	private LoginUserDetails userDetails;
	private String recordStatus;
	private String roleCode = "";
	private String nextRoleCode = "";
	private String taskId = "";
	private String nextTaskId = "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;

	private HashMap<String, AccountingSet> lovDescAERule = new HashMap<String, AccountingSet>();
	private List<FinanceMarginSlab> financeMarginSlabsList = new ArrayList<FinanceMarginSlab>();
	private HashMap<String, List<AuditDetail>> lovDescMarginSlabAuditDetailMap = new HashMap<String, List<AuditDetail>>();
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	//==================Not the Table
	private int lovDescFinFormetter;
	private String lovDescFinCcyName;
	private String lovDescFinDivisionName;
	private String lovDescFinDaysCalTypeName;
	private String lovDescFinIndBaseRateName;
	private String lovDescFinAcTypeName;
	private String lovDescPftPayAcTypeName;
	private String lovDescFinContingentAcTypeName;
	private String lovDescFinSuspAcTypeName;
	private String lovDescFinBankContingentAcTypeName;
	private String lovDescFinProvisionAcTypeName;
	private String lovDescFinSchdMthdName;
	private String lovDescFInRepayMethodName;
	private String lovDescFinRateTypeName;
	private String lovDescFinBaseRateName;
	private String lovDescFinSplRateName;
	private String lovDescFinGrcRateTypeName;
	private String lovDescFinGrcBaseRateName;
	private String lovDescFinGrcSplRateName;
	private String lovDescFinAEAddDsbODName;
	private String lovDescEVFinAEAddDsbODName;
	private String lovDescFinAEAddDsbFDName;
	private String lovDescEVFinAEAddDsbFDName;
	private String lovDescFinAEAddDsbFDAName;
	private String lovDescEVFinAEAddDsbFDAName;
	private String lovDescFinAEAmzNormName;
	private String lovDescEVFinAEAmzNormName;
	private String lovDescFinAEAmzSuspName;
	private String lovDescEVFinAEAmzSuspName;
	private String lovDescFinAEToNoAmzName;
	private String lovDescEVFinAEToNoAmzName;
	private String lovDescFinToAmzName;
	private String lovDescEVFinToAmzName;
	private String lovDescFinMAmzName;
	private String lovDescEVFinMAmzName;
	private String lovDescFinAERateChgName;
	private String lovDescEVFinAERateChgName;
	private String lovDescFinAERepayName;
	private String lovDescEVFinAERepayName;
	private String lovDescFinLatePayRuleName;
	private String lovDescEVFinLatePayRuleName;
	private String lovDescFinAEEarlyPayName;
	private String lovDescEVFinAEEarlyPayName;
	private String lovDescFinAEEarlySettleName;
	private String lovDescEVFinAEEarlySettleName;
	private String lovDescEVFinAEPlanDefName;
	private String lovDescFinAEPlanDefName;
	private String lovDescEVFinDefRepayName;
	private String lovDescFinDefRepayName;
	private String lovDescFinAEWriteOffName;
	private String lovDescEVFinAEWriteOffName;
	private String lovDescFinAEWriteOffBKName;
	private String lovDescEVFinAEWriteOffBKName;
	private String lovDescFinAEGraceEndName;
	private String lovDescEVFinAEGraceEndName;
	private String lovDescFinProvisionName;
	private String lovDescEVFinProvisionName;
	private String lovDescFinSchdChangeName;
	private String lovDescEVFinSchdChangeName;
	private String lovDescFinAECapitalizeName;
	private String lovDescEVFinAECapitalizeName;
	private String lovDescFinAEProgClaimName;
	private String lovDescFinAEMaturityName;
	private String lovDescEVFinAEProgClaimName;
	private String lovDescEVFinAEMaturityName;
	private String lovDescFinDepreciationRuleName;
	private String lovDescEVFinDepreciationRuleName;
	private String lovDescFinGrcIndBaseRateName;
	private String lovDescFinInstDateName;
	private String lovDescEVFinInstDateName;
	private String lovDescWorkFlowRolesName;
	private String lovDescWorkFlowTypeName;
	private String lovDescFinDepositRestrictedTo;
	private String lovDescFinAEBuyOrInceptionName;
	private String lovDescEVFinAEBuyOrInceptionName;
	private String lovDescFinAESellOrMaturityName;
	private String lovDescEVFinAESellOrMaturityName;
	private String lovDescProductCodeName;
	private String lovDescProductCodeDesc;
	private String lovDescAssetCodeName;
	
	private List<FinTypeAccount> finTypeAccounts=new ArrayList<FinTypeAccount>();
	
	public boolean isNew() {
		return isNewRecord();
	}

	public FinanceType() {

	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("finTypeAccounts");
		return excludeFields;
	}

	public FinanceType(String finCategory) {
		if (StringUtils.trimToEmpty(finCategory).equals("CF")) {
			this.workflowId = WorkFlowUtil.getWorkFlowID("CommodityFinanceType");
		} else {
			this.workflowId = WorkFlowUtil.getWorkFlowID("FinanceType");
		}

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
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

	public String getLovDescFinCcyName() {
		return this.lovDescFinCcyName;
	}

	public void setLovDescFinCcyName(String lovDescFinCcyName) {
		this.lovDescFinCcyName = lovDescFinCcyName;
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

	public String getLovDescFinDaysCalTypeName() {
		return this.lovDescFinDaysCalTypeName;
	}

	public void setLovDescFinDaysCalTypeName(String lovDescFinDaysCalTypeName) {
		this.lovDescFinDaysCalTypeName = lovDescFinDaysCalTypeName;
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

	public String getLovDescFinBankContingentAcTypeName() {
		return lovDescFinBankContingentAcTypeName;
	}

	public void setLovDescFinBankContingentAcTypeName(String lovDescFinBankContingentAcTypeName) {
		this.lovDescFinBankContingentAcTypeName = lovDescFinBankContingentAcTypeName;
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

	public String getLovDescFinSchdMthdName() {
		return this.lovDescFinSchdMthdName;
	}

	public void setLovDescFinSchdMthdName(String lovDescFinSchdMthdName) {
		this.lovDescFinSchdMthdName = lovDescFinSchdMthdName;
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

	public String getLovDescFinRateTypeName() {
		return this.lovDescFinRateTypeName;
	}

	public void setLovDescFinRateTypeName(String lovDescFinRateTypeName) {
		this.lovDescFinRateTypeName = lovDescFinRateTypeName;
	}

	public String getFinBaseRate() {
		return finBaseRate;
	}

	public void setFinBaseRate(String finBaseRate) {
		this.finBaseRate = finBaseRate;
	}

	public String getLovDescFinBaseRateName() {
		return this.lovDescFinBaseRateName;
	}

	public void setLovDescFinBaseRateName(String lovDescFinBaseRateName) {
		this.lovDescFinBaseRateName = lovDescFinBaseRateName;
	}

	public String getFinSplRate() {
		return finSplRate;
	}

	public void setFinSplRate(String finSplRate) {
		this.finSplRate = finSplRate;
	}

	public String getLovDescFinSplRateName() {
		return this.lovDescFinSplRateName;
	}

	public void setLovDescFinSplRateName(String lovDescFinSplRateName) {
		this.lovDescFinSplRateName = lovDescFinSplRateName;
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

	public String getLovDescFinGrcRateTypeName() {
		return this.lovDescFinGrcRateTypeName;
	}

	public void setLovDescFinGrcRateTypeName(String lovDescFinGrcRateTypeName) {
		this.lovDescFinGrcRateTypeName = lovDescFinGrcRateTypeName;
	}

	public String getFinGrcBaseRate() {
		return finGrcBaseRate;
	}

	public void setFinGrcBaseRate(String finGrcBaseRate) {
		this.finGrcBaseRate = finGrcBaseRate;
	}

	public String getLovDescFinGrcBaseRateName() {
		return this.lovDescFinGrcBaseRateName;
	}

	public void setLovDescFinGrcBaseRateName(String lovDescFinGrcBaseRateName) {
		this.lovDescFinGrcBaseRateName = lovDescFinGrcBaseRateName;
	}

	public String getFinGrcSplRate() {
		return finGrcSplRate;
	}

	public void setFinGrcSplRate(String finGrcSplRate) {
		this.finGrcSplRate = finGrcSplRate;
	}

	public String getLovDescFinGrcSplRateName() {
		return this.lovDescFinGrcSplRateName;
	}

	public void setLovDescFinGrcSplRateName(String lovDescFinGrcSplRateName) {
		this.lovDescFinGrcSplRateName = lovDescFinGrcSplRateName;
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

	public String getFInRepayMethod() {
		return fInRepayMethod;
	}

	public void setFInRepayMethod(String fInRepayMethod) {
		this.fInRepayMethod = fInRepayMethod;
	}

	public String getLovDescFInRepayMethodName() {
		return this.lovDescFInRepayMethodName;
	}

	public void setLovDescFInRepayMethodName(String lovDescFInRepayMethodName) {
		this.lovDescFInRepayMethodName = lovDescFInRepayMethodName;
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

	public String getFinLatePayRule() {
		return finLatePayRule;
	}

	public void setFinLatePayRule(String finLatePayRule) {
		this.finLatePayRule = finLatePayRule;
	}

	public String getLovDescFinLatePayRuleName() {
		return this.lovDescFinLatePayRuleName;
	}

	public void setLovDescFinLatePayRuleName(String lovDescFinLatePayRuleName) {
		this.lovDescFinLatePayRuleName = lovDescFinLatePayRuleName;
	}

	public String getFinAEAddDsbOD() {
		return finAEAddDsbOD;
	}

	public void setFinAEAddDsbOD(String finAEAddDsbOD) {
		this.finAEAddDsbOD = finAEAddDsbOD;
	}

	public String getLovDescFinAEAddDsbODName() {
		return this.lovDescFinAEAddDsbODName;
	}

	public void setLovDescFinAEAddDsbODName(String lovDescFinAEAddDsbODName) {
		this.lovDescFinAEAddDsbODName = lovDescFinAEAddDsbODName;
	}

	public String getFinAEAddDsbFD() {
		return finAEAddDsbFD;
	}

	public void setFinAEAddDsbFD(String finAEAddDsbFD) {
		this.finAEAddDsbFD = finAEAddDsbFD;
	}

	public String getLovDescFinAEAddDsbFDName() {
		return this.lovDescFinAEAddDsbFDName;
	}

	public void setLovDescFinAEAddDsbFDName(String lovDescFinAEAddDsbFDName) {
		this.lovDescFinAEAddDsbFDName = lovDescFinAEAddDsbFDName;
	}

	public String getFinAEAddDsbFDA() {
		return finAEAddDsbFDA;
	}

	public void setFinAEAddDsbFDA(String finAEAddDsbFDA) {
		this.finAEAddDsbFDA = finAEAddDsbFDA;
	}

	public String getLovDescFinAEAddDsbFDAName() {
		return this.lovDescFinAEAddDsbFDAName;
	}

	public void setLovDescFinAEAddDsbFDAName(String lovDescFinAEAddDsbFDAName) {
		this.lovDescFinAEAddDsbFDAName = lovDescFinAEAddDsbFDAName;
	}

	public String getFinAEAmzNorm() {
		return finAEAmzNorm;
	}

	public void setFinAEAmzNorm(String finAEAmzNorm) {
		this.finAEAmzNorm = finAEAmzNorm;
	}

	public String getLovDescFinAEAmzNormName() {
		return this.lovDescFinAEAmzNormName;
	}

	public void setLovDescFinAEAmzNormName(String lovDescFinAEAmzNormName) {
		this.lovDescFinAEAmzNormName = lovDescFinAEAmzNormName;
	}

	public String getFinAEAmzSusp() {
		return finAEAmzSusp;
	}

	public void setFinAEAmzSusp(String finAEAmzSusp) {
		this.finAEAmzSusp = finAEAmzSusp;
	}

	public String getLovDescFinAEAmzSuspName() {
		return this.lovDescFinAEAmzSuspName;
	}

	public void setLovDescFinAEAmzSuspName(String lovDescFinAEAmzSuspName) {
		this.lovDescFinAEAmzSuspName = lovDescFinAEAmzSuspName;
	}

	public String getFinAEToNoAmz() {
		return finAEToNoAmz;
	}

	public void setFinAEToNoAmz(String finAEToNoAmz) {
		this.finAEToNoAmz = finAEToNoAmz;
	}

	public String getLovDescFinAEToNoAmzName() {
		return this.lovDescFinAEToNoAmzName;
	}

	public void setLovDescFinAEToNoAmzName(String lovDescFinAEToNoAmzName) {
		this.lovDescFinAEToNoAmzName = lovDescFinAEToNoAmzName;
	}

	public String getFinToAmz() {
		return finToAmz;
	}

	public void setFinToAmz(String finToAmz) {
		this.finToAmz = finToAmz;
	}

	public String getLovDescFinToAmzName() {
		return this.lovDescFinToAmzName;
	}

	public void setLovDescFinToAmzName(String lovDescFinToAmzName) {
		this.lovDescFinToAmzName = lovDescFinToAmzName;
	}

	public String getFinAEMAmz() {
    	return finAEMAmz;
    }
	public void setFinAEMAmz(String finAEMAmz) {
    	this.finAEMAmz = finAEMAmz;
    }

	public String getLovDescFinMAmzName() {
    	return lovDescFinMAmzName;
    }
	public void setLovDescFinMAmzName(String lovDescFinMAmzName) {
    	this.lovDescFinMAmzName = lovDescFinMAmzName;
    }

	public String getFinAERateChg() {
		return finAERateChg;
	}

	public void setFinAERateChg(String finAERateChg) {
		this.finAERateChg = finAERateChg;
	}

	public String getLovDescFinAERateChgName() {
		return this.lovDescFinAERateChgName;
	}

	public void setLovDescFinAERateChgName(String lovDescFinAERateChgName) {
		this.lovDescFinAERateChgName = lovDescFinAERateChgName;
	}

	public String getFinAERepay() {
		return finAERepay;
	}

	public void setFinAERepay(String finAERepay) {
		this.finAERepay = finAERepay;
	}

	public String getLovDescFinAERepayName() {
		return this.lovDescFinAERepayName;
	}

	public void setLovDescFinAERepayName(String lovDescFinAERepayName) {
		this.lovDescFinAERepayName = lovDescFinAERepayName;
	}

	public String getFinAEEarlyPay() {
		return finAEEarlyPay;
	}

	public void setFinAEEarlyPay(String finAEEarlyPay) {
		this.finAEEarlyPay = finAEEarlyPay;
	}

	public String getLovDescFinAEEarlyPayName() {
		return this.lovDescFinAEEarlyPayName;
	}

	public void setLovDescFinAEEarlyPayName(String lovDescFinAEEarlyPayName) {
		this.lovDescFinAEEarlyPayName = lovDescFinAEEarlyPayName;
	}

	public String getFinAEEarlySettle() {
		return finAEEarlySettle;
	}

	public void setFinAEEarlySettle(String finAEEarlySettle) {
		this.finAEEarlySettle = finAEEarlySettle;
	}

	public String getLovDescFinAEEarlySettleName() {
		return this.lovDescFinAEEarlySettleName;
	}

	public void setLovDescFinAEEarlySettleName(String lovDescFinAEEarlySettleName) {
		this.lovDescFinAEEarlySettleName = lovDescFinAEEarlySettleName;
	}

	public String getFinAEWriteOff() {
		return finAEWriteOff;
	}

	public void setFinAEWriteOff(String finAEWriteOff) {
		this.finAEWriteOff = finAEWriteOff;
	}

	public String getLovDescFinAEWriteOffName() {
		return this.lovDescFinAEWriteOffName;
	}

	public void setLovDescFinAEWriteOffName(String lovDescFinAEWriteOffName) {
		this.lovDescFinAEWriteOffName = lovDescFinAEWriteOffName;
	}

	public String getFinAEWriteOffBK() {
    	return finAEWriteOffBK;
    }

	public void setFinAEWriteOffBK(String finAEWriteOffBK) {
    	this.finAEWriteOffBK = finAEWriteOffBK;
    }

	public String getFinAEGraceEnd() {
    	return finAEGraceEnd;
    }

	public void setFinAEGraceEnd(String finAEGraceEnd) {
    	this.finAEGraceEnd = finAEGraceEnd;
    }

	public String getLovDescFinAEWriteOffBKName() {
    	return lovDescFinAEWriteOffBKName;
    }

	public void setLovDescFinAEWriteOffBKName(String lovDescFinAEWriteOffBKName) {
    	this.lovDescFinAEWriteOffBKName = lovDescFinAEWriteOffBKName;
    }

	public String getLovDescFinAEGraceEndName() {
    	return lovDescFinAEGraceEndName;
    }

	public void setLovDescFinAEGraceEndName(String lovDescFinAEGraceEndName) {
    	this.lovDescFinAEGraceEndName = lovDescFinAEGraceEndName;
    }

	public boolean isFinIsActive() {
		return finIsActive;
	}

	public void setFinIsActive(boolean finIsActive) {
		this.finIsActive = finIsActive;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(Timestamp lastMntON) {
		this.lastMntOn = lastMntON;
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

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getNextRoleCode() {
		return nextRoleCode;
	}

	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getNextTaskId() {
		return nextTaskId;
	}

	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getUserAction() {
		return userAction;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public boolean isWorkflow() {
		if (this.workflowId == 0) {
			return false;
		}
		return true;
	}

	public long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	// Overridden Equals method to handle the comparison
	public boolean equals(FinanceType financeType) {
		return getId() == financeType.getId();
	}

	/**
	 * Check object is equal or not with Other object
	 * 
	 * @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof FinanceType) {
			FinanceType financeType = (FinanceType) obj;
			return equals(financeType);
		}
		return false;
	}

	/** @return the lovDescEVFinAEAddDsbODName */
	public String getLovDescEVFinAEAddDsbODName() {
		return lovDescEVFinAEAddDsbODName;
	}

	/**
	 * @param lovDescEVFinAEAddDsbODName
	 *            the lovDescEVFinAEAddDsbODName to set
	 */
	public void setLovDescEVFinAEAddDsbODName(String lovDescEVFinAEAddDsbODName) {
		this.lovDescEVFinAEAddDsbODName = lovDescEVFinAEAddDsbODName;
	}

	/** @return the lovDescEVFinAEAddDsbFDName */
	public String getLovDescEVFinAEAddDsbFDName() {
		return lovDescEVFinAEAddDsbFDName;
	}

	/**
	 * @param lovDescEVFinAEAddDsbFDName
	 *            the lovDescEVFinAEAddDsbFDName to set
	 */
	public void setLovDescEVFinAEAddDsbFDName(String lovDescEVFinAEAddDsbFDName) {
		this.lovDescEVFinAEAddDsbFDName = lovDescEVFinAEAddDsbFDName;
	}

	/** @return the lovDescEVFinAEAddDsbFDAName */
	public String getLovDescEVFinAEAddDsbFDAName() {
		return lovDescEVFinAEAddDsbFDAName;
	}

	/**
	 * @param lovDescEVFinAEAddDsbFDAName
	 *            the lovDescEVFinAEAddDsbFDAName to set
	 */
	public void setLovDescEVFinAEAddDsbFDAName(String lovDescEVFinAEAddDsbFDAName) {
		this.lovDescEVFinAEAddDsbFDAName = lovDescEVFinAEAddDsbFDAName;
	}

	/** @return the lovDescEVFinAEAmzNormName */
	public String getLovDescEVFinAEAmzNormName() {
		return lovDescEVFinAEAmzNormName;
	}

	/**
	 * @param lovDescEVFinAEAmzNormName
	 *            the lovDescEVFinAEAmzNormName to set
	 */
	public void setLovDescEVFinAEAmzNormName(String lovDescEVFinAEAmzNormName) {
		this.lovDescEVFinAEAmzNormName = lovDescEVFinAEAmzNormName;
	}

	/** @return the lovDescEVFinAEAmzSuspName */
	public String getLovDescEVFinAEAmzSuspName() {
		return lovDescEVFinAEAmzSuspName;
	}

	/**
	 * @param lovDescEVFinAEAmzSuspName
	 *            the lovDescEVFinAEAmzSuspName to set
	 */
	public void setLovDescEVFinAEAmzSuspName(String lovDescEVFinAEAmzSuspName) {
		this.lovDescEVFinAEAmzSuspName = lovDescEVFinAEAmzSuspName;
	}

	/** @return the lovDescEVFinAEToNoAmzName */
	public String getLovDescEVFinAEToNoAmzName() {
		return lovDescEVFinAEToNoAmzName;
	}

	/**
	 * @param lovDescEVFinAEToNoAmzName
	 *            the lovDescEVFinAEToNoAmzName to set
	 */
	public void setLovDescEVFinAEToNoAmzName(String lovDescEVFinAEToNoAmzName) {
		this.lovDescEVFinAEToNoAmzName = lovDescEVFinAEToNoAmzName;
	}

	/** @return the lovDescEVFinAEIncPftName */
	public String getLovDescEVFinAERateChgName() {
		return lovDescEVFinAERateChgName;
	}

	/**
	 * @param lovDescEVFinAERateChgName
	 *            the lovDescEVFinAEIncPftName to set
	 */
	public void setLovDescEVFinAERateChgName(String lovDescEVFinAERateChgName) {
		this.lovDescEVFinAERateChgName = lovDescEVFinAERateChgName;
	}

	/** @return the lovDescEVFinAERepayName */
	public String getLovDescEVFinAERepayName() {
		return lovDescEVFinAERepayName;
	}

	/**
	 * @param lovDescEVFinAERepayName
	 *            the lovDescEVFinAERepayName to set
	 */
	public void setLovDescEVFinAERepayName(String lovDescEVFinAERepayName) {
		this.lovDescEVFinAERepayName = lovDescEVFinAERepayName;
	}

	/** @return the lovDescEVFinAEEarlyPayName */
	public String getLovDescEVFinAEEarlyPayName() {
		return lovDescEVFinAEEarlyPayName;
	}

	/**
	 * @param lovDescEVFinAEEarlyPayName
	 *            the lovDescEVFinAEEarlyPayName to set
	 */
	public void setLovDescEVFinAEEarlyPayName(String lovDescEVFinAEEarlyPayName) {
		this.lovDescEVFinAEEarlyPayName = lovDescEVFinAEEarlyPayName;
	}

	/** @return the lovDescEVFinAEEarlySettleName */
	public String getLovDescEVFinAEEarlySettleName() {
		return lovDescEVFinAEEarlySettleName;
	}

	/**
	 * @param lovDescEVFinAEEarlySettleName
	 *            the lovDescEVFinAEEarlySettleName to set
	 */
	public void setLovDescEVFinAEEarlySettleName(String lovDescEVFinAEEarlySettleName) {
		this.lovDescEVFinAEEarlySettleName = lovDescEVFinAEEarlySettleName;
	}

	public String getLovDescEVFinAEWriteOffName() {
		return lovDescEVFinAEWriteOffName;
	}
	public void setLovDescEVFinAEWriteOffName(String lovDescEVFinAEWriteOffName) {
		this.lovDescEVFinAEWriteOffName = lovDescEVFinAEWriteOffName;
	}
	
	public String getLovDescEVFinAEWriteOffBKName() {
		return lovDescEVFinAEWriteOffBKName;
	}
	public void setLovDescEVFinAEWriteOffBKName(String lovDescEVFinAEWriteOffBKName) {
		this.lovDescEVFinAEWriteOffBKName = lovDescEVFinAEWriteOffBKName;
	}
	
	public String getLovDescEVFinAEGraceEndName() {
		return lovDescEVFinAEGraceEndName;
	}
	public void setLovDescEVFinAEGraceEndName(String lovDescEVFinAEGraceEndName) {
		this.lovDescEVFinAEGraceEndName = lovDescEVFinAEGraceEndName;
	}

	public String getLovDescEVFinToAmzName() {
		return lovDescEVFinToAmzName;
	}
	public void setLovDescEVFinToAmzName(String lovDescEVFinToAmzName) {
		this.lovDescEVFinToAmzName = lovDescEVFinToAmzName;
	}
	
	public String getLovDescEVFinMAmzName() {
    	return lovDescEVFinMAmzName;
    }
	public void setLovDescEVFinMAmzName(String lovDescEVFinMAmzName) {
    	this.lovDescEVFinMAmzName = lovDescEVFinMAmzName;
    }

	/**
	 * @param finOrgPrfUnchanged
	 *            the finOrgPrfUnchanged to set
	 */
	public void setFinOrgPrfUnchanged(boolean finOrgPrfUnchanged) {
		this.finOrgPrfUnchanged = finOrgPrfUnchanged;
	}

	/** @return the finOrgPrfUnchanged */
	public boolean isFinOrgPrfUnchanged() {
		return finOrgPrfUnchanged;
	}

	/**
	 * @param finFrEqrepayment
	 *            the finFrEqrepayment to set
	 */
	public void setFinFrEqrepayment(boolean finFrEqrepayment) {
		this.finFrEqrepayment = finFrEqrepayment;
	}

	/** @return the finFrEqrepayment */
	public boolean isFinFrEqrepayment() {
		return finFrEqrepayment;
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

	public void setLovDescFinFormetter(int lovDescFinFormetter) {
		this.lovDescFinFormetter = lovDescFinFormetter;
	}

	public int getLovDescFinFormetter() {
		return lovDescFinFormetter;
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

	public String getFinGrcRvwRateApplFor() {
		return finGrcRvwRateApplFor;
	}

	public void setFinGrcRvwRateApplFor(String finGrcRvwRateApplFor) {
		this.finGrcRvwRateApplFor = finGrcRvwRateApplFor;
	}

	public boolean isFinGrcAlwRateChgAnyDate() {
		return finGrcAlwRateChgAnyDate;
	}

	public void setFinGrcAlwRateChgAnyDate(boolean finGrcAlwRateChgAnyDate) {
		this.finGrcAlwRateChgAnyDate = finGrcAlwRateChgAnyDate;
	}

	public BigDecimal getFinMinDownPayAmount() {
		return finMinDownPayAmount;
	}

	public void setFinMinDownPayAmount(BigDecimal finMinDownPayAmount) {
		this.finMinDownPayAmount = finMinDownPayAmount;
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

	public void setFinAssetType(long finAssetType) {
		this.finAssetType = finAssetType;
	}

	public long getFinAssetType() {
		return finAssetType;
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

	public void setFinAEBuyOrInception(String finAEBuyOrInception) {
		this.finAEBuyOrInception = finAEBuyOrInception;
	}

	public String getFinAEBuyOrInception() {
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

	public void setFinAESellOrMaturity(String finAESellOrMaturity) {
		this.finAESellOrMaturity = finAESellOrMaturity;
	}

	public String getFinAESellOrMaturity() {
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

	public List<FinanceMarginSlab> getFinanceMarginSlabsList() {
		return financeMarginSlabsList;
	}

	public void setFinanceMarginSlabsList(List<FinanceMarginSlab> financeMarginSlabsList) {
		this.financeMarginSlabsList = financeMarginSlabsList;
	}

	public void setLovDescMarginSlabAuditDetailMap(
	        HashMap<String, List<AuditDetail>> lovDescMarginSlabAuditDetailMap) {
		this.lovDescMarginSlabAuditDetailMap = lovDescMarginSlabAuditDetailMap;
	}

	public HashMap<String, List<AuditDetail>> getLovDescMarginSlabAuditDetailMap() {
		return lovDescMarginSlabAuditDetailMap;
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

	public String getLovDescProductCodeName() {
		return lovDescProductCodeName;
	}

	public void setLovDescProductCodeName(String lovDescProductCodeName) {
		this.lovDescProductCodeName = lovDescProductCodeName;
	}
	
	public String getLovDescAssetCodeName() {
		return lovDescAssetCodeName;
	}

	public void setLovDescAssetCodeName(String lovDescAssetCodeName) {
		this.lovDescAssetCodeName = lovDescAssetCodeName;
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

	public String getFinAEPlanDef() {
		return finAEPlanDef;
	}

	public void setFinAEPlanDef(String finAEPlanDef) {
		this.finAEPlanDef = finAEPlanDef;
	}

	public String getLovDescEVFinAEPlanDefName() {
		return lovDescEVFinAEPlanDefName;
	}

	public void setLovDescEVFinAEPlanDefName(String lovDescEVFinAEPlanDefName) {
		this.lovDescEVFinAEPlanDefName = lovDescEVFinAEPlanDefName;
	}

	public String getLovDescFinAEPlanDefName() {
		return lovDescFinAEPlanDefName;
	}

	public void setLovDescFinAEPlanDefName(String lovDescFinAEPlanDefName) {
		this.lovDescFinAEPlanDefName = lovDescFinAEPlanDefName;
	}

	public String getFinDefRepay() {
		return finDefRepay;
	}

	public void setFinDefRepay(String finDefRepay) {
		this.finDefRepay = finDefRepay;
	}

	public String getLovDescEVFinDefRepayName() {
		return lovDescEVFinDefRepayName;
	}

	public void setLovDescEVFinDefRepayName(String lovDescEVFinDefRepayName) {
		this.lovDescEVFinDefRepayName = lovDescEVFinDefRepayName;
	}

	public String getLovDescFinDefRepayName() {
		return lovDescFinDefRepayName;
	}

	public void setLovDescFinDefRepayName(String lovDescFinDefRepayName) {
		this.lovDescFinDefRepayName = lovDescFinDefRepayName;
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

	public void setLovDescEVFinLatePayRuleName(String lovDescEVFinLatePayRuleName) {
		this.lovDescEVFinLatePayRuleName = lovDescEVFinLatePayRuleName;
	}

	public String getLovDescEVFinLatePayRuleName() {
		return lovDescEVFinLatePayRuleName;
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

	public String getFinProvision() {
		return finProvision;
	}

	public void setFinProvision(String finProvision) {
		this.finProvision = finProvision;
	}

	public String getLovDescFinProvisionName() {
		return lovDescFinProvisionName;
	}

	public void setLovDescFinProvisionName(String lovDescFinProvisionName) {
		this.lovDescFinProvisionName = lovDescFinProvisionName;
	}

	public String getLovDescEVFinProvisionName() {
		return lovDescEVFinProvisionName;
	}

	public void setLovDescEVFinProvisionName(String lovDescEVFinProvisionName) {
		this.lovDescEVFinProvisionName = lovDescEVFinProvisionName;
	}

	public void setFinSchdChange(String finSchdChange) {
		this.finSchdChange = finSchdChange;
	}

	public String getFinSchdChange() {
		return finSchdChange;
	}

	public void setLovDescFinSchdChangeName(String lovDescFinSchdChangeName) {
		this.lovDescFinSchdChangeName = lovDescFinSchdChangeName;
	}

	public String getLovDescFinSchdChangeName() {
		return lovDescFinSchdChangeName;
	}

	public void setLovDescEVFinSchdChangeName(String lovDescEVFinSchdChangeName) {
		this.lovDescEVFinSchdChangeName = lovDescEVFinSchdChangeName;
	}

	public String getLovDescEVFinSchdChangeName() {
		return lovDescEVFinSchdChangeName;
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

	public void setFinDepreciationRule(String finDepreciationRule) {
		this.finDepreciationRule = finDepreciationRule;
	}

	public String getFinDepreciationRule() {
		return finDepreciationRule;
	}

	public void setLovDescFinDepreciationRuleName(String lovDescFinDepreciationRuleName) {
		this.lovDescFinDepreciationRuleName = lovDescFinDepreciationRuleName;
	}

	public String getLovDescFinDepreciationRuleName() {
		return lovDescFinDepreciationRuleName;
	}

	public void setLovDescEVFinDepreciationRuleName(String lovDescEVFinDepreciationRuleName) {
		this.lovDescEVFinDepreciationRuleName = lovDescEVFinDepreciationRuleName;
	}

	public String getLovDescEVFinDepreciationRuleName() {
		return lovDescEVFinDepreciationRuleName;
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



	public boolean isFinAlwIndRate() {
		return finAlwIndRate;
	}

	public void setFinAlwIndRate(boolean finAlwIndRate) {
		this.finAlwIndRate = finAlwIndRate;
	}

	public String getFinIndBaseRate() {
		return finIndBaseRate;
	}

	public void setFinIndBaseRate(String finIndBaseRate) {
		this.finIndBaseRate = finIndBaseRate;
	}

	public boolean isFinGrcAlwIndRate() {
		return finGrcAlwIndRate;
	}

	public void setFinGrcAlwIndRate(boolean finGrcAlwIndRate) {
		this.finGrcAlwIndRate = finGrcAlwIndRate;
	}

	public String getFinGrcIndBaseRate() {
		return finGrcIndBaseRate;
	}

	public void setFinGrcIndBaseRate(String finGrcIndBaseRate) {
		this.finGrcIndBaseRate = finGrcIndBaseRate;
	}

	public void setLovDescAERule(HashMap<String, AccountingSet> lovDescAERule) {
		this.lovDescAERule = lovDescAERule;
	}

	public String getLovDescFinIndBaseRateName() {
		return lovDescFinIndBaseRateName;
	}

	public void setLovDescFinIndBaseRateName(String lovDescFinIndBaseRateName) {
		this.lovDescFinIndBaseRateName = lovDescFinIndBaseRateName;
	}

	public String getLovDescFinGrcIndBaseRateName() {
		return lovDescFinGrcIndBaseRateName;
	}

	public void setLovDescFinGrcIndBaseRateName(String lovDescFinGrcIndBaseRateName) {
		this.lovDescFinGrcIndBaseRateName = lovDescFinGrcIndBaseRateName;
	}

	public String getFinAECapitalize() {
		return finAECapitalize;
	}

	public void setFinAECapitalize(String finAECapitalize) {
		this.finAECapitalize = finAECapitalize;
	}

	public String getLovDescFinAECapitalizeName() {
		return lovDescFinAECapitalizeName;
	}

	public void setLovDescFinAECapitalizeName(String lovDescFinAECapitalizeName) {
		this.lovDescFinAECapitalizeName = lovDescFinAECapitalizeName;
	}

	public String getLovDescEVFinAECapitalizeName() {
		return lovDescEVFinAECapitalizeName;
	}

	public void setLovDescEVFinAECapitalizeName(String lovDescEVFinAECapitalizeName) {
		this.lovDescEVFinAECapitalizeName = lovDescEVFinAECapitalizeName;
	}

	public void setAllowRIAInvestment(boolean allowRIAInvestment) {
		this.allowRIAInvestment = allowRIAInvestment;
	}

	public boolean isAllowRIAInvestment() {
		return allowRIAInvestment;
	}

	public void setAllowParllelFinance(boolean allowParllelFinance) {
		this.allowParllelFinance = allowParllelFinance;
	}

	public boolean isAllowParllelFinance() {
		return allowParllelFinance;
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

	public void setFinInstDate(String finInstDate) {
		this.finInstDate = finInstDate;
	}

	public String getFinInstDate() {
		return finInstDate;
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

	public void setLovDescFinInstDateName(String lovDescFinInstDateName) {
		this.lovDescFinInstDateName = lovDescFinInstDateName;
	}

	public String getLovDescFinInstDateName() {
		return lovDescFinInstDateName;
	}

	public void setLovDescEVFinInstDateName(String lovDescEVFinInstDateName) {
		this.lovDescEVFinInstDateName = lovDescEVFinInstDateName;
	}

	public String getLovDescEVFinInstDateName() {
		return lovDescEVFinInstDateName;
	}

	public void setFinRepayPftOnFrq(boolean finRepayPftOnFrq) {
		this.finRepayPftOnFrq = finRepayPftOnFrq;
	}

	public boolean isFinRepayPftOnFrq() {
		return finRepayPftOnFrq;
	}

	public void setFinAEProgClaim(String finAEProgClaim) {
	    this.finAEProgClaim = finAEProgClaim;
    }

	public String getFinAEProgClaim() {
	    return finAEProgClaim;
    }

	public void setLovDescFinAEProgClaimName(String lovDescFinAEProgClaimName) {
	    this.lovDescFinAEProgClaimName = lovDescFinAEProgClaimName;
    }

	public String getLovDescFinAEProgClaimName() {
	    return lovDescFinAEProgClaimName;
    }

	public void setLovDescEVFinAEProgClaimName(String lovDescEVFinAEProgClaimName) {
	    this.lovDescEVFinAEProgClaimName = lovDescEVFinAEProgClaimName;
    }

	public String getLovDescEVFinAEProgClaimName() {
	    return lovDescEVFinAEProgClaimName;
    }

	public String getFinAEMaturity() {
    	return finAEMaturity;
    }

	public void setFinAEMaturity(String finAEMaturity) {
    	this.finAEMaturity = finAEMaturity;
    }

	public String getLovDescFinAEMaturityName() {
    	return lovDescFinAEMaturityName;
    }

	public void setLovDescFinAEMaturityName(String lovDescFinAEMaturityName) {
    	this.lovDescFinAEMaturityName = lovDescFinAEMaturityName;
    }

	public String getLovDescEVFinAEMaturityName() {
    	return lovDescEVFinAEMaturityName;
    }

	public void setLovDescEVFinAEMaturityName(String lovDescEVFinAEMaturityName) {
    	this.lovDescEVFinAEMaturityName = lovDescEVFinAEMaturityName;
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

	public String getLovDescProductCodeDesc() {
    	return lovDescProductCodeDesc;
    }
	public void setLovDescProductCodeDesc(String lovDescProductCodeDesc) {
    	this.lovDescProductCodeDesc = lovDescProductCodeDesc;
    }

	public List<FinTypeAccount> getFinTypeAccounts() {
    	return finTypeAccounts;
    }

	public void setFinTypeAccounts(List<FinTypeAccount> finTypeAccounts) {
    	this.finTypeAccounts = finTypeAccounts;
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

	public boolean isTakafulMandatory() {
		return takafulMandatory;
	}
	public void setTakafulMandatory(boolean takafulMandatory) {
		this.takafulMandatory = takafulMandatory;
	}

	public boolean isTakafulReq() {
		return takafulReq;
	}
	public void setTakafulReq(boolean takafulReq) {
		this.takafulReq = takafulReq;
	}

	public String getRemFeeSchdMethod() {
		return remFeeSchdMethod;
	}
	public void setRemFeeSchdMethod(String remFeeSchdMethod) {
		this.remFeeSchdMethod = remFeeSchdMethod;
	}
}
