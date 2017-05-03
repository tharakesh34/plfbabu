package com.pennant.backend.model.rulefactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.rmtmasters.TransactionEntry;

public class AEEvent {

	private AEAmountCodes			aeAmountCodes;
	private HashMap<String, Object>	executingMap;
	private List<TransactionEntry>	transactionEntries	= new ArrayList<TransactionEntry>(1);
	private Map<String, FeeRule>	feeChargeMap;
	private List<ReturnDataSet>		returnDataSet		= new ArrayList<ReturnDataSet>(1);

	private String					finReference;
	private String					finType;
	private String					promotion;
	private String					finEvent;
	private Date					postDate;
	private Date					valueDate;
	private Date					schdDate;
	private String					branch;
	private String					ccy;
	private long					custID;
	private String					custCIF;
	private boolean					newRecord			= false;
	private String					moduleDefiner;
	private String					disbAccountID;
	private String					partnerBank;
	private long					acSetID;
	private boolean					createNow			= false;
	private boolean					wif					= false;
	private String					cmtReference;
	private boolean					commitment			= false;
	private boolean					alwCmtPostings		= false;
	private boolean					postingSucess		= false;
	private boolean					isEOD				= false;
	private String					errorMessage;
	private long					linkedTranId;
	private String					partnerBankAcType;
	private String					partnerBankAc;

	public AEEvent() {

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AEAmountCodes getAeAmountCodes() {
		return aeAmountCodes;
	}

	public void setAeAmountCodes(AEAmountCodes aeAmountCodes) {
		this.aeAmountCodes = aeAmountCodes;
	}

	public HashMap<String, Object> getExecutingMap() {
		return executingMap;
	}

	public void setExecutingMap(HashMap<String, Object> executingMap) {
		this.executingMap = executingMap;
	}

	public List<TransactionEntry> getTransactionEntries() {
		return transactionEntries;
	}

	public void setTransactionEntries(List<TransactionEntry> transactionEntries) {
		this.transactionEntries = transactionEntries;
	}

	public Map<String, FeeRule> getFeeChargeMap() {
		return feeChargeMap;
	}

	public void setFeeChargeMap(Map<String, FeeRule> feeChargeMap) {
		this.feeChargeMap = feeChargeMap;
	}

	public List<ReturnDataSet> getReturnDataSet() {
		return returnDataSet;
	}

	public void setReturnDataSet(List<ReturnDataSet> returnDataSet) {
		this.returnDataSet = returnDataSet;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getPromotion() {
		return promotion;
	}

	public void setPromotion(String promotion) {
		this.promotion = promotion;
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

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getCcy() {
		return ccy;
	}

	public void setCcy(String ccy) {
		this.ccy = ccy;
	}

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

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getModuleDefiner() {
		return moduleDefiner;
	}

	public void setModuleDefiner(String moduleDefiner) {
		this.moduleDefiner = moduleDefiner;
	}

	public String getDisbAccountID() {
		return disbAccountID;
	}

	public void setDisbAccountID(String disbAccountID) {
		this.disbAccountID = disbAccountID;
	}

	public String getPartnerBank() {
		return partnerBank;
	}

	public void setPartnerBank(String partnerBank) {
		this.partnerBank = partnerBank;
	}

	public long getAcSetID() {
		return acSetID;
	}

	public void setAcSetID(long acSetID) {
		this.acSetID = acSetID;
	}

	public boolean isCreateNow() {
		return createNow;
	}

	public void setCreateNow(boolean createNow) {
		this.createNow = createNow;
	}

	public boolean isWif() {
		return wif;
	}

	public void setWif(boolean wif) {
		this.wif = wif;
	}

	public String getCmtReference() {
		return cmtReference;
	}

	public void setCmtReference(String cmtReference) {
		this.cmtReference = cmtReference;
	}

	public boolean isCommitment() {
		return commitment;
	}

	public void setCommitment(boolean commitment) {
		this.commitment = commitment;
	}

	public boolean isAlwCmtPostings() {
		return alwCmtPostings;
	}

	public void setAlwCmtPostings(boolean alwCmtPostings) {
		this.alwCmtPostings = alwCmtPostings;
	}

	public boolean isPostingSucess() {
		return postingSucess;
	}

	public void setPostingSucess(boolean postingSucess) {
		this.postingSucess = postingSucess;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public boolean isEOD() {
		return isEOD;
	}

	public void setEOD(boolean isEOD) {
		this.isEOD = isEOD;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getPartnerBankAcType() {
		return partnerBankAcType;
	}

	public void setPartnerBankAcType(String partnerBankAcType) {
		this.partnerBankAcType = partnerBankAcType;
	}

	public String getPartnerBankAc() {
		return partnerBankAc;
	}

	public void setPartnerBankAc(String partnerBankAc) {
		this.partnerBankAc = partnerBankAc;
	}

}
