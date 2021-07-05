package com.pennant.backend.model.agreement;

import java.util.Date;
import java.util.List;

public class CovenantAggrement {

	private Date appDate;
	private String custshrtname = "";
	private String finReference = "";
	private String custAddrHnbr = "";
	private String custAddrStreet = "";
	private String custAddrCountry = "";
	private String custAddrCity = "";
	private String custFlatNbr;
	private String custPOBox;
	private String custAddrProvince;
	private String documentName;
	private String documentReceivedDate;
	private String docCategory;
	private String receivableDate;
	private String documentType;
	private int originalDocument;
	private String description;
	private String keyReference;
	private List<CovenantAggrement> covenantAggrementList;

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCustAddrHnbr() {
		return custAddrHnbr;
	}

	public void setCustAddrHnbr(String custAddrHnbr) {
		this.custAddrHnbr = custAddrHnbr;
	}

	public String getCustAddrStreet() {
		return custAddrStreet;
	}

	public String getDocumentReceivedDate() {
		return documentReceivedDate;
	}

	public void setDocumentReceivedDate(String documentReceivedDate) {
		this.documentReceivedDate = documentReceivedDate;
	}

	public String getDocCategory() {
		return docCategory;
	}

	public void setDocCategory(String docCategory) {
		this.docCategory = docCategory;
	}

	public String getReceivableDate() {
		return receivableDate;
	}

	public void setReceivableDate(String receivableDate) {
		this.receivableDate = receivableDate;
	}

	public void setCustAddrStreet(String custAddrStreet) {
		this.custAddrStreet = custAddrStreet;
	}

	public String getCustAddrCountry() {
		return custAddrCountry;
	}

	public void setCustAddrCountry(String custAddrCountry) {
		this.custAddrCountry = custAddrCountry;
	}

	public String getCustAddrCity() {
		return custAddrCity;
	}

	public void setCustAddrCity(String custAddrCity) {
		this.custAddrCity = custAddrCity;
	}

	public String getCustFlatNbr() {
		return custFlatNbr;
	}

	public void setCustFlatNbr(String custFlatNbr) {
		this.custFlatNbr = custFlatNbr;
	}

	public String getCustPOBox() {
		return custPOBox;
	}

	public void setCustPOBox(String custPOBox) {
		this.custPOBox = custPOBox;
	}

	public String getCustAddrProvince() {
		return custAddrProvince;
	}

	public void setCustAddrProvince(String custAddrProvince) {
		this.custAddrProvince = custAddrProvince;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public int getOriginalDocument() {
		return originalDocument;
	}

	public void setOriginalDocument(int originalDocument) {
		this.originalDocument = originalDocument;
	}

	public String getCustshrtname() {
		return custshrtname;
	}

	public void setCustshrtname(String custshrtname) {
		this.custshrtname = custshrtname;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getKeyReference() {
		return keyReference;
	}

	public void setKeyReference(String keyReference) {
		this.keyReference = keyReference;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<CovenantAggrement> getCovenantAggrementList() {
		return covenantAggrementList;
	}

	public void setCovenantAggrementList(List<CovenantAggrement> covenantAggrementList) {
		this.covenantAggrementList = covenantAggrementList;
	}

}
