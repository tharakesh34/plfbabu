package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;
import java.util.HashMap;

public class AEAmountCodesRIA {

	private String finReference;
	private long contributorId;
	private String custCIF;
	private String accountId;
	private BigDecimal investment = BigDecimal.ZERO;;
	private BigDecimal mudaribPercent = BigDecimal.ZERO;
	
	private BigDecimal INVAMT = BigDecimal.ZERO;
	
	private BigDecimal IACRUE = BigDecimal.ZERO;
	private BigDecimal IACRUES = BigDecimal.ZERO;
	private BigDecimal IDACRUE = BigDecimal.ZERO;
	private BigDecimal INACRUE = BigDecimal.ZERO;
	private BigDecimal ILACRUE = BigDecimal.ZERO;
	private BigDecimal IPFT = BigDecimal.ZERO;
	private BigDecimal IPFTAB = BigDecimal.ZERO;
	private BigDecimal IPFTAP = BigDecimal.ZERO;
	private BigDecimal IPFTCHG = BigDecimal.ZERO;
	private BigDecimal IPFTS = BigDecimal.ZERO;
	private BigDecimal IPFTSB = BigDecimal.ZERO;
	private BigDecimal IPFTSP = BigDecimal.ZERO;
	private BigDecimal IPRI = BigDecimal.ZERO;
	private BigDecimal IPRIAB = BigDecimal.ZERO;
	private BigDecimal IPRIAP = BigDecimal.ZERO;
	private BigDecimal IPRIS = BigDecimal.ZERO;
	private BigDecimal IPRISB = BigDecimal.ZERO;
	private BigDecimal IPRISP = BigDecimal.ZERO;
	
	private BigDecimal IRPPFT = BigDecimal.ZERO;
	private BigDecimal IRPPRI = BigDecimal.ZERO;
	private BigDecimal IRPTOT = BigDecimal.ZERO;
			
	private BigDecimal IMFACR = BigDecimal.ZERO;
	private BigDecimal IMFCHG = BigDecimal.ZERO;
	private BigDecimal IMFDACR = BigDecimal.ZERO;
	private BigDecimal IMFNACR = BigDecimal.ZERO;
	private BigDecimal IMUDFEE = BigDecimal.ZERO;
	private BigDecimal IRPMF = BigDecimal.ZERO;
	
	private BigDecimal IMFACRS = BigDecimal.ZERO;
	private BigDecimal IMFLACR = BigDecimal.ZERO;
	
	private BigDecimal ICPZCHG = BigDecimal.ZERO;
	private BigDecimal ICPZTOT = BigDecimal.ZERO;
	private BigDecimal ICPZPRV = BigDecimal.ZERO;
	private BigDecimal ICPZCUR = BigDecimal.ZERO;
	private BigDecimal ICPZNXT = BigDecimal.ZERO;
	private BigDecimal IPFTINADV = BigDecimal.ZERO;
	
	private BigDecimal IPNLTY = BigDecimal.ZERO;
	private BigDecimal IREFUND = BigDecimal.ZERO;
	private BigDecimal IWAIVER = BigDecimal.ZERO;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }
	
	public long getContributorId() {
    	return contributorId;
    }
	public void setContributorId(long contributorId) {
    	this.contributorId = contributorId;
    }
	
	public void setCustCIF(String custCIF) {
	    this.custCIF = custCIF;
    }
	public String getCustCIF() {
	    return custCIF;
    }
	public BigDecimal getMudaribPercent() {
    	return mudaribPercent;
    }
	public void setMudaribPercent(BigDecimal mudaribPercent) {
    	this.mudaribPercent = mudaribPercent;
    }
	
	public void setInvestment(BigDecimal investment) {
	    this.investment = investment;
    }
	public BigDecimal getInvestment() {
	    return investment;
    }
	
	public BigDecimal getINVAMT() {
    	return INVAMT;
    }
	public void setINVAMT(BigDecimal iNVAMT) {
    	INVAMT = iNVAMT;
    }
	public BigDecimal getIACRUE() {
    	return IACRUE;
    }
	public void setIACRUE(BigDecimal iACRUE) {
    	IACRUE = iACRUE;
    }
	public BigDecimal getIACRUES() {
    	return IACRUES;
    }
	public void setIACRUES(BigDecimal iACRUES) {
    	IACRUES = iACRUES;
    }
	public BigDecimal getIDACRUE() {
    	return IDACRUE;
    }
	public void setIDACRUE(BigDecimal iDACRUE) {
    	IDACRUE = iDACRUE;
    }
	public BigDecimal getINACRUE() {
    	return INACRUE;
    }
	public void setINACRUE(BigDecimal iNACRUE) {
    	INACRUE = iNACRUE;
    }
	public BigDecimal getILACRUE() {
    	return ILACRUE;
    }
	public void setILACRUE(BigDecimal iLACRUE) {
    	ILACRUE = iLACRUE;
    }
	public BigDecimal getIPFT() {
    	return IPFT;
    }
	public void setIPFT(BigDecimal iPFT) {
    	IPFT = iPFT;
    }
	public BigDecimal getIPFTAB() {
    	return IPFTAB;
    }
	public void setIPFTAB(BigDecimal iPFTAB) {
    	IPFTAB = iPFTAB;
    }
	public BigDecimal getIPFTAP() {
    	return IPFTAP;
    }
	public void setIPFTAP(BigDecimal iPFTAP) {
    	IPFTAP = iPFTAP;
    }
	public BigDecimal getIPFTCHG() {
    	return IPFTCHG;
    }
	public void setIPFTCHG(BigDecimal iPFTCHG) {
    	IPFTCHG = iPFTCHG;
    }
	public BigDecimal getIPFTS() {
    	return IPFTS;
    }
	public void setIPFTS(BigDecimal iPFTS) {
    	IPFTS = iPFTS;
    }
	public BigDecimal getIPFTSB() {
    	return IPFTSB;
    }
	public void setIPFTSB(BigDecimal iPFTSB) {
    	IPFTSB = iPFTSB;
    }
	public BigDecimal getIPFTSP() {
    	return IPFTSP;
    }
	public void setIPFTSP(BigDecimal iPFTSP) {
    	IPFTSP = iPFTSP;
    }
	public BigDecimal getIPRI() {
    	return IPRI;
    }
	public void setIPRI(BigDecimal iPRI) {
    	IPRI = iPRI;
    }
	public BigDecimal getIPRIAB() {
    	return IPRIAB;
    }
	public void setIPRIAB(BigDecimal iPRIAB) {
    	IPRIAB = iPRIAB;
    }
	public BigDecimal getIPRIAP() {
    	return IPRIAP;
    }
	public void setIPRIAP(BigDecimal iPRIAP) {
    	IPRIAP = iPRIAP;
    }
	public BigDecimal getIPRIS() {
    	return IPRIS;
    }
	public void setIPRIS(BigDecimal iPRIS) {
    	IPRIS = iPRIS;
    }
	public BigDecimal getIPRISB() {
    	return IPRISB;
    }
	public void setIPRISB(BigDecimal iPRISB) {
    	IPRISB = iPRISB;
    }
	public BigDecimal getIPRISP() {
    	return IPRISP;
    }
	public void setIPRISP(BigDecimal iPRISP) {
    	IPRISP = iPRISP;
    }
	public BigDecimal getIRPPFT() {
    	return IRPPFT;
    }
	public void setIRPPFT(BigDecimal iRPPFT) {
    	IRPPFT = iRPPFT;
    }
	public BigDecimal getIRPPRI() {
    	return IRPPRI;
    }
	public void setIRPPRI(BigDecimal iRPPRI) {
    	IRPPRI = iRPPRI;
    }
	public BigDecimal getIRPTOT() {
    	return IRPTOT;
    }
	public void setIRPTOT(BigDecimal iRPTOT) {
    	IRPTOT = iRPTOT;
    }
	public BigDecimal getIMFACR() {
    	return IMFACR;
    }
	public void setIMFACR(BigDecimal iMFACR) {
    	IMFACR = iMFACR;
    }
	public BigDecimal getIMFCHG() {
    	return IMFCHG;
    }
	public void setIMFCHG(BigDecimal iMFCHG) {
    	IMFCHG = iMFCHG;
    }
	public BigDecimal getIMFDACR() {
    	return IMFDACR;
    }
	public void setIMFDACR(BigDecimal iMFDACR) {
    	IMFDACR = iMFDACR;
    }
	public BigDecimal getIMFNACR() {
    	return IMFNACR;
    }
	public void setIMFNACR(BigDecimal iMFNACR) {
    	IMFNACR = iMFNACR;
    }
	public BigDecimal getIMUDFEE() {
    	return IMUDFEE;
    }
	public void setIMUDFEE(BigDecimal iMUDFEE) {
    	IMUDFEE = iMUDFEE;
    }
	public BigDecimal getIRPMF() {
    	return IRPMF;
    }
	public void setIRPMF(BigDecimal iRPMF) {
    	IRPMF = iRPMF;
    }
	public BigDecimal getIMFACRS() {
    	return IMFACRS;
    }
	public void setIMFACRS(BigDecimal iMFACRS) {
    	IMFACRS = iMFACRS;
    }
	public BigDecimal getIMFLACR() {
    	return IMFLACR;
    }
	public void setIMFLACR(BigDecimal iMFLACR) {
    	IMFLACR = iMFLACR;
    }
	public BigDecimal getICPZCHG() {
    	return ICPZCHG;
    }
	public void setICPZCHG(BigDecimal iCPZCHG) {
    	ICPZCHG = iCPZCHG;
    }
	public BigDecimal getICPZTOT() {
    	return ICPZTOT;
    }
	public void setICPZTOT(BigDecimal iCPZTOT) {
    	ICPZTOT = iCPZTOT;
    }
	public BigDecimal getICPZPRV() {
    	return ICPZPRV;
    }
	public void setICPZPRV(BigDecimal iCPZPRV) {
    	ICPZPRV = iCPZPRV;
    }
	public BigDecimal getICPZCUR() {
    	return ICPZCUR;
    }
	public void setICPZCUR(BigDecimal iCPZCUR) {
    	ICPZCUR = iCPZCUR;
    }
	public BigDecimal getICPZNXT() {
    	return ICPZNXT;
    }
	public void setICPZNXT(BigDecimal iCPZNXT) {
    	ICPZNXT = iCPZNXT;
    }
	public BigDecimal getIPFTINADV() {
    	return IPFTINADV;
    }
	public void setIPFTINADV(BigDecimal iPFTINADV) {
    	IPFTINADV = iPFTINADV;
    }
	public BigDecimal getIPNLTY() {
    	return IPNLTY;
    }
	public void setIPNLTY(BigDecimal iPNLTY) {
    	IPNLTY = iPNLTY;
    }
	public BigDecimal getIREFUND() {
    	return IREFUND;
    }
	public void setIREFUND(BigDecimal iREFUND) {
    	IREFUND = iREFUND;
    }
	public BigDecimal getIWAIVER() {
    	return IWAIVER;
    }
	public void setIWAIVER(BigDecimal iWAIVER) {
    	IWAIVER = iWAIVER;
    }
	public void setAccountId(String accountId) {
	    this.accountId = accountId;
    }
	public String getAccountId() {
	    return accountId;
    }
	
	//Set values into Map
	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> feeMap = new HashMap<String, Object>();	
		try {
			for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
				feeMap.put(this.getClass().getDeclaredFields()[i].getName(), this.getClass().getDeclaredFields()[i].get(this));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return feeMap;
	}
	
}
