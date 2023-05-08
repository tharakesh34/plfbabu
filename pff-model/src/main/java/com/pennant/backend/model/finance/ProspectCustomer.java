package com.pennant.backend.model.finance;

import java.io.Serializable;

public class ProspectCustomer implements Serializable {

	private static final long serialVersionUID = 5631543033048266883L;

	private long custId = Long.MIN_VALUE;
	private String custCIF;
	private String custShrtName;
	private String custCtgCode;
	private String finReference;
	private String custDftBranch;

	public ProspectCustomer() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinReference() {
		return finReference;
	}

	public String getCustCtgCode() {
		return custCtgCode;
	}

	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}

	public void setCustDftBranch(String custDftBranch) {
		this.custDftBranch = custDftBranch;
	}

	public String getCustDftBranch() {
		return custDftBranch;
	}

}
