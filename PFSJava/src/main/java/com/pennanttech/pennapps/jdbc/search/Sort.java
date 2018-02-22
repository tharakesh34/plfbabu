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
package com.pennanttech.pennapps.jdbc.search;

import java.io.Serializable;

/**
 * Used to specify field ordering in <code>Search</code>.
 * 
 * @see Search
 */
public class Sort implements Serializable {
	private static final long serialVersionUID = 1L;

	private String property;
	private boolean desc = false;

	public Sort() {
		super();
	}

	public Sort(String property, boolean desc) {
		this.property = property;
		this.desc = desc;
	}

	public Sort(String property) {
		this.property = property;
	}

	public static Sort asc(String property) {
		return new Sort(property);
	}

	public static Sort desc(String property) {
		return new Sort(property, true);
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public boolean isDesc() {
		return desc;
	}

	public void setDesc(boolean desc) {
		this.desc = desc;
	}

	/**
	 * Get the hashCode
	 * 
	 * @return int
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((property == null) ? 0 : property.hashCode());
		result = prime * result + (desc ? 1231 : 1237);
		return result;
	}

	/**
	 * Check object is equal or not with Other object
	 * 
	 * @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Sort other = (Sort) obj;
		if (desc != other.desc) {
			return false;
		}
		if (property == null) {
			if (other.property != null) {
				return false;
			}
		} else if (!property.equals(other.property)) {
			return false;
		}
		return true;
	}

	/**
	 * Get the String return type Value
	 * 
	 * @return String
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (property == null) {
			sb.append("null");
		} else {
			sb.append("`");
			sb.append(property);
			sb.append("`");
		}
		sb.append(desc ? " desc" : " asc");

		return sb.toString();
	}
}
