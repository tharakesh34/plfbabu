package com.pennant.coreinterface.model.customer;

import java.io.Serializable;
import java.util.Date;

public class InterfaceCustomerIdentity implements Serializable {

	private static final long serialVersionUID = -5822763684521083089L;
	
	private long idCustID;
	private String idType;
	private String idIssuedBy;
	private String idRef;
	private String idIssueCountry;
	private Date idIssuedOn;
	private Date idExpiresOn;
	private String idLocation;
	private String recordType;

	public InterfaceCustomerIdentity() {
		
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getIdCustID() {
		return idCustID;
	}

	public void setIdCustID(long idCustID) {
		this.idCustID = idCustID;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getIdIssuedBy() {
		return idIssuedBy;
	}

	public void setIdIssuedBy(String idIssuedBy) {
		this.idIssuedBy = idIssuedBy;
	}

	public String getIdRef() {
		return idRef;
	}

	public void setIdRef(String idRef) {
		this.idRef = idRef;
	}

	public String getIdIssueCountry() {
		return idIssueCountry;
	}

	public void setIdIssueCountry(String idIssueCountry) {
		this.idIssueCountry = idIssueCountry;
	}

	public Date getIdIssuedOn() {
		return idIssuedOn;
	}

	public void setIdIssuedOn(Date idIssuedOn) {
		this.idIssuedOn = idIssuedOn;
	}

	public Date getIdExpiresOn() {
		return idExpiresOn;
	}

	public void setIdExpiresOn(Date idExpiresOn) {
		this.idExpiresOn = idExpiresOn;
	}

	public String getIdLocation() {
		return idLocation;
	}

	public void setIdLocation(String idLocation) {
		this.idLocation = idLocation;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

}
