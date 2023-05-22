package com.pennant.backend.model.messages;

public class UserContactsList implements java.io.Serializable {
	private static final long serialVersionUID = -780921694962617549L;

	private String usrID;
	private String type;
	private String groupName;
	private String contactsList;

	public UserContactsList() {
	    super();
	}

	public String getUsrID() {
		return usrID;
	}

	public void setUsrID(String usrID) {
		this.usrID = usrID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getContactsList() {
		return contactsList;
	}

	public void setContactsList(String contactsList) {
		this.contactsList = contactsList;
	}
}
