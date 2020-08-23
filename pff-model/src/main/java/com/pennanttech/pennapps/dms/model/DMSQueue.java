package com.pennanttech.pennapps.dms.model;

import java.io.Serializable;
import java.util.Date;

import com.pennanttech.model.dms.DMSModule;

public class DMSQueue implements Serializable {
	private static final long serialVersionUID = 8369115207507822418L;

	private long id = Long.MIN_VALUE;
	private long docManagerID;
	private Long custId;
	private String custCif;
	private String finReference;
	private String reference;
	private DMSModule module;
	private DMSModule subModule;
	private String docName;
	private String docCategory;
	private String docType;
	private String docExt;
	private String docUri;
	private Date createdOn;
	private long createdBy;
	private int processingFlag;
	private String errorCode;
	private String errorDesc;
	private long documentId;
	byte[] docImage;
	private int attemptNum;
	private String offerId;
	private String applicationNo;
	private String auxiloryFields1;
	private String auxiloryFields2;
	private String auxiloryFields3;
	private String auxiloryFields4;
	private String auxiloryFields5;
	/* -----Below Fields are Excluded----*/
	private String folderIndex;
	
	public DMSQueue() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDocManagerID() {
		return docManagerID;
	}

	public void setDocManagerID(long docManagerID) {
		this.docManagerID = docManagerID;
	}

	public Long getCustId() {
		return custId;
	}

	public void setCustId(Long custId) {
		this.custId = custId;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public DMSModule getModule() {
		return module;
	}

	public void setModule(DMSModule module) {
		this.module = module;
	}

	public DMSModule getSubModule() {
		return subModule;
	}

	public void setSubModule(DMSModule subModule) {
		this.subModule = subModule;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getDocCategory() {
		return docCategory;
	}

	public void setDocCategory(String docCategory) {
		this.docCategory = docCategory;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocExt() {
		return docExt;
	}

	public void setDocExt(String docExt) {
		this.docExt = docExt;
	}

	public String getDocUri() {
		return docUri;
	}

	public void setDocUri(String docUri) {
		this.docUri = docUri;
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

	public int getProcessingFlag() {
		return processingFlag;
	}

	public void setProcessingFlag(int processingFlag) {
		this.processingFlag = processingFlag;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}

	public byte[] getDocImage() {
		return docImage;
	}

	public void setDocImage(byte[] docImage) {
		this.docImage = docImage;
	}

	public int getAttemptNum() {
		return attemptNum;
	}

	public void setAttemptNum(int attemptNum) {
		this.attemptNum = attemptNum;
	}

	public String getOfferId() {
		return offerId;
	}

	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	public String getApplicationNo() {
		return applicationNo;
	}

	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}

	public String getAuxiloryFields1() {
		return auxiloryFields1;
	}

	public void setAuxiloryFields1(String auxiloryFields1) {
		this.auxiloryFields1 = auxiloryFields1;
	}

	public String getAuxiloryFields2() {
		return auxiloryFields2;
	}

	public void setAuxiloryFields2(String auxiloryFields2) {
		this.auxiloryFields2 = auxiloryFields2;
	}

	public String getAuxiloryFields3() {
		return auxiloryFields3;
	}

	public void setAuxiloryFields3(String auxiloryFields3) {
		this.auxiloryFields3 = auxiloryFields3;
	}

	public String getAuxiloryFields4() {
		return auxiloryFields4;
	}

	public void setAuxiloryFields4(String auxiloryFields4) {
		this.auxiloryFields4 = auxiloryFields4;
	}

	public String getAuxiloryFields5() {
		return auxiloryFields5;
	}

	public void setAuxiloryFields5(String auxiloryFields5) {
		this.auxiloryFields5 = auxiloryFields5;
	}

	public String getFolderIndex() {
		return folderIndex;
	}

	public void setFolderIndex(String folderIndex) {
		this.folderIndex = folderIndex;
	}

}
