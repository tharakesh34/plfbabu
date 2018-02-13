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
 * FileName    		:  ProvisionMovement.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.financemanagement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>ProvisionMovement table</b>.<br>
 *
 */
public class ProvisionMovement extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	private String finReference = null;
	private Date provMovementDate;
	private int provMovementSeq;
	private Date provCalDate;
	private BigDecimal provisionedAmt;
	private BigDecimal provisionAmtCal;
	private BigDecimal provisionDue;
	private String provisionPostSts;
	private BigDecimal nonFormulaProv;
	private boolean useNFProv;
	private boolean autoReleaseNFP;
	private BigDecimal principalDue;
	private BigDecimal profitDue;
	private Date dueFromDate;
	private Date lastFullyPaidDate;
	private long linkedTranId;
	private boolean newRecord=false;
	private String lovValue;
	private ProvisionMovement befImage;
	private LoggedInUser userDetails;
	
	private List<ReturnDataSet> postingsList = new ArrayList<ReturnDataSet>();

	public boolean isNew() {
		return isNewRecord();
	}

	public ProvisionMovement() {
		super();
	}

	public ProvisionMovement(String id) {
		super();
		this.setId(id);
	}

	//Getter and Setter methods
	
	public String getId() {
		return this.finReference;
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
	
	
		
	
	public Date getProvMovementDate() {
		return provMovementDate;
	}
	public void setProvMovementDate(Date provMovementDate) {
		this.provMovementDate = provMovementDate;
	}
	
	
		
	
	public int getProvMovementSeq() {
		return provMovementSeq;
	}
	public void setProvMovementSeq(int provMovementSeq) {
		this.provMovementSeq = provMovementSeq;
	}
	
	
		
	
	public Date getProvCalDate() {
		return provCalDate;
	}
	public void setProvCalDate(Date provCalDate) {
		this.provCalDate = provCalDate;
	}
	
	
		
	
	public BigDecimal getProvisionedAmt() {
		return provisionedAmt;
	}
	public void setProvisionedAmt(BigDecimal provisionedAmt) {
		this.provisionedAmt = provisionedAmt;
	}
	
	
		
	
	public BigDecimal getProvisionAmtCal() {
		return provisionAmtCal;
	}
	public void setProvisionAmtCal(BigDecimal provisionAmtCal) {
		this.provisionAmtCal = provisionAmtCal;
	}
	
	
		
	
	public BigDecimal getProvisionDue() {
		return provisionDue;
	}
	public void setProvisionDue(BigDecimal provisionDue) {
		this.provisionDue = provisionDue;
	}
	
	
		
	
	public String getProvisionPostSts() {
		return provisionPostSts;
	}
	public void setProvisionPostSts(String provisionPostSts) {
		this.provisionPostSts = provisionPostSts;
	}
	
	
		
	
	public BigDecimal getNonFormulaProv() {
		return nonFormulaProv;
	}
	public void setNonFormulaProv(BigDecimal nonFormulaProv) {
		this.nonFormulaProv = nonFormulaProv;
	}
	
	
		
	
	public boolean isUseNFProv() {
		return useNFProv;
	}
	public void setUseNFProv(boolean useNFProv) {
		this.useNFProv = useNFProv;
	}
	
	
		
	
	public boolean isAutoReleaseNFP() {
		return autoReleaseNFP;
	}
	public void setAutoReleaseNFP(boolean autoReleaseNFP) {
		this.autoReleaseNFP = autoReleaseNFP;
	}
	
	
		
	
	public BigDecimal getPrincipalDue() {
		return principalDue;
	}
	public void setPrincipalDue(BigDecimal principalDue) {
		this.principalDue = principalDue;
	}
	
	
		
	
	public BigDecimal getProfitDue() {
		return profitDue;
	}
	public void setProfitDue(BigDecimal profitDue) {
		this.profitDue = profitDue;
	}
	
	
		
	
	public Date getDueFromDate() {
		return dueFromDate;
	}
	public void setDueFromDate(Date dueFromDate) {
		this.dueFromDate = dueFromDate;
	}
	
	
		
	
	public Date getLastFullyPaidDate() {
		return lastFullyPaidDate;
	}
	public void setLastFullyPaidDate(Date lastFullyPaidDate) {
		this.lastFullyPaidDate = lastFullyPaidDate;
	}
	
	
		
	
	public long getLinkedTranId() {
		return linkedTranId;
	}
	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
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

	public ProvisionMovement getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(ProvisionMovement beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setPostingsList(List<ReturnDataSet> postingsList) {
		this.postingsList = postingsList;
	}

	public List<ReturnDataSet> getPostingsList() {
		return postingsList;
	}

}
