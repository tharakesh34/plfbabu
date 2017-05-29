package com.pennant.backend.model.rulefactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AEEventRSP {
	private long				custID;
	private String				custCIF;
	private String				finReference;
	private String				cmtReference;
	private String				collateralRef;
	private String				finEvent;
	private Date				postDate;
	private Date				valueDate;
	private Date				schdDate;
	private boolean				newRecord		= false;
	private boolean				createNow		= false;
	private boolean				wif				= false;
	private boolean				commitment		= false;
	private boolean				alwCmtPostings	= false;
	private boolean				isEOD			= false;
	private boolean				postingSucess	= false;
	private String				errorMessage;
	private long				linkedTranId;

	private List<ReturnDataSet>	returnDataSet	= new ArrayList<ReturnDataSet>(1);

	public AEEventRSP() {

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCmtReference() {
		return cmtReference;
	}

	public void setCmtReference(String cmtReference) {
		this.cmtReference = cmtReference;
	}

	public String getCollateralRef() {
		return collateralRef;
	}

	public void setCollateralRef(String collateralRef) {
		this.collateralRef = collateralRef;
	}

	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public Date getSchdDate() {
		return schdDate;
	}

	public void setSchdDate(Date schdDate) {
		this.schdDate = schdDate;
	}

	public boolean isPostingSucess() {
		return postingSucess;
	}

	public void setPostingSucess(boolean postingSucess) {
		this.postingSucess = postingSucess;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public List<ReturnDataSet> getReturnDataSet() {
		return returnDataSet;
	}

	public void setReturnDataSet(List<ReturnDataSet> returnDataSet) {
		this.returnDataSet = returnDataSet;
	}

}
