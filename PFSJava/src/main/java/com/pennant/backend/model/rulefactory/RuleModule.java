package com.pennant.backend.model.rulefactory;

/**
 * Model class for the <b>RuleModule Object</b>.<br>
 */
public class RuleModule {

	private String rbmModule;
	private String rbmEvent;
	private String rbmFldName;
	private String rbmFldType;

	public RuleModule() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getRbmModule() {
		return rbmModule;
	}

	public void setRbmModule(String rbmModule) {
		this.rbmModule = rbmModule;
	}

	public String getRbmEvent() {
		return rbmEvent;
	}

	public void setRbmEvent(String rbmEvent) {
		this.rbmEvent = rbmEvent;
	}

	public String getRbmFldName() {
		return rbmFldName;
	}

	public void setRbmFldName(String rbmFldName) {
		this.rbmFldName = rbmFldName;
	}

	public String getRbmFldType() {
		return rbmFldType;
	}

	public void setRbmFldType(String rbmFldType) {
		this.rbmFldType = rbmFldType;
	}

}
