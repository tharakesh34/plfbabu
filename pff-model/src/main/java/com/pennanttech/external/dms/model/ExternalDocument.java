package com.pennanttech.external.dms.model;

import java.io.Serializable;

public class ExternalDocument implements Serializable {

	private static final long serialVersionUID = 4512492988043429848L;

	private String applicationNumber;
	private String leadId;
	private String finReference;
	private String docName;
	private String documentType;
	private String custCIF;
	private String vaultStorage;
	private String customerType;
	private String categoryOfDocument;
	private String remarks1;
	private String remarks2;
	private String remarks3;
	private String remarks4;
	private String remarks5;
	private String remarks6;
	private String docRefId;
	private byte[] docImage;
	private String creationDate;
	private String revisedDate;
	private String docModule;
	private String folderIndex;
	private String docExtn;
	private String applicantType;
	private String loggedInUser;
	private String branch;
	private String docSource;
	private String imageIndex;
	private String custId;

	public ExternalDocument() {
		// TODO Auto-generated constructor stub
	}

	public String getApplicationNumber() {
		return applicationNumber;
	}

	public void setApplicationNumber(String applicationNumber) {
		this.applicationNumber = applicationNumber;
	}

	public String getLeadId() {
		return leadId;
	}

	public void setLeadId(String leadId) {
		this.leadId = leadId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getVaultStorage() {
		return vaultStorage;
	}

	public void setVaultStorage(String vaultStorage) {
		this.vaultStorage = vaultStorage;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getCategoryOfDocument() {
		return categoryOfDocument;
	}

	public void setCategoryOfDocument(String categoryOfDocument) {
		this.categoryOfDocument = categoryOfDocument;
	}

	public String getRemarks1() {
		return remarks1;
	}

	public void setRemarks1(String remarks1) {
		this.remarks1 = remarks1;
	}

	public String getRemarks2() {
		return remarks2;
	}

	public void setRemarks2(String remarks2) {
		this.remarks2 = remarks2;
	}

	public String getRemarks3() {
		return remarks3;
	}

	public void setRemarks3(String remarks3) {
		this.remarks3 = remarks3;
	}

	public String getRemarks4() {
		return remarks4;
	}

	public void setRemarks4(String remarks4) {
		this.remarks4 = remarks4;
	}

	public String getRemarks5() {
		return remarks5;
	}

	public void setRemarks5(String remarks5) {
		this.remarks5 = remarks5;
	}

	public String getRemarks6() {
		return remarks6;
	}

	public void setRemarks6(String remarks6) {
		this.remarks6 = remarks6;
	}

	public String getDocRefId() {
		return docRefId;
	}

	public void setDocRefId(String docRefId) {
		this.docRefId = docRefId;
	}

	public byte[] getDocImage() {
		return docImage;
	}

	public void setDocImage(byte[] docImage) {
		this.docImage = docImage;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getRevisedDate() {
		return revisedDate;
	}

	public void setRevisedDate(String revisedDate) {
		this.revisedDate = revisedDate;
	}

	public String getDocModule() {
		return docModule;
	}

	public void setDocModule(String docModule) {
		this.docModule = docModule;
	}

	public String getFolderIndex() {
		return folderIndex;
	}

	public void setFolderIndex(String folderIndex) {
		this.folderIndex = folderIndex;
	}

	public String getDocExtn() {
		return docExtn;
	}

	public void setDocExtn(String docExtn) {
		this.docExtn = docExtn;
	}

	public String getApplicantType() {
		return applicantType;
	}

	public void setApplicantType(String applicantType) {
		this.applicantType = applicantType;
	}

	public String getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(String loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getDocSource() {
		return docSource;
	}

	public void setDocSource(String docSource) {
		this.docSource = docSource;
	}

	public String getImageIndex() {
		return imageIndex;
	}

	public void setImageIndex(String imageIndex) {
		this.imageIndex = imageIndex;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

}
