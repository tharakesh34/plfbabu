package com.pennanttech.ws.model.miscellaneous;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "checkListId", "ansDesc", "docRequired", "docType", "remarksMandatory" })
@XmlRootElement(name = "checkListDetail")
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckListDetailsRespons implements Serializable {
	private static final long serialVersionUID = -3176600783924484359L;

	@XmlElement
	private String ansDesc;
	@XmlElement
	private boolean docRequired;
	@XmlElement
	private String docType;
	@XmlElement
	private long checkListId;
	@XmlElement(name = "remarksMandatory")
	private boolean remarksMand;

	public boolean isRemarksMand() {
		return remarksMand;
	}

	public void setRemarksMand(boolean remarksMand) {
		this.remarksMand = remarksMand;
	}

	public long getCheckListId() {
		return checkListId;
	}

	public void setCheckListId(long checkListId) {
		this.checkListId = checkListId;
	}

	public String getAnsDesc() {
		return ansDesc;
	}

	public void setAnsDesc(String ansDesc) {
		this.ansDesc = ansDesc;
	}

	public boolean isDocRequired() {
		return docRequired;
	}

	public void setDocRequired(boolean docRequired) {
		this.docRequired = docRequired;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocType() {
		return docType;
	}

}
