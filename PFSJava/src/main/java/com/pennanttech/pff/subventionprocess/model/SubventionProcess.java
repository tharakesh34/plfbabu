package com.pennanttech.pff.subventionprocess.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class SubventionProcess extends AbstractWorkflowEntity {

	/**
	 * SubventionProcess for Subvention upload details
	 */
	private static final long serialVersionUID = 1L;

	private Long id = Long.MIN_VALUE;
	private String hostReference;
	private String issuer;
	private String acquirer;
	private String merchantUsername;
	private String manufacturerName;
	private String storeName;
	private String storeCity;
	private String storeState;
	private String manufactureId;
	private String terminalId;
	private int emiOffer;
	private int rrn;
	private int bankApprovalCode;
	private Timestamp transactionDateTime;
	private Timestamp settlementDateTime;
	private BigDecimal transactionAmount;
	private String txnStatus;
	private String productCategory;
	private String subcat1;
	private String subcat2;
	private String subcat3;
	private String productSrNo;
	private long cardHash;
	private int emiModel;
	private String posId;
	private BigDecimal discountRate;
	private BigDecimal discountamount;
	private BigDecimal cashbackRate;
	private BigDecimal cashBackAmount;
	private BigDecimal nbfcCashbackRate;
	private BigDecimal nbfcCashbackAmount;
	private BigDecimal linkedTranId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getHostReference() {
		return hostReference;
	}

	public void setHostReference(String hostReference) {
		this.hostReference = hostReference;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getMerchantUsername() {
		return merchantUsername;
	}

	public void setMerchantUsername(String merchantUsername) {
		this.merchantUsername = merchantUsername;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getStoreCity() {
		return storeCity;
	}

	public void setStoreCity(String storeCity) {
		this.storeCity = storeCity;
	}

	public String getStoreState() {
		return storeState;
	}

	public void setStoreState(String storeState) {
		this.storeState = storeState;
	}

	public String getManufactureId() {
		return manufactureId;
	}

	public void setManufactureId(String manufactureId) {
		this.manufactureId = manufactureId;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public int getEmiOffer() {
		return emiOffer;
	}

	public void setEmiOffer(int emiOffer) {
		this.emiOffer = emiOffer;
	}

	public int getRrn() {
		return rrn;
	}

	public void setRrn(int rrn) {
		this.rrn = rrn;
	}

	public int getBankApprovalCode() {
		return bankApprovalCode;
	}

	public void setBankApprovalCode(int bankApprovalCode) {
		this.bankApprovalCode = bankApprovalCode;
	}

	public Timestamp getTransactionDateTime() {
		return transactionDateTime;
	}

	public void setTransactionDateTime(Timestamp transactionDateTime) {
		this.transactionDateTime = transactionDateTime;
	}

	public Timestamp getSettlementDateTime() {
		return settlementDateTime;
	}

	public void setSettlementDateTime(Timestamp settlementDateTime) {
		this.settlementDateTime = settlementDateTime;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getTxnStatus() {
		return txnStatus;
	}

	public void setTxnStatus(String txnStatus) {
		this.txnStatus = txnStatus;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getSubcat1() {
		return subcat1;
	}

	public void setSubcat1(String subcat1) {
		this.subcat1 = subcat1;
	}

	public String getSubcat2() {
		return subcat2;
	}

	public void setSubcat2(String subcat2) {
		this.subcat2 = subcat2;
	}

	public String getSubcat3() {
		return subcat3;
	}

	public void setSubcat3(String subcat3) {
		this.subcat3 = subcat3;
	}

	public String getProductSrNo() {
		return productSrNo;
	}

	public void setProductSrNo(String productSrNo) {
		this.productSrNo = productSrNo;
	}

	public long getCardHash() {
		return cardHash;
	}

	public void setCardHash(long cardHash) {
		this.cardHash = cardHash;
	}

	public int getEmiModel() {
		return emiModel;
	}

	public void setEmiModel(int emiModel) {
		this.emiModel = emiModel;
	}

	public String getPosId() {
		return posId;
	}

	public void setPosId(String posId) {
		this.posId = posId;
	}

	public BigDecimal getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(BigDecimal discountRate) {
		this.discountRate = discountRate;
	}

	public BigDecimal getDiscountamount() {
		return discountamount;
	}

	public void setDiscountamount(BigDecimal discountamount) {
		this.discountamount = discountamount;
	}

	public BigDecimal getCashbackRate() {
		return cashbackRate;
	}

	public void setCashbackRate(BigDecimal cashbackRate) {
		this.cashbackRate = cashbackRate;
	}

	public BigDecimal getCashBackAmount() {
		return cashBackAmount;
	}

	public void setCashBackAmount(BigDecimal cashBackAmount) {
		this.cashBackAmount = cashBackAmount;
	}

	public BigDecimal getNbfcCashbackRate() {
		return nbfcCashbackRate;
	}

	public void setNbfcCashbackRate(BigDecimal nbfcCashbackRate) {
		this.nbfcCashbackRate = nbfcCashbackRate;
	}

	public BigDecimal getNbfcCashbackAmount() {
		return nbfcCashbackAmount;
	}

	public void setNbfcCashbackAmount(BigDecimal nbfcCashbackAmount) {
		this.nbfcCashbackAmount = nbfcCashbackAmount;
	}

	public BigDecimal getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(BigDecimal linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

}
