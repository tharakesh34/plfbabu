package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;
import java.util.HashMap;

public class DataSetFiller {

	private String custCIF;
	private String custCOB;
	private String custCtgCode;
	private String custIndustry;
	private String custIsStaff;
	private String custNationality;
	private String custParentCountry;
	private String custResdCountry;
	private String custRiskCountry;
	private String custSector;
	private String custSegment;
	private String custSubSector;
	private String custSubSegment;
	private String custTypeCode;
	private String reqCampaign;
	private String reqFinAcType;
	private String reqFinCcy;
	private String reqFinType;
	private String reqFinBranch;
	private String reqGLHead;
	private String reqProduct;
	private String debitOrCredit;
	private int terms;
	private boolean isNewLoan= true;
	
	private BigDecimal DISBURSE=new BigDecimal(0);
	private BigDecimal PFT=new BigDecimal(0);
	private BigDecimal PFTS=new BigDecimal(0);
	private BigDecimal PFTSP=new BigDecimal(0);
	private BigDecimal PFTSB=new BigDecimal(0);
	private BigDecimal PFTAP=new BigDecimal(0);
	private BigDecimal PFTAB=new BigDecimal(0);
	private BigDecimal PRI=new BigDecimal(0);
	private BigDecimal PRIS=new BigDecimal(0);
	private BigDecimal PRISP=new BigDecimal(0);
	private BigDecimal PRISB=new BigDecimal(0);
	private BigDecimal PRIAP=new BigDecimal(0);
	private BigDecimal PRIAB=new BigDecimal(0);
	private BigDecimal DACCRUE =new BigDecimal(0);
	private BigDecimal NACCRUE =new BigDecimal(0);
	private BigDecimal PFTCHG=new BigDecimal(0);
	private BigDecimal CPZCHG=new BigDecimal(0);
	private BigDecimal RPPFT=new BigDecimal(0);
	private BigDecimal RPPRI=new BigDecimal(0);
	private BigDecimal RPTOT=new BigDecimal(0);
	private BigDecimal ACCRUE=new BigDecimal(0);
	private BigDecimal ACCRUE_S=new BigDecimal(0);
	private BigDecimal DOWNPAY=new BigDecimal(0);
	private BigDecimal REFUND =new BigDecimal(0);
	private BigDecimal CPZTOT=new BigDecimal(0);
	private BigDecimal CPZPRV=new BigDecimal(0);
	private BigDecimal CPZCUR=new BigDecimal(0);
	private BigDecimal CPZNXT=new BigDecimal(0);
	private BigDecimal PROVAMT=new BigDecimal(0);
	
	private int cPNoOfDays;
	private int cpDaysTill;
	private int tPPNoOfDays;
	private int daysDiff;
	
	private BigDecimal finAmount=new BigDecimal(0);
	private int finOverDueCntInPast;
	private boolean finOverDueInPast;
	private int noOfInst;
	private int frqDfrCount;
	private int ODDays;
	private int ODInst;
	private int rpyDfrCount;
	
	private int ELPDAYS;
	private int ELPMNTS;
	private int ELPTERMS;
	private int TTLDAYS;
	private int TTLMNTS;
	private int TTLTERMS;
	
	private BigDecimal PROVDUE = BigDecimal.ZERO;
	private BigDecimal SUSPNOW = new BigDecimal(0);
	private BigDecimal SUSPRLS = new BigDecimal(0);
	private BigDecimal PENALTY = new BigDecimal(0);
	private BigDecimal WAIVER = new BigDecimal(0);
	private BigDecimal ODCPLShare = new BigDecimal(0);
	private BigDecimal AstValO = new BigDecimal(0);
	
	//ISTISNA Details
	private BigDecimal CLAIMAMT = BigDecimal.ZERO;
	private BigDecimal DEFFEREDCOST = BigDecimal.ZERO;
	private BigDecimal CURRETBILL = BigDecimal.ZERO;
	private BigDecimal TTLRETBILL = BigDecimal.ZERO;
	
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
	
	public String getCustIsStaff() {
		return custIsStaff;
	}
	public void setCustIsStaff(String custIsStaff) {
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
	
	public String getCustSegment() {
		return custSegment;
	}
	public void setCustSegment(String custSegment) {
		this.custSegment = custSegment;
	}
	
	public String getCustSubSector() {
		return custSubSector;
	}
	public void setCustSubSector(String custSubSector) {
		this.custSubSector = custSubSector;
	}
	
	public String getCustSubSegment() {
		return custSubSegment;
	}
	public void setCustSubSegment(String custSubSegment) {
		this.custSubSegment = custSubSegment;
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
	public void setCPZCHG(BigDecimal CPZCHG) {
		this.CPZCHG = CPZCHG;
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
	public void setACCRUE_S(BigDecimal aCCRUE_S) {
		ACCRUE_S = aCCRUE_S;
	}
	
	public BigDecimal getDOWNPAY() {
		return DOWNPAY;
	}
	public void setDOWNPAY(BigDecimal dOWNPAY) {
		this.DOWNPAY = dOWNPAY;
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
