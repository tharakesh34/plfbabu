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
package com.pennanttech.pennapps.core.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * AbstractEntity class. Any class that should be uniquely identifiable from another should subclass from
 * AbstractEntity.
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractEntity implements Serializable {
	private static final long serialVersionUID = 8987922026116401165L;

	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;

	protected AbstractEntity() {
		super();
	}

	public final int getVersion() {
		return version;
	}

	public final void setVersion(int version) {
		this.version = version;
	}

	public final long getLastMntBy() {
		return lastMntBy;
	}

	public final void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public final Timestamp getLastMntOn() {
		return lastMntOn == null ? null : (Timestamp) lastMntOn.clone();
	}

	public final void setLastMntOn(Timestamp lastMntOn) {
		this.lastMntOn = lastMntOn == null ? null : (Timestamp) lastMntOn.clone();
	}
}
