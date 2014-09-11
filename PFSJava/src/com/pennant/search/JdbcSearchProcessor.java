/**
 *  Copyright 2009 The Revere Group
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.CustomCondition;
import com.healthmarketscience.sqlbuilder.CustomSql;
import com.healthmarketscience.sqlbuilder.OrderObject;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 *  
 * A singleton instance of this class is maintained for each SessionFactory. This should
 * be accessed using {@link JdbcSearchProcessor#getInstanceForSessionFactory(SessionFactory)}.
 * 
 * @author dwolverton
 */
public class JdbcSearchProcessor  {
	private final static Logger logger = Logger.getLogger(JdbcSearchProcessor.class);

	private static final String SELECT = "select";
	private static final String FROM = "from";
	private static final String DISTINCT = "distinct";

	private static Map<DataSource, JdbcSearchProcessor> map = new HashMap<DataSource, JdbcSearchProcessor>();
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
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

	// --- Public Methods --- //
	/**
	 * Search for objects based on the search parameters in the specified
	 * <code>ISearch</code> object.
	 * 
	 * @see ISearch
	 */
	@SuppressWarnings("rawtypes")
	public List search(DataSource dataSource, ISearch search) {
		if (search == null)
			return null;

		return search(dataSource, search.getSearchClass(), search);
	}


	/**
	 * Search for objects based on the search parameters in the specified
	 * <code>ISearch</code> object. Uses the specified searchClass, ignoring the
	 * searchClass specified on the search itself.
	 * 
	 * @see ISearch
	 */
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	public List search(DataSource dataSource, Class<?> searchClass, ISearch search) throws DataAccessException {
		if (search == null || (searchClass == null && StringUtils.trimToEmpty(search.getTabelName()).equals(""))) //TODO to discuss with Vasu
			return null;

		//Build query object
		SelectQuery selectQuery =   new SelectQuery();
		if(searchClass != null){  //TODO to discuss with Vasu
			System.out.println("Object Name : " + searchClass.getSimpleName());
			System.out.println("Table Name : " + PennantJavaUtil.getTabelMap(searchClass.getSimpleName()));
		}
		
		if (StringUtils.trimToEmpty(search.getTabelName()).equals("")){
			switch (PennantConstants.DatabaseSystem) {
			case 1:
				selectQuery.addCustomFromTable((String) PennantJavaUtil.getTabelMap(searchClass.getSimpleName()) + " WITH (NOLOCK)");
				break;
			default:
				selectQuery.addCustomFromTable((String) PennantJavaUtil.getTabelMap(searchClass.getSimpleName()));
				break;
			}
		}else{
			switch (PennantConstants.DatabaseSystem) {
			case 1:
				selectQuery.addCustomFromTable(search.getTabelName() + " WITH (NOLOCK)");
				break;
			default:
				selectQuery.addCustomFromTable(search.getTabelName());
				break;
			}
		}

		// Add the fields to the query from the fields List
		List fields = search.getFields();
		if (fields.size() <= 0 ) {
			// If not fields added select all fields
			selectQuery.addCustomColumns(new CustomSql("*"));
		}
		else {
			// If specific fields added to search object select only required fields
			for (Iterator iterator = fields.iterator(); iterator.hasNext();) {
				Field field = (Field) iterator.next();
				selectQuery.addCustomColumns(new CustomSql(field.property));
			}
		}

		// Add where conditions

		if (search.getFilters() != null) {
			List filters = search.getFilters();

			for (Iterator iterator = filters.iterator(); iterator.hasNext();) {
				Filter filter = (Filter) iterator.next();

				if (filter.getProperty().equals("OR")){
					String orCond = filter.toString().replace("'", "").replace('"', '\'').trim();
					selectQuery.addCondition(new CustomCondition(orCond));
				}else if (filter.getOperator()==filter.OP_IN || filter.getOperator()==filter.OP_NOT_IN){
					selectQuery.addCondition(setInCond(filter));
				}else{
					String andCond = filter.toString().replace("'", "").replace('"', '\'').trim();
					selectQuery.addCondition(new CustomCondition(andCond));
				}
			}
		}

		// Add direct where clause sent by client
		if (search.getWhereClause() != null) {
			selectQuery.addCondition(new CustomCondition(search.getWhereClause()));
		}
		// Add order by conditions
		if (search.getFilters() != null ) {
			List sorts = search.getSorts();

			for (Iterator iterator = sorts.iterator(); iterator.hasNext();) {
				Sort sortField = (Sort) iterator.next();
				selectQuery.addCustomOrdering(sortField.getProperty(), (sortField.isDesc() ? OrderObject.Dir.DESCENDING : OrderObject.Dir.ASCENDING));
			}
		}

		System.out.println("1SQL : " + selectQuery.toString());
		selectQuery.validate();		
		Map<String,Object> namedParameters = new HashMap<String,Object>();

		boolean firstResult = false;
		if (search.getFirstResult() > 0)
			firstResult = true;
		System.out.println("2SQL : " + getLimitString(selectQuery.toString(), firstResult, search.getFirstResult(),search.getMaxResults()));
		
		List rowTypes = null;
		if(searchClass != null){  //TODO to discuss with Vasu
			RowMapper rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(searchClass);
			rowTypes = this.namedParameterJdbcTemplate.query(
					getLimitString(selectQuery.toString(), firstResult, search.getFirstResult(),search.getMaxResults()),
					namedParameters , 
					rowMapper);
		}else{
			rowTypes = this.namedParameterJdbcTemplate.queryForList(
					getLimitString(selectQuery.toString(), firstResult, search.getFirstResult(),search.getMaxResults()),
					namedParameters );
		}
 		//addPaging(query, search);
		return rowTypes;
	}
	

	 
	/**
	 * Returns the total number of results that would be returned using the
	 * given <code>ISearch</code> if there were no paging or maxResult limits.
	 * 
	 * @see ISearch
	 */
	public int count(DataSource dataSource, ISearch search) {
		if (search == null)
			return 0;
		return count(dataSource, search.getSearchClass(), search);
	}

	/**
	 * Returns the total number of results that would be returned using the
	 * given <code>ISearch</code> if there were no paging or maxResult limits.
	 * Uses the specified searchClass, ignoring the searchClass specified on the
	 * search itself.
	 * 
	 * @see ISearch
	 */
	@SuppressWarnings("static-access")
	public int count(DataSource dataSource, Class<?> searchClass, ISearch search) {
		
		int count;
		if (searchClass == null || search == null)
			return 0;

		//Build query object
		SelectQuery selectQuery =   new SelectQuery();
		
		//selectQuery.addCustomFromTable((String) PennantJavaUtil.getTabelMap(searchClass.getSimpleName()));

		if (StringUtils.trimToEmpty(search.getTabelName()).equals("")){
			switch (PennantConstants.DatabaseSystem) {
			case 1:
				selectQuery.addCustomFromTable((String) PennantJavaUtil.getTabelMap(searchClass.getSimpleName()) + " WITH (NOLOCK)");
				break;
			default:
				selectQuery.addCustomFromTable((String) PennantJavaUtil.getTabelMap(searchClass.getSimpleName()));
				break;
			}
		}else{
			switch (PennantConstants.DatabaseSystem) {
			case 1:
				selectQuery.addCustomFromTable(search.getTabelName() + " WITH (NOLOCK)");
				break;
			default:
				selectQuery.addCustomFromTable(search.getTabelName());
				break;
			}
		}
		
		// select count of all fields
		selectQuery.addCustomColumns(new CustomSql("count(*)"));

		// Add where conditions
		Map<String,Object> namedParameters = new HashMap<String,Object>();
		if (search.getFilters() != null ) {
			List<Filter> filters = search.getFilters();

			for (Iterator<Filter> iterator = filters.iterator(); iterator.hasNext();) {
				Filter filter = (Filter) iterator.next();
				if (filter.getProperty().equals("OR")){
					String orCond = filter.toString().replace("'", "").replace('"', '\'').trim();
					selectQuery.addCondition(new CustomCondition(orCond));
					System.out.println(selectQuery);
				}else if (filter.getOperator()==filter.OP_IN || filter.getOperator()==filter.OP_NOT_IN){
					selectQuery.addCondition(setInCond(filter));
				}else{
					String andCond = filter.toString().replace("'", "").replace('"', '\'').trim();
					selectQuery.addCondition(new CustomCondition(andCond));
					//selectQuery.addCondition(new CustomCondition(filter.getProperty() + filter.getSqlOperator() + ":"+filter.getProperty()));
					//namedParameters.put(filter.getProperty(), filter.getValue());
				}
			}
			// Add direct where clause sent by client
			if (search.getWhereClause() != null) {
				
						selectQuery.addCondition(new CustomCondition(search.getWhereClause()));
			}
		}
 

		selectQuery.validate();
		System.out.println("3SQL : " + selectQuery.toString());
		
		count = this.namedParameterJdbcTemplate.queryForInt(selectQuery.toString(), namedParameters);
		return count;
	}

	/**
	 * Returns a <code>SearchResult</code> object that includes the list of
	 * results like <code>search()</code> and the total length like
	 * <code>searchLength</code>.
	 * 
	 * @see ISearch
	 */
	@SuppressWarnings("rawtypes")
	public SearchResult searchAndCount(DataSource dataSource, ISearch search) {
		
		if (search == null)
			return null;
		return searchAndCount(dataSource, search.getSearchClass(), search);
	}

	/**
	 * Returns a <code>SearchResult</code> object that includes the list of
	 * results like <code>search()</code> and the total length like
	 * <code>searchLength</code>. Uses the specified searchClass, ignoring the
	 * searchClass specified on the search itself.
	 * 
	 * @see ISearch
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SearchResult searchAndCount(DataSource dataSource, Class<?> searchClass, ISearch search) {
	
		if (searchClass == null || search == null)
			return null;

		SearchResult result = new SearchResult();
		result.setResult(search(dataSource, searchClass, search));

		if (search.getMaxResults() > 0) {
			result.setTotalCount(count(dataSource, searchClass, search));
		} else {
			result.setTotalCount(result.getResult().size() + SearchUtil.calcFirstResult(search));
		}

		return result;
	}

	public static String getLimitString(String sql, boolean hasOffset, int startRow, int endRow) {
		switch (PennantConstants.DatabaseSystem) {
		case 1:
			return getMSSQLLimitString(sql,hasOffset,startRow, endRow);
		case 2:
			return getORACLELimitString(sql,hasOffset,startRow, endRow);
		case 3:
			return getDB2LimitString(sql,hasOffset,startRow, endRow);
		case 4:
			return getMYSQLLimitString(sql,hasOffset,startRow, endRow);
		default:
			return null;
		}
	}

	public static String getMYSQLLimitString(String sql, boolean hasOffset, int startRow, int endRow) {

		return new StringBuffer(sql.length() + 20).append(sql).append(
				hasOffset ? " limit " + startRow + " , " + endRow : " limit " + endRow).toString();
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
	public static String getMSSQLLimitString(String querySqlString,int startRow, int endRow) {
		StringBuilder sb = new StringBuilder(querySqlString.trim().toLowerCase());

		int orderByIndex = sb.indexOf("order by");
		CharSequence orderby = orderByIndex > 0 ? sb.subSequence(orderByIndex, sb.length())
				: "ORDER BY CURRENT_TIMESTAMP";

		if (startRow!=0){
			endRow = startRow + endRow;
			startRow=startRow+1;
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
		sb.append("WHERE row_nr BETWEEN "+ startRow + " AND "+ endRow);

		return sb.toString();
	}

	/**
	 * Utility method that checks if the given sql query is a select distinct one and if so replaces the distinct select
	 * with an equivalent simple select with a group by clause. See
	 * {@link SQLServer2005DialectTestCase#testReplaceDistinctWithGroupBy()}
	 * 
	 * @param sql an sql query
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
		StringBuffer pagingSelect = new StringBuffer(sql.length() + 100)
				.append(sql.substring(0, startOfSelect)) // add the comment
				.append("select * from ( select ") // nest the main query in an
													// outer select
				.append(getRowNumber(sql)); // add the rownnumber bit into the
											// outer query select list

		if (startRow!=0){
			endRow = startRow + endRow;
		}
		  
		
		if (hasDistinct(sql)) {
			pagingSelect.append(" row_.* from ( ") // add another (inner) nested
													// select
					.append(sql.substring(startOfSelect)) // add the main query
					.append(" ) as row_"); // close off the inner nested select
		} else {
			pagingSelect.append("Results.* From(" +sql.substring(startOfSelect)+") as Results"); // add the
																	// main
																	// query
		}
		pagingSelect.append(" ) as temp_ where rownumber_ "); // add the
																// restriction
																// to the outer
																// select
		pagingSelect.append(" between "+(startRow+1)+" and "+endRow);

		return pagingSelect.toString();

	}

	public static String getORACLELimitString(String sql, boolean hasOffset, int startRow, int endRow) {

		sql = sql.trim();
		boolean isForUpdate = false;
		if (sql.toLowerCase().endsWith(" for update")) {
			sql = sql.substring(0, sql.length() - 11);
			isForUpdate = true;
		}
		StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
		if (hasOffset) {
			pagingSelect
					.append("select * from ( select row_.*, rownum rownum_ from ( ");
		} else {
			pagingSelect.append("select * from ( ");
		}
		pagingSelect.append(sql);
		if (hasOffset) {
			pagingSelect.append(" ) row_ ) where rownum_ <= ? and rownum_ > ?");
		} else {
			pagingSelect.append(" ) where rownum <= ?");
		}
		if (isForUpdate) {
			pagingSelect.append(" for update");
		}
		return pagingSelect.toString();

	}
	
	private static String getRowNumber(String sql) {
		StringBuffer rownumber = new StringBuffer(50)
				.append("rownumber() over(");
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
	private Condition setInCond(Filter filter){

		String inCondString="";		
		String[] strArray=null;
		List<String> objArray=null;
		Object[] valArray=null;

		if(filter.getValue() instanceof String[]){
			strArray=(String[])filter.getValue();
		}else if(filter.getValue() instanceof List){
			objArray=(List<String>)filter.getValue();
		}else{
			valArray=(Object[])filter.getValue();
		}

		if(strArray!=null){
			for (int i = 0; i < strArray.length; i++) {
				if (i!=0){
					inCondString=inCondString+",";
				}
				inCondString=inCondString+"'"+strArray[i]+"'";
			}
		}

		if(objArray!=null){
			for (int i = 0; i < objArray.size(); i++) {
				if (i!=0){
					inCondString=inCondString+",";
				}
				inCondString=inCondString+"'"+objArray.get(i)+"'";
			}
		}

		if(valArray!=null){
			inCondString = valArray[0].toString();
		}
		inCondString="("+inCondString+")";
		return new CustomCondition(filter.getProperty() + filter.getSqlOperator() +inCondString);

	}
}