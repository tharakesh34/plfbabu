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
 *
 * FileName : SearchOperators.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.util.searching;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import com.pennanttech.pennapps.jdbc.search.Filter;

/**
 * This class represents a few types of search operators <br>
 * corresponding to the com.trg.search.Search.java class. <br>
 * from the Hibernate-Generic-DAO framework. <br>
 * <br>
 * The domain model have no corresponding table in a database and has a fixed length of records that should see as the
 * search operators of what to search. <br>
 * It's used only for showing the several operators in a dropdown list. <br>
 * <br>
 * Int | sign | search operator <br>
 * __________________________________________<br>
 * -1 | | no operator (like empty for reset) <br>
 * 0 | = | equals <br>
 * 1 | # | not equal <br>
 * 2 | < | less than <br>
 * 3 | > | greater than <br>
 * 4 | <= | less or equal <br>
 * 5 | >= | greater or equal <br>
 * 7 | ~ | ilike <br>
 * <br>
 * 
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering.<br>
 * 
 */
public class SearchOperators implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient int searchOperatorId;
	private transient String searchOperatorSign;
	private transient String searchOperatorName;

	/**
	 * default constructor.<br>
	 */
	public SearchOperators() {
	    super();
	}

	/**
	 * constructor.<br>
	 * 
	 * @param searchOperatorId
	 * @param searchOperatorSign
	 * @param searchOperatorName
	 */
	public SearchOperators(int searchOperatorId, String searchOperatorSign, String searchOperatorName) {
		this.searchOperatorId = searchOperatorId;
		this.searchOperatorSign = searchOperatorSign;
		this.searchOperatorName = searchOperatorName;
	}

	public List<SearchOperators> getAllOperators() {

		List<SearchOperators> result = new ArrayList<SearchOperators>();

		// list position 0
		result.add(new SearchOperators(Filter.OP_EQUAL, "=", "equals"));
		// list position 1
		result.add(new SearchOperators(Filter.OP_NOT_EQUAL, "<>", "not equal"));
		// list position 2
		result.add(new SearchOperators(Filter.OP_LESS_THAN, "<", "less than"));
		// list position 3
		result.add(new SearchOperators(Filter.OP_GREATER_THAN, ">", "greater than"));
		// list position 4
		result.add(new SearchOperators(Filter.OP_LESS_OR_EQUAL, "<=", "less or equal"));
		// list position 5
		result.add(new SearchOperators(Filter.OP_GREATER_OR_EQUAL, ">=", "greater or equal"));
		// list position 6
		result.add(new SearchOperators(Filter.OP_LIKE, "%", "like"));

		return result;
	}

	public List<SearchOperators> getNumericOperators() {

		List<SearchOperators> result = new ArrayList<SearchOperators>();

		// list position 0
		result.add(new SearchOperators(Filter.OP_EQUAL, "=", "equals"));
		// list position 1
		result.add(new SearchOperators(Filter.OP_NOT_EQUAL, "<>", "not equal"));
		// list position 2
		result.add(new SearchOperators(Filter.OP_LESS_THAN, "<", "less than"));
		// list position 3
		result.add(new SearchOperators(Filter.OP_GREATER_THAN, ">", "greater than"));
		// list position 4
		result.add(new SearchOperators(Filter.OP_LESS_OR_EQUAL, "<=", "less or equal"));
		// list position 5
		result.add(new SearchOperators(Filter.OP_GREATER_OR_EQUAL, ">=", "greater or equal"));

		return result;
	}

	public List<SearchOperators> getStringOperators() {

		List<SearchOperators> result = new ArrayList<SearchOperators>();

		// list position 0
		result.add(new SearchOperators(Filter.OP_EQUAL, "=", "equals"));
		// list position 1
		result.add(new SearchOperators(Filter.OP_NOT_EQUAL, "<>", "not equal"));
		// list position 2
		result.add(new SearchOperators(Filter.OP_LIKE, "%", "like"));

		return result;
	}

	public List<SearchOperators> getAlphaNumOperators() {

		List<SearchOperators> result = new ArrayList<SearchOperators>();

		// list position 0
		result.add(new SearchOperators(Filter.OP_EQUAL, "=", "equals"));
		// list position 1
		result.add(new SearchOperators(Filter.OP_NOT_EQUAL, "<>", "not equal"));
		// list position 2
		result.add(new SearchOperators(Filter.OP_LIKE, "%", "like"));
		// list position 3
		result.add(new SearchOperators(Filter.OP_LESS_THAN, "<", "less than"));
		// list position 4
		result.add(new SearchOperators(Filter.OP_GREATER_THAN, ">", "greater than"));
		// list position 5
		result.add(new SearchOperators(Filter.OP_LESS_OR_EQUAL, "<=", "less or equal"));
		// list position 6
		result.add(new SearchOperators(Filter.OP_GREATER_OR_EQUAL, ">=", "greater or equal"));
		return result;
	}

	public List<SearchOperators> getMultiStringOperators() {

		List<SearchOperators> result = new ArrayList<SearchOperators>();

		// list position 0
		result.add(new SearchOperators(Filter.OP_EQUAL, "=", "equals"));
		// list position 1
		result.add(new SearchOperators(Filter.OP_NOT_EQUAL, "<>", "not equal"));
		// list position 2
		result.add(new SearchOperators(Filter.OP_LIKE, "%", "like"));
		// list position 3
		result.add(new SearchOperators(Filter.OP_IN, "IN", "IN"));
		// list position 4
		result.add(new SearchOperators(Filter.OP_NOT_IN, "NOT IN", "NOT IN"));

		return result;
	}

	public List<SearchOperators> getMultiDateOperators() {

		List<SearchOperators> result = new ArrayList<SearchOperators>();

		// list position 0
		result.add(new SearchOperators(Filter.OP_EQUAL, "=", "equals"));
		// list position 1
		result.add(new SearchOperators(Filter.OP_NOT_EQUAL, "<>", "not equal"));
		// list position 2
		result.add(new SearchOperators(Filter.OP_LESS_THAN, "<", "less than"));
		// list position 3
		result.add(new SearchOperators(Filter.OP_GREATER_THAN, ">", "greater than"));
		// list position 4
		result.add(new SearchOperators(Filter.OP_LESS_OR_EQUAL, "<=", "less or equal"));
		// list position 5
		result.add(new SearchOperators(Filter.OP_GREATER_OR_EQUAL, ">=", "greater or equal"));
		// list position 6
		result.add(new SearchOperators(Filter.OP_BETWEEN, "BETWEEN", "RANGE"));

		return result;
	}

	public List<SearchOperators> getBooleanOperators() {

		List<SearchOperators> result = new ArrayList<SearchOperators>();

		// list position 0
		result.add(new SearchOperators(-1, "", "no operator"));
		// list position 1
		result.add(new SearchOperators(Filter.OP_EQUAL, "=", "equals"));
		// list position 2
		result.add(new SearchOperators(Filter.OP_NOT_EQUAL, "<>", "not equal"));

		return result;
	}

	public List<SearchOperators> getEqualOperators() {

		List<SearchOperators> result = new ArrayList<SearchOperators>();

		// list position 0
		result.add(new SearchOperators(Filter.OP_EQUAL, "=", "equals"));

		return result;
	}

	public List<SearchOperators> getEqualOrNotOperators() {

		List<SearchOperators> result = new ArrayList<SearchOperators>();

		// list position 0
		result.add(new SearchOperators(Filter.OP_EQUAL, "=", "equals"));
		// list position 1
		result.add(new SearchOperators(Filter.OP_NOT_EQUAL, "<>", "not equal"));

		return result;
	}

	public List<SearchOperators> getSimpleStringOperators() {

		List<SearchOperators> result = new ArrayList<SearchOperators>();

		// list position 0
		result.add(new SearchOperators(Filter.OP_EQUAL, "=", "equals"));
		// list position 1
		result.add(new SearchOperators(Filter.OP_NOT_EQUAL, "<>", "not equal"));
		// list position 2
		result.add(new SearchOperators(Filter.OP_LIKE, "%", "like"));

		return result;
	}

	public List<SearchOperators> getSimpleNumericOperators() {

		List<SearchOperators> result = new ArrayList<SearchOperators>();

		// list position 0
		result.add(new SearchOperators(Filter.OP_EQUAL, "=", "equals"));
		// list position 1
		result.add(new SearchOperators(Filter.OP_NOT_EQUAL, "<>", "not equal"));
		// list position 2
		result.add(new SearchOperators(Filter.OP_LESS_THAN, "<", "less than"));
		// list position 3
		result.add(new SearchOperators(Filter.OP_GREATER_THAN, ">", "greater than"));
		// list position 4
		result.add(new SearchOperators(Filter.OP_LESS_OR_EQUAL, "<=", "less or equal"));
		// list position 5
		result.add(new SearchOperators(Filter.OP_GREATER_OR_EQUAL, ">=", "greater or equal"));

		return result;
	}

	public List<SearchOperators> getSimpleAlphaNumOperators() {

		List<SearchOperators> result = new ArrayList<SearchOperators>();

		// list position 0
		result.add(new SearchOperators(Filter.OP_EQUAL, "=", "equals"));
		// list position 1
		result.add(new SearchOperators(Filter.OP_LIKE, "%", "like"));
		// list position 2
		result.add(new SearchOperators(Filter.OP_LESS_THAN, "<", "less than"));
		// list position 3
		result.add(new SearchOperators(Filter.OP_GREATER_THAN, ">", "greater than"));
		// list position 4
		result.add(new SearchOperators(Filter.OP_LESS_OR_EQUAL, "<=", "less or equal"));
		// list position 5
		result.add(new SearchOperators(Filter.OP_GREATER_OR_EQUAL, ">=", "greater or equal"));
		return result;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setSearchOperatorId(int searchOperatorId) {
		this.searchOperatorId = searchOperatorId;
	}

	public int getSearchOperatorId() {
		return searchOperatorId;
	}

	public void setSearchOperatorSign(String searchOperatorSign) {
		this.searchOperatorSign = searchOperatorSign;
	}

	public String getSearchOperatorSign() {
		return searchOperatorSign;
	}

	public void setSearchOperatorName(String searchOperatorName) {
		this.searchOperatorName = searchOperatorName;
	}

	public String getSearchOperatorName() {
		return searchOperatorName;
	}

	/**
	 * Restore the all operators sign in the operators listbox by comparing the <br>
	 * value of the filter. <br>
	 * 
	 * @param listbox Listbox that shows the operator signs.
	 * @param filter  Filter that corresponds to the operator listbox.
	 */
	public static void restoreOperator(Listbox listbox, Filter filter) {
		if (filter.getOperator() == Filter.OP_EQUAL) {
			listbox.setSelectedIndex(0);
		} else if (filter.getOperator() == Filter.OP_NOT_EQUAL) {
			listbox.setSelectedIndex(1);
		} else if (filter.getOperator() == Filter.OP_LESS_THAN) {
			listbox.setSelectedIndex(2);
		} else if (filter.getOperator() == Filter.OP_GREATER_THAN) {
			listbox.setSelectedIndex(3);
		} else if (filter.getOperator() == Filter.OP_LESS_OR_EQUAL) {
			listbox.setSelectedIndex(4);
		} else if (filter.getOperator() == Filter.OP_GREATER_OR_EQUAL) {
			listbox.setSelectedIndex(5);
		} else if (filter.getOperator() == Filter.OP_LIKE) {
			// Delete used '%' signs if the operator is like or iLike
			final String str = StringUtils.replaceChars(filter.getValue().toString(), "%", "");
			filter.setValue(str);
			listbox.setSelectedIndex(6);
		}
	}

	/**
	 * Restore the string operators sign in the operators listbox by comparing the <br>
	 * value of the filter. <br>
	 * 
	 * @param listbox Listbox that shows the operator signs.
	 * @param filter  Filter that corresponds to the operator listbox.
	 */
	public static void restoreStringOperator(Listbox listbox, Filter filter) {
		if (filter.getOperator() == Filter.OP_EQUAL) {
			listbox.setSelectedIndex(0);
		} else if (filter.getOperator() == Filter.OP_NOT_EQUAL) {
			listbox.setSelectedIndex(1);
		} else if (filter.getOperator() == Filter.OP_LIKE) {
			// Delete used '%' signs if the operator is like or iLike
			final String str = StringUtils.replaceChars(filter.getValue().toString(), "%", "");
			filter.setValue(str);
			listbox.setSelectedIndex(2);
		}
	}

	/**
	 * Restore the AlphaNumeric operators sign in the operators listbox by comparing the <br>
	 * value of the filter. <br>
	 * 
	 * @param listbox Listbox that shows the operator signs.
	 * @param filter  Filter that corresponds to the operator listbox.
	 */
	public static void restoreAlphaNumOperator(Listbox listbox, Filter filter) {
		if (filter.getOperator() == Filter.OP_EQUAL) {
			listbox.setSelectedIndex(0);
		} else if (filter.getOperator() == Filter.OP_NOT_EQUAL) {
			listbox.setSelectedIndex(1);
		} else if (filter.getOperator() == Filter.OP_LIKE) {
			// Delete used '%' signs if the operator is like or iLike
			final String str = StringUtils.replaceChars(filter.getValue().toString(), "%", "");
			filter.setValue(str);
			listbox.setSelectedIndex(2);
		} else if (filter.getOperator() == Filter.OP_LESS_THAN) {
			listbox.setSelectedIndex(3);
		} else if (filter.getOperator() == Filter.OP_GREATER_THAN) {
			listbox.setSelectedIndex(4);
		} else if (filter.getOperator() == Filter.OP_LESS_OR_EQUAL) {
			listbox.setSelectedIndex(5);
		} else if (filter.getOperator() == Filter.OP_GREATER_OR_EQUAL) {
			listbox.setSelectedIndex(6);
		}
	}

	/**
	 * Restore the string operators sign in the operators listbox
	 * 
	 * @param listbox Listbox that shows the operator signs.
	 * @param filter  Filter that corresponds to the operator listbox.
	 */
	public static void resetOperator(Listbox listbox, Filter filter) {
		if (filter.getOperator() == Filter.OP_EQUAL) {
			listbox.setSelectedIndex(0);
		} else if (filter.getOperator() == Filter.OP_NOT_EQUAL) {
			listbox.setSelectedIndex(1);
		} else if (filter.getOperator() == Filter.OP_LIKE) {
			// Delete used '%' signs if the operator is like or iLike
			final String str = StringUtils.replaceChars(filter.getValue().toString(), "%", "");
			filter.setValue(str);
			listbox.setSelectedIndex(2);
		}
	}

	/**
	 * Restore the numeric operator sign in the numeric operators listbox by comparing the <br>
	 * value of the filter. <br>
	 * 
	 * @param listbox Listbox that shows the operator signs.
	 * @param filter  Filter that corresponds to the operator listbox.
	 */
	public static void restoreNumericOperator(Listbox listbox, Filter filter) {
		if (filter.getOperator() == Filter.OP_EQUAL) {
			listbox.setSelectedIndex(0);
		} else if (filter.getOperator() == Filter.OP_NOT_EQUAL) {
			listbox.setSelectedIndex(1);
		} else if (filter.getOperator() == Filter.OP_LESS_THAN) {
			listbox.setSelectedIndex(2);
		} else if (filter.getOperator() == Filter.OP_GREATER_THAN) {
			listbox.setSelectedIndex(3);
		} else if (filter.getOperator() == Filter.OP_LESS_OR_EQUAL) {
			listbox.setSelectedIndex(4);
		} else if (filter.getOperator() == Filter.OP_GREATER_OR_EQUAL) {
			listbox.setSelectedIndex(5);
		}
	}

	/**
	 * Restore the boolean operator sign in the boolean operators listbox by comparing the <br>
	 * value of the filter. <br>
	 * 
	 * @param listbox Listbox that shows the operator signs.
	 * @param filter  Filter that corresponds to the operator listbox.
	 */
	public static void restoreBooleanOperators(Listbox listbox, Filter filter) {

		if (filter.getOperator() == Filter.OP_EQUAL) {
			listbox.setSelectedIndex(1);
		} else if (filter.getOperator() == Filter.OP_NOT_EQUAL) {
			listbox.setSelectedIndex(2);
		}
	}

	public static ListModelList<SearchOperators> getOperators(Integer... filters) {
		List<SearchOperators> result = new ArrayList<>();
		for (Integer filter : filters) {
			switch (filter) {
			case Filter.OP_EQUAL:
				result.add(new SearchOperators(Filter.OP_EQUAL, "=", "equals"));
				break;
			case Filter.OP_LIKE:
				result.add(new SearchOperators(Filter.OP_LIKE, "%", "like"));
				break;

			default:
				break;
			}
		}

		return new ListModelList<SearchOperators>(result);
	}

}
