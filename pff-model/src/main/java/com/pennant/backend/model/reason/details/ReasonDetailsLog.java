package com.pennant.backend.model.reason.details;

import java.io.Serializable;
import java.util.Date;

import com.pennant.backend.model.Entity;

public class ReasonDetailsLog implements Serializable, Entity {
	private static final long serialVersionUID = 1L;

	private String module;
	private String reference;
	private String remarks;
	private String roleCode;
	private String activity;
	private String description;
	private long toUser;
	private String usrLogin;
	private String usrFname;
	private String usrMname;
	private String usrLname;
	private String roleDesc;
	private Date logTime;
	private String code;

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getToUser() {
		return toUser;
	}

	public void setToUser(long toUser) {
		this.toUser = toUser;
	}

	public String getUsrLogin() {
		return usrLogin;
	}

	public void setUsrLogin(String usrLogin) {
		this.usrLogin = usrLogin;
	}

	public String getUsrFname() {
		return usrFname;
	}

	public void setUsrFname(String usrFname) {
		this.usrFname = usrFname;
	}

	public String getUsrMname() {
		return usrMname;
	}

	public void setUsrMname(String usrMname) {
		this.usrMname = usrMname;
	}

	public String getUsrLname() {
		return usrLname;
	}

	public void setUsrLname(String usrLname) {
		this.usrLname = usrLname;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public Date getLogTime() {
		return logTime;
	}

	public void setLogTime(Date logTime) {
		this.logTime = logTime;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public long getId() {
		return 0;
	}

	@Override
	public void setId(long id) {
	}
}
