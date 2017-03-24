package com.pennant.eod.beans;

import java.util.Date;
import java.util.List;


public class PaymentRecoveryHeader {

	private String batchRefNumber;
	private String batchType;
	private String fileName;
	private Date fileCreationDate;
	private int numberofRecords;
	private int negativeCounter;
	private int positiveCounter;

	// header
	private String hRecordIdentifier;
	// trailer
	private String tRecordIdentifier;
	//details
	private List<PaymentRecoveryDetail>  paymentRecoveryDetails;
	public String gethRecordIdentifier() {
		return hRecordIdentifier;
	}
	public void sethRecordIdentifier(String hRecordIdentifier) {
		this.hRecordIdentifier = hRecordIdentifier;
	}
	public String getBatchType() {
		return batchType;
	}
	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}
	public String getBatchRefNumber() {
		return batchRefNumber;
	}
	public void setBatchRefNumber(String batchReferenceNumber) {
		this.batchRefNumber = batchReferenceNumber;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Date getFileCreationDate() {
		return fileCreationDate;
	}
	public void setFileCreationDate(Date fileCreationDate) {
		this.fileCreationDate = fileCreationDate;
	}
	public String gettRecordIdentifier() {
		return tRecordIdentifier;
	}
	public void settRecordIdentifier(String tRecordIdentifier) {
		this.tRecordIdentifier = tRecordIdentifier;
	}
	public int getNumberofRecords() {
		return numberofRecords;
	}
	public void setNumberofRecords(int numberofRecords) {
		this.numberofRecords = numberofRecords;
	}
	public List<PaymentRecoveryDetail> getPaymentRecoveryDetails() {
		return paymentRecoveryDetails;
	}
	public void setPaymentRecoveryDetails(List<PaymentRecoveryDetail> paymentRecoveryDetails) {
		this.paymentRecoveryDetails = paymentRecoveryDetails;
	}
	public int getNegativeCounter() {
		return negativeCounter;
	}
	public void setNegativeCounter(int negativeCounter) {
		this.negativeCounter = negativeCounter;
	}
	public int getPositiveCounter() {
		return positiveCounter;
	}
	public void setPositiveCounter(int positiveCounter) {
		this.positiveCounter = positiveCounter;
	}
	

	
}
