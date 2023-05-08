package com.pennant.backend.model.reports;

import java.util.List;

import com.pennant.backend.model.finance.FinanceMain;

public class LoanEnquiry implements java.io.Serializable {

	private static final long serialVersionUID = -3651607703173829894L;
	private String finReference = null;
	private long custID;
	private String lovDescCustCIF;
	private String finType;
	private String finBranch;
	private String lovDescFinBranchName;
	private String lovDescFinTypeName;
	private List<FinanceMain> financeMainList;

	public LoanEnquiry() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}

	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getLovDescFinTypeName() {
		return lovDescFinTypeName;
	}

	public void setLovDescFinTypeName(String lovDescFinTypeName) {
		this.lovDescFinTypeName = lovDescFinTypeName;
	}

	public void setFinanceMainList(List<FinanceMain> financeMainList) {
		this.financeMainList = financeMainList;
	}

	public List<FinanceMain> getFinanceMainList() {
		return financeMainList;
	}

	public void setLovDescFinBranchName(String lovDescFinBranchName) {
		this.lovDescFinBranchName = lovDescFinBranchName;
	}

	public String getLovDescFinBranchName() {
		return lovDescFinBranchName;
	}

}
