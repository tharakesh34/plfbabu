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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;

/**
 * A <code>Filter</code> is used by the <code>Search</code> class to specify a
 * restriction on what results should be returned in the search.
 */
public class Filter implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int OP_EQUAL = 0;
	public static final int OP_NOT_EQUAL = 1;
	public static final int OP_LESS_THAN = 2;
	public static final int OP_GREATER_THAN = 3;
	public static final int OP_LESS_OR_EQUAL = 4;
	public static final int OP_GREATER_OR_EQUAL = 5;
	public static final int OP_LIKE = 6;
	public static final int OP_NULL = 10;
	public static final int OP_NOT_NULL = 11;
	public static final int OP_IN = 8;
	public static final int OP_NOT_IN = 9;
	public static final int OP_AND = 100;
	public static final int OP_OR = 101;
	public static final int OP_BETWEEN = 300; // Not implemented.

	/**
	 * The name of the property to filter on.
	 */
	private String property;

	/**
	 * The value to compare the property with. Should be of a compatible type
	 * with the property.
	 */
	private Object value;

	/**
	 * The type of comparison to do between the property and the value.<br/>
	 * Operators:
	 * <code>OP_EQUAL, OP_NOT_EQUAL, OP_LESS_THAN, OP_GREATER_THAN, OP_LESS_OR_EQUAL, OP_GREATER_OR_EQUAL, OP_LIKE,
	 * OP_IN, OP_NOT_IN, OP_NULL, OP_NOT_NULL, OP_AND, OP_OR, OP_NOT</code>
	 */
	private int operator;

	protected Filter() {
		super();
	}

	public Filter(String property, Object value, int operator) {
		this.property = property;
		this.value = value;
		this.operator = operator;

		if (App.DATABASE == Database.ORACLE && (value == null || ("").equals(value))) {
			this.value = true;
			if (operator == OP_EQUAL) {
				this.operator = OP_NULL;
			} else if (operator == OP_NOT_EQUAL) {
				this.operator = OP_NOT_NULL;
			}
		}
	}

	public Filter(String property, Object value) {
		this.property = property;
		this.value = value;
		this.operator = OP_EQUAL;
	}

	/**
	 * Convenience method for generating a <code>Filter</code> for checking if a
	 * property is equal to the value.
	 * 
	 * @param property
	 *            The column to check.
	 * @param value
	 *            The value to compare with.
	 * @return The <code>Filter</code> for the property.
	 */
	public static Filter equalTo(String property, Object value) {
		return new Filter(property, value, OP_EQUAL);
	}

	/**
	 * Create a new Filter using the < operator.
	 */
	public static Filter lessThan(String property, Object value) {
		return new Filter(property, value, OP_LESS_THAN);
	}

	/**
	 * Create a new Filter using the > operator.
	 */
	public static Filter greaterThan(String property, Object value) {
		return new Filter(property, value, OP_GREATER_THAN);
	}

	/**
	 * Create a new Filter using the <= operator.
	 */
	public static Filter lessOrEqual(String property, Object value) {
		return new Filter(property, value, OP_LESS_OR_EQUAL);
	}

	/**
	 * Create a new Filter using the >= operator.
	 */
	public static Filter greaterOrEqual(String property, Object value) {
		return new Filter(property, value, OP_GREATER_OR_EQUAL);
	}

	/**
	 * Create a new Filter using the IN operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of values can be
	 * specified.
	 */
	public static Filter in(String property, Collection<?> value) {
		return new Filter(property, value, OP_IN);
	}

	/**
	 * Create a new Filter using the IN operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of values can be
	 * specified.
	 */
	public static Filter in(String property, Object... value) {
		return new Filter(property, value, OP_IN);
	}

	/**
	 * Create a new Filter using the NOT IN operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of values can be
	 * specified.
	 */
	public static Filter notIn(String property, Collection<?> value) {
		return new Filter(property, value, OP_NOT_IN);
	}

	/**
	 * Create a new Filter using the NOT IN operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of values can be
	 * specified.
	 */
	public static Filter notIn(String property, Object... value) {
		return new Filter(property, value, OP_NOT_IN);
	}

	/**
	 * Create a new Filter using the LIKE operator.
	 */
	public static Filter like(String property, String value) {
		return new Filter(property, value, OP_LIKE);
	}

	/**
	 * Create a new Filter using the != operator.
	 */
	public static Filter notEqual(String property, Object value) {
		return new Filter(property, value, OP_NOT_EQUAL);
	}

	/**
	 * Create a new Filter using the IS NULL operator.
	 */
	public static Filter isNull(String property) {
		return new Filter(property, true, OP_NULL);
	}

	/**
	 * Create a new Filter using the IS NOT NULL operator.
	 */
	public static Filter isNotNull(String property) {
		return new Filter(property, true, OP_NOT_NULL);
	}

	/**
	 * Create a new Filter using the AND operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of
	 * <code>Filter</code>s can be specified.
	 */
	public static Filter and(Filter... filters) {
		Filter filter = new Filter("AND", null, OP_AND);
		for (Filter f : filters) {
			filter.add(f);
		}
		return filter;
	}

	/**
	 * Create a new Filter using the OR operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of
	 * <code>Filter</code>s can be specified.
	 */
	public static Filter or(Filter... filters) {
		Filter filter = and(filters);
		filter.property = "OR";
		filter.operator = OP_OR;
		return filter;
	}

	/**
	 * Used with OP_OR and OP_AND filters. These filters take a collection of
	 * filters as their value. This method adds a filter to that list.
	 */
	@SuppressWarnings("unchecked")
	public void add(Filter filter) {
		if (!(value instanceof List)) {
			value = new ArrayList<Filter>();
		}
		((List<Filter>) value).add(filter);
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getOperator() {
		return operator;
	}

	public void setOperator(int operator) {
		this.operator = operator;
	}
}
