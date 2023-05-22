package com.pennant.backend.model.reports;

public class CreditReviewSubCtgDetails {

	private String subCategoryDesc = "";
	private String curYearAuditValue = "";
	private String curYearUSDConvstn = "";
	private String curYearBreakDown = "";
	private String curYearPercentage = "";
	private String preYearAuditValue = "";
	private String preYearUSDConvstn = "";
	private String preYearBreakDown = "";

	private String header = "F";
	private String mainGroup = "F";
	private String mainGroupDesc = "";
	private String tabDesc = "";
	private String subGroup = "F";

	private String curYearAuditValueHeader = "";
	private String curYearBreakDownHeader = "";
	private String preYearAuditValueHeader = "";
	private String preYearBreakDownHeader = "";
	private String curYearPerHeader = "";
	private String calC = "";
	private String currencyConvertion = "";
	private String remarks = "";
	private String groupCode = "";

	/*
	 * For Inquiry report
	 */

	// For Headers
	private String yera1AuditValueHeader = "";
	private String yera2AuditValueHeader = "";
	private String yera3AuditValueHeader = "";
	private String yera1BreakDownHeader = "";
	private String yera2BreakDownHeader = "";
	private String yera3BreakDownHeader = "";
	private String yera12PerChangeHeader = "";
	private String yera23PerChangeHeader = "";

	// For Values
	private String yera1AuditValue = "";
	private String yera2AuditValue = "";
	private String yera3AuditValue = "";
	private String yera1BreakDown = "";
	private String yera2BreakDown = "";
	private String yera3BreakDown = "";
	private String yera12PerChange = "";
	private String yera23PerChange = "";
	private String year1USDConvstn = "";
	private String year2USDConvstn = "";
	private String year3USDConvstn = "";

	public CreditReviewSubCtgDetails() {
	    super();
	}

	public String getSubCategoryDesc() {
		return subCategoryDesc;
	}

	public void setSubCategoryDesc(String subCategoryDesc) {
		this.subCategoryDesc = subCategoryDesc;
	}

	public String getCurYearAuditValue() {
		return curYearAuditValue;
	}

	public void setCurYearAuditValue(String curYearAuditValue) {
		this.curYearAuditValue = curYearAuditValue;
	}

	public String getCurYearUSDConvstn() {
		return curYearUSDConvstn;
	}

	public void setCurYearUSDConvstn(String curYearUSDConvstn) {
		this.curYearUSDConvstn = curYearUSDConvstn;
	}

	public String getCurYearBreakDown() {
		return curYearBreakDown;
	}

	public void setCurYearBreakDown(String curYearBreakDown) {
		this.curYearBreakDown = curYearBreakDown;
	}

	public String getCurYearPercentage() {
		return curYearPercentage;
	}

	public void setCurYearPercentage(String curYearPercentage) {
		this.curYearPercentage = curYearPercentage;
	}

	public String getPreYearAuditValue() {
		return preYearAuditValue;
	}

	public void setPreYearAuditValue(String preYearAuditValue) {
		this.preYearAuditValue = preYearAuditValue;
	}

	public String getPreYearUSDConvstn() {
		return preYearUSDConvstn;
	}

	public String getCalC() {
		return calC;
	}

	public void setCalC(String calC) {
		this.calC = calC;
	}

	public void setPreYearUSDConvstn(String preYearUSDConvstn) {
		this.preYearUSDConvstn = preYearUSDConvstn;
	}

	public String getPreYearBreakDown() {
		return preYearBreakDown;
	}

	public void setPreYearBreakDown(String preYearBreakDown) {
		this.preYearBreakDown = preYearBreakDown;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getMainGroup() {
		return mainGroup;
	}

	public void setMainGroup(String mainGroup) {
		this.mainGroup = mainGroup;
	}

	public String getSubGroup() {
		return subGroup;
	}

	public void setSubGroup(String subGroup) {
		this.subGroup = subGroup;
	}

	public String getMainGroupDesc() {
		return mainGroupDesc;
	}

	public void setMainGroupDesc(String mainGroupDesc) {
		this.mainGroupDesc = mainGroupDesc;
	}

	public String getCurYearAuditValueHeader() {
		return curYearAuditValueHeader;
	}

	public void setCurYearAuditValueHeader(String curYearAuditValueHeader) {
		this.curYearAuditValueHeader = curYearAuditValueHeader;
	}

	public String getCurYearBreakDownHeader() {
		return curYearBreakDownHeader;
	}

	public void setCurYearBreakDownHeader(String curYearBreakDownHeader) {
		this.curYearBreakDownHeader = curYearBreakDownHeader;
	}

	public String getPreYearAuditValueHeader() {
		return preYearAuditValueHeader;
	}

	public String getCurYearPerHeader() {
		return curYearPerHeader;
	}

	public void setCurYearPerHeader(String curYearPerHeader) {
		this.curYearPerHeader = curYearPerHeader;
	}

	public void setPreYearAuditValueHeader(String preYearAuditValueHeader) {
		this.preYearAuditValueHeader = preYearAuditValueHeader;
	}

	public String getPreYearBreakDownHeader() {
		return preYearBreakDownHeader;
	}

	public void setPreYearBreakDownHeader(String preYearBreakDownHeader) {
		this.preYearBreakDownHeader = preYearBreakDownHeader;
	}

	/*
	 * For Inquiry Screen Report
	 */
	public String getYera1AuditValueHeader() {
		return yera1AuditValueHeader;
	}

	public void setYera1AuditValueHeader(String yera1AuditValueHeader) {
		this.yera1AuditValueHeader = yera1AuditValueHeader;
	}

	public String getYera2AuditValueHeader() {
		return yera2AuditValueHeader;
	}

	public void setYera2AuditValueHeader(String yera2AuditValueHeader) {
		this.yera2AuditValueHeader = yera2AuditValueHeader;
	}

	public String getYera3AuditValueHeader() {
		return yera3AuditValueHeader;
	}

	public void setYera3AuditValueHeader(String yera3AuditValueHeader) {
		this.yera3AuditValueHeader = yera3AuditValueHeader;
	}

	public String getYera1BreakDownHeader() {
		return yera1BreakDownHeader;
	}

	public void setYera1BreakDownHeader(String yera1BreakDownHeader) {
		this.yera1BreakDownHeader = yera1BreakDownHeader;
	}

	public String getYera2BreakDownHeader() {
		return yera2BreakDownHeader;
	}

	public void setYera2BreakDownHeader(String yera2BreakDownHeader) {
		this.yera2BreakDownHeader = yera2BreakDownHeader;
	}

	public String getYera3BreakDownHeader() {
		return yera3BreakDownHeader;
	}

	public void setYera3BreakDownHeader(String yera3BreakDownHeader) {
		this.yera3BreakDownHeader = yera3BreakDownHeader;
	}

	public String getYera12PerChangeHeader() {
		return yera12PerChangeHeader;
	}

	public void setYera12PerChangeHeader(String yera12PerChangeHeader) {
		this.yera12PerChangeHeader = yera12PerChangeHeader;
	}

	public String getYera23PerChangeHeader() {
		return yera23PerChangeHeader;
	}

	public void setYera23PerChangeHeader(String yera23PerChangeHeader) {
		this.yera23PerChangeHeader = yera23PerChangeHeader;
	}

	public String getYera1AuditValue() {
		return yera1AuditValue;
	}

	public void setYera1AuditValue(String yera1AuditValue) {
		this.yera1AuditValue = yera1AuditValue;
	}

	public String getYera2AuditValue() {
		return yera2AuditValue;
	}

	public void setYera2AuditValue(String yera2AuditValue) {
		this.yera2AuditValue = yera2AuditValue;
	}

	public String getYera3AuditValue() {
		return yera3AuditValue;
	}

	public void setYera3AuditValue(String yera3AuditValue) {
		this.yera3AuditValue = yera3AuditValue;
	}

	public String getYera1BreakDown() {
		return yera1BreakDown;
	}

	public void setYera1BreakDown(String yera1BreakDown) {
		this.yera1BreakDown = yera1BreakDown;
	}

	public String getYera2BreakDown() {
		return yera2BreakDown;
	}

	public void setYera2BreakDown(String yera2BreakDown) {
		this.yera2BreakDown = yera2BreakDown;
	}

	public String getYera3BreakDown() {
		return yera3BreakDown;
	}

	public void setYera3BreakDown(String yera3BreakDown) {
		this.yera3BreakDown = yera3BreakDown;
	}

	public String getYera12PerChange() {
		return yera12PerChange;
	}

	public void setYera12PerChange(String yera12PerChange) {
		this.yera12PerChange = yera12PerChange;
	}

	public String getYera23PerChange() {
		return yera23PerChange;
	}

	public void setYera23PerChange(String yera23PerChange) {
		this.yera23PerChange = yera23PerChange;
	}

	public String getCurrencyConvertion() {
		return currencyConvertion;
	}

	public void setCurrencyConvertion(String currencyConvertion) {
		this.currencyConvertion = currencyConvertion;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getYear1USDConvstn() {
		return year1USDConvstn;
	}

	public void setYear1USDConvstn(String year1usdConvstn) {
		year1USDConvstn = year1usdConvstn;
	}

	public String getYear2USDConvstn() {
		return year2USDConvstn;
	}

	public void setYear2USDConvstn(String year2usdConvstn) {
		year2USDConvstn = year2usdConvstn;
	}

	public String getYear3USDConvstn() {
		return year3USDConvstn;
	}

	public void setYear3USDConvstn(String year3usdConvstn) {
		year3USDConvstn = year3usdConvstn;
	}

	public String getTabDesc() {
		return tabDesc;
	}

	public void setTabDesc(String tabDesc) {
		this.tabDesc = tabDesc;
	}

}
