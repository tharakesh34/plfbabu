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
package com.pennanttech.pff.core.util;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pff.core.TableType;

/**
 * <p>
 * A suite of utilities for building the SQL queries.
 * </p>
 */
public final class QueryUtil {
	private QueryUtil() {
		super();
	}

	/**
	 * Returns the SQL query to check the number of records exist in the tables with the specified criteria.
	 * 
	 * @param tables      The array of tables that need to be looked.
	 * @param whereClause The criteria that need to be checked.
	 * @return The SQL query.
	 */
	public static String getCountQuery(String table, String whereClause) {
		StringBuilder sql = new StringBuilder("select count(*) from ");
		sql.append(table);
		sql.append(" where ");
		sql.append(whereClause);

		return sql.toString();
	}

	/**
	 * Returns the SQL query to check the number of records exist in the tables with the specified criteria.
	 * 
	 * @param tables      The array of tables that need to be looked.
	 * @param whereClause The criteria that need to be checked.
	 * @return The SQL query.
	 */
	public static String getCountQuery(String[] tables, String whereClause) {
		StringBuilder sql = new StringBuilder("select sum(cnt) from (");

		for (int i = 0; i < tables.length; i++) {
			sql.append("select count(*) cnt from ");
			sql.append(tables[i]);
			sql.append(" where ");
			sql.append(whereClause);

			if (i < (tables.length - 1)) {
				sql.append(" union all ");
			}
		}

		sql.append(") t");

		return sql.toString();
	}

	public static String getConcurrencyCondition(TableType tableType) {
		if (tableType == TableType.TEMP_TAB) {
			return " and LastMntOn = :PrevMntOn";
		} else {
			return " and Version = :Version - 1";
		}
	}

	public static String getConcurrencyClause(TableType tableType) {
		if (tableType == TableType.TEMP_TAB) {
			return " and LastMntOn = ?";
		} else {
			return " and Version = ?";
		}
	}

	public static String getInsertQuery(Set<String> columnSet, String tableName) {
		StringBuilder columns = new StringBuilder();
		StringBuilder values = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		sql.append("Insert into ").append(tableName);

		for (String key : columnSet) {
			if (values.length() > 0) {
				values.append(", ");
			}
			values.append(key);
			if (columns.length() > 0) {
				columns.append(", ");
			}
			columns.append(":" + key);
		}
		sql.append(" (").append(values.toString()).append(") ");
		sql.append("Values (").append(columns.toString()).append(")");

		return sql.toString();
	}

	public static String getQueryConcat() {
		if (App.DATABASE == Database.ORACLE || App.DATABASE == Database.POSTGRES) {
			return "||";
		}

		return "+";
	}

	public static String buildWhereClause(ISearch search, List<Object> psList) {
		StringBuilder sql = new StringBuilder();

		for (Filter filter : search.getFilters()) {
			String condition = filter.getProperty();

			if ("AND".equals(condition) || "OR".equals(condition)) {
				if (!(filter.getValue() instanceof List<?>)) {
					continue;
				}

				List<?> list = (List<?>) filter.getValue();

				for (Object object : list) {
					if (object instanceof Filter) {
						try {
							if (sql.length() > 0) {
								sql.append(condition).append(" ");
							}

							buildQueryByOperator((Filter) object, psList, sql);
						} catch (Exception e) {
							//
						}

					}
				}
			} else {
				try {
					if (sql.length() > 0) {
						sql.append(" AND ");
					}
					buildQueryByOperator(filter, psList, sql);
				} catch (Exception e) {
					//
				}
			}
		}

		if (sql.length() > 0) {
			return " Where ".concat(sql.toString());
		}

		return "";
	}

	public static void buildQueryByOperator(Filter filter, List<Object> psList, StringBuilder sql) {

		String property = filter.getProperty();
		sql.append(property);

		switch (filter.getOperator()) {
		case 0:
			sql.append(" = ");
			sql.append("? ");

			psList.add(filter.getValue());
			break;
		case 1:
			sql.append(" <> ");
			sql.append("? ");

			psList.add(filter.getValue());
			break;
		case 2:
			String sql2 = "";
			sql.append(" like ? ");
			if (App.DATABASE == Database.POSTGRES) {
				sql2 = sql.toString().replaceAll("(?i)like", "ilike");
			}

			sql = new StringBuilder(sql2);

			psList.add("%" + filter.getValue() + "%");
			break;
		case 3:
			sql.append(" > ");
			sql.append("? ");

			psList.add(filter.getValue());
			break;
		case 4:
			sql.append(" <= ");
			sql.append("? ");

			psList.add(filter.getValue());
			break;
		case 5:
			sql.append(" >= ");
			sql.append("? ");

			psList.add(filter.getValue());
			break;
		case 6:
			sql.append(" LIKE ");
			sql.append("%?% ");

			psList.add(filter.getValue());
			break;
		case 10:
			sql.append(" IS NULL ");

			break;
		case 11:
			sql.append(" IS NOT NULL ");
			break;
		case 8:
			sql.append(" IN (");
			commaJoin(sql, filter.getValue(), psList);

			break;
		case 9:
			sql.append(" NOT IN (");
			commaJoin(sql, filter.getValue(), psList);

			break;
		default:
			break;
		}
	}

	private static void commaJoin(StringBuilder sql, Object value, List<Object> psList) {
		List<Object> inList = Arrays.asList(value);
		for (Object object : inList) {
			String valu = String.valueOf(object);
			String[] split = new String[] {};
			if (valu.contains(",")) {
				split = valu.split(",");

			} else {
				split[0] = valu;
			}

			for (String s1 : split) {
				sql.append(" ?,");
				psList.add(s1);
			}

		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(") ");
	}
}
