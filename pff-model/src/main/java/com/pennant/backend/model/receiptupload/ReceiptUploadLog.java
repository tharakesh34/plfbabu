package com.pennant.backend.model.receiptupload;

import java.util.concurrent.atomic.AtomicInteger;

public class ReceiptUploadLog extends ReceiptUploadHeader {

	private static final long serialVersionUID = 1L;
	private long Id;
	private long headerId;
	private int totalcount;
	private AtomicInteger processedRecords = new AtomicInteger(0);
	private AtomicInteger successRecords = new AtomicInteger(0);
	private AtomicInteger failRecords = new AtomicInteger(0);

	public ReceiptUploadLog() {
	    super();
	}

	public ReceiptUploadLog(long headerId) {
		this.headerId = headerId;
	}

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public int getTotalcount() {
		return totalcount;
	}

	public void setTotalcount(int totalcount) {
		this.totalcount = totalcount;
	}

	public AtomicInteger getProcessedRecords() {
		return processedRecords;
	}

	public AtomicInteger getSuccessRecords() {
		return successRecords;
	}

	public AtomicInteger getFailRecords() {
		return failRecords;
	}

	public void setProcessedRecords(AtomicInteger processedRecords) {
		this.processedRecords = processedRecords;
	}

	public void setSuccessRecords(AtomicInteger successRecords) {
		this.successRecords = successRecords;
	}

	public void setFailRecords(AtomicInteger failRecords) {
		this.failRecords = failRecords;
	}

	public void incProcessedRecords() {
		this.processedRecords.getAndIncrement();
	}

	public void incSuccessRecords() {
		this.successRecords.getAndIncrement();
	}

	public void incFailRecords() {
		this.failRecords.getAndIncrement();
	}

	public int getProgress() {
		double a = 1
				- ((double) (this.totalcount - (this.successRecords.get() + this.failRecords.get())) / this.totalcount);
		return (int) (a * 100);
	}
}
