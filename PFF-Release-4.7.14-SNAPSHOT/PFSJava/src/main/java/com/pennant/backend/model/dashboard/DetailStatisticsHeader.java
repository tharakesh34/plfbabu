package com.pennant.backend.model.dashboard;



public class DetailStatisticsHeader implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4399638057132606254L;
	private String moduleName;
	private String roleCode;
	private int   recordCount;
	private int   lovDescTotRecordCount;
	
	public DetailStatisticsHeader() {
		super();
	}
	
	public DetailStatisticsHeader(String moduleName, String roleCode,
			int recordCount) {
		super();
		this.moduleName = moduleName;
		this.roleCode = roleCode;
		this.recordCount = recordCount;
	}
	
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	public int getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public void setLovDescTotRecordCount(int lovDescTotRecordCount) {
		this.lovDescTotRecordCount = lovDescTotRecordCount;
	}

	public int getLovDescTotRecordCount() {
		return lovDescTotRecordCount;
	}

	
	
}
