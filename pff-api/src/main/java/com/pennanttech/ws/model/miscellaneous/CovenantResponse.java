package com.pennanttech.ws.model.miscellaneous;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.covenant.CovenantType;

@XmlAccessorType(XmlAccessType.FIELD)
public class CovenantResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private WSReturnStatus returnStatus;

	private List<CovenantType> covenantType = new ArrayList<>();

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public List<CovenantType> geCovenantTypes() {
		return covenantType;
	}

	public void setCovenantDocuments(List<CovenantType> covenantType) {
		this.covenantType = covenantType;
	}

}
