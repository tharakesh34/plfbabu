package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import com.pennant.backend.model.WSReturnStatus;

@XmlAccessorType(XmlAccessType.FIELD)
public class ForeClosureResponse implements Serializable {
	private static final long serialVersionUID = 1051334309884378798L;

	private String finReference;
	private ForeClosureLetter foreClosure;
	private List<FinFeeDetail> foreClosureFees;
	private List<FinFeeDetail> feeDues;
	private WSReturnStatus returnStatus;

	public ForeClosureResponse() {
		super();
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public ForeClosureLetter getForeClosure() {
		return foreClosure;
	}

	public void setForeClosure(ForeClosureLetter foreClosure) {
		this.foreClosure = foreClosure;
	}

	public List<FinFeeDetail> getFeeDues() {
		return feeDues;
	}

	public void setFeeDues(List<FinFeeDetail> feeDues) {
		this.feeDues = feeDues;
	}

	public List<FinFeeDetail> getForeClosureFees() {
		return foreClosureFees;
	}

	public void setForeClosureFees(List<FinFeeDetail> foreClosureFees) {
		this.foreClosureFees = foreClosureFees;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
