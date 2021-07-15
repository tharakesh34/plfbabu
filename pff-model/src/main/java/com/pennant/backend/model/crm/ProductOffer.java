package com.pennant.backend.model.crm;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class ProductOffer extends AbstractWorkflowEntity implements Entity, Serializable {
	private static final long serialVersionUID = 1L;

	private long leadId;
	private Long offerId = Long.MIN_VALUE;
	private String baseProduct;
	private String offerProduct;
	private String businessVertical;
	private String loanType;
	private String extCustSeg;
	private Date offerDate;
	private BigDecimal offerAmount;
	private String existingLAN;
	private String existingLANBranch;
	private String bTS;
	private String dataMartStatus;
	private String offerType;
	private String offerName;
	private String bT;
	private String ownerType;
	private String ownerId;
	private String processType;
	private String leadSource;
	private Date pOValidity;
	private String productOfferSource;
	private boolean offerConverted;
	private String holdReason;
	private String remarks;
	private BigDecimal sALLineUtilized;
	private String queueCode;// from POAssign_ULView
	private String userlist;// from POAssign_ULView
	private String propensity;
	private ProductOffer befImage;
	private String queueLevel;
	private String status;
	private String cibilScore;
	private String productOfferingType;
	private String productOfferingName;
	private String qualityRemarks;
	private String tags;
	private String policyType;
	private boolean dncFlag;
	private String sourceId;
	private String usrfname;
	private String usrmname;
	private String usrlname;
	private String assignedTo;
	private String processingType;
	private String queueDesc;

	// Lead Details
	private String leadType;
	private String leadMobNumber;
	private String leadUniqueID;
	private String hostLeadId;
	private String leadFName;
	private String leadLName;
	private String leadName;
	private String leadReference;

	/* Offer Disposition */
	private String dispositionType;
	private String dispositionStatus1;
	private String dispositionStatus2;
	private String dispositionStatus3;
	private String dispRemarks;
	private Date followUpDate;
	private String actionTat;
	private String mobPORef;
	private boolean followupreq;
	private String dispositionLvl1;
	private String lastAction;
	private Date lastActionDate;
	private boolean upload;

	/* Sourcing Details */
	private String processingBranch;
	private String sourcingBranch;
	private String processingBranchDesc;

	// Exclude fields
	private String lovDescBusiVert;
	private String lovDescExtCustSeg;
	private String users;
	private String loanReference;
	private boolean convert;

	// CIBIL watch Extended Fields
	private Date enquiryDateAndTime;
	private String enquiryProduct;
	private String enquiryCity;
	private BigDecimal enquiryLoanAmount;
	private String source;
	private String latestCIBILMobile;
	private String cibilNotes;
	private String noOfEnquires;
	private String bankCategory;
	private String cibilPORef;

	private String cibilScoreValue;
	private Date cibilInitiatedDate;

	/* Campaign Details */
	private String responseType;
	private Date responseDate;
	private String utmSource;

	/* Sec Users */
	private String ownername;

	private boolean newRecord;

	public ProductOffer() {
		super();
	}

	@Override
	public boolean isNew() {
		return isNewRecord();
	}

	@Override
	public long getId() {
		return offerId;
	}

	@Override
	public void setId(long id) {
		this.offerId = id;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public long getLeadId() {
		return leadId;
	}

	public void setLeadId(long leadId) {
		this.leadId = leadId;
	}

	public Long getOfferId() {
		return offerId;
	}

	public void setOfferId(Long offerId) {
		this.offerId = offerId;
	}

	public String getBaseProduct() {
		return baseProduct;
	}

	public void setBaseProduct(String baseProduct) {
		this.baseProduct = baseProduct;
	}

	public String getOfferProduct() {
		return offerProduct;
	}

	public void setOfferProduct(String offerProduct) {
		this.offerProduct = offerProduct;
	}

	public String getBusinessVertical() {
		return businessVertical;
	}

	public void setBusinessVertical(String businessVertical) {
		this.businessVertical = businessVertical;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public String getExtCustSeg() {
		return extCustSeg;
	}

	public void setExtCustSeg(String extCustSeg) {
		this.extCustSeg = extCustSeg;
	}

	public String getExistingLAN() {
		return existingLAN;
	}

	public void setExistingLAN(String existingLAN) {
		this.existingLAN = existingLAN;
	}

	public String getExistingLANBranch() {
		return existingLANBranch;
	}

	public void setExistingLANBranch(String existingLANBranch) {
		this.existingLANBranch = existingLANBranch;
	}

	public String getbTS() {
		return bTS;
	}

	public void setbTS(String bTS) {
		this.bTS = bTS;
	}

	public String getDataMartStatus() {
		return dataMartStatus;
	}

	public void setDataMartStatus(String dataMartStatus) {
		this.dataMartStatus = dataMartStatus;
	}

	public String getOfferType() {
		return offerType;
	}

	public void setOfferType(String offerType) {
		this.offerType = offerType;
	}

	public String getOfferName() {
		return offerName;
	}

	public void setOfferName(String offerName) {
		this.offerName = offerName;
	}

	public String getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public String getLeadSource() {
		return leadSource;
	}

	public void setLeadSource(String leadSource) {
		this.leadSource = leadSource;
	}

	public String getLeadType() {
		return leadType;
	}

	public void setLeadType(String leadType) {
		this.leadType = leadType;
	}

	public String getProductOfferSource() {
		return productOfferSource;
	}

	public void setProductOfferSource(String productOfferSource) {
		this.productOfferSource = productOfferSource;
	}

	public boolean isOfferConverted() {
		return offerConverted;
	}

	public void setOfferConverted(boolean offerConverted) {
		this.offerConverted = offerConverted;
	}

	public String getHoldReason() {
		return holdReason;
	}

	public void setHoldReason(String holdReason) {
		this.holdReason = holdReason;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getPropensity() {
		return propensity;
	}

	public void setPropensity(String propensity) {
		this.propensity = propensity;
	}

	public BigDecimal getOfferAmount() {
		return offerAmount;
	}

	public void setOfferAmount(BigDecimal offerAmount) {
		this.offerAmount = offerAmount;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getQueueCode() {
		return queueCode;
	}

	public ProductOffer getBefImage() {
		return befImage;
	}

	public void setBefImage(ProductOffer befImage) {
		this.befImage = befImage;
	}

	public void setQueueCode(String queueCode) {
		this.queueCode = queueCode;
	}

	public String getUserlist() {
		return userlist;
	}

	public void setUserlist(String userlist) {
		this.userlist = userlist;
	}

	public String getQueueLevel() {
		return queueLevel;
	}

	public void setQueueLevel(String queueLevel) {
		this.queueLevel = queueLevel;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLeadFName() {
		return leadFName;
	}

	public void setLeadFName(String leadFName) {
		this.leadFName = leadFName;
	}

	public String getLeadLName() {
		return leadLName;
	}

	public void setLeadLName(String leadLName) {
		this.leadLName = leadLName;
	}

	public String getCibilScore() {
		return cibilScore;
	}

	public void setCibilScore(String cibilScore) {
		this.cibilScore = cibilScore;
	}

	public String getProductOfferingType() {
		return productOfferingType;
	}

	public void setProductOfferingType(String productOfferingType) {
		this.productOfferingType = productOfferingType;
	}

	public String getProductOfferingName() {
		return productOfferingName;
	}

	public void setProductOfferingName(String productOfferingName) {
		this.productOfferingName = productOfferingName;
	}

	public String getQualityRemarks() {
		return qualityRemarks;
	}

	public void setQualityRemarks(String qualityRemarks) {
		this.qualityRemarks = qualityRemarks;
	}

	public boolean isDncFlag() {
		return dncFlag;
	}

	public void setDncFlag(boolean dncFlag) {
		this.dncFlag = dncFlag;
	}

	public Date getOfferDate() {
		return offerDate;
	}

	public void setOfferDate(Date offerDate) {
		this.offerDate = offerDate;
	}

	public Date getpOValidity() {
		return pOValidity;
	}

	public void setpOValidity(Date pOValidity) {
		this.pOValidity = pOValidity;
	}

	public BigDecimal getsALLineUtilized() {
		return sALLineUtilized;
	}

	public void setsALLineUtilized(BigDecimal sALLineUtilized) {
		this.sALLineUtilized = sALLineUtilized;
	}

	public String getDispositionType() {
		return dispositionType;
	}

	public void setDispositionType(String dispositionType) {
		this.dispositionType = dispositionType;
	}

	public String getDispositionStatus1() {
		return dispositionStatus1;
	}

	public void setDispositionStatus1(String dispositionStatus1) {
		this.dispositionStatus1 = dispositionStatus1;
	}

	public String getDispositionStatus2() {
		return dispositionStatus2;
	}

	public void setDispositionStatus2(String dispositionStatus2) {
		this.dispositionStatus2 = dispositionStatus2;
	}

	public String getDispositionStatus3() {
		return dispositionStatus3;
	}

	public void setDispositionStatus3(String dispositionStatus3) {
		this.dispositionStatus3 = dispositionStatus3;
	}

	public String getDispRemarks() {
		return dispRemarks;
	}

	public void setDispRemarks(String dispRemarks) {
		this.dispRemarks = dispRemarks;
	}

	public Date getFollowUpDate() {
		return followUpDate;
	}

	public void setFollowUpDate(Date followUpDate) {
		this.followUpDate = followUpDate;
	}

	public String getActionTat() {
		return actionTat;
	}

	public void setActionTat(String actionTat) {
		this.actionTat = actionTat;
	}

	public String getMobPORef() {
		return mobPORef;
	}

	public void setMobPORef(String mobPORef) {
		this.mobPORef = mobPORef;
	}

	public boolean isFollowupreq() {
		return followupreq;
	}

	public void setFollowupreq(boolean followupreq) {
		this.followupreq = followupreq;
	}

	public String getLovDescBusiVert() {
		return lovDescBusiVert;
	}

	public void setLovDescBusiVert(String lovDescBusiVert) {
		this.lovDescBusiVert = lovDescBusiVert;
	}

	public String getLovDescExtCustSeg() {
		return lovDescExtCustSeg;
	}

	public void setLovDescExtCustSeg(String lovDescExtCustSeg) {
		this.lovDescExtCustSeg = lovDescExtCustSeg;
	}

	public String getLeadMobNumber() {
		return leadMobNumber;
	}

	public void setLeadMobNumber(String leadMobNumber) {
		this.leadMobNumber = leadMobNumber;
	}

	public String getLeadUniqueID() {
		return leadUniqueID;
	}

	public void setLeadUniqueID(String leadUniqueID) {
		this.leadUniqueID = leadUniqueID;
	}

	public String getHostLeadId() {
		return hostLeadId;
	}

	public void setHostLeadId(String hostLeadId) {
		this.hostLeadId = hostLeadId;
	}

	public String getDispositionLvl1() {
		return dispositionLvl1;
	}

	public void setDispositionLvl1(String dispositionLvl1) {
		this.dispositionLvl1 = dispositionLvl1;
	}

	public String getLastAction() {
		return lastAction;
	}

	public void setLastAction(String lastAction) {
		this.lastAction = lastAction;
	}

	public Date getLastActionDate() {
		return lastActionDate;
	}

	public void setLastActionDate(Date lastActionDate) {
		this.lastActionDate = lastActionDate;
	}

	public String getProcessingBranch() {
		return processingBranch;
	}

	public void setProcessingBranch(String processingBranch) {
		this.processingBranch = processingBranch;
	}

	public String getSourcingBranch() {
		return sourcingBranch;
	}

	public void setSourcingBranch(String sourcingBranch) {
		this.sourcingBranch = sourcingBranch;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getPolicyType() {
		return policyType;
	}

	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}

	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}

	public String getLeadName() {
		return leadName;
	}

	public void setLeadName(String leadName) {
		this.leadName = leadName;
	}

	public Date getEnquiryDateAndTime() {
		return enquiryDateAndTime;
	}

	public void setEnquiryDateAndTime(Date enquiryDateAndTime) {
		this.enquiryDateAndTime = enquiryDateAndTime;
	}

	public String getEnquiryProduct() {
		return enquiryProduct;
	}

	public void setEnquiryProduct(String enquiryProduct) {
		this.enquiryProduct = enquiryProduct;
	}

	public String getEnquiryCity() {
		return enquiryCity;
	}

	public void setEnquiryCity(String enquiryCity) {
		this.enquiryCity = enquiryCity;
	}

	public BigDecimal getEnquiryLoanAmount() {
		return enquiryLoanAmount;
	}

	public void setEnquiryLoanAmount(BigDecimal enquiryLoanAmount) {
		this.enquiryLoanAmount = enquiryLoanAmount;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getLatestCIBILMobile() {
		return latestCIBILMobile;
	}

	public void setLatestCIBILMobile(String latestCIBILMobile) {
		this.latestCIBILMobile = latestCIBILMobile;
	}

	public String getCibilNotes() {
		return cibilNotes;
	}

	public void setCibilNotes(String cibilNotes) {
		this.cibilNotes = cibilNotes;
	}

	public String getNoOfEnquires() {
		return noOfEnquires;
	}

	public void setNoOfEnquires(String noOfEnquires) {
		this.noOfEnquires = noOfEnquires;
	}

	public String getBankCategory() {
		return bankCategory;
	}

	public void setBankCategory(String bankCategory) {
		this.bankCategory = bankCategory;
	}

	public String getCibilPORef() {
		return cibilPORef;
	}

	public void setCibilPORef(String cibilPORef) {
		this.cibilPORef = cibilPORef;
	}

	public boolean isUpload() {
		return upload;
	}

	public void setUpload(boolean upload) {
		this.upload = upload;
	}

	public String getCibilScoreValue() {
		return cibilScoreValue;
	}

	public void setCibilScoreValue(String cibilScoreValue) {
		this.cibilScoreValue = cibilScoreValue;
	}

	public Date getCibilInitiatedDate() {
		return cibilInitiatedDate;
	}

	public void setCibilInitiatedDate(Date cibilInitiatedDate) {
		this.cibilInitiatedDate = cibilInitiatedDate;
	}

	public boolean isConvert() {
		return convert;
	}

	public void setConvert(boolean convert) {
		this.convert = convert;
	}

	public String getLoanReference() {
		return loanReference;
	}

	public void setLoanReference(String loanReference) {
		this.loanReference = loanReference;
	}

	public String getbT() {
		return bT;
	}

	public void setbT(String bT) {
		this.bT = bT;
	}

	public String getUsrfname() {
		return usrfname;
	}

	public void setUsrfname(String usrfname) {
		this.usrfname = usrfname;
	}

	public String getUsrmname() {
		return usrmname;
	}

	public void setUsrmname(String usrmname) {
		this.usrmname = usrmname;
	}

	public String getUsrlname() {
		return usrlname;
	}

	public void setUsrlname(String usrlname) {
		this.usrlname = usrlname;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	public Date getResponseDate() {
		return responseDate;
	}

	public void setResponseDate(Date responseDate) {
		this.responseDate = responseDate;
	}

	public String getUtmSource() {
		return utmSource;
	}

	public void setUtmSource(String utmSource) {
		this.utmSource = utmSource;
	}

	public String getOwnername() {
		return ownername;
	}

	public void setOwnername(String ownername) {
		this.ownername = ownername;
	}

	public String getLeadReference() {
		return leadReference;
	}

	public void setLeadReference(String leadReference) {
		this.leadReference = leadReference;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getProcessingType() {
		return processingType;
	}

	public void setProcessingType(String processingType) {
		this.processingType = processingType;
	}

	public String getProcessingBranchDesc() {
		return processingBranchDesc;
	}

	public void setProcessingBranchDesc(String processingBranchDesc) {
		this.processingBranchDesc = processingBranchDesc;
	}

	public String getQueueDesc() {
		return queueDesc;
	}

	public void setQueueDesc(String queueDesc) {
		this.queueDesc = queueDesc;
	}

}
