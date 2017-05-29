package com.pennant.backend.model.rulefactory;

import java.io.Serializable;
import java.math.BigDecimal;

public class NFScoreRuleDetail implements Serializable {

	private static final long serialVersionUID = 7187955896188248418L; 

	private long groupId;
	private long nFRuleId;
	private String  nFRuleDesc;
	private BigDecimal maxScore;

	public NFScoreRuleDetail() {
		super();
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
	
	public long getNFRuleId() {
		return nFRuleId;
	}
	public void setNFRuleId(long nFRuleId) {
		this.nFRuleId = nFRuleId;
	}
	
	public String getNFRuleDesc() {
		return nFRuleDesc;
	}
	public void setNFRuleDesc(String nFRuleDesc) {
		this.nFRuleDesc = nFRuleDesc;
	}
	public BigDecimal getMaxScore() {
		return maxScore;
	}
	public void setMaxScore(BigDecimal maxScore) {
		this.maxScore = maxScore;
	}

}
