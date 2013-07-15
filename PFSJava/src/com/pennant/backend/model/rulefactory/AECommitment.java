package com.pennant.backend.model.rulefactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;

public class AECommitment {

	//Amount Code Details
	private BigDecimal CMTAMT = new BigDecimal(0);
	private BigDecimal CHGAMT = new BigDecimal(0);
	private BigDecimal DISBURSE = new BigDecimal(0);
	private BigDecimal RPPRI = new BigDecimal(0);

	//Commitment Details
	private BigDecimal cmtAmount;
	private BigDecimal cmtAmountOther;
	private BigDecimal cmtUtilized;
	private BigDecimal cmtUtilizedOther;
	private boolean cmtMultiBrach;
	private boolean cmtRevolving;
	private boolean cmtShared;
	private boolean cmtUsedEarlier;

	// Customer Details
	private String custCIF;
	private String custCOB;
	private String custCtgCode;
	private String custIndustry;
	private String custNationality;
	private String custParentCountry;
	private String custResdCountry;
	private String custRiskCountry;
	private String custSector;
	private String custSegment;
	private String custSubSector;
	private String custSubSegment;
	private String custTypeCode;
	private boolean custIsStaff;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setCMTAMT(BigDecimal cMTAMT) {
	    CMTAMT = cMTAMT;
    }
	public BigDecimal getCMTAMT() {
	    return CMTAMT;
    }
	
	public BigDecimal getCHGAMT() {
    	return CHGAMT;
    }
	public void setCHGAMT(BigDecimal cHGAMT) {
    	CHGAMT = cHGAMT;
    }
	
	public void setDISBURSE(BigDecimal dISBURSE) {
	    DISBURSE = dISBURSE;
    }
	public BigDecimal getDISBURSE() {
	    return DISBURSE;
    }

	public void setRPPRI(BigDecimal rPPRI) {
	    RPPRI = rPPRI;
    }
	public BigDecimal getRPPRI() {
	    return RPPRI;
    }

	public BigDecimal getCmtAmount() {
		return cmtAmount;
	}
	public void setCmtAmount(BigDecimal cmtAmount) {
		this.cmtAmount = cmtAmount;
	}

	public BigDecimal getCmtAmountOther() {
		return cmtAmountOther;
	}
	public void setCmtAmountOther(BigDecimal cmtAmountOther) {
		this.cmtAmountOther = cmtAmountOther;
	}

	public BigDecimal getCmtUtilized() {
		return cmtUtilized;
	}
	public void setCmtUtilized(BigDecimal cmtUtilized) {
		this.cmtUtilized = cmtUtilized;
	}

	public BigDecimal getCmtUtilizedOther() {
		return cmtUtilizedOther;
	}
	public void setCmtUtilizedOther(BigDecimal cmtUtilizedOther) {
		this.cmtUtilizedOther = cmtUtilizedOther;
	}

	public boolean isCmtMultiBrach() {
		return cmtMultiBrach;
	}
	public void setCmtMultiBrach(boolean cmtMultiBrach) {
		this.cmtMultiBrach = cmtMultiBrach;
	}

	public boolean isCmtRevolving() {
		return cmtRevolving;
	}
	public void setCmtRevolving(boolean cmtRevolving) {
		this.cmtRevolving = cmtRevolving;
	}

	public boolean isCmtShared() {
		return cmtShared;
	}
	public void setCmtShared(boolean cmtShared) {
		this.cmtShared = cmtShared;
	}

	public boolean isCmtUsedEarlier() {
		return cmtUsedEarlier;
	}
	public void setCmtUsedEarlier(boolean cmtUsedEarlier) {
		this.cmtUsedEarlier = cmtUsedEarlier;
	}

	public boolean isCustIsStaff() {
		return custIsStaff;
	}
	public void setCustIsStaff(boolean custIsStaff) {
		this.custIsStaff = custIsStaff;
	}

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

	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		try {
			Field[] fields = this.getClass().getDeclaredFields();
			for (Field field : fields) {
				hashMap.put(field.getName(), field.get(this));
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return hashMap;
	}

}
