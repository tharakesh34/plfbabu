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

import com.pennanttech.pennapps.core.feature.ModuleUtil;

/**
 * A convenient fully-featured implementation of ISearch and IMutableSearch for
 * general use in Java code.
 * 
 */
public class Search implements IMutableSearch, Serializable {
	private static final long serialVersionUID = 1L;

	private int firstResult = -1; // -1 stands for unspecified
	private int maxResults = -1; // -1 stands for unspecified
	private int page = -1; // -1 stands for unspecified
	private Class<?> searchClass;
	private boolean disjunction;
	private boolean distinct;
	private int resultMode = RESULT_AUTO;
	private String tabelName;
	private String whereClause;
	private List<Filter> filters = new ArrayList<>();
	private List<Sort> sorts = new ArrayList<>();
	private List<Field> fields = new ArrayList<>();
	private List<String> fetches = new ArrayList<>();

	public Search() {
		super();
	}

	public Search(Class<?> searchClass) {
		this.tabelName = ModuleUtil.getTableName(searchClass.getSimpleName());
		this.searchClass = searchClass;
	}

	public int getFirstResult() {
		return firstResult;
	}

	public Search setFirstResult(int firstResult) {
		this.firstResult = firstResult;
		return this;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public Search setMaxResults(int maxResults) {
		this.maxResults = maxResults;
		return this;
	}

	public int getPage() {
		return page;
	}

	public Search setPage(int page) {
		this.page = page;
		return this;
	}

	public Search setSearchClass(Class<?> searchClass) {
		this.searchClass = searchClass;
		return this;
	}

	public Class<?> getSearchClass() {
		return searchClass;
	}

	public boolean isDisjunction() {
		return disjunction;
	}

	public Search setDisjunction(boolean disjunction) {
		this.disjunction = disjunction;
		return this;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public Search setDistinct(boolean distinct) {
		this.distinct = distinct;
		return this;
	}

	public int getResultMode() {
		return resultMode;
	}

	public Search setResultMode(int resultMode) {
		if (resultMode < 0 || resultMode > 4) {
			throw new IllegalArgumentException("Result Mode ( " + resultMode + " ) is not a valid option.");
		}
		this.resultMode = resultMode;
		return this;
	}

	public String getTabelName() {
		return this.tabelName;
	}

	public void addTabelName(String tabelName) {
		this.tabelName = tabelName;
	}

	/**
	 * Return Where Clause which will be added directly to the SQL Query
	 * 
	 * @return The Where Clause which will be added directly to the SQL Query
	 * 
	 */
	public String getWhereClause() {
		return this.whereClause;
	}

	/**
	 * Set the where clause which will be added directly to the SQL Query
	 * 
	 * @param whereClause
	 *            The where clause which will be added directly to the SQL Query
	 * 
	 */
	public void addWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public Search setFilters(List<Filter> filters) {
		this.filters = filters;
		return this;
	}

	public List<Sort> getSorts() {
		return sorts;
	}

	public Search setSorts(List<Sort> sorts) {
		this.sorts = sorts;
		return this;
	}

	public List<Field> getFields() {
		return fields;
	}

	public Search setFields(List<Field> fields) {
		this.fields = fields;
		return this;
	}

	public List<String> getFetches() {
		return fetches;
	}

	public Search setFetches(List<String> fetches) {
		this.fetches = fetches;
		return this;
	}

	/**
	 * Add a filter.
	 * 
	 * @param filter
	 *            The specified filter
	 * @return The <code>Search</code>
	 */
	public Search addFilter(Filter filter) {
		SearchUtil.addFilter(this, filter);
		return this;
	}

	/**
	 * <p>
	 * Add a filter.
	 * <p>
	 * This takes a variable number of parameters. Any number of Filters can be
	 * specified.
	 * 
	 * @param filters
	 *            The specified number of filter
	 * @return The <code>Search</code>
	 */
	public Search addFilters(Filter... filters) {
		SearchUtil.addFilters(this, filters);
		return this;
	}

	/**
	 * Add a filter that uses the == operator.
	 * 
	 * @param property
	 *            The property to include in the filter
	 * @param value
	 *            The value to compare with.
	 * @return The <code>Search</code>
	 */
	public Search addFilterEqual(String property, Object value) {
		SearchUtil.addFilterEqual(this, property, value);
		return this;
	}

	/**
	 * Add a filter that uses the == operator.
	 * 
	 * @param property
	 *            The column to filter
	 * @param value
	 *            The value to compare with.
	 * @return The <code>Search</code>
	 */
	public Search addFilterGreaterOrEqual(String property, Object value) {
		SearchUtil.addFilterGreaterOrEqual(this, property, value);
		return this;
	}

	/**
	 * Add a filter that uses the &lt;= operator.
	 * 
	 * @param property
	 *            The column to filter
	 * @param value
	 *            The value to compare with.
	 * @return The <code>Search</code>
	 */
	public Search addFilterGreaterThan(String property, Object value) {
		SearchUtil.addFilterGreaterThan(this, property, value);
		return this;
	}

	/**
	 * <p>
	 * Add a filter that uses the IN operator.
	 * <p>
	 * This takes a variable number of parameters.
	 * <p>
	 * Any number of values can be specified.
	 * 
	 * @param property
	 *            The column to filter.
	 * @param value
	 *            The value's to compare with.
	 * @return The <code>Search</code>
	 */
	public Search addFilterIn(String property, Collection<?> value) {
		SearchUtil.addFilterIn(this, property, value);
		return this;
	}

	/**
	 * <p>
	 * Add a filter that uses the IN operator.
	 * <p>
	 * This takes a variable number of parameters.
	 * <p>
	 * Any number of values can be specified.
	 * 
	 * @param property
	 *            The column to filter.
	 * @param value
	 *            The value's to compare with.
	 * @return The <code>Search</code>
	 */
	public Search addFilterIn(String property, Object... value) {
		SearchUtil.addFilterIn(this, property, value);
		return this;
	}

	/**
	 * <p>
	 * Add a filter that uses the NOT IN operator.
	 * 
	 * @param property
	 *            The column to filter.
	 * @param value
	 *            The value's to compare with.
	 * @return The <code>Search</code>
	 */
	public Search addFilterNotIn(String property, Collection<?> value) {
		SearchUtil.addFilterNotIn(this, property, value);
		return this;
	}

	/**
	 * <p>
	 * Add a filter that uses the NOT IN operator.
	 * <p>
	 * This takes a variable number of parameters.
	 * <p>
	 * Any number of values can be specified.
	 * 
	 * @param property
	 *            The column to filter.
	 * @param value
	 *            The value's to compare with.
	 * @return The <code>Search</code>
	 */
	public Search addFilterNotIn(String property, Object... value) {
		SearchUtil.addFilterNotIn(this, property, value);
		return this;
	}

	/**
	 * <p>
	 * Add a filter that uses the &lt;= operator.
	 * 
	 * @param property
	 *            The column to filter.
	 * @param value
	 *            The value's to compare with.
	 * @return The <code>Search</code>
	 */
	public Search addFilterLessOrEqual(String property, Object value) {
		SearchUtil.addFilterLessOrEqual(this, property, value);
		return this;
	}

	/**
	 * <p>
	 * Add a filter that uses the &gt;= operator.
	 * 
	 * @param property
	 *            The column to filter.
	 * @param value
	 *            The value's to compare with.
	 * @return The <code>Search</code>
	 */
	public Search addFilterLessThan(String property, Object value) {
		SearchUtil.addFilterLessThan(this, property, value);
		return this;
	}

	/**
	 * <p>
	 * Add a filter that uses the LIKE operator.
	 * 
	 * @param property
	 *            The column to filter.
	 * @param value
	 *            The value's to compare with.
	 * @return The <code>Search</code>
	 */
	public Search addFilterLike(String property, String value) {
		SearchUtil.addFilterLike(this, property, value);
		return this;
	}

	/**
	 * <p>
	 * Add a filter that uses the != operator.
	 * 
	 * @param property
	 *            The column to filter.
	 * @param value
	 *            The value's to compare with.
	 * @return The <code>Search</code>
	 */
	public Search addFilterNotEqual(String property, Object value) {
		SearchUtil.addFilterNotEqual(this, property, value);
		return this;
	}

	/**
	 * <p>
	 * Add a filter that uses the IS NULL operator.
	 * 
	 * @param property
	 *            The column to filter.
	 * @return The <code>Search</code>
	 */
	public Search addFilterNull(String property) {
		SearchUtil.addFilterNull(this, property);
		return this;
	}

	/**
	 * <p>
	 * Add a filter that uses the IS NOT NULL operator.
	 * 
	 * @param property
	 *            The column to filter.
	 * @return The <code>Search</code>
	 */
	public Search addFilterNotNull(String property) {
		SearchUtil.addFilterNotNull(this, property);
		return this;
	}

	/**
	 * <p>
	 * Add a filter that uses the AND operator.
	 * <p>
	 * This takes a variable number of parameters. Any number of Filters can be
	 * specified.
	 * 
	 * @param filters
	 *            The filters to filter.
	 * @return The <code>Search</code>
	 */
	public Search addFilterAnd(Filter... filters) {
		SearchUtil.addFilterAnd(this, filters);
		return this;
	}

	/**
	 * <p>
	 * Add a filter that uses the OR operator.
	 * <p>
	 * This takes a variable number of parameters. Any number of Filters can be
	 * specified.
	 * 
	 * @param filters
	 *            The filters to filter.
	 * @return The <code>Search</code>
	 */
	public Search addFilterOr(Filter... filters) {
		SearchUtil.addFilterOr(this, filters);
		return this;
	}

	/**
	 * <p>
	 * Removes the filter from the <code>Search</code>
	 * 
	 * @param filter
	 *            The filter to remove from the <code>Search</code>.
	 * @return The <code>Search</code>
	 */
	public Search removeFilter(Filter filter) {
		SearchUtil.removeFilter(this, filter);
		return this;
	}

	public Search removeFiltersOnProperty(String property) {
		SearchUtil.removeFiltersOnProperty(this, property);
		return this;
	}

	public Search clearFilters() {
		SearchUtil.clearFilters(this);
		return this;
	}

	public Search addSort(Sort sort) {
		SearchUtil.addSort(this, sort);
		return this;
	}

	public Search addSorts(Sort... sorts) {
		SearchUtil.addSorts(this, sorts);
		return this;
	}

	/**
	 * Add ascending sort by property
	 * 
	 * @param property
	 *            The column to sort by ascending
	 * @return The <code>Search</code>
	 */
	public Search addSortAsc(String property) {
		SearchUtil.addSortAsc(this, property);
		return this;
	}

	/**
	 * Add descending sort by property
	 * 
	 * @param property
	 *            The column to sort by descending
	 * @return The <code>Search</code>
	 */
	public Search addSortDesc(String property) {
		SearchUtil.addSortDesc(this, property);
		return this;
	}

	/**
	 * Add sort by property. Ascending if <code>desc == false</code>, descending
	 * if <code>desc == true</code>.
	 * 
	 * @param property
	 *            The column to sort by ascending or descending
	 * @param desc
	 *            ascending or descending
	 * @return The <code>Search</code>
	 */
	public Search addSort(String property, boolean desc) {
		SearchUtil.addSort(this, property, desc);
		return this;
	}

	public Search removeSort(Sort sort) {
		SearchUtil.removeSort(this, sort);
		return this;
	}

	public Search removeSort(String property) {
		SearchUtil.removeSort(this, property);
		return this;
	}

	public Search clearSorts() {
		SearchUtil.clearSorts(this);
		return this;
	}

	protected Search addField(Field field) {
		SearchUtil.addField(this, field);
		return this;
	}

	protected Search addFields(Field... fields) {
		SearchUtil.addFields(this, fields);
		return this;
	}

	/**
	 * If this field is used with <code>resultMode == RESULT_MAP</code>, the
	 * <code>property</code> will also be used as the key for this value in the
	 * map.
	 * 
	 * @param property
	 *            The property used as key
	 * @return The <code>Search</code>
	 */
	public Search addField(String property) {
		SearchUtil.addField(this, property);
		return this;
	}
	
	/**
	 * If this field is used with <code>resultMode == RESULT_MAP</code>, the
	 * <code>property</code> will also be used as the key for this value in the
	 * map.
	 * 
	 * @param property
	 *            The property used as key
	 * @return The <code>Search</code>
	 */
	public Search addFields(String[] properties) {
		for (String property : properties) {
			SearchUtil.addField(this, property);
		}

		return this;
	}

	/**
	 * If this field is used with <code>resultMode == RESULT_MAP</code>, the
	 * <code>key</code> will be used as the key for this value in the map.
	 * 
	 * @param property
	 *            The name of the column
	 * 
	 * @param key
	 *            The key to use for the property
	 * @return The <code>Search</code>
	 * 
	 */
	public Search addField(String property, String key) {
		SearchUtil.addField(this, property, key);
		return this;
	}

	/**
	 * If this field is used with <code>resultMode == RESULT_MAP</code>, the
	 * <code>property</code> will also be used as the key for this value in the
	 * map.
	 * 
	 * @param property
	 *            The name of the column
	 * 
	 * @param operator
	 *            The operator to apply to the column
	 * @return The <code>Search</code>
	 * 
	 */
	public Search addField(String property, int operator) {
		SearchUtil.addField(this, property, operator);
		return this;
	}

	/**
	 * If this field is used with <code>resultMode == RESULT_MAP</code>, the
	 * <code>key</code> will be used as the key for this value in the map.
	 * 
	 * @param property
	 *            The name of the column
	 * 
	 * @param operator
	 *            The operator to apply to the column
	 * 
	 * @param key
	 *            The key to use for the property
	 * @return The <code>Search</code>
	 */
	public Search addField(String property, int operator, String key) {
		SearchUtil.addField(this, property, operator, key);
		return this;
	}

	/**
	 * <p>
	 * Add a filter that uses the ILIKE operator.
	 * 
	 * <p>
	 * This takes list of values to filter
	 * 
	 * @param property
	 *            The property to include in the filter
	 * 
	 * @param listValues
	 *            The list of values to filter
	 * 
	 * @param emptyEqual
	 *            Whether this filter will consider the empty values or not
	 * @return The <code>Search</code>
	 */
	public Search addFilterOrLike(String property, List<String> listValues, boolean emptyEqual) {
		SearchUtil.addFilterOrLike(this, property, listValues, emptyEqual);
		return this;
	}

	/**
	 * <p>
	 * Add a filter that uses the IN operator.
	 * 
	 * <p>
	 * This takes list of values to filter
	 * 
	 * @param property
	 *            The property to include in the filter
	 * 
	 * @param listValues
	 *            The list of values to filter
	 * 
	 * @param emptyEqual
	 *            Whether this filter will consider the empty values or not
	 * @return The <code>Search</code>
	 */
	public Search addFilterIn(String property, List<String> listValues, boolean emptyEqual) {
		SearchUtil.addFilterIn(this, property, listValues, emptyEqual);
		return this;
	}

	/**
	 * <p>
	 * Add a filter that uses the NOT IN operator.
	 * 
	 * <p>
	 * This takes list of values to filter
	 * 
	 * @param property
	 *            The property to include in the filter
	 * 
	 * @param listValues
	 *            The list of values to filter
	 * 
	 * @param emptyEqual
	 *            Whether this filter will consider the empty values or not
	 * @return The <code>Search</code>
	 */
	public Search addFilterNotIn(String property, List<String> listValues, boolean emptyEqual) {
		Object[] values = null;
		int cnt = 0;

		if (listValues != null && listValues.size() > 0) {
			if (emptyEqual) {
				values = new String[listValues.size() + 1];
				values[0] = " ";
				cnt = 1;
			} else {
				values = new String[listValues.size()];
			}

			for (int i = 0; i < listValues.size(); i++) {
				values[cnt + i] = listValues.get(i);
			}

			this.addFilterNotIn(property, values);
		}

		return this;
	}

	public Search removeField(Field field) {
		SearchUtil.removeField(this, field);
		return this;
	}

	public Search removeField(String property) {
		SearchUtil.removeField(this, property);
		return this;
	}

	public Search removeField(String property, String key) {
		SearchUtil.removeField(this, property, key);
		return this;
	}

	public Search clearFields() {
		SearchUtil.clearFields(this);
		return this;
	}

	public Search removeFetch(String property) {
		SearchUtil.removeFetch(this, property);
		return this;
	}

	public Search clearFetches() {
		SearchUtil.clearFetches(this);
		return this;
	}

	public Search clear() {
		SearchUtil.clear(this);
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		return SearchUtil.equals(this, obj);
	}

	@Override
	public int hashCode() {
		return SearchUtil.hashCode(this);
	}

	@Override
	public String toString() {
		return SearchUtil.toString(this);
	}
}
