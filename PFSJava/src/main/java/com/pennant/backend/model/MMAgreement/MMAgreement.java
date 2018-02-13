package com.pennant.backend.model.MMAgreement;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class MMAgreement extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;
	
	private long mMAId = Long.MIN_VALUE;
	private String  	custCIF = "";
	private String  	custShrtName = "";
	private String  	MMAReference;
	private Date  		ContractDate;
	private BigDecimal 	contractAmt = BigDecimal.ZERO;
	private BigDecimal 	rate = BigDecimal.ZERO;
	private String 		purchRegOffice ="";
	private String 		lovDescPurchRegOffice ="";
	private String  	purchaddress ="";
	private String  	attention ="";
	private String  	fax ="";
	private String  	titleNo ="";
	private String  	product;
	private String  	agreeName;
	private String  	mMAgreeType;
	private int 		lovDescFinFormetter;
	
	
	private Date 		fOLIssueDate ;
	private Date 		maturityDate;
	private BigDecimal 	facilityLimit;
	private BigDecimal  profitRate = BigDecimal.ZERO;
	private String 		baseRateCode;
	private BigDecimal	minRate;
	private BigDecimal 	margin;
	private int 		profitPeriod;
	private BigDecimal  minAmount;
	private int         numberOfTerms;
	private BigDecimal  latePayRate;
	
	private String 		fOlReference;
	private int 		avlPerDays;
	private BigDecimal  maxCapProfitRate;
	private BigDecimal  minCapRate ;
	private Date 		facOfferLetterDate;
	private String 		pmaryRelOfficer = "";
	private String 		custAccount ="";
	private String  	lovDescBaseRateName;
	private long 		dealer;
	private String 		dealerName;
	private String 		dealerAddr;
	private String 		dealerCountry;
	private String 		dealerCity;
	private String		custCountry;
	private String 		custProvince;
	private String 		custCity;
	private String 		lovDescCustAddrCityName;
	private String 		lovDescCustAddrProvinceName;
	private String 		lovDescCustAddrCountryName;
	private String 		custShrtNameLclLng;
	
	private String 		assetDesc;
	private BigDecimal 	assetValue = BigDecimal.ZERO;
	private BigDecimal 	sharePerc = BigDecimal.ZERO;
	
	private String 		custPOBox;
	
	

	
	private String      commodityCode = null;
	private String      commodityName;
	private String      commodityUnitCode;
	private String      commodityUnitName;
	
	private boolean newRecord=false;
	private MMAgreement befImage;
	private LoggedInUser userDetails;

	public MMAgreement() {
		super();
	}
	public boolean isNew() {
		return isNewRecord();
	}

	public MMAgreement(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custShrtName");
		excludeFields.add("lovDescpurchRegOffice");
		excludeFields.add("commodityCode");
		excludeFields.add("commodityName");
		excludeFields.add("commodityUnitCode");
		excludeFields.add("commodityUnitName");
		excludeFields.add("custCountry");
		excludeFields.add("custCity");
		excludeFields.add("custPOBox");
		excludeFields.add("custProvince");
		excludeFields.add("dealerAddr");
		excludeFields.add("dealerCountry");
		excludeFields.add("dealerAddr");
		excludeFields.add("dealerCity");
		excludeFields.add("dealerName");
		excludeFields.add("lovDescCustAddrCityName");
		excludeFields.add("lovDescCustAddrProvinceName");
		excludeFields.add("lovDescCustAddrCountryName");
		excludeFields.add("custShrtNameLclLng");
		return excludeFields;
	}

	//Getter and Setter methods
	public long getId() {
		return mMAId;
	}
	
	public void setId(long id) {
		this.mMAId = id;
	}
	public long getMMAId() {
		return mMAId;
	}
	public void setMMAId(long mMAId) {
		this.mMAId = mMAId;
	}
	
	public String getCustCIF() {
		return custCIF;
	}
	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}
	public Date getContractDate() {
		return ContractDate;
	}
	public void setContractDate(Date contractDate) {
		ContractDate = contractDate;
	}
	public BigDecimal getContractAmt() {
		return contractAmt;
	}
	public void setContractAmt(BigDecimal contractAmt) {
		this.contractAmt = contractAmt;
	}
	public BigDecimal getRate() {
		return rate;
	}
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	public String getPurchRegOffice() {
		return purchRegOffice;
	}
	public void setPurchRegOffice(String purchRegOffice) {
		this.purchRegOffice = purchRegOffice;
	}
	public String getPurchaddress() {
		return purchaddress;
	}
	public void setPurchaddress(String purchaddress) {
		this.purchaddress = purchaddress;
	}
	public String getAttention() {
		return attention;
	}
	public void setAttention(String attention) {
		this.attention = attention;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}


	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getTitleNo() {
		return titleNo;
	}
	public void setTitleNo(String titleNo) {
		this.titleNo = titleNo;
	}
	public int getLovDescFinFormatter() {
	    // TODO Auto-generated method stub
	    return 0;
    }
	public MMAgreement getBefImage() {
	    return befImage;
    }
	public void setBefImage(MMAgreement befImage) {
	    this.befImage = befImage;
    }
	public String getCustShrtName() {
	    return custShrtName;
    }
	public void setCustShrtName(String custShrtName) {
	    this.custShrtName = custShrtName;
    }
	public String getLovDescPurchRegOffice() {
	    return lovDescPurchRegOffice;
    }
	public void setLovDescPurchRegOffice(String lovDescPurchRegOffice) {
	    this.lovDescPurchRegOffice = lovDescPurchRegOffice;
    }
	public String getmMAgreeType() {
	    return mMAgreeType;
    }
	public void setmMAgreeType(String mMAgreeType) {
	    this.mMAgreeType = mMAgreeType;
    }
	public String getProduct() {
	    return product;
    }
	public void setProduct(String product) {
	    this.product = product;
    }
	public String getAgreeName() {
	    return agreeName;
    }
	public void setAgreeName(String agreeName) {
	    this.agreeName = agreeName;
    }
	

	
	public Date getfOLIssueDate() {
		return fOLIssueDate;
	}
	public void setfOLIssueDate(Date fOLIssueDate) {
		this.fOLIssueDate = fOLIssueDate;
	}
	public Date getMaturityDate() {
		return maturityDate;
	}
	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}
	public BigDecimal getFacilityLimit() {
		return facilityLimit;
	}
	public void setFacilityLimit(BigDecimal facilityLimit) {
		this.facilityLimit = facilityLimit;
	}
	public BigDecimal getProfitRate() {
		return profitRate;
	}
	public void setProfitRate(BigDecimal profitRate) {
		this.profitRate = profitRate;
	}
	public String getBaseRateCode() {
		return baseRateCode;
	}
	public void setBaseRateCode(String baseRateCode) {
		this.baseRateCode = baseRateCode;
	}
	public BigDecimal getMinRate() {
		return minRate;
	}
	public void setMinRate(BigDecimal minRate) {
		this.minRate = minRate;
	}
	public BigDecimal getMargin() {
		return margin;
	}
	public void setMargin(BigDecimal margin) {
		this.margin = margin;
	}
	public int getProfitPeriod() {
		return profitPeriod;
	}
	public void setProfitPeriod(int profitPeriod) {
		this.profitPeriod = profitPeriod;
	}
	public BigDecimal getMinAmount() {
		return minAmount;
	}
	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}
	public int getNumberOfTerms() {
		return numberOfTerms;
	}
	public void setNumberOfTerms(int numberOfTerms) {
		this.numberOfTerms = numberOfTerms;
	}
	public String getCommodityCode() {
		return commodityCode;
	}
	public void setCommodityCode(String commodityCode) {
		this.commodityCode = commodityCode;
	}
	public String getCommodityName() {
		return commodityName;
	}
	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}
	public String getCommodityUnitCode() {
		return commodityUnitCode;
	}
	public void setCommodityUnitCode(String commodityUnitCode) {
		this.commodityUnitCode = commodityUnitCode;
	}
	public String getCommodityUnitName() {
		return commodityUnitName;
	}
	public void setCommodityUnitName(String commodityUnitName) {
		this.commodityUnitName = commodityUnitName;
	}
	public int getLovDescFinFormetter() {
	    return lovDescFinFormetter;
    }
	public void setLovDescFinFormetter(int lovDescFinFormetter) {
	    this.lovDescFinFormetter = lovDescFinFormetter;
    }
	public BigDecimal getLatePayRate() {
	    return latePayRate;
    }
	public void setLatePayRate(BigDecimal latePayRate) {
	    this.latePayRate = latePayRate;
    }
	
	public int getAvlPerDays() {
		return avlPerDays;
	}
	public void setAvlPerDays(int avlPerDays) {
		this.avlPerDays = avlPerDays;
	}
	
	public BigDecimal getMaxCapProfitRate() {
		return maxCapProfitRate;
	}
	public void setMaxCapProfitRate(BigDecimal maxCapProfitRate) {
		this.maxCapProfitRate = maxCapProfitRate;
	}
	
	public BigDecimal getMinCapRate() {
		return minCapRate;
	}
	public void setMinCapRate(BigDecimal minCapRate) {
		this.minCapRate = minCapRate;
	}
	
	public Date getFacOfferLetterDate() {
		return facOfferLetterDate;
	}
	public void setFacOfferLetterDate(Date facOfferLetterDate) {
		this.facOfferLetterDate = facOfferLetterDate;
	}
	
	
	
	public String getPmaryRelOfficer() {
		return pmaryRelOfficer;
	}
	public void setPmaryRelOfficer(String pmaryRelOfficer) {
		this.pmaryRelOfficer = pmaryRelOfficer;
	}
	
	public String getCustAccount() {
		return custAccount;
	}
	public void setCustAccount(String custAccount) {
		this.custAccount = custAccount;
	}
	
	
	public String getLovDescBaseRateName() {
	    return lovDescBaseRateName;
    }
	public void setLovDescBaseRateName(String lovDescBaseRateName) {
	    this.lovDescBaseRateName = lovDescBaseRateName;
    }
	public String getfOlReference() {
	    return fOlReference;
    }
	public void setfOlReference(String fOlReference) {
	    this.fOlReference = fOlReference;
    }
	public String getMMAReference() {
	    return MMAReference;
    }
	public void setMMAReference(String mMAReference) {
	    MMAReference = mMAReference;
    }
	
	public long getDealer() {
	    return dealer;
    }
	public void setDealer(long dealer) {
	    this.dealer = dealer;
    }
	
	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	
	public String getDealerAddr() {
		return dealerAddr;
	}
	public void setDealerAddr(String dealerAddr) {
		this.dealerAddr = dealerAddr;
	}
	
	public String getAssetDesc() {
		return assetDesc;
	}
	public void setAssetDesc(String assetDesc) {
		this.assetDesc = assetDesc;
	}
	
	public BigDecimal getAssetValue() {
		return assetValue;
	}
	public void setAssetValue(BigDecimal assetValue) {
		this.assetValue = assetValue;
	}
	
	public BigDecimal getSharePerc() {
		return sharePerc;
	}
	public void setSharePerc(BigDecimal sharePerc) {
		this.sharePerc = sharePerc;
	}
	
	public String getCustCountry() {
	    return custCountry;
    }
	public void setCustCountry(String custCountry) {
	    this.custCountry = custCountry;
    }
	public String getCustProvince() {
	    return custProvince;
    }
	public void setCustProvince(String custProvince) {
	    this.custProvince = custProvince;
    }
	public String getCustCity() {
	    return custCity;
    }
	public void setCustCity(String custCity) {
	    this.custCity = custCity;
    }
	public String getDealerCountry() {
	    return dealerCountry;
    }
	public void setDealerCountry(String dealerCountry) {
	    this.dealerCountry = dealerCountry;
    }
	public String getCustPOBox() {
	    return custPOBox;
    }
	public void setCustPOBox(String custPOBox) {
	    this.custPOBox = custPOBox;
    }
	public String getDealerCity() {
	    return dealerCity;
    }
	public void setDealerCity(String dealerCity) {
	    this.dealerCity = dealerCity;
    }
	public String getLovDescCustAddrCityName() {
		return lovDescCustAddrCityName;
	}
	public void setLovDescCustAddrCityName(String lovDescCustAddrCityName) {
		this.lovDescCustAddrCityName = lovDescCustAddrCityName;
	}
	public String getLovDescCustAddrProvinceName() {
		return lovDescCustAddrProvinceName;
	}
	public void setLovDescCustAddrProvinceName(String lovDescCustAddrProvinceName) {
		this.lovDescCustAddrProvinceName = lovDescCustAddrProvinceName;
	}
	public String getLovDescCustAddrCountryName() {
		return lovDescCustAddrCountryName;
	}
	public void setLovDescCustAddrCountryName(String lovDescCustAddrCountryName) {
		this.lovDescCustAddrCountryName = lovDescCustAddrCountryName;
	}
	public String getCustShrtNameLclLng() {
		return custShrtNameLclLng;
	}
	public void setCustShrtNameLclLng(String custShrtNameLclLng) {
		this.custShrtNameLclLng = custShrtNameLclLng;
	}

}
