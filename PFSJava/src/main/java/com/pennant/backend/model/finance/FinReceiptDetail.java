package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.pennant.backend.model.Entity;

@XmlAccessorType(XmlAccessType.NONE)
public class FinReceiptDetail implements Entity {

	private long						receiptID		= 0;										// Only setting from Receipt Header
	private long						receiptSeqID	= 0;										// Auto Generated
	private String						receiptType;
	private String						paymentTo;
	private String						paymentType;
	private long						payAgainstID	= 0;
	private int							payOrder		= 0;
	private BigDecimal					amount			= BigDecimal.ZERO;
	private String						favourNumber;
	private Date						valueDate;
	private String						bankCode;
	private String						bankCodeDesc;
	private String						favourName;
	private Date						depositDate;
	private String						depositNo;
	@XmlElement
	private String						paymentRef;
	@XmlElement
	private String						transactionRef;
	private String						chequeAcNo;
	@XmlElement(name="fundingAccount")
	private long						fundingAc		= 0;
	private String						fundingAcDesc;
	@XmlElement
	private Date						receivedDate;
	private String						status;
	//API Specific
	@XmlElement
	private String						remarks;
	private long						logKey			= 0;
	private boolean						delRecord		= false;
	private String						partnerBankAc;
	private String						partnerBankAcType;
	private String						reference;					// only for Fees
	private String						feeTypeDesc;
	private boolean 					noReserve;

	private List<FinRepayHeader>		repayHeaders	= new ArrayList<FinRepayHeader>(1);
	private List<ManualAdviseMovements>	advMovements	= new ArrayList<ManualAdviseMovements>(1);
	
	
	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> receiptDetailMap = new HashMap<String, Object>();
		getDeclaredFieldValues(receiptDetailMap);
		return receiptDetailMap;
	}

	public void getDeclaredFieldValues(HashMap<String, Object> receiptDetailMap) {
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				// "rd_" Should be in small case only, if we want to change the case we need to update the configuration fields as well.
				receiptDetailMap.put("rd_" + this.getClass().getDeclaredFields()[i].getName(),
						this.getClass().getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}
	}
	
	public FinReceiptDetail() {

	}
	
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("noReserve");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public long getId() {
		return receiptSeqID;
	}

	@Override
	public void setId(long id) {
		this.receiptSeqID = id;
	}

	public long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

	public long getReceiptSeqID() {
		return receiptSeqID;
	}

	public void setReceiptSeqID(long receiptSeqID) {
		this.receiptSeqID = receiptSeqID;
	}

	public String getReceiptType() {
		return receiptType;
	}

	public void setReceiptType(String receiptType) {
		this.receiptType = receiptType;
	}

	public String getPaymentTo() {
		return paymentTo;
	}

	public void setPaymentTo(String paymentTo) {
		this.paymentTo = paymentTo;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public long getPayAgainstID() {
		return payAgainstID;
	}

	public void setPayAgainstID(long payAgainstID) {
		this.payAgainstID = payAgainstID;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getFavourNumber() {
		return favourNumber;
	}

	public void setFavourNumber(String favourNumber) {
		this.favourNumber = favourNumber;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getFavourName() {
		return favourName;
	}

	public void setFavourName(String favourName) {
		this.favourName = favourName;
	}

	public Date getDepositDate() {
		return depositDate;
	}

	public void setDepositDate(Date depositDate) {
		this.depositDate = depositDate;
	}

	public String getDepositNo() {
		return depositNo;
	}

	public void setDepositNo(String depositNo) {
		this.depositNo = depositNo;
	}

	public String getPaymentRef() {
		return paymentRef;
	}

	public void setPaymentRef(String paymentRef) {
		this.paymentRef = paymentRef;
	}

	public String getTransactionRef() {
		return transactionRef;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	public String getChequeAcNo() {
		return chequeAcNo;
	}

	public void setChequeAcNo(String chequeAcNo) {
		this.chequeAcNo = chequeAcNo;
	}

	public long getFundingAc() {
		return fundingAc;
	}

	public void setFundingAc(long fundingAc) {
		this.fundingAc = fundingAc;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<FinRepayHeader> getRepayHeaders() {
		return repayHeaders;
	}

	public void setRepayHeaders(List<FinRepayHeader> repayHeaders) {
		this.repayHeaders = repayHeaders;
	}

	public String getBankCodeDesc() {
		return bankCodeDesc;
	}

	public void setBankCodeDesc(String bankCodeDesc) {
		this.bankCodeDesc = bankCodeDesc;
	}

	public String getFundingAcDesc() {
		return fundingAcDesc;
	}

	public void setFundingAcDesc(String fundingAcDesc) {
		this.fundingAcDesc = fundingAcDesc;
	}

	public List<ManualAdviseMovements> getAdvMovements() {
		return advMovements;
	}

	public void setAdvMovements(List<ManualAdviseMovements> advMovements) {
		this.advMovements = advMovements;
	}

	public boolean isDelRecord() {
		return delRecord;
	}

	public void setDelRecord(boolean delRecord) {
		this.delRecord = delRecord;
	}

	public int getPayOrder() {
		return payOrder;
	}

	public void setPayOrder(int payOrder) {
		this.payOrder = payOrder;
	}

	public long getLogKey() {
		return logKey;
	}

	public void setLogKey(long logKey) {
		this.logKey = logKey;
	}

	public String getPartnerBankAc() {
		return partnerBankAc;
	}

	public void setPartnerBankAc(String partnerBankAc) {
		this.partnerBankAc = partnerBankAc;
	}

	public String getPartnerBankAcType() {
		return partnerBankAcType;
	}

	public void setPartnerBankAcType(String partnerBankAcType) {
		this.partnerBankAcType = partnerBankAcType;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getFeeTypeDesc() {
		return feeTypeDesc;
	}

	public void setFeeTypeDesc(String feeTypeDesc) {
		this.feeTypeDesc = feeTypeDesc;
	}

	public boolean isNoReserve() {
		return noReserve;
	}

	public void setNoReserve(boolean noReserve) {
		this.noReserve = noReserve;
	}

}
