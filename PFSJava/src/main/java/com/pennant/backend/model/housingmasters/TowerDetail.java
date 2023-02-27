package com.pennant.backend.model.housingmasters;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>TowerDetail table</b>.<br>
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class TowerDetail extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;

	private String name;
	private Long projectId;
	private String projectIdName;
	private Long developerId;
	private String developerIdName;
	private String city;
	private String cityName;
	private String structureAsApproved;
	private String apf;
	private String apfName;
	private String apfType;
	private String apfTypeName;
	private String apfNumber;

	@JsonProperty("TowerId")
	private String peTowerId;
	private String sancFloors;

	@JsonProperty("ActualSiteStructure")
	private String actSiteStructure;

	private int numberOfUnits;

	private Date complDate;

	private Date revComplDate;

	@JsonProperty("ConstructionStagePercentage")
	private double constnPercent;
	private Date regDate;

	@JsonProperty("TotalUnits")
	private int totalUnits;
	private boolean constnFinance;
	private BigDecimal perate = BigDecimal.ZERO;

	@JsonProperty("SoldUnits")
	private int soldUnits;
	private BigDecimal launchPrice = BigDecimal.ZERO;

	@JsonProperty("DelayMonths")
	private int delayInMonths;

	private String priceBasedon;

	@JsonProperty("ResalePriceMin")
	private BigDecimal resalePriceMin = BigDecimal.ZERO;

	@JsonProperty("ResalePriceMax")
	private BigDecimal resalePriceMax = BigDecimal.ZERO;
	private BigDecimal currPrimPriceMin = BigDecimal.ZERO;
	private BigDecimal currPrimPriceMax = BigDecimal.ZERO;
	private String apfProjOtherBank;
	private String rERARegNumber;
	private Date updateDate;
	private String projAffluence;
	private BigDecimal wtMicroMarkRate = BigDecimal.ZERO;
	private String cosntFinAvailed;
	private String cosntFinAvailedName;
	private String conFinInstName;

	@JsonProperty("TowerAbsorptionRate")
	private BigDecimal towerLvlAbsRate = BigDecimal.ZERO;
	private String remarks;
	private String bankAccNo;
	private String branchIfscCode;
	private String branchIfscCodeBranchName;
	private String bankAccFavouring;
	private String bank;
	private String bankName;
	private String aprvngAuthority;
	private int aprvdNumber;
	private double plotArea;
	private boolean aprvdPlanAvailable;
	private Date aprvdPlanValidUntil;
	private double excavation;
	private double basement;
	private double podiums;
	private double stiltFloor;
	private double slabs;
	private double internalWorks;
	private double sanitaryElectirical;
	private double liftsStaircases;
	private double externalWorks;
	private double fireFightingFitting;
	private String peProjectId;

	private boolean newRecord = false;
	private String lovValue;
	private TowerDetail befImage;
	private LoggedInUser userDetails;

	@JsonProperty("CompletionDate")
	private String complDateAPI;

	@JsonProperty("RevisedCompletionDate")
	private String revComplDateAPI;

	@JsonProperty("UpdateDate")
	private String updateDateAPI;

	@JsonProperty("PricePerSqft")
	private String pricePerSqft;

	private BigDecimal sqftRateMin = BigDecimal.ZERO;
	private BigDecimal sqftRateMax = BigDecimal.ZERO;
	private BigDecimal unitSizeRangeMin = BigDecimal.ZERO;
	private BigDecimal unitSizeRangeMax = BigDecimal.ZERO;

	private Integer minAccNoLength;
	private Integer maxAccNoLength;
	private boolean active;
	private Date visitDate;
	@JsonProperty("LastVisitDate")
	private String visitDateAPI;

	@Override
	public boolean isNew() {
		return isNewRecord();
	}

	public TowerDetail() {
		super();
	}

	public TowerDetail(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("projectIdName");
		excludeFields.add("developerIdName");
		excludeFields.add("cityName");
		excludeFields.add("apfName");
		excludeFields.add("apfTypeName");
		excludeFields.add("cosntFinAvailedName");
		excludeFields.add("bankName");
		excludeFields.add("branchIfscCodeBranchName");

		excludeFields.add("updateDateAPI");
		excludeFields.add("revComplDateAPI");
		excludeFields.add("complDateAPI");

		excludeFields.add("pricePerSqft");
		excludeFields.add("unitSizeRange");
		excludeFields.add("minAccNoLength");
		excludeFields.add("maxAccNoLength");
		excludeFields.add("visitDateAPI");
		return excludeFields;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectIdName() {
		return this.projectIdName;
	}

	public void setProjectIdName(String projectIdName) {
		this.projectIdName = projectIdName;
	}

	public Long getDeveloperId() {
		return developerId;
	}

	public void setDeveloperId(Long developerId) {
		this.developerId = developerId;
	}

	public String getDeveloperIdName() {
		return this.developerIdName;
	}

	public void setDeveloperIdName(String developerIdName) {
		this.developerIdName = developerIdName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCityName() {
		return this.cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getStructureAsApproved() {
		return structureAsApproved;
	}

	public void setStructureAsApproved(String structureAsApproved) {
		this.structureAsApproved = structureAsApproved;
	}

	public String getApf() {
		return apf;
	}

	public void setApf(String apf) {
		this.apf = apf;
	}

	public String getApfName() {
		return this.apfName;
	}

	public void setApfName(String apfName) {
		this.apfName = apfName;
	}

	public String getApfType() {
		return apfType;
	}

	public void setApfType(String apfType) {
		this.apfType = apfType;
	}

	public String getApfTypeName() {
		return this.apfTypeName;
	}

	public void setApfTypeName(String apfTypeName) {
		this.apfTypeName = apfTypeName;
	}

	public String getApfNumber() {
		return apfNumber;
	}

	public void setApfNumber(String apfNumber) {
		this.apfNumber = apfNumber;
	}

	public String getPeTowerId() {
		return peTowerId;
	}

	public void setPeTowerId(String peTowerId) {
		this.peTowerId = peTowerId;
	}

	public String getSancFloors() {
		return sancFloors;
	}

	public void setSancFloors(String sancFloors) {
		this.sancFloors = sancFloors;
	}

	public String getActSiteStructure() {
		return actSiteStructure;
	}

	public void setActSiteStructure(String actSiteStructure) {
		this.actSiteStructure = actSiteStructure;
	}

	public int getNumberOfUnits() {
		return numberOfUnits;
	}

	public void setNumberOfUnits(int numberOfUnits) {
		this.numberOfUnits = numberOfUnits;
	}

	public Date getComplDate() {
		return complDate;
	}

	public void setComplDate(Date complDate) {
		this.complDate = complDate;
	}

	public Date getRevComplDate() {
		return revComplDate;
	}

	public void setRevComplDate(Date revComplDate) {
		this.revComplDate = revComplDate;
	}

	public double getConstnPercent() {
		return constnPercent;
	}

	public void setConstnPercent(double constnPercent) {
		this.constnPercent = constnPercent;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	public int getTotalUnits() {
		return totalUnits;
	}

	public void setTotalUnits(int totalUnits) {
		this.totalUnits = totalUnits;
	}

	public boolean isConstnFinance() {
		return constnFinance;
	}

	public void setConstnFinance(boolean constnFinance) {
		this.constnFinance = constnFinance;
	}

	public BigDecimal getPerate() {
		return perate;
	}

	public void setPerate(BigDecimal perate) {
		this.perate = perate;
	}

	public int getSoldUnits() {
		return soldUnits;
	}

	public void setSoldUnits(int soldUnits) {
		this.soldUnits = soldUnits;
	}

	public BigDecimal getLaunchPrice() {
		return launchPrice;
	}

	public void setLaunchPrice(BigDecimal launchPrice) {
		this.launchPrice = launchPrice;
	}

	public int getDelayInMonths() {
		return delayInMonths;
	}

	public void setDelayInMonths(int delayInMonths) {
		this.delayInMonths = delayInMonths;
	}

	public String getPriceBasedon() {
		return priceBasedon;
	}

	public void setPriceBasedon(String priceBasedon) {
		this.priceBasedon = priceBasedon;
	}

	public BigDecimal getResalePriceMin() {
		return resalePriceMin;
	}

	public void setResalePriceMin(BigDecimal resalePriceMin) {
		this.resalePriceMin = resalePriceMin;
	}

	public BigDecimal getResalePriceMax() {
		return resalePriceMax;
	}

	public void setResalePriceMax(BigDecimal resalePriceMax) {
		this.resalePriceMax = resalePriceMax;
	}

	public BigDecimal getCurrPrimPriceMin() {
		return currPrimPriceMin;
	}

	public void setCurrPrimPriceMin(BigDecimal currPrimPriceMin) {
		this.currPrimPriceMin = currPrimPriceMin;
	}

	public BigDecimal getCurrPrimPriceMax() {
		return currPrimPriceMax;
	}

	public void setCurrPrimPriceMax(BigDecimal currPrimPriceMax) {
		this.currPrimPriceMax = currPrimPriceMax;
	}

	public String getApfProjOtherBank() {
		return apfProjOtherBank;
	}

	public void setApfProjOtherBank(String apfProjOtherBank) {
		this.apfProjOtherBank = apfProjOtherBank;
	}

	public String getRERARegNumber() {
		return rERARegNumber;
	}

	public void setRERARegNumber(String rERARegNumber) {
		this.rERARegNumber = rERARegNumber;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getProjAffluence() {
		return projAffluence;
	}

	public void setProjAffluence(String projAffluence) {
		this.projAffluence = projAffluence;
	}

	public BigDecimal getWtMicroMarkRate() {
		return wtMicroMarkRate;
	}

	public void setWtMicroMarkRate(BigDecimal wtMicroMarkRate) {
		this.wtMicroMarkRate = wtMicroMarkRate;
	}

	public String getCosntFinAvailed() {
		return cosntFinAvailed;
	}

	public void setCosntFinAvailed(String cosntFinAvailed) {
		this.cosntFinAvailed = cosntFinAvailed;
	}

	public String getCosntFinAvailedName() {
		return this.cosntFinAvailedName;
	}

	public void setCosntFinAvailedName(String cosntFinAvailedName) {
		this.cosntFinAvailedName = cosntFinAvailedName;
	}

	public String getConFinInstName() {
		return conFinInstName;
	}

	public void setConFinInstName(String conFinInstName) {
		this.conFinInstName = conFinInstName;
	}

	public BigDecimal getTowerLvlAbsRate() {
		return towerLvlAbsRate;
	}

	public void setTowerLvlAbsRate(BigDecimal towerLvlAbsRate) {
		this.towerLvlAbsRate = towerLvlAbsRate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getBankAccNo() {
		return bankAccNo;
	}

	public void setBankAccNo(String bankAccNo) {
		this.bankAccNo = bankAccNo;
	}

	public String getBranchIfscCode() {
		return branchIfscCode;
	}

	public void setBranchIfscCode(String branchIfscCode) {
		this.branchIfscCode = branchIfscCode;
	}

	public String getBankAccFavouring() {
		return bankAccFavouring;
	}

	public void setBankAccFavouring(String bankAccFavouring) {
		this.bankAccFavouring = bankAccFavouring;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getAprvngAuthority() {
		return aprvngAuthority;
	}

	public void setAprvngAuthority(String aprvngAuthority) {
		this.aprvngAuthority = aprvngAuthority;
	}

	public int getAprvdNumber() {
		return aprvdNumber;
	}

	public void setAprvdNumber(int aprvdNumber) {
		this.aprvdNumber = aprvdNumber;
	}

	public double getPlotArea() {
		return plotArea;
	}

	public void setPlotArea(double plotArea) {
		this.plotArea = plotArea;
	}

	public boolean isAprvdPlanAvailable() {
		return aprvdPlanAvailable;
	}

	public void setAprvdPlanAvailable(boolean aprvdPlanAvailable) {
		this.aprvdPlanAvailable = aprvdPlanAvailable;
	}

	public Date getAprvdPlanValidUntil() {
		return aprvdPlanValidUntil;
	}

	public void setAprvdPlanValidUntil(Date aprvdPlanValidUntil) {
		this.aprvdPlanValidUntil = aprvdPlanValidUntil;
	}

	public double getExcavation() {
		return excavation;
	}

	public void setExcavation(double excavation) {
		this.excavation = excavation;
	}

	public double getBasement() {
		return basement;
	}

	public void setBasement(double basement) {
		this.basement = basement;
	}

	public double getPodiums() {
		return podiums;
	}

	public void setPodiums(double podiums) {
		this.podiums = podiums;
	}

	public double getStiltFloor() {
		return stiltFloor;
	}

	public void setStiltFloor(double stiltFloor) {
		this.stiltFloor = stiltFloor;
	}

	public double getSlabs() {
		return slabs;
	}

	public void setSlabs(double slabs) {
		this.slabs = slabs;
	}

	public double getInternalWorks() {
		return internalWorks;
	}

	public void setInternalWorks(double internalWorks) {
		this.internalWorks = internalWorks;
	}

	public double getSanitaryElectirical() {
		return sanitaryElectirical;
	}

	public void setSanitaryElectirical(double sanitaryElectirical) {
		this.sanitaryElectirical = sanitaryElectirical;
	}

	public double getLiftsStaircases() {
		return liftsStaircases;
	}

	public void setLiftsStaircases(double liftsStaircases) {
		this.liftsStaircases = liftsStaircases;
	}

	public double getExternalWorks() {
		return externalWorks;
	}

	public void setExternalWorks(double externalWorks) {
		this.externalWorks = externalWorks;
	}

	public double getFireFightingFitting() {
		return fireFightingFitting;
	}

	public void setFireFightingFitting(double fireFightingFitting) {
		this.fireFightingFitting = fireFightingFitting;
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

	public TowerDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(TowerDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public String getBranchIfscCodeBranchName() {
		return branchIfscCodeBranchName;
	}

	public void setBranchIfscCodeBranchName(String branchIfscCodeBranchName) {
		this.branchIfscCodeBranchName = branchIfscCodeBranchName;
	}

	public String getPeProjectId() {
		return peProjectId;
	}

	public void setPeProjectId(String peProjectId) {
		this.peProjectId = peProjectId;
	}

	public String getPricePerSqft() {
		return pricePerSqft;
	}

	public void setPricePerSqft(String pricePerSqft) {
		this.pricePerSqft = pricePerSqft;
	}

	public String getComplDateAPI() {
		return complDateAPI;
	}

	public void setComplDateAPI(String complDateAPI) {
		this.complDateAPI = complDateAPI;
	}

	public String getRevComplDateAPI() {
		return revComplDateAPI;
	}

	public void setRevComplDateAPI(String revComplDateAPI) {
		this.revComplDateAPI = revComplDateAPI;
	}

	public String getUpdateDateAPI() {
		return updateDateAPI;
	}

	public void setUpdateDateAPI(String updateDateAPI) {
		this.updateDateAPI = updateDateAPI;
	}

	public BigDecimal getSqftRateMin() {
		return sqftRateMin;
	}

	public void setSqftRateMin(BigDecimal sqftRateMin) {
		this.sqftRateMin = sqftRateMin;
	}

	public BigDecimal getSqftRateMax() {
		return sqftRateMax;
	}

	public void setSqftRateMax(BigDecimal sqftRateMax) {
		this.sqftRateMax = sqftRateMax;
	}

	public BigDecimal getUnitSizeRangeMin() {
		return unitSizeRangeMin;
	}

	public void setUnitSizeRangeMin(BigDecimal unitSizeRangeMin) {
		this.unitSizeRangeMin = unitSizeRangeMin;
	}

	public BigDecimal getUnitSizeRangeMax() {
		return unitSizeRangeMax;
	}

	public void setUnitSizeRangeMax(BigDecimal unitSizeRangeMax) {
		this.unitSizeRangeMax = unitSizeRangeMax;
	}

	public Integer getMinAccNoLength() {
		return minAccNoLength;
	}

	public void setMinAccNoLength(Integer minAccNoLength) {
		this.minAccNoLength = minAccNoLength;
	}

	public Integer getMaxAccNoLength() {
		return maxAccNoLength;
	}

	public void setMaxAccNoLength(Integer maxAccNoLength) {
		this.maxAccNoLength = maxAccNoLength;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}

	public String getVisitDateAPI() {
		return visitDateAPI;
	}

	public void setVisitDateAPI(String visitDateAPI) {
		this.visitDateAPI = visitDateAPI;
	}

}
