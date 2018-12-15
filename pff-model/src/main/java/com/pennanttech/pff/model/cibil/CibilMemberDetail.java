package com.pennanttech.pff.model.cibil;

import java.io.Serializable;

public class CibilMemberDetail implements Serializable {
	private static final long serialVersionUID = 6436665754327587923L;

	private String memberCode;
	private String memberType;
	private String segmentType;
	private String memberName;
	private String memberShortName;
	private String memberId;
	private String previousMemberId;
	private String memberPassword;
	private String filePath;
	private String delimiter;

	public CibilMemberDetail() {
		super();
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getMemberType() {
		return memberType;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}

	public String getMemberName() {
		return memberName;
	}

	public String getSegmentType() {
		return segmentType;
	}

	public void setSegmentType(String segmentType) {
		this.segmentType = segmentType;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getMemberShortName() {
		return memberShortName;
	}

	public void setMemberShortName(String memberShortName) {
		this.memberShortName = memberShortName;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getPreviousMemberId() {
		return previousMemberId;
	}

	public void setPreviousMemberId(String previousMemberId) {
		this.previousMemberId = previousMemberId;
	}

	public String getMemberPassword() {
		return memberPassword;
	}

	public void setMemberPassword(String memberPassword) {
		this.memberPassword = memberPassword;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

}
