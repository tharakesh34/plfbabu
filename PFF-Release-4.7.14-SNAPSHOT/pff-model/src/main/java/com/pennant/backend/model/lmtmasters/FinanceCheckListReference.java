/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FinanceCheckListReference.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-12-2011    														*
 *                                                                  						*
 * Modified Date    :  08-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-12-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.backend.model.lmtmasters;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FinanceCheckListReference table</b>.<br>
 *
 */
public class FinanceCheckListReference extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	private String finReference = null;
	private long questionId;
	private long answer;
	private String remarks;
	private boolean newRecord=false;
	private String lovValue;
	private FinanceCheckListReference befImage;
	private LoggedInUser userDetails;

	private Map<Long, Long>  lovDescSelAnsCountMap  =new HashMap<Long, Long>();
	private String lovDescQuesDesc;
	private String lovDescAnswerDesc;
	private long  lovDescMaxAnsCount;
	private long  lovDescMinAnsCount;
	public String getLovDescQuesDesc() {
		return lovDescQuesDesc;
	}

	public void setLovDescQuesDesc(String lovDescQuesDesc) {
		this.lovDescQuesDesc = lovDescQuesDesc;
	}


	public boolean isNew() {
		return isNewRecord();
	}

	public FinanceCheckListReference() {
		super();
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceCheckListReference(String id) {
		super();
		this.setId(id);
	}

	public String getId() {
		return finReference;
	}
	
	public void setId (String id) {
		this.finReference = id;
	}
	
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	
	
		
	
	public long getQuestionId() {
		return questionId;
	}
	public void setQuestionId(long questionId) {
		this.questionId = questionId;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public FinanceCheckListReference getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(FinanceCheckListReference beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setAnswer(long answer) {
		this.answer = answer;
	}

	public long getAnswer() {
		return answer;
	}
	
	public void setLovDescMaxAnsCount(long lovDescMaxAnsCount) {
		this.lovDescMaxAnsCount = lovDescMaxAnsCount;
	}

	public long getLovDescMaxAnsCount() {
		return lovDescMaxAnsCount;
	}

	public void setLovDescMinAnsCount(long lovDescMinAnsCount) {
		this.lovDescMinAnsCount = lovDescMinAnsCount;
	}

	public long getLovDescMinAnsCount() {
		return lovDescMinAnsCount;
	}

	public void setLovDescSelAnsCountMap(Map<Long, Long> lovDescSelAnsCountMap) {
		this.lovDescSelAnsCountMap = lovDescSelAnsCountMap;
	}

	public Map<Long, Long> getLovDescSelAnsCountMap() {
		return lovDescSelAnsCountMap;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setLovDescAnswerDesc(String lovDescAnswerDesc) {
		this.lovDescAnswerDesc = lovDescAnswerDesc;
	}

	public String getLovDescAnswerDesc() {
		return lovDescAnswerDesc;
	}
}
