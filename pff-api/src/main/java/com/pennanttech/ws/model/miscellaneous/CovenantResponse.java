package com.pennanttech.ws.model.miscellaneous;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.covenant.CovenantType;

@XmlType(propOrder = { "covenants", "covenantType", "returnStatus" })
@XmlAccessorType(XmlAccessType.FIELD)
public class CovenantResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private WSReturnStatus returnStatus;
	private List<CovenantType> covenantType = new ArrayList<>();
	private List<Covenant> covenants = new ArrayList<>();

	public CovenantResponse() {
		super();
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public List<CovenantType> getCovenantType() {
		return covenantType;
	}

	public void setCovenantType(List<CovenantType> covenantType) {
		this.covenantType = covenantType;
	}

	public List<Covenant> getCovenants() {
		return covenants;
	}

	public void setCovenants(List<Covenant> covenants) {
		this.covenants = covenants;
	}

}
