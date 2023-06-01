package com.pennant.backend.model.letter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennant.pff.noc.model.ServiceBranch;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class LoanLetter implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private long finID;
	private String finReference;
	private String requestType;
	private Long feeID;
	private Date createdDate;
	private Date createdOn;
	private long createdBy;
	private int generated;
	private Date generatedDate;
	private Date generatedOn;
	private long generatedBy;
	private long approvedBy;
	private Timestamp approvedOn;
	private String custAcctHolderName;
	private String finBranch;
	private String product;
	private String modeofTransfer;
	private String courierAgencyName;
	private String status;
	private String emailID;
	private String fileName;
	private String letterLocation;
	private String remarks;
	private Long adviseID;
	private GenerateLetter befImage;
	private LoggedInUser userDetails;
	private FinanceDetail financeDetail;
	private long agreementTemplate;
	private Long emailTemplate;
	private Long emailNotificationID;

	private int sequence;
	private Date businessDate;
	private String letterName;
	private String letterDesc;
	private String letterType;
	private String letterMode;
	private int saveFormat;
	private byte[] content;

	private MailTemplate mailTemplate;
	private ServiceBranch serviceBranch;
	private EventProperties eventProperties;

	private long custID;
	private String custCif;
	private String custCoreBank;
	private String salutation;
	private String custShrtName;
	private String custFullName;
	private String custCareOf;
	private String custHouseBullingNo;
	private String custFlatNo;
	private String custStreet;
	private String custLandMark;
	private String custLocalty;
	private String custCity;
	private String custDistrict;
	private String custState;
	private String custPinCode;
	private String custPoBox;
	private String custCountry;
	private String custSubDistrict;

	private String csbCode;
	private String csbDescription;
	private String csbHouseNo;
	private String csbFlatNo;
	private String csbStreet;
	private String csbAddrL1;
	private String csbAddrL2;
	private String csbCity;
	private String csbState;
	private String csbCounty;
	private String csbPoBox;
	private String csbPinCode;
	private String csbTelePhone;
	private String csbFax;
	private String csbFolderPath;

	private String fbCode;
	private String fbDescription;
	private String fbHouseNo;
	private String fbFlatNo;
	private String fbStreet;
	private String fbAddrL1;
	private String fbAddrL2;
	private String fbCity;
	private String fbState;
	private String fbCounty;
	private String fbPoBox;
	private String fbPinCode;
	private String fbTelePhone;
	private String fbFax;

	private String finType;
	private String finTypeDesc;
	private String finStartDate;
	private String appDate;

	private boolean blocked;
	private String custCtgCode;
	private String custGenderCode;
	private String customerType;
	private int loanClosureAge;
	private int loanCancellationAge;
	private String closureType;
	private int sequenceNo;
	private String statusOfpreviousletters;
	private String closureDate;
	private String cancelDate;
	private String letterSeqNo;

	public LoanLetter() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public Long getFeeID() {
		return feeID;
	}

	public void setFeeID(Long feeID) {
		this.feeID = feeID;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public int getGenerated() {
		return generated;
	}

	public void setGenerated(int generated) {
		this.generated = generated;
	}

	public Date getGeneratedDate() {
		return generatedDate;
	}

	public void setGeneratedDate(Date generatedDate) {
		this.generatedDate = generatedDate;
	}

	public Date getGeneratedOn() {
		return generatedOn;
	}

	public void setGeneratedOn(Date generatedOn) {
		this.generatedOn = generatedOn;
	}

	public long getGeneratedBy() {
		return generatedBy;
	}

	public void setGeneratedBy(long generatedBy) {
		this.generatedBy = generatedBy;
	}

	public long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Timestamp getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn;
	}

	public String getCustAcctHolderName() {
		return custAcctHolderName;
	}

	public void setCustAcctHolderName(String custAcctHolderName) {
		this.custAcctHolderName = custAcctHolderName;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getModeofTransfer() {
		return modeofTransfer;
	}

	public void setModeofTransfer(String modeofTransfer) {
		this.modeofTransfer = modeofTransfer;
	}

	public String getCourierAgencyName() {
		return courierAgencyName;
	}

	public void setCourierAgencyName(String courierAgencyName) {
		this.courierAgencyName = courierAgencyName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLetterLocation() {
		return letterLocation;
	}

	public void setLetterLocation(String letterLocation) {
		this.letterLocation = letterLocation;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Long getAdviseID() {
		return adviseID;
	}

	public void setAdviseID(Long adviseID) {
		this.adviseID = adviseID;
	}

	public GenerateLetter getBefImage() {
		return befImage;
	}

	public void setBefImage(GenerateLetter befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public long getAgreementTemplate() {
		return agreementTemplate;
	}

	public void setAgreementTemplate(long agreementTemplate) {
		this.agreementTemplate = agreementTemplate;
	}

	public Long getEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(Long emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	public Long getEmailNotificationID() {
		return emailNotificationID;
	}

	public void setEmailNotificationID(Long emailNotificationID) {
		this.emailNotificationID = emailNotificationID;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public Date getBusinessDate() {
		return businessDate;
	}

	public void setBusinessDate(Date businessDate) {
		this.businessDate = businessDate;
	}

	public String getLetterName() {
		return letterName;
	}

	public void setLetterName(String letterName) {
		this.letterName = letterName;
	}

	public String getLetterDesc() {
		return letterDesc;
	}

	public void setLetterDesc(String letterDesc) {
		this.letterDesc = letterDesc;
	}

	public String getLetterType() {
		return letterType;
	}

	public void setLetterType(String letterType) {
		this.letterType = letterType;
	}

	public String getLetterMode() {
		return letterMode;
	}

	public void setLetterMode(String letterMode) {
		this.letterMode = letterMode;
	}

	public int getSaveFormat() {
		return saveFormat;
	}

	public void setSaveFormat(int saveFormat) {
		this.saveFormat = saveFormat;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public MailTemplate getMailTemplate() {
		return mailTemplate;
	}

	public void setMailTemplate(MailTemplate mailTemplate) {
		this.mailTemplate = mailTemplate;
	}

	public ServiceBranch getServiceBranch() {
		return serviceBranch;
	}

	public void setServiceBranch(ServiceBranch serviceBranch) {
		this.serviceBranch = serviceBranch;
	}

	public EventProperties getEventProperties() {
		return eventProperties;
	}

	public void setEventProperties(EventProperties eventProperties) {
		this.eventProperties = eventProperties;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public String getCustCoreBank() {
		return custCoreBank;
	}

	public void setCustCoreBank(String custCoreBank) {
		this.custCoreBank = custCoreBank;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getCustFullName() {
		return custFullName;
	}

	public void setCustFullName(String custFullName) {
		this.custFullName = custFullName;
	}

	public String getCustCareOf() {
		return custCareOf;
	}

	public void setCustCareOf(String custCareOf) {
		this.custCareOf = custCareOf;
	}

	public String getCustHouseBullingNo() {
		return custHouseBullingNo;
	}

	public void setCustHouseBullingNo(String custHouseBullingNo) {
		this.custHouseBullingNo = custHouseBullingNo;
	}

	public String getCustFlatNo() {
		return custFlatNo;
	}

	public void setCustFlatNo(String custFlatNo) {
		this.custFlatNo = custFlatNo;
	}

	public String getCustStreet() {
		return custStreet;
	}

	public void setCustStreet(String custStreet) {
		this.custStreet = custStreet;
	}

	public String getCustLandMark() {
		return custLandMark;
	}

	public void setCustLandMark(String custLandMark) {
		this.custLandMark = custLandMark;
	}

	public String getCustLocalty() {
		return custLocalty;
	}

	public void setCustLocalty(String custLocalty) {
		this.custLocalty = custLocalty;
	}

	public String getCustCity() {
		return custCity;
	}

	public void setCustCity(String custCity) {
		this.custCity = custCity;
	}

	public String getCustDistrict() {
		return custDistrict;
	}

	public void setCustDistrict(String custDistrict) {
		this.custDistrict = custDistrict;
	}

	public String getCustState() {
		return custState;
	}

	public void setCustState(String custState) {
		this.custState = custState;
	}

	public String getCustPinCode() {
		return custPinCode;
	}

	public void setCustPinCode(String custPinCode) {
		this.custPinCode = custPinCode;
	}

	public String getCustPoBox() {
		return custPoBox;
	}

	public void setCustPoBox(String custPoBox) {
		this.custPoBox = custPoBox;
	}

	public String getCustCountry() {
		return custCountry;
	}

	public void setCustCountry(String custCountry) {
		this.custCountry = custCountry;
	}

	public String getCustSubDistrict() {
		return custSubDistrict;
	}

	public void setCustSubDistrict(String custSubDistrict) {
		this.custSubDistrict = custSubDistrict;
	}

	public String getCsbCode() {
		return csbCode;
	}

	public void setCsbCode(String csbCode) {
		this.csbCode = csbCode;
	}

	public String getCsbDescription() {
		return csbDescription;
	}

	public void setCsbDescription(String csbDescription) {
		this.csbDescription = csbDescription;
	}

	public String getCsbHouseNo() {
		return csbHouseNo;
	}

	public void setCsbHouseNo(String csbHouseNo) {
		this.csbHouseNo = csbHouseNo;
	}

	public String getCsbFlatNo() {
		return csbFlatNo;
	}

	public void setCsbFlatNo(String csbFlatNo) {
		this.csbFlatNo = csbFlatNo;
	}

	public String getCsbStreet() {
		return csbStreet;
	}

	public void setCsbStreet(String csbStreet) {
		this.csbStreet = csbStreet;
	}

	public String getCsbAddrL1() {
		return csbAddrL1;
	}

	public void setCsbAddrL1(String csbAddrL1) {
		this.csbAddrL1 = csbAddrL1;
	}

	public String getCsbAddrL2() {
		return csbAddrL2;
	}

	public void setCsbAddrL2(String csbAddrL2) {
		this.csbAddrL2 = csbAddrL2;
	}

	public String getCsbCity() {
		return csbCity;
	}

	public void setCsbCity(String csbCity) {
		this.csbCity = csbCity;
	}

	public String getCsbState() {
		return csbState;
	}

	public void setCsbState(String csbState) {
		this.csbState = csbState;
	}

	public String getCsbCounty() {
		return csbCounty;
	}

	public void setCsbCounty(String csbCounty) {
		this.csbCounty = csbCounty;
	}

	public String getCsbPoBox() {
		return csbPoBox;
	}

	public void setCsbPoBox(String csbPoBox) {
		this.csbPoBox = csbPoBox;
	}

	public String getCsbPinCode() {
		return csbPinCode;
	}

	public void setCsbPinCode(String csbPinCode) {
		this.csbPinCode = csbPinCode;
	}

	public String getCsbTelePhone() {
		return csbTelePhone;
	}

	public void setCsbTelePhone(String csbTelePhone) {
		this.csbTelePhone = csbTelePhone;
	}

	public String getCsbFax() {
		return csbFax;
	}

	public void setCsbFax(String csbFax) {
		this.csbFax = csbFax;
	}

	public String getCsbFolderPath() {
		return csbFolderPath;
	}

	public void setCsbFolderPath(String csbFolderPath) {
		this.csbFolderPath = csbFolderPath;
	}

	public String getFbCode() {
		return fbCode;
	}

	public void setFbCode(String fbCode) {
		this.fbCode = fbCode;
	}

	public String getFbDescription() {
		return fbDescription;
	}

	public void setFbDescription(String fbDescription) {
		this.fbDescription = fbDescription;
	}

	public String getFbHouseNo() {
		return fbHouseNo;
	}

	public void setFbHouseNo(String fbHouseNo) {
		this.fbHouseNo = fbHouseNo;
	}

	public String getFbFlatNo() {
		return fbFlatNo;
	}

	public void setFbFlatNo(String fbFlatNo) {
		this.fbFlatNo = fbFlatNo;
	}

	public String getFbStreet() {
		return fbStreet;
	}

	public void setFbStreet(String fbStreet) {
		this.fbStreet = fbStreet;
	}

	public String getFbAddrL1() {
		return fbAddrL1;
	}

	public void setFbAddrL1(String fbAddrL1) {
		this.fbAddrL1 = fbAddrL1;
	}

	public String getFbAddrL2() {
		return fbAddrL2;
	}

	public void setFbAddrL2(String fbAddrL2) {
		this.fbAddrL2 = fbAddrL2;
	}

	public String getFbCity() {
		return fbCity;
	}

	public void setFbCity(String fbCity) {
		this.fbCity = fbCity;
	}

	public String getFbState() {
		return fbState;
	}

	public void setFbState(String fbState) {
		this.fbState = fbState;
	}

	public String getFbCounty() {
		return fbCounty;
	}

	public void setFbCounty(String fbCounty) {
		this.fbCounty = fbCounty;
	}

	public String getFbPoBox() {
		return fbPoBox;
	}

	public void setFbPoBox(String fbPoBox) {
		this.fbPoBox = fbPoBox;
	}

	public String getFbPinCode() {
		return fbPinCode;
	}

	public void setFbPinCode(String fbPinCode) {
		this.fbPinCode = fbPinCode;
	}

	public String getFbTelePhone() {
		return fbTelePhone;
	}

	public void setFbTelePhone(String fbTelePhone) {
		this.fbTelePhone = fbTelePhone;
	}

	public String getFbFax() {
		return fbFax;
	}

	public void setFbFax(String fbFax) {
		this.fbFax = fbFax;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(String finStartDate) {
		this.finStartDate = finStartDate;
	}

	public String getAppDate() {
		return appDate;
	}

	public void setAppDate(String appDate) {
		this.appDate = appDate;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public String getCustCtgCode() {
		return custCtgCode;
	}

	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}

	public String getCustGenderCode() {
		return custGenderCode;
	}

	public void setCustGenderCode(String custGenderCode) {
		this.custGenderCode = custGenderCode;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public int getLoanClosureAge() {
		return loanClosureAge;
	}

	public void setLoanClosureAge(int loanClosureAge) {
		this.loanClosureAge = loanClosureAge;
	}

	public int getLoanCancellationAge() {
		return loanCancellationAge;
	}

	public void setLoanCancellationAge(int loanCancellationAge) {
		this.loanCancellationAge = loanCancellationAge;
	}

	public String getClosureType() {
		return closureType;
	}

	public void setClosureType(String closureType) {
		this.closureType = closureType;
	}

	public int getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(int sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public String getStatusOfpreviousletters() {
		return statusOfpreviousletters;
	}

	public void setStatusOfpreviousletters(String statusOfpreviousletters) {
		this.statusOfpreviousletters = statusOfpreviousletters;
	}

	public String getClosureDate() {
		return closureDate;
	}

	public void setClosureDate(String closureDate) {
		this.closureDate = closureDate;
	}

	public String getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(String cancelDate) {
		this.cancelDate = cancelDate;
	}

	public String getLetterSeqNo() {
		return letterSeqNo;
	}

	public void setLetterSeqNo(String letterSeqNo) {
		this.letterSeqNo = letterSeqNo;
	}

	public Map<String, Object> getDeclaredFieldValues() {
		Map<String, Object> map = new HashMap<>();

		map.put("CustCif", this.custCif);
		map.put("CustCoreBank", this.custCoreBank);
		map.put("CustShrtName", this.custShrtName);
		map.put("FinType", this.finType);
		map.put("FinTypeDesc", this.finTypeDesc);
		map.put("FinBranch", this.finBranch);
		map.put("FinReference", this.finReference);
		map.put("FinStartDate", this.finStartDate);
		map.put("ClosureDate", this.closureDate);
		map.put("CancelDate", this.cancelDate);
		map.put("ClosureType", this.closureType);
		map.put("FileName", this.fileName);
		map.put("LetterName", this.letterName);
		map.put("LetterSeqNo", this.letterSeqNo);
		map.put("CSBCode", this.csbCode);
		map.put("CSBDescription", this.csbDescription);

		return map;
	}

}
