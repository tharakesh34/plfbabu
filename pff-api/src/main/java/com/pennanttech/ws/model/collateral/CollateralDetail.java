package com.pennanttech.ws.model.collateral;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.collateral.CollateralSetup;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "cif", "collateralSetup", "returnStatus" })
@XmlAccessorType(XmlAccessType.FIELD)
public class CollateralDetail implements Serializable {
	private static final long serialVersionUID = -7149894537732020044L;

	private String cif;

	@JsonProperty("collateral")
	private List<CollateralSetup> collateralSetup;
	private WSReturnStatus returnStatus;

	public CollateralDetail() {

	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public List<CollateralSetup> getCollateralSetup() {
		return collateralSetup;
	}

	public void setCollateralSetup(List<CollateralSetup> collateralSetup) {
		this.collateralSetup = collateralSetup;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}
}
