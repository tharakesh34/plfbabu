package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.util.Date;

public class PaymentTransaction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long transactionId;
	private String tranModule;
	private String tranReference;
	private String tranBatch;
	private String tranStatus;
	private String messageId;
	private long paymentId;
	private String respReference;
	private String finReference;
	private String statusCode;
	private String statusDesc;
	private Date transctionInitOn;
	private Date statusUpdateOn;
	private PaymentTransaction befImage;
	private int seqNo;

	private String requstedByUser;
	private int bankId;
	private String bankReference;

	private FinAdvancePayments finAdvancePayments;

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public String getTranModule() {
		return tranModule;
	}

	public void setTranModule(String tranModule) {
		this.tranModule = tranModule;
	}

	public String getTranReference() {
		return tranReference;
	}

	public void setTranReference(String tranReference) {
		this.tranReference = tranReference;
	}

	public String getTranBatch() {
		return tranBatch;
	}

	public void setTranBatch(String tranBatch) {
		this.tranBatch = tranBatch;
	}

	public String getTranStatus() {
		return tranStatus;
	}

	public void setTranStatus(String tranStatus) {
		this.tranStatus = tranStatus;
	}

	public String getRespReference() {
		return respReference;
	}

	public void setRespReference(String respReference) {
		this.respReference = respReference;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public Date getTransctionInitOn() {
		return transctionInitOn;
	}

	public void setTransctionInitOn(Date transctionInitOn) {
		this.transctionInitOn = transctionInitOn;
	}

	public Date getStatusUpdateOn() {
		return statusUpdateOn;
	}

	public void setStatusUpdateOn(Date statusUpdateOn) {
		this.statusUpdateOn = statusUpdateOn;
	}

	/**
	 * @return the paymentId
	 */
	public long getPaymentId() {
		return paymentId;
	}

	/**
	 * @param paymentId the paymentId to set
	 */
	public void setPaymentId(long paymentId) {
		this.paymentId = paymentId;
	}

	/**
	 * @return the messageId
	 */
	public String getMessageId() {
		return messageId;
	}

	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	/**
	 * @return the finReference
	 */
	public String getFinReference() {
		return finReference;
	}

	/**
	 * @param finReference the finReference to set
	 */
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	/**
	 * @return the finAdvancePayments
	 */
	public FinAdvancePayments getFinAdvancePayments() {
		return finAdvancePayments;
	}

	/**
	 * @param finAdvancePayments the finAdvancePayments to set
	 */
	public void setFinAdvancePayments(FinAdvancePayments finAdvancePayments) {
		this.finAdvancePayments = finAdvancePayments;
	}

	/**
	 * @return the befImage
	 */
	public PaymentTransaction getBefImage() {
		return befImage;
	}

	/**
	 * @param befImage the befImage to set
	 */
	public void setBefImage(PaymentTransaction befImage) {
		this.befImage = befImage;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public String getRequstedByUser() {
		return requstedByUser;
	}

	public void setRequstedByUser(String requstedByUser) {
		this.requstedByUser = requstedByUser;
	}

	public int getBankId() {
		return bankId;
	}

	public void setBankId(int bankId) {
		this.bankId = bankId;
	}

	public String getBankReference() {
		return bankReference;
	}

	public void setBankReference(String bankReference) {
		this.bankReference = bankReference;
	}

}
