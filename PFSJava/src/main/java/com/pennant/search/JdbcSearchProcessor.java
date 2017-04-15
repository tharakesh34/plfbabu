/**
 * Copyright 2009 The Revere Group
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.pennant.search;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.CustomCondition;
import com.healthmarketscience.sqlbuilder.CustomSql;
import com.healthmarketscience.sqlbuilder.OrderObject;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.App.Database;
import com.pennanttech.pff.core.util.ModuleUtil;

/**
 * 
 * A singleton instance of this class is maintained for each SessionFactory.
 * 
 * @author dwolverton
 */
public class JdbcSearchProcessor {
	private static final Logger							logger		= Logger.getLogger(JdbcSearchProcessor.class);

	private static final String							SELECT		= "select";
	private static final String							FROM		= "from";
	private static final String							DISTINCT	= "distinct";

	private static Map<DataSource, JdbcSearchProcessor>	map			= new HashMap<>();
	private NamedParameterJdbcTemplate					namedParameterJdbcTemplate;

	public static JdbcSearchProcessor getInstanceForDataSource(DataSource dataSource) {
		logger.debug("Entering");
		JdbcSearchProcessor instance = map.get(dataSource);
		if (instance == null) {
			instance = new JdbcSearchProcessor();
			instance.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			map.put(dataSource, instance);
		}
		logger.debug("Leaving");
		return instance;
	}

	public String getSearchQuery(ISearch search) {
		return search == null ? null : getQuery(search);
	}

	/**
	 * Get the results for the specified <code>ISearch</code> object.
	 * 
	 * @param search
	 *            The search object that contains the parameters.
	 * @return The results mapped to a List (one entry for each row).
	 * @throws IllegalArgumentException
	 *             - If the given search object is <code>null</code>.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> List<T> getResults(ISearch search) {
		if (search == null) {
			throw new IllegalArgumentException();
		}

		// Prepare the query.
		SelectQuery query = new SelectQuery();
		addSelectList(query, search);
		addTableSource(query, search);

		// Add where conditions
		addWhereClause(query, search);

		// Add direct where clause sent by client
		if (search.getWhereClause() != null) {
			query.addCondition(new CustomCondition(search.getWhereClause()));
		}
		// Add order by conditions
		if (search.getFilters() != null) {
			List<Sort> sorts = search.getSorts();

			for (Iterator iterator = sorts.iterator(); iterator.hasNext();) {
				Sort sortField = (Sort) iterator.next();
				query.addCustomOrdering(sortField.getProperty(),
						sortField.isDesc() ? OrderObject.Dir.DESCENDING : OrderObject.Dir.ASCENDING);
			}
		}

		logger.trace("1SQL : " + query.toString());
		query.validate();

		boolean firstResult = false;
		if (search.getFirstResult() > 0) {
			firstResult = true;
		}
		logger.trace("2SQL : "
				+ getLimitString(query.toString(), firstResult, search.getFirstResult(), search.getMaxResults()));

		List resultList = null;

		if (search.getSearchClass() != null) {
			RowMapper rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(search.getSearchClass());
			try {
				resultList = namedParameterJdbcTemplate.query(
						getLimitString(query.toString(), firstResult, search.getFirstResult(), search.getMaxResults()),
						rowMapper);

			} catch (Exception e) {
				logger.debug(e);
			}
		} else {
			Map<String, Object> paramMap = new HashMap<>();
			resultList = namedParameterJdbcTemplate.queryForList(
					getLimitString(query.toString(), firstResult, search.getFirstResult(), search.getMaxResults()),
					paramMap);
		}

		return resultList;
	}

	@SuppressWarnings({ "rawtypes" })
	public String getQuery(ISearch search) {
		if (search == null) {
			throw new IllegalArgumentException();
		}

		// Prepare the query.
		SelectQuery query = new SelectQuery();
		addSelectList(query, search);
		addTableSource(query, search);

		// Add where conditions
		addWhereClause(query, search);

		// Add direct where clause sent by client
		if (search.getWhereClause() != null) {
			query.addCondition(new CustomCondition(search.getWhereClause()));
		}
		// Add order by conditions
		if (search.getFilters() != null) {
			List sorts = search.getSorts();

			for (Iterator iterator = sorts.iterator(); iterator.hasNext();) {
				Sort sortField = (Sort) iterator.next();
				query.addCustomOrdering(sortField.getProperty(),
						sortField.isDesc() ? OrderObject.Dir.DESCENDING : OrderObject.Dir.ASCENDING);
			}
		}

		logger.debug("Query : " + query.toString());
		query.validate();

		return query.toString();
	}

	/**
	 * Returns the total number of results that would be returned using the given <code>ISearch</code> if there were no
	 * paging or maxResult limits. Uses the specified searchClass, ignoring the searchClass specified on the search
	 * itself.
	 * 
	 * @see ISearch
	 */
	public int count(ISearch search) {
		if (search == null) {
			throw new IllegalArgumentException();
		}

		// Prepare the query.
		SelectQuery query = new SelectQuery();
		addSelectList(query, "count(*)");
		addTableSource(query, search);

		// Add where conditions
		addWhereClause(query, search);

		// Add direct where clause sent by client
		if (search.getWhereClause() != null) {
			query.addCondition(new CustomCondition(search.getWhereClause()));
		}

		query.validate();
		logger.debug("3SQL : " + query.toString());

		Map<String, Object> namedParameters = new HashMap<>();

		return namedParameterJdbcTemplate.queryForObject(query.toString(), namedParameters, Integer.class);
	}

	/**
	 * Returns a <code>SearchResult</code> object that includes the list of results like <code>search()</code> and the
	 * total length like <code>searchLength</code>. Uses the specified searchClass, ignoring the searchClass specified
	 * on the search itself.
	 * 
	 * @see ISearch
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SearchResult searchAndCount(ISearch search) {
		if (search == null) {
			return null;
		}

		SearchResult result = new SearchResult();
		result.setResult(getResults(search));

		if (search.getMaxResults() > 0) {
			result.setTotalCount(count(search));
		} else {
			result.setTotalCount(result.getResult().size() + SearchUtil.calcFirstResult(search));
		}

		return result;
	}

	public static String getLimitString(String sql, boolean hasOffset, int startRow, int endRow) {
		switch (App.DATABASE) {
		case SQL_SERVER:
			return getMSSQLLimitString(sql, hasOffset, startRow, endRow);
		case ORACLE:
			return getORACLELimitString(sql, hasOffset, startRow, endRow);
		case DB2:
			return getDB2LimitString(sql, hasOffset, startRow, endRow);
		case MYSQL:
			return getMYSQLLimitString(sql, hasOffset, startRow, endRow);
		default:
			return null;
		}
	}

	public static String getMYSQLLimitString(String sql, boolean hasOffset, int startRow, int endRow) {

		return new StringBuffer(sql.length() + 20).append(sql)
				.append(hasOffset ? " limit " + startRow + " , " + endRow : " limit " + endRow).toString();
	}

	public static String getMSSQLLimitString(String sql, boolean hasOffset, int startRow, int endRow) {
		if (startRow > 1 || endRow > 1) {
			return getMSSQLLimitString(sql, startRow, endRow);
		}
		return sql;
	}

	public static String getDB2LimitString(String sql, boolean hasOffset, int startRow, int endRow) {
		if (startRow > 1 || endRow > 1) {
			return getDB2LimitString(sql, startRow, endRow);
		}
		return sql;
	}

	/**
	 * Add a LIMIT clause to the given SQL SELECT (HHH-2655: ROW_NUMBER for Paging)
	 * 
	 * The LIMIT SQL will look like:
	 * 
	 * <pre>
	 * WITH query AS (
	 *   SELECT ROW_NUMBER() OVER (ORDER BY orderby) as __hibernate_row_nr__, 
	 *   original_query_without_orderby
	 * )
	 * SELECT * FROM query WHERE __hibernate_row_nr__ BEETWIN offset AND offset + last
	 * </pre>
	 * 
	 * 
	 * @param querySqlString
	 *            The SQL statement to base the limit query off of.
	 * @param offset
	 *            Offset of the first row to be returned by the query (zero-based)
	 * @param limit
	 *            Maximum number of rows to be returned by the query
	 * 
	 * @return A new SQL statement with the LIMIT clause applied.
	 */
	public static String getMSSQLLimitString(String querySqlString, int startRow, int endRow) {
		StringBuilder sb = new StringBuilder(querySqlString.trim().toLowerCase());

		int orderByIndex = sb.indexOf("order by");
		CharSequence orderby = orderByIndex > 0 ? sb.subSequence(orderByIndex, sb.length())
				: "ORDER BY CURRENT_TIMESTAMP";

		if (startRow != 0) {
			endRow = startRow + endRow;
			startRow = startRow + 1;
		}

		// Delete the order by clause at the end of the query
		if (orderByIndex > 0) {
			sb.delete(orderByIndex, orderByIndex + orderby.length());
		}

		// HHH-5715 bug fix
		replaceDistinctWithGroupBy(sb);

		insertRowNumberFunction(sb, orderby);

		// Wrap the query within a with statement:
		sb.insert(0, "WITH query AS (").append(") SELECT * FROM query ");
		sb.append("WHERE row_nr BETWEEN " + startRow + " AND " + endRow);

		return sb.toString();
	}

	/**
	 * Utility method that checks if the given sql query is a select distinct one and if so replaces the distinct select
	 * with an equivalent simple select with a group by clause. See
	 * {@link SQLServer2005DialectTestCase#testReplaceDistinctWithGroupBy()}
	 * 
	 * @param sql
	 *            an sql query
	 */
	protected static void replaceDistinctWithGroupBy(StringBuilder sql) {
		int distinctIndex = sql.indexOf(DISTINCT);
		if (distinctIndex > 0) {
			sql.delete(distinctIndex, distinctIndex + DISTINCT.length() + 1);
			sql.append(" group by").append(getSelectFieldsWithoutAliases(sql));
		}
	}

	/**
	 * This utility method searches the given sql query for the fields of the select statement and returns them without
	 * the aliases. See {@link SQLServer2005DialectTestCase#testGetSelectFieldsWithoutAliases()}
	 * 
	 * @param an
	 *            sql query
	 * @return the fields of the select statement without their alias
	 */
	protected static CharSequence getSelectFieldsWithoutAliases(StringBuilder sql) {
		String select = sql.substring(sql.indexOf(SELECT) + SELECT.length(), sql.indexOf(FROM));

		// Strip the as clauses
		return stripAliases(select);
	}

	/**
	 * Utility method that strips the aliases. See {@link SQLServer2005DialectTestCase#testStripAliases()}
	 * 
	 * @param a
	 *            string to replace the as statements
	 * @return a string without the as statements
	 */
	protected static String stripAliases(String str) {
		return str.replaceAll("\\sas[^,]+(,?)", "$1");
	}

	/**
	 * Right after the select statement of a given query we must place the row_number function
	 * 
	 * @param sql
	 *            the initial sql query without the order by clause
	 * @param orderby
	 *            the order by clause of the query
	 */
	protected static void insertRowNumberFunction(StringBuilder sql, CharSequence orderby) {
		// Find the end of the select statement
		int selectEndIndex = sql.indexOf(SELECT) + SELECT.length();

		// Insert after the select statement the row_number() function:
		sql.insert(selectEndIndex, " ROW_NUMBER() OVER (" + orderby + ") row_nr,");
	}

	public static String getDB2LimitString(String sql, int startRow, int endRow) {

		int startOfSelect = sql.toLowerCase().indexOf("select");
		StringBuffer pagingSelect = new StringBuffer(sql.length() + 100).append(sql.substring(0, startOfSelect)) // add the comment
				.append("select * from ( select ") // nest the main query in an
				// outer select
				.append(getRowNumber(sql)); // add the rownnumber bit into the
		// outer query select list

		if (startRow != 0) {
			endRow = startRow + endRow;
		}

		if (hasDistinct(sql)) {
			pagingSelect.append(" row_.* from ( ") // add another (inner) nested
					// select
					.append(sql.substring(startOfSelect)) // add the main query
					.append(" ) as row_"); // close off the inner nested select
		} else {
			pagingSelect.append("Results.* From(" + sql.substring(startOfSelect) + ") as Results"); // add the
			// main
			// query
		}
		pagingSelect.append(" ) as temp_ where rownumber_ "); // add the
																// restriction
																// to the outer
																// select
		pagingSelect.append(" between " + (startRow + 1) + " and " + endRow);

		return pagingSelect.toString();

	}

	public static String getORACLELimitString(String sql, boolean hasOffset, int startRow, int endRow) {
		// Condition added to by pass the usage of valid item in extended combo box
		if (startRow > 1 || endRow > 1) {
			sql = sql.trim();
			boolean isForUpdate = false;
			if (sql.toLowerCase().endsWith(" for update")) {
				sql = sql.substring(0, sql.length() - 11);
				isForUpdate = true;
			}
			StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
			if (hasOffset) {
				pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
			} else {
				pagingSelect.append("select * from ( ");
			}
			pagingSelect.append(sql);
			if (hasOffset) {
				pagingSelect.append(" ) row_ ) where rownum_ <= " + (endRow + startRow) + " and rownum_ > " + startRow);
			} else {
				pagingSelect.append(" ) where rownum <= " + endRow);
			}
			if (isForUpdate) {
				pagingSelect.append(" for update");
			}

			return pagingSelect.toString();
		}

		return sql;
	}

	private static String getRowNumber(String sql) {
		StringBuilder rownumber = new StringBuilder(50).append("rownumber() over(");
		int orderByIndex = sql.toLowerCase().indexOf("order by");
		if (orderByIndex > 0 && !hasDistinct(sql)) {
			rownumber.append(sql.substring(orderByIndex));
		}
		rownumber.append(") as rownumber_,");
		return rownumber.toString();
	}

	private static boolean hasDistinct(String sql) {
		return sql.toLowerCase().indexOf("select distinct") >= 0;
	}

	@SuppressWarnings("unchecked")
	private Condition setInCond(Filter filter) {

		String inCondString = "";
		String[] strArray = null;
		List<String> objArray = null;
		Object[] valArray = null;

		if (filter.getValue() instanceof String[]) {
			strArray = (String[]) filter.getValue();
		} else if (filter.getValue() instanceof List) {
			objArray = (List<String>) filter.getValue();
		} else {
			valArray = (Object[]) filter.getValue();
		}

		if (strArray != null) {
			for (int i = 0; i < strArray.length; i++) {
				if (i != 0) {
					inCondString = inCondString.concat(",");
				}
				inCondString = inCondString.concat("'" + strArray[i] + "'");
			}
		}

		if (objArray != null) {
			for (int i = 0; i < objArray.size(); i++) {
				if (i != 0) {
					inCondString = inCondString.concat(",");
				}
				inCondString = inCondString.concat("'" + objArray.get(i) + "'");
			}
		}

		if (valArray != null) {
			inCondString = valArray[0].toString();
		}
		inCondString = "(" + inCondString + ")";
		return new CustomCondition(filter.getProperty() + filter.getSqlOperator() + inCondString);

	}

	@SuppressWarnings("unchecked")
	private void addWhereClause(SelectQuery query, ISearch search) {
		if (search.getFilters() == null) {
			return;
		}

		for (Filter filter : search.getFilters()) {
			if ("OR".equals(filter.getProperty())) {
				List<Filter> subFilters = (List<Filter>) filter.getValue();
				StringBuilder whereClause = new StringBuilder();

				for (Filter subFilter : subFilters) {
					if (whereClause.length() > 0) {
						whereClause.append(" or ");
					}

					if (subFilter.getOperator() == Filter.OP_IN || subFilter.getOperator() == Filter.OP_NOT_IN) {
						whereClause.append(setInCond(subFilter));
					} else {
						String andCond = subFilter.toString().trim();
						whereClause.append(new CustomCondition(andCond));
					}
				}

				query.addCondition(new CustomCondition(whereClause.toString()));
			} else if (filter.getOperator() == Filter.OP_IN || filter.getOperator() == Filter.OP_NOT_IN) {
				query.addCondition(setInCond(filter));
			} else {
				String andCond = filter.toString().trim();
				query.addCondition(new CustomCondition(andCond));
			}
		}
	}

	/**
	 * Adds the given columns to the SELECT query. If no columns specified adds the ALL_SYMBOL (*).
	 * 
	 * @param query
	 *            The select query to which the columns to be added.
	 * @param search
	 *            The search object that contains the columns.
	 */
	private void addSelectList(SelectQuery query, ISearch search) {
		if (search.getFields().isEmpty()) {
			query.addCustomColumns(new CustomSql("*"));

			return;
		}

		for (Field field : search.getFields()) {
			query.addCustomColumns(new CustomSql(field.property));
		}
	}

	/**
	 * Adds the given columns to the SELECT query.
	 * 
	 * @param query
	 *            The select query to which the columns to be added.
	 * @param selectList
	 *            The columns to be added. The select list is a series of expressions separated by commas.
	 */
	private void addSelectList(SelectQuery query, String selectList) {
		query.addCustomColumns(new CustomSql(selectList));
	}

	/**
	 * Adds the table source to the SELECT query.
	 * 
	 * @param query
	 *            The select query to which the table source to be added.
	 * @param search
	 *            The search object that contains the table source.
	 */
	private void addTableSource(SelectQuery query, ISearch search) {
		String tableName = search.getTabelName();

		if (StringUtils.isBlank(tableName)) {
			tableName = ModuleUtil.getTableName(search.getSearchClass().getSimpleName());
		}

		if (App.DATABASE == Database.SQL_SERVER) {
			tableName = tableName.concat(" with (nolock)");
		}

		query.addCustomFromTable(tableName);
	}
}
