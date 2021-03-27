package com.pennant.backend.model.receiptupload;

public class ThreadAllocation extends ReceiptUploadDetail {

	private static final long serialVersionUID = 1L;

	private int count;

	public ThreadAllocation() {
		super();
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
