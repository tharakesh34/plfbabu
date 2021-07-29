package com.pennant.backend.model.crm;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class CrmDetails extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -1472467289111692722L;

	private long custId = Long.MIN_VALUE;
	private String custCif;
	private String relationshipNumber;
	private String description;
	private String origin;
	private String fileName = "";
	private String contentType = "";
	private String fileData = "";
	private String finReference;
	private String finType;
	private String product;

	public CrmDetails() {
		super();
	}

	public long getId() {
		return custId;
	}

	public void setId(long id) {
		this.custId = id;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getRelationshipNumber() {
		return relationshipNumber;
	}

	public void setRelationshipNumber(String relationshipNumber) {
		this.relationshipNumber = relationshipNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileData() {
		return fileData;
	}

	public void setFileData(String fileData) {
		this.fileData = fileData;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}