package com.pennant.backend.model.receiptupload;

import java.util.HashMap;
import java.util.Map;

public class ReceiptUploadTracker {
	private long headerId;
	private Map<Long, Integer> importStatusMap = new HashMap<>();
	private int batchSize;
	private int totalProcesses;
	private int progressCount;
	private int processNo;
	private double maxProgress;

	public ReceiptUploadTracker() {
	    super();
	}

	public long getHeaderId() {
		return headerId;
	}

	public Map<Long, Integer> getImportStatusMap() {
		return importStatusMap;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public int getTotalProcesses() {
		return totalProcesses;
	}

	public int getProgressCount() {
		return progressCount;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	public void setImportStatusMap(Map<Long, Integer> importStatusMap) {
		this.importStatusMap = importStatusMap;
	}

	public void setTotalProcesses(int totalProcesses) {
		maxProgress = (double) 100 / totalProcesses;
		this.totalProcesses = totalProcesses;
	}

	public void setProgressCount(int progressCount) {
		this.progressCount = progressCount;
	}

	public void setBatchSize(int batchSize) {
		processNo++;
		this.progressCount = 0;
		this.batchSize = batchSize;
	}

	public void incrementProgress() {
		double a = 1 - ((double) (batchSize - ++progressCount) / batchSize);
		int value = (int) ((a * maxProgress) + maxProgress * (processNo - 1));
		importStatusMap.put(headerId, value);
	}

}
