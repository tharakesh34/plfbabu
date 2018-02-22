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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;

/**
 * Utilities for working with searches {@link ISearch}, {@link IMutableSearch}.
 * 
 */
public class SearchUtil {

	private SearchUtil() {

	}

	protected static void addField(IMutableSearch search, Field field) {
		List<Field> fields = search.getFields();
		if (fields == null) {
			fields = new ArrayList<Field>();
			search.setFields(fields);
		}
		fields.add(field);
	}

	protected static void addFields(IMutableSearch search, Field... fields) {
		if (fields != null) {
			for (Field field : fields) {
				addField(search, field);
			}
		}
	}

	protected static void addField(IMutableSearch search, String property) {
		if (property == null) {
			return; // null properties do nothing, don't bother to add them.
		}
		addField(search, new Field(property));
	}
	
	
	protected static void addFields(IMutableSearch search, String[] properties) {
		if (properties == null) {
			return; // null properties do nothing, don't bother to add them.
		}

		for (String property : properties) {
			addField(search, new Field(property));
		}

	}

	protected static void addField(IMutableSearch search, String property, int operator) {
		if (property == null) {
			return; // null properties do nothing, don't bother to add them.
		}
		addField(search, new Field(property, operator));
	}

	protected static void addField(IMutableSearch search, String property, int operator, String key) {
		if (property == null || key == null) {
			return; // null properties do nothing, don't bother to add them.
		}
		addField(search, new Field(property, operator, key));
	}

	protected static void addField(IMutableSearch search, String property, String key) {
		if (property == null || key == null) {
			return; // null properties do nothing, don't bother to add them.
		}
		addField(search, new Field(property, key));
	}

	protected static void addFilter(IMutableSearch search, Filter filter) {
		List<Filter> filters = search.getFilters();
		if (filters == null) {
			filters = new ArrayList<Filter>();
			search.setFilters(filters);
		}

		// Handle empty string for Oracle
		if (App.DATABASE == Database.ORACLE) {
			if ("".equals(filter.getValue())) {
				if (Filter.OP_EQUAL == filter.getOperator()) {
					filter.setOperator(Filter.OP_NULL);
				} else if (Filter.OP_NOT_EQUAL == filter.getOperator()) {
					filter.setOperator(Filter.OP_NOT_NULL);
				}
			}
		}

		filters.add(filter);
	}

	protected static void addFilters(IMutableSearch search, Filter... filters) {
		if (filters != null) {
			for (Filter filter : filters) {
				addFilter(search, filter);
			}
		}
	}

	protected static void addFilterAnd(IMutableSearch search, Filter... filters) {
		addFilter(search, Filter.and(filters));
	}

	protected static void addFilterEqual(IMutableSearch search, String property, Object value) {
		addFilter(search, Filter.equalTo(property, value));
	}

	protected static void addFilterGreaterOrEqual(IMutableSearch search, String property, Object value) {
		addFilter(search, Filter.greaterOrEqual(property, value));
	}

	protected static void addFilterGreaterThan(IMutableSearch search, String property, Object value) {
		addFilter(search, Filter.greaterThan(property, value));
	}

	protected static void addFilterIn(IMutableSearch search, String property, Collection<?> value) {
		addFilter(search, Filter.in(property, value));
	}

	protected static void addFilterIn(IMutableSearch search, String property, Object... value) {
		addFilter(search, Filter.in(property, value));
	}

	protected static void addFilterLessOrEqual(IMutableSearch search, String property, Object value) {
		addFilter(search, Filter.lessOrEqual(property, value));
	}

	protected static void addFilterLessThan(IMutableSearch search, String property, Object value) {
		addFilter(search, Filter.lessThan(property, value));
	}

	protected static void addFilterLike(IMutableSearch search, String property, String value) {
		value = "%" + value + "%";
		addFilter(search, Filter.like(property, value));
	}

	protected static void addFilterNotEqual(IMutableSearch search, String property, Object value) {
		addFilter(search, Filter.notEqual(property, value));
	}

	protected static void addFilterNotIn(IMutableSearch search, String property, Collection<?> value) {
		addFilter(search, Filter.notIn(property, value));
	}

	protected static void addFilterNotIn(IMutableSearch search, String property, Object... value) {
		addFilter(search, Filter.notIn(property, value));
	}

	protected static void addFilterNotNull(IMutableSearch search, String property) {
		addFilter(search, Filter.isNotNull(property));
	}

	protected static void addFilterNull(IMutableSearch search, String property) {
		addFilter(search, Filter.isNull(property));
	}

	protected static void addFilterOr(IMutableSearch search, Filter... filters) {
		addFilter(search, Filter.or(filters));
	}

	protected static void addSort(IMutableSearch search, Sort sort) {
		if (sort == null) {
			return;
		}

		List<Sort> sorts = search.getSorts();
		if (sorts == null) {
			sorts = new ArrayList<Sort>();
			search.setSorts(sorts);
		}
		sorts.add(sort);
	}

	protected static void addSorts(IMutableSearch search, Sort... sorts) {
		if (sorts != null) {
			for (Sort sort : sorts) {
				addSort(search, sort);
			}
		}
	}

	protected static void addSort(IMutableSearch search, String property, boolean desc) {
		if (property == null) {
			return; // null properties do nothing, don't bother to add them.
		}
		addSort(search, new Sort(property, desc));
	}

	protected static void addSortAsc(IMutableSearch search, String property) {
		addSort(search, property, false);
	}

	protected static void addSortDesc(IMutableSearch search, String property) {
		addSort(search, property, true);
	}

	protected static void removeFetch(IMutableSearch search, String property) {
		if (search.getFetches() != null) {
			search.getFetches().remove(property);
		}
	}

	protected static void removeField(IMutableSearch search, Field field) {
		if (search.getFields() != null) {
			search.getFields().remove(field);
		}
	}

	protected static void removeField(IMutableSearch search, String property) {
		if (search.getFields() == null) {
			return;
		}

		Iterator<Field> itr = search.getFields().iterator();
		while (itr.hasNext()) {
			if (itr.next().getProperty().equals(property)) {
				itr.remove();
			}
		}
	}

	protected static void removeField(IMutableSearch search, String property, String key) {
		if (search.getFields() == null) {
			return;
		}

		Iterator<Field> itr = search.getFields().iterator();
		while (itr.hasNext()) {
			Field field = itr.next();
			if (field.getProperty().equals(property) && field.getKey().equals(key)) {
				itr.remove();
			}
		}
	}

	protected static void removeFilter(IMutableSearch search, Filter filter) {
		List<Filter> filters = search.getFilters();
		if (filters != null) {
			filters.remove(filter);
		}
	}

	protected static void removeFiltersOnProperty(IMutableSearch search, String property) {
		if (property == null || search.getFilters() == null) {
			return;
		}
		Iterator<Filter> itr = search.getFilters().iterator();
		while (itr.hasNext()) {
			if (property.equals(itr.next().getProperty())) {
				itr.remove();
			}
		}
	}

	protected static void removeSort(IMutableSearch search, Sort sort) {
		if (search.getSorts() != null) {
			search.getSorts().remove(sort);
		}
	}

	protected static void removeSort(IMutableSearch search, String property) {
		if (property == null || search.getSorts() == null) {
			return;
		}
		Iterator<Sort> itr = search.getSorts().iterator();
		while (itr.hasNext()) {
			if (property.equals(itr.next().getProperty())) {
				itr.remove();
			}
		}
	}

	protected static void clear(IMutableSearch search) {
		clearFilters(search);
		clearSorts(search);
		clearFields(search);
		clearPaging(search);
		clearFetches(search);
		search.setResultMode(ISearch.RESULT_AUTO);
		search.setDisjunction(false);
	}

	protected static void clearFetches(IMutableSearch search) {
		if (search.getFetches() != null) {
			search.getFetches().clear();
		}
	}

	protected static void clearFields(IMutableSearch search) {
		if (search.getFields() != null) {
			search.getFields().clear();
		}
	}

	protected static void clearFilters(IMutableSearch search) {
		if (search.getFilters() != null) {
			search.getFilters().clear();
		}
	}

	protected static void clearPaging(IMutableSearch search) {
		search.setFirstResult(-1);
		search.setPage(-1);
		search.setMaxResults(-1);
	}

	protected static void clearSorts(IMutableSearch search) {
		if (search.getSorts() != null) {
			search.getSorts().clear();
		}
	}

	protected static void mergeSortsBefore(IMutableSearch search, List<Sort> sorts) {
		List<Sort> list = search.getSorts();
		if (list == null) {
			list = new ArrayList<Sort>();
			search.setSorts(list);
		}

		if (list.size() > 0) {
			// remove any sorts from the search that already sort on the same
			// property as one of the new sorts
			Iterator<Sort> itr = list.iterator();
			while (itr.hasNext()) {
				String property = itr.next().getProperty();
				if (property == null) {
					itr.remove();
				} else {
					for (Sort sort : sorts) {
						if (property.equals(sort.getProperty())) {
							itr.remove();
							break;
						}
					}
				}
			}
		}

		list.addAll(0, sorts);
	}

	protected static void mergeSortsBefore(IMutableSearch search, Sort... sorts) {
		mergeSortsBefore(search, Arrays.asList(sorts));
	}

	protected static void mergeSortsAfter(IMutableSearch search, List<Sort> sorts) {
		List<Sort> list = search.getSorts();
		if (list == null) {
			list = new ArrayList<Sort>();
			search.setSorts(list);
		}

		int origLen = list.size();

		if (origLen > 0) {
			// don't add sorts that are already in the list
			for (Sort sort : sorts) {
				if (sort.getProperty() != null) {
					boolean found = false;
					for (int i = 0; i < origLen; i++) {
						if (sort.getProperty().equals(list.get(i).getProperty())) {
							found = true;
							break;
						}
					}
					if (!found) {
						list.add(sort);
					}
				}
			}
		} else {
			list.addAll(sorts);
		}
	}

	protected static void mergeSortsAfter(IMutableSearch search, Sort... sorts) {
		mergeSortsAfter(search, Arrays.asList(sorts));
	}

	protected static void mergeFetches(IMutableSearch search, List<String> fetches) {
		List<String> list = search.getFetches();
		if (list == null) {
			list = new ArrayList<String>();
			search.setFetches(list);
		}

		for (String fetch : fetches) {
			if (!list.contains(fetch)) {
				list.add(fetch);
			}
		}
	}

	protected static void mergeFetches(IMutableSearch search, String... fetches) {
		mergeFetches(search, Arrays.asList(fetches));
	}

	protected static void mergeFiltersAnd(IMutableSearch search, List<Filter> filters) {
		List<Filter> list = search.getFilters();
		if (list == null) {
			list = new ArrayList<Filter>();
			search.setFilters(list);
		}

		if (list.size() == 0 || !search.isDisjunction()) {
			search.setDisjunction(false);
			list.addAll(filters);
		} else {
			search.setFilters(new ArrayList<Filter>());

			// add the previous filters with an OR
			Filter orFilter = Filter.or();
			orFilter.setValue(list);
			addFilter(search, orFilter);

			// add the new filters with AND
			search.setDisjunction(false);
			search.getFilters().addAll(filters);
		}
	}

	protected static void mergeFiltersAnd(IMutableSearch search, Filter... filters) {
		mergeFiltersAnd(search, Arrays.asList(filters));
	}

	protected static void mergeFiltersOr(IMutableSearch search, List<Filter> filters) {
		List<Filter> list = search.getFilters();
		if (list == null) {
			list = new ArrayList<Filter>();
			search.setFilters(list);
		}

		if (list.size() == 0 || search.isDisjunction()) {
			search.setDisjunction(true);
			list.addAll(filters);
		} else {
			search.setFilters(new ArrayList<Filter>());

			// add the previous filters with an AND
			Filter orFilter = Filter.and();
			orFilter.setValue(list);
			addFilter(search, orFilter);

			// add the new filters with or
			search.setDisjunction(true);
			search.getFilters().addAll(filters);
		}
	}

	protected static void mergeFiltersOr(IMutableSearch search, Filter... filters) {
		mergeFiltersOr(search, Arrays.asList(filters));
	}

	protected static void mergeFieldsBefore(IMutableSearch search, List<Field> fields) {
		List<Field> list = search.getFields();
		if (list == null) {
			list = new ArrayList<Field>();
			search.setFields(list);
		}

		list.addAll(0, fields);
	}

	protected static void mergeFieldsBefore(IMutableSearch search, Field... fields) {
		mergeFieldsBefore(search, Arrays.asList(fields));
	}

	protected static void mergeFieldsAfter(IMutableSearch search, List<Field> fields) {
		List<Field> list = search.getFields();
		if (list == null) {
			list = new ArrayList<Field>();
			search.setFields(list);
		}

		list.addAll(fields);
	}

	protected static void mergeFieldsAfter(IMutableSearch search, Field... fields) {
		mergeFieldsAfter(search, Arrays.asList(fields));
	}

	protected static int calcFirstResult(ISearch search) {
		return (search.getFirstResult() > 0) ? search.getFirstResult()
				: (search.getPage() > 0 && search.getMaxResults() > 0) ? search.getPage() * search.getMaxResults() : 0;
	}

	protected static IMutableSearch shallowCopy(ISearch source, IMutableSearch destination) {
		destination.setSearchClass(source.getSearchClass());
		destination.setDistinct(source.isDistinct());
		destination.setDisjunction(source.isDisjunction());
		destination.setResultMode(source.getResultMode());
		destination.setFirstResult(source.getFirstResult());
		destination.setPage(source.getPage());
		destination.setMaxResults(source.getMaxResults());
		destination.setFetches(source.getFetches());
		destination.setFields(source.getFields());
		destination.setFilters(source.getFilters());
		destination.setSorts(source.getSorts());

		return destination;
	}

	protected static <T extends IMutableSearch> T copy(ISearch source, T destination) {
		shallowCopy(source, destination);

		ArrayList<String> fetches = new ArrayList<String>();
		fetches.addAll(source.getFetches());
		destination.setFetches(fetches);

		ArrayList<Field> fields = new ArrayList<Field>();
		fields.addAll(source.getFields());
		destination.setFields(fields);

		ArrayList<Filter> filters = new ArrayList<Filter>();
		filters.addAll(source.getFilters());
		destination.setFilters(filters);

		ArrayList<Sort> sorts = new ArrayList<Sort>();
		sorts.addAll(source.getSorts());
		destination.setSorts(sorts);

		return destination;
	}

	protected static boolean equals(ISearch search, Object obj) {
		if (search.equals(obj)) {
			return true;
		}
		if (!(obj instanceof ISearch)) {
			return false;
		}
		ISearch s = (ISearch) obj;
		if (search.getSearchClass() == null ? s.getSearchClass() != null
				: !search.getSearchClass().equals(s.getSearchClass())) {
			return false;
		}
		if (search.isDisjunction() != s.isDisjunction() || search.getResultMode() != s.getResultMode()
				|| search.getFirstResult() != s.getFirstResult() || search.getPage() != s.getPage()
				|| search.getMaxResults() != s.getMaxResults()) {
			return false;
		}

		if (search.getFetches() == null ? s.getFetches() != null : !search.getFetches().equals(s.getFetches())) {
			return false;
		}
		if (search.getFields() == null ? s.getFields() != null : !search.getFields().equals(s.getFields())) {
			return false;
		}
		if (search.getFilters() == null ? s.getFilters() != null : !search.getFilters().equals(s.getFilters())) {
			return false;
		}
		if (search.getSorts() == null ? s.getSorts() != null : !search.getSorts().equals(s.getSorts())) {
			return false;
		}

		return true;
	}

	protected static int hashCode(ISearch search) {
		int hash = 1;
		hash = hash * 31 + (search.getSearchClass() == null ? 0 : search.getSearchClass().hashCode());
		hash = hash * 31 + (search.getFields() == null ? 0 : search.getFields().hashCode());
		hash = hash * 31 + (search.getFilters() == null ? 0 : search.getFilters().hashCode());
		hash = hash * 31 + (search.getSorts() == null ? 0 : search.getSorts().hashCode());
		hash = hash * 31 + (search.isDisjunction() ? 1 : 0);
		hash = hash * 31 + (Integer.valueOf(search.getResultMode()).hashCode());
		hash = hash * 31 + (Integer.valueOf(search.getFirstResult()).hashCode());
		hash = hash * 31 + (Integer.valueOf(search.getPage()).hashCode());
		hash = hash * 31 + (Integer.valueOf(search.getMaxResults()).hashCode());

		return hash;
	}

	protected static String toString(ISearch search) {
		StringBuilder sb = new StringBuilder("Search(");
		sb.append(search.getSearchClass());
		sb.append(")[first: ").append(search.getFirstResult());
		sb.append(", page: ").append(search.getPage());
		sb.append(", max: ").append(search.getMaxResults());
		sb.append("] {\n resultMode: ");

		switch (search.getResultMode()) {
		case ISearch.RESULT_AUTO:
			sb.append("AUTO");
			break;
		case ISearch.RESULT_ARRAY:
			sb.append("ARRAY");
			break;
		case ISearch.RESULT_LIST:
			sb.append("LIST");
			break;
		case ISearch.RESULT_MAP:
			sb.append("MAP");
			break;
		case ISearch.RESULT_SINGLE:
			sb.append("SINGLE");
			break;
		default:
			sb.append("**INVALID RESULT MODE: (" + search.getResultMode() + ")**");
			break;
		}

		sb.append(",\n disjunction: ").append(search.isDisjunction());
		sb.append(",\n fields: { ");
		appendList(sb, search.getFields(), ", ");
		sb.append(" },\n filters: {\n  ");
		appendList(sb, search.getFilters(), ",\n  ");
		sb.append("\n },\n sorts: { ");
		appendList(sb, search.getSorts(), ", ");
		sb.append(" }\n}");

		return sb.toString();
	}
	
	protected static void addFilterOrLike(IMutableSearch search, String property, List<String> listValues,
			boolean emptyEqual) {
		Filter[] filters = null;
		int cnt = 0;

		if (listValues != null && listValues.size() > 0) {
			if (emptyEqual) {
				filters = new Filter[listValues.size() + 1];
				filters[0] = new Filter(property, "", Filter.OP_EQUAL);
				cnt = 1;
			} else {
				filters = new Filter[listValues.size()];
			}
			for (int i = 0; i < listValues.size(); i++) {
				filters[cnt + i] = new Filter(property, "%" + listValues.get(i) + "%", Filter.OP_LIKE);
			}
		}
		addFilterOr(search, filters);
	}
	
	protected static void addFilterIn(IMutableSearch search, String property, List<String> listValues, boolean emptyEqual) {
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

			Filter[] filters = null;

			switch (App.DATABASE) {
			case ORACLE:
				if (emptyEqual) {
					filters = new Filter[2];
					filters[0] = Filter.isNull(property);
					filters[1] = Filter.in(property, listValues);
				} else {
					filters = new Filter[1];
					filters[0] = Filter.in(property, listValues);
				}

				addFilterOr(search, filters);
				break;

			case POSTGRES:
				if (emptyEqual) {
					filters = new Filter[3];
					filters[0] = Filter.isNull(property);
					filters[1] = Filter.equalTo(property, "");
					filters[2] = Filter.in(property, listValues);
				} else {
					filters = new Filter[1];
					filters[0] = Filter.in(property, listValues);
				}
				addFilterOr(search, filters);
				break;

			default:
				addFilterIn(search, property, values);
				break;
			}
		}
	}

	private static void appendList(StringBuilder sb, List<?> list, String separator) {
		if (list == null) {
			sb.append("null");
			return;
		}

		boolean first = true;
		for (Object o : list) {
			if (first) {
				first = false;
			} else {
				sb.append(separator);
			}
			sb.append(o);
		}
	}
	
	protected static class ItemVisitor<T> {
		protected T visit(T item) {
			return item;
		}
	}

	protected static class FilterVisitor {
		protected Filter visitBefore(Filter filter) {
			return filter;
		}

		protected Filter visitAfter(Filter filter) {
			return filter;
		}
	}
}
