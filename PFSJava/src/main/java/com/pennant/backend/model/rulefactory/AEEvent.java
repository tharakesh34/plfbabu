package com.pennant.backend.model.rulefactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pennant.app.constants.AccountConstants;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.ManualAdviseMovements;

public class AEEvent implements Serializable {
	private static final long serialVersionUID = 853801780166711631L;

	private AEAmountCodes aeAmountCodes = new AEAmountCodes();
	private String moduleDefiner;
	private Map<String, Object> dataMap = new HashMap<>();

	private List<Long> acSetIDList = new ArrayList<>(1);
	private List<ReturnDataSet> returnDataSet = new ArrayList<ReturnDataSet>(1);

	private Date postDate;
	private Date valueDate;
	private Date appDate;
	private Date appValueDate;
	private Date schdDate;
	private Date custAppDate;

	private boolean newRecord = false;
	private boolean createNow = false;
	private boolean wif = false;
	private boolean commitment = false;
	private boolean alwCmtPostings = false;
	private boolean isEOD = false;

	private boolean postingSucess = true;
	private String errorMessage;
	private long linkedTranId = Long.MIN_VALUE;
	private long postRefId = Long.MIN_VALUE;
	private long postingId = Long.MIN_VALUE;

	private long custID;
	private String custCIF;
	private long finID;
	private String finReference;
	private String cmtReference;
	private String finType;
	private String promotion;
	private String branch;
	private String ccy;
	private String accountingEvent;
	private String postingUserBranch;
	private String entityCode;

	// VAS
	private String vasPostAgainst;
	private String collateralRef;
	private int transOrder = 0;

	private boolean uAmzExists = false;
	private boolean cpzChgExists = false;
	private boolean bpiIncomized = false;
	private boolean simulateAccounting;

	// Advise Movement
	private ManualAdviseMovements movement;

	private EventProperties eventProperties = new EventProperties();

	// Cash Management
	private String postingType = AccountConstants.ACCOUNT_EVENT_POSTINGTYPE_LOAN;

	private long paymentId;

	public AEEvent() {
		super();
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

	public String getModuleDefiner() {
		return moduleDefiner;
	}

	public void setModuleDefiner(String moduleDefiner) {
		this.moduleDefiner = moduleDefiner;
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
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

	public boolean isEOD() {
		return isEOD;
	}

	public void setEOD(boolean isEOD) {
		this.isEOD = isEOD;
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

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
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

	public String getAccountingEvent() {
		return accountingEvent;
	}

	public void setAccountingEvent(String accountingEvent) {
		this.accountingEvent = accountingEvent;
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

	public String getVasPostAgainst() {
		return vasPostAgainst;
	}

	public void setVasPostAgainst(String vasPostAgainst) {
		this.vasPostAgainst = vasPostAgainst;
	}

	public String getCollateralRef() {
		return collateralRef;
	}

	public void setCollateralRef(String collateralRef) {
		this.collateralRef = collateralRef;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public Date getAppValueDate() {
		return appValueDate;
	}

	public void setAppValueDate(Date appValueDate) {
		this.appValueDate = appValueDate;
	}

	public List<ReturnDataSet> getReturnDataSet() {
		return returnDataSet;
	}

	public void setReturnDataSet(List<ReturnDataSet> returnDataSet) {
		this.returnDataSet = returnDataSet;
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

	public List<Long> getAcSetIDList() {
		return acSetIDList;
	}

	public void setAcSetIDList(List<Long> acSetIDList) {
		this.acSetIDList = acSetIDList;
	}

	public String getPostingUserBranch() {
		return postingUserBranch;
	}

	public void setPostingUserBranch(String postingUserBranch) {
		this.postingUserBranch = postingUserBranch;
	}

	public Date getCustAppDate() {
		return custAppDate;
	}

	public void setCustAppDate(Date custAppDate) {
		this.custAppDate = custAppDate;
	}

	public int getTransOrder() {
		return transOrder;
	}

	public void setTransOrder(int transOrder) {
		this.transOrder = transOrder;
	}

	public boolean isuAmzExists() {
		return uAmzExists;
	}

	public void setuAmzExists(boolean uAmzExists) {
		this.uAmzExists = uAmzExists;
	}

	public long getPostRefId() {
		return postRefId;
	}

	public void setPostRefId(long postRefId) {
		this.postRefId = postRefId;
	}

	public long getPostingId() {
		return postingId;
	}

	public void setPostingId(long postingId) {
		this.postingId = postingId;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getPostingType() {
		return postingType;
	}

	public void setPostingType(String postingType) {
		this.postingType = postingType;
	}

	public boolean isCpzChgExists() {
		return cpzChgExists;
	}

	public void setCpzChgExists(boolean cpzChgExists) {
		this.cpzChgExists = cpzChgExists;
	}

	public boolean isBpiIncomized() {
		return bpiIncomized;
	}

	public void setBpiIncomized(boolean bpiIncomized) {
		this.bpiIncomized = bpiIncomized;
	}

	public ManualAdviseMovements getMovement() {
		return movement;
	}

	public void setMovement(ManualAdviseMovements movement) {
		this.movement = movement;
	}

	public boolean isSimulateAccounting() {
		return simulateAccounting;
	}

	public void setSimulateAccounting(boolean simulateAccounting) {
		this.simulateAccounting = simulateAccounting;
	}

	public EventProperties getEventProperties() {
		return eventProperties;
	}

	public void setEventProperties(EventProperties eventProperties) {
		this.eventProperties = eventProperties;
	}

	public long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(long paymentId) {
		this.paymentId = paymentId;
	}

}
