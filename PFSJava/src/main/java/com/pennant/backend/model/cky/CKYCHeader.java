package com.pennant.backend.model.cky;

import java.util.Date;

public class CKYCHeader {
	private int recordType;
	private String batchNo;
	private String fiCode;
	private String regionCode;
	private int totDetailsRecord;
	private Date createDate;
	private String version;
	private String headerFiller1;
	private String headerFiller2;
	private String headerFiller3;
	private String headerFiller4;

	public int getRecordType() {
		return recordType;
	}

	public void setRecordType(int recordType) {
		this.recordType = recordType;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getFiCode() {
		return fiCode;
	}

	public void setFiCode(String fiCode) {
		this.fiCode = fiCode;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public int getTotDetailsRecord() {
		return totDetailsRecord;
	}

	public void setTotDetailsRecord(int totDetailsRecord) {
		this.totDetailsRecord = totDetailsRecord;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getHeaderFiller1() {
		return headerFiller1;
	}

	public void setHeaderFiller1(String headerFiller1) {
		this.headerFiller1 = headerFiller1;
	}

	public String getHeaderFiller2() {
		return headerFiller2;
	}

	public void setHeaderFiller2(String headerFiller2) {
		this.headerFiller2 = headerFiller2;
	}

	public String getHeaderFiller3() {
		return headerFiller3;
	}

	public void setHeaderFiller3(String headerFiller3) {
		this.headerFiller3 = headerFiller3;
	}

	public String getHeaderFiller4() {
		return headerFiller4;
	}

	public void setHeaderFiller4(String headerFiller4) {
		this.headerFiller4 = headerFiller4;
	}

}
