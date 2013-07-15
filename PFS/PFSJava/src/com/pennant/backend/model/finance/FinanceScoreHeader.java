package com.pennant.backend.model.finance;

import java.io.Serializable;

import com.pennant.backend.model.Entity;

public class FinanceScoreHeader implements Serializable,Entity{

	private static final long serialVersionUID = -453344106804424915L;

	private long headerId = Long.MIN_VALUE;
	private String finReference;
	private long groupId = Long.MIN_VALUE;
	private String groupCode;
	private String groupCodeDesc;
	private int minScore;
	private boolean override;
	private int overrideScore;
	private String creditWorth;
	
	@Override
    public boolean isNew() {
	    return false;
    }
	@Override
    public long getId() {
	    return headerId;
    }
	@Override
    public void setId(long id) {
	    this.headerId = id;	    
    }

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public long getHeaderId() {
    	return headerId;
    }
	public void setHeaderId(long headerId) {
    	this.headerId = headerId;
    }
	
	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }
	
	public long getGroupId() {
    	return groupId;
    }
	public void setGroupId(long groupId) {
    	this.groupId = groupId;
    }
	
	public String getGroupCode() {
    	return groupCode;
    }
	public void setGroupCode(String groupCode) {
    	this.groupCode = groupCode;
    }
	
	public String getGroupCodeDesc() {
    	return groupCodeDesc;
    }
	public void setGroupCodeDesc(String groupCodeDesc) {
    	this.groupCodeDesc = groupCodeDesc;
    }
	
	public int getMinScore() {
    	return minScore;
    }
	public void setMinScore(int minScore) {
    	this.minScore = minScore;
    }
	
	public boolean isOverride() {
    	return override;
    }
	public void setOverride(boolean override) {
    	this.override = override;
    }
	
	public int getOverrideScore() {
    	return overrideScore;
    }
	public void setOverrideScore(int overrideScore) {
    	this.overrideScore = overrideScore;
    }
	
	public String getCreditWorth() {
    	return creditWorth;
    }
	public void setCreditWorth(String creditWorth) {
    	this.creditWorth = creditWorth;
    }

}
