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
package com.pennant.search;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.ComboCondition;
import com.healthmarketscience.sqlbuilder.ComboCondition.Op;
import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.CustomCondition;
import com.healthmarketscience.sqlbuilder.CustomSql;
import com.healthmarketscience.sqlbuilder.OrderObject;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.UnaryCondition;
import com.healthmarketscience.sqlbuilder.custom.NamedParamObject;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;

/**
 * A singleton instance of this class is maintained for each SessionFactory.
 */
public class JdbcSearchProcessor implements Serializable {
	private static final long serialVersionUID = 4460401213988371185L;
	private static final Logger logger = Logger.getLogger(JdbcSearchProcessor.class);

	private transient NamedParameterJdbcTemplate jdbcTemplate;

	private enum Clause {
		SELECT("SELECT "),
		DISTINCT(" DISTINCT"),
		FROM(" FROM"),
		GROUP_BY(" GROUP BY"),
		ORDER_BY(" ORDER BY"),
		LIMIT(" LIMIT "),
		OFFSET(" OFFSET ");

		private String key;

		private Clause(String key) {
			this.key = key;
		}
	}

	/**
	 * Creates a new <code>JdbcSearchProcessor</code> for the given {@link DataSource}.
	 * 
	 * @param dataSource
	 *            The JDBC data source to access.
	 */
	public JdbcSearchProcessor(DataSource dataSource) {
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * Get the results for the specified <code>ISearch</code> along with the number of records if requested.
	 * 
	 * @param search
	 *            The search object that contains the parameters.
	 * @return The {@link SearchResult} object.
	 * @throws IllegalArgumentException
	 *             - If the given search object is <code>null</code>.
	 */
	@SuppressWarnings("unchecked")
	public <T> SearchResult<T> getResults(ISearch search, boolean includeCount) {
		if (search == null) {
			throw new IllegalArgumentException();
		}

		SearchResult<T> result = new SearchResult<>();

		result.setResult((List<T>) getResults(search));

		if (includeCount) {
			if (search.getMaxResults() > 0) {
				result.setTotalCount(getCount(search));
			} else {
				result.setTotalCount(result.getResult().size() + SearchUtil.calcFirstResult(search));
			}
		}

		return result;
	}

	/**
	 * Get the results for the specified <code>ISearch</code>.
	 * 
	 * @param search
	 *            The search object that contains the parameters.
	 * @return The results mapped to a <code>List</code> (one entry for each row).
	 * @throws IllegalArgumentException
	 *             - If the given search object is <code>null</code>.
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getResults(ISearch search) {
		if (search == null) {
			throw new IllegalArgumentException();
		}

		// Prepare the query.
		SelectQuery query = new SelectQuery();
		addSelectList(query, search);
		addTableSource(query, search);
		MapSqlParameterSource paramSource = addWhereClause(query, search);
		addOrderByExpression(query, search);
		query.validate();

		// Change the query to retrieve a portion of the rows.
		String sql = getLimitRowsSql(query, search);
		logger.trace(Literal.SQL + sql);

		// Execute the SQL, binding the arguments.
		if (search.getSearchClass() != null) {
			RowMapper<?> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(search.getSearchClass());

			return (List<T>) jdbcTemplate.query(sql, paramSource, rowMapper);
		} else {
			return (List<T>) jdbcTemplate.queryForList(sql, paramSource);
		}
	}

	/**
	 * Get the number of records for the specified <code>ISearch</code>.
	 * 
	 * @param search
	 *            The search object that contains the parameters.
	 * @return The number of records.
	 * @throws IllegalArgumentException
	 *             - If the given search object is <code>null</code>.
	 */
	public int getCount(ISearch search) {
		if (search == null) {
			throw new IllegalArgumentException();
		}

		// Prepare the query.
		SelectQuery query = new SelectQuery();
		addSelectList(query, "count(*)");
		addTableSource(query, search);
		MapSqlParameterSource paramSource = addWhereClause(query, search);
		query.validate();

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + query.toString());
		return jdbcTemplate.queryForObject(query.toString(), paramSource, Integer.class);
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
	 * Adds the search conditions of the WHERE clause to the SELECT query. The conditions will be prepared with named
	 * parameters and stores the values for those named parameters in <code>MapSqlParameterSource</code> and return the
	 * same.
	 * 
	 * @param query
	 *            The select query to which the WHERE clause search conditions to be added.
	 * @param search
	 *            The search object that contains the WHERE clause search conditions.
	 * @return The container of arguments to bind to the query.
	 */
	private MapSqlParameterSource addWhereClause(SelectQuery query, ISearch search) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource();

		// Add search conditions specified in filters.
		for (Filter filter : search.getFilters()) {
			if ("AND".equals(filter.getProperty())) {
				query.addCondition(getLogicalCondition(Op.AND, filter, paramSource));
			} else if ("OR".equals(filter.getProperty())) {
				query.addCondition(getLogicalCondition(Op.OR, filter, paramSource));
			} else {
				query.addCondition(getComparisonCondition(filter, paramSource));
			}
		}

		// Add custom search condition, if any.
		if (search.getWhereClause() != null) {
			query.addCondition(new CustomCondition(search.getWhereClause()));
		}

		return paramSource;
	}

	/**
	 * Returns the logical condition as specified in the filter.
	 *
	 * @param operator
	 *            The logical operator (<code>AND | OR</code>).
	 * @param filter
	 *            The filter that contain the parameters of the condition.
	 * @param paramSource
	 *            The container of arguments to bind to the query.
	 * @return The logical condition (<code>AND | OR</code>). <code>null</code> if invalid parameters specified for the
	 *         condition.
	 */
	private ComboCondition getLogicalCondition(Op operator, Filter filter, MapSqlParameterSource paramSource) {
		if (!(filter.getValue() instanceof List<?>)) {
			return null;
		}

		List<?> list = (List<?>) filter.getValue();

		ComboCondition condition = new ComboCondition(operator);
		for (Object object : list) {
			if (object instanceof Filter) {
				condition.addCondition(getComparisonCondition((Filter) object, paramSource));
			}
		}

		if (condition.isEmpty()) {
			return null;
		}

		return condition;
	}

	/**
	 * Gets a comparison condition. The valid comparison operators are:<br/>
	 * <code>=, <>, <, >, <=, >=, LIKE, IS NULL, IS NOT NULL, IN, and NOT IN</code>.
	 * 
	 * @param filter
	 *            The filter object that contains the parameters of the condition.
	 * @param paramSource
	 *            The container of arguments to bind to the query.
	 * @return The comparison condition. <code>null</code> if invalid operator specified.
	 */
	private Condition getComparisonCondition(Filter filter, MapSqlParameterSource paramSource) {
		// Set the unique parameter name.
		String paramName = StringUtils.upperCase(filter.getProperty());
		int i = 0;

		while (paramSource.hasValue(paramName)) {
			paramName = StringUtils.upperCase(filter.getProperty()).concat(String.valueOf(++i));
		}

		// Add the parameter to the source along with parameterized condition.
		CustomSql column = new CustomSql(filter.getProperty());
		NamedParamObject namedParam = new NamedParamObject(paramName);
		Object value = filter.getValue();
		List<?> values; // Applicable for IN and NOT IN conditions.

		switch (filter.getOperator()) {
		case Filter.OP_EQUAL:
			paramSource.addValue(paramName, value);

			return BinaryCondition.equalTo(column, namedParam);
		case Filter.OP_NOT_EQUAL:
			paramSource.addValue(paramName, value);

			return BinaryCondition.notEqualTo(column, namedParam);
		case Filter.OP_LESS_THAN:
			paramSource.addValue(paramName, value);

			return BinaryCondition.lessThan(column, namedParam, false);
		case Filter.OP_GREATER_THAN:
			paramSource.addValue(paramName, value);

			return BinaryCondition.greaterThan(column, namedParam, false);
		case Filter.OP_LESS_OR_EQUAL:
			paramSource.addValue(paramName, value);

			return BinaryCondition.lessThan(column, namedParam, true);
		case Filter.OP_GREATER_OR_EQUAL:
			paramSource.addValue(paramName, value);

			return BinaryCondition.greaterThan(column, namedParam, true);
		case Filter.OP_LIKE:
			paramSource.addValue(paramName, value);

			return BinaryCondition.like(column, namedParam);
		case Filter.OP_NULL:
			return UnaryCondition.isNull(column);
		case Filter.OP_NOT_NULL:
			return UnaryCondition.isNotNull(column);
		case Filter.OP_IN:
			values = value instanceof List<?> ? (List<?>) value : Arrays.asList((Object[]) value);
			paramSource.addValue(paramName, values);

			return new CustomCondition(column + " in (" + namedParam + ")");
		case Filter.OP_NOT_IN:
			values = value instanceof List<?> ? (List<?>) value : Arrays.asList((Object[]) value);
			paramSource.addValue(paramName, values);

			return new CustomCondition(column + " not in (" + namedParam + ")");
		default:
			return null;
		}
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
	 * Gets the SELECT query that limits the number of records that will be returned based on the specified offset and
	 * number of records. If limits rows not specified returns the actual SELECT query.
	 * 
	 * @param query
	 *            The select query to fetch an ordered result set.
	 * @param search
	 *            The search object that contains the parameters of offset and number of records.
	 * @return The SELECT query that limits the number of records.
	 */
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
			return getSqlServerLimitRowsSql(query.toString(), offset, pageSize);
		case DB2:
			return getDB2LimitRowsSql(query.toString(), offset, pageSize);
		case MY_SQL:
			return getMySqlLimitRowsSql(query.toString(), offset, pageSize);
		case POSTGRES:
			return getPostgreSqlLimitRowsSql(query.toString(), offset, pageSize);
		default:
			return query.toString();
		}
	}

	/**
	 * Gets the <code>Oracle</code> limit rows statement. e.g., <br/>
	 * <code><i>SELECT * FROM EMPLOYEE ORDER BY ID ASC</i> OFFSET 20 ROWS FETCH NEXT 10 ROWS ONLY;</code>
	 * 
	 * @param sql
	 *            The statement to fetch an ordered result set.
	 * @param offset
	 *            The number of rows to offset.
	 * @param pageSize
	 *            The number of rows to fetch.
	 * @return The <code>Oracle</code> limit rows statement.
	 */
	private String getOracleLimitRowsSql(String sql, int offset, int pageSize) {
		StringBuilder result = new StringBuilder(sql);

		if (offset <= 0) {
			result.append(" fetch first ").append(pageSize).append(" rows only");
		} else {
			result.append(Clause.OFFSET.key).append(offset).append(" rows fetch next ").append(pageSize)
					.append(" rows only");
		}

		return result.toString();
	}

	/**
	 * Gets the <code>Microsoft SQL Server</code> limit rows statement. e.g., <br/>
	 * <code>with query as ( </br>
	 * &nbsp; <i>SELECT </i>row_number() over ( <i>ORDER BY ID ASC</i> ) row_nr,<i>* FROM EMPLOYEE</i></br>
	 * ) select * from query where row_nr between 21 and 30;</code>
	 * 
	 * @param sql
	 *            The statement to fetch an ordered result set.
	 * @param offset
	 *            The number of rows to offset.
	 * @param pageSize
	 *            The number of rows to fetch.
	 * @return The <code>Microsoft SQL Server</code> limit rows statement.
	 */
	private String getSqlServerLimitRowsSql(String sql, int offset, int pageSize) {
		StringBuilder result = new StringBuilder(sql);

		// Extract the order by clause and remove from the actual SQL.
		String orderByClause;
		int index = result.indexOf(Clause.ORDER_BY.key);

		if (index > 0) {
			orderByClause = result.substring(index);
			result.delete(index, index + orderByClause.length());
		} else {
			orderByClause = " order by current_timestamp";
		}

		// Replace distinct with group by clause.
		index = result.toString().toUpperCase().indexOf(Clause.DISTINCT.key);

		if (index > 0) {
			result.delete(index, index + Clause.DISTINCT.key.length());

			String groupByClause = Clause.GROUP_BY.key + " "
					+ result.substring(Clause.SELECT.key.length(), result.indexOf(Clause.FROM.key));
			groupByClause = groupByClause.replaceAll("\\sas[^,]+(,?)", "$1");

			result.append(groupByClause);
		}

		// Insert ROW_NUMBER() and wrap the query within WITH statement.
		result.insert(Clause.SELECT.key.length(), "row_number() over (".concat(orderByClause).concat(" ) row_nr,"));

		result.insert(0, "with query as ( ").append(" ) select * from query ");
		result.append("where row_nr between ").append(offset + 1).append(" and ").append(offset + pageSize);

		return result.toString();
	}

	/**
	 * Gets the <code>DB2</code> limit rows statement. e.g., <br/>
	 * <code>select * from ( </br>
	 * &nbsp; <i>SELECT </i>row_number() over ( <i>ORDER BY ID ASC</i> ) row_nr,<i>* FROM EMPLOYEE</i></br>
	 * ) where row_nr between 21 and 30;</code>
	 * 
	 * @param sql
	 *            The statement to fetch an ordered result set.
	 * @param offset
	 *            The number of rows to offset.
	 * @param pageSize
	 *            The number of rows to fetch.
	 * @return The <code>DB2</code> limit rows statement.
	 */
	private String getDB2LimitRowsSql(String sql, int offset, int pageSize) {
		StringBuilder result = new StringBuilder(sql);

		// Extract the order by clause and remove from the actual SQL.
		String orderByClause = "";
		int index = result.indexOf(Clause.ORDER_BY.key);

		if (index > 0) {
			orderByClause = result.substring(index);
			result.delete(index, index + orderByClause.length());
		}

		// Replace distinct with group by clause.
		index = result.toString().toUpperCase().indexOf(Clause.DISTINCT.key);

		if (index > 0) {
			result.delete(index, index + Clause.DISTINCT.key.length());

			String groupByClause = Clause.GROUP_BY.key + " "
					+ result.substring(Clause.SELECT.key.length(), result.indexOf(Clause.FROM.key));
			groupByClause = groupByClause.replaceAll("\\sas[^,]+(,?)", "$1");

			result.append(groupByClause);
		}

		// Insert ROW_NUMBER() and wrap the query within SELECT statement.
		result.insert(Clause.SELECT.key.length(), "row_number() over (".concat(orderByClause).concat(" ) row_nr,"));

		result.insert(0, "select * from ( ").append(" ) ");
		result.append("where row_nr between ").append(offset + 1).append(" and ").append(offset + pageSize);

		return result.toString();
	}

	/**
	 * Gets the <code>MySQL</code> limit rows statement. e.g., <br/>
	 * <code><i>SELECT * FROM EMPLOYEE ORDER BY ID ASC</i> LIMIT 21,30;</code>
	 * 
	 * @param sql
	 *            The statement to fetch an ordered result set.
	 * @param offset
	 *            The number of rows to offset.
	 * @param pageSize
	 *            The number of rows to fetch.
	 * @return The <code>MySQL</code> limit rows statement.
	 */
	private String getMySqlLimitRowsSql(String sql, int offset, int pageSize) {
		StringBuilder result = new StringBuilder(sql);

		if (offset <= 0) {
			result.append(Clause.LIMIT.key).append(pageSize);
		} else {
			result.append(Clause.LIMIT.key).append(offset).append(",").append(pageSize);
		}

		return result.toString();
	}

	/**
	 * Gets the <code>PostgreSQL</code> limit rows statement. e.g., <br/>
	 * <code><i>SELECT * FROM EMPLOYEE ORDER BY ID ASC</i> LIMIT 10 OFFSET 20;</code>
	 * 
	 * @param sql
	 *            The statement to fetch an ordered result set.
	 * @param offset
	 *            The number of rows to offset.
	 * @param pageSize
	 *            The number of rows to fetch.
	 * @return The <code>PostgreSQL</code> limit rows statement.
	 */
	private String getPostgreSqlLimitRowsSql(String sql, int offset, int pageSize) {
		StringBuilder result = new StringBuilder(sql);

		if (offset <= 0) {
			result.append(Clause.LIMIT.key).append(pageSize);
		} else {
			result.append(Clause.LIMIT.key).append(pageSize).append(Clause.OFFSET.key).append(offset);
		}

		return result.toString();
	}
}
