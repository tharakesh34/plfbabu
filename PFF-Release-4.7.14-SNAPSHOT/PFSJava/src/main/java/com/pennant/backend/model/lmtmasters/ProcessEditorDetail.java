package com.pennant.backend.model.lmtmasters;

public class ProcessEditorDetail {
	
	private long moduleId = Long.MIN_VALUE;
	private String moduleName;
	private String moduleDesc;
	
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public String getModuleDesc() {
		return moduleDesc;
	}
	public void setModuleDesc(String moduleDesc) {
		this.moduleDesc = moduleDesc;
	}

}
