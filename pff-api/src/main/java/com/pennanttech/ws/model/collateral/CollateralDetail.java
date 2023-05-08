package com.pennanttech.ws.model.collateral;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.collateral.CollateralSetup;

@XmlType(propOrder = { "cif", "collateralSetup", "returnStatus" })
@XmlAccessorType(XmlAccessType.FIELD)
public class CollateralDetail implements Serializable {
	private static final long serialVersionUID = -7149894537732020044L;

	private String cif;

	@XmlElement(name = "collateral")
	private List<CollateralSetup> collateralSetup;
	private WSReturnStatus returnStatus;

	public CollateralDetail() {
	    super();
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
