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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pennanttech.pennapps.core.util.ModuleUtil;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.App.Database;

/**
 * A convenient fully-featured implementation of ISearch and IMutableSearch for general use in Java code.
 * 
 * @author dwolverton
 */
public class Search implements IMutableSearch, Serializable {
	private static final long	serialVersionUID	= 1L;

	protected int				firstResult			= -1;				// -1 stands for unspecified
	protected int				maxResults			= -1;				// -1 stands for unspecified
	protected int				page				= -1;				// -1 stands for unspecified
	protected Class<?>			searchClass;
	private List<Filter>		filters				= new ArrayList<>();
	protected boolean			disjunction;
	private List<Sort>			sorts				= new ArrayList<>();
	private List<Field>			fields				= new ArrayList<>();
	protected boolean			distinct;
	private List<String>		fetches				= new ArrayList<>();
	protected int				resultMode			= RESULT_AUTO;
	protected String			tabelName			= null;
	protected String			whereClause			= null;

	public Search() {
		super();
	}

	public Search(Class<?> searchClass) {
		this.tabelName = ModuleUtil.getTableName(searchClass.getSimpleName());
		this.searchClass = searchClass;
	}

	public Search setSearchClass(Class<?> searchClass) {
		this.searchClass = searchClass;
		return this;
	}

	public Class<?> getSearchClass() {
		return searchClass;
	}

	// Filters
	public Search addFilter(Filter filter) {
		SearchUtil.addFilter(this, filter);
		return this;
	}

	public Search addFilters(Filter... filters) {
		SearchUtil.addFilters(this, filters);
		return this;
	}

	/**
	 * Add a filter that uses the == operator.
	 */
	public Search addFilterEqual(String property, Object value) {
		SearchUtil.addFilterEqual(this, property, value);
		return this;
	}

	/**
	 * Add a filter that uses the >= operator.
	 */
	public Search addFilterGreaterOrEqual(String property, Object value) {
		SearchUtil.addFilterGreaterOrEqual(this, property, value);
		return this;
	}

	/**
	 * Add a filter that uses the > operator.
	 */
	public Search addFilterGreaterThan(String property, Object value) {
		SearchUtil.addFilterGreaterThan(this, property, value);
		return this;
	}

	/**
	 * Add a filter that uses the IN operator.
	 */
	public Search addFilterIn(String property, Collection<?> value) {
		SearchUtil.addFilterIn(this, property, value);
		return this;
	}

	/**
	 * Add a filter that uses the IN operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of values can be specified.
	 */
	public Search addFilterIn(String property, Object... value) {
		SearchUtil.addFilterIn(this, property, value);
		return this;
	}

	/**
	 * Add a filter that uses the NOT IN operator.
	 */
	public Search addFilterNotIn(String property, Collection<?> value) {
		SearchUtil.addFilterNotIn(this, property, value);
		return this;
	}

	/**
	 * Add a filter that uses the NOT IN operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of values can be specified.
	 */
	public Search addFilterNotIn(String property, Object... value) {
		SearchUtil.addFilterNotIn(this, property, value);
		return this;
	}

	/**
	 * Add a filter that uses the <= operator.
	 */
	public Search addFilterLessOrEqual(String property, Object value) {
		SearchUtil.addFilterLessOrEqual(this, property, value);
		return this;
	}

	/**
	 * Add a filter that uses the < operator.
	 */
	public Search addFilterLessThan(String property, Object value) {
		SearchUtil.addFilterLessThan(this, property, value);
		return this;
	}

	/**
	 * Add a filter that uses the LIKE operator.
	 */
	public Search addFilterLike(String property, String value) {
		SearchUtil.addFilterLike(this, property, value);
		return this;
	}

	/**
	 * Add a filter that uses the != operator.
	 */
	public Search addFilterNotEqual(String property, Object value) {
		SearchUtil.addFilterNotEqual(this, property, value);
		return this;
	}

	/**
	 * Add a filter that uses the IS NULL operator.
	 */
	public Search addFilterNull(String property) {
		SearchUtil.addFilterNull(this, property);
		return this;
	}

	/**
	 * Add a filter that uses the IS NOT NULL operator.
	 */
	public Search addFilterNotNull(String property) {
		SearchUtil.addFilterNotNull(this, property);
		return this;
	}

	/**
	 * Add a filter that uses the AND operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of <code>Filter
	 * </code>s can be specified.
	 */
	public Search addFilterAnd(Filter... filters) {
		SearchUtil.addFilterAnd(this, filters);
		return this;
	}

	/**
	 * Add a filter that uses the OR operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of <code>Filter
	 * </code>s can be specified.
	 */
	public Search addFilterOr(Filter... filters) {
		SearchUtil.addFilterOr(this, filters);
		return this;
	}

	public void removeFilter(Filter filter) {
		SearchUtil.removeFilter(this, filter);
	}

	/**
	 * Remove all filters on the given property.
	 */
	public void removeFiltersOnProperty(String property) {
		SearchUtil.removeFiltersOnProperty(this, property);
	}

	public void clearFilters() {
		SearchUtil.clearFilters(this);
	}

	public boolean isDisjunction() {
		return disjunction;
	}

	/**
	 * Filters added to a search are "ANDed" together if this is false (default) and "ORed" if it is set to true.
	 */
	public Search setDisjunction(boolean disjunction) {
		this.disjunction = disjunction;
		return this;
	}

	// Sorts
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
	 */
	public Search addSortAsc(String property) {
		SearchUtil.addSortAsc(this, property);
		return this;
	}

	/**
	 * Add descending sort by property
	 */
	public Search addSortDesc(String property) {
		SearchUtil.addSortDesc(this, property);
		return this;
	}

	/**
	 * Add sort by property. Ascending if <code>desc == false</code>, descending if <code>desc == true</code>.
	 */
	public Search addSort(String property, boolean desc) {
		SearchUtil.addSort(this, property, desc);
		return this;
	}

	public void removeSort(Sort sort) {
		SearchUtil.removeSort(this, sort);
	}

	public void removeSort(String property) {
		SearchUtil.removeSort(this, property);
	}

	public void clearSorts() {
		SearchUtil.clearSorts(this);
	}

	// Fields
	public Search addField(Field field) {
		SearchUtil.addField(this, field);
		return this;
	}

	public Search addFields(Field... fields) {
		SearchUtil.addFields(this, fields);
		return this;
	}

	/**
	 * If this field is used with <code>resultMode == RESULT_MAP</code>, the <code>property</code> will also be used as
	 * the key for this value in the map.
	 */
	public Search addField(String property) {
		SearchUtil.addField(this, property);
		return this;
	}

	/**
	 * If this field is used with <code>resultMode == RESULT_MAP</code>, the <code>key</code> will be used as the key
	 * for this value in the map.
	 */
	public Search addField(String property, String key) {
		SearchUtil.addField(this, property, key);
		return this;
	}

	/**
	 * If this field is used with <code>resultMode == RESULT_MAP</code>, the <code>property</code> will also be used as
	 * the key for this value in the map.
	 */
	public Search addField(String property, int operator) {
		SearchUtil.addField(this, property, operator);
		return this;
	}

	/**
	 * If this field is used with <code>resultMode == RESULT_MAP</code>, the <code>key</code> will be used as the key
	 * for this value in the map.
	 */
	public Search addField(String property, int operator, String key) {
		SearchUtil.addField(this, property, operator, key);
		return this;
	}

	public void removeField(Field field) {
		SearchUtil.removeField(this, field);
	}

	public void removeField(String property) {
		SearchUtil.removeField(this, property);
	}

	public void removeField(String property, String key) {
		SearchUtil.removeField(this, property, key);
	}

	public void clearFields() {
		SearchUtil.clearFields(this);
	}

	public boolean isDistinct() {
		return distinct;
	}

	public IMutableSearch setDistinct(boolean distinct) {
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

	public void removeFetch(String property) {
		SearchUtil.removeFetch(this, property);
	}

	public void clearFetches() {
		SearchUtil.clearFetches(this);
	}

	public void clear() {
		SearchUtil.clear(this);
	}

	// Paging
	public int getFirstResult() {
		return firstResult;
	}

	public Search setFirstResult(int firstResult) {
		this.firstResult = firstResult;
		return this;
	}

	public int getPage() {
		return page;
	}

	public Search setPage(int page) {
		this.page = page;
		return this;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public Search setMaxResults(int maxResults) {
		this.maxResults = maxResults;
		return this;
	}

	/**
	 * Create a copy of this search. All collections are copied into new collections, but them items in those
	 * collections are not duplicated; they still point to the same objects.
	 */
	public Search copy() {
		Search dest = new Search();
		SearchUtil.copy(this, dest);
		return dest;
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
	 * Filering throw using LIKE condition
	 */
	public Filter[] addFilterOrLike(String field, List<String> listValues, boolean emptyEqual) {
		Filter[] filters = null;
		int cnt = 0;

		if (listValues != null && listValues.size() > 0) {
			if (emptyEqual) {
				filters = new Filter[listValues.size() + 1];
				filters[0] = new Filter(field, "", Filter.OP_EQUAL);
				cnt = 1;
			} else {
				filters = new Filter[listValues.size()];
			}
			for (int i = 0; i < listValues.size(); i++) {
				filters[cnt + i] = new Filter(field, "%" + listValues.get(i) + "%", Filter.OP_LIKE);
			}
		}
		this.addFilterOr(filters);
		return filters;
	}

	/**
	 * Filtering throw using IN condition
	 */
	public void addFilterIn(String field, List<String> listValues, boolean emptyEqual) {
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

			if (App.DATABASE == Database.ORACLE) {
				Filter[] filters = null;

				if (emptyEqual) {
					filters = new Filter[2];
					filters[0] = Filter.isNull(field);
					filters[1] = Filter.in(field, listValues);
				} else {
					filters = new Filter[1];
					filters[0] = Filter.in(field, listValues);
				}

				this.addFilterOr(filters);
			} else {
				this.addFilterIn(field, values);
			}
		}
	}

	/**
	 * Filtering throw using Not IN condition
	 */
	public void addFilterNotIn(String field, List<String> listValues, boolean emptyEqual) {
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

			this.addFilterNotIn(field, values);
		}
	}

	public String getTabelName() {
		return this.tabelName;
	}

	public Search addTabelName(String tabelName) {
		this.tabelName = tabelName;
		return this;
	}

	/**
	 * Return Where Clause which will be added directly to the SQL Query
	 */
	public String getWhereClause() {
		return this.whereClause;
	}

	/**
	 * Set the Where Clause which will be added directly to the SQL Query
	 */
	public Search addWhereClause(String whereClause) {
		this.whereClause = whereClause;
		return this;
	}
}
