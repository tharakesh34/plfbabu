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

package com.pennant.interfaceservice.model;

import java.sql.Timestamp;

/**
 * Model class for the <b>CustomerRating table</b>.<br>
 * 
 */
public class InterfaceCustomerRating {

	private long custID = Long.MIN_VALUE;
	private String custRatingType;
	private String custRatingCode;
	private String custRating;
	private boolean valueType;
	private long lastMntBy;
	private Timestamp lastMntOn;

	

	public InterfaceCustomerRating() {
	}



	public long getCustID() {
		return custID;
	}



	public void setCustID(long custID) {
		this.custID = custID;
	}



	public String getCustRatingType() {
		return custRatingType;
	}



	public void setCustRatingType(String custRatingType) {
		this.custRatingType = custRatingType;
	}



	public String getCustRatingCode() {
		return custRatingCode;
	}



	public void setCustRatingCode(String custRatingCode) {
		this.custRatingCode = custRatingCode;
	}



	public String getCustRating() {
		return custRating;
	}



	public void setCustRating(String custRating) {
		this.custRating = custRating;
	}



	public boolean isValueType() {
		return valueType;
	}



	public void setValueType(boolean valueType) {
		this.valueType = valueType;
	}



	public long getLastMntBy() {
		return lastMntBy;
	}



	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}



	public Timestamp getLastMntOn() {
		return lastMntOn;
	}



	public void setLastMntOn(Timestamp lastMntOn) {
		this.lastMntOn = lastMntOn;
	}

	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	
}
