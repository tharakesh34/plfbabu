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
 * * FileName : Collateral.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-12-2013 * * Modified Date :
 * 04-12-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 04-12-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.model.collateral;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Collateral table</b>.<br>
 * 
 */
public class Collateral extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;
	
	private String cAFReference = null;
	private String reference;
	private String lastReview;
	private String currency;
	private String lovDescCurrencyName;
	private BigDecimal value;
	private BigDecimal bankvaluation;
	private BigDecimal bankmargin;
	private BigDecimal actualCoverage;
	private BigDecimal proposedCoverage;
	private String description;
	private boolean newRecord = false;
	private String lovValue;
	private Collateral befImage;
	private LoggedInUser userDetails;

	private int ccyFormat;

	private long CustID;

	public boolean isNew() {
		return isNewRecord();
	}

	public Collateral() {
		super();
	}

	public Collateral(String id) {
		super();
		cAFReference = id;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("ccyFormat");

		return excludeFields;
	}

	//Getter and Setter methods

	public long getId() {
		return Long.parseLong(reference);
	}

	public void setId(long id) {
		this.reference = String.valueOf(id);
	}

	public String getCAFReference() {
		return cAFReference;
	}

	public void setCAFReference(String cAFReference) {
		this.cAFReference = cAFReference;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getLastReview() {
		return lastReview;
	}

	public void setLastReview(String lastReview) {
		this.lastReview = lastReview;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getLovDescCurrencyName() {
		return this.lovDescCurrencyName;
	}

	public void setLovDescCurrencyName(String lovDescCurrencyName) {
		this.lovDescCurrencyName = lovDescCurrencyName;
	}

	public BigDecimal getValue() {
		if (value != null) {
	        return value;
        }
        return BigDecimal.ZERO;
		
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public BigDecimal getBankvaluation() {
		if (bankvaluation != null) {
			return bankvaluation;
		}
		return BigDecimal.ZERO;
	}

	public void setBankvaluation(BigDecimal bankvaluation) {
		this.bankvaluation = bankvaluation;
	}

	public BigDecimal getBankmargin() {
		if (bankmargin != null) {
	        return bankmargin;
        }
        return BigDecimal.ZERO;
	}

	public void setBankmargin(BigDecimal bankmargin) {
		this.bankmargin = bankmargin;
	}

	public BigDecimal getActualCoverage() {
		if (actualCoverage != null) {
	        return actualCoverage;
        }
        return BigDecimal.ZERO;
	}

	public void setActualCoverage(BigDecimal actualCoverage) {
		this.actualCoverage = actualCoverage;
	}

	public BigDecimal getProposedCoverage() {
		if (proposedCoverage != null) {
	        return proposedCoverage;
        }
        return BigDecimal.ZERO;
	}

	public void setProposedCoverage(BigDecimal proposedCoverage) {
		this.proposedCoverage = proposedCoverage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Collateral getBefImage() {
		return this.befImage;
	}

	public void setBefImage(Collateral beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public long getCustID() {
		return CustID;
	}

	public void setCustID(long custID) {
		CustID = custID;
	}

	public void setCcyFormat(int ccyFormat) {
		this.ccyFormat = ccyFormat;
	}

	public int getCcyFormat() {
		return ccyFormat;
	}
}
