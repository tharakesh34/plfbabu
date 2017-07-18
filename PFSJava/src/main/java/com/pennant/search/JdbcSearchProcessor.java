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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.healthmarketscience.sqlbuilder.CustomCondition;
import com.healthmarketscience.sqlbuilder.CustomSql;
import com.healthmarketscience.sqlbuilder.OrderObject;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.ModuleUtil;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.App.Database;

/**
 * A singleton instance of this class is maintained for each SessionFactory.
 */
public class JdbcSearchProcessor implements Serializable {
	private static final long						serialVersionUID	= 4460401213988371185L;
	private static final Logger						logger				= Logger.getLogger(JdbcSearchProcessor.class);

	private static final String						SELECT				= "select";
	private static final String						FROM				= "from";
	private static final String						DISTINCT			= "distinct";
	private transient NamedParameterJdbcTemplate	jdbcTemplate;

	/**
	 * Create a new <code>JdbcSearchProcessor</code> for the given {@link DataSource}.
	 * 
	 * @param dataSource
	 *            The JDBC DataSource to access.
	 */
	public JdbcSearchProcessor(DataSource dataSource) {
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> List<T> getResults(ISearch search) {
		if (search == null) {
			throw new IllegalArgumentException();
		}

		// Prepare the query.
		SelectQuery query = new SelectQuery();
		addSelectList(query, search);
		addTableSource(query, search);
		addWhereClause(query, search);
		addOrderByExpression(query, search);
		query.validate();

		// Change the query to retrieve a portion of the rows.
		String sql = getLimitRowsSql(query, search);
		logger.trace(Literal.SQL + sql);

		// Execute the SQL, binding the arguments.
		if (search.getSearchClass() != null) {
			RowMapper rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(search.getSearchClass());

			return jdbcTemplate.query(sql, rowMapper);
		} else {
			Map<String, Object> paramMap = new HashMap<>();

			return (List<T>) jdbcTemplate.queryForList(sql, paramMap);
		}
	}

	public String getQuery(ISearch search) {
		if (search == null) {
			throw new IllegalArgumentException();
		}

		// Prepare the query.
		SelectQuery query = new SelectQuery();
		addSelectList(query, search);
		addTableSource(query, search);
		addWhereClause(query, search);
		query.validate();

		return query.toString();
	}

	/**
	 * Returns the total number of results that would be returned using the given <code>ISearch</code> if there were no
	 * paging or maxResult limits.
	 * 
	 * @see ISearch
	 */
	public int getCount(ISearch search) {
		if (search == null) {
			throw new IllegalArgumentException();
		}

		// Prepare the query.
		SelectQuery query = new SelectQuery();
		addSelectList(query, "count(*)");
		addTableSource(query, search);
		addWhereClause(query, search);
		query.validate();

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + query.toString());
		Map<String, Object> namedParameters = new HashMap<>();

		return jdbcTemplate.queryForObject(query.toString(), namedParameters, Integer.class);
	}

	/**
	 * Returns a <code>SearchResult</code> object that includes the list of results like <code>search()</code> and the
	 * total length like <code>searchLength</code>. Uses the specified searchClass, ignoring the searchClass specified
	 * on the search itself.
	 * 
	 * @see ISearch
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SearchResult searchAndCount(ISearch search) {
		if (search == null) {
			return null;
		}

		SearchResult result = new SearchResult();
		result.setResult(getResults(search));

		if (search.getMaxResults() > 0) {
			result.setTotalCount(getCount(search));
		} else {
			result.setTotalCount(result.getResult().size() + SearchUtil.calcFirstResult(search));
		}

		return result;
	}

	private String getLimitRowsSql(SelectQuery query, ISearch search) {
		int offset = search.getFirstResult();
		int pageSize = search.getMaxResults();

		// If limits rows not required, no additional processing required.
		if (offset <= 0 && pageSize <= 0) {
			return query.toString();
		}

		switch (App.DATABASE) {
		case ORACLE:
			return getOracleLimitRowsSql(query.toString(), offset, pageSize);
		case SQL_SERVER:
			return getMSSQLLimitString(query.toString(), offset, pageSize);
		case DB2:
			return getDB2LimitString(query.toString(), offset, pageSize);
		case MYSQL:
			return getMYSQLLimitString(query.toString(), offset, pageSize);
		default:
			return query.toString();
		}
	}

	private String getMYSQLLimitString(String sql, int startRow, int endRow) {
		return new StringBuffer(sql.length() + 20).append(sql)
				.append(startRow > 0 ? " limit " + startRow + " , " + endRow : " limit " + endRow).toString();
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
	private String getMSSQLLimitString(String querySqlString, int startRow, int endRow) {
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
	private void replaceDistinctWithGroupBy(StringBuilder sql) {
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
	private CharSequence getSelectFieldsWithoutAliases(StringBuilder sql) {
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
	private String stripAliases(String str) {
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
	private void insertRowNumberFunction(StringBuilder sql, CharSequence orderby) {
		// Find the end of the select statement
		int selectEndIndex = sql.indexOf(SELECT) + SELECT.length();

		// Insert after the select statement the row_number() function:
		sql.insert(selectEndIndex, " ROW_NUMBER() OVER (" + orderby + ") row_nr,");
	}

	private String getDB2LimitString(String sql, int startRow, int endRow) {
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

	/**
	 * Adds the search conditions of the WHERE clause to the SELECT query.
	 * 
	 * @param query
	 *            The select query to which the WHERE clause search conditions to be added.
	 * @param search
	 *            The search object that contains the WHERE clause search conditions.
	 */
	private void addWhereClause(SelectQuery query, ISearch search) {
		// Add search conditions specified in filters.
		for (Filter filter : search.getFilters()) {
			if ("OR".equals(filter.getProperty())) {
				query.addCondition(new CustomCondition(getOrCondition(filter)));
			} else if (filter.getOperator() == Filter.OP_IN || filter.getOperator() == Filter.OP_NOT_IN) {
				query.addCondition(new CustomCondition(getInCondition(filter)));
			} else {
				query.addCondition(new CustomCondition(filter.toString()));
			}
		}

		// Add custom search condition.
		if (search.getWhereClause() != null) {
			query.addCondition(new CustomCondition(search.getWhereClause()));
		}
	}

	/**
	 * Returns the OR condition as specified in the filter.
	 * 
	 * @param filter
	 *            The filter that contain the parameters of OR condition.
	 * @return The OR condition.
	 */
	private String getOrCondition(Filter filter) {
		if (!(filter.getValue() instanceof List<?>)) {
			return "";
		}

		List<?> list = (List<?>) filter.getValue();
		StringBuilder expression = new StringBuilder();

		for (Object object : list) {
			if (object instanceof Filter) {
				Filter condition = (Filter) object;

				if (expression.length() > 0) {
					expression.append(" or ");
				}

				if (condition.getOperator() == Filter.OP_IN || condition.getOperator() == Filter.OP_NOT_IN) {
					expression.append(getInCondition(condition));
				} else {
					expression.append(condition.toString());
				}
			}
		}

		return expression.toString();
	}

	/**
	 * Returns the IN condition as specified in the filter.
	 * 
	 * @param filter
	 *            The filter that contain the parameters of IN condition.
	 * @return The IN condition.
	 */
	private String getInCondition(Filter filter) {
		if (filter.getValue() == null) {
			return "";
		}

		String expression;

		if (filter.getValue() instanceof String[]) {
			expression = StringUtils.join((String[]) filter.getValue(), "','");
		} else if (filter.getValue() instanceof List<?>) {
			expression = StringUtils.join((List<?>) filter.getValue(), "','");
		} else {
			expression = StringUtils.join((Object[]) filter.getValue(), "','");
		}

		return filter.getProperty().concat(filter.getSqlOperator()).concat("('").concat(expression).concat("')");
	}

	/**
	 * Adds the order by expressions to the SELECT query.
	 * 
	 * @param query
	 *            The select query to which the order by expressions to be added.
	 * @param search
	 *            The search object that contains the order by expressions.
	 */
	private void addOrderByExpression(SelectQuery query, ISearch search) {
		for (Sort sort : search.getSorts()) {
			query.addCustomOrdering(sort.getProperty(),
					sort.isDesc() ? OrderObject.Dir.DESCENDING : OrderObject.Dir.ASCENDING);
		}
	}

	/**
	 * Gets the Oracle limit rows statement.
	 * 
	 * @param sql
	 *            The statement to fetch an ordered result set.
	 * @param offset
	 *            The number of rows to offset.
	 * @param pageSize
	 *            The number of rows to fetch.
	 * @return The Oracle limit rows statement.
	 */
	private String getOracleLimitRowsSql(String sql, int offset, int pageSize) {
		StringBuilder result = new StringBuilder(sql);

		if (offset <= 0) {
			result.append(" fetch first ").append(pageSize).append(" rows only");
		} else {
			result.append(" offset ").append(offset).append(" rows fetch next ").append(pageSize).append(" rows only");
		}

		return result.toString();
	}
}
