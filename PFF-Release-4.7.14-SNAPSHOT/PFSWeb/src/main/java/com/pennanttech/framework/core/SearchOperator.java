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
package com.pennanttech.framework.core;

import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.ValueLabel;
import com.pennanttech.pennapps.jdbc.search.Filter;

/**
 * <p>
 * This class represents a few types of search operators to show in dropdown list for the corresponding search
 * component.
 * </p>
 * <table>
 * <th>Operator Code|</th>
 * <th>Operator Sign|</th>
 * <th>Operator Description</th>
 * <tr>
 * <td>0</td>
 * <td>=</td>
 * <td>equals</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td><></td>
 * <td>not equal</td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td><</td>
 * <td>less than</td>
 * </tr>
 * * </tr>
 * <tr>
 * <td>4</td>
 * <td><=</td>
 * <td>less or equal</td>
 * </tr>
 * <tr>
 * <td>3</td>
 * <td>></td>
 * <td>greater than</td>
 * <tr>
 * <td>5</td>
 * <td>>=</td>
 * <td>greater or equal</td>
 * </tr>
 * <tr>
 * <td>7</td>
 * <td>%</td>
 * <td>like</td>
 * </tr>
 * * </tr>
 * <tr>
 * <td>7</td>
 * <td>%</td>
 * <td>like</td>
 * </tr>
 * </tr>
 * <tr>
 * <td>8</td>
 * <td>IN</td>
 * <td>IN</td>
 * </tr>
 * <tr>
 * <td>9</td>
 * <td>NOT IN</td>
 * <td>NOT IN</td>
 * </tr>
 * </table>
 * */
public class SearchOperator {

	public enum Operators {
		/**
		 * Filter.OP_EQUAL
		 * 
		 */
		DEFAULT,
		/**
		 * Filter.OP_EQUAL, Filter.OP_NOT_EQUAL
		 */
		SIMPLE,
		/**
		 * Filter.OP_EQUAL, Filter.OP_NOT_EQUAL, Filter.OP_LIKE
		 */
		STRING,
		/**
		 * Filter.OP_EQUAL, Filter.OP_NOT_EQUAL
		 */
		SIMPLE_NUMARIC,
		/**
		 * Filter.OP_EQUAL, Filter.OP_NOT_EQUAL
		 */
		BOOLEAN,
		/**
		 * Filter.OP_EQUAL, Filter.OP_NOT_EQUAL, Filter.OP_LESS_THAN, Filter.OP_LESS_OR_EQUAL, Filter.OP_GREATER_THAN,
		 * Filter.OP_GREATER_OR_EQUAL
		 */
		NUMERIC,
		/**
		 * Filter.OP_EQUAL, Filter.OP_NOT_EQUAL, Filter.OP_LESS_THAN, Filter.OP_LESS_OR_EQUAL, Filter.OP_GREATER_THAN,
		 * Filter.OP_GREATER_OR_EQUAL
		 */
		DATE,
		/**
		 * Filter.OP_EQUAL, Filter.OP_NOT_EQUAL, Filter.OP_LESS_THAN, Filter.OP_LESS_OR_EQUAL, Filter.OP_GREATER_THAN,
		 * Filter.OP_GREATER_OR_EQUAL, Filter.OP_BETWEEN
		 */
		DATE_RANGE,
		/**
		 * Filter.OP_EQUAL, Filter.OP_NOT_EQUAL, Filter.OP_LIKE, Filter.OP_IN, Filter.OP_NOT_IN
		 */
		MULTISELECT;
	}

	private static List<ValueLabel> defaultOperators = new ArrayList<>();
	private static List<ValueLabel> simpleOperators = new ArrayList<>();
	private static List<ValueLabel> stringOperators = new ArrayList<>();
	private static List<ValueLabel> simpleNumaricOperators = new ArrayList<>();
	private static List<ValueLabel> numaricOperators = new ArrayList<>();
	private static List<ValueLabel> booleanOperators = new ArrayList<>();
	private static List<ValueLabel> dateOperators = new ArrayList<>();
	private static List<ValueLabel> dateRangeOperators = new ArrayList<>();
	private static List<ValueLabel> multiselectOperators = new ArrayList<>();

	static {
		defaultOperators.add(new ValueLabel(String.valueOf(Filter.OP_EQUAL), "="));

		simpleOperators.add(new ValueLabel(String.valueOf("-1"), ""));
		simpleOperators.add(new ValueLabel(String.valueOf(Filter.OP_EQUAL), "="));
		simpleOperators.add(new ValueLabel(String.valueOf(Filter.OP_NOT_EQUAL), "<>"));

		stringOperators.add(new ValueLabel(String.valueOf(Filter.OP_EQUAL), "="));
		stringOperators.add(new ValueLabel(String.valueOf(Filter.OP_NOT_EQUAL), "<>"));
		stringOperators.add(new ValueLabel(String.valueOf(Filter.OP_LIKE), "%"));

		simpleNumaricOperators.add(new ValueLabel(String.valueOf(Filter.OP_EQUAL), "="));
		simpleNumaricOperators.add(new ValueLabel(String.valueOf(Filter.OP_NOT_EQUAL), "<>"));

		numaricOperators.add(new ValueLabel(String.valueOf(Filter.OP_EQUAL), "="));
		numaricOperators.add(new ValueLabel(String.valueOf(Filter.OP_NOT_EQUAL), "<>"));
		numaricOperators.add(new ValueLabel(String.valueOf(Filter.OP_LESS_THAN), "<"));
		numaricOperators.add(new ValueLabel(String.valueOf(Filter.OP_LESS_OR_EQUAL), "<="));
		numaricOperators.add(new ValueLabel(String.valueOf(Filter.OP_GREATER_THAN), ">"));
		numaricOperators.add(new ValueLabel(String.valueOf(Filter.OP_GREATER_OR_EQUAL), ">="));

		booleanOperators.add(new ValueLabel(String.valueOf(-1), ""));
		booleanOperators.add(new ValueLabel(String.valueOf(Filter.OP_EQUAL), "="));
		booleanOperators.add(new ValueLabel(String.valueOf(Filter.OP_NOT_EQUAL), "<>"));

		dateOperators.add(new ValueLabel(String.valueOf(Filter.OP_EQUAL), "="));
		dateOperators.add(new ValueLabel(String.valueOf(Filter.OP_NOT_EQUAL), "<>"));
		dateOperators.add(new ValueLabel(String.valueOf(Filter.OP_LESS_THAN), "<"));
		dateOperators.add(new ValueLabel(String.valueOf(Filter.OP_LESS_OR_EQUAL), "<="));
		dateOperators.add(new ValueLabel(String.valueOf(Filter.OP_GREATER_THAN), ">"));
		dateOperators.add(new ValueLabel(String.valueOf(Filter.OP_GREATER_OR_EQUAL), ">="));

		dateRangeOperators.addAll(dateOperators);
		dateRangeOperators.add(new ValueLabel(String.valueOf(Filter.OP_BETWEEN), "BETWEEN"));

		multiselectOperators.add(new ValueLabel(String.valueOf(Filter.OP_EQUAL), "="));
		multiselectOperators.add(new ValueLabel(String.valueOf(Filter.OP_NOT_EQUAL), "<>"));
		multiselectOperators.add(new ValueLabel(String.valueOf(Filter.OP_LIKE), "%"));
		multiselectOperators.add(new ValueLabel(String.valueOf(Filter.OP_IN), "IN"));
		multiselectOperators.add(new ValueLabel(String.valueOf(Filter.OP_NOT_IN), "NOT IN"));
	}

	public static List<ValueLabel> getOperators(Operators operator) {
		List<ValueLabel> operators = null;
		switch (operator) {
		case DEFAULT:
			operators = defaultOperators;
			break;
		case SIMPLE:
			operators = simpleOperators;
			break;
		case STRING:
			operators = stringOperators;
			break;
		case SIMPLE_NUMARIC:
			operators = simpleNumaricOperators;
			break;
		case NUMERIC:
			operators = numaricOperators;
			break;
		case BOOLEAN:
			operators = booleanOperators;
			break;
		case DATE:
			operators = dateOperators;
			break;
		case DATE_RANGE:
			operators = dateRangeOperators;
			break;
		case MULTISELECT:
			operators = multiselectOperators;
			break;
		default:
			break;
		}

		return operators;
	}
}
