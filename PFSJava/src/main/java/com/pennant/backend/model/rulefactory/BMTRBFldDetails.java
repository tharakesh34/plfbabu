package com.pennant.backend.model.rulefactory;

public class BMTRBFldDetails {

	private String rbModule;
	private String rbEvent;
	private String rbFldName;
	private String rbFldDesc;
	private String rbFldType;
	private int rbFldLen;
	private boolean rbForCalFlds;
	private boolean rbForBldFlds;
	private String rbFldTableName;
	private String rbSTFlds;

	public BMTRBFldDetails() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getRbModule() {
		return rbModule;
	}

	public void setRbModule(String rbModule) {
		this.rbModule = rbModule;
	}

	public String getRbEvent() {
		return rbEvent;
	}

	public void setRbEvent(String rbEvent) {
		this.rbEvent = rbEvent;
	}

	public String getRbFldName() {
		return rbFldName;
	}

	public void setRbFldName(String rbFldName) {
		this.rbFldName = rbFldName;
	}

	public String getRbFldDesc() {
		return rbFldDesc;
	}

	public void setRbFldDesc(String rbFldDesc) {
		this.rbFldDesc = rbFldDesc;
	}

	public String getRbFldType() {
		return rbFldType;
	}

	public void setRbFldType(String rbFldType) {
		this.rbFldType = rbFldType;
	}

	public int getRbFldLen() {
		return rbFldLen;
	}

	public void setRbFldLen(int rbFldLen) {
		this.rbFldLen = rbFldLen;
	}

	public void setRbForCalFlds(boolean rbForCalFlds) {
		this.rbForCalFlds = rbForCalFlds;
	}

	public boolean isRbForCalFlds() {
		return rbForCalFlds;
	}

	public void setRbForBldFlds(boolean rbForBldFlds) {
		this.rbForBldFlds = rbForBldFlds;
	}

	public boolean isRbForBldFlds() {
		return rbForBldFlds;
	}

	public String getRbFldTableName() {
		return rbFldTableName;
	}

	public void setRbFldTableName(String rbFldTableName) {
		this.rbFldTableName = rbFldTableName;
	}

	public String getRbSTFlds() {
		return rbSTFlds;
	}

	public void setRbSTFlds(String rbSTFlds) {
		this.rbSTFlds = rbSTFlds;
	}

}
