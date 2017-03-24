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
	 * @param tables
	 *            The array of tables that need to be looked.
	 * @param whereClause
	 *            The criteria that need to be checked.
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
}
