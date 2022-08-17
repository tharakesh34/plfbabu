package com.pennanttech.pff.npa.model;

import java.io.Serializable;
import java.util.Date;

public class NPAStageDetail implements Serializable {
	private static final long serialVersionUID = -2222382615427605229L;

	private String finReference;
	private String effectiveFinReference;
	private String classCode;
	private String classDesc;
	private String subClassCode;
	private String subClassDesc;
	private int pastDueDays;
	private Date pastDueDate;
	private int npaPastDueDays;
	private Date npaPastDueDate;
	private boolean npaStage;
	private int type;

	public NPAStageDetail() {
		super();
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getEffectiveFinReference() {
		return effectiveFinReference;
	}

	public void setEffectiveFinReference(String effectiveFinReference) {
		this.effectiveFinReference = effectiveFinReference;
	}

	public String getClassCode() {
		return classCode;
	}

	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}

	public String getClassDesc() {
		return classDesc;
	}

	public void setClassDesc(String classDesc) {
		this.classDesc = classDesc;
	}

	public String getSubClassCode() {
		return subClassCode;
	}

	public void setSubClassCode(String subClassCode) {
		this.subClassCode = subClassCode;
	}

	public String getSubClassDesc() {
		return subClassDesc;
	}

	public void setSubClassDesc(String subClassDesc) {
		this.subClassDesc = subClassDesc;
	}

	public int getPastDueDays() {
		return pastDueDays;
	}

	public void setPastDueDays(int pastDueDays) {
		this.pastDueDays = pastDueDays;
	}

	public Date getPastDueDate() {
		return pastDueDate;
	}

	public void setPastDueDate(Date pastDueDate) {
		this.pastDueDate = pastDueDate;
	}

	public int getNpaPastDueDays() {
		return npaPastDueDays;
	}

	public void setNpaPastDueDays(int npaPastDueDays) {
		this.npaPastDueDays = npaPastDueDays;
	}

	public Date getNpaPastDueDate() {
		return npaPastDueDate;
	}

	public void setNpaPastDueDate(Date npaPastDueDate) {
		this.npaPastDueDate = npaPastDueDate;
	}

	public boolean isNpaStage() {
		return npaStage;
	}

	public void setNpaStage(boolean npaStage) {
		this.npaStage = npaStage;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
