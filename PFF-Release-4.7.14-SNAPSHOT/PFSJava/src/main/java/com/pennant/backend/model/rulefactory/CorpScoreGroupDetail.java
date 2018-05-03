package com.pennant.backend.model.rulefactory;

import java.io.Serializable;

public class CorpScoreGroupDetail implements Serializable {

    private static final long serialVersionUID = 6819187970322755690L;
    
    private long groupId= Long.MIN_VALUE;
    private String groupDesc;
    private int groupSeq;
    private String categoryType;
    private String custCategory;
    
    public CorpScoreGroupDetail() {
	    super();
    }
    
    public CorpScoreGroupDetail(long groupId) {
	    this.groupId = groupId;
    }
    
 // ******************************************************//
 // ****************** getter / setter *******************//
 // ******************************************************//
	
	public long getGroupId() {
    	return groupId;
    }
	public void setGroupId(long groupId) {
    	this.groupId = groupId;
    }
	
	public String getGroupDesc() {
    	return groupDesc;
    }
	public void setGroupDesc(String groupDesc) {
    	this.groupDesc = groupDesc;
    }
	
	public int getGroupSeq() {
    	return groupSeq;
    }
	public void setGroupSeq(int groupSeq) {
    	this.groupSeq = groupSeq;
    }
	
	public String getCategoryType() {
    	return categoryType;
    }
	public void setCategoryType(String categoryType) {
    	this.categoryType = categoryType;
    }

	public void setCustCategory(String custCategory) {
	    this.custCategory = custCategory;
    }
	public String getCustCategory() {
	    return custCategory;
    }
    
}
