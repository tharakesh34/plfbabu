package com.pennant.backend.model.rulefactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;

public class DataSetFiller implements Serializable { 

    private static final long serialVersionUID = -1586459460155302565L;
    
	private String custCIF;
	private String custCOB;
	private String custCtgCode;
	private String custIndustry;
	private boolean custIsStaff;
	private String custNationality;
	private String custParentCountry;
	private String custResdCountry;
	private String custRiskCountry;
	private String custSector;
	private String custSubSector;
	private String custTypeCode;
	private String reqCampaign;
	private String reqFinAcType;
	private String reqFinCcy;
	private String reqFinType;
	private String reqFinBranch;
	private String reqGLHead;
	private String reqProduct;
	private String reqFinPurpose;
	private String debitOrCredit;
	private int terms = 0;
	private int tenure = 0;
	private boolean isNewLoan= true;
	
	private BigDecimal DISBURSE=BigDecimal.ZERO;
	private BigDecimal FEEAMOUNT=BigDecimal.ZERO;
	private BigDecimal PFT=BigDecimal.ZERO;
	private BigDecimal PFTS=BigDecimal.ZERO;
	private BigDecimal PFTSP=BigDecimal.ZERO;
	private BigDecimal PFTSB=BigDecimal.ZERO;
	private BigDecimal PFTAP=BigDecimal.ZERO;
	private BigDecimal PFTAB=BigDecimal.ZERO;
	private BigDecimal PRI=BigDecimal.ZERO;
	private BigDecimal PRIS=BigDecimal.ZERO;
	private BigDecimal PRISP=BigDecimal.ZERO;
	private BigDecimal PRISB=BigDecimal.ZERO;
	private BigDecimal PRIAP=BigDecimal.ZERO;
	private BigDecimal PRIAB=BigDecimal.ZERO;
	private BigDecimal DACCRUE =BigDecimal.ZERO;
	private BigDecimal NACCRUE =BigDecimal.ZERO;
	private BigDecimal PFTCHG=BigDecimal.ZERO;
	private BigDecimal CPZCHG=BigDecimal.ZERO;
	private BigDecimal RPPFT=BigDecimal.ZERO;
	private BigDecimal RPPRI=BigDecimal.ZERO;
	private BigDecimal RPTOT=BigDecimal.ZERO;
	private BigDecimal ACCRUE=BigDecimal.ZERO;
	private BigDecimal ACCRUE_S=BigDecimal.ZERO;
	private BigDecimal ACCRUETSFD=BigDecimal.ZERO;
	private BigDecimal DOWNPAY=BigDecimal.ZERO;
	private BigDecimal DOWNPAYB=BigDecimal.ZERO;
	private BigDecimal DOWNPAYS=BigDecimal.ZERO;
	private BigDecimal REFUND =BigDecimal.ZERO;
	private BigDecimal INSREFUND =BigDecimal.ZERO;
	private BigDecimal CPZTOT=BigDecimal.ZERO;
	private BigDecimal CPZPRV=BigDecimal.ZERO;
	private BigDecimal CPZCUR=BigDecimal.ZERO;
	private BigDecimal CPZNXT=BigDecimal.ZERO;
	private BigDecimal PROVAMT=BigDecimal.ZERO;
	private BigDecimal SECDEPST =BigDecimal.ZERO;
	private BigDecimal RETAMT =BigDecimal.ZERO;
	private BigDecimal NETRET =BigDecimal.ZERO;
	private BigDecimal GRCPFTCH =BigDecimal.ZERO;
	private BigDecimal GRCPFTTB =BigDecimal.ZERO;
	private BigDecimal ADVDUE =BigDecimal.ZERO;
	
	private int cPNoOfDays = 0;
	private int cpDaysTill = 0;
	private int tPPNoOfDays = 0;
	private int daysDiff = 0;
	
	private BigDecimal finAmount = BigDecimal.ZERO;
	private int finOverDueCntInPast = 0;
	private boolean finOverDueInPast = false;
	private int noOfInst = 0;
	private int frqDfrCount = 0;
	private int ODDays = 0;
	private int ODInst = 0;
	private int rpyDfrCount = 0;
	private int finJointAcCount = 0;
	
	private int ELPDAYS = 0;
	private int ELPMNTS = 0;
	private int ELPTERMS = 0;
	private int TTLDAYS = 0;
	private int TTLMNTS = 0;
	private int TTLTERMS = 0;
	
	private BigDecimal PROVDUE = BigDecimal.ZERO;
	private BigDecimal SUSPNOW = BigDecimal.ZERO;
	private BigDecimal SUSPRLS = BigDecimal.ZERO;
	private BigDecimal PENALTY = BigDecimal.ZERO;
	private BigDecimal WAIVER = BigDecimal.ZERO;
	private BigDecimal ODCPLShare = BigDecimal.ZERO;
	private BigDecimal AstValO = BigDecimal.ZERO;
	private BigDecimal FINAMT = BigDecimal.ZERO;
	
	//ISTISNA Details
	private BigDecimal CLAIMAMT = BigDecimal.ZERO;
	private BigDecimal DEFFEREDCOST = BigDecimal.ZERO;
	private BigDecimal CURRETBILL = BigDecimal.ZERO;
	private BigDecimal TTLRETBILL = BigDecimal.ZERO;
	
	//SUKUK Details
	private BigDecimal FACEVAL = BigDecimal.ZERO;
	private BigDecimal PRMVALUE = BigDecimal.ZERO;
	private BigDecimal PRMAMZ = BigDecimal.ZERO;
	private BigDecimal ACCRBAL = BigDecimal.ZERO;
	private BigDecimal REVALAMT = BigDecimal.ZERO;
	
	//DEPRECIATION Details
	private BigDecimal ACCDPRPRI = BigDecimal.ZERO;
	private BigDecimal DPRPRI = BigDecimal.ZERO;
	private boolean FINISACTIVE ;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getCustCIF() {
		return custCIF;
	}
	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}
	
	public String getCustCOB() {
		return custCOB;
	}
	public void setCustCOB(String custCOB) {
		this.custCOB = custCOB;
	}
	
	public String getCustCtgCode() {
		return custCtgCode;
	}
	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}
	
	public String getCustIndustry() {
		return custIndustry;
	}
	public void setCustIndustry(String custIndustry) {
		this.custIndustry = custIndustry;
	}
	
	public boolean isCustIsStaff() {
    	return custIsStaff;
    }
	public void setCustIsStaff(boolean custIsStaff) {
    	this.custIsStaff = custIsStaff;
    }
	
	public String getCustNationality() {
		return custNationality;
	}
	public void setCustNationality(String custNationality) {
		this.custNationality = custNationality;
	}
	
	public String getCustParentCountry() {
		return custParentCountry;
	}
	public void setCustParentCountry(String custParentCountry) {
		this.custParentCountry = custParentCountry;
	}
	
	public String getCustResdCountry() {
		return custResdCountry;
	}
	public void setCustResdCountry(String custResdCountry) {
		this.custResdCountry = custResdCountry;
	}
	
	public String getCustRiskCountry() {
		return custRiskCountry;
	}
	public void setCustRiskCountry(String custRiskCountry) {
		this.custRiskCountry = custRiskCountry;
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
	
	public String getCustTypeCode() {
		return custTypeCode;
	}
	public void setCustTypeCode(String custTypeCode) {
		this.custTypeCode = custTypeCode;
	}
	
	public String getReqCampaign() {
		return reqCampaign;
	}
	public void setReqCampaign(String reqCampaign) {
		this.reqCampaign = reqCampaign;
	}
	
	public String getReqFinAcType() {
		return reqFinAcType;
	}
	public void setReqFinAcType(String reqFinAcType) {
		this.reqFinAcType = reqFinAcType;
	}
	
	public String getReqFinCcy() {
		return reqFinCcy;
	}
	public void setReqFinCcy(String reqFinCcy) {
		this.reqFinCcy = reqFinCcy;
	}
	
	public String getReqFinType() {
		return reqFinType;
	}
	public void setReqFinType(String reqFinType) {
		this.reqFinType = reqFinType;
	}
	
	public String getReqFinBranch() {
		return reqFinBranch;
	}
	public void setReqFinBranch(String reqFinBranch) {
		this.reqFinBranch = reqFinBranch;
	}
	
	public String getReqGLHead() {
		return reqGLHead;
	}
	public void setReqGLHead(String reqGLHead) {
		this.reqGLHead = reqGLHead;
	}
	
	public String getReqProduct() {
		return reqProduct;
	}
	public void setReqProduct(String reqProduct) {
		this.reqProduct = reqProduct;
	}
	
	public String getReqFinPurpose() {
    	return reqFinPurpose;
    }
	public void setReqFinPurpose(String reqFinPurpose) {
    	this.reqFinPurpose = reqFinPurpose;
    }
	
	public String getDebitOrCredit() {
		return debitOrCredit;
	}
	public void setDebitOrCredit(String debitOrCredit) {
		this.debitOrCredit = debitOrCredit;
	}
	
	public int getTerms() {
		return terms;
	}
	public void setTerms(int terms) {
		this.terms = terms;
	}

	public boolean isNewLoan() {
		return isNewLoan;
	}
	public void setNewLoan(boolean isNewLoan) {
		this.isNewLoan = isNewLoan;
	}
	
	public BigDecimal getDISBURSE() {
		return DISBURSE;
	}
	public void setDISBURSE(BigDecimal dISBURSE) {
		DISBURSE = dISBURSE;
	}
	
	public BigDecimal getPFT() {
		return PFT;
	}
	public void setPFT(BigDecimal pFT) {
		PFT = pFT;
	}
	
	public BigDecimal getPFTS() {
		return PFTS;
	}
	public void setPFTS(BigDecimal pFTS) {
		PFTS = pFTS;
	}
	
	public BigDecimal getPFTSP() {
		return PFTSP;
	}
	public void setPFTSP(BigDecimal pFTSP) {
		PFTSP = pFTSP;
	}
	
	public BigDecimal getPFTSB() {
		return PFTSB;
	}
	public void setPFTSB(BigDecimal pFTSB) {
		PFTSB = pFTSB;
	}
	
	public BigDecimal getPFTAP() {
		return PFTAP;
	}
	public void setPFTAP(BigDecimal pFTAP) {
		PFTAP = pFTAP;
	}
	
	public BigDecimal getPFTAB() {
		return PFTAB;
	}
	public void setPFTAB(BigDecimal pFTAB) {
		PFTAB = pFTAB;
	}
	
	public BigDecimal getPRI() {
		return PRI;
	}
	public void setPRI(BigDecimal pRI) {
		PRI = pRI;
	}
	
	public BigDecimal getPRIS() {
		return PRIS;
	}
	public void setPRIS(BigDecimal pRIS) {
		PRIS = pRIS;
	}
	
	public BigDecimal getPRISP() {
		return PRISP;
	}
	public void setPRISP(BigDecimal pRISP) {
		PRISP = pRISP;
	}
	
	public BigDecimal getPRISB() {
		return PRISB;
	}
	public void setPRISB(BigDecimal pRISB) {
		PRISB = pRISB;
	}
	
	public BigDecimal getPRIAP() {
		return PRIAP;
	}
	public void setPRIAP(BigDecimal pRIAP) {
		PRIAP = pRIAP;
	}
	
	public BigDecimal getPRIAB() {
		return PRIAB;
	}
	public void setPRIAB(BigDecimal pRIAB) {
		PRIAB = pRIAB;
	}
	
	public BigDecimal getDACCRUE() {
    	return DACCRUE;
    }
	public void setDACCRUE(BigDecimal dACCRUE) {
    	DACCRUE = dACCRUE;
    }
	
	public BigDecimal getNACCRUE() {
    	return NACCRUE;
    }
	public void setNACCRUE(BigDecimal nACCRUE) {
    	NACCRUE = nACCRUE;
    }
	
	public BigDecimal getPFTCHG() {
		return PFTCHG;
	}
	public void setPFTCHG(BigDecimal pFTCHG) {
		PFTCHG = pFTCHG;
	}
	
	public BigDecimal getCPZCHG() {
		return CPZCHG;
	}
	public void setCPZCHG(BigDecimal cPZCHG) {
		this.CPZCHG = cPZCHG;
	}
	
	public BigDecimal getRPPFT() {
		return RPPFT;
	}
	public void setRPPFT(BigDecimal rPPFT) {
		RPPFT = rPPFT;
	}
	
	public BigDecimal getRPPRI() {
		return RPPRI;
	}
	public void setRPPRI(BigDecimal rPPRI) {
		RPPRI = rPPRI;
	}
	
	public BigDecimal getRPTOT() {
		return RPTOT;
	}
	public void setRPTOT(BigDecimal rPTOT) {
		RPTOT = rPTOT;
	}
	
	public BigDecimal getACCRUE() {
		return ACCRUE;
	}
	public void setACCRUE(BigDecimal aCCRUE) {
		ACCRUE = aCCRUE;
	}
	
	public BigDecimal getACCRUE_S() {
		return ACCRUE_S;
	}
	public void setACCRUE_S(BigDecimal aCCRUES) {
		ACCRUE_S = aCCRUES;
	}
	
	public BigDecimal getDOWNPAY() {
		return DOWNPAY;
	}
	public void setDOWNPAY(BigDecimal dOWNPAY) {
		this.DOWNPAY = dOWNPAY;
	}
	
	public BigDecimal getDOWNPAYB() {
    	return DOWNPAYB;
    }
	public void setDOWNPAYB(BigDecimal dOWNPAYB) {
    	DOWNPAYB = dOWNPAYB;
    }
	
	public BigDecimal getDOWNPAYS() {
    	return DOWNPAYS;
    }
	public void setDOWNPAYS(BigDecimal dOWNPAYS) {
    	DOWNPAYS = dOWNPAYS;
    }
	
	public BigDecimal getFinAmount() {
		return finAmount;
	}
	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}
	
	public int getFinOverDueCntInPast() {
		return finOverDueCntInPast;
	}
	public void setFinOverDueCntInPast(int finOverDueCntInPast) {
		this.finOverDueCntInPast = finOverDueCntInPast;
	}
	
	public boolean isFinOverDueInPast() {
		return finOverDueInPast;
	}
	public void setFinOverDueInPast(boolean finOverDueInPast) {
		this.finOverDueInPast = finOverDueInPast;
	}
	
	public int getCPNoOfDays() {
		return cPNoOfDays;
	}
	public void setCPNoOfDays(int cPNoOfDays) {
		this.cPNoOfDays = cPNoOfDays;
	}
	
	public int getCpDaysTill() {
		return cpDaysTill;
	}
	public void setCpDaysTill(int cpDaysTill) {
		this.cpDaysTill = cpDaysTill;
	}
	
	public int getTPPNoOfDays() {
		return tPPNoOfDays;
	}
	public void setTPPNoOfDays(int tPPNoOfDays) {
		this.tPPNoOfDays = tPPNoOfDays;
	}
	
	public int getDaysDiff() {
		return daysDiff;
	}
	public void setDaysDiff(int daysDiff) {
		this.daysDiff = daysDiff;
	}
	
	public int getNoOfInst() {
		return noOfInst;
	}
	public void setNoOfInst(int noOfInst) {
		this.noOfInst = noOfInst;
	}
	
	public int getFrqDfrCount() {
		return frqDfrCount;
	}
	public void setFrqDfrCount(int frqDfrCount) {
		this.frqDfrCount = frqDfrCount;
	}
	
	public int getODDays() {
		return ODDays;
	}
	public void setODDays(int oDDays) {
		ODDays = oDDays;
	}
	
	public int getODInst() {
		return ODInst;
	}
	public void setODInst(int oDInst) {
		ODInst = oDInst;
	}
	
	public int getRpyDfrCount() {
		return rpyDfrCount;
	}
	public void setRpyDfrCount(int rpyDfrCount) {
		this.rpyDfrCount = rpyDfrCount;
	}
	
	public int getFinJointAcCount() {
    	return finJointAcCount;
    }
	public void setFinJointAcCount(int finJointAcCount) {
    	this.finJointAcCount = finJointAcCount;
    }
	
	public BigDecimal getREFUND() {
    	return REFUND;
    }
	public void setREFUND(BigDecimal rEFUND) {
    	REFUND = rEFUND;
    }
	
	public BigDecimal getCPZTOT() {
		return CPZTOT;
	}
	public void setCPZTOT(BigDecimal cPZTOT) {
		CPZTOT = cPZTOT;
	}
	
	public BigDecimal getCPZPRV() {
		return CPZPRV;
	}
	public void setCPZPRV(BigDecimal cPZPRV) {
		CPZPRV = cPZPRV;
	}
	
	public BigDecimal getCPZCUR() {
		return CPZCUR;
	}
	public void setCPZCUR(BigDecimal cPZCUR) {
		CPZCUR = cPZCUR;
	}
	
	public BigDecimal getCPZNXT() {
		return CPZNXT;
	}
	public void setCPZNXT(BigDecimal cPZNXT) {
		CPZNXT = cPZNXT;
	}
	
	public void setPROVAMT(BigDecimal pROVAMT) {
	    PROVAMT = pROVAMT;
    }
	public BigDecimal getPROVAMT() {
	    return PROVAMT;
    }
	public int getELPDAYS() {
		return ELPDAYS;
	}
	public void setELPDAYS(int eLPDAYS) {
		ELPDAYS = eLPDAYS;
	}
	
	public int getELPMNTS() {
		return ELPMNTS;
	}
	public void setELPMNTS(int eLPMNTS) {
		ELPMNTS = eLPMNTS;
	}
	
	public int getELPTERMS() {
		return ELPTERMS;
	}
	public void setELPTERMS(int eLPTERMS) {
		ELPTERMS = eLPTERMS;
	}
	
	public int getTTLDAYS() {
		return TTLDAYS;
	}
	public void setTTLDAYS(int tTLDAYS) {
		TTLDAYS = tTLDAYS;
	}
	
	public int getTTLMNTS() {
		return TTLMNTS;
	}
	public void setTTLMNTS(int tTLMNTS) {
		TTLMNTS = tTLMNTS;
	}
	
	public int getTTLTERMS() {
		return TTLTERMS;
	}
	public void setTTLTERMS(int tTLTERMS) {
		TTLTERMS = tTLTERMS;
	}
	
	public BigDecimal getPROVDUE() {
    	return PROVDUE;
    }
	public void setPROVDUE(BigDecimal pROVDUE) {
    	PROVDUE = pROVDUE;
    }
	
	public BigDecimal getSUSPNOW() {
    	return SUSPNOW;
    }
	public void setSUSPNOW(BigDecimal sUSPNOW) {
    	SUSPNOW = sUSPNOW;
    }
	
	public BigDecimal getSUSPRLS() {
    	return SUSPRLS;
    }
	public void setSUSPRLS(BigDecimal sUSPRLS) {
    	SUSPRLS = sUSPRLS;
    }
	
	public BigDecimal getPENALTY() {
    	return PENALTY;
    }
	public void setPENALTY(BigDecimal pENALTY) {
    	PENALTY = pENALTY;
    }
	
	public BigDecimal getWAIVER() {
    	return WAIVER;
    }
	public void setWAIVER(BigDecimal wAIVER) {
    	WAIVER = wAIVER;
    }
	
	public BigDecimal getODCPLShare() {
    	return ODCPLShare;
    }
	public void setODCPLShare(BigDecimal oDCPLShare) {
    	ODCPLShare = oDCPLShare;
    }
	
	public BigDecimal getAstValO() {
    	return AstValO;
    }
	public void setAstValO(BigDecimal astValO) {
    	AstValO = astValO;
    }
	
	public BigDecimal getCLAIMAMT() {
    	return CLAIMAMT;
    }
	public void setCLAIMAMT(BigDecimal cLAIMAMT) {
    	CLAIMAMT = cLAIMAMT;
    }
	
	public BigDecimal getDEFFEREDCOST() {
    	return DEFFEREDCOST;
    }
	public void setDEFFEREDCOST(BigDecimal dEFFEREDCOST) {
    	DEFFEREDCOST = dEFFEREDCOST;
    }
	
	public BigDecimal getCURRETBILL() {
    	return CURRETBILL;
    }
	public void setCURRETBILL(BigDecimal cURRETBILL) {
    	CURRETBILL = cURRETBILL;
    }
	
	public BigDecimal getTTLRETBILL() {
    	return TTLRETBILL;
    }
	public void setTTLRETBILL(BigDecimal tTLRETBILL) {
    	TTLRETBILL = tTLRETBILL;
    }
	public void setSECDEPST(BigDecimal sECDEPST) {
	    SECDEPST = sECDEPST;
    }
	public BigDecimal getSECDEPST() {
	    return SECDEPST;
    }
	public void setRETAMT(BigDecimal rETAMT) {
	    RETAMT = rETAMT;
    }
	public BigDecimal getRETAMT() {
	    return RETAMT;
    }
	public void setNETRET(BigDecimal nETRET) {
	    NETRET = nETRET;
    }
	public BigDecimal getNETRET() {
	    return NETRET;
    }
	public void setGRCPFTCH(BigDecimal gRCPFTCH) {
	    GRCPFTCH = gRCPFTCH;
    }
	public BigDecimal getGRCPFTCH() {
	    return GRCPFTCH;
    }
	public void setFINAMT(BigDecimal fINAMT) {
	    FINAMT = fINAMT;
    }
	public BigDecimal getFINAMT() {
	    return FINAMT;
    }
	public void setGRCPFTTB(BigDecimal gRCPFTTB) {
	    GRCPFTTB = gRCPFTTB;
    }
	public BigDecimal getGRCPFTTB() {
	    return GRCPFTTB;
    }
	public void setADVDUE(BigDecimal aDVDUE) {
	    ADVDUE = aDVDUE;
    }
	public BigDecimal getADVDUE() {
	    return ADVDUE;
    }
	public void setACCRUETSFD(BigDecimal aCCRUETSFD) {
	    ACCRUETSFD = aCCRUETSFD;
    }
	public BigDecimal getACCRUETSFD() {
	    return ACCRUETSFD;
    }
	public void setTenure(int tenure) {
	    this.tenure = tenure;
    }
	public int getTenure() {
	    return tenure;
    }
	
	public BigDecimal getFACEVAL() {
    	return FACEVAL;
    }
	public void setFACEVAL(BigDecimal fACEVAL) {
    	FACEVAL = fACEVAL;
    }
	public BigDecimal getPRMVALUE() {
    	return PRMVALUE;
    }
	public void setPRMVALUE(BigDecimal pRMVALUE) {
    	PRMVALUE = pRMVALUE;
    }
	public BigDecimal getPRMAMZ() {
    	return PRMAMZ;
    }
	public void setPRMAMZ(BigDecimal pRMAMZ) {
    	PRMAMZ = pRMAMZ;
    }
	public BigDecimal getACCRBAL() {
	    return ACCRBAL;
    }
	public void setACCRBAL(BigDecimal aCCRBAL) {
	    ACCRBAL = aCCRBAL;
    }
	public BigDecimal getREVALAMT() {
	    return REVALAMT;
    }
	public void setREVALAMT(BigDecimal rEVALAMT) {
	    REVALAMT = rEVALAMT;
    }
	public BigDecimal getINSREFUND() {
	    return INSREFUND;
    }
	public void setINSREFUND(BigDecimal iNSREFUND) {
	    INSREFUND = iNSREFUND;
    }
	public BigDecimal getFEEAMOUNT() {
	    return FEEAMOUNT;
    }
	public void setFEEAMOUNT(BigDecimal fEEAMOUNT) {
	    FEEAMOUNT = fEEAMOUNT;
    }
	public BigDecimal getACCDPRPRI() {
	    return ACCDPRPRI;
    }
	public void setACCDPRPRI(BigDecimal aCCDPRPRI) {
	    ACCDPRPRI = aCCDPRPRI;
    }
	public BigDecimal getDPRPRI() {
	    return DPRPRI;
    }
	public void setDPRPRI(BigDecimal dPRPRI) {
	    DPRPRI = dPRPRI;
    }
	public boolean isFINISACTIVE() {
		return FINISACTIVE;
	}
	public void setFINISACTIVE(boolean fINISACTIVE) {
		FINISACTIVE = fINISACTIVE;
	}
	
	//Set values into Map
	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> dataSetMap = new HashMap<String, Object>();	
		try {
			for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
				dataSetMap.put(this.getClass().getDeclaredFields()[i].getName(), this.getClass().getDeclaredFields()[i].get(this));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataSetMap;
	}

}
