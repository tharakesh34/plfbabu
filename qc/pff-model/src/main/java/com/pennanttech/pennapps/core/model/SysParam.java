package com.pennanttech.pennapps.core.model;

public class SysParam extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String code;
	private String category;
	private String description;
	private String value;
	private String type;
	private int length;
	private boolean maintain;
	private int decimals;
	private String list;
	private String allowedFormat;
	private SysParam befImage;
	private LoggedInUser userDetails;

	public SysParam() {
		super();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isMaintain() {
		return maintain;
	}

	public void setMaintain(boolean maintain) {
		this.maintain = maintain;
	}

	public int getDecimals() {
		return decimals;
	}

	public void setDecimals(int decimals) {
		this.decimals = decimals;
	}

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public String getAllowedFormat() {
		return allowedFormat;
	}

	public void setAllowedFormat(String allowedFormat) {
		this.allowedFormat = allowedFormat;
	}

	public SysParam getBefImage() {
		return befImage;
	}

	public void setBefImage(SysParam befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

}
