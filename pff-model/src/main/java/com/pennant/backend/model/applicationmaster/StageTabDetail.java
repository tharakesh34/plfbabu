package com.pennant.backend.model.applicationmaster;

/**
 * Model class for the <b>StageTabDetail table</b>.<br>
 *
 */
public class StageTabDetail{
	
	private int tabId;
	private String tabCode; 
	private String tabDescription;
	private	String moduleName;
	private String lovValue;
	
	public StageTabDetail() {
		super();
	}

	public int getTabId() {
		return tabId;
	}

	public void setTabId(int tabId) {
		this.tabId = tabId;
	}

	public String getTabCode() {
		return tabCode;
	}

	public void setTabCode(String tabCode) {
		this.tabCode = tabCode;
	}

	public String getTabDescription() {
		return tabDescription;
	}

	public void setTabDescription(String tabDescription) {
		this.tabDescription = tabDescription;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	
}
