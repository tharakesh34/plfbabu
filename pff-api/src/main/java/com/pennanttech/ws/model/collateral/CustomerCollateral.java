package com.pennanttech.ws.model.collateral;

import java.io.Serializable;

public class CustomerCollateral implements Serializable {
	private static final long serialVersionUID = 3778786327248596731L;

	private String cif;
	private String collateralRef;

	public CustomerCollateral() {
	    super();
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public String getCollateralRef() {
		return collateralRef;
	}

	public void setCollateralRef(String collateralRef) {
		this.collateralRef = collateralRef;
	}
}
