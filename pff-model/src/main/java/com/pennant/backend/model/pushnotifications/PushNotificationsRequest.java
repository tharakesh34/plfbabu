package com.pennant.backend.model.pushnotifications;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "referenceId", "docCategory", "custDocTitle", "custDocIssuedCountry", "custDocSysName",
		"custDocIssuedOn", "custDocExpDate", "docPurpose", "docName", "doctype", "docImage", "docUri" })
@XmlRootElement(name = "DocumentDetail")
@XmlAccessorType(XmlAccessType.NONE)
public class PushNotificationsRequest {
	private static final long serialVersionUID = -5569765259024813213L;
	private String finReference;
	private String roleCode;
	private String recordStatus;
	private String nextRoleCode;
	private String product;

	public PushNotificationsRequest() {
		super();
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getNextRoleCode() {
		return nextRoleCode;
	}

	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
