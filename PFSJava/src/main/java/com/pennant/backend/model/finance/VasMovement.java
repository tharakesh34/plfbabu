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
 * FileName    		:  VasMovement.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-12-2011    														*
 *                                                                  						*
 * Modified Date    :  12-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>VasMovement table</b>.<br>
 *
 */
public class VasMovement extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = -3060817228345423733L;

	private long vasMovementId = 0;
	private String finReference;
	private BigDecimal finAmount;
	private Date finStartdate;
	private Date maturityDate;
	private String custCif;
	private String finBranch;
	private int numberOfTerms;
	private String finCcy;
	private String finType;
	private boolean newRecord;
	private String lovValue;
	private VasMovement befImage;
	private LoggedInUser userDetails;
	private List<VasMovementDetail> vasMvntList = new ArrayList<VasMovementDetail>();
	private HashMap<String, List<AuditDetail>> lovDescAuditDetailMap = new HashMap<String, List<AuditDetail>>();

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();

		excludeFields.add("finAmount");
		excludeFields.add("finStartdate");
		excludeFields.add("custCif");
		excludeFields.add("maturityDate");
		excludeFields.add("finType");
		excludeFields.add("finCcy");
		excludeFields.add("numberOfTerms");
		excludeFields.add("finBranch");
		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public VasMovement() {
		super();
	}

	public VasMovement(long id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public VasMovement getBefImage() {
		return this.befImage;
	}

	public void setBefImage(VasMovement beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public HashMap<String, List<AuditDetail>> getLovDescAuditDetailMap() {
		return lovDescAuditDetailMap;
	}

	public void setLovDescAuditDetailMap(HashMap<String, List<AuditDetail>> lovDescAuditDetailMap) {
		this.lovDescAuditDetailMap = lovDescAuditDetailMap;
	}

	public List<VasMovementDetail> getVasMvntList() {
		return vasMvntList;
	}

	public void setVasMvntList(List<VasMovementDetail> vasMvntList) {
		this.vasMvntList = vasMvntList;
	}

	public long getVasMovementId() {
		return vasMovementId;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public int getNumberOfTerms() {
		return numberOfTerms;
	}

	public void setNumberOfTerms(int numberOfTerms) {
		this.numberOfTerms = numberOfTerms;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public void setVasMovementId(long vasMovementId) {
		this.vasMovementId = vasMovementId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	@Override
	public long getId() {
		return vasMovementId;
	}

	public BigDecimal getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}

	public Date getFinStartdate() {
		return finStartdate;
	}

	public void setFinStartdate(Date finStartdate) {
		this.finStartdate = finStartdate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	@Override
	public void setId(long vasMovementId) {
		this.vasMovementId = vasMovementId;
	}
}
