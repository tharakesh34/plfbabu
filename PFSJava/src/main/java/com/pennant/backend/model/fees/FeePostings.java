/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * * FileName : JVPosting.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-06-2013 * * Modified Date :
 * 21-06-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 21-06-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.model.fees;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>JVPosting table</b>.<br>
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
public class FeePostings extends AbstractWorkflowEntity implements Entity {
	private static final long	serialVersionUID	= 1L;

	private long				postId =Long.MIN_VALUE;
	private String				postAgainst;
	private String				reference;
	@XmlElement(name= "feeCode")
	private String				feeTyeCode;
	@XmlElement(name = "amount")
	private BigDecimal			postingAmount;
	private Date				postDate;
	@XmlElement
	private Date				valueDate;
	private boolean				newRecord			= false;
	private String				lovValue;
	private FeePostings			befImage;
	private LoggedInUser		userDetails;
	@XmlElement(name="ccy")
	private String				currency;
	@XmlElement
	private String				remarks;
	@XmlElement
	private long 				partnerBankId;
	private String                 accountSetId;
	private List<ReturnDataSet> 				returnDataSetList 	= new ArrayList<ReturnDataSet>(1);
	private String				partnerBankName;
	private String				partnerBankAc;
    private String 			    partnerBankAcType;
    private String				postingDivision;
    private String				divisionCodeDesc;
    
    //API Specific
    @XmlElement
    private String cif;
    @XmlElement
	private long limitId = Long.MIN_VALUE;
    @XmlElement
	private String finReference;
    @XmlElement
    private String collateralRef;
    private String sourceId;
	// API validation purpose only
	@SuppressWarnings("unused")
	private FeePostings validateFeePostings = this;
	public boolean isNew() {
		return isNewRecord();
	}

	public FeePostings() {
		super();
	}

	public FeePostings(String id) {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("partnerBankName");
		excludeFields.add("partnerBankAc");
		excludeFields.add("accountSetId");
		excludeFields.add("currency");
		excludeFields.add("partnerBankAcType");
		excludeFields.add("cif");
		excludeFields.add("limitId");
		excludeFields.add("finReference");
		excludeFields.add("sourceId");
		excludeFields.add("validateFeePostings");
		excludeFields.add("collateralRef");
		excludeFields.add("divisionCodeDesc");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	
	public HashMap<String, Object> getDeclaredFieldValues(HashMap<String, Object> feePostingMap) {
		
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				feePostingMap.put("fp_" + this.getClass().getDeclaredFields()[i].getName(), this.getClass().getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				
			}
		}
		return feePostingMap;
	}
	public long getId() {
		return postId;
	}

	@Override
	public void setId(long id) {
		this.postId = id;
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

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getPostAgainst() {
		return postAgainst;
	}

	public void setPostAgainst(String postAgainst) {
		this.postAgainst = postAgainst;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public FeePostings getBefImage() {
		return befImage;
	}

	public void setBefImage(FeePostings befImage) {
		this.befImage = befImage;
	}

	public long getPostId() {
		return postId;
	}

	public void setPostId(long postId) {
		this.postId = postId;
	}

	public String getFeeTyeCode() {
		return feeTyeCode;
	}

	public void setFeeTyeCode(String feeTyeCode) {
		this.feeTyeCode = feeTyeCode;
	}

	public BigDecimal getPostingAmount() {
		return postingAmount;
	}

	public void setPostingAmount(BigDecimal postingAmount) {
		this.postingAmount = postingAmount;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public long getPartnerBankId() {
		return partnerBankId;
	}

	public void setPartnerBankId(long partnerBankId) {
		this.partnerBankId = partnerBankId;
	}


	public String getAccountSetId() {
		return accountSetId;
	}

	public void setAccountSetId(String accountSetId) {
		this.accountSetId = accountSetId;
	}

	public List<ReturnDataSet> getReturnDataSetList() {
		return returnDataSetList;
	}

	public void setReturnDataSetList(List<ReturnDataSet> returnDataSetList) {
		this.returnDataSetList = returnDataSetList;
	}

	public String getPartnerBankName() {
		return partnerBankName;
	}

	public void setPartnerBankName(String partnerBankName) {
		this.partnerBankName = partnerBankName;
	}

	public String getPartnerBankAc() {
		return partnerBankAc;
	}

	public void setPartnerBankAc(String partnerBankAc) {
		this.partnerBankAc = partnerBankAc;
	}

	public String getPartnerBankAcType() {
		return partnerBankAcType;
	}

	public void setPartnerBankAcType(String partnerBankAcType) {
		this.partnerBankAcType = partnerBankAcType;
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public long getLimitId() {
		return limitId;
	}

	public void setLimitId(long limitId) {
		this.limitId = limitId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getCollateralRef() {
		return collateralRef;
	}

	public void setCollateralRef(String collateralRef) {
		this.collateralRef = collateralRef;
	}

	public String getPostingDivision() {
		return postingDivision;
	}

	public void setPostingDivision(String postingDivision) {
		this.postingDivision = postingDivision;
	}

	public String getDivisionCodeDesc() {
		return divisionCodeDesc;
	}

	public void setDivisionCodeDesc(String divisionCodeDesc) {
		this.divisionCodeDesc = divisionCodeDesc;
	}

}
