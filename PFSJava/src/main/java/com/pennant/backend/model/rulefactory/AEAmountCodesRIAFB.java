package com.pennant.backend.model.rulefactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;

public class AEAmountCodesRIAFB implements Serializable {

    private static final long serialVersionUID = -4908143094737504910L;
    
	private String finReference;
	private BigDecimal INVAMT = BigDecimal.ZERO;
	
	// Finance Details For RIA investment
	private BigDecimal 	DISBURSE = BigDecimal.ZERO;	
	private BigDecimal 	FACRUE	 = BigDecimal.ZERO;	
	private BigDecimal 	FDACRUE	 = BigDecimal.ZERO;	
	private BigDecimal 	FMFACR	 = BigDecimal.ZERO;	
	private BigDecimal 	FMFCHG	 = BigDecimal.ZERO;	
	private BigDecimal 	FMFDACR	 = BigDecimal.ZERO;	
	private BigDecimal 	FMFNACR	 = BigDecimal.ZERO;	
	private BigDecimal 	FMUDFEE	 = BigDecimal.ZERO;	
	private BigDecimal 	FNACRUE	 = BigDecimal.ZERO;	
	private BigDecimal 	FLACRUE	 = BigDecimal.ZERO;	
	private BigDecimal 	FPFT	 = BigDecimal.ZERO;	
	private BigDecimal 	FPFTAB	 = BigDecimal.ZERO;	
	private BigDecimal 	FPFTAP	 = BigDecimal.ZERO;	
	private BigDecimal 	FPFTCHG	 = BigDecimal.ZERO;	
	private BigDecimal 	FPFTS	 = BigDecimal.ZERO;	
	private BigDecimal 	FPFTSB	 = BigDecimal.ZERO;	
	private BigDecimal 	FPFTSP	 = BigDecimal.ZERO;	
	private BigDecimal 	FPNLTY	 = BigDecimal.ZERO;	
	private BigDecimal 	FPRI	 = BigDecimal.ZERO;	
	private BigDecimal 	FPRIAB	 = BigDecimal.ZERO;	
	private BigDecimal 	FPRIAP	 = BigDecimal.ZERO;	
	private BigDecimal 	FPRIS	 = BigDecimal.ZERO;	
	private BigDecimal 	FPRISB	 = BigDecimal.ZERO;	
	private BigDecimal 	FPRISP	 = BigDecimal.ZERO;	
	private BigDecimal 	FREFUND	 = BigDecimal.ZERO;	
	private BigDecimal 	FRPMF	 = BigDecimal.ZERO;	
	private BigDecimal 	FRPPFT	 = BigDecimal.ZERO;	
	private BigDecimal 	FRPPRI	 = BigDecimal.ZERO;	
	private BigDecimal 	FWAIVER	 = BigDecimal.ZERO;	

	// Bank Details For RIA investment
	private BigDecimal 	BACRUE	 = BigDecimal.ZERO;	
	private BigDecimal 	BDACRUE	 = BigDecimal.ZERO;	
	private BigDecimal 	BNACRUE	 = BigDecimal.ZERO;	
	private BigDecimal 	BNKINV	 = BigDecimal.ZERO;	
	private BigDecimal 	BPFT	 = BigDecimal.ZERO;	
	private BigDecimal 	BPFTAB	 = BigDecimal.ZERO;	
	private BigDecimal 	BPFTAP	 = BigDecimal.ZERO;	
	private BigDecimal 	BPFTCHG	 = BigDecimal.ZERO;	
	private BigDecimal 	BPFTS	 = BigDecimal.ZERO;	
	private BigDecimal 	BPFTSB	 = BigDecimal.ZERO;	
	private BigDecimal 	BPFTSP	 = BigDecimal.ZERO;	
	private BigDecimal 	BPNLTY	 = BigDecimal.ZERO;	
	private BigDecimal 	BPRI	 = BigDecimal.ZERO;	
	private BigDecimal 	BPRIAB	 = BigDecimal.ZERO;	
	private BigDecimal 	BPRIAP	 = BigDecimal.ZERO;	
	private BigDecimal 	BPRIS	 = BigDecimal.ZERO;	
	private BigDecimal 	BPRISB	 = BigDecimal.ZERO;	
	private BigDecimal 	BPRISP	 = BigDecimal.ZERO;	
	private BigDecimal 	BREFUND	 = BigDecimal.ZERO;	
	private BigDecimal 	BRPPFT	 = BigDecimal.ZERO;	
	private BigDecimal 	BRPPRI	 = BigDecimal.ZERO;	
	private BigDecimal 	BWAIVER	 = BigDecimal.ZERO;
	
	private BigDecimal 	ODCPLShare	 = BigDecimal.ZERO;
	private BigDecimal 	SUSPNOW	 = BigDecimal.ZERO;
	private BigDecimal 	SUSPRLS	 = BigDecimal.ZERO;
	private int 	TTLDAYS;
	private int 	TTLMNTS;
	private int 	TTLTERMS;
	
	public AEAmountCodesRIAFB() {
		
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
	public BigDecimal getINVAMT() {
    	return INVAMT;
    }
	public void setINVAMT(BigDecimal iNVAMT) {
    	INVAMT = iNVAMT;
    }
	public void setDISBURSE(BigDecimal dISBURSE) {
	    DISBURSE = dISBURSE;
    }
	public BigDecimal getDISBURSE() {
	    return DISBURSE;
    }
	public BigDecimal getFACRUE() {
    	return FACRUE;
    }
	public void setFACRUE(BigDecimal fACRUE) {
    	FACRUE = fACRUE;
    }
	public BigDecimal getFDACRUE() {
    	return FDACRUE;
    }
	public void setFDACRUE(BigDecimal fDACRUE) {
    	FDACRUE = fDACRUE;
    }
	public BigDecimal getFMFACR() {
    	return FMFACR;
    }
	public void setFMFACR(BigDecimal fMFACR) {
    	FMFACR = fMFACR;
    }
	public BigDecimal getFMFCHG() {
    	return FMFCHG;
    }
	public void setFMFCHG(BigDecimal fMFCHG) {
    	FMFCHG = fMFCHG;
    }
	public BigDecimal getFMFDACR() {
    	return FMFDACR;
    }
	public void setFMFDACR(BigDecimal fMFDACR) {
    	FMFDACR = fMFDACR;
    }
	public BigDecimal getFMFNACR() {
    	return FMFNACR;
    }
	public void setFMFNACR(BigDecimal fMFNACR) {
    	FMFNACR = fMFNACR;
    }
	public BigDecimal getFMUDFEE() {
    	return FMUDFEE;
    }
	public void setFMUDFEE(BigDecimal fMUDFEE) {
    	FMUDFEE = fMUDFEE;
    }
	public BigDecimal getFNACRUE() {
    	return FNACRUE;
    }
	public void setFNACRUE(BigDecimal fNACRUE) {
    	FNACRUE = fNACRUE;
    }
	public BigDecimal getFLACRUE() {
    	return FLACRUE;
    }
	public void setFLACRUE(BigDecimal fLACRUE) {
    	FLACRUE = fLACRUE;
    }
	public BigDecimal getFPFT() {
    	return FPFT;
    }
	public void setFPFT(BigDecimal fPFT) {
    	FPFT = fPFT;
    }
	public BigDecimal getFPFTAB() {
    	return FPFTAB;
    }
	public void setFPFTAB(BigDecimal fPFTAB) {
    	FPFTAB = fPFTAB;
    }
	public BigDecimal getFPFTAP() {
    	return FPFTAP;
    }
	public void setFPFTAP(BigDecimal fPFTAP) {
    	FPFTAP = fPFTAP;
    }
	public BigDecimal getFPFTCHG() {
    	return FPFTCHG;
    }
	public void setFPFTCHG(BigDecimal fPFTCHG) {
    	FPFTCHG = fPFTCHG;
    }
	public BigDecimal getFPFTS() {
    	return FPFTS;
    }
	public void setFPFTS(BigDecimal fPFTS) {
    	FPFTS = fPFTS;
    }
	public BigDecimal getFPFTSB() {
    	return FPFTSB;
    }
	public void setFPFTSB(BigDecimal fPFTSB) {
    	FPFTSB = fPFTSB;
    }
	public BigDecimal getFPFTSP() {
    	return FPFTSP;
    }
	public void setFPFTSP(BigDecimal fPFTSP) {
    	FPFTSP = fPFTSP;
    }
	public BigDecimal getFPNLTY() {
    	return FPNLTY;
    }
	public void setFPNLTY(BigDecimal fPNLTY) {
    	FPNLTY = fPNLTY;
    }
	public BigDecimal getFPRI() {
    	return FPRI;
    }
	public void setFPRI(BigDecimal fPRI) {
    	FPRI = fPRI;
    }
	public BigDecimal getFPRIAB() {
    	return FPRIAB;
    }
	public void setFPRIAB(BigDecimal fPRIAB) {
    	FPRIAB = fPRIAB;
    }
	public BigDecimal getFPRIAP() {
    	return FPRIAP;
    }
	public void setFPRIAP(BigDecimal fPRIAP) {
    	FPRIAP = fPRIAP;
    }
	public BigDecimal getFPRIS() {
    	return FPRIS;
    }
	public void setFPRIS(BigDecimal fPRIS) {
    	FPRIS = fPRIS;
    }
	public BigDecimal getFPRISB() {
    	return FPRISB;
    }
	public void setFPRISB(BigDecimal fPRISB) {
    	FPRISB = fPRISB;
    }
	public BigDecimal getFPRISP() {
    	return FPRISP;
    }
	public void setFPRISP(BigDecimal fPRISP) {
    	FPRISP = fPRISP;
    }
	public BigDecimal getFREFUND() {
    	return FREFUND;
    }
	public void setFREFUND(BigDecimal fREFUND) {
    	FREFUND = fREFUND;
    }
	public BigDecimal getFRPMF() {
    	return FRPMF;
    }
	public void setFRPMF(BigDecimal fRPMF) {
    	FRPMF = fRPMF;
    }
	public BigDecimal getFRPPFT() {
    	return FRPPFT;
    }
	public void setFRPPFT(BigDecimal fRPPFT) {
    	FRPPFT = fRPPFT;
    }
	public BigDecimal getFRPPRI() {
    	return FRPPRI;
    }
	public void setFRPPRI(BigDecimal fRPPRI) {
    	FRPPRI = fRPPRI;
    }
	public BigDecimal getFWAIVER() {
    	return FWAIVER;
    }
	public void setFWAIVER(BigDecimal fWAIVER) {
    	FWAIVER = fWAIVER;
    }
	public BigDecimal getBACRUE() {
    	return BACRUE;
    }
	public void setBACRUE(BigDecimal bACRUE) {
    	BACRUE = bACRUE;
    }
	public BigDecimal getBDACRUE() {
    	return BDACRUE;
    }
	public void setBDACRUE(BigDecimal bDACRUE) {
    	BDACRUE = bDACRUE;
    }
	public BigDecimal getBNACRUE() {
    	return BNACRUE;
    }
	public void setBNACRUE(BigDecimal bNACRUE) {
    	BNACRUE = bNACRUE;
    }
	public BigDecimal getBNKINV() {
    	return BNKINV;
    }
	public void setBNKINV(BigDecimal bNKINV) {
    	BNKINV = bNKINV;
    }
	public BigDecimal getBPFT() {
    	return BPFT;
    }
	public void setBPFT(BigDecimal bPFT) {
    	BPFT = bPFT;
    }
	public BigDecimal getBPFTAB() {
    	return BPFTAB;
    }
	public void setBPFTAB(BigDecimal bPFTAB) {
    	BPFTAB = bPFTAB;
    }
	public BigDecimal getBPFTAP() {
    	return BPFTAP;
    }
	public void setBPFTAP(BigDecimal bPFTAP) {
    	BPFTAP = bPFTAP;
    }
	public BigDecimal getBPFTCHG() {
    	return BPFTCHG;
    }
	public void setBPFTCHG(BigDecimal bPFTCHG) {
    	BPFTCHG = bPFTCHG;
    }
	public BigDecimal getBPFTS() {
    	return BPFTS;
    }
	public void setBPFTS(BigDecimal bPFTS) {
    	BPFTS = bPFTS;
    }
	public BigDecimal getBPFTSB() {
    	return BPFTSB;
    }
	public void setBPFTSB(BigDecimal bPFTSB) {
    	BPFTSB = bPFTSB;
    }
	public BigDecimal getBPFTSP() {
    	return BPFTSP;
    }
	public void setBPFTSP(BigDecimal bPFTSP) {
    	BPFTSP = bPFTSP;
    }
	public BigDecimal getBPNLTY() {
    	return BPNLTY;
    }
	public void setBPNLTY(BigDecimal bPNLTY) {
    	BPNLTY = bPNLTY;
    }
	public BigDecimal getBPRI() {
    	return BPRI;
    }
	public void setBPRI(BigDecimal bPRI) {
    	BPRI = bPRI;
    }
	public BigDecimal getBPRIAB() {
    	return BPRIAB;
    }
	public void setBPRIAB(BigDecimal bPRIAB) {
    	BPRIAB = bPRIAB;
    }
	public BigDecimal getBPRIAP() {
    	return BPRIAP;
    }
	public void setBPRIAP(BigDecimal bPRIAP) {
    	BPRIAP = bPRIAP;
    }
	public BigDecimal getBPRIS() {
    	return BPRIS;
    }
	public void setBPRIS(BigDecimal bPRIS) {
    	BPRIS = bPRIS;
    }
	public BigDecimal getBPRISB() {
    	return BPRISB;
    }
	public void setBPRISB(BigDecimal bPRISB) {
    	BPRISB = bPRISB;
    }
	public BigDecimal getBPRISP() {
    	return BPRISP;
    }
	public void setBPRISP(BigDecimal bPRISP) {
    	BPRISP = bPRISP;
    }
	public BigDecimal getBREFUND() {
    	return BREFUND;
    }
	public void setBREFUND(BigDecimal bREFUND) {
    	BREFUND = bREFUND;
    }
	public BigDecimal getBRPPFT() {
    	return BRPPFT;
    }
	public void setBRPPFT(BigDecimal bRPPFT) {
    	BRPPFT = bRPPFT;
    }
	public BigDecimal getBRPPRI() {
    	return BRPPRI;
    }
	public void setBRPPRI(BigDecimal bRPPRI) {
    	BRPPRI = bRPPRI;
    }
	public BigDecimal getBWAIVER() {
    	return BWAIVER;
    }
	public void setBWAIVER(BigDecimal bWAIVER) {
    	BWAIVER = bWAIVER;
    }	
	public BigDecimal getODCPLShare() {
    	return ODCPLShare;
    }
	public void setODCPLShare(BigDecimal oDCPLShare) {
    	ODCPLShare = oDCPLShare;
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
	
	//Set values into Map
	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> feeMap = new HashMap<String, Object>();	
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				feeMap.put(this.getClass().getDeclaredFields()[i].getName(), this.getClass().getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}
		return feeMap;
	}
	
}
