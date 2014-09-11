/**
 * Copyright 2009 The Revere Group
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pennant.search;

import java.io.Serializable;

/**
 * Used to specify field selection in <code>Search</code>.
 * 
 * @see Search
 */
public class Field implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Property string representing the root entity of the search. This is just the empty string ("").
	 */
	public static final String ROOT_ENTITY = "";

	/**
	 * The property to include in the result.
	 */
	protected String property;

	/**
	 * The key to use for the property when using result mode
	 * <code>RESULT_MAP</code>.
	 */
	protected String key;

	/**
	 * The operator to apply to the column: for example
	 * <code>OP_COUNT, OP_SUM, OP_MAX</code>. The default is
	 * <code>OP_PROPERTY</code>.
	 */
	protected int operator = 0;

	/**
	 * Possible value for <code>operator</code>. This is the default value
	 * and does not apply any operator to the column. All the rows in the result
	 * set are returned.
	 */
	public static final int OP_PROPERTY = 0;

	/**
	 * Possible value for <code>operator</code>. This returns the number of
	 * rows in the result set where the given property is non-null.
	 */
	public static final int OP_COUNT = 1;

	/**
	 * Possible value for <code>operator</code>. This returns the number of
	 * distinct values of the given property in the result set.
	 */
	public static final int OP_COUNT_DISTINCT = 2;

	/**
	 * Possible value for <code>operator</code>. This returns the maximum
	 * value of the given property in the result set.
	 */
	public static final int OP_MAX = 3;

	/**
	 * Possible value for <code>operator</code>. This returns the minimum
	 * value of the given property in the result set.
	 */
	public static final int OP_MIN = 4;

	/**
	 * Possible value for <code>operator</code>. This returns the sum of the
	 * given property in all rows of the result set.
	 */
	public static final int OP_SUM = 5;

	/**
	 * Possible value for <code>operator</code>. This returns the average
	 * value of the given property in the result set.
	 */
	public static final int OP_AVG = 6;

	public Field() {
	}

	public Field(String property) {
		this.property = property;
	}

	public Field(String property, String key) {
		this.property = property;
		this.key = key;
	}

	public Field(String property, int operator) {
		this.property = property;
		this.operator = operator;
	}

	public Field(String property, int operator, String key) {
		this.property = property;
		this.operator = operator;
		this.key = key;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getOperator() {
		return operator;
	}

	public void setOperator(int operator) {
		this.operator = operator;
	}
	
	/**
	 * Get the hashCode
	 * 
	 *  @return int
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + operator;
		result = prime * result + ((property == null) ? 0 : property.hashCode());
		return result;
	}

	/**
	 * Check object is equal or not with Other object
	 * 
	 *  @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Field other = (Field) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (operator != other.operator)
			return false;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		return true;
	}

	/**
	 * Get the String return type Value
	 * 
	 *  @return String
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		boolean parens = true;
		switch (operator) {
		case OP_AVG: sb.append("AVG(");
		             break;
		case OP_COUNT: sb.append("COUNT(");
		  			 break;
		case OP_COUNT_DISTINCT: sb.append("COUNT_DISTINCT(");
				     break;
		case OP_MAX: sb.append("MAX(");
					 break;
		case OP_MIN: sb.append("MIN(");
					 break;
		case OP_PROPERTY: parens = false; 
					 break;
		case OP_SUM: sb.append("SUM(");
					 break;
		  default: 	 sb.append("**INVALID OPERATOR: (" + operator + ")** "); 
				     parens = false;
				     break;
		}

		if (property == null) {
			sb.append("null");
		} else {
			sb.append("`");
			sb.append(property);
			sb.append("`");
		}
		if (parens)
			sb.append(")");

		if (key != null) {
			sb.append(" as `");
			sb.append(key);
			sb.append("`");
		}
		return sb.toString();
	}
}
